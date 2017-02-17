package cn.cjp.logger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class Application {

	/**
	 * 创建异步任务执行器
	 * 
	 * @return
	 */
	@Scope(scopeName = "singleton")
	@Bean
	public SimpleAsyncTaskExecutor simpleAsyncTaskExecutor() {
		SimpleAsyncTaskExecutor simpleAsyncTaskExecutor = new SimpleAsyncTaskExecutor();
		simpleAsyncTaskExecutor.setThreadNamePrefix("Async-Task-");
		simpleAsyncTaskExecutor.setDaemon(true);
		simpleAsyncTaskExecutor.setConcurrencyLimit(10);
		return simpleAsyncTaskExecutor;
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}