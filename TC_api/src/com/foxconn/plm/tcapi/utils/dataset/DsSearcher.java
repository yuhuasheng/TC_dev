package com.foxconn.plm.tcapi.utils.dataset;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.foxconn.plm.tcapi.constants.DatasetPropConstant;
import com.foxconn.plm.tcapi.utils.CommonTools;
import com.foxconn.plm.tcapi.utils.TCPublicUtils;
import com.teamcenter.services.strong.core.DataManagementService;
import com.teamcenter.soa.client.FileManagementUtility;
import com.teamcenter.soa.client.GetFileResponse;
import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.client.model.strong.Dataset;
import com.teamcenter.soa.client.model.strong.ImanFile;
import com.teamcenter.soa.exceptions.NotLoadedException;

public class DsSearcher {

	private static final Logger log = LoggerFactory.getLogger(DsSearcher.class);
	
	protected DsSearcher() {

	}

	/**
	 * 获取数据集数组
	 * @param dataset 数据集
	 * @param dmService 工具类
	 * @param fmsFileManagement fcc类
	 * @return
	 */
	public File[] getDataSetFiles(Dataset dataset, DataManagementService dmService,
			FileManagementUtility fmsFileManagement) {
		try {
			TCPublicUtils.getProperty(dmService, dataset, "ref_list");
			ModelObject[] dsfilevec = dataset.get_ref_list();
			ImanFile dsFile = null;
			if (dsfilevec.length > 0) {
				if (dsfilevec[0] instanceof ImanFile) {
					dsFile = (ImanFile) dsfilevec[0];
				}
				// getProperty(dmService, dsFile, "original_file_name");
				GetFileResponse getFileResponse = fmsFileManagement.getFiles(dsfilevec);
				return getFileResponse.getFiles();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 下载数据集
	 * 
	 * @param dataset           数据集对象
	 * @param dmService         工具类
	 * @param fmsFileManagement fms类
	 * @param fileExtensions    文件后缀名
	 * @param dirPath           存放文件路径
	 * @return
	 */
	public String downloadDataset(Dataset dataset, DataManagementService dmService, FileManagementUtility fmsFileManagement, String fileExtensions, String dirPath) {
		File newfile = null;
		try {
			TCPublicUtils.getProperty(dmService, dataset, DatasetPropConstant.REF_LIST);
			ModelObject[] dsfiles = dataset.get_ref_list();
			if (CommonTools.isEmpty(dsfiles)) {
				return "";
			}
			ImanFile dsFile = null;
			for (int i = 0; i < dsfiles.length; i++) {
				if (!(dsfiles[i] instanceof ImanFile)) {
					continue;
				}
				dsFile = (ImanFile) dsfiles[i];
				TCPublicUtils.refreshObject(dmService, dsFile);
				TCPublicUtils.getProperty(dmService, dsFile, DatasetPropConstant.ORIGINAL_FILE_NAME);
				String fileName = dsFile.get_original_file_name();
				System.out.println("【INFO】 fileName: " + fileName);
				if (!fileName.toLowerCase().contains(fileExtensions)) {
					continue;
				}
				GetFileResponse responseFiles = fmsFileManagement.getFiles(new ModelObject[] { dsFile });
				File[] fileinfovec = responseFiles.getFiles();
				File file = fileinfovec[0];

				String filePath = "";
				if (dirPath.endsWith("\\")) {
					filePath = dirPath + fileName;
				} else {
					filePath = dirPath + File.separator + fileName;
				}
				System.out.println("【INFO】 filePath: " + filePath);
				// 判断数据集是否存在
				newfile = new File(filePath);
				if (newfile.exists()) {
					newfile.delete();
				}
				File dstFile = new File(filePath);
				// 复制文件
				TCPublicUtils.copyFile(file, dstFile);
			}
			return newfile == null ? "" : newfile.getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
			log.info(CommonTools.getExceptionMsg(e));
		}
		return "";
	}

	/**
	 * 获取数据集
	 * 
	 * @param dataManagementService 工具类
	 * @param objects               数据集对象数组
	 * @return
	 * @throws NotLoadedException
	 */
	public Map<Dataset, String> getDataset(DataManagementService dataManagementServices, ModelObject[] objects)
			throws NotLoadedException {		
		if (CommonTools.isEmpty(objects)) {
			return null;
		}
		Map<Dataset, String> map = new LinkedHashMap<Dataset, String>();
		TCPublicUtils.refreshObject(dataManagementServices, objects);		
		for (ModelObject obj : objects) {
			if (!(obj instanceof Dataset)) {
				continue;
			}
			Dataset dataset = (Dataset) obj;
			String objectName = TCPublicUtils.getPropStr(dataManagementServices, dataset, DatasetPropConstant.OBJECT_NAME);			
			map.put(dataset, objectName);
		}
		return map;
	}

	public File getImanFile(ImanFile dsFile, DataManagementService dmService, FileManagementUtility fmsFileManagement) {
	        try {
	            ModelObject[] dsfilevec = new ModelObject[1];
	            if (dsFile != null) {
	                dsfilevec[0] = dsFile;
	                GetFileResponse getFileResponse = fmsFileManagement.getFiles(dsfilevec);
	                File[] files = getFileResponse.getFiles();
	                if (files.length > 0)
	                    return getFileResponse.getFiles()[0];
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return null;
	    }
}
