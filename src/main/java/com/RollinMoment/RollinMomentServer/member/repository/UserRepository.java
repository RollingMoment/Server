package com.RollinMoment.RollinMomentServer.member.repository;

import com.RollinMoment.RollinMomentServer.member.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity,Long> {

    Boolean existsByUserId(String userId);

    Optional<UserEntity> findByUserId(String userId);
}
