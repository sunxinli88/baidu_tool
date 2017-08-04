package com.seassoon.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;

import org.apache.poi.ss.usermodel.DataFormatter;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.seassoon.utils.ConnectionDB;

public class ReadExcel {

	public static void main(String[] args) throws EncryptedDocumentException, InvalidFormatException, IOException {
		// TODO Auto-generated method stub

		ConnectionDB db = new ConnectionDB(
				"jdbc:mysql://localhost:3306/db_wg_xuhui?useUnicode=true&characterEncoding=utf8", "root", "123");

		InputStream inp = new FileInputStream("G://网格办相关文件//徐汇//5天数据导出_徐汇.xlsx");
		// InputStream inp = new FileInputStream("workbook.xlsx");

		Workbook wb = WorkbookFactory.create(inp);

		DataFormatter formatter = new DataFormatter();
		//
		// for(Sheet sheet1:wb){
		//
		//
		// String sheetName = sheet1.getSheetName();
		//
		// System.out.println(sheetName);
		Sheet sheet1 = wb.getSheetAt(0);

		int j = 0, erro = 0;

		for (Row row : sheet1) {

			if (j == 0) {
				j++;
				continue;
			}

			String[] num = new String[6];

			if (null == row.getCell(0)) {

				num[0] = "";
			}else{
				num[0] = row.getCell(0).getStringCellValue();
			}
			
			if (null == row.getCell(4)) {

				num[1] = "";
			}else{
				num[1] = row.getCell(4).getStringCellValue();
			}
			
			if (null == row.getCell(5)) {

				num[2] = "";
			}else{
				num[2] = row.getCell(5).getStringCellValue();
			}

			if (null == row.getCell(6)) {

				num[3] = "";
			}else{
				num[3] = row.getCell(6).getStringCellValue();
			}
			
			if (null == row.getCell(7)) {

				num[4] = "";
			}else{
				num[4] = row.getCell(7).getStringCellValue();
			}
			
			if (null == row.getCell(8)) {

				num[5] = "";
			}else{
				num[5] = row.getCell(8).getStringCellValue();
			}

			db.insert(new String[] { "TASKID", "PERCREATE", "CREATE1", "DISPATCH", "ACCEPT", "INSERT1" },
					new String[] { num[0], num[1], num[2], num[3], num[4], num[5] }, "sc_first");

			j++;
		}

	}

}
