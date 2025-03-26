package spot.spot.global.mongodb;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.client.MongoClients;

@Configuration
@Profile("kafka")
public class MongoDBConfig {

	@Value("${spring.data.mongodb.uri}")
	private String mongoClientUri;

	@Value("${spring.data.mongodb.database}")
	private String dbName;

	@Bean
	public MongoTemplate mongoTemplate() {
		return new MongoTemplate(MongoClients.create(mongoClientUri), dbName);
	}
}
