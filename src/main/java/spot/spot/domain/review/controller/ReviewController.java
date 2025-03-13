package spot.spot.domain.review.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import spot.spot.domain.review.dto.request.ReviewRequestDto;
import spot.spot.domain.review.dto.response.CompletedJobReview;
import spot.spot.domain.review.service.ReviewService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public void createReview(
            @RequestBody @Valid ReviewRequestDto requestDto) {
        reviewService.createReview(requestDto);
    }

    @GetMapping("/{jobId}")
    public List<CompletedJobReview> getReviewByJobId(@PathVariable Long jobId) {
        log.info("{}",jobId);
        return reviewService.getReviewsByJobId1(jobId);
    }

//    @GetMapping("/mypage")
//    public List<>

    @GetMapping("/ok")
    public void ok() {
    }
}
