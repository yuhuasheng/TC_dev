package com.foxconn.plm.tcapi.utils.bom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.foxconn.plm.tcapi.constants.BOMLinePropConstant;
import com.foxconn.plm.tcapi.utils.CommonTools;
import com.foxconn.plm.tcapi.utils.TCPublicUtils;
import com.sun.tools.xjc.Plugin;
import com.teamcenter.services.strong.cad.StructureManagementService;
import com.teamcenter.services.strong.core.DataManagementService;
import com.teamcenter.services.strong.structuremanagement.StructureService;
import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.client.model.ServiceData;
import com.teamcenter.soa.client.model.strong.BOMLine;
import com.teamcenter.soa.client.model.strong.BOMWindow;
import com.teamcenter.soa.client.model.strong.ItemRevision;
import com.teamcenter.soa.exceptions.NotLoadedException;

public class BOMModifier {

	private static final Logger log = LoggerFactory.getLogger(BOMModifier.class);
	
	protected BOMModifier() {

	}

	/**
	 * 修改BOMLine 属性字段值
	 * 
	 * @param structureManagementService BOM结构服务类
	 * @param dataManagementService      工具类
	 * @param itemRevision               对象版本
	 * @param map                        存放更改属性名和值
	 * @return
	 */
	public List<String> modifyBOMLineInfo(StructureManagementService structureManagementService, StructureService strucService,
			DataManagementService dataManagementService, ItemRevision itemRevision, Map<String, List<String>> map, String ref, int level) {
		TCPublicUtils.refreshObject(dataManagementService, itemRevision);
		// BOMWindow窗口
		BOMWindow[] bomWindows = null;
		List<BOMLine> list = new ArrayList<>();
		try {
			// Open BOMWindow
			List createBOMWindowsResponse = BOMFacade.commonOperate.openBOMWindow(structureManagementService,
					dataManagementService, itemRevision);
			if (CommonTools.isEmpty(createBOMWindowsResponse)) {
				log.info("【ERROR】 打开BOMWindow失败！");
				return null;
			}			
			// BOMWindow窗口
			bomWindows = new BOMWindow[] { (BOMWindow) createBOMWindowsResponse.get(0) };
			dataManagementService.refreshObjects(bomWindows);

			// 顶层BOMLine
			BOMLine topLine = (BOMLine) createBOMWindowsResponse.get(1);
			TCPublicUtils.refreshObject(dataManagementService, topLine);
			boolean bIsBomLinePacked = TCPublicUtils.getPropBoolean(dataManagementService, topLine, BOMLinePropConstant.BL_IS_PACKED);
			if (bIsBomLinePacked) { // 判断是否为打包状态
				System.out.println("BOM Line is packed, unpacking the complete BOMLine");
				ServiceData response = strucService.packOrUnpack(new BOMLine[] { topLine }, level);
				if (response.sizeOfPartialErrors() == 0) {
					System.out.println("unpacking successfull ");
				}
			}
			list.add(topLine);
			ModelObject[] children = TCPublicUtils.getPropModelObjectArray(dataManagementService, topLine,
					BOMLinePropConstant.BL_ALL_CHILD_LINES);
			if (CommonTools.isEmpty(children)) {
				log.info("【WARN】 不存在子BOMLine, 无需进行下序操作！");
				throw new Exception("【WARN】 不存在子BOMLine, 无需进行下序操作！");
			} else {
				// 遍历BOMLine结构树，获取子BOMLine对应的对象版本
				BOMFacade.commonOperate.getChildBom(strucService, dataManagementService, topLine, level, list, null, null);
			}
			// 更新BOM字段信息
			return updateBOMLinePropsNew2(dataManagementService, list, map, ref);
		} catch (Exception e) {
			e.printStackTrace();
			log.info(CommonTools.getExceptionMsg(e));
			return null;
		} finally {
			// 关闭BOMWindow
			if (bomWindows != null) {
				// 保存BOMWindow
				BOMFacade.commonOperate.saveBOMWindow(structureManagementService, bomWindows[0]);
				// 关闭BOMWindow
				BOMFacade.commonOperate.closeBOMWindow(structureManagementService, bomWindows[0]);
			}
		}
	}

	/**
	 * 更新BOM字段信息
	 * 
	 * @param dataManagementService 工具类
	 * @param list                  BOM集合
	 * @param map                   更新属性和属性值集合
	 * @return
	 * @throws NotLoadedException
	 */
	private List<String> updateBOMLinePropsNew(DataManagementService dataManagementService, List<BOMLine> list,
			Map<String, List<String>> map) throws NotLoadedException {
		List<String> errorMsgList = new ArrayList<String>();
		for (BOMLine bomLine : list) {
			TCPublicUtils.refreshObject(dataManagementService, bomLine);
			String[] propertyNames = bomLine.getPropertyNames(); // 获取BOMLine所有属性
			Iterator<Map.Entry<String, List<String>>> it = map.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, List<String>> entry = it.next();
				String key = entry.getKey();
				System.out.println("==>> key: " + key);
				if (CommonTools.isEmpty(key)) { // 判断匹配的属性值是否为空
					continue;
				}
				List<String> value = entry.getValue();
				boolean found = false;
				String propName = null;
				for (String prop : propertyNames) {
					if (prop.equals(key.split("=")[0])) {
						found = true;
						propName = prop;
						break;
					}
				}
				if (found) {
					// 获取属性名称和属性值
					List<Object> resultList = getPropsMap(value);
					String itemId = TCPublicUtils.getPropStr(dataManagementService, bomLine,
							BOMLinePropConstant.BL_ITEM_ITEM_ID);
					String version = TCPublicUtils.getPropStr(dataManagementService, bomLine,
							BOMLinePropConstant.BL_REV_ITEM_REVISION_ID);
					System.out.println("==>> itemId: " + itemId);
					System.out.println("==>> version: " + version);
					TCPublicUtils.refreshObject(dataManagementService, bomLine);
					String location = TCPublicUtils.getPropStr(dataManagementService, bomLine, propName);
					System.out.println("==>> location: " + location);
					if (CommonTools.isEmpty(location)) { // location为空直接跳出循环
						break;
					}
					if (!location.equals(key.split("=")[1])) {
						continue;
					}
					if (TCPublicUtils.setProperties(dataManagementService, bomLine, (String[]) resultList.get(0),
							(String[][]) resultList.get(1))) { // 更改BOM的属性值
						// 更新完属性之后, 刷新一下BOMLine
						TCPublicUtils.refreshObject(dataManagementService, bomLine);
						// 判断属性是否更新成功
						checkBOMProps(dataManagementService, bomLine, (String[]) resultList.get(0),
								(String[][]) resultList.get(1), errorMsgList, itemId, version);
						it.remove(); // 移除当前已经处理完的记录
						break;
					}
				}
			}

		}
		return errorMsgList;
	}

	private List<String> updateBOMLinePropsNew2(DataManagementService dataManagementService, List<BOMLine> list,
			Map<String, List<String>> map, String ref) {
		List<String> errorMsgList = new ArrayList<String>();
		list.parallelStream().forEach(bomLine -> {
			try {
				TCPublicUtils.refreshObject(dataManagementService, bomLine);
				String location = TCPublicUtils.getPropStr(dataManagementService, bomLine, BOMLinePropConstant.BL_REF_DESIGNATOR);
				if (CommonTools.isNotEmpty(location)) {
                  String itemId = TCPublicUtils.getPropStr(dataManagementService, bomLine, BOMLinePropConstant.BL_ITEM_ITEM_ID);
                  System.out.println("==>> itemID: " + itemId);
                  String version = TCPublicUtils.getPropStr(dataManagementService, bomLine, BOMLinePropConstant.BL_REV_ITEM_REVISION_ID);
                  System.out.println("==>> version: " + version);
					List<String> value = map.get(ref + location);
					if (CommonTools.isNotEmpty(value)) {						
                        List<Object> resultList = getPropsMap(value); // 获取属性名称和属性值
                        if (TCPublicUtils.setProperties(dataManagementService, bomLine, (String[]) resultList.get(0), (String[][]) resultList.get(1))) {
                        	map.remove(ref + location);
                        } else {
                        	String msg = " 图号为: " + itemId + ", 版本号为: " + version + ", 更新失败";
                        	 if (!errorMsgList.contains(msg)) {
                                 errorMsgList.add(msg);
                             }
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info(CommonTools.getExceptionMsg(e));
			}
		});
		return errorMsgList;
	}

	/**
	 * 更新BOM字段信息
	 * 
	 * @param dataManagementService 工具类
	 * @param list                  BOM集合
	 * @param map                   更新属性和属性值集合
	 * @return
	 * @throws NotLoadedException
	 */
	private List<String> updateBOMLineProps(DataManagementService dataManagementService, List<BOMLine> list,
			Map<String, List<String>> map) throws NotLoadedException {
		List<String> errorMsgList = new ArrayList<String>();
		for (BOMLine bomLine : list) {
			String[] propertyNames = bomLine.getPropertyNames(); // 获取BOMLine所有属性
			for (Map.Entry<String, List<String>> entry : map.entrySet()) {
				String key = entry.getKey();
				System.out.println("==>> key: " + key);
				if (CommonTools.isEmpty(key)) { // 判断匹配的属性值是否为空
					continue;
				}
				List<String> value = entry.getValue();
				boolean found = false;
				String propName = null;
				for (String prop : propertyNames) {
					if (prop.equals(key.split("=")[0])) {
						found = true;
						propName = prop;
						break;
					}
				}
				if (found) {
					// 获取属性名称和属性值
					List<Object> resultList = getPropsMap(value);
					String itemId = TCPublicUtils.getPropStr(dataManagementService, bomLine,
							BOMLinePropConstant.BL_ITEM_ITEM_ID);
					String version = TCPublicUtils.getPropStr(dataManagementService, bomLine,
							BOMLinePropConstant.BL_REV_ITEM_REVISION_ID);
					System.out.println("==>> itemId: " + itemId);
					System.out.println("==>> version: " + version);
					TCPublicUtils.refreshObject(dataManagementService, bomLine);
					String location = TCPublicUtils.getPropStr(dataManagementService, bomLine, propName);
					System.out.println("==>> location: " + location);
					if (CommonTools.isEmpty(location)) { // location为空直接跳出循环
						break;
					}
					if (!location.equals(key.split("=")[1])) {
						continue;
					}
					if (TCPublicUtils.setProperties(dataManagementService, bomLine, (String[]) resultList.get(0),
							(String[][]) resultList.get(1))) { // 更改BOM的属性值
						// 更新完属性之后, 刷新一下BOMLine
						TCPublicUtils.refreshObject(dataManagementService, bomLine);
						// 判断属性是否更新成功
						checkBOMProps(dataManagementService, bomLine, (String[]) resultList.get(0),
								(String[][]) resultList.get(1), errorMsgList, itemId, version);
						break;
					}
				}
			}
		}
		return errorMsgList;
	}

	/**
	 * 判断属性是否更新成功
	 * 
	 * @param dataManagementService 工具类
	 * @param bomLine
	 * @param strs                  属性名数组
	 * @return
	 * @throws NotLoadedException
	 */
	private void checkBOMProps(DataManagementService dataManagementService, BOMLine bomLine, String[] properties,
			String[][] propertyvalue, List<String> msgList, String itemId, String version) throws NotLoadedException {
		for (int i = 0; i < properties.length; i++) {
			String msg = "";
			String propsName = properties[i];
			TCPublicUtils.refreshObject(dataManagementService, bomLine);
			String value = TCPublicUtils.getPropStr(dataManagementService, bomLine, propsName);
			if (value.equals(propertyvalue[i][0])) { // 判断BOMLine的属性值是否更新成功
				msg = " 图号为: " + itemId + ", 版本号为: " + version + ", 属性字段" + propsName + "更新成功";
				System.out.println(msg);
			} else {
				msg = " 图号为: " + itemId + ", 版本号为: " + version + ", 属性字段" + propsName + "更新失败";
				if (!msgList.contains(msg)) {
					msgList.add(msg);
				}
			}
		}

	}

	/**
	 * 获取属性名称和属性值
	 * 
	 * @param list
	 * @return
	 */
	private List<Object> getPropsMap(List<String> list) {
		List<Object> resultList = new ArrayList<Object>();
		String[] properties = new String[list.size()];
		String[][] propertyvalue = new String[list.size()][1];
		for (int i = 0; i < list.size(); i++) {
			String propsName = list.get(i).split("=")[0];
			String propsValue = list.get(i).split("=")[1];
			// 判断属性名称和属性值是否都为空
			if (CommonTools.isEmpty(propsName) || CommonTools.isEmpty(propsValue)) {
				continue;
			}
			properties[i] = propsName;
			propertyvalue[i][0] = propsValue;
		}
		resultList.add(properties);
		resultList.add(propertyvalue);
		return resultList;
	}

	/**
	 * 
	 * @param strucService          工具类
	 * @param dataManagementService
	 * @param list                  bomline集合
	 * @param level                 标志 0：打包行 1：解包行 2：打包所有行 3：解包所有行
	 * @throws NotLoadedException
	 */
	public boolean packOrUnpack(StructureService strucService, DataManagementService dataManagementService,
			List<BOMLine> list, int level) throws NotLoadedException {
		boolean flag = true;
		for (BOMLine bomLine : list) {
			dataManagementService.refreshObjects(new ModelObject[] { bomLine });
			dataManagementService.getProperties(new ModelObject[] { bomLine },
					new String[] { BOMLinePropConstant.BL_IS_PACKED });
			boolean bIsBomLinePacked = TCPublicUtils.getPropBoolean(dataManagementService, bomLine, BOMLinePropConstant.BL_IS_PACKED);
			if (bIsBomLinePacked) { // 判断BOMLine是否为打包状态
				System.out.println("BOM Line is packed, unpacking the complete BOMLine");
				ServiceData response = strucService.packOrUnpack(new BOMLine[] { bomLine }, level);
				if (response.sizeOfPartialErrors() == 0) {
					System.out.println("unpacking successfull ");
				} else {
					flag = false;
					break;
				}
			}
		}
		return flag;
	}

}
