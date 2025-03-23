package com.RollinMoment.RollinMomentServer.member.repository;

import com.RollinMoment.RollinMomentServer.member.entity.UserAuthority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAuthorityRepository extends JpaRepository<UserAuthority,Long> {

    Optional<UserAuthority> findByUserId(String userId);
}
