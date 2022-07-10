package fake.client.pojo.response;

import java.util.List;
import java.util.Map;

public class MySQLQueryResponse extends BasicResponse{
	private List<Map<String, Object>> resultSet;

	public List<Map<String, Object>> getResultSet() {
		return resultSet;
	}
	public void setResultSet(List<Map<String, Object>> resultSet) {
		this.resultSet = resultSet;
	}
}
