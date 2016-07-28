package cn.cjp.logger.redis;

import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

@Configuration
@PropertySource(value = "classpath:/redis.properties")
public class RedisConfig {

	@Value("${redis.cluster.master}")
	String masterName;

	@Value("${redis.sentinels}")
	Set<String> sentinels;

	@Value("${redis.host}")
	String host;

	@Value("${redis.password}")
	String password;

	@Value("${redis.timeout}")
	int timeout;

	@Value("${redis.maxTotal}")
	int maxTotal;

	@Value("${redis.maxIdle}")
	int maxIdle;

	@Value("${redis.minIdle}")
	int minIdle;

	@Value("${redis.maxWaitMillis}")
	int maxWaitMillis;

	@Value("${redis.testOnBorrow}")
	boolean testOnBorrow;

	@Value("${redis.testOnReturn}")
	boolean testOnReturn;

	@Value("${redis.testWhileIdle}")
	boolean testWhileIdle;

	@Bean
	public JedisPoolConfig jedisPoolConfig() {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(maxTotal);
		config.setMinIdle(minIdle);
		config.setMaxWaitMillis(maxWaitMillis);
		config.setTestOnBorrow(testOnBorrow);
		config.setTestOnReturn(testOnReturn);
		config.setTestWhileIdle(testWhileIdle);
		return config;
	}

	@Bean(destroyMethod = "close")
	public JedisSentinelPool jedisSentinelPool() {
		JedisSentinelPool pool = new JedisSentinelPool(masterName, sentinels, jedisPoolConfig(), timeout, password);
		return pool;
	}

}
