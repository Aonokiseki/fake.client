package fake.client.util;

import org.springframework.context.ApplicationContext;

public class SpringContextUtil {
	
	private static ApplicationContext applicationContext;
	
	public static ApplicationContext get() {
		return applicationContext;
	}
	
	public static void setApplicationContext(ApplicationContext context) {
		applicationContext = context;
	}
	
	public static Object getBean(String name) {
		return applicationContext.getBean(name);
	}
	
	public static Object getBean(Class<?> requiredType) {
		return applicationContext.getBean(requiredType);
	}
}
