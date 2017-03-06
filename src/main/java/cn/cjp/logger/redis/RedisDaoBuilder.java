package cn.cjp.logger.redis;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import cn.cjp.logger.util.PropertiesUtil;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

/**
 * 
 * @author Jinpeng Cui
 *
 */
public class RedisDaoBuilder {

	private static final Logger LOGGER = Logger.getLogger(RedisDaoBuilder.class);

	public static final String CONFIG_FILE = "redis.properties";

	private static RedisDao redisDao;

	private static String masterName;
	private static Set<String> sentinels;

	private static JedisPoolConfig poolConfig;
	private static String password;
	private static int maxTotal;
	private static int maxIdle;
	private static int minIdle;
	private static long maxWaitMillis;
	private static boolean testOnBorrow;
	private static boolean testOnReturn;
	private static boolean testWhileIdle;

	static {
		try {
			PropertiesUtil prop = new PropertiesUtil(CONFIG_FILE);
			masterName = prop.getValue("redis.cluster.master");
			String sentinelsStr = prop.getValue("redis.sentinels");
			sentinels = new HashSet<>(Arrays.asList(sentinelsStr.split(",")));

			password = prop.getValue("redis.password");
			maxTotal = prop.getInt("redis.maxTotal", JedisPoolConfig.DEFAULT_MAX_TOTAL);
			maxIdle = prop.getInt("redis.maxIdle", JedisPoolConfig.DEFAULT_MAX_IDLE);
			minIdle = prop.getInt("redis.minIdle", JedisPoolConfig.DEFAULT_MIN_IDLE);
			maxWaitMillis = prop.getLong("redis.maxWaitMillis", JedisPoolConfig.DEFAULT_MAX_WAIT_MILLIS);
			testOnBorrow = prop.getBoolean("redis.testOnBorrow", JedisPoolConfig.DEFAULT_TEST_ON_BORROW);
			testOnReturn = prop.getBoolean("redis.testOnReturn", JedisPoolConfig.DEFAULT_TEST_ON_RETURN);
			testWhileIdle = prop.getBoolean("redis.testWhileIdle", JedisPoolConfig.DEFAULT_TEST_WHILE_IDLE);

			poolConfig = new JedisPoolConfig();
			poolConfig.setMaxTotal(maxTotal);
			poolConfig.setMaxIdle(maxIdle);
			poolConfig.setMinIdle(minIdle);
			poolConfig.setMaxWaitMillis(maxWaitMillis);
			poolConfig.setTestOnBorrow(testOnBorrow);
			poolConfig.setTestOnReturn(testOnReturn);
			poolConfig.setTestWhileIdle(testWhileIdle);
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * 单例
	 * 
	 * @return
	 */
	public static RedisDao instance() {
		if (redisDao == null) {
			synchronized (RedisDaoBuilder.class) {
				if (redisDao == null) {
					redisDao = newInstance();
				}
			}
		}
		return redisDao;
	}

	public static RedisDao newInstance() {
		JedisSentinelPool jedisSentinelPool = new JedisSentinelPool(masterName, sentinels, poolConfig, password);
		return new SentinelJedis(jedisSentinelPool);
	}

	public static void main(String[] args) {
		RedisDao redisDao = RedisDaoBuilder.instance();
		System.out.println(redisDao.hgetAll("v"));
	}

}
