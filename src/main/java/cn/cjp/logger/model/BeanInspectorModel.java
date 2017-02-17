package cn.cjp.logger.model;

import java.util.Date;

import org.bson.Document;
import org.springframework.util.StringUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * 实体检测实体
 * 
 * @author Jinpeng Cui
 *
 */
public class BeanInspectorModel extends BaseModel implements AbstractModel {

	public static final String COLLECTION_NAME = "bean_inspector";

	public static final String CLASS = "clazz";
	public static final String METHOD = "method";
	public static final String PERIOD = "period";
	public static final String AVGPERIOD = "avgPeriod";
	public static final String CALLEDTIMES = "calledTimes";
	public static final String RETURNLINENUM = "returnLineNum";
	public static final String TIME = "time";
	public static final String REMARKS = "remarks";

	private String clazz;

	private String method;

	private long period = 0;

	private long avgPeriod;

	private long calledTimes = 1L;

	/**
	 * 返回行数，默认为1
	 */
	private int returnLineNum = 1;

	private Date time = new Date();

	private String remarks;

	@Override
	public DBObject toDBObject() {
		BasicDBObject basicDBObject = new BasicDBObject();
		if (!StringUtils.isEmpty(getId())) {
			basicDBObject.put(ID, getId());
		}
		basicDBObject.put(CLASS, clazz);
		basicDBObject.put(METHOD, method);
		basicDBObject.put(PERIOD, period);
		basicDBObject.put(CALLEDTIMES, calledTimes);
		basicDBObject.put(AVGPERIOD, avgPeriod);
		basicDBObject.put(RETURNLINENUM, returnLineNum);
		basicDBObject.put(TIME, time);
		basicDBObject.put(REMARKS, remarks);
		return basicDBObject;
	}

	public Document toDoc() {
		Document basicDBObject = new Document();
		if (!StringUtils.isEmpty(getId())) {
			basicDBObject.put(ID, getId());
		}
		basicDBObject.put(CLASS, clazz);
		basicDBObject.put(METHOD, method);
		basicDBObject.put(PERIOD, period);
		basicDBObject.put(CALLEDTIMES, calledTimes);
		basicDBObject.put(AVGPERIOD, avgPeriod);
		basicDBObject.put(RETURNLINENUM, returnLineNum);
		basicDBObject.put(TIME, time);
		basicDBObject.put(REMARKS, remarks);
		return basicDBObject;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public long getPeriod() {
		return period;
	}

	public void setPeriod(long period) {
		this.period = period;
	}

	public long getCalledTimes() {
		return calledTimes;
	}

	public void setCalledTimes(long calledTimes) {
		this.calledTimes = calledTimes;
	}

	public int getReturnLineNum() {
		return returnLineNum;
	}

	public void setReturnLineNum(int returnLineNum) {
		this.returnLineNum = returnLineNum;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public static BeanInspectorModel fromDBObject(DBObject dbo) {
		BeanInspectorModel model = new BeanInspectorModel();
		model.setCalledTimes(null == dbo.get(CALLEDTIMES) ? 0L : (long) dbo.get(CALLEDTIMES));
		model.setClazz(null == dbo.get(CLASS) ? "" : (String) dbo.get(CLASS));
		if (null != dbo.get(ID)) {
			model.setId(dbo.get(ID).toString());
		}
		model.setMethod(null == dbo.get(METHOD) ? "" : (String) dbo.get(METHOD));
		model.setPeriod(null == dbo.get(PERIOD) ? 0L : (long) dbo.get(PERIOD));
		model.setReturnLineNum(null == dbo.get(RETURNLINENUM) ? 0 : (int) dbo.get(RETURNLINENUM));
		model.setAvgPeriod(null == dbo.get(AVGPERIOD) ? 0L : (long) dbo.get(AVGPERIOD));
		model.setTime(null == dbo.get(TIME) ? new Date() : (Date) dbo.get(TIME));
		model.setRemarks(null == dbo.get(REMARKS) ? "" : (String) dbo.get(REMARKS));
		return model;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public long getAvgPeriod() {
		return avgPeriod;
	}

	public void setAvgPeriod(long avgPeriod) {
		this.avgPeriod = avgPeriod;
	}

	public static BeanInspectorModel fromDoc(Document dbo) {
		BeanInspectorModel model = new BeanInspectorModel();
		model.setCalledTimes(null == dbo.get(CALLEDTIMES) ? 1 : (Integer) dbo.get(CALLEDTIMES));
		model.setClazz(null == dbo.get(CLASS) ? "" : (String) dbo.get(CLASS));
		if (null != dbo.get(ID)) {
			model.setId(dbo.get(ID).toString());
		}
		model.setMethod(null == dbo.get(METHOD) ? "" : (String) dbo.get(METHOD));
		model.setPeriod(null == dbo.get(PERIOD) ? 0L : (long) dbo.get(PERIOD));
		model.setReturnLineNum(null == dbo.get(RETURNLINENUM) ? 0 : (int) dbo.get(RETURNLINENUM));
		model.setAvgPeriod(null == dbo.get(AVGPERIOD) ? 0L : (long) dbo.get(AVGPERIOD));
		model.setTime(null == dbo.get(TIME) ? new Date() : (Date) dbo.get(TIME));
		model.setRemarks(null == dbo.get(REMARKS) ? "" : (String) dbo.get(REMARKS));
		return model;
	}

}
