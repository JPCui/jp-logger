package cn.cjp.logger.model;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import cn.cjp.utils.JacksonUtil;

public class StackTrace {

	private String className;

	private String method;

	private int line;

	public static List<StackTrace> parseArray(StackTraceElement[] stackTraceElements) {
		List<StackTrace> stackTraces = null;
		if (stackTraceElements != null) {
			stackTraces = new ArrayList<>();
			for (int i = 0; i < stackTraceElements.length; i++) {
				StackTraceElement stackTraceElement = stackTraceElements[i];
				stackTraces.add(parse(stackTraceElement));
			}
		}
		return stackTraces;
	}

	public static StackTrace parse(StackTraceElement stackTraceElement) {
		StackTrace stackTrace = new StackTrace();
		stackTrace.setClassName(stackTraceElement.getClassName());
		stackTrace.setLine(stackTraceElement.getLineNumber());
		stackTrace.setMethod(stackTraceElement.getMethodName());
		return stackTrace;
	}

	public static StackTrace fromDBObject(DBObject dbo) {
		StackTrace stackTrace = new StackTrace();
		stackTrace.setClassName((String) dbo.get("className"));
		stackTrace.setLine((int) dbo.get("line"));
		stackTrace.setMethod((String) dbo.get("method"));
		return stackTrace;
	}

	public DBObject toDBObject() {
		BasicDBObject dbo = new BasicDBObject();
		dbo.append("className", className);
		dbo.append("method", method);
		dbo.append("line", line);
		return dbo;
	}

	public String toString() {
		return JacksonUtil.toJson(this);
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * @deprecated use {@link #getLine()} instead
	 * @return
	 */
	@Deprecated
	public int getLineNumber() {
		return line;
	}

	/**
	 * @deprecated use {@link #setLine(int)} instead
	 * @param lineNumber
	 */
	@Deprecated
	public void setLineNumber(int lineNumber) {
		this.line = lineNumber;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

}
