package cn.cjp.logger.redis;

import java.io.IOException;

import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import cn.cjp.logger.model.Log;
import cn.cjp.logger.service.LogPublish;
import cn.cjp.utils.StringUtil;

/**
 * 
 * @author Jinpeng Cui
 *
 */
public class RedisAppender extends AppenderSkeleton implements Appender {

	public static final String DEFAULT_QUEUE = "QUEUE|LOG";

	private static RedisDao redisDao;

	private static LogPublish logPubSub;

	private String queue;

	public RedisAppender() throws IOException {
	}

	@Override
	public void activateOptions() {
		redisDao = RedisDaoBuilder.newInstance();

		logPubSub = new LogPublish();
		logPubSub.setRedisDao(redisDao);
		if (StringUtil.isEmpty(queue)) {
			queue = DEFAULT_QUEUE;
		}
		logPubSub.setQueue(queue);
		System.err.println(String.format("Activate appender."));
	}

	@Override
	public void close() {
		try {
			redisDao.close();
			System.err.println("log4jedis(" + threshold + ") closed.");
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.closed = true;
	}

	@Override
	public boolean requiresLayout() {
		return false;
	}

	@Override
	protected void append(LoggingEvent event) {
		try {
			Log log = Log.parse(event);
			log.setName(this.getName());
			logPubSub.publish(log);
		} catch (Exception e) {
			print(e);
		}
	}

	public void setQueue(String queue) {
		this.queue = queue;
	}

	private void print(Object message) {
		System.err.println("cn.cjp.logger.redis.RedisAppender -> " + message);
	}

}
