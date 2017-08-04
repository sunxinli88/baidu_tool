package com.seassoon.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class JsonCope {
	
	//��������--���ĳ�������roadcode�ظ��Ľ��кϲ���coordinates׷�ӵ�����
	
	public static String subS(String s){
		
		String sub1 = "";
		
		if(s.contains("360")){
			int index = s.indexOf("360");
			
			sub1 = s.substring(0, index);
			
		}else if(s.contains("36")){
			int index = s.indexOf("36");
			
			sub1 = s.substring(0, index);		
		}
		
		System.out.println(s);
		
		return sub1;
		
		
	}
	public static void main(String[] args) throws Exception{
		
		Map<String, JSONObject> map = new HashMap<String, JSONObject>();
		
//		String JsonContext = new readJson().ReadFile("G:\\���ĳ�\\���ٹ�·.json");
//
//		JSONArray jsonArray = JSONArray.fromObject(JsonContext);
//		
//		int size = jsonArray.size();
		
//		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File("G:\\���ĳ�\\test1.json")), "utf-8"));

		BufferedReader br = new BufferedReader(new FileReader("G:\\���ĳ�\\���ٹ�·.json"));
		
		String line = null;
		
		while((line = br.readLine())!=null){
			
//		for (int i = 0; i < size; i++) {
//			
//			JSONObject jo = jsonArray.getJSONObject(i);
			line = line.substring(0, line.length()-1);
			
			JSONObject jo = JSONObject.fromObject(line);
			
			//***************************
			
			Iterator it = jo.keys();
			
			while(it.hasNext()){
				
			}
			
			//*****************************
			
			String roadcode = subS(jo.getJSONObject("properties").getString("ROADCODE"));
			
			String roadname = jo.getJSONObject("properties").getString("ROADNAME");
			
			JSONArray ja = jo.getJSONObject("geometry").getJSONArray("coordinates");
			
			if(map.containsKey(roadcode)){
				
				JSONObject oneRes = map.get(roadcode);
				
				for(int j=0;j<ja.size();j++){
					
					oneRes.getJSONObject("geometry").getJSONArray("coordinates").add(ja.get(j));
				}
									
			}else{
				
				JSONObject oneRes = new JSONObject();
				
				JSONObject level41 = new JSONObject();
				
				level41.put("coordinates", ja);
				
				level41.put("type", "LineString");

				
				JSONObject level42 = new JSONObject();
				
				level42.put("ROADCODE", roadcode);
				
				level42.put("ROADNAME", roadname);

				
				oneRes.put("type", "Feature");
				
				oneRes.put("geometry", level41);
				
				oneRes.put("properties", level42);
				
				map.put(roadcode, oneRes);
			}
		}
		br.close();
		
		
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(new File("G:\\���ĳ�\\���ٹ�·-out.json")),"utf-8"));
		
		for(Entry<String, JSONObject> entry: map.entrySet()){
			
			pw.println(entry.getValue()+",");
			
		}
		pw.close();
	}
}
