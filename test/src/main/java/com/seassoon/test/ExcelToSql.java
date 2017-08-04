
package com.seassoon.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;

import org.apache.poi.ss.usermodel.DataFormatter;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.seassoon.utils.ConnectionDB;

public class ExcelToSql {

	public static void main(String[] args) throws EncryptedDocumentException, InvalidFormatException, IOException {
		// TODO Auto-generated method stub

		ConnectionDB db = new ConnectionDB(
				"jdbc:mysql://10.50.5.15:3306/db_kechuang?useUnicode=true&characterEncoding=utf8", "user_kechuang", "tykioqwsx4567hnjk");

		InputStream inp = new FileInputStream("C://Users//xx//Desktop//企业年报数据//2016.xls");

		String tableName = "kechuang_nianbao";
		// InputStream inp = new FileInputStream("workbook.xlsx");

		Workbook wb = WorkbookFactory.create(inp);

		DataFormatter formatter = new DataFormatter();
		String[] colums = new String[] { "jigoudaima","jigoumingcheng","nmcyryhj","gjjszcry","zjjszcry","jybsxlry","jyssxlry","jydxbkxlry","cskjhdryhj","cskjhdqsry","qynbyykjhddjfzc","dnxcyykjhddgdzc","sylzzfbmdkjhdzj","wtwdwkzkjhddjfzc","zhuanlishenqingshu","fmzlsqs","zhuanlishouquanshu","fangmingzhuanlishou","yyyxdfmzls","rjzzqdjs","jcdlbtsjdjs","qtzscqdjs","cskjzxhzjfwze","laizizhengfubumen","laiziqiye","laiziqitajigou","zhuceziben","zichanzongji","fuzhaiheji","zongshouru","zhuyingyewushouru","gxjscpfwsr","gyzczdnjg","chukouchuanghuizonge","shuijinzonge","suodeshui","lirunzonge","zycpfwjsly","zyjscxhzhbszqy","zyyclbcpfwgyslzqy","zycpfwtxqy","dianhua","tianbiaoren","tongjifuzeren","danweifuzeren","zhucedizhi","zcdyzbm","jingyingdizhi","jydyzbm","jingyingdichuanzhenhao","lxrdzyx","farenxingming","chenglishijian","zhucezijin","xingyefenlei","jingjileixing","qysfbrdwgxjsqy","jingyingfanwei","dmzyxjzrq","lianxiren","lxrEmail","lianxirenshouji","suoshuquxian","year" };// ,"CC","CD","CE","CF","CG"};

		String[] col = new String[] { "name" };

		int line = 0;
		for (Sheet sheet1 : wb) {

			for (Row row : sheet1) {
				if(line ==0){
					line++;
					continue;
				}

				String[] name = new String[colums.length];
				
				for(int i=0;i<name.length-1;i++){
					name[i] = formatter.formatCellValue(row.getCell(i));
				}
				name[name.length-1] = "2016";

//				String con = formatter.formatCellValue(row.getCell(0));
//				if (con.startsWith("公司名称")) {
//					String[] s1 = con.split("；");
//
//					name[0] = s1[0].split(":")[1];
//				}

				db.insert(colums, name, tableName);
			}

		}

		inp.close();

	}

}
