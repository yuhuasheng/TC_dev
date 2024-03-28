package com.foxconn.update.constants;
/**
* @author infodba
* @version 创建时间：2021年12月22日 下午2:43:03
* @Description Placement文件枚举类
*/
public enum PlacementEnum {

	BL_REF_DESIGNATOR("refdes", "bl_ref_designator"), BL_OCC_D9_X_COORDINATE("symbol_x", "bl_occ_d9_X_Coordinate"), BL_OCC_D9_Y_COORDINATE("symbol_y", "bl_occ_d9_Y_Coordinate"), 
	BL_OCC_D9_ANGLE("rotation", "bl_occ_d9_Angle"), BL_OCC_D9_SIDE("mirror", "bl_occ_d9_Side"), D9_SIDE("", "D9_Side");
	
	private final String key;
	private final String value;
    private PlacementEnum(String key, String value) {
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
