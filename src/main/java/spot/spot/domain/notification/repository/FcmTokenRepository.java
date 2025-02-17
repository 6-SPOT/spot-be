package spot.spot.domain.notification.repository;

import feign.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.notification.entity.FcmToken;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {

    Optional<FcmToken> findByMemberAndData(Member member, String data);

    // Member가 가진 모든 FCM 토큰을 가져와라
    @Query("SELECT f FROM FcmToken f JOIN FETCH f.member WHERE f.member = :member")
    Optional<List<FcmToken>> fetchAll4Member(@Param("member") Member member);
    // 유효하지 않은 토큰 삭제
    void deleteByData(String fcmToken);
}
