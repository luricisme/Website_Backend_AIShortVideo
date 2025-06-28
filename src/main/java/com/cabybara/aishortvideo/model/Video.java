package com.cabybara.aishortvideo.model;

import com.cabybara.aishortvideo.utils.VideoStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "videos")
public class Video extends AbstractEntity{
    @Column(name = "title")
    private String title;

    @Column(name = "category")
    private String category;

    @Column(name = "style")
    private String style;

    @Column(name = "target")
    private String target;

    @Column(name = "script")
    private String script;

    @Column(name = "audio_url")
    private String audioUrl;

    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "like_cnt")
    private int likeCnt;

    @Column(name = "dislike_cnt")
    private int dislikeCnt;

    @Column(name = "view_cnt")
    private int viewCnt;

    @Column(name = "length")
    private double length;

    @Column(name = "thumbnail")
    private String thumbnail;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status")
    private VideoStatus status;

    // Quan hệ ManyToOne với User
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_user", referencedColumnName = "id")
    private User user;

    // Quan hệ OneToMany với VideoTags
    @OneToMany(mappedBy = "video", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VideoTag> tags = new ArrayList<>();

    // Quan hệ OneToMany với VideoImages
    @OneToMany(mappedBy = "video", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VideoImage> images = new ArrayList<>();
}
