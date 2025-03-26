package com.RollinMoment.RollinMomentServer.oauth.naver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NaverResponseDto {
    private String userId;
    private String nickname;
}
