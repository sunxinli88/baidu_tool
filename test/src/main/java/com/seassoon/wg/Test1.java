package com.seassoon.wg;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;

import com.seassoon.utils.ConnectionDB;

import util.ConnPool;

public class Test1 {

	
	public static ConnPool cp = new ConnPool();

	public static ConnectionDB db = new ConnectionDB("jdbc:mysql://localhost:3306/aa?useUnicode=true&characterEncoding=utf8","root","123");
	
	static public BlockingQueue<Map<String, Object>> bq = null;

	static List<Map<String, Object>> pool = null;

	/**
	 * 两种实现方式：1。规范的生产消费模式；2.for循环加上线程池，（需要理解）
	 * 
	 * 
	 */
	public static void main(String args[]) {
		

		String sql = "SELECT a.EVENTID,a.INFOBCNAME,count(1) count FROM wg_baidumap a "
				+ "WHERE EVENTID IS NOT NULL GROUP BY a.EVENTID, a.INFOBCNAME";
		
		List<Map<String, Object>> getData = db.excuteQuery(sql,null);

//		pool = cp.executeQuery(sql, new Object[] {});

		bq = new LinkedBlockingQueue<>(getData);

		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 10, 100, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>());

		// new RunAdd().start();

		int i = 0;
		while (i < 1) {
			i++;
			MyRunnable mr = new MyRunnable();
			threadPoolExecutor.execute(mr);
		}
		threadPoolExecutor.shutdown();
	}
}

class MyRunnable implements Runnable {
	
	List<Object> params = new ArrayList<Object>();

	@Override
	public void run() {
		// TODO Auto-generated method stub
		Map<String, Object> m = null;
		String sql, sql1 = "";
		while ((m = Test1.bq.poll()) != null) {

			System.out.println("队列大小：【" + Test1.bq.size() + "】");

			Long count = (Long) m.get("count");

			sql1 = "select * from wg_baidumap where EVENTID = " + m.get("EVENTID") + " and INFOBCNAME = \'"
					+ m.get("INFOBCNAME") + "\'";

			String sql2 = "select * from wg_baidumap where EVENTID = 10147 and INFOBCNAME = \'街面秩序\'";

			List<Map<String, Object>> result = Test1.db.excuteQuery(sql1, null);

			Object[] obj = new Object[8];
			obj[0] = result.get(0).get("EVENTID");
			obj[1] = result.get(0).get("INFOSCNAME");
			obj[2] = count;
			obj[3] = result.get(0).get("INFOBCNAME");
//			obj[4] = obj[3] + "|" + obj[0] + "md5";
			
			obj[4] = DigestUtils.md5Hex(obj[3] + "|" + obj[0]);

			System.out.println(obj[0] + "" + obj[3]);
			if (count == 1) {
				obj[5] = result.get(0).get("DISCOVERTIME");
				obj[7] = result.get(0).get("SOLVINGTIME");
			} else {
				try {
					obj[5] = oneTime(result, 1);
					obj[7] = oneTime(result, 0);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if (obj[5] == null || obj[7] == null)
				obj[6] = null;
			else {
				String disTim = (String) obj[5];
				String endTim = (String) obj[7];

				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

				try {
					Long star = df.parse(disTim).getTime();
					Long end = df.parse(endTim).getTime();
					obj[6] = (end - star) / 60000;

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			String[] col = {"EVENTID","INFOSCNAME","CC_AMOUNT","INFOBCNAME","TT_ID","DISCOVER_TIME","EXCUTE_TIME","FINISH_TIME"};
			List<String> cols = new ArrayList<String>();
			
			for(String s:col){
				cols.add(s);
			}
			//批量插入**********************
			
			if(params.size()<800){
				for(Object o:obj){
					params.add(o);
				}
			}else{
				String tb = "wg_event";
				Test1.db.insert(Test1.db,cols, params,tb);
				params.clear();
			}
		}

	}

	public Object oneTime(List<Map<String, Object>> result, int flag) throws ParseException {
		// flag=0,求SOLVING的最大时间；=1求DISCOVER的最小时间

		List<Long> ls = new ArrayList<Long>();

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		if (flag == 0) {
			for (Map<String, Object> m : result) {
				if (m.get("SOLVINGTIME") == null)
					continue;
				String s = (String) m.get("SOLVINGTIME");
				Long l = df.parse(s).getTime();
				ls.add(l);
			}

		} else if (flag == 1) {
			for (Map<String, Object> m : result) {
				if (m.get("DISCOVERTIME") == null)
					continue;
				String s = (String) m.get("DISCOVERTIME");
				Long l = df.parse(s).getTime();
				ls.add(l);
			}
		}
		if (ls.size() == 0)
			return null;
		Long mm = ls.get(0);

		if (flag == 0) {
			for (Long o : ls) {
				if (o > mm)
					mm = o;
			}
		} else if (flag == 1) {
			for (Long o : ls) {
				if (o < mm)
					mm = o;
			}
		}
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Object o = ft.format(mm);

		return o;
	}

}
