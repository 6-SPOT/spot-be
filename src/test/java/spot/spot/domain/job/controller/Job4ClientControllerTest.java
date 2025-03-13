package spot.spot.domain.job.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import spot.spot.domain.chat.service.ChatService;
import spot.spot.domain.job.command.controller.ClientCommandController;
import spot.spot.domain.job.command.dto.request.RegisterJobRequest;
import spot.spot.domain.job.command.dto.response.RegisterJobResponse;
import spot.spot.domain.job.command.service.ClientCommandService;
import spot.spot.domain.job.command.service.WorkerCommandService;
import spot.spot.domain.job.query.service.ClientQueryService;
import spot.spot.domain.member.service.MemberService;
import spot.spot.domain.notification.command.service.FcmService;
import spot.spot.domain.pay.service.PayService;
import spot.spot.domain.pay.service.PointService;
import spot.spot.global.redis.service.TokenService;
import spot.spot.domain.review.service.ReviewService;
import spot.spot.global.security.util.UserAccessUtil;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ClientCommandController.class)
@ActiveProfiles("local")
@WithMockUser(username = "testUser")
@ImportAutoConfiguration(exclude = OAuth2ClientAutoConfiguration.class)
class Job4ClientControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ClientCommandService clientCommandService;

    @MockitoBean
    ClientQueryService clientQueryService;

    @MockitoBean
    ChatService chatService;

    @MockitoBean
    SimpMessageSendingOperations messagingTemplate;

    @MockitoBean
    WorkerCommandService workerCommandService;

    @MockitoBean
    MemberService memberService;

    @MockitoBean
    TokenService tokenService;

    @MockitoBean
    FcmService fcmService;

    @MockitoBean
    PayService payService;

    @MockitoBean
    PointService pointService;

    @MockitoBean
    ReviewService reviewService;

    @MockitoBean
    UserAccessUtil userAccessUtil;

    @DisplayName("일을 등록하면 등록한 일의 JobId가 반환된다.")
    @Test
    void registerJob() throws Exception {
        ///given
        RegisterJobRequest req = new RegisterJobRequest("title", "content", 1000, 100, 142.111111, 142.111111);
        RegisterJobResponse res = RegisterJobResponse.create(1L);
        MockMultipartFile jsonPart = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(req)
        );

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-file.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "This is a test file".getBytes()
        );
        given(clientCommandService.registerJob(any(), any())).willReturn(res);

        ///when ///then
        ///multipart 요청 시 요청form이 다르다.
        mockMvc.perform(
                        multipart("/api/job/register")
                                .file(jsonPart)
                                .file(file)
                                .with(csrf())
                                .with(putMethod())
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("정상적으로 처리하였습니다."));
    }

    private static RequestPostProcessor putMethod() {
        return request -> {
            request.setMethod("PUT");
            return request;
        };
    }
}