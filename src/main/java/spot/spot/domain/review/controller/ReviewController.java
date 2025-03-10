package spot.spot.domain.review.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.spring6.processor.SpringActionTagProcessor;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.member.entity.OAuth2Member;
import spot.spot.domain.review.dto.ReviewRequestDto;
import spot.spot.domain.review.dto.ReviewResponseDto;
import spot.spot.domain.review.entity.Review;
import spot.spot.domain.review.service.ReviewService;
import spot.spot.global.security.util.UserAccessUtil;

import java.awt.image.renderable.RenderableImage;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    private final UserAccessUtil userAccessUtil;

    @PostMapping
    public void createReview(
            @RequestBody @Valid ReviewRequestDto requestDto) {
        Member currentMember = userAccessUtil.getMember();
        reviewService.createReview(currentMember.getId(), requestDto);
    }

    @GetMapping("/{jobId}")
    public List<ReviewResponseDto> getReviewByJobId(@PathVariable Long jobId) {
        log.info("{}",jobId);
        return reviewService.getReviewsByJobId(jobId);
    }

    @GetMapping("/ok")
    public void ok() {
    }
}
