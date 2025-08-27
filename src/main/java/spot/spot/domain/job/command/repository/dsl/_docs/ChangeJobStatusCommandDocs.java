package spot.spot.domain.job.command.repository.dsl._docs;

import com.querydsl.jpa.JPAExpressions;
import java.util.Optional;
import spot.spot.domain.job.command.entity.Job;
import spot.spot.domain.job.command.entity.Matching;
import spot.spot.domain.job.command.entity.MatchingStatus;
import spot.spot.domain.member.entity.Member;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;

public interface ChangeJobStatusCommandDocs {
    // 1. 해당 해결사가 예상한 일의 상태를 가지고 있는지 체크
    public Job findJobWithValidation(long worker_id, long job_id, MatchingStatus expected_status);
    // 2. 이미 해당 해결사가 그 일과 연관 관계를 맺고 있는지 체크
    public Job findJobWithValidation(long worker_id, long job_id);
    // 3. 해당 해결사가 예상한 일의 상태를 가지고 있는지 체크 - 그 일이 두가지 일 때
    public void findJobWithValidation(long worker_id, long job_id, MatchingStatus expected_status1, MatchingStatus expected_status2);
    // 4. 일의 상태 변경 (변경 후 변경된 Matching Entity 반환)
    public Matching updateMatchingStatus(long worker_id, long job_id, MatchingStatus next);
    // 5. 일 상태 변경 변경 후 아무것도 반환하지 않음.
    public void updateMatchingStatus (long matching_id, MatchingStatus next);
    // 6. 일과 일의 주인을 찾아온다.
    public Member getJobsOwner(long jobId);
}
