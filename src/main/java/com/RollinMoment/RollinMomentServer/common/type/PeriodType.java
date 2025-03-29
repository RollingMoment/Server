package com.RollinMoment.RollinMomentServer.common.type;

import lombok.Getter;

@Getter
public enum PeriodType {
	NONE("없음", 0),
	THREE_DAYS_LATER("3일 후", 3),
	SEVEN_DAYS_LATER("7일 후", 7);

	private String krName;
	private Integer days;

	PeriodType(String krName, Integer days) {
		this.krName = krName;
		this.days = days;
	}
}
