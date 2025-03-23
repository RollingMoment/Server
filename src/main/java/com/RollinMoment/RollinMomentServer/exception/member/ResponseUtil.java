package com.RollinMoment.RollinMomentServer.exception.member;

import com.RollinMoment.RollinMomentServer.response.member.ResponseJoinDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResponseUtil {

    // 성공 응답 (`code: 0`)
    public static ResponseEntity<ResponseJoinDto> SuccessResponse(String message) {
        ResponseJoinDto.Meta meta = new ResponseJoinDto.Meta(0, "success");
        Map<String, Object> body = new HashMap<>();
        body.put("message", message);
        ResponseJoinDto response = new ResponseJoinDto(meta, body);
        return ResponseEntity.ok(response);
    }

    // 성공 응답 (`code: 0` + 데이터 포함)
    public static ResponseEntity<ResponseJoinDto> SuccessDataResponse(Map<String, Object> data) {
        ResponseJoinDto.Meta meta = new ResponseJoinDto.Meta(0, "success");
        Map<String, Object> body = new HashMap<>();
        return ResponseEntity.ok(new ResponseJoinDto(meta, body));
    }

    // 오류 응답 (`code: -1` or 기타 코드)
    public static ResponseEntity<ResponseJoinDto> ErrorResponse(int code, String message , HttpStatus status) {
        ResponseJoinDto.Meta meta = new ResponseJoinDto.Meta(code, "error");
        Map<String, Object> body = new HashMap<>();
        body.put("message", message);
        ResponseJoinDto response = new ResponseJoinDto(meta, body);
        return ResponseEntity.status(status).body(response);
    }
}
