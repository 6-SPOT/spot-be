package spot.spot.domain.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import spot.spot.domain.review.dto.response.ReceivedReview;
import spot.spot.domain.review.entity.Review;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {


}
