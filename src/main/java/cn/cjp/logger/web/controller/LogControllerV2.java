package cn.cjp.logger.web.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import cn.cjp.logger.service.LogService;
import cn.cjp.logger.util.Page;

@RestController(value = "logControllerV2")
@RequestMapping("/log/v2")
public class LogControllerV2 {

	@Resource(name = "logService")
	LogService logService;

	/**
	 * 上传一条log
	 * 
	 * @param log
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/report")
	public String report(HttpServletRequest request) throws Exception {
		return logService.report(request).toString();
	}

	/**
	 * 接口：查看log
	 * 
	 * @param response
	 * @param time
	 * @param level
	 * @param _pageNum
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/{level}")
	public ModelAndView logToMap(HttpServletResponse response,
			@RequestParam(value = "time", defaultValue = "") String time, @PathVariable("level") String level,
			@RequestParam(defaultValue = "1") int _pageNum) throws Exception {
		_pageNum = _pageNum < 1 ? 1 : _pageNum;
		Page model;
		if (StringUtils.isEmpty(time)) {
			model = logService.findAllToMap(level, _pageNum);
		} else {
			model = logService.findAllByTimeToMap(level, time, _pageNum);
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
