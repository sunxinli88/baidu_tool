package com.seassoon.test.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.seassoon.utils.ConnectionDB;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ZhuJson {
	
	static ConnectionDB db = new ConnectionDB(
			"jdbc:mysql://172.16.40.125:3306/db_court_drug?useUnicode=true&characterEncoding=utf8", "user_c_drug", "fgtfghy*&JKuiklo");

	
	public static void main(String[] args) throws IOException {
		test2();
		//test();
	}
	
	public static void test2() throws IOException{
		List<String> files = getFileName();
		
		for(String file:files){
			BufferedReader br = new BufferedReader(new FileReader("G:/朱阳光/shanghai/"+file));
			
			String line = null;
			
			file = file.replace(".txt", "");
			
			StringBuilder sb = new StringBuilder();
			
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			String[] colums = new String[]{"title","content"};
			
			Object[] params = new Object[]{file,sb.toString()};
			
			db.insert(colums, params, "flws_data");
			
			br.close();
		}
		
		
	}
	
	public static List<String> getFileName() {
		
		List<String> rs = new ArrayList<String>();
		        String path = "G:/朱阳光/shanghai"; // 路径
		         File f = new File(path);
		         if (!f.exists()) {
		             System.out.println(path + " not exists");
		             return null;
		         }
		 
		         File fa[] = f.listFiles();
		         for (int i = 0; i < fa.length; i++) {
		             File fs = fa[i];
		             if (fs.isDirectory()) {
		                 System.out.println(fs.getName() + " [目录]");
		             } else {
		            	 rs.add(fs.getName());
		                System.out.println(fs.getName());
		             }
		         }
		         return rs;
		     }

	public static void test() throws IOException {
		ConnectionDB db = new ConnectionDB(
				"jdbc:mysql://172.16.40.125:3306/db_court_drug?useUnicode=true&characterEncoding=utf8", "user_c_drug", "fgtfghy*&JKuiklo");

		BufferedReader br = new BufferedReader(new FileReader("G:\\朱阳光\\ex-drug.json"));

		String[] colums = new String[] { "category", "title", "extract" };

		String line = null;

		while ((line = br.readLine()) != null) {
			JSONObject jo = JSONObject.fromObject(line);

			Iterator<?> it = jo.keys();
			String[] rs = new String[3];

			rs[2] = line;
			
			rs[0] = jo.get("文书种类").toString();
			
			rs[1] = jo.get("案号").toString();
			
			db.insert(colums, rs, "feature");

		}
		
		br.close();
	}


	public static void test1() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("G:\\朱阳光\\exampleStandard.json"));

		String line = null;

		while ((line = br.readLine()) != null) {

			JSONObject jo = JSONObject.fromObject(line);

			Iterator<?> it = jo.keys();

			int i = 0;
			while (it.hasNext()) {

				String key = (String) it.next();
				String value = jo.getString(key);

				if (i == 0) {
					i++;
					continue;
				}
				JSONArray array = jo.getJSONArray(key);

				for (int j = 0; j < array.size(); j++) {
					JSONObject arr = array.getJSONObject(j);
					Iterator<?> it2 = arr.keys();

					while (it2.hasNext()) {
						String key2 = (String) it2.next();
						String value2 = arr.getString(key2);

						System.out.println(key2 + ":::" + value2);
					}
				}

			}

		}

		br.close();
	}
}
