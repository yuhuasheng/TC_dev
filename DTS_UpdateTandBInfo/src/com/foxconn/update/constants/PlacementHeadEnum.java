package com.foxconn.update.constants;
/**
* @author infodba
* @version 创建时间：2021年12月22日 下午3:04:09
* @Description
*/
public enum PlacementHeadEnum {
	
	separator("!"), HORIZONTALLINE("#--"); 
	
	private final String value;
    private PlacementHeadEnum(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
