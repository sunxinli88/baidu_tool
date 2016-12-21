package com.seassoon.clim.medicine;

import java.util.HashMap;
import java.util.Map;
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

public class MagicMedicinePlus implements PageProcessor {

	private Site site = Site.me().setCycleRetryTimes(3).setRetryTimes(3).setSleepTime(0)
			.setHttpProxy(new HttpHost("101.200.143.21", 8123));

	public void process(Page page) {

		// ��ҳ�棬��ȡ������С�����ҳ��
		if (page.getRequest().getUrl().toString().equals("http://drugs.dxy.cn/")) {

			//����Ϊtrue����ʾ��ҳ����ȡ�Ľ���������û�����
			page.getResultItems().setSkip(true);

			//���ʹ��ǰһ����������Ϊ����һ�ַ�ʽ���ҳ����д���
			Document doc = Jsoup.parse(page.getRawText());// page.getHtml().getDocument();

			Elements trs = doc.select(".hide-more a");

			//�洢��������Ӧ������
			Map<String, String> map = new HashMap<String, String>();

			for (Element tr : trs) {

				String key1 = tr.attr("onclick");

				if (key1.contains(",")) {
					int index = key1.indexOf(",");
					key1 = key1.substring(index + 1, key1.length() - 1);
				}
				// value�����֣��������ֶԴ���Ͷ����������ƥ�䣬�ڶ��������Ǵ���
				map.put(key1, tr.text());
			}

			Elements divs = doc.select(".common_main div");

			for (Element div : divs) {
				
				//key��ȡ�������������Ӧ�����֣������ֺ�һ����������������Ƕ�Ӧ��
				String key = div.attr("id");// select("div:eq("+i+")").
				
				if (key.contains("_")) {
					int index = key.indexOf("_");
					key = key.substring(index + 1, key.length());
				}

				Elements lis = div.select("li");
				
				for (Element li : lis) {

					String href = li.select("a").attr("href");
					// key----��ȡ�����Ӧ�ļ�ֵ����,href---��ȡ�������������硰��ǻ����ҩ��,text---��ȡС���ı�

					String firstLevel = "";

					for (String key1 : map.keySet()) {
						if (key1.equals(key)) {
							firstLevel = map.get(key1).toString();
						}
					}
					// �����ҳ������Ŀ¼���ӣ��������ȼ�Ϊ1������Խ�����ȼ�Խ��
					page.addTargetRequest(new Request(href).putExtra("firstLevel", firstLevel).setPriority(1));

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

				if (!Utils.pageIsSpidered("medicine","urls",href)) {
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
			if (!Utils.pageIsSpidered("medicine","urls",page.getRequest().getUrl().toString())) {
				
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
					
					//��ȡҩƷ����ҳ�������е�����
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
					//***********************����click�¼�***********************************************

					Elements clickes = Utils.clickes(page);
	
					// �ж�ҳ�����Ƿ����onclick�¼�
					if (clickes.size() > 0) {

						int order = 1;

						for (Element e : clickes) {

							// ��ȡckick�����Ϣ������mongoDB��֮�󵥶������ݿ���ȡ����Ϣ������ȡ
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
		Spider.create(new MagicMedicinePlus()).addUrl("http://drugs.dxy.cn/")
		.setScheduler(new PriorityScheduler()).addPipeline(new MagicPiplinePlus()).setDownloader(new NewHttpClient()).thread(1).run();
	}

}
