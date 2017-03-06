package cn.cjp.logger.service;

import java.net.UnknownHostException;
import java.util.Date;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.QueryOperators;
import com.mongodb.client.MongoDatabase;

import cn.cjp.utils.Logger;

public class CollectionRemove {

	static Logger logger = Logger.getLogger(CollectionRemove.class);

	public static void main(String[] args) throws UnknownHostException {

		Date date = new Date();
		// DBObject query = new BasicDBObject("time", new
		// BasicDBObject(QueryOperators.LTE, date));
		// DBObject query = new BasicDBObject();
		Document query = new Document();
		query.append("time", new Document(QueryOperators.LTE, date));

		MongoClient client = new MongoClient("mongo.host");
		MongoDatabase db = client.getDatabase("cjp_logger");
		logger.info(db.getCollection("visit").count());
		logger.info(db.getCollection("info").deleteMany(query));
		logger.info(db.getCollection("warn").deleteMany(query));
		logger.info(db.getCollection("error").deleteMany(query));
		logger.info(db.getCollection("visit").deleteMany(query));
		logger.info(db.getCollection("bean_inspector_node").deleteMany(query));
		client.close();
	}

}
