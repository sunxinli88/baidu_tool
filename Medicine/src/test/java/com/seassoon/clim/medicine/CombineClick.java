package com.seassoon.clim.medicine;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.seassoon.utils.MongoDBUtils;

public class CombineClick {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		combineClick();
	}

	public static void addUrl(String page, String filepath) {
		// TODO Auto-generated method stub
		try {
			// 把刚刚爬取过的药品内容页的url保存到文件
			FileWriter writer = new FileWriter("G:/xx/getFromDB.txt", true);

			BufferedWriter bufferWritter = new BufferedWriter(writer);

			bufferWritter.write(page + "\r\n");

			bufferWritter.flush();
			bufferWritter.close();
			writer.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void combineClick() {
		// TODO Auto-generated method stub
		MongoDBUtils mongo = MongoDBUtils.getInstance("127.0.0.1", 27017);

		MongoCollection<Document> clcikContent = mongo.getCollection("medicine", "clickContent");
		
		int i=0;

		Bson filter = Filters.ne("proxy", "0");
		
		
		for (Document cur : clcikContent.find(filter)) {


			String pageParameter;

			if ((pageParameter = cur.getString("pageParameter")) != null) {
				
//				if(pageParameter.equals("148852")){
					String entry = cur.getString("entry");
					
					String url = cur.getString("url");

					String content = cur.getString("content");
				
//					Map<String, Object> filterMap = new HashMap<String, Object>();
	//
//					filterMap.put("pagePrameter", pageParameter);
	//
//					Map<String, Object> updateMap = new HashMap<String, Object>();
	//
//					updateMap.put(entry, content);
	//
//					mongo.updateOne("medicine", "medicinePlus", filterMap, updateMap);

					BasicDBObject query = new BasicDBObject();
					query.put("pagePrameter", pageParameter);

					BasicDBObject newDocument = new BasicDBObject();
					newDocument.put(entry, content);

					BasicDBObject updateObj = new BasicDBObject();
					updateObj.put("$set", newDocument);

					mongo.getCollection("medicine", "medicinePlus").updateOne(query, updateObj);
					
					
					BasicDBObject query1 = new BasicDBObject();

					query1.put("url", url);
					
					BasicDBObject new1 = new BasicDBObject();
					new1.put("proxy", "0");

					BasicDBObject updateObj1 = new BasicDBObject();
					updateObj1.put("$set", new1);
					
					mongo.getCollection("medicine", "clickContent").updateOne(query1, updateObj1);
					
					i++;
				}

				System.out.println(i);
//			}

		}
	}

}
