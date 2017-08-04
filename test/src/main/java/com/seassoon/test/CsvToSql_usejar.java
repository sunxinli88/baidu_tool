package com.seassoon.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.common.base.Joiner;
import com.seassoon.etl.input.impl.CsvInputer;
import com.seassoon.etl.outputer.impl.CsvOutputer;
import com.seassoon.etl.outputer.impl.CsvStreamOutputer;
import com.seassoon.utils.ConnectionDB;

import util.connSql_2;

public class CsvToSql_usejar {



	/**
	 * CSV�ļ�-->���ݿ�
	 * 
	 * 
	 * */
		public static void main(String[] args) throws SQLException, ParseException {
			final List<String> columnsList = new ArrayList<>();
			
			CsvInputer inputer =new CsvInputer("g://company_baidu_news.csv", columnsList, null,"UTF-8");
			
			try {
				//CsvStreamOutputer outPuter = new CsvStreamOutputer("H:\\�����ļ���\\icd2.csv", columnsList);
			
				CsvStreamOutputer outPuter = new CsvStreamOutputer(new FileOutputStream(new File("C:\\Users\\xx\\Desktop\\test23.csv")), columnsList);
				
				String[] data = null;
				
				List<String> list =new ArrayList<>();
				connSql_2 cm = new connSql_2();
			
				int  count_inserted=28184;
				
				int count=0;
				
				
				while (true) {
					data = inputer.next();//ÿ��ȡ�� String ���͵�����
					
					count+=1;			
					
					if (data == null) {
				
						outPuter.close();
						
						break;
					}				
//					
					if(count<=count_inserted ){
						continue;
					}
//										
					//outPuter.process(data);
					String tbName = "baidumap";
					
					for(int j=0;j<data.length;j++){
						
						list.add(data[j]);
					}
					cm.insert(list,tbName); 
					list.clear();
					System.out.println(Joiner.on(",").useForNull("").join(data));
					
				}

				
//				ResultSet resultSet = db.executeQueryRS("select * from cc", null);
//				while (resultSet.next()) {
//					//		Map<String, Object> map = new HashMap<String, Object>();
//					for(int i=1;i<=columns.length;i++){
//						System.out.print(resultSet.getObject(i)+",");
//					}	
//					System.out.println();
//				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

