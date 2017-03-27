package cn.cjp.logger.web.controller;

import java.util.Enumeration;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class IndexController {

	private static final ModelAndView EMPTY_MV = new ModelAndView("/index");

	@RequestMapping
	public ModelAndView request(HttpServletRequest request) {
		Enumeration<String> keys = request.getHeaderNames();
		while(keys.hasMoreElements()) {
			String key = keys.nextElement();
			System.out.println(key + " : " + request.getHeader(key));
		}
		Cookie[] cookies = request.getCookies();
		System.out.println(cookies);
		return EMPTY_MV;
	}

}
