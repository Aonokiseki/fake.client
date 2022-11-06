package fake.client.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ListMapUtil {
	private ListMapUtil() {}
	
	public static List<String> mapListGlobalKeySet(List<Map<String,Object>> mapList){
		List<String> columnNames = new LinkedList<String>();
		if(mapList == null || mapList.isEmpty())
			return columnNames;
		Map<String, Object> recordModule = mapList.get(0);
		for(Entry<String, Object> e : recordModule.entrySet())
			columnNames.add(e.getKey());
		return columnNames;
	}
	
	public static String mapListToSql(String dbName, List<Map<String, Object>> mapList) {
		List<String> columnNames = mapListGlobalKeySet(mapList);
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("insert into ").append(dbName).append("(");
		int columnIndex = 0;
		for(int i = 0; i < columnNames.size(); i++) {
			sqlBuilder.append("`").append(columnNames.get(i)).append("`");
			if(columnIndex < columnNames.size() - 1)
				sqlBuilder.append(", ");
			columnIndex++;
		}
		sqlBuilder.append(") values").append(System.lineSeparator());
		Map<String, Object> record = null;
		for(int i = 0; i < mapList.size(); i++) {
			sqlBuilder.append("(");
			columnIndex = 0;
			record = mapList.get(i);
			for(Entry<String, Object> e : record.entrySet()) {
				sqlBuilder.append("'").append(e.getValue()).append("'");
				if(columnIndex < record.size() - 1)
					sqlBuilder.append(", ");
				columnIndex++;
			}
			sqlBuilder.append(")");
			if(i < mapList.size() - 1)
				sqlBuilder.append(",").append(System.lineSeparator());
			else
				sqlBuilder.append(";");
		}
		return sqlBuilder.toString();
	}
}
