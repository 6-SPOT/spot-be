package spot.spot.domain.pay.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.member.service.MemberService;
import spot.spot.domain.pay.entity.PayHistory;
import spot.spot.domain.pay.entity.dto.*;
import spot.spot.domain.pay.repository.PayHistoryRepository;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;
import spot.spot.global.response.format.ResultResponse;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class PayService {

    @Value("${kakao.pay.cid}")
    private String cid;

    @Value("${kakao.pay.admin-key}")
    private String adminKey;

    @Value("${kakao.pay.partner-order-id}")
    private String domain;

    private final RestTemplate restTemplate = new RestTemplate();
    private final MemberService memberService;
    private final PayHistoryRepository payHistoryRepository;

    //결제준비 (결제페이지로 이동)
    public PayReadyResponseDto payReady(String memberNickname, String title, int amount, int point) {
        String totalAmount = String.valueOf(amount);

        Map<String, String> parameters = new HashMap<>();
        parameters.put("cid", cid);
        parameters.put("partner_order_id", domain);
        parameters.put("partner_user_id", memberNickname);
        parameters.put("item_name", title);
        parameters.put("quantity", "1");
        parameters.put("total_amount", totalAmount);
        parameters.put("vat_amount", "0");
        parameters.put("tax_free_amount", "0");
        parameters.put("approval_url", "http://localhost:8080/payment/success");
        parameters.put("fail_url", "http://localhost:8080/payment/fail");
        parameters.put("cancel_url", "http://localhost:8080/payment/cancel");

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(parameters, getHeaders());
        PayReadyResponse payReadyResponse = payAPIRequest("ready", requestEntity, PayReadyResponse.class);
        if(payReadyResponse.getTid() == null) throw new GlobalException(ErrorCode.FAIL_PAY_READY);

        savePayHistory(memberNickname, "", amount, point);
        return new PayReadyResponseDto(payReadyResponse.getTid(), payReadyResponse.getNext_redirect_pc_url());
    }

    //결제 승인(결제)
    public PayApproveResponse payApprove(String memberId, String tid, String pgToken) {
        long parseMemberId = Long.parseLong(memberId);
        Member findMember = memberService.findById(parseMemberId);
        Map<String, String> parameters = new HashMap<>();
        parameters.put("cid", cid);
        parameters.put("partner_order_id", domain);
        parameters.put("partner_user_id", findMember.getNickname());
        parameters.put("tid", tid);
        parameters.put("pg_token", pgToken);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(parameters, getHeaders());

        PayApproveResponse approve = payAPIRequest("approve", requestEntity, PayApproveResponse.class);

        return approve;
    }

    //주문 조회
    public PayOrderResponse payOrder(String tid) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("cid", cid);
        parameters.put("tid", tid);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(parameters, getHeaders());

        return payAPIRequest("order", requestEntity, PayOrderResponse.class);
    }

    //결제 취소(환불)
    public PayCancelResponse payCancel(String tid, int amount){
        String totalAmount = String.valueOf(amount);

        Map<String, String> parameters = new HashMap<>();
        parameters.put("cid", cid);
        parameters.put("tid", tid);
        parameters.put("cancel_amount", totalAmount);
        parameters.put("cancel_tax_free_amount", "0");
        parameters.put("cancel_vat_amount", "0");
        parameters.put("cancel_available_amount", totalAmount);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(parameters, getHeaders());

        return payAPIRequest("cancel", requestEntity, PayCancelResponse.class);
    }

    //일 완료 시 구직자에게 포인트 반환
    public PaySuccessResponseDto payTransfer(Long workerId, int amount, String title) {
        Member worker = memberService.findById(workerId);
        int point = worker.getPoint();
        worker.setPoint(point + amount);

        return new PaySuccessResponseDto(point + amount);
    }

    //일 등록 시 payHistory에 저장
    @Transactional
    protected void savePayHistory(String depositor, String worker, int payAmount, int point) {
        PayHistory payHistory = PayHistory.builder()
                .payAmount(payAmount)
                .payPoint(point)
                .depositor(depositor)
                .worker(worker)
                .build();

        payHistoryRepository.save(payHistory);
    }

    //매칭 시 PayHistory에 worker 업데이트
    @Transactional
    public void updatePayHistory(PayHistory payHistory, String worker) {
        payHistory.setWorker(worker);
    }

    private <T> T payAPIRequest(String url, HttpEntity<Map<String, String>> requestEntity, Class<T> responseType) {
        try {
            ResponseEntity<T> response = restTemplate.exchange(
                    "https://open-api.kakaopay.com/online/v1/payment/" + url,
                    HttpMethod.POST,
                    requestEntity,
                    responseType
            );
            return response.getBody(); // ✅ 응답 객체 반환
        } catch (Exception e) {
            log.error("카카오페이 API 요청 실패: {}", url, e);
            throw new GlobalException(ErrorCode.FAIL_PAY_READY);
        }
    }

    private HttpHeaders getHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();

        String auth = "SECRET_KEY " + adminKey;

        httpHeaders.set("Authorization", auth);
        httpHeaders.set("Content-type", "application/json");

        return httpHeaders;
    }
}
