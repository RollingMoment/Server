package com.RollinMoment.RollinMomentServer.trace.entity;

import com.RollinMoment.RollinMomentServer.common.GenerateUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "trace")
@Getter
@NoArgsConstructor
public class Trace {
	@Id
	@Column(name = "code")
	private String code;

	@Column(name = "moment_code", nullable = false)
	private String momentCode;

	@Column(name = "writer", nullable = false)
	private String writer;		// 작성자 userId

	@Column(name = "content", nullable = false)
	private String content;

	@Column(name = "is_anonymous", nullable = false)
	private Boolean isAnonymous;

	@CreatedDate
	private LocalDateTime createdAt;

	@Column(name = "is_removed", nullable = false)
	private Boolean isRemoved;

	@Column(name = "removed_at")
	private LocalDateTime removedAt;

	public Trace(String momentCode, String writer, String content, Boolean isAnonymous,
				 LocalDateTime removedAt) {
		this.code = GenerateUtils.makeTraceCode();
		this.momentCode = momentCode;
		this.writer = writer;
		this.content = content;
		this.isAnonymous = isAnonymous;
		this.isRemoved = false;
		this.removedAt = removedAt;
	}

	public void remove(String writer) {
		if(!this.writer.equals(writer)) {
			throw new IllegalArgumentException("작성자만 흔적을 지울 수 있습니다.");
		}
		this.isRemoved = true;
	}
}
