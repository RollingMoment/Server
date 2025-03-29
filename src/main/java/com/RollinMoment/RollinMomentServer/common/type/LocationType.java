package com.RollinMoment.RollinMomentServer.common.type;

public enum LocationType {
	IN_APP("인앱 이동"),
	OUTSIDE("외부 이동");

	private String krName;

	LocationType(String krName) {
		this.krName = krName;
	}
}

