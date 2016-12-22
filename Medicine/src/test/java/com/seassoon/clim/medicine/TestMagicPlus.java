
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

		// ��ҳ�棬��ȡ������С�����ҳ��
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
				// value�����֣��������ֶԴ���Ͷ����������ƥ�䣬�ڶ��������Ǵ���
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
					// �����ҳ������Ŀ¼���ӣ��������ȼ�Ϊ1
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

		// ����С��������ҳ���ҩƷ������ҳ��Ŀ¼����
		if (page.getRequest().getUrl().matches("http://drugs.dxy.cn/category/.*")) {

			page.getResultItems().setSkip(true);

			Document doc = Jsoup.parse(page.getRawText());// page.getHtml().getDocument();

			Elements Level2 = doc.select(".common_hd");

			String secondLevel = Level2.select("div:eq(0)").text();

			// ��ȡ�������࣬ͬʱ�ڸ����ֺ�����ϸ���ҩƷ�����ж�������¼
			secondLevel += Level2.select("div:eq(1) span").text();

			Elements lis = doc.select(".list a");

			// ���ҩƷ����ҳ��Ŀ¼����,�������ȼ�2
			for (Element li : lis) {

				String href = li.select("a").attr("href");

				if (!Utils.pageIsSpidered(href)) {
					page.addTargetRequest(
							new Request(href).putExtra("firstLevel", page.getRequest().getExtra("firstLevel"))
									.putExtra("secondLevel", secondLevel).setPriority(2));
				}
			}

			// ȥ��Ŀ¼ҳurlĩβ������ַ���
			String nextPage = "";

			Elements ps = doc.select(".pagination a");

			String nowPage = page.getUrl().toString();

			if (nowPage.contains("?page=")) {
				int index = nowPage.indexOf("?page=");
				nowPage = nowPage.substring(0, index);
			}

			// ��Ӯ�ǰ���������퓵���ײ��Ŀ¼ҳ���������ȼ�Ϊ1

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

		// ��ȡҩƷ����ҳ,
		if (page.getRequest().getUrl().matches("http://drugs.dxy.cn/drug/.*")) {

			// �ж�ҳ���Ƿ��Ѿ���ȡ,������ݿ⼯�����Ѿ����ڸ����ӣ���ʾ�Ѿ���ȡ
			if (Utils.pageIsSpidered(page.getRequest().getUrl().toString())) {

				page.getResultItems().setSkip(true);

			} else {

				Document doc = Jsoup.parse(page.getRawText());

				Elements ds = doc.select(".m49 dd,dt");

				// �ж��Ƿ���ȡ��ҳ����Ϣ�����û�У������¼��뵽��ȡ����
				if (ds.size() == 0) {

					page.addTargetRequest(page.getUrl().toString());

				} else {

					String name = "", content = "", img = "";

					// ��ȡ������С����
					page.putField("page", page.getRequest().getUrl().toString());

					page.putField("firstLevel", page.getRequest().getExtra("firstLevel"));

					page.putField("secondLevel", page.getRequest().getExtra("secondLevel"));

					// ��ȡҩƷ����ҳ�������е�����
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

					// �ж�ҳ�����Ƿ����onclick�¼�
					if (clickes.size() > 0) {

						int order = 1;

						for (Element e : clickes) {

							// ��ȡckick�����Ϣ����ȡ��click���ڵ���Ŀ
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
