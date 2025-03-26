package com.RollinMoment.RollinMomentServer.common;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

//@Entity
@Getter
@NoArgsConstructor
public class BaseEntity {

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public String generateCode(String type) {
        // TODO :: code 형식 정의 필요
        return String.format("%s-%s", type, System.currentTimeMillis());
    }
}
