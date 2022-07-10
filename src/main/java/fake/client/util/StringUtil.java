package fake.client.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class StringUtil {
	private StringUtil() {}
	
	/**
	 * 文件名追加时间戳
	 * @param file
	 * @return 新文件名
	 */
	public static String fileNameAppendCurrentTimeMillis(String fileName) {
		int lastIndexOfPoint = fileName.lastIndexOf(".");
		String suffix = "";
		if(lastIndexOfPoint >= 0)
			suffix = fileName.substring(lastIndexOfPoint);
		String base = fileName.replace(suffix, "");
		StringBuilder nameBuilder = new StringBuilder();
		nameBuilder.append(base).append("_").append(System.currentTimeMillis()).append(suffix);
		return nameBuilder.toString();
	}
	
	public static List<String> arrayToList(String[] strings) {
		List<String> result = new LinkedList<String>();
		for(String s : strings)
			result.add(s);
		return result;
	}
	
	public static String buildInsertionSQL(String databaseName, Map<String, Object> record, Boolean ignore){
		List<Object> values = new ArrayList<Object>(record.size());
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("insert");
		if(ignore != null && ignore.booleanValue() == true)
			sqlBuilder.append(" ignore");
		sqlBuilder.append(" into `").append(databaseName).append("`(");
		int columnIndex = 0;
		for(Entry<String, Object> e : record.entrySet()) {
			sqlBuilder.append("`").append(e.getKey()).append("`");
			values.add(e.getValue());
			if(columnIndex++ < record.size() - 1)
				sqlBuilder.append(",");
		}
		sqlBuilder.append(") value (");
		for(int i = 0, size = values.size(); i < size; i++) {
			sqlBuilder.append("'").append(String.valueOf(values.get(i))).append("'");
			if(i < size - 1)
				sqlBuilder.append(",");
		}
		sqlBuilder.append(");");
		return sqlBuilder.toString();
	}
	
	public static String buildUpdatingSQL(String databaseName, Map<String, Object> updates, String where) {
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("update ").append(databaseName).append(" set ");
		int columnIndex = 0;
		for(Entry<String, Object> e : updates.entrySet()) {
			sqlBuilder.append("`").append(e.getKey()).append("` = '").append(String.valueOf(e.getValue())).append("'");
			if(columnIndex++ < updates.size() - 1)
				sqlBuilder.append(", ");
		}
		sqlBuilder.append(" where ").append(where);
		return sqlBuilder.toString();
	}
}
