package fake.client.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import fake.client.meta.Constants;
import fake.client.pojo.response.BasicResponse;
import fake.client.util.ExceptionsUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Api(tags = "Encode/Decode")
public class EnDecodeController {
	
	@Autowired
	private Gson gson;
	
	@ApiOperation(value = "base64编码")
	@ApiResponse(code = Constants.SUCCESS, message = Constants.SUCCESS_MSG)
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "source", value = "原始字符串", required = true),
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "charset", value = "字符编码", required = false)
	})
	@GetMapping(path = "/base64/encode")
	public String base64Encoding(String source, String charset) {
		BasicResponse basicResponse = new BasicResponse();
		Encoder encode = Base64.getEncoder();
		byte[] bytes = source.getBytes(Charset.forName(charset == null ? "utf-8" : charset));
		String encoded = encode.encodeToString(bytes);
		basicResponse.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG).setResult(encoded);
		return gson.toJson(basicResponse);
	}
	
	@ApiOperation(value = "base64解码")
	@ApiResponse(code = Constants.SUCCESS, message = Constants.SUCCESS_MSG)
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "encoded", value = "Base64编码后的字符串", required = true)
	})
	@GetMapping(path = "/base64/decode")
	public String base64Decoding(String encoded) {
		BasicResponse basicResponse = new BasicResponse();
		Decoder decode = Base64.getDecoder();
		byte[] bytes = decode.decode(encoded);
		String decoded = new String(bytes);
		basicResponse.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG).setResult(decoded);
		return gson.toJson(basicResponse);
	}
	
	@ApiOperation(value = "URL编码")
	@ApiResponses({
		@ApiResponse(code = Constants.SUCCESS, message = Constants.SUCCESS_MSG),
		@ApiResponse(code = Constants.ERROR, message = Constants.ERROR_MSG)
	})
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "source", value = "原始字符串", required = true),
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "charset", value = "字符串编码", required = false)
	})
	@GetMapping(path = "/url/encode")
	public String urlEncoding(String source, String charset) {
		charset = (charset == null) ? "utf-8" : charset;
		BasicResponse response = new BasicResponse();
		try {
			String encoded = URLEncoder.encode(source, charset);
			response.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG).setResult(encoded);
		} catch (UnsupportedEncodingException e) {
			String errorInfo = ExceptionsUtil.stackTraceToString(e);
			response.setCode(Constants.ERROR).setMessage(Constants.ERROR_MSG).setResult(errorInfo);
		}
		return gson.toJson(response);
	}
	
	@ApiOperation(value = "URL解码")
	@ApiResponses({
		@ApiResponse(code = Constants.SUCCESS, message = Constants.SUCCESS_MSG),
		@ApiResponse(code = Constants.ERROR, message = Constants.ERROR_MSG)
	})
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "encoded", value = "URL编码后的字符串", required = true),
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "charset", value = "字符串编码", required = false)
	})
	@GetMapping(path = "/url/decode")
	public String urlDecoding(String encoded, String charset) {
		charset = (charset == null) ? "utf-8" : charset;
		BasicResponse response = new BasicResponse();
		try {
			String decoded = URLDecoder.decode(encoded, charset);
			response.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG).setResult(decoded);
		} catch (UnsupportedEncodingException e) {
			String errorInfo = ExceptionsUtil.stackTraceToString(e);
			response.setCode(Constants.ERROR).setMessage(Constants.ERROR_MSG).setResult(errorInfo);
		}
		return gson.toJson(response);
	}
}
