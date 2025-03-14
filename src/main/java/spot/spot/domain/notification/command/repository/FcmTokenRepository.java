package spot.spot.domain.notification.command.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.notification.command.entity.FcmToken;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {

    Optional<FcmToken> findByMemberAndData(Member member, String data);

    List<FcmToken> findAllByMember_Id(long memberId);
    // 유효하지 않은 토큰 삭제
    void deleteByData(String fcmToken);
}
