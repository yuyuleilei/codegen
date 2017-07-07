package com.xhh.codegen.service.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.xhh.codegen.model.ForeignKeyModel;
import com.xhh.codegen.model.JdbcConfig;
import com.xhh.codegen.model.PrimaryKeyModel;
import com.xhh.codegen.service.DbProvider;
import com.xhh.codegen.utils.JdbcUtil;

/**
 * 针对Oracle的数据库信息提供者
 * @author 黄天政
 *
 */
public class OracleProvider extends DbProvider {
	private static final long serialVersionUID = -4293483654633811103L;

	public OracleProvider(Connection conn) {
		super(conn);
	}
	
	public OracleProvider(JdbcConfig jdbcConfig) {
		super(jdbcConfig);
	}
	
	/**
	 * 统一设置大写格式的表名模式
	 */
	public void setTableNamePatterns(String tableNamePatterns) {
		if(StringUtils.isNotBlank(tableNamePatterns)){
			tableNamePatterns = tableNamePatterns.toUpperCase();
		}
		super.setTableNamePatterns(tableNamePatterns);
	}
	
	@Override
	protected Map<String, String> doGetColumnComments(String tableName) {
		Map<String, String> colComment = new LinkedHashMap<String, String>();
		String columnName = null, comment = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = "select * from USER_COL_COMMENTS where TABLE_NAME='"+tableName.toUpperCase()+"'";
		try{
			stmt = getConn().createStatement();
			rs = stmt.executeQuery(sql);
			while(rs.next()){
				columnName = rs.getString("COLUMN_NAME").toLowerCase();
				comment = StringUtils.trim(rs.getString("COMMENTS"));
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
		String sql = "select * from USER_TAB_COMMENTS WHERE 1=1 ";
		String tnps = getTableNamePatterns();
		if(StringUtils.isNotBlank(tnps)){
			for (String tnp : tnps.split(",")) {
				sql += " AND TABLE_NAME LIKE '"+tnp+"' ";
			}			
		}
		try{
			stmt = getConn().createStatement();
			rs = stmt.executeQuery(sql);
			while(rs.next()){
				tableComments.put(rs.getString("TABLE_NAME").toLowerCase(), rs.getString("COMMENTS")) ;
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			JdbcUtil.safelyClose(rs, stmt);
		}
		return tableComments;
	}

	@Override
	protected List<PrimaryKeyModel> getPrimaryKeys(String tableName) {
		//oracle在此方法调用需要大写表名
		tableName = StringUtils.upperCase(tableName);
		return super.getPrimaryKeys(tableName);
	}

	@Override
	protected List<ForeignKeyModel> getImportedKeys(String tableName) {
		//oracle在此方法调用需要大写表名
		tableName = StringUtils.upperCase(tableName);
		return super.getImportedKeys(tableName);
	}

	@Override
	protected List<ForeignKeyModel> getExportedKeys(String tableName) {
		//oracle在此方法调用需要大写表名
		tableName = StringUtils.upperCase(tableName);
		return super.getExportedKeys(tableName);
	}

	
}
