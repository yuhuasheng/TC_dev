package com.foxconn.plm.tcapi.utils.dataset;

import java.io.File;

import org.apache.log4j.Logger;

import com.foxconn.plm.tcapi.constants.DatasetPropConstant;
import com.teamcenter.services.loose.core._2006_03.FileManagement;
import com.teamcenter.services.strong.core.DataManagementService;
import com.teamcenter.services.strong.core._2006_03.DataManagement.CreateDatasetsResponse;
import com.teamcenter.services.strong.core._2008_06.DataManagement;
import com.teamcenter.soa.client.Connection;
import com.teamcenter.soa.client.FileManagementUtility;
import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.client.model.ServiceData;
import com.teamcenter.soa.client.model.strong.Dataset;
import com.teamcenter.soa.client.model.strong.ItemRevision;
import com.teamcenter.soa.exceptions.NotLoadedException;
import com.teamcenter.tctp.util.Log;

public class DsCreator {

	private static final Logger log = Logger.getLogger(DsCreator.class);

	protected DsCreator() {

	}

	/**
	 * 创建数据集
	 * 
	 * @param dataManagementService 工具类
	 * @param itemRevision          对象版本
	 * @param dsname                数据集名称
	 * @param type                  数据集类型
	 * @param relationType          数据集类型
	 * @return
	 */
	public Dataset createDataset(DataManagementService dataManagementService, ItemRevision itemRevision, String dsname,
			String type, String relationType) {
		DataManagement.DatasetProperties2 props = new DataManagement.DatasetProperties2();
		props.clientId = "datasetClientId";
		props.type = type;
		props.name = dsname;
		props.description = "Create dataset object";
		props.container = itemRevision;
		props.relationType = relationType;
		DataManagement.DatasetProperties2[] currProps = { props };
		CreateDatasetsResponse response = dataManagementService.createDatasets2(currProps);
		if (response.serviceData.sizeOfPartialErrors() > 0) {
			int errorMsg = response.serviceData.getPartialError(0).getErrorValues()[0].getCode();
			if (errorMsg == 515239) {
				log.error("【error】 用户infodba被锁死，请联系管理员！");
			}
			return null;
		}
		Dataset dataset = response.output[0].dataset;
		return dataset;
	}
	
}
