package spot.spot.domain.pay.service;

import com.klaytn.caver.wallet.keyring.SingleKeyring;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

import java.util.*;

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

    private final MemberService memberService;
    private final PayHistoryRepository payHistoryRepository;
    private final ExchangeRateByBithumbApi exchangeRateByBithumbApi;
    private final ConnectToKlaytnNetwork connectToKlaytnNetwork;
    private final KlayAboutJobRepository klayAboutJobRepository;
    private final PayAPIRequestService payAPIRequestService;

    //결제준비 (결제페이지로 이동)
    public PayReadyResponseDto payReady(String memberNickname, String content, int amount, int point) {
        ///파라미터 검증
        List<Object> params = Arrays.asList(memberNickname, content, amount, point);
        List<ErrorCode> errorCodes = Arrays.asList(ErrorCode.EMPTY_MEMBER, ErrorCode.EMPTY_TITLE, ErrorCode.INVALID_AMOUNT, ErrorCode.EMPTY_POINT);
        validateParams(params, errorCodes);

        ///요청 파라미터 생성
        String totalAmount = String.valueOf(amount - point);
        Map<String, String> parameters = createPaymentParameters(memberNickname, null, content, "1", totalAmount, null, false);

        ///결제 내역 기록 및 결제 준비
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(parameters, getHeaders());
        PayHistory payHistory = savePayHistory(memberNickname, amount, point);
        PayReadyResponse payReadyResponse = payAPIRequestService.payAPIRequest("ready", requestEntity, PayReadyResponse.class);
        return PayReadyResponseDto.of(payReadyResponse, payHistory);
    }


    //결제 승인(결제)
    public PayApproveResponseDto payApprove(String memberId, Job job, String pgToken, int totalAmount) {
        ///파라미터 검증
        List<Object> params = Arrays.asList(job.getId(), pgToken, totalAmount);
        List<ErrorCode> errorCodes = Arrays.asList(ErrorCode.JOB_NOT_FOUND, ErrorCode.EMPTY_PG_TOKEN, ErrorCode.INVALID_AMOUNT);
        validateParams(params, errorCodes);

        ///요청 파라미터 생성
        Member findMember = memberService.findById(memberId);
        Map<String, String> parameters = createPaymentParameters(findMember.getNickname(), job.getTid(), null, null, null, pgToken, false);

        ///결제 내역 업데이트
        Optional<String> workerNicknameByJob = matchingDsl.findWorkerNicknameByJob(job);
        String worker = workerNicknameByJob.orElse("");
        updatePayHistory(job.getPayment(), PayStatus.PROCESS, worker);

        ///클레이튼에 전송
        double peb = exchangeToPebAndSaveExchangeInfo(job, totalAmount);
        depositToKlaytn((int) peb);

        ///결제 승인
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(parameters, getHeaders());
        PayApproveResponse payApproveResponse = payAPIRequestService.payAPIRequest("approve", requestEntity, PayApproveResponse.class);
        return PayApproveResponseDto.of(payApproveResponse);
    }

    //주문 조회
    public PayOrderResponseDto payOrder(String tid) {
        ///파라미터 검증
        List<Object> params = Arrays.asList(tid);
        List<ErrorCode> errorCodes = Arrays.asList(ErrorCode.EMPTY_TID);
        validateParams(params, errorCodes);

        ///요청 파라미터 생성
        Map<String, String> parameters = new HashMap<>();
        parameters.put("cid", cid);
        parameters.put("tid", tid);

        ///주문 내역 조회
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(parameters, getHeaders());
        PayOrderResponse PayOrderResponse = payAPIRequestService.payAPIRequest("order", requestEntity, PayOrderResponse.class);
        return PayOrderResponseDto.of(PayOrderResponse);
    }

    //결제 취소(등록 취소 시)
    public PayCancelResponseDto payCancel(Job job, int amount){
        ///파라미터 검증
        PayHistory payHistory = job.getPayment();
        List<Object> params = Arrays.asList(job.getId(), amount, payHistory.getPayStatus());
        List<ErrorCode> errorCodes = Arrays.asList(ErrorCode.JOB_NOT_FOUND, ErrorCode.INVALID_AMOUNT, ErrorCode.ALREADY_PAY_FAIL);
        validateParams(params, errorCodes);

        ///요청 파라미터 생성
        String totalAmount = String.valueOf(amount);
        Map<String, String> parameters = createPaymentParameters(null, job.getTid(), null, null, totalAmount, null, true);

        ///포인트로 반환
        int paybackAmount = payHistory.getPayAmount() + payHistory.getPayPoint();
        returnPoints(null, payHistory.getDepositor(), paybackAmount);

        ///결제 내역 업데이트
        updatePayHistory(payHistory, PayStatus.FAIL, payHistory.getWorker());

        ///클레이튼에 전송
        KlayAboutJob klayAboutJob = klayAboutJobRepository.findByJob(job).orElseThrow(() -> new GlobalException(ErrorCode.PAY_SUCCESS_NOT_FOUND));
        double amtKlay = klayAboutJob.getAmtKlay();
        transferToKlaytn((int) amtKlay);
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(parameters, getHeaders());
        PayCancelResponse payCancelResponse = payAPIRequestService.payAPIRequest("cancel", requestEntity, PayCancelResponse.class);
        return PayCancelResponseDto.of(payCancelResponse);
    }

    //일 완료 시 구직자에게 포인트 반환
    public PaySuccessResponseDto payTransfer(String workerId, int amount, Job job) {
        ///파라미터 검증
        List<Object> params = Arrays.asList(job.getId(), amount);
        List<ErrorCode> errorCodes = Arrays.asList(ErrorCode.JOB_NOT_FOUND, ErrorCode.INVALID_AMOUNT);
        validateParams(params, errorCodes);

        ///포인트로 반환
        int returnPoint = returnPoints(workerId, null, amount);

        ///결제 내역 업데이트
        updatePayHistory(job.getPayment(), PayStatus.SUCCESS, job.getPayment().getWorker());

        ///클레이튼에 전송
        KlayAboutJob klayAboutJob = klayAboutJobRepository.findByJob(job).orElseThrow(() -> new GlobalException(ErrorCode.PAY_SUCCESS_NOT_FOUND));
        double amtKlay = klayAboutJob.getAmtKlay();
        transferToKlaytn((int) amtKlay);
        return PaySuccessResponseDto.of(returnPoint);
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
        if(worker.equals("") || worker.isEmpty()) throw new GlobalException(ErrorCode.MEMBER_NOT_FOUND);
        payHistory.setWorker(worker);
        payHistory.setPayStatus(payStatus);
    }
    public int getPayAmountByJob(Job job) {
        return payQueryRepository.findPayAmountByPayHistory(job.getId());
    }

    private void validateParams(List<Object> params, List<ErrorCode> errorCodes) {
        for (int i = 0; i < params.size(); i++) {
            Object param = params.get(i);
            ErrorCode errorCode = errorCodes.get(i);

            if (param == null) {
                throw new GlobalException(errorCode);
            }

            if(param instanceof String && ((String) param).isEmpty()) {
                throw new GlobalException(errorCode);
            }

            if(param instanceof Integer && ((Integer) param) <= 0) {
                throw new GlobalException(errorCode);
            }

            if (param instanceof PayStatus) {
                if(((PayStatus) param).equals(PayStatus.FAIL)){
                    throw new GlobalException(errorCode);
                }
            }
        }
    }
    private Map<String, String> createPaymentParameters(String partnerUserId, String tid, String itemName, String quantity, String totalAmount, String pgToken, boolean isCancel) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("cid", cid);
        parameters.put("partner_order_id", domain);
        parameters.put("partner_user_id", partnerUserId);

        if (tid != null) {
            parameters.put("tid", tid);
        }

        if (itemName != null) {
            parameters.put("item_name", itemName);
        }

        if (quantity != null) {
            parameters.put("quantity", quantity);
        }

        if (totalAmount != null) {
            parameters.put("total_amount", totalAmount);
            parameters.put("vat_amount", "0");
            parameters.put("tax_free_amount", "0");
        }

        if (pgToken != null) {
            parameters.put("pg_token", pgToken);
        }

        if (isCancel) {
            parameters.put("cancel_amount", totalAmount);
            parameters.put("cancel_tax_free_amount", "0");
            parameters.put("cancel_vat_amount", "0");
            parameters.put("cancel_available_amount", totalAmount);
        } else {
            parameters.put("approval_url", approvalUrl);
            parameters.put("fail_url", failUrl);
            parameters.put("cancel_url", cancelUrl);
        }

        return parameters;
    }

    private void depositToKlaytn(int peb) {
        SingleKeyring singleKeyring = connectToKlaytnNetwork.getSingleKeyring();
        connectToKlaytnNetwork.deposit(peb, singleKeyring.getAddress());
    }

    private void transferToKlaytn(int amtKlay) {
        SingleKeyring singleKeyring = connectToKlaytnNetwork.getSingleKeyring();
        connectToKlaytnNetwork.transfer(amtKlay, singleKeyring.getAddress());
    }

    private double exchangeToPebAndSaveExchangeInfo(Job job, int totalAmount) {
        double kaia = exchangeRateByBithumbApi.exchangeToKaia(totalAmount / 100);
        double peb = kaia * 10000000;

        double changeRateCash = exchangeRateByBithumbApi.getChangeRateCash();
        KlayAboutJob klayAboutJob = KlayAboutJob.builder()
                .amtKlay(kaia)
                .amtKrw(totalAmount)
                .exchangeRate(changeRateCash)
                .job(job)
                .build();
        klayAboutJobRepository.save(klayAboutJob);
        return peb;
    }

    private int returnPoints(String id, String nickname, int amount) {
        Member member = memberService.findMemberByIdOrNickname(id, nickname);
        member.setPoint(member.getPoint() + amount);
        return member.getPoint() + amount;
    }

    private HttpHeaders getHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();

        String auth = "SECRET_KEY " + adminKey;

        httpHeaders.set("Authorization", auth);
        httpHeaders.set("Content-type", "application/json");

        return httpHeaders;
    }
}
