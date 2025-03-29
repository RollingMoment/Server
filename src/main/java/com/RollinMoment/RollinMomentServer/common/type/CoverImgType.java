package com.RollinMoment.RollinMomentServer.common.type;

public enum CoverImgType {
	CUSTOM("카메라 또는 앨범"),
	ANNIVERSARY("기념의 순간"),
	SENDING_HEARTS("마음 전하기"),
	FAREWELL("작별의 순간"),
	NONE("기본 커버 이미지");

	private String krName;

	CoverImgType(String krName) {
		this.krName = krName;
	}
}
