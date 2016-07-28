package cn.cjp.logger.mongo;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import cn.cjp.logger.dao.mongo.MongoDao;

@Configuration
@PropertySource(value = "classpath:/mongo.properties")
public class MongoConfig {

	@Value("${mongo.username}")
	String username;

	@Value("${mongo.password}")
	String password;

	@Value("${mongo.host}")
	String host;

	@Value("${mongo.port}")
	int port;

	@Value("${mongo.database}")
	String database;

	@Value("${mongo.connectTimeout}")
	int connectTimeout;

	@Value("${mongo.heartbeatConnectRetryFrequency}")
	int heartbeatConnectRetryFrequency;

	@Value("${mongo.heartbeatConnectTimeout}")
	int heartbeatConnectTimeout;

	@Value("${mongo.heartbeatSocketTimeout}")
	int heartbeatSocketTimeout;

	@Value("${mongo.connectionsPerHost}")
	int connectionsPerHost;

	@Value("${mongo.minConnectionsPerHost}")
	int minConnectionsPerHost;

	@Bean(destroyMethod = "close")
	public MongoDao mongoDao() throws IOException {
		MongoDao dao = new MongoDao(host, port, username, password, database, connectTimeout,
				heartbeatConnectRetryFrequency, heartbeatConnectTimeout, heartbeatSocketTimeout);
		return dao;
	}

}
