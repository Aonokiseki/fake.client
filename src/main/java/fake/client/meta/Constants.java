package fake.client.meta;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Constants {
	public final static int SUCCESS = 1;
	public final static int EMPTY = -1;
	public final static int ERROR = 0;
	
	public final static String SUCCESS_MSG = "操作成功";
	public final static String EMPTY_MSG = "结果为空";
	public final static String ERROR_MSG = "发生错误";
	
	public final static String APPLICATION_DESCRIPTION = "希望这能省去你寻找客户端和记忆命令的时间";
	public final static String APPLICATION_TITLE = "伪·客户端";
	public final static String APPLICATION_VERSION = "1.0";
	public final static String COMPILE_TIME = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
	public final static String FILE_SEPARATOR = System.getProperty("file.separator");
	public final static String LINE_SEPARATOR = System.lineSeparator();
}
