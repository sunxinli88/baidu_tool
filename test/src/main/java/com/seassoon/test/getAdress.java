package com.seassoon.test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.baidu.util.BaiduMapUtils;
import com.baidu.util.Location;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Stopwatch;
import com.seassoon.utils.ConnectionDB;

public class getAdress {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		final ConnectionDB db = new ConnectionDB(
				"jdbc:mysql://localhost:3306/aa?useUnicode=true&characterEncoding=utf8", "root", "123");

//		String sql = "select addresstjw.* from addresstjw  left join addresstjw_copy on  addresstjw.TASKID = addresstjw_copy.TASKID      where addresstjw.FLAG_TIME = 1    and   addresstjw_copy.TASKID is null   ";
//		String sql ="select addresstjw.* from addresstjw LEFT JOIN addresstjw_copy3 on addresstjw.TASKID =  addresstjw_copy3.taskid where addresstjw_copy3.taskid is null and addresstjw.FLAG_TIME!=2";
		String sql = "select * from t3 where lon is  not null";
		
		List<Map<String, Object>> x = db.excuteQuery(sql, null);

		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1, 100, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>());

		final AtomicInteger atomicInteger = new AtomicInteger(0);

		final ObjectMapper mapper = new ObjectMapper();

		for (Map record : x) {

			final Map record_final = record;

			threadPoolExecutor.execute(new Runnable() {

				@Override
				public void run() {
					Stopwatch stopwatch = new Stopwatch();
					stopwatch.start();
					Location location;
					try {
						// location =
						// BaiduMapUtils.geocoder(record_final.get("ADDRESS").toString());

//						String json = BaiduMapUtils.geocoderV2("�Ϻ����ֶ�����" + record_final.get("ADDRESS").toString());
						String lon = record_final.get("LON").toString();
						String lat = record_final.get("LAT").toString();
						String taskid = record_final.get("TASKID").toString();
						String address1 = record_final.get("ADDRESS").toString();						
						
						Map<String,String> address = BaiduMapUtils.geocoder_location(lat+","+lon);
						
						System.out.println(taskid+address1+":"+ address.get("province"));
						
						db.executeUpdate("insert into  reverseLon (TASKID,ADDRESS,LON,LAT,province,city,district,address1,FLAG_TIME) values(?,?,?,?,?,?,?,?,2)",
								new Object[] {taskid ,address1 ,lon,lat,address.get("province"),address.get("city"),address.get("district"),address.get("address")});
						
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
//						db.executeUpdate("update  lon_lat set FLAG=1 where TASKID=? ",
//								new Object[] { record_final.get("TASKID") });

					}

					atomicInteger.incrementAndGet();
					System.out.println(atomicInteger.intValue());
					try {
						Thread.sleep(100L);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			while (true) {
				if (threadPoolExecutor.getQueue().size() > 10000) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					break;
				}

			}

		}

		threadPoolExecutor.shutdown();

	}

}
