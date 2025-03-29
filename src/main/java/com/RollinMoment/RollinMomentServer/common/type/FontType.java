package com.RollinMoment.RollinMomentServer.common.type;

public enum FontType {
	DEFAULT("기본 글씨체"),
	ONGUELEAF("온글잎 박다현체"),
	ESCORE("에스코어 드림"),
	NAVER("네이버 마루부리");

	private String krName;

	FontType(String krName) {
		this.krName = krName;
	}
}
