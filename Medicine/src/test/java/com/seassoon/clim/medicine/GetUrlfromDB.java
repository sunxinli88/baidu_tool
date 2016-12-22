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

//������ȡ��Ľ����������һ����ҳ�����Ľ������6�ɾ����ȡ��click�����ǿյĽ����
//ɾ����Ч���ݺ�Ľ�������е�URL���뵽�ĵ��У��Ա�������ȡ���������ж��Ƿ��Ѿ���ȡ���

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
				
				//ɾ��һ����¼��СС��6�ļ�¼
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
