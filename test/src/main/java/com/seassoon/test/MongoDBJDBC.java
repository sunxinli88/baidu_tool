package com.seassoon.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.crypto.dsig.spec.XPathType.Filter;

import org.bson.Document;

import com.google.common.base.Joiner;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

public class MongoDBJDBC {

	// ���ӵ� mongodb ����
	private MongoClient mongoClient = new MongoClient("localhost", 27017);

	// ���ӵ����ݿ�
	private MongoDatabase mongoDatabase = mongoClient.getDatabase("runoob");

	public void insert(Map<String, Object> map,String collec) {
		try {
			// �����ĵ�
			/**
			 * 1. �����ĵ� org.bson.Document ����Ϊkey-value�ĸ�ʽ 2. �����ĵ�����List<Document>
			 * 3. ���ĵ����ϲ������ݿ⼯���� mongoCollection.insertMany(List<Document>)
			 * ���뵥���ĵ������� mongoCollection.insertOne(Document)
			 */
			List<String> list = new ArrayList<String>();
			Document document = new Document();
			
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				   String key = entry.getKey().toString();
				   String value = entry.getValue().toString();
				   list.add(key);
				   list.add(value);
				   document.append(key, value);
				  }
		
			// append("description", "database").
			// append("likes", 100).
			// append("by", "Fly");
			// Document document2 = new Document("title", "MongoDB").
			// append("description", "database").
			// append("description", "database");
			//// append("likes", 100).
			//// append("by", "Fly");
			// List<Document> documents = new ArrayList<Document>();
			// documents.add(document);
			// documents.add(document2);
			// collection.insertMany(documents);
			// System.out.println("�ĵ�����ɹ�");

			// ���������ĵ�
			/**
			 * 1. ��ȡ������FindIterable<Document> 2. ��ȡ�α�MongoCursor<Document> 3.
			 * ͨ���α�������������ĵ�����
			 */

			MongoCollection<Document> collection = mongoDatabase.getCollection(collec);

			collection.insertOne(document);
			
//			FindIterable<Document> findIt = collection.find();
//			MongoCursor<Document> moncur = findIt.iterator();
//
//			while (moncur.hasNext()) {
//				System.out.println(moncur.next());
//			}

			/**
			 * �����ĵ�
			 */
			// collection.updateMany(Filters.eq("likes", 100), new
			// Document("$set",new Document("likes",200)));

			/**
			 * ɾ���ĵ�
			 */
			// ɾ�����������ĵ�һ���ĵ�
//			collection.deleteOne(Filters.eq("likes", 200));
//			// ɾ�����з����������ĵ�
//			collection.deleteMany(Filters.eq("likes", 200));

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}
}