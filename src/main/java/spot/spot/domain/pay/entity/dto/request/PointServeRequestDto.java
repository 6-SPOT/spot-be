package spot.spot.domain.pay.entity.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import spot.spot.domain.pay.entity.Point;

@Builder
public record PointServeRequestDto(

        @NotBlank(message = "포인트 이름이 빈값입니다. 확인해주세요.")
        String pointName,

        @Positive(message = "포인트는 양수 값이여야 합니다.")
        int point,

        @Positive(message = "갯수는 양수 값이어야 합니다.")
        int count
) {

    public static PointServeRequestDto create(String pointName, int point, int count) {
        return PointServeRequestDto.builder()
                .pointName(pointName)
                .point(point)
                .count(count)
                .build();
    }

    public static Point toPoint(PointServeRequestDto req, String pointCode) {
        return Point.builder()
                .pointCode(pointCode)
                .pointName(req.pointName)
                .point(req.point)
                .count(req.count)
//                .isValid(true)
                .build();
    }
}
