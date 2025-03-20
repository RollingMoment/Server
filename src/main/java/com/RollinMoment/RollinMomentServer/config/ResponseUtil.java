package com.RollinMoment.RollinMomentServer.config;

import com.RollinMoment.RollinMomentServer.response.member.ResponseJoinDto;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResponseUtil {
    // 성공 응답 (`code: 0`)
    public static ResponseEntity<ResponseJoinDto> SuccessResponse(String message) {
        ResponseJoinDto.Meta meta = new ResponseJoinDto.Meta(0, "success");
        Map<String, Object> body = new HashMap<>(); // 데이터가 없는 경우 빈 body 반환

        ResponseJoinDto response = new ResponseJoinDto(meta, body);
        return ResponseEntity.ok(response); // 항상 HTTP 200 OK
    }

    // 성공 응답 (`code: 0` + 데이터 포함)
    public static ResponseEntity<ResponseJoinDto> SuccessDataResponse(String message, Map<String, Object> data) {
        ResponseJoinDto.Meta meta = new ResponseJoinDto.Meta(0, "success");

        ResponseJoinDto response = new ResponseJoinDto(meta, data != null ? data : new HashMap<>());
        return ResponseEntity.ok(response);
    }

    // 오류 응답 (`code: -1` or 기타 코드)
    public static ResponseEntity<ResponseJoinDto> ErrorResponse(int code, String message) {
        ResponseJoinDto.Meta meta = new ResponseJoinDto.Meta(code, "error");
        Map<String, Object> body = new HashMap<>(); // 오류 발생 시 빈 body 반환

        ResponseJoinDto response = new ResponseJoinDto(meta, body);
        return ResponseEntity.ok(response); // 항상 HTTP 200 OK
    }
}
