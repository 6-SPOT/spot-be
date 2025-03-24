package spot.spot.domain.pay.entity.dto.response;

import lombok.Getter;

@Getter
public class PayFakeAPIReadyResponse {
    private String status;
    private String message;

    private ResponseData data;

    @Getter
    public class ResponseData {
        String tid;
        String redirectPCUrl;
        String redirectMobileUrl;
    }
}
