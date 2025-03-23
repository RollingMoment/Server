package com.RollinMoment.RollinMomentServer.config;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class JwtProperties {
    @Value("${jwt.hashcode}")
    private String secret;
}
