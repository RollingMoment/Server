package com.RollinMoment.RollinMomentServer.alert.service;

import com.RollinMoment.RollinMomentServer.alert.entity.Alert;
import com.RollinMoment.RollinMomentServer.alert.entity.AlertType;
import com.RollinMoment.RollinMomentServer.fcm.service.FcmService;
import com.RollinMoment.RollinMomentServer.fcm.service.dto.FcmPushAlertRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlertService {
    private final FcmService fcmService;

    // 한명에게 발송
    public void sendAlert(Alert alert) {
//        User user = userRepository.findById(alert.getUserId()).orElseThrow(() ->
//                new IllegalArgumentException("사용자를 찾을 수 없습니다.");
//        );
//        String token = user.getPushToken();
        String token = "pushtoken";

        fcmService.sendByToken(new FcmPushAlertRequest(
                alert.getUserId()
                , alert.getAlertType()
                , alert.getTitle()
                , alert.getContent()
        ), token);
    }

    public void sendMultipleAlert() {
        // TODO :: 여러명에게 발송
    }
}
