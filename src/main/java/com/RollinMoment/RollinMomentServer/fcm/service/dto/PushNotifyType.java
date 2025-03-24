package com.RollinMoment.RollinMomentServer.fcm.service.dto;

public enum PushNotifyType {
    ANNOUNCEMENT("공지"),
    NOTIFICATION("안내"),
    ALERT("알림");

    private String krName;

    PushNotifyType(String krName) {
        this.krName = krName;
    }
}
