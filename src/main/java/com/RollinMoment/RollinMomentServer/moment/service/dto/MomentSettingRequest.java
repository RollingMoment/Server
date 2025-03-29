package com.RollinMoment.RollinMomentServer.moment.service.dto;

import com.RollinMoment.RollinMomentServer.common.type.Category;
import com.RollinMoment.RollinMomentServer.common.type.PeriodType;

public record MomentSettingRequest(
		String code,
		String coverImg,		// 수정 예정
		String title,
		PeriodType expireType,
		String comment,
		Category category
) {
}
