package cn.cjp.freemarker.template.ext;

import java.util.List;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

/**
 * 如果为null，则返回 空字符串
 * 
 * @author Jinpeng Cui
 *
 */
public class StringIgnoreNullMethod implements TemplateMethodModelEx {

	@SuppressWarnings("rawtypes")
	@Override
	public Object exec(List arguments) throws TemplateModelException {
		if (arguments.size() <= 0) {
			throw new TemplateModelException("arguments size must > 0");
		}
		Object arg0 = arguments.get(0);
		if (arg0 == null) {
			return "";
		}
		return arg0.toString();
	}

}
