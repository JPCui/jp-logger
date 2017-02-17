package cn.cjp.logger.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import cn.cjp.logger.Application;
import cn.cjp.logger.model.BeanInspectorModel;
import cn.cjp.logger.model.Log;
import cn.cjp.logger.model.Node;
import cn.cjp.logger.util.JacksonUtil;

/**
 * 
 * @SpringApplicationConfiguration(classes = Application.class)
 *                                         指定我们SpringBoot工程的Application启动类
 * 
 * @WebAppConfiguration 由于是Web项目，Junit需要模拟ServletContext，
 *                      因此我们需要给我们的测试类加上@WebAppConfiguration。
 * @author Jinpeng Cui
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class NodeProducerTest {

	@Autowired
	NodeProducer nodeProducer;

	/**
	 * 生产数据
	 */
	@Test
	public void produce() {
		Node node = new Node(null);
		node.setBean(new BeanInspectorModel());
		nodeProducer.produce(node);
	}

	public static void main(String[] args) {
		Node node = new Node(null);
		System.out.println(JacksonUtil.fromJsonToObj(JacksonUtil.toJson(node), Node.class));
		Log log = new Log();
		System.out.println(JacksonUtil.fromJsonToObj(JacksonUtil.toJson(log), Log.class));
	}

}
