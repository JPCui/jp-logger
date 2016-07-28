package cn.cjp.logger.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.accept.ContentNegotiationManagerFactoryBean;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.handler.SimpleServletHandlerAdapter;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import org.springframework.web.servlet.view.xml.MarshallingView;

/**
 * MVC代码配置方法
 * <p>
 * 用于替代springMVC的配置文件
 * 
 * @author Jinpeng Cui
 *
 */
@Configuration
@EnableWebMvc
public class MVCConfig extends WebMvcConfigurationSupport {

	private static final Logger logger = Logger.getLogger(MVCConfig.class);

	@Bean(name = "viewResolver")
	public ContentNegotiatingViewResolver viewResolver() {
		logger.info("create viewResolver");
		ContentNegotiatingViewResolver viewResolver = new ContentNegotiatingViewResolver();
		viewResolver.setContentNegotiationManager(contentNegotiationManager());
		viewResolver.setViewResolvers(viewResolvers());

		List<View> defaultViews = new ArrayList<>();
		// 默认视图
		// json视图解析
		defaultViews.add(new MappingJackson2JsonView());
		// xml视图解析
		MarshallingView marshallingView = new MarshallingView();
		marshallingView.setMarshaller(new XStreamMarshaller());
		defaultViews.add(marshallingView);
		viewResolver.setDefaultViews(defaultViews);
		return viewResolver;
	}

	private List<ViewResolver> viewResolvers() {
		List<ViewResolver> viewResolvers = new ArrayList<>();
		viewResolvers.add(new BeanNameViewResolver());

		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setPrefix("/WEB-INF/pages/");
		viewResolver.setSuffix(".jsp");
		viewResolver.setContentType("text/html;charset=UTF-8");
		viewResolvers.add(viewResolver);

		return viewResolvers;
	}

	// @Bean
	public ContentNegotiationManager contentNegotiationManager() {
		logger.info("create contentNegotiationManager");
		ContentNegotiationManagerFactoryBean factoryBean = new ContentNegotiationManagerFactoryBean();
		factoryBean.addMediaType("json", MediaType.APPLICATION_JSON_UTF8);
		factoryBean.addMediaType("xml", MediaType.APPLICATION_XML);
		factoryBean.addMediaType("htm", MediaType.TEXT_HTML);
		return factoryBean.getObject();
	}

	/**
	 * 描述 : <注册消息资源处理器>. <br>
	 * <p>
	 * <使用方法说明>
	 * </p>
	 * 
	 * @return
	 */
	@Bean
	public MessageSource messageSource() {
		logger.info("MessageSource");
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasename("config.messages.messages");

		return messageSource;
	}

	/**
	 * 描述 : <注册servlet适配器>. <br>
	 * <p>
	 * <只需要在自定义的servlet上用@Controller("映射路径")标注即可>
	 * </p>
	 * 
	 * @return
	 */
	@Bean
	public HandlerAdapter servletHandlerAdapter() {
		logger.info("HandlerAdapter");
		return new SimpleServletHandlerAdapter();
	}

	/**
	 * 描述 : <本地化拦截器>. <br>
	 * <p>
	 * <使用方法说明>
	 * </p>
	 * 
	 * @return
	 */
	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		logger.info("LocaleChangeInterceptor");
		return new LocaleChangeInterceptor();
	}

	/**
	 * 描述 : <基于cookie的本地化资源处理器>. <br>
	 * <p>
	 * <使用方法说明>
	 * </p>
	 * 
	 * @return
	 */
	@Bean(name = "localeResolver")
	public CookieLocaleResolver cookieLocaleResolver() {
		logger.info("CookieLocaleResolver");
		return new CookieLocaleResolver();
	}

	/**
	 * 描述 : <RequestMappingHandlerMapping需要显示声明，否则不能注册自定义的拦截器>. <br>
	 * <p>
	 * <这个比较奇怪,理论上应该是不需要的>
	 * </p>
	 * 
	 * @return
	 */
	@Bean
	public RequestMappingHandlerMapping requestMappingHandlerMapping() {
		logger.info("RequestMappingHandlerMapping");

		return super.requestMappingHandlerMapping();
	}

	/**
	 * 描述 : <添加拦截器>. <br>
	 * <p>
	 * <使用方法说明>
	 * </p>
	 * 
	 * @param registry
	 */
	@Override
	protected void addInterceptors(InterceptorRegistry registry) {
		logger.info("addInterceptors start");
		registry.addInterceptor(localeChangeInterceptor());
		logger.info("addInterceptors end");
	}

	/**
	 * 描述 : <HandlerMapping需要显示声明，否则不能注册资源访问处理器>. <br>
	 * <p>
	 * <这个比较奇怪,理论上应该是不需要的>
	 * </p>
	 * 
	 * @return
	 */
	@Bean
	public HandlerMapping resourceHandlerMapping() {
		logger.info("HandlerMapping");
		return super.resourceHandlerMapping();
	}

	/**
	 * 描述 : <资源访问处理器>. <br>
	 * <p>
	 * <可以在jsp中使用/static/**的方式访问/WEB-INF/static/下的内容>
	 * </p>
	 * 
	 * @param registry
	 */
	@Override
	protected void addResourceHandlers(ResourceHandlerRegistry registry) {
		logger.info("addResourceHandlers");
		registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
	}

	/**
	 * 描述 : <RequestMappingHandlerAdapter需要显示声明，否则不能注册通用属性编辑器>. <br>
	 * <p>
	 * <这个比较奇怪,理论上应该是不需要的>
	 * </p>
	 * 
	 * @return
	 */
	@Bean
	public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {
		logger.info("RequestMappingHandlerAdapter");
		return super.requestMappingHandlerAdapter();
	}

	/**
	 * 描述 : <注册通用属性编辑器>. <br>
	 * <p>
	 * <这里只增加了字符串转日期和字符串两边去空格的处理>
	 * </p>
	 * 
	 * @return
	 */
	@Override
	protected ConfigurableWebBindingInitializer getConfigurableWebBindingInitializer() {
		logger.info("ConfigurableWebBindingInitializer");
		ConfigurableWebBindingInitializer initializer = super.getConfigurableWebBindingInitializer();
		return initializer;
	}

}
