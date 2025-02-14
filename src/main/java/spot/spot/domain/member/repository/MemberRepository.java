package spot.spot.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spot.spot.domain.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
