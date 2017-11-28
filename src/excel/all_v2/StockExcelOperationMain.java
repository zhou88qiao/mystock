package excel.all_v2;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.PropertyConfigurator;

import common.ConstantsInfo;
import common.stockLogger;

import dao.DbConn;
import dao.StockBaseDao;
import dao.StockData;
import dao.StockDataDao;
import dao.StockOperation;
import dao.StockPoint;
import dao.StockPointDao;
import dao.StockSummary;
import dao.StockSummaryDao;

public class StockExcelOperationMain {
	private StockDataDao sdDao;
	private StockBaseDao sbDao;
	private StockPointDao spDao;
	private StockSummaryDao ssDao;
	
	public StockExcelOperationMain(Connection stockBaseConn,Connection stockDataConn,Connection stockPointConn,Connection stockSummaryConn)
	{
		   this.sbDao = new StockBaseDao(stockBaseConn);
		   this.sdDao =new StockDataDao(stockDataConn);
		   this.spDao =new StockPointDao(stockPointConn);
		   this.ssDao = new StockSummaryDao(stockSummaryConn);
	}
    
    public StockExcelOperationMain(StockBaseDao sbDao,StockDataDao sdDao,StockPointDao spDao,StockSummaryDao ssDao)
	{
		this.sbDao = sbDao;
		this.sdDao = sdDao;
		this.spDao = spDao;
		this.ssDao = ssDao;
	}

  //股票当天开盘收盘价关系 或买出 卖入价关系
	public float getStockOpenCloseValueInfo(float openPrice,float closePrice)
	{
		return (openPrice - closePrice)/closePrice;
	}
	
	public void analyseStockOperationAll(int marketType,String anaylseDate) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException 
	{
		List<String> listStockFullId = new ArrayList<String>();
			
		if(marketType == ConstantsInfo.StockMarket )
			listStockFullId=sbDao.getAllStockFullId(marketType);
		else 
			listStockFullId=sbDao.getAllFuturesFullId(marketType);
		
		int ret =0;
			
		for (int i=0;i<listStockFullId.size();i++)	
		{
			String fullId = listStockFullId.get(i);
			
			//if(!fullId.equals("SH601128"))
   			//	continue;
					
		//	if(i==2)
		//		throw new IOException();
			
			ret = analyseSingleStockOperation(fullId,anaylseDate,ConstantsInfo.DayDataType);
			if (ret> 0){
				ret = analyseSingleStockOperation(fullId,anaylseDate,ConstantsInfo.WeekDataType);
				if (ret > 0) {
					ret = analyseSingleStockOperation(fullId,anaylseDate,ConstantsInfo.MonthDataType);
				}
			}
		
		}
	}
	
	public int analyseSingleStockOperation(String fullId, String anaylseDate, int dateType) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException{
			
		//if(!fullId.equals("SH601111"))
		//	continue;
			
		stockLogger.logger.fatal("*****"+fullId+"*****" + dateType);
		int isTableExist = 0;
		isTableExist=sdDao.isExistStockTable(fullId,ConstantsInfo.TABLE_SUMMARY_STOCK);
		if(isTableExist==0){//不存在
			return -1;
		}
				
		isTableExist=sdDao.isExistStockTable(fullId,ConstantsInfo.TABLE_OPERATION_STOCK);
		if(isTableExist==0){//不存在
			ssDao.createStockOperationTable(fullId);		
		} 
		
		//最后一天汇总数据
		StockSummary lastSS;
	
		lastSS = ssDao.getZhiDingSummaryFromSummaryTable(fullId, anaylseDate, dateType);
		if(lastSS == null) {
			stockLogger.logger.fatal("summary no data");
			return -1;
		}
		
		//最后一天交易数据
		StockData lastSD;
		lastSD = sdDao.getZhiDingDataStock(fullId,dateType,anaylseDate);
		if(lastSD == null) {
			stockLogger.logger.fatal("stock data no data");
			return -1;
		}
		
		int flagContinue = 0;
		int trend = 0;
		//时间差 疑当
		int dateGapPs =0; 
		//前极点时间
		String priPointDate = "";
		//疑似点时间
		String PSDate = "";
		//当前时间
		String curDate = "";
		float endValue = 0;
		
		//开盘收盘价关系
		float octype = getStockOpenCloseValueInfo(lastSD.getOpeningPrice(),lastSD.getClosingPrice());
		
		switch(dateType)
		{
		case ConstantsInfo.DayDataType:
		default:	
			if(lastSS.getDayPSValueGap().contains("-")) //跌
				trend = 1;
			else 
				trend = 0;
			
			priPointDate = lastSS.getDayStartDate();
			PSDate = lastSS.getDayEndDate();
			curDate = lastSS.getDayCurDate();
			if(lastSS.getDayEndValue() == null || lastSS.getDayEndValue().length()<=0){
				flagContinue = 1;
				stockLogger.logger.fatal("stock summary day data null");
			} else {
				endValue = Float.parseFloat(lastSS.getDayEndValue());
			}
			
			//时间差 疑当 >2
			dateGapPs = sdDao.getStockDataDateGap(fullId,PSDate,curDate,dateType);
			if(dateGapPs>2) {
				stockLogger.logger.fatal("dataGap more than 2");	
				flagContinue = 1;	
			}
					
			break;
		case ConstantsInfo.WeekDataType:
			if(lastSS.getWeekPSValueGap().contains("-")) //跌
				trend = 1;
			else 
				trend = 0;
			
			priPointDate = lastSS.getWeekStartDate();
			PSDate = lastSS.getWeekEndDate();
			curDate = lastSS.getWeekCurDate();
			
			if(lastSS.getWeekEndValue() == null || lastSS.getWeekEndValue().length()<=0){
				flagContinue = 1;
				stockLogger.logger.fatal("stock summary week data null");
			} else {	
				endValue = Float.parseFloat(lastSS.getWeekEndValue());
			}	
			break;
		case ConstantsInfo.MonthDataType:
			if(lastSS.getMonthPSValueGap().contains("-")) //跌
				trend = 1;
			else 
				trend = 0;
			
			priPointDate = lastSS.getMonthStartDate();
			PSDate = lastSS.getMonthEndDate();
			curDate = lastSS.getMonthCurDate();
			if(lastSS.getMonthEndValue() == null || lastSS.getMonthEndValue().length()<=0){
				flagContinue = 1;
				stockLogger.logger.fatal("stock summary month data null");
			} else {
				endValue = Float.parseFloat(lastSS.getMonthEndValue());
			}
			break;
		}
		
		if (flagContinue == 1){
			return -1;
		} 
		
		/*
		if(!lastSD.getDate().toString().equals(curDate.toString())) {
			stockLogger.logger.fatal(lastSD.getDate().toString()+ "stock no same date"+ curDate.toString());
			return -1;
		}
		*/
		
		//最后一条操作记录
		StockOperation lastOp;	
		lastOp = ssDao.getLastOperation(fullId, anaylseDate, dateType);
		//重复分析
		if(lastOp!=null && lastOp.getOpDate().equals(curDate)){
			System.out.println("double anysle");
			return -1;
		}	
					
		//疑似最低点与前高极值点时间差>5
		int dataGap = sdDao.getStockDataDateGap(fullId,priPointDate,PSDate,dateType);		
		StockOperation sop = null;
				
		int flag_of_insert_update = 0; // 0 insert  1 update
		StockOperation curOp;
		if(trend == 1) {			
			//买点
			if((dataGap >= 5 && (dateType == ConstantsInfo.DayDataType) && octype <= 0) 
					|| (dataGap >= 5 && ((dateType == ConstantsInfo.WeekDataType) || (dateType == ConstantsInfo.MonthDataType)))) {			
				//又一个买点过滤掉		
				if(lastOp != null && (lastOp.getOpType() == ConstantsInfo.BUG)){
					//continue;
										
					//stockLogger.logger.fatal("cur optype"+ConstantsInfo.BUG+"-pri optype"+lastOp.getOpType());
					//if (dateType == ConstantsInfo.DayDataType){
					//	continue;
					//}
					
					//需要更新
					if (lastSD.getLowestPrice() < lastOp.getStopValue()) {
						flag_of_insert_update = 1;
						stockLogger.logger.fatal("update buy operation");
					} else {
						return -1;
					}
					
				} else {
					flag_of_insert_update = 0;
					//周 判断当天是否为买点
					if (dateType == ConstantsInfo.WeekDataType){
						
						//当天是否为买点
						curOp = ssDao.getCurOperation(fullId, curDate, ConstantsInfo.DayDataType);
						if(curOp!=null && curOp.getOpType() == ConstantsInfo.BUG){
							stockLogger.logger.fatal("insert week buy operation");
						} else {						
							stockLogger.logger.fatal("week buy but not day buy");
							return -1;
						}
						
					} else if (dateType == ConstantsInfo.MonthDataType){
						curOp = ssDao.getCurOperation(fullId, curDate, ConstantsInfo.WeekDataType);
						if(curOp!=null && curOp.getOpType() == ConstantsInfo.BUG){
							stockLogger.logger.fatal("insert month buy operation");
						} else {
							stockLogger.logger.fatal("month buy but not  week buy");
							return -1;
						}
					} else {
						stockLogger.logger.fatal("insert day buy operation");
					}
				}
				//sop = new StockOperation(fullId,0, curDate,lastSD.getOpeningPrice(),lastSD.getLowestPrice(),0,0,0,0,ConstantsInfo.BUG,1);
				if (dateGapPs == 0) {
					dateGapPs = 1;
					sop = new StockOperation(fullId, dateGapPs, curDate, lastSD.getOpeningPrice(),endValue,0,0,0,0,ConstantsInfo.BUG,dateType);
					
				} else { 
					//疑似最低点  开盘价大于收盘价
					StockData ps_data = sdDao.getZhiDingDataStock(fullId,ConstantsInfo.DayDataType,PSDate);
					if(ps_data == null) {	
						return -1;
					}
					//开盘收盘价关系
					float ps_octype = getStockOpenCloseValueInfo(ps_data.getOpeningPrice(),ps_data.getClosingPrice());
					if (ps_octype>0){
						dateGapPs = 2;
						sop = new StockOperation(fullId, dateGapPs, curDate, lastSD.getOpeningPrice(),endValue,0,0,0,0,ConstantsInfo.BUG,dateType);	
					}
				}
				
			//}	else if(lastOp!=null && lastSD.getLowestPrice()<lastOp.getStopValue() && lastSD.getClosingPrice() < lastOp.getStopValue() ){
			} else if(lastOp!=null && lastSD.getLowestPrice()<lastOp.getStopValue() ){
				//止损点				
				//又一个卖点或止损点过滤掉
				if(lastOp.getOpType() == ConstantsInfo.SALE|| lastOp.getOpType() == ConstantsInfo.STOP ) {
					stockLogger.logger.fatal("cur optype"+ConstantsInfo.STOP+"-pri optype"+lastOp.getOpType());					
					if (dateType == ConstantsInfo.DayDataType){
						return -1;
					}
					
					flag_of_insert_update = 1;
					stockLogger.logger.fatal("update stop operation");
					
					float stopRation= getStockOpenCloseValueInfo(lastOp.getStopValue(),lastOp.getBuyValue() );
					sop = new StockOperation(fullId, 3 /* lastOp.getAssId()*/, curDate,0,lastOp.getStopValue(), 0,0,stopRation,0, ConstantsInfo.STOP,dateType);	
				} else {
					
					//如果小于最低点，判断今天如果收盘价大于开盘价，把买点更新到今天，否则就是止损
					if (octype <= 0) {
						flag_of_insert_update = 1;
						stockLogger.logger.fatal("update buy operation");
						
						sop = new StockOperation(fullId, 3, curDate, lastSD.getOpeningPrice(),endValue,0,0,0,0,ConstantsInfo.BUG,dateType);							
					} else {
						flag_of_insert_update = 0;
						stockLogger.logger.fatal("insert stop operation");
						
						float stopRation= getStockOpenCloseValueInfo(lastOp.getStopValue(),lastOp.getBuyValue() );
						sop = new StockOperation(fullId, 3, curDate,0,lastOp.getStopValue(), 0,0,stopRation,0, ConstantsInfo.STOP,dateType);	
					}

				}
				
			}
			
		} else {
			
			//卖点,前一个必须是买点
			if(lastOp!=null && dataGap >= 5 && octype >= 0){
				
				//又一个卖点或止损点过滤掉
				if(lastOp.getOpType() == ConstantsInfo.SALE|| lastOp.getOpType() == ConstantsInfo.STOP ) {
					stockLogger.logger.fatal("cur optype"+ConstantsInfo.SALE+"-pri optype"+lastOp.getOpType());
					if (dateType == ConstantsInfo.DayDataType){
						return -1;
					}
					
					flag_of_insert_update = 1;
					stockLogger.logger.fatal("update sale operation");
				} else {
					flag_of_insert_update = 0;
					stockLogger.logger.fatal("insert sale operation");
				}
				
				float earnOrlose = getStockOpenCloseValueInfo(lastSD.getOpeningPrice(),lastOp.getBuyValue());
				if(earnOrlose>0) // // 买 止 卖 赢 止 亏 
					sop = new StockOperation(fullId, 4/*lastOp.getAssId()*/,curDate,0,0,lastSD.getOpeningPrice(),earnOrlose,0, 0, ConstantsInfo.SALE,dateType);
				else 
					sop = new StockOperation(fullId, 4/*lastOp.getAssId()*/,curDate,0,0,lastSD.getOpeningPrice(),0,0,earnOrlose, ConstantsInfo.SALE,dateType);
				
			}
		}
		
		if(sop !=null) {
			if ( 0 == flag_of_insert_update ) {
				ssDao.insertStockOperationTable(sop);
			} else {
				ssDao.updateStockOperationTable(sop, fullId, lastOp.getId());
			}	
			
			return sop.getOpType();
		} else {
			return -1;
		}
		//else 
		//	 stockLogger.logger.fatal("sop null");	
	}
	
	 public void delete_data(int marketType) throws IOException, ClassNotFoundException, SQLException
	 {
		 List<String> listStockFullId = new ArrayList<String>();
			
			if(marketType == ConstantsInfo.StockMarket )
				listStockFullId=sbDao.getAllStockFullId(marketType);
			else 
				listStockFullId=sbDao.getAllFuturesFullId(marketType);
			String fullId;
			for (int i=0;i<listStockFullId.size();i++)	
			{
				fullId = listStockFullId.get(i);
				System.out.println(fullId);
				ssDao.deleteSummayData(fullId, "2016-07-25");
				
			}
	 		
	 }
	
	
	/**
	 * @param args
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 * @throws NoSuchFieldException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws SecurityException 
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException {
		PropertyConfigurator.configure("stockConf/log4j_excelWriter.properties");
		Connection stockBaseConn = DbConn.getConnDB("stockConf/conn_base_db.ini"); 
        Connection stockDataConn = DbConn.getConnDB("stockConf/conn_data_db.ini"); 
        Connection stockPointConn = DbConn.getConnDB("stockConf/conn_point_db.ini");
        Connection stockSummaryConn = DbConn.getConnDB("stockConf/conn_summary_db.ini");
        
        stockLogger.logger.fatal("excel operation start");	
        StockExcelOperationMain seOp = new StockExcelOperationMain(stockBaseConn,stockDataConn,stockPointConn,stockSummaryConn);
		
        
       // seOp.analyseSingleStockOperation("666","12-91-1",1);
        
        StockOperation sop = new StockOperation("111001", 4, "2017-11-24",(float)1.3,(float)1.2,(float)3.4,(float)2.3,(float)7.9,(float)1.8,1,1);
		
        
        seOp.ssDao.updateStockOperationTable(sop, "111001",1);
        
        
        //排序
        //"2016-04-11"
      //  seOp.analyseStockOperation(ConstantsInfo.StockMarket,"2016-07-19");
    //   seOp.analyseStockOperation(ConstantsInfo.StockMarket,"2016-07-29");
      // seOp.analyseStockOperation(ConstantsInfo.FuturesMarket,null);
        
     //  seOp.analyseStockOperationAll(ConstantsInfo.StockMarket,"2017-11-07");
        
     //  seOp.analyseStockOperationAll(ConstantsInfo.StockMarket,null,ConstantsInfo.WeekDataType);
      
      //  seOp.delete_data(ConstantsInfo.StockMarket);
        stockBaseConn.close();
	    stockDataConn.close();
	    stockPointConn.close();
	    stockSummaryConn.close();

	}

}
