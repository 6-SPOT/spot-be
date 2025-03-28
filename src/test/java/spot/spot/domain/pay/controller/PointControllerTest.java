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
import spot.spot.domain.member.service.MemberService;
import spot.spot.domain.pay.entity.dto.request.PointServeRequestDto;
import spot.spot.domain.pay.entity.dto.response.PointServeResponseDto;
import spot.spot.domain.pay.service.PointService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PointController.class)
@ActiveProfiles("local")
@WithMockUser(username = "testUser")
@ImportAutoConfiguration(exclude = OAuth2ClientAutoConfiguration.class)
class PointControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    PointService pointService;

    @MockitoBean
    MemberService memberService;

    @MockitoBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @DisplayName("포인트 이름, 갯수, 금액을 받아 포인트를 생성한다.")
    @Test
    void servePointCoupon() throws Exception {
        ///given
        PointServeResponseDto pointServeResponseDto = PointServeResponseDto.create("point1", 1000, "TESTSTS");
        List<PointServeResponseDto> pointServeResponseDtos = new ArrayList<>();
        pointServeResponseDtos.add(pointServeResponseDto);
        PointServeRequestDto pointReq = PointServeRequestDto.create("point1", 1000, 3);
        List<PointServeRequestDto> pointReqs = new ArrayList<>();
        pointReqs.add(pointReq);

        when(pointService.servePoint(anyList())).thenReturn(pointServeResponseDtos);

        ///when ///then
        mockMvc.perform(
                post("/api/point/serve")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pointReqs))
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("정상적으로 처리하였습니다."))
        ;
    }

    @DisplayName("포인트 코드를 입력하면 존재하는 포인트가 사용된다.")
    @Test
    void register() throws Exception {
        ///given
        String pointCode = "TESTSTET";
        doNothing()
                .when(pointService)
                .registerPoint(anyString(), anyString());

        ///when ///then
        mockMvc.perform(
                get("/api/point/register")
                        .with(csrf())
                        .param("pointCode", pointCode)
        )
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.message").value("정상적으로 처리하였습니다."))
                ;
    }

    @DisplayName("포인트 코드를 입력하면 일치하는 포인트가 삭제됩니다.")
    @Test
    void deletePointCoupon() throws Exception {
        ///given
        String pointCode = "T123131415";
        doNothing()
                .when(pointService)
                .deletePoint(anyString());

        ///when ///then
        mockMvc.perform(
                post("/api/point/delete")
                        .with(csrf())
                        .param("pointCode", pointCode)
        )
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.message").value("정상적으로 처리하였습니다."));
    }

}