package cn.cjp.logger.web.interceptor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import cn.cjp.logger.redis.TicketService;
import cn.cjp.logger.util.RequestUtil;
import cn.cjp.logger.web.SessionManager;
import cn.cjp.utils.StringUtil;

/**
 * 登录拦截器
 * 
 * 
 */
@Component
public class LoginRequiredInterceptor extends HandlerInterceptorAdapter {

	private static Logger logger = Logger.getLogger(LoginRequiredInterceptor.class);

	/**
	 * 登录服务的 CONTEXT_PATH
	 */
	@Value("${login.server.contextPath}")
	String LOGIN_CONTEXT_PATH;

	@Value("${login.server.loginURI}")
	String LOGIN_URL;

	@Autowired
	SessionManager sessionManager;

	@Autowired
	TicketService ticketService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		response.setCharacterEncoding("UTF-8");
		request.setCharacterEncoding("UTF-8");

		if (isLocalRequest(request)) {
			return true;
		}

		// 根据票据登录
		if (!isInvalid(request)) {
			toLogin(request, response);
			return false;
		}

		Map<String, Object> userPrinciple = getUserPrincipleFromCookie(request);
		String token = (String) userPrinciple.get(SessionManager.TOKEN);
		int userId = (int) userPrinciple.get(SessionManager.USER_ID_KEY);
		String username = (String) userPrinciple.get(SessionManager.USERNAME_KEY);

		// 验证登录状态
		boolean isLogin = passed(userPrinciple, request);
		String requestUrl = request.getRequestURL() == null ? "" : request.getRequestURL().toString();

		// 非登录页面，未登陆状态禁止访问，重定向到登录，并设置参数r
		if (!isLogin) {
			String url = genCurrentUrl(request);
			if (!request.getRequestURI().startsWith(request.getContextPath() + LOGIN_URL)) {
				logger.debug("user not login, no permittion");
				if (requestUrl.endsWith(".json")) {
					StringBuilder builder = new StringBuilder();
					builder.append("{");
					builder.append("\"flag\":\"401\",");
					builder.append("\"msg\":\"未登录\",");
					builder.append(String.format("\"r\":\"%s\"", LOGIN_CONTEXT_PATH + LOGIN_URL + "?r=" + url));
					builder.append("}");
				} else {
					response.sendRedirect(LOGIN_CONTEXT_PATH + LOGIN_URL + "?r=" + url);
				}
				return false;
			}
			// 非登录状态，访问登录页面，直接通过
			else if (request.getRequestURI().startsWith(LOGIN_CONTEXT_PATH + LOGIN_URL)) {
				return true;
			}
		} else {
			// 已登录，当访问登录页面，自动重定向到首页
			if (request.getRequestURI().startsWith(request.getContextPath() + LOGIN_URL)) {
				String redirectUrl = request.getParameter("r");
				if (RequestMethod.POST.name().equalsIgnoreCase(request.getMethod())) {
					String referer = request.getHeader("Referer");
					response.sendRedirect(referer);
				} else {
					if (StringUtil.isEmpty(redirectUrl)) {
						response.sendRedirect(request.getContextPath() + "/admin/news/list");
					} else {
						response.sendRedirect(redirectUrl);
					}
				}
				logger.debug("user is already login. will redirect by 'r'");
				return false;
			}
			// 已登录，访问其他页面
			else {
				request.setAttribute(SessionManager.IS_LOGIN, isLogin);
				request.setAttribute(SessionManager.USER_ID_KEY, isLogin ? userId : SessionManager.NOT_LOGIN_UID);
				request.setAttribute(SessionManager.USERNAME_KEY, isLogin ? username : "");
				request.setAttribute(SessionManager.TOKEN, token);
				return true;
			}
		}
		return false;
	}

	private Map<String, Object> getUserPrincipleFromCookie(HttpServletRequest request) {
		Map<String, Object> userPrinciple = new HashMap<>();
		String userIdStr = null;
		String username = null;
		String token = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				String name = cookie.getName();
				String value = cookie.getValue();
				switch (name) {
				case SessionManager.USER_ID_KEY:
					userIdStr = value;
					break;
				case SessionManager.USERNAME_KEY:
					username = value;
					break;
				case SessionManager.TOKEN:
					token = value;
					break;
				default:
					break;
				}
			}
		}
		int userId = 0;
		try {
			userId = Integer.parseInt(userIdStr);
		} catch (Exception e) {
		}

		userPrinciple.put(SessionManager.TOKEN, token);
		userPrinciple.put(SessionManager.USER_ID_KEY, userId);
		userPrinciple.put(SessionManager.USERNAME_KEY, username);
		return userPrinciple;
	}

	private boolean passed(Map<String, Object> userPrinciple, HttpServletRequest request) {
		// if ("127.0.0.1".equals(RequestUtil.getIpAddr(request))) {
		// return true;
		// }
		if (isLogin(userPrinciple, request)) {
			return true;
		}
		return false;
	}

	private boolean isLogin(Map<String, Object> userPrinciple, HttpServletRequest request) {
		boolean isLogin = false;
		String token = (String) userPrinciple.get(SessionManager.TOKEN);
		int userId = (int) userPrinciple.get(SessionManager.USER_ID_KEY);

		// 根据token是否存在判断用户的登录状态
		if (userId > 0 && token != null && sessionManager.existToken(token)) {
			isLogin = true;
		}
		return isLogin;
	}

	private boolean isInvalid(HttpServletRequest request) {
		return isTicketInvalid(request);
	}

	private boolean isLocalRequest(HttpServletRequest request) {
		String clientIp = request.getRemoteAddr();
		String serverIp = request.getLocalAddr();
		return clientIp.equals(serverIp);
	}

	private boolean isTicketInvalid(HttpServletRequest request) {
		String ticket = RequestUtil.get(request, "ticket", null);
		if (null == ticket || ticketService.exists(ticket)) {
			return true;
		}
		return false;
	}

	private void toLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String url = genCurrentUrl(request);
		((HttpServletRequest) request).setAttribute("flag", 1);
		((HttpServletRequest) request).setAttribute("msg", "页面失效，请在管理端进行操作");
		response.sendRedirect(LOGIN_CONTEXT_PATH + LOGIN_URL + "?r=" + url);
	}

	private String genCurrentUrl(HttpServletRequest request) {
		String url = null;
		String r = request.getParameter("r");
		if (!StringUtil.isEmpty(r)) {
			url = r;
		} else {
			String method = request.getMethod();
			if (RequestMethod.POST.name().equals(method)) {
				url = request.getHeader("Referer");
			} else {
				url = "";
				url += request.getRequestURL().toString();
				url += "%3f";
				url += RequestUtil.encodeParameter(request);
			}
		}
		return url;
	}
}
