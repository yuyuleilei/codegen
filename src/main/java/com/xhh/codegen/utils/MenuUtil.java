package com.xhh.codegen.utils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.xhh.codegen.model.TableMetaData;
import com.xhh.codegen.service.DbProvider;

public class MenuUtil {
	
	/**
	 * 构建菜单树：表列表
	 * @param projectConfig
	 * @param tableType
	 * @return
	 */
	public static String buildTableListForJson(ProjectConfig projectConfig, String tableType, String tableNamePatterns){
		StringBuilder sb = new StringBuilder("[");
		DbProvider dbProvider = projectConfig.getDbProvider();
		dbProvider.clearTableMetaDataCache(); //先清理缓存，否则不会加载新的数据
		dbProvider.setTableNamePatterns("%"+tableNamePatterns+"%");
		Map<String,TableMetaData> tmdMap = dbProvider.getTableMetaData();
		int index =0;
		for(Entry<String,TableMetaData> entry: tmdMap.entrySet()){
			if("INFORMATION_SCHEMA".equalsIgnoreCase(entry.getValue().getTableSchem())
				||"sys".equalsIgnoreCase(entry.getValue().getTableSchem())
				||"sysdiagrams".equalsIgnoreCase(entry.getValue().getTableName())
				||tableType.equals(entry.getValue().getTableType())==false) continue;
			index++;
			sb.append("{")
			.append("\"id\":1").append(index)
			.append(",\"text\":\"<label title='"+entry.getKey()+"' onclick=\\\"operate('"+entry.getKey()+"')\\\">")
			.append(entry.getKey()).append("</label>\"")
			.append(",\"iconCls\":\"icon-").append(tableType.toLowerCase()).append("\"")
			//.append("\"•attributes\":{tableName}")
			.append("},");
		}
		String content = sb.toString();
		if(content.endsWith(",")){
			content = content.substring(0,content.length()-1);
		}
		return content+"]";
	}
	
	/**
	 * 构建模板菜单树
	 * @param file
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static String buildTemplateListForJson(File file,int level) throws UnsupportedEncodingException{
		StringBuilder sb = new StringBuilder("[");
		int index =level*100+0;
		File[] files = file.listFiles();
		for(File file1: files){
			index++;
			sb.append("{")
			.append("\"id\":1").append(index)
			.append(",\"text\":\"<input type='hidden' value='"+URLEncoder.encode(file1.getPath(),"UTF-8")+"'/>"+file1.getName()+"\"");
			//.append(",\"iconCls\":\"icon-").append(tableType.toLowerCase()).append("\"")
			if(file1.isDirectory()){
				sb.append(",\"children\":").append(buildTemplateListForJson(file1,index));
			}
			sb.append("},");
			
		}
		String content = sb.toString();
		if(content.endsWith(",")){
			content = content.substring(0,content.length()-1);
		}
		return content+"]";
	}
	
	/**
	 * 构建菜单树：表列表
	 * @param projectConfig
	 * @param tableType
	 * @return
	 */
	public static String buildTreeMenuForTableList(ProjectConfig projectConfig, String tableType){
		StringBuilder sb = new StringBuilder("");
		Map<String,TableMetaData> tmdMap = projectConfig.getDbProvider().getTableMetaData();
		for(Entry<String,TableMetaData> entry: tmdMap.entrySet()){
			if("INFORMATION_SCHEMA".equalsIgnoreCase(entry.getValue().getTableSchem())
				||"sys".equalsIgnoreCase(entry.getValue().getTableSchem())
				||"sysdiagrams".equalsIgnoreCase(entry.getValue().getTableName())
				||tableType.equals(entry.getValue().getTableType())==false) continue;
			String tabComment = projectConfig.getDbProvider().getTableComment(entry.getKey());
			sb.append("<li>")
			.append("<label title='"
				+(StringUtils.isBlank(tabComment)?entry.getKey():tabComment)
				+"' onclick=\"operate('"+entry.getKey()+"')\" >"
				+entry.getKey()+"</label>") //+"||"+entry.getValue()
			.append("</li>");
		}
		return sb.toString();
	}
	
	/**
	 * 构建模板菜单树
	 * @param file
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static String buildTreeMenuForTemplate(File file) throws UnsupportedEncodingException{
		StringBuilder sb = new StringBuilder("");
		File[] files = file.listFiles();
		for(File file1: files){
			sb.append("<li>")
			.append("<span >")
			.append("<input type='hidden' value='"+URLEncoder.encode(file1.getPath(),"UTF-8")+"'/>")
			.append(file1.getName())
			.append("</span>");
			if(file1.isDirectory()){
				sb.append("<ul>")
				.append(buildTreeMenuForTemplate(file1))
				.append("</ul>");
			}
			sb.append("</li>");
		}
		return sb.toString();
	}
}
