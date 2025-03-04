package spot.spot.domain.job.entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum MatchingStatus {
    OWNER       ("의뢰인-일의 주인"),
    ATTENDER    ("일 해결하겠다고 신청한 사람"),
    REQUEST     ("의뢰인이 해당 레코드의 주인에게 일 해결을 요청"),
    YES         ("일 승낙"),
    NO          ("일 거절"),
    START       ("일 시작"),
    SLEEP       ("의뢰인이 레코드의 주인에게 일 해결을 취소 요청 - 10분 뒤 자동 취소"),
    CANCEL      ("레코드 주인의 일 해결이 취소됨"),
    FINISH      ("해결사 입장에서는 일이 끝났음을 알림"),
    CONFIRM     ("의뢰인이 해결됨을 확정"),
    REJECT      ("의뢰인이 해결됨을 반려");
    private final String message;
}
