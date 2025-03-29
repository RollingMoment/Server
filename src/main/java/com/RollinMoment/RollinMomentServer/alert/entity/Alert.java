package com.RollinMoment.RollinMomentServer.alert.entity;

import com.RollinMoment.RollinMomentServer.common.type.AlertStatus;
import com.RollinMoment.RollinMomentServer.common.type.AlertType;
import com.RollinMoment.RollinMomentServer.common.type.LocationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

//@Entity
@Table(name = "alert")
@Getter
@NoArgsConstructor
public class Alert {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "alert_code", nullable = false)
	private String alertCode;

	@Enumerated(EnumType.STRING)
	@Column(name = "alert_type", nullable = false, columnDefinition = "ENUM('NOTICE','MOMENT','TRACE','REACTION','PROMOTION')")
	private AlertType alertType;

	@Column(name = "user_id", nullable = false)
	private String userId;

	@Column(name = "title")
	private String title;

	@Column(name = "content")
	private String content;

	@Enumerated(EnumType.STRING)
	@Column(name = "location_type", nullable = false, columnDefinition = "ENUM('IN_APP','OUTSIDE')")
	private LocationType locationType;

	@Column(name = "data_key")
	private String dataKey;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, columnDefinition = "ENUM('WAITING','SENT','SEND_FAILED','READ')")
	private AlertStatus status = AlertStatus.WAITING;

	@Column(name = "outer_url")
	private String outerUrl;

	@CreatedDate
	@Column(name = "send_at", nullable = false)
	private LocalDateTime sendAt;

	public Alert(AlertType alertType, String userId, String title, String content, LocationType locationType, String dataKey, String outerUrl) {
		this.alertType = alertType;
		this.userId = userId;
		this.title = title;
		this.content = content;
		this.locationType = locationType;
		this.dataKey = dataKey;
		this.outerUrl = outerUrl;
		this.alertCode = generateAlertCode(alertType, userId);
	}

	public static Alert notice(String userId, String title, String content, LocationType locationType, String dataKey, String outerUrl) {
		return new Alert(AlertType.NOTICE, userId, title, content, locationType, dataKey, outerUrl);
	}

	public static Alert moment(String userId, String title, String content, LocationType locationType, String dataKey, String outerUrl) {
		return new Alert(AlertType.MOMENT, userId, title, content, locationType, dataKey, outerUrl);
	}

	public static Alert trace(String userId, String title, String content, LocationType locationType, String dataKey, String outerUrl) {
		return new Alert(AlertType.TRACE, userId, title, content, locationType, dataKey, outerUrl);
	}

	public static Alert reaction(String userId, String title, String content, LocationType locationType, String dataKey, String outerUrl) {
		return new Alert(AlertType.REACTION, userId, title, content, locationType, dataKey, outerUrl);
	}

	public static Alert promotion(String userId, String title, String content, LocationType locationType, String dataKey, String outerUrl) {
		return new Alert(AlertType.PROMOTION, userId, title, content, locationType, dataKey, outerUrl);
	}

	private String generateAlertCode(AlertType type, String userId) {
		// TODO :: alert_code 형식 정의 필요
		//      type_timemillis_randomtext? ex.리액션 -> R{user_id}{currentTimeMillis} ??
		return String.format("%s%s%s", type.getCode(), userId, System.currentTimeMillis());
	}

	public void sentSuccess() {
		if(this.status == AlertStatus.WAITING) {
			this.status = AlertStatus.SENT;
		} else {
			throw new IllegalArgumentException("전송 대기 상태인 알림만 전송할 수 있습니다.");
		}
	}

	public void sentFailed() {
		if(this.status == AlertStatus.WAITING) {
			this.status = AlertStatus.SEND_FAILED;
		} else {
			throw new IllegalArgumentException("전송 대기 상태인 알림만 전송할 수 있습니다.");
		}
	}

	public void read() {
		if(this.status == AlertStatus.SENT) {
			this.status = AlertStatus.READ;
		} else {
			throw new IllegalArgumentException("전송되지 않은 알림은 읽음 처리 할 수 없습니다.");
		}
	}
}
