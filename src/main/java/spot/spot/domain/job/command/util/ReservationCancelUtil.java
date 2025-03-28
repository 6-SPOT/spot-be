package spot.spot.domain.job.command.util;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import spot.spot.domain.job.command.entity.Matching;
import spot.spot.domain.job.command.entity.MatchingStatus;
import spot.spot.domain.job.command.repository.dsl.ChangeJobStatusCommandDsl;
import spot.spot.domain.job.command.util._docs.ReservationCancelUtilDocs;
import spot.spot.domain.job.query.repository.jpa.MatchingRepository;
import spot.spot.domain.pay.service.PayMockService;
import spot.spot.domain.pay.service.PayService;
import spot.spot.global.logging.ColorLogger;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationCancelUtil implements ReservationCancelUtilDocs {
    private final ThreadPoolTaskScheduler taskScheduler;
    private final Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    private final TransactionTemplate transactionTemplate;
    private final MatchingRepository matchingRepository;
    private final ChangeJobStatusCommandDsl changeJobStatusCommandDsl;
    private final PayService payService;
    private final PayMockService payMockService;

    public void scheduledSleepMatching2Cancel(Matching matching) {
        long matching_id = matching.getId();
        if(scheduledTasks.containsKey(matching_id)) {
            log.info("취소하려는 매칭이 이미 스케줄 예약이 걸려 있습니다. id= {} 따라서 새로운 취소 예약 작업은 종료됩니다.", matching_id);
            return;
        }

        ScheduledFuture<?> future = taskScheduler.schedule(
            () -> executeCancel(matching_id),
            Instant.now().plusSeconds(600));

        scheduledTasks.put(matching_id, future);
        ColorLogger.green("Matching-{}는 10분 후 취소 예약 설정 되었습니다. SIGN-OK ", matching_id);
    }

    public void scheduledSleepMatching2CancelTest(Matching matching) {
        long matching_id = matching.getId();
        if(scheduledTasks.containsKey(matching_id)) {
            log.info("취소하려는 매칭이 이미 스케줄 예약이 걸려 있습니다. id= {} 따라서 새로운 취소 예약 작업은 종료됩니다.", matching_id);
            return;
        }

        ScheduledFuture<?> future = taskScheduler.schedule(
                () -> mockExecuteCancel(matching_id),
                Instant.now().plusSeconds(600));

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
            changeJobStatusCommandDsl.updateMatchingStatus(matching_id, MatchingStatus.CANCEL);
            scheduledTasks.remove(matching_id);
            int payAmountByMatchingJob = payService.findPayAmountByMatchingJob(matching_id, matching.getMember().getId());
            payService.payCancel(matching.getJob(), payAmountByMatchingJob);
            log.info("Matching-{}는 해결사의 노쇼로 인해 취소되었음을 알립니다.", matching_id);
            return 0;
        });
    }

    public void mockExecuteCancel(long matching_id) {
        transactionTemplate.execute(status -> {
            Matching matching = matchingRepository.findById(matching_id).orElseThrow(() -> new GlobalException(
                    ErrorCode.MATCHING_NOT_FOUND));
            if(matching.getStatus() != MatchingStatus.SLEEP) {
                ColorLogger.red("더 이상 잠수 상태가 아닌 해결사-일 Matching-{}은 skip 합니다. ", matching_id);
                return -1;
            }
            changeJobStatusCommandDsl.updateMatchingStatus(matching_id, MatchingStatus.CANCEL);
            scheduledTasks.remove(matching_id);
            int payAmountByMatchingJob = payMockService.findPayAmountByMatchingJob(matching_id, matching.getMember().getId());
            payMockService.payCancel(matching.getJob(), payAmountByMatchingJob);
            log.info("Matching-{}는 해결사의 노쇼로 인해 취소되었음을 알립니다.", matching_id);
            return 0;
        });
    }

    public void withdrawalExistingScheduledTask(long matchingId) {
        ScheduledFuture<?> future = scheduledTasks.remove(matchingId);  // 맵에서 해당 Future 객체 삭제
        if(future != null) {    // 맵에 있었다면 future는 null이 아님.
            future.cancel(false);   // 해당 Future 객체의 예약 취소
            ColorLogger.green("Matching-id: {}를 취소 예약이 철회되었습니다.", matchingId);
        }
    }

}
