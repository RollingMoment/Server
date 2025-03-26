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
	// TODO :: syPushToken == Android(소영) 푸시토큰 / jhPushToken == iOS(재현) 푸시토큰 -> userId 적용 후 삭제 필요
	private final String syPushToken = "fLEr_JbXRtK-yRRrwYfF7z:APA91bFFWhbdcUMAxR9C86yQp7iiBVr1haWve2np__xeiF3V60f1jtNWlNJeXJ7tKf4H_1MDN7Zw6wFRgdiAdDCmohb4pGpHK7kT-plBVFpCRSV56KnzlRk";
	private final String jhPushToken = "fuoLaWI3r08Il2uBGdauyU:APA91bE2wEXH_JzPbFG5BJH6V17FsmwMPlgh9Q2o-kschkRl8rsLF4BICAxqEYRUh1cDsyZVzPMe-yIaSmADDSjWlhG-nMH0D5UCsyyDbPz12UQj6GTlYsA";

	public void sendByToken(FcmPushAlertRequest request, String token){
		// TODO: test 후 제거
		if(token.isEmpty()) {
			token = syPushToken;
		}

		Message message = Message.builder()
									.putData("location", "ex.moment")
									.putData("locationType", "ex.inApp")
									.putData("dataKey", "ex.momentCode")
									.setToken(token)
									.setNotification(
											Notification.builder()
													.setTitle(request.title())
													.setBody(request.content())
													.build()
									)
									.setAndroidConfig(TokenAndroidConfig(request))
									.setApnsConfig(TokenApnsConfig(request))
								.build();


		try {
			String response = FirebaseMessaging.getInstance().send(message);
			log.info("FCMsend-"+response);
		} catch (FirebaseMessagingException e) {
			log.error("FCMexcept-"+ e.getMessage());
		}
	}

	// Android 세팅
	public AndroidConfig TokenAndroidConfig(FcmPushAlertRequest request) {
		return AndroidConfig.builder()
//				.setCollapseKey(request.getCollapseKey())
				.setNotification(AndroidNotification.builder()
						.setTitle(request.title())
						.setBody(request.content())
						.setClickAction("push_click")
						.build())
				.build();
	}

	// APNs 세팅 ( iOS )
	public ApnsConfig TokenApnsConfig(FcmPushAlertRequest request) {
		return ApnsConfig.builder()
				.setAps(Aps.builder()
						.setAlert(
								ApsAlert.builder()
										.setTitle(request.title())
										.setBody(request.content())
//										.setLaunchImage(request.getImgUrl())
										.build()
						)
//						.setCategory(request.getCollapseKey())
						.setSound("default")
						.build())
				.build();
	}
}
