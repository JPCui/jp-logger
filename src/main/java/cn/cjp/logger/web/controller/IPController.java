package cn.cjp.logger.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.cjp.logger.util.IPUtil;

@Controller
@RequestMapping("/ip")
public class IPController {

	@RequestMapping("/info")
	@ResponseBody
	public Object info(String ip) {
		ModelAndView mv = new ModelAndView();
		mv.addObject("ret", 1);
		mv.addObject("province", IPUtil.getCityByIp(ip));
		return mv;
	}

}
