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
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalArgumentException("현재 로그인할 수 없는 계정입니다.");
        }
        String accessToken = jwtTokenProvider.generateAccessToken(user.getUserId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUserId());
        Date expiresAt = jwtTokenProvider.getRefreshTokenExpiryDate();
        int updated = userAuthorityRepository.updateTokenByUserId(refreshToken, expiresAt, user.getUserId());

        if (updated == 0) {
            userAuthorityRepository.save(new UserAuthority(
                    user.getUserId(),
                    refreshToken,
                    expiresAt
            ));
        }
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

    public TokenDto reissueToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("Refresh Token이 유효하지 않습니다.");
        }
        String userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        UserAuthority authority = userAuthorityRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("저장된 토큰 정보가 없습니다."));

        if (!authority.getRefreshToken().equals(refreshToken)) {
            throw new RuntimeException("토큰이 일치하지 않습니다.");
        }

        if (authority.getExpiresAt().before(new Date())) {
            logout(userId);
            throw new RuntimeException("Refresh Token이 만료되었습니다. 다시 로그인해주세요.");
        }
        String newAccessToken = jwtTokenProvider.generateAccessToken(userId);

        return new TokenDto(newAccessToken , refreshToken);
    }
}