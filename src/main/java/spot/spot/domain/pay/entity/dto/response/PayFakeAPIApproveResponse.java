package spot.spot.domain.pay.entity.dto.response;

import lombok.Getter;

@Getter
public class PayFakeAPIApproveResponse {
    private String status;
    private String message;
    private ResponseData data;

    @Getter
    public class ResponseData {
        String itemName;
        String partnerOrderId;
        String partnerUserId;
        String totalAmount;
        String quantity;
    }
}
