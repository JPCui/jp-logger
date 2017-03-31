package cn.cjp.logger;

import javax.servlet.annotation.WebFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cn.cjp.logger.util.BeanWatcher;
import cn.cjp.logger.web.filter.StopwatchFilter;

/**
 * 
 * @author Jinpeng Cui
 *
 */
@Configuration
public class BeanWatcherTest {

	@Bean
	public BeanWatcher getBeanWatcher() {
		BeanWatcher bi = new BeanWatcher();
		return bi;
	}

	/**
	 * 测试 {@link WebFilter}
	 * 
	 * @return
	 */
	// @Bean
	public StopwatchFilter getStopwatchFilter() {
		return new StopwatchFilter();
	}

	public static void main(String[] args) {
		Application.main(new String[0]);
	}

}
