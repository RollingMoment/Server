package com.RollinMoment.RollinMomentServer.moment.entity;

import com.RollinMoment.RollinMomentServer.common.GenerateUtils;
import com.RollinMoment.RollinMomentServer.common.type.BgColor;
import com.RollinMoment.RollinMomentServer.common.type.PeriodType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "moment")
@Getter
@NoArgsConstructor
public class Moment {
	@Id
	private String code;

	@Column(name = "invite_code", nullable = false)
	private String inviteCode;

	@Column(name = "title", nullable = false)
	private String title;

	@Column(name = "bgColor", nullable = false,
			columnDefinition = "ENUM('RED','ORANGE','YELLOW','BLUE','GREEN','MINT','PINK','PURPLE','NAVY','GRAY')")
	@Enumerated(EnumType.STRING)
	private BgColor bgColor;

	@ManyToOne(fetch = FetchType.EAGER)
	@Column(name = "moment_cover_img_code")
	private MomentCoverImg coverImg;

	@Column(name = "font", nullable = false)
	private String font;				// font 미설정 시 default

	@Column(name = "expire_type", nullable = false,
	 		columnDefinition = "ENUM('NONE','THREE_DAYS_LATER','SEVEN_DAYS_LATER')")
	@Enumerated(EnumType.STRING)
	private PeriodType expireType;

	@Column(name = "expire_at")
	private LocalDateTime expireAt;		// expireType == None 인 경우 null

	@Column(name = "category_en_name", nullable = false)
	private String categoryEnName;

	@Column(name = "comment")
	private String comment;

	@Column(name = "receiver")
	private String receiver; // 수신자 user_id - nullable?

	@Column(name = "is_public", nullable = false)
	private Boolean isPublic;

	@CreatedDate
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "is_deleted", nullable = false)
	private Boolean isDeleted;

	public Moment(String title, MomentCoverImg coverImg, String font, PeriodType expireType, String categoryEnName,
				  String comment, String receiver, Boolean isPublic) {
		this.code = GenerateUtils.makeMomentCode();
		this.inviteCode = GenerateUtils.makeInviteCode(this.code);
		this.title = title;
		this.bgColor = randomBgColor();
		this.coverImg = coverImg;
		this.font = font;
		this.expireType = expireType;
		this.expireAt = makeExpireDate(expireType);
		this.categoryEnName = categoryEnName;
		this.comment = comment;
		this.receiver = receiver;
		this.isPublic = isPublic;
	}

	public void updateMomentInfo() {
		// TODO : moment patch (설정 수정)
	}

	public void deleteMoment() {
		// TODO : softDelete -- 어디까지 지울 것인가?
	}

	public void sendMomentTo(String userId) {
		// TODO : 모먼트 선물하기 -> mvp 이후 , 후순위 (useId 에게 모든 권한 이양?)
		//		선물한 이력이 남는지? 등등 재확인 필요
	}


	// TODO : 배경 컬러 랜덤으로 설정 -> BgColor 에 index 설정 후 math.random 활용
	private BgColor randomBgColor() {
		int random = (int) ((Math.random()*10) % 10);
		System.out.println(BgColor.class.getSuperclass().getDeclaredFields());
		return null;
	}

	private LocalDateTime makeExpireDate(PeriodType expireType) {
		return LocalDateTime.now().plusDays(expireType.getDays());
	}
}
