package cn.cjp.logger.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.QueryOperators;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import cn.cjp.logger.model.Log;
import cn.cjp.logger.mongo.MongoDao;
import cn.cjp.logger.util.Page;

/**
 * 日志
 * 
 * @author Jinpeng Cui
 *
 */
@Component("logService")
@Configuration
@PropertySource(value = "classpath:/config.properties")
public class LogService {

	private final static Logger logger = Logger.getLogger(LogService.class);

	private final int default_page_size = 50;

	@Value("${config.collection.prefix}")
	String collectionPrefix;

	public String collection(String level) {
		return collectionPrefix + level;
	}

	@Autowired
	MongoDao mongoDao;

	public Object report(HttpServletRequest request) throws Exception {
		Map<String, Object> logMap = new HashMap<String, Object>();
		Enumeration<String> paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String paramName = paramNames.nextElement();
			logMap.put(paramName, request.getParameter(paramName));
		}
		if (!logMap.containsKey("level") || !logMap.containsKey("time")) {
			throw new Exception("level/time field not exist.");
		}
		if (logger.isDebugEnabled()) {
			logger.debug(logMap);
		}

		Document doc = new Document(logMap);
		mongoDao.getDB().getCollection(collection(logMap.get("level").toString())).insertOne(doc);
		return doc.get("_id");
	}

	/**
	 * 
	 * @param log
	 * @return the value of the _id of an upserted document
	 */
	public Object report(Log log) {
		if (logger.isDebugEnabled()) {
			logger.debug(log);
		}
		Document doc = log.toDoc();
		mongoDao.getDB().getCollection(collection(log.getLevel())).insertOne(doc);
		return doc.get("_id");
	}

	public Page findAll(String level, String timeString, String keyword, int pageNum) throws Exception {
		Date date = null;
		if (!StringUtils.isEmpty(timeString)) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			date = sdf.parse(timeString);
		}
		Document filter = new Document();
		if (date != null) {
			filter.put("time", new BasicDBObject(QueryOperators.LTE, date));
		}
		if (!StringUtils.isEmpty(keyword)) {
			filter.put("message", new BasicDBObject("$regex", keyword));
		}

		List<Object> logs = new ArrayList<>();
		MongoDatabase db = mongoDao.getDB(mongoDao.getDatabase());

		MongoCollection<Document> dbc = db.getCollection(level);
		MongoCursor<Document> cursor = null;
		if (filter.isEmpty()) {
			cursor = dbc.find().skip((pageNum - 1) * default_page_size).sort(new BasicDBObject("time", -1))
					.limit(default_page_size).iterator();
		} else {
			cursor = dbc.find(filter).skip((pageNum - 1) * default_page_size).sort(new BasicDBObject("time", -1))
					.limit(default_page_size).iterator();
		}

		while (cursor.hasNext()) {
			Document dbo = cursor.next();
			Log log = Log.fromDoc(dbo);
			logs.add(log);
		}

		Page page = new Page(pageNum, default_page_size, dbc.count(), logs);
		return page;
	}

	@Deprecated
	public Page findAllByTime(String level, String timeString, int pageNum) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = sdf.parse(timeString);
		BasicDBObject filter = new BasicDBObject();
		filter.put("time", new BasicDBObject(QueryOperators.LTE, date));

		List<Object> logs = new ArrayList<>();
		MongoDatabase db = mongoDao.getDB(mongoDao.getDatabase());

		MongoCollection<Document> dbc = db.getCollection(collection(level));
		FindIterable<Document> it = dbc.find(filter).skip((pageNum - 1) * default_page_size)
				.sort(new BasicDBObject("time", -1)).limit(default_page_size);
		MongoCursor<Document> cursor = it.iterator();

		while (cursor.hasNext()) {
			Document dbo = cursor.next();
			Log log = Log.fromDoc(dbo);
			logs.add(log);
		}

		Page page = new Page(pageNum, default_page_size, dbc.count(), logs);
		return page;
	}

	@Deprecated
	public Page findAll(String level, int pageNum) {

		List<Object> logs = new ArrayList<>();
		MongoDatabase db = mongoDao.getDB(mongoDao.getDatabase());

		MongoCollection<Document> dbc = db.getCollection(collection(level));
		FindIterable<Document> it = dbc.find().skip((pageNum - 1) * default_page_size)
				.sort(new BasicDBObject("time", -1)).limit(default_page_size);
		MongoCursor<Document> cursor = it.iterator();

		while (cursor.hasNext()) {
			Document dbo = cursor.next();
			Log log = Log.fromDoc(dbo);
			logs.add(log);
		}

		Page page = new Page(pageNum, default_page_size, (long) dbc.count(), logs);
		return page;
	}

	public Page findAllToMap(String level, int pageNum) {

		List<Object> logs = new ArrayList<>();
		MongoDatabase db = mongoDao.getDB(mongoDao.getDatabase());

		MongoCollection<Document> dbc = db.getCollection(collection(level));
		FindIterable<Document> it = dbc.find().skip((pageNum - 1) * default_page_size)
				.sort(new BasicDBObject("time", -1)).limit(default_page_size);
		MongoCursor<Document> cursor = it.iterator();

		while (cursor.hasNext()) {
			Document dbo = cursor.next();
			logs.add(dbo);
		}

		Page page = new Page(pageNum, default_page_size, (long) dbc.count(), logs);
		return page;
	}

	public Page findAllByTimeToMap(String level, String timeString, int pageNum) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = sdf.parse(timeString);
		BasicDBObject filter = new BasicDBObject();
		filter.put("time", new BasicDBObject(QueryOperators.LTE, date));

		List<Object> logs = new ArrayList<>();
		MongoDatabase db = mongoDao.getDB(mongoDao.getDatabase());

		MongoCollection<Document> dbc = db.getCollection(collection(level));
		MongoCursor<Document> cursor = dbc.find(filter).skip((pageNum - 1) * default_page_size)
				.sort(new BasicDBObject("time", -1)).limit(default_page_size).iterator();

		while (cursor.hasNext()) {
			Document dbo = cursor.next();
			logs.add(dbo);
		}

		Page page = new Page(pageNum, default_page_size, dbc.count(), logs);
		return page;
	}

}
