package cn.cjp.logger.service;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Component;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import cn.cjp.logger.model.Log;
import cn.cjp.logger.mongo.MongoDao;
import cn.cjp.logger.redis.RedisDao;
import cn.cjp.logger.util.JacksonUtil;
import redis.clients.jedis.exceptions.JedisConnectionException;

@Component
@PropertySource(value = "classpath:/config.properties")
public class LogConsumer extends AbstractConsumer {

	private static final Logger logger = Logger.getLogger(LogConsumer.class);
	/**
	 * 异步任务执行器
	 */
	@Autowired
	SimpleAsyncTaskExecutor asyncTaskExecutor;

	/**
	 * 日志队列
	 */
	@Value("${config.queue.log}")
	String queueName;

	@Resource(name = "enableRedis")
	RedisDao redisDao;

	@Value("${config.collection.prefix}")
	String collectionPrefix;

	@Autowired
	MongoDao mongoDao;

	public void run() {
		logger.info("log consumer ready");
		while (!shutdown) {
			try {
				// 阻塞队列操作
				String value = pop();
				if (value != null) {
					Log log = JacksonUtil.fromJsonToObj(value, Log.class);
					Document dbo = log.toDoc();
					MongoDatabase db = mongoDao.getDB();
					final String collectionName = collectionPrefix + log.getName().toLowerCase();
					MongoCollection<Document> dbc = db.getCollection(collectionName);
					dbc.insertOne(dbo);
					String _id = dbo.get("_id").toString();
					if (logger.isDebugEnabled()) {
						logger.info(
								String.format("[%s]insert new log %s, msg: %s", collectionName, _id, log.getMessage()));
					}
				} else {
					logger.error("cache read list error, the err value is " + value);
				}
			} catch (JedisConnectionException e) {
				logger.error("", e);
				break;
			} catch (Exception e) {
				logger.error("", e);
			}
		}
	}

	@Override
	public SimpleAsyncTaskExecutor getAsyncTaskExecutor() {
		return this.asyncTaskExecutor;
	}

	@Override
	public RedisDao getRedisDao() {
		return this.redisDao;
	}

	@Override
	public String getQueueName() {
		return this.queueName;
	}

}
