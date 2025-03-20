package com.RollinMoment.RollinMomentServer.member.contorller;


import com.RollinMoment.RollinMomentServer.config.ResponseUtil;
import com.RollinMoment.RollinMomentServer.member.dto.SignUpDto;
import com.RollinMoment.RollinMomentServer.member.service.LoginService;
import com.RollinMoment.RollinMomentServer.member.service.SignUpService;
import com.RollinMoment.RollinMomentServer.response.member.ResponseJoinDto;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 로그인 회원가입 컨트롤러
 */
@RequestMapping("/api/v1/auth")
@RestController
@AllArgsConstructor
public class SignUpController {

    private final SignUpService signUpService;



    @PostMapping("/signUp")
    public ResponseEntity<ResponseJoinDto> joinProcess(@RequestBody SignUpDto signUpDto) {
        signUpService.SignUp(signUpDto);
        //  회원가입 성공 (code: 0)
        return ResponseUtil.SuccessResponse("회원가입 성공");
    }
}
