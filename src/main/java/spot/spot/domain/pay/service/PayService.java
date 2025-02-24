package spot.spot.domain.pay.service;

import com.klaytn.caver.wallet.keyring.SingleKeyring;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import spot.spot.domain.job.entity.Job;
import spot.spot.domain.job.entity.MatchingStatus;
import spot.spot.domain.job.repository.dsl.MatchingDsl;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.member.service.MemberService;
import spot.spot.domain.pay.entity.KlayAboutJob;
import spot.spot.domain.pay.entity.PayHistory;
import spot.spot.domain.pay.entity.PayStatus;
import spot.spot.domain.pay.entity.dto.*;
import spot.spot.domain.pay.repository.KlayAboutJobRepository;
import spot.spot.domain.pay.repository.PayHistoryRepository;
import spot.spot.global.klaytn.ConnectToKlaytnNetwork;
import spot.spot.global.klaytn.api.ExchangeRateByBithumbApi;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;
import spot.spot.global.response.format.ResultResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PayService {

    private final MatchingDsl matchingDsl;
    @Value("${kakao.pay.cid}")
    private String cid;

    @Value("${kakao.pay.admin-key}")
    private String adminKey;

    @Value("${kakao.pay.partner-order-id}")
    private String domain;

    private final RestTemplate restTemplate = new RestTemplate();
    private final MemberService memberService;
    private final PayHistoryRepository payHistoryRepository;
    private final ExchangeRateByBithumbApi exchangeRateByBithumbApi;
    private final ConnectToKlaytnNetwork connectToKlaytnNetwork;
    private final KlayAboutJobRepository klayAboutJobRepository;

    //결제준비 (결제페이지로 이동)
    public PayReadyResponseDto payReady(String memberNickname, String title, int amount, int point) {
        String totalAmount = String.valueOf(amount - point);

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

        PayHistory payHistory = savePayHistory(memberNickname, "", amount, point);
        return new PayReadyResponseDto(payReadyResponse.getNext_redirect_pc_url(), payReadyResponse.getNext_redirect_mobile_url(), payReadyResponse.getTid(), payHistory);
    }

    //결제 승인(결제)
    @Transactional
    public PayApproveResponse payApprove(String memberId, Job job, String pgToken, int totalAmount) {
        long parseMemberId = Long.parseLong(memberId);
        Member findMember = memberService.findById(parseMemberId);
        Map<String, String> parameters = new HashMap<>();
        parameters.put("cid", cid);
        parameters.put("partner_order_id", domain);
        parameters.put("partner_user_id", findMember.getNickname());
        parameters.put("tid", job.getTid());
        parameters.put("pg_token", pgToken);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(parameters, getHeaders());

        PayApproveResponse approve = payAPIRequest("approve", requestEntity, PayApproveResponse.class);
        Optional<String> workerNicknameByJob = matchingDsl.findWorkerNicknameByJob(job);
        String worker = "";
        if(workerNicknameByJob.isPresent()) {
            worker = workerNicknameByJob.get();
        }
        updatePayHistory(job.getPayment(), PayStatus.PENDING, worker);
        //결제 후 클레이튼 블록체인에 결제 translation 저장
        double kaia = exchangeRateByBithumbApi.exchangeToKaia(totalAmount / 100);
        log.info("kaia = {}", kaia);
        SingleKeyring singleKeyring = connectToKlaytnNetwork.getSingleKeyring();
        double peb = kaia * 10000000;
        connectToKlaytnNetwork.deposit((int) peb, singleKeyring.getAddress());

        double changeRateCash = exchangeRateByBithumbApi.getChangeRateCash();
        KlayAboutJob klayAboutJob = KlayAboutJob.builder().amtKlay(kaia).amtKrw(totalAmount).exchangeRate(changeRateCash).job(job).build();
        klayAboutJobRepository.save(klayAboutJob);

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

    //결제 취소(등록 취소 시)
    @Transactional
    public PayCancelResponse payCancel(Job job, int amount){
        String totalAmount = String.valueOf(amount);

        Map<String, String> parameters = new HashMap<>();
        parameters.put("cid", cid);
        parameters.put("tid", job.getTid());
        parameters.put("cancel_amount", totalAmount);
        parameters.put("cancel_tax_free_amount", "0");
        parameters.put("cancel_vat_amount", "0");
        parameters.put("cancel_available_amount", totalAmount);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(parameters, getHeaders());
        PayCancelResponse cancel = payAPIRequest("cancel", requestEntity, PayCancelResponse.class);
        updatePayHistory(job.getPayment(), PayStatus.FAIL, "");

        //결제된 내역의 카이아를 다시 현금화한 후 환급
        KlayAboutJob klayAboutJob = klayAboutJobRepository.findByJob(job).orElseThrow(() -> new GlobalException(ErrorCode.PAY_SUCCESS_NOT_FOUND));
        double amtKlay = klayAboutJob.getAmtKlay();
        amtKlay = exchangeRateByBithumbApi.exchangeToCash(amtKlay);
        SingleKeyring singleKeyring = connectToKlaytnNetwork.getSingleKeyring();
        String transfer = connectToKlaytnNetwork.transfer((int) amtKlay, singleKeyring.getAddress());
        log.info("txHash = {}", transfer);

//        klayAboutJobRepository.delete(klayAboutJob);
        return cancel;
    }

    //일 완료 시 구직자에게 포인트 반환
    public PaySuccessResponseDto payTransfer(Long workerId, int amount, Job job) {
        Member worker = memberService.findById(workerId);
        int point = worker.getPoint();
        worker.setPoint(point + amount);

        updatePayHistory(job.getPayment(), PayStatus.SUCCESS, worker.getNickname());
        KlayAboutJob klayAboutJob = klayAboutJobRepository.findByJob(job).orElseThrow(() -> new GlobalException(ErrorCode.PAY_SUCCESS_NOT_FOUND));
        double amtKlay = klayAboutJob.getAmtKlay();
        SingleKeyring singleKeyring = connectToKlaytnNetwork.getSingleKeyring();
        connectToKlaytnNetwork.transfer((int) amtKlay, singleKeyring.getAddress());

        klayAboutJobRepository.delete(klayAboutJob);

        return new PaySuccessResponseDto(point + amount);
    }

    //일 등록 시 payHistory에 저장
    @Transactional
    protected PayHistory savePayHistory(String depositor, String worker, int payAmount, int point) {
        PayHistory payHistory = PayHistory.builder()
                .payAmount(payAmount)
                .payPoint(point)
                .depositor(depositor)
                .worker(worker)
                .payStatus(PayStatus.PENDING)
                .build();

        return payHistoryRepository.save(payHistory);
    }

    //매칭 시 PayHistory에 worker 업데이트
    @Transactional
    public void updatePayHistory(PayHistory payHistory, PayStatus payStatus, String worker) {
        payHistory.setWorker(worker);
        payHistory.setPayStatus(payStatus);
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
