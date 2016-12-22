package com.seassoon.common;

import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.model.Filters;
import com.seassoon.utils.MongoDBUtils;

public class Spidered {
	
	private static  String dbName;
	
	private String collection;
	
	private static MongoDBUtils mongo;
	
	public Spidered(String adbName,String acollection){
		
		mongo = MongoDBUtils.getInstance("127.0.0.1", 27017);
		
		dbName = adbName;
		
		collection = acollection;
	}
	
	public boolean isSpidered(String url) {

		boolean flag = false;

		// 判断当前药品内容页是否已经爬取
		Bson filter = Filters.eq("url", url);

		List<Document> list = mongo.find(dbName, collection, filter);

		if (list.size() > 0) {

			flag = true;
		}

		return flag; 
	}
	public void save(Map<String,Object> m){
		mongo.insert(dbName, collection, m);
	}
	

}
