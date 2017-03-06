package cn.cjp.logger.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

import cn.cjp.logger.model.Log;
import cn.cjp.logger.mongo.MongoDao;

public class MongoTest {

	@Test
	public void t2() throws IOException {
		MongoDao mongoDao = new MongoDao();
		mongoDao.getDB().getCollection("info").insertOne(new Log().toDoc());
		System.out.println(mongoDao.getDB().getCollection("log_visit").count());
		mongoDao.close();
	}

	@Test
	public void t1() {

		ServerAddress addr = new ServerAddress("mongo.host", 27017);

		List<MongoCredential> credentials = new ArrayList<>();
		credentials.add(MongoCredential.createCredential("<hidden>", "<hidden>", "<hidden>".toCharArray()));

		MongoClient mongoClient = new MongoClient(addr, credentials);
		MongoDatabase db = mongoClient.getDatabase("<hidden>");
		System.out.println(db.listCollections());

		Document doc = new Log().toDoc();
		mongoClient.getDatabase("<hidden>").getCollection("info").insertOne(doc);
		System.out.println(doc);

		mongoClient.close();
	}

}
