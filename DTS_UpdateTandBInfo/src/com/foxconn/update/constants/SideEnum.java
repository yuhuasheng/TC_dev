package com.foxconn.update.constants;

/**
 * @author infodba
 * @version 创建时间：2021年12月22日 下午4:24:11
 * @Description
 */
public enum SideEnum {

	FRONT("", "TOP"), BACK("m", "BUTTOM");

	private final String key;
	private final String value;

	private SideEnum(String key, String value) {
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
