package com.foxconn.plm.tcapi.utils.bom;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.foxconn.plm.tcapi.constants.BOMLinePropConstant;
import com.foxconn.plm.tcapi.utils.CommonTools;
import com.foxconn.plm.tcapi.utils.TCPublicUtils;
import com.teamcenter.dc.util.gensrc.ItemRevStmpType;
import com.teamcenter.services.strong.cad.StructureManagementService;
import com.teamcenter.services.strong.cad._2007_01.StructureManagement.CloseBOMWindowsResponse;
import com.teamcenter.services.strong.cad._2007_01.StructureManagement.CreateBOMWindowsInfo;
import com.teamcenter.services.strong.cad._2007_01.StructureManagement.CreateBOMWindowsOutput;
import com.teamcenter.services.strong.cad._2007_01.StructureManagement.CreateBOMWindowsResponse;
import com.teamcenter.services.strong.cad._2008_06.StructureManagement.SaveBOMWindowsResponse;
import com.teamcenter.services.strong.core.DataManagementService;
import com.teamcenter.services.strong.structuremanagement.StructureService;
import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.client.model.ServiceData;
import com.teamcenter.soa.client.model.strong.BOMLine;
import com.teamcenter.soa.client.model.strong.BOMWindow;
import com.teamcenter.soa.client.model.strong.ItemRevision;
import com.teamcenter.soa.client.model.strong.PSBOMViewRevision;
import com.teamcenter.soa.exceptions.NotLoadedException;

/**
 * @author infodba
 * @version 创建时间：2021年12月20日 下午4:46:56
 * @Description
 */
public class BOMCommonOperate {

	private static final Logger log = LoggerFactory.getLogger(BOMCommonOperate.class);
	
	protected BOMCommonOperate() {

	}

	/**
	 * 遍历BOMLine结构树，获取子BOMLine
	 *
	 * @param dataManagementService 工具类
	 * @param topLine               顶层BOMLie
	 * @param list
	 * @throws NotLoadedException
	 */
	public void getChildBom(StructureService strucService, DataManagementService dataManagementService, BOMLine topLine, 
			int level, List<BOMLine> list, List<ModelObject> itemRevList, List<String> propertyList)
			throws NotLoadedException {
		TCPublicUtils.refreshObject(dataManagementService, topLine);
		ModelObject[] children = TCPublicUtils.getPropModelObjectArray(dataManagementService, topLine, BOMLinePropConstant.BL_ALL_CHILD_LINES);	
		if (CommonTools.isEmpty(children)) { // 判断数组是否为空
			return;
		}
		for (ModelObject obj : children) {
			BOMLine childBomLine = (BOMLine) obj;			
			TCPublicUtils.refreshObject(dataManagementService, childBomLine);
			if (itemRevList != null) {
				ModelObject modelObject = TCPublicUtils.getPropModelObject(dataManagementService, childBomLine, BOMLinePropConstant.BL_LINES_OBJECT);
				itemRevList.add(modelObject);
			}
			
			if (propertyList != null) {
				String refId = TCPublicUtils.getPropStr(dataManagementService, childBomLine,BOMLinePropConstant.BL_REF_DESIGNATOR);
				if (CommonTools.isNotEmpty(refId)) {
					propertyList.add(refId);
				}
			}			
			boolean bIsBomLinePacked = TCPublicUtils.getPropBoolean(dataManagementService, childBomLine, BOMLinePropConstant.BL_IS_PACKED);
			if (bIsBomLinePacked) { // 判断是否为打包状态
				log.info("BOM Line is packed, unpacking the complete BOMLine");
				ServiceData response = strucService.packOrUnpack(new BOMLine[] { topLine }, level);
				if (response.sizeOfPartialErrors() == 0) {
					System.out.println("unpacking successfull ");
				}
			}
			ModelObject[] objects = null;
			try {
				objects = TCPublicUtils.getPropModelObjectArray(dataManagementService, childBomLine, BOMLinePropConstant.BL_ALL_CHILD_LINES);	
				if (list != null) {
					list.add(childBomLine);
				}				
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}			
			if (CommonTools.isNotEmpty(objects)) {
				getChildBom(strucService, dataManagementService, childBomLine, level, list, itemRevList, propertyList);
			}
		}
	}
	

	/**
	 * Open BOMWindow
	 *s
	 * @param connection   连接
	 * @param itemRevision 对象版本
	 * @return
	 */
	public List openBOMWindow(StructureManagementService structureManagementService, DataManagementService dataManagementService, ItemRevision itemRevision) {
		List bomWindowParentLine = new ArrayList(2);
		try {
			CreateBOMWindowsInfo[] createBOMWindowsInfo = new CreateBOMWindowsInfo[1];
			createBOMWindowsInfo[0] = new CreateBOMWindowsInfo();
			createBOMWindowsInfo[0].itemRev = itemRevision;
			createBOMWindowsInfo[0].clientId = "BOMUtils";
			createBOMWindowsInfo[0].item = itemRevision.get_items_tag();
//			createBOMWindowsInfo[0].bomView =
			CreateBOMWindowsResponse createBOMWindowsResponse = structureManagementService
					.createBOMWindows(createBOMWindowsInfo);			
			if (createBOMWindowsResponse.serviceData.sizeOfPartialErrors() > 0) {
				for (int i = 0; i < createBOMWindowsResponse.serviceData.sizeOfPartialErrors(); i++) {
					System.out.println("【ERROR】 Partial Error in Open BOMWindow = "
							+ createBOMWindowsResponse.serviceData.getPartialError(i).getMessages()[0]);
				}
				return null;
			}
			CreateBOMWindowsOutput[] output = createBOMWindowsResponse.output;
			if (null == output || output.length < 0) {
				return null;
			}
			// BOMWindow
			bomWindowParentLine.add(output[0].bomWindow);
			// TOPLine in BOMWindow
			bomWindowParentLine.add(output[0].bomLine);
			return bomWindowParentLine;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(CommonTools.getExceptionMsg(e));
		}
		return null;
	}

	
	/**
	 * Open BOMWindow
	 *
	 * @param connection   连接
	 * @param itemRevision 对象版本
	 * @return
	 */
	public List openBOMWindowNew(StructureManagementService structureManagementService, ItemRevision itemRevision, PSBOMViewRevision psbomViewRevision) {
		List bomWindowParentLine = new ArrayList(2);
		try {
			CreateBOMWindowsInfo[] createBOMWindowsInfo = new CreateBOMWindowsInfo[1];
			createBOMWindowsInfo[0] = new CreateBOMWindowsInfo();
//			createBOMWindowsInfo[0].itemRev = itemRevision;
			createBOMWindowsInfo[0].clientId = "BOMUtils";
//			createBOMWindowsInfo[0].item = itemRevision.get_items_tag();
//			createBOMWindowsInfo[0].bomView
			CreateBOMWindowsResponse createBOMWindowsResponse = structureManagementService
					.createBOMWindows(createBOMWindowsInfo);
			if (createBOMWindowsResponse.serviceData.sizeOfPartialErrors() > 0) {
				for (int i = 0; i < createBOMWindowsResponse.serviceData.sizeOfPartialErrors(); i++) {
					System.out.println("【ERROR】 Partial Error in Open BOMWindow = "
							+ createBOMWindowsResponse.serviceData.getPartialError(i).getMessages()[0]);
				}
				return null;
			}
			CreateBOMWindowsOutput[] output = createBOMWindowsResponse.output;
			if (null == output || output.length < 0) {
				return null;
			}
			// BOMWindow
			bomWindowParentLine.add(output[0].bomWindow);
			// TOPLine in BOMWindow
			bomWindowParentLine.add(output[0].bomLine);
			return bomWindowParentLine;
		} catch (Exception e) {
			e.printStackTrace();
			log.info(CommonTools.getExceptionMsg(e));
		}
		return null;
	}
	
	
	/**
	 * Close BOMWindow
	 *
	 * @param connection 连接
	 * @param bomWindow  BOM窗口
	 */
	public void closeBOMWindow(StructureManagementService structureManagementService, BOMWindow bomWindow) {
		CloseBOMWindowsResponse response = null;
		if (structureManagementService != null && bomWindow != null) {
			response = structureManagementService.closeBOMWindows(new BOMWindow[] { bomWindow });
		}
		if (response.serviceData.sizeOfPartialErrors() > 0) {
			for (int i = 0; i < response.serviceData.sizeOfPartialErrors(); i++) {
				System.out.println("Close BOMWindow Partial Error -- " + response.serviceData.getPartialError(i).getMessages()[0]);
			}
		}
	}

	/**
	 * Save BOMWindow
	 *
	 * @param connection 连接
	 * @param bomWindow  BOMWindow对象
	 */
	public void saveBOMWindow(StructureManagementService structureManagementService, BOMWindow bomWindow) {
		SaveBOMWindowsResponse saveResponse = structureManagementService.saveBOMWindows(new BOMWindow[] { bomWindow });
		if (saveResponse.serviceData.sizeOfPartialErrors() > 0) {
			for (int i = 0; i < saveResponse.serviceData.sizeOfPartialErrors(); i++) {
				System.out.println("Save BOMWindow Partial Error -- "
						+ saveResponse.serviceData.getPartialError(i).getMessages()[0]);
			}
		}
	}

}
