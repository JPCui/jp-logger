package cn.cjp.logger.mongo;

import java.io.Closeable;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.springframework.beans.factory.InitializingBean;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import cn.cjp.logger.model.BeanInspectorModel;
import cn.cjp.logger.model.Log;
import cn.cjp.logger.util.PropertiesUtil;

/**
 * 可以使用 {@link #build(ServerAddress, List, Builder)} 来实例化 MongoDao
 * 
 * @author Jinpeng Cui
 */
// @Component
// @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class MongoDao implements InitializingBean, Closeable {

	private static Logger logger = Logger.getLogger(MongoDao.class);

	private String host;
	private int port;
	private String username;
	private String password;
	private String database;
	private String defaultDatabase;

	private int connectionsPerHost = 10;
	private int minConnectionsPerHost = 0;

	private int connectTimeout;
	private int heartbeatFrequency;
	private int heartbeatConnectTimeout;
	private int heartbeatSocketTimeout;

	private MongoClient client;

	private boolean closed = false;

	/**
	 * 根据默认配置文件生成 MongoDao
	 * 
	 * @throws IOException
	 */
	private MongoDao() throws IOException {
		initProperties();
		client = createMongoClient();
	}

	private MongoDao(MongoClient client) {
		this.client = client;
	}

	public static MongoDao newInstance() throws IOException {
		return new MongoDao();
	}

	/**
	 * 
	 * @param addr
	 * @param credentials
	 * @param builder
	 * @return
	 */
	public static MongoDao build(ServerAddress addr, List<MongoCredential> credentials, Builder builder) {
		MongoClientOptions options = builder.build();
		MongoClient mongoClient = new MongoClient(addr, credentials, options);
		return new MongoDao(mongoClient);
	}

	public static void main(String[] args) throws IOException {
		MongoDao mongoDao = new MongoDao();
		mongoDao.getDB().getCollection("log_visit").insertOne(new Log().toDoc());
		logger.error(mongoDao.getDB().getCollection("log_visit").count());
		mongoDao.close();
	}

	/**
	 * mongo.properties
	 * 
	 * @throws IOException
	 */
	private void initProperties() throws IOException {
		try {
			PropertiesUtil props = new PropertiesUtil("mongo.properties");
			host = props.getValue("mongo.host");
			port = Integer.parseInt(props.getValue("mongo.port"));
			username = props.getValue("mongo.username");
			password = props.getValue("mongo.password");
			database = props.getValue("mongo.database");
			defaultDatabase = database;

			connectionsPerHost = props.getInt("mongo.connectionsPerHost", 100);
			minConnectionsPerHost = props.getInt("mongo.minConnectionsPerHost", 0);

			connectTimeout = props.getInt("mongo.connectTimeout", 20_000);
			heartbeatFrequency = props.getInt("mongo.heartbeatFrequency", 10);
			heartbeatConnectTimeout = props.getInt("mongo.heartbeatConnectTimeout", 20_000);
			heartbeatSocketTimeout = props.getInt("mongo.heartbeatSocketTimeout", 20_000);
		} catch (IOException e) {
			logger.error("配置文件访问失败");
			throw e;
		}
	}

	private MongoClient createMongoClient() throws UnknownHostException {
		ServerAddress addr = new ServerAddress(host, port);

		MongoCredential credential = MongoCredential.createCredential(username, database, password.toCharArray());
		List<MongoCredential> credentials = new ArrayList<>();
		credentials.add(credential);

		MongoClientOptions options = new Builder().connectTimeout(connectTimeout).heartbeatFrequency(heartbeatFrequency)
				.heartbeatConnectTimeout(heartbeatConnectTimeout).heartbeatSocketTimeout(heartbeatSocketTimeout)
				.connectionsPerHost(connectionsPerHost).minConnectionsPerHost(minConnectionsPerHost).sslEnabled(false)
				.build();
		MongoClient mongoClient = new MongoClient(addr, credentials, options);
		return mongoClient;
	}

	public MongoDatabase getDB() {
		return client.getDatabase(defaultDatabase);
	}

	public MongoDatabase getDB(String databaseName) {
		return client.getDatabase(databaseName);
	}

	@Override
	public void close() throws IOException {
		if (closed) {
			return;
		}
		client.close();
		closed = true;
		logger.info("MongoDao closed.");
	}

	public boolean isClosed() {
		return closed;
	}

	public MongoClient getClient() {
		return client;
	}

	public void setClient(MongoClient client) {
		this.client = client;
	}

	public static Logger getLogger() {
		return logger;
	}

	public static void setLogger(Logger logger) {
		MongoDao.logger = logger;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public int getHeartbeatFrequency() {
		return heartbeatFrequency;
	}

	public void setHeartbeatFrequency(int heartbeatFrequency) {
		this.heartbeatFrequency = heartbeatFrequency;
	}

	public int getHeartbeatConnectTimeout() {
		return heartbeatConnectTimeout;
	}

	public void setHeartbeatConnectTimeout(int heartbeatConnectTimeout) {
		this.heartbeatConnectTimeout = heartbeatConnectTimeout;
	}

	public int getHeartbeatSocketTimeout() {
		return heartbeatSocketTimeout;
	}

	public void setHeartbeatSocketTimeout(int heartbeatSocketTimeout) {
		this.heartbeatSocketTimeout = heartbeatSocketTimeout;
	}

	public void setConnectionsPerHost(int connectionsPerHost) {
		this.connectionsPerHost = connectionsPerHost;
	}

	public void setMinConnectionsPerHost(int minConnectionsPerHost) {
		this.minConnectionsPerHost = minConnectionsPerHost;
	}

	public void setDefaultDatabase(String defaultDatabase) {
		this.defaultDatabase = defaultDatabase;
	}

	private void buildIndex() {
		String[] collectionNames = new String[] { "info", "warn", "error" };
		logger.info("build index.");
		for (int i = 0; i < collectionNames.length; i++) {
			String collectionName = collectionNames[i];
			logger.info(collectionName + " - init index");

			MongoCollection<Document> dbc = this.getDB().getCollection(collectionName);
			Document key = new Document();
			key.put(BeanInspectorModel.TIME, -1);
			dbc.createIndex(key);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		buildIndex();
	}

}
