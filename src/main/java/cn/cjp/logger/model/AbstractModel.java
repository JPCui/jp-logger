package cn.cjp.logger.model;

import com.mongodb.DBObject;

public interface AbstractModel {

	public DBObject toDBObject();
	
}
