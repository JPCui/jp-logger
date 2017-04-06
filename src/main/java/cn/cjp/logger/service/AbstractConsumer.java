package cn.cjp.logger.service;

import java.util.List;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.Async;

import cn.cjp.logger.redis.RedisDao;
import cn.cjp.utils.Logger;

public abstract class AbstractConsumer implements Runnable, InitializingBean, DisposableBean {

	private final static Logger LOGGER = Logger.getLogger(AbstractConsumer.class);

	@Value("${config.consumer.enable}")
	boolean enable;

	protected volatile boolean shutdown = false;

	public void run() {
		LOGGER.info("log consumer ready");
		try {
			while (!shutdown && enable) {
				try {
					execute();
				} catch (UnexpectedException e) {
					LOGGER.error(e, e);
				}
			}
		} catch (Exception e) {
			LOGGER.error(e, e);
		} finally {
			shutdown = true;
		}
	}

	/**
	 * 消费者具体的执行过程
	 * @throws UnexpectedException
	 */
	public abstract void execute() throws UnexpectedException;

	/**
	 * 从队列中读取一条数据
	 * @return
	 */
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

	@Override
	public void destroy() throws Exception {
		shutdown = true;
	}

	public abstract SimpleAsyncTaskExecutor getAsyncTaskExecutor();

	public abstract RedisDao getRedisDao();

	public abstract String getQueueName();

}

class UnexpectedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6471255941338126732L;

	public UnexpectedException() {
		super();
	}

	public UnexpectedException(Throwable cause) {
		super(cause);
	}

	public UnexpectedException(String message) {
		super(message);
	}

	public UnexpectedException(String message, Throwable cause) {
		super(message, cause);
	}

}