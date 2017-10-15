package mybatis;


import java.io.IOException;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;


import dao.StockSingle;

public class StockSingleDao implements StockSingleMapper {

	private static SqlSessionFactory sessionFactory;// = MybatisUtil.getSessionFactory();  
	
	
	 public void setup() throws IOException{        
	        sessionFactory = MybatisUtil.getSessionFactory();  
	    }
	
	@Override
	public List<StockSingle> getAllStockSingle() {
		List<StockSingle> sSingle = null;
		SqlSession session = null; 
		try {  
	      	session = sessionFactory.openSession();  
	        sSingle = session  
	                .selectList("mybatis.StockSingleMapper.getAllStockSingle");  
		} finally {  
		    session.close();  
		} 
		
		return sSingle;
	}

	@Override
	public List<String> getStockSingleAllFullId() {
		List<String> fullId = null;
		SqlSession session = null; 
		try {  
	      	session = sessionFactory.openSession();  
	      	fullId = session  
	                .selectList("mybatis.StockSingleMapper.getAllFullId");  
		} finally {  
		    session.close();  
		} 
		
		return fullId;
	}

	

}
