package util;

import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.common.base.Joiner;


public class connSql_2 {
	
//	public static int erroCount = 0;
	/**
	 * mysql插入数据,sql语句采用传参数的概念，避免修改字符串格式问题
	 * 
	 */

	private String url = "jdbc:mysql://localhost:3306/aa?useUnicode=true&characterEncoding=utf8";
	private String name = "com.mysql.jdbc.Driver";
	private String user = "root";
	private String password = "123";
	// Connection conn = DriverManager.getConnection(url, user, password);//获取连接

	private Connection conn = null;
	private ResultSet rs = null;
	private PreparedStatement ps = null;

	public connSql_2() throws SQLException {
		
		try {
			Class.forName(name);// 指定连接类型
			System.out.println("Success loading Mysql Driver!");
		} catch (Exception e) {
			System.out.print("Error loading Mysql Driver!");
			e.printStackTrace();
		}
		conn = DriverManager.getConnection(url, user, password);// 获取连接
	}

	
	public void createTb(String[] cols,String tbName) throws SQLException {

		StringBuffer sb = new StringBuffer();
		
		sb.append("create table "+tbName+"(");
		int i=0;
		for(String s:cols){
			if(s.length()>63){
				s = s.substring(0, 63);
			}
			if(s.equals("DESCRIPTION")){
				sb.append("`"+s+"` varchar(1000),");
			}else if(s.equals("BACKNOTES")){
				sb.append("`"+s+"` varchar(1000),");
			}else if(s.equals("XEXECUTEDEPTNAME")){
				sb.append("`"+s+"` varchar(600),");
			}else{
				sb.append("`"+s+"` varchar(100),");
			}
			
		}
		
		sb.append("`lon` varchar(20),`lat` varchar(20))");
		
		ps = conn.prepareStatement(sb.toString());

		ps.executeUpdate(sb.toString());
	}
	
	

	public ResultSet executeQ(String sql, Object[] params) throws SQLException {

		ps = conn.prepareStatement(sql);

		// 参数赋值
		if (params != null) {
			for (int i = 0; i < params.length; i++) {
				ps.setObject(i + 1, params[i]);
			}
		}

		rs = ps.executeQuery();

		return rs;

	}

	public int insert(List<String> headList, List<String> customerList, String table)
			throws SQLException, ParseException {
		
		StringBuffer sb = new StringBuffer();
		
		sb.append(" insert into " + table + " ( ");

		// sb.append(" insert ignore into " + table + " ( " +
		// Joiner.on(",").useForNull("").join(headList) + " ) values ");
		//
		// sb.append("("+Joiner.on(",").join(customerList)+")" );

		for (String s : headList) {

				sb.append(s + ",");
		}

		sb.deleteCharAt(sb.length() - 1);

		sb.append(")values(");

		for (int j = 0; j < customerList.size(); j++) {
			sb.append("?,");
		}

		sb.deleteCharAt(sb.length() - 1);

		sb.append(")");

		String sql = sb.toString();

		// System.out.println(sql);
		//
		PreparedStatement pst = conn.prepareStatement(sql);// 准备执行语句

		int i = 0;
		for (String s : customerList) {
			if (i < customerList.size()) {
				pst.setObject(i + 1, s);
				i++;
			} else
				break;
		}
		int result = 0;
		synchronized(table){
		try {
			
				result = pst.executeUpdate();
				System.out.println("插入成功");
			
			
		} catch (Exception e) {
			System.out.println("插入失敗");
//			erroCount++;
			for(int j=0;j<headList.size();j++){
				System.out.println(headList.get(j)+":"+customerList.get(j));
			}
			System.out.println(headList.size());
			e.printStackTrace();
		}
		pst.close();
		conn.close();
		return result;
		}
	}
	
	public int insert(List<String> customerList, String table) throws SQLException{

		StringBuffer sb = new StringBuffer();

		sb.append("insert into " + table + " values(");
		
		for (String s : customerList) {
			sb.append("\""+s+"\"" + ",");
		}
		sb.deleteCharAt(sb.length() - 1);
		
		sb.append(")");
		
		PreparedStatement ps = conn.prepareStatement(sb.toString());// 准备执行语句
		ps.executeUpdate(sb.toString());
		
		return 0;
		
	}

	public int insert(String[] headList, List<String> customerList, String table) throws SQLException, ParseException {

		List<String> head_col = new ArrayList<String>();
		for (String s : headList) {
			head_col.add(s);
		}
		StringBuffer sb = new StringBuffer();

		sb.append("insert into " + table + "(");

		for (String s : head_col) {
			sb.append(s + ",");
		}

		sb.deleteCharAt(sb.length() - 1);

		sb.append(")values(");

		for (int i = 0; i < headList.length; i++) {
			sb.append("?,");
		}

		sb.deleteCharAt(sb.length() - 1);

		sb.append(")");

		String sql = sb.toString();
		System.out.println(sql);
		PreparedStatement pst = conn.prepareStatement(sql);// 准备执行语句

		int i = 0;
		for (String s : customerList) {
//			if (i == 3) {
//				// //时间格式转换
//
//				SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//
//				Date date = dateFormat.parse(s);
//
//				SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
//
//				s = dateFormat2.format(date);
//			}
//			if (i == 6 && s == null) {
//				s = "0";
//			}
			pst.setObject(i + 1, s);
			i++;
		}

		int result = 0;

		try {
			result = pst.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		pst.close();
		conn.close();
		return result;

	}

}
