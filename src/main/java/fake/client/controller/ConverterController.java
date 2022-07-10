package fake.client.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import fake.client.meta.Constants;
import fake.client.pojo.response.BasicResponse;
import fake.client.service.XlsxService;
import fake.client.util.MapListUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags = "Converter")
public class ConverterController {
	
	@Autowired
	private Gson gson;
	
	@ApiOperation(value = "简单json转换为xlsx", notes="尝试将json转换为List<Map<String,Object>>然后再转换为xlsx文件")
	@PostMapping(path = "/json/xlsx")
	public String jsonConvertToXlsx(String jsonStr, HttpServletResponse response) throws IOException {
		List<Map<String,Object>> records = gson.fromJson(jsonStr, new TypeToken<List<Map<String,Object>>>(){}.getType());
		XSSFWorkbook book = XlsxService.convertRecordsToBook(records);
		OutputStream output = response.getOutputStream();
		String fileName = String.valueOf(System.currentTimeMillis()) + ".xlsx";
		response.setContentType("application/octet-stream;charset=utf-8");
		response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
		response.flushBuffer();
		book.write(output);
		book.close();
		BasicResponse basicResponse = new BasicResponse();
		basicResponse.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG).setResult(fileName);
		return gson.toJson(basicResponse);
	}
	
	@ApiOperation(value = "简单json文件转换为xlsx", notes="尝试读取json文件并将json转换为List<Map<String,Object>>然后再转换为xlsx文件")
	@PostMapping(path = "/json/file/xlsx")
	public String jsonFileConvertToXlsx(
			@RequestPart("uploadFile") MultipartFile uploadFile, HttpServletResponse response) throws IOException {
		InputStream in;
		in = uploadFile.getInputStream();
		List<Map<String,Object>> records = gson.fromJson(new JsonReader(new InputStreamReader(in)), 
				new TypeToken<List<Map<String,Object>>>(){}.getType());
		XSSFWorkbook book = XlsxService.convertRecordsToBook(records);
		OutputStream output = response.getOutputStream();
		String fileName = String.valueOf(System.currentTimeMillis()) + ".xlsx";
		response.setContentType("application/octet-stream;charset=utf-8");
		response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
		response.flushBuffer();
		book.write(output);
		BasicResponse basicResponse = new BasicResponse();
		basicResponse.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG).setResult(fileName);
		return gson.toJson(basicResponse);
	}
	
	@ApiOperation(value = "简单json文件转换为SQL")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "dbName", value = "表名", required = true),
	})
	@PostMapping(path = "/json/file/sql")
	public String jsonFileConvertToSQL(
			@RequestPart("uploadFile") MultipartFile uploadFile, String dbName, HttpServletResponse response) throws IOException {
		InputStream in = uploadFile.getInputStream();
		List<Map<String,Object>> records = gson.fromJson(new JsonReader(new InputStreamReader(in)), 
				new TypeToken<List<Map<String,Object>>>(){}.getType());
		String sql = MapListUtil.mapListConvertToSQL(dbName, records);
		String fileName = String.valueOf(System.currentTimeMillis()) + ".sql";
		response.setContentType("application/octet-stream;charset=utf-8");
		response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
		response.flushBuffer();
		OutputStream out = response.getOutputStream();
		out.write(sql.getBytes("utf-8"));
		BasicResponse basicResponse = new BasicResponse();
		basicResponse.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG).setResult(fileName);
		return gson.toJson(basicResponse);
	}
}