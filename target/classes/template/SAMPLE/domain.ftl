<#include "include/head.ftl">
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

package ${NamespaceDomain};

import java.io.Serializable;
import java.util.*;
import ${bfun}.bannotation.*;

/**
 * 《${tableLabel}》 实体
 * @author ${copyright.author}
 *
 */
@Table(value="${table.tableName}")
public class ${Po} implements Serializable {
	private static final long serialVersionUID = 1L;
	
	<#list table.columnList as column>
	<#if (column.primaryKey && column.columnName?lower_case='id')>
	@PK(value="${column.fieldName}")
	<#else>
	@Column(value="${column.fieldName}")
	</#if>
	private ${column.columnSimpleClassName} ${column.columnName?uncap_first}; //${column.columnLabel}
	
	</#list>
    
	/**
	 *默认空构造函数
	 */
	public ${Po}() {
		super();
	}
	 
	<#list table.columnList as column>
	/**
	 * @return ${column.fieldName} ${column.columnLabel}
	 */
	public ${column.columnSimpleClassName} get${column.columnName?cap_first}(){
		return this.${column.columnName?uncap_first};
	}
	/**
	 * @param ${column.fieldName} ${column.columnLabel}
	 */
	public void set${column.columnName?cap_first}(${column.columnSimpleClassName} ${column.columnName?uncap_first}){
		this.${column.columnName?uncap_first} = ${column.columnName?uncap_first};
	}
	</#list>
	
	public String getTableName() {
		return "${table.tableName}";
	}
	
	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append("${Po} [")
		<#list table.columnList as column>
		<#if column_index==0>
		.append("${column.columnName}=").append(this.get${column.columnName?cap_first}())
		<#else>
		.append(",${column.columnName}=").append(this.get${column.columnName?cap_first}())
		</#if>
		</#list>
		.append("]");
		return builder.toString();
	}
	
	public static enum ${Po}Enum{
		<#list table.columnList as column>
		<#if !column_has_next>
		${column.columnName}("${column.fieldName}","${column.columnLabel}");
		<#else>
		${column.columnName}("${column.fieldName}","${column.columnLabel}"),
		</#if>
		</#list>
		
		private String fieldName;
		private String remark;
		
		${Po}Enum(String fieldName,String remark){
			this.fieldName = fieldName;
			this.remark = remark;
		}
		
		public String get(){
			return this.fieldName;
		}
		
		public String getRemark(){
			return this.remark;
		}
	}
}
