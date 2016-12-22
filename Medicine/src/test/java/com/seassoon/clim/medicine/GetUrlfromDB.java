package com.seassoon.clim.medicine;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.jayway.jsonpath.Filter;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.seassoon.utils.MongoDBUtils;

//处理爬取后的结果，包括，一个网页爬出的结果少于6项。删除爬取的click内容是空的结果。
//删除无效数据后的结果，所有的URL存入到文档中，以便后面的爬取过程首先判断是否已经爬取结果

public class GetUrlfromDB {
	public static void main(String[] args) {
		get();
	}

	public static void get() {
		MongoDBUtils mongo = MongoDBUtils.getInstance("127.0.0.1", 27017);

		MongoCollection<Document> medicine = mongo.getCollection("medicine", "spider");

		try {
			FileWriter writer = new FileWriter("G:/xx/getFromDB.txt", true);

			BufferedWriter bufferWritter = new BufferedWriter(writer);

			String url = "";
			
			for (Document cur : medicine.find()) {
				
//				try{
//					String click = cur.get("clickname").toString();
//					
//					String content = cur.get(click).toString();
//					
//					System.out.println(click+"::"+content);
//					
//					if(content.equals("")){
//						
//						Bson filter = Filters.eq("_id", cur.get("_id"));
//						
//						medicine.deleteOne(filter);
//					}
//					
//				}catch(NullPointerException e){
//					continue;
//				}
				
				//删除一条记录大小小于6的记录
//				int len = cur.size();
//				if(len<6){
//					
//					Bson filter = Filters.eq("_id", cur.get("_id"));
//					
//					medicine.deleteOne(filter);
//					
//				}else if(len ==6){
//					System.out.println(cur.toJson());
//				}

				url = cur.get("page").toString();

				bufferWritter.write(url + "\r\n");

				bufferWritter.flush();
				
			}
			bufferWritter.close();
			writer.close();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
