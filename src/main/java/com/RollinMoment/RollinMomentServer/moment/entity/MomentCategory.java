package com.RollinMoment.RollinMomentServer.moment.entity;

import com.RollinMoment.RollinMomentServer.common.type.Category;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "moment")
@Getter
@NoArgsConstructor
public class MomentCategory {
	@Id
	@Enumerated(EnumType.STRING)
	@Column(name = "en_name",
			columnDefinition = "ENUM('WEDDING','BIRTHDAY','FIRST_BIRTHDAY','GRADUATION','FAREWELL','CHRISTMAS'," +
									   "'TEACHERS_DAY','APPRECIATION','CHEERING','CONFESSION','PRAISE','CELEBRATION')")
	private Category category;

	@Column(name = "is_public", nullable = false)
	private Boolean isPublic;

	@CreatedDate
	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@CreatedBy
	@Column(name = "created_by")
	private String createdBy;		// 추후 관리자 명
}
