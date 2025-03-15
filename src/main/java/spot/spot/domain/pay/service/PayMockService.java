package spot.spot.domain.pay.service;

import com.klaytn.caver.wallet.keyring.SingleKeyring;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spot.spot.domain.job.command.entity.Job;
import spot.spot.domain.job.query.repository.dsl.SearchingOneQueryDsl;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.member.service.MemberService;
import spot.spot.domain.pay.entity.KlayAboutJob;
import spot.spot.domain.pay.entity.PayHistory;
import spot.spot.domain.pay.entity.PayStatus;
import spot.spot.domain.pay.entity.dto.response.*;
import spot.spot.domain.pay.repository.KlayAboutJobRepository;
import spot.spot.domain.pay.repository.PayHistoryRepository;
import spot.spot.domain.pay.repository.PayRepositoryDsl;
import spot.spot.domain.pay.util.PayUtil;
import spot.spot.global.klaytn.ConnectToKlaytnNetwork;
import spot.spot.global.klaytn.api.ExchangeRateByBithumbApi;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayMockService {

    private final SearchingOneQueryDsl searchingOneQueryDsl;
    private final PayRepositoryDsl payRepositoryDsl;
    @Value("${kakao.pay.cid}")
    private String cid;

    @Value("${kakao.pay.admin-key}")
    private String adminKey;

    @Value("${kakao.pay.partner_order_id}")
    private String domain;

    private final MemberService memberService;
    private final PayHistoryRepository payHistoryRepository;
    private final ExchangeRateByBithumbApi exchangeRateByBithumbApi;
    private final ConnectToKlaytnNetwork connectToKlaytnNetwork;
    private final KlayAboutJobRepository klayAboutJobRepository;
    private final PayUtil payUtil;

    //결제준비Mock
    public PayReadyResponseDto payReady(String memberId, String content, int amount, int point, Job job) {
        Member findMember = memberService.findById(memberId);

        ///결제 내역 기록 및 결제 준비
        PayHistory payHistory = savePayHistory(findMember.getNickname(), amount, point, job);
        payUtil.insertFromSchedule(payHistory);
        Long id = job.getId();
        String StringIdNumber = String.valueOf(id);

        PayReadyResponse payReadyResponse = new PayReadyResponse();
        PayReadyResponse mockPayReadyResponse = payReadyResponse.create("mockTid_" + StringIdNumber, "https://mock-redirect-pc-url.com/payment/success", "https://mock-redirect-mobile-url.com/payment/success");
        return PayReadyResponseDto.of(mockPayReadyResponse);
    }

    //결제 승인Mock
    public PayApproveResponseDto payApprove(String memberId, Job job, String pgToken, int totalAmount) {
        Member findMember = memberService.findById(memberId);
        ///결제 내역 업데이트
        Optional<String> workerNicknameByJob = searchingOneQueryDsl.findWorkerNicknameByJob(job);
        String worker = workerNicknameByJob.orElse("");
        PayHistory payHistory = payHistoryRepository.findByJobAndDepositor(job, findMember.getNickname()).orElseThrow(() -> new GlobalException(ErrorCode.JOB_NOT_FOUND));
        updatePayHistory(payHistory, PayStatus.PROCESS, worker);

        ///결제 시간이 지난 결제건은 결제 불가
        if (payHistory.getPayStatus().equals(PayStatus.FAIL)) {
            throw new GlobalException(ErrorCode.ALREADY_PAY_FAIL);
        }

        ///결제 승인 시 스케쥴러에서 삭제함
        payUtil.deleteFromSchedule(payHistory);

        ///클레이튼에 전송
        double peb = exchangeToPebAndSaveExchangeInfo(job, totalAmount);
        depositToKlaytn((int) peb);

        ///결제 승인
        PayApproveResponse payApproveResponse = new PayApproveResponse();
        PayApproveResponse.Amount amount = new PayApproveResponse.Amount(totalAmount, 0, 0, 0, 0, 0);
        PayApproveResponse mockPayApproveResponse = payApproveResponse.create(job.getTid(), domain, findMember.getNickname(), amount, job.getContent());

        return PayApproveResponseDto.of(mockPayApproveResponse);
    }

    //결제 취소(등록 취소 시)
    public PayCancelResponseDto payCancel(Job job, int amount){
        Member memberByJobInfo = memberService.findMemberByJobInfo(job);
        PayHistory payHistory = findByJobWithDepositor(job, memberByJobInfo.getNickname());

        ///포인트로 반환
        int paybackAmount = payHistory.getPayAmount() + payHistory.getPayPoint();
        returnPoints(null, payHistory.getDepositor(), paybackAmount);

        ///결제 내역 업데이트
        updatePayHistory(payHistory, PayStatus.FAIL, payHistory.getWorker());
        payUtil.deleteFromSchedule(payHistory);

        ///클레이튼에 전송
        KlayAboutJob klayAboutJob = klayAboutJobRepository.findByJob(job).orElseThrow(() -> new GlobalException(ErrorCode.PAY_SUCCESS_NOT_FOUND));
        double amtKlay = klayAboutJob.getAmtKlay();
        transferToKlaytn(amtKlay);
        PayCancelResponse payCancelResponse = new PayCancelResponse();
        PayCancelResponse.Amount payCancelAmount = new PayCancelResponse.Amount();
        PayCancelResponse.Amount mockAmount = payCancelAmount.create(amount, 0);
        PayCancelResponse mockPayCancelResponse = payCancelResponse.create(memberByJobInfo.getNickname(), domain, mockAmount, mockAmount);
        return PayCancelResponseDto.of(mockPayCancelResponse);
    }

    @Transactional
    protected PayHistory savePayHistory(String depositor, int payAmount, int point, Job job) {
        PayHistory payHistory = PayHistory.builder()
                .payAmount(payAmount)
                .payPoint(point)
                .depositor(depositor)
                .worker("")
                .job(job)
                .payStatus(PayStatus.PENDING)
                .build();

        return payHistoryRepository.save(payHistory);
    }

    //매칭 시 PayHistory에 worker 업데이트
    public void updatePayHistory(PayHistory payHistory, PayStatus payStatus, String worker) {
        if(worker == null) throw new GlobalException(ErrorCode.MEMBER_NOT_FOUND);
        payHistory.setWorker(worker);
        payHistory.setPayStatus(payStatus);
    }

    private void depositToKlaytn(int peb) {
        SingleKeyring singleKeyring = connectToKlaytnNetwork.getSingleKeyring();
        connectToKlaytnNetwork.deposit(peb, singleKeyring.getAddress());
    }

    private void transferToKlaytn(double amtKlay) {
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

    public int findPayAmountByMatchingJob(Long matchingId, Long workerId) {
        return payRepositoryDsl.findByPayAmountFromMatchingJob(matchingId, workerId);
    }

    public PayHistory findByJobWithDepositor(Job job, String depositor) {
        return payHistoryRepository.findByJobAndDepositor(job, depositor).orElseThrow(() -> new GlobalException(ErrorCode.JOB_NOT_FOUND));
    }

    public PayHistory findByJobWithWorker(Job job, String worker) {
        return payHistoryRepository.findByJobAndWorker(job, worker).orElseThrow(() -> new GlobalException(ErrorCode.JOB_NOT_FOUND));
    }
}
