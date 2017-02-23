package cn.cjp.logger.service;

import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.Async;

import cn.cjp.logger.redis.RedisDao;
import cn.cjp.logger.util.Logger;

public abstract class AbstractConsumer implements Runnable, InitializingBean {

	private final static Logger LOGGER = Logger.getLogger(AbstractConsumer.class);

	public String pop() {
		List<String> rec = getRedisDao().blpop(0, getQueueName());
		if (rec != null && rec.size() == 2) {
			String value = rec.get(1);
			return value;
		}
		return rec.toString();
	}

	@Async
	@Override
	public void afterPropertiesSet() throws Exception {
		// 开启线程池
		try {
			getAsyncTaskExecutor().execute(this);
			LOGGER.info("log consumer thread start");
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw e;
		}
	}

	public abstract SimpleAsyncTaskExecutor getAsyncTaskExecutor();

	public abstract RedisDao getRedisDao();

	public abstract String getQueueName();

}
