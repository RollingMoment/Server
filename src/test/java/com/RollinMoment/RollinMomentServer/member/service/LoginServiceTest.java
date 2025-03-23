package com.RollinMoment.RollinMomentServer.member.service;

import com.RollinMoment.RollinMomentServer.common.util.AESUtil;
import com.RollinMoment.RollinMomentServer.member.dto.LoginDto;
import com.RollinMoment.RollinMomentServer.member.dto.SignUpDto;
import com.RollinMoment.RollinMomentServer.member.dto.TokenDto;
import com.RollinMoment.RollinMomentServer.member.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    @DisplayName("AES 암호화된 비밀번호로 로그인 테스트 (복호화 + 해시 비교)")
    void loginWithEncryptedPasswordTest() {
        // 1. 회원가입 먼저 (비밀번호 암호화)
        String rawPassword = "testLoginPassword123";
        String encryptedPassword = aesUtil.encrypt(rawPassword); // Postman처럼 암호화된 값

        // 회원가입 DTO
        SignUpDto signUpDto = new SignUpDto();
        signUpDto.setUserId("login@naver.com");
        signUpDto.setPassword(encryptedPassword);
        signUpDto.setNickname("로그인테스터");
        signUpDto.setDeviceId("login-device");
        signUpDto.setAlarm(true);
        signUpDto.setGender("FEMALE");
        signUpDto.setProvider("ROLLINMOMENT");
        signUpDto.setOsType("ANDROID");

        // 회원가입
        signUpService.SignUp(signUpDto);

        // 2️2. 로그인 시에도 AES 암호화된 값 사용
        LoginDto loginDto = new LoginDto();
        loginDto.setUserId("login@naver.com");
        loginDto.setPassword(encryptedPassword); // 클라이언트처럼 암호화된 비밀번호

        // 3️⃣ 로그인 수행 → 내부에서 복호화 + 해시 비교
        TokenDto tokenDto = loginService.login(loginDto);

        // 4️⃣ 결과 확인
        assertNotNull(tokenDto.getAccessToken());
        assertNotNull(tokenDto.getRefreshToken());

        log.info("✅ AccessToken 은??: {}", tokenDto.getAccessToken());
        log.info("✅ RefreshToken 은???: {}", tokenDto.getRefreshToken());
    }
}