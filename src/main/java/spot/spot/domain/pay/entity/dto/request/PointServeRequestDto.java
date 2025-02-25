package spot.spot.domain.pay.entity.dto.request;

public record PointServeRequestDto(
        String pointName,
        int point,
        int count
) {
}
