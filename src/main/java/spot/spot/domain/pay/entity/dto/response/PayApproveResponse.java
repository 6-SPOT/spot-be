package spot.spot.domain.pay.entity.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}
