package com.foxconn.plm.tcapi.constants;

/**
* @author infodba
* @version 创建时间：2021年12月23日 下午7:28:43
* @Description TC查询条件枚举类
*/
public enum TCSearchEnum {
	
	__WEB_FIND_USER("__WEB_find_user", new String[] {"User ID"});
	
	private final String queryName; // 查询名称
	private final String[] queryParams; // 查询参数名
    private TCSearchEnum(String queryName, String[] queryParams) {
    	this.queryName = queryName;
        this.queryParams = queryParams;
    }

    public String queryName() {
    	return queryName;
	}
    
    public String[] queryParams() {
        return queryParams;
    }
}
