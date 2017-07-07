package com.xhh.bfun.bif;

import java.util.List;

import com.xhh.bfun.bexception.BusinessException;
import com.xhh.bfun.bmodel.Conditions;
import com.xhh.bfun.bmodel.JsonResult;
import com.xhh.bfun.bmodel.Page;
import com.xhh.bfun.bmodel.UpdateParams;

/**
 * @author hejianhui
 */
public interface BaseService<T,PK> {
	
	/**
	 * 保存对象
	 * 
	 * @param entity
	 *            
	 * @return id 
	 */
	public abstract JsonResult add(T entity) throws BusinessException;

	/**
	 * @category 为空的不添加，效率最高 
	 * @param entity
	 * @return
	 * @time 2016年10月11日 下午9:55:59
	 */
	public abstract JsonResult addNotNull(T entity) throws BusinessException;

	/**
	 * 批量保存对象
	 * 
	 * @param entity
	 *            
	 * @return id 
	 */
	public abstract JsonResult add(List<T> list) throws BusinessException;
	
	/**
	 * 根据主键更新对象
	 * 
	 * @param entity
	 *            
	 * @return int 
	 */
	public abstract JsonResult update(T entity) throws BusinessException;

	/**
	 * @category 更新不为空的值 效率最高 
	 * @param entity
	 * @return
	 * @time 2016年10月11日 下午9:58:43
	 */
	public abstract JsonResult updateNotNull(T entity) throws BusinessException;

	/**
	 * @category 自定义更新 
	 * @param params
	 * @return
	 * @time 2016年10月13日 下午5:18:52
	 */
	public abstract JsonResult update(UpdateParams params) throws BusinessException;

	/**
	 * @category 根据主键删除，慎用
	 * @param params
	 * @return
	 * @time 2016年10月13日 下午5:18:52
	 */
	public abstract JsonResult delById(PK id) throws BusinessException;

	/**
	 * @category 根据主键批量删除，慎用
	 * @param ids
	 * @return
	 * @time 2016年10月14日 下午4:50:14
	 */
	public abstract JsonResult delByIds(PK[] ids) throws BusinessException;

	/**
	 * @category 自定义删除，慎用 
	 * @param conditions
	 * @return
	 * @time 2016年10月14日 下午4:54:06
	 */
	public abstract JsonResult delBy(Conditions conditions) throws BusinessException;

	/**
	 * @category 根据主键查询
	 * @param params
	 * @return
	 * @time 2016年10月13日 下午5:18:52
	 */
	public abstract T getById(PK id);
	
	/**
	 * @category 根据主键查询
	 * @param params
	 * @return
	 * @time 2016年10月13日 下午5:18:52
	 */
	public abstract T getByEntity(T entity);

	/**
	 * @category 自定义查询
	 * @param params
	 * @return
	 * @time 2016年10月13日 下午5:18:52
	 */
	public abstract List<T> getList(Conditions conditions);

	/**
	 * @category 自定义查询
	 * @param params
	 * @return
	 * @time 2016年10月13日 下午5:18:52
	 */
	public abstract T getBy(Conditions conditions);
	
	/**
	 * @category 获取数量 
	 * @param condition
	 * @return
	 * @time 2016年10月18日 下午2:21:02
	 */
	public abstract Long getCount(Conditions conditions);

	/**
	 * @category 分页 
	 * @param condition
	 * @param page
	 * @return
	 * @time 2016年10月14日 下午8:13:05
	 */
	public abstract Page<T> getPage(Conditions conditions);

}
