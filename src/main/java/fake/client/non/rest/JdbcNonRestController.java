package fake.client.non.rest;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class JdbcNonRestController {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@GetMapping("/jdbc/query/page")
	public String queryPage(
			@RequestParam(name="sql") String sql, 
			@RequestParam(name="pageNum", defaultValue="0") int pageNum,
			@RequestParam(name="pageSize", defaultValue="5") int pageSize,
			Model model) {
		String totalSql = "select * from ("+ sql +") as usersql limit ?, ?;";
		List<Map<String, Object>> objects = jdbcTemplate.queryForList(totalSql, pageNum, pageSize);
		model.addAttribute("objects", objects);
		return "listObjects";
	}
	
	@GetMapping("/jdbc/query/page/simple")
	public String queryPageSimple(
			@RequestParam(name = "databaseName") String databaseName,
			@RequestParam(name = "where", defaultValue = "1=1") String where,
			@RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
			@RequestParam(name = "pageSize", defaultValue = "5") int pageSize,
			Model model) {
		String sql = "select * from " + databaseName + " where " + where + " limit " + pageNum + ", " + pageSize + ";";
		List<Map<String, Object>> objects = jdbcTemplate.queryForList(sql);
		model.addAttribute("objects", objects);
		return "listObjects";
	}
	
	@PostMapping("/jdbc/update/page")
	public String updatePage(
			@RequestParam(name = "sql") String sql, Model model) {
		int count = jdbcTemplate.update(sql);
		model.addAttribute("updateCount", count);
		return "afterUpdate";
	}
}
