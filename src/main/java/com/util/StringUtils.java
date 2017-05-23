package com.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StringUtils {
	public static String getUUID() {
		return UUID.randomUUID().toString();
	}
	public static String formatdate(String pudate) {
		pudate=pudate.replace("年", "-");
		pudate=pudate.replace("月", "-");
		pudate=pudate.replace("日", " ");
		pudate=pudate.replace("\\", "-");
		pudate=pudate.replace("//","-");
		return pudate;
	}
	public static String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		try {
			t.printStackTrace(pw);
			return sw.toString();
		} finally {
			pw.close();
		}
	}
	public static String md5(String plainText) {
		byte[] secretBytes = null;
		try {
			secretBytes = MessageDigest.getInstance("md5").digest(
					plainText.getBytes());
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("没有md5这个算法！");
		}
		String md5code = new BigInteger(1, secretBytes).toString(16);// 16进制数字
		// 如果生成数字未满32位，需要前面补0
		for (int i = 0; i < 32 - md5code.length(); i++) {
			md5code = "0" + md5code;
		}
		return md5code;
	}
	public static String convertStreamToString(String filePath,StringBuffer sbf){
		File file=new File(filePath);
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
			String str="";
			while ((str=br.readLine())!=null) {
				sbf.append(str);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sbf.toString();
	}
	public static List<String> convertStreamToString(String filePath){
		List<String>  wbList=new ArrayList<String>();
		File file=new File(filePath);
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
			String str="";
			while ((str=br.readLine())!=null) {
				wbList.add(str);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return wbList;
	}
}
