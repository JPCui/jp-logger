package cn.cjp.logger.util;

import java.util.List;

public class Page {

	/**
	 * 当前页
	 */
	public int currPage;
	/**
	 * 下一页
	 */
	public int nextPage;
	/**
	 * size of page
	 */
	public int sizeOfPage;
	/**
	 * 前一页
	 */
	public int prevPage;
	/**
	 * 总页数
	 */
	public int pageCount;
	/**
	 * 当前页开始的行号
	 */
	public long startRowInCurrPage;
	/**
	 * 当前页结束的行号
	 */
	public long endRowInCurrPage;
	/**
	 * 数据总数
	 */
	public long countOfData;
	
	@SuppressWarnings("rawtypes")
	private List resultList;
	
	private Object object;

	/**
	 * 初始化PageUtil
	 * 
	 * @param currPage
	 *            当前页
	 * @param sizeOfPage
	 *            每页大小
	 * @param countOfData
	 *            数据总数
	 */
	public Page(int currPage, int sizeOfPage, long countOfData) {
		this.currPage = currPage;
		this.sizeOfPage = sizeOfPage;
		this.countOfData = countOfData;

		this.compute();
	}
	
	public Page(int currPage, int sizeOfPage, long countOfData, List<Object> resultList) {
		this(currPage, sizeOfPage, countOfData);
		this.setResultList(resultList);
	}
	
	/**
	 * 计算出所有属性的值
	 */
	private void compute(){
		pageCount = (int) Math.ceil(countOfData * 1.0 / sizeOfPage);
		prevPage = currPage > 1 ? currPage - 1 : currPage;
		nextPage = currPage < pageCount ? currPage + 1 : pageCount;
		
		startRowInCurrPage = (this.currPage-1)* sizeOfPage + 1;
		if(currPage == pageCount){
			endRowInCurrPage = countOfData;
		}else{
			endRowInCurrPage = currPage * sizeOfPage;
		}
	}

	/**
	 * @return the resultList
	 */
	@SuppressWarnings("rawtypes")
	public List getResultList() {
		return resultList;
	}

	/**
	 * @param resultList the resultList to set
	 */
	@SuppressWarnings("rawtypes")
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

	/**
	 * @return the object
	 */
	public Object getObject() {
		return object;
	}

	/**
	 * @param object the object to set
	 */
	public void setObject(Object object) {
		this.object = object;
	}

}
