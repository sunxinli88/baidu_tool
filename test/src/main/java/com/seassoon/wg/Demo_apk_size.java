package com.seassoon.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import com.seassoon.common.model.TbApkInfo;
import com.seassoon.dao.ApkDao;
import com.seassoon.util.UUIDUtil;

/**
 * 统计APK流量大小 --> 只跑前一个小时的
 * @author sxad
 *
 */
public class Demo_apk_size {
	
//	private List<Map<String,String>> statisticsList = new ArrayList<Map<String,String>>();
	
//	private ExecutorService threadPool = Executors.newFixedThreadPool(10);
	
	private BlockingQueue<Map<String,String>> lineQueue = null;
	
	private Map<String,TbApkInfo> toUpdateMap = null;
	
	private MyThreadPoolExecutor threadPool = null;
	
	private MyReadAndSaveThreadPoolExecutor readAndSavePool = null;	
	
	private BlockingQueue<TbApkInfo> queue = null;
	
	private static Logger log = Logger.getLogger(Demo_apk_size.class);
	
	private ApkDao dao = new ApkDao();
	
	private List<TbApkInfo> toUpdateList = null;
	
//	private List<String> logLines = null;
	
	private String logPath;
	
//	private Properties config;
	
	
	private void releaseResource() {
		threadPool = null;
		queue = null;
		toUpdateList = null;
//		logLines = null;
		System.gc();
	}
	public Demo_apk_size(String logPath) throws Exception{
		this.logPath = logPath;
//		config = new Properties();
//		config.load(Demo_apk_size.class.getClassLoader().getResourceAsStream("config.properties"));
//		this.logPath = config.getProperty("logPath");
	}
	
	public static void main(String[] args) throws Exception {
//		args = new String[]{"C:\\Users\\sxad\\Desktop\\2016-4-11\\"};
//		String logPath = args[0];
//		if(args == null || args[0] == null ){
//			log.info("未输入路径");
//			return ;
//		}
		String parentDir = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		String subDir = new SimpleDateFormat("yyyy-MM-dd-HH").format(new Date(System.currentTimeMillis()-(60*60*1000))); 
//		/root/apk   /logs/APK_LOG
//		String logPath = "/root/APK_LOG/"+parentDir+"/"+subDir+"/";
		String logPath = "/logs/APK_LOG/"+parentDir+"/"+subDir+"/";
//		String logPath = "C:\\Users\\sxad\\Desktop\\"+parentDir+"\\"+subDir+"\\";
		Demo_apk_size demo = new Demo_apk_size(logPath);
		demo.begin();
	}

	private void begin() {
		List<String> fileList = new ArrayList<String>();  
		int count = 0;
		File[] files = new File(logPath).listFiles();
		for(int j=0;j<files.length;j++){
			File f = files[j];
			if(f.isDirectory()){
				continue;
			}
			fileList.add(f.getAbsolutePath());
			count++;
		}
		log.info("共有【"+ count + "】个日志文件");
		for(String filePath : fileList){
			queue = new LinkedBlockingQueue<TbApkInfo>();	
			toUpdateList = new ArrayList<TbApkInfo>();
			//读取日志文件
			readFile(filePath);
			//访问库中所有的URL
			updateApkSize();
			//释放资源
			releaseResource();
		}
	}
	
	class MyReadAndSaveRunnable implements Runnable {

		@Override
		public void run() {
			Map<String,String> data = null;
			while((data = (lineQueue.poll()))!=null){
				TbApkInfo t = new TbApkInfo();
				t = t.findFirst("select * from tb_apk_info a where a.url = ?",data.get("url"));
				if(t == null){
					t = new TbApkInfo();
					t.setId(UUIDUtil.randomUUID());
					t.setInterceptTime(data.get("interceptTime"));
					t.setUrl(data.get("url"));
					t.setUpdateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
					t.setFailedFlag("0");
//					list.add(t);
					try{
						t.save();
					}catch(Exception e){
					}
				}else{
					t.setInterceptTime(data.get("interceptTime"));
					t.setUpdateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
					toUpdateMap.put(data.get("url"), t);
				}
			}
		}
	}

	class MyRunnable implements Runnable {
		
		@Override
		public void run() {
			TbApkInfo t = null;
			while((t = queue.poll())!= null){
				InputStream is = null;
				try{
					String url = "http://"+t.getUrl();
//					log.info(url);
					URL realUrl = new URL(url);
					HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
					connection.setInstanceFollowRedirects(true);// 设置本次连接是否自动重定向
//						HttpURLConnection.setFollowRedirects(false);
					connection.setRequestProperty("Accept-Encoding", "identity");
					connection.setRequestProperty("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
//						connection.setRequestProperty("Accept-Encoding","gzip, deflate, sdch");
					connection.setRequestProperty("Accept-Language","zh-CN,zh;q=0.8");
					connection.setRequestProperty("Cache-Control","max-age=0");
					connection.setRequestProperty("Connection", "keep-alive");
//						connection.setRequestProperty("cookie", "vjuids=16807a6d6.1527758e911.0.105681d3; hxck_sq_common=LoginStateCookie=; bdshare_firstime=1453707444275; ch_id=84d486b8a50518eb66d9cd978c79fa53; hxck_zqzx_px_gourl=http://vip.px.hexun.com/vipmobileapp/reg/regjump.aspx?UserID=27394773&url=http://chat.hexun.com/787; record=http%3a%2f%2flogo1.tool.hexun.com%2f1d502a2-96.jpg%2c%e8%82%a1%e6%b5%b7%e7%ba%b5%e6%a8%aa%e7%8e%8b%e6%97%ad%e7%9b%b4%e6%92%ad..%2c%2f787; _ga=GA1.2.1635692645.1453701261; Hm_lvt_cb1b8b99a89c43761f616e8565c9107f=1453709187,1453712301,1453770093,1453774426; Hm_lpvt_cb1b8b99a89c43761f616e8565c9107f=1453802130; __utma=194262068.1635692645.1453701261.1453799829.1453802130.10; __utmb=194262068.1.10.1453802130; __utmc=194262068; __utmz=194262068.1453790426.8.8.utmcsr=stock.hexun.com|utmccn=(referral)|utmcmd=referral|utmcct=/broadcast/; vjlast=1453701262.1453770070.13; HexunTrack=SID=20160125135429146904c5bf520bd476ab49745cb3b4bb82d&CITY=42&TOWN=421200");
//						connection.setRequestProperty("Upgrade-Insecure-Requests", "1");
//						connection.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.111 Safari/537.36");
					System.setProperty("sun.net.client.defaultConnectTimeout", "30000");  
					System.setProperty("sun.net.client.defaultReadTimeout", "30000");  
					Integer size = null;
					try{
						connection.connect();
						size = connection.getContentLength();
						is = connection.getInputStream();
						is.close();
					}catch(Exception e){
//						log.info(e);
						int failedFlag = Integer.parseInt(t.getFailedFlag()) + 1;
						t.setFailedFlag(String.valueOf(failedFlag));
					}
//					log.info("apk大小为：【" + size + "】");
					if(size != null){
						t.setSize(String.valueOf(size));
					}
					/*boolean res = */t.update();
//					log.info("更新结果：【"+ res + "】");
				}catch(Exception e){
					log.error("访问URL异常",e);
				}finally {
					try{
						if(is != null){
							is.close();
						}
					}catch(Exception e){
					}
				}
			}
		}
		
	}
	
	private void updateApkSize() {
		threadPool = new MyThreadPoolExecutor(50, 80,0, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(80));
		queue.addAll(toUpdateList);
		System.out.println("size:"+queue.size());
		int i = 0;
		while (i<40) {
			i++;
			Runnable mr = new MyRunnable();
			threadPool.execute(mr);
		}
//		threadPool.shutdown();
		threadPool.isEndTask();
		threadPool.shutdownNow();
		
	}



	private void readFile(String filePath) {
		BufferedReader br = null; 
		try{
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),"utf-8"),512*1024);
			Pattern p1 = Pattern.compile("^.*Mozilla/.*$");
			toUpdateMap = new Hashtable<String, TbApkInfo>();
			lineQueue = new LinkedBlockingQueue<Map<String,String>>();
			String line = null;
			while((line = br.readLine()) != null){
				if(!"".equals(line)){
//					logLines.add(line.trim());
					String[] arr = line.split(" ", 8);
					String interceptTime = arr[0]+" "+arr[1];
//					String vlan = arr[2];
					String status = arr[3];
					String url = arr[4];
//					String source = arr[5];
//					String afterReplaceUrl = arr[6];
					String userAgent = arr[7];
					if(url.contains("?")){
						url = url.substring(0, url.indexOf("?"));
					}
					if(userAgent==null || "".equals(userAgent) || status.contains("APKFILTER-3")){
						continue;
					}
					Matcher m = p1.matcher(userAgent);
					if(!m.matches() && !userAgent.contains("AndroidDownloadManager")){
						continue;
					}
					if(url.length()>255){
						continue;
					}
					Map<String,String> data = new HashMap<String, String>();
					data.put("url", url);
					data.put("interceptTime", interceptTime);
					lineQueue.add(data);
				}
			}
			
			readAndSavePool = new MyReadAndSaveThreadPoolExecutor(50, 80,0, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(80));
			log.info("lineQueue_size:"+lineQueue.size());
			int i = 0;
			while (i<40) {
				i++;
				Runnable mr = new MyReadAndSaveRunnable();
				readAndSavePool.execute(mr);
			}
//			threadPool.shutdown();
			readAndSavePool.isEndTask();
			readAndSavePool.shutdownNow();
			readAndSavePool = null;
			lineQueue = null;
			System.gc();
			
			
			toUpdateList = new TbApkInfo().find("select * from tb_apk_info a where a.size is null and (a.failed_flag+0)<5");
			List<TbApkInfo> alreadyExistsList = new ArrayList<TbApkInfo>();
			alreadyExistsList.addAll(toUpdateMap.values());
			int res = dao.updateList(alreadyExistsList);
			log.info("更新已经存在的总数：【"+res+"】");
		}catch(Exception e){
			log.error(e);
		}finally{
			try{
				if(br != null){
					br.close();
				}
			}catch(Exception e){
			}
		}
	}
	
	
	
	class MyThreadPoolExecutor extends ThreadPoolExecutor {
		
		private boolean hasFinish = false;

		public MyThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
				BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
			super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
			// TODO Auto-generated constructor stub
		}

		public MyThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
				BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
			super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
			// TODO Auto-generated constructor stub
		}

		public MyThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
				BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
			super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
			// TODO Auto-generated constructor stub
		}

		public MyThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
				BlockingQueue<Runnable> workQueue) {
			super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
			// TODO Auto-generated constructor stub
		}
		

		protected void afterExecute(Runnable r, Throwable t) {
			log.info(Thread.currentThread().getName()+ "afterExecute --> 生产队列大小："+queue.size());
			// TODO Auto-generated method stub
			super.afterExecute(r, t);
			synchronized (this) {
				System.out.println("自动调用了....afterEx 此时getActiveCount()值:" + this.getActiveCount());
				if (this.getActiveCount() <= 1)// 已执行完任务之后的最后一个线程
				{
					this.hasFinish = true;
					this.notify();
				} // if
			} // synchronized
		}

		public void isEndTask() {
			synchronized (this) {
				while (this.hasFinish == false) {
					System.out.println( Thread.currentThread().getName() + "等待线程池所有任务结束: wait...");
					try {
						this.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

	}	
	
	
	class MyReadAndSaveThreadPoolExecutor extends ThreadPoolExecutor {
		
		private boolean hasFinish = false;

		public MyReadAndSaveThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
				BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
			super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
			// TODO Auto-generated constructor stub
		}

		public MyReadAndSaveThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
				BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
			super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
			// TODO Auto-generated constructor stub
		}

		public MyReadAndSaveThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
				BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
			super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
			// TODO Auto-generated constructor stub
		}

		public MyReadAndSaveThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
				BlockingQueue<Runnable> workQueue) {
			super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
			// TODO Auto-generated constructor stub
		}
		

		protected void afterExecute(Runnable r, Throwable t) {
//			log.info(Thread.currentThread().getName()+ "afterExecute --> 生产队列大小："+lineQueue.size());
			// TODO Auto-generated method stub
			super.afterExecute(r, t);
			synchronized (this) {
//				System.out.println("自动调用了....afterEx 此时getActiveCount()值:" + this.getActiveCount());
				if (this.getActiveCount() <= 1)// 已执行完任务之后的最后一个线程
				{
					this.hasFinish = true;
					this.notify();
				} // if
			} // synchronized
		}

		public void isEndTask() {
			synchronized (this) {
				while (this.hasFinish == false) {
					System.out.println( Thread.currentThread().getName() + "等待线程池所有任务结束: wait...");
					try {
						this.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

	}	
	
	
}
