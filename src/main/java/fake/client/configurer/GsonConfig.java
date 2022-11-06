package fake.client.configurer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Configuration
public class GsonConfig {
	
	@Bean
	public Gson gson() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		return gsonBuilder.setPrettyPrinting()
				          .setDateFormat("yyyy-MM-dd")
				          .disableHtmlEscaping()
				          .create();
	}
}
