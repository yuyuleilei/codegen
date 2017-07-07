<#include "include/head.ftl">
package ${NamespaceDao}.impl;

import org.springframework.stereotype.Repository;
import ${NamespaceProject}.base.dao.BaseDaoImpl;
import ${NamespaceDao}.${Po}DAO;
import ${NamespaceDomain}.${Po}Entity;

 /**
 * 《${tableLabel}》 数据访问接口
 * @author ${copyright.author}
 *
 */
@Repository("${po}DAO")
public class ${Po}DAOImpl extends BaseDaoImpl<${Po}Entity,${pkcolumnSimpleClassName}> implements ${Po}DAO{

}