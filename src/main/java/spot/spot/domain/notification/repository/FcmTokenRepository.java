package spot.spot.domain.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spot.spot.domain.notification.entity.FcmToken;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {

}
