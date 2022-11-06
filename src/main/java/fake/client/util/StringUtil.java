package fake.client.util;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fake.client.pojo.data.MatchResult;
import fake.client.pojo.data.MatchResult.Group;

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
	
	/**
	 * 正则表达式匹配
	 * @param text 待匹配文本
	 * @param regex 正则表达式
	 * @return
	 */
	public static MatchResult regularExpression(String text, String regex) {
		MatchResult result = new MatchResult();
		Pattern pattern = Pattern.compile(regex == null ? "" : regex);
		Matcher matcher = pattern.matcher(text == null ? "" : text);
		boolean isFind = matcher.find();
		result.setIsFind(isFind);
		result.setIsMatches(matcher.matches());
		if(isFind) {
			matcher.reset();
			List<Group> groups = new ArrayList<Group>();
			Group group = null;
			while(matcher.find()) {
				group = new Group();
				group.setGroupZero(matcher.group());
				group.setStart(matcher.start());
				group.setEnd(matcher.end());
				groups.add(group);
			}
			result.setGroups(groups);
		}
		return result;
	}
	
	/**
	 * 身份证号校验
	 * @param id
	 * @return
	 */
	public static boolean isValidId(String id) {
		if(id == null || id.trim().isEmpty())
			return false;
		id = id.trim();
		if(id.length() != 18)
			return false;
		boolean isValidFormat = Pattern.matches("^[0-9]{17}[0-9xX]{1}$", id);
		if(!isValidFormat)
			return false;
		id = id.toLowerCase();
		int[] weights = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
		char[] checksums = {'1', '0', 'x', '9', '8', '7', '6', '5', '4', '3', '2'};
		int sum = 0;
		for(int i = 0, length = id.length() - 1; i < length; i++)
			sum += weights[i] * (id.charAt(i) - '0');
		int remain = sum % 11;
		char checksum = checksums[remain];
		char last = id.charAt(id.length() - 1);
		return checksum == last;
	}
	
	/**
	 * base64编码
	 * @param text
	 * @param encoding
	 * @return
	 */
	public static String base64Encode(String text, String encoding) {
		Base64.Encoder encoder = Base64.getEncoder();
		String encodedStr = encoder.encodeToString(text.getBytes(Charset.forName(encoding)));
		return encodedStr;
	}
	/**
	 * base64解码
	 * @param text
	 * @param encoding
	 * @return
	 */
	public static String base64Decode(String text, String encoding) {
		Base64.Decoder decoder = Base64.getDecoder();
		byte[] decoded = decoder.decode(text.getBytes(Charset.forName(encoding)));
		return new String(decoded);
	}
}
