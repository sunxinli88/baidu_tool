package com.seassoon.clim.medicine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpHost;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.omg.CORBA.NameValuePair;

import com.seassoon.clim.dw.NewHttpClient;
import com.seassoon.common.config.JfinalORMConfig;

import net.sf.json.util.NewBeanInstanceStrategy;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.FileCacheQueueScheduler;
import us.codecraft.webmagic.scheduler.PriorityScheduler;

public class MagicMedicine implements PageProcessor {

	// ip代理

	private Site site = Site.me().setCycleRetryTimes(3).setRetryTimes(3).setSleepTime(0).setHttpProxy(new HttpHost("101.200.143.21",8123));

	public void process(Page page) {

		if (page.getRequest().getUrl().toString()
				.contains("http://drugs.dxy.cn/dwr/call/plaincall/DrugUtils.showDetail.dwr?")) {
			
			if (!Utils.isSpidered(page.getRequest().getUrl().toString())) {

				Document doc = Jsoup.parse(page.getRawText());

				String text = Convertion.convertUnicode(doc.text());
				
				if(text.equals("")){
					
					page.getResultItems().setSkip(true);
					
				}else{
					Map<String,Object> m = page.getRequest().getExtras();
					
					for(Entry<String, Object> en:m.entrySet()){
						
						String key = en.getKey();
										
						String val = "";
						try{
							val = en.getValue().toString();
						}catch(Exception e){
							System.out.println("value is null");
						}
						
						page.putField(key, val);			
					}
		
					page.putField("page", page.getRequest().getUrl());
					
					page.putField(page.getRequest().getExtra("clickname").toString(), text);
				}				
			}else{
				page.getResultItems().setSkip(true);
			}

		}

//		if (page.getRequest().getUrl().toString().equals("http://drugs.dxy.cn/drug/4024.htm")) {
//
//			Request req = new Request();
//			req.setUrl("http://drugs.dxy.cn/dwr/call/plaincall/DrugUtils.showDetail.dwr");
//			req.setMethod("POST");
//			Map<String, Object> params = new HashMap<String, Object>();
//			BasicNameValuePair[] nvps = new BasicNameValuePair[] { new BasicNameValuePair("callCount", "1"),
//					new BasicNameValuePair("page", "/drug/4024.htm"), new BasicNameValuePair("httpSessionId", ""),
//					new BasicNameValuePair("scriptSessionId", "AB5E5844BF5E1AC8E590AAB4E373C05E256"),
//					new BasicNameValuePair("c0-scriptName", "DrugUtils"),
//					new BasicNameValuePair("c0-methodName", "showDetail"), new BasicNameValuePair("c0-id", "0"),
//					new BasicNameValuePair("c0-param0", "number:4024"),
//					new BasicNameValuePair("c0-param1", "number:4"), new BasicNameValuePair("batchId", "1") };
//
//			params.put("nameValuePair", nvps);
//			req.setExtras(params);
//			page.addTargetRequest(req);
//		}

		// 主页面，获取大分类和小分类的页面
		if (page.getRequest().getUrl().toString().equals("http://drugs.dxy.cn/")) {
			
//			page.getRequest().setPriority(0);
 
			page.getResultItems().setSkip(true);

			Document doc = Jsoup.parse(page.getRawText());// page.getHtml().getDocument();

			Elements trs = doc.select(".hide-more a");

			Map<String, String> map = new HashMap<String, String>();

			for (Element tr : trs) {

				String value = tr.attr("onclick");

				if (value.contains(",")) {
					int index = value.indexOf(",");
					value = value.substring(index + 1, value.length() - 1);
				}
				// value是数字根据数字大类和二级分类匹配，第二个参数是大类
				map.put(value, tr.text());
			}

			Elements divs = doc.select(".common_main div");

			for (Element div : divs) {
				String key = div.attr("id");// select("div:eq("+i+")").
				if (key.contains("_")) {
					int index = key.indexOf("_");
					key = key.substring(index + 1, key.length());
				}

				Elements lis = div.select("li");
				for (Element li : lis) {

					String href = li.select("a").attr("href");
					// key----获取大类对应的键值数字,href---获取小类连接如“口腔科用药”,text---获取小类文本

					String firstLevel = "";

					Set<String> keys = map.keySet();

					for (String key1 : keys) {
						if (key1.equals(key)) {
							firstLevel = map.get(key1).toString();
						}
					}
					//添加首页中右侧的目录连接，设置优先级为1
					Request request = new Request(href);
					
//					request.setPriority(1);
					
					if (firstLevel == null) {
						System.out.println("as");
					}
					request.putExtra("firstLevel", firstLevel);

					page.addTargetRequest(request);
					
//					System.out.println(firstLevel+"firstLevel::"+request.getUrl().toString());

				}

			}
		}

		// 根据小分类内容页添加药品的内容页和目录连接
		if (page.getRequest().getUrl().matches("http://drugs.dxy.cn/category/.*")) {

			page.getResultItems().setSkip(true);

			Document doc = Jsoup.parse(page.getRawText());// page.getHtml().getDocument();

			String secondLevel = doc.select(".common_hd").select("div:eq(0)").text();

			Elements lis = doc.select(".list a");

			//添加药品内容页的目录连接,设置优先级2
			for (Element li : lis) {
				// String name = li.select("a").text();
				String href = li.select("a").attr("href");
				
				if(!Utils.isSpidered(href)){
					page.addTargetRequest(new Request(href).putExtra("firstLevel", page.getRequest().getExtra("firstLevel"))
							.putExtra("secondLevel", secondLevel).setPriority(2));
				}				
			}
			
			//去除目录页url末尾多余的字符串
			String nextPage = "";

			Elements ps = doc.select(".pagination a");

			String nowPage = page.getUrl().toString();

			if (nowPage.contains("?page=")) {
				int index = nowPage.indexOf("?page=");
				nowPage = nowPage.substring(0, index);
			}

			// 添加當前小分類內容頁的最底层的目录页

			for (Element p : ps) {

				nextPage = p.attr("href");

				nextPage = nowPage + nextPage;
				// }
				if (!nextPage.equals("")) {
					page.addTargetRequest(
							new Request(nextPage).putExtra("firstLevel", page.getRequest().getExtra("firstLevel"))
									.putExtra("secondLevel", secondLevel).setPriority(1));
//					System.out.println(nextPage);
				}
			}

		}

		// 获取药品内容页的内容,
		if (page.getRequest().getUrl().matches("http://drugs.dxy.cn/drug/.*")) {
			
			//判断页面是否已经爬取
			if(!Utils.isSpidered(page.getRequest().getUrl().toString())){
				Elements clickes = Utils.clickes(page);
				
				//判断页面中是否包含onclick事件
				if (clickes.size() > 0) {
					
					page.getResultItems().setSkip(true);

					int i = 1;

					for (Element e : clickes) {
						
						String s = e.parent().text();
										
						//设置onclick事件关联的url，设置优先级为3,数字越大优先级越高
						Request req = Utils.setClickUrl(page,e,i);
						
						req.setPriority(3);
						
						//传递url的同时，附加上药品内容页上爬取的信息，在下一等级页面中传递给pipeline处理
						req.putExtra("firstLevel", page.getRequest().getExtra("firstLevel"));
						
						req.putExtra("secondLevel",page.getRequest().getExtra("secondLevel"));
						
						req.putExtra("clickname", s);
						
						req.putExtra("page", page.getRequest().getUrl().toString());
						
						Document doc = Jsoup.parse(page.getRawText());
						
						Elements img = doc.select(".m49 img");
						
						if(img.size()!=0){
							req.putExtra("img","yes");
						}else{
							req.putExtra("img","no");
						}

						Elements ds = doc.select(".m49 dd,dt");
						
						String name = "", content = "";
						
						for (Element d : ds) {
						
							if (name.equals("")) {
								name = d.getElementsByTag("dt").text();
							}

							content = d.getElementsByTag("dd").text();

							if (!(name.equals("") || content.equals(""))) {
								req.putExtra(name, content);
								name = "";
							}	
						}
						if (!Utils.isSpidered(req.getUrl())) {
							
							page.addTargetRequest(req);
						}
						i++;
					}
					//没有click事件
				} else if(clickes.size()==0){
					
					Document doc = Jsoup.parse(page.getRawText());

					Elements ds = doc.select(".m49 dd,dt");
					
					if(ds.size()==0){
						try{
							int erro = 10/ds.size();
						}catch(Exception e){
							page.addTargetRequest(page.getUrl().toString());
						}
					}
					
					Elements img = doc.select(".m49 img");
					
					if(img.size()!=0){
						page.putField("img","yes");
					}else{
						page.putField("img","no");
					}
					
					String name = "", content = "";

					// 獲取大分類和小分類
					page.putField("page",page.getRequest().getUrl().toString());
					
					page.putField("firstLevel", page.getRequest().getExtra("firstLevel"));

					page.putField("secondLevel", page.getRequest().getExtra("secondLevel"));

					for (Element d : ds) {

						if (name.equals("")) {
							name = d.getElementsByTag("dt").text();
						}

						content = d.getElementsByTag("dd").text();

						if (!(name.equals("") || content.equals(""))) {
							page.putField(name, content);
							name = "";
						}	
					}
				}		
		}else{
			page.getResultItems().setSkip(true);
		}

	}
	}

	

	public Site getSite() {
		return site;
	}

	public static void main(String[] args) {
		
		

		Spider.create(new MagicMedicine()).addUrl("http://drugs.dxy.cn/")//.addRequest(requests).setScheduler(new PriorityScheduler())
		.addPipeline(new MagicPipline()).setDownloader(new NewHttpClient()).thread(1).run();
	}

}
