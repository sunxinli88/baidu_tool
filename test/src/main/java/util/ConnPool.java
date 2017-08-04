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
		log.info("��ʼ�� ---> DB_Mysql : ");
		try {
//			prop.load(new FileInputStream(new File("db2.properties")));
			prop.load(ConnPool.class.getClassLoader().getResourceAsStream(CONF_DB));
			//1				��������
//			Class.forName(prop.getProperty("jdbc.driver"));
			/**
			 * ��ʼ�����ӳ�
			 */
			bds = new BasicDataSource();
			//����ȡ��֪���ӳ�
			bds.setDriverClassName(prop.getProperty("jdbc.driver"));
			//��֪���ӳ����ݿ�λ��
			bds.setUrl(prop.getProperty("jdbc.url"));
			//��֪���ӳ����ݿ���û���
			bds.setUsername(prop.getProperty("jdbc.user"));
			//��֪���ӳ����ݿ�����
			bds.setPassword(prop.getProperty("jdbc.password"));
			//�������ӳ��е������������  ----ע�⣺���յĲ�����Intֵ
//			bds.setMaxActive(Integer.parseInt(prop.getProperty("jdbc.maxActive")));
			bds.setMaxActive(800);
			//���ﲻ�ᱻ�ͷŵ���������������
			bds.setMaxIdle(30);
			//���ó�ʱʱ��
			//���ﲻ�ᱻ�ͷŵ���С������������
			bds.setMinIdle(5);
			//���ӱ�й¶ʱ�Ƿ��ӡ
//			bds.setLogAbandoned(true);
			//��ʼ��������
			bds.setInitialSize(500);
			//�Ƿ�ر�δ�رյ�����, Ĭ��Ϊfalse
			bds.setRemoveAbandoned(true);
			//���ùر�δ�رյ����ӵĳ�ʱʱ�䣬��λ����
			bds.setRemoveAbandonedTimeout(30);
			/**
			 * ���ӳط�æʱ���ȴ��ĳ�ʱʱ��
			 * �����ӳ������е����Ӿ���ռ��ʱ������ʱ������ӳػ�ȡ������ʱ
			 * ����Ҫ�Ŷӣ���ʱʱ������Ŷ�ʱ�䣬������ʱʱ��ʱ����ȡ���ӵķ�����
			 * ���׳���ʱ�쳣
			 *///ע�������longֵ
			bds.setMaxWait(5000);
		
			
//			this.pkMap = new HashMap<String,String>();
			
		} catch (IOException e) {
			log.error("���������ļ�����",e);
			e.printStackTrace();
		}
	}
	
	public Connection getConnection() throws SQLException {
		return bds.getConnection();
	}
	
	/**
	 * �в���DML
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
			System.out.println("����close");
		}
		return affectedLine;
	}
	
	/**
	 * ����ִ���в���DML,���ݴ������ύ��ع�
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
			log.error("�������������쳣�������ع�",e);
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				log.error("����ع�ʧ��",e1);
				e1.printStackTrace();
			};
		} finally {
			if(conn!=null){
				try {
					conn.setAutoCommit(true);
				} catch (SQLException e) {
					log.error("�����桿�������Զ��ύʧ��",e);
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
//ignore,�˴��ĵ�������Ҫ�Ǵ�����ͷ����ؼ���һ��������
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

		log.info(tableName+"����:" + result);
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
	 * �޲���DML
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
	 * ��ѯ������¼
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
	 * ��ѯ������ؼ���
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
	 * �ڲ�����
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
	 * ��������
	 * 
	 * 
	 * */
	
	public int insert(String[] colums, Object[] params, String tableName) {

		StringBuffer sql = new StringBuffer();
//ignore,�˴��ĵ�������Ҫ�Ǵ�����ͷ����ؼ���һ��������
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

		log.info(tableName+"����:" + result);
		
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
