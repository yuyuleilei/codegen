<%@page import="java.net.URLDecoder"%>
<%@page import="com.xhh.codegen.service.DbProvider"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.io.File"%>
<%@page import="com.xhh.codegen.util.ZipUtil"%>
<%@page import="com.xhh.codegen.util.FileUtil"%>
<%@page import="com.xhh.codegen.model.InOutType"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="com.xhh.codegen.service.impl.ProjectBuildConfig"%>
<%@page import="com.xhh.codegen.service.impl.CodeBuilder"%>
<%@page import="com.xhh.codegen.service.Builder"%>
<%@page import="com.xhh.codegen.model.OutputModel"%>
<%@page language="java" import="java.util.*"  pageEncoding="UTF-8"%>
<%@page import="com.xhh.codegen.util.ProjectConfig"%>
<%@page import="com.xhh.codegen.util.BuildHelper"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.xhh.codegen.model.TableModel"%>
<%@page import="com.xhh.codegen.util.BuildHelper"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
request.setCharacterEncoding("UTF-8");
response.setCharacterEncoding("UTF-8");

  String method = request.getParameter("method");
  
  String projectName =StringUtils.defaultString(request.getParameter("projectName"),"");
  String tableName = StringUtils.defaultString(request.getParameter("tableName"),"");
  String tableLabel = StringUtils.defaultString(request.getParameter("tableLabel"),"");
  String groupName = StringUtils.defaultString(request.getParameter("groupName"),"");
  String moduleName = StringUtils.defaultString(request.getParameter("moduleName"),"");
  String author = StringUtils.defaultString(request.getParameter("author"),"");
  String[] outputs = request.getParameterValues("outputs");
  
  ProjectConfig projectConfig = BuildHelper.getProjectConfig(getServletContext(),projectName);
  DbProvider dp = projectConfig.getDbProvider();
  Map<String,OutputModel> omMap = new LinkedHashMap<String, OutputModel>();
  
// 	String  tableNameOpts= "";
// 	List<String> tableNameList = dp.getTableNames();
// 	for(String tn: tableNameList){
// 		String tabComment = StringUtils.abbreviate(dp.getTableComment(tn),30);
		
// 		tableNameOpts += "<option value='"+tn+"' "
// 			+(tn.equals(tableName)?"selected='selected'":"")+">"
// 			+tn+(StringUtils.isBlank(tabComment)? "" : "【"+tabComment+"】")
// 			+"</option>";
// 	}
    
    if("input".equals(method)){
		tableLabel = dp.getTableLabelFromComment(dp.getTableComment(tableName,tableName));
		if(tableName.contains("_")){
			groupName = tableName.split("_")[0];
		}else{
			groupName = "";
		}
		if(tableName.contains("_")){
			moduleName = StringUtils.removeStart(tableName, groupName+"_");
		}else{
			moduleName = "";
		}
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
    
    ProjectBuildConfig buildConfig = new ProjectBuildConfig(projectConfig);
	//设置数据库表名称（Sys_UserInfo）
	buildConfig.setTableName(tableName); 
	//设置表标签（如：用户信息）
	buildConfig.setTableLabel(tableLabel); 
	//设置分组名称（通常为表 名称的前缀，如：Sys）
	buildConfig.setGroupName(groupName); 
	//设置模块名称（如：UserInfo）
	buildConfig.setModuleName(moduleName); 
	//设置版本的创建人
	buildConfig.getCopyright().setAuthor(author);
	if("input".equals(method)){
		//初始化项目配置信息
		buildConfig.initConfig();
	}
	
	Map<String,OutputModel> originOmMap = buildConfig.getProjectConfig().getOutputMap();
	if(outputs!=null&&outputs.length>0){
		List<String> outputNameList = Arrays.asList(outputs);
		for(Entry<String,OutputModel> entry : originOmMap.entrySet()){
			entry.getValue().setDisabled(false==outputNameList.contains(entry.getKey()));
		}	
	}
	
	
    if("build".equals(method)||"packModule".equals(method)||"packProject".equals(method)){
  		 
		//由一个构建配置对象实例化一个代码生成器对象
		Builder builder = new CodeBuilder(buildConfig);
		omMap = builder.build();
		//为创建人设置cookies
		Cookie cookieAuthor = new Cookie("codgen.author", URLEncoder.encode(author,"UTF-8"));
		cookieAuthor.setMaxAge(0);
		cookieAuthor.setPath("/");
    	response.addCookie(cookieAuthor);
    	
		if("packModule".equals(method)||"packProject".equals(method)){
			ZipUtil zu = new ZipUtil();  
	        zu.setComment("该压缩包由Codgen代码生成器生成");
	        String outputDir = (String)(buildConfig.getDataModel().get(ProjectBuildConfig.DMK_OUTPUTDIRECTORY));
	        String zipFilename = getServletContext().getRealPath("/"+projectName+".zip");
	        List<String> filter = new ArrayList<String>();  
	        if("packModule".equals(method)){
		        for(Entry<String,OutputModel> entry : omMap.entrySet()){		
					if(entry.getValue().getType()==InOutType.FILE){
						String f1 = entry.getValue().getOutput().replace("\\", "/").toLowerCase().replace(outputDir.replace("\\", "/").toLowerCase(), "");
						if(f1.startsWith("/")){
							f1= f1.substring(1);
						}
						filter.add(f1);
					}
				}  
			}
	        zu.zip(outputDir, zipFilename, filter); 
	        
	        response.setContentType("application/x-download");//设置为下载application/x-download
			String filedownload = "/"+projectName+".zip";//即将下载的文件的相对路径
			String filedisplay = projectName+".zip";//下载文件时显示的文件保存名称
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
	}else if("cleanProject".equals(method)){
		if(buildConfig.isHasParseOutputModel()==false){
			buildConfig.parseOutputModel();
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
    
    <title>My JSP 'main.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<link rel="stylesheet" type="text/css" href="<%=basePath%>js/easyui/themes/default/easyui.css">
	<link rel="stylesheet" type="text/css" href="<%=basePath%>js/easyui/themes/icon.css">
	<script type="text/javascript" src="<%=basePath%>js/jquery-1.8.0.min.js"></script>
	<script type="text/javascript" src="<%=basePath%>js/easyui/jquery.easyui.min.js"></script>
	<script type="text/javascript" src="<%=basePath%>js/easyui/locale/easyui-lang-zh_CN.js"></script>	
	
	<link href="<%=basePath%>js/prettify/prettify.css" type="text/css" rel="stylesheet" />
	<script type="text/javascript" src="<%=basePath%>js/prettify/prettify.js"></script>
	
	<script type="text/javascript" src="<%=basePath%>js/jquery.zclip.min.js"></script>

	<script type="text/javascript">
	
	function operate(method){
		if($("#form1").form("validate")==false){
			return ;
		}
		
		jQuery("#method").val(method);
		document.forms[0].action = "<%=basePath%>main.jsp";
		document.forms[0].submit();
	}
		
	var $tabContainer;
	$(function(){ 
		$tabContainer = $(".easyui-tabs");
		//var fullwidth = $(document).width(); alert(fullwidth);
		//$(".panel").width(fullwidth+50);
		$("body").css("margin","0px");
		
		if($("#projectName").val()!=""){
			$("#tableName").focus();
		}
		
// 		$("#mainDiv").resize(function(){
// 			if($tabContainer){
// 				$tabContainer.tabs({ 
// 					width:$(".easyui-panel").width(), 
// 					height:$(".easyui-panel").height()-$("#form1").height()-20
// 				}); 
// 			}
// 		}).resize();
		
		prettyPrint();
		
		$("a#copyCode").zclip({
	        path:"<%=basePath%>js/ZeroClipboard.swf",
	        copy:function(){ return $tabContainer.tabs("getSelected").find("pre").text(); }
	    });
		
		//打包操作绑定
		$("#packModule,#packProject,#cleanProject").click(function(){
			operate(this.id);
		});
	});
	
	//查看表结构
	function viewTableDoc(){
		var tableName = $("#tableName").val();
		var url = "viewTableDoc.jsp?projectName="+$("#projectName").val()+"&tableName="+tableName;
		var title = "doc/"+tableName;
		top.openTab(url,title);
	}
	</script>
  </head>
  <body class="easyui-layout">
  <div id="topDiv" region="north" border="true"
		style="overflow:hidden;height:130px;" >
    <form id="form1" method="post">
    	<input type="hidden" id="method" name="method" value="<%=method%>">
    	<input type="hidden" id="projectName" name="projectName" value="<%=projectName%>">
    	<input type="hidden" id="tableName" name="tableName" value="<%=tableName%>">
        <table width="95%" border="0" align="center" cellpadding="0" cellspacing="0" class="table01"> 
			<tr>
				<td class="td_title" >
					表注释：
				</td>
				<td class="td_content" >
					<input class="easyui-validatebox" type="text" name="tableLabel" data-options="required:true" size="50" value="<%=buildConfig.getTableLabel()%>"></input>    			
				</td>
				<td rowspan="4">
					<% int index=0;
					for(Entry<String,OutputModel> entry : originOmMap.entrySet()){
						index++;
						String omName= entry.getKey();
						String omChecked = entry.getValue().isDisabled()?"":"checked='checked'";
						out.append("<span>");
						out.append("<input id='outputs_"+omName+"' type='checkbox' name='outputs' value='"+omName+"' "+omChecked+" >");
						out.append("<label for='outputs_"+omName+"'>"+omName+"</label>");
						out.append("</span>");
						if(index%((originOmMap.size()-1)/4+1)==0){
							out.append("<br/>");
						}						
					}
					%>
				</td>
			</tr>
			<tr>
				<td class="td_title" >
					所属组：
				</td>
				<td class="td_content" >
					<input class="easyui-validatebox" type="text" name="groupName" data-options="required:true" size="50" value="<%=buildConfig.getGroupName()%>"></input>	    			
				</td>
			</tr>
			<tr>
				<td class="td_title" >
					模块名称：
				</td>
				<td class="td_content" >
					<input class="easyui-validatebox" type="text" name="moduleName" data-options="required:true" size="50" value="<%=buildConfig.getModuleName()%>"></input>	    			
				</td>
			</tr>
			<tr>
				<td class="td_title">
		   			创建人：
		   		</td>
		   		<td class="td_content">
					<input class="easyui-validatebox" type="text" name="author" data-options="required:true" size="50" value="<%=author%>"></input>
		   		</td>
		   	</tr>
			 <tr>
		       	<td height="35" colspan="3" class="table_bottom">
		       	<div align="center">
		       		<a href="javascript:operate('build');" class="easyui-linkbutton" > 生 成 </a>&nbsp;
		       		<a href="javascript:operate('packModule');" class="easyui-splitbutton" 
		       			data-options="menu:'#mm1',plain:false" title="直接生成并打包下载" > 打 包  </a>&nbsp;
		       		<a href="javascript:viewTableDoc();" class="easyui-linkbutton" > 查看表结构 </a>
		        </div>  
			    <div id="mm1" style="width:100px;">  
			        <div id="packModule" data-options="iconCls:'icon-pack'">仅打包本模块</div>  
			        <div id="packProject" data-options="iconCls:'icon-packAll'">打包整个项目</div>
					<div id="cleanProject" data-options="iconCls:'icon-cancel'">清理输出文件夹</div>
			    </div> 
		        </td> 
			</tr>    
		</table>
    </form>
	</div>
	<div id="mainDiv" split="true" region="center">
	<div class="easyui-tabs" data-options="tools:'#tab-tools',fit:true">
		<%
		String outputContent;
		for(Entry<String,OutputModel> entry : omMap.entrySet()){
			if(entry.getValue().isDisabled()){
				//禁用的输出，如果以前曾经输出过，为防止混淆，在本次也要删掉
				FileUtil.deleteFile(entry.getValue().getOutput());
				continue; //禁用的不作输出
			}
			out.append("<div title='"+entry.getKey()+"' >");	
			out.append("<pre class=\"prettyprint "+BuildHelper.getPrettifyLang(entry.getValue().getOutput())+" \" contentEditable=\"true\">");		
			
			if(entry.getValue().getType()==InOutType.FILE){
				outputContent = FileUtil.copyToString(entry.getValue().getOutput(),projectConfig.getOutputEncoding());
			}else{
				outputContent = entry.getValue().getOutput();
			}
			if(StringUtils.isNotBlank(outputContent)){
				outputContent = outputContent.replace("<", "&lt;").replace(">", "&gt;");
			}
			out.append(outputContent);
			
			out.append("</pre>");
			out.append("</div>");
		}
		 %>
	</div>
	<div id="tab-tools">  
        <a id="copyCode" href="javascript:void(0)" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-copy'" title="复制" >复制</a>  
        
    </div>
  </div>
  </body>
</html>
