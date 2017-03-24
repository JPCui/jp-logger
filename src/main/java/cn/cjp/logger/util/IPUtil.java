package cn.cjp.logger.util;

import java.io.IOException;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import cn.cjp.utils.Logger;

public class IPUtil {

	private static final Logger LOGGER = Logger.getLogger(IPUtil.class);

	public static String getCityByIp(String ip) {
		String api = "http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json&ip=" + ip;
		Connection conn = Jsoup.connect(api).ignoreContentType(true).ignoreHttpErrors(true);
		conn.header("Content-Type", "application/json; charset=utf-8");
		try {
			String text = conn.get().text();
			Map<String, Object> json = JacksonUtil.fromJsonToMap(text, String.class, Object.class);
			return json.get("province").toString();
		} catch (IOException e) {
			LOGGER.warn(e.toString(), e);
		}
		return null;
	}

}
