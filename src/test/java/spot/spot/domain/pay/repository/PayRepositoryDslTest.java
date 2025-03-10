package spot.spot.domain.pay.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import spot.spot.domain.job.dto.request.RegisterJobRequest;
import spot.spot.domain.job.dto.response.RegisterJobResponse;
import spot.spot.domain.job.entity.Job;
import spot.spot.domain.job.entity.Matching;
import spot.spot.domain.job.repository.jpa.MatchingRepository;
import spot.spot.domain.job.service.ClientService;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.member.repository.MemberRepository;
import spot.spot.domain.pay.service.PayService;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;
import spot.spot.global.util.AwsS3ObjectStorage;

@SpringBootTest
@WithMockUser(username = "1")
class PayRepositoryDslTest {

    @Autowired
    PayRepositoryDsl payRepositoryDsl;

    @Autowired
    ClientService clientService;

    @Autowired
    PayService payService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MatchingRepository matchingRepository;

    @MockitoBean
    AwsS3ObjectStorage awsS3ObjectStorage;

    @BeforeEach
    void before() {
        matchingRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

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
        BDDMockito.given(awsS3ObjectStorage.uploadFile(file))
                .willReturn("https://s3-bucket.com/test-file.txt");

        RegisterJobResponse registerJobResponse = clientService.registerJob(request, file);
        Job findJob = clientService.findById(registerJobResponse.jobId());
        payService.payReady(member.getNickname(), request.content(), request.money(), request.point(), findJob);
        Matching matching = matchingRepository.findByMemberAndJob_Id(member, findJob.getId()).orElseThrow(() -> new GlobalException(ErrorCode.MATCHING_NOT_FOUND));

        ///when
        Integer amount = payRepositoryDsl.findByPayAmountFromMatchingJob(matching.getId());

        ///then
        Assertions.assertThat(amount).isEqualTo(validAmount);
    }

}