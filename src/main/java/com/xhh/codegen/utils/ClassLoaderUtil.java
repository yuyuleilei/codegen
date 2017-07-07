/*
 * Copyright 2002-2003,2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xhh.codegen.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

	/**
	 *用来加载类，classpath下的资源文件，属性文件等。
	 *getExtendResource(StringrelativePath)方法，可以使用../符号来加载classpath外部的资源。
	 */
	public class ClassLoaderUtil {
	    private static Log log=LogFactory.getLog(ClassLoaderUtil.class);
	    /**
	     *Thread.currentThread().getContextClassLoader().getResource("")
	     */
	    
	    /**
	     *加载Java类。 使用全限定类名
	     *@paramclassName
	     *@return
	     */
	    @SuppressWarnings("rawtypes")
		public static Class loadClass(String className) {
	        try {
	          return getClassLoader().loadClass(className);
	        } catch (ClassNotFoundException e) {
	          throw new RuntimeException("class not found '"+className+"'", e);
	        }
	     }
	     /**
	       *得到类加载器
	       *@return
	       */
	     public static ClassLoader getClassLoader() {
	     
	        return ClassLoaderUtil.class.getClassLoader();
	     }
	     /**
	       *提供相对于classpath的资源路径，返回文件的输入流
	       *@paramrelativePath必须传递资源的相对路径。是相对于classpath的路径。如果需要查找classpath外部的资源，需要使用../来查找
	       *@return 文件输入流
	     *@throwsIOException
	     *@throwsMalformedURLException
	       */
	     public static InputStream getStream(String relativePath) throws MalformedURLException, IOException {
	         if(!relativePath.contains("../")){
	             return getClassLoader().getResourceAsStream(relativePath);
	             
	         }else{
	             return ClassLoaderUtil.getStreamByExtendResource(relativePath);
	         }
	        
	     }
	     /**
	       *
	       *@paramurl
	       *@return
	       *@throwsIOException
	       */
	     public static InputStream getStream(URL url) throws IOException{
	         if(url!=null){
	             
	                return url.openStream();
	            
	             
	         }else{
	             return null;
	         }
	     }
	     /**
	       *
	       *@paramrelativePath必须传递资源的相对路径。是相对于classpath的路径。如果需要查找classpath外部的资源，需要使用../来查找
	       *@return
	       *@throwsMalformedURLException
	       *@throwsIOException
	       */
	     public static InputStream getStreamByExtendResource(String relativePath) throws MalformedURLException, IOException{
	        return ClassLoaderUtil.getStream(ClassLoaderUtil.getExtendResource(relativePath));
	         
	         
	     }
	     
	      /**
	       *提供相对于classpath的资源路径，返回属性对象，它是一个散列表
	       *@paramresource
	       *@return
	       */
	     public static Properties getProperties(String resource) {
	        Properties properties = new Properties();
	        try {
	          properties.load(getStream(resource));
	        } catch (IOException e) {
	          throw new RuntimeException("couldn't load properties file '"+resource+"'", e);
	        }
	        return properties;
	     }
	     /**
	       *得到本Class所在的ClassLoader的Classpat的绝对路径。
	       *URL形式的
	       *@return
	       */
	     public static String getAbsolutePathOfClassLoaderClassPath(){
	         
	         
	         ClassLoaderUtil.log.info(ClassLoaderUtil.getClassLoader().getResource("").toString());
	         return ClassLoaderUtil.getClassLoader().getResource("").toString();
	         
	     }
	     /**
	       *
	       *@paramrelativePath 必须传递资源的相对路径。是相对于classpath的路径。如果需要查找classpath外部的资源，需要使用../来查找
	       *@return资源的绝对URL
	     *@throwsMalformedURLException
	       */
	     public static URL getExtendResource(String relativePath) throws MalformedURLException{
	     
	         ClassLoaderUtil.log.info("传入的相对路径："+relativePath) ;
	         //ClassLoaderUtil.log.info(Integer.valueOf(relativePath.indexOf("../"))) ;
	         if(!relativePath.contains("../")){
	             return ClassLoaderUtil.getResource(relativePath);
	             
	         }
	         String classPathAbsolutePath=ClassLoaderUtil.getAbsolutePathOfClassLoaderClassPath();
	         if(relativePath.substring(0, 1).equals("/")){
	             relativePath=relativePath.substring(1);
	         }
	         ClassLoaderUtil.log.info(Integer.valueOf(relativePath.lastIndexOf("../"))) ;
	        
	         String wildcardString=relativePath.substring(0,relativePath.lastIndexOf("../")+3);
	        relativePath=relativePath.substring(relativePath.lastIndexOf("../")+3);
	         int containSum=ClassLoaderUtil.containSum(wildcardString, "../");
	         classPathAbsolutePath= ClassLoaderUtil.cutLastString(classPathAbsolutePath, "/", containSum);
	         String resourceAbsolutePath=classPathAbsolutePath+relativePath;
	         ClassLoaderUtil.log.info("绝对路径："+resourceAbsolutePath) ;
	         URL resourceAbsoluteURL=new URL(resourceAbsolutePath);
	         return resourceAbsoluteURL;
	     }
	     /**
	      *
	       *@paramsource
	       *@paramdest
	       *@return
	       */
	     private static int containSum(String source,String dest){
	         int containSum=0;
	         int destLength=dest.length();
	         while(source.contains(dest)){
	             containSum=containSum+1;
	             source=source.substring(destLength);
	             
	         }
	         
	         
	         return containSum;
	     }
	     /**
	       *
	       *@paramsource
	       *@paramdest
	       *@paramnum
	       *@return
	       */
	     private static String cutLastString(String source,String dest,int num){
	         // String cutSource=null;
	         for(int i=0;i<num;i++){
	             source=source.substring(0, source.lastIndexOf(dest, source.length()-2)+1);
	             
	             
	         }
	         
	         
	         
	         return source;
	     }
	     /**
	       *
	       *@paramresource
	       *@return
	       */
	      public static URL getResource(String resource){
	      ClassLoaderUtil.log.info("传入的相对于classpath的路径："+resource) ;
	         return ClassLoaderUtil.getClassLoader().getResource(resource);
	     }
	     
	 
	     
	 
	    /**
	     *@paramargs
	     *@throwsMalformedURLException
	     */
	    public static void main(String[] args) throws MalformedURLException {
	        
	            //ClassLoaderUtil.getExtendResource("../spring/dao.xml");
	        //ClassLoaderUtil.getExtendResource("../../../src/log4j.properties");
	        ClassLoaderUtil.getExtendResource("log4j.properties");
	        
	        System.out.println(ClassLoaderUtil.getClassLoader().getResource("log4j.properties").toString());
	 
	    }

}
