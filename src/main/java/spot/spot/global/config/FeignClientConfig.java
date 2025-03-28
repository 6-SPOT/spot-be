package spot.spot.global.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients (basePackages = "spot.spot")
public class FeignClientConfig {

}
