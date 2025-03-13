package spot.spot.global.mongodb;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.client.MongoClients;

@Configuration
public class MongoDBConfig {

	@Value("${mongodb.client}")
	private String mongoClientUri;

	@Value("${mongodb.name}")
	private String dbName;

	@Bean
	public MongoTemplate mongoTemplate() {
		return new MongoTemplate(MongoClients.create(mongoClientUri), dbName);
	}
}
