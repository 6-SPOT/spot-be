package spot.spot.domain.notification.command.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spot.spot.domain.notification.command.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

}
