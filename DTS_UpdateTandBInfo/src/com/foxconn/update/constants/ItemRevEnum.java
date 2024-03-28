package com.foxconn.update.constants;

/**
 * @author infodba
 * @version 创建时间：2022年1月24日 下午4:46:07
 * @Description
 */
public enum ItemRevEnum {

	EE_SCHEMREVISION("D9_EE_SchemRevision");

	private final String type;

	private ItemRevEnum(String type) {
		this.type = type;
	}

	public String type() {
		return type;
	}
}
