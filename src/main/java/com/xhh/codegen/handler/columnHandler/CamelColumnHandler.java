package com.xhh.codegen.handler.columnHandler;

import java.io.Serializable;

import com.xhh.codegen.model.ColumnModel;
import com.xhh.codegen.service.ColumnHandler;

/**
 * 把oracle带下划线的列名称转换为驼峰式列名称
 * @author tengen
 *
 */
@SuppressWarnings("serial")
public class CamelColumnHandler implements ColumnHandler,Serializable{

	public void handle(ColumnModel col) {
		//下划线转驼峰式
        StringBuffer sb = new StringBuffer();  
        sb.append(col.getColumnName().toLowerCase());  
        int count = sb.indexOf("_");  
        while(count!=0){  
            int num = sb.indexOf("_",count);  
            count = num+1;  
            if(num!=-1){  
                char ss = sb.charAt(count);  
                char ia = (char) (ss - 32);  
                sb.replace(count,count+1,ia+"");  
            }  
        }
        String ss = sb.toString().replaceAll("_","");
		col.setColumnName(ss);
		//SMALLINT转Short
		if ("SMALLINT".equalsIgnoreCase(col.getColumnTypeName())) {
			col.setColumnClassName("java.lang.Short");
			col.setColumnSimpleClassName("Short");
		}
	}
}
