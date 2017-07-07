package com.xhh.codegen.service.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.xhh.codegen.model.JdbcConfig;
import com.xhh.codegen.model.TableMetaData;
import com.xhh.codegen.service.DbProvider;
import com.xhh.codegen.utils.JdbcUtil;

/**
 * 一个默认的数据库信息提供者，该类主要使用JDBC的方式来尝试获得列注释和表注释，
 * 而对于那些在JDBC驱动里没有提供列注释和表注释的DBMS，则需要另外扩展DbProvider来实现自定义获取列注释和表注释
 * @author 黄天政
 *
 */
public class DefaultProvider extends DbProvider {
	private static final long serialVersionUID = 512932698031580465L;

	public DefaultProvider(Connection conn) {
		super(conn);
	}
	
	public DefaultProvider(JdbcConfig jdbcConfig) {
		super(jdbcConfig);
	}

	/**
	 * 从JDBC中通过DatabaseMetaData.getColumns方法获取指定表所有列注释。
	 * 返回的集合的元素格式为：Map&lt;列名称,列注释&gt;。
	 */
	@Override
	protected Map<String, String> doGetColumnComments(String tableName) {
		ResultSet rs=null;
		Map<String, String> colCommentMap = new LinkedHashMap<String, String>();
		try {
			rs = getConn().getMetaData().getColumns(this.getCatalog(), this.getSchema(), tableName.toUpperCase(),null);
			while(rs.next()){
				colCommentMap.put(rs.getString("COLUMN_NAME"), rs.getString("REMARKS"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			JdbcUtil.safelyClose(rs);
		}
		return colCommentMap;
	}

	/**
	 * 从JDBC中通过DatabaseMetaData.getTables方法获取指定表所有列注释。
	 * 返回的集合的元素格式为：Map&lt;表名称,表注释&gt;。
	 */
	@Override
	protected Map<String, String> doGetTableComments() {
		Map<String, String> tableComments = new LinkedHashMap<String, String>();
		Map<String, TableMetaData> tmdMap = getTableMetaData();
		for (Entry<String,TableMetaData> entry : tmdMap.entrySet()) {
			tableComments.put(entry.getKey(), entry.getValue().getRemarks());
		}
		
		return tableComments;
	}

}
