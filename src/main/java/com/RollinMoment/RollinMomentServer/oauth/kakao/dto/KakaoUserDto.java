package com.RollinMoment.RollinMomentServer.oauth.kakao.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KakaoUserDto {
    private String deviceId;
    private boolean alarm;
    private String osType;

}
