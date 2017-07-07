<#include "include/head.ftl">

package ${NamespaceServiceImpl};

import ${NamespaceDao}.${Po}Mapper;
import ${NamespaceDomain}.${Po};
import ${NamespaceService}.${Po}Service;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ${bfun}.bif.*;
import ${CommonModelDir}.exception.BusinessException;
import ${bfun}.bmodel.JsonResult;
import ${bfun}.bmodel.JsonResultUtil;


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
		${po}Mapper.batchInsert(list);
		return JsonResultUtil.success();
	}
	
	@Override
	@Transactional
	public JsonResult update(UpdateParams params) throws BusinessException {
		${po}Mapper.updateCustom(params);
		return JsonResultUtil.success();
	}

	@Override
	@Transactional
	public JsonResult delByIds(String[] ids) throws BusinessException {
		${po}Mapper.deleteByIds(ids);
		return JsonResultUtil.success();
	}

	@Override
	@Transactional
	public JsonResult delBy(Conditions conditions) throws BusinessException {
		${po}Mapper.deleteCustom(conditions);
		return JsonResultUtil.success();
	}

}