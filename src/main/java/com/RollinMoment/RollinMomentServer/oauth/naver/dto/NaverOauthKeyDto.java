package com.RollinMoment.RollinMomentServer.oauth.naver.dto;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class NaverOauthKeyDto {
    @Value("${oauth2.naver.id}")
    private String clientId;
    @Value("${oauth2.naver.secret}")
    private String clientSecret;
}
