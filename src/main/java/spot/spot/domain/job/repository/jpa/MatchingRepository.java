package spot.spot.domain.job.repository.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spot.spot.domain.job.entity.Job;
import spot.spot.domain.job.entity.Matching;
import spot.spot.domain.job.entity.MatchingStatus;
import spot.spot.domain.member.entity.Member;

@Repository
public interface MatchingRepository extends JpaRepository<Matching, Long> {

    Optional<Matching> findByMemberAndJob_Id(Member member, long job_id);
    Optional<Matching> findByJobAndStatus(Job job, MatchingStatus status);

}
