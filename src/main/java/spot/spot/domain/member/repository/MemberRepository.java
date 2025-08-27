package spot.spot.domain.member.repository;

import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import spot.spot.domain.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsById(@NotNull Long id);
    Optional<Member> findByNickname(String nickname);
}
