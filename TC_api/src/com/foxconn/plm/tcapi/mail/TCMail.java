package com.foxconn.plm.tcapi.mail;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.foxconn.plm.tcapi.utils.CommonTools;
import com.foxconn.plm.tcapi.utils.HttpUtil;

/**
 * @author infodba
 * @version 创建时间：2021年12月23日 上午11:17:59
 * @Description 发送邮件类
 */
public class TCMail {


	/**
	 * 生成发送邮件需要的文件，并写入信息
	 * 
	 * @param errList
	 * @param dir
	 * @return
	 */
	public static String generateMailFile(List<String> list, String dir, String fileName) {
		// 生成txt文件
		String filePath = TCMail.generateTxtFile(dir, fileName);
		// 记录属性更改的错误记录
		TCMail.recordContent(filePath, list);
		return filePath;
	}

	/**
	 * 生成TXT文件
	 * 
	 * @param filePath 文件夹路径
	 * @param fileName 文件名
	 * @return
	 * @throws IOException
	 */
	public static String generateTxtFile(String dir, String fileName) {
		try {
			String txtFilePath = dir + "\\" + fileName + ".txt";
			File file = new File(txtFilePath);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			return file.getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(CommonTools.getExceptionMsg(e));
			return null;
		}
	}

	/**
	 * 记录邮件正文
	 * 
	 * @param filePath 文件路径
	 * @param content  正文内容
	 * @param itemId   ID
	 * @param version  版本号
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	public static void recordContent(String filePath, List<String> contentList) {
		System.out.println("==========start writing txtFile==========");
		for (String content : contentList) {
			// 将内容写入到文本中
			CommonTools.write2(filePath, content);
		}
		System.out.println("==========ending writing txtFile==========");
	}

	/**
	 * 生成邮件接收者的邮箱
	 * 
	 * @param emailMap 邮箱集合
	 * @return
	 */
	public static String generateToUser(Map<String, String> emailMap) {		
		if (CommonTools.isEmpty(emailMap)) {
			return null;
		}
		String str = "";
		for (String value : emailMap.values()) {			
			str += value + ",";
		}
		return str;
	}

	/**
	 * 获取管理员邮箱
	 * 
	 * @param str
	 * @return
	 */
	public static String getAdminEmail(String str) {
		String[] split = str.split(";");
		String email = "";
		for (String value : split) {
			email += value + "&&";
		}
		return email.substring(0, email.lastIndexOf("&&"));
	}

	/**
	 * 发送邮件
	 * 
	 * @param tc_mail_exe_path ootb发送邮件EXE
	 * @param subject          主题
	 * @param server           服务
	 * @param toUser           接收者
	 * @param fromUser         发送者
	 * @param body             正文
	 * @param attachments      附件
	 * @return
	 */
	public static Boolean sendMail(String tc_mail_exe_path, String subject, String server, String to, String user,
			String body, String attachments) {
		try {
			String[] split = to.split("&&");
			String toUser = "";
			for (String str : split) {
				toUser += "-to=" + str + " ";
			}

			System.out.println("【INFO】 toUser: " + toUser);
			String command = null;
			if (CommonTools.isEmpty(attachments)) {
				command = tc_mail_exe_path + " " + "-to=" + toUser + "-user=" + user + " " + "-server=" + server + " "
						+ "-subject=" + subject + " " + "-body=" + body;
			} else {
				command = tc_mail_exe_path + " " + toUser + "-user=" + user + " " + "-server=" + server + " "
						+ "-subject=" + subject + " " + "-body=" + body + " " + "-attachments=" + attachments;
			}
			System.out.println("【INFO】 command: " + command);
			System.out.println("【INFO】 开始发送邮件...");
			Process process = Runtime.getRuntime().exec(command);
			process.waitFor();
			System.out.println("【INFO】 发送邮件结束...");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(CommonTools.getExceptionMsg(e));
			return false;
		}
	}

	/**
	 * 发送邮件(包含附件)
	 * 
	 * @param mailMap 邮件基本参数
	 * @param attachmentList 附件参数
	 * @return
	 */
	public static String sendMail(HashMap<String, String> mailMap, List<String> attachmentList) {
		return HttpUtil.httpPost(mailMap, attachmentList);
	}
	
	/**
	 * 发送邮件(不包含附件)
	 * @param mailMap
	 * @return
	 */
	public static String sendMail(HashMap<String, String> mailMap) {
		return HttpUtil.httpPost(mailMap);
	}
	
	
}
