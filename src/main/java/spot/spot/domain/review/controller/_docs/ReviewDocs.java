package spot.spot.domain.review.controller._docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import spot.spot.domain.review.dto.request.ReviewRequestDto;
import spot.spot.domain.review.dto.response.AuthoredReview;
import spot.spot.domain.review.dto.response.ReceivedReview;


@Tag(
    name="7. REVIEW API",
    description = "<br/> REVIEW API "
)
public interface ReviewDocs {

    @Operation(summary = "리뷰 등록",
        description = "DTO 확인 필요")
    @PostMapping
    public void createReview(@RequestBody @Valid ReviewRequestDto requestDto);

    @Operation(summary = "내가 쓴 리뷰 전부 보기",
        description = "내가 쓴 리뷰를 전부 보여줍니다. 페이지 네이션 적용 완료")
    @GetMapping
    public Slice<AuthoredReview> getReviewListByAuthor(@PathVariable long id,
        @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
        Pageable pageable);

    @Operation(summary = "내가 받은 리뷰 전부 보기",
        description = "내가 받은 리뷰를 전부 보여줍니다. 페이지 네이션 적용 완료")
    @GetMapping
    public Slice<ReceivedReview> getReviewListByReceiver(@PathVariable long id,
        @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
        Pageable pageable);

    @Operation(summary = "인프라 OK 헬스 체크용",
        description = "인프라 OK 헬스 체크용")
    @GetMapping
    public void ok();
}
