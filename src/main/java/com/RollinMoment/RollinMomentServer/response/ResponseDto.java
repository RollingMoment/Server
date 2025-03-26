package com.RollinMoment.RollinMomentServer.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
public class ResponseDto {
    private Meta meta;
    private Object body;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Meta {
        private int code;
        private String message;
    }
}
