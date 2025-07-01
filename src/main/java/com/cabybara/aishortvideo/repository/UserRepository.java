package com.cabybara.aishortvideo.repository;

import com.cabybara.aishortvideo.model.User;
import com.cabybara.aishortvideo.utils.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndStatus(String username, UserStatus userStatus);

    Optional<User> findByIdAndStatus(Long id, UserStatus userStatus);

    Optional<User> findByUsernameAndStatus(String username, UserStatus userStatus);
}
