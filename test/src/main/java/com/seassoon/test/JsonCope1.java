package com.seassoon.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class JsonCope1 {
	public static void main(String[] args) throws JsonProcessingException, IOException {
		// TODO Auto-generated method stub
		
		ObjectMapper objMapper = new ObjectMapper(); 
		
	    JsonNode rootNode = objMapper.readTree(new File("G:\\李文超\\高速公路.json"));
	    
	    JSONArray outData = new JSONArray();
	    
//	    JsonNode y
	    	      
	    int len = rootNode.size();
	    
	    System.out.println(len);
	    
	    System.out.println(rootNode.get(0));
	    
	    //���治�ظ��ĵ�·
	    Set<ArrayList<String>> m = new HashSet<ArrayList<String>>();
	    
	    for(int i=0;i<len;i++){
	    	ArrayList<String> x = new ArrayList<>();
	    	
	    	x.add(rootNode.get(i).get("properties").get("ROADCODE").asText().substring(0,4));
	    	
	    	x.add(rootNode.get(i).get("properties").get("ROADNAME").asText());
	    	
	    	m.add(x);
	    }
	    for(ArrayList<String> x:m){
	    	
	    	JSONObject properties = new JSONObject();
	    	
	    	JSONObject properties1 = new JSONObject();
	    	
	    	properties.put("ROADCODE", x.get(0));
	    	
	    	properties.put("ROADNAME", x.get(1));
	    	
	    	properties1.put("properties", properties);
	    	
	    	properties1.put("coordinates", null);
	    		    	
	    	outData.add(properties1);
    	}
	    
	    
	    for(int i=0;i<len;i++){
	    	
	    	String roadCode = rootNode.get(i).get("properties").get("ROADCODE").asText().substring(0,4);
	    	
	    	for(Object x : outData){
	    		JSONObject xObject = (JSONObject)x;
	    		
	    		String roadCode1 = ((JSONObject)((JSONObject)x).get("properties")).get("ROADCODE").toString();
	    		
	    		if(roadCode.equals(roadCode1)){
	    			
//	    			System.out.println(
	    					
	    			JsonNode coor =rootNode.get(i).get("geometry").get("coordinates");
	    			
	    			JSONObject coordinates = new JSONObject();
	    			
	    			JsonNode coor1 = ((JsonNode) ((JSONObject)x).get("coordinates"));
	    			
	    			xObject.put("coordinates", JSONArray.fromObject(coor.toString()));
	    			
	    			
	    			System.out.println("dw");
	    		}
	    	}
	    	
	    	
	    }
	    
	    System.out.println(m.size());
	    
	    //.get("geometry").get("coordinates").get(1));//.toString());
	    
//	    for(int i=0;i<len;i++){
//	    	rootNode.get(i).get("geometry").toString();
//	    }
	    
	    System.out.println();
	    
	        
//		String JsonContext = new readJson().ReadFile("G:\\���ĳ�\\test.json");
//
//		JSONArray jsonArray = JSONArray.fromObject(JsonContext);
//
//		int size = jsonArray.size();
//
//		System.out.println("Size: " + size);
//
//		for (int i = 0; i < size; i++) {
//
//			JSONObject jsonObject = jsonArray.getJSONObject(i);
//			
//			System.out.println(jsonObject.get("geometry"));
//			
//			System.out.println("dkweop"+jsonObject.size());
//			
//		}
	}
}
