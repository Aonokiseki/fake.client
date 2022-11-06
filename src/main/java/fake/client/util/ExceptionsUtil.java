package fake.client.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionsUtil {
	
	private ExceptionsUtil() {}
	
	 /**
     * 返回堆栈字符串
     * 
     * @param throwable
     * @return String 堆栈信息
     */
    public static String stackTraceToString(Throwable throwable){
    	StringWriter sw = new StringWriter();
 	    throwable.printStackTrace(new PrintWriter(sw, true));
 	    return sw.getBuffer().toString();
    }
}
