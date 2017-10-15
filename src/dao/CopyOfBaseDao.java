package dao;

import java.io.IOException;
import java.lang.reflect.Field;  
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import common.stockLogger;

public class CopyOfBaseDao {
	
	 private Connection conn;  
	 private PreparedStatement ps = null;  
	 private ResultSet rs = null;  
//	 private DbConn dbconn;  
	 public CopyOfBaseDao()
	 {
		 
	 }
	 
	 public CopyOfBaseDao(Connection conn)
	 {
		 this.conn  = conn;
	 }
	    
	  //获取查询数据
	    public List<String> getQuery(String sql,String parameters[]) throws SQLException{
	    	List list = new ArrayList();  
	        try{
	        	Connection	conn = DbConn.getConn();  
	        	PreparedStatement ps = conn.prepareStatement(sql);  
	            
	            if(parameters!=null&&!parameters.equals("")){
	                for(int i=0;i<parameters.length;i++){
	                    ps.setString(i+1,parameters[i]);
	                }                    
	            }    
	            ResultSet rs = ps.executeQuery();
	         // 获取此 ResultSet 对象的列的编号、类型和属性。
	            ResultSetMetaData metaData = rs.getMetaData();
	            int col_len = metaData.getColumnCount();// 获取列的长度
	            while(rs.next())
	            {
	            	 for (int i = 0; i < col_len; i++)
	                 {
	            	//	 rs.
	            		 String value=rs.getString(i+1);
	            		 /*
	                     String cols_name = metaData.getColumnName(i + 1);
	                     Object cols_value = rs.getObject(cols_name);
	                     if (cols_value == null)// 列的值没有时，设置列值为“”
	                     {
	                         cols_value = "";	                     
	                  	}
	                  	 System.out.println(+cols_name+":"+cols_name);
	                     */
	                 //    System.out.println(value);	 
	                     list.add(value);
	                 }
	            }
	        }catch (SQLException e) {
	            System.out.println("MySQL操作错误");
	            e.printStackTrace();
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	        	//DbConn.closeResult(ps,rs);
	        	DbConn.closeConn(conn, ps, rs);  
	        }

	        return list;
	    } 
	    
	  //获取查询数据float
	    public List<Float> getFloatQuery(String sql,String parameters[]) throws SQLException{
	    	/////List list = new ArrayList(); /// 
	    	List<Float> list = new ArrayList<Float>();
	        try{
	        	Connection conn = DbConn.getConn();  
	        	PreparedStatement ps = conn.prepareStatement(sql);  
	            
	            if(parameters!=null&&!parameters.equals("")){
	                for(int i=0;i<parameters.length;i++){
	                    ps.setString(i+1,parameters[i]);
	                }                    
	            }    
	            ResultSet rs = ps.executeQuery();
	         // 获取此 ResultSet 对象的列的编号、类型和属性。
	            ResultSetMetaData metaData = rs.getMetaData();
	            int col_len = metaData.getColumnCount();// 获取列的长度
	            while(rs.next())
	            {
	            	 for (int i = 0; i < col_len; i++)
	                 {
	            		 float value=rs.getFloat(i+1);
	                     list.add(value);
	                 }
	            }
	        }catch (SQLException e) {
	            System.out.println("MySQL操作错误");
	            e.printStackTrace();
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	        	//DbConn.closeResult(ps,rs);
	        	DbConn.closeConn(conn, ps, rs);  
	        }

	        return list;
	    } 
	    
	    //获取查询数据float
	    public List<Integer> getIntQuery(String sql,String parameters[]) throws SQLException{
	    	/////List list = new ArrayList(); /// 
	    	List<Integer> list = new ArrayList<Integer>();
	        try{
	        	Connection conn = DbConn.getConn();  
	        	PreparedStatement ps = conn.prepareStatement(sql);  
	            
	            if(parameters!=null&&!parameters.equals("")){
	                for(int i=0;i<parameters.length;i++){
	                    ps.setString(i+1,parameters[i]);
	                }                    
	            }    
	            ResultSet rs = ps.executeQuery();
	         // 获取此 ResultSet 对象的列的编号、类型和属性。
	            ResultSetMetaData metaData = rs.getMetaData();
	            int col_len = metaData.getColumnCount();// 获取列的长度
	            while(rs.next())
	            {
	            	 for (int i = 0; i < col_len; i++)
	                 {
	            		 int value=rs.getInt(i+1);
	                     list.add(value);
	                 }
	            }
	        }catch (SQLException e) {
	            System.out.println("MySQL操作错误");
	            e.printStackTrace();
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	        	//DbConn.closeResult(ps,rs);
	        	DbConn.closeConn(conn, ps, rs);  
	        }

	        return list;
	    } 
	    
	  //获取单行查询数据
	    public String getSingleQuery(String sql,String parameters[]) throws SQLException{
	    	String value="";
	        try{
	        	Connection conn = DbConn.getConn();  
	        	PreparedStatement ps = conn.prepareStatement(sql);  
	            
	            if(parameters!=null&&!parameters.equals("")){
	                for(int i=0;i<parameters.length;i++){
	                    ps.setString(i+1,parameters[i]);
	                }                    
	            }    
	            ResultSet rs = ps.executeQuery();
	         // 获取此 ResultSet 对象的列的编号、类型和属性。
	            ResultSetMetaData metaData = rs.getMetaData();
	            int col_len = metaData.getColumnCount();// 获取列的长度
	           // System.out.println("col_len:"+col_len);
	            if(col_len!=1)
	            	return null;
	            while(rs.next())
	            {
	            	 for (int i = 0; i < col_len; i++)
	                 {
	            		  value=rs.getString(i+1);
	            		 /*
	                     String cols_name = metaData.getColumnName(i + 1);
	                     Object cols_value = rs.getObject(cols_name);
	                     if (cols_value == null)// 列的值没有时，设置列值为“”
	                     {
	                         cols_value = "";	                     
	                  	}
	                  	 System.out.println(+cols_name+":"+cols_name);
	                     */
	                 //    System.out.println(value);	 
	                     return value;
	                 }
	            }
	        }catch (SQLException e) {
	            System.out.println("MySQL操作错误");
	            e.printStackTrace();
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	        //	DbConn.closeResult(ps,rs);
	        	DbConn.closeConn(conn, ps, rs);  
	        }

	        return value;
	    } 
	    //获取单行查询数据
	    public Date getSingleDateQuery(String sql,String parameters[]) throws SQLException{
	    	Date value = null;
	        try{
	        	Connection conn = DbConn.getConn();  
	        	PreparedStatement ps = conn.prepareStatement(sql);  
	            
	            if(parameters!=null&&!parameters.equals("")){
	                for(int i=0;i<parameters.length;i++){
	                    ps.setString(i+1,parameters[i]);
	                }                    
	            }    
	            rs = ps.executeQuery();
	         // 获取此 ResultSet 对象的列的编号、类型和属性。
	            ResultSetMetaData metaData = rs.getMetaData();
	            int col_len = metaData.getColumnCount();// 获取列的长度
	           // System.out.println("col_len:"+col_len);
	            if(col_len!=1)
	            	return null;
	            while(rs.next())
	            {
	            	 for (int i = 0; i < col_len; i++)
	                 {
	            		  value=rs.getDate(i+1);
	            		 /*
	                     String cols_name = metaData.getColumnName(i + 1);
	                     Object cols_value = rs.getObject(cols_name);
	                     if (cols_value == null)// 列的值没有时，设置列值为“”
	                     {
	                         cols_value = "";	                     
	                  	}
	                  	 System.out.println(+cols_name+":"+cols_name);
	                     */
	                 //    System.out.println(value);	 
	                     return value;
	                 }
	            }
	        }catch (SQLException e) {
	            System.out.println("MySQL操作错误");
	            e.printStackTrace();
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	        	//DbConn.closeResult(ps,rs);
	        	DbConn.closeConn(conn, ps, rs);  
	        }

	        return value;
	    } 
	    //获取单行float查询数据
	    public float getSingleFloatQuery(String sql,String parameters[]) throws SQLException{
	    	float value=0.0f;
	        try{
	        	Connection conn = DbConn.getConn();  
	        	PreparedStatement ps = conn.prepareStatement(sql);  
	            
	            if(parameters!=null&&!parameters.equals("")){
	                for(int i=0;i<parameters.length;i++){
	                    ps.setString(i+1,parameters[i]);
	                }                    
	            }    
	            ResultSet rs = ps.executeQuery();
	         // 获取此 ResultSet 对象的列的编号、类型和属性。
	            ResultSetMetaData metaData = rs.getMetaData();
	            int col_len = metaData.getColumnCount();// 获取列的长度
	           // System.out.println("col_len:"+col_len);
	            if(col_len!=1)
	            	return 0;
	            while(rs.next())
	            {
	            	 for (int i = 0; i < col_len; i++)
	                 {
	            		  value=rs.getFloat(i+1);
	            		  	            		  
	            		 /*
	                     String cols_name = metaData.getColumnName(i + 1);
	                     Object cols_value = rs.getObject(cols_name);
	                     if (cols_value == null)// 列的值没有时，设置列值为“”
	                     {
	                         cols_value = "";	                     
	                  	}
	                  	 System.out.println(+cols_name+":"+cols_name);
	                     */
	                 //    System.out.println(value);	 
	                     return value;
	                 }
	            }
	        }catch (SQLException e) {
	            System.out.println("MySQL操作错误");
	            e.printStackTrace();
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	        	//DbConn.closeResult(ps,rs);
	        	DbConn.closeConn(conn, ps, rs);  
	        }

	        return value;
	    } 
	    
	    //获取单行int查询数据
	    public int getSingleIntQuery(String sql,String parameters[]) throws SQLException{
	    	int value=0;
	        try{
	        	Connection conn = DbConn.getConn(); 
	        	
	        	PreparedStatement ps = conn.prepareStatement(sql);  
	            
	            if(parameters!=null&&!parameters.equals("")){
	                for(int i=0;i<parameters.length;i++){
	                    ps.setString(i+1,parameters[i]);
	                }                    
	            }    
	            ResultSet rs = ps.executeQuery();
	         // 获取此 ResultSet 对象的列的编号、类型和属性。
	            ResultSetMetaData metaData = rs.getMetaData();
	            int col_len = metaData.getColumnCount();// 获取列的长度
	           // System.out.println("col_len:"+col_len);
	            if(col_len!=1)
	            	return 0;
	            while(rs.next())
	            {
	            	 for (int i = 0; i < col_len; i++)
	                 {
	            		  value=rs.getInt(i+1);
	            		 /*
	                     String cols_name = metaData.getColumnName(i + 1);
	                     Object cols_value = rs.getObject(cols_name);
	                     if (cols_value == null)// 列的值没有时，设置列值为“”
	                     {
	                         cols_value = "";	                     
	                  	}
	                  	 System.out.println(+cols_name+":"+cols_name);
	                     */
	                 //    System.out.println(value);	 
	                     return value;
	                 }
	            }
	        }catch (SQLException e) {
	            System.out.println("MySQL操作错误");
	            e.printStackTrace();
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	        	//DbConn.closeResult(ps,rs);
	        	 DbConn.closeConn(conn, ps, rs);  
	        }

	        return value;
	    } 
	    
	    /** 
	     * 查询符合条件的记录数 
	     *  
	     * @param sql 
	     *            要执行的sql语句 
	     * @param args 
	     *            给sql语句中的？赋值的参数列表 
	     * @return 符合条件的记录数 
	     * @throws SQLException 
	     * @throws ClassNotFoundException 
	     * @throws IOException 
	     */  
	    public long getCount(String sql, Object... args) throws IOException, ClassNotFoundException, SQLException {  
	      Connection conn = DbConn.getConn();  
	        try {  
	        	PreparedStatement ps = conn.prepareStatement(sql);  
	            for (int i = 0; i < args.length; i++) {  
	                ps.setObject(i + 1, args[i]);  
	            }  
	            ResultSet rs = ps.executeQuery();  
	            if (rs.next()) {  
	                return rs.getLong(1);  
	            }  
	        } catch (SQLException e) {  
	            e.printStackTrace();  
	        } finally {  
	        	
	        	//DbConn.closeResult(ps,rs);	  
	        	DbConn.closeConn(conn, ps, rs); 
	        }  
	        return 0L;  
	    }  
	  
	    /** 
	     * 查询实体对象的，并封装到一个集合 
	     *  
	     * @param <T> 
	     *            要查询的对象的集合 
	     * @param sql 
	     *            要执行的sql语句 
	     * @param clazz 
	     *            要查询的对象的类型 
	     * @param args 
	     *            给sql语句中的？赋值的参数列表 
	     * @return 要查询的类的集合，无结果时返回null 
	     * @throws SQLException 
	     * @throws ClassNotFoundException 
	     * @throws IOException 
	     */  
	    public <T> List<T> executeQuery(String sql, Class<T> clazz, Object... args) throws IOException, ClassNotFoundException, SQLException {  
	    	Connection conn = DbConn.getConn();         
	       	        
	        List list = new ArrayList();  
	        try {  
	        	PreparedStatement ps = conn.prepareStatement(sql);  
	            for (int i = 0; i < args.length; i++) {  
	                ps.setObject(i + 1, args[i]);  
	            }  
	            ResultSet rs = ps.executeQuery();  
	            Field[] fs = clazz.getDeclaredFields();  
	            String[] colNames = new String[fs.length];  
	            String[] rTypes = new String[fs.length];  
	            Method[] methods = clazz.getMethods();  
	            while (rs.next()) {  	            	 
	                for (int i = 0; i < fs.length; i++) {  
	                    Field f = fs[i];  
	                    String colName = f.getName().substring(0, 1).toUpperCase()  
	                            + f.getName().substring(1);  	                 
	                    colNames[i] = colName;  
	                    String rType = f.getType().getSimpleName();  
	                    rTypes[i] = rType;  
	                }  
	  
	                Object object = (T) clazz.newInstance();  
	                for (int i = 0; i < colNames.length; i++) {  
	                    String colName = colNames[i];  
	                    String methodName = "set" + colName;  
	                    // 查找并调用对应的setter方法赋  
	                    for (Method m : methods) {  
	                        if (methodName.equals((m.getName()))) {  
	                            // 如果抛了参数不匹配异常，检查JavaBean中该属性类型，并添加else分支进行处理  
	                            if ("int".equals(rTypes[i])  
	                                    || "Integer".equals(rTypes[i])) {  
	                            	//  m.invoke(object, rs.getObject(colName));  
	                              int b=rs.getInt(colName);  	                           
	                              m.invoke(object, rs.getInt(colName));  
	                            } else if ("Date".equals(rTypes[i])) {  
	                                m.invoke(object, rs.getDate(colName));  
	                            } else if ("Timestamp".equals(rTypes[i])) {  
	                                m.invoke(object, rs.getTimestamp(colName));  
	                            } else {  	                            
	                                m.invoke(object, rs.getObject(colName));  
	                            }  
	                            break;  
	                        }  
	                    }  
	                }  
	               
	                list.add(object);  
	            } 
	            
	            return list;  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        } finally {  
	        	
	        	//DbConn.closeResult(ps,rs);
	            DbConn.closeConn(conn, ps, rs);  
	        }  
	        
	        return null;  
	    }  
	   
	    /*
	    public <T> T executeSingleQuery(String sql, Class<T>cls, List<Object> params) throws SQLException, InstantiationException, IllegalAccessException, SecurityException, NoSuchFieldException 
	    {		
    		T resultObject = null;		
    		int index = 1;		
    		ps = conn.prepareStatement(sql);  		
    	 	if(params != null && !params.isEmpty()){		
	    		for(int i = 0; i<params.size(); i++){			
	    			ps.setObject(index++, params.get(i));	
	    		}	
	    	}			
    		ResultSet rs = ps.executeQuery();	
    		ResultSetMetaData metaData  = rs.getMetaData();	
    		int cols_len = metaData.getColumnCount();		
    		while(rs.next())
    		{		
    			//通过反射机制创建一个实例		
    			resultObject = (T) cls.newInstance();		
    			for(int i = 0; i<cols_len; i++){		
    				String cols_name = metaData.getColumnName(i+1);			
    				Object cols_value = rs.getObject(cols_name);		
    				if(cols_value == null){		
    					cols_value = "";			
    				}			
    				Field field = cls.getDeclaredField(cols_name);		
    				field.setAccessible(true); //打开javabean的访问权限	
    				field.set(resultObject, cols_value);		
    			}		
    		}		
    		return resultObject;    	
    		
	    }
	    */
	  
	    /** 
	     * 以对象的形式保存或更新一个实体 
	     *  
	     * @param sql 
	     *            要执行的sql语句 
	     * @param object 
	     *            要保存或更新的实体对象 
	     * @param args 
	     *            不需要赋值的列标组成的数组，例如sql语句 
	     *            "insert into tbl_user values(seq_user.nextval,?,?,?)"应为1 
	     * @return 操作结果，1 成功，0 失败 
	     * @throws SQLException 
	     * @throws ClassNotFoundException 
	     * @throws IOException 
	     */  
	    public int saveEntity(String sql, Object object, int... args) throws SQLException, IOException, ClassNotFoundException {  
	    	Connection conn = DbConn.getConn();  
	        try {  
	        	PreparedStatement ps = conn.prepareStatement(sql);  
	            Class c = object.getClass();  
	            Field[] fields = object.getClass().getDeclaredFields();  
	            int temp = 1;// 正赋值的？的下标，最大下标为args的长度  
	            int colIndex = 1;// SQL语句中的当前字段下标  
	            int t = 0;// args数组的下标  
	            for (int j = 0; j < fields.length; j++) {  
	                Field field = fields[j];// 得到某个声明属性  
	                String methodName = "get"  
	                        + field.getName().substring(0, 1).toUpperCase()  
	                        + field.getName().substring(1);  
	                Method method = c.getMethod(methodName);// 得到了当前类中的一个method  
	                String rType = field.getType().getSimpleName().toString();  
	                if (t < args.length && colIndex == args[t]) {  
	                    t++;  
	                } else if ("int".equals(rType) || "INTEGER".equals(rType)) {  
	                    ps.setInt(temp++, (Integer) method.invoke(object));  
	                } else {  
	                    ps.setObject(temp++, method.invoke(object));  
	                }  
	                colIndex++;// 更新索引下标  
	            }  
	            return ps.executeUpdate();  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        } finally {  
	        	//DbConn.closeResult(ps,rs);
	           DbConn.closeConn(conn, ps, null);  
	        }  
	        return 0;  
	    }  
	    
	    //反射
	    public <T>T executeSingleQuery(String sql, Class<T> clazz, Object... args) throws IOException, ClassNotFoundException, SQLException {  
	    	Connection conn = DbConn.getConn(); 
	        T object = null;
	        try {  
	        	PreparedStatement ps = conn.prepareStatement(sql);  
	            for (int i = 0; i < args.length; i++) {  
	                ps.setObject(i + 1, args[i]);  
	            }  
	            ResultSet rs = ps.executeQuery();  
	            Field[] fs = clazz.getDeclaredFields();  
	            String[] colNames = new String[fs.length];  
	            String[] rTypes = new String[fs.length];  
	            Method[] methods = clazz.getMethods();  
	            while (rs.next()) {  
	                for (int i = 0; i < fs.length; i++) {  
	                    Field f = fs[i];  
	                    String colName = f.getName().substring(0, 1).toUpperCase()  
	                            + f.getName().substring(1);  	                 
	                    colNames[i] = colName;  
	                    String rType = f.getType().getSimpleName();  
	                    rTypes[i] = rType;  
	                }  
	  
	                object = (T) clazz.newInstance();  
	                for (int i = 0; i < colNames.length; i++) {  
	                    String colName = colNames[i];  
	                    String methodName = "set" + colName;  
	                    // 查找并调用对应的setter方法赋  
	                    for (Method m : methods) {  
	                        if (methodName.equals((m.getName()))) {  
	                            // 如果抛了参数不匹配异常，检查JavaBean中该属性类型，并添加else分支进行处理  
	                            if ("int".equals(rTypes[i])  
	                                    || "Integer".equals(rTypes[i])) {  
	                            	//  m.invoke(object, rs.getObject(colName));  
	                              int b=rs.getInt(colName);  	                           
	                              m.invoke(object, rs.getInt(colName));  
	                            } else if ("Date".equals(rTypes[i])) {  
	                                m.invoke(object, rs.getDate(colName));  
	                            } else if ("Timestamp".equals(rTypes[i])) {  
	                                m.invoke(object, rs.getTimestamp(colName));  
	                            } else {  
	                            //	System.out.println(colName);
	                                m.invoke(object, rs.getObject(colName));  
	                            }  
	                            break;  
	                        }  
	                    }  
	                }  
	                 
	            }  
	            return object;  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        } finally {  
	        //	DbConn.closeResult(ps,rs);
	           DbConn.closeConn(conn, ps, rs);  
	        }  
	        return null;  
	    }  
	   
	  
	    /** 
	     * 执行可变参数的SQL语句，进行保存、删除或更新操作 
	     *  
	     * @param sql 
	     *            要执行的sql语句，?的赋值顺序必须与args数组的顺序相同 
	     * @param args 
	     *            要赋值的参数列表 
	     * @return 操作结果，正数 成功，0 失败 
	     * @throws SQLException 
	     * @throws ClassNotFoundException 
	     * @throws IOException 
	     */  
	    public int saveOrUpdate(String sql, Object... args) throws IOException, ClassNotFoundException, SQLException {  
	      Connection conn = DbConn.getConn();  
	        try {  
	        	PreparedStatement ps = conn.prepareStatement(sql);  
	            for (int j = 0; j < args.length; j++) {  
	                ps.setObject(j + 1, args[j]); 
	               // System.out.println(args[j]);
	            }  
	            return ps.executeUpdate();  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        } finally {  
	        	
	        	//DbConn.closeResult(ps,rs);
	            DbConn.closeConn(conn, ps, null);  
	        }  
	        return 0;  
	    }  

}
