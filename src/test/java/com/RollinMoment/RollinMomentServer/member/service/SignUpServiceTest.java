package com.RollinMoment.RollinMomentServer.member.service;

import com.RollinMoment.RollinMomentServer.common.util.AESUtil;
import com.RollinMoment.RollinMomentServer.member.dto.SignUpDto;
import com.RollinMoment.RollinMomentServer.member.entity.UserEntity;
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
class SignUpServiceTest {
    @Autowired
    private AESUtil aesUtil;

    @Autowired
    private SignUpService signUpService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    @Test
    @DisplayName("회원가입")
    void testSignUpWithEncryptedPassword() {
        // GIVE
        String rawPassword = "password123";
        String encryptedPassword = aesUtil.encrypt(rawPassword);
        log.info("암호화 비번 : {}", encryptedPassword);

        SignUpDto dto = new SignUpDto();
        dto.setUserId("test1@example.com");
        dto.setPassword(encryptedPassword);
        dto.setNickname("테스터");
        dto.setDeviceId("test-device");
        dto.setAlarm(true);
        dto.setGender("FEMALE");
        dto.setProvider("ROLLINMOMENT");
        dto.setOsType("ANDROID");

        // WHEN
        signUpService.SignUp(dto);

        // THEN
        UserEntity saved = userRepository.findByUserId("test1@example.com").orElseThrow();


        String decrypted = aesUtil.decrypt(encryptedPassword);
        log.info(" 복호화된 비밀번호 = {}:"  , decrypted);

        assertEquals(rawPassword, decrypted); // 복호화 검증

        // 7️⃣ DB에 저장된 해시된 비밀번호가 맞는지 확인
        boolean matches = bCryptPasswordEncoder.matches(rawPassword, saved.getPassword());
        log.info("DB에 저장된 비밀번호 해시 = {}" , saved.getPassword());
        log.info("비교 결과 = {}" , matches);

        assertTrue(matches); // 최종 검증
    }
}