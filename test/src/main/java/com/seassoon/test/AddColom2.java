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

public class AddColom2 {
	public static void main(String[] args) throws Exception {

		AddColom2.readWithCsvListReader();
		System.out.println("Hello World!");
	}

	/**
	 * An example of reading using CsvListReader.
	 */
	private static void readWithCsvListReader() throws Exception {
		final ConnectionDB db = new ConnectionDB(
				"jdbc:mysql://172.16.40.122:3306/db_wg?useUnicode=true&characterEncoding=utf8", "user_wg", "hfyhfb^&njguwiik");
		
		InputStream inp = new FileInputStream("G://���������ļ�//�����õ�λ14.xls");
		ThreadPoolExecutor executor =new ThreadPoolExecutor(10, 10, 10000, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		Workbook wb = WorkbookFactory.create(inp);
		DataFormatter formatter = new DataFormatter();
		Sheet sheet1 = wb.getSheetAt(0);
		int j = 0;
		for (Row row : sheet1) {
			if (j == 0) {
				j++;
				continue;
			}

			final String[] num = new String[2];
			int i = 0;
			for (Cell cell : row) {
				// get the text that appears in the cell by getting the cell
				// value and applying any data formats (Date, 0.00, 1.23e9,
				// $1.23, etc)
				num[i] = formatter.formatCellValue(cell);
				i++;
	
			}
			executor.execute(new Runnable() {
				
				@Override  
				public void run() {
					String sql = "insert into street(36street,executedeptname)values('" + num[0] + "' ,'" + num[1] + "\')";
					
					System.out.println(num[0]+"->"+num[1] +" "+	db.executeUpdate(sql));
				}
			});
			

			j++;
		}
		
		executor.shutdown();

	}

	/**
	 * Sets up the processors used for the examples. There are 10 CSV columns,
	 * so 10 processors are defined. Empty columns are read as null (hence the
	 * NotNull() for mandatory columns). ��ʽ��
	 * 
	 * @return the cell processors
	 */
	private static CellProcessor[] getProcessors() {

		final String emailRegex = "[a-z0-9\\._]+@[a-z0-9\\.]+"; // just an
																// example, not
																// very robust!
		StrRegEx.registerMessage(emailRegex, "must be a valid email address");

		final CellProcessor[] processors = new CellProcessor[] { new UniqueHashCode(), // customerNo
																						// (must
																						// be
																						// unique)
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

