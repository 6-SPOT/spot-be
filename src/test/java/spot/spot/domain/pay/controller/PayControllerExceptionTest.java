package spot.spot.domain.pay.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import spot.spot.domain.chat.service.ChatService;
import spot.spot.domain.job.entity.Job;
import spot.spot.domain.job.service.Job4ClientService;
import spot.spot.domain.pay.entity.dto.request.PayApproveRequestDto;
import spot.spot.domain.pay.entity.dto.response.PayApproveResponseDto;
import spot.spot.domain.pay.service.PayService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PayController.class)
@ActiveProfiles("local")
@WithMockUser(username = "testUser")
public class PayControllerExceptionTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    PayService payService;

    @MockitoBean
    Job4ClientService job4ClientService;

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
}
