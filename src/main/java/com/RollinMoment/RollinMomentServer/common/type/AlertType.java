package com.RollinMoment.RollinMomentServer.common.type;

import lombok.Getter;

@Getter
public enum AlertType {
	NOTICE("공지사항", "notice", "N"),                // 관리자가 발송한 알림. (공지)
	MOMENT("모먼트", "moment", "M"),
	REACTION("리액션", "moment", "R"),
	TRACE("흔적", "moment", "T"),
	PROMOTION("이벤트/혜택", "promotion", "P");

	// 2025.03.26 mvp 개발 -> 모먼트/리액션/흔적 모두 모먼트 페이지로 이동 / 추후 화면 변동이 있다면 각각 다른 페이지로 안내 필요

	private String krName;
	private String location;
	private String code;

	AlertType(String krName, String location, String code) {
		this.krName = krName;
		this.location = location;
		this.code = code;
	}
}
