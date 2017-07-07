package com.xhh.codegen.service.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.xhh.codegen.model.JdbcConfig;
import com.xhh.codegen.service.DbProvider;
import com.xhh.codegen.utils.JdbcUtil;

/**
 * 针对Mysql的数据库信息提供者
 * @author 黄天政
 *
 */
public class MysqlProvider extends DbProvider {
	private static final long serialVersionUID = 3208056888252225777L;

	public MysqlProvider(Connection conn) {
		super(conn);
	}
	
	public MysqlProvider(JdbcConfig jdbcConfig) {
		super(jdbcConfig);
	}
	
	
	@Override
	protected Map<String, String> doGetColumnComments(String tableName) {
		Map<String, String> colComment = new LinkedHashMap<String, String>();
		String columnName = null, comment = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = "show full fields from "+tableName;
		try{
			stmt = getConn().createStatement();
			rs = stmt.executeQuery(sql);
			while(rs.next()){
				columnName = rs.getString("Field");
				comment = StringUtils.trim(rs.getString("Comment"));
				colComment.put(columnName, comment);
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			JdbcUtil.safelyClose(rs, stmt);
		}
		return colComment;
	}

	@Override
	protected Map<String, String> doGetTableComments() {
		Map<String, String> tableComments = new LinkedHashMap<String, String>();
		Statement stmt = null;
		ResultSet rs = null;
		String sql = "show table status where 1=1 ";
		String tnps = getTableNamePatterns();
		if(StringUtils.isNotBlank(tnps)){
			for (String tnp : tnps.split(",")) {
				sql += " AND Name LIKE '"+tnp+"' ";
			}			
		}
		try{
			stmt = getConn().createStatement();
			rs = stmt.executeQuery(sql);
			while(rs.next()){
				tableComments.put(rs.getString("Name"), rs.getString("Comment")) ;
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			JdbcUtil.safelyClose(rs, stmt);
		}
		return tableComments;
	}

}
