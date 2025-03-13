package spot.spot.domain.pay.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import spot.spot.domain.pay.entity.PayHistory;
import spot.spot.domain.pay.entity.PayStatus;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Component
@RequiredArgsConstructor
@Getter
@Slf4j
public class PayUtil {

    private final ThreadPoolTaskScheduler taskScheduler;
    private final Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    private final TransactionTemplate transactionTemplate;

    public void insertFromSchedule(PayHistory payHistory) {
        Long payHistoryId = payHistory.getId();

        ScheduledFuture<?> schedule = taskScheduler.schedule(() ->
                        payCancel(payHistory),
                Instant.now().plusSeconds(300)
        );

        scheduledTasks.put(payHistoryId, schedule);
        log.info("{} 번의 페이가 결제 준비가 되었습니다. 5분 안에 승인이 이루어지지않으면 결제가 취소됩니다.", payHistory.getId());
    }

    public void deleteFromSchedule(PayHistory payHistory) {
        Long payHistoryId = payHistory.getId();
        ScheduledFuture<?> active = scheduledTasks.remove(payHistoryId);
        if (active != null) {
            active.cancel(false);
            log.info("{} 번의 페이가 결제 승인 또는 취소가 되었습니다.", payHistoryId);
        }
    }

    public void payCancel(PayHistory payHistory) {
        transactionTemplate.execute(status -> {
            payHistory.setPayStatus(PayStatus.FAIL);
            log.info("{} 번의 페이가 취소되었습니다.", payHistory.getId());
            return 0;
        });
    }


}
