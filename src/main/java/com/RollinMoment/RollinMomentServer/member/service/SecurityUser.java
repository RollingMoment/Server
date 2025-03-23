package com.RollinMoment.RollinMomentServer.member.service;

import com.RollinMoment.RollinMomentServer.member.entity.UserEntity;
import com.RollinMoment.RollinMomentServer.member.entity.type.Gender;
import com.RollinMoment.RollinMomentServer.member.entity.type.Ostype;
import com.RollinMoment.RollinMomentServer.member.entity.type.Provider;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class SecurityUser implements UserDetails {
    private String userId;
    private String deviceId;
    private String password;
    private String nickname;
    private boolean alarm; // 알림 성정 True : ON , False : OFF
    private Gender gender; // 기본값 none
    private Provider provider;
    private Ostype ostype;


    public SecurityUser(UserEntity userEntity) {
        this.userId = userEntity.getUserId();
        this.deviceId = userEntity.getDeviceId();
        this.password = userEntity.getPassword();
        this.nickname = userEntity.getNickname();
        this.alarm = userEntity.isAlarm();
        this.gender = userEntity.getGender();
        this.provider = userEntity.getProvider();
        this.ostype = userEntity.getOstype();
    }

    // UserDetails 인터페이스 구현 (Spring Security용)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // 현재는 권한(Role) 사용 X
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userId; // userId를 Spring Security에서 username으로 사용
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 여부 (true = 만료되지 않음)
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정 잠금 여부 (true = 잠금되지 않음)
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 비밀번호 만료 여부 (true = 만료되지 않음)
    }

    @Override
    public boolean isEnabled() {
        return true; // 계정 활성화 여부 (true = 활성화됨)
    }

}
