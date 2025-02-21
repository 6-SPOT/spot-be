package spot.spot.domain.pay.entity.dto;

import java.util.List;

public record PointServeRequestDto(
        List<PointServeDto> registerDto
) {
}
