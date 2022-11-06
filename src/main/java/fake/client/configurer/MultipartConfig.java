package fake.client.configurer;

import javax.servlet.MultipartConfigElement;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

@Configuration
public class MultipartConfig {
	
	@Bean
	public MultipartConfigElement multipartConfigElement(
			@Value("${spring.servlet.multipart.max-file-size}") long maxFileSize,
			@Value("${spring.servlet.multipart.max-request-size}") long maxRequestSize
 			) {
		MultipartConfigFactory factory = new MultipartConfigFactory();
		factory.setMaxFileSize(DataSize.ofBytes(maxFileSize));
		factory.setMaxRequestSize(DataSize.ofBytes(maxRequestSize));
		return factory.createMultipartConfig();
	}
}
