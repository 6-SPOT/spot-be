package spot.spot.domain.job.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import spot.spot.domain.job._docs.Job4ClientDocs;
import spot.spot.domain.job.dto.request.RegisterJobRequest;
import spot.spot.domain.job.dto.request.Worker2JobRequest;
import spot.spot.domain.job.dto.response.AttenderResponse;
import spot.spot.domain.job.dto.response.NearByWorkersResponse;
import spot.spot.domain.job.service.Job4ClientService;
import spot.spot.domain.pay.entity.dto.PayReadyResponseDto;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/job")
public class Job4ClientController implements Job4ClientDocs {

    private final Job4ClientService job4ClientService;

    @PutMapping(value = "/register",  consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PayReadyResponseDto registerJob(
        @RequestPart(value = "request")RegisterJobRequest request,
        @RequestPart(value = "file", required = false )MultipartFile file
    ) { return job4ClientService.registerJob(request);}

    @GetMapping("/near-by")
    public List<NearByWorkersResponse> nearByWorkersResponseList (
        @RequestParam(required = true) double lat,
        @RequestParam(required = true) double lng,
        @RequestParam(required = false, defaultValue = "21") Integer zoom
    ) {
        return job4ClientService.findNearByWorkers(lat, lng, zoom);
    }

    @GetMapping("/search-list")
    public Slice<AttenderResponse> getAttenderList(
        @RequestParam long id,
        Pageable pageable) {
        return job4ClientService.findJobAttenderList(id, pageable);
    }

    @Override
    public void askJob2Worker(Worker2JobRequest request) {

    }

}
