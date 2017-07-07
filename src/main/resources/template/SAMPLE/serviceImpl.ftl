<#include "include/head.ftl">

package ${serviceImplDir};

import ${MapperDir}.${Po}Mapper;
import ${ModelDir}.${Po};
import ${serviceDir}.${Po}Service;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ${bfun}.bif.*;
import ${bfun}.bexception.BusinessException;
import ${bfun}.bmodel.*;
import ${bfun}.benum.BaseEnum.BusinessExceptionEnum;
import ${bfun}.butils.JsonResultUtil;


 /**
 * 《${tableLabel}》 业务逻辑服务类
 * @author ${copyright.author}
 *
 */
@Service("${po}Service")
public class ${Po}ServiceImpl extends BaseServiceImpl<${Po}, ${pkcolumnSimpleClassName}> implements ${Po}Service{

	@Autowired
	protected ${Po}Mapper ${po}Mapper;
	
	@Override
	protected BaseMapper<${Po}, ${pkcolumnSimpleClassName}> initMapper() {
		return ${po}Mapper;
	}
	
	@Override
	@Transactional
	public JsonResult add(List<${Po}> list) throws BusinessException {
		try {
			${po}Mapper.batchInsert(list);
		} catch (Exception e) {
			throw new BusinessException(BusinessExceptionEnum.INSERT,e);
		}
		return JsonResultUtil.success();
	}
	
	@Override
	@Transactional
	public JsonResult update(UpdateParams params) throws BusinessException {
		try {
			${po}Mapper.updateCustom(params);
		} catch (Exception e) {
			throw new BusinessException(BusinessExceptionEnum.UPDATE,e);
		}
		return JsonResultUtil.success();
	}

	@Override
	@Transactional
	public JsonResult delByIds(String[] ids) throws BusinessException {
		try {
			${po}Mapper.deleteByIds(ids);
		} catch (Exception e) {
			throw new BusinessException(BusinessExceptionEnum.DELETE,e);
		}
		return JsonResultUtil.success();
	}

	@Override
	@Transactional
	public JsonResult delBy(Conditions conditions) throws BusinessException {
		try {
			${po}Mapper.deleteCustom(conditions);
		} catch (Exception e) {
			throw new BusinessException(BusinessExceptionEnum.DELETE,e);
		}
		return JsonResultUtil.success();
	}

}