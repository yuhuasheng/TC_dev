package com.foxconn.plm.tcapi.service;

import com.teamcenter.services.strong.cad.StructureManagementService;
import com.teamcenter.services.strong.core.DataManagementService;
import com.teamcenter.soa.client.FileManagementUtility;
import com.teamcenter.services.strong.administration.PreferenceManagementService;
import com.teamcenter.services.strong.structuremanagement.StructureService;
import java.io.FileNotFoundException;
import com.foxconn.plm.tcapi.soa.client.AppXSession;
import com.teamcenter.services.loose.core.SessionService;
import com.teamcenter.services.strong.query.SavedQueryService;

/**
 * @author 作者 Administrator
 * @version 创建时间：2021年12月18日 下午8:52:54 Description: TC公共类制造工厂
 */
public class TCSOAServiceFactory {

	private static StructureManagementService structureManagementService; // bom结构

	private static DataManagementService dataManagementService; // tc object

	private static FileManagementUtility fileManagementUtility; // fms

	private static PreferenceManagementService preferenceManagementService; // 首选项

	private static SessionService sessionservice;// 用户服务类

	private static SavedQueryService savedQueryService; // 搜索

	private static StructureService strucService;

	public static SavedQueryService getSavedQueryService() {
		if (savedQueryService == null) {
			savedQueryService = SavedQueryService.getService(AppXSession.getConnection());
		}
		return savedQueryService;
	}

	public static SessionService getSessionService() {
		if (sessionservice == null) {
			sessionservice = SessionService.getService(AppXSession.getConnection());
		}
		return sessionservice;
	}

	public static StructureManagementService getStructureManagementService() {
		if (structureManagementService == null) {
			structureManagementService = StructureManagementService.getService(AppXSession.getConnection());
		}
		return structureManagementService;
	}

	public static StructureService getStructureService() {
		if (null == strucService) {
			strucService = StructureService.getService(AppXSession.getConnection());
		}
		return strucService;
	}

	public static DataManagementService getDataManagementService() {
		if (dataManagementService == null) {
			dataManagementService = DataManagementService.getService(AppXSession.getConnection());
		}
		return dataManagementService;
	}

	public static FileManagementUtility getFileManagementUtility(String fmsUrl) {

		if (fileManagementUtility == null) {
			if (fmsUrl != null && fmsUrl.length() > 0) {
				try {
					fileManagementUtility = new FileManagementUtility(AppXSession.getConnection(), null, null, new String[] { fmsUrl }, null);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
			if (fileManagementUtility == null) {
				fileManagementUtility = new FileManagementUtility(AppXSession.getConnection());
			}
		}
		return fileManagementUtility;
	}

	public static PreferenceManagementService getPreferenceManagementService() {

		if (preferenceManagementService == null) {
			preferenceManagementService = PreferenceManagementService.getService(AppXSession.getConnection());
		}
		return preferenceManagementService;
	}

}
