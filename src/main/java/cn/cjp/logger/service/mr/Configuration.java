package cn.cjp.logger.service.mr;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import cn.cjp.utils.Logger;
import cn.cjp.utils.PropertiesUtil;

public class Configuration {

	private static final Logger LOGGER = Logger.getLogger(Configuration.class);

	private static final String KEY_PROJECTS = "mongo.mr.projects";

	private static final String KEY_PROJECT_SCRIPT_PATH = "mongo.mr.project.script.path";

	private static final Map<String, Boolean> projects = new HashMap<>();

	/**
	 * 所有的 map 脚本, key: project
	 */
	private static final Map<String, String> maps = new HashMap<>();

	/**
	 * 所有的 reduce 脚本, key: project
	 */
	private static final Map<String, String> reduces = new HashMap<>();

	static {
		ClassLoader classLoader = Configuration.class.getClassLoader();
		try {
			PropertiesUtil props = new PropertiesUtil("mongo.mr.properties");
			Map<String, String> propsMap = props.getParams();
			String projects = propsMap.get(KEY_PROJECTS);
			String projectScriptPath = propsMap.get(KEY_PROJECT_SCRIPT_PATH);
			LOGGER.info(projects);

			String[] projectsArr = projects.split(",");

			for (String project : projectsArr) {
				Configuration.projects.put(project, true);

				File mapFile = new File(
						classLoader.getResource(String.format("%s%s.map", projectScriptPath, project)).getFile());
				File reduceFile = new File(
						classLoader.getResource(String.format("%s%s.reduce", projectScriptPath, project)).getFile());

				String jsMap = FileUtils.readFileToString(mapFile);
				String jsReduce = FileUtils.readFileToString(reduceFile);

				maps.put(project, jsMap);
				reduces.put(project, jsReduce);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Configuration() {
	}

	public static boolean exists(String project) {
		return projects.containsKey(project);
	}

	public static String getMap(String project) {
		String script = maps.get(project);
		if (script == null) {
			throw new NullPointerException("Script not exists.");
		}
		return script;
	}

	public static String getReduce(String project) {
		String script = reduces.get(project);
		if (script == null) {
			throw new NullPointerException("Script not exists.");
		}
		return script;
	}

}
