package cn.cjp.logger.service;

import java.net.UnknownHostException;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoTest {
	public static void main(String[] args) throws UnknownHostException {

		MongoClient client = new MongoClient("mongo.host");
		MongoDatabase db = client.getDatabase("cjp_logger");

		Document query = new Document();
		query.put("_id", new ObjectId("58acf848aa5bb2d9d406d5cd"));

		Document update = new Document();
		Document d1 = new Document();
		d1.put("host", new Document("hostAddress", "1.1.1.2"));
		update.put("$set", d1);

		MongoCollection<Document> coll = db.getCollection("visit");
		System.out.println(coll.findOneAndUpdate(query, update));

		client.close();
	}

}
