package com.foxconn.plm.tcapi.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.foxconn.plm.tcapi.service.TCSOAServiceFactory;
import com.foxconn.plm.tcapi.startup.TCStartUp;
import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.client.model.strong.ItemRevision;

public class Test {
	public static void main(String[] args) {
//		 String[] properties = new String[]{"d8_mark1", "d8_mark2", "d8_mark3", "d8_mark4"};
//	     String[][] propertyvalue = new String[4][1];
//	     //项目名称
//         propertyvalue[0][0] = "1";
//         //图号
//         propertyvalue[1][0] = "2";
//         //变更类型
//         propertyvalue[2][0] = "3";
//         //eip流程链接
//         propertyvalue[3][0] = "4";
//         List<Object> resultList = new ArrayList<Object>();
//         resultList.add(properties);
// 		resultList.add(propertyvalue);
// 		System.out.println((String[])resultList.get(0));
// 		System.out.println((String[][])resultList.get(1));
// 		System.out.println(123);
//         String result = JSON.toJSON(properties).toString() + "=" + JSON.toJSON(propertyvalue).toString();
//         System.out.println(result);
////		
//       //字符串转换成一维数组
//		String[] propertiess = JSON.parseObject(result.split("=")[0], String[].class);
//		String[][] propertyvalues = JSON.parseObject(result.split("=")[1], String[][].class);
//		System.out.println(123);	
		TCStartUp startUp = new TCStartUp("http://localhost:7001/tc", "infodba", "infodba");
		String uid = "REpFb$2UrdoWuC";
		ModelObject object = TCPublicUtils.findObjectByUID(TCSOAServiceFactory.getDataManagementService(), uid);
		ItemRevision itemRevision = (ItemRevision) object;
		System.out.println(itemRevision.getTypeObject().getName());
		String[] propertyNames = itemRevision.getPropertyNames();
		System.out.println(object);
	}
}
