package com.cabybara.aishortvideo.model.composite_id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFollowerId implements Serializable {
    @Column(name = "id_follower")
    private Long followerId;

    @Column(name = "id_user")
    private Long userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserFollowerId)) return false;
        UserFollowerId that = (UserFollowerId) o;
        return Objects.equals(followerId, that.followerId) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(followerId, userId);
    }
}
