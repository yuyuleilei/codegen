<%@page import="com.xhh.codegen.model.ColumnModel"%>
<%@page import="com.xhh.codegen.model.TableModel"%>
<%@page import="com.xhh.codegen.utils.BuildHelper"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.xhh.codegen.utils.ProjectConfig"%>
<%@page import="java.util.*"%>
<%@page contentType="text/html;charset=UTF-8" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
request.setCharacterEncoding("UTF-8");
response.setCharacterEncoding("UTF-8");

String projectName = request.getParameter("projectName");
ProjectConfig projectConfig= BuildHelper.getProjectConfig(getServletContext(), projectName);
String tableName = request.getParameter("tableName");
TableModel tm = projectConfig.getDbProvider().createTableModel(tableName);
List<ColumnModel> cmList = tm.getColumnList();
String tableLabel = StringUtils.isBlank(tm.getTableLabel())?tableName:tm.getTableLabel();
%>
<head>
<title></title>
	<style type="text/css">
	</style>
	<link rel="stylesheet" type="text/css" href="<%=basePath%>js/easyui/themes/default/easyui.css">
	<link rel="stylesheet" type="text/css" href="<%=basePath%>js/easyui/themes/icon.css">
	<script type="text/javascript" src="<%=basePath%>js/jquery-1.8.0.min.js"></script>
	<script type="text/javascript" src="<%=basePath%>js/easyui/jquery.easyui.min.js"></script>
<script type="text/javascript">
	$(function(){
		$("#docTable").datagrid("resize",{
			height: top.tabContainer.height()-30
		});
		$("#docTable").datagrid("fixColumnSize");
	});
</script>
</head>
<html>
<body style="overflow: hidden;" >
<form name="form1" method="post">
<div id="mainDiv" >
<table id="docTable" class="easyui-datagrid" title="<%=tableLabel %>" style="height:400px;" data-options="singleSelect:true,fitColumns:true" >
	<thead >
	<tr align="center" style="font-weight: bold;" class="td_title">
		<th  data-options="field:'字段名称'">字段名称</th>
		<th  data-options="field:'中文名称'">中文名称</th>
		<th  data-options="field:'类型（长度）'">类型（长度）</th>
		<th  data-options="field:'允许空'">允许空</th>
		<th  data-options="field:'说明'">说明</th>
	</tr>
	</thead>
	<tbody>
	<% for(ColumnModel cm : cmList){ %>
	<tr style="cursor: default;">	
		<td >
			<%=cm.getFieldName() %>
		</td>
		<td >
			<%=cm.getColumnLabel() %>
		</td>
		<td >
			<% 
				String str = "";
				String typeName = cm.getColumnTypeName();
				str += typeName;
				if(typeName.contains("char")||typeName.contains("binary")){
					str += "("+cm.getPrecision()+")";
				}else if("decimal".equals(typeName)||"numeric".equals(typeName)){
					str += "("+cm.getPrecision()+","+cm.getScale()+")";
				}
				out.println(str);
			%>
		</td>
		<td align="center">
		<%
			if(cm.isNullable()){
				out.println("是");
			}else{
				out.println("否");
			}
		 %>
<!--			<bean:write name="cm" property="nullable"/>-->
		</td>
		<td >
			<%=cm.getColRemark() %>
		</td>
	</tr>
	<%} %>
	</tbody>
</table>
<br/>
</div>

</form>
</body>
</html>