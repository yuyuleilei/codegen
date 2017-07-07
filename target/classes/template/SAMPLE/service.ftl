<#include "include/head.ftl">

package ${NamespaceService};

import ${NamespaceDomain}.${Po};
import ${bfun}.bif.BaseService;

 /**
 * 《${tableLabel}》 业务逻辑服务接口
 * @author ${copyright.author}
 */
public interface ${Po}Service extends BaseService<${Po}, ${pkcolumnSimpleClassName}>{

}