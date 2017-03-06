package cn.cjp.logger.util;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

import cn.cjp.utils.StringUtil;

public class RequestUtil {

	/**
	 * 禁止出现数组
	 * 
	 * @param request
	 * @return
	 */
	public static Map<String, String> getRequestParamMap(HttpServletRequest request) {
		Map<String, String> params = new HashMap<String, String>();
		for (String key : request.getParameterMap().keySet()) {
			for (String value : request.getParameterValues(key)) {
				params.put(key, value);
			}
		}
		return params;
	}

	/**
	 * 获取请求全路径（带参数）
	 * 
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 *             nothing happen
	 */
	public static String getFullRequestURL(ServletRequest request) {
		StringBuilder url = new StringBuilder();
		url.append(((HttpServletRequest) request).getRequestURI().toString() + "?");
		url.append(getRequestParamString((HttpServletRequest) request));
		return url.toString();
	}

	/**
	 * 获取请求参数字符串
	 * 
	 * @param request
	 * @return
	 */
	public static String getRequestParamString(HttpServletRequest request) {
		StringBuilder paramsStrBuilder = new StringBuilder();
		for (String key : request.getParameterMap().keySet()) {
			for (String value : request.getParameterValues(key)) {
				paramsStrBuilder.append(key + '=' + HtmlUtils.htmlEscape(value, "UTF-8") + '&');
			}
		}
		return paramsStrBuilder.toString();
	}

	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	/**
	 * <p>
	 * ? -> %3f
	 * <p>
	 * & -> %26
	 * <p>
	 * = -> %3d
	 * 
	 * @param request
	 * @return
	 */
	public static String encodeQueryURI(HttpServletRequest request) {
		String uri = request.getRequestURI();
		Map<String, String[]> params = request.getParameterMap();
		uri += "%3f";
		for (String key : params.keySet()) {
			for (String value : params.get(key)) {
				uri += key + "%3d" + value + "%26";
			}
		}
		return uri;
	}
	
	/**
	 * ? -> %3f
	 * @param request
	 * @return a=1&b=2% like
	 */
	public static String encodeParameter(HttpServletRequest request) {
		StringBuilder s = new StringBuilder();
		Map<String, String[]> params = request.getParameterMap();
		for (String key : params.keySet()) {
			for (String value : params.get(key)) {
				s.append(key + "%3d" + value + "%26");
			}
		}
		return s.toString();
	}

	/**
	 * 
	 * @param request
	 * @param name
	 * @return default is null
	 */
	public static Integer getInt(ServletRequest request, String name) {
		String value = request.getParameter(name);
		Integer intValue = null;
		try {
			intValue = Integer.parseInt(value);
		} catch (NumberFormatException e) {
		}
		return intValue;
	}

	public static String get(ServletRequest request, String name, String defaultValue) {
		String value = request.getParameter(name);
		if (value == null) {
			return defaultValue;
		}
		return value;
	}

	public static Boolean getBoolean(ServletRequest request, String name, boolean defaultValue) {
		String value = request.getParameter(name);
		if (value != null) {
			if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("1")) {
				return true;
			} else if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("0")) {
				return false;
			}
		}
		return defaultValue;
	}

	public static Long getLong(HttpServletRequest request, String name) {
		String value = request.getParameter(name);
		Long longValue = null;
		try {
			longValue = Long.parseLong(value);
		} catch (NumberFormatException e) {
		}
		return longValue;
	}

	public static float getFloat(HttpServletRequest request, String name, float defaultValue) {
		String value = request.getParameter(name);
		float floatValue = 0L;
		try {
			floatValue = Float.parseFloat(value);
		} catch (NumberFormatException | NullPointerException e) {
			return defaultValue;
		}
		return floatValue;
	}

	public static Integer[] getIntArray(HttpServletRequest request, String name) {
		String str = request.getParameter(name);
		if (StringUtils.isEmpty(str)) {
			return new Integer[0];
		}

		Integer[] nums = StringUtil.split(str, ",");
		return nums;
	}

}
