package com.RollinMoment.RollinMomentServer.member.service;

import com.RollinMoment.RollinMomentServer.common.util.AESUtil;
import com.RollinMoment.RollinMomentServer.jwt.JwtTokenProvider;
import com.RollinMoment.RollinMomentServer.member.dto.LoginDto;
import com.RollinMoment.RollinMomentServer.member.dto.SignUpDto;
import com.RollinMoment.RollinMomentServer.member.dto.TokenDto;
import com.RollinMoment.RollinMomentServer.member.entity.UserAuthority;
import com.RollinMoment.RollinMomentServer.member.entity.UserEntity;
import com.RollinMoment.RollinMomentServer.member.entity.type.Gender;
import com.RollinMoment.RollinMomentServer.member.entity.type.Ostype;
import com.RollinMoment.RollinMomentServer.member.entity.type.Provider;
import com.RollinMoment.RollinMomentServer.member.entity.type.UserStatus;
import com.RollinMoment.RollinMomentServer.member.repository.UserAuthorityRepository;
import com.RollinMoment.RollinMomentServer.member.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@Transactional
class LoginServiceTest {
    @Autowired
    private AESUtil aesUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private SignUpService signUpService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserAuthorityRepository userAuthorityRepository;

    @Test
    @DisplayName("로그인 테스트")
    void loginWithEncryptedPasswordTest() {
        // GIVE
        String rawPassword = "testLoginPassword123";
        String encryptedPassword = aesUtil.encrypt(rawPassword); // Postman처럼 암호화된 값

        SignUpDto signUpDto = new SignUpDto();
        signUpDto.setUserId("login@naver.com");
        signUpDto.setPassword(encryptedPassword);
        signUpDto.setNickname("로그인테스터");
        signUpDto.setDeviceId("login-device");
        signUpDto.setAlarm(true);
        signUpDto.setGender("FEMALE");
        signUpDto.setProvider("ROLLINMOMENT");
        signUpDto.setOsType("ANDROID");

        signUpService.SignUp(signUpDto);

        //WHEN
        LoginDto loginDto = new LoginDto();
        loginDto.setUserId("login@naver.com");
        loginDto.setPassword(encryptedPassword); // 클라이언트처럼 암호화된 비밀번호

        TokenDto tokenDto = userService.login(loginDto);

        //THEN
        assertNotNull(tokenDto.getAccessToken());
        assertNotNull(tokenDto.getRefreshToken());

        log.info("✅ AccessToken 은??: {}", tokenDto.getAccessToken());
        log.info("✅ RefreshToken 은???: {}", tokenDto.getRefreshToken());
    }
    @Test
    @DisplayName("로그아웃 테스트")
    void logoutTest()  {
        //GIVE
        String userId = "testuser@rollin.com";
        UserEntity user = userRepository.save(new UserEntity(
                userId, "device123", "암호", "닉네임", true,
                Gender.NONE, Provider.ROLLINMOMENT, Ostype.ANDROID , UserStatus.ACTIVE,null
        ));
        userRepository.save(user);

        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUserId());
        Date expiryDate = jwtTokenProvider.getRefreshTokenExpiryDate();
        userAuthorityRepository.save(new UserAuthority(userId, refreshToken, expiryDate));

        String accessToken = jwtTokenProvider.generateAccessToken(userId);

        String parsedUserId = jwtTokenProvider.getUserIdFromToken(accessToken);
        assertThat(parsedUserId).isEqualTo(userId); // 확인용

        // WHEN
        userService.logout(parsedUserId);

        // THEN
        Optional<UserAuthority> deleted = userAuthorityRepository.findByUserId(userId);
        assertThat(deleted).isEmpty(); //  삭제됨
    }
    @Test
    void refreshToken으로_AccessToken_재발급_성공() {
        // GIVEN
        String userId = "relogin@test.com";
        UserEntity user = userRepository.save(new UserEntity(
                userId, "device123", "암호", "닉네임", true,
                Gender.NONE, Provider.ROLLINMOMENT, Ostype.ANDROID , UserStatus.ACTIVE,null
        ));

        // Refresh Token 생성 및 DB 저장
        String refreshToken = jwtTokenProvider.generateRefreshToken(userId);
        Date expiry = jwtTokenProvider.getRefreshTokenExpiryDate();
        userAuthorityRepository.save(new UserAuthority(userId, refreshToken, expiry));

        // WHEN
        // 1. Refresh Token 유효성 검증
        boolean isValid = jwtTokenProvider.validateToken(refreshToken);
        assertThat(isValid).isTrue();

        // 2. 토큰에서 사용자 ID 추출
        String extractedUserId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        assertThat(extractedUserId).isEqualTo(userId);

        // 3. DB에서 토큰 일치 여부 확인
        UserAuthority authority = userAuthorityRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));

        assertThat(authority.getRefreshToken()).isEqualTo(refreshToken);

        // 4. AccessToken 재발급
        String newAccessToken = jwtTokenProvider.generateAccessToken(userId);
        assertThat(newAccessToken).isNotNull();

        log.info(" 새로 발급된 Access Token: " + newAccessToken);
    }
}