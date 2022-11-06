package fake.client.configurer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fake.client.util.FileUtils;

@Configuration
@RefreshScope
public class FakeCkmConfig {
	
	@Bean("stopwords")
	@RefreshScope
	public Set<String> stopwords(@Value("${nlp.stopwords.path}") String stopwordsPath) throws IOException{
		Set<String> stopwords = new HashSet<String>();
		List<String> list = FileUtils.readAsList(new FileInputStream(stopwordsPath), "utf-8");
		stopwords.addAll(list);
		return stopwords;
	}
	
	@Bean("idf")
	@RefreshScope
	public Map<String, Double> idf(@Value("${nlp.idf.path}") String idfPath) throws IOException{
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(idfPath), Charset.forName("UTF-8")));
		Map<String, Double> idf = new HashMap<String, Double>();
		String line = null;
        while ((line = reader.readLine()) != null) {
            line = reader.readLine();
            String[] tokens = line.split("[\t ]+");
            if (tokens.length < 2)
                continue;
            String word = tokens[0];
            double freq = Double.valueOf(tokens[1]);
            idf.put(word, freq);
        }
        reader.close();
        return idf;
	}
}
