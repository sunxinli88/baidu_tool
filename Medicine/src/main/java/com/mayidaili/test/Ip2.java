package com.mayidaili.test;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.common.base.Joiner;
import com.seassoon.common.model.DynamicProxy;

public class Ip2 {
	private static Logger log = Logger.getLogger(Ip2.class);
	// 定义申请获得的appKey和appSecret
	String appkey = "17683621";
	String secret = "cdcd9ae906fccab6cd28e24ed9164441";
//	String proxyIP = "test.proxy.mayidaili.com";
	String proxyIP = "123.57.138.199";
	int proxyPort = 8123;
	
	public String getProxyIP() {
		return proxyIP;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public String getAuthHeader() {
		
		// 创建参数表
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("app_key", appkey);
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		format.setTimeZone(TimeZone.getTimeZone("GMT+8"));// 使用中国时间，以免时区不同导致认证错误
		paramMap.put("timestamp", format.format(new Date()));
		
		// 对参数名进行排序
		String[] keyArray = paramMap.keySet().toArray(new String[0]);
		Arrays.sort(keyArray);
		
		// 拼接有序的参数名-值串
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(secret);
		for (String key : keyArray) {
			stringBuilder.append(key).append(paramMap.get(key));
		}
		
		stringBuilder.append(secret);
		String codes = stringBuilder.toString();
		
		// MD5编码并转为大写， 这里使用的是Apache codec
		String sign = org.apache.commons.codec.digest.DigestUtils.md5Hex(codes).toUpperCase();
		
		paramMap.put("sign", sign);
		
		// 拼装请求头Proxy-Authorization的值，这里使用 guava 进行map的拼接
		String authHeader = "MYH-AUTH-MD5 " + Joiner.on('&').withKeyValueSeparator("=").join(paramMap);
		
		log.info(authHeader);
		try {
			saveProxyInfo(authHeader);
		} catch (Exception e) {
			log.error(e);
		}
		return authHeader;
	}
	
	
	public void saveProxyInfo(String header) throws Exception{
		Document doc = Jsoup.connect("http://1212.ip138.com/ic.asp").header("Proxy-Authorization", header)
				.proxy(proxyIP, proxyPort, null).followRedirects(true).validateTLSCertificates(false).timeout(10000).get();
		
		String centerStr = doc.select("center").text().trim();
		String host = centerStr.substring(centerStr.indexOf("[")+1, centerStr.indexOf("]"));
		String region = centerStr.substring(centerStr.indexOf("来自：")+3);
		DynamicProxy dp = DynamicProxy.dao.findFirst("select * from dynamic_proxy where host = ?", host);
		
		if(dp!=null){
			dp.setCount(dp.getCount()+1);
			dp.update();
		}else{
			dp = new DynamicProxy(header, host, region);
			dp.save();
		}
	}
	
	
	
	public void ip() {
		
		try {
			Document doc = Jsoup.connect("http://1212.ip138.com/ic.asp").header("Proxy-Authorization", getAuthHeader())
					.proxy(proxyIP, proxyPort, null).followRedirects(true).validateTLSCertificates(false).timeout(10000).get();
			log.info(doc);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new Ip2().ip();
	}
}
