package test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.seassoon.utils.ConnectionDB;

public class CopeTwice {

	public static void main(String[] args) throws EncryptedDocumentException, InvalidFormatException, IOException {
		// TODO Auto-generated method stub
		getInformation();
	}

	private static void getInformation() {
		// TODO Auto-generated method stub
		
	}

	private static void process() throws EncryptedDocumentException, InvalidFormatException, IOException {
		// TODO Auto-generated method stub
		ConnectionDB db = new ConnectionDB("jdbc:mysql://localhost:3306/wg_pudong?useUnicode=true&characterEncoding=utf8",
				"root", "123");

		String sql = "select taskid from missioncount where count = 2";

		List<Map<String, Object>> sources = db.excuteQuery(sql, null);
		
		List<String> source = new ArrayList<String>();
		
		for (Map<String, Object> element : sources) {
			
			source.add(element.get("taskid").toString());
		}

		InputStream inp = new FileInputStream("G://网格办相关文件//2016.xls");

		Workbook wb = WorkbookFactory.create(inp);
		
		Map<String, Map<String, Object>> results = new HashMap<String, Map<String, Object>>();
		
		for(Sheet sheet1:wb){
			int j=0;

			for (Row row : sheet1) {
				// 获取taskid，派遣时间，派遣部门
				
				if(j==0){
					j++;
					continue;
				}
				String idExcel = row.getCell(0).getStringCellValue();

				for (String idSql : source) {

					if (idExcel.equals(idSql)) {

						Date time = row.getCell(1).getDateCellValue();
						
						String instItution = row.getCell(2).getStringCellValue();

						if (results.containsKey(idExcel)) {

							results.get(idExcel).put("secondTime", time);

							results.get(idExcel).put("secondInstitu", instItution);
							
							sources.remove(idSql);

						} else {
							
							Map<String, Object> result = new HashMap<String, Object>();
							
							result.put("firstTime", time);

							result.put("firstInstitu", instItution);

							results.put(idExcel, result);
						}
						
						break;
						
					} else {

						continue;
					}

				}
			}
		}

		int totalNum = 0;
		
		List<String> noTargetIds = new ArrayList<>();
		
		for(String id:results.keySet()){
			
			Date firstTime = (Date)results.get(id).get("firstTime");
			
			Date secondTime = (Date)results.get(id).get("secondTime");
			
			Calendar c1 = Calendar.getInstance();

			c1.setTime(firstTime);
			
			Calendar c2 = Calendar.getInstance();

			c2.setTime(secondTime);
			
			String firstInstitu = results.get(id).get("firstInstitu").toString();
			
			String secondInstitu = results.get(id).get("secondInstitu").toString();
			
			int result=c1.compareTo(c2);

//			if(result==0)
//
//			System.out.println("c1相等c2");
//
//			else if(result<0)
//
//			System.out.println("c1小于c2");
			
			if(result<=0&&firstInstitu.equals("浦东网格中心")&&(!secondInstitu.equals("浦东网格中心"))){
				
				totalNum++;
				
			}else if(result>=0&&(!firstInstitu.equals("浦东网格中心"))&&secondInstitu.equals("浦东网格中心")){
				
				totalNum++;
				
			}else{
				noTargetIds.add(id);
			}
		}
		
		db.insert(new String[]{"TASKID"}, noTargetIds.toArray(), "noTargetIds");

		System.out.println(totalNum);
	}

}
