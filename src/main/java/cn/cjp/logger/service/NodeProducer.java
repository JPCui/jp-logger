package cn.cjp.logger.service;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import cn.cjp.logger.model.Node;
import cn.cjp.logger.redis.RedisDao;
import cn.cjp.logger.util.JacksonUtil;

@Configuration
@PropertySource(value = "classpath:/config.properties")
@Component
public class NodeProducer {

	/**
	 * 日志队列
	 */
	@Value("${config.queue.node}")
	String queueName;

	@Resource(name = "enableRedis")
	RedisDao redisDao;

	public void produce(Node node) {
		redisDao.lpush(queueName, JacksonUtil.toJson(node));
		redisDao.ltrim(queueName, 0, 20000);
	}

}
