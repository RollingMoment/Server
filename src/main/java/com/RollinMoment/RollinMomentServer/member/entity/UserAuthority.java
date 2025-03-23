package com.RollinMoment.RollinMomentServer.member.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.Date;


@Table(name = "user_authority")
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Entity
public class UserAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auth_id")
    private Long authId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String refreshToken; // 변수명 변경

    @Setter
    @Column(nullable = false, columnDefinition = "DATETIME")
    private Date expiresAt;

    public UserAuthority(String userId, String refreshToken , Date expiresAt) {
        this.userId = userId;
        this.refreshToken = refreshToken;
        this.expiresAt = expiresAt;
    }
}
