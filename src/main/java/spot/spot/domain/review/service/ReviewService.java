package spot.spot.domain.review.service;

import io.grpc.internal.ClientStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spot.spot.domain.job.command.entity.Job;
import spot.spot.domain.job.query.repository.jpa.JobRepository;
import spot.spot.domain.review.dto.request.ReviewRequestDto;
import spot.spot.domain.review.dto.response.CompletedJobReview;
import spot.spot.domain.review.entity.Review;
import spot.spot.domain.review.repository.ReviewRepository;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;
import spot.spot.global.security.util.UserAccessUtil;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final JobRepository jobRepository;
    private final UserAccessUtil userAccessUtil;

    @Transactional
    public void createReview(ReviewRequestDto dto) {
        Job job = jobRepository.findById(dto.getJobId())
                .orElseThrow(() -> new GlobalException(ErrorCode.JOB_NOT_FOUND));
        Review review = Review.builder()
                .job(job)
                .writerId(userAccessUtil.getMember().getId())
                .score(dto.getScore())
                .targetId(dto.getTargetId())
                .comment(dto.getComment())
                .build();
        reviewRepository.save(review);
    }

    //jqa 사용시
    @Transactional(readOnly = true)
    public List<CompletedJobReview> getReviewsByJobId(Long jobId) {
        return reviewRepository.findAllByJobIdAndTargetId(jobId, userAccessUtil.getMember().getId());
    }

    //jpql 사용시
    @Transactional
    public List<CompletedJobReview> getReviewsByJobId1(Long jobId) {
        List<Object[]> rawReviews = reviewRepository.findAllByJobIdAndTargetId1(jobId, userAccessUtil.getMember().getId());

        return rawReviews.stream().map(row -> new CompletedJobReview(
                ((Number) row[0]).longValue(),  // review_id
                (String) row[1],               // writer_nickname
                ((Number) row[2]).longValue(), // writer_id
                (String) row[3],               // writer_img
                ((Number) row[4]).intValue(),  // score
                (String) row[5]                // comment
        )).collect(Collectors.toList());
    }
}
