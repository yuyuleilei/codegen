package com.xhh.codegen.service;

import java.util.Map;

import com.xhh.codegen.model.OutputModel;

/**
 * 【构建配置】接口
 * @author 黄天政
 *
 */
public interface BuildConfig {
	/**
	 * 获取输出编码类型
	 * @return
	 */
	String getOutputEncoding();
	/**
	 * 获取数据模型
	 * @return
	 */
	Map<String,Object> getDataModel();
	
	/**
	 * 获取输出模型
	 * @return
	 */
	Map<String,OutputModel> getOutputModel();
	
}
