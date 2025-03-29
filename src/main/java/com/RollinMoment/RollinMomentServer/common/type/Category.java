package com.RollinMoment.RollinMomentServer.common.type;

public enum Category {
	WEDDING("결혼", ""), // 결혼
	BIRTHDAY("생일", ""), // 생일
	FIRST_BIRTHDAY("돌잔치", ""), // 돌잔치
	GRADUATION("졸업", ""), // 졸업
	FAREWELL("송별회", ""), // 송별회
	CHRISTMAS("크리스마스", ""), // 크리스마스
	TEACHERS_DAY("스승의 날", ""), // 스승의 날
	APPRECIATION("감사", ""), // 감사
	CHEERING("응원", ""), // 응원
	CONFESSION("고백", ""), // 고백
	PRAISE("칭찬", ""), // 칭찬
	CELEBRATION("축하", ""); // 축하

	// TODO:: 첫 순간을 모아놓은 카테고리가 있으면 어떨까? (ex. 돌잔치 -> First Birthday / 첫 연주회 등..?

	private String krName;
	private String description;

	Category(String krName, String description) {
		this.krName = krName;
		this.description = description;
	}
}
