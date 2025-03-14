package spot.spot.domain.notification.command.service;

import org.springframework.stereotype.Component;
import spot.spot.domain.notification.command.dto.response.FcmDTO;

@Component
public class FcmMessageUtil {

    public FcmDTO makeFcmDTO(String title, String body) {
        return FcmDTO.builder()
            .title(title)
            .body(body)
            .build();
    }

    public String askRequest2ClientMsg(String attenderName, String jobName){
        return attenderName + "님이 " + jobName + "을 해결하길 원합니다!";
    }

    public String askRequest2WorkerMsg(String workerName, String jobName) {
        return workerName + "님! " + jobName + "을 해결해 주십쇼!";
    }

    public String getStartedJobMsg(String attenderName, String jobName){
        return attenderName + "님이 " + jobName + "을 시작합니다!";
    }

    public String requestAcceptedBody (String owner_name, String attender_name, String jobName){
        return owner_name + "님이 " + jobName + "에 대한 " + attender_name + "님의 요청을 승낙하셨습니다!";
    }
}
