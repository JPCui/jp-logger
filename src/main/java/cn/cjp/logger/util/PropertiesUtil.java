package cn.cjp.logger.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

/**
 * @author Cui 资源文件工具类（properties）
 */
public class PropertiesUtil {

	private Map<String, String> params = new HashMap<>();

	public static String[] getNullPropertyNames(Object source) {
		final BeanWrapper src = new BeanWrapperImpl(source);
		java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

		Set<String> emptyNames = new HashSet<String>();
		for (java.beans.PropertyDescriptor pd : pds) {
			Object srcValue = src.getPropertyValue(pd.getName());
			if (srcValue == null)
				emptyNames.add(pd.getName());
		}
		String[] result = new String[emptyNames.size()];
		return emptyNames.toArray(result);
	}

	public PropertiesUtil(InputStream stream) throws IOException {
		load(stream);
	}

	/**
	 * 
	 * @param resource
	 *            resource in classpath
	 * @throws Exception
	 */
	public PropertiesUtil(String resource) throws IOException {
		System.err.println(String.format("Load %s", resource));
		String stripped = resource.startsWith("/") ? resource.substring(1) : resource;

		InputStream stream = null;
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader != null) {
			stream = classLoader.getResourceAsStream(stripped);
		}
		if (stream == null) {
			stream = Thread.class.getResourceAsStream(resource);
		}
		if (stream == null) {
			stream = Thread.class.getClassLoader().getResourceAsStream(stripped);
		}
		if (stream == null) {
			final String err = resource + " not found";
			throw new IOException(err);
		}

		load(stream);

	}

	private void load(InputStream stream) throws IOException {
		Properties prop = new Properties();
		prop.load(new InputStreamReader(stream, "UTF-8"));
		System.err.println(String.format("Loaded [%s]", prop));
		for (Object key : prop.keySet()) {
			params.put(key.toString(), prop.getProperty(key.toString()));
		}
	}

	public String getValue(String key) {
		return params.get(key);
	}

	public int getInt(String key, int defaultValue) {
		int value = defaultValue;
		try {
			String s = params.get(key);
			if (s != null) {
				value = Integer.parseInt(params.get(key));
			}
		} catch (NumberFormatException e) {
		}
		return value;
	}

	public long getLong(String key, long defaultValue) {
		long value = defaultValue;
		try {
			String s = params.get(key);
			if (s != null) {
				value = Long.parseLong(params.get(key));
			}
		} catch (NumberFormatException e) {
		}
		return value;
	}

	public boolean getBoolean(String key, boolean defaultValue) {
		boolean value = defaultValue;
		String s = params.get(key);
		if (s != null) {
			value = Boolean.parseBoolean(params.get(key));
		}
		return value;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public static void main(String[] args) throws IOException {
		PropertiesUtil util = null;
		try (FileInputStream fis = new FileInputStream(
				new File("D:/Library/Tomcat/webapps/config/weixin.properties"))) {
			util = new PropertiesUtil(fis);
			System.out.println(util.params);
		}
	}
}