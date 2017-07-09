<#include "include/head.ftl" />
<#macro mapperEl value>${r"#{"}${value}}</#macro>
<#macro conditionMapperEl value>${r"#{condition."}${value}}</#macro>
<#macro jspEl value>${r"${"}${value}}</#macro>
<#assign idJavaType="java.lang.Long" />
<#assign pkName="ID" />
<#list table.columnList as column>
	<#if column.primaryKey>
	<#assign pkName="${column.fieldName}" />
    </#if>
</#list>
<#list table.columnList as column>
    <#if column.primaryKey==true>
        <#assign idJavaType = column.columnClassName />
    </#if>
</#list>
<#assign entity="${NamespaceDomain}.${Po}" />
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="${MapperDir}.${Po}Mapper">
	<resultMap type="${entity}" id="BaseResultMap">
	  <#list table.columnList as column>
      <result column="${column.fieldName}" property="${column.columnName}"/>
	  </#list>
	</resultMap>
	
	<sql id="Conditions_Where_Clause">
 	  <foreach collection="oredCriteria" open="where" item="o" separator="or">
	   <if test="o.valid">
	    (
	     <trim suffixOverrides="and">
	     <foreach collection="o.criteriaWithoutValue" item="oc" separator="and"><@jspEl 'oc'/> and </foreach>
	     <foreach collection="o.criteriaWithSingleValue" item="os"><@jspEl 'os.condition'/> <@mapperEl 'os.val'/> and </foreach>
	     <foreach collection="o.criteriaWithBetweenValue" item="ob"><@jspEl 'ob.condition'/> <@mapperEl 'ob.val[0]'/> and <@mapperEl 'ob.val[1]'/> and</foreach>
	     <foreach collection="o.criteriaWithListValue" item="ol"><@jspEl 'ol.condition'/>
	       <foreach collection="ol.val" open="(" close=")" item="olv" separator=","><@mapperEl 'olv'/></foreach>
	     </foreach>
	     </trim>
	    )
	   </if>
 	  </foreach>
	</sql>
	
	<sql id="Update_Conditions_Where_Clause">
	 <if test="conditions != null">
 	  <foreach collection="conditions.oredCriteria" open="where" item="o" separator="or">
	   <if test="o.valid">
	    (
	     <trim suffixOverrides="and">
	     <foreach collection="o.criteriaWithoutValue" item="oc" separator="and"><@jspEl 'oc'/> and </foreach>
	     <foreach collection="o.criteriaWithSingleValue" item="os"><@jspEl 'os.condition'/> <@mapperEl 'os.val'/> and </foreach>
	     <foreach collection="o.criteriaWithBetweenValue" item="ob"><@jspEl 'ob.condition'/> <@mapperEl 'ob.val[0]'/> and <@mapperEl 'ob.val[1]'/> and</foreach>
	     <foreach collection="o.criteriaWithListValue" item="ol"><@jspEl 'ol.condition'/>
	       <foreach collection="ol.val" open="(" close=")" item="olv" separator=","><@mapperEl 'olv'/></foreach>
	     </foreach>
	     </trim>
	    )
	   </if>
 	  </foreach>
     </if>
	</sql>
	
    <sql id="Base_Column_List">
        <#list table.columnList as column>
        ${column.fieldName}<#if column_has_next>,</#if>
        </#list>
    </sql>
    
    <insert id="insert" keyProperty="${primaryKey}" useGeneratedKeys="true" parameterType="${entity}">
    	<selectKey resultType="${idJavaType}" order="AFTER" keyProperty="${primaryKey}">
            SELECT LAST_INSERT_ID() AS ID
        </selectKey>
        INSERT INTO ${tableName} (
		    <#list table.columnList as column>
		    ${column.fieldName}<#if column_has_next>,</#if>
		    </#list>
        ) VALUES (
		    <#list table.columnList as column>
		    <#if column.primaryKey=true>
		     REPLACE(UUID(),'-','')<#if column_has_next>,</#if>
		    </#if>
		    <#if column.primaryKey=false>
		    ${'#'}{${column.columnName?uncap_first}}<#if column_has_next>,</#if>
		    </#if>
		    </#list>
        )
    </insert>
    
    <insert id="insertNotNull" keyProperty="${primaryKey}" useGeneratedKeys="true" parameterType="${entity}">
    <selectKey resultType="${idJavaType}" order="AFTER" keyProperty="${primaryKey}">
        SELECT LAST_INSERT_ID() AS ID
    </selectKey>
    insert into ${tableName}
    <trim prefix="(" suffix=")" suffixOverrides=",">
     <#list table.columnList as column>
     	<#if column.primaryKey=true>
	     ${column.fieldName}<#if column_has_next>,</#if>
	    </#if>
	    <#if column.primaryKey=false>
	    <if test="${column.columnName} != null and ${column.columnName} != ''">
	    ${column.fieldName}<#if column_has_next>,</#if>
	    </if>
	    </#if>
	 </#list>
    </trim>
    values
    <trim prefix="(" suffix=")" suffixOverrides=",">
     <#list table.columnList as column>
     	<#if column.primaryKey=true>
	     REPLACE(UUID(),'-','')<#if column_has_next>,</#if>
	    </#if>
     	<#if column.primaryKey=false>
     	<if test="${column.columnName} != null and ${column.columnName} != ''">
	    ${'#'}{${column.columnName?uncap_first}}<#if column_has_next>,</#if>
	    </if>
	    </#if>
	 </#list>
    </trim>
  </insert>
	
	<insert id="batchInsert"  parameterType="java.util.List">
		INSERT INTO ${tableName} (
		    <#list table.columnList as column>
		   	${column.fieldName}<#if column_has_next>,</#if>
		    </#list>
        ) VALUES
		<foreach collection="list" item="item" index="index" separator="," > 
			(
			<#list table.columnList as column>
	        ${"#{"}item.${column.columnName}${"}"}<#if column_has_next>,</#if>
	        </#list>
	        )
		</foreach> 
	</insert>
	
    <!-- 更新 -->
    <update id="update" parameterType="${entity}">
        UPDATE ${tableName}
        <trim prefix="SET" suffixOverrides=",">
            <#list table.columnList as column>
		    <#if column.primaryKey=false>
		    ${column.fieldName}=${'#'}{${column.columnName?uncap_first}}<#if column_has_next>,</#if>
		    </#if>
		    </#list>
        </trim>
        WHERE
        <#assign hasData=false>
        <#list table.columnList as column>
		<#if column.primaryKey>
        ${column.fieldName} = ${'#'}{${column.columnName?uncap_first}}<#if hasData==false><#assign hasData=true><#else> AND </#if>
        </#if>
		</#list>
    </update>
	
    <update id="updateNotNull" parameterType="${entity}">
        UPDATE ${tableName}
        <trim prefix="SET" suffixOverrides=",">
            <#list table.columnList as column>
		    <#if column.primaryKey=false>
            <if test="${column.columnName?uncap_first} != null <#if column.columnClassName=='java.lang.String'> and ${column.columnName} != '' </#if>">
                ${column.fieldName} = ${'#'}{${column.columnName?uncap_first}}<#if column_has_next>,</#if>
            </if>
            </#if>
		    </#list>
        </trim>
        <#list table.columnList as column>
	    <#if column.primaryKey=true>
        WHERE ${pkName}=<@mapperEl column.columnName?uncap_first />
        </#if>
	    </#list>
    </update>

    <update id="updateCustom" parameterType="${UpdateParams}">
    UPDATE ${tableName}
    <trim prefix="SET" suffixOverrides=",">
    	<foreach collection="fields" item="item" index="index" separator="," >
        <@jspEl 'item'/>  ${"#{"}values[${"${"}index}]}
        </foreach>
    </trim>
	<include refid="Update_Conditions_Where_Clause" />	
	<if test="conditions != null">
		<if test="conditions.orderByClause != null"> <@jspEl 'conditions.orderByClause'/></if> 
	</if>	
    </update>
    
    <!-- 按Id删除 -->
    <delete id="deleteById" parameterType="${idJavaType}">
        DELETE FROM ${tableName}
        <trim prefix="WHERE" prefixOverrides="AND | OR">
            <#list table.columnList as column>
			<#if column.primaryKey>
            AND ${column.fieldName} = <@mapperEl 'id'/>
            </#if>
			</#list>
        </trim>
    </delete>

    <!--根据list(ids)删除对象-->
    <delete id="deleteByIds">
        DELETE FROM ${tableName}
        WHERE id in
        <foreach collection="list" item="id" open="(" separator="," close=")"><@mapperEl 'id'/></foreach>
    </delete>

	<!--根据自定义删除对象-->
    <delete id="deleteCustom" parameterType="${Conditions}">
        DELETE FROM ${tableName}
        <include refid="Conditions_Where_Clause" />
    </delete>
    
    <select id="findById" parameterType="${idJavaType}" resultMap="BaseResultMap">
        SELECT
	    <include refid="Base_Column_List" />
        FROM ${tableName} 
        WHERE 
        ${pkName} = <@mapperEl 'id'/>
        LIMIT 0,1
    </select>
    
    <select id="findByEntity" parameterType="${entity}" resultMap="BaseResultMap">
        SELECT  
	    <include refid="Base_Column_List" />
        FROM ${tableName} 
        WHERE 1 = 1
        <#list table.columnList as column>
	     	<if test="${column.columnName} != null <#if column.columnClassName=='java.lang.String'> and ${column.columnName} != '' </#if>">
		   	 and ${column.fieldName}=${'#'}{${column.columnName?uncap_first}}
		    </if>
	 	</#list>
	 	LIMIT 0,1
    </select>
    
    <select id="findListByEntity" parameterType="${entity}" resultMap="BaseResultMap">
        SELECT  
	    <include refid="Base_Column_List" />
        FROM ${tableName} 
        WHERE 1 = 1
        <#list table.columnList as column>
	     	<if test="${column.columnName} != null <#if column.columnClassName=='java.lang.String'> and ${column.columnName} != '' </#if>">
		   	 and ${column.fieldName}=${'#'}{${column.columnName?uncap_first}}
		    </if>
	 	</#list>
    </select>

    <select id="findByCondition" parameterType="${Conditions}" resultMap="BaseResultMap">
        SELECT 
        <choose>
           <when test="returnFields != null">
           <foreach collection="returnFields" item="f" separator=","><@jspEl 'f'/></foreach>
           </when>
           <otherwise>
	       <include refid="Base_Column_List" />
           </otherwise>
        </choose>
        FROM ${tableName}
        <if test="connectTable != null">
	        <foreach collection="connectTable" item="tb">
	       	,<@jspEl 'tb'/>
	       	</foreach>
        </if>
        <include refid="Conditions_Where_Clause" />
        LIMIT 0,1
    </select>

    <select id="findList"  parameterType="${Conditions}" resultMap="BaseResultMap">
        SELECT
        <if test="distinct != false">
        DISTINCT
        </if>
        <choose>
           <when test="returnFields != null">
           <foreach collection="returnFields" item="f" separator=","><@jspEl 'f'/></foreach>
           </when>
           <otherwise>
	       <include refid="Base_Column_List" />
           </otherwise>
        </choose>
        FROM ${tableName}
         <if test="connectTable != null">
	        <foreach collection="connectTable" item="tb">
	       	,<@jspEl 'tb'/>
	       	</foreach>
        </if>
        <include refid="Conditions_Where_Clause" />
        <if test="orderByClause != null"><@jspEl 'orderByClause'/></if>
        <if test="limit != ''"><@jspEl 'limit'/></if>
    </select>

    <select id="findCount" parameterType="${Conditions}" resultType="java.lang.Long">
        SELECT
        COUNT(1)
        FROM ${tableName}
        <if test="connectTable != null">
	        <foreach collection="connectTable" item="tb">
	       	,<@jspEl 'tb'/>
	       	</foreach>
        </if>
        <include refid="Conditions_Where_Clause" />
    </select>

    <select id="selectMaxId" resultType="${idJavaType}">
        SELECT
        MAX(id)
        FROM ${tableName}
    </select>
   
</mapper>