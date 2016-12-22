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
//import com.seassoon.common.model.DynamicProxy;

public class Ip {
	private static Logger log = Logger.getLogger(Ip.class);
	// 瀹氫箟鐢宠鑾峰緱鐨刟ppKey鍜宎ppSecret
	String appkey = "243167619";
	String secret = "960cc50d8a774d5dda78af660641df0a";
	String proxyIP = "101.200.143.21";
	int proxyPort = 8123;
	
	public String getProxyIP() {
		return proxyIP;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public String getAuthHeader() {
		
		// 鍒涘缓鍙傛暟琛�
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("app_key", appkey);
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		format.setTimeZone(TimeZone.getTimeZone("GMT+8"));// 浣跨敤涓浗鏃堕棿锛屼互鍏嶆椂鍖轰笉鍚屽鑷磋璇侀敊璇�
		paramMap.put("timestamp", format.format(new Date()));
		
		// 瀵瑰弬鏁板悕杩涜鎺掑簭
		String[] keyArray = paramMap.keySet().toArray(new String[0]);
		Arrays.sort(keyArray);
		
		// 鎷兼帴鏈夊簭鐨勫弬鏁板悕-鍊间覆
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(secret);
		for (String key : keyArray) {
			stringBuilder.append(key).append(paramMap.get(key));
		}
		
		stringBuilder.append(secret);
		String codes = stringBuilder.toString();
		
		// MD5缂栫爜骞惰浆涓哄ぇ鍐欙紝 杩欓噷浣跨敤鐨勬槸Apache codec
		String sign = org.apache.commons.codec.digest.DigestUtils.md5Hex(codes).toUpperCase();
		
		paramMap.put("sign", sign);
		
		// 鎷艰璇锋眰澶碢roxy-Authorization鐨勫�硷紝杩欓噷浣跨敤 guava 杩涜map鐨勬嫾鎺�
		String authHeader = "MYH-AUTH-MD5 " + Joiner.on('&').withKeyValueSeparator("=").join(paramMap);
		
		log.info(authHeader);
//		try {
//			saveProxyInfo(authHeader);
//		} catch (Exception e) {
//			log.error(e);
//		}
		return authHeader;
	}
	
	
//	public void saveProxyInfo(String header) throws Exception{
//		Document doc = Jsoup.connect("http://1212.ip138.com/ic.asp").header("Proxy-Authorization", header)
//				.proxy(proxyIP, proxyPort, null).followRedirects(true).validateTLSCertificates(false).timeout(10000).get();
//		
//		String centerStr = doc.select("center").text().trim();
//		String host = centerStr.substring(centerStr.indexOf("[")+1, centerStr.indexOf("]"));
//		String region = centerStr.substring(centerStr.indexOf("鏉ヨ嚜锛�")+3);
//		DynamicProxy dp = DynamicProxy.dao.findFirst("select * from dynamic_proxy where host = ?", host);
//		
//		if(dp!=null){
//			dp.setCount(dp.getCount()+1);
//			dp.update();
//		}else{
//			dp = new DynamicProxy(header, host, region);
//			dp.save();
//		}
//	}
	
	
	
//	public void ip() {
//		
//		try {
//			Document doc = Jsoup.connect("http://1212.ip138.com/ic.asp").header("Proxy-Authorization", getAuthHeader())
//					.proxy(proxyIP, proxyPort, null).followRedirects(true).validateTLSCertificates(false).timeout(10000).get();
//			log.info(doc);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
//	public static void main(String[] args) {
//		new Ip().ip();
//	}
}
