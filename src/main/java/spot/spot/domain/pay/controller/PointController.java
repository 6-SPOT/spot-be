package spot.spot.domain.pay.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import spot.spot.domain.pay.entity.dto.request.PointServeRequestDto;
import spot.spot.domain.pay.entity.dto.response.PointServeResponseDto;
import spot.spot.domain.pay.service.PointService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/point")
public class PointController {

    private final PointService pointService;

    @PostMapping("/serve")
    public List<PointServeResponseDto> servePointCoupon(@Valid @RequestBody List<@Valid PointServeRequestDto> requestDto) {
        return pointService.servePoint(requestDto);
    }

    @GetMapping("/register")
    public void registerPointCoupon(@RequestParam @NotBlank(message = "포인트 등록 시 포인트 코드는 필수 입력값입니다.") String pointCode, Authentication auth) {
        pointService.registerPoint(pointCode, auth.getName());
    }

    @PostMapping("/delete")
    public void deletePointCoupon(@RequestParam @NotBlank(message = "포인트 삭제 시 포인트 코드는 필수 입력값입니다.") String pointCode) {
        pointService.deletePoint(pointCode);
    }
}
