package com.RollinMoment.RollinMomentServer.common.util;

import com.RollinMoment.RollinMomentServer.response.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResponseUtil {

    // 성공 응답 (`code: 0`)
    public static ResponseEntity<ResponseDto> SuccessResponse(String message) {
        ResponseDto.Meta meta = new ResponseDto.Meta(0, "success");
        Map<String, Object> body = new HashMap<>();
        body.put("message", message);
        ResponseDto response = new ResponseDto(meta, body);
        return ResponseEntity.ok(response);
    }

    // 성공 응답 (`code: 0` + 데이터 포함)
    public static ResponseEntity<ResponseDto> SuccessDataResponse(Map<String, Object> data) {
        ResponseDto.Meta meta = new ResponseDto.Meta(0, "success");
        return ResponseEntity.ok(new ResponseDto(meta, data));
    }

    // 오류 응답 (`code: -1` or 기타 코드)
    public static ResponseEntity<ResponseDto> ErrorResponse(int code, String message , HttpStatus status) {
        ResponseDto.Meta meta = new ResponseDto.Meta(code, "error");
        Map<String, Object> body = new HashMap<>();
        body.put("message", message);
        ResponseDto response = new ResponseDto(meta, body);
        return ResponseEntity.status(status).body(response);
    }
}
