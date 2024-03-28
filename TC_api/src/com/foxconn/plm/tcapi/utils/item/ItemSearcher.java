package com.foxconn.plm.tcapi.utils.item;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.foxconn.plm.tcapi.service.TCSOAServiceFactory;
import com.foxconn.plm.tcapi.utils.CommonTools;
import com.foxconn.plm.tcapi.utils.TCPublicUtils;
import com.teamcenter.services.strong.core.DataManagementService;
import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.client.model.strong.ItemRevision;
import com.teamcenter.soa.exceptions.NotLoadedException;

/**
* @author infodba
* @version 创建时间：2022年1月24日 下午4:12:27
* @Description
*/
public class ItemSearcher {
	
	/**
	 * 获取对象版本UID
	 * @param itemRevUid
	 * @return
	 */
	public ItemRevision getItemRev(String itemRevUid) {
		DataManagementService ds = TCSOAServiceFactory.getDataManagementService();
		ModelObject modelObject = TCPublicUtils.findObjectByUID(ds, itemRevUid);
		return (ItemRevision) modelObject;
	}
	
	
	/**
	 * 获取对象的所有者
	 * @param revisionList 对象版本集合
	 * @return
	 * @throws NotLoadedException
	 */
	public Map<ModelObject, String> getModelObjectOwner(DataManagementService dataManagementServices, List<ModelObject> list) throws NotLoadedException {
		Map<ModelObject, String> ownMap = new LinkedHashMap<ModelObject, String>();		
		list.parallelStream().forEach(obj -> {
			String ownUser = TCPublicUtils.getOwnUser(dataManagementServices, obj);
			if (CommonTools.isNotEmpty(ownUser)) {				
				ownMap.put(obj, ownUser);
			}
		});
		return ownMap;
	}
	
}
