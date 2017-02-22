package cn.cjp.logger.service;

import java.net.UnknownHostException;

import org.bson.BsonDocument;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class CollectionRemove {
	public static void main(String[] args) throws UnknownHostException {

		// Date date = DateUtil.add(Calendar.DAY_OF_YEAR, -2);
		// DBObject query = new BasicDBObject("time", new
		// BasicDBObject(QueryOperators.LTE, date));
		// DBObject query = new BasicDBObject();
		BsonDocument query = new BsonDocument();

		MongoClient client = new MongoClient("mongo.host");
		MongoDatabase db = client.getDatabase("cjp_logger");
		System.out.println(db.getCollection("visit").count());
		System.out.println(db.getCollection("info").deleteMany(query));
		System.out.println(db.getCollection("warn").deleteMany(query));
		System.out.println(db.getCollection("error").deleteMany(query));
		System.out.println(db.getCollection("visit").deleteMany(query));
		System.out.println(db.getCollection("bean_inspector").deleteMany(query));
		System.out.println(db.getCollection("bean_inspector_node").deleteMany(query));
		client.close();
	}

}
