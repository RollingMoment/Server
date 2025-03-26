package com.RollinMoment.RollinMomentServer.member.web;


import com.RollinMoment.RollinMomentServer.common.util.ResponseUtil;
import com.RollinMoment.RollinMomentServer.jwt.JwtTokenProvider;
import com.RollinMoment.RollinMomentServer.member.dto.LoginDto;
import com.RollinMoment.RollinMomentServer.member.dto.SignUpDto;
import com.RollinMoment.RollinMomentServer.member.dto.TokenDto;
import com.RollinMoment.RollinMomentServer.member.service.UserService;
import com.RollinMoment.RollinMomentServer.member.service.SignUpService;
import com.RollinMoment.RollinMomentServer.oauth.kakao.dto.KakaoResponseDto;
import com.RollinMoment.RollinMomentServer.oauth.kakao.dto.KakaoUserDto;
import com.RollinMoment.RollinMomentServer.oauth.kakao.service.KakaoOauthService;
import com.RollinMoment.RollinMomentServer.oauth.naver.dto.NaverResponseDto;
import com.RollinMoment.RollinMomentServer.oauth.naver.dto.NaverUserDto;
import com.RollinMoment.RollinMomentServer.oauth.naver.service.NaverOauthService;
import com.RollinMoment.RollinMomentServer.response.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequestMapping("/api/v1/auth")
@RestController
@AllArgsConstructor
@Tag(name = "로그인 API", description = "회원가입 , 로그인 , 회원탈퇴 , 카카오, 애플, 네이버")
public class UserController {

    private final SignUpService signUpService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final KakaoOauthService kakaoOauthService;
    private final NaverOauthService naverOauthService;

    @Operation(summary = "회원가입", description = "사용자 회원가입 아이디 이메일")
    @ApiResponse(responseCode = "200", description = "회원가입 성공")
    @ApiResponse(responseCode = "400", description = "회원가입 실패")
    @PostMapping("/signUp")
    public ResponseEntity<ResponseDto> joinProcess(@RequestBody SignUpDto signUpDto) {
        try {
            signUpService.SignUp(signUpDto);
            return ResponseUtil.SuccessResponse("회원가입 성공");
        } catch (Exception e) {
            return ResponseUtil.ErrorResponse(-101, e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "로그인", description = "사용자 로그인 입니다.")
    @ApiResponse(responseCode = "200", description = "로그인 성공")
    @ApiResponse(responseCode = "400", description = "로그인 실패")
    @PostMapping("/signIn")
    public ResponseEntity<ResponseDto> login(@RequestBody LoginDto loginDto) {
        try {
            TokenDto tokenDto = userService.login(loginDto);
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("accessToken", tokenDto.getAccessToken());
            responseBody.put("refreshToken", tokenDto.getRefreshToken());
            return ResponseUtil.SuccessDataResponse(responseBody);
        } catch (IllegalArgumentException e) {
            return ResponseUtil.ErrorResponse(-1, e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "로그아웃", description = "사용자 로그아웃")
    @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    @ApiResponse(responseCode = "400", description = "로그아웃 실패")
    @PostMapping("/signOut")
    public ResponseEntity<ResponseDto> logout(HttpServletRequest request) {
        try {
            String token = jwtTokenProvider.getHeaderToken(request, JwtTokenProvider.ACCESS_TOKEN_HEADER);

            if (token == null || !jwtTokenProvider.validateToken(token)) {
                return ResponseUtil.ErrorResponse(-1, "유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED);
            }
            String userId = jwtTokenProvider.getUserIdFromToken(token);
            userService.logout(userId);
            return ResponseUtil.SuccessResponse("로그아웃 되었습니다.");
        } catch (Exception e) {
            return ResponseUtil.ErrorResponse(-1, "로그아웃 실패: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "회원 탈퇴", description = "로그인한 사용자가 자신의 계정을 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공")
    @ApiResponse(responseCode = "400", description = "회원 탈퇴 실패")
    @PutMapping("/withdraw")
    public ResponseEntity<ResponseDto> withdrawUser(@RequestParam String userId) {
        try {
            userService.delete(userId);
            return ResponseUtil.SuccessResponse("회원탈퇴 완료 되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseUtil.ErrorResponse(-1, "존재하지 않는 사용자입니다.", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return ResponseUtil.ErrorResponse(-1, "회원 탈퇴 중 오류가 발생했습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "카카오 회원가입", description = "카카오 회원가입입니다.")
    @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공")
    @ApiResponse(responseCode = "400", description = "회원 탈퇴 실패")
    @PostMapping("/signUp/kakao")
    public ResponseEntity<ResponseDto> kakaoSignUp(
            @RequestHeader("Authorization") String kakaoAccessToken,
            @RequestBody KakaoUserDto kakaoUserDto
    ) {
        try {
            kakaoAccessToken = kakaoAccessToken.replace("Bearer ", "").trim();

            // 카카오 정보 요청
            KakaoResponseDto kakaoResponseDto = kakaoOauthService.getUserProfile(kakaoAccessToken);

            // 회원가입 및 로그인 처리
            TokenDto tokenDto = kakaoOauthService.processLogin(kakaoUserDto, kakaoResponseDto);

            // 응답 데이터 구성
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("id", kakaoResponseDto.getUserId());
            responseBody.put("nickname", kakaoResponseDto.getNickname());
            responseBody.put("accessToken", tokenDto.getAccessToken());
            responseBody.put("refreshToken", tokenDto.getRefreshToken());

            return ResponseUtil.SuccessDataResponse(responseBody);

        } catch (Exception e) {
            return ResponseUtil.ErrorResponse(-1, "카카오 회원가입 실패: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @Operation(summary = "카카오 로그인", description = "카카오 로그인 입니다.")
    @ApiResponse(responseCode = "200", description = "로그인 성공")
    @ApiResponse(responseCode = "400", description = "로그인 실패")
    @PostMapping("/signIn/kakao")
    public ResponseEntity<?> kakaoLogin(@RequestHeader("Authorization") String kakaoAccessToken) {
        try {
            kakaoAccessToken = kakaoAccessToken.replace("Bearer ", "").trim();
            TokenDto tokenDto = kakaoOauthService.loginKakao(kakaoAccessToken);

            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", tokenDto.getAccessToken());
            response.put("refreshToken", tokenDto.getRefreshToken());

            return ResponseUtil.SuccessDataResponse(response);

        } catch (Exception e) {
            return ResponseUtil.ErrorResponse(-1, "카카오 로그인 실패: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
    @Operation(summary = "카카오 로그아웃", description = "카카오 소셜 로그인 사용자의 로그아웃 처리")
    @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    @ApiResponse(responseCode = "400", description = "로그아웃 실패")
    @PostMapping("/signOut/kakao")
    public ResponseEntity<ResponseDto> kakaoLogout(HttpServletRequest request) {
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
    public ResponseEntity<ResponseDto> withdrawKakaoUser(HttpServletRequest request) {
        try {
            String accessToken = jwtTokenProvider.getHeaderToken(request, JwtTokenProvider.ACCESS_TOKEN_HEADER);
            String userId = jwtTokenProvider.getUserIdFromToken(accessToken);
            kakaoOauthService.unlinkKakao(accessToken);
            userService.delete(userId);
            return ResponseUtil.SuccessResponse("카카오 회원 탈퇴 완료");
        } catch (Exception e) {
            return ResponseUtil.ErrorResponse(-1, "카카오 회원 탈퇴 실패: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "네이버 회원가입", description = "네이버 회원가입 구현입니다.")
    @ApiResponse(responseCode = "200", description = "네이버 회원가입 성공")
    @ApiResponse(responseCode = "400", description = "네이버 회원가입 실패")
    @PostMapping("/signUp/naver")
    public ResponseEntity<ResponseDto> naverLogin(
            @RequestHeader("Authorization") String naverAccessToken,
            @RequestBody NaverUserDto naverUserDto) {
        try {
            naverAccessToken = naverAccessToken.replace("Bearer ", "").trim();

            NaverResponseDto naverResponseDto = naverOauthService.getUserProfileNaver(naverAccessToken);

            TokenDto tokenDto = naverOauthService.processLogin(naverResponseDto , naverUserDto);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("id", naverResponseDto.getUserId());
            responseBody.put("nickname", naverResponseDto.getNickname());
            responseBody.put("accessToken", tokenDto.getAccessToken());
            responseBody.put("refreshToken", tokenDto.getRefreshToken());
            return ResponseUtil.SuccessDataResponse(responseBody);
        } catch (Exception e) {
            return ResponseUtil.ErrorResponse(-1, "네이버 로그인 실패: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @Operation(summary = "네이버 로그인", description = "네이버 로그인 구현입니다.")
    @ApiResponse(responseCode = "200", description = "네이버 로그인 성공")
    @ApiResponse(responseCode = "400", description = "네이버 로그인 실패")
    @PostMapping("/signIn/naver")
    public ResponseEntity<ResponseDto> naverLogin(
            @RequestHeader("Authorization") String naverAccessToken) {
        try {
            naverAccessToken = naverAccessToken.replace("Bearer ", "").trim();
            TokenDto tokenDto = naverOauthService.loginNaver(naverAccessToken);
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("accessToken", tokenDto.getAccessToken());
            responseBody.put("refreshToken", tokenDto.getRefreshToken());
            return ResponseUtil.SuccessDataResponse(responseBody);
        } catch (Exception e) {
            return ResponseUtil.ErrorResponse(-1, "네이버 로그인 실패: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @Operation(summary = "네이버 로그아웃", description = "네이버 소셜 로그인 사용자의 로그아웃 처리")
    @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    @ApiResponse(responseCode = "400", description = "로그아웃 실패")
    @PostMapping("/signOut/naver")
    public ResponseEntity<ResponseDto> naverLogout(HttpServletRequest request) {
        try {
            String accessToken = jwtTokenProvider.getHeaderToken(request, JwtTokenProvider.ACCESS_TOKEN_HEADER);
            String userId = jwtTokenProvider.getUserIdFromToken(accessToken);
            userService.logout(userId);
            return ResponseUtil.SuccessResponse("네이버 로그아웃 완료");
        } catch (Exception e) {
            return ResponseUtil.ErrorResponse(-1, "네이버 로그아웃 실패: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @Operation(summary = "네이버 회원 탈퇴", description = "네이버 로그인 사용자의 계정을 탈퇴 처리합니다.")
    @ApiResponse(responseCode = "200", description = "회원탈퇴 성공")
    @ApiResponse(responseCode = "400", description = "회원탈퇴 실패")
    @PutMapping("/withdraw/naver")
    public ResponseEntity<ResponseDto> withdrawNaverUser(HttpServletRequest request) {
        try {
            String accessToken = jwtTokenProvider.getHeaderToken(request, JwtTokenProvider.ACCESS_TOKEN_HEADER);
            String userId = jwtTokenProvider.getUserIdFromToken(accessToken);
            naverOauthService.unlinkNaver(accessToken);
            userService.delete(userId);
            return ResponseUtil.SuccessResponse("네이버 회원 탈퇴 완료");
        } catch (Exception e) {
            return ResponseUtil.ErrorResponse(-1, "네이버 회원 탈퇴 실패: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
