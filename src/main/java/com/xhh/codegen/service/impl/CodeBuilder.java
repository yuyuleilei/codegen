package com.xhh.codegen.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.xhh.codegen.model.InOutType;
import com.xhh.codegen.model.OutputModel;
import com.xhh.codegen.service.BuildConfig;
import com.xhh.codegen.service.Builder;
import com.xhh.codegen.utils.StringTemplateLoader;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * 代码生成器
 * @author 黄天政  360138308@qq.com
 *
 */
public class CodeBuilder implements Builder {
	private static Logger log = Logger.getLogger(Builder.class);
	
	private String outputEncoding = "UTF-8";
	private Locale locale = Locale.CHINA;
	private Map<String,Object> dataModelMap = new LinkedHashMap<String,Object>();
	private Map<String,OutputModel> outputMap = new LinkedHashMap<String, OutputModel>();
	
	/**
	 * 根据指定的数据模型集合和输出模型集合实例化一个代码文件生成器
	 * @param dataModelMap 数据模型集合
	 * @param outputMap 输出模型集合
	 */
	public CodeBuilder(Map<String, Object> dataModelMap,
			Map<String, OutputModel> outputMap) {
		super();
		this.dataModelMap = dataModelMap;
		this.outputMap = outputMap;
	}
	
	/**
	 * 根据一个构建配置模型实例化一个代码文件生成器
	 * @param buildConfig
	 */
	public CodeBuilder(BuildConfig buildConfig){
		this(buildConfig.getDataModel(),buildConfig.getOutputModel());
		if(buildConfig instanceof ProjectBuildConfig){
			ProjectBuildConfig pbConfig = (ProjectBuildConfig)buildConfig;
			if(pbConfig.isHasParseDataModel()==false){
				pbConfig.parseDataModel();
			}
			if(pbConfig.isHasParseOutputModel()==false){
				pbConfig.parseOutputModel();
			}
		}
		this.setOutputEncoding(buildConfig.getOutputEncoding());
	}

	/**
	 * @return 获取当前的本地化信息
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * @param locale 设置当前的本地化信息
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	/**
	 * @return 获取文件输出的编码类型
	 */
	public String getOutputEncoding() {
		return outputEncoding;
	}

	/**
	 * @param outputEncoding 设置文件输出的编码类型
	 */
	public void setOutputEncoding(String outputEncoding) {
		this.outputEncoding = outputEncoding;
	}

	/**
	 * @return 获取当前的数据模型集合
	 */
	public Map<String, Object> getDataModelMap() {
		return dataModelMap;
	}
	/**
	 * @param dataModelMap 设置当前的数据模型集合
	 */
	public void setDataModelMap(Map<String, Object> dataModelMap) {
		this.dataModelMap = dataModelMap;
	}
	/**
	 * @return 获取当前的输出模型集合
	 */
	public Map<String, OutputModel> getOutputMap() {
		return outputMap;
	}
	/**
	 * @param outputMap 设置当前的输出模型集合
	 */
	public void setOutputMap(Map<String, OutputModel> outputMap) {
		this.outputMap = outputMap;
	}
	
	
	/***
	 * 生成代码文件操作。
	 */
	public Map<String, OutputModel> build(){
		Template tp = null;
		PrintWriter pw = null;
		Configuration cfg = new Configuration();
		File templateFile, outputFile;
		try {
			cfg.setEncoding(locale, outputEncoding);
			for (Entry<String, OutputModel>  entry : outputMap.entrySet()) {
				if(entry.getValue().isDisabled()){
					continue; //禁用的输出模型不作生成
				}
				if(entry.getValue().getTemplateModel().getType()==InOutType.FILE){
					templateFile = new File(entry.getValue().getTemplateModel().getTemplate());
					if(templateFile.exists()==false){
						throw new IOException(String.format("模板文件%s不存在！",templateFile));
					}
					cfg.setDirectoryForTemplateLoading(templateFile.getParentFile());
					tp = cfg.getTemplate(templateFile.getName(), locale);
				}else{
					cfg.setTemplateLoader(new StringTemplateLoader(entry.getValue().getTemplateModel().getTemplate())); 
					tp = cfg.getTemplate(""); 
				}
				tp.setEncoding(outputEncoding);
				if(entry.getValue().getType()==InOutType.FILE){
					outputFile = new File(entry.getValue().getOutput());
					if(outputFile.getParentFile().exists()==false){
						outputFile.getParentFile().mkdirs();
					}
					pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outputFile), outputEncoding));
					tp.process(dataModelMap, pw);
					pw.close();
					log.debug("构建并输出文件="+outputFile.getAbsolutePath());
				}else{
					StringWriter writer = new StringWriter(); 
					tp.process(dataModelMap, writer);
					entry.getValue().setOutput(writer.toString());
				}
			}
		} catch (TemplateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if(pw!=null){
				pw.close();
			}
		}
		
		return outputMap;
	}
}
