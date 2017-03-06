package cn.cjp.logger.web;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import cn.cjp.logger.redis.RedisDao;

@Component
public class SessionManager {

	public static final int SECONDS_SEVEN_DAY = 7 * 24 * 3600;

	public static final String TOKEN_MANAGER_LOGIN = "token.manager.login";

	public static final String USER_ID_KEY = "USER_ID";

	public static final String USERNAME_KEY = "USERNAME";

	public static final String TOKEN = "TOKEN";

	public static final String IS_LOGIN = "IS_LOGIN";

	public static final int NOT_LOGIN_UID = 0;

	@Resource(name = "enableRedis")
	RedisDao jedis;

	private String generateKey(String token) {
		return TOKEN_MANAGER_LOGIN + "." + token;
	}

	public void setToken(String token) {
		jedis.hset(this.generateKey(token), TOKEN, token);
		jedis.expire(this.generateKey(token), SECONDS_SEVEN_DAY);
	}

	/**
	 * 
	 * @param token
	 * @param userId
	 * @param username
	 */
	public void set(HttpServletResponse response, String token, String userId, String username) {
		Cookie cookieToken = new Cookie(TOKEN, token);
		cookieToken.setMaxAge(SECONDS_SEVEN_DAY);
		cookieToken.setPath("/");
		response.addCookie(cookieToken);

		Cookie cookieUserId = new Cookie(USER_ID_KEY, userId);
		cookieUserId.setMaxAge(SECONDS_SEVEN_DAY);
		cookieUserId.setPath("/");
		response.addCookie(cookieUserId);

		Cookie cookieUsername = new Cookie(USERNAME_KEY, username);
		cookieUsername.setMaxAge(SECONDS_SEVEN_DAY);
		cookieUsername.setPath("/");
		response.addCookie(cookieUsername);

		jedis.hset(this.generateKey(token), TOKEN, token);
		jedis.expire(this.generateKey(token), SECONDS_SEVEN_DAY);
		jedis.hset(this.generateKey(token), USER_ID_KEY, userId);
		jedis.hset(this.generateKey(token), USERNAME_KEY, username);
		jedis.expire(this.generateKey(token), SECONDS_SEVEN_DAY);
	}

	public void removeToken(String token) {
		jedis.del(TOKEN_MANAGER_LOGIN + "." + token);
	}

	public boolean existToken(String token) {
		return jedis.exists(this.generateKey(token));
	}

}
