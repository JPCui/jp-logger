package cn.cjp.logger.model;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import org.bson.Document;
import org.bson.types.BasicBSONList;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import cn.cjp.logger.util.JacksonUtil;

/**
 * 日志基本信息
 * 
 * @author JinPeng Cui
 * 
 */
public class Log implements AbstractModel {

	public static String version = "v20160428";

	private String id;

	private Date time = new Date();

	private String name = "info";

	private String level = "level";

	private String thread = Thread.currentThread().getName();

	private String message = "message";

	private String method = "method";

	private String clazz = getClass().getName();

	private String line = "0";

	/**
	 * 发布日志的主机
	 */
	private Host host;

	private Host client;

	/**
	 * @deprecated 允许为空，需要修改
	 */
	private List<Throwable> throwables = new ArrayList<>();

	public Log() {
	}

	public static Log parse(LoggingEvent event) {
		Log log = new Log();
		log.setClazz(event.getLoggerName());
		log.setLevel(event.getLevel().toString());
		log.setLine(event.getLocationInformation().getLineNumber());
		log.setMessage(event.getMessage() == null ? "" : event.getMessage().toString());
		log.setMethod(event.getLocationInformation().getMethodName());
		log.setThread(event.getThreadName());
		ThrowableInformation throwInfo = event.getThrowableInformation();
		if (throwInfo != null) {
			List<Throwable> ts = new ArrayList<>();
			java.lang.Throwable throwable = throwInfo.getThrowable();
			while (throwable != null) {
				Throwable t = Throwable.parse(throwable);
				ts.add(t);
				throwable = throwable.getCause();
			}
			log.setThrowables(ts);
		}
		log.setTime(new Date(event.getTimeStamp()));
		try {
			log.setServer(Host.instance());
		} catch (UnknownHostException e) {
			log.setServer(new Host("127.0.0.1", "unknown host"));
		}
		return log;
	}

	public Document toDoc() {
		Document dbo = new Document();
		dbo.append("version", version);
		dbo.append("time", time);
		dbo.append("name", name);
		dbo.append("level", level);
		dbo.append("thread", thread);
		dbo.append("message", message);
		dbo.append("method", method);
		dbo.append("clazz", clazz);
		dbo.append("line", line);
		if (throwables != null) {
			BasicDBList dbl = new BasicDBList();
			for (Throwable throwable : throwables) {
				dbl.add(throwable.toDBObject());
			}
			dbo.append("throwables", dbl);
		}
		if (this.host != null) {
			dbo.append("host", this.host.toDBObject());
		}
		return dbo;
	}

	public static Log fromDoc(Document dbo) {
		Object version = dbo.get("version");
		if (version == null || !version.toString().equals(Log.version)) {
			// return null;
		}
		Log log = new Log();
		log.setId(dbo.get("_id").toString());
		log.setTime((Date) dbo.get("time"));
		log.setName(dbo.getString("Name"));
		log.setLevel((String) dbo.get("level"));
		log.setThread((String) dbo.get("thread"));
		log.setMethod((String) dbo.get("method"));
		log.setMessage((String) dbo.get("message"));
		log.setLine((String) dbo.get("line"));
		// 调用类
		log.setClazz((String) dbo.get("clazz"));

		/*
		 * 获取异常
		 */
		List<Throwable> throwables = log.getThrowables();
		if (throwables == null) {
			throwables = new ArrayList<>();
		}
		@SuppressWarnings("unchecked")
		List<Document> bsonList = (List<Document>) dbo.get("throwables");
		if (bsonList != null) {
			Iterator<Document> throwablesIter = bsonList.iterator();
			while (throwablesIter.hasNext()) {
				Document dbo4Throwables = (Document) throwablesIter.next();
				Throwable throwable = Throwable.fromDoc(dbo4Throwables);
				throwables.add(throwable);
			}
			log.setThrowables(throwables);
		}
		log.setServer(Host.fromDoc(dbo));
		return log;
	}

	public static Log fromDBObject(DBObject dbo) {
		Object version = dbo.get("version");
		if (version == null || !version.toString().equals(Log.version)) {
			// return null;
		}
		Log log = new Log();
		log.setId(dbo.get("_id").toString());
		log.setTime((Date) dbo.get("time"));
		log.setLevel((String) dbo.get("level"));
		log.setName((String) dbo.get("level"));
		log.setThread((String) dbo.get("thread"));
		log.setMethod((String) dbo.get("method"));
		log.setMessage((String) dbo.get("message"));
		log.setLine((String) dbo.get("line"));
		// 调用类
		log.setClazz((String) dbo.get("clazz"));

		/*
		 * 获取异常
		 */
		List<Throwable> throwables = log.getThrowables();
		if (throwables == null) {
			throwables = new ArrayList<>();
		}
		BasicBSONList bsonList = (BasicBSONList) dbo.get("throwables");
		if (bsonList != null) {
			Iterator<Object> throwablesIter = bsonList.iterator();
			while (throwablesIter.hasNext()) {
				DBObject dbo4Throwables = (DBObject) throwablesIter.next();
				Throwable throwable = Throwable.fromDBObject(dbo4Throwables);
				throwables.add(throwable);
			}
			log.setThrowables(throwables);
		}
		log.setServer(Host.fromDBObject(dbo));
		return log;
	}

	public BasicDBObject toDBObject() {
		return toDBObject(true);
	}

	public BasicDBObject toDBObject(boolean needId) {
		BasicDBObject dbo = new BasicDBObject();
		if (needId) {
			dbo.append("_id", id);
		}
		dbo.append("version", version);
		dbo.append("time", time);
		dbo.append("name", name);
		dbo.append("level", level);
		dbo.append("thread", thread);
		dbo.append("message", message);
		dbo.append("method", method);
		dbo.append("clazz", clazz);
		dbo.append("line", line);
		if (throwables != null) {
			BasicDBList dbl = new BasicDBList();
			for (Throwable throwable : throwables) {
				dbl.add(throwable.toDBObject());
			}
			dbo.append("throwables", dbl);
		}
		if (this.host != null) {
			dbo.append("host", this.host.toDBObject());
		}
		return dbo;
	}

	public String toString() {
		return JacksonUtil.toJson(this);
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getThread() {
		return thread;
	}

	public void setThread(String thread) {
		this.thread = thread;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public List<Throwable> getThrowables() {
		return throwables;
	}

	public void setThrowables(List<Throwable> throwables) {
		this.throwables = throwables;
	}

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getVersion() {
		return Log.version;
	}

	public void setVersion(String version) {
		Log.version = version;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Host getServer() {
		return host;
	}

	public void setServer(Host host) {
		this.host = host;
	}

	public Host getClient() {
		return client;
	}

	public void setClient(Host client) {
		this.client = client;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
