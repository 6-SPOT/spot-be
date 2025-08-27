package spot.spot.domain.notification.command.service;

import org.springframework.stereotype.Component;
import spot.spot.domain.notification.command.dto.response.FcmDTO;

@Component
public class FcmMessageUtil {

    public FcmDTO askingJob2WorkerMsg(String ownerName, String workerName, String jobTitle) {
        return makeMsg("일 의뢰 알림!", ownerName + "님이 " + workerName + "님께 " + jobTitle + "을 신청하였습니다.");
    }

    public FcmDTO sayYes2WorkerMsg(String ownerName, String workerName, String jobTitle) {
        return makeMsg("일 신청 수락 알림",
            ownerName + "님이 " + workerName + "님의 " + jobTitle + "해결 요청을 수락하셨습니다!");

    }

    public FcmDTO sayNo2WorkerMsg(String ownerName, String workerName, String jobTitle) {
        return makeMsg("일 신청 거절 알림",
            ownerName + "님이 " + workerName + "님의 " + jobTitle + "해결 요청을 거절 하셨습니다.");

    }

    public FcmDTO doYouSleepMsg(String ownerName, String workerName, String jobTitle) {
        String msg = ownerName + "님이 " + workerName + "님의 " + jobTitle + "해결 요청을 철회하길 원합니다."
            + "/n 혹시 일을 재개하고 싶으시다면, 10분 내로 알려주세요!";
        return makeMsg("의뢰자로부터 예약 철회가 들어왔어요!", msg);

    }

    public FcmDTO confirm2WorkerMsg(String ownerName, String workerName, String jobTitle) {
        return makeMsg("일 완료 확정!",
            ownerName + "님이 " + workerName + "님의 " + jobTitle + "완료를 확정했습니다!");

    }
    public FcmDTO reject2WorkerMsg(String ownerName, String workerName, String jobTitle) {
        return makeMsg("일 완료 거절! 다시 인증 바랍니다.",
            ownerName + "님이 " + workerName + "님의 " + jobTitle + "완료를 거절했습니다.");

    }

    public FcmDTO askingJob2ClientMsg(String ownerName, String workerName, String jobTitle) {
        return makeMsg("일 해결 신청!",
            workerName + "님이 " + ownerName + "님의 " + jobTitle + "을 하고 싶어 합니다!");
    }

    public FcmDTO startJob2ClientMsg(String ownerName, String workerName, String jobTitle) {
        return  makeMsg("일 시작 알림", workerName + "님이 " + ownerName + "님의 " + jobTitle + "을 시작했습니다!");

    }

    public FcmDTO sayYes2ClientMsg(String ownerName, String workerName, String jobTitle) {
        return makeMsg("일 신청 수락 알림",
            workerName + "님이 " + ownerName + "님의 " + jobTitle + "해결 의뢰을 수락하셨습니다!");
    }

    public FcmDTO sayNo2ClientMsg(String ownerName, String workerName, String jobTitle) {
        return makeMsg("일 신청 거절 알림",
            workerName + "님이 " + ownerName + "님의 " + jobTitle + "해결 의뢰을 거절 하셨습니다.");

    }

    public FcmDTO continueJobMsg(String ownerName, String workerName){
        return makeMsg("일 재개 알림", workerName + "님이 " + ownerName + "님의 일을 재개했습니다.");

    }

    public FcmDTO finishJobMsg(String ownerName, String workerName, String jobTitle) {
        return makeMsg("일 성공 알림", workerName + "님이 " + ownerName + "님의 일을 끝냈다고 합니다!");

    }

    private FcmDTO makeMsg(String title, String body) {
        return FcmDTO.builder()
            .title(title)
            .body(body)
            .build();
    }

}
