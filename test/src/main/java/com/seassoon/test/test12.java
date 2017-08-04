package com.seassoon.test;

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

		String stringDate = "2011-3-14 9:6:20"; // �����ַ������͵�����/ʱ��

		System.out.println("�ַ������͵�����/ʱ�����£�");

		System.out.println(stringDate); // ����ַ������͵�����/ʱ��

		Date date = null; // ����Date����

		try {

			date = df.parse(stringDate); // ���ַ������͵�����/ʱ�����ΪDate����

		} catch (ParseException e) {

			e.printStackTrace();

		}

		System.out.println("���������ɵ�Date�����ʾ������/ʱ�����£�");

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
