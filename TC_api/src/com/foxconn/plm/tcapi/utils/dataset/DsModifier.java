package com.foxconn.plm.tcapi.utils.dataset;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.foxconn.plm.tcapi.constants.DatasetPropConstant;
import com.foxconn.plm.tcapi.utils.CommonTools;
import com.foxconn.plm.tcapi.utils.bom.BOMSearcher;
import com.teamcenter.services.loose.core._2006_03.FileManagement;
import com.teamcenter.services.strong.core.DataManagementService;
import com.teamcenter.soa.client.FileManagementUtility;
import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.client.model.ServiceData;
import com.teamcenter.soa.client.model.strong.Dataset;
import com.teamcenter.soa.exceptions.NotLoadedException;

public class DsModifier {

	private static final Logger log = LoggerFactory.getLogger(DsModifier.class);
	
	protected DsModifier() {

	}

	/**
	 * 数据集添加附件
	 * 
	 * @param fMSFileManagement     fcc
	 * @param dataManagementService 工具类
	 * @param dataset               数据集
	 * @param fileName              文件
	 * @param refName               引用关系
	 * @return
	 * @throws NotLoadedException
	 */
	public boolean addDatasetFile(FileManagementUtility fMSFileManagement, DataManagementService dataManagementService,
			Dataset dataset, String fileName, String refName, boolean isText) {
		try {
			dataManagementService.refreshObjects(new ModelObject[] { dataset });
			dataManagementService.getProperties(new ModelObject[] { dataset },
					new String[] { DatasetPropConstant.REF_LIST });
			ModelObject[] dsFileVec = dataset.get_ref_list();
			// 删除数据集命名的引用下的文件
			dataManagementService.deleteObjects(dsFileVec);
			dataManagementService.refreshObjects(new ModelObject[] { dataset });

			FileManagement.DatasetFileInfo[] fileInfos = new FileManagement.DatasetFileInfo[1];
			FileManagement.DatasetFileInfo fileInfo = new FileManagement.DatasetFileInfo();
			File file = new File(fileName);
			if (!file.exists()) {
				return false;
			}

			fileInfo.fileName = file.getAbsolutePath();
			;
			fileInfo.allowReplace = true;
//	         fileInfo.isText = false;
			fileInfo.isText = isText;
			fileInfo.namedReferencedName = refName;
			fileInfos[0] = fileInfo;

			FileManagement.GetDatasetWriteTicketsInputData[] inputDatas = new FileManagement.GetDatasetWriteTicketsInputData[1];
			FileManagement.GetDatasetWriteTicketsInputData inputData = new FileManagement.GetDatasetWriteTicketsInputData();

			inputData.dataset = dataset;
			inputData.createNewVersion = false;
			inputData.datasetFileInfos = fileInfos;
			inputDatas[0] = inputData;

			ServiceData response = fMSFileManagement.putFiles(inputDatas);
			if (response.sizeOfPartialErrors() > 0) {
				file.delete();
				return false;
			}
			dataManagementService.refreshObjects(new ModelObject[] { dataset });
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			log.info(CommonTools.getExceptionMsg(e));
		}
		return false;
	}

	/**
	 * 移除数据集的命名的引用下的物理文件
	 * @param dataset 数据集
	 * @param dataManagementService 工具类
	 * @param type 类型
	 * @throws NotLoadedException
	 */
	public static boolean removeFileFromDataset(DataManagementService dataManagementService, Dataset dataset, String type) {
		try {
			dataManagementService.refreshObjects(new ModelObject[] { dataset });
			dataManagementService.getProperties(new ModelObject[] { dataset },
					new String[] { DatasetPropConstant.REF_LIST, DatasetPropConstant.OBJECT_NAME });
			ModelObject[] files = dataset.get_ref_list();
			boolean flag = false;
			for (int i = 0; i < files.length; i++) {
				com.teamcenter.services.strong.core._2007_09.DataManagement.NamedReferenceInfo[] nrInfo = new com.teamcenter.services.strong.core._2007_09.DataManagement.NamedReferenceInfo[1];
				nrInfo[0] = new com.teamcenter.services.strong.core._2007_09.DataManagement.NamedReferenceInfo();
				nrInfo[0].clientId = files[i].getUid();
				nrInfo[0].deleteTarget = true;
				nrInfo[0].type = type;
				nrInfo[0].targetObject = files[i];
				com.teamcenter.services.strong.core._2007_09.DataManagement.RemoveNamedReferenceFromDatasetInfo datasetinfo[] = new com.teamcenter.services.strong.core._2007_09.DataManagement.RemoveNamedReferenceFromDatasetInfo[1];
	            datasetinfo[0] = new com.teamcenter.services.strong.core._2007_09.DataManagement.RemoveNamedReferenceFromDatasetInfo();
	            datasetinfo[0].clientId = dataset.getUid();
	            datasetinfo[0].dataset = dataset;
	            datasetinfo[0].nrInfo = nrInfo;
	            dataManagementService.removeNamedReferenceFromDataset(datasetinfo);
	            dataManagementService.removeNamedReferenceFromDataset(datasetinfo);
	            System.out.println("AAAAA源文件已经删除");
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			log.info(CommonTools.getExceptionMsg(e));
		}
		return false;
	}
}
