package cn.cjp.logger.service;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.cjp.logger.dao.mongo.MongoDao;
import cn.cjp.logger.dao.redis.SentinelJedis;

@Component
public class LogConsumerManager implements InitializingBean {

	private static final Logger logger = Logger.getLogger(LogConsumerManager.class);

	public int THREAD_NUM = 3;

	@Autowired
	SentinelJedis sentinelJedis;

	@Autowired
	MongoDao mongoDao;

	ThreadPoolExecutor executor = null;

	public int activeCount = 0;

	public long taskCount = 0;

	public void run() {
		ArrayBlockingQueue<Runnable> runnables = new ArrayBlockingQueue<>(THREAD_NUM);
		for (int i = 0; i < THREAD_NUM; i++) {
			runnables.add(newThread());
		}

		try {
			executor = new ThreadPoolExecutor(THREAD_NUM, THREAD_NUM * 2, 30, TimeUnit.SECONDS, runnables);
			for (int i = 0; i < THREAD_NUM; i++) {
				executor.submit(newThread());
			}
			logger.info("log consumer thread start");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					if (null != executor && !executor.isShutdown()) {
						activeCount = executor.getActiveCount();
						taskCount = executor.getTaskCount();
						logger.info("active count: " + activeCount);
						logger.info("task count: " + taskCount);
					}
					try {
						Thread.sleep(30_000L);
					} catch (InterruptedException e) {
					}
				}
			}
		}).start();
	}

	private Runnable newThread() {
		LogConsumer thread = LogConsumer.newInstance(sentinelJedis, mongoDao);
		return thread;
	}

	@Override
	public void afterPropertiesSet() {
		run();
	}

}
