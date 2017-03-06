package cn.cjp.logger.redis;

import java.util.UUID;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

/**
 * 用于生成票据，验证票据
 * <p>
 * 基于redis实现
 * 
 * @author Jinpeng Cui
 * @see RedisDao
 */
@Component
public class TicketService {

	public static final int EXPIRE = 24 * 3600;

	@Resource(name = "enableRedis")
	RedisDao redis;

	public String genTicket() {
		String ticket = UUID.randomUUID().toString();
		redis.set(ticket, "1");
		redis.expire(ticket, EXPIRE);
		return ticket;
	}

	/**
	 * 如果存在，并返回true，否则false
	 * 
	 * @param ticket
	 * @return
	 */
	public boolean exists(String ticket) {
		if (redis.exists(ticket)) {
			return true;
		}
		return false;
	}

}
