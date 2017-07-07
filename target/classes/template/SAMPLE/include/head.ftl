<#-- variants  declaration begin -->
<#assign po=(moduleName?uncap_first)>
<#assign Po=(moduleName?cap_first)>
<#--  variants  declaration end -->
<#assign hasPrimaryKey=false>
<#assign primaryKey="">
<#assign primaryColumn="">
<#assign pkcolumnSimpleClassName="">
<#list table.columnList as column>
<#if column.primaryKey>
<#assign hasPrimaryKey=true>
<#assign primaryKey=column.columnName>
<#assign primaryColumn=column>
<#assign pkcolumnSimpleClassName=column.columnSimpleClassName>
</#if>
</#list>