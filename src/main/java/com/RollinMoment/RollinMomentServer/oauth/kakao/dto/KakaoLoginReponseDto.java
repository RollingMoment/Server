package com.RollinMoment.RollinMomentServer.oauth.kakao.dto;

import com.RollinMoment.RollinMomentServer.member.dto.UserInfoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KakaoLoginReponseDto {
    private String accessToken;
    private String refreshToken;
    private UserInfoDto userInfo;

}
