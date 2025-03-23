package com.RollinMoment.RollinMomentServer.response.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Data
@AllArgsConstructor
public class ResponseJoinDto {
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
