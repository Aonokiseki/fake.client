package fake.client.interceptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import fake.client.util.StringUtil;

public class RequestInterceptor implements HandlerInterceptor{
	
	private Logger logger = LoggerFactory.getLogger(RequestInterceptor.class);
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		StringBuilder infoBuilder = new StringBuilder();
		infoBuilder.append(request.getRequestURI()).append(", ");
		Map<String, String[]> parameterMap = request.getParameterMap();
		Map<String, List<String>> parameterStrMap = new HashMap<String, List<String>>();
		for(Entry<String, String[]> entry : parameterMap.entrySet())
			parameterStrMap.put(entry.getKey(), StringUtil.arrayToList(entry.getValue()));
		if(!parameterStrMap.isEmpty())
			infoBuilder.append(parameterStrMap);
		logger.info(infoBuilder.toString());
		return true;
	}
}