package com.seassoon.clim.dw;

import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import util.connSql_2;

public class webMagicTest implements PageProcessor {
	
	
	
	private Site site = Site.me().setCycleRetryTimes(3).setRetryTimes(3).setSleepTime(100);

	@Override
	public void process(Page page) {

		if (page.getRequest().getUrl().matches("http://www\\.med126\\.com/pharm/cnmed.*")) {

			// 在爬取队列中加入等待爬取页面目录里的链接
			List<String> list2 = page.getHtml().$(".listdlmid .showpage a").links().all();
			page.addTargetRequests(list2);
			System.out.println(list2.size());
			
			// 此时的页面是每一页，获取每一个页面的医药品列表以及药品的链接，把药品的链接加入堆栈

			List<String> list = page.getHtml().$(".listdlmid .e").links().all();
			page.addTargetRequests(list);
			
			page.setSkip(true);

		}
		// if(page.getResultItems().get("author").toString().startsWith("20")){
		if (page.getRequest().getUrl().matches("http://www\\.med126\\.com/pharm/20.*")) {

			Document doc = page.getHtml().getDocument();

			Elements trs = doc.select(".med126content tr");

			for (Element tr : trs) {

				page.putField(tr.select("td:eq(0)").text(), tr.select("td:eq(1)").text());
				
				
			
				
			}
		}
	}

	@Override
	public Site getSite() {
		return site;
	}

	public static void main(String[] args) {

		
			Spider.create(new webMagicTest()).addUrl("http://www.med126.com/pharm/2009/20090113055005_336043.shtml")
			.addPipeline(new TestPipeline()).thread(5).run();
//		Spider.create(new webMagicTest()).addUrl("http://www.med126.com/pharm/cnmed/")
//				.addPipeline(new TestPipeline()).thread(5).run();//.addPipeline(new MongDBpipeline()).thread(5).run();
//		System.out.println(connSql_2.erroCount);
	}
}
