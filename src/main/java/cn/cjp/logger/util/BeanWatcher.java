package cn.cjp.logger.util;

import java.util.Collection;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import cn.cjp.logger.model.BeanInspectorModel;
import cn.cjp.logger.service.NodeProducer;
import cn.cjp.utils.Logger;
import cn.cjp.utils.Stopwatchs;
import cn.cjp.utils.Stopwatchs.Stopwatch;
import cn.cjp.utils.Stopwatchs.Task;

/**
 * 实体调用检测拦截器.
 * 
 * @usage <code>
   <bean id="beanInspectAdvice" class="xxx.BeanInspector"></bean>
	
	<bean id="inspector-stat-pointcut" class="org.springframework.aop.support.JdkRegexpMethodPointcut"
		scope="prototype">
		<property name="patterns">
			<list>
				<value>your_package.((?!common)(?!base).)+.service.*</value>
			</list>
		</property>
	</bean>
	
   <aop:config proxy-target-class="true">
      <aop:advisor advice-ref="beanInspectAdvice" pointcut-ref="inspector-stat-pointcut"/>
   </aop:config>
 * </code>
 * 
 * @usage
 * 
 *        <pre>
 *        &#64;Bean
 *        public BeanWatcher BeanWatcher() {
 *        	BeanWatcher w = new BeanWatcher();
 *        	return w;
 *        }
 *        </pre>
 * 
 * @author JinPeng Cui
 * @see EnableAspectJAutoProxy 如何在SpringBoot中使用 aspect
 */
@Aspect
public class BeanWatcher {

	private static final Logger logger = Logger.getLogger(BeanWatcher.class);

	public BeanWatcher() {
	}

	@Autowired
	NodeProducer producer;

	/**
	 * @use @Pointcut(value=
	 *      "execution(* your_package.*.sayAdvisorBefore(..)) && args(param)",
	 *      argNames = "param")
	 * @param method
	 * @param args
	 * @param target
	 */
	@Before(value = "execution(public * cn.cjp.logger.service.**.*(..))")
	public void before(JoinPoint point) {
		BeanInspectorModel model = initBeanInspectModel(point);
		Task task = new Task(model.toFullName());
		task.setObj(model);
		Stopwatchs.start(task);
	}

	// @After(value = "cn.cjp.logger.[dao|service].*")
	@AfterReturning(value = "execution(public * cn.cjp.logger.service.**.*(..))", returning = "returnValue")
	public void after(JoinPoint point, Object returnValue) {
		Stopwatch stopwatch = Stopwatchs.getCurrent();
		Stopwatchs.end();
		fill(stopwatch, point, returnValue);
	}

	private void fill(Stopwatch stopwatch, JoinPoint point, Object returnValue) {

		int returnLineNum = 1;
		if (returnValue != null) {
			if (returnValue.getClass().isArray()) {
				returnLineNum = ((Object[]) returnValue).length;
			} else if ((returnValue instanceof Collection<?>)) {
				returnLineNum = ((Collection<?>) returnValue).size();
			} else if ((returnValue instanceof Page)) {
				returnLineNum = ((Page) returnValue).getResultList().size();
			}
		}

		Task task = stopwatch.getTask();
		BeanInspectorModel inspectorModel = (BeanInspectorModel) task.getObj();
		// 当前model: calledTime+1, period++, returnLineNum++
		inspectorModel.mergeValue(stopwatch.getElapsedTime(), returnLineNum);
	}

	/**
	 * 建立一个初始化model
	 * 
	 * @param invocation
	 * @return
	 */
	private BeanInspectorModel initBeanInspectModel(JoinPoint point) {
		Signature sign = point.getSignature();
		String className = sign.getDeclaringTypeName();
		String method = sign.toString().substring(sign.toString().lastIndexOf(".") + 1);

		BeanInspectorModel model = BeanInspectorModel.newInstance(className, method, "");
		return model;
	}

}
