package com.foxconn.plm.tcapi.utils;

import com.teamcenter.services.strong.query._2006_03.SavedQuery.GetSavedQueriesResponse;
import com.foxconn.plm.tcapi.constants.ModelObjectConstant;
import com.foxconn.plm.tcapi.constants.PersonPropConstant;
import com.foxconn.plm.tcapi.constants.TCSearchEnum;
import com.foxconn.plm.tcapi.constants.UserPropConstant;
import com.foxconn.plm.tcapi.service.TCSOAServiceFactory;
import com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException;
import com.teamcenter.services.loose.core.SessionService;
import com.teamcenter.services.strong.core.DataManagementService;
import com.teamcenter.services.strong.core.ReservationService;
import com.teamcenter.services.strong.query.SavedQueryService;
import com.teamcenter.soa.client.Connection;
import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.client.model.ServiceData;
import com.teamcenter.soa.client.model.strong.ImanQuery;
import com.teamcenter.soa.client.model.strong.ItemRevision;
import com.teamcenter.soa.client.model.strong.Person;
import com.teamcenter.services.strong.core._2007_01.Session;
import com.teamcenter.services.strong.core._2007_01.Session.MultiPreferencesResponse;
import com.teamcenter.services.strong.core._2006_03.DataManagement.ObjectOwner;
import com.teamcenter.soa.client.model.strong.Group;
import com.teamcenter.soa.client.model.strong.Item;
import com.teamcenter.soa.client.model.strong.User;
import com.teamcenter.soa.client.model.strong.WorkspaceObject;
import com.teamcenter.soa.exceptions.NotLoadedException;
import com.teamcenter.services.strong.query._2006_03.SavedQuery.DescribeSavedQueriesResponse;
import com.teamcenter.services.strong.query._2006_03.SavedQuery.SavedQueryFieldObject;
import com.teamcenter.services.strong.query._2007_09.SavedQuery.QueryResults;
import com.teamcenter.services.strong.query._2007_09.SavedQuery.SavedQueriesResponse;
import com.teamcenter.services.strong.query._2008_06.SavedQuery.QueryInput;
import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.teamcenter.services.strong.core._2010_09.DataManagement.PropInfo;
import com.teamcenter.services.strong.core._2010_09.DataManagement.SetPropertyResponse;
import com.teamcenter.services.strong.core._2010_09.DataManagement.NameValueStruct1;

/**
 * @author 作者 Administrator 
 * @version 创建时间：2021年12月18日 下午8:52:54 Description: TC公共工具类
 */
public class TCPublicUtils {

	private static final Logger log = LoggerFactory.getLogger(TCPublicUtils.class);
	
	public static ModelObject findObjectByUID(DataManagementService dmService, String uid) {
		ServiceData sd = dmService.loadObjects(new String[] { uid });
		if (sd != null && sd.sizeOfPlainObjects() != 0) {
			return sd.getPlainObject(sd.sizeOfPlainObjects() - 1);
		}
		return null;
	}

	/**
	 * 加载属性
	 * 
	 * @param dmService
	 * @param object
	 * @param propName
	 */
	public static void getProperty(DataManagementService dmService, ModelObject object, String propName) {
		ModelObject[] objects = { object };
		String[] atts = { propName };
		dmService.getProperties(objects, atts);
	}

	/**
	 * 加载属性
	 * 
	 * @param dmService
	 * @param object
	 * @param propName
	 */
	public static void getProperty(DataManagementService dmService, ModelObject[] objects, String[] atts) {		
		dmService.getProperties(objects, atts);
	}

	/**
	 * 返回某个对象多值属性的值(对象数组)
	 * 
	 * @param object
	 * @param propNames
	 * @return
	 * @throws NotLoadedException
	 */
	public static ModelObject[] getPropModelObjectArray(DataManagementService dmService, ModelObject object,
			String propName) throws NotLoadedException {
		getProperty(dmService, object, propName);
		return object.getPropertyObject(propName).getModelObjectArrayValue();
	}

	/**
	 * 返回某个对象单值属性的值(对象)
	 * 
	 * @param dmService
	 * @param object
	 * @param propName
	 * @return
	 * @throws NotLoadedException
	 */
	public static ModelObject getPropModelObject(DataManagementService dmService, ModelObject object, String propName)
			throws NotLoadedException {
		getProperty(dmService, object, propName);
		return object.getPropertyObject(propName).getModelObjectValue();
	}	

	/**
	 * 刷新对象
	 * 
	 * @param dmService
	 * @param object
	 */
	public static void refreshObject(DataManagementService dmService, ModelObject[] objects) {
		dmService.refreshObjects(objects);	
	}

	/**
	 * 刷新对象
	 * 
	 * @param dmService
	 * @param object
	 */
	public static void refreshObject(DataManagementService dmService, ModelObject object) {
		dmService.refreshObjects(new ModelObject[] {object});
	}
	
	
	/**
	 * 返回某个对象属性单值(boolean)
	 * @param dmService
	 * @param object
	 * @param propName
	 * @return
	 * @throws NotLoadedException
	 */
	public static boolean getPropBoolean(DataManagementService dmService, ModelObject object, String propName) throws NotLoadedException {
		getProperty(dmService, object, propName);
		return object.getPropertyObject(propName).getBoolValue();
	}
	
	
	/**
	 * 返回某个对象属性单值(string)
	 * 
	 * @param dmService
	 * @param object
	 * @param propName
	 * @return
	 * @throws NotLoadedException
	 */
	public static String getPropStr(DataManagementService dmService, ModelObject object, String propName)
			throws NotLoadedException {
		getProperty(dmService, object, propName);
		return object.getPropertyObject(propName).getStringValue();
	}

	/**
	 * 返回某个对象属性单值(string[])
	 * 
	 * @param dmService
	 * @param object
	 * @param propName
	 * @return
	 * @throws NotLoadedException
	 */
	public static String[] getPropStrArray(DataManagementService dmService, ModelObject object, String propName)
			throws NotLoadedException {
		getProperty(dmService, object, propName);
		return object.getPropertyObject(propName).getStringArrayValue();
	}

	/**
	 * 获取对象/对象版本类型映射
	 * 
	 * @param objects
	 * @return
	 */
	public static Map<ModelObject, String> getModelObjectTypeMapp(ModelObject[] objects) {
		Map<ModelObject, String> map = new LinkedHashMap<ModelObject, String>();
		for (ModelObject obj : objects) {
			String objectType = obj.getTypeObject().getName();
			map.put(obj, objectType);
		}
		return map;
	}

	/**
	 * 修改对象属性值
	 * 
	 * @param datamanagementservice 工具类
	 * @param model                 对象
	 * @param propertyname          属性名
	 * @param propertyvalue         属性值
	 * @return
	 */
	public static Boolean setProperties(DataManagementService datamanagementservice, ModelObject model,
			String[] propertyname, String[][] propertyvalue) {
		if (model == null) {
			return false;
		}

		if (propertyname.length != propertyvalue.length) {
			return false;
		}
		try {
			PropInfo[] apropinfo = new PropInfo[1];
			PropInfo propinfo = new PropInfo();
			propinfo.object = model;
			NameValueStruct1 anamevaluestruct1[] = new NameValueStruct1[propertyname.length];
			for (int j = 0; j < propertyname.length; j++) {
				anamevaluestruct1[j] = new NameValueStruct1();
				anamevaluestruct1[j].name = propertyname[j];
				anamevaluestruct1[j].values = propertyvalue[j];
//				anamevaluestruct1[j].values = new String[] {"0"};
			}
			propinfo.vecNameVal = anamevaluestruct1;
			apropinfo[0] = propinfo;
			String[] as = { "ENABLE_PSE_BULLETIN_BOARD" };
			SetPropertyResponse setpropertyresponse = datamanagementservice.setProperties(apropinfo, as);
			if (setpropertyresponse.data.sizeOfPartialErrors() > 0) {
				throw new ServiceException("DataManagementService.createFroms returned a partial error");
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();			
			log.error(CommonTools.getExceptionMsg(e));
		}
		return false;
	}

	/**
	 * 返回用户的邮箱
	 * @param savedQueryService
	 * @param datamanagementservice
	 * @param map
	 * @param userId
	 * @return 
	 * @return 
	 * @throws NotLoadedException
	 */
	public static Map<String, String> getEmail(SavedQueryService savedQueryService, DataManagementService datamanagementservice, List<String> list) {
		Map<String, String> emailMap = new LinkedHashMap<String, String>();		
		list.forEach(userId -> {
			try {
				ModelObject[] userObjects = null;
				User user = null;
				Person person = null;
				userObjects = executequery(savedQueryService, datamanagementservice, TCSearchEnum.__WEB_FIND_USER.queryName(), TCSearchEnum.__WEB_FIND_USER.queryParams(), new String[] { userId });
				if (CommonTools.isEmpty(userObjects)) { // 判断是否为空					
					return; // 跳出一次循环然后继续下一次循环
				}
				user = (User) userObjects[0];
				// 获取person对象
				person = (Person)getPropModelObject(datamanagementservice, user, UserPropConstant.PERSON);
				// 获取邮箱
				String email = getPropStr(datamanagementservice, person, PersonPropConstant.PA9);		
				if (CommonTools.isEmpty(email)) {
					log.info("【ERROR】 账号为: " + userId + ", 获取邮箱失败...");
					return; // 跳出一次循环然后继续下一次循环
				}
				emailMap.put(userId, email);				
			} catch (Exception e) {
				e.printStackTrace();
				log.error(CommonTools.getExceptionMsg(e));
			}
		});	
		return emailMap;
	}
	
	
	/**
	 * 查询方法，返回ModelObject对象
	 *
	 * @param queryname             查询明智
	 * @param entries               查询列名
	 * @param values                查询值
	 * @param connetion             连接
	 * @param datamanagementservice 工具类
	 * @return
	 */
	public static ModelObject[] executequery(SavedQueryService queryService, DataManagementService datamanagementservice, String queryname, String[] entries, String[] values) {
		ImanQuery query = null;
		try {
			GetSavedQueriesResponse savedQueries = queryService.getSavedQueries();
			if (savedQueries.queries.length == 0) {
				log.info("【ERROR】 There are no saved queries in the system.");
				return null;
			}
			for (int i = 0; i < savedQueries.queries.length; i++) {
				if (savedQueries.queries[i].name.equals(queryname)) {
					query = savedQueries.queries[i].query;
					break;
				}
			}
		} catch (ServiceException e) {
			log.info("【ERROR】 GetSavedQueries service request failed.");
			log.error(CommonTools.getExceptionMsg(e));
			return null;
		}
		if (query == null) {
			log.info("【ERROR】 There is not an 'Item Name' query.");
			return null;
		}

		DescribeSavedQueriesResponse descResp = queryService.describeSavedQueries(new ImanQuery[] { query });
		SavedQueryFieldObject[] queryFields = descResp.fieldLists[0].fields;
		for (int i = 0; i < queryFields.length; i++) {
			System.out.println(queryFields[i].entryName);
		}

		try {
			QueryInput[] savedQueryInput = new QueryInput[1];
			savedQueryInput[0] = new QueryInput();
			savedQueryInput[0].query = query;
			savedQueryInput[0].maxNumToReturn = 9999;
			savedQueryInput[0].limitList = new ModelObject[0];
			savedQueryInput[0].entries = entries;
			savedQueryInput[0].values = values;

			SavedQueriesResponse savedQueryResult = queryService.executeSavedQueries(savedQueryInput);
			QueryResults found = savedQueryResult.arrayOfResults[0];

			System.out.println("Found Items:");

			String[] uids = new String[found.objectUIDS.length];
			for (int i = 0; i < found.objectUIDS.length; i++) {

				uids[i] = found.objectUIDS[i];

			}
			if (uids == null || uids.length == 0) {
				return null;
			}
			ServiceData sd = datamanagementservice.loadObjects(uids);
			ModelObject[] foundObjs = new ModelObject[sd.sizeOfPlainObjects()];
			for (int k = 0; k < sd.sizeOfPlainObjects(); k++) {
				foundObjs[k] = (ModelObject) sd.getPlainObject(k);
			}
			return foundObjs;
		} catch (Exception e) {
			log.error("【ERROR】 ExecuteSavedQuery service request failed.");
			log.error(CommonTools.getExceptionMsg(e));
			return null;
		}
	}

	/**
	 * 更改对象所有权
	 *
	 * @param datamanagementservice 工具类
	 * @param obj                   对象
	 * @param user                  用户
	 * @param group                 组
	 * @return
	 */
	private static Boolean changeOwner(DataManagementService datamanagementservice, ModelObject obj, User user,
			Group group) {
		try {
			ObjectOwner[] owners = new ObjectOwner[1];
			owners[0] = new ObjectOwner();
			owners[0].group = group;
			owners[0].owner = user;
			owners[0].object = obj;
			ServiceData data = datamanagementservice.changeOwnership(owners);
			if (data.sizeOfPartialErrors() > 0) {
				throw new ServiceException("DataManagementService changeOwner returned a partial error");
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(CommonTools.getExceptionMsg(e));
			log.error("【ERROR】 更改所有权失败，请联系管理员！");
		}
		return false;
	}

	/**
	 * 获取对象的所有者
	 * 
	 * @param datamanagementservice
	 * @param objects
	 * @return
	 */
	public static String getOwnUser(DataManagementService datamanagementservice, ModelObject obj) {
		try {
			User owner = (User) getPropModelObject(datamanagementservice, obj, ModelObjectConstant.OWNING_USER);
			String owningUserId = getPropStr(datamanagementservice, owner, UserPropConstant.USER_ID);
			String owningUserName = getPropStr(datamanagementservice, owner, UserPropConstant.USER_NAME);
			System.out.println("【INFO】 owningId is: " + owningUserId);
			System.out.println("【INFO】 owningName is: " + owningUserName);
			return owningUserId;
		} catch (Exception e) {
			e.printStackTrace();			
			log.error(CommonTools.getExceptionMsg(e));
			log.error("【ERROR】 获取对象所有者失败！");
		}
		return null;
	}

	/**
	 * 更改所有权
	 *
	 * @param dataManagementService 工具类
	 * @param obj                   对象
	 * @param user                  用户
	 * @param group                 组
	 * @return
	 */
	public static Boolean changeOwnShip(DataManagementService dataManagementService, ModelObject obj, User user,
			Group group) {
		Boolean check = null;
		try {
			check = changeOwner(dataManagementService, obj, user, group);
			if (!check) {
				return false;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(CommonTools.getExceptionMsg(e));
			log.error("【ERROR】 对象/对象版本版本的所有权更改失败，请联系管理员！");
		}
		return false;
	}

	/**
	 * 获取TC首选项
	 *
	 * @param connection 连接
	 * @param preferName 首相向名称
	 * @param site 首选项级别
	 * @return 首选项值
	 */
	public static List<String> getTcPreference(Connection connection, String preferName, String site) {
		try {
			com.teamcenter.services.strong.core.SessionService sessionService = com.teamcenter.services.strong.core.SessionService
					.getService(connection);
			Session.ScopedPreferenceNames[] arrayOfScopedPreferenceNames = new Session.ScopedPreferenceNames[1];
			arrayOfScopedPreferenceNames[0] = new Session.ScopedPreferenceNames();
			arrayOfScopedPreferenceNames[0].names = new String[] { preferName };
			arrayOfScopedPreferenceNames[0].scope = site;
			MultiPreferencesResponse localMultiPreferencesResponse = sessionService
					.getPreferences(arrayOfScopedPreferenceNames);
			String[] preferences = localMultiPreferencesResponse.preferences[0].values;
			if (CommonTools.isNotEmpty(preferences)) { // 不为空
				return Arrays.asList(preferences);
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(CommonTools.getExceptionMsg(e));
			log.error("【ERROR】 " + preferName + "首选项查询失败...");			
		}
		return null;
	}

	/**
	 * 复制文件
	 *
	 * @param sourceFile 源文件
	 * @param targetFile 目标文件
	 * @throws IOException
	 */
	public static void copyFile(File sourceFile, File targetFile) throws IOException {
		BufferedInputStream inBuff = null;
		BufferedOutputStream outBuff = null;

		try {
			inBuff = new BufferedInputStream(new FileInputStream(sourceFile));
			outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = inBuff.read(b)) != -1) {
				outBuff.write(b, 0, len);
			}

			outBuff.flush();
		} finally {
			if (inBuff != null) {
				inBuff.close();
			}
			if (outBuff != null) {
				outBuff.close();
			}
		}
	}

	/**
	 * 开启旁路
	 *
	 * @param flag
	 * @throws ServiceException
	 */
	public static void byPass(SessionService sessionservice, boolean flag) throws ServiceException {
		com.teamcenter.services.loose.core._2007_12.Session.StateNameValue stateNameValues[] = new com.teamcenter.services.loose.core._2007_12.Session.StateNameValue[1];
		stateNameValues[0] = new com.teamcenter.services.loose.core._2007_12.Session.StateNameValue();
		stateNameValues[0].name = "bypassFlag";
		stateNameValues[0].value = toBooleanString(flag);
		ServiceData servicedata = sessionservice.setUserSessionState(stateNameValues);
		if (servicedata.sizeOfPartialErrors() > 0) {
			throw new ServiceException("SessionService setbypass returned a partial error.");
		} else {
			return;
		}

	}

	public static String toBooleanString(boolean flag) {
		return flag ? "1" : "0";
	}

	
	/**
	 * 签入(假如已经签出, 则签入, 否则不予处理)
	 * @param dataManagementService
	 * @param object
	 * @param connetion
	 * @throws NotLoadedException
	 * @throws ServiceException
	 */
	public static void checkin(DataManagementService dataManagementService, Connection connection, ModelObject object)
			throws NotLoadedException, ServiceException {
		// 判断是否已经被签出
		dataManagementService.refreshObjects(new ModelObject[] { object });
		dataManagementService.getProperties(new ModelObject[] { object }, new String[] { "checked_out" });
		// 是否签出的标志 Y带包已经签出, ""代表已经签入
		String checkedOut = object.getPropertyObject("checked_out").getStringValue().trim();
		// 无需重复签入
		if ("".equals(checkedOut)) {
			return;
		}
		ReservationService rs = ReservationService.getService(connection);
		ModelObject[] objects = new ModelObject[1];
        objects[0] = object;
        ServiceData servicedata = rs.checkin(objects);
        if (servicedata.sizeOfPartialErrors() > 0) {
            throw new ServiceException("ReservationService checkin returned a partial error.");
        }
        return;
	}
	
	/**
	 * 签出(假如已经签出, 则先签入, 然后签出)
	 * @param dataManagementService 
	 * @param object
	 * @param connection
	 * @return
	 * @throws NotLoadedException
	 * @throws ServiceException
	 */
	public static ModelObject checkout(DataManagementService dataManagementService, Connection connection, ModelObject object) throws NotLoadedException, ServiceException {
		ModelObject checkoutobject = null;
		//判断是否已经被签出
        dataManagementService.refreshObjects(new ModelObject[]{object});
        dataManagementService.getProperties(new ModelObject[]{object}, new String[]{"checked_out"});
        //是否签出的标志 Y带包已经签出, ""代表已经签入
        String checkedOut = object.getPropertyObject("checked_out").getStringValue().trim();
      //如果已经签出, 如果已经签出, 则先签入, 然后再进行签出
        if ("Y".equals(checkedOut)) {
            checkin(dataManagementService, connection, object);
        }
        ReservationService rs = ReservationService.getService(connection);
        ModelObject[] objects = new ModelObject[1];
        objects[0] = object;
        ServiceData servicedata = rs.checkout(objects, "ImportData", "");
        if (servicedata.sizeOfPartialErrors() > 0) {
            throw new ServiceException("ReservationService checkout returned a partial error.");
        }
        checkoutobject = servicedata.getUpdatedObject(0);
        return checkoutobject;        
	}
}
