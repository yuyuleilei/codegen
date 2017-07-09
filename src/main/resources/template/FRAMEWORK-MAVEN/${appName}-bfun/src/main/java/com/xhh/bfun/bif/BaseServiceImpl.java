package com.xhh.bfun.bif;

import java.util.List;

import com.xhh.bfun.benum.BaseEnum.BusinessExceptionEnum;
import com.xhh.bfun.bexception.BusinessException;
import com.xhh.bfun.bmodel.Conditions;
import com.xhh.bfun.bmodel.JsonResult;
import com.xhh.bfun.bmodel.Page;
import com.xhh.bfun.butils.JsonResultUtil;

public abstract class BaseServiceImpl<T,PK> implements BaseService<T, PK>{
	
	private BaseMapper<T, PK> mapper;
	
	protected abstract BaseMapper<T, PK> initMapper();
	
	private BaseMapper<T, PK> getMapper(){
		if (mapper==null) {
			mapper = initMapper();
		}
		return mapper;
	}
	
	@Override
	public JsonResult add(T entity) throws BusinessException{
		try {
			long i = getMapper().insert(entity);
			if (i<1) {
				return JsonResultUtil.error(BusinessExceptionEnum.INSERT);
			}
		} catch (Exception e) {
			throw new BusinessException(BusinessExceptionEnum.INSERT,e);
		}
		return JsonResultUtil.success();
	}

	@Override
	public JsonResult addNotNull(T entity) throws BusinessException{
		try {
			long i = getMapper().insertNotNull(entity);
			if (i<1) {
				return JsonResultUtil.error(BusinessExceptionEnum.INSERT);
			}
		} catch (Exception e) {
			throw new BusinessException(BusinessExceptionEnum.INSERT,e);
		}
		return JsonResultUtil.success();
	}
	
	@Override
	public JsonResult update(T entity) throws BusinessException{
		try {
			int i = getMapper().update(entity);
			if (i<1) {
				return JsonResultUtil.error(BusinessExceptionEnum.UPDATE);
			}
		} catch (Exception e) {
			throw new BusinessException(BusinessExceptionEnum.UPDATE,e);
		}
		return JsonResultUtil.success();
	}

	@Override
	public JsonResult updateNotNull(T entity) throws BusinessException{
		try {
			int i = getMapper().updateNotNull(entity);
			if (i<1) {
				return JsonResultUtil.error(BusinessExceptionEnum.UPDATE);
			}
		} catch (Exception e) {
			throw new BusinessException(BusinessExceptionEnum.UPDATE,e);
		}
		return JsonResultUtil.success();
	}

	@Override
	public JsonResult delById(PK id) throws BusinessException{
		try {
			int i = getMapper().deleteById(id);
			if (i<1) {
				return JsonResultUtil.error(BusinessExceptionEnum.DELETE);
			}
		} catch (Exception e) {
			throw new BusinessException(BusinessExceptionEnum.DELETE,e);
		}
		return JsonResultUtil.success();
	}

	@Override
	public T getById(PK id) {
		return getMapper().findById(id);
	}
	
	@Override
	public T getByEntity(T entity) {
		return getMapper().findByEntity(entity);
	}
	
	@Override
	public List<T> getListByEntity(T entity) {
		return getMapper().findListByEntity(entity);
	}
	
	@Override
	public Long getCount(Conditions condition) {
		return getMapper().findCount(condition);
	}

	@Override
	public List<T> getList(Conditions condition) {
		return getMapper().findList(condition);
	}

	@Override
	public T getBy(Conditions condition) {
		return getMapper().findByCondition(condition);
	}

	@Override
	public Page<T> getPage(Conditions condition) {
		List<T> list = getMapper().findList(condition);
		Long num = getMapper().findCount(condition);
		Page<T> page = new Page<T>();
		page.setList(list);
		page.setTotalCount(num);
		return page;
	}
	
}
