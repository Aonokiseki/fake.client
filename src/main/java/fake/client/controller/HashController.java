package fake.client.controller;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;

import fake.client.meta.Constants;
import fake.client.pojo.response.BasicResponse;
import fake.client.util.MurmurHash;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Api(tags = "Hash")
public class HashController {
	
	@Autowired
	private Gson gson;
	
	@ApiOperation(value = "计算64位Hash")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "value", value = "值", required = true),
	})
	@PostMapping(path="/calculate/hash64", produces = "application/json;charset=utf-8")
	@ApiResponses({
		@ApiResponse(code = Constants.SUCCESS, message = Constants.SUCCESS_MSG)
	})
	public String hash64(String value) {
		byte[] byteValue = value.getBytes(Charset.forName("utf-8"));
		long hash = MurmurHash.hash(byteValue);
		BasicResponse response = new BasicResponse();
		response.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG).setResult(String.valueOf(hash));
		return gson.toJson(response);
	}
	
	@ApiOperation(value = "计算32位Hash", notes="")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "value", value = "值", required = true),
	})
	@PostMapping(path="/calculate/hash32", produces = "application/json;charset=utf-8")
	@ApiResponses({
		@ApiResponse(code = Constants.SUCCESS, message = Constants.SUCCESS_MSG)
	})
	public String hash32(String value) {
		byte[] byteValue = value.getBytes(Charset.forName("utf-8"));
		int hash = MurmurHash.hash32(ByteBuffer.wrap(byteValue), 0);
		BasicResponse response = new BasicResponse();
		response.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG).setResult(String.valueOf(hash));
		return gson.toJson(response);
	}
	
	@ApiOperation(value = "计算64位Hash")
	@PostMapping(path="/calculate/file/hash64", produces = "application/json;charset=utf-8")
	@ApiResponses({
		@ApiResponse(code = Constants.SUCCESS, message = Constants.SUCCESS_MSG),
		@ApiResponse(code = Constants.ERROR, message = Constants.ERROR_MSG)
	})
	public String fileHash64(@RequestPart("uploadFile") MultipartFile uploadFile) throws IOException {
		BasicResponse response = new BasicResponse();
		long hash = MurmurHash.hash(uploadFile.getBytes());
		response.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG).setResult(String.valueOf(hash));
		return gson.toJson(response);
	}
	
	@ApiOperation(value = "计算32位Hash")
	@PostMapping(path="/calculate/file/hash32", produces = "application/json;charset=utf-8")
	@ApiResponses({
		@ApiResponse(code = Constants.SUCCESS, message = Constants.SUCCESS_MSG),
		@ApiResponse(code = Constants.ERROR, message = Constants.ERROR_MSG)
	})
	public String fileHash32(@RequestPart("uploadFile") MultipartFile uploadFile) throws IOException {
		BasicResponse response = new BasicResponse();
		long hash = MurmurHash.hash32(ByteBuffer.wrap(uploadFile.getBytes()), 0);
		response.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG).setResult(String.valueOf(hash));
		return gson.toJson(response);
	}
}
