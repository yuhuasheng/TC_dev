package com.foxconn.update.util;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.foxconn.plm.tcapi.constants.BOMLinePropConstant;
import com.foxconn.plm.tcapi.constants.ItemRevPropConstant;
import com.foxconn.plm.tcapi.mail.TCMail;
import com.foxconn.plm.tcapi.utils.CommonTools;
import com.foxconn.plm.tcapi.utils.TCPublicUtils;
import com.foxconn.update.constants.PlacementEnum;
import com.foxconn.update.constants.SideEnum;
import com.teamcenter.services.strong.core.DataManagementService;
import com.teamcenter.soa.client.model.strong.BOMLine;
import com.teamcenter.soa.client.model.strong.ItemRevision;

public class ExcelPlacementFileTools {

	public static void main(String[] args) {
		Map<String, List<String>> map = analysePlacementExcel(
				"C:\\Users\\HuashengYu\\Desktop\\492A00CB1300H RA Location  20210203-test.xlsx");
		System.out.println(map);
	}

	/**
	 * 解析Excel Placement文件
	 * 
	 * @param excelFilePath
	 * @return
	 */
	public static Map<String, List<String>> analysePlacementExcel(String excelFilePath) {
		Map<String, List<String>> map = new LinkedHashMap<String, List<String>>();
		try {
			System.out.println("******** 开始解析placement文件 ********");
			long timeStart = System.currentTimeMillis();
			FileInputStream fis = new FileInputStream(excelFilePath);
			// 获取整个Excel
			Workbook workbook = WorkbookFactory.create(fis);
			// 获取第一个表单sheet
			Sheet sheet = workbook.getSheetAt(0);
			int count = sheet.getLastRowNum(); // 获取最后一行
			for (int i = 1; i <= count; i++) {
				if (CommonTools.isEmpty(sheet.getRow(i))) {
					continue;
				}
				if (CommonTools.isEmpty(sheet.getRow(i).getCell(0))) {
					continue;
				}
				String key = "";
				String valueX = "";
				String valueY = "";
				String valueAngle = "";
				String valueSide = "";
				String valueSide2 = "";
				List<String> valuelist = new ArrayList<String>();
				Row row = sheet.getRow(i);
				short cellNum = row.getLastCellNum();
				for (short j = 0; j < cellNum; j++) {
					Cell cell = row.getCell(j);
					String value = removeBlank(getCellValue(cell)); // 获取单元格数值
					if (j == 1) {
						key = PlacementEnum.BL_REF_DESIGNATOR.value() + "=" + value;
					} else if (j == 6) {
						valueX = PlacementEnum.BL_OCC_D9_X_COORDINATE.value() + "=" + value;
					} else if (j == 7) {
						valueY = PlacementEnum.BL_OCC_D9_Y_COORDINATE.value() + "=" + value;
					} else if (j == 5) {
						valueAngle = PlacementEnum.BL_OCC_D9_ANGLE.value() + "=" + value;
					} else if (j == 4) {
						if (value.toUpperCase().equals(SideEnum.FRONT.value()) || CommonTools.isEmpty(value)) {							
							valueSide = PlacementEnum.BL_OCC_D9_SIDE.value() + "=" + SideEnum.FRONT.value();
							valueSide2 = PlacementEnum.D9_SIDE.value() + "=" + SideEnum.FRONT.value();
						} else if (value.toUpperCase().equals(SideEnum.BACK.value())) {
							valueSide = PlacementEnum.BL_OCC_D9_SIDE.value() + "=" + SideEnum.BACK.value();
							valueSide2 = PlacementEnum.D9_SIDE.value() + "=" + SideEnum.BACK.value();
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
			}
			System.out.println("******** 解析placement文件结束 ********");
			long timeEnd = System.currentTimeMillis();
			System.out.println("总共花费：" + (timeEnd - timeStart) + "ms");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("读取文件内容出错");
		}
		return map;
	}
	

	/**
	 * 获取单元格内容
	 * 
	 * @param cell
	 * @return
	 */
	public static String getCellValue(Cell cell) {
		String value = "";
		if (null == cell) {
			return value;
		}
		switch (cell.getCellType().name()) {
		case "STRING":
			value = cell.getRichStringCellValue().getString();
			break;
		case "NUMERIC":
			value = cell.getNumericCellValue() + "";
			break;
		case "BOOLEAN":
			value = String.valueOf(cell.getBooleanCellValue());
			break;
		case "BLANK":
			value = null;
			break;
		case "ERROR":
			value = null;
			break;
		case "FORMULA":
			value = cell.getCellFormula() + "";
			break;
		default:
			value = cell.toString();
			break;
		}
		return value;
	}

	/**
	 * 去掉字符串中的前后空格、回车、换行符、制表符 value
	 *
	 * @return
	 */
	public static String removeBlank(String value) {
		String result = "";
		if (value != null) {
			Pattern p = Pattern.compile("|\t|\r|\n");
			Matcher m = p.matcher(value);
			result = m.replaceAll("");
			result = result.trim();
		} else {
			result = value;
		}
		return result;
	}

}
