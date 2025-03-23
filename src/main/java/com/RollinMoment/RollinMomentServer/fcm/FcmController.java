package com.RollinMoment.RollinMomentServer.fcm;

import com.google.firebase.messaging.FirebaseMessagingException;
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
	// TODO :: push alarm api


	private final FcmService fcmService;

	//클라이언트에게 FCM registraion을 받아 user_id값과 매필하여 DB에 저장하기
//	@PostMapping("/send/registeration-token")
//	public void saveClientId(@RequestBody FcmClientRequest fcmClientRequest){
//		fcmService.saveClientId(fcmClientRequest);
//	}

	@PostMapping("/send")
	public ResponseEntity<Void> sendPushAlarm(@RequestBody FcmServiceDto dto) throws FirebaseMessagingException {
		fcmService.sendByToken(dto);
		return ResponseEntity.ok().build();
	}
}
