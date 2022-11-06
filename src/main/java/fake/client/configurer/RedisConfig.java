package fake.client.configurer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@RefreshScope
public class RedisConfig {
	
	@Bean(name="JedisPoolConfig")
	@RefreshScope
	public JedisPoolConfig jedisPoolConfig(
			@Value("${jedis.enabled}") Boolean enabled,
			@Value("${jedis.maxTotal}") int maxActive,
			@Value("${jedis.maxIdle}") int maxIdle,
			@Value("${jedis.minIdle}") int minIdle,
			@Value("${jedis.maxWaitMillis}") long maxWaitMillis,
			@Value("${jedis.testOnBorrow}") boolean testOnBorrow) {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxTotal(maxActive);
		jedisPoolConfig.setMaxIdle(maxIdle);
		jedisPoolConfig.setMinIdle(minIdle);
		jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
		jedisPoolConfig.setTestOnBorrow(testOnBorrow);
		return jedisPoolConfig;
	}
	
	@Bean(name="JedisPool")
	@DependsOn(value = "JedisPoolConfig")
	public JedisPool jedisPool(
			@Value("${jedis.enabled}") Boolean enabled,
			@Value("${jedis.host}") String host,
			@Value("${jedis.port}") int port,
			@Value("${jedis.password}") String password,
			@Value("${jedis.timeout}") int timeout,
			JedisPoolConfig jedisPoolConfig
			) {
		if(enabled != null && !enabled.booleanValue()) {
			return null;
		}
		if(password == null || password.trim().isEmpty())
			return new JedisPool(jedisPoolConfig, host, port, timeout);
		return new JedisPool(jedisPoolConfig, host, port, timeout, password);
	}
}
