package spot.spot.domain.review.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spot.spot.domain.review.dto.ReviewRequestDto;
import spot.spot.domain.review.entity.Review;
import spot.spot.domain.review.repository.ReviewRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;

    @Transactional
    public Review createReview(Long writerId, ReviewRequestDto requestDto) {
        Review review = new Review();
        review.setJobId(requestDto.getJobId());
        review.setWriterId(writerId);
        review.setTargetId(requestDto.getTargetId());
        review.setComment(requestDto.getComment());
        review.setScore(requestDto.getScore());

        return reviewRepository.save(review);
    }

    public List<Review> getReviewsByJobId(Long jobId) {
        return reviewRepository.findByJobId(jobId);
    }
}
