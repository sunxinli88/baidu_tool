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

	// ip����

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

		// ��ҳ�棬��ȡ������С�����ҳ��
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
				// value�����ָ������ִ���Ͷ�������ƥ�䣬�ڶ��������Ǵ���
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
					// key----��ȡ�����Ӧ�ļ�ֵ����,href---��ȡС�������硰��ǻ����ҩ��,text---��ȡС���ı�

					String firstLevel = "";

					Set<String> keys = map.keySet();

					for (String key1 : keys) {
						if (key1.equals(key)) {
							firstLevel = map.get(key1).toString();
						}
					}
					//�����ҳ���Ҳ��Ŀ¼���ӣ��������ȼ�Ϊ1
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

		// ����С��������ҳ���ҩƷ������ҳ��Ŀ¼����
		if (page.getRequest().getUrl().matches("http://drugs.dxy.cn/category/.*")) {

			page.getResultItems().setSkip(true);

			Document doc = Jsoup.parse(page.getRawText());// page.getHtml().getDocument();

			String secondLevel = doc.select(".common_hd").select("div:eq(0)").text();

			Elements lis = doc.select(".list a");

			//���ҩƷ����ҳ��Ŀ¼����,�������ȼ�2
			for (Element li : lis) {
				// String name = li.select("a").text();
				String href = li.select("a").attr("href");
				
				if(!Utils.isSpidered(href)){
					page.addTargetRequest(new Request(href).putExtra("firstLevel", page.getRequest().getExtra("firstLevel"))
							.putExtra("secondLevel", secondLevel).setPriority(2));
				}				
			}
			
			//ȥ��Ŀ¼ҳurlĩβ������ַ���
			String nextPage = "";

			Elements ps = doc.select(".pagination a");

			String nowPage = page.getUrl().toString();

			if (nowPage.contains("?page=")) {
				int index = nowPage.indexOf("?page=");
				nowPage = nowPage.substring(0, index);
			}

			// ��Ӯ�ǰС�����퓵���ײ��Ŀ¼ҳ

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

		// ��ȡҩƷ����ҳ������,
		if (page.getRequest().getUrl().matches("http://drugs.dxy.cn/drug/.*")) {
			
			//�ж�ҳ���Ƿ��Ѿ���ȡ
			if(!Utils.isSpidered(page.getRequest().getUrl().toString())){
				Elements clickes = Utils.clickes(page);
				
				//�ж�ҳ�����Ƿ����onclick�¼�
				if (clickes.size() > 0) {
					
					page.getResultItems().setSkip(true);

					int i = 1;

					for (Element e : clickes) {
						
						String s = e.parent().text();
										
						//����onclick�¼�������url���������ȼ�Ϊ3,����Խ�����ȼ�Խ��
						Request req = Utils.setClickUrl(page,e,i);
						
						req.setPriority(3);
						
						//����url��ͬʱ��������ҩƷ����ҳ����ȡ����Ϣ������һ�ȼ�ҳ���д��ݸ�pipeline����
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
					//û��click�¼�
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

					// �@ȡ����С���
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
