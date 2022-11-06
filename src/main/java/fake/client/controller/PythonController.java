package fake.client.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import fake.client.meta.Constants;
import fake.client.pojo.request.TextRankRequestBody;
import fake.client.pojo.response.BasicResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@Api(tags = "Python")
public class PythonController {
	
	@Autowired
	private Gson gson;
	
	@Value("${python.enabled}")
	private Boolean enabled;
	
	@Autowired
	@Qualifier(value = "python.interpreter")
	private File pythonInterpreter;
	
	@Value("${python.encode}")
	private String encode;
	
	@Autowired
	@Qualifier(value = "word.cloud.config")
	private Map<String, String> wordcloudConfig;
	
	@Autowired
	@Qualifier(value = "python.scripts.directory")
	private File pythonScriptsDirectory;
	
	@ApiOperation(value="TextRank摘要", notes = "调用Python 实现的 text-rank 算法完成")
	@PostMapping(path = "/python/textrank", produces = "*/*", consumes = "application/x-www-form-urlencoded")
	@ApiResponses({
		@ApiResponse(code = Constants.SUCCESS, message = Constants.SUCCESS_MSG),
		@ApiResponse(code = Constants.ERROR, message = Constants.ERROR_MSG),
		@ApiResponse(code = Constants.EMPTY, message = Constants.EMPTY_MSG)
	})
	public String textRank(@RequestBody TextRankRequestBody requestBody) throws IOException {
		if(!enabled.booleanValue())
			return gson.toJson(new BasicResponse()
					.setCode(Constants.ERROR).setMessage(Constants.ERROR_MSG).setResult("enabled=false"));
		File entranceFile = new File(pythonScriptsDirectory.getAbsolutePath() + "/text_rank.py");
		String topNSentence = "3";
		Integer getTopNSentence = requestBody.getTopNSentences();
		if(getTopNSentence == null)
			topNSentence = String.valueOf(getTopNSentence);
		Process process = Runtime.getRuntime().exec(
				new String[] {
						pythonInterpreter.getAbsolutePath(), 
						entranceFile.getAbsolutePath(), 
						requestBody.getText(), 
						topNSentence});
		InputStream input = process.getInputStream();
		if(encode == null || encode.isEmpty())
			encode = "utf-8";
		BufferedReader reader = new BufferedReader(new InputStreamReader(input, encode));
		StringBuilder result = new StringBuilder();
		String line = null;
		while((line = reader.readLine()) != null)
			result.append(line).append(System.lineSeparator());
		BasicResponse basicResponse = new BasicResponse();
		basicResponse.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG).setResult(result.toString());
		reader.close();
		return gson.toJson(basicResponse);
	}
	
	@ApiOperation(value="标签云", notes = "根据所给文本生成分词标签云")
	@PostMapping(path = "/python/word-cloud")
	public void wordcloud(String text, HttpServletResponse response) throws IOException {
		if(enabled == null || !enabled)
			throw new IllegalStateException("Python interpreter is disabled.");
		if(pythonInterpreter == null || !pythonInterpreter.exists())
			throw new IOException(String.format("[%s] not exist.", pythonInterpreter.getAbsolutePath()));
		File scriptFile = new File(pythonScriptsDirectory.getAbsolutePath() + "/word_cloud.py");
		String photoName = UUID.randomUUID().toString();
		String[] commandAndParameters = new String[] {
				pythonInterpreter.getAbsolutePath(), 
				scriptFile.getAbsolutePath(),
				photoName, 
				text, 
				wordcloudConfig.getOrDefault("width", "640"), 
				wordcloudConfig.getOrDefault("height", "480"), 
				wordcloudConfig.getOrDefault("background-color", "white"), 
				wordcloudConfig.get("directory"),
				wordcloudConfig.get("stopwords-path"), 
				wordcloudConfig.get("font-path")
		};
		Process process = Runtime.getRuntime().exec(commandAndParameters);
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line = null;
		List<String> infos = new LinkedList<String>();
		if((line = reader.readLine()) != null)
			infos.add(line);
		File photo = new File(infos.get(infos.size() - 1).toString());
		response.setContentType("application/octet-stream;charset=utf-8");
		response.setHeader("Content-Disposition", "attachment; filename=" + photo.getName());
		response.flushBuffer();
		InputStream in = new FileInputStream(photo);
		OutputStream out = response.getOutputStream();
		IOUtils.copy(in, out);
		return;
	}
}