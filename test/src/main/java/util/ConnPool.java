package util;


import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;

import com.google.common.base.Joiner;
import com.seassoon.utils.ConnectionDB;



import org.apache.commons.dbcp.BasicDataSource;



public class ConnPool {
	public static final String CONF_DB = "mysql.properties";
	
	private static Logger log = Logger.getLogger(ConnPool.class);
	
	private BasicDataSource bds = null;
	
	
	public ConnPool () {
		Properties prop = new Properties();
		log.info("初始化 ---> DB_Mysql : ");
		try {
//			prop.load(new FileInputStream(new File("db2.properties")));
			prop.load(ConnPool.class.getClassLoader().getResourceAsStream(CONF_DB));
			//1				加载驱动
//			Class.forName(prop.getProperty("jdbc.driver"));
			/**
			 * 初始化连接池
			 */
			bds = new BasicDataSource();
			//将读取告知连接池
			bds.setDriverClassName(prop.getProperty("jdbc.driver"));
			//告知连接池数据库位置
			bds.setUrl(prop.getProperty("jdbc.url"));
			//告知连接池数据库的用户名
			bds.setUsername(prop.getProperty("jdbc.user"));
			//告知连接池数据库密码
			bds.setPassword(prop.getProperty("jdbc.password"));
			//设置连接池中的最大连接数量  ----注意：接收的参数是Int值
//			bds.setMaxActive(Integer.parseInt(prop.getProperty("jdbc.maxActive")));
			bds.setMaxActive(800);
			//池里不会被释放的最大空闲连接数量
			bds.setMaxIdle(30);
			//设置超时时间
			//池里不会被释放的最小空闲连接数量
			bds.setMinIdle(5);
			//连接被泄露时是否打印
//			bds.setLogAbandoned(true);
			//初始化连接数
			bds.setInitialSize(500);
			//是否关闭未关闭的连接, 默认为false
			bds.setRemoveAbandoned(true);
			//设置关闭未关闭的连接的超时时间，单位是秒
			bds.setRemoveAbandonedTimeout(30);
			/**
			 * 连接池繁忙时，等待的超时时间
			 * 当连接池中所有的连接均被占用时，若这时向该连接池获取新连接时
			 * 就需要排队，超时时间就是排队时间，超出超时时间时，获取链接的方法就
			 * 会抛出超时异常
			 *///注意参数是long值
			bds.setMaxWait(5000);
		
			
//			this.pkMap = new HashMap<String,String>();
			
		} catch (IOException e) {
			log.error("加载配置文件错误",e);
			e.printStackTrace();
		}
	}
	
	public Connection getConnection() throws SQLException {
		return bds.getConnection();
	}
	
	/**
	 * 有参数DML
	 * @param sql
	 * @param params
	 * @return
	 */
	public int executeUpdate(String sql, Object[] params) {
		int affectedLine = 0;
		Connection conn = null;
		PreparedStatement preStmt = null;
		try {
			conn = this.getConnection();
			preStmt = conn.prepareStatement(sql);
			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					preStmt.setObject(i + 1, params[i]);
				}
			}
			affectedLine = preStmt.executeUpdate();
		} catch (SQLException e) {
			log.error(e);
			e.printStackTrace();
		} finally {
			closeAll(conn,preStmt,null,null);
			System.out.println("走了close");
		}
		return affectedLine;
	}
	
	/**
	 * 批量执行有参数DML,根据处理结果提交或回滚
	 * @param sql
	 * @param list
	 * @return
	 */
	public int executeUpdateForBatch(String sql, List<Object[]> paramsList){
		int affectedLine = 0;
		int[] execResult = null;
		Connection conn = null;
		PreparedStatement preStmt = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			preStmt = conn.prepareStatement(sql);
			if (paramsList != null && paramsList.size()>0) {
				for(Object[] params:paramsList){
					for (int i = 0; i < params.length; i++) {
						preStmt.setObject(i + 1, params[i]);
					}
					preStmt.addBatch();
				}
			}
			execResult = preStmt.executeBatch(); 
			preStmt.clearBatch();
			for(int i=0;i<execResult.length;i++){
				affectedLine += execResult[i];
			}
			conn.commit();
		} catch (Exception e) {	
			log.error("批处理插入出现异常，即将回滚",e);
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				log.error("事务回滚失败",e1);
				e1.printStackTrace();
			};
		} finally {
			if(conn!=null){
				try {
					conn.setAutoCommit(true);
				} catch (SQLException e) {
					log.error("【警告】：设置自动提交失败",e);
					e.printStackTrace();
					throw new RuntimeException();
				}
			}
			closeAll(conn,preStmt,null,null);
		}
		return affectedLine;
	}
	
	public int insert2(String[] colums, Object[] params, String tableName) {

		StringBuffer sql = new StringBuffer();
//ignore,此处的单引号主要是处理列头会跟关键字一样的问题
		sql.append(" insert  ignore   into `" + tableName + "`  ( `" + Joiner.on("`,`").useForNull("").join(colums) + "`  )   values  ");

		List<String> recordList = new ArrayList<>();
		List<String> paramList = new ArrayList<>();

		for (int i = 1; i <= params.length; i++) {

			paramList.add("?");

			if (i >= colums.length && i % colums.length == 0) {
				recordList.add("(" + Joiner.on(",").join(paramList) + ")");
				paramList.clear();
			}

		}
	
		sql.append(Joiner.on(",").join(recordList));
		
		// System.out.println(sql .toString());

		int result = this.executeUpdate(sql.toString(), params);

		log.info(tableName+"新增:" + result);
		if(result==0){
			return 199;
		}
		
//		this.closeAll();
		return 0;
	}
	
	public int insert(ConnPool db, List<String> columnsList, List<Object> paramsList, String tableName) {
		log.info(" insert start");
		int x = db.insert(columnsList.toArray(new String[columnsList.size()]), paramsList.toArray(), tableName);

		paramsList.clear();
		log.info(" insert end");
		return x;
	}
	
	
	/**
	 * 无参数DML
	 * @param sql
	 * @return
	 */
	public int executeUpdate(String sql) {
		int affectedLine = 0;
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = this.getConnection();
			stmt = conn.createStatement();
			affectedLine = stmt.executeUpdate(sql);
		} catch (SQLException e) {
			log.error(e);
			e.printStackTrace();
		} finally {
			closeAll(conn,null,stmt,null);
		}
		return affectedLine;
	}
	
	/**
	 * 查询单条记录
	 * @param sql
	 * @param params
	 * @return
	 */
	public Object executeQuerySingle(String sql, Object[] params) {
		Object object = null;
		Connection conn = null;
		PreparedStatement preStmt = null;
		try {
			conn = this.getConnection();
			preStmt = conn.prepareStatement(sql);
			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					preStmt.setObject(i + 1, params[i]);
				}
			}
			ResultSet resSet = preStmt.executeQuery();
			if (resSet.next()) {
				object = resSet.getObject(1);
			}
		} catch (SQLException e) {
			log.error(e);
			e.printStackTrace();
		} finally {
			closeAll(conn,preStmt,null,null);
		}
		return object;
	}
	/**
	 * 查询结果返回集合
	 * @param sql
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> executeQuery(String sql, Object[] params) {
		Connection conn = null;
		PreparedStatement preStmt = null;
		ResultSet rs = null;
		try {
			rs = executeQueryRS(conn,preStmt,sql, params);
		} catch (Exception e) {
			// TODO: handle exception
		}
		if (rs == null) {
			System.out.println("");
		}
		
		ResultSetMetaData rsmd = null;
		
		int columnCount = 0;
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			rsmd = rs.getMetaData();
			columnCount = rsmd.getColumnCount();
			while (rs.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				for (int i = 1; i <= columnCount; i++) {
					map.put(rsmd.getColumnLabel(i), rs.getObject(i));
				}
				list.add(map);
			}
		} catch (SQLException e) {
			log.error(e);
			e.printStackTrace();
		} finally {
			closeAll(conn,preStmt,null,rs);
		}
		return list;
	}
	
	/**
	 * 内部调用
	 * @param sql
	 * @param params
	 * @return
	 */
	private ResultSet executeQueryRS(Connection conn, PreparedStatement preStmt,String sql,Object[] params) {
		ResultSet resSet = null;
		try {
			conn = this.getConnection();
			preStmt = conn.prepareStatement(sql);
			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					preStmt.setObject(i + 1, params[i]);
				}
			}
			resSet = preStmt.executeQuery();
		} catch (SQLException e) {
			log.error(e);
			e.printStackTrace();
		}
		return resSet;
	}
	/**
	 * 批量插入
	 * 
	 * 
	 * */
	
	public int insert(String[] colums, Object[] params, String tableName) {

		StringBuffer sql = new StringBuffer();
//ignore,此处的单引号主要是处理列头会跟关键字一样的问题
		sql.append(" insert  ignore   into `" + tableName + "`  ( `" + Joiner.on("`,`").useForNull("").join(colums) + "`  )   values  ");

		List<String> recordList = new ArrayList<>();
		List<String> paramList = new ArrayList<>();

		for (int i = 1; i <= params.length; i++) {

			paramList.add("?");

			if (i >= colums.length && i % colums.length == 0) {
				recordList.add("(" + Joiner.on(",").join(paramList) + ")");
				paramList.clear();
			}

		}
	
		sql.append(Joiner.on(",").join(recordList));
		
		// System.out.println(sql .toString());

		int result = this.executeUpdate(sql.toString(), params);

		log.info(tableName+"新增:" + result);
		
		return 0;
	}
	
	public void closeAll(Connection conn,PreparedStatement preStmt,Statement stmt,ResultSet resSet) {
//		try {
//			if (resSet != null) {
//				resSet.close();
//			}
//			if (preStmt != null) {
//				preStmt.close();
//			}
//			if (stmt != null) {
//				stmt.close();
//			}
//			if (conn != null) {
//				conn.close();
//			}
//		} catch (SQLException e) {
//		}
	}
}
