package cn.cjp.logger.service;

import org.apache.log4j.Logger;

/**
 * 运行Application，然后执行 main()，最后在 log 列表页显示下面的“日志”
 * 
 * @author Jinpeng Cui
 *
 */
public class LoggerTest {

	static Logger VISIT = Logger.getLogger("visit");

	static Logger logger = Logger.getLogger(LoggerTest.class);

	public static void main(String[] args) throws Throwable {
		VISIT.info("visit info");
		logger.info("info");
		logger.warn("warn");
		logger.error("error");
	}

}
