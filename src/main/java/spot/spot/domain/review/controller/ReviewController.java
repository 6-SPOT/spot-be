package spot.spot.domain.review.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;
import spot.spot.domain.review.controller._docs.ReviewDocs;
import spot.spot.domain.review.dto.request.ReviewRequestDto;
import spot.spot.domain.review.dto.response.AuthoredReview;
import spot.spot.domain.review.dto.response.ReceivedReview;
import spot.spot.domain.review.service.ReviewService;

@Slf4j
@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController implements ReviewDocs {
    private final ReviewService reviewService;

    @PostMapping
    public void createReview(
            @RequestBody @Valid ReviewRequestDto requestDto) {
        reviewService.createReview(requestDto);
    }

    @GetMapping("/list/authored/{id}")
    public Slice<AuthoredReview> getReviewListByAuthor(@PathVariable long id, Pageable pageable) {
        return  reviewService.getReviewListByAuthor(id, pageable);
    }

    @GetMapping("/list/received/{id}")
    public Slice<ReceivedReview> getReviewListByReceiver(@PathVariable long id, Pageable pageable) {
        return reviewService.getReviewListByReceiver(id, pageable);
    }

    @GetMapping("/ok")
    public void ok() {
    }
}
