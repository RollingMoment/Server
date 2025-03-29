package com.RollinMoment.RollinMomentServer.moment.service.dto;

import com.RollinMoment.RollinMomentServer.common.type.Category;
import com.RollinMoment.RollinMomentServer.common.type.FontType;
import com.RollinMoment.RollinMomentServer.common.type.PeriodType;

public record MomentCreateRequest (
		String title,
		String coverImg,
		FontType font,
		PeriodType expireType,
		Category category,
		String comment,
		Boolean isPublic
) {
}
