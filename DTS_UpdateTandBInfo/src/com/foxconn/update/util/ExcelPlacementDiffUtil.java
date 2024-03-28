package com.foxconn.update.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.foxconn.plm.tcapi.constants.BOMLinePropConstant;
import com.foxconn.plm.tcapi.constants.ItemRevPropConstant;
import com.foxconn.plm.tcapi.mail.TCMail;
import com.foxconn.plm.tcapi.utils.CommonTools;
import com.foxconn.plm.tcapi.utils.TCPublicUtils;
import com.teamcenter.services.strong.core.DataManagementService;
import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.client.model.strong.BOMLine;
import com.teamcenter.soa.client.model.strong.ItemRevision;

public class ExcelPlacementDiffUtil {
	
	public static String diffWithItemRevision(DataManagementService dataManagementService, List<String> propertyList,
			Map<String, List<String>> map, String dir, ItemRevision itemRevision) {
		try {
			List<String> refList = getRefList(map); // 获取refdes集合
			List<String> diffList = new ArrayList<>();
			boolean found;
			for (String refId : propertyList) {
//				TCPublicUtils.refreshObject(dataManagementService, bomLine);				
//				String refId = TCPublicUtils.getPropStr(dataManagementService, bomLine, BOMLinePropConstant.BL_REF_DESIGNATOR);
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
					diffList.add(refId); // 差异
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
