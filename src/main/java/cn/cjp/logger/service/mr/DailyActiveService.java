package cn.cjp.logger.service.mr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mongodb.client.MapReduceIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import cn.cjp.logger.mongo.MongoDao;
import cn.cjp.utils.Logger;

@Component
public class DailyActiveService {

	private static final Logger LOGGER = Logger.getLogger(DailyActiveService.class);

	@Autowired
	MongoDao mongoDao;

	private static final String project = "daily_active";

	public List<Object> mr(Calendar day) {
		return mr(day, Configuration.getMap(project), Configuration.getReduce(project));
	}

	/**
	 * 获取某天各个时段的活跃量
	 * 
	 * @param day
	 * @param jsMap
	 * @param jsReduce
	 * @return
	 */
	private List<Object> mr(Calendar day, String jsMap, String jsReduce) {
		day.set(Calendar.HOUR_OF_DAY, 0);
		day.set(Calendar.MINUTE, 0);
		day.set(Calendar.SECOND, 0);
		day.set(Calendar.MILLISECOND, 0);
		Date dayStart = day.getTime();
		day.add(Calendar.DAY_OF_MONTH, 1);
		Date dayEnd = day.getTime();

		MongoDatabase db = mongoDao.getDB();
		MongoCollection<Document> dbc = db.getCollection("visit");
		Document query = new Document();
		query.append("time", new Document().append("$gte", dayStart).append("$lte", dayEnd));
		MapReduceIterable<Document> iterable = dbc.mapReduce(jsMap, jsReduce).filter(query);
		// see: OutputType
		// iterable.action(MapReduceAction.MERGE);

		List<Object> result = new ArrayList<>();
		// 获取结果
		MongoCursor<Document> cursor = iterable.iterator();
		while (cursor.hasNext()) {
			Document doc = cursor.next();
			LOGGER.debug(doc);
			String key = doc.get("_id").toString();
			Double value = doc.getDouble("value");
			DailyActive dailyActive = new DailyActive();
			dailyActive.date = key;
			dailyActive.value = value;
			result.add(dailyActive);
		}
		return result;
	}

	public static void main(String[] args) throws IOException {
		DailyActiveService mr = new DailyActiveService();
		mr.mongoDao = MongoDao.newInstance();

		String project = "daily_active";
		// String project = "not_exist_project"; // 不存在的 project
		System.out.println(
				mr.mr(Calendar.getInstance(), Configuration.getMap(project), Configuration.getReduce(project)));
	}

}

class DailyActive {
	
	String date;

	double value;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
	
}
