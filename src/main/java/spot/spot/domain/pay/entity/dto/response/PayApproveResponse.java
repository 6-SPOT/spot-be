package spot.spot.domain.pay.entity.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class PayApproveResponse {
    private String aid;
    private String tid;
    private String cid;
    private String sid;
    private String partner_order_id;
    private String partner_user_id;
    private String item_name;
    private String item_code;
    private String payload;
    private int quantity;
    private Amount amount;
    private String payment_method_type;
    private CardInfo card_info;
    private String created_at;
    private String approved_at;

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
        public Amount(int total, int tax_free, int vat, int point, int discount, int green_deposit) {
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

    @Getter
    @Setter
    @Builder
    public static class CardInfo {
        private String kakaopay_purchase_corp;
        private String kakaopay_purchase_corp_code;
        private String kakaopay_issuer_corp;
        private String kakaopay_issuer_corp_code;
        private String bin;
        private String card_type;
        private String install_month;
        private String approved_id;
        private String card_mid;
        private String interest_free_install;
        private String card_item_code;
        private String installment_type;
    }

    @Builder
    private PayApproveResponse(String aid, String tid, String cid, String sid, String partner_order_id, String partner_user_id, String item_name, String item_code, String payload, int quantity, Amount amount, String payment_method_type, CardInfo card_info, String created_at, String approved_at) {
        this.aid = aid;
        this.tid = tid;
        this.cid = cid;
        this.sid = sid;
        this.partner_order_id = partner_order_id;
        this.partner_user_id = partner_user_id;
        this.item_name = item_name;
        this.item_code = item_code;
        this.payload = payload;
        this.quantity = quantity;
        this.amount = amount;
        this.payment_method_type = payment_method_type;
        this.card_info = card_info;
        this.created_at = created_at;
        this.approved_at = approved_at;
    }

    // tid, cid, partner_order_id, partner_user_id, Amount
    public PayApproveResponse create(String tid, String partner_order_id, String partner_user_id, Amount amount, String content) {
        return PayApproveResponse.builder()
                .tid(tid)
                .partner_user_id(partner_user_id)
                .partner_order_id(partner_order_id)
                .amount(amount)
                .item_name(content)
                .build();
    }
}
