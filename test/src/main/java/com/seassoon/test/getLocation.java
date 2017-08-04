package com.seassoon.test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.util.EntityUtils;

import com.baidu.util.BaiduMapUtils;
import com.baidu.util.Location;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Stopwatch;
import com.seassoon.utils.ConnectionDB;

public class getLocation {

	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		// List<Record> records = Db.find("select * from view_patient_location
		// where address_location is null");

		final ConnectionDB db = new ConnectionDB(
				"jdbc:mysql://localhost:3306/aa?useUnicode=true&characterEncoding=utf8", "root", "123");

		String sql = "select * from addresstjw where FLAG_TIME = 3 and lon is null   ";

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

						String json = BaiduMapUtils.geocoderV2("�Ϻ����ֶ�����" + record_final.get("ADDRESS").toString());

						JsonNode jsonNode = mapper.readTree(json);

						stopwatch.stop();
						System.out.println("get:" + stopwatch.elapsedMillis());

						String status = jsonNode.get("status").asText();

						String msg = jsonNode.get("msg") != null ? jsonNode.get("msg").asText() : null;

						if (status.equals("0")) {
							stopwatch.start();
							String s = (String) record_final.get("TASKID");
							db.executeUpdate("update  addresstjw  set FLAG_TIME=?,LON=?,LAT=?,FLAG=0 where TASKID=? ",
									new Object[] { 3,jsonNode.get("result").get("location").get("lng").asText(),
											jsonNode.get("result").get("location").get("lat").asText(),
											(String) record_final.get("TASKID") });
							stopwatch.stop();
							System.out.println("update:" + stopwatch.elapsedMillis());
						} else {

							db.executeUpdate("update  addresstjw set FLAG_TIME=?,FLAG=?,msg=? where TASKID=? ",
									new Object[] { 3,status, msg, record_final.get("TASKID") });

						}

						// else if(location==null){
						// db.executeUpdate("update addressTjw set FLAG=1 where
						// TASKID=? ",new Object[]{record_final.get("TASKID")});
						// }
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						db.executeUpdate("update  addresstjw set FLAG=1 where TASKID=? ,FLAG_TIME=?",
								new Object[] { record_final.get("TASKID"),3 });

					}

					atomicInteger.incrementAndGet();
					System.out.println(atomicInteger.intValue());
					try {
						Thread.sleep(300L);
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
