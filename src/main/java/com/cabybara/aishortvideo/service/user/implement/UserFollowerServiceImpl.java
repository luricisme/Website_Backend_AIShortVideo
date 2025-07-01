package com.cabybara.aishortvideo.service.user.implement;

import com.cabybara.aishortvideo.repository.UserFollowerRepository;
import com.cabybara.aishortvideo.service.user.UserFollowerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserFollowerServiceImpl implements UserFollowerService {
    private final UserFollowerRepository userFollowerRepository;

    public UserFollowerServiceImpl(
            UserFollowerRepository userFollowerRepository
    ) {
        this.userFollowerRepository = userFollowerRepository;
    }

    
}
