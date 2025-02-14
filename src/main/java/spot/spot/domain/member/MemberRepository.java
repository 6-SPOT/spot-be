package spot.spot.domain.member;

import org.springframework.data.jpa.repository.JpaRepository;
import spot.spot.domain.member.entity.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {

    Optional<Member> findByEmail(String email);
}
