package com.RollinMoment.RollinMomentServer.moment.entity;

import com.RollinMoment.RollinMomentServer.common.utils.GenerateUtil;
import com.RollinMoment.RollinMomentServer.common.type.BgColor;
import com.RollinMoment.RollinMomentServer.common.type.FontType;
import com.RollinMoment.RollinMomentServer.common.type.PeriodType;
import com.RollinMoment.RollinMomentServer.moment.service.dto.MomentSettingRequest;
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

	@Column(name = "font", nullable = false,
			columnDefinition = "ENUM('DEFAULT','ONGEULEAF','ESCORE','NAVER'")
	@Enumerated(EnumType.STRING)
	private FontType font;				// font 미설정 시 default

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

	public Moment(String title, MomentCoverImg coverImg, FontType font, PeriodType expireType, String categoryEnName,
				  String comment, Boolean isPublic) {
		this.code = GenerateUtil.makeMomentCode();
		this.inviteCode = GenerateUtil.makeInviteCode(this.code);
		this.title = title;
		this.bgColor = randomBgColor();
		this.coverImg = coverImg;
		this.font = font;
		this.expireType = expireType;
		this.expireAt = makeExpireDate(expireType);
		this.categoryEnName = categoryEnName;
		this.comment = comment;
		this.isPublic = isPublic;
	}

	public void changeMomentSettings(MomentSettingRequest request) {
		this.title = request.title();
		this.comment = request.comment();
		if(!this.expireType.equals(request.expireType())) {
			// 기존 마감일 타입과 다를때만 변경
			this.expireType = request.expireType();
			this.expireAt = makeExpireDate(request.expireType());
		}
//		this.coverImg = request.coverImg();
		this.categoryEnName = request.category().name();
	}

	public void deleteMoment() {
		this.isDeleted = true;
	}

	public void sendMomentTo(String userId) {
		// TODO : 모먼트 선물하기 -> mvp 에서 제외
	}


	// TODO :: 후순위
	//  	배경 컬러 랜덤으로 설정 -> BgColor 에 index 설정 후 math.random 활용
	private BgColor randomBgColor() {
//		int random = (int) ((Math.random()*10) % 10);
//		System.out.println(BgColor.class.getSuperclass().getDeclaredFields());
		return BgColor.PURPLE;
	}

	private LocalDateTime makeExpireDate(PeriodType expireType) {
		return LocalDateTime.now().plusDays(expireType.getDays());
	}
}
