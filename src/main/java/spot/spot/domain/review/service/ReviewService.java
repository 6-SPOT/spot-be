package spot.spot.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spot.spot.domain.job.entity.Job;
import spot.spot.domain.job.repository.jpa.JobRepository;
import spot.spot.domain.review.dto.ReviewRequestDto;
import spot.spot.domain.review.dto.ReviewResponseDto;
import spot.spot.domain.review.entity.Review;
import spot.spot.domain.review.repository.ReviewRepository;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final JobRepository jobRepository;

    @Transactional
    public void createReview(Long writerId, ReviewRequestDto dto) {
//        Review review = new Review();
//        review.setJobId(requestDto.getJobId());
//        review.setWriterId(writerId);
//        review.setTargetId(requestDto.getTargetId());
//        review.setComment(requestDto.getComment());
//        review.setScore(requestDto.getScore());

        Job job = jobRepository.findById(dto.getJobId())
                .orElseThrow(() -> new GlobalException(ErrorCode.JOB_NOT_FOUND));
        Review review = Review.builder()
                .job(job)
                .writerId(writerId)
                .score(dto.getScore())
                .targetId(dto.getTargetId())
                .comment(dto.getComment())
                .build();
        reviewRepository.save(review);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getReviewsByJobId(Long jobId) {
        List<Review> reviews = reviewRepository.findAllByJob_Id(jobId);

        return reviews.stream()
                .map(ReviewResponseDto::new)
                .collect(Collectors.toList());
    }
}
