package cn.cjp.logger.service.mr;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mongodb.client.MapReduceIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import cn.cjp.logger.mongo.MongoDao;
import cn.cjp.logger.util.IPUtil;
import cn.cjp.utils.Logger;
import cn.cjp.utils.StringUtil;
import cn.cjp.utils.ValueComparator;

/**
 * 简单的MapReduce，其实就是一些分组统计
 * <p>
 * MR返回的结果是 _id, value 这两个字段
 * 
 * @author Jinpeng Cui
 *
 */
@Component
public class SimpleMapReduce {

	private static final Logger LOGGER = Logger.getLogger(SimpleMapReduce.class);

	@Autowired
	MongoDao mongoDao;

	public Map<String, Double> mr(String project) {
		return this.mr(Configuration.getMap(project), Configuration.getReduce(project));
	}

	/**
	 * 
	 * @param jsMap
	 * @param jsReduce
	 * @return
	 * @throws IOException
	 */
	public Map<String, Double> mr(String jsMap, String jsReduce) {
		MongoDatabase db = mongoDao.getDB();
		MongoCollection<Document> dbc = db.getCollection("visit");
		MapReduceIterable<Document> iterable = dbc.mapReduce(jsMap, jsReduce);
		// see: OutputType
		// iterable.action(MapReduceAction.MERGE);

		// 获取结果
		Map<String, Double> ipNumResult = new HashMap<>();
		MongoCursor<Document> cursor = iterable.iterator();
		while (cursor.hasNext()) {
			Document doc = cursor.next();
			LOGGER.debug(doc);
			String key = doc.get("_id").toString();
			Double value = doc.getDouble("value");
			ipNumResult.put(key, value);
		}

		Map<String, Double> tempResult = new HashMap<>();
		ValueComparator<String, Double> comparator = new ValueComparator<>(tempResult);
		SortedMap<String, Double> result = new TreeMap<>(comparator);
		//
		String[] ips = ipNumResult.keySet().toArray(new String[0]);
		Map<String, String> ipInfo = IPUtil.getCityByIps(ips);
		Iterator<String> it = ipNumResult.keySet().iterator();
		while (it.hasNext()) {
			String ip = it.next();
			String province = ipInfo.get(ip);
			if (StringUtil.isEmpty(province)) {
				continue;
			}
			// replace key
			if (tempResult.containsKey(province)) {
				tempResult.put(province, tempResult.get(province) + ipNumResult.get(ip));
			} else {
				tempResult.put(province, ipNumResult.get(ip));
			}
		}
		result.putAll(tempResult);
		return result;
	}

	public static void main(String[] args) throws IOException {
		SimpleMapReduce mr = new SimpleMapReduce();
		mr.mongoDao = MongoDao.newInstance();

		String project = "count_ip";
		// String project = "not_exist_project"; // 不存在的 project
		System.out.println(mr.mr(Configuration.getMap(project), Configuration.getReduce(project)));
	}

}
