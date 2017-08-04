package com.seassoon.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ReadCsv {
/**
 * ��ԭʼ�Ķ�ȡ csv �ļ�
 * */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		FileInputStream dir = new FileInputStream("F:\\test.csv");
		BufferedReader dr=new BufferedReader(new InputStreamReader(dir));
		
		String   instring;     		 
		while   ((instring   =  dr.readLine()) != null){
			String[] aa;
			if(instring != null){     
				aa = instring.split(",");
				for(String s:aa){
					System.out.print(s+' ');
				}
			}
			System.out.println();
			
			}
		dr.close();		
	}

}
