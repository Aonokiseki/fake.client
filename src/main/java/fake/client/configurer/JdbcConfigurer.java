package fake.client.configurer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
@RefreshScope
public class JdbcConfigurer {
	
	@Bean(name = "jdbcTemplate")
	@RefreshScope
	public static JdbcTemplate jdbcTemplate(
			@Value("${jdbc.enabled}") Boolean enabled,
			@Value("${jdbc.driver.classname}") String driverClassName, 
			@Value("${jdbc.url}") String url, 
			@Value("${jdbc.user}") String user, 
			@Value("${jdbc.password}") String password) {
		if(enabled == null || enabled.booleanValue() == false)
			return null;
		DriverManagerDataSource source = new DriverManagerDataSource(url, user, password);
		source.setDriverClassName(driverClassName);
		JdbcTemplate jdbcTemplate =  new JdbcTemplate(source);
		return jdbcTemplate;
	}
	
	@Bean(name = "kingbaseTemplate")
	@RefreshScope
	public static JdbcTemplate kingbaseTemplate(
		@Value("${kingbase.enabled}") Boolean enabled,
		@Value("${kingbase.driver.classname}") String driverClassName,
		@Value("${kingbase.url}") String url,
		@Value("${kingbase.user}") String user,
		@Value("${kingbase.password}") String password) {
		if(enabled == null || enabled.booleanValue() == false)
			return null;
		DriverManagerDataSource source = new DriverManagerDataSource(url, user, password);
		source.setDriverClassName(driverClassName);
		JdbcTemplate jdbcTemplate =  new JdbcTemplate(source);
		return jdbcTemplate;
	}
	
	@Bean(name = "dmTemplate")
	@RefreshScope
	public static JdbcTemplate dmTemplate(
		@Value("${dm.enabled}") Boolean enabled,
		@Value("${dm.driver.classname}") String driverClassName,
		@Value("${dm.url}") String url,
		@Value("${dm.user}") String user,
		@Value("${dm.password}") String password) {
		if(enabled == null || enabled.booleanValue() == false)
			return null;
		DriverManagerDataSource source = new DriverManagerDataSource(url, user, password);
		source.setDriverClassName(driverClassName);
		JdbcTemplate jdbcTemplate =  new JdbcTemplate(source);
		return jdbcTemplate;
	}
}
