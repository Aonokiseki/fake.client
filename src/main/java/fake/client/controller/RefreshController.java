package fake.client.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import fake.client.meta.Constants;
import fake.client.pojo.response.BasicResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Api(tags = "Configuration")
public class RefreshController {
	
	@Autowired
	private ContextRefresher contextRefresher;
	@Autowired
	private Gson gson;
	
	@ApiOperation(value="刷新", notes = "刷新配置文件,无需重启进程")
	@PostMapping(path = "/refresh")
	@ApiResponses({
		@ApiResponse(code=Constants.SUCCESS, message=Constants.SUCCESS_MSG)
	})
    public String refresh() {
		BasicResponse response = new BasicResponse();
        Set<String> result = contextRefresher.refresh();
        response.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG).setResult(result.toString());
        return gson.toJson(response);
    }
}
