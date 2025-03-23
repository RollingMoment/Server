package com.RollinMoment.RollinMomentServer.exception.member;

import com.RollinMoment.RollinMomentServer.response.member.ResponseJoinDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class GlobalExceptionHandler {

    // ✅ 이미 존재하는 이메일 (`code: 101`)
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ResponseJoinDto> handleUsernameAlreadyExists(UsernameAlreadyExistsException ex) {
        return ResponseUtil.ErrorResponse(101, ex.getMessage());
    }

    // ✅ 기타 예외 발생 시 (`code: -1`)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseJoinDto> handleGlobalException(Exception ex) {
        return ResponseUtil.ErrorResponse(-1, "알 수 없는 오류가 발생했습니다.");
    }

}
