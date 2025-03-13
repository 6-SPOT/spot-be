package spot.spot.domain.job.query.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import spot.spot.domain.job.query.service.PositionQueryService;
import spot.spot.domain.job.query.controller._docs.ClientQueryDocs;
import spot.spot.domain.job.query.dto.response.AttenderResponse;
import spot.spot.domain.job.command.dto.response.JobSituationResponse;
import spot.spot.domain.job.query.dto.response.NearByWorkersResponse;
import spot.spot.domain.job.query.service.ClientQueryService;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/job")
public class ClientQueryController implements ClientQueryDocs {

    private final ClientQueryService clientQueryService;
    private final PositionQueryService positionQueryService;

    @GetMapping("/near-by")
    public List<NearByWorkersResponse> nearByWorkersResponseList (
        @RequestParam(required = true) double lat,
        @RequestParam(required = true) double lng,
        @RequestParam(required = false, defaultValue = "21") Integer zoom
    ) {
        return clientQueryService.findNearByWorkers(lat, lng, zoom);
    }

    @GetMapping("/search-list")
    public Slice<AttenderResponse> getAttenderList(
        @RequestParam long id,
        Pageable pageable) {
        return clientQueryService.findJobAttenderList(id, pageable);
    }

    @GetMapping("/dash-board")
    public List<JobSituationResponse> getSituationByOwner() {
        return clientQueryService.getSituationsByOwner();
    }
}
