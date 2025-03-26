package com.RollinMoment.RollinMomentServer.oauth.kakao.service;


import com.RollinMoment.RollinMomentServer.jwt.JwtTokenProvider;
import com.RollinMoment.RollinMomentServer.member.entity.UserAuthority;
import com.RollinMoment.RollinMomentServer.member.entity.type.UserStatus;
import com.RollinMoment.RollinMomentServer.member.repository.UserAuthorityRepository;
import com.RollinMoment.RollinMomentServer.oauth.kakao.dto.KakaoResponseDto;
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
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoOauthService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RestTemplate restTemplate;
    private final UserAuthorityRepository userAuthorityRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public KakaoResponseDto getUserProfile(String kakaoAccessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + kakaoAccessToken);
        HttpEntity<?> request = new HttpEntity<>(headers);
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    "https://kapi.kakao.com/v2/user/me",
                    HttpMethod.GET,
                    request,
                    new ParameterizedTypeReference<>(){}
            );
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                log.info("Kakao API 응답 = {}", responseBody);

                Map<String, Object> kakaoAccount = (Map<String, Object>) responseBody.get("kakao_account");
                String email = kakaoAccount != null ? (String) kakaoAccount.get("email") : null;

                Map<String, Object> profile = kakaoAccount != null ? (Map<String, Object>) kakaoAccount.get("profile") : null;
                String nickname = profile != null ? (String) profile.get("nickname") : null;

                return KakaoResponseDto.builder()
                        .userId(email)
                        .nickname(nickname)
                        .build();

            } else {
                throw new RuntimeException("카카오 응답 실패: " + response.getStatusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("카카오 사용자 정보 파싱 실패: " + e.getMessage(), e);
        }
    }

    public TokenDto processLogin(KakaoUserDto kakaoUserDto, KakaoResponseDto kakaoResponseDto) {
        UserEntity userEntity = userRepository.findByUserId(kakaoResponseDto.getUserId())
                .orElseGet(() -> {
                    UserEntity newUser = UserEntity.builder()
                            .userId(kakaoResponseDto.getUserId())
                            .password(bCryptPasswordEncoder.encode("kakao_dummy_password"))
                            .nickname(kakaoResponseDto.getNickname() != null ? kakaoResponseDto.getNickname() : "카카오유저")
                            .deviceId(kakaoUserDto.getDeviceId() != null ? kakaoUserDto.getDeviceId() : "카카오고유번호")
                            .alarm(kakaoUserDto.isAlarm())
                            .gender(Gender.NONE)
                            .ostype(Ostype.valueOf(kakaoUserDto.getOsType().toUpperCase()))
                            .provider(Provider.KAKAO)
                            .status(UserStatus.ACTIVE)
                            .deleteTime(null)
                            .build();
                    return userRepository.save(newUser);
                });

        //  JWT 생성
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

    public void unlinkKakao(String kakaoAccessToken) {
        String url = "https://kapi.kakao.com/v1/user/unlink";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + kakaoAccessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> request = new HttpEntity<>("", headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            log.info("카카오 연결 해제 성공: {}", response.getBody());
        } catch (Exception e) {
            log.error("카카오 연결 해제 실패: {}", e.getMessage());
            throw new RuntimeException("카카오 연결 끊기에 실패했습니다.");
        }
    }

    public TokenDto loginKakao(String kakaoAccessToken) {
        KakaoResponseDto kakaoUserInfo = getUserProfile(kakaoAccessToken);
        UserEntity user = userRepository.findByUserId(kakaoUserInfo.getUserId())
                .orElseThrow(() -> new RuntimeException("가입된 사용자가 아닙니다."));

        String accessToken = jwtTokenProvider.generateAccessToken(user.getUserId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUserId());
        Date refreshTokenExpiry = jwtTokenProvider.getRefreshTokenExpiryDate();
        userAuthorityRepository.updateTokenByUserId(refreshToken, refreshTokenExpiry,user.getUserId());
        return new TokenDto(accessToken, refreshToken);
    }
}
