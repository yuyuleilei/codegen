<#include "include/head.ftl">
<#include "include/copyright.ftl">
<#assign isCbd = false>
<#assign idJavaType="long">
<#list table.columnList as column>
    <#if column.columnName?lower_case=='creator'>
        <#assign isCbd = true>
    </#if>
    <#if column.columnName?lower_case=='id'>
        <#assign idJavaType = column.columnSimpleClassName>
    </#if>
</#list>

package ${NamespaceParam};

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
<#if isCbd>
import ${NamespaceProject}.param.CreateBaseParam;
<#else>
import ${NamespaceProject}.param.BaseParam;
</#if>

import java.util.*;

/**
 * 《${tableLabel}》 查询参数实体
 * @author ${copyright.author}
 *
 */
public class ${Po}Param extends <#if isCbd>CreateBaseParam<${idJavaType}><#else>BaseParam<${idJavaType}></#if> {
	private static final long serialVersionUID = 1L;
	
	<#list table.columnList as column>
	<#if column.columnName?lower_case='id'||column.columnName?lower_case='creator'||column.columnName?lower_case='createdate'||column.columnName?lower_case='lastmodifier'||column.columnName?lower_case='lastmoddate'||column.columnName?lower_case='status'>
	<#else>
	/**
	*字段常量——${column.columnLabel}
	*/
	public static final String F_${column.columnName?cap_first}="${column.columnName}";
	</#if>
	</#list>
	
	<#list table.columnList as column>
	<#if column.columnName?lower_case='id'||column.columnName?lower_case='creator'||column.columnName?lower_case='createdate'||column.columnName?lower_case='lastmodifier'||column.columnName?lower_case='lastmoddate'||column.columnName?lower_case='status'>
	<#else>
	private ${column.columnSimpleClassName} ${column.columnName?uncap_first}; //${column.columnLabel}
	</#if>
	</#list>
    
	/**
	 *默认空构造函数
	 */
	public ${Po}Param() {
		super();
	}
	 
	<#list table.columnList as column>
	<#if column.columnName?lower_case='id'||column.columnName?lower_case='creator'||column.columnName?lower_case='createdate'||column.columnName?lower_case='lastmodifier'||column.columnName?lower_case='lastmoddate'||column.columnName?lower_case='status'>
	<#else>
	/**
	 * @return ${column.columnName?uncap_first} ${column.columnLabel}
	 */
	public ${column.columnSimpleClassName} get${column.columnName?cap_first}(){
		return this.${column.columnName?uncap_first};
	}
	/**
	 * @param ${column.columnName?uncap_first} ${column.columnLabel}
	 */
	public void set${column.columnName?cap_first}(${column.columnSimpleClassName} ${column.columnName?uncap_first}){
		this.${column.columnName?uncap_first} = ${column.columnName?uncap_first};
	}
	</#if>
	</#list>
	
	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
		<#list table.columnList as column>
			.append("${column.columnName}",get${column.columnName?cap_first}())
		</#list>
			.toString();
	}
	
}
