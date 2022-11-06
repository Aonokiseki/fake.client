package fake.client.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import fake.client.meta.Constants;
import fake.client.pojo.response.BasicResponse;
import fake.client.pojo.response.RedisKeysResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@RestController 
@Api(tags = "Redis")
public class RedisController {
	
	@Autowired(required=false)
	private JedisPool jedisPool;
	
	private Jedis jedis;
	
	@Autowired
	private Gson gson;
	
	@ApiOperation(value="设置Key-Value", notes="设置键值对, 若Key已存在则覆盖Value")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "key", value="键", required=true),
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "value", value="值", required=true)
	})
	@PostMapping(path="/redis/set", produces="application/json;charset=utf-8")
	@ApiResponses({
		@ApiResponse(code=Constants.SUCCESS, message=Constants.SUCCESS_MSG)
	})
	public String set(String key, String value) {
		jedis = jedisPool.getResource();
		String result = jedis.set(key, value);
		BasicResponse response = new BasicResponse();
		response.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG).setResult(result);
		jedis.close();
		return gson.toJson(response);
	}
	
	@ApiOperation(value = "获取Value", notes="通过Key获取Value")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "key", value="键", required=true)
	})
	@GetMapping(path="/redis/get", produces="application/json;charset=utf-8")
	@ApiResponses({
		@ApiResponse(code=Constants.SUCCESS, message=Constants.SUCCESS_MSG),
		@ApiResponse(code=Constants.EMPTY, message=Constants.EMPTY_MSG),
	})
	public String get(String key) {
		 jedis = jedisPool.getResource();
		 String result = jedis.get(key);
		 BasicResponse response = new BasicResponse();
		 if(result != null && !result.isEmpty())
			 response.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG).setResult(result);
		 else
			 response.setCode(Constants.EMPTY).setMessage(Constants.EMPTY_MSG);
		 jedis.close();
		 return gson.toJson(response);
	}
	
	@ApiOperation(value = "删除Key-Value", notes="通过Key删除Key-Value")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "key", value = "键", required = true)
	})
	@PostMapping(path = "/redis/del", produces="application/json;charset=utf-8")
	@ApiResponses({
		@ApiResponse(code=Constants.SUCCESS, message=Constants.SUCCESS_MSG),
		@ApiResponse(code=Constants.EMPTY, message=Constants.EMPTY_MSG)
	})
	public String del(String key) {
		jedis = jedisPool.getResource();
		Long del = jedis.del(key);
		BasicResponse response = new BasicResponse();
		if(del == null)
			response.setCode(Constants.EMPTY).setMessage(Constants.EMPTY_MSG);
		else 
			response.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG).setResult(String.valueOf(del));
		jedis.close();
		return gson.toJson(response);
	}
	
	@ApiOperation(value = "批量删除", notes = "批量删除Key-Value")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "patternStr", value = "正则表达式(为空视为全部)", required = false)
	})
	@PostMapping(path = "/redis/del/batch", produces = "application/json;charset=utf-8")
	@ApiResponses({
		@ApiResponse(code = Constants.SUCCESS, message = Constants.SUCCESS_MSG),
		@ApiResponse(code = Constants.EMPTY, message = Constants.EMPTY_MSG)
	})
	public String batchDelete(String patternStr) {
		jedis = jedisPool.getResource();
		Set<String> keys = jedis.keys(patternStr);
		List<String> keyList = new LinkedList<String>();
		for(String key : keys)
			keyList.add(key);
		Long count = jedis.del(keyList.toArray(new String[] {}));
		BasicResponse response = new BasicResponse();
		if(count == null)
			response.setCode(Constants.EMPTY).setMessage(Constants.EMPTY_MSG);
		else
			response.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG).setResult(String.valueOf(count));
		jedis.close();
		return gson.toJson(response);
	}
	
	@ApiOperation(value="查看Key的数量", notes="查看key的数量")
	@GetMapping(path = "/redis/dbsize", produces="application/json;charset=utf-8")
	@ApiResponses({
		@ApiResponse(code=Constants.SUCCESS, message=Constants.SUCCESS_MSG),
		@ApiResponse(code=Constants.EMPTY, message=Constants.EMPTY_MSG)
	})
	public String dbsize() {
		jedis = jedisPool.getResource();
		Long count = jedis.dbSize();
		BasicResponse response = new BasicResponse();
		if(count == null)
			response.setCode(Constants.EMPTY).setMessage(Constants.EMPTY_MSG);
		else
			response.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG).setResult(String.valueOf(count));
		jedis.close();
		return gson.toJson(response);
	}
	
	@ApiOperation(value="列出Key", notes="按照指定规则列出key")
	@GetMapping(path = "/redis/keys", produces="application/json;charset=utf-8")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "pattern", value = "模式", required = false)
	})
	@ApiResponses({
		@ApiResponse(code=Constants.SUCCESS, message=Constants.SUCCESS_MSG),
		@ApiResponse(code=Constants.EMPTY, message=Constants.EMPTY_MSG)
	})
	public String keys(String pattern) {
		jedis = jedisPool.getResource();
		if(pattern == null || pattern.isEmpty())
			pattern = "*";
		Set<String> keys = jedis.keys(pattern);
		RedisKeysResponse response = new RedisKeysResponse();
		if(keys == null || keys.isEmpty())
			response.setCode(Constants.EMPTY).setMessage(Constants.EMPTY_MSG);
		else {
			response.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG);
			response.setKeys(keys);
		}
		jedis.close();
		return gson.toJson(response);
	}
	
	@ApiOperation(value = "导出key-value",  notes="按照指定规则导出key-value")
	@GetMapping(path = "/redis/export", produces="application/json;charset=utf-8")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "pattern", value = "规则", required = false)
	})
	public String export(String pattern, HttpServletResponse response) throws IOException {
		jedis = jedisPool.getResource();
		if(pattern == null || pattern.isEmpty())
			pattern = "*";
		Set<String> keys = jedis.keys(pattern);
		String[] keyArray = new String[keys.size()];
		int index = 0;
		Iterator<String> iterator = keys.iterator();
		while(iterator.hasNext()) {
			keyArray[index] = iterator.next();
			index++;
		}
		List<String> values = jedis.mget(keyArray);
		Map<String,String> mapple = new HashMap<String, String>();
		for(int i = 0; i < keyArray.length; i++) {
			mapple.put(keyArray[i], values.get(i));
		}
		String json = gson.toJson(mapple);
		String fileName = String.valueOf(System.currentTimeMillis()) + ".json";
		response.setContentType("application/octet-stream;charset=utf-8");
		response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
		response.flushBuffer();
		OutputStream out = response.getOutputStream();
		out.write(json.getBytes("utf-8"));
		out.close();
		BasicResponse basicResponse = new BasicResponse();
		basicResponse.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG).setResult(fileName);
		return gson.toJson(basicResponse);
	}
}