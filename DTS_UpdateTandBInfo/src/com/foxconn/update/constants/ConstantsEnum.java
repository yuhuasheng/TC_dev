package com.foxconn.update.constants;
/**
* @author infodba
* @version 创建时间：2021年12月21日 下午9:03:32
* @Description: 常用枚举类
*/
public enum ConstantsEnum {	

	EDAPLACEMENTFOLDER("", "Placement"), DIFFERNAME("", "_differ_txt");
	
	private final String key;
	private final String value;
    private ConstantsEnum(String key, String value) {
    	this.key = key;
        this.value = value;
    }

    public String key() {
    	return key;
	}
    
    public String value() {
        return this.value;
    }
}
