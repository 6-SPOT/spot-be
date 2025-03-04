package spot.spot.domain.job.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spot.spot.domain.job.entity.Certification;

@Repository
public interface CertificationRepository extends JpaRepository<Certification, Long> {

}
