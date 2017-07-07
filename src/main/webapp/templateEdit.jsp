<%@page import="com.xhh.codegen.util.BuildHelper"%>
<%@page import="com.xhh.codegen.util.ProjectConfig"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="java.io.File"%>
<%@page import="com.xhh.codegen.util.FileUtil"%>
<%@page import="java.net.URLDecoder"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

String projectName="";
String[] paramKvp = request.getQueryString().split("&");
for(String kvp : paramKvp){
	String key = kvp.split("=")[0];
	String value = kvp.split("=")[1];
	if("projectName".equals(key)){
		projectName = value;
		break;
	}
}
ProjectConfig projectConfig = BuildHelper.getProjectConfig(getServletContext(),projectName);
String encoding = projectConfig.getOutputEncoding();
request.setCharacterEncoding(encoding);
response.setCharacterEncoding(encoding);

String method = request.getParameter("method");
String filename = URLDecoder.decode(request.getParameter("filename"),"UTF-8");
String fileContent =  request.getParameter("fileContent");

if("input".equals(method)||"cancel".equals(method)){
	fileContent = FileUtil.copyToString(filename);
	if(StringUtils.isNotBlank(fileContent)){
		fileContent = fileContent.replace("<", "&lt;").replace(">", "&gt;");
	}

}else if("save".equals(method)){
	if(StringUtils.isNotBlank(fileContent)){
		FileUtil.newFile(filename, fileContent, encoding);
	}
}
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'templateEdit.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<link rel="stylesheet" type="text/css" href="<%=basePath%>js/easyui/themes/default/easyui.css">
	<link rel="stylesheet" type="text/css" href="<%=basePath%>js/easyui/themes/icon.css">
	<script type="text/javascript" src="<%=basePath%>js/jquery-1.8.0.min.js"></script>
	<script type="text/javascript" src="<%=basePath%>js/easyui/jquery.easyui.min.js"></script>
	<script type="text/javascript" src="<%=basePath%>js/easyui/locale/easyui-lang-zh_CN.js"></script>	
	
	<link href="<%=basePath%>js/prettify/prettify.css" type="text/css" rel="stylesheet" />
	<script type="text/javascript" src="<%=basePath%>js/prettify/prettify.js"></script>

	<script type="text/javascript">
	var hasChange = false;
	function operate(method){
		if(method=="edit"){
			$("pre,#btnEdit").hide();
			$("#fileContent,#btnCancel,#btnSave").show();
			$("#fileContent").closest("div").css("overflow","hidden");
			return;
		}else if(method=="cancel"){
			if(hasChange&&confirm("内容已修改但未保存，确定要返回吗？")==false){
				return;
			}
		}
			
		jQuery("#method").val(method);	//alert(jQuery("#fileContent").text());
		document.forms[0].action = "<%=basePath%>templateEdit.jsp?projectName=<%=projectName%>";
		document.forms[0].submit();
	}
	
	$(function(){
		$("#btnSave,#btnCancel").hide();
		$("#fileContent").change(function(){ hasChange = true; });
		
		prettyPrint();
		
		$("#topDiv").resize(function(){ //alert($("#mainDiv").height());
			$("#bottomDiv").height($("#mainDiv").height()-$("#topDiv").height()-30);
		}).trigger("resize");
	});
	</script>
	
  </head>
  
  <body>
  <div id="mainDiv" class="easyui-panel" maximized="true" >
  	<form name="form1"  method="post" >
  		<input type="hidden" id="method" name="method" value="<%=method %>" > 
  		<input type="hidden" id="filename" name="filename" value="<%=filename %>" > 
  		
  		<div id="topDiv" style="margin: 5px 20px 5px 20px;text-align: right;height: 15px;">
  			<a id="btnEdit" href="javascript:operate('edit');" class="easyui-linkbutton" data-options="iconCls:'icon-edit'"> 编 辑  </a>
  			<a id="btnSave" href="javascript:operate('save');" class="easyui-linkbutton" data-options="iconCls:'icon-save'"> 保 存  </a>&nbsp;
  			<a id="btnCancel" href="javascript:operate('cancel');" class="easyui-linkbutton" data-options="iconCls:'icon-back'"> 返 回  </a>
  		</div>
  		<div id="bottomDiv" style="overflow: scroll;height: 200px;border:1px solid #888888;">
  		<textarea id="fileContent" name="fileContent" style="width: 100%;height: 100%;  display: none;">
<%=fileContent %>
  		</textarea> 	
  		<pre class="prettyprint <%=BuildHelper.getPrettifyLang(filename) %> " >
<%=fileContent %>
  		</pre>
  		</div>
  	</form>
  	</div>
  </body>
</html>
