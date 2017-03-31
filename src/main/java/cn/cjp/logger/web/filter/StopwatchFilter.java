package cn.cjp.logger.web.filter;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import org.springframework.context.annotation.Configuration;

import cn.cjp.logger.model.BeanInspectorModel;
import cn.cjp.logger.model.Node;
import cn.cjp.utils.Logger;
import cn.cjp.utils.Stopwatchs;
import cn.cjp.utils.Stopwatchs.Stopwatch;
import cn.cjp.utils.Stopwatchs.Task;

/**
 * 请求日志过滤器
 * 
 * @usage spring-boot {@link Configuration} or @WebFilter
 * 
 */
@WebFilter(urlPatterns = { "/log/*", "/mr/*" })
public class StopwatchFilter implements Filter {

	private static final Logger LOGGER = Logger.getLogger(StopwatchFilter.class);

	public StopwatchFilter() {
		LOGGER.info(this);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		LOGGER.info(filterConfig);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		try {
			chain.doFilter(request, response);
		} catch (Exception e) {
			LOGGER.error(e, e);
		} finally {
			Stopwatch root = Stopwatchs.getRoot();
			Stopwatchs.release();
			LOGGER.info(root.toFullString());

			LOGGER.info(toNode(root));

		}
	}

	private Node toNode(Stopwatch root) {
		Task rootTask = root.getTask();

		Node node = new Node();
		node.setSource(null);
		BeanInspectorModel rootModel = (BeanInspectorModel) rootTask.getObj();
		node.setBean(rootModel);

		List<Stopwatch> stopwatchs = root.getLeaves();
		for (Stopwatch stopwatch : stopwatchs) {
			node.addChild(toNode(stopwatch));
		}
		return node;
	}

	@Override
	public void destroy() {
	}

}
