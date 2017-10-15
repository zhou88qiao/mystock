package getStockData;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

import common.ConstantsInfo;

import dao.DayStock;
import dao.DayStockDao;
import dao.StockInformationDao;
import dao.StockPoint;

public class GetMA {

	static StockInformationDao sd =new StockInformationDao();
	static DayStockDao dsDao =new DayStockDao();
	
	public List<Integer> getTuringPoing(String fullId) throws SQLException
	{
		List<Integer> listTuringDay = new ArrayList<Integer>();
		int idIndex,idOfStart,maxId;
		float md5Pri = 0,md10Pri,md5Next,md10Next;
		
		int pointId=0;
		boolean priceWillFall,priceWillRise;
		TreeMap<Integer, String> pointTM = new TreeMap<Integer, String>(); 
		String pointDate;
		
		idOfStart=dsDao.getId(fullId, "2014-01-02");
		maxId=dsDao.getMaxId(fullId);
		
		for(idIndex=idOfStart;idIndex<=maxId;idIndex++)
		{
			md5Pri=dsDao.getStockMaData(fullId,idIndex-1,5);
			md10Pri=dsDao.getStockMaData(fullId,idIndex-1,10);
			md5Next=dsDao.getStockMaData(fullId,idIndex+1,5);
			md10Next=dsDao.getStockMaData(fullId,idIndex+1,10);
			
			//System.out.println("md5:"+md5+"md10:"+md10);
			if(md5Pri==0 || md10Pri==0 || md5Next==0 || md10Next==0)
			{
				//System.out.println("zero:");
				continue;
			}
			priceWillFall = md5Pri>=md10Pri && md5Next<=md10Next;
			priceWillRise = md5Pri<=md10Pri && md5Next>=md10Next;
		//	System.out.println("111priceWillFall:"+priceWillFall+"--priceWillRise:"+priceWillRise);
			//if(priceWillFall || priceWillFall )
			if((md5Pri>=md10Pri && md5Next<=md10Next) || (md5Pri<=md10Pri && md5Next>=md10Next))
			{
			
				if(pointId<=idIndex-1)
				{
					if(pointId==idIndex-1)
					{	
						//	pointId=idIndex;	
						continue;
					}
					pointDate=dsDao.getDate(fullId,idIndex);
					listTuringDay.add(idIndex);
					
				//	listPointDay.add(pointDate);
				//	System.out.println("priceWillFall:"+priceWillFall+"--priceWillRise:"+priceWillRise);
					if(priceWillFall)
						pointTM.put(idIndex, "fall"); 
					else if(priceWillRise)
						pointTM.put(idIndex, "rise");
						
					System.out.println("pointDate:"+pointDate);
				}
				
				pointId=idIndex;					
			}
		}
		
		return listTuringDay;
		
	}
	
	public List<StockPoint> getMaExtremeDayStock(String fullId) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException
	{
		
		int idIndex;
		String pointDate,pointStartDate = null,pointEndDate = null;
		float extremePrice = 0;
		String extremeDate = null;
		int flagRiseFall=0;
		StockPoint sp;
		List<StockPoint> listStockPoint = new ArrayList<StockPoint>();
		
		List<Integer> listPointDay = new ArrayList<Integer>();
	//	List<String> listPointDay = new ArrayList<String>();
		TreeMap<Integer, String> pointTM = new TreeMap<Integer, String>();  
		
		//idOfStart=dsDao.getId(fullId, "2013-10-08");
		int idOfStart=dsDao.getId(fullId, "2014-01-02");
		int maxId=dsDao.getMaxId(fullId);
		System.out.println(idOfStart);
		int pointId=idOfStart;
	//	pointId=idIndex;
	//	idOfStart 1332 maxId-1,1287 idOfStart
		boolean priceWillFall,priceWillRise;
		int startId=0,endId=0;
		int getStartflag=0;
		DayStock ds;
		float md5Pri = 0,md10Pri,md5Next,md10Next;
		float extremeBeforePrice=0;
		float extremeCurPrice=0;
		for(idIndex=idOfStart;idIndex<=maxId;idIndex++)
		{
			md5Pri=dsDao.getStockMaData(fullId,idIndex-1,5);
			md10Pri=dsDao.getStockMaData(fullId,idIndex-1,10);
			md5Next=dsDao.getStockMaData(fullId,idIndex+1,5);
			md10Next=dsDao.getStockMaData(fullId,idIndex+1,10);
			
			//System.out.println("md5:"+md5+"md10:"+md10);
			if(md5Pri==0 || md10Pri==0 || md5Next==0 || md10Next==0)
			{
				continue;
			}
			priceWillFall = md5Pri>=md10Pri && md5Next<=md10Next;
			priceWillRise = md5Pri<=md10Pri && md5Next>=md10Next;
			//if(priceWillFall || priceWillFall )
			if((md5Pri>=md10Pri && md5Next<=md10Next) || (md5Pri<=md10Pri && md5Next>=md10Next))
			{
			
				if(pointId<=idIndex-1)
				{
					if(pointId==idIndex-1)
					{	
					//	pointId=idIndex;	
						continue;
					}
					pointDate=dsDao.getDate(fullId,idIndex);
					listPointDay.add(idIndex);
					System.out.println("pointDate:"+pointDate);
					switch(getStartflag)
					{
						case 2:
						default:
							startId=endId;
							endId=idIndex;	
							pointStartDate=pointEndDate;
							pointEndDate=pointDate;
							break;
						case 1:
							endId=idIndex;//第二次
							getStartflag++;
							pointEndDate=pointDate;
							break;
						case 0:
							startId=idIndex;//第一次
							getStartflag++;	
							pointStartDate=pointDate;
							break;
					}						
				  
					if(getStartflag==2)
					{
						if(priceWillFall)
						{

							ds=dsDao.getMaxStockPoint(fullId,startId,endId);					    		
				    		extremePrice=ds.getHighestPrice();
				    		extremeDate=ds.getDate().toString();
				    		System.out.println("maxHighestPrice:"+extremePrice);
				    		System.out.println("maxHighestDate:"+extremeDate);
				    		flagRiseFall=1;
				    		
						}							
						else if(priceWillRise)
						{
							ds=dsDao.getMinStockPoint(fullId,startId,endId);				
				    		extremePrice=ds.getLowestPrice();
				    		extremeDate=ds.getDate().toString();
				    		System.out.println("minLowestPrice:"+extremePrice);
				    		System.out.println("minLowestDate:"+extremeDate);
				    		flagRiseFall=0;
						}								
						
						extremeBeforePrice=extremeCurPrice;
						extremeCurPrice=extremePrice;
						float ratio=(extremeCurPrice-extremeBeforePrice)/extremeCurPrice;
						sp=new StockPoint(0,ConstantsInfo.DayDataType,Date.valueOf(extremeDate),extremePrice,Date.valueOf(pointStartDate),Date.valueOf(pointEndDate),flagRiseFall,ratio,0);
						//sp=new StockPoint(extremeDate,extremePrice,pointStartDate,pointEndDate,flagRiseFall);
						listStockPoint.add(sp);
					}				
					
				}
				
				pointId=idIndex;					
			}
		}
		
	    return listStockPoint;
			
		/*	
			DayStock ds;
			int startId=listPointDay.get(0);
			int endId=listPointDay.get(1);
			float maxHighestPrice,minlowestPrice;
			String value;
			Set<Integer> keys = pointTM.keySet();  
			startId=pointTM.firstKey();
			int flag=1;
		    for(Integer key: keys)
		    {  
		    	startId=endId;
		    	value=pointTM.get(key);
		    	endId=key;
		    	if(flag==1)
		    	{
		    		flag=0;
		    		continue;		    		
		    	}
		    //	System.out.println("Value of "+key+" is: "+pointTM.get(key));  
		    	System.out.println("startDate:"+dsDao.getDate(fullId,startId)+"-endDate:"+dsDao.getDate(fullId,endId));
		    	if(value.equals("fall"))
		    	{	
		    	//	maxHighestPrice=dsDao.getStockMaxHighestPrice(fullId,startId,endId);	
		    	//	System.out.println("maxHighestPrice:"+maxHighestPrice);
		    		ds=dsDao.getMaxStockPoint(fullId,startId,endId);
		    		System.out.println("maxHighestPrice:"+ds.getHighestPrice());		    			    	
		    	}
		    	else
		    	{
		    	//	minlowestPrice=dsDao.getStockMinHighestPrice(fullId,startId,endId);
		    	//	System.out.println("minLowestPrice:"+minlowestPrice);
		    		ds=dsDao.getMinStockPoint(fullId,startId,endId);
		    		System.out.println("minLowestPrice:"+ds.getLowestPrice());
		    	}
		    	listPointDayStock.add(ds);		    		    	
		       
		     }  
		    */ 
	
		
			
			
	
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException {
				
		GetMA gm=new GetMA();
		Iterator it;
		List<String> listStockFullId = new ArrayList<String>();
		listStockFullId=sd.getAllFullId();
		List<StockPoint> listStockPoint = new ArrayList<StockPoint>();
		for(it = listStockFullId.iterator();it.hasNext();)
		{
			String fullId = (String) it.next();
			if(!fullId.equals("sh000001")) //sh000001
				continue;
			listStockPoint=gm.getMaExtremeDayStock(fullId);
		}
		
	
	}

}
