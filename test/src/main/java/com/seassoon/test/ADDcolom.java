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
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

/**
 * ��csv�ļ���ȡ���ݣ�Ϊ���ݿ������һ��
 * 
 * 
 * */
public class ADDcolom {
	public static void main(String[] args) throws Exception {

		ADDcolom.readWithCsvListReader();
		System.out.println("Hello World!");
	}

	/**
	 * An example of reading using CsvListReader.
	 */
	private static void readWithCsvListReader() throws Exception {
		final ConnectionDB db = new ConnectionDB(
				"jdbc:mysql://localhost:3306/aa?useUnicode=true&characterEncoding=utf8", "root", "123");

		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 10, 100, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>());

		ICsvListReader listReader = null;
		try {
			// "F://test.csv"
			// new InputStreamReader(new
			// FileInputStream("F://�ֶ���������//Total//Total.csv"), "GBK");
			listReader = new CsvListReader(
					new InputStreamReader(new FileInputStream("F://�ֶ���������//Total//clustering_eventids.tsv"), "UTF-8"),
					CsvPreference.STANDARD_PREFERENCE);

			String tbName = "baidumap";

			List<String> customerList;//
			String[] col = new String[3];
			String sql;
			List<String> sqlexcu = new ArrayList<String>();

			while ((customerList = listReader.read()) != null) {

				for (String s : customerList) {
					if (s.contains("\t")) {
						col = s.split("\t");
						// System.out.println(s);
						sql = "update " + tbName + " set EVENTID = " + col[1] + " where TASKID = \'" + col[2] + "\'";

					} else {
						sql = "update " + tbName + " set EVENTID = " + col[1] + " where TASKID = \'" + s + "\'";
					}
					sqlexcu.add(sql);
				}
			}
			for (final String s : sqlexcu) {
				threadPoolExecutor.execute(new Runnable() {
					// final String sql_final = s;
					@Override
					public void run() {
						System.out.println(s);
						db.executeUpdate(s);

					}
				});
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (listReader != null) {
				listReader.close();
			}
		}

		threadPoolExecutor.shutdown();

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
