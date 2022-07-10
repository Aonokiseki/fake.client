package fake.client.configurer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
@RefreshScope
public class MongoDBConfig {
	
	@Bean(name = "mongo")
	@RefreshScope
	public MongoClient mongoDBClient(
			@Value("${mongodb.enabled}") Boolean enabled,
			@Value("${mongodb.url}") String url) {
		if(enabled != null && !enabled.booleanValue())
			return null;
		return MongoClients.create(new ConnectionString(url));
	}
}
