package cn.cjp.logger.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.BsonString;
import org.bson.Document;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;

import cn.cjp.logger.model.Node;
import cn.cjp.logger.mongo.MongoDao;
import cn.cjp.logger.util.Page;

@Configuration
@PropertySource(value = "classpath:/config.properties")
@Component
public class NodeService implements InitializingBean {

	public static boolean enable = true;

	private final static Logger logger = Logger.getLogger(NodeService.class);

	private final int default_page_num = 20;
	
	@Autowired
	NodeProducer producer;

	@Value("${config.collection.node}")
	String collectionName;

	@Autowired
	MongoDao mongoDao;

	public void save(Node node) {
		if (node == null || node.getBean() == null) {
			logger.info("some question happened.");
			return;
		}
		MongoCollection<Document> dbc = mongoDao.getDB().getCollection(collectionName);

		Document query = new Document();
		query.put(Node.CLASS, new BsonString(node.getBean().getClazz()));
		query.put(Node.METHOD, new BsonString(node.getBean().getMethod()));

		FindIterable<Document> it = dbc.find(query);
		Document dbo = it.first();
		if (null == dbo || dbo.isEmpty()) {
			dbo = node.toDoc();
			dbc.insertOne(dbo);
		} else {
			Document sort = new Document();
			sort.put(Node.ID, 1);

			Node nodeInDB = Node.fromDoc(dbo);
			long avgPeriod = (nodeInDB.getBean().getPeriod() + node.getBean().getPeriod())
					/ (nodeInDB.getBean().getCalledTimes() + 1);

			Document update = new Document();
			Document updateInc = new Document();
			Document updateSet = new Document();
			update.put("$inc", updateInc);
			updateInc.put(Node.PERIOD, node.getBean().getPeriod());
			updateInc.put(Node.CALLEDTIMES, 1);
			updateInc.put(Node.RETURNLINENUM, node.getBean().getReturnLineNum());

			updateSet.put(Node.AVGPERIOD, avgPeriod);
			// 比较平均耗时，如果大于之前的数据，则覆盖函数参数
			if (node.getBean().getAvgPeriod() > nodeInDB.getBean().getAvgPeriod()) {
				updateSet.put(Node.REMARKS, node.getBean().getRemarks());
			}
			update.put("$set", updateSet);

			FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
			options.upsert(true);
			dbo = dbc.findOneAndUpdate(query, update, options);
		}
		if (logger.isDebugEnabled()) {
			logger.debug(dbo);
		}
	}

	public Page findAll(String sortedName, int pageNum) {
		List<Node> nodes = new ArrayList<>();
		MongoDatabase db = mongoDao.getDB();

		MongoCollection<Document> dbc = db.getCollection(collectionName);
		FindIterable<Document> it = dbc.find().sort(new Document(sortedName, -1)).skip((pageNum - 1) * default_page_num)
				.limit(default_page_num);
		MongoCursor<Document> cursor = it.iterator();

		while (cursor.hasNext()) {
			Document dbo = cursor.next();
			Node model = Node.fromDoc(dbo);
			nodes.add(model);
			logger.debug(model);
		}

		Page page = new Page(pageNum, default_page_num, (int) dbc.count(), new ArrayList<>(nodes));
		return page;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		logger.info(collectionName + " - init index");
		try {
			MongoCollection<Document> dbc = mongoDao.getDB().getCollection(collectionName);
			Document key = new Document();
			key.put(Node.CLASS, 1);
			key.put(Node.METHOD, 1);
			dbc.createIndex(key);

			key = new Document();
			key.put(Node.TIME, -1);
			dbc.createIndex(key);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

}
