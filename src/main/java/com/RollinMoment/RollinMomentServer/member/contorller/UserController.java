package com.RollinMoment.RollinMomentServer.member.contorller;


import com.RollinMoment.RollinMomentServer.exception.member.ResponseUtil;
import com.RollinMoment.RollinMomentServer.jwt.JwtTokenProvider;
import com.RollinMoment.RollinMomentServer.member.dto.LoginDto;
import com.RollinMoment.RollinMomentServer.member.dto.SignUpDto;
import com.RollinMoment.RollinMomentServer.member.dto.TokenDto;
import com.RollinMoment.RollinMomentServer.member.service.LoginService;
import com.RollinMoment.RollinMomentServer.member.service.SignUpService;
import com.RollinMoment.RollinMomentServer.response.member.ResponseJoinDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequestMapping("/api/v1/auth")
@RestController
@AllArgsConstructor
@Tag(name = "Auth", description = "이메일 회원가입 및 로그인")
public class UserController {

    private final SignUpService signUpService;
    private final LoginService loginService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "회원가입", description = "사용자 회원가입 아이디 이메일")
    @PostMapping("/signUp")
    public ResponseEntity<ResponseJoinDto> joinProcess(@RequestBody SignUpDto signUpDto) {
        signUpService.SignUp(signUpDto);
        //  회원가입 성공 (code: 0)
        return ResponseUtil.SuccessResponse("회원가입 성공");
    }
    @Operation(summary = "로그인", description = "사용자 로그인 입니다.")
    @PostMapping("/signIn")
    public ResponseEntity<ResponseJoinDto> login(@RequestBody LoginDto loginDto) {
        TokenDto tokenDto = loginService.login(loginDto);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("accessToken", tokenDto.getAccessToken());
        responseBody.put("refreshToken", tokenDto.getRefreshToken());
        return ResponseUtil.SuccessDataResponse("토큰 갱신 성공", responseBody);
    }

    @Operation(summary = "로그아웃", description = "사용자 로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<ResponseJoinDto> logout(HttpServletRequest request) {
        String token = jwtTokenProvider.getHeaderToken(request, JwtTokenProvider.ACCESS_TOKEN_HEADER);
        String userId = jwtTokenProvider.getUserIdFromToken(token);

        loginService.logout(userId); // ✅ 이 메서드에서 Refresh Token 삭제 처리

        return ResponseUtil.SuccessResponse("로그아웃 되었습니다.");
    }
}
