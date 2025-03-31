package spot.spot.domain.notification.command.service;

import org.springframework.stereotype.Component;
import spot.spot.domain.notification.command.dto.response.FcmDTO;

@Component
public class FcmMessageUtil {

    private static final ThreadLocal<StringBuilder> localBuilder = ThreadLocal.withInitial(StringBuilder::new);

    public FcmDTO askingJob2WorkerMsg(String ownerName, String workerName, String jobTitle) {
        StringBuilder msg = localBuilder.get();
        msg.setLength(0);
        msg.append(ownerName).append("님이 ").append(workerName).append("님께 ").append(jobTitle).append("을 신청하였습니다.");
        return makeMsg("일 의뢰 알림!",msg.toString());
    }

    public FcmDTO sayYes2WorkerMsg(String ownerName, String workerName, String jobTitle) {
        StringBuilder msg = localBuilder.get();
        msg.setLength(0);
        msg.append(ownerName).append("님이 ").append(workerName).append("님의 ").append(jobTitle).append("해결 요청을 수락하셨습니다!");
        return makeMsg("일 신청 수락 알림", msg.toString());

    }

    public FcmDTO sayNo2WorkerMsg(String ownerName, String workerName, String jobTitle) {
        StringBuilder msg = localBuilder.get();
        msg.setLength(0);
        msg.append(ownerName).append("님이 ").append(workerName).append("님의 ").append(jobTitle).append("해결 요청을 거절 하셨습니다.");
        return makeMsg("일 신청 거절 알림", msg.toString());

    }

    public FcmDTO doYouSleepMsg(String ownerName, String workerName, String jobTitle) {
        StringBuilder msg = localBuilder.get();
        msg.setLength(0);
        msg.append(ownerName).append("님이 ").append(workerName).append("님의 ").append(jobTitle).append("해결 요청을 철회하길 원합니다.");
        msg.append("/n 혹시 일을 재개하고 싶으시다면, 10분 내로 알려주세요!");
        return makeMsg("의뢰자로부터 예약 철회가 들어왔어요!", msg.toString());

    }

    public FcmDTO confirm2WorkerMsg(String ownerName, String workerName, String jobTitle) {
        StringBuilder msg = localBuilder.get();
        msg.setLength(0);
        msg.append(ownerName).append("님이 ").append(workerName).append("님의 ").append(jobTitle).append("완료를 확정했습니다!");
        return makeMsg("일 완료 확정!", msg.toString());

    }
    public FcmDTO reject2WorkerMsg(String ownerName, String workerName, String jobTitle) {
        StringBuilder msg = localBuilder.get();
        msg.setLength(0);
        msg.append(ownerName).append("님이 ").append(workerName).append("님의 ").append(jobTitle).append("완료를 거절했습니다.");
        return makeMsg("일 완료 거절! 다시 인증 바랍니다.", msg.toString());

    }

    public FcmDTO askingJob2ClientMsg(String ownerName, String workerName, String jobTitle) {
        StringBuilder msg = localBuilder.get();
        msg.setLength(0);
        msg.append(workerName).append("님이 ").append(ownerName).append("님의 ").append(jobTitle).append("을 하고 싶어 합니다!");
        return makeMsg("일 해결 신청!", msg.toString());
    }

    public FcmDTO startJob2ClientMsg(String ownerName, String workerName, String jobTitle) {
        StringBuilder msg = localBuilder.get();
        msg.setLength(0);
        msg.append(workerName).append("님이 ").append(ownerName).append("님의 ").append(jobTitle).append("을 시작했습니다!");
        return  makeMsg("일 시작 알림", msg.toString());

    }

    public FcmDTO sayYes2ClientMsg(String ownerName, String workerName, String jobTitle) {
        StringBuilder msg = localBuilder.get();
        msg.setLength(0);
        msg.append(workerName).append("님이 ").append(ownerName).append("님의 ").append(jobTitle).append("해결 의뢰을 수락하셨습니다!");
        return makeMsg("일 신청 수락 알림", msg.toString());
    }

    public FcmDTO sayNo2ClientMsg(String ownerName, String workerName, String jobTitle) {
        StringBuilder msg = localBuilder.get();
        msg.setLength(0);
        msg.append(workerName).append("님이 ").append(ownerName).append("님의 ").append(jobTitle).append("해결 의뢰을 거절 하셨습니다.");
        return makeMsg("일 신청 거절 알림", msg.toString());

    }

    public FcmDTO continueJobMsg(String ownerName, String workerName){
        StringBuilder msg = localBuilder.get();
        msg.setLength(0);
        msg.append(workerName).append("님이 ").append(ownerName).append("님의 일을 재개했습니다.");
        return makeMsg("일 재개 알림", msg.toString());

    }

    public FcmDTO finishJobMsg(String ownerName, String workerName, String jobTitle) {
        StringBuilder msg = localBuilder.get();
        msg.setLength(0);
        msg.append(workerName).append("님이 ").append(ownerName).append("님의 일을 끝냈다고 합니다!");
        return makeMsg("일 성공 알림", msg.toString());

    }

    private FcmDTO makeMsg(String title, String body) {
        return FcmDTO.builder()
            .title(title)
            .body(body)
            .build();
    }

}
