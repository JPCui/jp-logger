package cn.cjp.logger.service;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import cn.cjp.logger.model.BeanInspectorModel;
import cn.cjp.logger.model.Node;
import cn.cjp.logger.util.JacksonUtil;

/**
 * 实体调用检测拦截器.
 * 
 * @usage <code>
   <bean id="beanInspectAdvice" class="xxx.BeanInspector"></bean>
	
	<bean id="inspector-stat-pointcut" class="org.springframework.aop.support.JdkRegexpMethodPointcut"
		scope="prototype">
		<property name="patterns">
			<list>
				<value>xxx.((?!common)(?!base).)+.service.*</value>
			</list>
		</property>
	</bean>
	
   <aop:config proxy-target-class="true">
      <aop:advisor advice-ref="beanInspectAdvice" pointcut-ref="inspector-stat-pointcut"/>
   </aop:config>
 * </code>
 * 
 * @author JinPeng Cui
 *
 */
public class BeanInspector implements MethodInterceptor {

	private static Logger logger = Logger.getLogger(BeanInspector.class);

	public static ThreadLocal<Node> safeNode = new ThreadLocal<>();

	static boolean enable = true;

	@Autowired
	NodeProducer producer;

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		if (!enable) {
			logger.info("disable bean inspect.");
			return invocation.proceed();
		}

		logger.info("inspect: " + invocation.getMethod().toString());
		Object result = null;

		Node parent = safeNode.get();
		if (parent == null) {
			// 主线程线程运行，建立原始节点
			parent = new Node(null);
			parent.setBean(initBeanInspectModel(invocation));
			safeNode.set(parent);
		} else {
			// 子线程运行，创建子节点，并指向父节点
			Node child = new Node(parent);
			child.setBean(initBeanInspectModel(invocation));
			parent.addChild(child);
			// 并把子节点（当前节点）存到threadlocal中
			safeNode.set(child);
		}
		long period = System.currentTimeMillis();
		result = invocation.proceed();
		period = System.currentTimeMillis() - period;
		try {
			inspect(invocation, result, period);
		} catch (Exception e) {
			logger.error(e);
		}
		Node currNode = safeNode.get();
		if (currNode != null) {
			if (currNode.getParent() != null) {
				safeNode.set(currNode.getParent());
			} else {
				try {
					producer.produce(currNode);
				} catch (Exception e) {
					logger.error(JacksonUtil.toJson(currNode), e);
				} finally {
					safeNode.set(null);
				}
			}
		} else {
			logger.error(String.format("node is null, %s", invocation.getMethod().toString()));
		}

		return result;
	}

	/**
	 * 建立一个初始化model
	 * 
	 * @param invocation
	 * @return
	 */
	private BeanInspectorModel initBeanInspectModel(MethodInvocation invocation) {
		Method method = invocation.getMethod();
		StringBuilder methodName = new StringBuilder(method.getName());
		methodName.append("(");
		// 获取参数
		StringBuilder remarks = new StringBuilder();
		remarks.append("(");
		Parameter[] params = method.getParameters();
		Object[] args = invocation.getArguments();
		for (int i = 0; i < params.length; i++) {
			Parameter param = params[i];
			if (i == 0) {
				methodName.append(param.getType().getName() + " " + param.getName());
				remarks.append((null == args[i] ? null : args[i].toString()));
			} else {
				methodName.append(", " + param.getType().getName() + " " + param.getName());
				remarks.append(", " + (null == args[i] ? null : args[i].toString()));
			}
		}
		methodName.append(")");
		remarks.append(")");
		String className = invocation.getThis().getClass().getName();

		try {
			BeanInspectorModel inspectorModel = new BeanInspectorModel();
			// 现在还没开始调用
			inspectorModel.setCalledTimes(0);
			inspectorModel.setClazz(className);
			inspectorModel.setMethod(methodName.toString());
			inspectorModel.setRemarks(remarks.toString());
			return inspectorModel;
		} catch (Exception e) {
			logger.error("inspector error while inspecting " + method, e);
		}
		return null;
	}

	private void inspect(MethodInvocation invocation, Object result, long period) {
		Method method = invocation.getMethod();
		StringBuilder methodName = new StringBuilder(method.getName());
		methodName.append("(");
		// 获取参数
		StringBuilder remarks = new StringBuilder();
		remarks.append("(");
		Parameter[] params = method.getParameters();
		Object[] args = invocation.getArguments();
		int length = params.length;
		if (length > 0) {
			Parameter param = params[0];
			methodName.append(param.getType().getName() + " " + param.getName());
			remarks.append((null == args[0] ? null : args[0].toString()));
			for (int i = 1; i < params.length; i++) {
				param = params[i];
				methodName.append(", " + param.getType().getName() + " " + param.getName());
				remarks.append(", " + (null == args[i] ? null : args[i].toString()));
			}
		}
		methodName.append(")");
		remarks.append(")");
		String className = method.getDeclaringClass().getName();
		int returnLineNum = 1;
		if (null != result) {
			if (result instanceof Collection) {
				returnLineNum = ((Collection<?>) result).size();
			} else if (result.getClass().isArray()) {
				returnLineNum = ((Object[]) result).length;
			}
		}

		try {
			Node currNode = safeNode.get();
			if (currNode != null) {
				BeanInspectorModel inspectorModel = currNode.getBean();
				if (inspectorModel == null) {
					inspectorModel = new BeanInspectorModel();
					inspectorModel.setClazz(className);
					inspectorModel.setMethod(methodName.toString());
					inspectorModel.setRemarks(remarks.toString());
					currNode.setBean(inspectorModel);
				}
				// 当前model: calledTime+1, period++, returnLineNum++
				inspectorModel.mergeValue(period, returnLineNum);
			} else {
				logger.error(invocation);
			}
		} catch (Exception e) {
			try {
				logger.error("inspector error while inspecting " + method, e);
			} catch (Exception e2) {
			}
		}
	}

}
