package cn.cjp.logger.service;

import java.util.ArrayList;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import cn.cjp.logger.Application;
import cn.cjp.logger.model.Host;
import cn.cjp.logger.model.Log;
import cn.cjp.utils.JacksonUtil;
import redis.clients.jedis.Jedis;

@RunWith(SpringJUnit4ClassRunner.class) // SpringJUnit支持，由此引入Spring-Test框架支持！
@SpringApplicationConfiguration(classes = Application.class) // 指定我们SpringBoot工程的Application启动类
@WebAppConfiguration // 由于是Web项目，Junit需要模拟ServletContext，因此我们需要给我们的测试类加上@WebAppConfiguration。
public class LogServiceTest {

	public static String LEVEL = "DEBUG";

	@Autowired
	LogService logService;

	@Test
	public void findAll() {
		Object obj = logService.findAll("DEBUG", 1);
		System.out.println(JacksonUtil.toJson(obj));
	}

	private static Log buildLog(Object msg) {
		Log log = new Log();
		log.setClazz(Log.class.getName());
		log.setClient(new Host("0.0.0.0", "client"));
		log.setLevel(LEVEL);
		log.setLine("1");
		log.setMessage("it is a test message(" + msg + ")");
		log.setMethod("public void test()");
		log.setServer(new Host("1.1.1.1", "server"));
		log.setThread("thread-0");
		log.setThrowables(new ArrayList<>());
		log.setTime(new Date());
		log.setVersion("0.0.1");
		return log;
	}

	@Test
	public void report() {
		logService.report(buildLog(1));
	}

	public static void main(String[] args) {
		for (int i = 0; i < 50; i++) {
			Jedis jedis = new Jedis("redis.host", 6379);
			jedis.auth("51cuotihui");
			jedis.lpush(LogConsumer.CACHE_LIST, JacksonUtil.toJson(buildLog(i)));
			jedis.close();
		}
	}

}
