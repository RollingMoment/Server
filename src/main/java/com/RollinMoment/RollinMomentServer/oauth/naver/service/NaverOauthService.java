package com.RollinMoment.RollinMomentServer.oauth.naver.service;

import com.RollinMoment.RollinMomentServer.jwt.JwtTokenProvider;
import com.RollinMoment.RollinMomentServer.member.dto.TokenDto;
import com.RollinMoment.RollinMomentServer.member.entity.UserAuthority;
import com.RollinMoment.RollinMomentServer.member.entity.UserEntity;
import com.RollinMoment.RollinMomentServer.member.entity.type.Gender;
import com.RollinMoment.RollinMomentServer.member.entity.type.Ostype;
import com.RollinMoment.RollinMomentServer.member.entity.type.Provider;
import com.RollinMoment.RollinMomentServer.member.entity.type.UserStatus;
import com.RollinMoment.RollinMomentServer.member.repository.UserAuthorityRepository;
import com.RollinMoment.RollinMomentServer.member.repository.UserRepository;
import com.RollinMoment.RollinMomentServer.oauth.naver.dto.NaverOauthKeyDto;
import com.RollinMoment.RollinMomentServer.oauth.naver.dto.NaverResponseDto;
import com.RollinMoment.RollinMomentServer.oauth.naver.dto.NaverUserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NaverOauthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RestTemplate restTemplate;
    private final UserAuthorityRepository userAuthorityRepository;
    private final NaverOauthKeyDto naverOauthKeyDto;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public NaverResponseDto getUserProfileNaver(String naverAccessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + naverAccessToken);

        HttpEntity<?> request = new HttpEntity<>(headers);
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    "https://openapi.naver.com/v1/nid/me",
                    HttpMethod.GET,
                    request,
                    new ParameterizedTypeReference<>() {}
            );
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                Map<String, Object> naverResponse = (Map<String, Object>) body.get("response");

                return NaverResponseDto.builder()
                        .userId((String) naverResponse.get("email"))
                        .nickname((String) naverResponse.get("nickname"))
                        .gender((String) naverResponse.get("gender"))
                        .build();
            } else {
                throw new RuntimeException("네이버 유저 정보 조회 실패");
            }


        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("네이버 실패: " + e.getMessage(), e);
        }
    }

    public TokenDto processLogin(NaverResponseDto naverResponseDto ,NaverUserDto naverUserDto) {
        UserEntity userEntity = userRepository.findByUserId(naverResponseDto.getUserId())
                .orElseGet(() -> {
                    // 회원가입
                    UserEntity newUser = UserEntity.builder()
                            .userId(naverResponseDto.getUserId())
                            .password(bCryptPasswordEncoder.encode("naver_dummy_password"))
                            .nickname(naverResponseDto.getNickname() != null ? naverResponseDto.getNickname() : "네이버 유저")
                            .deviceId(naverUserDto.getDeviceId() != null ? naverUserDto.getDeviceId() : "네이버 고유번호")
                            .alarm(naverUserDto.isAlarm()) // 기본 알림 설정 ON
                            .gender(naverResponseDto.getGender() != null
                                    ? Gender.valueOf(naverResponseDto.getGender().toUpperCase())
                                    : Gender.NONE // ✅ null일 경우 기본값 Gender.NONE
                            )
                            .ostype(Ostype.valueOf(naverUserDto.getOsType().toUpperCase()))
                            .provider(Provider.NAVER)
                            .status(UserStatus.ACTIVE)
                            .deleteTime(null)
                            .build();

                    return userRepository.save(newUser);
                });

        // JWT 발급
        String accessToken = jwtTokenProvider.generateAccessToken(userEntity.getUserId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(userEntity.getUserId());
        Date expiryDate = jwtTokenProvider.getRefreshTokenExpiryDate();
        userAuthorityRepository.save(new UserAuthority(
                userEntity.getUserId(),
                refreshToken,
                expiryDate
        ));

        return new TokenDto(accessToken, refreshToken);
    }

    public TokenDto loginNaver(String naverAccessToken) {
        NaverResponseDto naverUserInfo = getUserProfileNaver(naverAccessToken);
        UserEntity user = userRepository.findByUserId(naverUserInfo.getUserId())
                .orElseThrow(() -> new RuntimeException("가입된 사용자가 아닙니다."));
        String accessToken = jwtTokenProvider.generateAccessToken(user.getUserId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUserId());
        Date refreshTokenExpiry = jwtTokenProvider.getRefreshTokenExpiryDate();
        userAuthorityRepository.updateTokenByUserId(refreshToken, refreshTokenExpiry,user.getUserId());
        return new TokenDto(accessToken, refreshToken);
    }


    public void unlinkNaver(String accessToken) {
        String url = "https://nid.naver.com/oauth2.0/token?grant_type=delete&client_id=" +
                naverOauthKeyDto.getClientId() + "&client_secret=" + naverOauthKeyDto.getClientSecret() + "&access_token=" + accessToken + "&service_provider=NAVER";
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);
            log.info("네이버 연결 해제 성공: {}", response.getBody());
        } catch (Exception e) {
            log.error("네이버 연결 해제 실패: {}", e.getMessage());
            throw new RuntimeException("네이버 연결 끊기에 실패했습니다.");
        }
    }
}
