package com.RollinMoment.RollinMomentServer.fcm.service.dto;

public record FcmPushAlertRequest(
		String userId,
		PushNotifyType type,
		String title,
		String content
) {

	public static FcmPushAlertRequest announcement(String userId, String title, String content) {
		return new FcmPushAlertRequest(userId, PushNotifyType.ANNOUNCEMENT, title, content);
	}

	public static FcmPushAlertRequest notification(String userId, String title, String content) {
		return new FcmPushAlertRequest(userId, PushNotifyType.NOTIFICATION, title, content);
	}

	public static FcmPushAlertRequest alert(String userId, String title, String content) {
		return new FcmPushAlertRequest(userId, PushNotifyType.ALERT, title, content);
	}
}
