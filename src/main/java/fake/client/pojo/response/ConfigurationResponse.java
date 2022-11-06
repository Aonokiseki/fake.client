package fake.client.pojo.response;

import java.util.Collections;
import java.util.Map;

public class ConfigurationResponse extends BasicResponse{
	private Map<String, String> configs;
	
	public void setConfigs(Map<String, String> configs) {
		this.configs = configs;
	}
	public Map<String, String> configs(){
		return Collections.unmodifiableMap(configs);
	}
}
