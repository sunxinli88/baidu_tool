package com.seassoon.clim.medicine;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;

import org.jsoup.select.Elements;

import com.seassoon.common.Spidered;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;

public class Utils {

	public static boolean pageIsSpidered(String db,String collection,String url) {
		return new Spidered(db,collection).isSpidered(url);
		
	}
	
	public static boolean clickIsSpidered(String url) {
		
		return new Spidered("medicine","clickUrls").isSpidered(url);
	}
	

	public static Elements clickes(Page page) {
		// TODO Auto-generated method stub
		org.jsoup.nodes.Document doc = Jsoup.parse(page.getRawText());

		// 判断是否包含onclick事件
		Elements onclick = doc.select(".m49 .bg");

		return onclick;
	}

	public static void addUrl(String page) {
		// TODO Auto-generated method stub
		try {
			// 把刚刚爬取过的药品内容页的url保存到文件
			FileWriter writer = new FileWriter("G:/xx/getFromDB.txt", true);

			BufferedWriter bufferWritter = new BufferedWriter(writer);

			bufferWritter.write(page + "\r\n");

			bufferWritter.flush();
			bufferWritter.close();
			writer.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Request setClickUrl(String url, String e, String i,String pagePrameter) {
		// TODO Auto-generated method stub

		Request req = new Request();
		
		req.setUrl(url);
		req.setMethod("POST");
		Map<String, Object> params = new HashMap<String, Object>();

		BasicNameValuePair[] nvps = new BasicNameValuePair[] { new BasicNameValuePair("callCount", "1"),
				new BasicNameValuePair("page", "/drug/" + pagePrameter + ".htm"),
				new BasicNameValuePair("httpSessionId", ""),
				new BasicNameValuePair("scriptSessionId", "ABD124B9DC0A19CE8BA66D5A518ECDC8419"),
				new BasicNameValuePair("c0-scriptName", "DrugUtils"),
				new BasicNameValuePair("c0-methodName", "showDetail"), new BasicNameValuePair("c0-id", "0"),
				new BasicNameValuePair("c0-param0", "number:" + pagePrameter),
				new BasicNameValuePair("c0-param1", "number:" + e),
				new BasicNameValuePair("batchId",i) };

		params.put("nameValuePair", nvps);
		req.setExtras(params);

		return req;
	}

	public static void saveClickUrl(String entry, int order, String indexAtpage, String pagePrameter) {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<>();
		
		map.put("entry", entry);
		
		map.put("order", order);
		
		map.put("indexAtpage", indexAtpage);
		
		map.put("pagePrameter", pagePrameter);
		
		//0表示没有爬取过的clickUrl
		map.put("state", "0");
		
		map.put("url", "http://drugs.dxy.cn/dwr/call/plaincall/DrugUtils.showDetail.dwr?onclick=" + pagePrameter + "x" + order);
		
		new Spidered("medicine", "clickUrls").save(map);
	}
}
