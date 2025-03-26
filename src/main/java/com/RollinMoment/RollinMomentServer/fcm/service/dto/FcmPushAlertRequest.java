package com.RollinMoment.RollinMomentServer.fcm.service.dto;

import com.RollinMoment.RollinMomentServer.alert.entity.AlertType;

public record FcmPushAlertRequest(
		String userId,
		AlertType type,
		String title,
		String content
) {
}
