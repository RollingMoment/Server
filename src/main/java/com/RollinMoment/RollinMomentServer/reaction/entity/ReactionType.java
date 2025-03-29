package com.RollinMoment.RollinMomentServer.reaction.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "reaction_type")
@Getter
@NoArgsConstructor
public class ReactionType {
	@Id
	@Column(name = "type_name")
	private String typeName;

	@Column(name = "kr_name")
	private String krName;

	@Column(name = "description")
	private String description;

	@Column(name = "icon")
	private String icon;

	@Column(name = "created_at")
	@CreatedDate
	private LocalDateTime createdAt;

	@Column(name = "is_public")
	private Boolean isPublic;

	@Column(name = "created_by")
	@CreatedBy
	private String createdBy;

}
