package cn.cjp.logger.service;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import cn.cjp.logger.model.Node;
import cn.cjp.logger.mongo.MongoDao;
import cn.cjp.logger.redis.RedisDao;
import cn.cjp.logger.util.JacksonUtil;
import redis.clients.jedis.exceptions.JedisConnectionException;

@Configuration
@PropertySource(value = "classpath:/config.properties")
@Component
public class NodeConsumer implements Runnable, InitializingBean {

	private static final Logger logger = Logger.getLogger(NodeConsumer.class);

	/**
	 * 异步任务执行器
	 */
	@Autowired
	SimpleAsyncTaskExecutor asyncTaskExecutor;

	/**
	 * 日志队列
	 */
	@Value("${config.queue.node}")
	String queueName;

	@Resource(name = "enableRedis")
	RedisDao redisDao;

	@Autowired
	MongoDao mongoDao;
	
	@Value("${config.collection.node}")
	String collectionName;

	public void run() {
		logger.info("node consumer ready");
		while (true) {
			try {
				// 阻塞队列操作
				List<String> rec = redisDao.brpop(0, queueName);
				if (logger.isDebugEnabled()) {
					System.out.println(rec);
				}
				if (rec.size() == 2) {
					String value = rec.get(1);
					Node node = JacksonUtil.fromJsonToObj(value, Node.class);
					Document dbo = node.toDoc();
					MongoDatabase db = mongoDao.getDB(mongoDao.getDatabase());
					MongoCollection<Document> dbc = db.getCollection(collectionName);
					dbc.insertOne(dbo);
					String _id = dbo.getString("_id");
					if (logger.isInfoEnabled()) {
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

	@Async
	@Override
	public void afterPropertiesSet() throws Exception {
		// 开启线程池
		try {
			asyncTaskExecutor.execute(this);
			logger.info("log consumer thread start");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

}
