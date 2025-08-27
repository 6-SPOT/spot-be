package spot.spot.domain.job.query.repository.jpa;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spot.spot.domain.job.command.entity.Matching;
import spot.spot.domain.member.entity.Member;

@Repository
public interface MatchingRepository extends JpaRepository<Matching, Long> {

    Optional<Matching> findByMemberAndJob_Id(Member member, long job_id);

}
