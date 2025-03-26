package com.RollinMoment.RollinMomentServer.alert.entity;

public enum AlertStatus {
    WAITING("전송 대기"),
    SENT("전송 완료"),
    SEND_FAILED("전송 실패"),
    READ("읽음");

    private String krName;

    AlertStatus(String krName) {
        this.krName = krName;
    }
}
