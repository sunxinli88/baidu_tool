package com.seassoon.test.test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import util.ConnPool;

public class test12 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		ConnPool cp = new ConnPool();

		List<Map<String, Object>> pool = cp.executeQuery("select * from wg_baidumap where TASKID=15030827831", null);

		String s = (String) pool.get(0).get("DISCOVERTIME");

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String stringDate = "2011-3-14 9:6:20"; // 定义字符串类型的日期/时间

		System.out.println("字符串类型的日期/时间如下：");

		System.out.println(stringDate); // 输出字符串类型的日期/时间

		Date date = null; // 定义Date对象

		try {

			date = df.parse(stringDate); // 将字符串类型的日期/时间解析为Date类型

		} catch (ParseException e) {

			e.printStackTrace();

		}

		System.out.println("解析后生成的Date对象表示的日期/时间如下：");

		System.out.println(date);
		
		Date d = new Date(117707000);
		
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		ft.format(d);
		
		System.out.println(ft.format(d));
		
		Calendar cal=Calendar.getInstance();
		cal.setTime(d);
		
		System.out.println(cal);

	}

}
