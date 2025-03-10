package spot.spot.domain.pay.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import spot.spot.domain.job.entity.Job;
import spot.spot.domain.job.service.ClientService;
import spot.spot.domain.pay.entity.PayHistory;
import spot.spot.domain.pay.entity.PayStatus;
import spot.spot.domain.pay.entity.dto.request.PayApproveRequestDto;
import spot.spot.domain.pay.entity.dto.request.PayReadyRequestDto;
import spot.spot.domain.pay.entity.dto.response.PayApproveResponseDto;
import spot.spot.domain.pay.entity.dto.response.PayReadyResponseDto;
import spot.spot.domain.pay.service.PayService;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PayController.class)
@ActiveProfiles("local")
@WithMockUser(username = "testUser")
@ImportAutoConfiguration(exclude = OAuth2ClientAutoConfiguration.class)
class PayControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PayService payService;

    @MockitoBean
    private ClientService clientService;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @DisplayName("결제 승인시 결제가 완료된다.")
    @Test
    void deposit() throws Exception {
        ///given
        PayApproveRequestDto req = PayApproveRequestDto.create("T123451", "content", 10000, "T111111");
        PayApproveResponseDto res = PayApproveResponseDto.create("test", "domain", "content", 10000);
        when(payService.payApprove(any(String.class), any(Job.class), any(String.class),any(Integer.class))).thenReturn(res);

        ///when, then
        mockMvc.perform(
                        post("/api/pay/deposit")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)  // JSON 요청 타입 설정
                                .content(objectMapper.writeValueAsString(req))  // 요청 데이터
                )
                .andDo(print())  // 요청 및 응답 출력 (디버깅용)
                .andExpect(status().isOk())  // HTTP 200 응답 기대
                .andExpect(jsonPath("$.message").value("정상적으로 처리하였습니다."))
        ;
    }

    @DisplayName("멤버Id, 일 타이틀, 가격정보, 포인트 정보가 요청이 오면 결제를 준비한다.")
    @Test
    void payReady() throws Exception {
        ///given
        PayReadyRequestDto req = PayReadyRequestDto.create("title", 10000, 1000, 1L);
        PayHistory payHistory = PayHistory.builder().payAmount(1000).payPoint(1000).worker("worker").payStatus(PayStatus.PENDING).build();
        PayReadyResponseDto res = PayReadyResponseDto.create("redirect_pc_url", "redirect_mobile_url", "T123131", payHistory);

        when(payService.payReady(anyString(), anyString(),anyInt(),anyInt(), any())).thenReturn(res);
        when(clientService.updateTidToJob(any(), any())).thenReturn(new Job());
        ///when ///then
        mockMvc.perform(
                        post("/api/pay/ready")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("정상적으로 처리하였습니다."));
    }

    @DisplayName("포인트 가격이 0원일때는 결제준비가 통과된다.")
    @Test
    void payReadyPointZero() throws Exception {
        ///given
        PayReadyRequestDto req = PayReadyRequestDto.create("title", 10000, 0, 1L);
        PayHistory payHistory = PayHistory.builder().payAmount(1000).payPoint(1000).worker("worker").payStatus(PayStatus.PENDING).build();
        PayReadyResponseDto res = PayReadyResponseDto.create("redirect_pc_url", "redirect_mobile_url", "T123131", payHistory);
        when(payService.payReady(anyString(), anyString(), anyInt(), anyInt(), any())).thenReturn(res);
        when(clientService.updateTidToJob(any(), any())).thenReturn(new Job());

        ///when ///then
        mockMvc.perform(
                        post("/api/pay/ready")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req))

                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("정상적으로 처리하였습니다."));
    }

}