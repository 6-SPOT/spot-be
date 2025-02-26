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
import spot.spot.domain.job.repository.dsl.MatchingDsl;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.member.service.MemberService;
import spot.spot.domain.pay.entity.KlayAboutJob;
import spot.spot.domain.pay.entity.PayHistory;
import spot.spot.domain.pay.entity.PayStatus;
import spot.spot.domain.pay.entity.dto.response.*;
import spot.spot.domain.pay.repository.KlayAboutJobRepository;
import spot.spot.domain.pay.repository.PayHistoryRepository;
import spot.spot.domain.pay.repository.PayQueryRepository;
import spot.spot.global.klaytn.ConnectToKlaytnNetwork;
import spot.spot.global.klaytn.api.ExchangeRateByBithumbApi;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PayService {

    private final MatchingDsl matchingDsl;
    private final PayQueryRepository payQueryRepository;
    @Value("${kakao.pay.cid}")
    private String cid;

    @Value("${kakao.pay.admin-key}")
    private String adminKey;

    @Value("${kakao.pay.partner_order_id}")
    private String domain;

    @Value("${kakao.pay.approval_url}")
    private String approvalUrl;

    @Value("${kakao.pay.fail_url}")
    private String failUrl;

    @Value("${kakao.pay.cancel_url}")
    private String cancelUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final MemberService memberService;
    private final PayHistoryRepository payHistoryRepository;
    private final ExchangeRateByBithumbApi exchangeRateByBithumbApi;
    private final ConnectToKlaytnNetwork connectToKlaytnNetwork;
    private final KlayAboutJobRepository klayAboutJobRepository;
    private final PayAPIRequestService payAPIRequestService;

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
        parameters.put("approval_url", approvalUrl);
        parameters.put("fail_url", failUrl);
        parameters.put("cancel_url", cancelUrl);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(parameters, getHeaders());
        PayReadyResponse payReadyResponse = payAPIRequestService.payAPIRequest("ready", requestEntity, PayReadyResponse.class);
        if(payReadyResponse.getTid() == null) throw new GlobalException(ErrorCode.FAIL_PAY_READY);

        PayHistory payHistory = savePayHistory(memberNickname, amount, point);
        return new PayReadyResponseDto(payReadyResponse.getNext_redirect_pc_url(), payReadyResponse.getNext_redirect_mobile_url(), payReadyResponse.getTid(), payHistory);
    }

    //결제 승인(결제)
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

        PayApproveResponse approve = payAPIRequestService.payAPIRequest("approve", requestEntity, PayApproveResponse.class);
        Optional<String> workerNicknameByJob = matchingDsl.findWorkerNicknameByJob(job);
        String worker = "";
        if(workerNicknameByJob.isPresent()) {
            worker = workerNicknameByJob.get();
        }
        updatePayHistory(job.getPayment(), PayStatus.PROCESS, worker);
        //결제 후 클레이튼 블록체인에 결제 translation 저장
        double kaia = exchangeRateByBithumbApi.exchangeToKaia(totalAmount / 100);
        log.info("kaia = {}", kaia);
        SingleKeyring singleKeyring = connectToKlaytnNetwork.getSingleKeyring();
        double peb = kaia * 10000000;
        connectToKlaytnNetwork.deposit((int) peb, singleKeyring.getAddress());

        double changeRateCash = exchangeRateByBithumbApi.getChangeRateCash();
        KlayAboutJob klayAboutJob = KlayAboutJob.builder()
                .amtKlay(kaia)
                .amtKrw(totalAmount)
                .exchangeRate(changeRateCash)
                .job(job)
                .payStatus(PayStatus.PROCESS)
                .build();
        klayAboutJobRepository.save(klayAboutJob);

        return approve;
    }

    //주문 조회
    public PayOrderResponse payOrder(String tid) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("cid", cid);
        parameters.put("tid", tid);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(parameters, getHeaders());

        return payAPIRequestService.payAPIRequest("order", requestEntity, PayOrderResponse.class);
    }

    //결제 취소(등록 취소 시)
    public PayCancelResponse payCancel(Job job, int amount){
        PayHistory payHistory = job.getPayment();
        if(payHistory.getPayStatus().equals(PayStatus.FAIL)) {
            throw new GlobalException(ErrorCode.ALREADY_PAY_FAIL);
        }
        String totalAmount = String.valueOf(amount);

        Map<String, String> parameters = new HashMap<>();
        parameters.put("cid", cid);
        parameters.put("tid", job.getTid());
        parameters.put("cancel_amount", totalAmount);
        parameters.put("cancel_tax_free_amount", "0");
        parameters.put("cancel_vat_amount", "0");
        parameters.put("cancel_available_amount", totalAmount);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(parameters, getHeaders());
        PayCancelResponse cancel = payAPIRequestService.payAPIRequest("cancel", requestEntity, PayCancelResponse.class);

        //결제된 내역의 카이아를 다시 현금화한 후 환급
        KlayAboutJob klayAboutJob = klayAboutJobRepository.findByJob(job).orElseThrow(() -> new GlobalException(ErrorCode.PAY_SUCCESS_NOT_FOUND));
        double amtKlay = klayAboutJob.getAmtKlay();
        amtKlay = exchangeRateByBithumbApi.exchangeToCash(amtKlay);
        SingleKeyring singleKeyring = connectToKlaytnNetwork.getSingleKeyring();
        String transfer = connectToKlaytnNetwork.transfer((int) amtKlay, singleKeyring.getAddress());
        log.info("txHash = {}", transfer);

        //결제 시 사용한 현금
        int paybackAmount = payHistory.getPayAmount() + payHistory.getPayPoint();
        //포인트로 반환
        String depositor = payHistory.getDepositor();
        Member findMember = memberService.findByNickname(depositor);
        findMember.setPoint(findMember.getPoint() + paybackAmount);

        //카이아 현금 결제 내역 삭제
        klayAboutJobRepository.delete(klayAboutJob);
        klayAboutJob.setPayStatus(PayStatus.FAIL);
        //결제내역에 결제 취소 정보 입력
        updatePayHistory(payHistory, PayStatus.FAIL, payHistory.getWorker());
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

        klayAboutJob.setPayStatus(PayStatus.SUCCESS);
        return new PaySuccessResponseDto(point + amount);
    }

    //일 등록 시 payHistory에 저장
    protected PayHistory savePayHistory(String depositor, int payAmount, int point) {
        PayHistory payHistory = PayHistory.builder()
                .payAmount(payAmount)
                .payPoint(point)
                .depositor(depositor)
                .worker("")
                .payStatus(PayStatus.PENDING)
                .build();

        return payHistoryRepository.save(payHistory);
    }

    //매칭 시 PayHistory에 worker 업데이트
    public void updatePayHistory(PayHistory payHistory, PayStatus payStatus, String worker) {
        payHistory.setWorker(worker);
        payHistory.setPayStatus(payStatus);
    }

    public int getPayAmountByJob(Job job) {
        return payQueryRepository.findPayAmountByPayHistory(job.getId());
    }

    private HttpHeaders getHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();

        String auth = "SECRET_KEY " + adminKey;

        httpHeaders.set("Authorization", auth);
        httpHeaders.set("Content-type", "application/json");

        return httpHeaders;
    }
}
