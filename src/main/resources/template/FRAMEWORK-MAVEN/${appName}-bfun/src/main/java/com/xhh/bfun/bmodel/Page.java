package com.xhh.bfun.bmodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页对象. 包含当前页数据及分页信息如总记录数. 页码从１开始
 */
@SuppressWarnings("serial")
public class Page<T> implements Serializable{

	/** 页面默认大小 20条 */
	public static final int DEFAULT_PAGE_SIZE = 20;

	/** 页码 */
	protected int pageIndex = 1;
	/** 页面大小 */
	protected int pageSize = DEFAULT_PAGE_SIZE;
	/** 总记录数 */
	protected long totalCount = -1;
	/** 总页数 */
	protected int pageCount = -1;
	/** 当前页数据 */
	protected List<T> list;
	
	public Page(int pageIndex,int pageSize){
		this.pageIndex = pageIndex;
		this.pageSize = pageSize;
	}

	/**
	 * 构造方法，只构造空页.
	 */
	public Page() {
		this(0, DEFAULT_PAGE_SIZE, 0, new ArrayList<T>());
	}

	/**
	 * 默认构造方法.
	 * 
	 * @param start
	 *            本页数据在数据库中的起始位置
	 * @param totalSize
	 *            数据库中总记录条数
	 * @param pageSize
	 *            本页容量
	 * @param data
	 *            本页包含的数据
	 */
	public Page(int pageIndex, int pageSize, int totalCount, List<T> list) {
		this.pageSize = pageSize;
		this.pageIndex = pageIndex;
		this.totalCount = totalCount;
		this.list = list;
		this.pageCount = computePageCount(totalCount, pageSize);// 计算总页数
	}

	/**
	 * 取总记录数.
	 */
	public long getTotalCount() {
		return this.totalCount;
	}
	
	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
		int pageCount = computePageCount(totalCount, pageSize);
		this.pageCount = pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	/**
	 * 取总页数.
	 */
	public int getPageCount() {
		return pageCount;
	}

	/**
	 * 取每页数据容量.
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * 取当前页中的记录.
	 */
	public List<T> getList() {
		return list;
	}
	
	public void setList(List<T> list) {
		this.list = list;
	}

	/**
	 * 该页是否有下一页.
	 */
	public boolean isHasNextPage() {
		return this.pageIndex < this.getPageCount();
	}

	/**
	 * 该页是否有上一页.
	 */
	public boolean isHasPrevPage() {
		return this.pageIndex > 1;
	}

	/**
	 * 当前页
	 * 
	 * @return
	 */
	public int getPageIndex() {
		return pageIndex;
	}

	/**
	 * <p>当前页记录移量</p>
	 * @return
	 */
	public int getOffset(){
		return computeSkipResult(pageIndex, pageSize);
	}

	/* ==================================================== */

	/**
	 * 计算任一页第一条数据在数据集的位置.
	 * 
	 * @param pageNo
	 *            页码(从1开始)
	 * @param pageSize
	 *            页面大小
	 * @return 该页第一条数据
	 */
	public static int computeSkipResult(int pageIndex, int pageSize) {
		if (pageIndex < 1)
			pageIndex = 1;
		return (pageIndex - 1) * pageSize;
	}
	
	public static int computePageIndex(int skipResults, int pageSize){
		if(pageSize==0){
			return 1;
		}
		return (int)(skipResults/pageSize)+1;
	}
	/**
	 * 计算总页数
	 * 
	 * @param totalCount
	 *            总记录数
	 * @param pageSize
	 *            页面大小
	 * @return 页数
	 */
	public static int computePageCount(long totalCount, int pageSize) {
		int pageCount = 0;
		if (pageSize > 0) {
			if (totalCount % pageSize == 0)
				pageCount = (int) (totalCount / pageSize);
			else
				pageCount = (int) (totalCount / pageSize + 1);
		}
		return pageCount;
	}
	
	
	public static void main(String [] args){
		int pageIndex = 2;
		int pageSize = 10;
		
		int skipResults = computeSkipResult(pageIndex, pageSize);
		
		System.out.println(skipResults);
		
		System.out.println(computePageIndex(0, pageSize));
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer("共").append(this.totalCount).append("记录,").append(this.pageCount + "页,每页").append(this.pageSize).append("条记录,");
		sb.append("本页有").append(this.list.size()).append("条记录");
		sb.append(";当前是第").append(this.pageIndex).append("页,是否有下一页:").append(String.valueOf(this.isHasNextPage())).append(";是否有上一页:")
				.append(String.valueOf(this.isHasPrevPage()));
		return sb.toString();
	}
}