// package spot.spot.global.config;
//
// import java.util.Arrays;
//
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.core.env.ConfigurableEnvironment;
// import org.springframework.core.env.EnumerablePropertySource;
// import org.springframework.stereotype.Component;
//
// import jakarta.annotation.PostConstruct;
//
// @Component
// public class PropertyPrinter {
// 	@Autowired
// 	private ConfigurableEnvironment env;
//
// 	@PostConstruct
// 	public void printProperties() {
// 		// 방법 1: MutablePropertySources 사용 test
// 		env.getPropertySources().forEach(propertySource -> {
// 			System.out.println("PropertySource: " + propertySource.getName());
// 			if (propertySource instanceof EnumerablePropertySource) {
// 				EnumerablePropertySource eps = (EnumerablePropertySource) propertySource;
// 				Arrays.stream(eps.getPropertyNames()).forEach(prop -> {
// 					System.out.println(prop + "=" + eps.getProperty(prop));
// 				});
// 			}
// 		});
//
//
// 	}
// }
