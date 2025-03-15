package spot.spot.global.config;

import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeometryFactoryConfig {

    @Bean
    public GeometryFactory geometryFactory() {
        return new GeometryFactory();
    }
}
