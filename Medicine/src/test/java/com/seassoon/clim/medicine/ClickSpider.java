package com.seassoon.clim.medicine;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpHost;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jsoup.Jsoup;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.seassoon.clim.dw.NewHttpClient;
import com.seassoon.utils.MongoDBUtils;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

public class ClickSpider implements PageProcessor{
	
	private Site site = Site.me().setCycleRetryTimes(3).setRetryTimes(3).setSleepTime(0).setHttpProxy(new HttpHost("101.200.143.21",8123));

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		Spider.create(new ClickSpider()).addUrl("http://drugs.dxy.cn/dwr/call/plaincall/DrugUtils.showDetail.dwr")
		.addPipeline(new ClickPipline()).setDownloader(new NewHttpClient()).thread(1).run();
	
	}

	@Override
	public void process(Page page) {
		// TODO Auto-generated method stub
		
		if(page.getRequest().getUrl().toString()
				.equals("http://drugs.dxy.cn/dwr/call/plaincall/DrugUtils.showDetail.dwr")){
			
			page.setSkip(true);
			
			MongoDBUtils mongo = MongoDBUtils.getInstance("127.0.0.1", 27017);

			MongoCollection<Document> medicine = mongo.getCollection("medicine", "clickUrls");

			String url="",e = "",order ="",pagePrameter = "",entry = "";
			
			BasicDBObject query = new BasicDBObject(); 
			
            query.put("state", "0"); 
			
			for (Document cur : medicine.find(query)) {
						
				entry = cur.getString("entry");
				url = cur.getString("url");
				e = cur.getString("indexAtpage");
				pagePrameter = cur.getString("pagePrameter");
				order = String.valueOf(cur.getInteger("order"));
				
				Request req = Utils.setClickUrl(url,e,order,pagePrameter);
				
				req.putExtra("pageParameter",pagePrameter);
				
				req.putExtra("entry",entry);
				
				page.addTargetRequest(req);
				
			}
			
		}
		
		if (page.getRequest().getUrl().toString()
				.contains("http://drugs.dxy.cn/dwr/call/plaincall/DrugUtils.showDetail.dwr?onclick=")) {
			
			org.jsoup.nodes.Document doc = Jsoup.parse(page.getRawText());

			String text = Convertion.convertUnicode(doc.text());
		
			if(text.equals("")){
				
				page.getResultItems().setSkip(true);
				
			}else{

				int index = text.indexOf("Callback");
				
				text = text.substring(index+18, text.length()-3);
				
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
				page.putField("content", text);
				
				page.putField("url", page.getRequest().getUrl());
			}
		}
	}

	@Override
	public Site getSite() {
		// TODO Auto-generated method stub
		return site;
	}

}
