package spot.spot.domain.job.service;

import org.springframework.stereotype.Component;

@Component
public class JobUtil {

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
