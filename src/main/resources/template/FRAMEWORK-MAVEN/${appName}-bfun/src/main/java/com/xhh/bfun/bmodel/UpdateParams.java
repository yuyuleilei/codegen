package com.xhh.bfun.bmodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.xhh.bfun.benum.BaseEnum.IsAdd;
import com.xhh.bfun.benum.BaseEnum.TableOrPkEnum;
import com.xhh.bfun.bexception.BaseException;
import com.xhh.bfun.butils.TableAndPKFactory;

/**
 * 组装mysql的set语句，可以扩展大部分mysql的函数，暂时不做字段校验
 * @className: com.wg.bsp.base.utils.UpdateParams.java
 * @author hejianhui@wegooooo.com
 * @date: 2016年10月13日 下午4:52:37
 */
@SuppressWarnings("serial")
public class UpdateParams implements Serializable{
	
	protected String tableName;
	
	private List<String> fields;

    private List<Object> values;
    
    private Conditions conditions;
    
    @SuppressWarnings("unused")
	private UpdateParams(){}
    
    @SuppressWarnings("rawtypes")
	public UpdateParams(Class entityClass){
    	super();
    	fields = new ArrayList<String>();
    	values = new ArrayList<Object>();
    	if (entityClass==null) {
			throw new BaseException("反射类型没有传递进来：T", this.getClass());
		}
    	this.tableName = TableAndPKFactory.get(entityClass, TableOrPkEnum.TABLE);
    }
    
    public Conditions createConditions(boolean isConver){
    	if (isConver) {
			this.conditions = new Conditions();
		}else {
			if (this.conditions==null) {
				this.conditions = new Conditions();
			}
		}
    	return this.conditions;
    }
    
    /**
     * 赋值 ，例如 field=value,
     * @category 
     * @author hejianhui@wegooooo.com
     * @param field
     * @param value
     * @return
     * @time 2016年10月13日 下午4:47:20
     */
	public UpdateParams set(String field,Object value){
    	fields.add(field+"=");
    	values.add(value);
    	return this;
    }
    
    /**
     * 自身+-，例如 field=field+value
     * @category 
     * @author hejianhui@wegooooo.com
     * @param field
     * @param isAdd
     * @param value
     * @return
     * @time 2016年10月13日 下午4:51:39
     */
	public UpdateParams set(String field,IsAdd isAdd ,String value){
    	fields.add(field+"="+field+isAdd);
    	values.add(value);
    	return this;
    }

	public Conditions getConditions() {
		return conditions;
	}

	public void setConditions(Conditions conditions) {
		this.conditions = conditions;
	}

	public List<String> getFields() {
		return fields;
	}

	public List<Object> getValues() {
		return values;
	}
	
	
	public boolean valid(){
		if (fields==null || fields.size()==0) {
			return false;
		}

		if (values==null || values.size()==0) {
			return false;
		}
		
		if (fields.size()!=values.size()) {
			return false;
		}
		//字段验证，condition校验
		
		return true;
	}
	
	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
    
}
