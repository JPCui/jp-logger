package cn.cjp.logger.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import cn.cjp.utils.Logger;

public class IPUtil {

	private static final Logger LOGGER = Logger.getLogger(IPUtil.class);

	/**
	 * 
	 * @param ip
	 * @return province
	 */
	public static String getCityByIp(String ip) {
		String api = "http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json&ip=" + ip;
		Connection conn = Jsoup.connect(api).ignoreContentType(true).ignoreHttpErrors(true);
		conn.header("Content-Type", "application/json; charset=utf-8");
		try {
			String text = conn.get().text();
			if (text != null && text.startsWith("{")) {
				Map<String, Object> json = JacksonUtil.fromJsonToMap(text, String.class, Object.class);
				return json.get("province").toString();
			}
		} catch (IOException e) {
			LOGGER.warn(e.toString(), e);
		}
		return null;
	}

	public static Map<String, String> getCityByIps(String[] ips) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Query ips(%d) info.", ips.length));
		}

		final ExecutorService service = Executors.newCachedThreadPool();
		Map<String, Future<String>> futures = new HashMap<>();
		for (String ip : ips) {
			Future<String> future = service.submit(Task.newInstance(ip));
			futures.put(ip, future);
		}

		Map<String, String> infoMap = new HashMap<>();
		for (String ip : futures.keySet()) {
			Future<String> future = futures.get(ip);
			try {
				String info = future.get(5, TimeUnit.SECONDS);
				infoMap.put(ip, info);
			} catch (InterruptedException | ExecutionException | TimeoutException e) {
				LOGGER.error(e, e);
			}
		}
		service.shutdown();
		return infoMap;
	}

	public static void main(String[] args) {
		String[] ips = { "202.196.35.35", "202.196.35.36", "202.196.35.37", "202.196.35.37", "101.200.143.36",
				"101.201.143.36" };
		Object r = getCityByIps(ips);
		LOGGER.info(r);

		// for (String ip : ips) {
		// LOGGER.info(getCityByIp(ip));
		// }
	}

}

class Task implements Callable<String> {

	String ip;

	private Task() {
	}

	public static Task newInstance(String ip) {
		Task task = new Task();
		task.ip = ip;
		return task;
	}

	@Override
	public String call() throws Exception {
		return IPUtil.getCityByIp(ip);
	}

	public String getIp() {
		return ip;
	}

}