package spot.spot.domain.notification.command.service;

import org.springframework.stereotype.Component;
import spot.spot.domain.notification.command.dto.response.FcmDTO;

@Component
public class FcmMessageUtil {
    public final StringBuilder msg = new StringBuilder();

    public FcmDTO askingJob2WorkerMsg(String ownerName, String workerName, String jobTitle) {
        msg.append(ownerName).append("님이 ").append(workerName).append("님께 ").append(jobTitle).append("을 신청하였습니다.");
        FcmDTO fcm = makeMsg("일 의뢰 알림!",msg.toString());
        msg.setLength(0);
        return fcm;
    }

    public FcmDTO sayYes2WorkerMsg(String ownerName, String workerName, String jobTitle) {
        msg.append(ownerName).append("님이 ").append(workerName).append("님의 ").append(jobTitle).append("해결 요청을 수락하셨습니다!");
        FcmDTO fcm = makeMsg("일 신청 수락 알림", msg.toString());
        msg.setLength(0);
        return fcm;
    }

    public FcmDTO sayNo2WorkerMsg(String ownerName, String workerName, String jobTitle) {
        msg.append(ownerName).append("님이 ").append(workerName).append("님의 ").append(jobTitle).append("해결 요청을 거절 하셨습니다.");
        FcmDTO fcm = makeMsg("일 신청 거절 알림", msg.toString());
        msg.setLength(0);
        return fcm;
    }

    public FcmDTO doYouSleepMsg(String ownerName, String workerName, String jobTitle) {
        msg.append(ownerName).append("님이 ").append(workerName).append("님의 ").append(jobTitle).append("해결 요청을 철회하길 원합니다.");
        msg.append("/n 혹시 일을 재개하고 싶으시다면, 10분 내로 알려주세요!");
        FcmDTO fcm = makeMsg("의뢰자로부터 예약 철회가 들어왔어요!", msg.toString());
        msg.setLength(0);
        return fcm;
    }

    public FcmDTO confirm2WorkerMsg(String ownerName, String workerName, String jobTitle) {
        msg.append(ownerName).append("님이 ").append(workerName).append("님의 ").append(jobTitle).append("완료를 확정했습니다!");
        FcmDTO fcm = makeMsg("일 완료 확정!", msg.toString());
        msg.setLength(0);
        return fcm;
    }
    public FcmDTO reject2WorkerMsg(String ownerName, String workerName, String jobTitle) {
        msg.append(ownerName).append("님이 ").append(workerName).append("님의 ").append(jobTitle).append("완료를 거절했습니다.");
        FcmDTO fcm = makeMsg("일 완료 거절! 다시 인증 바랍니다.", msg.toString());
        msg.setLength(0);
        return fcm;
    }


    private FcmDTO makeMsg(String title, String body) {
        return FcmDTO.builder()
            .title(title)
            .body(body)
            .build();
    }

}
