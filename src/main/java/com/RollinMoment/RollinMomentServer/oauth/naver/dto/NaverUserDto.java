package com.RollinMoment.RollinMomentServer.oauth.naver.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NaverUserDto {
    private String deviceId;
    private boolean alarm;
    private String osType;
}
