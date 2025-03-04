package spot.spot.domain.pay.entity.dto.response;

import lombok.Builder;
import spot.spot.domain.pay.entity.Point;

@Builder
public record PointServeResponseDto(
        String pointName,
        int point,
        String pointCode
) {

    public Point toPoint(PointServeResponseDto requestDto) {
        return Point.builder()
                .pointCode(requestDto.pointCode)
                .pointName(requestDto.pointName())
                .isValid(true)
                .point(requestDto.point())
                .build();
    }

    public static PointServeResponseDto create(String pointName, int point, String pointCode) {
        return PointServeResponseDto.builder()
                .pointName(pointName)
                .point(point)
                .pointCode(pointCode)
                .build();
    }
}
