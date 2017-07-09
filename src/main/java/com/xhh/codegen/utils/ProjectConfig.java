package com.xhh.codegen.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.xhh.codegen.model.OutputModel;
import com.xhh.codegen.service.BuildConfigHandler;
import com.xhh.codegen.service.DbProvider;



/**
 * 项目配置模型
 * @author 黄天政
 *
 */
public class ProjectConfig  implements Serializable {
	private static final long serialVersionUID = 1L;
	private String projectName;
	private String projectLabel;
	private String outputEncoding = "UTF-8";
	private DbProvider dbProvider;
	private Map<String, String> dataModelMap = new LinkedHashMap<String,String>();
	private Map<String, OutputModel> outputMap = new LinkedHashMap<String,OutputModel>();
	private boolean defaultProject;
	/**
	 * 数据模型处理器
	 */
	private List<BuildConfigHandler> buildConfigHandlers = new ArrayList<BuildConfigHandler>();
	
	
	/**
	 * @return 取得项目名称
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * @param projectName 设置项目名称
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	/**
	 * @return 取得项目标签（中文描述）
	 */
	public String getProjectLabel() {
		return projectLabel;
	}

	/**
	 * @param projectLabel 设置项目标签（中文描述）
	 */
	public void setProjectLabel(String projectLabel) {
		this.projectLabel = projectLabel;
	}

	/**
	 * @return 取得输出编码方式
	 */
	public String getOutputEncoding() {
		return outputEncoding;
	}

	/**
	 * @param outputEncoding 设置输出编码方式
	 */
	public void setOutputEncoding(String outputEncoding) {
		this.outputEncoding = outputEncoding;
	}

	/**
	 * @return 取得数据库信息提供者
	 */
	public DbProvider getDbProvider() {
		return dbProvider;
	}

	/**
	 * @param dbProvider 设置数据库信息提供者
	 */
	public void setDbProvider(DbProvider dbProvider) {
		this.dbProvider = dbProvider;
	}

	/**
	 * @return 取得数据模型映射集合
	 */
	public Map<String, String> getDataModelMap() {
		return dataModelMap;
	}

	/**
	 * @param dataModelMap 设置数据模型映射集合
	 */
	public void setDataModelMap(Map<String, String> dataModelMap) {
		this.dataModelMap = dataModelMap;
	}

	/**
	 * @return 取得输出映射集合
	 */
	public Map<String, OutputModel> getOutputMap() {
		return outputMap;
	}

	/**
	 * @param outputMap 设置输出映射集合
	 */
	public void setOutputMap(Map<String, OutputModel> outputMap) {
		this.outputMap = outputMap;
	}

	/**
	 * @return 是否为默认项目
	 */
	public boolean isDefaultProject() {
		return defaultProject;
	}

	/**
	 * @param defaultProject 设置是否为默认项目
	 */
	public void setDefaultProject(boolean defaultProject) {
		this.defaultProject = defaultProject;
	}
	

	/**
	 * @return 获取构建配置处理器
	 */
	public List<BuildConfigHandler> getBuildConfigHandlers() {
		return buildConfigHandlers;
	}

	/**
	 * @param buildConfigHandlers 设置构建配置处理器
	 */
	public void setBuildConfigHandlers(List<BuildConfigHandler> buildConfigHandlers) {
		this.buildConfigHandlers = buildConfigHandlers;
	}

	/**
	 * 使用序列化方式深度克隆项目配置模型
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
    public ProjectConfig deepClone() throws IOException, ClassNotFoundException {
    	ProjectConfig dc = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(this);
        oos.close();
        
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream bis = new ObjectInputStream(bais);
        dc = (ProjectConfig)bis.readObject();
        return dc;
    }
}
