package com.seassoon.test;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.supercsv.cellprocessor.FmtBool;
import org.supercsv.cellprocessor.FmtDate;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.LMinMax;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.UniqueHashCode;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

public class WriteCSV {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		writeWithCsvListWriter();
	}

	/**
	 * An example of reading using CsvListWriter.
	 */
	private static void writeWithCsvListWriter() throws Exception {

		ICsvListWriter listWriter = null;
		try {
			listWriter = new CsvListWriter(new FileWriter("G:/2015.csv",true),
					CsvPreference.STANDARD_PREFERENCE);

			
//			final CellProcessor[] processors = getProcessors();
			
			String[] file_name = new String[]{"网格15 01-03","网格15 04-06","网格15 07-09","网格15 10-12"};
			
			for(String file_n:file_name){
				InputStream inp = new FileInputStream("G://网格办相关文件//浦东网格中心//"+file_n+".xls");
				
				Workbook wb = WorkbookFactory.create(inp);

				DataFormatter formatter = new DataFormatter();
				
				int sheet_num = 0;

				for (Sheet sheet : wb) {

//					// 第一行，列头
//					if (sheet_num == 0) {
	//
//						Row row_0 = sheet.getRow(0);
//						
//						Iterator<Cell> it = row_0.iterator();
//						
//						List<String> header = new ArrayList<String>();
//						while(it.hasNext()){
//							
//							header.add(it.next().getStringCellValue());
//						}
//						
//						String[] head = new String[header.size()];
//						
//						for(int i=0;i<head.length;i++){
//							head[i] = header.get(i);
//						}
//						
//						// write the header
//						listWriter.writeHeader(head);
	//
//						sheet_num++;
//					}

					int row_num = 0;
					for (Row row : sheet) {

						if (row_num == 0) {
							row_num++;
							continue;
						}
						List<Object> ob = new ArrayList<Object>();
						for (int i = 0; i < 77; i++) {

							String con = formatter.formatCellValue(row.getCell(i));

							//去除换行
							con = con.replace("\n", "").replace(" ", "").replace("\t", "").replace("\r", "");

//							if (i == 2 || i == 3) {
//								con = con.replace(",", "");
//							}
//							System.out.print(con+"\t");
							ob.add(con);
							
							
						}
						
						listWriter.write(ob);
						
//						System.out.println("");

					}
				}

				inp.close();
			}

			

		} finally {
			if (listWriter != null) {
				listWriter.close();
			}
		}
	}

	/**
	 * Sets up the processors used for the examples. There are 10 CSV columns,
	 * so 10 processors are defined. All values are converted to Strings before
	 * writing (there's no need to convert them), and null values will be
	 * written as empty columns (no need to convert them to "").
	 * 
	 * @return the cell processors
	 */
	private static CellProcessor[] getProcessors() {

		final CellProcessor[] processors = new CellProcessor[] { new UniqueHashCode(), // customerNo
																						// (must
																						// be
																						// unique)
				new NotNull(), // firstName
				new NotNull(), // lastName
				new FmtDate("dd/MM/yyyy"), // birthDate
				new NotNull(), // mailingAddress
				new Optional(new FmtBool("Y", "N")), // married
				new Optional(), // numberOfKids
				new NotNull(), // favouriteQuote
				new NotNull(), // email
				new LMinMax(0L, LMinMax.MAX_LONG) // loyaltyPoints
		};

		return processors;
	}

}
