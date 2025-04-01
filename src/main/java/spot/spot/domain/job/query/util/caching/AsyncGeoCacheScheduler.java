package spot.spot.domain.job.query.util.caching;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import spot.spot.global.scheduler.SchedulingTask;
import static  spot.spot.global.util.ConstantUtil.SYNC_INTERVAL;

@Slf4j
@Component
@RequiredArgsConstructor
public class AsyncGeoCacheScheduler {

    private final ThreadPoolTaskScheduler taskScheduler;
    private final JobGeoCacheSyncUtil jobGeoCacheSyncUtil;
    private final TransactionTemplate transactionTemplate;

    @PostConstruct
    public void start() {
        SchedulingTask<JobGeoCacheSyncUtil> task = new SchedulingTask<>(
            jobGeoCacheSyncUtil,
            JobGeoCacheSyncUtil::syncGeoHashCache,
            transactionTemplate
        );

        taskScheduler.scheduleWithFixedDelay(task, SYNC_INTERVAL);
        log.info("[GeoHashCache] 비동기 스케줄링 시작 ({} 간격)", SYNC_INTERVAL);
    }
}
