package com.xhh.codegen.service;


public interface BuildConfigHandler {
	/**
	 * 初始化配置时处理
	 * @param buildConfig
	 */
	void initConfig(BuildConfig buildConfig);
	/**
	 * 获取数据模型前处理
	 * @param buildConfig
	 */
	void beforeParseDataModel(BuildConfig buildConfig);
	
	/**
	 * 获取数据模型后处理
	 * @param buildConfig
	 */
	void afterParseDataModel(BuildConfig buildConfig);
	
	/**
	 * 获取输出模型前处理
	 * @param buildConfig
	 */
	void beforeParseOutputModel(BuildConfig buildConfig);
	
	/**
	 * 获取输出模型后处理
	 * @param buildConfig
	 */
	void afterParseOutputModel(BuildConfig buildConfig);
}
