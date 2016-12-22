package com.seassoon.clim.dw;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

public class GithubRepoPageProcessor implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(100);

    @Override
    public void process(Page page) {
        
        page.putField("author", page.getUrl().regex("http://www\\.med126.com/pharm/(\\w+)").toString());
//        page.putField("author", page.getUrl().regex("http://www\\.med126.com/pharm/2010/(\\w+)").toString());
//    	  page.putField("author", page.getUrl().regex("http://www\\.pharmnet\\.com\\.cn/tcm/zybb/index\\.cgi\\?p=(\\w+)").toString());
//        page.putField("author", page.getUrl().regex("https://github\\.com/(\\w+)/.*").toString());
//        page.putField("name", page.getHtml().xpath("//div[@id='tablebig']/h1/a/text()").toString());
//        
//        page.getHtml().$("#h1");
    	  
//    	  Elements eles = doc.select("body>table:eq(6)").select("tbody:eq(0)").select("tr:eq(0)")
//					.select("td:eq(1)").select("table:eq(1)").select("tbody:eq(0)").select("tr:eq(3)")
//					.select("td:eq(0)").select("table:eq(0)").select("tbody:eq(0)").select("tr:eq(0)")
//					.select("td:eq(0)").select("div:eq(1)").select("span:eq(0)").select("font:eq(0)");// .select("*");
//        String s = page.getRequest().getUrl();
        if(page.getRequest().getUrl().equals("http://www.med126.com/pharm/cnmed/")){
        	//在爬取队列中加入网页爬取入口的页面目录,第一页
            List<String> list=  page.getHtml().$(".listdlmid .showpage a").links().all();
            page.addTargetRequests(list); 
            System.out.println(list.size());
        }
        if(page.getRequest().getUrl().matches("http://www\\.med126\\.com/pharm/cnmed/\\w*")){ 
//            Document doc =  Jsoup.parse(page.getRawText());
//            
//            Elements showP = doc.select(".listdlmid .showpage a");
        		//此时的页面是每一页，获取每一个页面的医药品列表以及药品的链接，把药品的链接加入堆栈
        	
            	List<String> list=  page.getHtml().$(".listdlmid .e").links().all();
                page.addTargetRequests(list);        	
          
//          if(showP.size() !=0){
//          	  List<String> urls = new ArrayList<>();
//                for(Element sp:showP){
//              	  String linkHref = sp.attr("href");
//              	  String s = sp.text();
//              	  urls.add(linkHref);
//              	  System.out.println(s+" "+linkHref);
//                }
//                
//                page.addTargetRequests(urls); 
//            }
            
        } //else if( page.getRequest().getUrl())//contains("http://www.med126.com/pharm/cnmed/")){
//        page.getHtml().xpath("//div[@id='tablebig']/h1/a/text()").toString());
        else if(page.getResultItems().get("author").toString().startsWith("20")){
        	
//        	Document doc =  Jsoup.parse(page.getRawText());
//        	
//        	Elements showP = doc.select(".med126content");
        	
        	Document doc = page.getHtml().getDocument();//.$(".med126content>table>tbody tr").all();
        	
        	Elements trs = doc.select(".med126content tr");
        	
        	for(Element tr:trs){
        		String s = tr.attr("id");
        		page.putField(page.getHtml().xpath("//tr[@id='"+s+"']/td[@class='td1c']/text()").toString(),
            			page.getHtml().xpath("//tr[@id='"+s+"']/td[@class='td1']/text()").toString());
        	}
        	
//        	page.putField(page.getHtml().xpath("//tr[@id='BM']/td[@class='td1c']/text()").toString(),
//        			page.getHtml().xpath("//tr[@id='BM']/td[@class='td1']/text()").toString());
//        	
//        	page.putField(page.getHtml().xpath("//tr[@id='HYPY']/td[@class='td1c']/text()").toString(),
//        			page.getHtml().xpath("//tr[@id='HYPY']/td[@class='td1']/text()").toString());//$(".YCJY>td1").toString());
//        	       	        	
//        	page.putField(page.getHtml().xpath("//tr[@id='YWM']/td[@class='td1c']/text()").toString(),
//        			page.getHtml().xpath("//tr[@id='YWM']/td[@class='td1']/text()").toString());
//        	     	
//        	page.putField(page.getHtml().xpath("//tr[@id='YCJY']/td[@class='td1c']/text()").toString(),
//        			page.getHtml().xpath("//tr[@id='YCJY']/td[@class='td1']/text()").toString());//$(".YCJY>td1").toString());
//        	
//        	page.putField(page.getHtml().xpath("//tr[@id='DZWXT']/td[@class='td1c']/text()").toString(),
//        			page.getHtml().xpath("//tr[@id='DZWXT']/td[@class='td1']/text()").toString());
//        	
//        	page.putField(page.getHtml().xpath("//tr[@id='BM']/td[@class='td1c']/text()").toString(),
//        			page.getHtml().xpath("//tr[@id='BM']/td[@class='td1']/text()").toString());
//        	
//        	page.putField(page.getHtml().xpath("//tr[@id='HYPY']/td[@class='td1c']/text()").toString(),
//        			page.getHtml().xpath("//tr[@id='HYPY']/td[@class='td1']/text()").toString());//$(".YCJY>td1").toString());
//        	       	        	
//        	page.putField(page.getHtml().xpath("//tr[@id='YWM']/td[@class='td1c']/text()").toString(),
//        			page.getHtml().xpath("//tr[@id='YWM']/td[@class='td1']/text()").toString());
//        	     	
//        	page.putField(page.getHtml().xpath("//tr[@id='YCJY']/td[@class='td1c']/text()").toString(),
//        			page.getHtml().xpath("//tr[@id='YCJY']/td[@class='td1']/text()").toString());//$(".YCJY>td1").toString());
//        	
//        	page.putField(page.getHtml().xpath("//tr[@id='DZWXT']/td[@class='td1c']/text()").toString(),
//        			page.getHtml().xpath("//tr[@id='DZWXT']/td[@class='td1']/text()").toString());
        	
        	List<String> list=  page.getHtml().$(".med126tablefull tr").all();
        	
        	System.out.println(page.getResultItems());
        	
        }

//          Elements eles = doc.select("div[class^='listbg']");//据说是选取class是以listtbg字符串开头的div，但是无效
          //选取class等于A或者B的方法是只要二者中间加上，就可以了
//          Elements eles = doc.select(".listdlmid .listbgs,.listbgb .e");
//          Document doc =  Jsoup.parse(page.getRawText());
//          Elements eles = doc.select(".listdlmid .e");
//          
//          for(Element e:eles){
//        	  String linkHref = e.attr("href");
//        	  String s = e.text();
//        	  System.out.println(s+" "+linkHref);
//          }
        
        	
        }
        
        
        
       
          
          
//          Elements s = doc.select("body>table:eq(5)").select("tbody:eq(0)").select("tr:eq(4)")
//        		  .select("td:eq(0)").select("table:eq(0)").select("tbody:eq(0) table a:nth-child(2)");
//          
//          
//          Elements node;
//          Elements el_trs;
//          int i=0;
//          for(Element el:s){
//        	  Elements el_td = el.getElementsByTag("td");
//        	   el_trs= el.select("tr:eq("+i+")");
//        	   		int j =0;
//        	   		for(Element el_tr:el_trs){
//        	   			String ss = "td:eq("+j+")";
//        	   			 node = el_tr.select(ss).select("table:eq(0)").select("tbody:eq(0)").select("tr:eq(0)").select("td:eq(0)").select("a:eq(0)");
// 
//        	   			 for(Element e:node){
//        	   				String linkHref = e.attr("href");
//        	   				System.out.println(linkHref);
//        	   			 }
//        	   			
//        	   			j++;
//        	   		}
//        	  i++;
//        	  
//          }
//          System.out.println(s);
        
//        if (page.getResultItems().get("name")==null){
//            //skip this page
//            page.setSkip(true);
//        }
//        page.putField("readme", page.getHtml().xpath("//div[@id='readme']/tidyText()"));
    	
//        page.addTargetRequests(page.getHtml().links().regex("(http://www\\.med126\\.com/pharm/cnmed/.*)").all());
//    	page.addTargetRequests(page.getHtml().links().regex("(http://www\\.pharmnet\\.com\\.cn/tcm/zybb/index\\.cgi\\?p=(\\w+))").all());
    

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
//        Spider.create(new GithubRepoPageProcessor()).addUrl("http://www.pharmnet.com.cn/tcm/zybb/index.cgi?p=1").thread(5).run();
    	Spider.create(new GithubRepoPageProcessor()).addPipeline(new FilePipeline("/data/temp/webmagic/")).addUrl("http://www.med126.com/pharm/cnmed/").thread(5).run();
//    	Spider.create(new GithubRepoPageProcessor()).addPipeline(new FilePipeline("/data/temp/webmagic/")).addUrl("http://www.med126.com/pharm/cnmed/").thread(5).run();
    }
}
