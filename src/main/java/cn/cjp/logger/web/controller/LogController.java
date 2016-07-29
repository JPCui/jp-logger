package cn.cjp.logger.web.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import cn.cjp.logger.model.Log;
import cn.cjp.logger.service.LogConsumerManager;
import cn.cjp.logger.service.LogService;
import cn.cjp.utils.Page;

@RestController(value = "logController")
@RequestMapping("/log")
public class LogController {

	@Resource(name = "logService")
	LogService logService;

	@Autowired
	LogConsumerManager logConsumerManager;

	/**
	 * 观察线程池状态
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/status")
	@ResponseBody
	public ModelAndView status(HttpServletResponse response) {
		ModelAndView mv = new ModelAndView("logs/index");
		mv.addObject("activeCount", logConsumerManager.activeCount);
		mv.addObject("taskCount", logConsumerManager.taskCount);
		return mv;
	}

	/**
	 * 上传一条log
	 * @param log
	 * @return
	 */
	@RequestMapping("/report")
	public String report(Log log) {
		return logService.report(log).toString();
	}

	/**
	 * 接口：查看log
	 * @param response
	 * @param time
	 * @param level
	 * @param _pageNum
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/{level}")
	public ModelAndView log(HttpServletResponse response, @RequestParam(value = "time", defaultValue = "") String time,
			@PathVariable("level") String level, @RequestParam(defaultValue = "1") int _pageNum) throws Exception {
		// response.setHeader("Access-Control-Allow-Origin", "*"); //
		// 允许哪些url可以跨域请求到本域
		// response.setHeader("Access-Control-Allow-Methods", "GET"); //
		// 允许的请求方法，一般是GET,POST,PUT,DELETE,OPTIONS
		// response.setHeader("Access-Control-Allow-Headers",
		// "x-requested-with,content-type"); // 允许哪些请求头可以跨域
		_pageNum = _pageNum < 1 ? 1 : _pageNum;
		Page model;
		if (StringUtils.isEmpty(time)) {
			model = logService.findAll(level, _pageNum);
		} else {
			model = logService.findAllByTime(level, time, _pageNum);
		}

		ModelAndView mv = new ModelAndView("logs/index");
		mv.addObject("time", time);
		mv.addObject("list", model.getResultList());
		mv.addObject("_pageNum", model.currPage);
		mv.addObject("nextPage", model.nextPage);
		mv.addObject("prevPage", model.prevPage);
		return mv;
	}

}
