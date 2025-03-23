package com.RollinMoment.RollinMomentServer.member.dto;


import com.RollinMoment.RollinMomentServer.member.entity.UserEntity;
import com.RollinMoment.RollinMomentServer.member.entity.type.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpDto {
    @NotBlank
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String userId; // 이메일 아이디 (로그인 ID)

    @NotBlank
    @Size(min = 8, max = 20, message = "비밀번호는 8~20자 사이여야 합니다.")
    private String password; // 비밀번호

    @NotBlank
    @Size(max = 50, message = "닉네임은 최대 50자까지 가능합니다.")
    private String nickname; // 닉네임

    @NotBlank
    private String deviceId; // 기기 고유값

    private boolean alarm; // 알림 설정 여부

    private String gender; // 성별 선택 (선택 안 하면 기본값 NONE)

    private String provider; // 소셜 로그인 제공자 (KAKAO, NAVER, GOOGLE, LOCAL)

    private String osType; // 운영체제 유형 (ANDROID, IOS, WEB)

}
