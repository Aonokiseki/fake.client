package fake.client.configurer;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WordCloudConfigurer {
	
	@Bean(name = "word.cloud.config")
	public Map<String, String> wordCloudConfig(
			@Value("${python.word-cloud.width}") String width,
			@Value("${python.word-cloud.height}") String height,
			@Value("${python.word-cloud.background-color}") String backgroundColor,
			@Value("${python.word-cloud.directory}") String directory,
			@Value("${python.word-cloud.stop-words-path}") String stopwordsPath,
			@Value("${python.word-cloud.font-path}") String fontPath){
		Map<String, String> config = new HashMap<String, String>();
		config.put("width", width);
		config.put("height", height);
		config.put("background-color", backgroundColor);
		config.put("directory", directory);
		config.put("stopwords-path", stopwordsPath);
		config.put("font-path", fontPath);
		return config;
	}
}
