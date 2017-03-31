package cn.cjp.logger.model;

import java.util.Date;

import org.bson.Document;
import org.springframework.util.StringUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * 函数调用模型
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

	/**
	 * 总运行时长
	 */
	private long period = 0;

	/**
	 * 平均运行时长
	 */
	private long avgPeriod;

	/**
	 * 最大运行时长
	 */
	private long maxPeriod;

	/**
	 * 总调用次数
	 */
	private int calledTimes = 0;

	/**
	 * 返回行数，默认为1
	 */
	private int returnLineNum = 1;

	/**
	 * 平均返回行数，默认为1
	 */
	private int avgReturnLineNum = 1;

	/**
	 * 最大返回行数，默认为1
	 */
	private int maxReturnLineNum = 1;

	private Date time = new Date();

	private String remarks;

	public static BeanInspectorModel newInstance(String className, String method, String remarks) {
		BeanInspectorModel inspectorModel = new BeanInspectorModel();
		inspectorModel.setCalledTimes(0);
		inspectorModel.setClazz(className);
		inspectorModel.setMethod(method);
		inspectorModel.setRemarks(remarks);
		return inspectorModel;
	}

	/**
	 * 
	 * @return class.method(..)
	 */
	public String toFullName() {
		return this.getClazz() + "." + this.getMethod();
	}

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

	/**
	 * 合并<br>
	 * 
	 * @param branch
	 */
	public void merge(BeanInspectorModel branch) {
		BeanInspectorModel root = this;
		long period = root.getPeriod();
		long maxPeriod = root.getMaxPeriod();

		int maxReturnLineNum = root.getMaxReturnLineNum();
		int returnLineNum = root.getReturnLineNum();

		int calledTimes = root.getCalledTimes();
		root.setCalledTimes(calledTimes + branch.getCalledTimes());

		if (branch.getPeriod() > maxPeriod) {
			root.setMaxPeriod(branch.getPeriod());
		}
		root.setPeriod(period + branch.getPeriod());
		if (root.getCalledTimes() == 0) {
			root.setAvgPeriod(root.getPeriod() / 1);
		} else {
			root.setAvgPeriod(root.getPeriod() / root.getCalledTimes());
		}

		if (branch.getReturnLineNum() > maxReturnLineNum) {
			root.setMaxReturnLineNum(branch.getReturnLineNum());
		}
		root.setReturnLineNum(returnLineNum + branch.getReturnLineNum());
		root.setAvgReturnLineNum(root.getReturnLineNum() / root.getCalledTimes());
	}

	/**
	 * 合并
	 * 
	 * @param branchPeriod
	 *            分支的运行时间
	 * @param branchReturnLineNum
	 *            分支的返回行数
	 */
	public void mergeValue(long branchPeriod, int branchReturnLineNum) {
		BeanInspectorModel root = this;
		long period = root.getPeriod();
		long maxPeriod = root.getMaxPeriod();

		int maxReturnLineNum = root.getMaxReturnLineNum();
		int returnLineNum = root.getReturnLineNum();

		int calledTimes = root.getCalledTimes();
		root.setCalledTimes(calledTimes + 1);

		if (branchPeriod > maxPeriod) {
			root.setMaxPeriod(branchPeriod);
		}
		root.setPeriod(period + branchPeriod);
		root.setAvgPeriod(root.getPeriod() / root.getCalledTimes());

		if (branchReturnLineNum > maxReturnLineNum) {
			root.setMaxReturnLineNum(branchReturnLineNum);
		}
		root.setReturnLineNum(returnLineNum + branchReturnLineNum);
		root.setAvgReturnLineNum(root.getReturnLineNum() / root.getCalledTimes());
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

	public int getCalledTimes() {
		return calledTimes;
	}

	public void setCalledTimes(int calledTimes) {
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
		model.setCalledTimes(null == dbo.get(CALLEDTIMES) ? 0 : (int) dbo.get(CALLEDTIMES));
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

	public long getMaxPeriod() {
		return maxPeriod;
	}

	public void setMaxPeriod(long maxPeriod) {
		this.maxPeriod = maxPeriod;
	}

	public int getAvgReturnLineNum() {
		return avgReturnLineNum;
	}

	public void setAvgReturnLineNum(int avgReturnLineNum) {
		this.avgReturnLineNum = avgReturnLineNum;
	}

	public int getMaxReturnLineNum() {
		return maxReturnLineNum;
	}

	public void setMaxReturnLineNum(int maxReturnLineNum) {
		this.maxReturnLineNum = maxReturnLineNum;
	}

}
