package com.foxconn.plm.tcapi.service;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.foxconn.plm.tcapi.constants.BOMLinePropConstant;
import com.foxconn.plm.tcapi.utils.TCPublicUtils;
import com.foxconn.plm.tcapi.utils.bom.BOMFacade;
import com.teamcenter.services.strong.cad.StructureManagementService;
import com.teamcenter.services.strong.core.DataManagementService;
import com.teamcenter.services.strong.structuremanagement.StructureService;
import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.client.model.strong.BOMLine;
import com.teamcenter.soa.client.model.strong.ItemRevision;
import com.teamcenter.soa.exceptions.NotLoadedException;

/**
* @author infodba
* @version 创建时间：2021年12月24日 下午4:08:23
* @Description
*/
public class BOMService {
	
	
	/**
	 * 获取BOMLine集合
	 * @param structureManagementService
	 * @param dataManagementService
	 * @param itemRevision
	 * @return
	 */
	public static boolean getBOMWindowInfo(StructureManagementService structureManagementService, StructureService strucService,
			DataManagementService dataManagementService, ItemRevision itemRevision, int level, List<ModelObject> itemRevList, List<String> propertyList) {
		return BOMFacade.searchService.getBOMWindowInfo(structureManagementService, strucService, dataManagementService, itemRevision, level, itemRevList, propertyList);
	}
	
	/**
	 * 修改BOMLine 属性字段值
	 * @param structureManagementService
	 * @param dataManagementService
	 * @param itemRevision
	 * @param map
	 * @param ref 作为判断更新哪一行BOMLine属性的标识符
	 * @return
	 */
	public static List<String> modifyBOMLineInfo(StructureManagementService structureManagementService, StructureService strucService,
			DataManagementService dataManagementService, ItemRevision itemRevision, Map<String, List<String>> map, String ref, int level) {
		return BOMFacade.modifyService.modifyBOMLineInfo(structureManagementService, strucService, dataManagementService, itemRevision, map, ref, level);
	}
	
	/**
	 * 加包或者解包
	 * @param strucService
	 * @param dataManagementService
	 * @param list
	 * @param level
	 * @return
	 * @throws NotLoadedException
	 */
	public static boolean packOrUnpack(StructureService strucService, DataManagementService dataManagementService, List<BOMLine> list, int level) throws NotLoadedException {
		return BOMFacade.modifyService.packOrUnpack(strucService, dataManagementService, list, level);
	}
	
	/**
	 * 将BOMLine的对象版本返回
	 *
	 * @param dataManagementService 工具类
	 * @param list                  集合
	 * @return
	 * @throws NotLoadedException
	 */
	public static List<ModelObject> conversionItemRev(DataManagementService dataManagementService, List<BOMLine> list) throws NotLoadedException {		
		return BOMFacade.searchService.conversionItemRev(dataManagementService, list);
	}
}
