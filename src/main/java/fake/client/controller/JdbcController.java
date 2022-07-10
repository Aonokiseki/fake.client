package fake.client.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import fake.client.meta.Constants;
import fake.client.pojo.response.BasicResponse;
import fake.client.pojo.response.MySQLQueryResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Api(tags = "Jdbc")
public class JdbcController {
	
	@Autowired
	@Qualifier("jdbcTemplate")
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private Gson gson;
	
	@ApiOperation(value = "SQL查询", notes="根据所给SQL语句查询, 返回列表")
	@GetMapping(path = "/jdbc/query")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "sql", value = "SQL语句", required = true),
	})
	@ApiResponses({
		@ApiResponse(code = Constants.SUCCESS, message = Constants.SUCCESS_MSG),
		@ApiResponse(code = Constants.ERROR, message = Constants.ERROR_MSG)
	})
	public String query(String sql) {
		MySQLQueryResponse response = new MySQLQueryResponse();
		List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
		response.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG);
		response.setResultSet(result);
		return gson.toJson(response);
	}
	
	@ApiOperation(value = "写入操作", notes="插入/修改/删除记录")
	@PostMapping(path = "/jdbc/update")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "sql", value = "SQL语句", required = true),
	})
	@ApiResponses({
		@ApiResponse(code = Constants.SUCCESS, message = Constants.SUCCESS_MSG),
		@ApiResponse(code = Constants.ERROR, message = Constants.ERROR_MSG)
	})
	public String update(String sql) {
		BasicResponse response = new BasicResponse();
		int result = jdbcTemplate.update(sql);
		response.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG).setResult(String.valueOf(result));
		return gson.toJson(response);
	}
	
	@ApiOperation(value = "执行SQL语句", notes="执行SQL语句(多用于建表等行为)")
	@PostMapping(path = "/jdbc/execute")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "sql", value = "SQL语句", required = true)
	})
	@ApiResponses({
		@ApiResponse(code = Constants.SUCCESS, message = Constants.SUCCESS_MSG),
		@ApiResponse(code = Constants.ERROR, message = Constants.ERROR_MSG)
	})
	public String execute(String sql) {
		BasicResponse response = new BasicResponse();
		jdbcTemplate.execute(sql);
		response.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG);
		return gson.toJson(response);
	}
}