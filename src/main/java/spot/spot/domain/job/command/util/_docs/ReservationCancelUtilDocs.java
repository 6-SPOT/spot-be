package spot.spot.domain.job.command.util._docs;

import spot.spot.domain.job.command.entity.Matching;

public interface ReservationCancelUtilDocs {
    // 1. SLEEP 상태로 돌아선 MATCHING RECORD를 10분 뒤에 취소로 바꾸는 비동기 로직
    public void scheduledSleepMatching2Cancel(Matching matching);
    // 2. 의뢰 취소 실행
    public void executeCancel(long matching_id);
    // 3. 취소 작업 철회 (스케줄러 우선순위 큐에 들어간 내용을 삭제)
    public void withdrawalExistingScheduledTask(long matchingId);
}
