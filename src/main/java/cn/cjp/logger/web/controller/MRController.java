package cn.cjp.logger.web.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.cjp.logger.service.mr.Configuration;
import cn.cjp.logger.service.mr.DailyActiveService;
import cn.cjp.logger.service.mr.SimpleMapReduce;

@Controller
@RequestMapping("/mr")
public class MRController {

	@Autowired
	SimpleMapReduce simpleMapReduce;

	@Autowired
	DailyActiveService dailyActiveService;

	@RequestMapping(value = "")
	public ModelAndView index(Date day) {
		ModelAndView mv = new ModelAndView("/mr/index");
		return mv;
	}

	@RequestMapping(value = "/{project}")
	public ModelAndView simple(@PathVariable("project") String project, HttpServletResponse response) {
		if (Configuration.exists(project)) {
			ModelAndView mv = new ModelAndView("/mr/" + project);
			mv.addObject("datas", simpleMapReduce.mr(project));
			return mv;
		}
		response.setStatus(HttpStatus.NOT_FOUND.value());
		return null;
	}

	@RequestMapping(value = "/area_distribution")
	public ModelAndView areaDistribution(HttpServletResponse response) {
		final String project = "count_ip";
		if (Configuration.exists(project)) {
			Map<String, Double> ipNumMap = simpleMapReduce.mr(project);

			ModelAndView mv = new ModelAndView("/mr/area_distribution");
			mv.addObject("datas", ipNumMap);
			return mv;
		}
		response.setStatus(HttpStatus.NOT_FOUND.value());
		return null;
	}

	@RequestMapping(value = "/daily_active")
	public ModelAndView toDailyActive(Date day) {
		ModelAndView mv = new ModelAndView("/mr/daily_active");
		return mv;
	}

	@RequestMapping(value = "/daily_active.json")
	@ResponseBody
	public List<Object> dailyActive(Date day) {
		if (day == null) {
			day = new Date();
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(day);
		List<Object> data = dailyActiveService.mr(cal);
		Map<String, Object> map = new HashMap<>();
		map.put("date", DateFormatUtils.format(day, "yyyy-MM-dd"));
		map.put("datas", data);

		Date day2 = DateUtils.addDays(day, -1);
		cal.setTime(day2);
		List<Object> data2 = dailyActiveService.mr(cal);
		Map<String, Object> map2 = new HashMap<>();
		map2.put("date", DateFormatUtils.format(day2, "yyyy-MM-dd"));
		map2.put("datas", data2);

		Date day3 = DateUtils.addDays(day2, -1);
		cal.setTime(day3);
		List<Object> data3 = dailyActiveService.mr(cal);
		Map<String, Object> map3 = new HashMap<>();
		map3.put("date", DateFormatUtils.format(day3, "yyyy-MM-dd"));
		map3.put("datas", data3);

		List<Object> rs = new ArrayList<>();
		rs.add(map);
		rs.add(map2);
		rs.add(map3);
		return rs;
	}

}
