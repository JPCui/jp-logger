package cn.cjp.logger.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.QueryOperators;

import cn.cjp.logger.dao.mongo.MongoDao;
import cn.cjp.logger.model.Log;
import cn.cjp.utils.Page;

@Component("logService")
public class LogService {

	private final static Logger logger = Logger.getLogger(LogService.class);

	private final int default_page_size = 50;

	public static final String COLLECTION = "cjp.logger.{level}";

	public static String collection(String level) {
		return COLLECTION.replace("{level}", level);
	}

	@Autowired
	MongoDao mongoDao;

	/**
	 * 
	 * @param log
	 * @return the value of the _id of an upserted document
	 */
	public Object report(Log log) {
		if (logger.isDebugEnabled()) {
			logger.debug(log);
		}
		return mongoDao.getDB().getCollection(collection(log.getLevel())).insert(log.toDBObject()).getUpsertedId();
	}

	public Page findAllByTime(String level, String timeString, int pageNum) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = sdf.parse(timeString);
		BasicDBObject filter = new BasicDBObject();
		filter.put("time", new BasicDBObject(QueryOperators.LTE, date));

		List<Object> logs = new ArrayList<>();
		DB db = mongoDao.getDB(mongoDao.getDatabase());

		DBCollection dbc = db.getCollection(collection(level));
		DBCursor cursor = dbc.find(filter).skip((pageNum - 1) * default_page_size).sort(new BasicDBObject("time", -1))
				.limit(default_page_size);

		while (cursor.hasNext()) {
			DBObject dbo = cursor.next();
			Log log = Log.fromDBObject(dbo);
			logs.add(log);
		}

		Page page = new Page(pageNum, default_page_size, cursor.count(), logs);
		return page;
	}

	public Page findAll(String level, int pageNum) {

		List<Object> logs = new ArrayList<>();
		DB db = mongoDao.getDB(mongoDao.getDatabase());

		DBCollection dbc = db.getCollection(collection(level));
		DBCursor cursor = dbc.find().skip((pageNum - 1) * default_page_size).sort(new BasicDBObject("time", -1))
				.limit(default_page_size);

		while (cursor.hasNext()) {
			DBObject dbo = cursor.next();
			Log log = Log.fromDBObject(dbo);
			logs.add(log);
		}

		Page page = new Page(pageNum, default_page_size, (long) cursor.count(), logs);
		return page;
	}

}
