package spot.spot.domain.job.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import spot.spot.domain.job._docs.ClientDocs;
import spot.spot.domain.job.dto.request.ChangeStatusClientRequest;
import spot.spot.domain.job.dto.request.RegisterJobRequest;
import spot.spot.domain.job.dto.request.YesOrNoWorkersRequest;
import spot.spot.domain.job.dto.request.AttenderResponse;
import spot.spot.domain.job.dto.response.JobSituationResponse;
import spot.spot.domain.job.dto.response.NearByWorkersResponse;
import spot.spot.domain.job.dto.response.RegisterJobResponse;
import spot.spot.domain.job.service.ClientService;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/job")
public class ClientController implements ClientDocs {

    private final ClientService clientService;

    @PutMapping(value = "/register",  consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RegisterJobResponse registerJob(
        @RequestPart(value = "request")RegisterJobRequest request,
        @RequestPart(value = "file", required = false )MultipartFile file
    ) {
       return clientService.registerJob(request, file);
    }

    @GetMapping("/near-by")
    public List<NearByWorkersResponse> nearByWorkersResponseList (
        @RequestParam(required = true) double lat,
        @RequestParam(required = true) double lng,
        @RequestParam(required = false, defaultValue = "21") Integer zoom
    ) {
        return clientService.findNearByWorkers(lat, lng, zoom);
    }

    @GetMapping("/search-list")
    public Slice<AttenderResponse> getAttenderList(
        @RequestParam long id,
        Pageable pageable) {
        return clientService.findJobAttenderList(id, pageable);
    }



    @PostMapping("/choice")
    public void askJob2Worker(@RequestBody ChangeStatusClientRequest request) {
        clientService.askingJob2Worker(request);
    }

    @PostMapping("/yes-or-no")
    public void acceptJobRequestOfWorker(@RequestBody YesOrNoWorkersRequest request) {
        clientService.yesOrNo2RequestOfWorker(request);
    }

    @PostMapping("/withdrawal")
    public void requestWithdrawal(@RequestBody ChangeStatusClientRequest request) {
        clientService.requestWithdrawal(request);
    }

    @GetMapping("/dash-board")
    public List<JobSituationResponse> getSituationByOwner() {
        return clientService.getSituationsByOwner();
    }

    @PatchMapping("/confirm-or-reject")
    public void confirmOrRejectJob(@RequestBody YesOrNoWorkersRequest request) {
        clientService.confirmOrRejectJob(request);
    }

}
