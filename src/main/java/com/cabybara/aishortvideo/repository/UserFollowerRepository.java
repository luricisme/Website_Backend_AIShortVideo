package com.cabybara.aishortvideo.repository;

import com.cabybara.aishortvideo.model.UserFollower;
import com.cabybara.aishortvideo.model.composite_id.UserFollowerId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFollowerRepository extends JpaRepository<UserFollower, UserFollowerId> {

}
