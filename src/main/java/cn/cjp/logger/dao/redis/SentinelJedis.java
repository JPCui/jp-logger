package cn.cjp.logger.dao.redis;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import cn.cjp.logger.redis.JedisCallback;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.Tuple;

/**
 * redis 操作基类
 * 
 * @author JinPeng Cui
 */

@Component
public class SentinelJedis implements RedisDao {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(SentinelJedis.class);

	@Resource(name = "jedisSentinelPool")
	JedisSentinelPool jedisSentinelPool;

	public <R> R execute(JedisCallback<R> callback) {
		try (Jedis jedis = jedisSentinelPool.getResource()) {
			return callback.doInJedis(jedis);
		}
	}

	@Override
	public void close() throws IOException {
		if (logger.isInfoEnabled()) {
			logger.info("jedis sentinel pool close.");
		}
		if (!jedisSentinelPool.isClosed()) {
			jedisSentinelPool.close();
		}
		jedisSentinelPool.destroy();
	}

	public List<String> brpop(int timeout, String... keys) {
		return execute(new JedisCallback<List<String>>() {
			@Override
			public List<String> doInJedis(Jedis jedis) {
				return jedis.brpop(timeout, keys);
			}
		});
	}

	public Long dbSize() {
		return execute(new JedisCallback<Long>() {
			@Override
			public Long doInJedis(Jedis jedis) {
				return jedis.dbSize();
			}
		});
	}

	public Long zrank(String key, String member) {
		return execute(new JedisCallback<Long>() {
			@Override
			public Long doInJedis(Jedis jedis) {
				return jedis.zrank(key, member);
			}
		});
	}

	public Long zadd(String key, double score, String member) {
		return execute(new JedisCallback<Long>() {
			@Override
			public Long doInJedis(Jedis jedis) {
				return jedis.zadd(key, score, member);
			}
		});
	}

	public boolean hexists(String key, String field) {
		return execute(new JedisCallback<Boolean>() {
			@Override
			public Boolean doInJedis(Jedis jedis) {
				return jedis.hexists(key, field);
			}
		});
	}

	public String hget(String key, String field) {
		return execute(new JedisCallback<String>() {
			@Override
			public String doInJedis(Jedis jedis) {
				return jedis.hget(key, field);
			}
		});
	}

	public Long hset(String key, String field, String value) {
		return execute(new JedisCallback<Long>() {
			@Override
			public Long doInJedis(Jedis jedis) {
				return jedis.hset(key, field, value);
			}
		});
	}

	public Long del(String key) {
		return execute(new JedisCallback<Long>() {
			@Override
			public Long doInJedis(Jedis jedis) {
				return jedis.del(key);
			}
		});
	}

	public Double zscore(String key, String member) {
		return execute(new JedisCallback<Double>() {
			@Override
			public Double doInJedis(Jedis jedis) {
				return jedis.zscore(key, member);
			}
		});
	}

	public Long zrem(String key, String... members) {
		return execute(new JedisCallback<Long>() {
			@Override
			public Long doInJedis(Jedis jedis) {
				return jedis.zrem(key, members);
			}
		});
	}

	public String set(String key, String value) {
		return execute(new JedisCallback<String>() {
			@Override
			public String doInJedis(Jedis jedis) {
				return jedis.set(key, value);
			}
		});
	}

	public String find(String key) {
		return execute(new JedisCallback<String>() {
			@Override
			public String doInJedis(Jedis jedis) {
				return jedis.get(key);
			}
		});
	}

	public Boolean exists(String key) {
		return execute(new JedisCallback<Boolean>() {
			@Override
			public Boolean doInJedis(Jedis jedis) {
				return jedis.exists(key);
			}
		});
	}

	public String get(String key) {
		return execute(new JedisCallback<String>() {
			@Override
			public String doInJedis(Jedis jedis) {
				return jedis.get(key);
			}
		});
	}

	public Long lpush(String key, String... strings) {
		return execute(new JedisCallback<Long>() {
			@Override
			public Long doInJedis(Jedis jedis) {
				return jedis.lpush(key, strings);
			}
		});
	}

	public Long expire(String key, int seconds) {
		return execute(new JedisCallback<Long>() {
			@Override
			public Long doInJedis(Jedis jedis) {
				return jedis.expire(key, seconds);
			}
		});
	}

	public Long llen(String key) {
		return execute(new JedisCallback<Long>() {
			@Override
			public Long doInJedis(Jedis jedis) {
				return jedis.llen(key);
			}
		});
	}

	public List<byte[]> lrange(byte[] key, long start, long end) {
		return execute(new JedisCallback<List<byte[]>>() {
			@Override
			public List<byte[]> doInJedis(Jedis jedis) {
				return jedis.lrange(key, start, end);
			}
		});
	}

	public List<String> lrange(String key, long start, long end) {
		return execute(new JedisCallback<List<String>>() {
			@Override
			public List<String> doInJedis(Jedis jedis) {
				return jedis.lrange(key, start, end);
			}
		});
	}

	public Long rpush(String key, String... value) {
		return execute(new JedisCallback<Long>() {
			@Override
			public Long doInJedis(Jedis jedis) {
				return jedis.rpush(key, value);
			}
		});
	}

	@Override
	public Long del(String... keys) {
		return execute(new JedisCallback<Long>() {
			@Override
			public Long doInJedis(Jedis jedis) {
				return jedis.del(keys);
			}
		});
	}

	@Override
	public Double zincrby(String key, int score, String member) {
		return execute(new JedisCallback<Double>() {
			@Override
			public Double doInJedis(Jedis jedis) {
				return jedis.zincrby(key, score, member);
			}
		});
	}

	@Override
	public Set<Tuple> zrevrangeWithScores(String key, int start, int end) {
		return execute(new JedisCallback<Set<Tuple>>() {
			@Override
			public Set<Tuple> doInJedis(Jedis jedis) {
				return jedis.zrevrangeWithScores(key, start, end);
			}
		});
	}

	@Override
	public Long zrevrank(String key, String member) {
		return execute(new JedisCallback<Long>() {
			@Override
			public Long doInJedis(Jedis jedis) {
				return jedis.zrevrank(key, member);
			}
		});
	}

	@Override
	public Long sadd(String key, String... members) {
		return execute(new JedisCallback<Long>() {
			@Override
			public Long doInJedis(Jedis jedis) {
				return jedis.sadd(key, members);
			}
		});
	}

	@Override
	public Long hdel(String key, String... fields) {
		return execute(new JedisCallback<Long>() {
			@Override
			public Long doInJedis(Jedis jedis) {
				return jedis.hdel(key, fields);
			}
		});
	}

	@Override
	public Map<String, String> hgetAll(String key) {
		return execute(new JedisCallback<Map<String, String>>() {
			@Override
			public Map<String, String> doInJedis(Jedis jedis) {
				return jedis.hgetAll(key);
			}
		});
	}

	@Override
	public Long scard(String key) {
		return execute(new JedisCallback<Long>() {
			@Override
			public Long doInJedis(Jedis jedis) {
				return jedis.scard(key);
			}
		});
	}

}
