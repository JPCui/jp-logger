package cn.cjp.logger.model;

import cn.cjp.logger.util.JacksonUtil;

public class BaseModel {

	public static final String ID = "_id";

	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String toString() {
		return JacksonUtil.toJson(this);
	}

}
