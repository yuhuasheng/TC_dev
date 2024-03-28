package com.foxconn.plm.tcapi.startup;

import java.net.URL;
import java.net.URLConnection;
import com.foxconn.plm.tcapi.soa.client.AppXSession;
import com.foxconn.plm.tcapi.utils.CommonTools;
import com.teamcenter.soa.client.model.strong.User;

/**
* @author 作者 Administrator
* @version 创建时间：2021年12月18日 下午8:52:54
* Description: TC登录类
*/
public class TCStartUp {

	private static AppXSession session = null;

	private static User user = null;

	private boolean isLogin = false;

	private String tc_IP = ""; // TC服务器ip

	private String tc_USERNAME = ""; // TC登录用户名

	private String tc_PASSWORD = ""; // TC登录密码

	public TCStartUp(String tc_IP, String tc_USERNAME, String tc_PASSWORD) {		
		this.tc_IP = tc_IP;
		this.tc_USERNAME = tc_USERNAME;
		this.tc_PASSWORD = tc_PASSWORD;
		// 连接TC
		connectTC();
	}

	/**
	 *  连接TC
	 */
	private void connectTC() {
		System.out.println("【INFO】登录TC系统中...");
		// 1、判断网页是否连通
		try {
			URL url = new URL(tc_IP);
			URLConnection in = url.openConnection();
			in.connect();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("【ERROR】 登录TC系统失败，TC12连接地址:" + tc_IP + ", 链接失败，请联系管理员！");
			try {
				throw new Exception("【ERROR】登录TC系统失败，TC12连接地址:" + tc_IP + ", 链接失败，请联系管理员！");
			} catch (Exception e1) {
				e1.printStackTrace();
				System.out.println(e1.getStackTrace());
			}
		}
		session = new AppXSession(tc_IP);
		user = session.mylogin(tc_USERNAME, tc_PASSWORD, "", "");
		if (null == user) {
			System.out.println("【ERROR】 登录TC系统失败，请联系管理员！");
			try {
				throw new Exception("【ERROR】 登录TC系统失败，请联系管理员！");
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println(CommonTools.getExceptionMsg(e));
			}
		} else {
			isLogin = true;
			System.out.println("【INFO】 通过SOA登录TC系统完毕....");
		}
	}
	
	/**
	 * 登出TC系统
	 */
	public void disconnectTC() {
		System.out.println("【INFO】 登出TC系统成功...");
		session.logout();
	}
	
	public boolean isLogin() {
		return isLogin;
	}	
}
