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

	
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// 驱动程序名
		String driver = "com.mysql.jdbc.Driver";
		// URL指向要访问的数据库名
		String url = "jdbc:mysql://127.0.0.1:3306/stock";
		// MySQL配置时的用户名
		String user = "root";
		// MySQL配置时的密码
		String password = "mysql";
	
		/*
		try {
			// 加载驱动程序
			Class.forName(driver);
			// 连续数据库
			Connection conn = DriverManager.getConnection(url, user, password);
			if(!conn.isClosed())
			{
				System.out.println("Succeeded connecting to the Database!");
				// statement用来执行SQL语句
				Statement statement = conn.createStatement();
				// 要执行的SQL语句
				String sql = "select stock_id,stock_name from stock_base_information";
				// 结果集
				ResultSet rs = statement.executeQuery(sql);
				System.out.println("-----------------");
				System.out.println("执行结果如下所示:");
				System.out.println("-----------------");
				System.out.println(" stock_id" + "\t" + " 股票名");
				System.out.println("-----------------");
				String name = null;
				while(rs.next()) {
					// 选择username这列数据
					name = rs.getString("stock_name");
					// 首先使用ISO-8859-1字符集将name解码为字节序列并将结果存储新的字节数组中。
					// 然后使用GB2312字符集解码指定的字节数组
					name = new String(name.getBytes("ISO-8859-1"),"GB2312");
					// 输出结果
					System.out.println(rs.getString("stock_id") + "\t" + name);
				}
				rs.close();
				conn.close();
			}
			else
			{
				System.out.println("failed connecting to the Database!");
			}
			
			} catch(ClassNotFoundException e) {
			System.out.println("Sorry,can`t find the Driver!");
			e.printStackTrace();
			} catch(SQLException e) {
			e.printStackTrace();
			} catch(Exception e) {
			e.printStackTrace();
			}	
*/
		
		
	}
		

}
