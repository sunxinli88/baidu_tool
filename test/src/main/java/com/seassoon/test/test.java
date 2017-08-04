package com.seassoon.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import util.connSql_2;

public class test {
	/**
	 * 
	 * �������ݿ⣬������������ɾ�ò���� http://www.cnblogs.com/wuyuegb2312/p/3872607.html
	 */

	public static void main(String args[]) {
		final String url = "jdbc:mysql://localhost:3306/aa?useUnicode=true&characterEncoding=utf8";
		final String name = "com.mysql.jdbc.Driver";
		final String user = "root";
		final String password = "123";
		Connection conn = null;

		try {
			Class.forName(name);// ָ����������
			System.out.println("Success loading Mysql Driver!");
		} catch (Exception e) {
			System.out.print("Error loading Mysql Driver!");
			e.printStackTrace();
		}
		try {
			conn = DriverManager.getConnection(url, user, password);// ��ȡ����

			// ��ѯ���

			String sql = "select * from test_content";

			PreparedStatement ps = conn.prepareStatement(sql);

			ResultSet rs = ps.executeQuery();

			System.out.println("execute successfully!");

			String html = null;

			int i = 0;
			while (rs.next()) {
				if (i == 10) {
					html = rs.getString(2);

					Document doc = Jsoup.parse(html);

					Elements eles = doc.select("body>table:eq(5)").select("tr:eq(4)").select(".y");
					// getElementsByTag("_blank");
					// <td class="border" id="abc" ccc="adf">
					//
					// doc.getElementsByClass("border") ;// <td class="border">
					// doc.select(".border");
					//
					// doc.select("[ccc='adf']")
					//
					// doc.select("#abc");
					//
					// doc.getElementById("abc");
					//
					// doc.getElementsByTag("td")

					// doc.getElementsByAttribute("class") ;//border

					for (Element el : eles) {
						String linkHref = el.attr("href");
						String s = el.text();
						System.out.println(s);
						System.out.println(linkHref);
					}
				}
				if (i == 1) {
					html = rs.getString(2);

					Document doc = Jsoup.parse(html);

					Elements eles = doc.select("body>table:eq(6)").select("tbody:eq(0)").select("tr:eq(0)")
							.select("td:eq(1)").select("table:eq(1)").select("tbody:eq(0)").select("tr:eq(3)")
							.select("td:eq(0)").select("table:eq(0)").select("tbody:eq(0)").select("tr:eq(0)")
							.select("td:eq(0)").select("div:eq(1)").select("span:eq(0)").select("font:eq(0)");// .select("*");
					
					
//					Elements eles=doc.select(".maintext");
//					System.out.println(eles.size() + " " + eles.first());
					
					String content=eles.first().html();
					System.out.println(content);
					int[] arr = new int[19];
					String[] b = new String[10];
					
				    String[] tempArray;
				    
				    connSql_2 cm = new connSql_2();
				    
					tempArray=content.split("<br>");
					String[] head = {"name","content"};
					String tb = "htmltwolist";
					for (String el : tempArray) {
						// String s = el.
						 System.out.println(el);
						 int index = el.indexOf('��');
						 String first= null,last = null;
						 if(index!=-1){
							 first = el.substring(0,index);
							 last = el.substring(index+1);
							 System.out.println(first+"\t"+last); 
						 }else{
							 System.out.println("���ݲ���");
						 }
						 List<String> clist = new ArrayList<>();
						 clist.add(first);
						 clist.add(last);
						 cm.insert(head,clist,tb);
						 
					}
					//��tempArrayÿ��Ԫ���� �� �ָ���ֱ���Ϊ�ֶκ�ֵ����mysql��
				}
				i++;
			}

		} catch (Exception e) {
			System.out.println("�������");
			e.printStackTrace();
		}

	}
}
