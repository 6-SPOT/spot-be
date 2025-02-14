package spot.spot.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spot.spot.domain.member.entity.Worker;

public interface WorkerRepository extends JpaRepository<Worker, Long> {

}
