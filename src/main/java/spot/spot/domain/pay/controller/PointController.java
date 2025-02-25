package spot.spot.domain.pay.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import spot.spot.domain.pay.entity.dto.request.PointServeRequestDto;
import spot.spot.domain.pay.entity.dto.response.PointServeResponseDto;
import spot.spot.domain.pay.service.PointService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/point")
public class PointController {

    private final PointService pointService;

    @PostMapping("/serve")
    public List<PointServeResponseDto> servePointCoupon(@RequestBody List<PointServeRequestDto> requestDto) {
        return pointService.servePoint(requestDto);
    }

    @GetMapping("/register")
    public void registerPointCoupon(@RequestParam String pointCode, Authentication auth) {
        pointService.registerPoint(pointCode, auth.getName());
    }

    @PostMapping("/delete")
    public void deletePointCoupon(@RequestParam String pointCode) {
        pointService.deletePoint(pointCode);
    }
}
