package com.seassoon.test;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;

import org.supercsv.prefs.CsvPreference;
import com.seassoon.utils.ConnectionDB;

public class AddPeopleNum {

	public static void main(String[] args) throws Exception {

		AddPeopleNum.readWithCsvListReader();
		System.out.println("Hello World!");
	}

	/**
	     * An example of reading using CsvListReader.
	     */
	    private static void readWithCsvListReader() throws Exception {
				ConnectionDB db = new ConnectionDB("jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf8","root","123");

	            ICsvListReader listReader = null;
	            try {
	                    listReader = new CsvListReader(new InputStreamReader(new FileInputStream("G://���˹�������ļ�//���������˿�.cvs"), "GBK"), CsvPreference.STANDARD_PREFERENCE);

		                List<String> customerList;
		                
		                while( (customerList = listReader.read()) != null) {
		                	
		                	String s = customerList.get(0);
		                	
		                	int index = s.indexOf(" ");
		                	
		                	String name = s.substring(0, index);
		                	
		                	String number = s.substring(index);
		                	
		                	String number2 ="";
		                	
		                	for(int i=0;i<number.length();i++){
		                		
		                		char ele = number.charAt(i);
		                		
		                		if(ele>='0'&&ele<='9'){
		                			
		                			number2+=ele;
		                		}	
		                	}
		                	
		                	System.out.println(name+"::"+number2);
		                	
		                	int line = db.executeUpdate("update wg_cc_3 set TOTALPEOPLE = "+number2+" where STREETNAME =\'"+name+"\'");
		                	
		                	System.out.println(line);
		                }
	            }catch(Exception e){
	            	
	            		e.printStackTrace();
	            } finally {
	            	
						if (listReader != null) {
							listReader.close();
						}
	            }
	    }
	            
}
