package com.xhh.bfun.butils;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.xhh.bfun.bannotation.PK;
import com.xhh.bfun.bannotation.Table;
import com.xhh.bfun.benum.BaseEnum.TableOrPkEnum;
import com.xhh.bfun.bexception.BaseException;

/**
 * 只支持一个主键
 * @className: com.wg.bsp.base.utils.TableAndPKFactory.java
 * @author hejianhui@wegooooo.com
 * @date: 2016年10月13日 下午8:11:06
 */
public class TableAndPKFactory {
	
	private static ConcurrentMap<String, String> tableAndPkMap = new ConcurrentHashMap<String, String>();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static String get(Class clz, TableOrPkEnum tableOrPk) {
		String clzName = clz.getName();
		if (tableOrPk.equals(TableOrPkEnum.TABLE)) {
			if (tableAndPkMap.containsKey(clzName)) {
				return tableAndPkMap.get(clzName);
			}else {
				Table tableAnnotation = (Table) clz.getAnnotation(Table.class);
				String table = tableAnnotation.value();
				tableAndPkMap.put(clzName, table);
				return table;
			}
		}else {
			String pk = clzName+".PK";
			if (tableAndPkMap.containsKey(clzName+".PK")) {
				return tableAndPkMap.get(pk);
			}else {
				Field[] fields = clz.getFields();
				for (Field field : fields) {
					PK pkAnnotation = field.getAnnotation(PK.class);
					pk = pkAnnotation.value();
					if (pkAnnotation==null || pk==null || "".equals(pk)) {
						throw new BaseException("该实体没有定义主键(@PK)注解", TableAndPKFactory.class);
					}
					return pk;
				}
			}
		}
		return null;
	}
	
}
