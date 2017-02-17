package cn.cjp.logger.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import cn.cjp.logger.model.Log;
import cn.cjp.logger.redis.RedisDao;

/**
 * 日志发布
 * 
 * @author Jinpeng Cui
 *
 */
@Component
@Configuration
@PropertySource(value = "classpath:/config.properties")
public class LogPublish {

	/**
	 * 操作日志队列
	 */
	@Autowired
	RedisDao redisDao;

	@Value("${config.queue.log}")
	String queue;

	public void setRedisDao(RedisDao redisDao) {
		this.redisDao = redisDao;
	}

	public void publish(Log log) {
		redisDao.lpush(queue, log.toString());
	}

}
