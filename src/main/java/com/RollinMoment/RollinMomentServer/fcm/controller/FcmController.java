package com.RollinMoment.RollinMomentServer.fcm.controller;

import com.RollinMoment.RollinMomentServer.fcm.service.dto.FcmPushAlertRequest;
import com.RollinMoment.RollinMomentServer.fcm.service.FcmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rollin-moment/api/push")
public class FcmController {

	private final FcmService fcmService;

	@PostMapping("/send")
	public ResponseEntity<Void> sendPushAlarm(@RequestBody FcmPushAlertRequest request) {
		fcmService.sendByToken(request, "");
		return ResponseEntity.ok().build();
	}
}
