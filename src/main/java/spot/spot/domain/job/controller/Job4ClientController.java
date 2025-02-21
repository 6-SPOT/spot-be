package spot.spot.domain.job.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import spot.spot.domain.job._docs.Job4ClientDocs;
import spot.spot.domain.job.dto.request.RegisterJobRequest;
import spot.spot.domain.job.dto.response.NearByWorkersResponse;
import spot.spot.domain.job.service.Job4ClientService;
import spot.spot.global.logging.Logging;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/job")
public class Job4ClientController implements Job4ClientDocs {

    private final Job4ClientService job4ClientService;

    @PutMapping(value = "/register",  consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void registerJob(
        @RequestPart(value = "request")RegisterJobRequest request,
        @RequestPart(value = "file", required = false )MultipartFile file
    ) { job4ClientService.registerJob(request, file);}

    @GetMapping("/near-by")
    public List<NearByWorkersResponse> nearByWorkersResponseList (
        @RequestParam(required = true) double lat,
        @RequestParam(required = true) double lng,
        @RequestParam(required = true, defaultValue = "21") int zoom
    ) {
        return job4ClientService.findNearByWorkers(lat, lng, zoom);
    }

}
