package com.xhh.codegen.service;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.xhh.codegen.model.ColumnModel;
import com.xhh.codegen.model.ForeignKeyModel;
import com.xhh.codegen.model.JdbcConfig;
import com.xhh.codegen.model.PrimaryKeyModel;
import com.xhh.codegen.model.TableMetaData;
import com.xhh.codegen.model.TableModel;
import com.xhh.codegen.service.impl.DataTypeConverterForJava;
import com.xhh.codegen.utils.IgnoreCaseMap;
import com.xhh.codegen.utils.JdbcUtil;

/**
 * 数据库信息提供者。该抽象类基于JDBC实现了与数据库方言无关的方法，把与数据库方言相关的操作分派给子类实现。
 * @author 黄天政 
 */
public abstract class DbProvider implements Serializable{
	private static final long serialVersionUID = -7312979095006311626L;
	private Connection conn;
	private JdbcConfig jdbcConfig;
	private String catalog = null;
	private String schema = null;
	private String tableNamePatterns = null;
	private List<ColumnHandler> columnHandlers = new ArrayList<ColumnHandler>();
	private Map<String,Map<String,String>> columnComments;
	private Map<String,String> tableComments;
	private List<String> tableNames;
	private Map<String, TableMetaData> tableMetaDatas;
	/**
	 * 从表（列）注释中提取表（列）标签所用的分隔符，默认值为一个空格符
	 */
	private String splitorForLabelFromComment = " ";

	/**
	 * 根据数据库连接构造一个数据库信息提供者
	 * @param conn 数据库连接
	 */
	public DbProvider(Connection conn) {
		super();
		this.conn = conn;
	}
	
	/**
	 * 根据jdbc配置模型构造一个数据库信息提供者
	 * @param jdbcConfig jdbc配置模型
	 */
	public DbProvider(JdbcConfig jdbcConfig) {
		super();
		this.jdbcConfig = jdbcConfig;
	}
	
	/**
	 * @return 获取一个数据库连接
	 */
	protected Connection getConn() {
		if(conn==null){
			if(jdbcConfig==null){
				try {
					throw new Exception(this.getClass().getName()+"jdbcConfig和conn不能同时为null");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			conn = JdbcUtil.getConn(jdbcConfig);
		}
		return conn;
	}

	/**
	 * @return 获取当前的数据库类别(一般为数据库名，可能为空)
	 */
	public String getCatalog() {
		return catalog;
	}

	/**
	 * @param catalog 设置当前的数据库类别(一般为数据库名，可以为空)
	 */
	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	/**
	 * @return 获取当前的数据库属主(一般为用户名，可能为空)
	 */
	public String getSchema() {
		return schema;
	}

	/**
	 * @param schema 设置当前的数据库属主(一般为用户名，可以为空)
	 */
	public void setSchema(String schema) {
		this.schema = schema;
	}
	
	/**
	 * @return 获取表名模式，支持多个表名模式，以英文逗号分隔
	 */
	public String getTableNamePatterns() {
		return tableNamePatterns;
	}

	/**
	 * @param tableNamePatterns 设置表名模式
	 */
	public void setTableNamePatterns(String tableNamePatterns) {
		this.tableNamePatterns = tableNamePatterns;
	}

	/**
	 * @return 取得一组列模型处理器
	 */
	public List<ColumnHandler> getColumnHandlers() {
		return columnHandlers;
	}

	/**
	 * @param columnHandlers 设置一组列模型处理器
	 */
	public void setColumnHandlers(List<ColumnHandler> columnHandlers) {
		this.columnHandlers = columnHandlers;
	}
	
	/**
	 * 获取在catalog和schema范围内的所有表名称。
	 * @return
	 */
	public List<String> getTableNames(){
		if(tableNames!=null){
			return tableNames;
		}
		tableNames = new ArrayList<String>();
		ResultSet rs = null;
		try{
			String tnps = getTableNamePatterns();
			if(StringUtils.isBlank(tnps)){
				rs = getConn().getMetaData().getTables(catalog, schema, null, null);
				while(rs.next()){
					tableNames.add(rs.getString("TABLE_NAME"));
				}
			}else{
				for (String tnp : tnps.split(",")) {
					rs = getConn().getMetaData().getTables(catalog, schema, tnp, null);
					while(rs.next()){
						tableNames.add(rs.getString("TABLE_NAME"));
					}					
				}
			}			
			
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			JdbcUtil.safelyClose(rs);
		}
		return tableNames;
	}
	/**
	 * 取得表的元数据
	 * @return
	 */
	public Map<String, TableMetaData> getTableMetaData(){
		if(tableMetaDatas!=null&&tableMetaDatas.size()>0){
			return tableMetaDatas;
		}
		tableMetaDatas = new IgnoreCaseMap<String, TableMetaData>();

		String tnps = getTableNamePatterns();
		if(StringUtils.isBlank(tnps)){
			tableMetaDatas.putAll(getTableMetaData(null));
		}else{
			for (String tnp : tnps.split(",")) {
				tableMetaDatas.putAll(getTableMetaData(tnp));
			}
		}
		
		return tableMetaDatas;
	}

	/**
	 * 取得表的元数据
	 * @param tableNamePattern 表名称匹配字符串
	 * @return
	 */
	private Map<String, TableMetaData> getTableMetaData(String tableNamePattern){
		ResultSet rs = null;
		try{
			rs = getConn().getMetaData().getTables(catalog, schema, tableNamePattern, null);
			List<String> columnNameList = new ArrayList<String>();
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				columnNameList.add(rsmd.getColumnName(i));
			}
			
			TableMetaData tmd;
			while(rs.next()){
				tmd = new TableMetaData();
				if(columnNameList.contains("TABLE_CAT")){
					tmd.setTableCat(rs.getString("TABLE_CAT"));
				}
				if(columnNameList.contains("TABLE_SCHEM")){
					tmd.setTableSchem(rs.getString("TABLE_SCHEM"));
				}
				if(columnNameList.contains("TABLE_NAME")){
					tmd.setTableName(rs.getString("TABLE_NAME"));
				}
				if(columnNameList.contains("TABLE_TYPE")){
					tmd.setTableType(rs.getString("TABLE_TYPE"));
				}
				if(columnNameList.contains("REMARKS")){
					tmd.setRemarks(rs.getString("REMARKS"));
				}
				if(columnNameList.contains("TYPE_CAT")){
					tmd.setTypeCat(rs.getString("TYPE_CAT"));
				}
				if(columnNameList.contains("TYPE_SCHEM")){
					tmd.setTypeSchem(rs.getString("TYPE_SCHEM"));
				}
				if(columnNameList.contains("TYPE_NAME")){
					tmd.setTypeName(rs.getString("TYPE_NAME"));
				}
				if(columnNameList.contains("SELF_REFERENCING_COL_NAME")){
					tmd.setSelfReferencingColName(rs.getString("SELF_REFERENCING_COL_NAME"));
				}
				if(columnNameList.contains("REF_GENERATION")){
					tmd.setRefGeneration(rs.getString("REF_GENERATION"));
				}
				
				tableMetaDatas.put(tmd.getTableName(),tmd);
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			JdbcUtil.safelyClose(rs);
		}
		return tableMetaDatas;
	}
	
	/**
	 * 清理表的元数据缓存
	 */
	public void clearTableMetaDataCache(){
		if(tableMetaDatas!=null){
			tableMetaDatas.clear();
		}
	}
	
	/**
	 * 取得所有表的表注释，不同的数据库方言有不同的实现方式。
	 * @return
	 */
	protected abstract Map<String,String> doGetTableComments();
	
	/**
	 * 一次性获取所有表的表注释，不同的数据库方言有不同的实现方式。
	 * @return
	 */
	public Map<String,String> getTableComments(){
		if(tableComments==null){
			tableComments = new IgnoreCaseMap<String, String>(doGetTableComments());
		}
		return tableComments;
	}
	
	/**
	 * 获取指定表的所有的列名称
	 * @return
	 */
	public List<String> getColumnNames(String tableName){
		List<String> colList = new ArrayList<String>();
		Statement stmt = null;
		ResultSet rs = null;
		try{
			stmt = getConn().createStatement();
			String sql = "select * from " + tableName;
			rs = stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();
			for(int i=1;i<=rsmd.getColumnCount();i++){
				colList.add(rsmd.getColumnName(i));
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			JdbcUtil.safelyClose(rs, stmt);
		}
		return colList;
	}
	
	/**
	 * 获取指定表所有列注释。返回的集合的元素格式为：列名(COLUMN_NAME)和注释(COMMENTS)。
	 * @return
	 */
	protected abstract Map<String, String> doGetColumnComments(String tableName);
	
	/**
	 * 一次性获取指定表的所有列注释。返回的集合的元素格式为：列名(COLUMN_NAME)和注释(COMMENTS)。
	 * @return 
	 */
	protected Map<String, String> getColumnComments(String tableName){
		if(columnComments==null){
			columnComments = new IgnoreCaseMap<String, Map<String,String>>();			
		}
		Map<String, String> resultMap =columnComments.get(tableName);
		if(resultMap==null){
			resultMap = new IgnoreCaseMap<String, String>(doGetColumnComments(tableName));
			columnComments.put(tableName,resultMap);
		}
		return resultMap;
	}
	
	/**
	 * 获取指定表的主键模型集合
	 * @param tableName 表名称
	 * @return
	 */
	protected List<PrimaryKeyModel> getPrimaryKeys(String tableName){
		ResultSet rs=null;
		List<PrimaryKeyModel> pkModelList = new ArrayList<PrimaryKeyModel>();
		PrimaryKeyModel pkModel ;
		try {
			rs = getConn().getMetaData().getPrimaryKeys(this.catalog, this.schema , tableName);
			while(rs.next()){
				pkModel = new PrimaryKeyModel();
				pkModel.setTableCat(rs.getString("TABLE_CAT"));
				pkModel.setTableSchem(rs.getString("TABLE_SCHEM"));
				pkModel.setTableName(rs.getString("TABLE_NAME"));
				pkModel.setColumnName(rs.getString("COLUMN_NAME"));
				pkModel.setKeySeq(rs.getShort("KEY_SEQ"));
				pkModel.setPkName(rs.getString("PK_NAME"));
				
				pkModelList.add(pkModel); 
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			JdbcUtil.safelyClose(rs);
		}
		return pkModelList;
	}
	/**
	 * 获取指定表的外键（引入）模型集合
	 * @param tableName 表名称
	 * @return
	 */
	protected List<ForeignKeyModel> getImportedKeys(String tableName){
		ResultSet rs=null;
		List<ForeignKeyModel> fkModelList = new ArrayList<ForeignKeyModel>();
		ForeignKeyModel fkModel ;
		try {
			rs = getConn().getMetaData().getImportedKeys(catalog, schema, tableName);
			while(rs.next()){
				fkModel = new ForeignKeyModel();
				fkModel.setPkTableCat(rs.getString("PKTABLE_CAT"));
				fkModel.setPkTableSchem(rs.getString("PKTABLE_SCHEM"));
				fkModel.setPkTableName(rs.getString("PKTABLE_NAME"));
				fkModel.setPkColumnName(rs.getString("PKCOLUMN_NAME"));
				fkModel.setFkTableCat(rs.getString("FKTABLE_CAT"));
				fkModel.setFkTableSchem(rs.getString("FKTABLE_SCHEM"));
				fkModel.setFkTableName(rs.getString("FKTABLE_NAME"));
				fkModel.setFkColumnName(rs.getString("FKCOLUMN_NAME"));
				fkModel.setKeySeq(rs.getShort("KEY_SEQ"));
				fkModel.setUpdateRule(rs.getShort("UPDATE_RULE"));
				fkModel.setDeleteRule(rs.getShort("DELETE_RULE"));
				fkModel.setPkName(rs.getString("PK_NAME"));
				fkModel.setFkName(rs.getString("FK_NAME"));
				fkModel.setDeferrability(rs.getShort("DEFERRABILITY"));
				fkModelList.add(fkModel); 
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			JdbcUtil.safelyClose(rs);
		}
		return fkModelList;
	}
	
	/**
	 * 获取指定表的外键（导出）模型集合
	 * @param tableName 表名称
	 * @return
	 */
	protected List<ForeignKeyModel> getExportedKeys(String tableName){
		ResultSet rs=null;
		List<ForeignKeyModel> fkModelList = new ArrayList<ForeignKeyModel>();
		ForeignKeyModel fkModel ;
		try {
			rs = getConn().getMetaData().getExportedKeys(catalog, schema, tableName);
			while(rs.next()){
				fkModel = new ForeignKeyModel();
				fkModel.setPkTableCat(rs.getString("PKTABLE_CAT"));
				fkModel.setPkTableSchem(rs.getString("PKTABLE_SCHEM"));
				fkModel.setPkTableName(rs.getString("PKTABLE_NAME"));
				fkModel.setPkColumnName(rs.getString("PKCOLUMN_NAME"));
				fkModel.setFkTableCat(rs.getString("FKTABLE_CAT"));
				fkModel.setFkTableSchem(rs.getString("FKTABLE_SCHEM"));
				fkModel.setFkTableName(rs.getString("FKTABLE_NAME"));
				fkModel.setFkColumnName(rs.getString("FKCOLUMN_NAME"));
				fkModel.setKeySeq(rs.getShort("KEY_SEQ"));
				fkModel.setUpdateRule(rs.getShort("UPDATE_RULE"));
				fkModel.setDeleteRule(rs.getShort("DELETE_RULE"));
				fkModel.setPkName(rs.getString("PK_NAME"));
				fkModel.setFkName(rs.getString("FK_NAME"));
				fkModel.setDeferrability(rs.getShort("DEFERRABILITY"));
				fkModelList.add(fkModel); 
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			JdbcUtil.safelyClose(rs);
		}
		return fkModelList;
	}
	
	/**
	 * 根据表名获取表注释，如果未取得注释，则返回空字符串
	 * @param tableName 表名称
	 * @return
	 */
	public String getTableComment(String tableName){
		return getTableComment(tableName,"");
	}
	/**
	 * 根据表名获取表注释，如果未取得注释，则返回指定的默认值
	 * @param tableName 表名称
	 * @param defaultValue 指定的默认表注释
	 * @return
	 */
	public String getTableComment(String tableName, String defaultValue){
		String comment = getTableComments().get(tableName);
		return StringUtils.defaultString(comment, defaultValue);
	}
	
	/**
	 * @return 从表（列）注释中提取表（列）标签所用的分隔符，默认值为一个空格符
	 */
	public String getSplitorForLabelFromComment() {
		return splitorForLabelFromComment;
	}

	/**
	 * @param splitorForLabelFromComment 设置从表（列）注释中提取表（列）标签所用的分隔符，默认值为一个空格符
	 */
	public void setSplitorForLabelFromComment(String splitorForLabelFromComment) {
		this.splitorForLabelFromComment = splitorForLabelFromComment;
	}

	/**
	 * 从表注释中 提取表的标签名，如果未能成功获取，则返回指定的表注释。
	 * <br>表注释约定：表的标签名 + 分隔符  + 表的备注信息。
	 * <br>若splitorForLabelFromComment属性为空值，则返回整个表注释
	 * @param tabComment 指定的表注释
	 * @return
	 */
	public String getTableLabelFromComment(String tabComment){
		String label = tabComment;
		if(StringUtils.isNotBlank(splitorForLabelFromComment)){
			label = StringUtils.substringBefore(tabComment, splitorForLabelFromComment);//返回指定字符串（空格）之前的所有字符
		}	
		return StringUtils.defaultString(label);
	}
	/**
	 * 返回指定表指定列的注释，如果未取得注释，则返回指定的列名称。
	 * 该操作会把列注释里的所有换行符号和回车符号统一替换成空格符号
	 * @param tableName 表名
	 * @param columnName 列名
	 * @return
	 */
	public String getColumnComment(String tableName, String columnName){
		String comment = columnName;
		Map<String,String> ccmap = getColumnComments(tableName);
		if(ccmap!=null){
			comment = StringUtils.defaultString(ccmap.get(columnName),columnName)
						.replace("\n", " ").replace("\r", " "); //把换行符和回车符统一替换成空格
		}
		return comment;
	}
	/**
	 * 从列注释中获取列标签（列的中文别名），如果未能获取成功，则返回指定的列注释。
	 * <br>列注释约定：列标签  + 分隔符  + 列备注信息
	 * <br>若splitorForLabelFromComment属性为空值，则返回整个列注释
	 * @param columnComment 列注释
	 * @return
	 */
	public String getColumnLabelFromComment(String columnComment){
		if(StringUtils.isNotBlank(splitorForLabelFromComment)){
			return StringUtils.substringBefore(columnComment, splitorForLabelFromComment);//返回指定字符串之前的所有字符
		}else{
			return StringUtils.defaultString(columnComment);
		}
	}
	/**
	 * 从列注释中获取列备注信息，如果未能获取成功，则返回空字符串。
	 * <br>列注释约定：列标签  + 分隔符  + 列的备注信息
	 * <br>若splitorForLabelFromComment属性为空值，则返回整个列注释
	 * @param columnComment 列注释
	 * @return
	 */
	public String getColumnRemarkFromComment(String columnComment){
		if(StringUtils.isNotBlank(splitorForLabelFromComment)){
			return StringUtils.substringAfter(columnComment, splitorForLabelFromComment);//返回指定字符串（空格）之后的所有字符
		}else{
			return StringUtils.defaultString(columnComment);
		}
	}

	/**
	 * 判断列类型是否为不可以返回精度（Precision）的列类型，如oracle的大字段类型Blob和Clob。
	 * @param columnType
	 * @return
	 */
	protected boolean isPrecisionUnknowType(int columnType){
		return columnType==java.sql.Types.CLOB||columnType==java.sql.Types.BLOB;
	}
	
	/**
	 * 根据指定的表名创建一个表模型
	 * @param tableName 表名称
	 * @return
	 * @throws SQLException
	 */
	public TableModel createTableModel(String tableName){
		TableModel table = new TableModel();
		
		//设置表的相关元数据
		table.setTableName(tableName);
		table.setSchema(getSchema());
		table.setCatalog(getCatalog());		
		table.setPrimaryKeyList(getPrimaryKeys(table.getTableName()));
		table.setImportedKeyList(getImportedKeys(table.getTableName()));
		table.setExportedKeyList(getExportedKeys(table.getTableName()));
		table.setTabComment(getTableComment(table.getTableName(),table.getTableName()));
		table.setTableLabel(getTableLabelFromComment(table.getTabComment()));
		if(getTableMetaData().containsKey(tableName)){
			table.setTableType(getTableMetaData().get(tableName).getTableType());
		}
		
		Statement stmt = null;
		ResultSet rs = null;
		try{
			stmt = getConn().createStatement();
			String sql = "select * from " + tableName;
			rs = stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData(); 
			ColumnModel col = null;
			for(int i=1;i<=rsmd.getColumnCount();i++){
				col = new ColumnModel();
				table.getColumnList().add(col);
				
				col.setColumnName(rsmd.getColumnName(i));
				col.setFieldName(rsmd.getColumnName(i));
				col.setColComment(getColumnComment(tableName, col.getColumnName()));
				col.setColumnLabel(getColumnLabelFromComment(col.getColComment()));
				col.setColRemark(getColumnRemarkFromComment(col.getColComment()));
				//设置sql数据类型
				col.setColumnType(rsmd.getColumnType(i));
				//设置sql数据类型的名称
				col.setColumnTypeName(rsmd.getColumnTypeName(i));
				//设置列的sql数据类型在java编程语言中对应的具体数据类型
				col.setColumnClassName(rsmd.getColumnClassName(i));
								
				//取得列显示的最大宽度
				col.setColumnDisplaySize(rsmd.getColumnDisplaySize(i));
				//设置列的数据标度
				col.setScale(rsmd.getScale(i));
				//判断该列是否为自增列
				col.setAutoIncrement(rsmd.isAutoIncrement(i));
				//判断是否为货币类型字段
				col.setCurrency(rsmd.isCurrency(i));
				//判断字段是否为只读状态
				col.setReadonly(rsmd.isReadOnly(i));
				//判断该字段是否可以作为搜索条件，并出现在where语句中
				col.setSearchable(rsmd.isSearchable(i));
				//判断列类型是否为未知类型，如oracle的大字段类型Blob和Clob。
				if(!isPrecisionUnknowType(rsmd.getColumnType(i))){
					//如果不是未知类型，则设置列的数据精度
				    col.setPrecision(rsmd.getPrecision(i));
				}
				//判断该列允许空否
				if(rsmd.isNullable(i)==ResultSetMetaData.columnNoNulls){
					col.setNullable(false);
				}else{
					col.setNullable(true);
				}
				//判断是否为主键列
				if(table.isPrimaryKey(col.getColumnName())){
					col.setPrimaryKey(true);
				}
				//判断是否为外键列（参照其他表的键）
				if(table.isImportedKey(col.getColumnName())){
					col.setImportedKey(true);
				}
				//判断是否为外键列（被其他表参照）
				if(table.isExportedKey(col.getColumnName())){
					col.setExportedKey(true);
				}
				
				//设置列所归属的表模型，这样在列模型处理器里还可以根据表模型的数据来做更多的逻辑判断
				col.setTableModel(table);
				
				//如果没有设置任何的列模型处理器，则默认使用一个java数据类型转换器来处理列模型
				if(columnHandlers==null||columnHandlers.size()==0){					
					new DataTypeConverterForJava().handle(col);
				}else{
					for (ColumnHandler columnHandler : columnHandlers) {
						columnHandler.handle(col);
					}
				}
			}
			System.out.println("成功创建表模型"+table);
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			JdbcUtil.safelyClose(rs, stmt);
		}
		return table;
	}
}
