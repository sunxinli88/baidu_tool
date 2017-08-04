package com.seassoon.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.client.utils.URLEncodedUtils;

import com.seassoon.utils.ConnectionDB;

public class SuiChao {

	private static ConnectionDB db = new ConnectionDB(
			"jdbc:mysql://172.16.40.8:3306/db_suichao?useUnicode=true&characterEncoding=utf8", "user_suichao",
			"xxhvnfh%$^hgjui9");

	/**
	 * 创建文件，用于存储每一个表格对应的接口文档
	 */
	public static boolean createFile(File fileName) throws Exception {
		boolean flag = false;
		try {
			if (!fileName.exists()) {
				fileName.createNewFile();
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 获取数据库下的所有表格
	 * */
	public static List<String> getTableNames() {
		// 获取数据库下所有表格
		String sql = "SHOW TABLES";

		List<String> tbs = new ArrayList<String>();
		
		List<Map<String, Object>> rs = db.excuteQuery(sql, null);

		for (Map<?, ?> r : rs) {

			String tbName = r.get("Tables_in_db_suichao").toString();
			
			tbs.add(tbName);
			}
		return tbs;
	}
	
	/**
	 * 返回表格的字段名字、字段是否为空、字段注释、是否主键、字段类型等等
	 * @return 
	 * */
	public static List<Map<String, Object>> getInfOfFields(String tbName){
		String sql ="SHOW FULL FIELDS FROM "+tbName;
		
		List<Map<String, Object>> rs = db.excuteQuery(sql, null);
		
		return rs;
	}

	/**
	 *  获取表格注释信息
	 * */
	public static String getCommentsOfTable(String tableName) {

		List<Map<String, Object>> x = db.excuteQuery("show  create  table " + tableName, null);

		int index = x.get(0).get("Create Table").toString().indexOf("COMMENT=");

		if (index < 0) {
			// 表格没有注释信息
			return "";
		}
		String comment = x.get(0).get("Create Table").toString().substring(index + 9);

		comment = comment.substring(0, comment.length() - 1);

		return comment;
	}
	
	/**
	 * 表名字从下划线转换为驼峰形式
	 * */
	public static String camelName(String name) {  

	    StringBuilder result = new StringBuilder();  
	    // 快速检查  
	    if (name == null || name.isEmpty()) {  
	        // 没必要转换  
	        return "";  
	    } else if (!name.contains("_")) {  
	        // 不含下划线，仅将首字母小写  
	        return name.substring(0, 1).toLowerCase() + name.substring(1);  
	    }  
	    // 用下划线将原始字符串分割  
	    String camels[] = name.split("_");  
	    for (String camel :  camels) {  
	        // 跳过原始字符串中开头、结尾的下换线或双重下划线  
	        if (camel.isEmpty()) {  
	            continue;  
	        }  
	        // 处理真正的驼峰片段  
	        if (result.length() == 0) {  
	            // 第一个驼峰片段，全部字母都小写  
	            result.append(camel.toLowerCase());  
	        } else {  
	            // 其他的驼峰片段，首字母大写  
	            result.append(camel.substring(0, 1).toUpperCase());  
	            result.append(camel.substring(1).toLowerCase());  
	        }  
	    }  
	    return result.toString();  
	} 
	
	/**
	 * 从数据库表格获取记录，用来显示
	 * */
	public static String getList(Map<String, Object> record){
		
		StringBuilder sb = new StringBuilder();
		
		int i = 1;
		
		for(String key:record.keySet()){
			if(i == record.size()){
				sb.append("\t\""+key+"\": \""+record.get(key)+"\"");
			}else{
				sb.append("\t\""+key+"\": \""+record.get(key)+"\",\r\n");
				i++;
			}
		}
			
		return sb.toString();
		
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		List<String> tables = getTableNames();
		
		for (String table : tables) {
			
			//选取两条记录用于list显示，其他的编辑、获取等只需要一条
			List<Map<String, Object>> records = db.excuteQuery("select * from " + table+" limit 2 ", null);
			
			String record1 = getList(records.get(0));
			
			String record2 = getList(records.get(1));		
			
			String camelN = camelName(table);

			//表格注释
			String tbComment = getCommentsOfTable(table);

			/**
			 * 获取表格名，用表格名创建文件
			 */

			String file_name = tbComment.equals("")?table:tbComment;
			
//			file_name = URLEncoder.encode(file_name, "UTF-8");
			String fileName = "G://隋巢//suichao08_1//" + file_name + ".txt";

			File f = new File(fileName);

			if (createFile(f)) {

				System.out.println(fileName + "创建文件成功");

			} else {

				System.out.println(fileName + "文件已存在");
				continue;
			}

			/**
			 * 读取模板，针对每一个表格修改模板中的特定数据
			 */
			String file = "G://隋巢//suichao125//demo.txt";

			InputStreamReader read = new InputStreamReader(new FileInputStream(file), "gbk");// 考虑到编码格式

			// 输入参数，读取模板
			BufferedReader input = new BufferedReader(read);

			// 输出参数，并修改模板中的部分内容
			BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f),"UTF-8"));

			String lineTxt = null;

			int line = 1;

			while ((lineTxt = input.readLine()) != null) {

				if (line == 1) {
					// 处理模板的第一行数据
					lineTxt = lineTxt.replace("《表格注释》", tbComment);
					lineTxt = lineTxt.replace("《表明》", table);
					
					output.write(lineTxt + "\r\n");

				}else if(line ==4||line==38||line==55||line==79||line==95){
					//处理url中的数据
					
					lineTxt = lineTxt.replace("《表格名称，驼峰显示》", camelN);
					
					output.write(lineTxt + "\r\n");
					
				}else if(line == 34||line == 64){
					//参数说明，数据库表格中：字段、是否为NULL和字段注释，
					
					List<Map<String, Object>> infoFiels = getInfOfFields(table);
										
					for (Map<?, ?> m : infoFiels) {
						
						StringBuilder sb = new StringBuilder();
						
						String nessesary = m.get("Null").toString();
						
						if(nessesary.equals("NO")){
							nessesary = "是";
						}else if(nessesary.equals("YES")){
							nessesary = "否";
						}
						
						sb.append("|"+m.get("Field") + "\t| "+nessesary+" \t |" + m.get("Comment")+"\t|");
						
						output.write(sb + "\r\n");
						
					}
				}else if(line ==24){
					//list的返回数据
					output.write("\t    {\r\n"+record1 + "\r\n\t\t},\r\n\t\t{\r\n"+record2+"\r\n\t\t}\r\n");
					
					
				}else if(line == 44||line==99||line == 59){
					//处理get的返回数据,处理add的请求示例,编辑示例
					output.write(record1 + "\r\n");
					
				}else{
					output.write(lineTxt + "\r\n");
				}
				
				line++;

			}
			output.close();

			read.close();
		}

	}

}
