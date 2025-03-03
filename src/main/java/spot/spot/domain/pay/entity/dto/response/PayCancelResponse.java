package spot.spot.domain.pay.entity.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
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
    @NoArgsConstructor
    public static class Amount {
        private int total;
        private int tax_free;
        private int vat;
        private int point;
        private int discount;
        private int green_deposit;

        @Builder
        private Amount(int total, int tax_free, int vat, int point, int discount, int green_deposit) {
            this.total = total;
            this.tax_free = tax_free;
            this.vat = vat;
            this.point = point;
            this.discount = discount;
            this.green_deposit = green_deposit;
        }

        public Amount create(int total, int point) {
            return Amount.builder()
                    .total(total)
                    .point(point)
                    .build();
        }
    }

    @Builder
    private PayCancelResponse(String tid, String cid, String status, String partner_order_id, String partner_user_id, String payment_method_type, String aid, int quantity, Amount amount, Amount canceled_amount, Amount cancel_available_amount, Amount approved_cancel_amount, String created_at, String approved_at, String canceled_at) {
        this.tid = tid;
        this.cid = cid;
        this.status = status;
        this.partner_order_id = partner_order_id;
        this.partner_user_id = partner_user_id;
        this.payment_method_type = payment_method_type;
        this.aid = aid;
        this.quantity = quantity;
        this.amount = amount;
        this.canceled_amount = canceled_amount;
        this.cancel_available_amount = cancel_available_amount;
        this.approved_cancel_amount = approved_cancel_amount;
        this.created_at = created_at;
        this.approved_at = approved_at;
        this.canceled_at = canceled_at;
    }

    public PayCancelResponse create(String nickname, String domain, Amount amount, Amount cancelAmount) {
        return PayCancelResponse.builder()
                .partner_user_id(nickname)
                .partner_order_id(domain)
                .amount(amount)
                .canceled_amount(cancelAmount)
                .build();
    }
}