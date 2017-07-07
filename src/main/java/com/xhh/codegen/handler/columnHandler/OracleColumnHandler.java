package com.xhh.codegen.handler.columnHandler;

import com.xhh.codegen.model.ColumnModel;
import com.xhh.codegen.service.ColumnHandler;

/**
 * 把oracle带下划线的列名称转换为驼峰式列名称
 * @author tengen
 *
 */
public class OracleColumnHandler implements ColumnHandler{

    public void handle(ColumnModel col) {
        //Java替换掉下划线并让紧跟它后面的字母大写  
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
    }
}
