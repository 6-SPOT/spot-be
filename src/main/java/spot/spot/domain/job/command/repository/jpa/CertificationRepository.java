package spot.spot.domain.job.command.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spot.spot.domain.job.command.entity.Certification;

@Repository
public interface CertificationRepository extends JpaRepository<Certification, Long> {

}
