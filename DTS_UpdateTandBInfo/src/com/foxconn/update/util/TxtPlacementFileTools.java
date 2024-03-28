package com.foxconn.update.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.foxconn.plm.tcapi.utils.CommonTools;
import com.foxconn.update.constants.PlacementEnum;
import com.foxconn.update.constants.PlacementHeadEnum;
import com.foxconn.update.constants.SideEnum;

/**
 * @author infodba
 * @version 创建时间：2021年12月22日 下午2:50:57
 * @Description 工具类
 */
public class TxtPlacementFileTools {

	public static void main(String[] args) {
//		Map<String, List<String>> map = analysePlacementTxt("C:\\place_txt.txt");
		System.out.println(CommonTools.getNowTime2());
	}
	
	/**
	 * 解析Placement文件
	 * 
	 * @param filePath 文件的绝对路径
	 * @return 坐标和正面反信息的集合
	 */
	public static Map<String, List<String>> analysePlacementTxt(String filePath) {
		Map<String, List<String>> map = new LinkedHashMap<String, List<String>>();
		InputStreamReader read = null;
		BufferedReader bufferedReader = null;
		try {
			System.out.println("******** 开始解析placement文件 ********");
			long timeStart = System.currentTimeMillis();
			File file = new File(filePath);// 文件路径
			if (file.isFile() && file.exists()) {
				read = new InputStreamReader(new FileInputStream(file));
				bufferedReader = new BufferedReader(read);
				String line = null;
				List<String> titleList = null;
				boolean startCapture = false;
				while ((line = bufferedReader.readLine()) != null) {
					System.out.println("【INFO】 " + line);
					if (startCapture) {	
						String[] propsValue = line.split(PlacementHeadEnum.separator.value());
						if ((!line.contains(PlacementHeadEnum.separator.value()) || (propsValue.length <= 1))) {
							continue;
						}
						String key = "";
						String valueX = "";
						String valueY = "";
						String valueAngle = "";
						String valueSide = "";
						String valueSide2 = "";
						List<String> valuelist = new ArrayList<String>();						
						for (int j = 0; j < propsValue.length; j++) {
							String value = propsValue[j].trim();
							if (j == Integer.parseInt(titleList.get(j).split("=")[1])) {
								if (titleList.get(j).contains(PlacementEnum.BL_REF_DESIGNATOR.key())) {
									key = PlacementEnum.BL_REF_DESIGNATOR.value() + "=" + value;
								} else if (titleList.get(j).contains(PlacementEnum.BL_OCC_D9_X_COORDINATE.key())) {
									valueX = PlacementEnum.BL_OCC_D9_X_COORDINATE.value() + "=" + value;
								} else if (titleList.get(j).contains(PlacementEnum.BL_OCC_D9_Y_COORDINATE.key())) {
									valueY = PlacementEnum.BL_OCC_D9_Y_COORDINATE.value() + "=" + value;
								} else if (titleList.get(j).contains(PlacementEnum.BL_OCC_D9_ANGLE.key())) {
									valueAngle = PlacementEnum.BL_OCC_D9_ANGLE.value() + "=" + value;
								} else if (titleList.get(j).contains(PlacementEnum.BL_OCC_D9_SIDE.key())) {
									if (value.equals(SideEnum.BACK.key())) {
										valueSide = PlacementEnum.BL_OCC_D9_SIDE.value() + "=" + SideEnum.BACK.value();
										valueSide2 = PlacementEnum.D9_SIDE.value() + "=" + SideEnum.BACK.value();
									} else {
										valueSide = PlacementEnum.BL_OCC_D9_SIDE.value() + "=" + SideEnum.FRONT.value();
										valueSide2 = PlacementEnum.D9_SIDE.value() + "=" + SideEnum.FRONT.value();								}
								}
							}
						}
						if (valueX.split("=").length > 1) { // =后面没有值则无需添加
							valuelist.add(valueX);
						}
						if (valueY.split("=").length > 1) { // =后面没有值则无需添加
							valuelist.add(valueY);
						}
						if (valueAngle.split("=").length > 1) { // =后面没有值则无需添加
							valuelist.add(valueAngle);
						}						
						valuelist.add(valueSide);
						valuelist.add(valueSide2);
						map.put(key, valuelist);					
					} else if (startCapture = (line.contains(PlacementEnum.BL_REF_DESIGNATOR.key()))) {
						// 获取标题和索引号
						titleList = getTitleIndex(line);
						if (CommonTools.isEmpty(titleList)) {
							return null;
						}
					}
				}
				System.out.println("******** 解析placement文件结束 ********");
				long timeEnd = System.currentTimeMillis();
				System.out.println("总共花费：" + (timeEnd - timeStart) + "ms");
			} else {
				System.out.println("找不到指定的文件");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("读取文件内容出错");
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (read != null) {
				try {
					read.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}			
		}
		return map;
	}

	/**
	 * 获取标题和索引号
	 * 
	 * @param str
	 * @return
	 */
	public static List<String> getTitleIndex(String str) {
		str = CommonTools.removeSpecialCharacters(str).replace("#", "");
		List<String> list = new ArrayList<String>();
		String[] propsName = str.split(PlacementHeadEnum.separator.value());
		for (int i = 0; i < propsName.length; i++) {
			list.add(propsName[i] + "=" + i);
		}
		return list;
	}
}
