<%@page import="java.io.File"%>
<%@page import="com.xhh.codegen.service.impl.ProjectBuildConfig"%>
<%@page import="com.xhh.codegen.utils.ProjectConfig"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.xhh.codegen.utils.BuildHelper"%>
<%@page import="com.xhh.codegen.utils.MenuUtil"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

response.setContentType("application/json");
response.setCharacterEncoding("UTF-8");
response.setHeader("Cache-Control", "no-cache");

String content = "";
String method = request.getParameter("method");
String projectName = request.getParameter("projectName");
ProjectConfig projectConfig=null;
if(StringUtils.isNotBlank(projectName)){
	projectConfig= BuildHelper.getProjectConfig(getServletContext(), projectName);
}

if("getTableList".equals(method)){
	String tableType = request.getParameter("tableType");
	String tableNamePatterns = request.getParameter("tableNamePatterns");
	content = MenuUtil.buildTableListForJson(projectConfig, tableType,tableNamePatterns);
}else if("getTemplateList".equals(method)){
	String templateDirectory =(String)projectConfig.getDataModelMap().get(ProjectBuildConfig.DMK_TEMPLATEDIRECTORY);
	if(templateDirectory.contains(":")==false){
		templateDirectory = MenuUtil.class.getResource("/").getPath()+templateDirectory;
	}
	File file = new File(templateDirectory);
	content = MenuUtil.buildTemplateListForJson(file,0);
}

response.getWriter().print(content);
   %>