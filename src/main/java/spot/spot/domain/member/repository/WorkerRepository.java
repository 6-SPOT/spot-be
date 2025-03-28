package spot.spot.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spot.spot.domain.member.entity.Worker;

@Repository
public interface WorkerRepository extends JpaRepository<Worker, Long> {

}
