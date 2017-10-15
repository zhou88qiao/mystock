package dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import common.ConstantsInfo;
import excel.all_v2.StockExcelTotalInfo;

public class StockSummaryDao extends BaseDao{
	
	public StockSummaryDao()
	{
		
	}
	public StockSummaryDao(Connection conn)
	{
		super(conn);
	}
	
	public int createStockSummaryTable(String strFullId) throws IOException, ClassNotFoundException, SQLException
	{
		//strFullID SH60000   mysql不区分大小写，表名会成为小写
		String tableName=ConstantsInfo.STOCK_SUMMARY_TABLE_NAME+strFullId;
		
		String dropTableSql="drop table if exists "+tableName+";";
		super.saveOrUpdate(dropTableSql);
		
		String createTablesql="create table " + tableName+
        "(id int auto_increment primary key," + //增加id字段
        "fullId varchar(48) NOT NULL DEFAULT '', " +
        "name varchar(48) NOT NULL DEFAULT '', " +
        "douRong varchar(48) NOT NULL DEFAULT '', " +
        "baseExpect varchar(1024) NOT NULL DEFAULT '', " +
        "main varchar(1024) NOT NULL DEFAULT '', " +
        "psychology varchar(1024) NOT NULL DEFAULT '', " +
        "risk varchar(1024) NOT NULL DEFAULT '', " +
        "potential varchar(1024) NOT NULL DEFAULT '', " +        
        "faucet varchar(1024) NOT NULL DEFAULT '', " +
        "curRange varchar(48) NOT NULL DEFAULT '', " +
        "priComState  varchar(48) NOT NULL DEFAULT '', " +
        "priState  varchar(48) NOT NULL DEFAULT '', " +
        "comState  varchar(48) NOT NULL DEFAULT '', " +
        "psState  varchar(48) NOT NULL DEFAULT '', " +
        "daySaleGrade  varchar(48) NOT NULL DEFAULT '', " +
        "daySaleState  varchar(48) NOT NULL DEFAULT '', " +
        "dayPriUpDateGap  varchar(48) NOT NULL DEFAULT '', " +
        "dayUpOrdownDates  varchar(48) NOT NULL DEFAULT '', " +
        "dayWarnDeal  varchar(48) NOT NULL DEFAULT '', " +
        "dayPS varchar(48) NOT NULL DEFAULT '', " +
		"daySC varchar(48) NOT NULL DEFAULT '', " +
		"weekSaleGrade  varchar(48) NOT NULL DEFAULT '', " +
        "weekSaleState  varchar(48) NOT NULL DEFAULT '', " +
        "weekUpOrdownDates  varchar(48) NOT NULL DEFAULT '', " +
        "weekWarnDeal  varchar(48) NOT NULL DEFAULT '', " +		
		"weekPS varchar(48) NOT NULL DEFAULT '',"+
		"weekSC varchar(48) NOT NULL DEFAULT '',"+		
		"monthUpOrdownDates  varchar(48) NOT NULL DEFAULT '', " +
	    "monthWarnDeal  varchar(48) NOT NULL DEFAULT '', " +	
		"monthPS varchar(48) NOT NULL DEFAULT '',"+
		"monthSC varchar(48) NOT NULL DEFAULT '', "+
		
		"dayPriDate  varchar(48) NOT NULL DEFAULT '', " +
        "dayPriHighOrLowest  varchar(48) NOT NULL DEFAULT '', " +
        "dayReversalRegion  varchar(48) NOT NULL DEFAULT '', " +
        "dayStartDate  varchar(48) NOT NULL DEFAULT '', " +
        "dayStartValue  varchar(48) NOT NULL DEFAULT '', " +
        "dayEndDate varchar(48) NOT NULL DEFAULT '', " +
		"dayEndValue varchar(48) NOT NULL DEFAULT '', " +		
		"dayCurDate  varchar(48) NOT NULL DEFAULT '', " +
        "dayCurValue  varchar(48) NOT NULL DEFAULT '', " +
        "dayWorkRegion  varchar(48) NOT NULL DEFAULT '', " +
        
        "dayPSDateGap varchar(48) NOT NULL DEFAULT '', " +
		"dayPSValueGap varchar(48) NOT NULL DEFAULT '', " +
		"dayPCDateGap varchar(48) NOT NULL DEFAULT '', " +
		"dayPCValueGap varchar(48) NOT NULL DEFAULT '', " +
		"daySCDateGap varchar(48) NOT NULL DEFAULT '', " +
		"daySCValueGap varchar(48) NOT NULL DEFAULT '', " +
			
		"dayMarketPSDateGap varchar(48) NOT NULL DEFAULT '', " +
		"dayMarketPSSpace varchar(48) NOT NULL DEFAULT '', " +
		"dayMarketPCDateGap varchar(48) NOT NULL DEFAULT '', " +
		"dayMarketPCSpace varchar(48) NOT NULL DEFAULT '', " +
		"dayMarketSCDateGap varchar(48) NOT NULL DEFAULT '', " +
		"dayMarketSCSpace varchar(48) NOT NULL DEFAULT '', " +
		"dayTrendConsistent varchar(48) NOT NULL DEFAULT '', " +
        
        "dayDesireValue1  varchar(48) NOT NULL DEFAULT '', " +
        "dayDesireRange1  varchar(48) NOT NULL DEFAULT '', " +
        "dayDesireRate1 varchar(48) NOT NULL DEFAULT '', " +
        "dayDesireValue2  varchar(48) NOT NULL DEFAULT '', " +
        "dayDesireRange2  varchar(48) NOT NULL DEFAULT '', " +
        "dayDesireRate2 varchar(48) NOT NULL DEFAULT '', " +
        "dayDesireValue3  varchar(48) NOT NULL DEFAULT '', " +
        "dayDesireRange3  varchar(48) NOT NULL DEFAULT '', " +
        "dayDesireRate3 varchar(48) NOT NULL DEFAULT '', " +
        "dayDesireValue4  varchar(48) NOT NULL DEFAULT '', " +
        "dayDesireRange4  varchar(48) NOT NULL DEFAULT '', " +
        "dayDesireRate4 varchar(48) NOT NULL DEFAULT '', " +
        "dayDesireValue5  varchar(48) NOT NULL DEFAULT '', " +
        "dayDesireRange5  varchar(48) NOT NULL DEFAULT '', " +
        "dayDesireRate5 varchar(48) NOT NULL DEFAULT '', " +
        "dayDesireValue6  varchar(48) NOT NULL DEFAULT '', " +
        "dayDesireRange6  varchar(48) NOT NULL DEFAULT '', " +
        "dayDesireRate6 varchar(48) NOT NULL DEFAULT '', " +
        
		"dayDesireValue1Gap varchar(48) NOT NULL DEFAULT '', " +
		"dayDesireValue2Gap varchar(48) NOT NULL DEFAULT '', " +
		"dayDesireValue3Gap varchar(48) NOT NULL DEFAULT '', " +
		"dayDesireValue4Gap varchar(48) NOT NULL DEFAULT '', " +
		"dayDesireValue5Gap varchar(48) NOT NULL DEFAULT '', " +
		"dayDesireValue6Gap varchar(48) NOT NULL DEFAULT '', " +
		
		
		"dayBugValue varchar(48) NOT NULL DEFAULT '', " +
		"dayWinValue varchar(48) NOT NULL DEFAULT '', " +
		"dayLoseValue varchar(48) NOT NULL DEFAULT '', " +
		"dayDealWarn varchar(48) NOT NULL DEFAULT '', " +
		"dayOption varchar(48) NOT NULL DEFAULT '', " +
		
		"weekPriDate  varchar(48) NOT NULL DEFAULT '', " +
		"weekPriHighOrLowest  varchar(48) NOT NULL DEFAULT '', " +
		"weekReversalRegion  varchar(48) NOT NULL DEFAULT '', " +
		"weekStartDate  varchar(48) NOT NULL DEFAULT '', " +
		"weekStartValue  varchar(48) NOT NULL DEFAULT '', " +
		"weekEndDate varchar(48) NOT NULL DEFAULT '', " +
		"weekEndValue varchar(48) NOT NULL DEFAULT '', " +		
		"weekCurDate  varchar(48) NOT NULL DEFAULT '', " +
		"weekCurValue  varchar(48) NOT NULL DEFAULT '', " +
		"weekWorkRegion  varchar(48) NOT NULL DEFAULT '', " +

		"weekPSDateGap varchar(48) NOT NULL DEFAULT '', " +
		"weekPSValueGap varchar(48) NOT NULL DEFAULT '', " +
		"weekPCDateGap varchar(48) NOT NULL DEFAULT '', " +
		"weekPCValueGap varchar(48) NOT NULL DEFAULT '', " +
		"weekSCDateGap varchar(48) NOT NULL DEFAULT '', " +
		"weekSCValueGap varchar(48) NOT NULL DEFAULT '', " +
		
		"weekMarketPSDateGap varchar(48) NOT NULL DEFAULT '', " +
		"weekMarketPSSpace varchar(48) NOT NULL DEFAULT '', " +
		"weekMarketPCDateGap varchar(48) NOT NULL DEFAULT '', " +
		"weekMarketPCSpace varchar(48) NOT NULL DEFAULT '', " +
		"weekMarketSCDateGap varchar(48) NOT NULL DEFAULT '', " +
		"weekMarketSCSpace varchar(48) NOT NULL DEFAULT '', " +
		"weekTrendConsistent varchar(48) NOT NULL DEFAULT '', " +

		"weekDesireValue1  varchar(48) NOT NULL DEFAULT '', " +
		"weekDesireRange1  varchar(48) NOT NULL DEFAULT '', " +
		"weekDesireRate1 varchar(48) NOT NULL DEFAULT '', " +
		"weekDesireValue2  varchar(48) NOT NULL DEFAULT '', " +
		"weekDesireRange2  varchar(48) NOT NULL DEFAULT '', " +
		"weekDesireRate2 varchar(48) NOT NULL DEFAULT '', " +
		"weekDesireValue3  varchar(48) NOT NULL DEFAULT '', " +
		"weekDesireRange3  varchar(48) NOT NULL DEFAULT '', " +
		"weekDesireRate3 varchar(48) NOT NULL DEFAULT '', " +
		"weekDesireValue4  varchar(48) NOT NULL DEFAULT '', " +
		"weekDesireRange4  varchar(48) NOT NULL DEFAULT '', " +
		"weekDesireRate4 varchar(48) NOT NULL DEFAULT '', " +
		"weekDesireValue5  varchar(48) NOT NULL DEFAULT '', " +
		"weekDesireRange5  varchar(48) NOT NULL DEFAULT '', " +
		"weekDesireRate5 varchar(48) NOT NULL DEFAULT '', " +
		"weekDesireValue6  varchar(48) NOT NULL DEFAULT '', " +
		"weekDesireRange6  varchar(48) NOT NULL DEFAULT '', " +
		"weekDesireRate6 varchar(48) NOT NULL DEFAULT '', " +

		"weekDesireValue1Gap varchar(48) NOT NULL DEFAULT '', " +
		"weekDesireValue2Gap varchar(48) NOT NULL DEFAULT '', " +
		"weekDesireValue3Gap varchar(48) NOT NULL DEFAULT '', " +
		"weekDesireValue4Gap varchar(48) NOT NULL DEFAULT '', " +
		"weekDesireValue5Gap varchar(48) NOT NULL DEFAULT '', " +
		"weekDesireValue6Gap varchar(48) NOT NULL DEFAULT '', " +

		"weekBugValue varchar(48) NOT NULL DEFAULT '', " +
		"weekWinValue varchar(48) NOT NULL DEFAULT '', " +
		"weekLoseValue varchar(48) NOT NULL DEFAULT '', " +
		"weekDealWarn varchar(48) NOT NULL DEFAULT '', " +
		"weekOption varchar(48) NOT NULL DEFAULT '', " +
		
		"monthPriDate  varchar(48) NOT NULL DEFAULT '', " +
		"monthPriHighOrLowest  varchar(48) NOT NULL DEFAULT '', " +
		"monthReversalRegion  varchar(48) NOT NULL DEFAULT '', " +
		"monthStartDate  varchar(48) NOT NULL DEFAULT '', " +
		"monthStartValue  varchar(48) NOT NULL DEFAULT '', " +
		"monthEndDate varchar(48) NOT NULL DEFAULT '', " +
		"monthEndValue varchar(48) NOT NULL DEFAULT '', " +		
		"monthCurDate  varchar(48) NOT NULL DEFAULT '', " +
		"monthCurValue  varchar(48) NOT NULL DEFAULT '', " +
		"monthWorkRegion  varchar(48) NOT NULL DEFAULT '', " +

		"monthPSDateGap varchar(48) NOT NULL DEFAULT '', " +
		"monthPSValueGap varchar(48) NOT NULL DEFAULT '', " +
		"monthPCDateGap varchar(48) NOT NULL DEFAULT '', " +
		"monthPCValueGap varchar(48) NOT NULL DEFAULT '', " +
		"monthSCDateGap varchar(48) NOT NULL DEFAULT '', " +
		"monthSCValueGap varchar(48) NOT NULL DEFAULT '', " +
		
		"monthMarketPSDateGap varchar(48) NOT NULL DEFAULT '', " +
		"monthMarketPSSpace varchar(48) NOT NULL DEFAULT '', " +
		"monthMarketPCDateGap varchar(48) NOT NULL DEFAULT '', " +
		"monthMarketPCSpace varchar(48) NOT NULL DEFAULT '', " +
		"monthMarketSCDateGap varchar(48) NOT NULL DEFAULT '', " +
		"monthMarketSCSpace varchar(48) NOT NULL DEFAULT '', " +
		"monthTrendConsistent varchar(48) NOT NULL DEFAULT '', " +	
		
		"monthDesireValue1  varchar(48) NOT NULL DEFAULT '', " +
		"monthDesireRange1  varchar(48) NOT NULL DEFAULT '', " +
		"monthDesireRate1 varchar(48) NOT NULL DEFAULT '', " +
		"monthDesireValue2  varchar(48) NOT NULL DEFAULT '', " +
		"monthDesireRange2  varchar(48) NOT NULL DEFAULT '', " +
		"monthDesireRate2 varchar(48) NOT NULL DEFAULT '', " +
		"monthDesireValue3  varchar(48) NOT NULL DEFAULT '', " +
		"monthDesireRange3  varchar(48) NOT NULL DEFAULT '', " +
		"monthDesireRate3 varchar(48) NOT NULL DEFAULT '', " +
		"monthDesireValue4  varchar(48) NOT NULL DEFAULT '', " +
		"monthDesireRange4  varchar(48) NOT NULL DEFAULT '', " +
		"monthDesireRate4 varchar(48) NOT NULL DEFAULT '', " +
		"monthDesireValue5  varchar(48) NOT NULL DEFAULT '', " +
		"monthDesireRange5  varchar(48) NOT NULL DEFAULT '', " +
		"monthDesireRate5 varchar(48) NOT NULL DEFAULT '', " +
		"monthDesireValue6  varchar(48) NOT NULL DEFAULT '', " +
		"monthDesireRange6  varchar(48) NOT NULL DEFAULT '', " +
		"monthDesireRate6 varchar(48) NOT NULL DEFAULT '', " +
	

		"monthDesireValue1Gap varchar(48) NOT NULL DEFAULT '', " +
		"monthDesireValue2Gap varchar(48) NOT NULL DEFAULT '', " +
		"monthDesireValue3Gap varchar(48) NOT NULL DEFAULT '', " +
		"monthDesireValue4Gap varchar(48) NOT NULL DEFAULT '', " +
		"monthDesireValue5Gap varchar(48) NOT NULL DEFAULT '', " +
		"monthDesireValue6Gap varchar(48) NOT NULL DEFAULT '', " +

		
		"monthBugValue varchar(48) NOT NULL DEFAULT '', " +
		"monthWinValue varchar(48) NOT NULL DEFAULT '', " +
		"monthLoseValue varchar(48) NOT NULL DEFAULT '', " +
		"monthDealWarn varchar(48) NOT NULL DEFAULT '', " +
		"monthOption varchar(48) NOT NULL DEFAULT '' " +
		
		") ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1";
		//System.out.println(createTablesql);
		return super.saveOrUpdate(createTablesql);
	}
	
	
	public int truncateSummaryStock(String fullId) throws IOException, ClassNotFoundException, SQLException { 
		String stockTable=ConstantsInfo.STOCK_SUMMARY_TABLE_NAME+fullId;
	    return super.saveOrUpdate("truncate "+stockTable);  
	} 
	  
	public int loadSummaryFiletoDB(String sqlPath,String stockName) throws IOException, ClassNotFoundException, SQLException
	{
		String tableName=ConstantsInfo.STOCK_SUMMARY_TABLE_NAME+stockName;
		String sql ="load data infile \""+sqlPath +"\" into table "+tableName+ " character set gbk FIELDS TERMINATED BY ',' LINES TERMINATED BY '\\r\\n' " +
				"(fullId,name,douRong,baseExpect,main,psychology,risk,potential,faucet," +
				"curRange,priComState,priState,comState,psState," +
				"daySaleGrade,daySaleState,dayPriUpDateGap,dayUpOrdownDates,dayWarnDeal,dayPS,daySC," +
				"weekSaleGrade,weekSaleState,weekUpOrdownDates,weekWarnDeal,weekPS,weekSC,	" +
				"monthUpOrdownDates,monthWarnDeal,monthPS,monthSC," +
				"dayPriDate,dayPriHighOrLowest,dayReversalRegion,dayStartDate,dayStartValue,dayEndDate,dayEndValue,	dayCurDate,dayCurValue,dayWorkRegion," +
				"dayPSDateGap,dayPSValueGap,dayPCDateGap,dayPCValueGap,daySCDateGap,daySCValueGap," +
				"dayMarketPSDateGap,dayMarketPSSpace,dayMarketPCDateGap,dayMarketPCSpace,dayMarketSCDateGap,dayMarketSCSpace,dayTrendConsistent," +
				"dayDesireValue1,dayDesireRange1,dayDesireRate1,dayDesireValue2,dayDesireRange2,dayDesireRate2,dayDesireValue3,dayDesireRange3,dayDesireRate3," +
				"dayDesireValue4,dayDesireRange4,dayDesireRate4,dayDesireValue5,dayDesireRange5,dayDesireRate5,dayDesireValue6,dayDesireRange6,dayDesireRate6," +
				"dayDesireValue1Gap,dayDesireValue2Gap,dayDesireValue3Gap,dayDesireValue4Gap,dayDesireValue5Gap,dayDesireValue6Gap," +
				"dayBugValue,dayWinValue,dayLoseValue,dayDealWarn,dayOption," +
				
				"weekPriDate,weekPriHighOrLowest,weekReversalRegion,weekStartDate,weekStartValue,weekEndDate,weekEndValue,	weekCurDate,weekCurValue,weekWorkRegion," +
				"weekPSDateGap,weekPSValueGap,weekPCDateGap,weekPCValueGap,weekSCDateGap,weekSCValueGap," +
				"weekMarketPSDateGap,weekMarketPSSpace,weekMarketPCDateGap,weekMarketPCSpace,weekMarketSCDateGap,weekMarketSCSpace,weekTrendConsistent," +
				"weekDesireValue1,weekDesireRange1,weekDesireRate1,weekDesireValue2,weekDesireRange2,weekDesireRate2,weekDesireValue3,weekDesireRange3,weekDesireRate3," +
				"weekDesireValue4,weekDesireRange4,weekDesireRate4,weekDesireValue5,weekDesireRange5,weekDesireRate5,weekDesireValue6,weekDesireRange6,weekDesireRate6," +
				"weekDesireValue1Gap,weekDesireValue2Gap,weekDesireValue3Gap,weekDesireValue4Gap,weekDesireValue5Gap,weekDesireValue6Gap," +
				"weekBugValue,weekWinValue,weekLoseValue,weekDealWarn,weekOption," +
				
				"monthPriDate,monthPriHighOrLowest,monthReversalRegion,monthStartDate,monthStartValue,monthEndDate,monthEndValue,	monthCurDate,monthCurValue,monthWorkRegion," +
				"monthPSDateGap,monthPSValueGap,monthPCDateGap,monthPCValueGap,monthSCDateGap,monthSCValueGap," +	
				"monthMarketPSDateGap,monthMarketPSSpace,monthMarketPCDateGap,monthMarketPCSpace,monthMarketSCDateGap,monthMarketSCSpace,monthTrendConsistent," +
				"monthDesireValue1,monthDesireRange1,monthDesireRate1,monthDesireValue2,monthDesireRange2,monthDesireRate2,monthDesireValue3,monthDesireRange3,monthDesireRate3," +
				"monthDesireValue4,monthDesireRange4,monthDesireRate4,monthDesireValue5,monthDesireRange5,monthDesireRate5,monthDesireValue6,monthDesireRange6,monthDesireRate6," +
				"monthDesireValue1Gap,monthDesireValue2Gap,monthDesireValue3Gap,monthDesireValue4Gap,monthDesireValue5Gap,monthDesireValue6Gap," +
				"monthBugValue,monthWinValue,monthLoseValue,monthDealWarn,monthOption)";
		//System.out.println("loadData sql:"+sql);
		return super.saveOrUpdate(sql);
	}
	  
	
	/*
	public int insertStockSummaryTable(String stockFullId, StockSummary ssu) throws IOException, ClassNotFoundException, SQLException
	{
		String stockTable=ConstantsInfo.STOCK_SUMMARY_TABLE_NAME+stockFullId;
	
		String insertSql = "insert into "+stockTable;
		return super.saveOrUpdate(
				 insertSql+" values(?,?,?,?,?,?,?,?,?,?)", 
				 0,ssu.getAnaly_date(),ssu.getComState(),ssu.getPsState(),
				 ssu.getDayPS(),ssu.getDaySC(),ssu.getWeekPS(),ssu.getWeekSC(),
				 ssu.getMonthPS(),ssu.getMonthSC());
		
	}
	*/
	

	//最近30交易日时间的所有值
	public List<String> getDatesFromSummarySH000001For15() throws SQLException
	{
		List<String> dates=new ArrayList<String>();
		String stockTable=ConstantsInfo.STOCK_SUMMARY_TABLE_NAME+"sh000001";
		String selectSql = null;
		
			//排好序
		selectSql="select DISTINCT(dayCurDate) from "+stockTable+" ORDER BY dayCurDate desc limit 15";
		//System.out.println(selectSql);
		dates=getQuery(selectSql,null);
		return dates;
	}
	
	//最近30交易日时间的所有值
	public List<String> getDatesFromSummarySH000001For30() throws SQLException
	{
		List<String> dates=new ArrayList<String>();
		String stockTable=ConstantsInfo.STOCK_SUMMARY_TABLE_NAME+"sh000001";
		String selectSql = null;
		
			//排好序
		selectSql="select DISTINCT(dayCurDate) from "+stockTable+" ORDER BY dayCurDate desc limit 30";
		//System.out.println(selectSql);
		dates=getQuery(selectSql,null);
		return dates;
	}
	
	
	//最近30交易日时间的所有值
	public List<StockOperation> getOperationFromOperationTable(String fullId,int dateType, int days) throws SQLException, IOException, ClassNotFoundException
	{
		/*
		StringBuilder builder = new StringBuilder("");
		builder.append("select * from ");
		builder.append(ConstantsInfo.STOCK_OPERATION_TABLE_NAME);
		builder.append(fullId);
		builder.append(" ORDER BY opDate desc limit "+days);
		String stockTable=ConstantsInfo.STOCK_OPERATION_TABLE_NAME+fullId;
		return super.executeQuery(builder.toString(),StockOperation.class); 
		*/
		String stockTable=ConstantsInfo.STOCK_OPERATION_TABLE_NAME+fullId;
		String selectSql = null;
		//排好序
		if (dateType < 0)
			selectSql="select * from "+stockTable+" ORDER BY opDate desc limit "+days;
		else
			selectSql="select * from "+stockTable+" where dateType="+dateType+" ORDER BY opDate desc limit "+days;

		//排好序
		//selectSql="select * from "+stockTable+" ORDER BY opDate desc limit "+days;
		//System.out.println(selectSql);
		return super.executeQuery(selectSql,StockOperation.class); 
	}
	
	
	public List<StockOperation> getOperationFromOperationTableByDate(String fullId, String curDate) throws SQLException, IOException, ClassNotFoundException
	{
		
		String stockTable=ConstantsInfo.STOCK_OPERATION_TABLE_NAME+fullId;
		String selectSql = null;
		
		selectSql="select * from "+stockTable+" where opDate =\""+curDate+"\"";

		//排好序
		//selectSql="select * from "+stockTable+" ORDER BY opDate desc limit "+days;
		System.out.println(selectSql);
		return super.executeQuery(selectSql,StockOperation.class); 
	}
	
	
	//最近30交易日时间的所有值
	public List<StockSummary> getSummaryFromSummaryTable(String fullId, int days) throws SQLException, IOException, ClassNotFoundException
	{
		
		StringBuilder builder = new StringBuilder("");
		builder.append("select * from ");
		builder.append(ConstantsInfo.STOCK_SUMMARY_TABLE_NAME);
		builder.append(fullId);
		builder.append(" ORDER BY dayCurDate desc limit "+days);
		/*
		String stockTable=ConstantsInfo.STOCK_SUMMARY_TABLE_NAME+fullId;
		String selectSql = null;
	
		//排好序
		selectSql="select * from "+stockTable+" ORDER BY dayCurDate desc limit "+days;
		*/
	//	System.out.println(builder.toString());
		return super.executeQuery(builder.toString(),StockSummary.class); 
	}
	
	
	//指定时间的数据状态组合
	public String getpsStatusFromSummaryTable(String fullId,String sdate) throws SQLException, IOException, ClassNotFoundException
	{
		
		String stockTable=ConstantsInfo.STOCK_SUMMARY_TABLE_NAME+fullId;
		String selectSql = null;
	
		//排好序
		selectSql="select psState from "+stockTable+" where dayCurDate = '"+sdate+"'";
	
		return super.getSingleQuery(selectSql,null); 
	}
	
	
	//指定时间的数据
	public StockSummary getZhiDingSummaryFromSummaryTable(String fullId,String sdate, int dateType) throws SQLException, IOException, ClassNotFoundException
	{
		
		String stockTable=ConstantsInfo.STOCK_SUMMARY_TABLE_NAME+fullId;
		String selectSql = null;
	
		//排好序
		switch(dateType)
		{
		case ConstantsInfo.DayDataType:
		default:
			selectSql="select * from "+stockTable+" where dayCurDate = '"+sdate+"'";
			break;
		case ConstantsInfo.WeekDataType:
			selectSql="select * from "+stockTable+" where weekCurDate = '"+sdate+"'";
			break;
		case ConstantsInfo.MonthDataType:
			selectSql="select * from "+stockTable+" where monthCurDate = '"+sdate+"'";
			break;
		}	
	
		return super.executeSingleQuery(selectSql,StockSummary.class); 
	}
	
	
	//最后一条的所有值
	public StockSummary getLastSummaryFromSummaryTable(String fullId) throws SQLException, IOException, ClassNotFoundException
	{
		
		String stockTable=ConstantsInfo.STOCK_SUMMARY_TABLE_NAME+fullId;
		String selectSql = null;
	
		//排好序
		selectSql="select * from "+stockTable+" ORDER BY dayCurDate desc limit 1";
		return super.executeSingleQuery(selectSql,StockSummary.class); 
	}
	
	
	//生成操作表
	public int createStockOperationTable(String strFullId) throws IOException, ClassNotFoundException, SQLException
	{
		//strFullID SH60000   mysql不区分大小写，表名会成为小写
		String tableName=ConstantsInfo.STOCK_OPERATION_TABLE_NAME+strFullId;
		
		String dropTableSql="drop table if exists "+tableName+";";
		super.saveOrUpdate(dropTableSql);
		
		String createTablesql="create table " + tableName+
        "(id int auto_increment primary key," + //增加id字段
        "fullId varchar(48) NOT NULL DEFAULT '', " +
        "assId int default 0, " +
        "opDate  varchar(48) NOT NULL DEFAULT '', " +
        "buyValue float default 0, " +
        "stopValue float default 0, " +
        "saleValue float default 0, " +  
        "earnRatio float default 0, " +
        "stopRatio float default 0, " +        
        "lossRatio float default 0, " +
        "opType  int default 1, " +
        "dateType  int default 1 " +
    	") ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1";
		System.out.println(createTablesql);
		return super.saveOrUpdate(createTablesql);
	}
	
	//生成操作表
	public int addColStockOperationTable(String strFullId) throws IOException, ClassNotFoundException, SQLException
	{
		//strFullID SH60000   mysql不区分大小写，表名会成为小写
		String tableName=ConstantsInfo.STOCK_OPERATION_TABLE_NAME+strFullId;
			
		String createTablesql="alter table " + tableName+" add column dateType int default 1;";
		System.out.println(createTablesql);
		return super.saveOrUpdate(createTablesql);
	}

	
	  //三级行业股票对应表
	  public int insertStockOperationTable(StockOperation sop) throws IOException, ClassNotFoundException, SQLException { 
		 
		  String stockTable=ConstantsInfo.STOCK_OPERATION_TABLE_NAME+sop.getFullId();
		 
		  return super.saveOrUpdate(  
	        		"insert into "+ stockTable+" (`fullId`,`assId`, `opDate`,`buyValue`,`stopValue`,`saleValue`,`earnRatio`,`stopRatio`,`lossRatio`,`opType`,`dateType`) " +
	        		" values(?,?,?,?,?,?,?,?,?,?,?)", sop.getFullId(),sop.getAssId(), sop.getOpDate(),sop.getBuyValue(),sop.getStopValue(),
	        		sop.getSaleValue(),sop.getEarnRatio(),sop.getStopRatio(),sop.getLossRatio(),
	        		sop.getOpType(),sop.getDateType());
		  	
	  }
	  
	  
	  public int updateStockOperationTable(StockOperation op,String stockFullId,int id) throws IOException, ClassNotFoundException, SQLException
	{
		String stockTable=ConstantsInfo.STOCK_OPERATION_TABLE_NAME+stockFullId;
	//id唯一	只更新四个值
		String updateSql = "update "+stockTable+" set assId='"+op.getAssId()+"',opDate='"+op.getOpDate()+"',buyValue='"+op.getBuyValue()+
		"',stopValue='"+op.getStopValue()+"',saleValue='"+op.getSaleValue()+
		"',earnRatio='"+op.getEarnRatio()+"',stopRatio='"+op.getStopRatio()+
		"',lossRatio='"+op.getLossRatio()+"' where id="+id;
		//System.out.println(updateSql);
		return super.saveOrUpdate(updateSql);		
	}
	  
	  
	  
	  //最后一条的所有值或之前时期的一条记录
		public StockOperation getLastOperation(String fullId,String sdate, int dateType) throws SQLException, IOException, ClassNotFoundException
		{
			
			String stockTable=ConstantsInfo.STOCK_OPERATION_TABLE_NAME+fullId;
			String selectSql = null;
		
			//排好序
			if(sdate!=null)
				selectSql="select * from "+stockTable+" where dateType="+dateType+" and opDate <= '"+sdate+"' ";
			else
				selectSql="select * from "+stockTable+" where dateType="+dateType+" ORDER BY id desc limit 1 ";
			//System.out.println(selectSql);
			return super.executeSingleQuery(selectSql,StockOperation.class); 
		}
		
		//获取当天的一条记录
		public StockOperation getCurOperation(String fullId, String sdate, int dateType) throws SQLException, IOException, ClassNotFoundException
		{
			String stockTable = ConstantsInfo.STOCK_OPERATION_TABLE_NAME+fullId;
			String selectSql = null;
		
			//排好序	
			selectSql="select * from "+stockTable+" where dateType="+dateType+" and opDate = '"+sdate+"' ";
			return super.executeSingleQuery(selectSql,StockOperation.class); 
		}
	
		
		//最后一条的所有值或之前时期的一条记录
		public int deleteSummayData(String fullId,String sdate) throws SQLException, IOException, ClassNotFoundException
		{
			
			String stockTable=ConstantsInfo.STOCK_SUMMARY_TABLE_NAME+fullId;
			String selectSql = null;
		
			//排好序
			if(sdate!=null)
				selectSql="DELETE from  "+stockTable+" where dayWorkRegion ='"+sdate+"'";
			
			return super.saveOrUpdate(selectSql); 
		}
		
}
