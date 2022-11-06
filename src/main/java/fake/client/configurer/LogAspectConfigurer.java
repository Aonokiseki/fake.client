package fake.client.configurer;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogAspectConfigurer {
	private Logger logger = LoggerFactory.getLogger(LogAspectConfigurer.class);
	
	@AfterReturning(value = "execution(String fake.client.controller.*.*(..))",returning = "result")
	public void afterReturning(JoinPoint point, String result) {
		logger.info("result="+result);
	}
}