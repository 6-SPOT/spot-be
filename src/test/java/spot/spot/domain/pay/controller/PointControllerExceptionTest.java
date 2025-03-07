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
public class PointControllerExceptionTest {

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

    @DisplayName("포인트 이름이 누락되면 예외를 발생시킨다.")
    @Test
    void servePointCouponWithoutPointName() throws Exception {
        ///given
        PointServeResponseDto pointServeResponseDto = PointServeResponseDto.create("point1", 1000, "TESTSTS");
        List<PointServeResponseDto> pointServeResponseDtos = new ArrayList<>();
        pointServeResponseDtos.add(pointServeResponseDto);
        PointServeRequestDto pointReq = PointServeRequestDto.create("", 1000, 3);
        List<PointServeRequestDto> pointReqs = new ArrayList<>();
        pointReqs.add(pointReq);

        given(pointService.servePoint(anyList())).willReturn(pointServeResponseDtos);

        ///when ///then
        mockMvc.perform(
                        post("/api/point/serve")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(pointReqs))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("포인트 이름이 빈값입니다. 확인해주세요."))
        ;
    }

    @DisplayName("포인트 가격이 0보다 작거나 누락되면 예외를 발생시킨다.")
    @Test
    void servePointCouponWithoutPoint() throws Exception {
        ///given
        PointServeResponseDto pointServeResponseDto = PointServeResponseDto.create("point1", 1000, "TESTSTS");
        List<PointServeResponseDto> pointServeResponseDtos = new ArrayList<>();
        pointServeResponseDtos.add(pointServeResponseDto);
        PointServeRequestDto pointReq = PointServeRequestDto.create("point1", 0, 3);
        List<PointServeRequestDto> pointReqs = new ArrayList<>();
        pointReqs.add(pointReq);

        given(pointService.servePoint(anyList())).willReturn(pointServeResponseDtos);

        ///when ///then
        mockMvc.perform(
                        post("/api/point/serve")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(pointReqs))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("포인트는 양수 값이여야 합니다."))
        ;
    }

    @DisplayName("포인트 갯수가 0보다 작거나 누락되면 예외를 발생시킨다.")
    @Test
    void servePointCouponWithoutCount() throws Exception {
        ///given
        PointServeResponseDto pointServeResponseDto = PointServeResponseDto.create("point1", 1000, "TESTSTS");
        List<PointServeResponseDto> pointServeResponseDtos = new ArrayList<>();
        pointServeResponseDtos.add(pointServeResponseDto);
        PointServeRequestDto pointReq = PointServeRequestDto.create("point1", 1000, 0);
        List<PointServeRequestDto> pointReqs = new ArrayList<>();
        pointReqs.add(pointReq);

        given(pointService.servePoint(anyList())).willReturn(pointServeResponseDtos);

        ///when ///then
        mockMvc.perform(
                        post("/api/point/serve")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(pointReqs))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("갯수는 양수 값이어야 합니다."))
        ;
    }

    @DisplayName("포인트 사용 시 포인트 값이 누락되면 예외가 발생한다.")
    @Test
    void registerWithoutPointCode() throws Exception {
        ///given
        String pointCode = "";
        doNothing()
                .when(pointService)
                .registerPoint(anyString(), anyString());

        ///when ///then
        mockMvc.perform(
                get("/api/point/register")
                        .param("pointCode", pointCode)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("포인트 등록 시 포인트 코드는 필수 입력값입니다."));
    }

    @DisplayName("포인트 삭제 시 포인트 코드 값이 누락되면 예외가 발생한다.")
    @Test
    void deleteWithoutPointCode() throws Exception {
        ///given
        String pointCode = "";
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
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("포인트 삭제 시 포인트 코드는 필수 입력값입니다."));
    }

}
