package com.xhh.codegen.model;

import java.io.Serializable;

/**
 * 表元数据
 * @author 黄天政
 *
 */
public class TableMetaData implements Serializable{
	private static final long serialVersionUID = 335136284779416764L;
	/*TABLE_CAT String => table catalog (may be null) 
	TABLE_SCHEM String => table schema (may be null) 
	TABLE_NAME String => table name 
	TABLE_TYPE String => table type. Typical types are "TABLE", "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM". 
	REMARKS String => explanatory comment on the table 
	TYPE_CAT String => the types catalog (may be null) 
	TYPE_SCHEM String => the types schema (may be null) 
	TYPE_NAME String => type name (may be null) 
	SELF_REFERENCING_COL_NAME String => name of the designated "identifier" column of a typed table (may be null) 
	REF_GENERATION String => specifies how values in SELF_REFERENCING_COL_NAME are created. Values are "SYSTEM", "USER", "DERIVED". (may be null) */
	private String tableCat;
	private String tableSchem;
	private String tableName;
	private String tableType;
	private String remarks;
	private String typeCat;
	private String typeSchem;
	private String typeName;
	private String selfReferencingColName;
	private String refGeneration;
	/**
	 * @return 取得表类别（可为 null）
	 */
	public String getTableCat() {
		return tableCat;
	}
	/**
	 * @param tableCat 设置表类别（可为 null）
	 */
	public void setTableCat(String tableCat) {
		this.tableCat = tableCat;
	}
	/**
	 * @return 取得表模式（可为 null）
	 */
	public String getTableSchem() {
		return tableSchem;
	}
	/**
	 * @param tableSchem 设置表模式（可为 null）
	 */
	public void setTableSchem(String tableSchem) {
		this.tableSchem = tableSchem;
	}	
	/**
	 * @return 取得表名称
	 */
	public String getTableName() {
		return tableName;
	}
	/**
	 * @param tableName 设置表名称
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	/**
	 * @return 取得表类型。典型的类型是 "TABLE"、"VIEW"、"SYSTEM TABLE"、"GLOBAL TEMPORARY"、"LOCAL TEMPORARY"、"ALIAS" 和 "SYNONYM"。
	 */
	public String getTableType() {
		return tableType;
	}
	/**
	 * @param tableType 设置表类型。典型的类型是 "TABLE"、"VIEW"、"SYSTEM TABLE"、"GLOBAL TEMPORARY"、"LOCAL TEMPORARY"、"ALIAS" 和 "SYNONYM"。
	 */
	public void setTableType(String tableType) {
		this.tableType = tableType;
	}
	/**
	 * @return 取得表的解释性注释
	 */
	public String getRemarks() {
		return remarks;
	}
	/**
	 * @param remarks 设置表的解释性注释
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	/**
	 * @return 取得类型的类别（可为 null）
	 */
	public String getTypeCat() {
		return typeCat;
	}
	/**
	 * @param typeCat 设置类型的类别（可为 null）
	 */
	public void setTypeCat(String typeCat) {
		this.typeCat = typeCat;
	}
	/**
	 * @return 取得类型模式（可为 null）
	 */
	public String getTypeSchem() {
		return typeSchem;
	}
	/**
	 * @param typeSchem 设置类型模式（可为 null）
	 */
	public void setTypeSchem(String typeSchem) {
		this.typeSchem = typeSchem;
	}
	/**
	 * @return 取得类型名称（可为 null）
	 */
	public String getTypeName() {
		return typeName;
	}
	/**
	 * @param typeName 设置类型名称（可为 null）
	 */
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	/**
	 * @return 取得有类型表的指定 "identifier" 列的名称（可为 null）
	 */
	public String getSelfReferencingColName() {
		return selfReferencingColName;
	}
	/**
	 * @param selfReferencingColName 设置有类型表的指定 "identifier" 列的名称（可为 null）
	 */
	public void setSelfReferencingColName(String selfReferencingColName) {
		this.selfReferencingColName = selfReferencingColName;
	}
	/**
	 * @return 取得指定在 SELF_REFERENCING_COL_NAME 中创建值的方式。这些值为 "SYSTEM"、"USER" 和 "DERIVED"。（可能为 null）
	 */
	public String getRefGeneration() {
		return refGeneration;
	}
	/**
	 * @param refGeneration 设置指定在 SELF_REFERENCING_COL_NAME 中创建值的方式。这些值为 "SYSTEM"、"USER" 和 "DERIVED"。（可能为 null）
	 */
	public void setRefGeneration(String refGeneration) {
		this.refGeneration = refGeneration;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TableMetaData [tableCat=");
		builder.append(tableCat);
		builder.append(", tableSchem=");
		builder.append(tableSchem);
		builder.append(", tableName=");
		builder.append(tableName);
		builder.append(", tableType=");
		builder.append(tableType);
		builder.append(", remarks=");
		builder.append(remarks);
		builder.append(", typeCat=");
		builder.append(typeCat);
		builder.append(", typeSchem=");
		builder.append(typeSchem);
		builder.append(", typeName=");
		builder.append(typeName);
		builder.append(", selfReferencingColName=");
		builder.append(selfReferencingColName);
		builder.append(", refGeneration=");
		builder.append(refGeneration);
		builder.append("]");
		return builder.toString();
	}
	
}
