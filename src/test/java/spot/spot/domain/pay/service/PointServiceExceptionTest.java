package spot.spot.domain.pay.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import spot.spot.domain.member.dto.request.MemberRequest;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.member.service.MemberService;
import spot.spot.domain.pay.entity.dto.request.PointServeRequestDto;
import spot.spot.domain.pay.entity.dto.response.PointServeResponseDto;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@ActiveProfiles("local")
public class PointServiceExceptionTest {

    @Autowired
    PointService pointService;

    @Autowired
    MemberService memberService;

    @BeforeEach
    void before() {
        MemberRequest.register build = MemberRequest.register.builder()
                .nickname("테스트유저1")
                .email("test@test.com")
                .img("img")
                .build();
        memberService.register(build);
        PointServeRequestDto serveCountPoint = new PointServeRequestDto("포인트1", 1000, 3);
        PointServeRequestDto serveCountPoint2 = new PointServeRequestDto("포인트2", 1000, 5);

        List<PointServeRequestDto> serveRequestDtos = new ArrayList<>();

        serveRequestDtos.add(serveCountPoint);
        serveRequestDtos.add(serveCountPoint2);

        pointService.servePoint(serveRequestDtos);
    }


    /**
     * ExceptionTest
     * servePoint -> pointName, point, count
     * registerPoint -> pointCode, member
     * deletePointOnce -> pointCode
     * deletePoint -> pointCode
     */
    @Test
    @DisplayName("포인트 생성 시 포인트 이름이 누락되면 예외가 발생한다.")
    void servePointEmptyPointNameException() {
        //given
        PointServeRequestDto serveCountPoint = new PointServeRequestDto("", 1000, 3);
        List<PointServeRequestDto> serveRequestDtos = new ArrayList<>();

        serveRequestDtos.add(serveCountPoint);

        //when,then
        Assertions.assertThatThrownBy(() -> pointService.servePoint(serveRequestDtos))
                .isInstanceOf(GlobalException.class)
                .extracting(e -> ((GlobalException) e).getErrorCode().getMessage())
                .isEqualTo(ErrorCode.EMPTY_POINT_NAME.getMessage());
    }

    @Test
    @DisplayName("포인트 생성 시 포인트 가격이 0 이하이거나 null값이면 예외가 발생한다.")
    void servePointInvalidPointException() {
        //given
        PointServeRequestDto serveCountPoint = new PointServeRequestDto("포인트네임", 0, 3);
        List<PointServeRequestDto> serveRequestDtos = new ArrayList<>();

        serveRequestDtos.add(serveCountPoint);

        //when,then
        Assertions.assertThatThrownBy(() -> pointService.servePoint(serveRequestDtos))
                .isInstanceOf(GlobalException.class)
                .extracting(e -> ((GlobalException) e).getErrorCode().getMessage())
                .isEqualTo(ErrorCode.INVALID_POINT_AMOUNT.getMessage());
    }

    @Test
    @DisplayName("포인트 생성 시 포인트 갯수가 0이하거나 null값이면 예외가 발생한다.")
    void servePointInvalidCountException() {
        //given
        PointServeRequestDto serveCountPoint = new PointServeRequestDto("포인트네임", 10000, 0);
        List<PointServeRequestDto> serveRequestDtos = new ArrayList<>();

        serveRequestDtos.add(serveCountPoint);

        //when,then
        Assertions.assertThatThrownBy(() -> pointService.servePoint(serveRequestDtos))
                .isInstanceOf(GlobalException.class)
                .extracting(e -> ((GlobalException) e).getErrorCode().getMessage())
                .isEqualTo(ErrorCode.INVALID_POINT_COUNT.getMessage());
    }

    @Test
    @DisplayName("포인트 등록시 유효하지 않은 포인트코드를 입력하면 예외가 발생한다.")
    void registerPointInvalidPointCodeException() {
        //유효하지 않은 포인트 코드
        String mockPointCode = "TE1235";
        Member mockMember = Member.builder().email("test@test.com").nickname("test").build();
        String memberId = String.valueOf(mockMember.getId());

        ///when, then
        Assertions.assertThatThrownBy(() -> pointService.registerPoint(mockPointCode, memberId))
                .isInstanceOf(GlobalException.class)
                .extracting(e -> ((GlobalException) e).getErrorCode().getMessage())
                .isEqualTo(ErrorCode.INVALID_POINT_CODE.getMessage());
    }

    @Test
    @DisplayName("포인트 등록 시 멤버가 누락되면 예외가 발생한다.")
    void registerPointEmptyMemberException() {
        ///given
        PointServeRequestDto point = new PointServeRequestDto("point", 1000, 3);
        List<PointServeResponseDto> responseDtos = pointService.servePoint(List.of(point));
        String pointCode = responseDtos.get(0).pointCode();

        ///when ,then
        Assertions.assertThatThrownBy(() -> pointService.registerPoint(pointCode, ""))
                .isInstanceOf(GlobalException.class)
                .extracting(e -> ((GlobalException) e).getErrorCode().getMessage())
                .isEqualTo(ErrorCode.EMPTY_MEMBER.getMessage());
    }

    @Test
    @DisplayName("포인트 등록 시 멤버 존재하지 않으면 예외가 발생한다.")
    void registerPointEmptyMemberException2() {
        ///given
        String notExistMemberId = "100";
        PointServeRequestDto point = new PointServeRequestDto("point", 1000, 3);
        List<PointServeResponseDto> responseDtos = pointService.servePoint(List.of(point));
        String pointCode = responseDtos.get(0).pointCode();

        ///when ,then
        Assertions.assertThatThrownBy(() -> pointService.registerPoint(pointCode, notExistMemberId))
                .isInstanceOf(GlobalException.class)
                .extracting(e -> ((GlobalException) e).getErrorCode().getMessage())
                .isEqualTo(ErrorCode.MEMBER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("포인트 삭제 시 유효하지 않은 코드를 입력하면 예외가 발생한다.")
    void deletePointOnceInvalidPointCode() {
        ///given
        String mockPointCode = "12TYYA2";

        ///when, then
        Assertions.assertThatThrownBy(() -> pointService.deletePointOnce(mockPointCode))
                .isInstanceOf(GlobalException.class)
                .extracting(e -> ((GlobalException) e).getErrorCode().getMessage())
                .isEqualTo(ErrorCode.INVALID_POINT_CODE.getMessage());
    }

    @Test
    @DisplayName("deltePoint 유효하지 않은 코드 예외")
    void deletePointInvalidPointCode() {
        Assertions.assertThatThrownBy(() -> pointService.deletePoint(""))
                .isInstanceOf(GlobalException.class)
                .extracting(e -> ((GlobalException) e).getErrorCode().getMessage())
                .isEqualTo(ErrorCode.INVALID_POINT_CODE.getMessage());
    }
}
