package cn.cjp.logger.redis;

import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

@Configuration
@PropertySource(value = "classpath:/redis.properties")
public class RedisConfig {

	private static final Logger logger = Logger.getLogger(RedisConfig.class);

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
	public JedisSentinelPool jedisSentinelPool() throws Exception {
		try {
			JedisSentinelPool pool = new JedisSentinelPool(masterName, sentinels, jedisPoolConfig(), timeout, password);
			Jedis jedis = pool.getResource();
			if (!jedis.auth(password).equals("OK")) {
				throw new Exception("redis auth fail");
			}
			return pool;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		throw new Exception("jedis create fail");
	}

}
