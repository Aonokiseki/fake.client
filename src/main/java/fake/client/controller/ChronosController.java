package fake.client.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import fake.client.meta.Constants;
import fake.client.pojo.data.Chronos;
import fake.client.pojo.response.BasicResponse;
import fake.client.pojo.response.ChronosResponse;
import fake.client.util.DateTimeUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Api(tags = "Chronos")
public class ChronosController {
	
	@Autowired
	private Gson gson;
	
	@ApiOperation(value = "计算时间戳", notes="根据时间字符串计算时间戳(zoneOffsetId=‘+8’ 表示GMT+8)")
	@ApiResponses({
		@ApiResponse(code = Constants.SUCCESS, message = Constants.SUCCESS_MSG),
	})
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "dateTimeStr", value = "日期字符串", required = true),
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "pattern", value = "日期格式", required = true),
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "zoneOffsetId", value = "时区偏移量", required = true),
	})
	@GetMapping(path = "/datetime/millis")
	public String dateTimeToTimeMillis(String dateTimeStr, String pattern, String zoneOffsetId) {
		BasicResponse response = new BasicResponse();
		long timeMillis = DateTimeUtil.localDateTimeToTimeMillis(dateTimeStr, pattern, zoneOffsetId);
		response.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG).setResult(String.valueOf(timeMillis));
		return gson.toJson(response);
	}
	
	@ApiOperation(value = "计算日期字符串", notes = "根据(不含毫秒的)时间戳计算日期字符串")
	@ApiResponses({
		@ApiResponse(code = Constants.SUCCESS, message = Constants.SUCCESS_MSG)
	})
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "timeSecondsStr", value = "时间戳字符串(秒级别)", required = true),
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "pattern", value = "日期格式", required = true),
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "zoneOffsetId", value = "时区偏移量", required = true),
	})
	@GetMapping(path = "/millis/datetime")
	public String timeMillisToDateTimeStr(String timeSecondsStr, String pattern, String zoneOffsetId) {
		BasicResponse response = new BasicResponse();
		String dateTimeStr = DateTimeUtil.timeSecondsToDateTimeStr(Long.parseLong(timeSecondsStr), pattern, zoneOffsetId);
		response.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG).setResult(dateTimeStr);
		return gson.toJson(response);
	}
	
	@ApiOperation(value = "计算日期")
	@ApiResponses({
		@ApiResponse(code = Constants.SUCCESS, message = Constants.SUCCESS_MSG)
	})
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "dateStr", value = "基准日期(空视为今天)", required = false),
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "pattern", value = "日期格式", required = true),
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "dayOffset", value = "增/减的天数", required = true),
	})
	@GetMapping(path = "/calculate/date")
	public String calculateDate(String dateStr, String pattern, String dayOffset) {
		ChronosResponse response = new ChronosResponse();
		Chronos chronos = DateTimeUtil.calculateDate(dateStr, pattern, Long.parseLong(dayOffset));
		response.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG);
		response.setChronos(chronos);
		return gson.toJson(response);
	}
	
	@ApiOperation(value = "计算日期相差天数")
	@ApiResponses({
		@ApiResponse(code = Constants.SUCCESS, message = Constants.SUCCESS_MSG)
	})
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "start", value = "起始日期", required = true),
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "end", value = "结束日期", required = true),
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "dateTimeFormatStr", value = "日期格式", required = true),
	})
	@GetMapping(path = "/date/between")
	public String daysBetween(String start, String end, String dateTimeFormatStr) {
		BasicResponse response = new BasicResponse();
		long days = DateTimeUtil.daysBetween(start, end, dateTimeFormatStr);
		response.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG).setResult(String.valueOf(days));
		return gson.toJson(response);
	}
}