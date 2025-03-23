package com.RollinMoment.RollinMomentServer.fcm;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FcmServiceDto {
	private String username;
	private Long contentId;
	private NotifyType type;
	private String title;
	private String content;

	public static FcmServiceDto of(String username, Long contentId, NotifyType type, String title, String content){
		FcmServiceDto dto = new FcmServiceDto();
		dto.username = username;
		dto.contentId = contentId;
		dto.type = type;
		dto.title = title;
		dto.content = content;
		return dto;
	}

	public enum NotifyType {
		PUSH,
		NOTIFICAITON,
		ALERT
	}
}
