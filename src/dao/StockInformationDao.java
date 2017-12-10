package dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import common.ConstantsInfo;
import common.stockLogger;

public class StockInformationDao extends BaseDao {
	
	/*
	private String stockId;
	private String stockName;
	private String stockClassification;
	private String stockIndustry;
	private String stockConcept;
	private String stockRegional;
	
	*/
	static String stockTable="stock_info";
	
	public StockInformationDao()
	{
		
	}
	public StockInformationDao(Connection conn)
	{
		super(conn);
	}
	/*
	System.out.println("aa"+this.getStockTableName()+stockInfo.getStockId()+stockInfo.getStockName()+stockInfo.getTodayOpeningPrice()+stockInfo.getYesterdayClosingPrice()+stockInfo.getCurrentPrice()+
            stockInfo.getDate()+stockInfo.getTime());*/
		public int addStock(StockInformation stockInformation) throws SQLException, IOException, ClassNotFoundException {  
	        return super.saveEntity(  
	              "insert into stock values(stock_info_field.nextval,?,?,?)",stockInformation, 1); 
	    }  
	  
	    public int addStock2(StockInformation stockInformation) throws IOException, ClassNotFoundException, SQLException { 
	        return super.saveOrUpdate(  
	        		"insert into stock values(?,?,?,?,?,?,?)", 
	        		stockInformation.getStockId(),stockInformation.getStockFullId(),stockInformation.getStockName(),stockInformation.getStockClassification(), stockInformation.getStockIndustry(),stockInformation.getStockConcept(),stockInformation.getStockRegional());  
	        /*
	        return super.saveOrUpdate(  
		              //  "insert into ? values(stock_info_field.nextval,?,?,?,?,?,?,?)", 
		                stockTableName,stockInformation.getStockId(),stockInformation.getStockName(),stockInformation.getTodayOpeningPrice(), stockInformation.getYesterdayClosingPrice(),stockInformation.getCurrentPrice(),
	                    stockInformation.getDate(), stockInformation.getTime());  
	       */
	    }  
	    public int addStockInfo(StockInformation stockInformation) throws IOException, ClassNotFoundException, SQLException { 
	        return super.saveOrUpdate(  
	        		"insert into stock_info values(?,?,?,?,?,?,?)", 
	        		stockInformation.getStockId(),stockInformation.getStockFullId(),stockInformation.getStockName(),stockInformation.getStockClassification(), stockInformation.getStockIndustry(),stockInformation.getStockConcept(),stockInformation.getStockRegional());  
	     } 
	  
	    public int deleteStockInfoById(int stockInformationId) throws IOException, ClassNotFoundException, SQLException {  
	        return super  
	                .saveOrUpdate("delete from stock_info where stockId=?",stockInformationId);  
	    }  
	
	    /*
	    public int modStockInfoById(int stockInformationId, StockInformation stockInformation) {  
	        return super.saveOrUpdate(  
	                        "update stock set stockName=?,todayOpeningPrice=?,yesterdayClosingPrice=?,currentPrice=?,date=?,time=? where stockId=?",  
	                        stockInformation.getStockName(),stockInformation.getTodayOpeningPrice(), stockInformation.getYesterdayClosingPrice(),stockInformation.getCurrentPrice(),
	                        stockInformation.getDate(), stockInformation.getTime(),stockInformationId);  
	    }  */
	    	  
	    public StockInformation getStockInformation(String fullId) throws IOException, ClassNotFoundException, SQLException {  
	        return super.executeQuery("select * from stock_info where stockFullId=?",  
	                StockInformation.class,fullId).get(0);  
	    }  
	    
	    
		//获取所有fullId
		public List<String> getAllFullId() throws IOException, ClassNotFoundException, SQLException
		{
			String selectSql = "select stockFullId from stock_info"; //distinct去重
		//	System.out.println(selectSql);
			return super.getQuery(selectSql, null);
		}
		
	    public List<StockInformation> getStockDaoList() throws IOException, ClassNotFoundException, SQLException {  
	        return super.executeQuery("select * from stock_info", StockInformation.class);  
	    }  
	      
	    public long getStockDaoCount() throws IOException, ClassNotFoundException, SQLException{  
	        return super.getCount("select count(*) from stock_info");  
	    }
	    
	    
	    public List<String> getAllIndustry(List<StockInformation> listStockInfo) throws IOException, ClassNotFoundException, SQLException 
		{
			
			List<String> listIndustry = new ArrayList<String>(); 	
				
			 for(Iterator it = listStockInfo.iterator();it.hasNext();)
			 {
				StockInformation si = (StockInformation) it.next();			
				String industryName=si.getStockIndustry();
				if(industryName==null || industryName.length()<1)
					continue;
			//	System.out.println(industryName);
				//去重
				if(Collections.frequency(listIndustry, industryName) < 1) 
					 listIndustry.add(industryName);
			 }
			 return listIndustry;
		}
		
		public List<String> getAllConcept(List<StockInformation> listStockInfo) throws IOException, ClassNotFoundException, SQLException 
		{
			List<String> listConcept = new ArrayList<String>(); 	
				
			 for(Iterator it = listStockInfo.iterator();it.hasNext();)
			 {
				StockInformation si = (StockInformation) it.next();			
				String conceptName=si.getStockConcept();
				if(conceptName==null || conceptName.length()<1)
					continue;
				//去重
				if(Collections.frequency(listConcept, conceptName) < 1) 
					listConcept.add(conceptName);
			 }
			 return listConcept;
		}
		
		public List<String> getAllRegional(List<StockInformation> listStockInfo) throws IOException, ClassNotFoundException, SQLException 
		{
			List<String> listRegional = new ArrayList<String>(); 	
				
			 for(Iterator it = listStockInfo.iterator();it.hasNext();)
			 {
				StockInformation si = (StockInformation) it.next();			
				String regionalName=si.getStockRegional();
				if(regionalName==null || regionalName.length()<1)
					continue;
				
				//去重
				if(Collections.frequency(listRegional, regionalName) < 1) 
				{	
					listRegional.add(regionalName);
				//	System.out.println(regionalName);
				}
				
			 }
			 return listRegional;
		}
	    
	    public List<StockInformation> getStockIndustryList(String infoIndustory) throws IOException, ClassNotFoundException, SQLException{  
	    	String selectIndustrySql = "select * from stock_info where stockIndustry like '%"+infoIndustory+"%'";
	    	//String selectIndustrySql = "select * from stock_info where stockIndustry = '"+infoIndustory+"'";
	    	//select * from stock_info where stockIndustry like '%银行%';
	        return super.executeQuery(selectIndustrySql,StockInformation.class);  
	    }
	    
	    public List<StockInformation> getStockConceptList(String infoConcept) throws IOException, ClassNotFoundException, SQLException{  
	    	String selectConceptSql = "select * from stock_info where stockConcept like '%"+infoConcept+"%'";
	        return super.executeQuery(selectConceptSql,StockInformation.class);  
	    }
	    
	    public List<StockInformation> getStockRegionalList(String infoRegional) throws IOException, ClassNotFoundException, SQLException{  
	    	String selectRegionalSql = "select * from stock_info where stockRegional like '%"+infoRegional+"%'";
	        return super.executeQuery(selectRegionalSql,StockInformation.class);  
	    }
	    public List<String> getStockIndustry(String induInfo) throws IOException, ClassNotFoundException, SQLException 
		{
			//String induInfo="银行";
			List<StockInformation> listStockInfoIndustry = new ArrayList<StockInformation>();
			List<String> listStockIndustryFullId = new ArrayList<String>(); 
			
			listStockInfoIndustry=getStockIndustryList(induInfo);
			 for(Iterator it = listStockInfoIndustry.iterator();it.hasNext();)
			 {
				StockInformation si = (StockInformation) it.next();
			//	System.out.println(si.getStockName()+si.getStockFullId());
				//listStockIndustryFullId.add(si.getStockName());
				listStockIndustryFullId.add(si.getStockFullId());
			 }
			 return listStockIndustryFullId;
		}
		
		public List<String> getStockConcept(String conInfo) throws IOException, ClassNotFoundException, SQLException 
		{
			
		//	String conInfo="云计算";
			List<StockInformation> listStockInfoConcept = new ArrayList<StockInformation>();
			List<String> listStockConceptFullId = new ArrayList<String>(); 
			
			listStockInfoConcept=getStockConceptList(conInfo);
			 for(Iterator it = listStockInfoConcept.iterator();it.hasNext();)
			 {
				StockInformation si = (StockInformation) it.next();
			//	System.out.println(si.getStockName()+si.getStockFullId());
				//listStockConceptFullId.add(si.getStockName());
				listStockConceptFullId.add(si.getStockFullId());
			 }
			 return listStockConceptFullId;
		}
		
		public List<String> getStockRegional(String regInfo) throws IOException, ClassNotFoundException, SQLException 
		{
			
		//	String regInfo="北京";
			List<StockInformation> listStockInfoRegional = new ArrayList<StockInformation>(); 	
			List<String> listStockRegionalFullId = new ArrayList<String>(); 
			
			listStockInfoRegional=getStockRegionalList(regInfo);
			 for(Iterator it = listStockInfoRegional.iterator();it.hasNext();)
			 {
				StockInformation si = (StockInformation) it.next();
			//	System.out.println(si.getStockName()+si.getStockFullId());
				listStockRegionalFullId.add(si.getStockFullId());
			//	listStockRegionalFullId.add(si.getStockName());
			 }
			 return listStockRegionalFullId;
		}
		
	    /*
	    public List<StockInformation> getStockFullIdList() throws IOException, ClassNotFoundException, SQLException {  
	        return super.executeQuery("select stockFullId from stock", StockInformation.class);  
	    } */
		
		public int  updateFromStockName(String stockName,String updateStr,int updateType) throws IOException, ClassNotFoundException, SQLException
		{
			String updateSql=null;
			switch (updateType)
			{
			case ConstantsInfo.StockIndustry:
				updateSql = "update "+stockTable+" set stockIndustry='"+updateStr+"' where stockName='"+stockName+"'";
				break;
			case ConstantsInfo.StockConcept:
				updateSql = "update "+stockTable+" set stockConcept='"+updateStr+"' where stockName='"+stockName+"'";
				break;
			case ConstantsInfo.StockRegional:
				updateSql = "update "+stockTable+" set stockRegional='"+updateStr+"' where stockName='"+stockName+"'";
				break;
			default:
				break;
			}
			if(updateSql!=null)
			{			
				return super.saveOrUpdate(updateSql);
			}
			else
				return -1;
		}
		
		public int  updateFromStockPlate(String oldStr,String newStr,int updateType) throws IOException, ClassNotFoundException, SQLException
		{
						
			String updateSql=null;
			switch (updateType)
			{
			case ConstantsInfo.StockIndustry:
				updateSql = "update "+stockTable+" set stockIndustry='"+newStr+"' where stockIndustry='"+oldStr+"'";
				break;
			case ConstantsInfo.StockConcept:
				updateSql = "update "+stockTable+" set stockConcept='"+newStr+"' where stockConcept='"+oldStr+"'";
				break;
			case ConstantsInfo.StockRegional:
				updateSql = "update "+stockTable+" set stockRegional='"+newStr+"' where stockRegional='"+oldStr+"'";
				break;
			default:
				break;
			}
			if(updateSql!=null)
			{		
				return super.saveOrUpdate(updateSql);
			}
			else
				return -1;
		}
		
		
		public int  deleteFromStockPlate(String str,int updateType) throws IOException, ClassNotFoundException, SQLException
		{
						
			String updateSql=null;
			switch (updateType)
			{
			case ConstantsInfo.StockIndustry:
				updateSql = "update "+stockTable+" set stockIndustry='未知' where stockIndustry='"+str+"'";
				break;
			case ConstantsInfo.StockConcept:
				updateSql = "update "+stockTable+" set stockConcept='未知' where stockConcept='"+str+"'";
				break;
			case ConstantsInfo.StockRegional:
				updateSql = "update "+stockTable+" set stockRegional='未知' where stockRegional='"+str+"'";
				break;
			default:
				break;
			}
			
			if(updateSql!=null)
			{			
				return super.saveOrUpdate(updateSql);
			}
			else
				return -1;
		}
		
		

}
