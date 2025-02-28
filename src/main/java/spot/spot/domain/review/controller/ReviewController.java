package spot.spot.domain.review.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.spring6.processor.SpringActionTagProcessor;
import spot.spot.domain.review.dto.ReviewRequestDto;
import spot.spot.domain.review.entity.Review;
import spot.spot.domain.review.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Review> createReview(
            @AuthenticationPrincipal Long writerId,
            @RequestBody @Valid ReviewRequestDto requestDto) {
        Review review = reviewService.createReview(writerId, requestDto);
        return ResponseEntity.ok(review);
    }

    @GetMapping("api/job/{jobId}")
    public ResponseEntity<Review> getReviewByJobId(@PathVariable Long jobId) {
        List<Review> reviews = reviewService.getReviewsByJobId(jobId);
        return ResponseEntity.ok(reviews.get(0));
    }
}
