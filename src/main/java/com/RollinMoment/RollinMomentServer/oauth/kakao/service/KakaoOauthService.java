package com.RollinMoment.RollinMomentServer.oauth.kakao.service;


import com.RollinMoment.RollinMomentServer.jwt.JwtTokenProvider;
import com.RollinMoment.RollinMomentServer.oauth.kakao.dto.KakaoUserDto;
import com.RollinMoment.RollinMomentServer.member.dto.TokenDto;
import com.RollinMoment.RollinMomentServer.member.entity.UserEntity;
import com.RollinMoment.RollinMomentServer.member.entity.type.Gender;
import com.RollinMoment.RollinMomentServer.member.entity.type.Ostype;
import com.RollinMoment.RollinMomentServer.member.entity.type.Provider;
import com.RollinMoment.RollinMomentServer.member.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoOauthService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RestTemplate restTemplate;

    public KakaoUserDto getUserProfile(String kakaoAccessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " +kakaoAccessToken);

        HttpEntity<?> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    "https://kapi.kakao.com/v2/user/me",
                    HttpMethod.GET,
                    request,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();

                System.out.println("Kakao API 응답: " + responseBody);

                // 데이터 파싱
                String userId = String.valueOf(responseBody.get("id"));
                Map<String, Object> kakaoAccount = (Map<String, Object>) responseBody.get("kakao_account");
                Map<String, Object> profile = kakaoAccount != null ? (Map<String, Object>) kakaoAccount.get("profile") : null;
                String nickname = profile != null ? (String) profile.get("nickname") : null;

                // KakaoUserDto 객체 생성
                return KakaoUserDto.builder()
                        .id(userId)
                        .nickname(nickname)
                        .build();
            } else {
                throw new RuntimeException("Kakao API 응답 실패: " + response.getStatusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch Kakao user profile: " + e.getMessage(), e);
        }
    }




    public TokenDto processLogin(KakaoUserDto kakaoUserDto) {

        UserEntity userEntity = userRepository.findByUserId(kakaoUserDto.getId())
                .orElseGet(() -> {
                    // 회원가입
                    UserEntity newUser = UserEntity.builder()
                            .userId(kakaoUserDto.getId())
                            .password("kakao_dummy_password") // 소셜 로그인은 password 없음
                            .nickname(kakaoUserDto.getNickname() != null ? kakaoUserDto.getNickname() : "카카오유저")
                            .deviceId(kakaoUserDto.getDeviceId()!= null ? kakaoUserDto.getDeviceId() : "카카오고유번호")
                            .alarm(kakaoUserDto.isAlarm()) // 기본 알림 설정 ON
                            .gender(Gender.valueOf(kakaoUserDto.getGender().toUpperCase()))
                            .ostype(Ostype.valueOf(kakaoUserDto.getOsType().toUpperCase()))
                            .provider(Provider.KAKAO)
                            .build();

                    return userRepository.save(newUser);
                });

        // JWT 발급
        String accessToken = jwtTokenProvider.generateAccessToken(
                userEntity.getUserId()
        );
        String refreshToken = jwtTokenProvider.generateRefreshToken();

        return new TokenDto(accessToken, refreshToken);
    }
}
