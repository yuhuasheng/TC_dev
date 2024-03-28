package com.foxconn.update.util;

import com.foxconn.plm.tcapi.constants.BOMLinePropConstant;
import com.foxconn.plm.tcapi.constants.ItemRevPropConstant;
import com.foxconn.plm.tcapi.mail.TCMail;
import com.foxconn.plm.tcapi.service.BOMService;
import com.foxconn.plm.tcapi.service.TCSOAServiceFactory;
import com.foxconn.plm.tcapi.utils.CommonTools;
import com.foxconn.plm.tcapi.utils.TCPublicUtils;
import com.foxconn.plm.tcapi.utils.bom.BOMFacade;
import com.foxconn.plm.tcapi.utils.bom.BOMSearcher;
import com.foxconn.update.constants.PlacementHeadEnum;
import com.teamcenter.services.strong.cad.StructureManagementService;
import com.teamcenter.services.strong.core.DataManagementService;
import com.teamcenter.soa.client.model.strong.BOMLine;
import com.teamcenter.soa.client.model.strong.ItemRevision;
import com.teamcenter.soa.exceptions.NotLoadedException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Placement和Design-BOM差异对比工具类
 * 
 * @author Administrator
 *
 */
public class TxtPlacementDiffUtil {

	public static String diffWithItemRevision(DataManagementService dataManagementService, List<String> propertyList,
			Map<String, List<String>> map, String dir, ItemRevision itemRevision) {
		try {
			// 读取placement文件
//			List<String> refList = loadTxt(placementPath);
			List<String> refList = getRefList(map); // 获取refdes集合
			List<String> diffList = new ArrayList<>();
			boolean found;
			for (String refId : propertyList) {
//				TCPublicUtils.refreshObject(dataManagementService, bomLine);
//				String refId = TCPublicUtils.getPropStr(dataManagementService, bomLine,BOMLinePropConstant.BL_REF_DESIGNATOR);
				if (CommonTools.isEmpty(refId)) {
					continue;
				}
				found = false;
				for (String refdes : refList) {
					if (refdes.equals(refId)) {
						found = true;
						break;
					}
				}
				if (!found) {
					// 差异
					diffList.add(refId);
				}
			}

			if (CommonTools.isEmpty(diffList)) {
				return null;
			}
			TCPublicUtils.refreshObject(dataManagementService, itemRevision);
			String itemId = TCPublicUtils.getPropStr(dataManagementService, itemRevision, ItemRevPropConstant.ITEM_ID);
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("HH.mm");
			String fileName = itemId + "_" + sdf.format(date);
			String filePath = TCMail.generateTxtFile(dir, fileName);
			List<String> contenList = new ArrayList<String>();
			contenList.add("#   序号   !   子集.BOM&Design-BOM中位号");
			contenList.add("#-------------------------------------");
			for (int i = 0; i < diffList.size(); i++) {
				String no = i + 1 + "";
				contenList.add(no + makeBlank(11 - no.length()) + "!" + "   " + diffList.get(i));
			}
			TCMail.recordContent(filePath, contenList);
			return filePath;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(CommonTools.getExceptionMsg(e));
			return null;
		}
	}

	private static String makeBlank(int n) {
		String blank = "";
		for (int i = 0; i < n; i++) {
			blank += " ";
		}
		return blank;
	}

	private static List<String> loadTxt(String path) throws Exception {
		List<String> list = new ArrayList<>();
		BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
		String row = bufferedReader.readLine();
		boolean startCapture = false;
		while (row != null) {
			if (startCapture) {
				String[] split = row.split(PlacementHeadEnum.separator.value());
				list.add(split[0].trim());
			} else {
				startCapture = row.startsWith(PlacementHeadEnum.HORIZONTALLINE.value());
			}
			row = bufferedReader.readLine();
		}
		bufferedReader.close();
		return list;
	}

	/**
	 * 获取refdes集合
	 * 
	 * @param map
	 * @return
	 */
	private static List<String> getRefList(Map<String, List<String>> map) {
		List<String> list = new ArrayList<String>();
		map.forEach((key, value) -> {
			String ref = key.split("=")[1];
			list.add(ref);
		});
		return list;
	}
}
