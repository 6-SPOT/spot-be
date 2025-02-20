package spot.spot.domain.pay.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import spot.spot.domain.pay.entity.dto.PointServeRequestDto;
import spot.spot.domain.pay.service.PointService;
import spot.spot.global.response.format.ResultResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/point")
public class PointController {

    private final PointService pointService;

    @PostMapping("/serve")
    public ResponseEntity<ResultResponse> servePointCoupon(@RequestBody PointServeRequestDto requestDto) {
        requestDto.registerDto().stream().forEach(
                pointRegisterDto -> pointService.servePoint(
                        pointRegisterDto.pointName(),
                        pointRegisterDto.point(),
                        pointRegisterDto.pointCode())
        );

        return ResponseEntity.ok().body(ResultResponse.success(requestDto));
    }

    @GetMapping("/register")
    public ResponseEntity<ResultResponse> registerPointCoupon(@RequestParam String pointCode, Authentication auth) {
        pointService.registerPoint(pointCode, auth.getName());

        return ResponseEntity.ok().body(ResultResponse.success(pointCode));
    }

    @PostMapping("/delete")
    public ResponseEntity<ResultResponse> deletePointCoupon(@RequestParam String pointCode) {
        pointService.deletePoint(pointCode);

        return ResponseEntity.ok().body(ResultResponse.success(pointCode));
    }
}
