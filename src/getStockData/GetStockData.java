package getStockData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import common.ConstantsInfo;

import dao.DayStock;
import dao.DayStockDao;
import dao.StockData;
import dao.StockDataDao;
import dao.StockInformation;
import dao.StockInformationDao;



public class GetStockData {
	static StockDataDao sdDao =new StockDataDao();
	static StockInformationDao sd =new StockInformationDao();
	static DayStockDao dsDao =new DayStockDao();
	private  final static int stockRow=20;

	public  String getHTML(String tempurl,String code) throws IOException{
		  URL url = null;
		  BufferedReader breader = null ;
		  InputStream is = null ;
		  InputStreamReader isReader=null;
		  StringBuffer resultBuffer = new StringBuffer();
		  try {
			   url = new URL(tempurl);
			   boolean isRetry = true;
	           int timesOfRetry = 0;
			   while(isRetry)
			   {
				   try{
					   HttpURLConnection conn = (HttpURLConnection)url.openConnection();
					   conn.setConnectTimeout(6000);
					   conn.setReadTimeout(6000);
					   is = conn.getInputStream();
					   if(is!=null)
					   {	
						  isReader=new InputStreamReader(is, code);
						  if(isReader!=null)
						  {
							   breader = new BufferedReader(isReader);
							   if(breader!=null)
							   {
								   String line = "";
								   while((line = breader.readLine()) != null){
									   resultBuffer.append(line);
								   }
							   }
						  }
					   }
				   }
				   catch(IOException e)
				   {
	                    if (timesOfRetry == 5) 
	                    isRetry = false;
	                    ++timesOfRetry;
	                    continue;
	               }
	                 
	                isRetry = false;
			   }
			   
		  } catch (MalformedURLException e) {
		   e.printStackTrace();
		  } finally{
			   try{
				   if(breader != null) breader.close();
			   }catch(Exception e){
				   e.printStackTrace();
			   }
			   try{
				   if(isReader != null) isReader.close();
			   }catch(Exception e){
				   e.printStackTrace();
			   }
			   try{
				   if(is != null) is.close();
			   }catch(Exception e){
				   e.printStackTrace();
			   }
			
		  }
		  return resultBuffer.toString();
	}
	
	 /*
    ��ȡ��1
    0����������·������Ʊ���֣�
    1����27.55�壬���տ��̼ۣ�
    2����27.25�壬�������̼ۣ�
    3����26.91�壬��ǰ�۸�
    4����27.55�壬������߼ۣ�
    5����26.20�壬������ͼۣ�
    6����26.91�壬����ۣ�������һ�����ۣ�
    7����26.92�壬�����ۣ�������һ�����ۣ�
    8����22114263�壬�ɽ��Ĺ�Ʊ�������ڹ�Ʊ������һ�ٹ�Ϊ������λ��������ʹ��ʱ��ͨ���Ѹ�ֵ����һ�٣�
    9����589824680�壬�ɽ�����λΪ��Ԫ����Ϊ��һĿ��Ȼ��ͨ���ԡ���Ԫ��Ϊ�ɽ����ĵ�λ������ͨ���Ѹ�ֵ����һ��*/
    //��ȡ���̼ۣ�1�� ���̼ۣ�3�� ��߼ۣ�4�� ��ͼۣ�5�� �ɽ�����8�� �ɽ��9��	
	public StockData getStockData(String []stockDayData,String FullId)
	{

		Date time;
		float openingPrice;
		float closingPrice;
		float highestPrice;
		float lowestPrice;
		long vol;
		double turnOver;
		
		time =Date.valueOf(stockDayData[30]);
		openingPrice=Float.parseFloat(stockDayData[1]);
		closingPrice=Float.parseFloat(stockDayData[3]);
		highestPrice=Float.parseFloat(stockDayData[4]);
		lowestPrice=Float.parseFloat(stockDayData[5]);
		vol=Long.parseLong(stockDayData[8]);
		turnOver=Double.parseDouble(stockDayData[9]);
		/*
		System.out.println("date:"+time);		
		System.out.println("open:"+openingPrice);	
		System.out.println("highest:"+highestPrice);
		System.out.println("lowest:"+lowestPrice);
		System.out.println("close:"+closingPrice);
		System.out.println("vol:"+vol);
		System.out.println("turnOver:"+turnOver);		
		*/
		StockData sdDayData=new StockData(time,openingPrice,highestPrice,lowestPrice,closingPrice,vol,turnOver,0,0,ConstantsInfo.DayDataType,0,0);
		return sdDayData;
	}
	
	
	public void InsertUpdateStockDayData(String FullId,StockData sdDayData,int year,int month,int week,String strDate) throws IOException, ClassNotFoundException, SQLException
	{
		
		float openingPrice;
		float closingPrice;
		float highestPrice;
		float lowestPrice;
		long vol;
		double turnOver;
		float ma5Price;
		float ma10Price;
		
		openingPrice=sdDayData.getOpeningPrice();
		closingPrice=sdDayData.getClosingPrice();
		highestPrice=sdDayData.getHighestPrice();
		lowestPrice=sdDayData.getLowestPrice();
		vol=sdDayData.getStockVolume();
		turnOver=sdDayData.getDailyTurnover();		
		/*	
		System.out.println(openingPrice);		
		System.out.println(closingPrice);		
		System.out.println(highestPrice);		
		System.out.println(lowestPrice);
		System.out.println(vol);
		System.out.println(turnOver);
		*/
		if(openingPrice==0 || highestPrice==0 || lowestPrice==0 || closingPrice==0)//ͣ�� ������
			return;
		int dayId=sdDao.getIdAndExistDataTimeType(FullId,year,month,week,strDate,ConstantsInfo.DayDataType);
		if(dayId==0)
			sdDao.insertStockDataTimeType(sdDayData,FullId);
		else
			return;
		
		ma5Price=sdDao.getMaValueFromDate(FullId,strDate,ConstantsInfo.DayDataType,5);
		ma10Price=sdDao.getMaValueFromDate(FullId,strDate,ConstantsInfo.DayDataType,10);	
		//System.out.println("ma5PriceForWeek:"+ma5Price);
		//System.out.println("ma10PriceForWeek:"+ma10Price);
		sdDao.insertMAtoDayStock(FullId,ConstantsInfo.DayDataType,ma5Price,ma10Price,strDate);
		
	}
	
	public void InsertUpdateStockWeekData(String FullId,StockData sdDayData,int year,int month,int week,String strDate) throws IOException, ClassNotFoundException, SQLException
	{
		//ÿ���һ����Ҫ���⴦��
		if(week!=0)
			InsertUpdateStockWeekDataAll(FullId,sdDayData,year,month,week,strDate);
		else
			InsertUpdateStockFirstWeekData(FullId,sdDayData,year,month,week,strDate);
			
	}
	
	public void InsertUpdateStockFirstWeekData(String FullId,StockData sdDayData,int year,int month,int week,String strDate) throws IOException, ClassNotFoundException, SQLException
	{
		
		float closingPriceForWeek=0,openingPriceForWeek = 0;
		float highestPriceForWeek=0,lowestPriceForWeek=0;
		float ma5PriceForWeek=0,ma10PriceForWeek=0;
		String dateForweek = null;
		Date dateSqlForweek = null;
		StockData sdata;
		List<StockData> listDayStockOfFirstWeek = new ArrayList<StockData>(); 
		List<StockData> listDayStockOfLastWeek = new ArrayList<StockData>(); 
		
		String strYear=String.valueOf(year);
		int priYearWeekNum=sdDao.getStockMonthWeekDaysOfYear(FullId,strYear,ConstantsInfo.WeekDataType);
		System.out.println("year:"+strYear+"-week:"+priYearWeekNum);
		
		listDayStockOfFirstWeek=sdDao.getAllDataOfWeek(FullId,0,year);//��ǰ���һ��
		listDayStockOfLastWeek=sdDao.getAllDataOfWeek(FullId,priYearWeekNum,year-1);//��һ�����һ��
		
		if(listDayStockOfFirstWeek.size()>0 && listDayStockOfLastWeek.size()>0)
		{
			//System.out.println("one");
			dateSqlForweek=sdDayData.getDate();
			//������һ��id
			int weekId=sdDao.getIdAndExistDataTimeType(FullId,year,month,priYearWeekNum,strDate,ConstantsInfo.WeekDataType);
						
			openingPriceForWeek=sdDao.getOpeningPriceFromFirstAndLastWeek(listDayStockOfFirstWeek,listDayStockOfLastWeek);
			highestPriceForWeek=sdDao.getHighestPriceFromFirstAndLastWeek(listDayStockOfFirstWeek,listDayStockOfLastWeek);
			lowestPriceForWeek=sdDao.getLowestPriceFromFirstAndLastWeek(listDayStockOfFirstWeek,listDayStockOfLastWeek);
			closingPriceForWeek=sdDao.getClosingPriceFromFirstAndLastWeek(listDayStockOfFirstWeek,listDayStockOfLastWeek);
			if(openingPriceForWeek==0 || highestPriceForWeek==0 || lowestPriceForWeek==0 || closingPriceForWeek==0)
				return;
			sdata=new StockData(dateSqlForweek,openingPriceForWeek,highestPriceForWeek,lowestPriceForWeek,closingPriceForWeek,0,0,0,0,ConstantsInfo.WeekDataType,0,0);
			sdDao.updateStockDataTimeType(sdata,FullId,weekId,ConstantsInfo.WeekDataType);
			
			ma5PriceForWeek=sdDao.getMaValueFromDate(FullId,dateForweek,ConstantsInfo.WeekDataType,5);
			ma10PriceForWeek=sdDao.getMaValueFromDate(FullId,dateForweek,ConstantsInfo.WeekDataType,10);	
			if(ConstantsInfo.DEBUG)
			{
				System.out.println("ma5PriceForWeek:"+ma5PriceForWeek);
				System.out.println("ma10PriceForWeek:"+ma10PriceForWeek);
			}
			
			sdDao.insertMAtoDayStock(FullId,ConstantsInfo.WeekDataType,ma5PriceForWeek,ma10PriceForWeek,dateForweek);
		}		
		else if(listDayStockOfFirstWeek.size()>0 && listDayStockOfLastWeek.size()==0)
		{
			//System.out.println("three");			
			dateSqlForweek=sdDayData.getDate();
			InsertUpdateStockWeekDataAll(FullId,sdDayData,year,month,week,strDate);
			return;				
		}
		else
		{
			return;		
		}		
	
	}
	
	public void InsertUpdateStockWeekDataAll(String FullId,StockData sdDayData,int year,int month,int week,String strDate) throws IOException, ClassNotFoundException, SQLException
	{
		
		Date date;
		float openingPrice;
		float closingPrice;
		float highestPrice;
		float lowestPrice;
		long vol=0;
		double turnOver=0;
		float ma5Price;
		float ma10Price;
		
		int weekId=sdDao.getIdAndExistDataTimeType(FullId,year,month,week,strDate,ConstantsInfo.WeekDataType);
		
  		System.out.println("weekId:"+weekId);  		
  		  			
		if(weekId==0)//����
		{
			System.out.println("insert week");
			date=sdDayData.getDate();
			openingPrice=sdDayData.getOpeningPrice();			
			highestPrice=sdDayData.getHighestPrice();
			lowestPrice=sdDayData.getLowestPrice();
			closingPrice=sdDayData.getClosingPrice();
			vol=0;
			turnOver=0;			
			if(openingPrice==0 || highestPrice==0 || lowestPrice==0 || closingPrice==0)//ͣ�� ������
			{
				return;
			}
			StockData sdWeekData=new StockData(date,openingPrice,highestPrice,lowestPrice,closingPrice,vol,turnOver,0,0,ConstantsInfo.WeekDataType,0,0);	
			sdDao.insertStockDataTimeType(sdWeekData,FullId);
		}
		else
		{
			System.out.println("update week");
			date=sdDayData.getDate();//����ʱ��
			openingPrice=sdDao.getOpeningPriceFromDate(FullId, week, year, ConstantsInfo.WeekDataType);
			highestPrice=sdDao.getHighestPriceFromDate(FullId, week, year, ConstantsInfo.WeekDataType);
			lowestPrice=sdDao.getLowestPriceFromDate(FullId, week, year, ConstantsInfo.WeekDataType);
			closingPrice=sdDao.getClosingPriceFromDate(FullId, week, year, ConstantsInfo.WeekDataType);
			vol=0;
			turnOver=0;
			if(openingPrice==0 || highestPrice==0 || lowestPrice==0 || closingPrice==0)//ͣ�� ������
				return;
			StockData sdWeekData=new StockData(date,openingPrice,highestPrice,lowestPrice,closingPrice,vol,turnOver,0,0,ConstantsInfo.WeekDataType,0,0);				
			sdDao.updateStockDataTimeType(sdWeekData,FullId,weekId,ConstantsInfo.WeekDataType);
			
		}
		ma5Price=sdDao.getMaValueFromDate(FullId,strDate,ConstantsInfo.WeekDataType,5);
		ma10Price=sdDao.getMaValueFromDate(FullId,strDate,ConstantsInfo.WeekDataType,10);	
		if(ConstantsInfo.DEBUG)
		{
			System.out.println("ma5PriceForWeek:"+ma5Price);
			System.out.println("ma10PriceForWeek:"+ma10Price);
		}
		sdDao.insertMAtoDayStock(FullId,ConstantsInfo.WeekDataType,ma5Price,ma10Price,strDate);
				
	}
	
	public void InsertUpdateStockMonthData(String FullId,StockData sdDayData,int year,int month,int week,String strDate) throws IOException, ClassNotFoundException, SQLException
	{
		
		Date date;
		float openingPrice;
		float closingPrice;
		float highestPrice;
		float lowestPrice;
		long vol=0;
		double turnOver=0;
		float ma5Price;
		float ma10Price;
		
		int monthId=sdDao.getIdAndExistDataTimeType(FullId,year,month,week,strDate,ConstantsInfo.MonthDataType);
		
  		System.out.println("monthId:"+monthId);
		StockData sdMonthData;
		if(monthId==0)//����
		{
			System.out.println("month insert");
			date=sdDayData.getDate();
			openingPrice=sdDayData.getOpeningPrice();			
			highestPrice=sdDayData.getHighestPrice();
			lowestPrice=sdDayData.getLowestPrice();
			closingPrice=sdDayData.getClosingPrice();
			vol=0;
			turnOver=0;			
			if(openingPrice==0 || highestPrice==0 || lowestPrice==0 || closingPrice==0)//ͣ�� ������
				return;
			
			sdMonthData=new StockData(date,openingPrice,highestPrice,lowestPrice,closingPrice,vol,turnOver,0,0,ConstantsInfo.MonthDataType,0,0);	
			sdDao.insertStockDataTimeType(sdMonthData,FullId);
		}
		else
		{
			System.out.println("month update");
			date=sdDayData.getDate();
			openingPrice=sdDao.getOpeningPriceFromDate(FullId, month, year, ConstantsInfo.MonthDataType);
			highestPrice=sdDao.getHighestPriceFromDate(FullId, month, year, ConstantsInfo.MonthDataType);
			lowestPrice=sdDao.getLowestPriceFromDate(FullId, month, year, ConstantsInfo.MonthDataType);
			closingPrice=sdDao.getClosingPriceFromDate(FullId, month, year, ConstantsInfo.MonthDataType);
			vol=0;
			turnOver=0;
			if(openingPrice==0 || highestPrice==0 || lowestPrice==0 || closingPrice==0)//ͣ�� ������
				return;
			
			sdMonthData=new StockData(date,openingPrice,highestPrice,lowestPrice,closingPrice,vol,turnOver,0,0,ConstantsInfo.MonthDataType,0,0);				
			sdDao.updateStockDataTimeType(sdMonthData,FullId,monthId,ConstantsInfo.MonthDataType);
		}
		
		ma5Price=sdDao.getMaValueFromDate(FullId,strDate,ConstantsInfo.MonthDataType,5);
		ma10Price=sdDao.getMaValueFromDate(FullId,strDate,ConstantsInfo.MonthDataType,10);
		if(ConstantsInfo.DEBUG)
		{
			System.out.println("ma5PriceForMonth:"+ma5Price);
			System.out.println("ma10PriceForMonth:"+ma10Price);
		}
		sdDao.insertMAtoDayStock(FullId,ConstantsInfo.MonthDataType,ma5Price,ma10Price,strDate);
				
	}
	
	public void InsertUpdateStockSeasonData(String FullId,StockData sdDayData,int year,int month,int week,String strDate) throws IOException, ClassNotFoundException, SQLException
	{
		
		Date date;
		float openingPrice;
		float closingPrice;
		float highestPrice;
		float lowestPrice;
		long vol=0;
		double turnOver=0;
		float ma5Price;
		float ma10Price;
		
		int seasonId=sdDao.getIdAndExistDataTimeType(FullId,year,month,week,strDate,ConstantsInfo.SeasonDataType);
		int seasonNum;
  		System.out.println("seasonId:"+seasonId);
		StockData sdSeasonData;
		if(seasonId==0)//����
		{
			date=sdDayData.getDate();
			openingPrice=sdDayData.getOpeningPrice();			
			highestPrice=sdDayData.getHighestPrice();
			lowestPrice=sdDayData.getLowestPrice();
			closingPrice=sdDayData.getClosingPrice();
			vol=0;
			turnOver=0;			
			if(openingPrice==0 || highestPrice==0 || lowestPrice==0 || closingPrice==0)//ͣ�� ������
				return;
			sdSeasonData=new StockData(date,openingPrice,highestPrice,lowestPrice,closingPrice,vol,turnOver,0,0,ConstantsInfo.SeasonDataType,0,0);	
			sdDao.insertStockDataTimeType(sdSeasonData,FullId);
		}
		else
		{
			date=sdDayData.getDate();
			seasonNum=sdDao.getSeasonNumFromMonth(month);
			
			openingPrice=sdDao.getOpeningPriceFromDate(FullId, seasonNum, year, ConstantsInfo.SeasonDataType);
			highestPrice=sdDao.getHighestPriceFromDate(FullId, seasonNum, year, ConstantsInfo.SeasonDataType);
			lowestPrice=sdDao.getLowestPriceFromDate(FullId, seasonNum, year, ConstantsInfo.SeasonDataType);
			closingPrice=sdDao.getClosingPriceFromDate(FullId, seasonNum, year, ConstantsInfo.SeasonDataType);
			vol=0;
			turnOver=0;
			if(openingPrice==0 || highestPrice==0 || lowestPrice==0 || closingPrice==0)//ͣ�� ������
				return;
			sdSeasonData=new StockData(date,openingPrice,highestPrice,lowestPrice,closingPrice,vol,turnOver,0,0,ConstantsInfo.SeasonDataType,0,0);				
			sdDao.updateStockDataTimeType(sdSeasonData,FullId,seasonId,ConstantsInfo.SeasonDataType);
		}
		ma5Price=sdDao.getMaValueFromDate(FullId,strDate,ConstantsInfo.SeasonDataType,5);
		ma10Price=sdDao.getMaValueFromDate(FullId,strDate,ConstantsInfo.SeasonDataType,10);	
		if(ConstantsInfo.DEBUG)
		{
			System.out.println("ma5PriceForSeason:"+ma5Price);
			System.out.println("ma10PriceForSeason:"+ma10Price);
		}
		sdDao.insertMAtoDayStock(FullId,ConstantsInfo.SeasonDataType,ma5Price,ma10Price,strDate);
				
	}
	
	public void InsertUpdateStockYearData(String FullId,StockData sdDayData,int year,int month,int week,String strDate) throws IOException, ClassNotFoundException, SQLException
	{
		
		Date date;
		float openingPrice;
		float closingPrice;
		float highestPrice;
		float lowestPrice;
		long vol=0;
		double turnOver=0;
		float ma5Price;
		float ma10Price;
		
		int yearId=sdDao.getIdAndExistDataTimeType(FullId,year,month,week,strDate,ConstantsInfo.YearDataType);
		System.out.println("yearId:"+yearId);
		StockData sdYearData;
		if(yearId==0)//����
		{
			date=sdDayData.getDate();
			openingPrice=sdDayData.getOpeningPrice();			
			highestPrice=sdDayData.getHighestPrice();
			lowestPrice=sdDayData.getLowestPrice();
			closingPrice=sdDayData.getClosingPrice();
			vol=0;
			turnOver=0;			
			if(openingPrice==0 || highestPrice==0 || lowestPrice==0 || closingPrice==0)//ͣ�� ������
				return;
			sdYearData=new StockData(date,openingPrice,highestPrice,lowestPrice,closingPrice,vol,turnOver,0,0,ConstantsInfo.YearDataType,0,0);	
			sdDao.insertStockDataTimeType(sdYearData,FullId);
		}
		else
		{
			date=sdDayData.getDate();
			openingPrice=sdDao.getOpeningPriceFromDate(FullId, year, year, ConstantsInfo.YearDataType);
			highestPrice=sdDao.getHighestPriceFromDate(FullId, year, year, ConstantsInfo.YearDataType);
			lowestPrice=sdDao.getLowestPriceFromDate(FullId, year, year, ConstantsInfo.YearDataType);
			closingPrice=sdDao.getClosingPriceFromDate(FullId, year, year, ConstantsInfo.YearDataType);
			vol=0;
			turnOver=0;
			if(openingPrice==0 || highestPrice==0 || lowestPrice==0 || closingPrice==0)//ͣ�� ������
				return;
			sdYearData=new StockData(date,openingPrice,highestPrice,lowestPrice,closingPrice,vol,turnOver,0,0,ConstantsInfo.YearDataType,0,0);				
			sdDao.updateStockDataTimeType(sdYearData,FullId,yearId,ConstantsInfo.YearDataType);
		}
		ma5Price=sdDao.getMaValueFromDate(FullId,strDate,ConstantsInfo.YearDataType,5);
		ma10Price=sdDao.getMaValueFromDate(FullId,strDate,ConstantsInfo.YearDataType,10);	
		if(ConstantsInfo.DEBUG)
		{
			System.out.println("ma5PriceForYear:"+ma5Price);
			System.out.println("ma5PriceForYear:"+ma10Price);
		}
		sdDao.insertMAtoDayStock(FullId,ConstantsInfo.YearDataType,ma5Price,ma10Price,strDate);
				
	}	
	
	public void getDayStockInfo(String sinaStock,List<String> listFullId) throws IOException, ClassNotFoundException, SQLException
	{
		 String webResult=null;		
		 String[] stockArray;
		 String singleStock=null;
		 String[] stockInfo=null;
		 String fullId=null;
		 float ma5=0.0f;
		 float ma10=0.0f;
		 int i;
		 int start,end;
		 int maxId=0;		
		 
		 for(i=0;i<5;i++) //����5��
		 {
			 try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			 webResult = getHTML(sinaStock, "GBK");
			 if(webResult==null || webResult.equals(""))
			 {
				 System.out.println("try again"+i);
				 continue;
			 }
			 else
			 {
				 break;
			 }
			 
			
		 }
		 
		 if(webResult==null || webResult.equals(""))
			 return;
		 
		 stockArray=webResult.split(";"); //��
		
	     for (i = 0; i < stockArray.length; i++) 
	     {   
		 //	System.out.println(stockArray[i]); 
		  	start=stockArray[i].indexOf("\"");
		  	end=stockArray[i].lastIndexOf("\"");
		  	
		  	singleStock =stockArray[i].substring(start+1, end);
		 //	System.out.println("start:"+start+" end:"+end);
			if(singleStock.length()>1)
			{	
				System.out.println("singleStock:"+singleStock);
			//	System.out.println("StockFullId:"+listFullId.get(i));
		  		stockInfo=singleStock.split(","); 
		  		fullId=listFullId.get(i);	
		  		int isTableExist=sdDao.isExistStockTable(fullId,ConstantsInfo.TABLE_DATA_STOCK);
   				if(isTableExist==0)//������
   					continue;   
		  		StockData sdDayData=getStockData(stockInfo,fullId);
		  		String date_time=stockInfo[30];
		  		
		  		int week=sdDao.getTimeTypeOfDate(date_time,ConstantsInfo.WeekDataType);
		  		int month=sdDao.getTimeTypeOfDate(date_time,ConstantsInfo.MonthDataType);
		  		int year=sdDao.getTimeTypeOfDate(date_time,ConstantsInfo.YearDataType);
		  	
		  		InsertUpdateStockDayData(fullId,sdDayData,year,month,week,date_time);
				InsertUpdateStockWeekData(fullId,sdDayData,year,month,week,date_time);
		 		InsertUpdateStockMonthData(fullId,sdDayData,year,month,week,date_time);
		 		InsertUpdateStockSeasonData(fullId,sdDayData,year,month,week,date_time);
		 	//	InsertUpdateStockYearData(fullId,sdDayData,year,month,week,date_time);	    		
		  
			}  
			
	     }
	       
	     
	}
	
	
	public void getStockInfoFromSina() throws IOException, ClassNotFoundException, SQLException
	{
		
		long stockCount=0;
		int st=0;
		int count=0;
		String sinaStockStr="http://hq.sinajs.cn/list=";
		stockCount=sd.getStockDaoCount();
		System.out.println(stockCount);
		
		 List<StockInformation> listStockInfo = new ArrayList<StockInformation>(); 
		 List<String> listStockFullId = new ArrayList<String>(); 
	
		
		 listStockInfo=sd.getStockDaoList();
		 Iterator it; 
	
		
		 for(it = listStockInfo.iterator();it.hasNext();)
		 {
			StockInformation si = (StockInformation) it.next();
		//	dsDao.alterMaColumnToDayStock(si.getStockFullId());
			sinaStockStr=sinaStockStr+si.getStockFullId()+",";	
			listStockFullId.add(si.getStockFullId());
			
			st++;
			//ÿ20����Ʊȡһ������
			if(st==stockRow)
			{
				st=0;
				//System.out.println(sinaStockStr);				
				count++;			
				getDayStockInfo(sinaStockStr,listStockFullId);
				listStockFullId.clear();
				sinaStockStr="http://hq.sinajs.cn/list=";				
			}
		 }
		//System.out.println(count);
		 //���ʣ�಻��stockRow 
		System.out.println(sinaStockStr);
		getDayStockInfo(sinaStockStr,listStockFullId);

		
		 //����ĳ�������
	/*
		 sinaStockStr=sinaStockStr+"sh000001";//sh000001
		// listStockFullId.add("sz399001");
		// listStockFullId.add("sz399005");
		 listStockFullId.add("sh000001");//sh000001
		getDayStockInfo(sinaStockStr,listStockFullId);
	*/	
			
	}
	
	public void delSameData() throws IOException, ClassNotFoundException, SQLException
	{
		
		List<String> listStockFullId = new ArrayList<String>(); 
		
		listStockFullId=sd.getAllFullId();
		Iterator it; 
		 
		 for(it = listStockFullId.iterator();it.hasNext();)
		 {
			 String si = (String) it.next();
		//	dsDao.alterMaColumnToDayStock(si.getStockFullId());
			// System.out.println(si);
			// if(si.equals("sz000014"))
			dsDao.delStockSameData(si);
			
		 }
		
	}


	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException, InterruptedException {
		
		Date startDate = new Date(0);
		GetStockData gs=new GetStockData();
		gs.getStockInfoFromSina();
	//	gs.delSameData();
		
		Date endDate = new Date(0);
		// ��������ʱ�����������
		long seconds =(endDate.getTime() - startDate.getTime())/1000;
		System.out.println("�ܹ���ʱ��"+seconds+"��");
		System.out.println("end");
	}

}
