package com.seassoon.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.google.common.base.Joiner;
import com.seassoon.utils.ConnectionDB;

public class writeExcel {

	public static void main(String[] args) throws EncryptedDocumentException, InvalidFormatException, IOException {
		// TODO Auto-generated method stub
		write();
	}
	
	public static void write() throws EncryptedDocumentException, InvalidFormatException, IOException{
		
		ConnectionDB db = new ConnectionDB("jdbc:mysql://localhost:3306/aa?useUnicode=true&characterEncoding=utf8",
				"root", "123");

//		FileOutputStream inp = new FileOutputStream("G://���������ļ�//missionCount2.xlsx");
		 OutputStream inp = new FileInputStream("workbook.xlsx");

		Workbook wb = WorkbookFactory.create(inp);
		
		Sheet first = wb.createSheet("first");

		for(int i=1;i<23;i++){
			
			Row r = first.createRow(i);
			
			Cell cell0 = r.createCell(0);
			
			String sql = "SELECT TASKID from missioncount WHERE COUNT = "+i;
			
			List<Map<String,Object>> taskids = db.excuteQuery(sql, null);
			
			System.out.println(i+"::"+taskids.size());

			if(taskids!=null){
				
					cell0.setCellValue(i);
					
					r.createCell(1).setCellValue(taskids.size());
				}
			}
		wb.write(inp);
		
		}
	

}
