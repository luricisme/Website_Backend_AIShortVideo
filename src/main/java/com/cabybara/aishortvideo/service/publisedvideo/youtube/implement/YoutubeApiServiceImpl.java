package com.cabybara.aishortvideo.service.publisedvideo.youtube.implement;

import com.cabybara.aishortvideo.exception.VideoNotFoundException;
import com.cabybara.aishortvideo.mapper.PublishedVideoMapper;
import com.cabybara.aishortvideo.model.PublishedVideo;
import com.cabybara.aishortvideo.model.UserSocialAccount;
import com.cabybara.aishortvideo.repository.PublishedVideoRepository;
import com.cabybara.aishortvideo.service.auth.GoogleOauthService;
import com.cabybara.aishortvideo.service.user.UserSocialAccountService;
import com.cabybara.aishortvideo.service.publisedvideo.PublishedVideoService;
import com.cabybara.aishortvideo.service.publisedvideo.youtube.YoutubeApiService;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.CredentialRefreshListener;
import com.google.api.client.auth.oauth2.TokenErrorResponse;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class YoutubeApiServiceImpl implements YoutubeApiService {
    private static final String APPLICATION_NAME = "AiShortVideo";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    class TokenRefreshListener implements CredentialRefreshListener {
        private final UserSocialAccountService userSocialAccountService;
        private final GoogleOauthService googleOauthService;

        public TokenRefreshListener(
                UserSocialAccountService userSocialAccountService,
                GoogleOauthService googleOauthService
        ) {
            this.userSocialAccountService = userSocialAccountService;
            this.googleOauthService = googleOauthService;
        }

        @Override
        public void onTokenResponse(Credential credential, TokenResponse tokenResponse) {
            String platformUserId = googleOauthService.getUserInfo(tokenResponse.getAccessToken()).getSub();
            if (platformUserId != null) {
                userSocialAccountService.updateAccessTokenAndExpiry(platformUserId,
                        "google",
                        tokenResponse.getAccessToken(),
                        Instant.ofEpochSecond(tokenResponse.getExpiresInSeconds()));
            }
        }

        @Override
        public void onTokenErrorResponse(Credential credential, TokenErrorResponse tokenErrorResponse) throws IOException {
            log.error("Failed to refresh token: " + tokenErrorResponse.getErrorDescription());
        }
    }

    private final UserSocialAccountService userSocialAccountService;
    private final PublishedVideoMapper publishedVideoMapper;
    private final PublishedVideoService publishedVideoService;
    private final PublishedVideoRepository publishedVideoRepository;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    public YoutubeApiServiceImpl(
            UserSocialAccountService userSocialAccountService,
            PublishedVideoMapper mapper,
            PublishedVideoService publishedVideoService,
            PublishedVideoRepository publishedVideoRepository
    ) {
        this.userSocialAccountService = userSocialAccountService;
        this.publishedVideoMapper = mapper;
        this.publishedVideoService = publishedVideoService;
        this.publishedVideoRepository = publishedVideoRepository;
    }

    private YouTube getYouTubeService(Long userId) throws GeneralSecurityException, IOException {
        UserSocialAccount userSocialAccount = userSocialAccountService.getUserSocialAccount(userId, "google");
        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(JSON_FACTORY)
                .setClientSecrets(clientId, clientSecret)
                .build()
                .setAccessToken(userSocialAccount.getAccessToken())
                .setRefreshToken(userSocialAccount.getRefreshToken());

        return new YouTube.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }


    @Override
    public Video uploadVideo(Long userId, File videoFile, String title, String description) throws Exception {
        YouTube youtube = getYouTubeService(userId);

        Video video = new Video();

        VideoSnippet snippet = new VideoSnippet();
        snippet.setTitle(title);
        snippet.setDescription(description);
        snippet.setCategoryId("1");
        video.setSnippet(snippet);

        VideoStatus status = new VideoStatus();
        status.setPrivacyStatus("private");
        video.setStatus(status);

        InputStreamContent mediaContent = new InputStreamContent(
                "application/octet-stream",
                new BufferedInputStream(new FileInputStream(videoFile)));
        mediaContent.setLength(videoFile.length());

        YouTube.Videos.Insert request = youtube.videos()
                .insert("snippet,status,contentDetails", video, mediaContent);

        Video responseVideo = request.execute();
        PublishedVideo publishedVideo = publishedVideoMapper.toPublishedVideoFromYoutubeVideo(responseVideo);
        publishedVideo.setUploadedBy(userId);
        publishedVideo.setPlatform("google");
        publishedVideoService.savePublishedVideo(publishedVideo);

        return responseVideo;
    }

    @Override
    public Video uploadVideoFromUrl(Long userId, String videoUrl, String title, String description, String privacyStatus) throws Exception {
        YouTube youtube = getYouTubeService(userId);

        Video video = new Video();
        VideoSnippet snippet = new VideoSnippet();
        snippet.setTitle(title);
        snippet.setDescription(description);
        snippet.setCategoryId("1");
        video.setSnippet(snippet);

        VideoStatus status = new VideoStatus();
        status.setPrivacyStatus(privacyStatus.equals("private") ? "private" : "public");
        video.setStatus(status);

        HttpURLConnection connection = (HttpURLConnection) new URL(videoUrl).openConnection();
        connection.connect();

        InputStream inputStream = new BufferedInputStream(connection.getInputStream());

        Long contentLength = connection.getContentLengthLong();
        InputStreamContent mediaContent = new InputStreamContent("application/octet-stream", inputStream);
        if (contentLength > 0) {
            mediaContent.setLength(contentLength);
        }

        YouTube.Videos.Insert request = youtube.videos()
                .insert("snippet,status,contentDetails", video, mediaContent);

        Video responseVideo = request.execute();
        PublishedVideo publishedVideo = publishedVideoMapper.toPublishedVideoFromYoutubeVideo(responseVideo);
        publishedVideo.setUploadedBy(userId);
        publishedVideo.setPlatform("google");
        publishedVideoService.savePublishedVideo(publishedVideo);
        return responseVideo;
    }



    @Override
    public Video getVideoStatistics(Long userId, String videoId) throws Exception {
        YouTube youTube = getYouTubeService(userId);

        YouTube.Videos.List request = youTube.videos()
                .list("statistics,snippet,status")
                .setId(videoId);

        try {
            VideoListResponse videoListResponse = request.execute();
            if (videoListResponse.getItems() == null || videoListResponse.getItems().isEmpty()) {
                throw new VideoNotFoundException("Video not found");
            }

            Video responseVideo = videoListResponse.getItems().getFirst();
            publishedVideoService.updatePublishedVideo(videoId, responseVideo);

            return responseVideo;
        } catch (com.google.api.client.googleapis.json.GoogleJsonResponseException e) {
            if (e.getStatusCode() == 400) {
                throw new VideoNotFoundException("Bad request or video not found: " + e.getDetails().getMessage());
            }
            throw e;
        } catch (RuntimeException e) {
            throw new VideoNotFoundException("Video not found");
        }
    }

    @Override
    public void fetchVideoStatistics(Long userId) throws Exception {
        List<String> publishedVideoIds = publishedVideoRepository.findAllVideoIdByUpdatedBy(userId);

        final YouTube youTubeService = getYouTubeService(userId);

        int batchSize = 50;
        for (int i = 0; i < publishedVideoIds.size(); i += batchSize) {
            List<String> sublist = publishedVideoIds.subList(i, Math.min(i + batchSize, publishedVideoIds.size()));
            String videoIdsParam = String.join(",", sublist);

            try {
                YouTube.Videos.List request = youTubeService.videos()
                        .list("statistics,snippet,status")
                        .setId(videoIdsParam);

                VideoListResponse videoListResponse = request.execute();

                if (videoListResponse.getItems() != null && !videoListResponse.getItems().isEmpty()) {
                    for (Video responseVideo : videoListResponse.getItems()) {
                        publishedVideoService.updatePublishedVideo(responseVideo.getId(), responseVideo);
                    }
                }
            } catch (com.google.api.client.googleapis.json.GoogleJsonResponseException e) {
                log.error("Error fetching batch of videos: " + e.getMessage());
            } catch (IOException e) {
                log.error("Network error fetching batch of videos: " + e.getMessage());
            }
        }
    }

    @Override
    public Boolean waitUntilVideoIsProcessed(Long userId, String videoId) throws Exception {
        int maxTry = 5;
        int tryCount = 0;
        int delayTryMs = 15000;

        while (tryCount < maxTry) {
            Video uploadedVideo = getVideoStatistics(userId, videoId);
            String uploadStatus = uploadedVideo.getStatus().getUploadStatus();
            Boolean isViewable = uploadedVideo.getStatus().getPublicStatsViewable();

            if ("processed".equalsIgnoreCase(uploadStatus) && Boolean.TRUE.equals(isViewable)) {
                return true;
            }

            tryCount++;
            Thread.sleep(delayTryMs);
        }

        return false;
    }

    public List<Video> getUploadedVideos(Long userId, String channelId) throws Exception {
        YouTube youtube = getYouTubeService(userId);

        YouTube.Search.List searchRequest = youtube.search()
                .list("snippet")
                .setChannelId(channelId)
                .setType("video")
                .setMaxResults(10L);

        SearchListResponse searchResponse = searchRequest.execute();
        List<String> videoIds = searchResponse.getItems().stream()
                .map(item -> item.getId().getVideoId())
                .collect(Collectors.toList());

        if (videoIds.isEmpty()) return Collections.emptyList();

        YouTube.Videos.List videosRequest = youtube.videos()
                .list("snippet,statistics")
                .setId(String.join(",", videoIds));

        return videosRequest.execute().getItems();
    }
}
