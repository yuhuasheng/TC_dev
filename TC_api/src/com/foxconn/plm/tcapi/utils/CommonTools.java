package com.foxconn.plm.tcapi.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 作者 Administrator
 * @version 创建时间：2021年12月6日 上午10:45:00 Description: 常用工具包
 */
public class CommonTools {

	
	private static final Logger log = LoggerFactory.getLogger(TCPublicUtils.class);
	
	/**
	 * 时间格式包含毫秒
	 */
	private static final String sdfm = "yyyy-MM-dd HH:mm:ss SSS";
	/**
	 * 普通的时间格式
	 */
	private static final String sdf = "yyyy-MM-dd HH:mm:ss";
	/**
	 * 时间戳格式
	 */
	private static final String sd = "yyyyMMddHHmmss";
	/**
	 * 检查是否为整型
	 */
	private static Pattern p = Pattern.compile("^\\d+$");

	/**
	 * 判断String类型的数据是否为空 null,""," " 为true "A"为false
	 *
	 * @return boolean
	 */
	public static boolean isEmpty(String str) {
		return (null == str || str.trim().length() == 0);
	}

	/**
	 * 判断String类型的数据是否为空 null,"", " " 为false "A", 为true
	 *
	 * @return boolean
	 */
	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}

	/**
	 * 判断list类型的数据是否为空 null,[] 为 true
	 *
	 * @return boolean
	 */
	public static boolean isEmpty(List<?> list) {
		return (null == list || list.size() == 0);
	}

	/**
	 * 判断list类型的数据是否为空 null,[] 为 false
	 *
	 * @return boolean
	 */
	public static boolean isNotEmpty(List<?> list) {
		return !isEmpty(list);
	}

	/**
	 * 判断Map类型的数据是否为空 null,[] 为true
	 *
	 * @return boolean
	 */
	public static boolean isEmpty(Map<?, ?> map) {
		return (null == map || map.size() == 0);
	}

	/**
	 * 判断map类型的数据是否为空 null,[] 为 false
	 *
	 * @return boolean
	 */
	public static boolean isNotEmpty(Map<?, ?> map) {
		return !isEmpty(map);
	}

	/**
	 * 
	 * @param 数组类型是否为空null,[] 为true
	 * @return
	 */
	public static boolean isEmpty(Object[] objects) {
		return (null == objects || objects.length == 0);
	}

	/**
	 * 数组类型是否为空null,[] 为false
	 * 
	 * @param objects
	 * @return
	 */
	public static boolean isNotEmpty(Object[] objects) {
		return !isEmpty(objects);
	}

	/**
	 * 
	 * @param 对象是否为空null,null为true
	 * @return
	 */
	public static boolean isEmpty(Object objects) {
		return null == objects;
	}

	/**
	 * 对象是否为空null,null为false
	 * 
	 * @param objects
	 * @return
	 */
	public static boolean isNotEmpty(Object objects) {
		return !isEmpty(objects);
	}

	/**
	 * 判断JSONObject类型的数据是否为空 null,[] 为true
	 *
	 * @return boolean
	 */
	public static boolean isEmpty(JSONObject json) {
		return (null == json || json.size() == 0);
	}

	/**
	 * 判断json类型的数据是否为空 null,[] 为 false
	 *
	 * @return boolean
	 */
	public static boolean isNotEmpty(JSONObject json) {
		return !isEmpty(json);
	}

	/**
	 * 字符串反转 如:入参为abc，出参则为cba
	 *
	 * @param str
	 * @return
	 */
	public static String reverse(String str) {
		if (isEmpty(str)) {
			return str;
		}
		return reverse(str.substring(1)) + str.charAt(0);
	}

	/**
	 * 获取当前long类型的的时间
	 *
	 * @return long
	 */
	public static long getNowLongTime() {
		return System.currentTimeMillis();
	}

	/**
	 * long类型的时间转换成 yyyyMMddHHmmss String类型的时间
	 *
	 * @param lo long类型的时间
	 * @return
	 */
	public static String longTime2StringTime(long lo) {
		return longTime2StringTime(lo, sd);
	}

	/**
	 * long类型的时间转换成自定义时间格式
	 *
	 * @param lo     long类型的时间
	 * @param format 时间格式
	 * @return String
	 */
	public static String longTime2StringTime(long lo, String format) {
		return new SimpleDateFormat(format).format(lo);
	}

	/**
	 * String类型的时间转换成 long
	 *
	 * @param
	 * @return String
	 * @throws ParseException
	 */
	public static long stringTime2LongTime(String time, String format) throws ParseException {
		if (isEmpty(format)) {
			format = sdf;
		}
		if (isEmpty(time)) {
			time = getNowTime(format);
		}
		SimpleDateFormat sd = new SimpleDateFormat(format);
		Date date = sd.parse(time);
		return date.getTime();
	}

	/**
	 * 格式化时间
	 *
	 * @param format1 之前的 时间格式
	 * @param format2 之后的 时间格式
	 * @param time    时间
	 * @return String
	 * @throws ParseException
	 */
	public static String formatTime(String format1, String format2, String time) throws ParseException {
		SimpleDateFormat d1 = new SimpleDateFormat(format1);
		SimpleDateFormat d2 = new SimpleDateFormat(format2);
		time = d2.format(d1.parse(time));
		return time;
	}

	/**
	 * 时间补全 例如将2018-04-04补全为2018-04-04 00:00:00.000
	 *
	 * @param time 补全的时间
	 * @return
	 */
	public static String complementTime(String time) {
		return complementTime(time, sdfm, 1);

	}

	/**
	 * 时间补全 例如将2018-04-04补全为2018-04-04 00:00:00.000
	 *
	 * @param time   补全的时间
	 * @param format 补全的格式
	 * @param type   类型 1:起始;2:终止
	 * @return
	 */
	public static String complementTime(String time, String format, int type) {
		if (isEmpty(time) || isEmpty(format)) {
			return null;
		}
		int tlen = time.length();
		int flen = format.length();
		int clen = flen - tlen;
		if (clen <= 0) {
			return time;
		}
		StringBuffer sb = new StringBuffer(time);
		if (clen == 4) {
			if (type == 1) {
				sb.append(".000");
			} else {
				sb.append(".999");
			}
		} else if (clen == 9) {
			if (type == 1) {
				sb.append(" 00:00:00");
			} else {
				sb.append(" 23:59:59");
			}
		} else if (clen == 13) {
			if (type == 1) {
				sb.append(" 00:00:00.000");
			} else {
				sb.append(" 23:59:59.999");
			}
		}
		return sb.toString();

	}

	/**
	 * 获取当前String类型的的时间 使用默认格式 yyyy-MM-dd HH:mm:ss
	 *
	 * @return String
	 */
	public static String getNowTime() {
		return getNowTime(sdf);
	}

	public static String getNowTime2() {
		return getNowTime(sd);		
	}
	
	
	public static String getCurrentTime() {
		return getNowTime(sd);
	}

	/**
	 * 获取当前String类型的的时间(自定义格式)
	 *
	 * @param format 时间格式
	 * @return String
	 */
	public static String getNowTime(String format) {
		return new SimpleDateFormat(format).format(new Date());
	}

	/**
	 * 获取当前Timestamp类型的的时间
	 *
	 * @return Timestamp
	 */
	public static Timestamp getTNowTime() {
		return new Timestamp(getNowLongTime());
	}

	/**
	 * 获取的String类型的当前时间并更改时间
	 *
	 * @param number 要更改的的数值
	 * @param format 更改时间的格式 如yyyy-MM-dd HH:mm:ss
	 * @param type   更改时间的类型 时:h; 分:m ;秒:s
	 * @return String
	 */
	public static String changeTime(int number, String format, String type) {
		return changeTime(number, format, type, "");
	}

	/**
	 * 获取的String类型时间并更改时间
	 *
	 * @param number 要更改的的数值
	 * @param format 更改时间的格式
	 * @param type   更改时间的类型 。时:h; 分:m ;秒:s
	 * @param time   更改的时间 没有则取当前时间
	 * @return String
	 */
	public static String changeTime(int number, String format, String type, String time) {
		if (isEmpty(time)) { // 如果没有设置时间则取当前时间
			time = getNowTime(format);
		}
		SimpleDateFormat format1 = new SimpleDateFormat(format);
		Date d = null;
		Calendar ca = null;
		String backTime = null;
		try {
			d = format1.parse(time);
			ca = Calendar.getInstance(); // 定义一个Calendar 对象
			ca.setTime(d);// 设置时间
			if ("h".equals(type)) {
				ca.add(Calendar.HOUR, number);// 改变时
			} else if ("m".equals(type)) {
				ca.add(Calendar.MINUTE, number);// 改变分
			} else if ("s".equals(type)) {
				ca.add(Calendar.SECOND, number);// 改变秒
			}
			backTime = format1.format(ca.getTime()); // 转化为String 的格式
		} catch (Exception e) {
			e.printStackTrace();
		}
		return backTime;
	}

	/**
	 * 两个日期带时间比较 第二个时间大于第一个则为true，否则为false
	 *
	 * @param
	 * @return boolean
	 * @throws ParseException
	 */
	public static boolean isCompareDay(String time1, String time2, String format) {
		if (isEmpty(format)) {// 如果没有设置格式使用默认格式
			format = sdf;
		}
		SimpleDateFormat s1 = new SimpleDateFormat(format);
		Date t1 = null;
		Date t2 = null;
		try {
			t1 = s1.parse(time1);
			t2 = s1.parse(time2);
			return t2.after(t1);// 当 t2 大于 t1 时，为 true，否则为 false
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 获取几天之前的时间
	 *
	 * @param day
	 * @return
	 * @since 1.8
	 */
	public static String getMinusDays(int day) {
		return getMinusDays(day, sdf);
	}

	/**
	 * 获取几天之前的时间
	 *
	 * @param day
	 * @param format
	 * @return
	 * @since 1.8
	 */
	public static String getMinusDays(int day, String format) {
		return LocalDateTime.now().minusDays(day).format(DateTimeFormatter.ofPattern(format));
	}

	/**
	 * 获取几天之后的时间
	 *
	 * @param day
	 * @return
	 * @since 1.8
	 */
	public static String getPlusDays(int day) {
		return getPlusDays(day, sdf);
	}

	/**
	 * 获取几天之后的时间
	 *
	 * @param day
	 * @param format
	 * @return
	 * @since 1.8
	 */
	public static String getPlusDays(int day, String format) {
		return LocalDateTime.now().plusDays(day).format(DateTimeFormatter.ofPattern(format));
	}

	/**
	 * 获取几天之后的时间
	 *
	 * @param
	 * @return
	 * @since 1.8
	 */
	public static String getPlusMonths(int month) {
		return getPlusMonths(month, sdf);
	}

	/**
	 * 获取几月之后的时间
	 *
	 * @param
	 * @param format
	 * @return
	 * @since 1.8
	 */
	public static String getPlusMonths(int month, String format) {
		return LocalDateTime.now().plusMonths(month).format(DateTimeFormatter.ofPattern(format));
	}

	/**
	 * 增加月份
	 *
	 * @param time  格式为yyyy-MM-dd
	 * @param month 增加月份
	 * @return
	 */
	public static String addPlusMonths(String time, int month) {
		return LocalDate.parse(time).plusMonths(month).toString();
	}

	/**
	 * 时间相比得月份 如果是201711和201801相比，返回的结果是2 前面的时间要小于后面的时间
	 *
	 * @param month   格式为yyyyMM
	 * @param toMonth 格式为yyyyMM
	 * @return
	 * @since jdk 1.8
	 */
	public static int diffMonth(String month, String toMonth) {
		int year1 = Integer.parseInt(month.substring(0, 4));
		int month1 = Integer.parseInt(month.substring(4, 6));
		int year2 = Integer.parseInt(month.substring(0, 4));
		int month2 = Integer.parseInt(month.substring(4, 6));
		LocalDate ld1 = LocalDate.of(year1, month1, 01);
		LocalDate ld2 = LocalDate.of(year2, month2, 01);
		return Period.between(ld1, ld2).getMonths();
	}

	/**
	 * 判断是否为整型
	 *
	 * @param
	 * @return boolean
	 */
	public static boolean isInteger(String str) {
		Matcher m = p.matcher(str);
		return m.find();
	}

	/**
	 * 自定义位数产生随机数字
	 *
	 * @param
	 * @return String
	 */
	public static String random(int count) {
		char start = '0';
		char end = '9';
		Random rnd = new Random();
		char[] result = new char[count];
		int len = end - start + 1;
		while (count-- > 0) {
			result[count] = (char) (rnd.nextInt(len) + start);
		}
		return new String(result);
	}

	/**
	 * 获取自定义长度的随机数(含字母)
	 *
	 * @param len 长度
	 * @return String
	 */
	public static String random2(int len) {
		int random = Integer.parseInt(random(5));
		Random rd = new Random(random);
		final int maxNum = 62;
		StringBuffer sb = new StringBuffer();
		int rdGet;// 取得随机数
		char[] str = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
				't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
				'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8',
				'9' };
		int count = 0;
		while (count < len) {
			rdGet = Math.abs(rd.nextInt(maxNum));// 生成的数最大为62-1
			if (rdGet >= 0 && rdGet < str.length) {
				sb.append(str[rdGet]);
				count++;
			}
		}
		return sb.toString();
	}

	/**
	 * 获取本机ip
	 *
	 * @return String
	 * @throws UnknownHostException
	 */
	public static String getLocalHostIp() throws UnknownHostException {
		return InetAddress.getLocalHost().getHostAddress();
	}

	/**
	 * Object 转换为 String
	 *
	 * @param
	 * @return String
	 */
	public static String toString(Object obj) {
		return JSON.toJSONString(obj);
	}

	/**
	 * 消除字符串中的特殊字符（替换大部分空白字符， 不限于空格 \s 可以匹配空格、制表符、换页符等空白字符的其中任意一个）
	 * 
	 * @param str
	 * @return
	 */
	public static String removeSpecialCharacters(String str) {
		return str.replaceAll("\\s*", "");
	}

	/**
	 * 将内容写入到文件中
	 * 
	 * @param fileName
	 * @param content
	 */
	public static void write(String fileName, String content) {
		log.info("==>> writing content is: " + content);
		try {
			FileWriter writer = new FileWriter(fileName, true);
			writer.write(content + "\r\n");
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将内容写入到文件中
	 * 
	 * @param fileName
	 * @param content
	 */
	public static void write2(String fileName, String content) {
		log.info("==>> writing content is: " + content);
		try {
			OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(fileName, true), "UTF-8");
			osw.write(content + "\r\n");
			osw.flush();
			osw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * JSON 转换为 JavaBean
	 *
	 * @param json
	 * @param t
	 * @return <T>
	 */
	public static <T> T toBean(JSONObject json, Class<T> t) {
		return JSON.toJavaObject(json, t);
	}

	/**
	 * JSON 字符串转换为 JavaBean
	 *
	 * @param str
	 * @param t
	 * @return <T>
	 */
	public static <T> T toBean(String str, Class<T> t) {
		return JSON.parseObject(str, t);
	}

	/**
	 * JSON 字符串 转换成JSON格式
	 *
	 * @param
	 * @return JSONObject
	 */
	public static JSONObject toJson(String str) {
		if (isEmpty(str)) {
			return new JSONObject();
		}
		return JSON.parseObject(str);

	}

	/**
	 * JavaBean 转化为JSON
	 *
	 * @param t
	 * @return
	 */
	public static JSONObject toJson(Object t) {
		if (null == t || "".equals(t)) {
			return new JSONObject();
		}
		return (JSONObject) JSON.toJSON(t);
	}

	/**
	 * JSON 字符串转换为 HashMap
	 *
	 * @param json - String
	 * @return Map
	 */
	@SuppressWarnings("rawtypes")
	public static Map toMap(String json) {
		if (isEmpty(json)) {
			return new HashMap();
		}
		return JSON.parseObject(json, HashMap.class);
	}

	/**
	 * 将map转化为string
	 *
	 * @param m
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String toString(Map m) {
		return JSONObject.toJSONString(m);
	}

	/**
	 * String转换为数组
	 *
	 * @param text
	 * @return
	 */
	public static <T> Object[] toArray(String text) {
		return toArray(text, null);
	}

	/**
	 * String转换为数组
	 *
	 * @param text
	 * @return
	 */
	public static <T> Object[] toArray(String text, Class<T> clazz) {
		return JSON.parseArray(text, clazz).toArray();
	}

	/**
	 * name1=value1&name2=value2格式的数据转换成json数据格式
	 *
	 * @param str
	 * @return
	 */
	public static JSONObject str2Json(String str) {
		if (isEmpty(str)) {
			return new JSONObject();
		}
		JSONObject json = new JSONObject();
		String[] str1 = str.split("&");
		String str3 = "", str4 = "";
		if (null == str1 || str1.length == 0) {
			return new JSONObject();
		}
		for (String str2 : str1) {
			str3 = str2.substring(0, str2.lastIndexOf("="));
			str4 = str2.substring(str2.lastIndexOf("=") + 1, str2.length());
			json.put(str3, str4);
		}
		return json;
	}

	/**
	 * json数据格式 转换成name1=value1&name2=value2格式
	 *
	 * @param
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String json2Str(JSONObject json) {
		if (isEmpty(json)) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		Iterator it = json.entrySet().iterator(); // 定义迭代器
		while (it.hasNext()) {
			Entry er = (Entry) it.next();
			sb.append(er.getKey());
			sb.append("=");
			sb.append(er.getValue());
			sb.append("&");
		}
		sb.delete(sb.length() - 1, sb.length()); // 去掉最后的&
		return sb.toString();
	}

	/**
	 * 将JDBC查询的数据转换成List类型
	 *
	 * @param
	 * @return List
	 * @throws SQLException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List convertList(ResultSet rs) throws SQLException {
		if (null == rs) {
			return new ArrayList<>();
		}
		List list = new ArrayList();
		ResultSetMetaData md = rs.getMetaData();
		int columnCount = md.getColumnCount();
		while (rs.next()) {
			JSONObject rowData = new JSONObject();
			for (int i = 1; i <= columnCount; i++) {
				rowData.put(md.getColumnName(i), rs.getObject(i));
			}
			list.add(rowData);
		}
		return list;
	}

	/**
	 * MD5加密
	 *
	 * @param message
	 * @return
	 */
	public static String md5Encode(String message) {
		byte[] secretBytes = null;
		try {
			secretBytes = MessageDigest.getInstance("md5").digest(message.getBytes());
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("没有md5这个算法！");
		}
		String md5code = new BigInteger(1, secretBytes).toString(16);// 16进制数字
		// 如果生成数字未满32位，需要前面补0
		int length = 32 - md5code.length();
		for (int i = 0; i < length; i++) {
			md5code = "0" + md5code;
		}
		return md5code;
	}

	/**
	 * base64 加密
	 *
	 * @param str
	 * @return
	 */
	public static String base64En(String str) {
		Base64 base64 = new Base64();
		byte[] encode = base64.encode(str.getBytes());
		return new String(encode);
	}

	/**
	 * base64解密
	 *
	 * @param encodeStr
	 * @return
	 */
	@SuppressWarnings("static-access")
	public static String base64De(String encodeStr) {
		Base64 base64 = new Base64();
		byte[] decodeStr = Base64.decodeBase64(encodeStr.getBytes());
		return new String(decodeStr);
	}

	/**
	 * 十进制转二进制
	 *
	 * @param n
	 * @return
	 */
	public static String decToBinary(int n) {
		String str = "";
		while (n != 0) {
			str = n % 2 + str;
			n = n / 2;
		}
		return str;
	}

	/**
	 * 二进制转十进制
	 *
	 * @param
	 * @return
	 */
	public static int binaryToDec(char[] cs) {
		return binaryToDec(cs);
	}

	/**
	 * 二进制转十进制
	 *
	 * @param
	 * @return
	 */
	public static int binaryToDec(String cs) {
		return new BigInteger(new String(cs), 2).intValue();
	}

	/**
	 * 后台执行bat文件
	 * 
	 * @param batPath
	 * @param batName
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public static boolean doCallBat(String batPath, String batName) throws IOException, InterruptedException {
		Boolean flag = false;
		log.info("******** 【INFO】 " + batName + " bat File excute start ********");
		if (isEmpty(batName)) {
			batName = new File(batPath).getName();
		}
		List list = new ArrayList();
		ProcessBuilder processBuilder = null;
		Process process = null;
		String line = null;
		BufferedReader stdout = null;
		list.add("cmd");
		list.add("/c");
//		list.add("start");
		list.add(batPath);
		processBuilder = new ProcessBuilder(list);
		processBuilder.redirectErrorStream(true);
		process = processBuilder.start();
		stdout = new BufferedReader(new InputStreamReader(process.getInputStream(), "BIG5"));
		OutputStreamWriter os = new OutputStreamWriter(process.getOutputStream());
		while ((line = stdout.readLine()) != null) {
			log.info("【" + batName + "】:" + line);
			if (line.contains("Updating BOMs and other references") && "ipemimport.bat".equals(batName)) { // 代表执行
				flag = true;
			} else if ("ipemexport.bat".equals(batName)) {
				flag = true;
			} else if ("ending".equals(line)) {
				break;
			}
		}
		int ret = process.waitFor();
		stdout.close();
		log.info("******** 【INFO】 " + batName + "bat File excute finsh ********");
		return flag;
	}

	@SuppressWarnings("unused")
	public static String getFilePath(String foldName) {
		String tempPath = System.getProperty("java.io.tmpdir") + File.separator;
		log.info("【INFO】 tempPath: " + tempPath);
		File file = new File(tempPath + foldName);
		if (!file.exists()) {
			file.mkdirs();
		}
		return file.getAbsolutePath();
	}

	/*
	 * 中文转unicode编码
	 */
	public static String gbEncoding(final String gbString) {
		char[] utfBytes = gbString.toCharArray();
		String unicodeBytes = "";
		for (int i = 0; i < utfBytes.length; i++) {
			String hexB = Integer.toHexString(utfBytes[i]);
			if (hexB.length() <= 2) {
				hexB = "00" + hexB;
			}
			unicodeBytes = unicodeBytes + "\\u" + hexB;
		}
		return unicodeBytes;
	}

	/*
	 * unicode编码转中文
	 */
	public static String decodeUnicode(final String dataStr) {
		int start = 0;
		int end = 0;
		final StringBuffer buffer = new StringBuffer();
		while (start > -1) {
			end = dataStr.indexOf("\\u", start + 2);
			String charStr = "";
			if (end == -1) {
				charStr = dataStr.substring(start + 2, dataStr.length());
			} else {
				charStr = dataStr.substring(start + 2, end);
			}
			char letter = (char) Integer.parseInt(charStr, 16); // 16进制parse整形字符串。
			buffer.append(new Character(letter).toString());
			start = end;
		}
		return buffer.toString();
	}

	/**
	 * 删除某个文件夹下的所有文件
	 * 
	 * @param delpath String
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @return boolean
	 */
	public static boolean deletefile(String delpath) throws Exception {
		try {
			File file = new File(delpath);
			// 当且仅当此抽象路径名表示的文件存在且 是一个目录时，返回 true
			if (!file.isDirectory()) {
				file.delete();
			} else if (file.isDirectory()) {
				String[] filelist = file.list();
				for (int i = 0; i < filelist.length; i++) {
					File delfile = new File(delpath + File.separator + filelist[i]);
					if (!delfile.isDirectory()) {
						delfile.delete();
						log.info("【INFO】 " + delfile.getAbsolutePath() + "删除文件成功");
					} else if (delfile.isDirectory()) {
						deletefile(delpath + File.separator + filelist[i]);
					}
				}
//				log.info("【INFO】 " + file.getAbsolutePath() + "删除成功");
//				file.delete();
			}

		} catch (FileNotFoundException e) {
			log.info("【ERROR】 " + "deletefile() Exception:" + e.getMessage());
			System.err.println(getExceptionMsg(e));
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 删除某个文件夹下的所有文件(包含进程正在使用的文件)
	 * 
	 * @param delpath String
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @return boolean
	 */
	public static boolean deletefileNew(String delpath) throws Exception {
		try {
			File file = new File(delpath);
			// 当且仅当此抽象路径名表示的文件存在且 是一个目录时，返回 true
			if (!file.isDirectory()) {
				file.delete();
			} else if (file.isDirectory()) {
				String[] filelist = file.list();
				for (int i = 0; i < filelist.length; i++) {
					File delfile = new File(delpath + File.separator + filelist[i]);
					if (!delfile.isDirectory()) {
						boolean result = delfile.delete();
						if (!result) {
							System.gc();
							delfile.delete();
						}
						log.info("【INFO】 " + delfile.getAbsolutePath() + "删除文件成功");
					} else if (delfile.isDirectory()) {
						deletefile(delpath + File.separator + filelist[i]);
					}
				}
//				log.info("【INFO】 " + file.getAbsolutePath() + "删除成功");
//				file.delete();
			}

		} catch (FileNotFoundException e) {
			log.info("【ERROR】 " + "deletefile() Exception:" + e.getMessage());
			System.err.println(getExceptionMsg(e));
			return false;
		}
		return true;
	}

	/**
	 * 判断文件夹是否有文件
	 * 
	 * @param filePath 文件夹路径
	 * @return
	 */
	public static boolean checkFolder(String filePath) {
		File file = new File(filePath);
		File[] listFiles = file.listFiles();
		if (listFiles.length > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获取jar包所在的文件夹
	 * 
	 * @return
	 */
	public static String getPath(Class<?> class1) {
		System.out.println(class1.getProtectionDomain().getCodeSource().getLocation());
		String path = class1.getProtectionDomain().getCodeSource().getLocation().getPath();
		if (System.getProperty("os.name").contains("dows")) {
			path = path.substring(1, path.length());
		}
		if (path.contains("jar")) {
			path = path.substring(0, path.lastIndexOf("."));
			return path.substring(0, path.lastIndexOf("/"));
		}
		return path.replace("target/classes/", "");
	}

	/**
	 * 获取日志文件的绝对路径
	 * 
	 * @return
	 * @throws IOException
	 */
	public static Properties getProperties(String filePath) {
		try {
			// 使用InPutStream流读取properties文件
			BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
			Properties props = new Properties();
			props.load(bufferedReader);
			return props;
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println(getExceptionMsg(e));
		}
		return null;
	}

	/**
	 * 获取含有文件名的绝对路径下的文件名称
	 * 
	 * @param absoluteFilePath
	 * @return
	 */
	public static String getFileName(String absoluteFilePath) {
		File tempFile = new File(absoluteFilePath.trim());
		return tempFile.getName();
	}

	/**
	 * 获取异常信息
	 * 
	 * @param e
	 * @return String
	 */
	public static String getExceptionMsg(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}

}
