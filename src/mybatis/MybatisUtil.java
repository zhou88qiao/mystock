package mybatis; 

import java.io.IOException;  
import java.io.InputStream;
import java.io.Reader;  
  
import org.apache.ibatis.io.Resources;  
import org.apache.ibatis.session.SqlSessionFactory;  
import org.apache.ibatis.session.SqlSessionFactoryBuilder;  
  
public class MybatisUtil {  
    
    private static SqlSessionFactory sqlSessionFactory;  
    
    public static SqlSessionFactory getSessionFactory() throws IOException {  
        if(sqlSessionFactory==null){  
          //  String resource = "D:/javawrok/StockTrunk/src/mybatis/SqlMapper.xml";  
            String resource = "mybatis/SqlMapper.xml";  
            InputStream inputStream = Resources.getResourceAsStream(resource);  
            return new SqlSessionFactoryBuilder().build(inputStream);  
        }else{  
            return sqlSessionFactory;  
        }  
    }  
  
}  