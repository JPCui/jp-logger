package cn.cjp.logger.redis;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.Tuple;

/**
 * use <tt>Bean</tt> implement RedisDao annotated by @Component("enableRedis")
 * <p>
 * <tt>Bean</tt> annotated by @Component("disableRedis") is useless
 * 
 * @author Jinpeng Cui
 * @see SentinelJedis
 * @see BaseDao
 */
public interface RedisDao extends Closeable {
	
	public Object callback(JedisCallback<Object> callback);

	String ltrim(String key, long start, long end);

	public Long publish(String channel, String message);

	public void subscribe(JedisPubSub jedisPubSub, String... channels);

	void close() throws IOException;

	String lpop(String key);

	String rpop(String key);

	/**
	 * 阻塞rpop
	 * 
	 * @param key
	 * @return
	 */
	List<String> brpop(int timeout, String key);

	List<String> blpop(int timeout, String key);

	Long zrank(String key, String member);

	Long zadd(String key, double score, String member);

	boolean hexists(String key, String field);

	String info();

	String hget(String key, String field);

	Long hset(String key, String field, String value);

	Long del(String... key);

	Double zscore(String key, String member);

	Long zrem(String key, String... members);

	String set(String key, String value);

	public Long setnx(String key, String value);

	Boolean exists(String key);

	String get(String key);

	Long lpush(String key, String... strings);

	Long expire(String key, int seconds);

	Long ttl(String key);

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

	Set<String> getAllKeys(String key);

	boolean isClose();
}
