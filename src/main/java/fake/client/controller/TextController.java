package fake.client.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import fake.client.meta.Constants;
import fake.client.pojo.data.MatchResult;
import fake.client.pojo.response.BasicResponse;
import fake.client.pojo.response.RegexResponse;
import fake.client.service.DuplicateCleanService;
import fake.client.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Api(tags = "Text")
public class TextController {
	
	@Autowired
	private Gson gson;
	@Autowired
	private DuplicateCleanService cleanService;
	
	@ApiOperation(value="文件按行去重", notes="文件按行去重")
	@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "charsetName", value = "字符集", required = false)
	@PostMapping(path = "text/duplicateClean")
	public String duplicateClean(
			@RequestPart(name = "uploadFile", required = true) MultipartFile uploadFile, 
			String charsetName, 
			HttpServletResponse response) throws IOException {
		InputStream in = uploadFile.getInputStream();
		cleanService.setInput(in);
		response.setContentType("application/octet-stream;charset=utf-8");
		response.setHeader("Content-Disposition", "attachment; filename=" + uploadFile.getOriginalFilename());
		response.flushBuffer();
		OutputStream out = response.getOutputStream();
		cleanService.setOutput(out);
		if(charsetName != null && !charsetName.trim().isEmpty())
			cleanService.setEncoding(charsetName);
		cleanService.execute();
		BasicResponse basicResponse = new BasicResponse();
		basicResponse.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG);
		return gson.toJson(basicResponse);
	}
	
	@ApiOperation(value = "正则表达式匹配")
	@ApiResponses({
		@ApiResponse(code=Constants.SUCCESS, message=Constants.SUCCESS_MSG)
	})
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "text", value = "待匹配字符串", required = false),
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "regex", value = "正则表达式", required = false),
	})
	@GetMapping(path = "/text/regex")
	public String regularExpression(String text, String regex) {
		RegexResponse response = new RegexResponse();
		MatchResult result = StringUtil.regularExpression(text, regex);
		response.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG);
		response.setMatchResult(result);
		return gson.toJson(response);
	}
	
	@ApiOperation(value = "验证18位身份证是否合法")
	@ApiResponses({
		@ApiResponse(code=Constants.SUCCESS, message=Constants.SUCCESS_MSG)
	})
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "id", value = "18位身份证号", required = true),
	})
	@GetMapping(path = "/id/valid")
	public String isValidId(String id) {
		boolean result = StringUtil.isValidId(id);
		BasicResponse response = new BasicResponse();
		response.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG).setResult(String.valueOf(result));
		return gson.toJson(response);
	}
}
