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
import com.RollinMoment.RollinMomentServer.member.repository.UserAuthorityRepository;
import com.RollinMoment.RollinMomentServer.member.repository.UserRepository;
import com.mysema.commons.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private LoginService loginService;

    @Autowired
    private SignUpService signUpService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
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

        TokenDto tokenDto = loginService.login(loginDto);

        //THEN
        assertNotNull(tokenDto.getAccessToken());
        assertNotNull(tokenDto.getRefreshToken());

        log.info("✅ AccessToken 은??: {}", tokenDto.getAccessToken());
        log.info("✅ RefreshToken 은???: {}", tokenDto.getRefreshToken());
    }
    @Test
    @DisplayName("로그아웃 테스트")
    void logoutTest() throws Exception {
        //GIVE
        String userId = "testuser@rollin.com";
        UserEntity user = userRepository.save(new UserEntity(
                userId, "device123", "암호", "닉네임", true,
                Gender.NONE, Provider.ROLLINMOMENT, Ostype.ANDROID
        ));
        userRepository.save(user);

        String refreshToken = jwtTokenProvider.generateRefreshToken();
        Date expiryDate = jwtTokenProvider.getRefreshTokenExpiryDate();
        userAuthorityRepository.save(new UserAuthority(userId, refreshToken, expiryDate));

        String accessToken = jwtTokenProvider.generateAccessToken(userId);

        String parsedUserId = jwtTokenProvider.getUserIdFromToken(accessToken);
        assertThat(parsedUserId).isEqualTo(userId); // 확인용

        // WHEN
        loginService.logout(parsedUserId);

        // THEN
        Optional<UserAuthority> deleted = userAuthorityRepository.findByUserId(userId);
        assertThat(deleted).isEmpty(); //  삭제됨
    }
}