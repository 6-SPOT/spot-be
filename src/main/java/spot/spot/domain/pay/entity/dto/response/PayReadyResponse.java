package spot.spot.domain.pay.entity.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class PayReadyResponse {

    private String tid;
    private boolean tms_result;
    private String next_redirect_pc_url; //웹
    private String next_redirect_mobile_url;
    private String next_redirect_app_url;
    private String android_app_scheme;
    private String ios_app_scheme;
    private String created_at;

    @Builder
    private PayReadyResponse(String tid, boolean tms_result, String next_redirect_pc_url, String next_redirect_mobile_url, String next_redirect_app_url, String android_app_scheme, String ios_app_scheme, String created_at) {
        this.tid = tid;
        this.tms_result = tms_result;
        this.next_redirect_pc_url = next_redirect_pc_url;
        this.next_redirect_mobile_url = next_redirect_mobile_url;
        this.next_redirect_app_url = next_redirect_app_url;
        this.android_app_scheme = android_app_scheme;
        this.ios_app_scheme = ios_app_scheme;
        this.created_at = created_at;
    }

    public PayReadyResponse create(String tid, String pcUrl, String mobileUrl) {
        return PayReadyResponse.builder()
                .tid(tid)
                .next_redirect_pc_url(pcUrl)
                .next_redirect_mobile_url(mobileUrl)
                .build();
    }
}
