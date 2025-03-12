package spot.spot.domain.pay.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import spot.spot.domain.job.command.dto.request.RegisterJobRequest;
import spot.spot.domain.job.command.dto.response.RegisterJobResponse;
import spot.spot.domain.job.command.entity.Job;
import spot.spot.domain.job.command.entity.Matching;
import spot.spot.domain.job.command.repository.jpa.CertificationRepository;
import spot.spot.domain.job.command.service.ClientCommandService;
import spot.spot.domain.job.query.repository.jpa.MatchingRepository;
import spot.spot.domain.job.query.service.ClientQueryService;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.member.repository.MemberRepository;
import spot.spot.domain.pay.service.PayService;
import spot.spot.domain.pay.util.PayUtil;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;
import spot.spot.global.util.AwsS3ObjectStorage;

import static org.mockito.BDDMockito.*;

@SpringBootTest
@WithMockUser(username = "1")
@Transactional
@ActiveProfiles("local")
class PayRepositoryDslTest {

    @Autowired
    PayRepositoryDsl payRepositoryDsl;

    @Autowired
    ClientCommandService clientCommandService;

    @Autowired
    ClientQueryService clientQueryService;

    @Autowired
    PayService payService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MatchingRepository matchingRepository;

    @Autowired
    CertificationRepository certificationRepository;

    @MockitoBean
    AwsS3ObjectStorage awsS3ObjectStorage;

    @MockitoBean
    PayUtil payUtil;

    @DisplayName("매칭정보로 해당하는 일의 가격 정보를 조회할 수 있다.")
    @Test
    void researchAmount(){
        ///given
        int validAmount = 1000;
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-file.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "This is a test file".getBytes()
        );

        Member testMember = Member.builder()
                .email("test@test")
                .nickname("test")
                .build();
        Member member = memberRepository.save(testMember);

        RegisterJobRequest request = new RegisterJobRequest("title", "content", validAmount, 0, 12.1111, 12.1111);
        given(awsS3ObjectStorage.uploadFile(file))
                .willReturn("https://s3-bucket.com/test-file.txt");
        doNothing()
                .when(payUtil)
                .insertFromSchedule(any());

        RegisterJobResponse registerJobResponse = clientCommandService.registerJob(request, file);
        Job findJob = clientQueryService.findById(registerJobResponse.jobId());
        payService.payReady(String.valueOf(member.getId()), request.content(), request.money(), request.point(), findJob);
        Matching matching = matchingRepository.findByMemberAndJob_Id(member, findJob.getId()).orElseThrow(() -> new GlobalException(ErrorCode.MATCHING_NOT_FOUND));

        ///when
        Integer amount = payRepositoryDsl.findByPayAmountFromMatchingJob(matching.getId());

        ///then
        Assertions.assertThat(amount).isEqualTo(validAmount);
    }

}