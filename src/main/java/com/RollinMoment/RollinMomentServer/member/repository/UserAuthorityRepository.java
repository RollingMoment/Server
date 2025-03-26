package com.RollinMoment.RollinMomentServer.member.repository;

import com.RollinMoment.RollinMomentServer.member.entity.UserAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

public interface UserAuthorityRepository extends JpaRepository<UserAuthority,Long> {

    @Query("SELECT ua FROM UserAuthority ua WHERE ua.userId = :userId")
    Optional<UserAuthority> findByUserId(@Param("userId") String userId);

    @Modifying
    @Transactional
    @Query("UPDATE UserAuthority ua SET ua.refreshToken = :refreshToken, ua.expiresAt = :expiresAt WHERE ua.userId = :userId")
    void updateTokenByUserId(@Param("refreshToken") String refreshToken,
                            @Param("expiresAt") Date expiresAt,
                             @Param("userId") String userId);
}
