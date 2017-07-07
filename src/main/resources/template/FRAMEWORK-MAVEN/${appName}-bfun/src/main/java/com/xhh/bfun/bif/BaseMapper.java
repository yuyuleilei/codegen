package com.xhh.bfun.bif;

import java.util.List;

import com.xhh.bfun.bmodel.Conditions;
import com.xhh.bfun.bmodel.Page;
import com.xhh.bfun.bmodel.UpdateParams;

public interface BaseMapper<T, PK> {

	/**
	 * 保存对象
	 * 
	 * @param entity
	 *            
	 * @return id 
	 */
	public abstract long insert(T entity) ;

	/**
	 * @category 为空的不添加，效率最高 
	 * @param entity
	 * @return
	 * @time 2016年10月11日 下午9:55:59
	 */
	public abstract long insertNotNull(T entity);

	/**
	 * 批量保存对象
	 * 
	 * @param entity
	 *            
	 * @return id 
	 */
	public abstract long batchInsert(List<T> list);
	
	/**
	 * 根据主键更新对象
	 * 
	 * @param entity
	 *            
	 * @return int 
	 */
	public abstract int update(T entity) ;

	/**
	 * @category 更新不为空的值 效率最高 
	 * @author hejianhui@wegooooo.com
	 * @param entity
	 * @return
	 * @time 2016年10月11日 下午9:58:43
	 */
	public abstract int updateNotNull(T entity) ;

	/**
	 * @category 自定义更新 
	 * @author hejianhui@wegooooo.com
	 * @param params
	 * @return
	 * @time 2016年10月13日 下午5:18:52
	 */
	public abstract int updateCustom(UpdateParams params) ;

	/**
	 * @category 根据主键删除，慎用
	 * @param params
	 * @return
	 * @time 2016年10月13日 下午5:18:52
	 */
	public abstract int deleteById(PK id) ;

	/**
	 * @category 根据主键批量删除，慎用
	 * @param ids
	 * @return
	 * @time 2016年10月14日 下午4:50:14
	 */
	public abstract int deleteByIds(PK[] ids) ;

	/**
	 * @category 自定义删除，慎用 
	 * @param conditions
	 * @return
	 * @time 2016年10月14日 下午4:54:06
	 */
	public abstract int deleteCustom(Conditions conditions) ;

	/**
	 * @category 根据主键查询
	 * @param params
	 * @return
	 * @time 2016年10月13日 下午5:18:52
	 */
	public abstract T findById(PK id);
	
	/**
	 * @category 根据对象查询
	 * @param params
	 * @return
	 * @time 2016年10月13日 下午5:18:52
	 */
	public abstract T findByEntity(T entity);

	/**
	 * @category 自定义查询
	 * @author hejianhui@wegooooo.com
	 * @param params
	 * @return
	 * @time 2016年10月13日 下午5:18:52
	 */
	public abstract List<T> findList(Conditions condition);

	/**
	 * @category 自定义查询
	 * @param params
	 * @return
	 * @time 2016年10月13日 下午5:18:52
	 */
	public abstract T findByCondition(Conditions condition);
	
	/**
	 * @category 获取数量
	 * @param condition
	 * @return
	 * @time 2016年10月18日 下午2:18:15
	 */
	public abstract Long findCount(Conditions condition);

	/**
	 * @category 分页 
	 * @param condition
	 * @param page
	 * @return
	 * @time 2016年10月14日 下午8:13:05
	 */
	public abstract Page<T> findPage(Conditions condition,
			Page<T> page);

}