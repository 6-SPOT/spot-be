package spot.spot.domain.pay.entity.dto.response;

import lombok.Builder;
import spot.spot.domain.pay.entity.Point;

@Builder
public record PointServeResponseDto(
        String pointName,
        int point,
        String pointCode,
        int count
) {

    public Point toPoint(PointServeResponseDto requestDto) {
        return Point.builder()
                .pointCode(requestDto.pointCode)
                .pointName(requestDto.pointName())
                .point(requestDto.point())
                .count(requestDto.count())
                .build();
    }

    public static PointServeResponseDto fromPoint(Point point) {
        return PointServeResponseDto.builder()
                .pointName(point.getPointName())
                .point(point.getPoint())
                .pointCode(point.getPointCode())
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
