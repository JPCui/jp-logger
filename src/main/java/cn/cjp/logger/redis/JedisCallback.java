package cn.cjp.logger.redis;

import redis.clients.jedis.Jedis;

public interface JedisCallback<R> {

	public R doInJedis(Jedis jedis);

}
