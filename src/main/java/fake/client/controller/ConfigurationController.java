package fake.client.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import fake.client.meta.Constants;
import fake.client.pojo.response.BasicResponse;
import fake.client.pojo.response.ConfigurationResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Api(tags = "Configuration")
public class ConfigurationController {
	
	@Autowired
	private ContextRefresher contextRefresher;
	@Autowired
	private Gson gson;
	@Autowired
	private Environment environment;
	
	@ApiOperation(value="刷新", notes = "刷新配置文件,无需重启进程")
	@PostMapping(path = "/config/refresh")
	@ApiResponses({
		@ApiResponse(code=Constants.SUCCESS, message=Constants.SUCCESS_MSG)
	})
    public String refresh() {
		BasicResponse response = new BasicResponse();
        Set<String> result = contextRefresher.refresh();
        response.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG).setResult(result.toString());
        return gson.toJson(response);
    }
	
	@SuppressWarnings("rawtypes")
	@ApiOperation(value="显示", notes = "显示所有配置")
	@PostMapping(path = "/config/display")
	@ApiResponses({
		@ApiResponse(code=Constants.SUCCESS, message=Constants.SUCCESS_MSG)
	})
	public String display() {
		ConfigurationResponse response = new ConfigurationResponse();
		Map<String, String> configs = new HashMap<String,String>();
		for(PropertySource<?> propertySource : ((AbstractEnvironment)environment).getPropertySources()) {
			if(propertySource instanceof EnumerablePropertySource) {
				for(String name : ((EnumerablePropertySource)propertySource).getPropertyNames()) {
					if(name == null)
						continue;
					if(name.startsWith("temp") || name.startsWith("jedis") || name.startsWith("hadoop") ||
					   name.startsWith("mongodb") || name.startsWith("jdbc") || name.startsWith("kingbase") ||
					   name.startsWith("dm") || name.startsWith("nlp") || name.startsWith("python")) {
						configs.put(name, environment.getProperty(name));
					}
				}
			}
		}
		response.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG);
		response.setConfigs(configs);
		return gson.toJson(response);
	}
}
