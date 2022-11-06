package fake.client.handler;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import fake.client.meta.Constants;
import fake.client.util.ExceptionsUtil;

@ControllerAdvice
public class GlobalExceptionHandler{
	
	private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
	@Autowired
	private Gson gson;
	
	@ResponseBody
	@ExceptionHandler(Exception.class)
	public String exception(Exception e, HttpServletResponse response) {
		String exceptionInfo = ExceptionsUtil.stackTraceToString(e);
		Map<String, String> infos = new HashMap<String, String>();
		infos.put("code", String.valueOf(Constants.ERROR));
		infos.put("message", Constants.ERROR_MSG);
		infos.put("result", e.getMessage());
		logger.error(infos + System.lineSeparator() + exceptionInfo);
		return gson.toJson(infos);
	}
}
