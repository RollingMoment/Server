package com.RollinMoment.RollinMomentServer.member.web;


import com.RollinMoment.RollinMomentServer.exception.member.ResponseUtil;
import com.RollinMoment.RollinMomentServer.jwt.JwtTokenProvider;
import com.RollinMoment.RollinMomentServer.member.dto.LoginDto;
import com.RollinMoment.RollinMomentServer.member.dto.SignUpDto;
import com.RollinMoment.RollinMomentServer.member.dto.TokenDto;
import com.RollinMoment.RollinMomentServer.member.entity.UserAuthority;
import com.RollinMoment.RollinMomentServer.member.repository.UserAuthorityRepository;
import com.RollinMoment.RollinMomentServer.member.service.UserService;
import com.RollinMoment.RollinMomentServer.member.service.SignUpService;
import com.RollinMoment.RollinMomentServer.response.member.ResponseJoinDto;
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
@Tag(name = "롤링모먼트 이메일 로그인", description = "회원가입 , 로그인 , 회원탈퇴")
public class UserController {

    private final SignUpService signUpService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserAuthorityRepository userAuthorityRepository;

    @Operation(summary = "회원가입", description = "사용자 회원가입 아이디 이메일")
    @ApiResponse(responseCode = "200", description = "회원가입 성공")
    @ApiResponse(responseCode = "400", description = "회원가입 실패")
    @PostMapping("/signUp")
    public ResponseEntity<ResponseJoinDto> joinProcess(@RequestBody SignUpDto signUpDto) {
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
    public ResponseEntity<ResponseJoinDto> login(@RequestBody LoginDto loginDto) {
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
    @PostMapping("/logout")
    public ResponseEntity<ResponseJoinDto> logout(HttpServletRequest request) {
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
    public ResponseEntity<ResponseJoinDto> withdrawUser(@RequestParam String userId) {
        try {
            userService.delete(userId);
            return ResponseUtil.SuccessResponse("회원탈퇴 완료 되었습니다.");
        } catch (IllegalArgumentException e) {
            // 예: 사용자가 존재하지 않을 경우 등
            return ResponseUtil.ErrorResponse(-1, "존재하지 않는 사용자입니다.", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return ResponseUtil.ErrorResponse(-1, "회원 탈퇴 중 오류가 발생했습니다.", HttpStatus.BAD_REQUEST);
        }
    }
    @Operation(summary = "자동 로그인", description = "Refresh Token 으로 Access Token 재발급")
    @PostMapping("/reissue")
    public ResponseEntity<ResponseJoinDto> reissue(@RequestHeader("Authorization") String refreshToken) {
        try {
            refreshToken = jwtTokenProvider.stripBearerPrefix(refreshToken);

            // RefreshToken 검증
            if (!jwtTokenProvider.validateToken(refreshToken)) {
                return ResponseUtil.ErrorResponse(-1, "Refresh Token이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED);
            }

            String userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
            UserAuthority authority = userAuthorityRepository.findByUserId(userId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

            if (!authority.getRefreshToken().equals(refreshToken)) {
                return ResponseUtil.ErrorResponse(-1, "Refresh Token이 일치하지 않습니다.", HttpStatus.UNAUTHORIZED);
            }

            // AccessToken 재발급
            String newAccessToken = jwtTokenProvider.generateAccessToken(userId);

            Map<String, Object> data = new HashMap<>();
            data.put("accessToken", newAccessToken);

            return ResponseUtil.SuccessDataResponse(data);
        } catch (Exception e) {
            return ResponseUtil.ErrorResponse(-1, "재발급 실패: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
