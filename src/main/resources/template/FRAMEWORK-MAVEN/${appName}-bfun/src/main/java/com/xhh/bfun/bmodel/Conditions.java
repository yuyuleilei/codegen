package com.xhh.bfun.bmodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xhh.bfun.benum.BaseEnum.ConditionEnum;
import com.xhh.bfun.benum.BaseEnum.DESCEnum;
import com.xhh.bfun.benum.BaseEnum.IsBTEnum;
import com.xhh.bfun.benum.BaseEnum.IsInEnum;
import com.xhh.bfun.benum.BaseEnum.IsNullEnum;
import com.xhh.bfun.benum.BaseEnum.TableOrPkEnum;
import com.xhh.bfun.butils.TableAndPKFactory;


public class Conditions implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	protected String tableName;
	
	protected List<String> connectTable;
	
	//是否验证字段有效，需要在实体字段加上注解，配合反射来验证，开启效率低。
	@SuppressWarnings("unused")
	private boolean isValidField = false;
	//用于字段验证时用，暂时不支持
	@SuppressWarnings("rawtypes")
	private Class entityClass;
	
	protected String orderByClause;
	
	protected boolean distinct = false;
	
	protected List<Criteria> oredCriteria = new ArrayList<Conditions.Criteria>();
	
	protected List<String> returnFields;
	
	//mysql
	protected String limit;
	
	//oracle
	protected Integer begin;
	
	protected Integer end;
	
	public Conditions(){}
	
	@SuppressWarnings({ "rawtypes" })
	public Conditions(Class entityClass){
		oredCriteria = new ArrayList<Criteria>();
		//暂时不开启
		if (entityClass!=null) {
			this.entityClass = entityClass;
			this.tableName = TableAndPKFactory.get(this.entityClass, TableOrPkEnum.TABLE);
		}
	}
	
    /**
     * This method corresponds to the database table tb_mem_account
     */
	protected Conditions(Conditions example) {
    	this.orderByClause = example.orderByClause;
        this.oredCriteria = example.oredCriteria;
        this.distinct = example.distinct;
    }
	
	public Conditions connectTable(String tableName){
		if (connectTable==null) {
			connectTable = new ArrayList<String>();
		}
		connectTable.add(tableName);
		return this;
	}

    /**
     * This method corresponds to the database table tb_mem_account
     */
    public void setOrderByClause(String orderByClause,DESCEnum desc) {
        this.orderByClause = " order by "+orderByClause+" "+desc.get()+" ";
    }
    
    /**
     * This method corresponds to the database table tb_mem_account
     */
    public void setOrderByClause(String[] orderByClause,DESCEnum[] desc) {
    	if (orderByClause==null || desc ==null || orderByClause.length!=desc.length) {
			throw new RuntimeException("参数个数不一致");
		}
    	StringBuilder sBuilder = new StringBuilder();
    	sBuilder.append(" order by ");
    	for (int i = 0; i < orderByClause.length; i++) {
			sBuilder.append(orderByClause[i]).append(" ").append(desc[i].get());
			if (i!=desc.length-1) {
				sBuilder.append(",");
			}
		}
        this.orderByClause = sBuilder.toString();
    }

    /**
     * This method corresponds to the database table tb_mem_account
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method corresponds to the database table tb_mem_account
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method corresponds to the database table tb_mem_account
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method corresponds to the database table tb_mem_account
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }
    
	/**
     * This method corresponds to the database table tb_mem_account
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method corresponds to the database table tb_mem_account
     */
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    /**
     * This method corresponds to the database table tb_mem_account
     */
    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    /**
     * This method corresponds to the database table tb_mem_account
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method corresponds to the database table tb_mem_account
     */
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }
    
    public List<String> getReturnFields() {
		return returnFields;
	}

	public void setReturnFields(List<String> returnFields) {
		this.returnFields = returnFields;
	}
	
	public void setReturnFields(String[] returnFields) {
		this.returnFields = new ArrayList<String>();
		for (String field : returnFields) {
			this.returnFields.add(field);
		}
	}

	public String getLimit() {
		return limit;
	}

	public void setLimit(int begin,int end){
		this.begin = begin;
		this.end = end;
	}

	@SuppressWarnings("serial")
	public static class Criteria implements Serializable{

    	protected List<String> criteriaWithoutValue;

        protected List<Map<String, Object>> criteriaWithSingleValue;

        protected List<Map<String, Object>> criteriaWithListValue;

        protected List<Map<String, Object>> criteriaWithBetweenValue;

        protected Criteria() {
            super();
            criteriaWithoutValue = new ArrayList<String>();
            criteriaWithSingleValue = new ArrayList<Map<String, Object>>();
            criteriaWithListValue = new ArrayList<Map<String, Object>>();
            criteriaWithBetweenValue = new ArrayList<Map<String, Object>>();
        }

        public boolean isValid() {
            return criteriaWithoutValue.size() > 0
                || criteriaWithSingleValue.size() > 0
                || criteriaWithListValue.size() > 0
                || criteriaWithBetweenValue.size() > 0;
        }

        public List<String> getCriteriaWithoutValue() {
            return criteriaWithoutValue;
        }

        public List<Map<String, Object>> getCriteriaWithSingleValue() {
            return criteriaWithSingleValue;
        }

        public List<Map<String, Object>> getCriteriaWithListValue() {
            return criteriaWithListValue;
        }

        public List<Map<String, Object>> getCriteriaWithBetweenValue() {
            return criteriaWithBetweenValue;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteriaWithoutValue.add(condition);
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("condition", condition);
            map.put("val", value);
            criteriaWithSingleValue.add(map);
        }

        protected void addCriterion(String condition, List<? extends Object> values, String property) {
            if (values == null || values.size() == 0) {
                throw new RuntimeException("Value list for " + property + " cannot be null or empty");
            }
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("condition", condition);
            map.put("val", values);
            criteriaWithListValue.add(map);
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            List<Object> list = new ArrayList<Object>();
            list.add(value1);
            list.add(value2);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("condition", condition);
            map.put("val", list);
            criteriaWithBetweenValue.add(map);
        }
        
        public Criteria add(String field,ConditionEnum condition,Object value){
    		addCriterion(field+condition.get(), value, field);
    		return this;
    	}
        
        public Criteria add(String field,IsBTEnum isBTEnum ,Object value1,Object value2){
    		addCriterion(field+isBTEnum.get(), value1, value2, field);
    		return this;
    	}
        
        public Criteria add(String field,IsInEnum isInEnum,List<Object> list){
        	addCriterion(field+isInEnum.get(), list, field);
        	return this;
        }
        
        public Criteria add(String field,IsInEnum isInEnum,Object[] objects){
        	List<Object> list = new ArrayList<Object>();
        	for (Object object : objects) {
				list.add(object);
			}
        	addCriterion(field+isInEnum.get(), list, field);
        	return this;
        }
        
        public Criteria add(String field,IsNullEnum isNull){
    		addCriterion(field+isNull.get());
    		return this;
    	}
        
        public Criteria addConnCondition(String left,ConditionEnum con,String right){
        	add(left+con.get()+right);
    		return this;
    	}
        
        public Criteria add(String connectCondition){
    		addCriterion(connectCondition);
    		return this;
    	}
    }
    
}
