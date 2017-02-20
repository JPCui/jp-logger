package cn.cjp.logger.redis;

import java.io.IOException;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import cn.cjp.logger.model.Log;
import cn.cjp.logger.service.LogPublish;

/**
 * 
 * @author Jinpeng Cui
 *
 */
public class RedisAppender extends AppenderSkeleton {

	private static RedisDao redisDao;

	private static LogPublish logPubSub;

	public RedisAppender() throws IOException {
	}

	@Override
	public void activateOptions() {
		redisDao = RedisDaoBuilder.newInstance();

		logPubSub = new LogPublish();
		logPubSub.setRedisDao(redisDao);
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
			System.err.println(e);
		}
	}

}
