package cn.cjp.logger.dao.redis;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

/**
 * use <tt>Bean</tt> implement RedisDao annotated by @Component("enableRedis")
 * <p>
 * <tt>Bean</tt> annotated by @Component("disableRedis") is useless
 * 
 * @author Jinpeng Cui
 * @since 20160728
 * @see SentinelJedis
 * @see BaseDao
 */
public interface RedisDao extends Closeable {

	/**
	 * 
	 * @param timeout
	 * @param keys
	 * @return
	 * @see Jedis#brpop(int, String...)
	 */
	public List<String> brpop(int timeout, String... keys);

	public Long dbSize();

	void close() throws IOException;

	Long zrank(String key, String member);

	Long zadd(String key, double score, String member);

	boolean hexists(String key, String field);

	String hget(String key, String field);

	Long hset(String key, String field, String value);

	Long del(String... key);

	Double zscore(String key, String member);

	Long zrem(String key, String... members);

	String set(String key, String value);

	Boolean exists(String key);

	String get(String key);

	Long lpush(String key, String... strings);

	Long expire(String key, int seconds);

	Long llen(String key);

	List<String> lrange(String key, long start, long end);

	Long rpush(String key, String... value);

	Double zincrby(String cacheKey, int i, String k);

	Set<Tuple> zrevrangeWithScores(String cacheKey, int i, int j);

	Long zrevrank(String replace, String string);

	Long sadd(String key, String... string);

	Long hdel(String key, String... fields);

	Map<String, String> hgetAll(String key);

	Long scard(String key);
}
