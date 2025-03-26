package com.RollinMoment.RollinMomentServer.oauth.kakao.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KakaoResponseDto {
    private String userId;
    private String nickname;
}
