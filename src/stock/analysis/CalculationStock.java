package stock.analysis;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.PropertyConfigurator;

import common.ConstantsInfo;
import common.stockLogger;

import dao.DayStockDao;
import dao.DbConn;
import dao.StockBaseDao;
import dao.StockDataDao;
import dao.StockInformationDao;
import dao.StockPointDao;
import date.timer.stockDateTimer;
import stock.timer.TimeStock;

public class CalculationStock {
	
	private StockDataDao sdDao;
	private StockBaseDao sbDao;
	static StockInformationDao siDao =new StockInformationDao();
	static stockDateTimer sdTime=new stockDateTimer();
	public CalculationStock()
	{
		
	}
	public CalculationStock(Connection stockBaseConn,Connection stockDataConn,Connection stockPointConn)
	{
		sbDao = new StockBaseDao(stockBaseConn);
		sdDao =new StockDataDao(stockDataConn);
	}
	
	 public CalculationStock(StockBaseDao sbDao,StockDataDao sdDao)
	{
		this.sbDao = sbDao;
		this.sdDao = sdDao;
		///	this.spDao = spDao;
	}	
	 
	 //计算某个股票
	public void calculStockAllDataForSingleStock(String fullId,int type) throws IOException, ClassNotFoundException, SQLException
	{
		if(type == ConstantsInfo.StockCalAllData)//计算所有
		{
			List<TimeStock> listTimeStock = new ArrayList<TimeStock>();
			//将刚导入的数据新表dataType由0改为1
			sdDao.updateDayTypeForNewStock(fullId);
			
			listTimeStock=sdDao.getStockTimeStockOfYear(fullId);
			
			sdDao.calAllHistoryDayStockData(fullId, listTimeStock);//计算日ma5 ma10
			sdDao.calAllHistoryWeekStockData(fullId, listTimeStock);//计算周所有值				
			sdDao.calAllHistoryMonthStockData(fullId, listTimeStock);//计算月所有值
			
		//	sdDao.calAllHistorySeasonStockData(fullId, listTimeStock);//计算季度
		//	sdDao.calAllHistoryYearStockData(fullId, listTimeStock);//计算年
			listTimeStock.clear();
		}
		else
		{
			List<String> listDay = new ArrayList<String>();
			//跨年情况
			List<Integer> listWeekPri = new ArrayList<Integer>();//
			List<Integer> listWeekCur = new ArrayList<Integer>();
			List<Integer> listMonthPri = new ArrayList<Integer>();
			List<Integer> listMonthCur = new ArrayList<Integer>();
			List<Integer> listYear = new ArrayList<Integer>();
				
			//计算日 导入数据最早一天，计算出所有导入的日期
		//	listDay=sdDao.getUnCalculationDay(fullId,"2015-07-20");	
			
			listDay=sdDao.getUnCalculationDay(fullId);
			
			listYear=sdDao.getUnCalculationYear(fullId);
			
			if(listDay.size()< 1 || listYear.size()<1){
				stockLogger.logger.fatal(fullId+" have no data");
				System.out.println(fullId+" have no data");
				return;
			}
			
			listWeekPri=sdDao.getUnCalculationWeek(fullId, listYear.get(0));
			listMonthPri =sdDao.getUnCalculationMonth(fullId, listYear.get(0));
			
			if (listYear.size()==2) {
				stockLogger.logger.fatal(fullId+" have compute more than 1 year");
				listWeekCur=sdDao.getUnCalculationWeek(fullId, listYear.get(1));
				listMonthCur =sdDao.getUnCalculationMonth(fullId, listYear.get(1));
			}
			
			//开始：年最早一天 
			System.out.println(listDay.get(0));
			System.out.println(listDay.get(listDay.size()-1));
			
			sdDao.callDayStockDataFromDate(fullId, listDay);//计算日ma5 ma10 涨幅比
			
			//计算周				
			//不能跨年  结尾：年最后一天
		//	listWeek=sdDao.getUnCalculationWeek(fullId,"2014-10-13","2014-12-28");
		//	sdDao.calWeekStockDataFromDate(fullId, listWeek,2014);//计算周

			//listWeek=sdDao.getUnCalculationWeek(fullId,"2015-07-20","2015-07-31");
	

			sdDao.calWeekStockDataFromDate(fullId, listWeekPri,listYear.get(0));//计算周 ma5 ma10 涨幅比
			sdDao.calMonthStockDataFromDate(fullId, listMonthPri,listYear.get(0));//计算周 ma5 ma10 涨幅比
			
			//跨年
			if (listYear.size()==2) {
				
				sdDao.calWeekStockDataFromDate(fullId, listWeekCur,listYear.get(1));//计算周 ma5 ma10 涨幅比
				sdDao.calMonthStockDataFromDate(fullId, listMonthCur,listYear.get(1));//计算周 ma5 ma10 涨幅比	
			}
			
			
			//sdDao.calWeekStockDataFromDate(fullId, listWeek,listYear.get(0));//计算周 ma5 ma10 涨幅比
			 //计算月
			
			//计算2014年月
		//	listMonth.add(10);//第10月
		//	sdDao.calMonthStockDataFromDate(fullId, listMonth,2014); //计算月 ma5 ma10 涨幅比 	
			//2015年
			//List<Integer> listMonth2015 = new ArrayList<Integer>();
			//listMonth2015.add(7);
	
		//	sdDao.calMonthStockDataFromDate(fullId, listMonth,listYear.get(0)); //计算月 ma5 ma10 涨幅比
			
			
		
		}
	}
	 
	public void calculStockAllData(int type) throws IOException, ClassNotFoundException, SQLException
	{
			
		List<String> listStockFullId = new ArrayList<String>();
//		listStockFullId=siDao.getAllFullId();
		
		listStockFullId=sbDao.getAllStockFullId(ConstantsInfo.StockMarket);		
		
	//	2624
		for (int i=0;i<listStockFullId.size();i++)	
		{
			String fullId = listStockFullId.get(i);
			
			//sh600000
		//	if(!fullId.equals("SH600007"))
		//		continue;
					
			
			System.out.println(fullId);
			stockLogger.logger.fatal(i+":"+fullId);
			int isTableExist=sdDao.isExistStockTable(fullId,ConstantsInfo.TABLE_DATA_STOCK);
			if(isTableExist==0)//不存在
			{
				stockLogger.logger.fatal(fullId+"not found data stock");
				continue;
			}

			if(type==0)//计算所有
			{
				List<TimeStock> listTimeStock = new ArrayList<TimeStock>();
				
				//将刚导入的数据新表dataType由0改为1
				sdDao.updateDayTypeForNewStock(fullId);
				listTimeStock=sdDao.getStockTimeStockOfYear(fullId);
				
				sdDao.calAllHistoryDayStockData(fullId, listTimeStock);//计算日ma5 ma10
				sdDao.calAllHistoryWeekStockData(fullId, listTimeStock);//计算周所有值				
				sdDao.calAllHistoryMonthStockData(fullId, listTimeStock);//计算月所有值
				
			//	sdDao.calAllHistorySeasonStockData(fullId, listTimeStock);//计算季度
			//	sdDao.calAllHistoryYearStockData(fullId, listTimeStock);//计算年
				listTimeStock.clear();
			}
			else
			{
				List<String> listDay = new ArrayList<String>();
				List<Integer> listWeek = new ArrayList<Integer>();
				List<Integer> listMonth = new ArrayList<Integer>();
				List<Integer> listYear = new ArrayList<Integer>();
					
				//计算日 导入数据最早一天，计算出所有导入的日期
			//	listDay=sdDao.getUnCalculationDay(fullId,"2015-07-20");	
				
				listDay=sdDao.getUnCalculationDay(fullId);
			
				listYear=sdDao.getUnCalculationYear(fullId);
				
				if (listYear.size()>1) {
					stockLogger.logger.fatal(fullId+" have compute more than 1 year");
				}
				//开始：年最早一天 
				System.out.println(listDay.get(0));
				System.out.println(listDay.get(listDay.size()-1));
				
				sdDao.callDayStockDataFromDate(fullId, listDay);//计算日ma5 ma10 涨幅比
				
				//计算周				
				//不能跨年  结尾：年最后一天
			//	listWeek=sdDao.getUnCalculationWeek(fullId,"2014-10-13","2014-12-28");
			//	sdDao.calWeekStockDataFromDate(fullId, listWeek,2014);//计算周
				//listWeek=sdDao.getUnCalculationWeek(fullId,"2015-07-20","2015-07-31");			
				
				for (i=0;i<listYear.size();i++)	
				{
					int year = listYear.get(i);
					listWeek=sdDao.getUnCalculationWeek(fullId, year);
					listMonth =sdDao.getUnCalculationMonth(fullId, year);
					System.out.println(year);
					sdDao.calWeekStockDataFromDate(fullId, listWeek,year);//计算周 ma5 ma10 涨幅比
					sdDao.calMonthStockDataFromDate(fullId, listMonth,year);//计算周 ma5 ma10 涨幅比
				}
			
			}
				
		}
		
	}
	
	//增加索引
	public void addIndexForStock() throws IOException, ClassNotFoundException, SQLException
	{
		List<String> listStockFullId = new ArrayList<String>();
		listStockFullId=siDao.getAllFullId();
		
		for(Iterator it = listStockFullId.iterator();it.hasNext();)
		{
			String fullId=(String)it.next();
			System.out.println(fullId);
			
			int isTableExist=sdDao.isExistStockTable(fullId,ConstantsInfo.TABLE_DATA_STOCK);
			if(isTableExist==0)//不存在
				continue;
			
			//if(!fullId.equals("sh000001"))
			//		continue;
			
			sdDao.addIndex(fullId);
		}
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
		
		System.out.println("cal data start");
		Date startDate = new Date();
		PropertyConfigurator.configure("StockConf/log4j_append.properties");
		stockLogger.logger.fatal("calculation stock append data start");
		 
	     Connection stockBaseConn = DbConn.getConnDB("stockConf/conn_base_db.ini");  
	     Connection stockDataConn = DbConn.getConnDB("stockConf/conn_data_db.ini"); 
	     Connection stockPointConn = DbConn.getConnDB("stockConf/conn_point_db.ini");  
	     CalculationStock cas =new CalculationStock(stockBaseConn,stockDataConn,stockPointConn);

		
		/*1 添加索引*/
		//addIndexForStock(); //索引
		
		/*2 计算全部数据*/
	//	cas.calculStockAllData(0); //计算全部
		
		//cas.calculStockAllData(1);//计算部分数据
		
		cas.calculStockAllDataForSingleStock("sh000001",ConstantsInfo.StockCalCurData);

		Date endDate = new Date();
		// 计算两个时间点相差的秒数
		long seconds =(endDate.getTime() - startDate.getTime())/1000;
		System.out.println("总共耗时："+seconds+"秒");
		System.out.println("cal data end");
		
		stockBaseConn.close();
		stockDataConn.close();
		stockPointConn.close();
		stockLogger.logger.fatal("总共耗时："+seconds+"秒");
		stockLogger.logger.fatal("calculation stock append data end");
		/*
		//测试"sh000001_copy"
		List<TimeStock> listTimeStock=new ArrayList<TimeStock>();
		listTimeStock=sdDao.getStockTimeStockOfYear("sh000001_copy");			
		sdDao.calAllHistoryWeekStockData("sh000001_copy", listTimeStock);
		sdDao.calAllHistoryMonthStockData("sh000001_copy", listTimeStock);
		sdDao.calAllHistorySeasonStockData("sh000001_copy", listTimeStock);
		sdDao.calAllHistoryYearStockData("sh000001_copy", listTimeStock);
		*/			
	}

}
