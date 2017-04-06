package cn.cjp.logger.mongo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;

import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

@Configuration
@PropertySource(value = "classpath:/mongo.properties")
public class MongoDaoFactory {

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

	@Value("${mongo.heartbeatFrequency}")
	int heartbeatFrequency;

	@Value("${mongo.heartbeatConnectTimeout}")
	int heartbeatConnectTimeout;

	@Value("${mongo.heartbeatSocketTimeout}")
	int heartbeatSocketTimeout;

	@Value("${mongo.connectionsPerHost}")
	int connectionsPerHost;

	@Value("${mongo.minConnectionsPerHost}")
	int minConnectionsPerHost;

	private static MongoDao singleton;

	@Scope(scopeName = "singleton")
	@Bean(destroyMethod = "close", initMethod = "afterPropertiesSet")
	public MongoDao mongoDao() throws IOException {
		if (singleton == null) {
			synchronized (MongoDaoFactory.class) {
				if (singleton == null) {
					ServerAddress addr = new ServerAddress(host, port);

					MongoCredential credential = MongoCredential.createCredential(username, database,
							password.toCharArray());
					List<MongoCredential> credentials = new ArrayList<>();
					credentials.add(credential);

					Builder builder = new Builder().connectTimeout(connectTimeout)
							.heartbeatFrequency(heartbeatFrequency).heartbeatConnectTimeout(heartbeatConnectTimeout)
							.heartbeatSocketTimeout(heartbeatSocketTimeout).connectionsPerHost(connectionsPerHost)
							.minConnectionsPerHost(minConnectionsPerHost).sslEnabled(false);
					singleton = MongoDao.build(addr, credentials, builder);
					singleton.setDefaultDatabase(database);
				}
			}
		}
		return singleton;
	}

}
