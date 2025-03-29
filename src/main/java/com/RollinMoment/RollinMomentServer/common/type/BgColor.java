package com.RollinMoment.RollinMomentServer.common.type;

public enum BgColor {
	RED("빨강", "#FBD4D4", 0),
	ORANGE("주황", "#FFEBC6", 1),
	YELLOW("노랑", "#FEF8CC", 2),
	BLUE("파랑", "#DDEFFB", 3),
	GREEN("초록", "#D9F5ED", 4),
	MINT("민트", "#E6F5E7", 5),
	PINK("핑크", "#FFE4FA", 6),
	PURPLE("보라", "#E3E1FF", 7),
	NAVY("남색", "#D8E2F3", 8),
	GRAY("회색", "#F6F6F6", 9);

	private String krName;
	private String colorCode;
	private Integer index;

	BgColor(String krName, String colorCode, Integer index) {
		this.krName = krName;
		this.colorCode = colorCode;
		this.index = index;
	}

	public String code() {
		return this.colorCode;
	}
}
