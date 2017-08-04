package com.seassoon.test;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
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
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.chainsaw.Main;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.bson.Document;
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
import com.mongodb.WriteConcern;
import com.mongodb.client.model.Filters;
import com.seassoon.utils.ConnectionDB;

import util.ConnMysql;
import util.ConnectionMysql2;
import util.connSql_2;

/**
 * �����ݿ��еı������һ�С������š������е���Ϣ��excel����ȡ
 * 
 * 
 * */

public class AddInstitution {
	public static void main(String[] args) throws Exception {

		AddInstitution.readWithCsvListReader();
		System.out.println("Hello World!");
	}

	/**
	 * An example of reading using CsvListReader.
	 */
	private static void readWithCsvListReader() throws Exception {
		final ConnectionDB db = new ConnectionDB(
				"jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf8", "root", "123");
		
		String tb ="wg_baidumap_add";
		
		InputStream inp = new FileInputStream("G://���������ļ�//�ֶ��˿������.xlsx");
//		ThreadPoolExecutor executor =new ThreadPoolExecutor(10, 10, 10000, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		Workbook wb = WorkbookFactory.create(inp);
		DataFormatter formatter = new DataFormatter();
		Sheet sheet1 = wb.getSheetAt(0);
		int j = 0;
		for (Row row : sheet1) {
			if (j == 0) {
				j++;
				continue;
			}

			final String[] num = new String[4];
			int i = 0;
			for (Cell cell : row) {
				
				num[i] = formatter.formatCellValue(cell);
				if(i==3)
					break;
				i++;
	
			}
			
					int line = db.executeUpdate("update wg_cc_3_local set TOTALPEOPLE = "+num[3]+" where STREETNAME =\'"+num[1]+"\'");
                	
                	System.out.println(line);
		

			j++;
		}
		
//		executor.shutdown();

	}

}
