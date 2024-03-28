package com.foxconn.plm.tcapi.service;

import com.foxconn.plm.tcapi.constants.EPMTaskPropConstant;
import com.foxconn.plm.tcapi.utils.TCPublicUtils;
import com.teamcenter.services.strong.core.DataManagementService;
import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.client.model.strong.EPMTask;
import com.teamcenter.soa.client.model.strong.ItemRevision;
import com.teamcenter.soa.exceptions.NotLoadedException;

/**
* @author infodba
* @version 创建时间：2021年12月24日 下午2:11:21
* @Description
*/
public class EPMTaskService {	
	
	/**
	 * 获取流程的根任务
	 * @param itemRevUid
	 * @return
	 * @throws NotLoadedException 
	 */
	public static EPMTask getEPMTask(DataManagementService dataManagementService, ModelObject obj) throws NotLoadedException {		
		return (EPMTask) TCPublicUtils.getPropModelObject(dataManagementService, obj, EPMTaskPropConstant.ROOT_TASK);
	}
	
	/**
	 * 获取名称名称
	 * @param rootTask
	 * @return
	 * @throws NotLoadedException
	 */
	public static String getProcessName(DataManagementService dataManagementService, EPMTask rootTask) throws NotLoadedException {	
		TCPublicUtils.refreshObject(dataManagementService, rootTask);
		return TCPublicUtils.getPropStr(dataManagementService, rootTask, EPMTaskPropConstant.JOB_NAME);			
	}
	
	
	/**
	 * 返回目标文件夹下的数组
	 * @param rootTask 流程根任务
	 * @return
	 * @throws NotLoadedException
	 */
	public static ModelObject[] getRootTargetAttachments(DataManagementService dataManagementService, EPMTask rootTask) throws NotLoadedException {	
		TCPublicUtils.refreshObject(dataManagementService, rootTask);
		return TCPublicUtils.getPropModelObjectArray(dataManagementService, rootTask, EPMTaskPropConstant.ROOT_TARGET_ATTACHMENTS);	
		
	}
	
	/**
	 * 返回引用文件夹下的数组
	 * @param rootTask
	 * @return
	 * @throws NotLoadedException
	 */
	public ModelObject[] getReferenceAttachments(DataManagementService dataManagementService, EPMTask rootTask) throws NotLoadedException {	
		TCPublicUtils.refreshObject(dataManagementService, rootTask);
		return TCPublicUtils.getPropModelObjectArray(dataManagementService, rootTask, EPMTaskPropConstant.ROOT_REFERENCE_ATTACHMENTS);		
	}	
	
	
}
