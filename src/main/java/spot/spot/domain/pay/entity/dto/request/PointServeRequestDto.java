package spot.spot.domain.pay.entity.dto.request;

import lombok.Builder;

public record PointServeRequestDto(
        String pointName,
        int point,
        int count
) {
}
