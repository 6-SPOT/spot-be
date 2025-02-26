package spot.spot.domain.pay.entity.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class PayOrderResponse {
    private String tid;
    private String cid;
    private String status;
    private String partner_order_id;
    private String partner_user_id;
    private String payment_method_type;
    private String item_name;
    private String item_code;
    private int quantity;
    private String created_at;
    private String approved_at;
    private String canceled_at;
    private Amount amount;
    private Amount canceled_amount;
    private Amount cancel_available_amount;
    private SelectedCardInfo selected_card_info;
    private List<PaymentActionDetail> payment_action_details;

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

    @Getter
    @Setter
    @Builder
    public static class SelectedCardInfo {
        private String card_bin;
        private String card_type;
        private String install_month;
        private String interest_free_install;
    }

    @Getter
    @Setter
    @Builder
    public static class PaymentActionDetail {
        private String aid;
        private String approved_at;
        private int point_amount;
        private int discount_amount;
        private int green_deposit_amount;
        private String payment_action_type;
    }
}
