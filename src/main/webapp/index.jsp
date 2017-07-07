<%@page import="java.io.File"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@page import="com.xhh.codegen.utils.ProjectConfig"%>
<%@page import="com.xhh.codegen.utils.BuildHelper"%>
<%@page import="java.util.Map.Entry"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
request.setCharacterEncoding("UTF-8");
response.setCharacterEncoding("UTF-8");

String projectName = request.getParameter("projectName");
ProjectConfig projectConfig;
BuildHelper.refreshConfig(getServletContext()); //重新刷新配置模型
if(StringUtils.isBlank(projectName)){	
	projectConfig = BuildHelper.getDefaultProjectConfig(getServletContext());
	projectName = projectConfig.getProjectName();
}else{
	projectConfig = BuildHelper.getProjectConfig(getServletContext(), projectName);
}
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<base href="<%=basePath%>">

<title>codgen代码生成器</title>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">

<link rel="stylesheet" type="text/css"
	href="<%=basePath%>js/easyui/themes/default/easyui.css">
<link rel="stylesheet" type="text/css"
	href="<%=basePath%>js/easyui/themes/icon.css">
<script type="text/javascript" src="<%=basePath%>js/jquery-1.8.0.min.js"></script>
<script type="text/javascript"
	src="<%=basePath%>js/easyui/jquery.easyui.min.js"></script>
<script type="text/javascript">
	var projectName = "<%=projectName%>";
    var mainContainer; //主容器
    var tabContainer; //tab容器
    
	$(function(){
		mainContainer = $('#mainDiv');
		//选项卡容器事件绑定
		tabContainer = $('#tabsDiv');
        BindTabMenuClose(); //绑定选项卡右键菜单的一些关闭操作
        				
		$("#mainDiv").resize(function(){
            if(!tabContainer) return;
		    tabContainer.tabs({ 
                width:tabContainer.parent().width(), 
                height:tabContainer.parent().height() 
            }); 
            tabContainer.find(".tabFrame").height(tabContainer.parent().height()-30);  
            //alert(tabContainer.width());
            //防止调整大小后跳到第一个选项卡
            /* var tab = tabContainer.tabs('getSelected');
            if(tab){ 
               var title = tab.panel('options').title;
               setTimeout(function(){
               if(title!=tabContainer.tabs('getSelected').panel('options').title){
                  tabContainer.tabs('select', title);
	           }
	           },100);
		    } */
		}).trigger("resize");
		
		//拦截mainFrame的网页到选项卡里。
        $("#mainFrame").load(function(){ //alert("mainFrame的src="+this.contentWindow.location);
            var myFrame = this, href = this.contentWindow.location.href;
            if(href.substr(href.length-1)!="/" && href.substr(href.length-5)!="blank"){
                var title = myFrame.contentWindow.document.title||"无标题";
                if(tabContainer.tabs('exists', title)){ tabContainer.tabs('close', title); }
                top.openTab(href,title);
            }
        });
		
		//如果是框架项目（后缀名为“-framework”）,则默认加载项目主页
		if(projectName.indexOf("-framework")>-1 && projectName.indexOf("-framework")==projectName.length-10){
			$("#mainFrame").attr("src","projectIndex.jsp?method=input&projectName="+projectName);
			return;
		}
                
        //加载表和视图操作
        loadTableList();
        
        $(".tree-title:contains('表')").closest("div").next("ul").find("span").filter(".tree-file").addClass("icon-table");
        $(".tree-title:contains('视图')").closest("div").next("ul").find("span").filter(".tree-file").addClass("icon-view");
		

		
	});

    
	//加载表和视图操作
	function loadTableList(){	
		//var rootNodes = $("#tableTree").tree("getRoots");
		//$.each(rootNodes,function(index,item){
		//	$("#tableTree").tree("remove",$(item).target);
		//});
		//$("#tableTree").empty().remove();
		
		//$("#tableListContainer").append("<ul id='tableTree'></ul>");
        $("#tableTree").tree({
        	data:[{"id":1,"text":"表","state":"closed"}
        	,{"id":2,"text":"视图","state":"closed"}
        	], 
        	onBeforeExpand:function(node,param){
        		var tableNode = $('#tableTree').tree('find', node.id); 
	    		var childNodes = $("#tableTree").tree("getChildren",tableNode.target);
	    		$.each(childNodes,function(index,item){
	    			$("#tableTree").tree("remove",item.target);
	    		});
        		var jsonUrl = "json.jsp?method=getTableList&projectName="+projectName
        		+"&tableType="+(node.id==1?"TABLE":"VIEW")
        		+"&tableNamePatterns="
        		+$("#tableListContainer").find(".searchbox-text").val().replace("在这里输入过滤条件...","");
        		//alert(jsonUrl);
                var jsonStr = $.ajax({
                      url: jsonUrl,
                      async: false
                     }).responseText;
                //alert(jsonStr);
                $('#tableTree').tree('append', {
                    parent: tableNode.target,
                    data: eval("("+jsonStr+")")
                }); 				 
				//$("#tableTree").tree("options").url = jsonUrl;
        	},
            onContextMenu: function(e,node){ 
            	if($(this).tree("isLeaf",node.target)==false||node.id==1||node.id==2){
            		return ;
            	} 
                e.preventDefault();  
                $(this).tree('select',node.target);  
                $("#tableMenu").menu('show',{  
                    left: e.pageX,  
                    top: e.pageY  
                });  
            }
            ,onLoadSuccess:function(node, data){
            	if(node&&node.text&&node.id!=1&&node.id!=2){
            		alert(node.id);
            	}
//             	$(this).tree("update",{
//             		target: node.target,
//             		iconCls:"icon-table"
//             	});
            } 
        }); 
	}
	

	//代码生成操作
	function operate(tableName){
		var url = "main.jsp?method=input&projectName="+projectName+"&tableName="+tableName;
		var title = "build/"+tableName;
		openTab(url,title);
	}
	
	//查看表结构
	function viewTableDoc(){
		var selectedNode = $("#tableTree").tree("getSelected");
		if(selectedNode==null){
			alert("请选中需要查看的表或视图"); 
			return ;
		}
		var tableName = $(selectedNode.target).text();
		var url = "viewTableDoc.jsp?projectName="+projectName+"&tableName="+tableName;
		var title = "doc/"+tableName;
		openTab(url,title);
	}
	//查询过滤
	 function doSearch(value){
         //alert('You input: ' + value);
         //$("#tableNamePatterns").val(value);
		 loadTableList();
		 //默认展开表节点
		 var tableNode = $('#tableTree').tree('find', 1); 
		 $("#tableTree").tree("expand",tableNode.target);
		 //$("#tableTree").tree("reload",$("#tableTree").tree("getRoot").target);
     }
	
	
	//在新的选项卡里面打开页面
    function openTab(){
        var args = Array.prototype.slice.call(arguments, 0);    //将参数转换为数组。  
        var len = args.length; 
        if(len==1){ //如果指定了url
            openTabWithParam(args[0],"无标题",true);
        }else if(len==2){ //如果指定了url,title
            openTabWithParam(args[0],args[1],false); 
        }else if(len==3){ //如果指定了url,title,reloadFlag
            openTabWithParam(args[0],args[1],args[2]); //alert("openTab第三个参数="+args[2]);
        }else{
            throw new Error("没有定义该种打开选项卡的方法"); 
        }
    };
    
    /*按照指定的参数打开选项卡
    *url: 要打开的URL
    *title: 要显示的选项卡标题
    *reloadFlag: 如果指定的选项卡已存在，是否重新加载页面。
    **/
    function openTabWithParam(url,title,reloadFlag){ 
        if(!url || url=="" || url=="#") return;
        
        
        //检查指定的url是否已经打开,如果已经打开则设置为当前窗口
        if (tabContainer.tabs('exists', title)){
            if(reloadFlag==true){
                ReloadTabPage(url,title);
            }else{
                tabContainer.tabs('select', title); 
            }
            return ;
        }
        
        //以下是新增一个选项卡的操作
        var pageFrame = "<iframe class='tabFrame' style=\"width:100%;height:"+$("#leftDiv").height()
            +"px;\"  marginheight=\"0\" marginwidth=\"0\" frameborder=\"0\"; scrolling=\"auto\"  src=\""
            +url+"\"></iframe>";
        tabContainer.tabs('add',{
			title:title,
			content:pageFrame,
			//iconCls:'icon-save',
			closable:true
		});
		var tab = $(".tabs-inner").find("span:contains('"+title+"')").parent();
		//绑定选项卡关闭操作
		BindTabClose(tab);
		//隐藏选项页卡的滚动条
		$(".tabFrame").parent("div").css("overflow","hidden");
		
    }
    //重新加载选项卡页面
    function ReloadTabPage(url,title){
        tabContainer.tabs("select",title);
        var tab = tabContainer.tabs('getSelected');
        var myFrame = tab.find(".tabFrame");
        if(myFrame){
            //防止缓存处理
            var randomnumber=Math.floor(Math.random()*100000); 
            if(url.indexOf("?")>0){url+="&";}else{url+="?";}
            url += "randomnumber=" + randomnumber;
            
            myFrame.attr("src",url);
            //myFrame[0].contentWindow.location.reload();
        }
    }
    
    //关闭指定标题的选项卡
    var closeTab=function(){
        var args = Array.prototype.slice.call(arguments, 0);    //将参数转换为数组。  
        var len = args.length; 
        if(len==0){ //如果未指定title，则关闭当前选项卡
            closeTabWithParam(null);
        }else if(len==1){ //如果指定了title
            closeTabWithParam(args[0]); 
        }else{
            throw new Error("没有定义该种关闭选项卡的方法"); 
        }
    };
    /*按照指定的参数关闭选项卡
    *title: 要关闭的选项卡标题
    **/
    function closeTabWithParam(title){
        if(title==null){
            var tab = tabContainer.tabs('getSelected');
            title = tab.panel('options').title;
        }
        tabContainer.tabs('close', title);
    }
 

	//绑定选项卡关闭操作
	function BindTabClose(tab)
	{
	    /*双击关闭TAB选项卡*/
	    tab.dblclick(function(){ 
	        var subtitle = $(this).children("span").text();
	        closeTab(subtitle);
	    });
	
	    tab.bind('contextmenu',function(e){
	        $('#mm').menu('show', {
	            left: e.pageX,
	            top: e.pageY
	        });
	        
	        var subtitle =$(this).children("span").text();
	        $('#mm').data("currtab",subtitle);
	        //把当前鼠标位置所在的选项卡置为当前状态
	        //tabContainer.tabs('select', subtitle);
	        return false;
	    });
	    
	}
	//绑定选项卡右键菜单的一些关闭操作
	function BindTabMenuClose()
	{
	    
	    //关闭当前
	    $('#mm-tabclose').click(function(){
	        var currtab_title = $('#mm').data("currtab");
	        closeTab(currtab_title);
	    });
	    //全部关闭
	    $('#mm-tabcloseall').click(function(){
	        $('.tabs-inner span').each(function(i,n){
	            var t = $(n).text();
	            closeTab(t);
	        });    
	    });
	    //关闭除当前之外的TAB
	    $('#mm-tabcloseother').click(function(){
	        var currtab_title = $('#mm').data("currtab");
	        $('.tabs-inner span').each(function(i,n){
	            var t = $(n).text();
	            if(t!=currtab_title)
	                closeTab(t);
	        });    
	    });
	    //关闭当前右侧的TAB
	    $('#mm-tabcloseright').click(function(){
	        var nextall = $('.tabs-selected').nextAll();
	        if(nextall.length==0){
	            top.showTip('右侧没有可关闭的选项卡了',2,2000);
	            return false;
	        }
	        nextall.each(function(i,n){
	            var t=$('a:eq(0) span',$(n)).text();
	            closeTab(t);
	        });
	        return false;
	    });
	    //关闭当前左侧的TAB
	    $('#mm-tabcloseleft').click(function(){
	        var prevall = $('.tabs-selected').prevAll();
	        if(prevall.length==0){
	            top.showTip('左侧没有可关闭的选项卡了',2,2000);
	            return false;
	        }
	        prevall.each(function(i,n){
	            var t=$('a:eq(0) span',$(n)).text();
	            closeTab(t);
	        });
	        return false;
	    });
	}
	
    //content 要提示的消息内容 
    //mode 1-一般提示，2-警告提示，5-错误提示
    //timeoutMills 超时的毫秒数。如：3000表示三秒后该提示自动消失
    var tipTimer;
    function showTip(content,mode,timeoutMills){
        if(tipTimer){window.clearTimeout(tipTimer);}
        tipTimer = setTimeout(hideMsgBox, timeoutMills);
        var color = "#68AF02";
        if(mode==2) color = "#EF8F00";
        if(mode==5) color = "red";
        $("#msgBoxDiv .msg").css("background-color",color);
        $("#msgBoxDiv .msg").html(content);
        $("#msgBoxDiv").show();
    }
    function hideMsgBox(){
        $("#msgBoxDiv").hide();
    }
	</script>
		
</head>
<%
  	Map<String,ProjectConfig> pcMap = BuildHelper.getAllProjectConfig(this.getServletContext());
   %>
<body class="easyui-layout">
    <div id="msgBoxDiv" style="Z-INDEX: 9999; POSITION: absolute; TEXT-ALIGN: center; WIDTH: 100%; DISPLAY: none; HEIGHT: 24px; TOP: 40px; PADDING: 2px; _height: 20px" >
        <span class="msg" style="color:White;"></span>
    </div>
	<div id="topDiv" region="north" border="true"
		style="overflow:hidden;height:60px;">
		<table width="100%" border="0">
		<tr>
			<td>
				<h1 style="margin: 15px;">
				C o d g e n 代 码 生 成 器 
<!--				<span style="font-size: large;">—— 开发之利器</span> -->
				</h1>
			</td>
			<td style="width: 200px;white-space: nowrap;">
				<select id="projectList" class="easyui-combobox" name="projectList" style="width:200px;" 
					data-options="onSelect: function(rec){   
			            window.location.href = 'index.jsp?projectName='+rec.value;   
			        }" > 
				<%
					for(Map.Entry<String,ProjectConfig> entry: pcMap.entrySet()){
						if("defaultProject".equals(entry.getKey())) continue; //不显示系统默认项目
						out.append("<option value='"+entry.getKey()+"' "
						+(entry.getKey().equals(projectName)?"selected='selected'":"")+">")
						.append(entry.getKey()).append("【"+entry.getValue().getProjectLabel()+"】")
						.append("</option>");
					}
				 %> 
			    </select>
			</td>
		</tr>
		</table>
	</div>
	<div region="south" split="true" style="height:30px;"></div>
	<!--	<div region="east" icon="icon-reload" title="East" split="true" style="width:180px;">-->
	<!--	</div>-->
	<div id="leftDiv" region="west" split="true" title="当前项目：<%=projectName %>" style="width:300px;">
		<div id="tableListContainer" title="表与视图" style="padding:5px;">
			<input id="tableNamePatterns" class="easyui-searchbox" data-options="prompt:'在这里输入过滤条件...',searcher:doSearch" style="width:270px;"></input>
			<ul id='tableTree'></ul>
		</div>
	</div>
	<div id="mainDiv" region="center">
		<div id="tabsDiv" class="easyui-tabs"
			style="overflow:hidden;padding:0px;width:100%;"></div>
		<iframe id="mainFrame" name="mainFrame" style="display:none;"
			marginheight="0" marginwidth="0" frameborder="0"
	        height="0" src=""></iframe>
	</div>
	    
	<div id="mm" class="easyui-menu"
		style="width:150px;left:-200px;position:absolute;">
		<div id="mm-tabclose">关闭</div>
		<div id="mm-tabcloseall">全部关闭</div>
		<div id="mm-tabcloseother">除此之外全部关闭</div>
		<div class="menu-sep"></div>
		<div id="mm-tabcloseright">当前页右侧全部关闭</div>
		<div id="mm-tabcloseleft">当前页左侧全部关闭</div>
	</div> 
	
    <div id="tableMenu" class="easyui-menu" style="width:120px;">  
        <div onclick="viewTableDoc()" data-options="iconCls:'icon-search'">查看表结构</div> 
    </div>  
	
</body>
</html>
