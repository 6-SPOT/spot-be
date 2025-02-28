package spot.spot.domain.pay.entity.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PayCancelResponse {
    private String tid;
    private String cid;
    private String status;
    private String partner_order_id;
    private String partner_user_id;
    private String payment_method_type;
    private String aid;
    private int quantity;
    private Amount amount;
    private Amount canceled_amount;
    private Amount cancel_available_amount;
    private Amount approved_cancel_amount;
    private String created_at;
    private String approved_at;
    private String canceled_at;

    @Getter
    @Setter
    @Builder
    public static class Amount {
        private int total;
        private int tax_free;
        private int vat;
        private int point;
        private int discount;
        private int green_deposit;
    }
}