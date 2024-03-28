package com.foxconn.update.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.foxconn.plm.tcapi.utils.HttpUtil;

/**
* @author infodba
* @version 创建时间：2021年12月27日 下午5:17:59
* @Description
*/
public class Test {
	
	public static void main(String[] args) {
		HashMap httpmap = new HashMap();
//		httpmap.put("requestPath", "http://10.203.163.42:");
//		httpmap.put("ruleName", "8888/teamcenter/sendMail");
		httpmap.put("requestPath", "http://localhost:");
		httpmap.put("ruleName", "8888/teamcenter/sendMail");
		httpmap.put("to","hua-sheng.yu@foxconn.com");
		httpmap.put("user", "cmm-it-plm@mail.foxconn.com");
		httpmap.put("server", "10.134.28.97:25");
		httpmap.put("subject", "电子原理图测试");
		httpmap.put("body", "T:\\EE-0101-00001_02_body.txt");
		httpmap.put("attachments", "T:\\EE-0101-00001_18.52.txt");
		String jsons = JSONArray.toJSONString(httpmap);
		try {
			String result = post("http://localhost:8888/teamcenter/sendMail5", jsons);
			System.out.println("==>> result: " + result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		String result = HttpUtil.httpPost(httpmap);
//		if ("".equals(result)) {
//			System.out.println("请求失败");
//		}
	}
	
	
	 public static String post(String actionUrl, String params) throws IOException
	    {
	        String serverURL = actionUrl;
	        StringBuffer sbf = new StringBuffer();
	        String strRead = null;
	        URL url = new URL(serverURL);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setRequestMethod("POST");// 鐠囬攱鐪皃ost閺傜懓绱�
	        connection.setDoInput(true);
	        connection.setDoOutput(true);
	        // header閸愬懐娈戦惃鍕棘閺佹澘婀潻娆撳櫡set
	        // connection.setRequestProperty("key", "value");
	        connection.setRequestProperty("Content-Type", "application/json");
	        connection.connect();
	        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
	        // body閸欏倹鏆熼弨鎹愮箹闁诧拷
	        writer.write(params);
	        writer.flush();
	        InputStream is = connection.getInputStream();
	        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
	        while ((strRead = reader.readLine()) != null)
	        {
	            sbf.append(strRead);
	            sbf.append("\r\n");
	        }
	        reader.close();
	        connection.disconnect();
	        String results = sbf.toString();
	        return results;
	    }
	 
	 
}
