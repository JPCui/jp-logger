package cn.cjp.logger.util;

import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

/**
 * wrapper org.apache.log4j.Logger
 * 
 * @author Jinpeng Cui
 *
 */
public class Logger {

	public static final boolean enable = true;

	private static final String FQCN = Logger.class.getName();

	private static org.apache.log4j.Logger proxy;

	public Logger(final String name) {
		proxy = org.apache.log4j.Logger.getLogger(name);
	}

	public static Logger getLogger(final Class<?> clazz) {
		return getLogger(clazz.getName());
	}

	public static Logger getLogger(String name) {
		return new Logger(name);
	}

	public void log(Level level, Object message) {
		if (isEnabledFor(level)) {
			forceLog(level, message);
		}
	}

	public void error(Object message) {
		if (isErrorEnable()) {
			forceLog(Level.ERROR, message);
		}
	}

	public void error(Object message, Throwable t) {
		if (isErrorEnable()) {
			forceLog(Level.ERROR, message, t);
		}
	}

	public void warn(Object message) {
		if (isEnabledFor(Level.WARN)) {
			forceLog(Level.WARN, message);
		}
	}

	public void info(Object message) {
		if (isInfoEnabled()) {
			forceLog(Level.INFO, message);
		}
	}

	public void debug(Object message) {
		if (isDebugEnabled()) {
			forceLog(Level.DEBUG, message);
		}
	}

	private void forceLog(Level level, Object message) {
		this.forceLog(level, message, null);
	}

	private void forceLog(Level level, Object message, Throwable t) {
		proxy.callAppenders(new LoggingEvent(FQCN, proxy, level, message, t));
	}

	public boolean isEnabledFor(Level level) {
		return enable && proxy.isEnabledFor(level);
	}

	public boolean isErrorEnable() {
		return isEnabledFor(Level.ERROR);
	}

	public boolean isInfoEnabled() {
		return isEnabledFor(Level.INFO);
	}

	public boolean isDebugEnabled() {
		return isEnabledFor(Level.DEBUG);
	}

}
