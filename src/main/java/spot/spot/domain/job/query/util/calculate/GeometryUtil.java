package spot.spot.domain.job.query.util.calculate;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Component;
import spot.spot.domain.job.query.util._docs.GeometryUtilDocs;

@Component
public class GeometryUtil implements GeometryUtilDocs {

    private final GeometryFactory geometryFactory = new GeometryFactory();

    @Override
    public Point createPoint(double lat, double lng) {
        return geometryFactory.createPoint(new Coordinate(lng, lat));
    }
}
