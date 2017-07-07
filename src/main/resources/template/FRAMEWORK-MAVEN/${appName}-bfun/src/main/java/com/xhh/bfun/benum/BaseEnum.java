package com.xhh.bfun.benum;

public class BaseEnum {

	/**
	 * 条件符号枚举
	 * @className: com.wg.bsp.base.constant.BaseEnum.java
	 * @author hejianhui@wegooooo.com
	 * @date: 2016年10月12日 上午10:23:37
	 */
	public enum ConditionEnum{
		GT(" > "),//>
		GT_(" &gt; "),//>
		GTE(" >= "),//>=
		GTE_(" &gt;= "),//>=
		LT(" < "),//<
		LT_(" &lt; "),//<
		LTE(" <= "),
		LTE_(" &lt;= "),
		EQ(" = "),
		NOTEQ(" <> "),//<>
		NOTLIKE(" not like "),//
		LIKE(" like ");
		
		private String msg;
		
		ConditionEnum(String msg){
			this.msg = msg;
		}
		
		public String get(){
			return msg;
		}
	}
	
	public enum IsNullEnum{
		NOTNULL(" is not null "),
		ISNULL(" is null ");
		
		private String msg;
		
		IsNullEnum(String msg){
			this.msg = msg;
		}
		
		public String get(){
			return msg;
		}
	}
	
	/*public enum LikeEnum{
		LIKE(" like "),
		NOTLIKE(" not like ");
		
		private String msg;
		
		LikeEnum(String msg){
			this.msg = msg;
		}
		
		public String get(){
			return msg;
		}
	}*/
	
	public enum DESCEnum{
		DESC(" desc "),
		ASC(" asc ");
		
		private String msg;
		
		DESCEnum(String msg){
			this.msg = msg;
		}
		
		public String get(){
			return msg;
		}
	}
	
	public enum IsInEnum{
		IN(" in "),
		NOTIN(" not in ");
		
		private String msg;
		
		IsInEnum(String msg){
			this.msg = msg;
		}
		
		public String get(){
			return msg;
		}
	}
	
	public enum IsBTEnum{
		BT(" between "),
		NOTBT(" not between ");
		
		private String msg;
		
		IsBTEnum(String msg){
			this.msg = msg;
		}
		
		public String get(){
			return msg;
		}
	}
	
	/**
	 * and或or枚举
	 * @className: com.wg.bsp.base.constant.BaseEnum.java
	 * @author hejianhui@wegooooo.com
	 * @date: 2016年10月13日 上午10:30:35
	 */
	public enum AndOrEnum{
		AND(" and "),
		OR(" or ");
		
		private String msg;
		
		AndOrEnum(String msg){
			this.msg = msg;
		}
		
		public String get(){
			return msg;
		}
	}
	
	/**
	 * 加减枚举
	 * @className: com.wg.bsp.base.constant.BaseEnum.java
	 * @author hejianhui@wegooooo.com
	 * @date: 2016年10月13日 下午4:49:24
	 */
	public enum IsAdd{
		ADD(" + "),
		SUBTRACT(" - ");
		
		private String msg;
		
		IsAdd(String msg){
			this.msg = msg;
		}
		
		public String get(){
			return msg;
		}
	}
	
	/**
	 * 业务异常枚举
	 * @className: com.wg.bsp.base.constant.BaseEnum.java
	 * @author hejianhui@wegooooo.com
	 * @date: 2016年10月12日 上午10:32:06
	 */
	public enum BusinessExceptionEnum{
		
		PARAMSEXCEPTION("paramsexception",1001,"参数错误"),
		SYSBUSYEXCEPTION("sysbusyexception",1002,"系统繁忙"),
		INSERT("insert",1003,"添加记录失败,请稍后再试"),
		DELETE("delete",1004,"删除记录失败,请稍后再试"),
		UPDATE("update",1005,"修改记录失败,请稍后再试"),
		//
		NOTEXISTS("not exists",1006,"抱歉，该记录不存在");
		
		
		private String type;
		private int code;
		private String msg;
		
		BusinessExceptionEnum(String type,int code,String remark){
			this.type = type;
			this.code = code;
			this.msg = remark;
		}

		public String Type() {
			return type;
		}

		public int Code() {
			return code;
		}

		public String Msg() {
			return msg;
		}
	}
	
	/**
	 * 数据层 异常枚举 2
	 * @author 何建辉
	 *
	 */
	public enum DBExceptionEnum{
		
		INSERT("insert",2001,"添加记录失败,请稍后再试"),
		DELETE("delete",2002,"删除记录失败,请稍后再试"),
		UPDATE("update",2003,"修改记录失败,请稍后再试");
		
		private String type;
		private int code;
		private String msg;
		
		DBExceptionEnum(String type,int code,String remark){
			this.type = type;
			this.code = code;
			this.msg = remark;
		}

		public String Type() {
			return type;
		}

		public int Code() {
			return code;
		}

		public String Msg() {
			return msg;
		}
	}
	
	/**
	 * 表或主键
	 * @className: com.wg.bsp.base.constant.BaseEnum.java
	 * @author hejianhui@wegooooo.com
	 * @date: 2016年10月13日 下午8:01:33
	 */
	public enum TableOrPkEnum{
		TABLE,
		PK;
	}
}
