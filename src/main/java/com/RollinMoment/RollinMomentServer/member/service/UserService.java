package com.RollinMoment.RollinMomentServer.member.service;

import com.RollinMoment.RollinMomentServer.common.util.AESUtil;
import com.RollinMoment.RollinMomentServer.jwt.JwtTokenProvider;
import com.RollinMoment.RollinMomentServer.member.dto.LoginDto;
import com.RollinMoment.RollinMomentServer.member.dto.TokenDto;
import com.RollinMoment.RollinMomentServer.member.entity.UserAuthority;
import com.RollinMoment.RollinMomentServer.member.entity.UserEntity;
import com.RollinMoment.RollinMomentServer.member.entity.type.UserStatus;
import com.RollinMoment.RollinMomentServer.member.repository.UserAuthorityRepository;
import com.RollinMoment.RollinMomentServer.member.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserAuthorityRepository userAuthorityRepository;
    private final AESUtil aesUtil;

    /**
     * 로그인!!
     */
    public TokenDto login(LoginDto loginDto) {
        UserEntity user = userRepository.findByUserId(loginDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String decryptedPassword = aesUtil.decrypt(loginDto.getPassword());

        if (!bCryptPasswordEncoder.matches(decryptedPassword, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user.getUserId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUserId());
        Date expiresAt = jwtTokenProvider.getRefreshTokenExpiryDate();

        // RefreshToken 저장
        userAuthorityRepository.save(new UserAuthority(user.getUserId(), refreshToken, expiresAt));

        return new TokenDto(accessToken, refreshToken);
    }

    /**
     * 로그아웃
     * @param userId
     */
    public void logout(String userId) {
        Optional<UserAuthority> authority = userAuthorityRepository.findByUserId(userId);
        authority.ifPresent(userAuthorityRepository::delete);
    }

    /**
     * 회원탈퇴
     * @param userId
     */
    @Transactional
    public void delete(String userId) {
        UserEntity user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));
        user.setStatus(UserStatus.WITHDRAWN);
        user.setDeleteTime(LocalDateTime.now());
    }
}