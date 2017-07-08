<%@page import="java.net.URLDecoder"%>
<%@page import="com.xhh.codegen.service.DbProvider"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.io.File"%>
<%@page import="com.xhh.codegen.utils.ZipUtil"%>
<%@page import="com.xhh.codegen.utils.FileUtil"%>
<%@page import="com.xhh.codegen.model.InOutType"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="com.xhh.codegen.service.impl.ProjectBuildConfig"%>
<%@page import="com.xhh.codegen.service.impl.CodeBuilder"%>
<%@page import="com.xhh.codegen.service.Builder"%>
<%@page import="com.xhh.codegen.model.OutputModel"%>
<%@page language="java" import="java.util.*"  pageEncoding="UTF-8"%>
<%@page import="com.xhh.codegen.utils.ProjectConfig"%>
<%@page import="com.xhh.codegen.utils.BuildHelper"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.xhh.codegen.model.TableModel"%>
<%@page import="com.xhh.codegen.utils.BuildHelper"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	request.setCharacterEncoding("UTF-8");
	response.setCharacterEncoding("UTF-8");

	String method = request.getParameter("method");
	
	String projectName =StringUtils.defaultString(request.getParameter("projectName"),"");
	ProjectConfig projectConfig = BuildHelper.getProjectConfig(getServletContext(),projectName);

	String appCode =StringUtils.defaultString(request.getParameter("appCode")
			,projectConfig.getDataModelMap().get("appCode"));
	String appName =StringUtils.defaultString(request.getParameter("appName")
			,projectConfig.getDataModelMap().get("appName"));
	String appLabel = StringUtils.defaultString(request.getParameter("appLabel")
			,projectConfig.getDataModelMap().get("appLabel"));
	String appBaseDir = StringUtils.defaultString(request.getParameter("appBaseDir")
			,projectConfig.getDataModelMap().get("appBaseDir"));
	String author = StringUtils.defaultString(request.getParameter("author"),"");
	   
	
	Map<String,OutputModel> omMap = new LinkedHashMap<String, OutputModel>(); 

  	if("input".equals(method)){
		//从cookies中加载创建人
		Cookie[] cookies = request.getCookies();
		if(cookies!=null&&cookies.length>0){
			for(int i =0; i<cookies.length; i++){
				if("codgen.author".equals(cookies[i].getName())){
					author = URLDecoder.decode(cookies[i].getValue(),"UTF-8");
				}
			}
		}
	}

	projectConfig.getDataModelMap().put("appCode", appCode);
	projectConfig.getDataModelMap().put("appName", appName);
	projectConfig.getDataModelMap().put("appLabel", appLabel);
	projectConfig.getDataModelMap().put("appBaseDir", appBaseDir);

	ProjectBuildConfig buildConfig = new ProjectBuildConfig(projectConfig);
	//设置版本的创建人
	buildConfig.getCopyright().setAuthor(author);

    if("build".equals(method)||"packProject".equals(method)){

    	Builder builder = new CodeBuilder(buildConfig);
		omMap = builder.build();
		for(Entry<String,OutputModel> entry : omMap.entrySet()){
			//System.out.println("生成内容="+entry.getValue().getOutput());
			if(entry.getValue().getType()==InOutType.FILE){
				//out.append("文件没有生成="+entry.getValue().getOutput()
				//		, new File(entry.getValue().getOutput()).exists());
			}
		}

		//为创建人设置cookies
		Cookie cookieAuthor = new Cookie("codgen.author", URLEncoder.encode(author,"UTF-8"));
		cookieAuthor.setMaxAge(0);
		cookieAuthor.setPath("/");
    	response.addCookie(cookieAuthor);
    	
		if("packProject".equals(method)){
			ZipUtil zu = new ZipUtil();  
	        zu.setComment("该压缩包由Codgen代码生成器生成");
	        String outputDir = (String)(buildConfig.getDataModel().get(ProjectBuildConfig.DMK_OUTPUTDIRECTORY));
	        String zipName = new File(outputDir).getName();
	        String zipFilename = getServletContext().getRealPath("/"+zipName+".zip");
	        List<String> filter = new ArrayList<String>();
	        zu.zip(outputDir, zipFilename, filter); 
	        
	        response.setContentType("application/x-download");//设置为下载application/x-download
			String filedownload = "/"+zipName+".zip";//即将下载的文件的相对路径
			String filedisplay = zipName+".zip";//下载文件时显示的文件保存名称
			String filenamedisplay = URLEncoder.encode(filedisplay,"UTF-8");
			response.addHeader("Content-Disposition","attachment;filename=" + filenamedisplay);
			 
			try
			{
				RequestDispatcher dis = application.getRequestDispatcher(filedownload);
				if(dis!= null)
				{
					dis.forward(request,response);
				}
				response.flushBuffer();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
			 	new File(zipFilename).delete();
			}
			out.clear();   
			out = pageContext.pushBody(); 
			//return;
		}
	}else if("cleanMyFolder".equals(method)){
		if(buildConfig.isHasParseDataModel()==false){
			buildConfig.parseDataModel();
		}
		//删除项目输出文件夹
		String outputDir = (String)buildConfig.getDataModel().get(ProjectBuildConfig.DMK_OUTPUTDIRECTORY);
		File outDirFile = new File(outputDir);
		if(outDirFile.exists()){
			FileUtil.deleteDirectory(outDirFile);
		}
	}
	
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title><%=projectConfig.getProjectLabel() %></title>
    
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
	
	<script type="text/javascript">
	
	function operate(method){
		if($("#form1").form("validate")==false){
			return ;
		}
		if(method=="cleanMyFolder"){
			if(confirm("将删除项目【"+$("#appName").val()+"】的输出文件夹，确认吗？")==false){
				return;
			}
		}
		jQuery("#method").val(method);
		document.forms[0].action = "<%=basePath%>projectIndex.jsp";
		document.forms[0].submit();
	}
		
	var $tabContainer;
	$(function(){ 
		$tabContainer = $(".easyui-tabs");
		//var fullwidth = $(document).width(); alert(fullwidth);
		//$(".panel").width(fullwidth+50);
		$("body").css("margin","0px");
		
		if($("#projectName").val()!=""){
			$("#appName").focus();
		}
			
		
	});
	</script>
</head>

  <body class="easyui-layout">
  <div id="mainDiv" region="center" border="true" style="padding-top: 10px;">
    <form id="form1" method="post">
    	<input type="hidden" id="method" name="method" value="<%=method%>">
    	<input type="hidden" id="projectName" name="projectName" value="<%=projectName%>">
        <table width="95%" border="0" align="center" cellpadding="0" cellspacing="0" class="table01"> 
			<tr>
				<td class="td_title" >
					应用代码：
				</td>
				<td class="td_content" >
					<input class="easyui-validatebox" type="text" name="appCode" data-options="required:true" size="50" value="<%=appCode%>"/>
					如：CODGEN_SAMPLE
				</td>
			</tr>
			<tr>
				<td class="td_title" >
					应用标识：
				</td>
				<td class="td_content" >
					<input class="easyui-validatebox" type="text" id="appName" name="appName" data-options="required:true" size="50" value="<%=appName%>"/>
					如：sample
				</td>
			</tr>
			<tr>
				<td class="td_title" >
					应用名称：
				</td>
				<td class="td_content" >
					<input class="easyui-validatebox" type="text" name="appLabel" data-options="required:true" size="50" value="<%=appLabel%>"/>
					如：示例应用
				</td>
			</tr>
			<tr>
				<td class="td_title" >
					应用基本包名：
				</td>
				<td class="td_content" >
					<input class="easyui-validatebox" type="text" name="appBaseDir" data-options="required:true" size="50" value="<%=appBaseDir%>"/>
					如：com.baidu.appName
				</td>
			</tr>
			<tr>
				<td class="td_title">
		   			创建人：
		   		</td>
		   		<td class="td_content">
					<input class="easyui-validatebox" type="text" id="author" name="author" data-options="required:true" size="50" value="<%=author%>"/>
		   		</td>
		   	</tr>
			 <tr>
		       	<td height="35" colspan="2" class="table_bottom">
		       	<div align="center">
		       		<a href="javascript:operate('build');" class="easyui-linkbutton" > 生 成 </a>&nbsp;
		       		<a href="javascript:operate('packProject');" class="easyui-linkbutton">打包下载</a>
					<a href="javascript:operate('cleanMyFolder');" class="easyui-linkbutton">清理输出文件夹</a>
		        </div>  
		        </td> 
			</tr>    
		</table>
    </form>
	</div>
	
  </body>
</html>