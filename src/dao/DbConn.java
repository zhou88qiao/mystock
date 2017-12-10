package dao;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;


public class DbConn {
	
	
//	private static String url = "jdbc:mysql://127.0.0.1:3306/stock";	
//	private static String username = "root";
//	private static String password = "mysql";
//	private static String driverName = "com.mysql.jdbc.Driver";
	
	 /** 
     * 获得一个数据库连接 
     *  
     * @return 
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
     */  
	
    public static Connection getConnDB(String confFile) throws IOException, ClassNotFoundException, SQLException { 
    	
    	Properties props=new Properties();
	//	FileInputStream in=new FileInputStream("conn.ini");
    	FileInputStream in=new FileInputStream(confFile);
		props.load(in);
		in.close();
		String driverName=props.getProperty("jdbc.drivers");
		String url=props.getProperty("jdbc.url");
		String username=props.getProperty("jdbc.username");
		String password=props.getProperty("jdbc.password");
    	
        Connection conn = null;  
        Class.forName(driverName);   
        conn = DriverManager.getConnection(url, username, password);  

        return conn;  
    } 
    
    
    public static Connection getConn() throws IOException, ClassNotFoundException, SQLException { 
    	
    	Properties props=new Properties();
	//	FileInputStream in=new FileInputStream("conn.ini");
    	FileInputStream in=new FileInputStream("StockConf/conn_data.ini");
		props.load(in);
		in.close();
		String driverName=props.getProperty("jdbc.drivers");
		String url=props.getProperty("jdbc.url");
		String username=props.getProperty("jdbc.username");
		String password=props.getProperty("jdbc.password");
    	
        Connection conn = null;  
        Class.forName(driverName);   
        conn = DriverManager.getConnection(url, username, password);  

        return conn;  
    } 
    
    
  
    /** 
     * 关闭数据库连接资源 
     *  
     * @param conn 
     * @param ps 
     * @param rs 
     * @throws SQLException 
     */  
    public static void closeConn(Connection conn, Statement ps, ResultSet rs) throws SQLException {  
        
        if (rs != null) {  
            rs.close();  
            rs = null;  
        }  
        if (ps != null) {  
            ps.close();  
            ps = null;  
        }  
        if (conn != null) {  
            conn.close();  
            conn = null;  
        }  
      
    }  
    
    public static void closeResult(Statement ps, ResultSet rs) throws SQLException {  
        
        if (rs != null) {  
            rs.close();  
            rs = null;  
        }  
        if (ps != null) {  
            ps.close();  
            ps = null;  
        }      
    } 
}
