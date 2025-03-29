package com.RollinMoment.RollinMomentServer.reaction.entity;

import com.RollinMoment.RollinMomentServer.common.GenerateUtils;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "moment")
@Getter
@NoArgsConstructor
public class Reaction {
	@Id
	@Column(name = "code")
	private String code;

	@Column(name = "type", nullable = false)
	private String typeName;

	@Column(name = "trace_code")
	private String traceCode;

	@Column(name = "reacted_user")
	private String reactedUser;

	@Column(name = "created_at")
	@CreatedDate
	private LocalDateTime createdAt;

	public Reaction(String typeName, String traceCode, String reactedUser) {
		this.code = GenerateUtils.makeReactionCode();
		this.typeName = typeName;
		this.traceCode = traceCode;
		this.reactedUser = reactedUser;
	}

	public static Reaction heart(String traceCode, String user) {
		// TODO : 리액션하기 메소드 구현 시 참고
		return new Reaction("HEART", traceCode, user);
	}

}
