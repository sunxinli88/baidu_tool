package util;

import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class ConnMysql {
	/**
	 * mysql����������ԭʼ�ķ�������Ҫ�޸��ַ�������������
	 * 
	 * */
	
	private String url = "jdbc:mysql://localhost:3306/aa?useUnicode=true&characterEncoding=utf8";  
	private String name = "com.mysql.jdbc.Driver";  
    private String user = "root";  
    private String password = "123";
    //Connection conn = DriverManager.getConnection(url, user, password);//��ȡ����  

	public ConnMysql(){		

 	   try {  
 		   Class.forName(name);//ָ����������  
 		   System.out.println("Success loading Mysql Driver!");
 		   }catch (Exception e) { 
 		     System.out.print("Error loading Mysql Driver!"); 
 		     e.printStackTrace(); 
 	   }	   
	}
	
	public Connection getConn() throws SQLException{
		Connection conn = DriverManager.getConnection(url, user, password);//��ȡ����
		return conn;
	}
	
	public int insert(List<String> customerList) throws SQLException, ParseException{
		Connection conn = getConn();
		StringBuffer sb = new StringBuffer();
		sb.append("insert into cc(customerNo, firstName,lastName,birthDate,mailingAddress,married,numberOfKids,favouriteQuote,email,loyaltyPoints)values(");
		int i=0;
		for(String s:customerList){
			if(s!=null)
				s = s.replaceAll("'", "\\\\'");
			System.out.println(s);
			if(i==3){
//				//ʱ���ʽת��
				
				SimpleDateFormat dateFormat =new SimpleDateFormat("dd/MM/yyyy");
				
				Date date =	dateFormat.parse(s);
			
				SimpleDateFormat dateFormat2 =new SimpleDateFormat("yyyy-MM-dd");
				
				s = dateFormat2.format(date);
			}
			if(i==6 && s==null)
				sb.append("'0',");
			else
				sb.append("'"+s+"',");
			i++;
		}
		
		sb.deleteCharAt(sb.length()-1);
		sb.append(")");
		String sql = sb.toString();
		System.out.println(sb.toString());
		
		PreparedStatement pst = conn.prepareStatement(sql);//׼��ִ�����  
		
		   //String sql = "insert into table cc values(7,lily,jacky)";
		   //PreparedStatement st = conn.prepareStatement("insert into  cc (customerNo,firstName,lastName) values(7,'lily','jacky')");
		int result = 0;
		try {
			result=pst.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		return result;

	}
	
	public void delete(){
		
	}
	  //ɾ�����
	
//	   String name1 = "Bob";
//	   int i =0;
//	   String sql = "delete from cc where firstName='" + name1 + "'";
//	   PreparedStatement pstmt;
//	    try {
//	        pstmt = (PreparedStatement) conn.prepareStatement(sql);
//	        i = pstmt.executeUpdate();
//	        System.out.println("resutl: " + i);
//	        pstmt.close();
//	        conn.close();
//	    } catch (SQLException e) {
//	        e.printStackTrace();
//	    }
	
	
}
