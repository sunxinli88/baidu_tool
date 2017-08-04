package com.seassoon.test;

	import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
	import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
	import java.util.HashMap;
	import java.util.List;
	import java.util.Map;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.seassoon.common.config.JfinalORMConfig;

	public class ExcelToTXT {

		

		public static void main(String[] args) throws  IOException, EncryptedDocumentException, InvalidFormatException {
			// TODO Auto-generated method stub
			write();

		}

		private static void write() throws IOException, EncryptedDocumentException, InvalidFormatException {
			// TODO Auto-generated method stub
			
			InputStream inp = new FileInputStream("G://网格办相关文件//2016年数据to李慧//04-06（3）.xls");
			
			File file = new File("G://网格办相关文件//2016年数据to李慧//测试.txt");
	        FileWriter fw = null;
	        BufferedWriter writer = null;
	        
	        try {
	            fw = new FileWriter(file,true);
	            writer = new BufferedWriter(fw);
	            
	            Workbook wb = WorkbookFactory.create(inp);
				DataFormatter formatter = new DataFormatter();
//				for(Sheet sheet1:wb){
				Sheet sheet1 = wb.getSheetAt(0);
					int j = 0;
//					for (Row row : sheet1) {
//						if (j == 0) {
//							j++;
//							continue;
//						}
					Row row = sheet1.getRow(0);
						StringBuilder sb = new StringBuilder();
						
						for (Cell cell : row) {
							sb.append(formatter.formatCellValue(cell)+",");
						}
						sb.delete(sb.length()-1, sb.length());
						writer.write(sb.toString());
						writer.newLine();//����
						j++;
//					}
	           
		            writer.flush();
//				}

				
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        }catch (IOException e) {
	            e.printStackTrace();
	        }finally{
	            try {
	                writer.close();
	                fw.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }		
	}
}
