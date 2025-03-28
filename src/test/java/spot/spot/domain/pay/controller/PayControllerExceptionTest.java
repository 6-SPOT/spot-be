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
import spot.spot.domain.job.command.entity.Job;
import spot.spot.domain.job.command.service.ClientCommandService;
import spot.spot.domain.job.query.service.ClientQueryService;
import spot.spot.domain.pay.entity.PayHistory;
import spot.spot.domain.pay.entity.PayStatus;
import spot.spot.domain.pay.entity.dto.request.PayApproveRequestDto;
import spot.spot.domain.pay.entity.dto.request.PayReadyRequestDto;
import spot.spot.domain.pay.entity.dto.response.PayApproveResponseDto;
import spot.spot.domain.pay.entity.dto.response.PayReadyResponseDto;
import spot.spot.domain.pay.service.PayMockService;
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
public class PayControllerExceptionTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    PayService payService;

    @MockitoBean
    PayMockService payMockService;

    @MockitoBean
    ClientCommandService clientCommandService;

    @MockitoBean
    ClientQueryService clientQueryService;

    @MockitoBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @DisplayName("pgToken값이 누락되면 valid에서 검증하고 예외를 반환한다.")
    @Test
    void depositWithoutPgToken() throws Exception {
        ///given
        PayApproveRequestDto req = PayApproveRequestDto.create("", "content", 10000, "T111111");
        PayApproveResponseDto res = PayApproveResponseDto.create("test", "domain", "content", 10000);
        when(payService.payApprove(any(String.class), any(Job.class), any(String.class),any(Integer.class))).thenReturn(res);

        ///when then
        mockMvc.perform(
                        post("/api/pay/deposit")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("pgToken 값이 누락되었습니다."));
        ;
    }

    @DisplayName("일 타이틀 값이 누락되면 valid에서 검증하고 예외를 반환한다.")
    @Test
    void depositWithoutContent() throws Exception {
        ///given
        PayApproveRequestDto req = PayApproveRequestDto.create("T1t1t", "", 10000, "T111111");
        PayApproveResponseDto res = PayApproveResponseDto.create("test", "domain", "content", 10000);
        when(payService.payApprove(any(String.class), any(Job.class), any(String.class),any(Integer.class))).thenReturn(res);

        ///when then
        mockMvc.perform(
                        post("/api/pay/deposit")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("일의 타이틀은 빈 값일 수 없습니다."));
        ;
    }

    @DisplayName("가격 값이 0보다 작거나 누락되면 valid에서 검증하고 예외를 반환한다.")
    @Test
    void depositWithoutAmount() throws Exception {
        ///given
        PayApproveRequestDto req = PayApproveRequestDto.create("T1t1t", "Content", 0, "T111111");
        PayApproveResponseDto res = PayApproveResponseDto.create("test", "domain", "content", 10000);
        when(payService.payApprove(any(String.class), any(Job.class), any(String.class),any(Integer.class))).thenReturn(res);

        ///when then
        mockMvc.perform(
                        post("/api/pay/deposit")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("가격은 양수 값이여야 합니다."));
        ;
    }

    @DisplayName("tid 값이 누락되면 valid에서 검증하고 예외를 반환한다.")
    @Test
    void depositWithoutTid() throws Exception {
        ///given
        PayApproveRequestDto req = PayApproveRequestDto.create("T1t1t", "Content", 10000, "");
        PayApproveResponseDto res = PayApproveResponseDto.create("test", "domain", "content", 10000);
        when(payService.payApprove(any(String.class), any(Job.class), any(String.class),any(Integer.class))).thenReturn(res);

        ///when then
        mockMvc.perform(
                        post("/api/pay/deposit")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("tid값은 빈 값일 수 없습니다."));
        ;
    }

    @DisplayName("일 타이틀 없이 결제 준비를 하면 예외가 발생한다.")
    @Test
    void payReadyWithOutContent() throws Exception {
        ///given
        PayReadyRequestDto req = PayReadyRequestDto.create("", 10000, 1000, 1L);
        PayHistory payHistory = PayHistory.builder().payAmount(1000).payPoint(1000).worker("worker").payStatus(PayStatus.PENDING).build();
        PayReadyResponseDto res = PayReadyResponseDto.create("redirect_pc_url", "redirect_mobile_url", "T123131", payHistory);
        when(payService.payReady(anyString(), anyString(), anyInt(), anyInt(), any())).thenReturn(res);
        when(clientCommandService.updateTidToJob(any(), any())).thenReturn(new Job());

        ///when ///then
        mockMvc.perform(
                        post("/api/pay/ready")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("일 타이틀이 비어있습니다. 확인해주세요."));
    }

    @DisplayName("가격 정보 없이 결제 준비를 요청하면 예외가 발생한다.")
    @Test
    void payReadyWithoutAmount() throws Exception {
        ///given
        PayReadyRequestDto req = PayReadyRequestDto.create("title", 0, 1000, 1L);
        PayHistory payHistory = PayHistory.builder().payAmount(1000).payPoint(1000).worker("worker").payStatus(PayStatus.PENDING).build();
        PayReadyResponseDto res = PayReadyResponseDto.create("redirect_pc_url", "redirect_mobile_url", "T123131", payHistory);
        when(payService.payReady(anyString(), anyString(), anyInt(), anyInt(), any())).thenReturn(res);
        when(clientCommandService.updateTidToJob(any(), any())).thenReturn(new Job());

        ///when ///then
        mockMvc.perform(
                post("/api/pay/ready")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))

        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("가격은 0보다 작거나 비어있을 수 없습니다."));
    }

    @DisplayName("포인트 가격이 음수거나 비어있을 때 결제 준비를 요청하면 예외가 발생한다.")
    @Test
    void payReadyWithOutPoint() throws Exception {
        ///given
        PayReadyRequestDto req = PayReadyRequestDto.create("title", 10000, -1, 1L);
        PayHistory payHistory = PayHistory.builder().payAmount(1000).payPoint(1000).worker("worker").payStatus(PayStatus.PENDING).build();
        PayReadyResponseDto res = PayReadyResponseDto.create("redirect_pc_url", "redirect_mobile_url", "T123131", payHistory);
        when(payService.payReady(anyString(), anyString(), anyInt(), anyInt(), any())).thenReturn(res);
        when(clientCommandService.updateTidToJob(any(), any())).thenReturn(new Job());

        ///when ///then
        mockMvc.perform(
                        post("/api/pay/ready")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req))

                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("포인트는 음수일 수 없습니다."));
    }

    @DisplayName("잡 아이디가 없을 때 결제 준비를 요청하면 예외가 발생한다.")
    @Test
    void payReadyWithOutJobId() throws Exception {
        ///given
        PayReadyRequestDto req = PayReadyRequestDto.create("title", 10000, 0, null);
        PayHistory payHistory = PayHistory.builder().payAmount(1000).payPoint(1000).worker("worker").payStatus(PayStatus.PENDING).build();
        PayReadyResponseDto res = PayReadyResponseDto.create("redirect_pc_url", "redirect_mobile_url", "T123131", payHistory);
        when(payService.payReady(anyString(), anyString(), anyInt(), anyInt(), any())).thenReturn(res);
        when(clientCommandService.updateTidToJob(any(), any())).thenReturn(new Job());

        ///when ///then
        mockMvc.perform(
                        post("/api/pay/ready")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req))

                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("잡 아이디 값은 필수입니다."));
    }

}
