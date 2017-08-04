package com.seassoon.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.seassoon.utils.ConnectionDB;

public class WikiPlate_old {

	private static ConnectionDB db;

	public static void main(String[] args) throws Exception {
		String dbName = "db_kechuang";
		String ip = "10.50.5.15";
		String user = "user_kechuang";
		String password = "tykioqwsx4567hnjk";

		String fileName = "G://Wiki模板//kechuang//kechuang.txt";

		mainFunction(dbName, ip, user, password, fileName);
	}

	public static void mainFunction(String dbName, String ip, String user, String password, String fileName)
			throws IOException {
		db = new ConnectionDB("jdbc:mysql://" + ip + ":3306/" + dbName + "?useUnicode=true&characterEncoding=utf8",
				user, password);

		File f = new File(fileName);

		// 输出参数，并修改模板中的部分内容
		BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"));

		List<String> tables = getTableNames(dbName);
		output.write("===="+dbName + "====\r\n");
		String lineTxt = "| 列名         |是否为空    | 字段类型       |  备注        |";
		String lists ="^ 列名        ^ 是否为空       ^ 字段类型       ^ 备注       ^";
		
		for (String table : tables) {
			String comment = getCommentsOfTable(table);
			output.write("===="+comment+ "====\r\n");
			output.write(table + "\r\n");
			output.write(lists + "\r\n");
			
			String sql = "SELECT  table_name 表名,COLUMN_NAME 列名,  COLUMN_TYPE 数据类型,  DATA_TYPE 字段类型,  "
					+ "CHARACTER_MAXIMUM_LENGTH 长度,  IS_NULLABLE 是否为空,  COLUMN_DEFAULT 默认值,  COLUMN_COMMENT 备注  "
					+ "FROM   INFORMATION_SCHEMA.COLUMNS  where  table_schema ='"+dbName+"' and table_name = '" + table
					+ "'";
			List<Map<String, Object>> rs = db.excuteQuery(sql, null);
			for (Map<String, Object> r : rs) {

				String text = lineTxt;
				text = text.replace("备注", r.get("备注").toString());
				text = text.replace("列名", r.get("列名").toString());
				text = text.replace("字段类型", r.get("字段类型").toString());
				text = text.replace("是否为空", r.get("是否为空").toString());
				System.out.println(text);

				output.write(text + "\r\n");
			}
		}
		output.write(lineTxt + "\r\n");
		output.close();
	}

	/**
	 * 创建文件，用于存储每一个表格对应的接口文档
	 */
	public static boolean createFile(File fileName) throws Exception {
		boolean flag = false;
		try {
			if (!fileName.exists()) {
				fileName.createNewFile();
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 获取表格注释信息
	 */
	public static String getCommentsOfTable(String tableName) {

		List<Map<String, Object>> x = db.excuteQuery("show  create  table " + tableName, null);

		int index = x.get(0).get("Create Table").toString().indexOf("COMMENT=");

		if (index < 0) {
			// 表格没有注释信息
			return "";
		}
		String comment = x.get(0).get("Create Table").toString().substring(index + 9);

		comment = comment.substring(0, comment.length() - 1);

		return comment;
	}

	/**
	 * 获取数据库下的所有表格
	 */
	public static List<String> getTableNames(String dbName) {
		// 获取数据库下所有表格
		String sql = "SHOW TABLES";

		List<String> tbs = new ArrayList<String>();

		List<Map<String, Object>> rs = db.excuteQuery(sql, null);

		for (Map<String, Object> r : rs) {

			for (String key : r.keySet()) {
				tbs.add(r.get(key).toString());
			}
		}
		return tbs;
	}

}
