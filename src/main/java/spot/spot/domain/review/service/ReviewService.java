package spot.spot.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spot.spot.domain.job.command.entity.Job;
import spot.spot.domain.job.query.repository.jpa.JobRepository;
import spot.spot.domain.review.dto.request.ReviewRequestDto;
import spot.spot.domain.review.dto.response.AuthoredReview;
import spot.spot.domain.review.dto.response.ReceivedReview;
import spot.spot.domain.review.entity.Review;
import spot.spot.domain.review.repository.ReviewRepository;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;
import spot.spot.global.security.util.UserAccessUtil;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final JobRepository jobRepository;
    private final UserAccessUtil userAccessUtil;
    private final spot.spot.domain.review.repository.searchingReivewListDsl searchingReivewListDsl;

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

    public Slice<AuthoredReview> getReviewListByAuthor (long member_id, Pageable pageable) {
        return  searchingReivewListDsl.getReviewListByAuthor(member_id, pageable);
    }

    public Slice<ReceivedReview> getReviewListByReceiver (long member_id, Pageable pageable) {
        return searchingReivewListDsl.getReviewListByReceiver(member_id, pageable);
    }

}
