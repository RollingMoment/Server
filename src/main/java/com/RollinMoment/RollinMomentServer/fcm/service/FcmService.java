package com.RollinMoment.RollinMomentServer.fcm.service;

import com.RollinMoment.RollinMomentServer.fcm.service.dto.FcmPushAlertRequest;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {
	// TODO :: syPushToken == 소영 폰 푸시토큰임. user 회원가입 등 진행 후 삭제
	private final String syPushToken = "fLEr_JbXRtK-yRRrwYfF7z:APA91bFFWhbdcUMAxR9C86yQp7iiBVr1haWve2np__xeiF3V60f1jtNWlNJeXJ7tKf4H_1MDN7Zw6wFRgdiAdDCmohb4pGpHK7kT-plBVFpCRSV56KnzlRk";
	private final String jhPushToken = "382478f2bd4fd97609f16a1c33bafeada9a13b0b9ff471ad8e4784b712e8b955";

//	private final UserRepository userRepository;

	public void sendByToken(FcmPushAlertRequest request){
		String token = getToken(request.userId());

		Message message = Message.builder()
								  .setToken(token)
								  .setNotification(
										  Notification.builder()
												  .setTitle(request.title())
												  .setBody(request.content())
												  .build()
								  )
								  .setAndroidConfig(
										  AndroidConfig.builder()
												  .setNotification(
														  AndroidNotification.builder()
																  .setTitle(request.title())
																  .setBody(request.content())
																  .setClickAction("push_click")
																  .build()
												  )
												  .build()
								  )
								  .setApnsConfig(
										  ApnsConfig.builder()
												  .setAps(Aps.builder()
																  .setCategory("push_click")
																  .build())
												  .build()
								  )
								  .build();

		try {
			String response = FirebaseMessaging.getInstance().send(message);
			log.info("FCMsend-"+response);
		} catch (FirebaseMessagingException e) {
			log.info("FCMexcept-"+ e.getMessage());
		}
	}

	// TODO :: token userId 기반으로 찾아서 사용
	private String getToken(String userId) {
		String pushToken = "";

//		User user = userRepository.findByUserId(userId).orElseThrow(() -> throw new IllegalArgumentException("사용자를 찾을 수 없습니다."));
//		pushToken = user.getPushToken();

		return !pushToken.isEmpty() ? pushToken : syPushToken;
	}

}
