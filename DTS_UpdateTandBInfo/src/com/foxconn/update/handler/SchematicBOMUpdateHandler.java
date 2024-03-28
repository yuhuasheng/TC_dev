package com.foxconn.update.handler;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.foxconn.plm.tcapi.constants.BOMLinePropConstant;
import com.foxconn.plm.tcapi.constants.DatasetEnum;
import com.foxconn.plm.tcapi.constants.DatasetPropConstant;
import com.foxconn.plm.tcapi.constants.EPMTaskPropConstant;
import com.foxconn.plm.tcapi.constants.ItemPropConstant;
import com.foxconn.plm.tcapi.constants.ItemRevPropConstant;
import com.foxconn.plm.tcapi.constants.PersonPropConstant;
import com.foxconn.plm.tcapi.constants.TCSearchEnum;
import com.foxconn.plm.tcapi.constants.UserPropConstant;
import com.foxconn.plm.tcapi.mail.TCMail;
import com.foxconn.plm.tcapi.service.BOMService;
import com.foxconn.plm.tcapi.service.DatasetService;
import com.foxconn.plm.tcapi.service.EPMTaskService;
import com.foxconn.plm.tcapi.service.ItemService;
import com.foxconn.plm.tcapi.service.TCSOAServiceFactory;
import com.foxconn.plm.tcapi.soa.client.AppXSession;
import com.foxconn.plm.tcapi.startup.TCStartUp;
import com.foxconn.plm.tcapi.utils.CommonTools;
import com.foxconn.plm.tcapi.utils.TCPublicUtils;
import com.foxconn.plm.tcapi.utils.bom.BOMFacade;
import com.foxconn.plm.tcapi.utils.dataset.DsFacade;
import com.foxconn.update.constants.ConstantsEnum;
import com.foxconn.update.constants.ItemRevEnum;
import com.foxconn.update.constants.TCMailFileConstant;
import com.foxconn.update.constants.TCPreferenceConstant;
import com.foxconn.update.util.TxtPlacementFileTools;
import com.foxconn.update.util.ExcelPlacementDiffUtil;
import com.foxconn.update.util.ExcelPlacementFileTools;
import com.foxconn.update.util.TxtPlacementDiffUtil;
import com.sun.tools.xjc.model.Model;
import com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException;
import com.teamcenter.schemas.soa._2011_06.metamodel.Tool;
import com.teamcenter.services.internal.strong.core.ICTService;
import com.teamcenter.services.internal.strong.core._2011_06.ICT;
import com.teamcenter.services.internal.strong.core._2011_06.ICT.Arg;
import com.teamcenter.services.strong.cad.StructureManagementService;
import com.teamcenter.services.strong.cad._2007_01.StructureManagement.CloseBOMWindowsResponse;
import com.teamcenter.services.strong.cad._2007_01.StructureManagement.CreateBOMWindowsInfo;
import com.teamcenter.services.strong.cad._2007_01.StructureManagement.CreateBOMWindowsOutput;
import com.teamcenter.services.strong.cad._2007_01.StructureManagement.CreateBOMWindowsResponse;
import com.teamcenter.services.strong.cad._2008_06.StructureManagement.SaveBOMWindowsResponse;
import com.teamcenter.services.strong.core.DataManagementService;
import com.teamcenter.soa.client.Connection;
import com.teamcenter.soa.client.FileManagementUtility;
import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.client.model.Type;
import com.teamcenter.soa.client.model.strong.BOMLine;
import com.teamcenter.soa.client.model.strong.BOMWindow;
import com.teamcenter.soa.client.model.strong.Dataset;
import com.teamcenter.soa.client.model.strong.EPMTask;
import com.teamcenter.soa.client.model.strong.Group;
import com.teamcenter.soa.client.model.strong.Item;
import com.teamcenter.soa.client.model.strong.ItemRevision;
import com.teamcenter.soa.client.model.strong.PSBOMViewRevision;
import com.teamcenter.soa.client.model.strong.Person;
import com.teamcenter.soa.client.model.strong.User;
import com.teamcenter.soa.exceptions.NotLoadedException;

/**
 * @author infodba
 * @version 创建时间：2021年12月20日 下午3:15:09
 * @Description 电子原理图正反面坐标更新handler
 */
public class SchematicBOMUpdateHandler {
	
	private static final Logger log = LoggerFactory.getLogger(SchematicBOMUpdateHandler.class);
	
//	private static final Logger log = Logger.getLogger(SchematicBOMUpdateHandler.class);

	private static Properties props = null;

	private static Properties tcProps = null;

	private static String PLACEMENTOWNER = ""; // Placement文件所有者

	private static String EEOWNER = ""; // D9_EE_SchemRevision的所有者
	
	private static String placementType = null;  // placement文件的类型
	
	static {
		System.out.println("configurating log4j with log4j.properties");
		System.out.println(CommonTools.getPath(SchematicBOMUpdateHandler.class));
		props = CommonTools.getProperties(
				CommonTools.getPath(SchematicBOMUpdateHandler.class) + File.separator + "ConstantParams.properties");
//		PropertyConfigurator.configure(props.getProperty("log4_properties"));
		tcProps = CommonTools.getProperties(props.getProperty("tc_properties"));
	}

	public static void main(String[] args) throws Exception {
		log.info("========== SchematicBOMUpdateHandler start ==========");
//		args = new String[] { "-taskUid=RkiJJiOtppJG1D" };
		if (null == args || args.length <= 0) {
			return;
		}		
		String taskUid = "";
		for (String arg : args) {
			System.out.println(arg);
			if (arg.startsWith("-taskUid")) {
				taskUid = arg.split("=")[1].trim();
				System.out.println("==>> taskUid: " + taskUid);
			}
		}

		if (CommonTools.isEmpty(taskUid)) {
			throw new Exception("【ERROR】 获取输入参数taskUid发生错误！");
		}
		Map<ModelObject, String> ownMap = null; // 对象版本所有者集合
		List<String> attachmentList = new ArrayList<String>(); // 存放邮件附件信息的集合
		Boolean check = null;
		String to = props.getProperty("to_userId"); // 邮件接受者		
		String bodymsg = null; // 邮件正文内容
		String processName = null; // 流程名称
		String layoutItemId = null; // layout对象版本ID
		String layoutVersion = null; // layout对象版本号
		String EEItemId = null; // EE对象版本ID
		String EEVersion = null; // EE对象版本号
		String absoluteFilePath = null; // placement文件的绝对路径
		
		// 登录TC系统
		TCStartUp startUp = new TCStartUp(tcProps.getProperty("TC_IP"), tcProps.getProperty("TC_USERNAME"), tcProps.getProperty("TC_PASSWORD"));
		boolean isLogin = startUp.isLogin();
		log.info("【INFO】 isLogin: " + isLogin);
		try {			
			// 开启旁路
			TCPublicUtils.byPass(TCSOAServiceFactory.getSessionService(), true);

			// 获取任务对象
			ModelObject object = TCPublicUtils.findObjectByUID(TCSOAServiceFactory.getDataManagementService(), taskUid);
			if (CommonTools.isEmpty(object)) {
				throw new Exception("【ERROR】 获取流程对象发生错误！");
			}
			// 获取流程根任务
			EPMTask rootTask = EPMTaskService.getEPMTask(TCSOAServiceFactory.getDataManagementService(), object);

			// 获取流程名称
			processName = EPMTaskService.getProcessName(TCSOAServiceFactory.getDataManagementService(), rootTask);
			log.info("【INFO】 流程名称为: " + processName);
 
			// 获取目标对象数组
			ModelObject[] rootTargetAttachments = EPMTaskService.getRootTargetAttachments(TCSOAServiceFactory.getDataManagementService(), rootTask);
			if (CommonTools.isEmpty(rootTargetAttachments)) {
				throw new Exception("【ERROR】 流程名称为: " + processName + ", 目标对象数组为空！");
			}

			// 获取对象/对象类型映射
			Map<ModelObject, String> modelObjectTypeMapp = TCPublicUtils.getModelObjectTypeMapp(rootTargetAttachments);
			if (CommonTools.isEmpty(modelObjectTypeMapp)) {
				throw new Exception("【ERROR】 流程名称为: " + processName + ", 获取目标对象数组类型失败！");
			}

			// 获取首选项的值
			List<String> itemRevTypeTcPreferences = TCPublicUtils.getTcPreference(AppXSession.getConnection(), TCPreferenceConstant.D9_ALLOWTANDBINFOITEM, "site");
			if (CommonTools.isEmpty(itemRevTypeTcPreferences)) {
				throw new Exception("【ERROR】 TC首选项: " + TCPreferenceConstant.D9_ALLOWTANDBINFOITEM + ", 不存在");
			}	
			
			// 获取符合首选项要求的对象版本
			ItemRevision layoutItemRev = filterItemRev(modelObjectTypeMapp, itemRevTypeTcPreferences);
			if (CommonTools.isEmpty(layoutItemRev)) {
				throw new Exception("【ERROR】流程名称为:" + processName + ", 目标对象数组的对象版本类型不符合本次操作要求，请重新选择...");
			}

			List<String> fmsUrlTCPreferences = TCPublicUtils.getTcPreference(AppXSession.getConnection(), TCPreferenceConstant.FMS_BOOTSTRAP_URLS, "site");
			if (CommonTools.isEmpty(fmsUrlTCPreferences)) {
				throw new Exception("【ERROR】 TC首选项: " + TCPreferenceConstant.FMS_BOOTSTRAP_URLS + ", 不存在");
			}
			String fmsUrl = fmsUrlTCPreferences.get(0);
			log.info("【INFO】 fmsUrl: " + fmsUrl);
			
			layoutItemId = TCPublicUtils.getPropStr(TCSOAServiceFactory.getDataManagementService(), layoutItemRev, ItemRevPropConstant.ITEM_ID);
			log.info("【INFO】 layoutItemId:" + layoutItemId);
			layoutVersion = TCPublicUtils.getPropStr(TCSOAServiceFactory.getDataManagementService(), layoutItemRev, ItemRevPropConstant.ITEM_REVISION_ID);
			log.info("【INFO】layoutVersion:" + layoutVersion);

			// 查找保存Placement文件的文件夹
			String dir = CommonTools.getFilePath(ConstantsEnum.EDAPLACEMENTFOLDER.value());
			log.info("【INFO】 filePath: " + dir);

			// 删除文件夹下面的所有文件
			CommonTools.deletefile(dir);

			ModelObject[] EDAHasDerivedDatasetObjects = TCPublicUtils.getPropModelObjectArray(TCSOAServiceFactory.getDataManagementService(), layoutItemRev, 
					ItemRevPropConstant.EDAHASDERIVEDDATASET);
			if (CommonTools.isNotEmpty(EDAHasDerivedDatasetObjects)) { // 判断是否含有TXT placement文件
				// 获取数据集文件
				Map<Dataset, String> datasetMap = DatasetService.getDataset(TCSOAServiceFactory.getDataManagementService(), EDAHasDerivedDatasetObjects);
				if (CommonTools.isEmpty(datasetMap)) {
					throw new Exception("【ERROR】 流程名称为: " + processName + ", 获取Placement数据集失败...");
				}				
				// 获取Placement文件的绝对路径和获取Placemet数据集的所有者
				absoluteFilePath = getDatasetFile(TCSOAServiceFactory.getDataManagementService(),TCSOAServiceFactory.getFileManagementUtility(null), datasetMap, dir);
				if (CommonTools.isEmpty(absoluteFilePath)) {
					throw new Exception("【ERROR】 流程名称为: " + processName + ", 下载Placement数据集文件失败...");
				}
				log.info("【INFO】 Placement文件的绝对路径为: " + absoluteFilePath);
				
			}			
			
			ModelObject[] imanSpecification = TCPublicUtils.getPropModelObjectArray(TCSOAServiceFactory.getDataManagementService(), layoutItemRev, ItemRevPropConstant.IMAN_SPECIFICATION);
			
			if (CommonTools.isEmpty(imanSpecification)) {
				throw new Exception("【ERROR】 流程名称为: " + processName + ", 获取IMAN_specification属性值失败...");
			}			
			
			ItemRevision EEItemRev = getEEItemRevision(TCSOAServiceFactory.getDataManagementService(), imanSpecification, itemRevTypeTcPreferences);
			if (CommonTools.isEmpty(EEItemRev)) {
				throw new Exception("【ERROR】 流程名称为: " + processName + ", 获取EE对象版本失败...");
			}
			EEOWNER = TCPublicUtils.getOwnUser(TCSOAServiceFactory.getDataManagementService(), EEItemRev); // 获取EE对象版本所有者
			
			
			EEItemId = TCPublicUtils.getPropStr(TCSOAServiceFactory.getDataManagementService(), EEItemRev, ItemRevPropConstant.ITEM_ID);
			log.info("【INFO】 EEItemId:" + EEItemId);
			EEVersion = TCPublicUtils.getPropStr(TCSOAServiceFactory.getDataManagementService(), EEItemRev, ItemRevPropConstant.ITEM_REVISION_ID);
			log.info("【INFO】EEVersion:" + EEVersion);
			
			if (CommonTools.isEmpty(absoluteFilePath)) { // 假如已经有了TXT placement文件，无需此重新获取placement文件
				// 获取数据集文件
				Map<Dataset, String> datasetMap = DatasetService.getDataset(TCSOAServiceFactory.getDataManagementService(), imanSpecification);
				if (CommonTools.isEmpty(datasetMap)) {
					throw new Exception("【ERROR】 流程名称为: " + processName + ", 获取Placement数据集失败...");
				}
				// 获取Placement文件的绝对路径和获取Placemet数据集的所有者
				absoluteFilePath = getDatasetFile(TCSOAServiceFactory.getDataManagementService(), TCSOAServiceFactory.getFileManagementUtility(null), 
						datasetMap, dir);
				if (CommonTools.isEmpty(absoluteFilePath)) {
					throw new Exception("【ERROR】 流程名称为: " + processName + ", 下载Placement数据集文件失败...");
				}
				log.info("【INFO】 Placement文件的绝对路径为: " + absoluteFilePath);
			}
				

			// 获取EE和Layout负责人邮箱
			Map<String, String> emailMap = TCPublicUtils.getEmail(TCSOAServiceFactory.getSavedQueryService(),
					TCSOAServiceFactory.getDataManagementService(), Arrays.asList(EEOWNER, PLACEMENTOWNER));
			if (CommonTools.isEmpty(emailMap)) {
				log.info("【ERROR】 流程名称为: " + processName + ", 获取EE和layout负责人邮箱失败...");
			}			
//			emailMap = null;
			// 拼接邮件接受者邮箱
			String email = TCMail.generateToUser(emailMap);
			to = email == null ? "" + to : email + to;						
				
			List<ModelObject> itemRevList = new ArrayList<ModelObject>();
			List<String> propertyList = new ArrayList<String>();
			// 获取BOMLine集合清单，需要解包的BOMLine将其解包
			check = BOMService.getBOMWindowInfo(TCSOAServiceFactory.getStructureManagementService(), TCSOAServiceFactory.getStructureService(), 
					TCSOAServiceFactory.getDataManagementService(), EEItemRev, 3, itemRevList, propertyList);
			if (!check || itemRevList.size() == 1) { // 获取BOMLine失败, 不予处理; 假如没有BOM结构，也不予处理
				throw new Exception("【ERROR】 流程名称为: " + processName + ", 获取BOMLine集合信息失败！");
			}			
			
			// 获取BOMLine的对象版本集合
//			List<ModelObject> itemRevList = BOMService.conversionItemRev(TCSOAServiceFactory.getDataManagementService(), bomLineList);
//			if (CommonTools.isEmpty(itemRevList)) {
//				throw new Exception("【ERROR】 流程名称为: " + processName + ", 获取BOMLine对应的对象版本失败...");
//			}		
			
			ownMap = ItemService.getModelObjectOwner(TCSOAServiceFactory.getDataManagementService(), itemRevList); // 记录对象版本和所有者
			if (CommonTools.isEmpty(ownMap)) {
				throw new Exception("【ERROR】 流程名称为: " + processName + ", 获取对象版本所有者失败...");
			}

			log.info("******** 执行更改BOMLine属性之前所有者明细  start ********.");
			printCurObjOwner(TCSOAServiceFactory.getDataManagementService(), ownMap);
			log.info("******** 执行更改BOMLine属性之前所有者明细  end ********.");
			
			Map<String, List<String>> placementMap = null;
			String diffFilePath = null;
			if ("Excel".equals(placementType)) {				
				placementMap = ExcelPlacementFileTools.analysePlacementExcel(absoluteFilePath); // 解析Excel Placement文件
				if (CommonTools.isEmpty(placementMap)) {
					throw new Exception("【ERROR】 流程名称为: " + processName + ", 解析Placement文件失败...");
				}				
				// 比较Placement文件和 
				diffFilePath = ExcelPlacementDiffUtil.diffWithItemRevision(TCSOAServiceFactory.getDataManagementService(), propertyList, placementMap, dir, EEItemRev);
				if (CommonTools.isEmpty(diffFilePath)) {
					log.info("【INFO】 流程名称为: " + processName + ", placement和Design-BOM不存在差异");
				} else {
					attachmentList.add(diffFilePath);
					// 生成差异性报表数据集
					generateDifferDataset(processName, EEItemRev, EEItemId, EEVersion, diffFilePath);
				}
			} else if ("txt".equals(placementType)) {				
				placementMap = TxtPlacementFileTools.analysePlacementTxt(absoluteFilePath); // 解析Txt Placement文件
				if (CommonTools.isEmpty(placementMap)) {
					throw new Exception("【ERROR】 流程名称为: " + processName + ", 解析Placement文件失败...");
				}				
				// 比较Placement文件和 
				diffFilePath = TxtPlacementDiffUtil.diffWithItemRevision(TCSOAServiceFactory.getDataManagementService(), propertyList, placementMap, dir, EEItemRev);
				if (CommonTools.isEmpty(diffFilePath)) {
					log.info("【INFO】 流程名称为: " + processName + ", placement和Design-BOM不存在差异");
				} else {
					attachmentList.add(diffFilePath);
					// 生成差异性报表数据集
					generateDifferDataset(processName, EEItemRev, EEItemId, EEVersion, diffFilePath);
				}
			}	
			
			// 修改BOMLine属性字段值
			List<String> errList = BOMService.modifyBOMLineInfo(TCSOAServiceFactory.getStructureManagementService(), TCSOAServiceFactory.getStructureService(), 
					TCSOAServiceFactory.getDataManagementService(), EEItemRev, placementMap, BOMLinePropConstant.BL_REF_DESIGNATOR + "=", 3);
			if (CommonTools.isNotEmpty(errList)) {
				// 生成修改失败错误文件
				String errorAbsoluteFilePath = TCMail.generateMailFile(errList, dir, EEItemId + "_" + EEVersion + "_" + TCMailFileConstant.ERRORRECORDFILENAME);
				attachmentList.add(errorAbsoluteFilePath);
			}			
			
			bodymsg = "<html><head></head><body><h3 style=\"font-family: 宋体;  font-size:15px;\">" + "流程為: " + processName + ", 圖號為: "
					+ EEItemId + ", 版本號為: " + EEVersion + ", 原理圖元件正反面信息餘Layout正反面信息同步完成，請登錄TC進行查看，謝謝！！"
					+ "</h3></body></html>";

		} catch (Exception e) {
			e.printStackTrace();
			log.info(CommonTools.getExceptionMsg(e));
			bodymsg = "<html><head></head><body><h3 style=\"font-family: 宋体;  font-size:15px;\">" + "流程為: " + processName + ", 圖號為: "
					+ EEItemId + ", 版本號為: " + EEVersion + ", 原理圖元件正反面信息餘Layout正反面信息同步存在錯誤，請登錄TC進行查看，謝謝！！"
					+ "</h3></body></html>";
		} finally {
			// 将对象版本的所有者修改为之前用户
			try {
				if (CommonTools.isNotEmpty(ownMap)) {
					ownMap = batchChangerOwner(TCSOAServiceFactory.getDataManagementService(), ownMap);
					log.info("******** 执行更改BOMLine属性之后所有者明细  start ********.");
					printCurObjOwner(TCSOAServiceFactory.getDataManagementService(), ownMap);
					log.info("******** 执行更改BOMLine属性之后所有者明细  end ********.");
					if (!checkChangerOwnerResult(ownMap)) {
						throw new Exception("流程为: " + processName + ", 图号为: " + EEItemId + ", 版本号为: " + EEVersion + ", 批量改回对象/对象版本所有权发生错误！");
					}
				}				
			} catch (Exception e) {  
				e.printStackTrace();
				System.err.println(CommonTools.getExceptionMsg(e));
				bodymsg = "<html><head></head><body><h3 style=\"font-family: 宋体;  font-size:15px;\">" + "流程為: " + processName + ",  圖號為: "
						+ EEItemId + ", 版本號為: " + EEVersion + ", 原理圖元件正反面信息餘Layout正反面信息同步存在錯誤，請登錄TC進行查看，謝謝！！"
						+ "</h3></body></html>";
			}

			if (CommonTools.isNotEmpty(bodymsg)) {
				HashMap<String, String> httpmap = new HashMap<String, String>();
				httpmap.put("requestPath", "http://10.203.163.43:");
				httpmap.put("ruleName", "80/tc-mail/teamcenter/sendMail3");
				httpmap.put("sendTo", to);
				httpmap.put("subject", props.getProperty("subject"));
				httpmap.put("htmlmsg", bodymsg);
				String result = TCMail.sendMail(httpmap, attachmentList);
				log.info("【INFO】 邮件发送结果: " + result);
			}
			try {
				TCPublicUtils.byPass(TCSOAServiceFactory.getSessionService(), false); // 关闭旁路
			} catch (ServiceException e) {
				e.printStackTrace();				
			}
			startUp.disconnectTC(); // 登出系统
			log.info("========== SchematicBOMUpdateHandler end ==========");
		}

	}

	/**
	 * 生成差异性报表数据集
	 * 
	 * @param attachmentContentList
	 * @param processName
	 * @param parentItemRev
	 * @param parentId
	 * @param parentVersion
	 * @param diffFilePath
	 * @return
	 * @throws NotLoadedException
	 * @throws Exception
	 */
	private static void generateDifferDataset(String processName, ItemRevision EEItemRev, String EEItemId,
			String EEVersion, String diffFilePath) throws NotLoadedException, Exception {
		Map<Dataset, String> datasetMap;
		log.info("【INFO】 diffFilePath: " + diffFilePath);
		// 获取IMAN_Specification的值
		ModelObject[] imanSpecification = TCPublicUtils.getPropModelObjectArray(TCSOAServiceFactory.getDataManagementService(), EEItemRev, ItemRevPropConstant.IMAN_SPECIFICATION);
		// 获取数据集文件
		datasetMap = DatasetService.getDataset(TCSOAServiceFactory.getDataManagementService(), imanSpecification);
		String dsName = EEItemId + "_" + EEVersion + "_" +CommonTools.getNowTime2() + "_" +ConstantsEnum.DIFFERNAME.value();
		// 获取差异性报表数据集
		Dataset differDataset = getDifferDataset(TCSOAServiceFactory.getDataManagementService(), datasetMap, dsName);
		if (CommonTools.isEmpty(differDataset)) { // 判断差异性报表Text数据集是否存在
			// 创建数据集
			differDataset = DatasetService.createDataset(TCSOAServiceFactory.getDataManagementService(), EEItemRev, dsName, 
					DatasetEnum.TXT.type(), DatasetEnum.TXT.relationType());
			if (CommonTools.isEmpty(differDataset)) {
				log.info("【ERROR】 流程名称为: " + processName + ", 生成差异性报表失败...");
				throw new Exception("【ERROR】 流程名称为: " + processName + ", 生成差异性报表失败...");
			}			
		} else {	
			DatasetService.removeFileFromDataset(TCSOAServiceFactory.getDataManagementService(), differDataset, DatasetEnum.TXT.refName()); // 数据集若存在，则先移除掉命名的引用下的数据集
		}
		
		Boolean check = DatasetService.addDatasetFile(TCSOAServiceFactory.getFileManagementUtility(null), // 数据集添加物理文件
				TCSOAServiceFactory.getDataManagementService(), differDataset, diffFilePath, DatasetEnum.TXT.refName(), true);			
		if (!check) {
			throw new Exception("【ERROR】 流程名称为: " + processName + ", 差异性报表数据集添加附件失败...");
		}
	}

	/**
	 * 将目标对象数组的对象和首选项TCPreferenceConstant 的值D9_ALLOWTANDBINFOITEM进行对比
	 * 
	 * @param targetMap     目标对象集合
	 * @param tcPreferences 数组首选项
	 * @return
	 */
	public static ItemRevision filterItemRev(Map<ModelObject, String> targetMap, List<String> itemRevTypeTcPreferences) {
		ItemRevision itemRev = null;
		boolean flag = false;
		for (String str : itemRevTypeTcPreferences) {
			log.info("==>> 首选项的值为: " + str);
			for (Map.Entry<ModelObject, String> entry : targetMap.entrySet()) {
				String value = entry.getValue();
				if (str.trim().equals(value)) {
					itemRev = (ItemRevision) entry.getKey();
					flag = true;
					break;
				}
			}
			if (flag) {
				break;
			}
		}
		return itemRev;
	}

	/**
	 * 下载Placement数据集
	 * 
	 * @param map      数据集集合
	 * @param filePath 数据集文件存放的物理路径
	 * @return
	 */
	private static String getDatasetFile(DataManagementService dataManagementService,
			FileManagementUtility fileManagementUtility, Map<Dataset, String> map, String dir) {
		String absoluteFilePath = "";
		for (Map.Entry<Dataset, String> entry : map.entrySet()) {
			String objectName = entry.getValue();
			Dataset dataset = entry.getKey();
			String objectType = dataset.getTypeObject().getName();
			String fileExtensions = null;
			if (DatasetEnum.MSExcel.type().equals(objectType) && objectName.contains("Location")) {
				fileExtensions = DatasetEnum.MSExcel.fileExtensions();
				placementType = "Excel";
			} else if (DatasetEnum.MSExcelX.type().equals(objectType) && objectName.contains("Location")) {
				fileExtensions = DatasetEnum.MSExcelX.fileExtensions();
				placementType = "Excel";
			} else if (DatasetEnum.D9_EDAPlacement.type().equals(objectType)) {
				fileExtensions = DatasetEnum.D9_EDAPlacement.fileExtensions();
				placementType = "txt";
			}
			if (CommonTools.isEmpty(fileExtensions)) {
				continue;
			}			
			log.info("【INFO】 数据集名称为:  " + objectName + ", 正在下载...");
			log.info("【INFO】 数据集类型为:  " + objectType);
			PLACEMENTOWNER = TCPublicUtils.getOwnUser(dataManagementService, dataset); // 获取Placement数据集的所有者
			// 下载数据集
			absoluteFilePath = DatasetService.downloadDataset(dataset, dataManagementService, fileManagementUtility, fileExtensions, dir);
			if (CommonTools.isEmpty(absoluteFilePath)) {
				log.info("【ERROR】 数据集名称为: " + objectName + ", 下载失败啦！");
			} else {
				log.info("【INFO】 数据集名称为: " + objectName + ", 下载成功...");
			}
		}
		return absoluteFilePath;
	}

	/**
	 * 判断差异性报表Text数据集是否存在
	 * 
	 * @param map
	 * @return
	 * @throws NotLoadedException
	 */
	@SuppressWarnings("unused")
	private static Dataset getDifferDataset(DataManagementService dataManagementService, Map<Dataset, String> map,
			String dsName) throws NotLoadedException {
		if (CommonTools.isEmpty(map)) {
			return null;
		}
		Dataset differDataset = null;
		for (Map.Entry<Dataset, String> entry : map.entrySet()) {
			Dataset dataset = entry.getKey();
			String objectType = dataset.getTypeObject().getName();
			String objectName = TCPublicUtils.getPropStr(dataManagementService, dataset,
					DatasetPropConstant.OBJECT_NAME);
			if (DatasetEnum.TXT.type().equals(objectType) && dsName.equals(objectName)) { // 判断数据集类型和数据集名称是否是创建出来的差异性报表数据集
				differDataset = dataset;
				break;
			}
		}
		return differDataset;
	}

	/**
	 * 获取D9_EE_SchemRevision的所有者
	 * 
	 * @param edaHasSchematicObject
	 */
	private static void getEDAHasSchematicOwner(Map<ModelObject, String> map) {
		for (Map.Entry<ModelObject, String> entry : map.entrySet()) {
			ModelObject obj = entry.getKey();
			if (!(obj instanceof ItemRevision)) {
				continue;
			}
			String objectType = obj.getTypeObject().getName();
			if (ItemRevEnum.EE_SCHEMREVISION.type().equals(objectType)) {
				EEOWNER = entry.getValue();
				break;
			}
		}
	}
	
	/**
	 * 获取EE对象版本
	 * @param dataManagementServices
	 * @param objects
	 * @param tcPreferences
	 * @return
	 */
	private static ItemRevision getEEItemRevision(DataManagementService dataManagementServices, ModelObject[] objects, List<String> itemRevTypeTcPreferences) {
		ItemRevision itemRev = null;
		boolean flag = false;
		if (CommonTools.isEmpty(objects)) {
			return null;
		}
		for (String str : itemRevTypeTcPreferences) {
			for (ModelObject obj : objects) {
				if (!(obj instanceof ItemRevision)) {
					continue;
				}				
				String objectType = obj.getTypeObject().getName();
				if (str.trim().equals(objectType)) {
					itemRev = (ItemRevision) obj;					
					flag = true;
					break;
				}
			}
			if (flag) {
				break;
			}
		}
		return itemRev;
	}
	
	/**
	 * 打印当前对象版本所属的所有者信息
	 * 
	 * @param ownMap
	 */
	private static void printCurObjOwner(DataManagementService dataManagementService, Map<ModelObject, String> ownMap) {
		ownMap.forEach((key, value) -> {
			ModelObject obj = key;
			String owner = value;
			try {
				String itemId = TCPublicUtils.getPropStr(dataManagementService, obj, ItemRevPropConstant.ITEM_ID);
				String version = TCPublicUtils.getPropStr(dataManagementService, obj, ItemRevPropConstant.ITEM_REVISION_ID);
				log.info("【INFO】 零组件ID为: " + itemId + ", 版本号为: " + version + ", 当前用户所有者为: " + owner);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * 批量更改对象/对象版本所有权
	 * 
	 * @param ownMap
	 * @return
	 * @throws NotLoadedException
	 */
	private static Map<ModelObject, String> batchChangerOwner(DataManagementService datamanagementservice,
			Map<ModelObject, String> ownMap) throws NotLoadedException {
		Map<ModelObject, String> recordOwnMap = new LinkedHashMap<ModelObject, String>();
		for (Map.Entry<ModelObject, String> entry : ownMap.entrySet()) {
			ModelObject object = entry.getKey();
			String itemId = TCPublicUtils.getPropStr(datamanagementservice, object, ItemRevPropConstant.ITEM_ID);
			String version = TCPublicUtils.getPropStr(datamanagementservice, object, ItemRevPropConstant.ITEM_REVISION_ID);
//			String ownUser = "20286";
			String ownUser = entry.getValue();
			String msg = ownUser;
			ModelObject[] userObjects = TCPublicUtils.executequery(TCSOAServiceFactory.getSavedQueryService(),
					TCSOAServiceFactory.getDataManagementService(), TCSearchEnum.__WEB_FIND_USER.queryName(),
					TCSearchEnum.__WEB_FIND_USER.queryParams(), new String[] { "20286" }); // 获取user对象
			if (CommonTools.isEmpty(userObjects)) { // 判断是否为空
				msg = "【ERROR】: " + ownUser + ", 用户在Teamcenter不存在";
				recordOwnMap.put(object, msg);
				continue;
			}
			User user = (User) userObjects[0];
			Group group = (Group) user.get_default_group();
			// 获取对象当前的所有者
			String str = TCPublicUtils.getOwnUser(datamanagementservice, object);
			if (ownUser.equals(str)) { // 如果当前用户的所有者和ownUser一致，无需更改
				recordOwnMap.put(object, msg);
				continue;
			}
			// 更改对象版本所有权
			if (!TCPublicUtils.changeOwnShip(datamanagementservice, object, user, group)) {
				msg = "【ERROR】 零组件ID为: " + itemId + ", 版本号为: " + version + ", 修改对象版本的所有权失败";
				recordOwnMap.put(object, msg);
				log.info(msg);
			} else {
				recordOwnMap.put(object, msg);
			}
		}
		return recordOwnMap;
	}

	/**
	 * 判断更改所有权的结果
	 * 
	 * @param changerOwnerResultMap
	 * @return
	 */
	private static boolean checkChangerOwnerResult(Map<ModelObject, String> changerOwnerResultMap) {
		if (CommonTools.isEmpty(changerOwnerResultMap)) {
			return false;
		}
		boolean flag = true;
		for (Map.Entry<ModelObject, String> entry : changerOwnerResultMap.entrySet()) {
			String msg = entry.getValue();
			if (msg.contains("【ERROR】")) {
				flag = false;
				break;
			}
		}
		return flag;
	}
}
