package com.cabybara.aishortvideo.repository;

import com.cabybara.aishortvideo.dto.user.AdminUsersOverviewDTO;
import com.cabybara.aishortvideo.model.User;
import com.cabybara.aishortvideo.utils.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndStatus(String username, UserStatus userStatus);

    Optional<User> findByIdAndStatus(Long id, UserStatus userStatus);

    Optional<User> findByUsernameAndStatus(String username, UserStatus userStatus);

    @Query("""
        SELECT new com.cabybara.aishortvideo.dto.user.AdminUsersOverviewDTO (
            SUM(CASE WHEN u.status = 'ACTIVE' THEN 1 ELSE 0 END),
            SUM(CASE WHEN u.status = 'INACTIVE' THEN 1 ELSE 0 END),
            SUM(CASE WHEN FUNCTION('DATE', u.createdAt) = CURRENT_DATE THEN 1 ELSE 0 END)
        )
        FROM User u
    """)
    AdminUsersOverviewDTO getOverviewUsers();

    @Query("""
        SELECT COUNT(u)
        FROM User u
        WHERE u.createdAt < :localDateTime
    """)
    Long countByCreatedAtBefore(LocalDateTime localDateTime);

    Page<User> findByStatus(UserStatus userStatus, Pageable pageable);

    @Query("""
        SELECT u
        FROM User u
        WHERE (u.firstName ILIKE :name OR u.lastName ILIKE :name) AND u.status = :userStatus
    """)
    Page<User> findByNameContainingIgnoreCaseAndStatus(String name, UserStatus userStatus, Pageable pageable);

    @Query("""
        SELECT u
        FROM User u
        WHERE (u.firstName ILIKE :name OR u.lastName ILIKE :name)
    """)
    Page<User> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
