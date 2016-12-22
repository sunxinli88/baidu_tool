package com.seassoon.clim.dw;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;
import com.mayidaili.test.Ip;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.downloader.HttpClientGenerator;

public class NewHttpClient extends HttpClientDownloader{
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	  private HttpClientGenerator httpClientGenerator = new HttpClientGenerator();
	  
	  private final Map<String, CloseableHttpClient> httpClients = new HashMap<String, CloseableHttpClient>();
	  
	  //****************************  添加代码   *****************************************
	  private static Ip proxyIp = new Ip();
	
    private CloseableHttpClient getHttpClient(Site site) {
        if (site == null) {
            return httpClientGenerator.getClient(null);
        }
        String domain = site.getDomain();
        CloseableHttpClient httpClient = httpClients.get(domain);
        if (httpClient == null) {
            synchronized (this) {
                httpClient = httpClients.get(domain);
                if (httpClient == null) {
                    httpClient = httpClientGenerator.getClient(site);
                    httpClients.put(domain, httpClient);
                }
            }
        }
        return httpClient;
    }
	
	 public Page download(Request request, Task task) {
	        Site site = null;
	        if (task != null) {
	            site = task.getSite();
	        }
	        Set<Integer> acceptStatCode;
	        String charset = null;
	        Map<String, String> headers = null;
	        if (site != null) {
	            acceptStatCode = site.getAcceptStatCode();
	            charset = site.getCharset();
	            headers = site.getHeaders();
	            
	            //添加代码
	            headers.put("Proxy-Authorization",proxyIp.getAuthHeader());
	            headers.put("Cookie", 	      
//	            		"__auc=9dd951c81589528e5d21c66baba; _ga=GA1.2.486900994.1479981979; JUTE_BBS_DATA=4246e186e2b23d5771961b8fd4fa022907dab2661180226b9c9cc3900057815560f97290091b827ff7638551c093fa7bf062027e4733041d3a3132127cfca108bd5190284d4ad400692b39640469bc3a; DRUGSSESSIONID=EE2CCB3D3DC9F06FE00FD800346633EF-n1; __utmt=1; __utma=129582553.486900994.1479981979.1481159418.1481163865.47; __utmb=129582553.6.10.1481163865; __utmc=129582553; __utmz=129582553.1479986767.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); Hm_lvt_d1780dad16c917088dd01980f5a2cfa7=1480301921,1480382596,1480415384,1481073095; Hm_lpvt_d1780dad16c917088dd01980f5a2cfa7=1481163890");
	        "__auc=9dd951c81589528e5d21c66baba; _ga=GA1.2.486900994.1479981979; JUTE_BBS_DATA=4246e186e2b23d5771961b8fd4fa022907dab2661180226b9c9cc3900057815560f97290091b827ff7638551c093fa7bf062027e4733041d3a3132127cfca108bd5190284d4ad400692b39640469bc3a; DRUGSSESSIONID=BD405BFB9D186302DA5F16B0F69FBA63-n2; __utmt=1; __utma=129582553.486900994.1479981979.1481163865.1481248151.48; __utmb=129582553.5.10.1481248151; __utmc=129582553; __utmz=129582553.1479986767.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); Hm_lvt_d1780dad16c917088dd01980f5a2cfa7=1480301921,1480382596,1480415384,1481073095; Hm_lpvt_d1780dad16c917088dd01980f5a2cfa7=1481248177");
	        } else {
	            acceptStatCode = Sets.newHashSet(200);
	        }
	        logger.info("downloading page {}", request.getUrl());
	        CloseableHttpResponse httpResponse = null;
	        int statusCode=0;
	        try {
	            HttpUriRequest httpUriRequest = getHttpUriRequest(request, site, headers);
	            httpResponse = getHttpClient(site).execute(httpUriRequest);
	            statusCode = httpResponse.getStatusLine().getStatusCode();
	            request.putExtra(Request.STATUS_CODE, statusCode);
	            if (statusAccept(acceptStatCode, statusCode)) {
	                Page page = handleResponse(request, charset, httpResponse, task);
	                onSuccess(request);
	                return page;
	            } else {
	                logger.warn("code error " + statusCode + "\t" + request.getUrl());
	                return null;
	            }
	       
	        }catch (IOException  e) {
//	            logger.warn("download page " + request.getUrl() + " error", e);
	            if (site.getCycleRetryTimes() > 0) {
	                return addToCycleRetry(request, site);
	            }
	            onError(request);
	            return null;
	        } finally {
	        	request.putExtra(Request.STATUS_CODE, statusCode);
	            try {
	                if (httpResponse != null) {
	                    //ensure the connection is released back to pool
	                    EntityUtils.consume(httpResponse.getEntity());
	                }
	            } catch (IOException e) {
	                logger.warn("close response fail", e);
	            }
	        }
	    }
}
