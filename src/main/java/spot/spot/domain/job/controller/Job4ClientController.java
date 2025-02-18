package spot.spot.domain.job.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import spot.spot.domain.job.dto.request.RegisterJobRequest;
import spot.spot.domain.job.service.Job4ClientService;
import spot.spot.global.logging.Logging;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/job")
public class Job4ClientController {

    private final Job4ClientService job4ClientService;

    @Logging
    @PutMapping(value = "/register",  consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void registerJob(
        @RequestPart(value = "request")RegisterJobRequest request,
        @RequestPart(value = "file", required = false )MultipartFile file
    ) { job4ClientService.registerJob(request, file);}
}
