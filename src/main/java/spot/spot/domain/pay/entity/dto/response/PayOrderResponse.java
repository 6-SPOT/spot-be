package spot.spot.domain.pay.entity.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
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

    @Builder
    private PayOrderResponse(String tid, String cid, String status, String partner_order_id, String partner_user_id, String payment_method_type, String item_name, String item_code, int quantity, String created_at, String approved_at, String canceled_at, Amount amount, Amount canceled_amount, Amount cancel_available_amount, SelectedCardInfo selected_card_info, List<PaymentActionDetail> payment_action_details) {
        this.tid = tid;
        this.cid = cid;
        this.status = status;
        this.partner_order_id = partner_order_id;
        this.partner_user_id = partner_user_id;
        this.payment_method_type = payment_method_type;
        this.item_name = item_name;
        this.item_code = item_code;
        this.quantity = quantity;
        this.created_at = created_at;
        this.approved_at = approved_at;
        this.canceled_at = canceled_at;
        this.amount = amount;
        this.canceled_amount = canceled_amount;
        this.cancel_available_amount = cancel_available_amount;
        this.selected_card_info = selected_card_info;
        this.payment_action_details = payment_action_details;
    }

    public PayOrderResponse create(String nickname, String domain, int amount, String content) {
        return PayOrderResponse.builder()
                .partner_user_id(nickname)
                .partner_order_id(domain)
                .amount(Amount.builder().total(amount).build())
                .item_name(content)
                .build();
    }
}
