package spot.spot.domain.job.query.controller._docs;

import static spot.spot.global.util.ConstantUtil.AUTHORIZATION;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import spot.spot.domain.job.query.dto.request.AttenderResponse;
import spot.spot.domain.job.query.dto.request.PositionRequest;
import spot.spot.domain.job.command.dto.response.JobSituationResponse;
import spot.spot.domain.job.query.dto.response.NearByWorkersResponse;

@Tag(
    name = "2. CLIENT QUERY API",
    description = "<br/> 일 의뢰자를 위한 조회 API")
public interface ClientQueryDocs {

    @Operation(summary = "근처 해결사 찾기",
        description = "위도, 경도, 줌 레벨을 주면, 사용자 위치 중심으로 반경을 그려 근처 사용자들을 반환해 줍니다.")
    @GetMapping
    public List<NearByWorkersResponse> nearByWorkersResponseList (
        @RequestParam(required = true) double lat,
        @RequestParam(required = true) double lng,
        @RequestParam(required = true, defaultValue = "21") Integer zoom
    );

    @Operation(summary = "일 신청자 리스트 얻기",
        description = "특정 job_id의 일을 신청한 신청자의 리스트를 얻습니다.")
    @GetMapping
    public Slice<AttenderResponse> getAttenderList (
        @RequestParam long id,
        Pageable pageable
    );

    @Operation(summary = "내가 맡긴 일의 현황 보기",
        description = "접속 유저가 생성한 일의 진행 현황을 한 눈에 봅니다.")
    @GetMapping
    public List<JobSituationResponse> getSituationByOwner();

}
