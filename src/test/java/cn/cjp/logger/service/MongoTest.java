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
import cn.cjp.utils.PropertiesUtil;

public class MongoTest {

	static String host;
	static int port;
	static String database;
	static String username;
	static String password;

	static {
		try {
			PropertiesUtil props = new PropertiesUtil("/mongo.properties");
			host = props.getValue("mongo.host");
			port = props.getInt("mongo.port", 27017);
			database = props.getValue("mongo.database");
			username = props.getValue("mongo.username");
			password = props.getValue("mongo.password");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void t2() throws IOException {
		MongoDao mongoDao = MongoDao.newInstance();
		mongoDao.getDB().getCollection("info").insertOne(new Log().toDoc());
		System.out.println(mongoDao.getDB().getCollection("log_visit").count());
		mongoDao.close();
	}

	@Test
	public void t1() {

		ServerAddress addr = new ServerAddress(host, 27017);

		List<MongoCredential> credentials = new ArrayList<>();
		credentials.add(MongoCredential.createCredential(username, database, password.toCharArray()));

		MongoClient mongoClient = new MongoClient(addr, credentials);
		MongoDatabase db = mongoClient.getDatabase(database);
		System.out.println(db.listCollections());

		Document doc = new Log().toDoc();
		db.getCollection("info").insertOne(doc);
		System.out.println(doc);

		mongoClient.close();
	}

}
