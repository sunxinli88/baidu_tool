package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class ConnectionMysql2 {
	/**
	 * 
	 * 操作数据库，包括基础的增删该查语句
	 * http://www.cnblogs.com/wuyuegb2312/p/3872607.html
	 * */
      
	public static void main(String args[] ) { 
		final String url = "jdbc:mysql://localhost:3306/aa?useUnicode=true&characterEncoding=utf8";  
		final String name = "com.mysql.jdbc.Driver";  
	    final String user = "root";  
	    final String password = "123";
	    Connection conn = null;  

 	   try {  
 		   Class.forName(name);//指定连接类型  
 		   System.out.println("Success loading Mysql Driver!");
 		   }catch (Exception e) { 
 		     System.out.print("Error loading Mysql Driver!"); 
 		     e.printStackTrace(); 
 		    }
 	   try{
 		   conn = DriverManager.getConnection(url, user, password);//获取连接  
 		   
 		   
 		   //查询语句
 		   Statement st = conn.createStatement();
 		   ResultSet rs = st.executeQuery("select * from test_content");
		   while(rs.next()){
			   System.out.println(rs.getString(2));
		   }
 		   
 		   //插入语句
// 		   pst = conn.prepareStatement(sql);//准备执行语句  
// 		   String sql = "insert into table cc values(7,lily,jacky)";
// 		   PreparedStatement st = conn.prepareStatement("insert into cc(customerNo, firstName,lastName,birthDate,mailingAddress,married,numberOfKids,favouriteQuote,email,loyaltyPoints)values('1','John','Dunbar','1945-06-13','1600 Amphitheatre ParkwayMountin View, CA 94043United States','null','0',' \"May the Force be with you.\" - Star Wars','jdunbar@gmail.com','0')");
// 		   st.executeUpdate();
// 		   st.close();
//	       conn.close();

		   
		   //删除语句
//		   String name1 = "Bob";
//		   int i =0;
//		   String sql = "delete from cc where firstName='" + name1 + "'";
//		   PreparedStatement pstmt;
//		    try {
//		        pstmt = (PreparedStatement) conn.prepareStatement(sql);
//		        i = pstmt.executeUpdate();
//		        System.out.println("resutl: " + i);
//		        pstmt.close();
//		        conn.close();
//		    } catch (SQLException e) {
//		        e.printStackTrace();
//		    }
		   
		    
		    //更新语句
//		    int j = 0;
//		    String sqlUpdate = "update cc set firstName='jacky' where lastName='Jobs'";
//		    PreparedStatement pt;
//		    try {
//		        pt = (PreparedStatement) conn.prepareStatement(sqlUpdate);
//		        j = pt.executeUpdate();
//		        System.out.println("resutl: " + j);
//		        pt.close();
//		        conn.close();
//		    } catch (SQLException e) {
//		        e.printStackTrace();
//		    }
		   
 		   System.out.println("execute successfully!");
 	   } catch (Exception e) {  
 		   System.out.println("aa");
 		   e.printStackTrace();  
	   } 
 	  	    
	}  
	}

