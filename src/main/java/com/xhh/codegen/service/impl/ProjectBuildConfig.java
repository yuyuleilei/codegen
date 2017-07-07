package com.xhh.codegen.service.impl;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.xhh.codegen.model.Copyright;
import com.xhh.codegen.model.InOutType;
import com.xhh.codegen.model.OutputModel;
import com.xhh.codegen.model.TableModel;
import com.xhh.codegen.service.BuildConfig;
import com.xhh.codegen.service.BuildConfigHandler;
import com.xhh.codegen.utils.BuilderHelper;
import com.xhh.codegen.utils.ClassLoaderUtil;
import com.xhh.codegen.utils.FilenameUtil;
import com.xhh.codegen.utils.ProjectConfig;

/**
 * 针对项目的构建配置信息
 * @author 黄天政
 *
 */
public class ProjectBuildConfig implements BuildConfig {

	/**
	 * 数据模型键：版权信息实体
	 */
	public static final String DMK_COPYRIGHT = "copyright";
	/**
	 * 数据模型键：模块名称
	 */
	public static final String DMK_MODULE_NAME = "moduleName";
	/**
	 * 数据模型键：分组名称
	 */
	public static final String DMK_GROUP_NAME = "groupName";
	/**
	 * 数据模型键：表标签
	 */
	public static final String DMK_TABLE_LABEL = "tableLabel";
	/**
	 * 数据模型键：表模型实体
	 */
	public static final String DMK_TABLE = "table";
	/**
	 * 数据模型键：表名
	 */
	public static final String DMK_TABLE_NAME = "tableName";
	/**
	 * 数据模型键：输出编码类型
	 */
	public static final String DMK_OUTPUT_ENCODING = "outputEncoding";
	/**
	 * 数据模型键：项目标签
	 */
	public static final String DMK_PROJECT_LABEL = "projectLabel";
	/**
	 * 数据模型键：项目名称
	 */
	public static final String DMK_PROJECT_NAME = "projectName";
	/**
	 * 数据模型键：默认的模板文件夹
	 */
	public final static String DMK_TEMPLATEDIRECTORY = "templateDirectory";
	/**
	 * 数据模型键：默认的输出文件夹
	 */
	public final static String DMK_OUTPUTDIRECTORY = "outputDirectory";
	
	private ProjectConfig projectConfig;
	private Map<String,Object> dataModelMap = null;
	private Map<String,OutputModel> outputModelMap = null;
	private String moduleName;
	private String tableName;
	private String tableLabel;
	private String groupName;
	private Copyright copyright = new Copyright();
	
	private boolean hasParseDataModel = false;//是否已解析了数据模型
	private boolean hasParseOutputModel = false;//是否已解析了输出模型
		
	/**
	 * 根据一个项目配置模型实例化一个项目构建配置信息
	 * @param projectConfig 项目配置模型
	 */
	public ProjectBuildConfig(ProjectConfig projectConfig) {
		super();
		this.projectConfig = projectConfig;
		initConfig();
	}
	
	/**
	 * 初始化配置信息
	 */
	public void initConfig() {
		doInitConfig();
	}

	/**
	 * @return 获取当前的项目配置模型
	 */
	public ProjectConfig getProjectConfig() {
		return projectConfig;
	}

	/**
	 * @param projectConfig 设置当前的项目配置模型
	 */
	public void setProjectConfig(ProjectConfig projectConfig) {
		this.projectConfig = projectConfig;
		initConfig();
	}

	/**
	 * @return 获取当前构建的模块名称
	 */
	public String getModuleName() {
		return moduleName;
	}

	/**
	 * @param moduleName 设置当前构建的模块名称
	 */
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	/**
	 * @return 获取当前构建的表名称
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * @param tableName 设置当前构建的表名称
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * @return 获取当前构建的表标签
	 */
	public String getTableLabel() {
		return tableLabel;
	}

	/**
	 * @param tableLabel 设置当前构建的表标签
	 */
	public void setTableLabel(String tableLabel) {
		this.tableLabel = tableLabel;
	}

	/**
	 * @return 获取当前构建的所属组名称
	 */
	public String getGroupName() {
		return groupName;
	}

	/**
	 * @param groupName 设置当前构建的所属组名称
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	/**
	 * @return 获取当前构建的版权信息
	 */
	public Copyright getCopyright() {
		return copyright;
	}

	/**
	 * @param copyright 设置当前构建的版权信息
	 */
	public void setCopyright(Copyright copyright) {
		this.copyright = copyright;
	}
	

	/**
	 * 设置数据模型
	 * @param dataModelMap
	 */
	public void setDataModel(Map<String, Object> dataModelMap) {
		this.dataModelMap = dataModelMap;
	}

	/**
	 * 设置输出模型
	 * @param outputModelMap
	 */
	public void setOutputModel(Map<String, OutputModel> outputModelMap) {
		this.outputModelMap = outputModelMap;
	}
			
	public boolean isHasParseDataModel() {
		return hasParseDataModel;
	}

	public void setHasParseDataModel(boolean hasParseDataModel) {
		this.hasParseDataModel = hasParseDataModel;
	}

	public boolean isHasParseOutputModel() {
		return hasParseOutputModel;
	}

	public void setHasParseOutputModel(boolean hasParseOutputModel) {
		this.hasParseOutputModel = hasParseOutputModel;
	}

	public Map<String, Object> getDataModel() {
		if(dataModelMap==null){
			dataModelMap = new LinkedHashMap<String, Object>();
		}
		return dataModelMap;
	}

	public Map<String, Object> parseDataModel() {
		if(hasParseDataModel){
			return dataModelMap;
		}
		
		doBeforeParseDataModel();
		
		getDataModel();//确保已实例化
		dataModelMap.put(DMK_PROJECT_NAME, projectConfig.getProjectName());		
		dataModelMap.put(DMK_PROJECT_LABEL, projectConfig.getProjectLabel());
		dataModelMap.put(DMK_OUTPUT_ENCODING, projectConfig.getOutputEncoding());
		dataModelMap.put(DMK_OUTPUTDIRECTORY, getDefaultOutputDirectory());
		
		//创建表模型	
		if(StringUtils.isBlank(getTableName())){
			//throw new Exception(this.getClass().getName()+"属性tableName的值不允许为空！");
		}else{
			dataModelMap.put(DMK_TABLE_NAME, getTableName());
			TableModel tm = projectConfig.getDbProvider().createTableModel(getTableName());			
			if(StringUtils.isNotBlank(this.tableLabel)){
				tm.setTableLabel(this.tableLabel);
			}else{
				this.tableLabel = tm.getTableLabel();
			}
			dataModelMap.put(DMK_TABLE, tm);
		
			if(StringUtils.isBlank(this.groupName)&&this.tableName.contains("_")){
				this.groupName = StringUtils.substringBefore(this.tableName, "_");
			}
			if(StringUtils.isBlank(this.moduleName)){
				if(this.tableName.contains("_")){
					this.moduleName = StringUtils.substringAfter(this.tableName, "_");
				}else{
					this.moduleName = this.tableName;
				}
			}
			dataModelMap.put(DMK_TABLE_LABEL, this.tableLabel);
			dataModelMap.put(DMK_GROUP_NAME, this.groupName);
			dataModelMap.put(DMK_MODULE_NAME, this.moduleName);
		}
		
		//设置版权信息
		dataModelMap.put(DMK_COPYRIGHT, this.copyright);
		
		
		
		//追加项目配置的【数据模型】信息
		String key,value;
		for(Entry<String, String> entry: projectConfig.getDataModelMap().entrySet()){
			key = entry.getKey();
			value = entry.getValue();
			//System.out.println("开始解析数据模型="+key);
			if(StringUtils.isNotBlank(value)&&value.contains("${")){
				value = BuilderHelper.parseBuildParams(dataModelMap, value); //解析带有构建参数的字符串
			}
			dataModelMap.put(key, value);
		}
			
		hasParseDataModel = true;
		doAfterParseDataModel();
		
		return dataModelMap;
	}
	
	public String getOutputEncoding() {
		return projectConfig.getOutputEncoding();
	}
	
	public Map<String, OutputModel> getOutputModel() {
		if(outputModelMap==null){
			outputModelMap = new LinkedHashMap<String, OutputModel>();
		}
		return outputModelMap;
	}

	public Map<String, OutputModel> parseOutputModel() {
		if(hasParseOutputModel){
			return outputModelMap;
		}		
		
		//确保已经获取数据模型
		if(hasParseDataModel==false){
			parseDataModel(); 
		}
		
		doBeforeParseOutputModel();
		
		getOutputModel();//确保已实例化
		String templateFilename,outputFilename;
		OutputModel outputModel;
		for(Entry<String,OutputModel> entry: projectConfig.getOutputMap().entrySet()){
			outputModel = entry.getValue();
			
			templateFilename = outputModel.getTemplateModel().getTemplate();
			if(templateFilename.contains("${")
					//如果模板是文本类型，则在这里不需要解析，而在输出的时候再解释，
					//防止多次解释后转义符失效的问题，譬如：${'$'}{rootMap}只能解释一次
					&&outputModel.getTemplateModel().getType()==InOutType.FILE){  
				templateFilename = BuilderHelper.parseBuildParams(dataModelMap, templateFilename); //解析带有构建参数的模板
			}
			
			if(entry.getValue().getTemplateModel().getType()==InOutType.FILE){
				templateFilename = formatTemplateFilename(templateFilename);
			}
			
			outputFilename = outputModel.getOutput();
			if(StringUtils.isNotBlank(outputFilename)){
				if(outputFilename.contains("${")){
					outputFilename = BuilderHelper.parseBuildParams(dataModelMap, outputFilename); //解析带有构建参数的输出路径
				}
				//如果不是绝对路径，则当作是相对默认输出路径的相对路径
				if(outputFilename.contains(":")==false){
					String outputDirectory = dataModelMap.get(DMK_OUTPUTDIRECTORY).toString().replace("\\", "/");
					if(outputDirectory.endsWith("/")==false){
						outputDirectory += "/";
					}
					outputFilename = outputDirectory + outputFilename; 
					outputFilename = FilenameUtil.normalize(outputFilename);
				}
			}
			try {
				outputModel = outputModel.deepClone();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			outputModel.getTemplateModel().setTemplate(templateFilename);
			outputModel.setOutput(outputFilename);
			outputModelMap.put(outputModel.getName(), outputModel);
		}
		hasParseOutputModel=true;
		doAfterParseOutputModel();
		
		return outputModelMap;
	}
	
	/**
	 * 取得系统默认的输出目录为：System.getProperty("user.dir")
	 * @return
	 */
	private String getDefaultOutputDirectory(){
		String defaultPath = System.getProperty("user.dir");
		return defaultPath;
	}

	/**
	 * 格式化模板文件名为完整的文件URL，如果名称未包含路径，则默认为类根路径下的 “template/”+项目名称
	 * @return
	 * @throws IOException 
	 */
	private String formatTemplateFilename(String filename){
		filename = filename.replace("\\", "/");
		if(filename.contains("/")==false){
			if(dataModelMap.containsKey(DMK_TEMPLATEDIRECTORY)){
				filename = dataModelMap.get(DMK_TEMPLATEDIRECTORY)+"/"+filename;
			}else{
				filename = "template/" + projectConfig.getProjectName()+"/"+filename;
			}
		}
		
		if(filename.contains(".")==false 
			|| StringUtils.substringAfterLast(filename, ".").contains("/")){
			filename += ".ftl";
		}
		
		if(filename.contains(":")==false ){
			URL url = ClassLoaderUtil.getResource(filename);
			if(url==null){
				try {
					throw new IOException(String.format("模板文件%s不存在！",filename));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else{
				filename = url.getFile();
			}
		}
		
		filename = FilenameUtil.normalize(filename);
		return filename;
	}

	/**
	 * 初始化配置时处理
	 */
	private void doInitConfig(){
		for (BuildConfigHandler handler : projectConfig.getBuildConfigHandlers()) {
			handler.initConfig(this);
		}
	}
	
	/**
	 * 数据模型处理前，可以对已加载的项目配置信息进行再处理
	 */
	private void doBeforeParseDataModel(){
		for (BuildConfigHandler handler : projectConfig.getBuildConfigHandlers()) {
			handler.beforeParseDataModel(this);
		}
	}

	/**
	 * 数据模型处理后，可以对已组装好的数据模型集进行再处理
	 */
	private void doAfterParseDataModel(){
		for (BuildConfigHandler handler : projectConfig.getBuildConfigHandlers()) {
			handler.afterParseDataModel(this);
		}
	}
	
	/**
	 * 获取输出模型前处理
	 */
	private void doBeforeParseOutputModel(){
		for (BuildConfigHandler handler : projectConfig.getBuildConfigHandlers()) {
			handler.beforeParseOutputModel(this);
		}
	}
	
	/**
	 * 获取输出模型后处理
	 */
	private void doAfterParseOutputModel(){
		for (BuildConfigHandler handler : projectConfig.getBuildConfigHandlers()) {
			handler.afterParseOutputModel(this);
		}
	}
}
