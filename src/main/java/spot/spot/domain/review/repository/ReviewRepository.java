package spot.spot.domain.review.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import spot.spot.domain.review.entity.Review;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByJobId(Long jobId);
    List<Review> findAllByJob_Id(Long jobId);// 특정 일(Job)에 대한 모든 리뷰 조회
}
