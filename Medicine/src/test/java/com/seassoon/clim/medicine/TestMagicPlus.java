
package com.seassoon.clim.medicine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpHost;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.seassoon.clim.dw.NewHttpClient;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.PriorityScheduler;

public class TestMagicPlus implements PageProcessor {
	
	private String ip;
	
	private int number;

	private List<String[]> httpProxyList = new ArrayList<>();
	
	public TestMagicPlus(){
		
		String[] ip1={};
		
		String[] ip2={};
		
		httpProxyList.add(ip1);
		
		httpProxyList.add(ip2);
		
	}
	


	private Site site = Site.me().setCycleRetryTimes(3).setRetryTimes(3).setSleepTime(0).setHttpProxyPool(httpProxyList);

	public void process(Page page) {

		// 主页面，获取大分类和小分类的页面
		if (page.getRequest().getUrl().toString().equals("http://drugs.dxy.cn/")) {

			// page.getRequest().setPriority(0);

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
				// value是数字，根据数字对大类和二级分类进行匹配，第二个参数是大类
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
					// 添加首页中左侧的目录连接，设置优先级为1
					Request request = new Request(href);

					// request.setPriority(1);

					if (firstLevel == null) {
						System.out.println("as");
					}
					request.putExtra("firstLevel", firstLevel);

					page.addTargetRequest(request);

				}

			}
		}

		// 根据小分类内容页添加药品的内容页和目录连接
		if (page.getRequest().getUrl().matches("http://drugs.dxy.cn/category/.*")) {

			page.getResultItems().setSkip(true);

			Document doc = Jsoup.parse(page.getRawText());// page.getHtml().getDocument();

			Elements Level2 = doc.select(".common_hd");

			String secondLevel = Level2.select("div:eq(0)").text();

			// 获取二级分类，同时在改名字后面加上该类药品下面有多少条记录
			secondLevel += Level2.select("div:eq(1) span").text();

			Elements lis = doc.select(".list a");

			// 添加药品内容页的目录连接,设置优先级2
			for (Element li : lis) {

				String href = li.select("a").attr("href");

				if (!Utils.pageIsSpidered(href)) {
					page.addTargetRequest(
							new Request(href).putExtra("firstLevel", page.getRequest().getExtra("firstLevel"))
									.putExtra("secondLevel", secondLevel).setPriority(2));
				}
			}

			// 去除目录页url末尾多余的字符串
			String nextPage = "";

			Elements ps = doc.select(".pagination a");

			String nowPage = page.getUrl().toString();

			if (nowPage.contains("?page=")) {
				int index = nowPage.indexOf("?page=");
				nowPage = nowPage.substring(0, index);
			}

			// 添加當前二级分類內容頁的最底层的目录页，设置优先级为1

			for (Element p : ps) {

				nextPage = p.attr("href");

				nextPage = nowPage + nextPage;

				if (!nextPage.equals("")) {
					page.addTargetRequest(
							new Request(nextPage).putExtra("firstLevel", page.getRequest().getExtra("firstLevel"))
									.putExtra("secondLevel", secondLevel).setPriority(1));

				}
			}

		}

		// 获取药品内容页,
		if (page.getRequest().getUrl().matches("http://drugs.dxy.cn/drug/.*")) {

			// 判断页面是否已经爬取,如果数据库集合中已经存在该链接，表示已经爬取
			if (Utils.pageIsSpidered(page.getRequest().getUrl().toString())) {

				page.getResultItems().setSkip(true);

			} else {

				Document doc = Jsoup.parse(page.getRawText());

				Elements ds = doc.select(".m49 dd,dt");

				// 判断是否爬取到页面信息，如果没有，则重新加入到爬取队列
				if (ds.size() == 0) {

					page.addTargetRequest(page.getUrl().toString());

				} else {

					String name = "", content = "", img = "";

					// 获取大分类和小分类
					page.putField("page", page.getRequest().getUrl().toString());

					page.putField("firstLevel", page.getRequest().getExtra("firstLevel"));

					page.putField("secondLevel", page.getRequest().getExtra("secondLevel"));

					// 获取药品内容页的连接中的数字
					String pagePrameter = "";

					Pattern pattern = Pattern.compile("[0-9]+");

					Matcher m = pattern.matcher(page.getUrl().toString());

					while (m.find()) {
						pagePrameter = m.group(0);

					}
					page.putField("pagePrameter", pagePrameter);

					for (Element d : ds) {

						if (name.equals("")) {
							name = d.getElementsByTag("dt").text();
						}

						content = d.getElementsByTag("dd").text();

						img = d.getElementsByTag("dd").select("img").attr("rel");

						if (!(name.equals("") || content.equals(""))) {

							page.putField(name, content);

							page.putField("img", img);
							name = "";
						}
					}

					Elements clickes = Utils.clickes(page);

					// 判断页面中是否包含onclick事件
					if (clickes.size() > 0) {

						int order = 1;

						for (Element e : clickes) {

							// 获取ckick相关信息，获取该click属于的项目
							String entry = e.parent().text();

							String indexAtpage = e.attr("href").toString().substring(1);

							Utils.saveClickUrl(entry, order, indexAtpage, pagePrameter);

							order++;
						}
					}

				}
			}
		}

	}

	public Site getSite() {
		return site;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Spider.create(new TestMagicPlus()).addUrl("http://drugs.dxy.cn/").setScheduler(new PriorityScheduler())
		.addPipeline(new MagicPiplinePlus()).setDownloader(new NewHttpClient2()).thread(1).run();
		
		
	}

}
