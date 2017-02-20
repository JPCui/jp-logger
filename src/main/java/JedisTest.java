
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class JedisTest {

	Lock lock = new ReentrantLock();

	static final int RUN_NUM = 1000_000;

	static String password = "";

	static AtomicInteger i = new AtomicInteger(0);

	int threadNum = 10;

	ExecutorService executorService = Executors.newFixedThreadPool(threadNum);

	public static void main(String[] args) throws InterruptedException {
		i.set(0);
		new JedisTest().simpleThread();
		i.set(0);
		new JedisTest().multiThread();
		i.set(0);
		new JedisTest().multiThreadShareResource();

		System.out.println(new Date());
	}

	public void simpleThread() {
		JedisPool jedisPool = new JedisPool("test.host");
		for (int t = i.get(); t < RUN_NUM; t = i.incrementAndGet()) {
			Jedis jedis = jedisPool.getResource();
			jedis.auth(password);
			jedis.hset("key" + (i.get() % 100), "field" + i, "value=" + i);
			jedis.close();
			if (t % 1000 == 0) {
				System.out.println(Thread.currentThread().getName() + " " + t);
			}
		}
		jedisPool.close();
	}

	public void multiThread() throws InterruptedException {
		JedisPool jedisPool = new JedisPool("test.host");
		for (int n = 0; n < threadNum; n++) {
			executorService.submit(new Thread(new Runnable() {
				@Override
				public void run() {
					int t = i.get();
					while (t < RUN_NUM) {
						t = i.incrementAndGet();
						Jedis jedis = jedisPool.getResource();
						jedis.auth(password);
						jedis.hset("key" + (t % 100), "field" + t, "value=" + t);
						jedis.close();
						if (t % 1000 == 0) {
							System.out.println(Thread.currentThread().getName() + " " + t);
						}
					}
					System.out.println(i.get() + " - " + RUN_NUM);
					synchronized (executorService) {
						executorService.notifyAll();
					}
				}
			}));

		}
		synchronized (executorService) {
			executorService.wait();
		}
		lock.lock();
		jedisPool.close();
		executorService.shutdown();
		System.out.println("finish");
	}

	/**
	 * 同一线程只用一个Jedis
	 * 
	 * @throws InterruptedException
	 */
	public void multiThreadShareResource() throws InterruptedException {
		JedisPool jedisPool = new JedisPool("test.host");

		for (int n = 0; n < threadNum; n++) {
			executorService.submit(new Thread(new Runnable() {
				@Override
				public void run() {
					Jedis jedis = jedisPool.getResource();
					jedis.auth(password);
					int t = i.get();
					while (t < RUN_NUM) {
						t = i.incrementAndGet();
						jedis.hset("key" + (t % 100), "field" + t, "value=" + t);
						if (t % 1000 == 0) {
							System.out.println(Thread.currentThread().getName() + " " + t);
						}
					}
					jedis.close();
					System.out.println(i.get() + " - " + RUN_NUM);
					synchronized (executorService) {
						executorService.notifyAll();
					}
				}
			}));

		}
		synchronized (executorService) {
			executorService.wait();
		}
		lock.lock();
		jedisPool.close();
		executorService.shutdown();
		System.out.println("finish");
	}
}
