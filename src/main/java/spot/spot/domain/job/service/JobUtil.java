package spot.spot.domain.job.service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Named;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import spot.spot.domain.job.entity.Matching;
import spot.spot.domain.job.entity.MatchingStatus;
import spot.spot.domain.job.repository.dsl.ChangeJobStatusDsl;
import spot.spot.domain.job.repository.jpa.MatchingRepository;
import spot.spot.global.logging.ColorLogger;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;
import spot.spot.global.util.ConstantUtil;
@Slf4j
@Component
@RequiredArgsConstructor
public class JobUtil {

    private final ThreadPoolTaskScheduler taskScheduler;
    private final MatchingRepository matchingRepository;
    private final Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    private final TransactionTemplate transactionTemplate;
    private final ChangeJobStatusDsl changeJobStatusDsl;

    // 줌 레벨을 실제 KM로 변환하는 함수
    public double convertZoomToRadius(int zoom_level) {
        return switch (zoom_level) {
            case 21 -> 0.05;
            case 20 -> 0.1;
            case 19 -> 0.2;
            case 18 -> 0.5;
            case 17 -> 1;
            case 16 -> 2;
            case 15 -> 5;
            case 14 -> 10;
            case 13 -> 20;
            case 12 -> 50;
            default -> 100;
        };
    }

    // 위도 경도 간의 차이를 km 차이로 변환하는 함수
    @Named("haversineDistance")
    public static double calculateHaversineDistance(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double distance_ratio = Math.sin(dLat / 2) * Math.sin(dLat / 2)
            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double distance_radian = 2 * Math.atan2(Math.sqrt(distance_ratio), Math.sqrt(1 - distance_ratio));

        return ConstantUtil.EARTH_RADIUS_KM * distance_radian;
    }
    // SLEEP 상태로 돌아선 MATCHING RECORD를 10분 뒤에 취소로 바꾸는 비동기 로직
    public void scheduledSleepMatching2Cancel(Matching matching) {
        long matching_id = matching.getId();
        if(scheduledTasks.containsKey(matching_id)) {
            log.info("취소하려는 매칭이 이미 스케줄 예약이 걸려 있습니다. id= {} 따라서 새로운 취소 예약 작업은 종료됩니다.", matching_id);
            return;
        }

        ScheduledFuture<?> future = taskScheduler.schedule(
            () -> executeCancel(matching_id),
            Instant.now().plusSeconds(60));

        scheduledTasks.put(matching_id, future);
        ColorLogger.green("Matching-{}는 10분 후 취소 예약 설정 되었습니다. SIGN-OK ", matching_id);


    }

    public void executeCancel(long matching_id) {
        transactionTemplate.execute(status -> {
            Matching matching = matchingRepository.findById(matching_id).orElseThrow(() -> new GlobalException(
                ErrorCode.MATCHING_NOT_FOUND));
            if(matching.getStatus() != MatchingStatus.SLEEP) {
                ColorLogger.red("더 이상 잠수 상태가 아닌 해결사-일 Matching-{}은 skip 합니다. ", matching_id);
                return -1;
            }
            changeJobStatusDsl.updateMatchingStatus(matching_id, MatchingStatus.CANCEL);
            scheduledTasks.remove(matching_id);
            log.info("Matching-{}는 해결사의 노쇼로 인해 취소되었음을 알립니다.", matching_id);
            return 0;
        });
    }

    // 스케줄링에 이미 들어간 매칭을 스케줄러에서 빼내기 -> 취소 작업 철회
    public void withdrawalExistingScheduledTask(long matchingId) {
        ScheduledFuture<?> future = scheduledTasks.remove(matchingId);  // 맵에서 해당 Future 객체 삭제
        if(future != null) {    // 맵에 있었다면 future는 null이 아님.
            future.cancel(false);   // 해당 Future 객체의 예약 취소
            log.info("Matching-id: {}를 취소 예약이 철회되었습니다.", matchingId);
        }
    }
}
