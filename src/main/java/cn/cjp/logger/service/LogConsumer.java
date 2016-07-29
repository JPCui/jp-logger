package cn.cjp.logger.service;

import java.util.List;

import org.apache.log4j.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;

import cn.cjp.logger.dao.mongo.MongoDao;
import cn.cjp.logger.dao.redis.SentinelJedis;
import cn.cjp.logger.model.Log;
import cn.cjp.utils.JacksonUtil;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class LogConsumer implements Runnable {

	private static final Logger logger = Logger.getLogger(LogConsumer.class);

	/**
	 * 日志队列
	 */
	public static final String CACHE_LIST = "list.cjp.logger";

	SentinelJedis jedis;

	MongoDao mongoDao;

	private LogConsumer() {
	}

	public static LogConsumer newInstance(SentinelJedis sentinelJedis, MongoDao mongoDao) {
		LogConsumer consumer = new LogConsumer();
		consumer.jedis = sentinelJedis;
		consumer.mongoDao = mongoDao;
		return consumer;
	}

	public void run() {
		logger.info("log consumer ready");
		while (true) {
			try {
				// 阻塞队列操作
				List<String> rec = jedis.brpop(0, CACHE_LIST);
				if (logger.isDebugEnabled()) {
					System.out.println(rec);
				}
				if (rec.size() == 2) {
					String value = rec.get(1);
					Log log = JacksonUtil.fromJsonToObj(value, Log.class);
					BasicDBObject dbo = log.toDBObject();
					DB db = mongoDao.getDB(mongoDao.getDatabase());
					DBCollection dbc = db.getCollection(LogService.collection(log.getLevel()));
					WriteResult wr = dbc.save(dbo, WriteConcern.SAFE);
					Object _id = wr.getUpsertedId();
					if (logger.isInfoEnabled()) {
						logger.error(wr.getLastConcern());
						logger.error(dbo);
						logger.error("insert new log " + _id);
					}
				} else {
					logger.error("cache read list error, the err value is " + rec);
				}
			} catch (JedisConnectionException e) {
				logger.error("", e);
				break;
			} catch (Exception e) {
				logger.error("", e);
			}
		}
	}

}
