package com.RollinMoment.RollinMomentServer.common.type;

import lombok.Getter;

@Getter
public enum PageType {
	MOMENT("moment", "모먼트", "MO"),
	REACTION("reaction", "리액션", "RE"),
	TRACE("trace", "흔적", "TR"),
	NOTICE("notice", "공지사항", "NO"),
	PROMOTION("promotion", "이벤트/혜택", "PRO");

	private String enName;
	private String krName;
	private String codePrefix;

	PageType(String enName, String krName, String codePrefix) {
		this.enName = enName;
		this.krName = krName;
		this.codePrefix = codePrefix;
	}
}
