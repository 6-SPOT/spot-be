package spot.spot.domain.job.query.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import spot.spot.domain.job.query.util._docs.DistanceCalculateUtilDocs;

@Slf4j
@Component
@RequiredArgsConstructor
public class DistanceCalculateUtil implements
    DistanceCalculateUtilDocs {

    // 줌 레벨을 실제 KM로 변환하는 함수
    public double convertZoomToRadius(int zoom_level) {
        return switch (zoom_level) {
            case 21 -> 0.05;
            case 20 -> 0.1;
            case 19 -> 0.2;
            case 18 -> 0.5;
            case 17 -> 1;
            case 16 -> 2;
            case 15 -> 5;
            case 14 -> 10;
            case 13 -> 20;
            case 12 -> 50;
            default -> 100;
        };
    }
}