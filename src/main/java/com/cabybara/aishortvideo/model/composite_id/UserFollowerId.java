package com.cabybara.aishortvideo.model.composite_id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Embeddable
@Data
@EqualsAndHashCode
public class UserFollowerId implements Serializable {
    @Column(name = "id_follower")
    private Integer followerId;

    @Column(name = "id_user")
    private Integer userId;
}
