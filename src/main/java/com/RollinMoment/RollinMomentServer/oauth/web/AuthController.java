package com.RollinMoment.RollinMomentServer.oauth.web;

import com.RollinMoment.RollinMomentServer.exception.member.ResponseUtil;
import com.RollinMoment.RollinMomentServer.jwt.JwtTokenProvider;
import com.RollinMoment.RollinMomentServer.member.dto.TokenDto;
import com.RollinMoment.RollinMomentServer.member.dto.UserInfoDto;
import com.RollinMoment.RollinMomentServer.member.service.UserService;
import com.RollinMoment.RollinMomentServer.oauth.kakao.dto.KakaoLoginReponseDto;
import com.RollinMoment.RollinMomentServer.oauth.kakao.dto.KakaoUserDto;
import com.RollinMoment.RollinMomentServer.oauth.kakao.service.KakaoOauthService;
import com.RollinMoment.RollinMomentServer.response.member.ResponseJoinDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "소셜로그인", description = "카카오, 네이버, 애플")
public class AuthController {
    private final KakaoOauthService kakaoOauthService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;


    @Operation(summary = "카카오 로그인", description = "카카오 OAuth를 사용해서 로그인한다.")
    @ApiResponse(responseCode = "200", description = "카카오 로그인 성공")
    @ApiResponse(responseCode = "400", description = "카카오 로그인 실패")
    @PostMapping("/login/kakao")
    public ResponseEntity<ResponseJoinDto> kakaoLogin(@RequestHeader("Authorization") String kakaoAccessToken) {
        try {
            kakaoAccessToken = kakaoAccessToken.replace("Bearer ", "").trim();

            // 사용자 정보 요청
            KakaoUserDto kakaoUserDto = kakaoOauthService.getUserProfile(kakaoAccessToken);
            TokenDto tokenDto = kakaoOauthService.processLogin(kakaoUserDto);

            KakaoLoginReponseDto response = new KakaoLoginReponseDto(
                    tokenDto.getAccessToken(),
                    tokenDto.getRefreshToken(),
                    new UserInfoDto(
                            kakaoUserDto.getId(),
                            kakaoUserDto.getNickname()
                    )
            );

            Map<String, Object> body = new HashMap<>();
            body.put("data", response);

            return ResponseUtil.SuccessDataResponse(body);

        } catch (Exception e) {
            return ResponseUtil.ErrorResponse(-1, "카카오 로그인 실패: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @Operation(summary = "카카오 로그아웃", description = "카카오 소셜 로그인 사용자의 로그아웃 처리")
    @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    @ApiResponse(responseCode = "400", description = "로그아웃 실패")
    @PostMapping("/logout/kakao")
    public ResponseEntity<ResponseJoinDto> kakaoLogout(HttpServletRequest request) {
        try {
            String accessToken = jwtTokenProvider.getHeaderToken(request, JwtTokenProvider.ACCESS_TOKEN_HEADER);
            String userId = jwtTokenProvider.getUserIdFromToken(accessToken);
            userService.logout(userId);

            return ResponseUtil.SuccessResponse("카카오 로그아웃 완료");
        } catch (Exception e) {
            return ResponseUtil.ErrorResponse(-1, "카카오 로그아웃 실패: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "카카오 회원 탈퇴", description = "카카오 로그인 사용자의 계정을 탈퇴 처리합니다.")
    @ApiResponse(responseCode = "200", description = "회원탈퇴 성공")
    @ApiResponse(responseCode = "400", description = "회원탈퇴 실패")
    @PutMapping("/withdraw/kakao")
    public ResponseEntity<ResponseJoinDto> withdrawKakaoUser(HttpServletRequest request) {
        try {
            String accessToken = jwtTokenProvider.getHeaderToken(request, JwtTokenProvider.ACCESS_TOKEN_HEADER);
            String userId = jwtTokenProvider.getUserIdFromToken(accessToken);

            userService.delete(userId);

            return ResponseUtil.SuccessResponse("카카오 회원 탈퇴 완료");
        } catch (Exception e) {
            return ResponseUtil.ErrorResponse(-1, "카카오 회원 탈퇴 실패: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}