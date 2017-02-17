package cn.cjp.logger.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bson.Document;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import cn.cjp.logger.util.JacksonUtil;

/**
 * 打印的异常信息
 * 
 * @author JinPeng Cui
 * 
 */
public class Throwable implements AbstractModel {

	private String clazz;

	private String message;

	/**
	 * @deprecated 允许为空，需要修改
	 */
	private List<StackTrace> stackTraces = new ArrayList<>();

	public DBObject toDBObject() {
		BasicDBObject dbo = new BasicDBObject();
		dbo.append("message", message);
		dbo.append("clazz", clazz);
		if (stackTraces != null) {
			BasicDBList dbl = new BasicDBList();
			for (StackTrace trace : this.stackTraces) {
				dbl.add(trace.toDBObject());
			}
			dbo.append("stackTraces", dbl);
		}
		return dbo;
	}

	public static Throwable fromDBObject(DBObject dbo) {
		Throwable throwable = new Throwable();
		throwable.setClazz((String) dbo.get("clazz"));
		String message = (String) dbo.get("message");
		throwable.setMessage(message);

		List<StackTrace> stackTraces = throwable.getStackTraces();
		BasicDBList stackTraceList = (BasicDBList) dbo.get("stackTraces");
		Iterator<Object> stackTraceIter = stackTraceList.iterator();
		while (stackTraceIter.hasNext()) {
			DBObject dbo4StackTrace = (DBObject) stackTraceIter.next();
			StackTrace stackTrace = StackTrace.fromDBObject(dbo4StackTrace);
			stackTraces.add(stackTrace);
		}
		throwable.setStackTraces(stackTraces);
		return throwable;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<StackTrace> getStackTraces() {
		return stackTraces;
	}

	public void setStackTraces(List<StackTrace> stackTraces) {
		this.stackTraces = stackTraces;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public static Throwable parse(java.lang.Throwable throwable) {
		Throwable t = new Throwable();
		t.setClazz(throwable.getClass().getName());
		t.setMessage(throwable.getMessage());
		t.setStackTraces(StackTrace.parseArray(throwable.getStackTrace()));
		return t;
	}

	public String toString() {
		return JacksonUtil.toJson(this);
	}

	public static Throwable fromDoc(Document dbo) {
		Throwable throwable = new Throwable();
		throwable.setClazz((String) dbo.get("clazz"));
		String message = (String) dbo.get("message");
		throwable.setMessage(message);

		List<StackTrace> stackTraces = throwable.getStackTraces();
		@SuppressWarnings("unchecked")
		List<Document> stackTraceList = (List<Document>) dbo.get("stackTraces", List.class);
		Iterator<Document> stackTraceIter = stackTraceList.iterator();
		while (stackTraceIter.hasNext()) {
			Document dbo4StackTrace = (Document) stackTraceIter.next();
			StackTrace stackTrace = StackTrace.fromDoc(dbo4StackTrace);
			stackTraces.add(stackTrace);
		}
		throwable.setStackTraces(stackTraces);
		return throwable;
	}

}
