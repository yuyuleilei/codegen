package com.xhh.codegen.utils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import com.xhh.codegen.model.InOutType;
import com.xhh.codegen.model.JdbcConfig;
import com.xhh.codegen.model.OutputModel;
import com.xhh.codegen.model.TemplateModel;
import com.xhh.codegen.service.BuildConfigHandler;
import com.xhh.codegen.service.ColumnHandler;
import com.xhh.codegen.service.DbProvider;

/**
 * 从配置文件里获取项目配置信息的辅助类
 * @author 黄天政
 *
 */
public class ProjectConfigHelper {
	private static final String CODGEN_DEFAULT = "com/xhh/codegen/resources/codegen-default.xml";
	private static final String DEFAULT_PROJECT_NAME = "defaultProject";
	private static final String USERCONFIGFILE_DEFAULT = "codegen-config.xml";
	
	private static Map<String, ProjectConfig> projectConfigMap = null;
	
	private static ProjectConfig firstProjectConfig = null;
	
	/**
	 * 取得项目配置文件信息。如果web.xml配置了名称为“codgen.config”的上下文初始化参数
	 * （相对于WEB-INF目录下的文件路径，多个文件名以英文逗号分隔），
	 * 则读取相应的配置文件。如果未配置该参数，则默认读取类路径根目录下的"codgen-config.xml"，
	 * 所以在这种情况下请确保src目录下存在codgen-config.xml文件。
	 * @param relativePath 相对于classpath的资源路径
	 * @param projectName
	 * @return
	 */
	public static ProjectConfig getProjectConfig(String relativePath,String projectName){
		if(projectConfigMap==null){
			loadConfigFromFile(relativePath);
		}
		
		if(projectConfigMap.containsKey(projectName)){
			return projectConfigMap.get(projectName);
		}
		
		return null;
	}
	
	/**
	 * 取得默认项目配置信息，如果没有设置，则默认为按配置文件中排列顺序的第一个项目
	 * @param relativePath 相对于classpath的资源路径
	 * @return
	 */
	public static ProjectConfig getDefaultProjectConfig(String relativePath){
		if(projectConfigMap==null){
			loadConfigFromFile(relativePath);
		}
		for(Entry<String, ProjectConfig> entry : projectConfigMap.entrySet()){
			if(entry.getValue().isDefaultProject()){
				firstProjectConfig = entry.getValue();
			}
		}
		return firstProjectConfig;
	}
	
	/**
	 * 根据当前web上下文环境获取所有项目配置
	 * @param relativePath 相对于classpath的资源路径
	 * @return
	 */
	public static Map<String, ProjectConfig> getAllProjectConfig(String relativePath){
		if(projectConfigMap==null){
			loadConfigFromFile(relativePath);
		}
		return projectConfigMap;
	}
	
	/**
	 * 根据当前web上下文环境重新加载配置信息
	 * @param relativePath 相对于classpath的资源路径
	 */
	public static void refreshConfig(String relativePath){
		if(projectConfigMap!=null){
			projectConfigMap.clear();
		}
		loadConfigFromFile(relativePath);
	}
	
	/**
	 * 实现从配置文件加载项目配置信息
	 * @param relativePath 相对于classpath的资源路径
	 */
	private static void loadConfigFromFile(String relativePath){
		projectConfigMap = new LinkedHashMap<String, ProjectConfig>();
		
		InputStream inputStream = null;
		try {
			if(StringUtils.isBlank(relativePath)){
				inputStream = ClassLoaderUtil.getStream(USERCONFIGFILE_DEFAULT);
				loadConfigFromInputStream(inputStream);
			}else{
				String[] configFilenames = relativePath.split(",");
				for (int i = 0; i < configFilenames.length; i++) {
					inputStream = ClassLoaderUtil.getStream(configFilenames[i]);
					loadConfigFromInputStream(inputStream);
				}
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 从输入流中加载项目配置信息
	 * @param inputStream configFile
	 * @return
	 */
	private static void loadConfigFromInputStream(InputStream inputStream){
		
		ProjectConfig projectConfig = null;
		DbProvider dbProvider;
		OutputModel outputModel;		
		String key, value;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
		try {				
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setEntityResolver(new CodgenDtdResolver());
			Document doc = builder.parse(inputStream); 
			Element root = doc.getDocumentElement();
			NodeList projectNodeList = root.getElementsByTagName("project"); 
			for(int j=0; j<projectNodeList.getLength(); j++){
				Element projectNode = (Element)(projectNodeList.item(j));
				String projectName = projectNode.getAttribute("name");
				String isEnabled = projectNode.getAttribute("isEnabled");
				if(StringUtils.isNotBlank(isEnabled)&&isEnabled.equalsIgnoreCase("false")){
					continue; //不加载已禁用的配置
				}
				
				if(projectConfigMap.containsKey(projectName)){
					throw new Exception("配置文件中存在相同的项目名称："+projectName);
				}
				
				if(projectNode.hasAttribute("extends")){
					String extendsProject = projectNode.getAttribute("extends");
					if(projectConfigMap.containsKey(extendsProject)==false){
						//如果是继承系统默认配置，则加载，否则抛异常
						if(DEFAULT_PROJECT_NAME.equals(extendsProject)){
							inputStream = ClassLoaderUtil.getStream(CODGEN_DEFAULT);
							loadConfigFromInputStream(inputStream);
						}else{
							throw new Exception("配置文件中项目【"+projectName
									+"】所继承的项目【"+extendsProject+"】不存在或已禁用");
						}
					}
					projectConfig = projectConfigMap.get(extendsProject).deepClone();
				}else{				
					projectConfig = new ProjectConfig();
				}
				
				projectConfig.setProjectName(projectName);				
				projectConfig.setProjectLabel(projectNode.getAttribute("label"));
				projectConfig.setOutputEncoding(projectNode.getAttribute("outputEncoding"));
				String isDefault = projectNode.getAttribute("isDefault");
				if(StringUtils.isNotBlank(isDefault)){
					projectConfig.setDefaultProject(isDefault.equalsIgnoreCase("true"));
				}
				
				NodeList childList = projectNode.getChildNodes();
				Map<String,TemplateModel> templateModelMap = new LinkedHashMap<String,TemplateModel>();				
				for (int i = 0; i < childList.getLength(); i++) {    
					Node child = childList.item(i);    
					if (child instanceof Element)    { 	
						Element childElement = (Element) child;
						String tagName = childElement.getTagName();
						if(tagName.equals("dbProvider")){
							dbProvider= parseForDbProvider(childElement);
							projectConfig.setDbProvider(dbProvider);
						}else if(tagName.equals("dataModel")){
							key = childElement.getAttribute("name");
							value = childElement.getTextContent().trim();
							projectConfig.getDataModelMap().put(key, value);
						}else if(tagName.equals("buildConfigHandler")){
							BuildConfigHandler bcHandler = parseForDataModelHandler(childElement);
							if(bcHandler!=null){
								projectConfig.getBuildConfigHandlers().add(bcHandler);
							}
						}else if(tagName.equals("template")){
							key = childElement.getAttribute("name");
							InOutType type = InOutType.valueOf(childElement.getAttribute("type").toUpperCase());
							value = childElement.getTextContent().trim();
							TemplateModel templateModel = new TemplateModel();
							templateModel.setName(key);
							templateModel.setType(type);
							templateModel.setTemplate(value);
							templateModelMap.put(key, templateModel);
						}else if(tagName.equals("output")){
							key = childElement.getAttribute("name");
							outputModel = new OutputModel(key);
							if(StringUtils.isNotBlank(childElement.getTextContent())){
								outputModel.setOutput(childElement.getTextContent().trim());
								outputModel.setType(InOutType.valueOf(childElement.getAttribute("type").toUpperCase()));
							}else{
								outputModel.setType(InOutType.TEXT);
							}
							
							//向输出模型里设置模板模型，判断优先顺序：文本>文件>引用 
							TemplateModel templateModel;
							if(childElement.hasAttribute("templateText")){ //文本模板
								templateModel = new TemplateModel();
								templateModel.setName(key);
								templateModel.setType(InOutType.TEXT);
								templateModel.setTemplate(childElement.getAttribute("templateText"));
							}else if(childElement.hasAttribute("templateFile")){ //文件模板
								templateModel = new TemplateModel();
								templateModel.setName(key);
								templateModel.setType(InOutType.FILE);
								templateModel.setTemplate(childElement.getAttribute("templateFile"));
							}else if(childElement.hasAttribute("templateName")){ //引用模板
								templateModel = templateModelMap.get(childElement.getAttribute("templateName"));
							}else{
								templateModel = new TemplateModel();
								templateModel.setName(key);
								templateModel.setType(InOutType.FILE); //默认为文件模板类型
								templateModel.setTemplate(key);	//默认模板文件名称合成规则：当前应用类路径+"template/" + 项目名称 + "/" + 输出名称+".ftl"
							}
							outputModel.setTemplateModel(templateModel);
							if(childElement.hasAttribute("disabled")){
								outputModel.setDisabled(Boolean.parseBoolean(childElement.getAttribute("disabled")));
							}
							
							projectConfig.getOutputMap().put(outputModel.getName(), outputModel);
						}
					} 
				}
				
				projectConfigMap.put(projectConfig.getProjectName(), projectConfig);
				if(firstProjectConfig==null){
					firstProjectConfig = projectConfig;
				}
			}
			

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	/**
	 * 从DOM模型中解析输出模型
	 * @param element
	 * @return
	 *//*
	private static OutputModel parseForOutputModel(Element element) {
		String outputName = element.getAttribute("name");
		
		NodeList childNodes  = element.getChildNodes();
		OutputModel outputModel = new OutputModel(outputName);
		for (int i = 0; i < childNodes.getLength(); i++) {    
			Node child = childNodes.item(i);    
			if (child instanceof Element){ 
				Element childElement = (Element) child;
				String tagName = childElement.getTagName();
				Text textNode = (Text) childElement.getFirstChild(); 
				if(tagName.equals("template")){
					String text = textNode.getData().trim();
					outputModel.setTemplate(text);
					outputModel.setTemplateType(InOutType.valueOf(childElement.getAttribute("type")));
				}else if(tagName.equals("output")){
					if(StringUtils.isNotBlank(textNode.getData())){
						outputModel.setOutput(textNode.getData().trim());
						outputModel.setOutputType(InOutType.valueOf(childElement.getAttribute("type")));
					}else{
						outputModel.setOutputType(InOutType.TEXT);
					}
				}
			}
		}
		
		return outputModel;
	}*/

	/**
	 * 从DOM模型中解析出配置的数据库信息提供者
	 * @param element
	 * @return
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 */
	@SuppressWarnings("rawtypes")
	private static DbProvider parseForDbProvider(Element element) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
		DbProvider dbProvider=null;

		String dbProviderClsName = element.getAttribute("class");	
		NodeList jdbcConfigNodeList = element.getElementsByTagName("jdbcConfig");
		if(jdbcConfigNodeList!=null&&jdbcConfigNodeList.getLength()>0){
			Element jdbcConfigElement = (Element)jdbcConfigNodeList.item(0);
			JdbcConfig jdbcConfig = parseForJdbcConfig(jdbcConfigElement);
			Class[] ctorArgs1 = new Class[]{JdbcConfig.class};
			Constructor con = Class.forName(dbProviderClsName).getConstructor(ctorArgs1);
			dbProvider = (DbProvider) con.newInstance(jdbcConfig);
		}else{
			dbProvider = (DbProvider) Class.forName(dbProviderClsName).newInstance();
		}
		
		List<ColumnHandler> columnHandlers = parseForColumnHandlers(element.getElementsByTagName("columnHandler"));
		if(columnHandlers!=null){
			dbProvider.setColumnHandlers(columnHandlers);
		}
		
		NodeList splitorForLabelFromCommentNodeList = element.getElementsByTagName("splitorForLabelFromComment");
		if(splitorForLabelFromCommentNodeList!=null
				&&splitorForLabelFromCommentNodeList.getLength()>0){
			Element splitorForLabelFromCommentElement = (Element)splitorForLabelFromCommentNodeList.item(0);
			dbProvider.setSplitorForLabelFromComment(splitorForLabelFromCommentElement.getTextContent().trim());
		}
		
		return dbProvider;
	}
	
	private static JdbcConfig parseForJdbcConfig(Element element){
		NodeList childNodes = element.getChildNodes(); 
		JdbcConfig jdbcConfig = new JdbcConfig() ;
		for (int i = 0; i < childNodes.getLength(); i++) {    
			Node child = childNodes.item(i);    
			if (child instanceof Element){ 
				Element childElement = (Element) child;
				String tagName = childElement.getTagName();
				Text textNode = (Text) childElement.getFirstChild(); 
				String text = textNode.getData().trim();
				if(tagName.equals("driver")){
					jdbcConfig.setDriver(text);						
				}else if(tagName.equals("url")){					
					jdbcConfig.setUrl(text);						
				}else if(tagName.equals("user")){					
					jdbcConfig.setUser(text);						
				}else if(tagName.equals("password")){					
					jdbcConfig.setPassword(text);						
				}
			}			
		}
		return jdbcConfig;
	}
	
	/**
	 * 从DOM模型中解析出配置的列模型处理器
	 * @param nodeList
	 * @return
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	private static List<ColumnHandler> parseForColumnHandlers(NodeList nodeList) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		List<ColumnHandler> columnHandlers = new ArrayList<ColumnHandler>();
		if(nodeList==null) return columnHandlers;
		
		ColumnHandler columnHandler;
		for (int i = 0; i < nodeList.getLength(); i++) {    
			Node node = nodeList.item(i);    
			if (node instanceof Element){ 
				String columnHandlerClsName = ((Element)node).getAttribute("class");
				columnHandler = (ColumnHandler) Class.forName(columnHandlerClsName).newInstance();
				columnHandlers.add(columnHandler);
			}
		}
		
		return columnHandlers;
	}
	
	private static BuildConfigHandler parseForDataModelHandler(Element element) throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		String dataModelHandlerClsName = element.getAttribute("class");
		BuildConfigHandler columnHandler = (BuildConfigHandler) Class.forName(dataModelHandlerClsName).newInstance();
		return columnHandler;
	}
}
