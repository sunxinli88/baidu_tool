package com.seassoon.test;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.chainsaw.Main;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseBool;
import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.LMinMax;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.StrRegEx;
import org.supercsv.cellprocessor.constraint.UniqueHashCode;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.prefs.CsvPreference;

import com.baidu.util.BaiduMapUtils;
import com.baidu.util.Location;
import com.google.common.base.Joiner;
import com.seassoon.utils.ConnectionDB;

import util.ConnMysql;
import util.ConnectionMysql2;
import util.connSql_2;



public class CsvToSql {

	
	public static void main( String[] args ) throws Exception
    {
    	
		CsvToSql.readWithCsvListReader();    	
        System.out.println( "Hello World!" );
    }
    /**
     * An example of reading using CsvListReader.
     */
    private static void readWithCsvListReader() throws Exception {
			ConnectionDB db = new ConnectionDB("jdbc:mysql://localhost:3306/db_sharedata?useUnicode=true&characterEncoding=utf8","root","123");

            ICsvListReader listReader = null;
            try {
            	//"F://test.csv"
            	//new InputStreamReader(new FileInputStream("F://�ֶ���������//Total//Total.csv"), "GBK");
                    listReader = new CsvListReader(new InputStreamReader(new FileInputStream("g://company_baidu_news.csv"), "UTF-8"), CsvPreference.STANDARD_PREFERENCE);
                    
                    String[] head = listReader.getHeader(true); // skip the header (can't be used with CsvListReader)
                    List<String> headList = new ArrayList<>();
                    //�����������������⣬��ȡ��Ч������Ϊ�ֶ���
//                    for(String s:head){
//                    	if(s.length()>63){
//            				s = s.substring(0, 63);
//            			}
//                    	headList.add(s);
//                    }
                    
                    for(String s:head){
                    	headList.add(s);
                    }
//                    headList.add("INSTITUTION");
//                    headList.add("NAME");
//                    headList.add("SYSTEMNAME");
//                    headList.add("SHARESTYLE");
//                    headList.add("PUBLICSTYLE");
//                    headList.add("PUBLICTIME");
//                    headList.add("DATACHINESE");
//                    headList.add("DATAENGLISH");
//                    headList.add("DATAEXPLAIN");
                    
                    String tbName = "company_baidu_news";
                   
//                    try{
//                    	db.createTb(head,tbName);
//                    }catch(Exception e){
//                    	System.out.println("����Ѿ�����");
//                    }
                   
//                    final CellProcessor[] processors = getProcessors();

	                    List<String> customerList;//
	                    int numEvery = 1;
	                    //��ű����������
	                    List<Object> insertList = new ArrayList<>();
//	                    int error = 0;
	                    //�洢�����������к�
	                    List<Integer> error = new ArrayList<>();

	                    while( (customerList = listReader.read()) != null) {

//	                    	�ҳ����ܴ������ݿ�ļ�¼��
//	                    	if(numEvery>881199&&numEvery<881600){
//	                    		for(String s:customerList){
//	                    			insertList.add(s);
//	                    		}
//	                    		 y = db.insert(db,headList, insertList, tbName);
//	                    		 if(y==199){
//	                    			 flag = customerList.get(0);
//	                    		 }
//	                    		 
//	                    		 insertList.clear();
//	                    	}
//	                    	numEvery++;
	                 
	                    		
	                    	for(String s:customerList){
                    			insertList.add(s);
                    		}
	                    	
//	                    	db.insert(db,headList, insertList, tbName);
	                    	
	                    	if(numEvery%400==0){
	             
	                    		int x = db.insert(db,headList, insertList, tbName);
	                    		if(x == 199){
	                    			error.add(numEvery);
	                    		}
	                    		insertList.clear();
	                    	}else if(numEvery==212523){
	                    		int x = db.insert(db,headList, insertList, tbName);
	                    		if(x == 199){
	                    			error.add(numEvery);
	                    		}
	                    		insertList.clear();
	                    	}

	                    	numEvery++;
//	                    	
	                    	//System.out.println(Joiner.on(",").useForNull("").join(customerList));
	                    	/*String address = customerList.get(23);
	                    	try{
	                    		Location= BaiduMapUtils.geocoder(address);
	                    		customerList.add(Location.getLat().toString());
	                    		customerList.add(Location.getLng().toString());
	                    		 
	                    	}catch(Exception e){
	                    		customerList.add(null);
	                    		customerList.add(null);
	                    		errorN++;
	                    		System.out.println("��ַ��ʽ����"+address+customerList.get(0));                    		
	                    	}	 
	                    	cm.insert(customerList,tbName); */

                    }
//	                    �ҳ����ܴ������ݿ�ļ�¼��
//	                    System.out.println(numEvery);	                    
//	                    System.out.println(flag);
//	                   for(int i:error){
//	                	   System.out.println(i);
//	                   }
                    
	            }catch (Exception e) {
					e.printStackTrace();
				}
	            finally {
	                    if( listReader != null ) {
	                            listReader.close();
	                    }
	            }
            
            
    }
    /**
     * Sets up the processors used for the examples. There are 10 CSV columns, so 10 processors are defined. Empty
     * columns are read as null (hence the NotNull() for mandatory columns).
     * ��ʽ��
     * @return the cell processors
     */
    private static CellProcessor[] getProcessors() {
            
            final String emailRegex = "[a-z0-9\\._]+@[a-z0-9\\.]+"; // just an example, not very robust!
            StrRegEx.registerMessage(emailRegex, "must be a valid email address");
            
            final CellProcessor[] processors = new CellProcessor[] { 
                    new UniqueHashCode(), // customerNo (must be unique)
                    new NotNull(), // firstName
                    new NotNull(), // lastName
                    new ParseDate("dd/MM/yyyy"), // birthDate
                    new NotNull(), // mailingAddress
                    new Optional(new ParseBool()), // married
                    new Optional(new ParseInt()), // numberOfKids
                    new NotNull(), // favouriteQuote
                    new StrRegEx(emailRegex), // email
                    new LMinMax(0L, LMinMax.MAX_LONG) // loyaltyPoints
            };
            
            return processors;
    }
    
 
}
