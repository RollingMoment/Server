package com.RollinMoment.RollinMomentServer.member.entity;


import com.RollinMoment.RollinMomentServer.global.BaseTimeEntity;
import com.RollinMoment.RollinMomentServer.member.dto.SignUpDto;
import com.RollinMoment.RollinMomentServer.member.entity.type.Gender;
import com.RollinMoment.RollinMomentServer.member.entity.type.Ostype;
import com.RollinMoment.RollinMomentServer.member.entity.type.Provider;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Table(name="user")

public class UserEntity extends BaseTimeEntity {

    @Id
    @Column(nullable = false)
    private String userId; //. 이메일 아이디

    @Column(nullable = false)
    private String deviceId; //기기 번호값

    @Column(nullable = false)
    private String password;

    @Column(nullable = false , length = 50)
    private String nickname;

    @Column(nullable = false)
    private boolean alarm; // 알림 성정 True : ON , False : OFF

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender; // 기본값 none

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Ostype ostype;

    public static UserEntity transDTO(SignUpDto signUpDto) {
        return new UserEntity(
                signUpDto.getUserId(),
                signUpDto.getPassword(),
                signUpDto.getNickname(),
                signUpDto.getDeviceId(),
                signUpDto.isAlarm(),
                Gender.valueOf(signUpDto.getGender().toUpperCase()), // String → Enum 변환
                Provider.valueOf(signUpDto.getProvider().toUpperCase()),
                Ostype.valueOf(signUpDto.getOsType().toUpperCase())
        );

    }
}
