package spot.spot.domain.member.dto.response;

import lombok.Builder;

@Builder
public record LoginResponseDto(
        String status,
        String message,
        TokenData data
) {
    public record TokenData(
            String accessToken,
            String refreshToken
    ) {}
}