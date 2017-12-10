package dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import common.ConstantsInfo;
import dao.BaseDao;
import dao.StockData;
import stock.analysis.PointClass;
import stock.timer.DateStock;
import stock.timer.StockDateTimer;
import stock.timer.TimeStock;

public class StockDataDao extends BaseDao{
	
	StockDateTimer sTimer=new StockDateTimer();
	static PointClass pClass=new PointClass();
	public StockDataDao()
	{
		
	}
	
	public StockDataDao(Connection conn)
	{
		super(conn);
	}
	
	public int createStockDataTable(String strFullId) throws IOException, ClassNotFoundException, SQLException
	{
		//strFullID SH60000   mysql不区分大小写，表名会成为小写
		String tableName=ConstantsInfo.STOCK_DATA_TABLE_NAME+strFullId;
		
		String dropTableSql="drop table if exists "+tableName+";";
		super.saveOrUpdate(dropTableSql);
		String createTablesql="create table " + tableName+
        "(id int auto_increment primary key," + //增加id字段
        "date Date, " +
        "openingPrice float default 0, " +
        "highestPrice float default 0, " +
        "lowestPrice float default 0, " +
        "closingPrice float default 0, " +
		"stockVolume bigint default 0, " +
		"dailyTurnover double default 0,"+
		"ma5Price float default 0,"+
		"ma10Price float default 0," +
		"range float default 0," +
		"amplitude float default 0," +
		"dataType SMALLINT default 0," +
		"index date_index(`date`)," +
		"index data_index(`dataType`)) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1";
		
		return super.saveOrUpdate(createTablesql);
	}
	
	public int loadDatafFiletoDB(String sqlPath,String stockName) throws IOException, ClassNotFoundException, SQLException
	{
		String tableName=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockName;
		String sql ="load data infile \""+sqlPath +"\" into table "+tableName+ " FIELDS TERMINATED BY '\\t' LINES TERMINATED BY '\\r\\n'"+" (`date`,`openingPrice`,`highestPrice`,`lowestPrice`,`closingPrice`,`stockVolume`,`dailyTurnover`);";
		System.out.println("loadData sql:"+sql);
		return super.saveOrUpdate(sql);
	}
	
	
	public int alterStockDeafaultDataType(String stockFullId) throws IOException, ClassNotFoundException, SQLException
	{
		String tableName=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		String sql=null;
		sql="alter table "+tableName +" alter column dataType set default '0'";		
		System.out.println(sql);
		return super.saveOrUpdate(sql);
	}
	

	
	/*是否存在股票表*/
	public int isExistStockTable(String stockFullId,int table_type) throws IOException, ClassNotFoundException, SQLException
	{
		String sql=null;
		String databaseName=null;
		String table_name=null;
		switch (table_type)
		{
		case ConstantsInfo.TABLE_DATA_STOCK:
			databaseName="stock_data";
			table_name = ConstantsInfo.STOCK_DATA_TABLE_NAME + stockFullId;
			break;
		case ConstantsInfo.TABLE_POINT_STOCK:
			databaseName="stock_point";
			table_name = ConstantsInfo.STOCK_POINT_TABLE_NAME + stockFullId;
			break;
		case ConstantsInfo.TABLE_SUMMARY_STOCK:
			databaseName="stock_summary";
			table_name = ConstantsInfo.STOCK_SUMMARY_TABLE_NAME + stockFullId;
			break;
		case ConstantsInfo.TABLE_OPERATION_STOCK:
			databaseName="stock_summary";
			table_name = ConstantsInfo.STOCK_OPERATION_TABLE_NAME + stockFullId;
			break;
		}
		sql="SELECT COUNT(*) FROM information_schema.TABLES WHERE TABLE_SCHEMA = '"+databaseName+"'  and TABLE_NAME = '"+table_name+"'";
		//System.out.println(sql);
		return super.getSingleIntQuery(sql, null);
	}
	
	
	
	public int deleteExtDataToStock(String stockFullId,int dataType) throws IOException, ClassNotFoundException, SQLException
	{
		String tableName="day_stock_"+stockFullId;
		//	delete from day_stock_sz000015 WHERE stockVolume=0 and dailyTurnover=0;
		String sql;
		sql="delete from "+tableName +" where stockVolume=0 and dailyTurnover=0 and dataType='"+ConstantsInfo.DayDataType+"'";
				
		return super.saveOrUpdate(sql);
	}
	
	public int deleteDataToStock(String stockFullId,int dataType) throws IOException, ClassNotFoundException, SQLException
	{
		String tableName="day_stock_"+stockFullId;

		String sql;
		
		switch(dataType)
		{
		case ConstantsInfo.WeekDataType:
		default:
			sql="delete from "+tableName +" where dataType='"+ConstantsInfo.WeekDataType+"'";
			break;
		case ConstantsInfo.MonthDataType:
			sql="delete from "+tableName +" where dataType='"+ConstantsInfo.MonthDataType+"'";
			break;
		case ConstantsInfo.SeasonDataType:
			sql="delete from "+tableName +" where dataType='"+ConstantsInfo.SeasonDataType+"'";			
			break;
		case ConstantsInfo.YearDataType:
			sql="delete from "+tableName +" where dataType='"+ConstantsInfo.YearDataType+"'";			
			break;
		}
		//System.out.println(sql);
		return super.saveOrUpdate(sql);
	}
	
		
	//获取某股票年份
	public List<String> getStockOfYear(String stockFullId) throws IOException, ClassNotFoundException, SQLException
	{
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		String selectSql = "select distinct YEAR(date) from "+stockTable; //distinct去重
	//	System.out.println(selectSql);
		return super.getQuery(selectSql, null);
	}
	
	//获取某股票年份月份数,周数，日数
	public int getStockMonthWeekDaysOfYear(String stockFullId,String year,int type) throws IOException, ClassNotFoundException, SQLException
	{
		String tableName=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		String selectSql=null;
		String firstDayofYear=year+"-01-01";		
		String lastDayofYear=year+"-12-31";
		switch(type)
		{
		case ConstantsInfo.MonthDataType:
			selectSql="select MAX(MONTH(date)) from "+tableName+" where date <='"+lastDayofYear+"' and date >='"+firstDayofYear+"' and dataType='"+ConstantsInfo.DayDataType+"'";
			break;
		case ConstantsInfo.WeekDataType:
			selectSql="select MAX(WEEK(date)) from "+tableName+" where date <='"+lastDayofYear+"' and date >='"+firstDayofYear+"' and dataType='"+ConstantsInfo.DayDataType+"'";
			break;
		case ConstantsInfo.DayDataType:
			selectSql="select MAX(DayofYear(date)) from "+tableName+" where date <='"+lastDayofYear+"' and date >='"+firstDayofYear+"' and dataType='"+ConstantsInfo.DayDataType+"'";
			break;
		}
		
	//	System.out.println(selectSql);
		return super.getSingleIntQuery(selectSql,null);
	}	
	
	public int getSeasonNumFromMonth(int month)
	{
		int seasonNum=0;
		switch(month)
		{
		case 1:
		case 2:
		case 3:
			seasonNum=1;
			break;
		case 4:
		case 5:
		case 6:
			seasonNum=2;
			break;
		case 7:
		case 8:
		case 9:
			seasonNum=3;
			break;
		case 10:
		case 11:
		case 12:
		default:
			seasonNum=4;
			break;				
		}
		return seasonNum;
		
	}
	
	public List<TimeStock> getStockTimeStockOfYear(String stockFullId) throws IOException, ClassNotFoundException, SQLException
	{
		
		List<String> listStockYear = new ArrayList<String>();
		listStockYear=getStockOfYear(stockFullId);
		List<TimeStock> listTimeStock=new ArrayList<TimeStock>();
		
		for(Iterator it = listStockYear.iterator();it.hasNext();)
		{
			String year = (String) it.next();	
			int dayNum=getStockMonthWeekDaysOfYear(stockFullId,year,ConstantsInfo.DayDataType);
			if(dayNum==0)
				continue;
			int weekNum=getStockMonthWeekDaysOfYear(stockFullId,year,ConstantsInfo.WeekDataType);
			int monthNum=getStockMonthWeekDaysOfYear(stockFullId,year,ConstantsInfo.MonthDataType);
			if(weekNum==0 || monthNum==0)
				continue;
			int seasonNum=getSeasonNumFromMonth(monthNum);		
		
			TimeStock ts=new TimeStock(year,seasonNum,monthNum,weekNum,dayNum);
			listTimeStock.add(ts);			
		}
		
		return listTimeStock;		
	}		

	public int updateDataTypeValueToStock(String stockFullId,int dataType,int id) throws IOException, ClassNotFoundException, SQLException
	{
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;	
	//	String updateSql = "update "+stockTable+" set dataType='1' where id <="+id;
		String updateSql = "update "+stockTable+" set dataType='"+ConstantsInfo.DayDataType+"'";;
		System.out.println(updateSql);
		return super.saveOrUpdate(updateSql);
	}
	
	public int updateDefaultMAValueToStock(String stockFullId,int dataType,int id) throws IOException, ClassNotFoundException, SQLException
	{
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;	
	
		String updateSql = "update "+stockTable+" set ma5Price='0',ma10Price='0' where dataType='"+ConstantsInfo.DayDataType+"'";;	
		return super.saveOrUpdate(updateSql);
	}
	
	
	//获取股票年份对应周数 map
	public Map getWeekNumStock(String stockFullId,List<String> yearList) throws IOException, ClassNotFoundException, SQLException
	{
		String tableName=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		String sql=null;
		Map weekofYearMap = new TreeMap(); //排序
		
		for(Iterator it = yearList.iterator();it.hasNext();)
		{
			String year = (String) it.next();	
			String firstDayofYear=year+"-01-01";
			String lastDayofYear=year+"-12-31";
			sql="select MAX(WEEK(date)) from "+tableName+" where date <='"+lastDayofYear+"' and date >='"+firstDayofYear+"' and dataType='"+ConstantsInfo.DayDataType+"'";
			//System.out.println(sql);
			int weekNum=super.getSingleIntQuery(sql,null);
			weekofYearMap.put(year, weekNum);
			System.out.println(year+"week:"+weekNum);				
		}
		
		return weekofYearMap;	
	}
	
	public String getDateFromLastWeek(List<StockData> firstWeek,List<StockData> lastWeek)
	{
		return lastWeek.get(lastWeek.size()-1).getDate().toString();//去年
	}
	
	
	public String getDateFromFirstAndLastWeek(List<StockData> firstWeek,List<StockData> lastWeek)
	{
		return firstWeek.get(firstWeek.size()-1).getDate().toString();//今年
	}
	
	public float getOpeningPriceFromFirstAndLastWeek(List<StockData> firstWeek,List<StockData> lastWeek)
	{
		return lastWeek.get(0).getOpeningPrice();//去年
	}
	
	public float getClosingPriceFromFirstAndLastWeek(List<StockData> firstWeek,List<StockData> lastWeek)
	{
		return firstWeek.get(firstWeek.size()-1).getClosingPrice();//今年
	}
	
	public float getHighestPriceFromFirstAndLastWeek(List<StockData> firstWeek,List<StockData> lastWeek)
	{
		float maxHighestPrice=0;
		Iterator it;
		for(it = firstWeek.iterator();it.hasNext();)
		{
			StockData sday= (StockData) it.next();
			maxHighestPrice=sday.getHighestPrice();	//初始值
			if(maxHighestPrice<sday.getHighestPrice())
				maxHighestPrice=sday.getHighestPrice();			
		}
		for(it = lastWeek.iterator();it.hasNext();)
		{
			StockData sday= (StockData) it.next();
			if(maxHighestPrice<sday.getHighestPrice())
				maxHighestPrice=sday.getHighestPrice();			
		}
		return maxHighestPrice;
	}
	
	public float getLowestPriceFromFirstAndLastWeek(List<StockData> firstWeek,List<StockData> lastWeek)
	{
		float minLowestPrice=0;
		Iterator it;
		for(it = firstWeek.iterator();it.hasNext();)
		{
			StockData sday= (StockData) it.next();
			minLowestPrice=sday.getLowestPrice();//初始值
			if(minLowestPrice>sday.getLowestPrice())
				minLowestPrice=sday.getLowestPrice();			
		}
		for(it = lastWeek.iterator();it.hasNext();)
		{
			StockData sday= (StockData) it.next();
			if(minLowestPrice>sday.getLowestPrice())
				minLowestPrice=sday.getLowestPrice();			
		}
		return minLowestPrice;
	}
	
	//SELECT * FROM day_stock_sh000001 where WEEK(date) = 52 and YEAR(date) = 2013;	
	//查询当前周的所有日表数据
	public List<StockData> getAllDataOfWeek(String stockFullId,int week,int year) throws IOException, ClassNotFoundException, SQLException
	{
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		String selectSql = "select * from "+ stockTable+" where WEEK(date)="+week+" and YEAR(date)="+year+" and dataType='"+ConstantsInfo.DayDataType+"'"; 
		System.out.println(selectSql);
		return super.executeQuery(selectSql,StockData.class); 
	}
	
	
	//查询每周的时间
	public String getDateOfWeek(String stockFullId,int week,int year) throws IOException, ClassNotFoundException, SQLException
	{
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		String selectSql = "select date from "+ stockTable+" where WEEK(date)="+week+" and YEAR(date)="+year+" and dataType='"+ConstantsInfo.DayDataType+"' ORDER BY date desc limit 1";
		System.out.println(selectSql);
		return super.getSingleQuery(selectSql, null);
	}
	
	//查询每周的收盘价
	//SELECT closingPrice FROM day_stock_sh000001 where WEEK(date) = 52 and YEAR(date) = 2013 ORDER BY date desc limit 1;
	public float getClosingPriceOfWeek(String stockFullId,int week,int year) throws IOException, ClassNotFoundException, SQLException
	{
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		String selectSql = "select closingPrice from "+ stockTable+" where WEEK(date)="+week+" and YEAR(date)="+year+" and dataType='"+ConstantsInfo.DayDataType+"' ORDER BY date desc limit 1";
		//System.out.println(selectSql);
		return super.getSingleFloatQuery(selectSql, null);
	}
		
	
	//查询每周的开盘价
	//SELECT openingPrice FROM day_stock_sh000001 where WEEK(date) = 51 and YEAR(date) = 2013 ORDER BY id limit 1;
	public float getOpeningPriceOfWeek(String stockFullId,int week,int year) throws IOException, ClassNotFoundException, SQLException
	{
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		String selectSql = "select openingPrice from "+ stockTable+" where WEEK(date)="+week+" and YEAR(date)="+year+" and dataType='"+ConstantsInfo.DayDataType+"' ORDER BY date limit 1";
		//System.out.println(selectSql);
		return super.getSingleFloatQuery(selectSql, null);
	}
	
	//查询每周最高 最低价
	//SELECT MAX(highestPrice) as maxNUM,MIN(lowestPrice) as minNUM FROM day_stock_sh000001 where WEEK(date) = 49 and YEAR(date) = 2013;
	public float getHighestPriceOfWeek(String stockFullId,int week,int year) throws IOException, ClassNotFoundException, SQLException
	{
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		String selectSql = "select MAX(highestPrice) from "+ stockTable+" where WEEK(date)="+week+" and YEAR(date)="+year+" and dataType='"+ConstantsInfo.DayDataType+"'";
		//System.out.println(selectSql);
		return super.getSingleFloatQuery(selectSql, null);
	}
	
	//查询每周 最低价
	public float getLowestPriceOfWeek(String stockFullId,int week,int year) throws IOException, ClassNotFoundException, SQLException
	{
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		String selectSql = "select MIN(lowestPrice) from "+ stockTable+" where WEEK(date)="+week+" and YEAR(date)="+year+" and dataType='"+ConstantsInfo.DayDataType+"'";
		//System.out.println(selectSql);
		return super.getSingleFloatQuery(selectSql, null);
	}
	
	
	//查询每周,每月，每年的时间
	//SELECT closingPrice FROM day_stock_sh000001 where WEEK(date) = 52 and YEAR(date) = 2013 ORDER BY id desc limit 1;
	public String getDateFromDate(String stockFullId,int num,int year,int dataType) throws IOException, ClassNotFoundException, SQLException
	{
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		String selectSql=null;
		switch(dataType)
		{
		case ConstantsInfo.DayDataType: //day
			selectSql= "select date from "+ stockTable+" where dayofyear(date)="+num+" and YEAR(date)="+year+" and dataType='"+ConstantsInfo.DayDataType+"' ORDER BY date desc limit 1";
			break;
		case ConstantsInfo.WeekDataType://week
			selectSql= "select date from "+ stockTable+" where WEEK(date)="+num+" and YEAR(date)="+year+" and dataType='"+ConstantsInfo.DayDataType+"' ORDER BY date desc limit 1";
			break;
		case ConstantsInfo.MonthDataType://month
			selectSql = "select date from "+ stockTable+" where Month(date)="+num+" and YEAR(date)="+year+" and dataType='"+ConstantsInfo.DayDataType+"' ORDER BY date desc limit 1";
			break;
		case ConstantsInfo.SeasonDataType://season
			String seasonStart=null,seasonEnd = null;
			switch(num)
			{
				case 1:
					seasonStart=year+"-01-01";
					seasonEnd=year+"-03-31";
					break;
				case 2:
					seasonStart=year+"-04-01";
					seasonEnd=year+"-06-30";
					break;
				case 3:
					seasonStart=year+"-07-01";
					seasonEnd=year+"-09-30";
					break;
				case 4:
					seasonStart=year+"-10-01";
					seasonEnd=year+"-12-31";
					break;
			}
			//从月表的日期中查找更快
			selectSql = "select date from "+ stockTable+" where date>='"+seasonStart+"' and date<='"+seasonEnd+"' and dataType='"+ConstantsInfo.MonthDataType+"' ORDER BY date desc limit 1";
			break;
		case ConstantsInfo.YearDataType://year
			String yearStart=null,yearEnd = null;
			yearStart=year+"-01-01";
			yearEnd=year+"-12-31";
			//从季表的日期中查找更快
			selectSql = "select date from "+ stockTable+" where date>='"+yearStart+"' and date<='"+yearEnd+"' and dataType='"+ConstantsInfo.SeasonDataType+"' ORDER BY date desc limit 1";
		}
		
		if(ConstantsInfo.DEBUG)
		{
			System.out.println(selectSql);
		}
		
		return super.getSingleQuery(selectSql, null);
	}
	
	
	//查询每周,每月，每年的收盘价
	//SELECT closingPrice FROM day_stock_sh000001 where WEEK(date) = 52 and YEAR(date) = 2013 ORDER BY date desc limit 1;
	public float getClosingPriceFromDate(String stockFullId,int num,int year,int dataType) throws IOException, ClassNotFoundException, SQLException
	{
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		String selectSql=null;
		switch(dataType)
		{
		case ConstantsInfo.DayDataType: //day
			break;
		case ConstantsInfo.WeekDataType://week
			selectSql= "select closingPrice from "+ stockTable+" where WEEK(date) ="+num+" and YEAR(date) = "+year+" and dataType='"+ConstantsInfo.DayDataType+"' ORDER BY date desc limit 1";
			break;
		case ConstantsInfo.MonthDataType://month
			selectSql = "select closingPrice from "+ stockTable+" where Month(date) ="+num+" and YEAR(date) = "+year+" and dataType='"+ConstantsInfo.DayDataType+"' ORDER BY date desc limit 1";
			break;
		case ConstantsInfo.SeasonDataType://season
			String seasonStart=null,seasonEnd = null;
			switch(num)
			{
				case 1:
					seasonStart=year+"-01-01";
					seasonEnd=year+"-03-31";
					break;
				case 2:
					seasonStart=year+"-04-01";
					seasonEnd=year+"-06-30";
					break;
				case 3:
					seasonStart=year+"-07-01";
					seasonEnd=year+"-09-30";
					break;
				case 4:
					seasonStart=year+"-10-01";
					seasonEnd=year+"-12-31";
					break;
			}
			selectSql = "select closingPrice from "+ stockTable+" where date>='"+seasonStart+"' and date<='"+seasonEnd+"' and dataType='"+ConstantsInfo.MonthDataType+"' ORDER BY date desc limit 1";
			break;
		case ConstantsInfo.YearDataType://year
			String yearStart=null,yearEnd = null;
			yearStart=year+"-01-01";
			yearEnd=year+"-12-31";
			selectSql = "select closingPrice from "+ stockTable+" where date>='"+yearStart+"' and date<='"+yearEnd+"' and dataType='"+ConstantsInfo.SeasonDataType+"' ORDER BY date desc limit 1";
		}
		
		if(ConstantsInfo.DEBUG)
		{
			System.out.println(selectSql);
		}

		return super.getSingleFloatQuery(selectSql, null);
	}
	
	
	//查询每周,每月，每年的开盘价
	//SELECT OpeningPrice FROM day_stock_sh000001 where WEEK(date) = 52 and YEAR(date) = 2013 ORDER BY date desc limit 1;
	public float getOpeningPriceFromDate(String stockFullId,int num,int year,int dataType) throws IOException, ClassNotFoundException, SQLException
	{
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		String selectSql=null;
		switch(dataType)
		{
		case ConstantsInfo.DayDataType: //day
			break;
		case ConstantsInfo.WeekDataType://week
			selectSql= "select openingPrice from "+ stockTable+" where WEEK(date) ="+num+" and YEAR(date) = "+year+" and dataType='"+ConstantsInfo.DayDataType+"' ORDER BY date limit 1";
			break;
		case ConstantsInfo.MonthDataType://month
			selectSql = "select openingPrice from "+ stockTable+" where Month(date) ="+num+" and YEAR(date) = "+year+" and dataType='"+ConstantsInfo.DayDataType+"' ORDER BY date limit 1";
			break;
		case ConstantsInfo.SeasonDataType://season
			String seasonStart=null,seasonEnd = null;
			switch(num)
			{
				case 1:
					seasonStart=year+"-01-01";
					seasonEnd=year+"-03-31";
					break;
				case 2:
					seasonStart=year+"-04-01";
					seasonEnd=year+"-06-30";
					break;
				case 3:
					seasonStart=year+"-07-01";
					seasonEnd=year+"-09-30";
					break;
				case 4:
					seasonStart=year+"-10-01";
					seasonEnd=year+"-12-31";
					break;
			}
			selectSql = "select openingPrice from "+ stockTable+" where date>='"+seasonStart+"' and date<='"+seasonEnd+"' and dataType='"+ConstantsInfo.MonthDataType+"' ORDER BY date limit 1";
			break;
		case ConstantsInfo.YearDataType://year
			String yearStart=null,yearEnd = null;
			yearStart=year+"-01-01";
			yearEnd=year+"-12-31";
			selectSql = "select openingPrice from "+ stockTable+" where date>='"+yearStart+"' and date<='"+yearEnd+"' and dataType='"+ConstantsInfo.SeasonDataType+"' ORDER BY date limit 1";
		}
		if(ConstantsInfo.DEBUG)
		{
			System.out.println(selectSql);
		}
		return super.getSingleFloatQuery(selectSql, null);
	}
	
	//查询每周,每月，每年的最高价
	//SELECT MAX(highestPrice) FROM day_stock_sh000001 where WEEK(date) = 52 and YEAR(date) = 2013;
	public float getHighestPriceFromDate(String stockFullId,int num,int year,int dataType) throws IOException, ClassNotFoundException, SQLException
	{
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		String selectSql=null;
		switch(dataType)
		{
		case ConstantsInfo.DayDataType: //day
			break;
		case ConstantsInfo.WeekDataType://week
			selectSql= "select MAX(highestPrice) from "+ stockTable+" where WEEK(date) ="+num+" and YEAR(date) = "+year+" and dataType='"+ConstantsInfo.DayDataType+"'";//type=1
			break;
		case ConstantsInfo.MonthDataType://month
			selectSql = "select MAX(highestPrice) from "+ stockTable+" where Month(date) ="+num+" and YEAR(date) = "+year+" and dataType='"+ConstantsInfo.DayDataType+"'";//type=1
			break;
		case ConstantsInfo.SeasonDataType://season
			String seasonStart=null,seasonEnd = null;
			switch(num)
			{
				case 1:
					seasonStart=year+"-01-01";
					seasonEnd=year+"-03-31";
					break;
				case 2:
					seasonStart=year+"-04-01";
					seasonEnd=year+"-06-30";
					break;
				case 3:
					seasonStart=year+"-07-01";
					seasonEnd=year+"-09-30";
					break;
				case 4:
					seasonStart=year+"-10-01";
					seasonEnd=year+"-12-31";
					break;
			}
			selectSql = "select MAX(highestPrice) from "+ stockTable+" where date>='"+seasonStart+"' and date<='"+seasonEnd+"' and dataType='"+ConstantsInfo.MonthDataType+"'";//type=3
			break;
		case ConstantsInfo.YearDataType://year
			String yearStart=null,yearEnd = null;
			yearStart=year+"-01-01";
			yearEnd=year+"-12-31";
			selectSql = "select MAX(highestPrice) from "+ stockTable+" where date>='"+yearStart+"' and date<='"+yearEnd+"' and dataType='"+ConstantsInfo.SeasonDataType+"'";//type=4
		}
		
		if(ConstantsInfo.DEBUG)
		{
			System.out.println(selectSql);
		}
		return super.getSingleFloatQuery(selectSql, null);
	}
	
	//查询每周,每月，每年的最低价
	//SELECT MAX(highestPrice) FROM day_stock_sh000001 where WEEK(date) = 52 and YEAR(date) = 2013;
	public float getLowestPriceFromDate(String stockFullId,int num,int year,int dataType) throws IOException, ClassNotFoundException, SQLException
	{
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		String selectSql=null;
		switch(dataType)
		{
		case ConstantsInfo.DayDataType: //day
			break;
		case ConstantsInfo.WeekDataType://week
			selectSql= "select MIN(lowestPrice) from "+ stockTable+" where WEEK(date) ="+num+" and YEAR(date) = "+year+" and dataType='"+ConstantsInfo.DayDataType+"'";//type=1
			break;
		case ConstantsInfo.MonthDataType://month
			selectSql = "select MIN(lowestPrice) from "+ stockTable+" where Month(date) ="+num+" and YEAR(date) = "+year+" and dataType='"+ConstantsInfo.DayDataType+"'";//type=1
			break;
		case ConstantsInfo.SeasonDataType://season
			String seasonStart=null,seasonEnd = null;
			switch(num)
			{
				case 1:
					seasonStart=year+"-01-01";
					seasonEnd=year+"-03-31";
					break;
				case 2:
					seasonStart=year+"-04-01";
					seasonEnd=year+"-06-30";
					break;
				case 3:
					seasonStart=year+"-07-01";
					seasonEnd=year+"-09-30";
					break;
				case 4:
					seasonStart=year+"-10-01";
					seasonEnd=year+"-12-31";
					break;
			}
			selectSql = "select MIN(lowestPrice) from "+ stockTable+" where date>='"+seasonStart+"' and date<='"+seasonEnd+"' and dataType='"+ConstantsInfo.MonthDataType+"'";//type=3
			break;
		case ConstantsInfo.YearDataType://year
			String yearStart=null,yearEnd = null;
			yearStart=year+"-01-01";
			yearEnd=year+"-12-31";
			selectSql = "select MIN(lowestPrice) from "+ stockTable+" where date>='"+yearStart+"' and date<='"+yearEnd+"' and dataType='"+ConstantsInfo.SeasonDataType+"'";//type=4
		}
		
		if(ConstantsInfo.DEBUG)
		{
			System.out.println(selectSql);
		}
		return super.getSingleFloatQuery(selectSql, null);
	}
	
	
	
	/**
	 * 每年每季度每月每周的ma5,ma10,date是指
	 * @param stockFullId
	 * @param date 当前查询的时间
	 * @param dataType 数据类型
	 * @param maType
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public float getMaValueFromDate(String stockFullId,String date,int dataType,int maType) throws IOException, ClassNotFoundException, SQLException
	{
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;	
		
		String selectSql ="select count(*) from "+ stockTable+" where date<='"+date+"' and dataType="+dataType;
		int count=super.getSingleIntQuery(selectSql, null);
		if((count<5) && (maType==5) || (count<10) && (maType==10) )
		{
				return 0;
		}
		//存在停牌，不能限定前面时间
		selectSql = "select AVG(closingPrice) from (select closingPrice from "+ stockTable+" where date<='"+date+"' and dataType="+dataType+" order by date desc limit "+maType+") as tmpTable";
		if(ConstantsInfo.DEBUG)
		{
			System.out.println(selectSql);
		}
		return super.getSingleFloatQuery(selectSql, null);	
	}
			
	//更新ma5 ma10
	public int insertMAtoDayStock(String stockFullId,int dataType,float ma5Price,float md10Price,String date) throws IOException, ClassNotFoundException, SQLException
	{
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		String updateSql = "update "+stockTable+" set ma5Price='"+ma5Price+"',ma10Price='"+md10Price+"' where date='"+date+"' and dataType="+dataType+"";
		//System.out.println(updateSql);
		return super.saveOrUpdate(updateSql);
	}
	//更新涨幅比 振幅
	public int insertRangeAndAmplitudetoDayStock(String stockFullId,int dataType,float range, float amplitude,String date) throws IOException, ClassNotFoundException, SQLException
	{
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		String updateSql = "update "+stockTable+" set range='"+range+"', amplitude='"+amplitude+"'  where date='"+date+"' and dataType="+dataType+"";
		//System.out.println(updateSql);
		return super.saveOrUpdate(updateSql);
	}
	
	
	//将刚导入的数据新表dataType由0改为1
	public int updateDayTypeForNewStock(String stockFullId) throws IOException, ClassNotFoundException, SQLException
	{
		String updateSql=null;
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		
		updateSql = "update "+stockTable+" set dataType='"+ConstantsInfo.DayDataType+"'  where dataType='"+ConstantsInfo.UnComputeDataType+"'";
		//System.out.println(updateSql);
		return super.saveOrUpdate(updateSql);
	}
	
	//将刚导入的数据dataType由0改为1
	public int updateDayTypeForLoadStock(String stockFullId,String date) throws IOException, ClassNotFoundException, SQLException
	{
		String updateSql=null;
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		
		updateSql = "update "+stockTable+" set dataType='"+ConstantsInfo.DayDataType+"'  where date='"+date+"' and dataType='"+ConstantsInfo.UnComputeDataType+"'";
		//System.out.println(updateSql);
		return super.saveOrUpdate(updateSql);
	}
	
	//更新涨ma5 ma10 幅比 振幅
	public int insertMaAndRangeAndAmplitudetoDayStock(String stockFullId,int dataType,float ma5Price,float md10Price,float range, float amplitude,String date) throws IOException, ClassNotFoundException, SQLException
	{
		String updateSql=null;
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		
		updateSql = "update "+stockTable+" set ma5Price='"+ma5Price+"',ma10Price='"+md10Price+"',range='"+range+"', amplitude='"+amplitude+"'  where date='"+date+"' and dataType='"+dataType+"'";
		//System.out.println(updateSql);
		return super.saveOrUpdate(updateSql);
	}
	
	
	//插入数据StockData
	public int insertStockData(StockData dataStock,String stockFullId) throws IOException, ClassNotFoundException, SQLException
	{
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		String insertSql = "insert into "+stockTable;
		//System.out.println(insertSql);
		return super.saveOrUpdate(
				 insertSql+" values(?,?,?,?,?,?,?,?,?,?,?,?,?)", 
	        		0,dataStock.getDate(),dataStock.getOpeningPrice(),dataStock.getHighestPrice(),dataStock.getLowestPrice(),dataStock.getClosingPrice(),dataStock.getStockVolume(),dataStock.getDailyTurnover(),0,0,0,0,dataStock.getDataType());  	
	}

	
	//得到最近一段时间内周 ，不能跨年
	public List<Integer> getUnCalculationYear(String stockFullId) throws SQLException
	{
	
		List<Integer> dates=new ArrayList<Integer>();
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		String selectSql = null;
		//排好序
		selectSql="select distinct Year(date) from "+stockTable+" where dataType='"+ConstantsInfo.UnComputeDataType+"' ORDER BY date";
		dates=getIntQuery(selectSql,null);
		return dates;
	}
	
	public List<String> getUnCalculationDay(String stockFullId) throws SQLException
	{
	
		List<String> dates=new ArrayList<String>();
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		String selectSql = null;
		//排好序
		selectSql="select date from "+stockTable+" where dataType='"+ConstantsInfo.UnComputeDataType+"' ORDER BY date";
		dates=getQuery(selectSql,null);
		return dates;
	}
	
	/*
	public List<String> getUnCalculationDay(String stockFullId,String today) throws SQLException
	{
	
		List<String> dates=new ArrayList<String>();
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		String selectSql = null;
		//排好序
		selectSql="select date from "+stockTable+" where date>='"+today+"' and dataType='"+ConstantsInfo.DayDataType+"' ORDER BY date";
		dates=getQuery(selectSql,null);
		return dates;
	}
	*/
	
	//得到最近一段时间内周 ，不能跨年
	public List<Integer> getUnCalculationWeek(String stockFullId,int  year) throws SQLException
	{
	
		List<Integer> dates=new ArrayList<Integer>();
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		String selectSql = null;
		
		 //排好序
		selectSql="select distinct WEEK(date) from "+stockTable+" where dataType='"+ConstantsInfo.UnComputeDataType+"' and Year(date)='"+year + "' ORDER BY date";
		//System.out.println(selectSql);
		
		dates=getIntQuery(selectSql,null);
		return dates;	
		
	}
	
	//得到最近一段时间内周 ，不能跨年
	public List<Integer> getUnCalculationMonth(String stockFullId,int year) throws SQLException
	{
	
		List<Integer> dates=new ArrayList<Integer>();
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		String selectSql = null;
		
		 //排好序
		selectSql="select distinct MONTH(date) from "+stockTable+" where dataType='"+ConstantsInfo.UnComputeDataType+"' and Year(date)='"+year + "' ORDER BY date";
		dates=getIntQuery(selectSql,null);
		return dates;	
	}
	
	
	
	
	/** 计算当时日数据
	 * @param stockFullId
	 * @param listDate
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public void callDayStockDataFromDate(String stockFullId,List<String> listDate) throws IOException, ClassNotFoundException, SQLException
	{

		String date;
		for(Iterator it = listDate.iterator();it.hasNext();)
		{
			date=(String)it.next();	
			//刚入库为 0 ，更新为DayDataType
			updateDayTypeForLoadStock(stockFullId,date);
			//计算ma 涨幅
			calStockAppendData(stockFullId,date,ConstantsInfo.DayDataType);			
		}
			
	}
	
	//只需要计算ma ma10
	public void calAllHistoryDayStockData(String stockFullId,List<TimeStock> listTimeStock) throws IOException, ClassNotFoundException, SQLException 
	{
		
		String date;
		for(Iterator it = listTimeStock.iterator();it.hasNext();)
		{
			TimeStock ts=(TimeStock)it.next();
			System.out.println("----------------------------------------");	
			System.out.println("year:"+ts.getYear());
			//System.out.println("season:"+ts.getSeasonNum());
			//System.out.println("month:"+ts.getMonthNum());
			//System.out.println("week:"+ts.getWeekNum());
			System.out.println("day:"+ts.getDayNum());	
			
			int year=Integer.parseInt(ts.getYear());	
			int	dayNum=ts.getDayNum();
		//	if(year!=2014)
		//		continue;
			//for(int i=1;i<=20;i++)
			for(int i=1;i<=dayNum;i++)		
			{
				date=getDateFromDate(stockFullId,i,year,ConstantsInfo.DayDataType);
				if(date==null || date.equals(""))//有可能停牌
					continue;
				//刚入库为 0 ，更新为DayDataType
				updateDayTypeForLoadStock(stockFullId,date);
				//计算ma 涨幅
				calStockAppendData(stockFullId,date,ConstantsInfo.DayDataType);	
				;	

			}
		}
				
	}
	
	
	public void calAllHistoryFirstLastWeekStockData(String stockFullId,int year) throws IOException, ClassNotFoundException, SQLException
	{
		float closingPriceForWeek=0,openingPriceForWeek = 0;
		float highestPriceForWeek=0,lowestPriceForWeek=0;
		
		String dateForweek = null;
		Date dateSqlForweek = null;
		StockData sdata;
		List<StockData> listDayStockOfFirstWeek = new ArrayList<StockData>(); 
		List<StockData> listDayStockOfLastWeek = new ArrayList<StockData>(); 

		String strYear=String.valueOf(year);
		String lastYear=String.valueOf(year-1);
		int priYearWeekNum=getStockMonthWeekDaysOfYear(stockFullId,lastYear,ConstantsInfo.WeekDataType);
		System.out.println("curYear:"+strYear+"--lastYear:"+lastYear+"--lastYear weeks:"+priYearWeekNum);
		
		listDayStockOfFirstWeek=getAllDataOfWeek(stockFullId,0,year);//当前年第一周
		listDayStockOfLastWeek=getAllDataOfWeek(stockFullId,priYearWeekNum,year-1);//上一年最后一周
		System.out.println("第0周");
	//	System.out.println("listDayStockOfFirstWeek.size():"+listDayStockOfFirstWeek.size()+" listDayStockOfLastWeek.size:"+ listDayStockOfLastWeek.size());

		int curYear = 0,curWeek = 0;
		
		if((listDayStockOfFirstWeek!=null && listDayStockOfFirstWeek.size()>0) && (listDayStockOfLastWeek!=null && listDayStockOfLastWeek.size()>0))
		{
			System.out.println("one");
			dateForweek=getDateFromFirstAndLastWeek(listDayStockOfFirstWeek,listDayStockOfLastWeek);
			if(dateForweek==null || dateForweek.equals(""))//有可能停牌
			{				
				return;
			}				
			openingPriceForWeek=getOpeningPriceFromFirstAndLastWeek(listDayStockOfFirstWeek,listDayStockOfLastWeek);
			highestPriceForWeek=getHighestPriceFromFirstAndLastWeek(listDayStockOfFirstWeek,listDayStockOfLastWeek);
			lowestPriceForWeek=getLowestPriceFromFirstAndLastWeek(listDayStockOfFirstWeek,listDayStockOfLastWeek);
			closingPriceForWeek=getClosingPriceFromFirstAndLastWeek(listDayStockOfFirstWeek,listDayStockOfLastWeek);
			curYear=year;
			curWeek=0;
		}
		else if((listDayStockOfFirstWeek==null || listDayStockOfFirstWeek.size()==0) && (listDayStockOfLastWeek!=null && listDayStockOfLastWeek.size()>0))
		{
			System.out.println("two");
			dateForweek=getDateFromDate(stockFullId,priYearWeekNum,year-1,ConstantsInfo.WeekDataType);
			if(dateForweek==null || dateForweek.equals(""))//有可能停牌
			{				
				return;
			}
			openingPriceForWeek=getOpeningPriceFromDate(stockFullId,priYearWeekNum,year-1,ConstantsInfo.WeekDataType);
			highestPriceForWeek=getHighestPriceFromDate(stockFullId,priYearWeekNum,year-1,ConstantsInfo.WeekDataType);
			lowestPriceForWeek=getLowestPriceFromDate(stockFullId,priYearWeekNum,year-1,ConstantsInfo.WeekDataType);
			closingPriceForWeek=getClosingPriceFromDate(stockFullId,priYearWeekNum,year-1,ConstantsInfo.WeekDataType);
			curYear=year-1;
			curWeek=priYearWeekNum;
		}
		else if((listDayStockOfFirstWeek!=null && listDayStockOfFirstWeek.size()>0) && (listDayStockOfLastWeek==null || listDayStockOfLastWeek.size()==0))
		{
			System.out.println("three");
			dateForweek=getDateFromDate(stockFullId,0,year,ConstantsInfo.WeekDataType);
			if(dateForweek==null || dateForweek.equals(""))//有可能停牌
			{
				return;
			}
			openingPriceForWeek=getOpeningPriceFromDate(stockFullId,0,year,ConstantsInfo.WeekDataType);
			highestPriceForWeek=getHighestPriceFromDate(stockFullId,0,year,ConstantsInfo.WeekDataType);
			lowestPriceForWeek=getLowestPriceFromDate(stockFullId,0,year,ConstantsInfo.WeekDataType);
			closingPriceForWeek=getClosingPriceFromDate(stockFullId,0,year,ConstantsInfo.WeekDataType);	
			curYear=year;
			curWeek=0;
		}
		else if((listDayStockOfFirstWeek==null || listDayStockOfFirstWeek.size()==0) && (listDayStockOfLastWeek==null || listDayStockOfLastWeek.size()==0))
		{
			System.out.println("four");
			return;
		}
		
		if(openingPriceForWeek==0 || highestPriceForWeek==0 || lowestPriceForWeek==0 || closingPriceForWeek==0)
			return;
	
		dateSqlForweek=java.sql.Date.valueOf(dateForweek);
	
		sdata=new StockData(dateSqlForweek,openingPriceForWeek,highestPriceForWeek,lowestPriceForWeek,closingPriceForWeek,0,0,0,0,ConstantsInfo.WeekDataType,0,0);

		System.out.println("curYear:"+curYear+"curWeek:"+curWeek);
		int weekId=getIdAndExistDataTimeType(stockFullId,curYear,0,curWeek,"",ConstantsInfo.WeekDataType);
		if(weekId==0)//未查找到 插入
		{
			insertStockData(sdata,stockFullId);		
		}
		else //更新
		{
			updateStockDataTimeType(sdata,stockFullId,weekId,ConstantsInfo.WeekDataType);
		}
				
		calStockAppendData(stockFullId,dateForweek,ConstantsInfo.WeekDataType);	
					
	}
	
	public void calWeekStockDataFromDate(String stockFullId,List<Integer> weekList,int year) throws IOException, ClassNotFoundException, SQLException
	{
		float closingPriceForWeek=0,openingPriceForWeek = 0;
		float highestPriceForWeek=0,lowestPriceForWeek=0;
		String dateForweek = null;
		Date dateSqlForweek = null;
		StockData sdata;
		
		//int year=2014;
		
		for(Iterator it = weekList.iterator();it.hasNext();)
		{
			int weekCount=(Integer)it.next();
			System.out.println("weekCount"+weekCount);
			openingPriceForWeek=getOpeningPriceFromDate(stockFullId,weekCount,year,ConstantsInfo.WeekDataType);
			highestPriceForWeek=getHighestPriceFromDate(stockFullId,weekCount,year,ConstantsInfo.WeekDataType);
			lowestPriceForWeek=getLowestPriceFromDate(stockFullId,weekCount,year,ConstantsInfo.WeekDataType);
			closingPriceForWeek=getClosingPriceFromDate(stockFullId,weekCount,year,ConstantsInfo.WeekDataType);			
			
			if(openingPriceForWeek==0 || highestPriceForWeek==0 || lowestPriceForWeek==0 || closingPriceForWeek==0)
				continue;					
			dateForweek=getDateFromDate(stockFullId,weekCount,year,ConstantsInfo.WeekDataType);
			if(dateForweek==null || dateForweek.equals(""))//有可能停牌
				continue;				
			dateSqlForweek=java.sql.Date.valueOf(dateForweek);	
			sdata=new StockData(dateSqlForweek,openingPriceForWeek,highestPriceForWeek,lowestPriceForWeek,closingPriceForWeek,0,0,0,0,ConstantsInfo.WeekDataType,0,0);
		
			int weekId=getIdAndExistDataTimeType(stockFullId,year,0,weekCount,"",ConstantsInfo.WeekDataType);
			if(weekId==0)
			{
				
				insertStockData(sdata,stockFullId);		
			}
			else
			{
				
				updateStockDataTimeType(sdata,stockFullId,weekId,ConstantsInfo.WeekDataType);
			}
			
			calStockAppendData(stockFullId,dateForweek,ConstantsInfo.WeekDataType);		

		}
		
	}
	
	
	public void calMonthStockDataFromDate(String stockFullId,List<Integer> monthList,int year) throws IOException, ClassNotFoundException, SQLException
	{
		float openingPrice=0,closingPrice = 0;
		float highestPrice=0,lowestPrice=0;

		String date = null;
		Date dateSql = null;
		StockData sdata;
	//	int year=2014;
		
		for(Iterator it = monthList.iterator();it.hasNext();)
		{
			int monthCount=(Integer)it.next();
			
			openingPrice=getOpeningPriceFromDate(stockFullId,monthCount,year,ConstantsInfo.MonthDataType);
			highestPrice=getHighestPriceFromDate(stockFullId,monthCount,year,ConstantsInfo.MonthDataType);
			lowestPrice=getLowestPriceFromDate(stockFullId,monthCount,year,ConstantsInfo.MonthDataType);
			closingPrice=getClosingPriceFromDate(stockFullId,monthCount,year,ConstantsInfo.MonthDataType);
			if(closingPrice==0 || openingPrice==0 || highestPrice==0 || lowestPrice==0)
				continue;
			date=getDateFromDate(stockFullId,monthCount,year,ConstantsInfo.MonthDataType);
			if(date==null || date.equals(""))//有可能停牌
				continue;
			dateSql=java.sql.Date.valueOf(date);	
			sdata=new StockData(dateSql,openingPrice,highestPrice,lowestPrice,closingPrice,0,0,0,0,ConstantsInfo.MonthDataType,0,0);
			
			int monthId=getIdAndExistDataTimeType(stockFullId,year,monthCount,0,"",ConstantsInfo.MonthDataType);
			if(monthId==0)
			{
				insertStockData(sdata,stockFullId);		
			}
			else
			{
				updateStockDataTimeType(sdata,stockFullId,monthId,ConstantsInfo.MonthDataType);
			}
				
			calStockAppendData(stockFullId,date,ConstantsInfo.MonthDataType);		

		}
			
	}
	
	public void calSeasonStockDataFromDate(String stockFullId,List<Integer> seasonList,int year) throws IOException, ClassNotFoundException, SQLException
	{
		float openingPrice=0,closingPrice = 0;
		float highestPrice=0,lowestPrice=0;

		String date = null;
		Date dateSql = null;
		StockData sdata;
	//	int year=2014;
		
		for(Iterator it = seasonList.iterator();it.hasNext();)
		{
			int seasonCount=(Integer)it.next();
			openingPrice=getOpeningPriceFromDate(stockFullId,seasonCount,year,ConstantsInfo.SeasonDataType);
			highestPrice=getHighestPriceFromDate(stockFullId,seasonCount,year,ConstantsInfo.SeasonDataType);
			lowestPrice=getLowestPriceFromDate(stockFullId,seasonCount,year,ConstantsInfo.SeasonDataType);
			closingPrice=getClosingPriceFromDate(stockFullId,seasonCount,year,ConstantsInfo.SeasonDataType);
			if(closingPrice==0 || openingPrice==0 || highestPrice==0 || lowestPrice==0)
				continue;
			date=getDateFromDate(stockFullId,seasonCount,year,ConstantsInfo.SeasonDataType);
			if(date==null || date.equals(""))//有可能停牌
				continue;
			dateSql=java.sql.Date.valueOf(date);	
			sdata=new StockData(dateSql,openingPrice,highestPrice,lowestPrice,closingPrice,0,0,0,0,ConstantsInfo.SeasonDataType,0,0);
			
			//month=i*3+1;
			int month=3*seasonCount-1;
			int seasonId=getIdAndExistDataTimeType(stockFullId,year,month,0,"",ConstantsInfo.SeasonDataType);
			if(seasonId==0)
			{
				insertStockData(sdata,stockFullId);		
			}
			else
			{
				updateStockDataTimeType(sdata,stockFullId,seasonId,ConstantsInfo.SeasonDataType);
			}				
			
			calStockAppendData(stockFullId,date,ConstantsInfo.SeasonDataType);	
		}
	}
	
	public void calAllHistoryWeekStockData(String stockFullId,List<TimeStock> listTimeStock) throws IOException, ClassNotFoundException, SQLException
	{
		int weekCount;
		float closingPriceForWeek=0,openingPriceForWeek = 0;
		float highestPriceForWeek=0,lowestPriceForWeek=0;
		
		String dateForweek = null;
		Date dateSqlForweek = null;
		StockData sdata;
		
		Calendar aTime=Calendar.getInstance();
		int curYear =  aTime.get(Calendar.YEAR);//得到年
		
		for(Iterator it = listTimeStock.iterator();it.hasNext();)
		{
			TimeStock ts=(TimeStock)it.next();
			System.out.println("----------------------------------------");	
			System.out.println("year:"+ts.getYear());
			//System.out.println("season:"+ts.getSeasonNum());
			//System.out.println("month:"+ts.getMonthNum());
			System.out.println("week:"+ts.getWeekNum());
			//System.out.println("day:"+ts.getDayNum());	
			
			int year=Integer.parseInt(ts.getYear());	
			int	weekNum=ts.getWeekNum();
		
		//	if(year!=2011 && year!=2012 && year!=2013 && year!=2014)
		//		continue;		
			
			//第一周
			calAllHistoryFirstLastWeekStockData(stockFullId,year);
			
			//最后一周不计算，留在下一年第一周
			//最后一年最后一周得计算
			if(year == curYear)
				weekNum=weekNum+1;
			
			//for(weekCount=1;weekCount<2;weekCount++)
			for(weekCount=1;weekCount<weekNum;weekCount++)
			{
				//System.out.println("第"+weekCount+"周");				
				
				openingPriceForWeek=getOpeningPriceFromDate(stockFullId,weekCount,year,ConstantsInfo.WeekDataType);
				highestPriceForWeek=getHighestPriceFromDate(stockFullId,weekCount,year,ConstantsInfo.WeekDataType);
				lowestPriceForWeek=getLowestPriceFromDate(stockFullId,weekCount,year,ConstantsInfo.WeekDataType);
				closingPriceForWeek=getClosingPriceFromDate(stockFullId,weekCount,year,ConstantsInfo.WeekDataType);			
				
				if(openingPriceForWeek==0 || highestPriceForWeek==0 || lowestPriceForWeek==0 || closingPriceForWeek==0)
					continue;					
				dateForweek=getDateFromDate(stockFullId,weekCount,year,ConstantsInfo.WeekDataType);
				if(dateForweek==null || dateForweek.equals(""))//有可能停牌
					continue;				
				dateSqlForweek=java.sql.Date.valueOf(dateForweek);	
				sdata=new StockData(dateSqlForweek,openingPriceForWeek,highestPriceForWeek,lowestPriceForWeek,closingPriceForWeek,0,0,0,0,ConstantsInfo.WeekDataType,0,0);
			
				int weekId=getIdAndExistDataTimeType(stockFullId,year,0,weekCount,"",ConstantsInfo.WeekDataType);
				if(weekId==0)
				{
					insertStockData(sdata,stockFullId);		
				}
				else
				{
					updateStockDataTimeType(sdata,stockFullId,weekId,ConstantsInfo.WeekDataType);
				}
				
				calStockAppendData(stockFullId,dateForweek,ConstantsInfo.WeekDataType);	
				 		
			}
			
		}
	}
	
/*
	public void calAllHistoryWeekStockData1(String stockFullId,List<TimeStock> listTimeStock) throws IOException, ClassNotFoundException, SQLException
	{
		int weekCount;
		float closingPriceForWeek=0,openingPriceForWeek = 0;
		float highestPriceForWeek=0,lowestPriceForWeek=0;
		float ma5PriceForWeek=0,ma10PriceForWeek=0;
		String dateForweek = null;
		Date dateSqlForweek = null;
		List<StockData> listDayStockOfWeek = new ArrayList<StockData>(); 
		List<StockData> listDayStockOfFirstWeek = new ArrayList<StockData>(); 
		List<StockData> listDayStockOfLastWeek = new ArrayList<StockData>(); 
		int priYearWeekNum=0;
		StockData sdata;
		int firstYearflag=1;
		for(Iterator it = listTimeStock.iterator();it.hasNext();)
		{
			TimeStock ts=(TimeStock)it.next();
			System.out.println("year:"+ts.getYear());
			//System.out.println("season:"+ts.getSeasonNum());
			//System.out.println("month:"+ts.getMonthNum());
			System.out.println("week:"+ts.getWeekNum());
			//System.out.println("day:"+ts.getDayNum());	
			
			int year=Integer.parseInt(ts.getYear());	
			int	weekNum=ts.getWeekNum();
			System.out.println("----------------------------------------");	
			System.out.println("year:"+year+"week:"+weekNum);	
			System.out.println("priYearWeekNum:"+priYearWeekNum);
		
			if(year!=2013 && year!=2014)
				continue;			
		
			listDayStockOfFirstWeek=getAllDataOfWeek(stockFullId,0,year);//当前年第一周
			listDayStockOfLastWeek=getAllDataOfWeek(stockFullId,priYearWeekNum,year-1);//上一年最后一周
			
	//计算第0周与上一年最后一周	
		//	System.out.println("first week size:"+listDayStockOfFirstWeek.size() +"prive year last week size:"+listDayStockOfLastWeek.size() );	
			if(listDayStockOfFirstWeek.size()>0 && listDayStockOfLastWeek.size()>0)
			{
				//System.out.println("one");
				dateForweek=getDateFromFirstAndLastWeek(listDayStockOfFirstWeek,listDayStockOfLastWeek);
				if(dateForweek==null || dateForweek.equals(""))//有可能停牌
				{
					priYearWeekNum=weekNum;//更新
					continue;
				}				
				openingPriceForWeek=getOpeningPriceFromFirstAndLastWeek(listDayStockOfFirstWeek,listDayStockOfLastWeek);
				highestPriceForWeek=getHighestPriceFromFirstAndLastWeek(listDayStockOfFirstWeek,listDayStockOfLastWeek);
				lowestPriceForWeek=getLowestPriceFromFirstAndLastWeek(listDayStockOfFirstWeek,listDayStockOfLastWeek);
				closingPriceForWeek=getClosingPriceFromFirstAndLastWeek(listDayStockOfFirstWeek,listDayStockOfLastWeek);
			}
			else if(listDayStockOfFirstWeek.size()==0 && listDayStockOfLastWeek.size()>0)
			{
				//System.out.println("two");
				dateForweek=getDateFromDate(stockFullId,priYearWeekNum,year-1,ConstantsInfo.WeekDataType);
				if(dateForweek==null || dateForweek.equals(""))//有可能停牌
				{
					priYearWeekNum=weekNum;//更新
					continue;
				}
				openingPriceForWeek=getOpeningPriceFromDate(stockFullId,priYearWeekNum,year-1,ConstantsInfo.WeekDataType);
				highestPriceForWeek=getHighestPriceFromDate(stockFullId,priYearWeekNum,year-1,ConstantsInfo.WeekDataType);
				lowestPriceForWeek=getLowestPriceFromDate(stockFullId,priYearWeekNum,year-1,ConstantsInfo.WeekDataType);
				closingPriceForWeek=getClosingPriceFromDate(stockFullId,priYearWeekNum,year-1,ConstantsInfo.WeekDataType);
			}
			else if(listDayStockOfFirstWeek.size()>0 && listDayStockOfLastWeek.size()==0)
			{
				//System.out.println("three");
				dateForweek=getDateFromDate(stockFullId,0,year,ConstantsInfo.WeekDataType);
				if(dateForweek==null || dateForweek.equals(""))//有可能停牌
				{
					priYearWeekNum=weekNum;//更新
					continue;
				}
				openingPriceForWeek=getOpeningPriceFromDate(stockFullId,0,year,ConstantsInfo.WeekDataType);
				highestPriceForWeek=getHighestPriceFromDate(stockFullId,0,year,ConstantsInfo.WeekDataType);
				lowestPriceForWeek=getLowestPriceFromDate(stockFullId,0,year,ConstantsInfo.WeekDataType);
				closingPriceForWeek=getClosingPriceFromDate(stockFullId,0,year,ConstantsInfo.WeekDataType);		
			}
			
			priYearWeekNum=weekNum;//更新并保存今年的周数
			
			
			if(openingPriceForWeek!=0 || highestPriceForWeek!=0 || lowestPriceForWeek!=0 || closingPriceForWeek!=0)
			{		
				dateSqlForweek=java.sql.Date.valueOf(dateForweek);
				sdata=new StockData(dateSqlForweek,openingPriceForWeek,highestPriceForWeek,lowestPriceForWeek,closingPriceForWeek,0,0,0,0,ConstantsInfo.WeekDataType);
				insertStockData(sdata,stockFullId);			
				ma5PriceForWeek=getMaValueFromDate(stockFullId,dateForweek,ConstantsInfo.WeekDataType,5);
				ma10PriceForWeek=getMaValueFromDate(stockFullId,dateForweek,ConstantsInfo.WeekDataType,10);	
				
				if(ConstantsInfo.DEBUG)
				{
					System.out.println("date:"+dateSqlForweek);				
					System.out.println("openingPrice:"+openingPriceForWeek);
					System.out.println("highestPrice:"+highestPriceForWeek);
					System.out.println("lowestPrice:"+lowestPriceForWeek);		
					System.out.println("closingPrice:"+closingPriceForWeek);
					System.out.println("ma5Price:"+ma5PriceForWeek);
					System.out.println("ma10Price:"+ma10PriceForWeek);
				}
				insertMAtoDayStock(stockFullId,ConstantsInfo.WeekDataType,ma5PriceForWeek,ma10PriceForWeek,dateForweek);
								
			}
			
			
			
		//计算其他周
			for(weekCount=1;weekCount<weekNum;weekCount++)
			{
				if(weekCount<=15)
				 {	
					System.out.println("第"+weekCount+"周");
					
					openingPriceForWeek=getOpeningPriceFromDate(stockFullId,weekCount,year,ConstantsInfo.WeekDataType);
					highestPriceForWeek=getHighestPriceFromDate(stockFullId,weekCount,year,ConstantsInfo.WeekDataType);
					lowestPriceForWeek=getLowestPriceFromDate(stockFullId,weekCount,year,ConstantsInfo.WeekDataType);
					closingPriceForWeek=getClosingPriceFromDate(stockFullId,weekCount,year,ConstantsInfo.WeekDataType);			
					
					if(openingPriceForWeek==0 || highestPriceForWeek==0 || lowestPriceForWeek==0 || closingPriceForWeek==0)
						continue;					
					dateForweek=getDateFromDate(stockFullId,weekCount,year,ConstantsInfo.WeekDataType);
					if(dateForweek==null || dateForweek.equals(""))//有可能停牌
						continue;				
					dateSqlForweek=java.sql.Date.valueOf(dateForweek);	
					sdata=new StockData(dateSqlForweek,openingPriceForWeek,highestPriceForWeek,lowestPriceForWeek,closingPriceForWeek,0,0,0,0,ConstantsInfo.WeekDataType);
					insertStockData(sdata,stockFullId);					
					
					ma5PriceForWeek=getMaValueFromDate(stockFullId,dateForweek,ConstantsInfo.WeekDataType,5);
					ma10PriceForWeek=getMaValueFromDate(stockFullId,dateForweek,ConstantsInfo.WeekDataType,10);	
					
					if(ConstantsInfo.DEBUG)
					{
						System.out.println("date:"+dateSqlForweek);				
						System.out.println("openingPrice:"+openingPriceForWeek);
						System.out.println("highestPrice:"+highestPriceForWeek);
						System.out.println("lowestPrice:"+lowestPriceForWeek);		
						System.out.println("closingPrice:"+closingPriceForWeek);
						System.out.println("ma5Price:"+ma5PriceForWeek);
						System.out.println("ma10Price:"+ma10PriceForWeek);
					}
					insertMAtoDayStock(stockFullId,ConstantsInfo.WeekDataType,ma5PriceForWeek,ma10PriceForWeek,dateForweek);
						
				 }

			}
			
			
		}
	}
*/
	public void calAllHistoryMonthStockData(String stockFullId,List<TimeStock> listTimeStock) throws IOException, ClassNotFoundException, SQLException
	{
		
		float closingPrice,openingPrice,highestPrice,lowestPrice=0;
	
		String date;
		Date dateSql = null;
		StockData sdata;
		
		for(Iterator it = listTimeStock.iterator();it.hasNext();)
		{
			TimeStock ts=(TimeStock)it.next();
			//System.out.println("year:"+ts.getYear());
			//System.out.println("season:"+ts.getSeasonNum());
			//System.out.println("month:"+ts.getMonthNum());
			//System.out.println("week:"+ts.getWeekNum());
			//System.out.println("day:"+ts.getDayNum());	
			
			int year=Integer.parseInt(ts.getYear());
			for(int i=1;i<=ts.getMonthNum();i++)
			{
				openingPrice=getOpeningPriceFromDate(stockFullId,i,year,ConstantsInfo.MonthDataType);
				highestPrice=getHighestPriceFromDate(stockFullId,i,year,ConstantsInfo.MonthDataType);
				lowestPrice=getLowestPriceFromDate(stockFullId,i,year,ConstantsInfo.MonthDataType);
				closingPrice=getClosingPriceFromDate(stockFullId,i,year,ConstantsInfo.MonthDataType);
				if(closingPrice==0 || openingPrice==0 || highestPrice==0 || lowestPrice==0)
					continue;
				date=getDateFromDate(stockFullId,i,year,ConstantsInfo.MonthDataType);
				if(date==null || date.equals(""))//有可能停牌
					continue;
				dateSql=java.sql.Date.valueOf(date);	
				sdata=new StockData(dateSql,openingPrice,highestPrice,lowestPrice,closingPrice,0,0,0,0,ConstantsInfo.MonthDataType,0,0);
				
				int monthId=getIdAndExistDataTimeType(stockFullId,year,i,0,"",ConstantsInfo.MonthDataType);
				if(monthId==0)
				{
					insertStockData(sdata,stockFullId);		
				}
				else
				{
					updateStockDataTimeType(sdata,stockFullId,monthId,ConstantsInfo.MonthDataType);
				}
				
				calStockAppendData(stockFullId,date,ConstantsInfo.MonthDataType);		

			}
			
		}
	}
	
	public void calAllHistorySeasonStockData(String stockFullId,List<TimeStock> listTimeStock) throws IOException, ClassNotFoundException, SQLException
	{
		float closingPrice,openingPrice,highestPrice,lowestPrice=0;
		float ma5Price,ma10Price;
		String date;
		Date dateSql = null;
		StockData sdata;
		List<StockData> listStockData = new ArrayList<StockData>();
		float range=0,rangeTmp=0;
		float amplitude =0, amplitudeTmp=0;
		for(Iterator it = listTimeStock.iterator();it.hasNext();)
		{
			TimeStock ts=(TimeStock)it.next();
			System.out.println("year:"+ts.getYear());
			System.out.println("season:"+ts.getSeasonNum());
			//System.out.println("month:"+ts.getMonthNum());
			//System.out.println("week:"+ts.getWeekNum());
			//System.out.println("day:"+ts.getDayNum());	
			
			int year=Integer.parseInt(ts.getYear());
			for(int i=1;i<=ts.getSeasonNum();i++)
			{
				openingPrice=getOpeningPriceFromDate(stockFullId,i,year,ConstantsInfo.SeasonDataType);
				highestPrice=getHighestPriceFromDate(stockFullId,i,year,ConstantsInfo.SeasonDataType);
				lowestPrice=getLowestPriceFromDate(stockFullId,i,year,ConstantsInfo.SeasonDataType);
				closingPrice=getClosingPriceFromDate(stockFullId,i,year,ConstantsInfo.SeasonDataType);
				if(closingPrice==0 || openingPrice==0 || highestPrice==0 || lowestPrice==0)
					continue;
				date=getDateFromDate(stockFullId,i,year,ConstantsInfo.SeasonDataType);
				if(date==null || date.equals(""))//有可能停牌
					continue;
				dateSql=java.sql.Date.valueOf(date);	
				sdata=new StockData(dateSql,openingPrice,highestPrice,lowestPrice,closingPrice,0,0,0,0,ConstantsInfo.SeasonDataType,0,0);
				
				//month=i*3+1;
				int month=3*i-1;
				int seasonId=getIdAndExistDataTimeType(stockFullId,year,month,0,"",ConstantsInfo.SeasonDataType);
				if(seasonId==0)
				{
					insertStockData(sdata,stockFullId);		
				}
				else
				{
					updateStockDataTimeType(sdata,stockFullId,seasonId,ConstantsInfo.SeasonDataType);
				}				
				
				calStockAppendData(stockFullId,date,ConstantsInfo.SeasonDataType);					

			}
		}		
		
	}
	
	public void calAllHistoryYearStockData(String stockFullId,List<TimeStock> listTimeStock) throws IOException, ClassNotFoundException, SQLException
	{
		float closingPrice,openingPrice,highestPrice,lowestPrice=0;
		float ma5Price,ma10Price;
		String date;
		Date dateSql = null;
		StockData sdata;
		for(Iterator it = listTimeStock.iterator();it.hasNext();)
		{
			TimeStock ts=(TimeStock)it.next();
			System.out.println("year:"+ts.getYear());				
			
			int year=Integer.parseInt(ts.getYear());
			
			openingPrice=getOpeningPriceFromDate(stockFullId,0,year,ConstantsInfo.YearDataType);
			highestPrice=getHighestPriceFromDate(stockFullId,0,year,ConstantsInfo.YearDataType);
			lowestPrice=getLowestPriceFromDate(stockFullId,0,year,ConstantsInfo.YearDataType);
			closingPrice=getClosingPriceFromDate(stockFullId,0,year,ConstantsInfo.YearDataType);
			if(closingPrice==0 || openingPrice==0 || highestPrice==0 || lowestPrice==0)
				continue;
			date=getDateFromDate(stockFullId,0,year,ConstantsInfo.YearDataType);
			if(date==null || date.equals(""))//有可能停牌
				continue;
			dateSql=java.sql.Date.valueOf(date);	
			sdata=new StockData(dateSql,openingPrice,highestPrice,lowestPrice,closingPrice,0,0,0,0,ConstantsInfo.YearDataType,0,0);
			int yearId=getIdAndExistDataTimeType(stockFullId,year,0,0,"",ConstantsInfo.YearDataType);
			if(yearId==0)
			{
				insertStockData(sdata,stockFullId);		
			}
			else
			{	
				updateStockDataTimeType(sdata,stockFullId,yearId,ConstantsInfo.YearDataType);
			}
			
			calStockAppendData(stockFullId,date,ConstantsInfo.YearDataType);	

			
		}		
				
	}
	
	public int getTimeTypeOfDate(String date,int type) throws SQLException
	{
		String getSql=null;
		switch(type)
		{
		case ConstantsInfo.WeekDataType:
			getSql = "select extract(week from '"+date+"')";
			break;
			
		case ConstantsInfo.MonthDataType:
			getSql = "select extract(month from '"+date+"')";
			break;
	//	case ConstantsInfo.SeasonDataType:
			
		case ConstantsInfo.YearDataType:
			getSql = "select extract(year from '"+date+"')";	
			break;
		}	
		return super.getSingleIntQuery(getSql,null);
	}
	
	public int getIdAndExistDataTimeType(String stockFullId,int year,int month,int week,String date,int type) throws SQLException
	{
		String selectSql=null;
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		switch(type)
		{
		case ConstantsInfo.DayDataType:
			selectSql="select id from "+stockTable+" where date='"+date+"' and dataType='"+ConstantsInfo.DayDataType+"'";
			break;
		case ConstantsInfo.WeekDataType:
			selectSql="select id from "+stockTable+" where WEEK(date)='"+week+"' and YEAR(date)='"+year+"' and dataType='"+ConstantsInfo.WeekDataType+"'";
			break;
			
		case ConstantsInfo.MonthDataType:
			selectSql="select id from "+stockTable+" where MONTH(date)='"+month+"' and YEAR(date)='"+year+"' and dataType='"+ConstantsInfo.MonthDataType+"'";
			break;
		case ConstantsInfo.SeasonDataType:
			String seasonStart=null,seasonEnd = null;		
			switch(month)
			{
			case 1:
			case 2:
			case 3:
				seasonStart=year+"-01-01";
				seasonEnd=year+"-03-31";
				break;
			case 4:
			case 5:
			case 6:
				seasonStart=year+"-04-01";
				seasonEnd=year+"-06-30";
				break;
			case 7:
			case 8:
			case 9:
				seasonStart=year+"-07-01";
				seasonEnd=year+"-09-30";
				break;
			case 10:
			case 11:
			case 12:
			default:
				seasonStart=year+"-10-01";
				seasonEnd=year+"-12-31";
				break;				
			}
		
			selectSql="select id from "+stockTable+" where date>='"+seasonStart+"' and date<='"+seasonEnd+"' and dataType='"+ConstantsInfo.SeasonDataType+"'";		
			break;
		case ConstantsInfo.YearDataType:				
			selectSql="select id from "+stockTable+" where YEAR(date)='"+year+"' and dataType='"+ConstantsInfo.YearDataType+"'";
			break;
		}
		
		return super.getSingleIntQuery(selectSql,null);		
	}

	
	public int insertStockDataTimeType(StockData stockData,String stockFullId) throws IOException, ClassNotFoundException, SQLException
	{
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		String insertSql = "insert into "+stockTable;
		
		return super.saveOrUpdate(
					 insertSql+" values(?,?,?,?,?,?,?,?,?,?,?)", 
		        		0,stockData.getDate(),stockData.getOpeningPrice(),stockData.getHighestPrice(),stockData.getLowestPrice(),stockData.getClosingPrice(),stockData.getStockVolume(),stockData.getDailyTurnover(),0,0,stockData.getDataType());
			
	}
	
	public int updateStockDataTimeType(StockData stockData,String stockFullId,int id,int type) throws IOException, ClassNotFoundException, SQLException
	{
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
	
	//id唯一	只更新四个值
		String updateSql = "update "+stockTable+" set date='"+stockData.getDate()+"',openingPrice='"+stockData.getOpeningPrice()+
		"',highestPrice='"+stockData.getHighestPrice()+"',lowestPrice='"+stockData.getLowestPrice()+"',closingPrice='"+stockData.getClosingPrice()+"' where id="+id;
	//	System.out.println(updateSql);
		return super.saveOrUpdate(updateSql);		
	}	
	
	public int insertMAtoDayStock(String stockFullId,float ma5Price,float md10Price,int id) throws IOException, ClassNotFoundException, SQLException
	{
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		String updateSql = "update "+stockTable+" set ma5Price='"+ma5Price+"',ma10Price='"+md10Price+"' where id="+id;
		//update day_stock_atest set ma5Price='15',ma10Price='12' where id=1332;
		//System.out.println(updateSql);
		return super.saveOrUpdate(updateSql);
	}
	
	
	//某表最大的id
	public int getMaxId(String stockFullId) throws SQLException
	{
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		String selectSql;
		selectSql="select max(id) from "+stockTable;
		return super.getSingleIntQuery(selectSql, null);
	}
	
	//某天的id
	public int getId(String stockFullId,String date) throws SQLException
	{
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		String selectSql;
		selectSql="select id from "+stockTable+" where date = '"+date+"'";
		//System.out.println(selectSql);
		return super.getSingleIntQuery(selectSql, null);
	}
	
	public List<String> getDatesFromTo(String stockFullId,int dateType, String dateStart, String dateEnd) throws SQLException
	{
		List<String> dates=new ArrayList<String>();
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		StringBuffer selectSql = new StringBuffer();
		
		selectSql.append("select date from ");
		selectSql.append(stockTable);
		selectSql.append(" where ");
		
		if (dateStart != null) {
			selectSql.append(" date>='"+dateStart+"' and ");
		}
		
		if (dateEnd != null) {
			selectSql.append(" date<='"+dateEnd+"' and ");
		}	
			
		switch(dateType)
		{
		case ConstantsInfo.DayDataType:
			 //排好序
			selectSql.append(" dataType='"+ConstantsInfo.DayDataType+"' ORDER BY date");
			break;
		case ConstantsInfo.WeekDataType:
			selectSql.append(" dataType='"+ConstantsInfo.WeekDataType+"' ORDER BY date");
			//selectSql="select date from "+stockTable+" where date>='"+dateStart+"' and date<='"+dateEnd+"' and dataType='"+ConstantsInfo.WeekDataType+"' ORDER BY date";
			break;
		case ConstantsInfo.MonthDataType:
			selectSql.append(" dataType='"+ConstantsInfo.MonthDataType+"' ORDER BY date");
			//selectSql="select date from "+stockTable+" where date>='"+dateStart+"' and date<='"+dateEnd+"' and dataType='"+ConstantsInfo.MonthDataType+"' ORDER BY date";
			break;
		case ConstantsInfo.SeasonDataType:
			selectSql.append(" dataType='"+ConstantsInfo.SeasonDataType+"' ORDER BY date");
			//selectSql="select date from "+stockTable+" where date>='"+dateStart+"' and date<='"+dateEnd+"' and dataType='"+ConstantsInfo.SeasonDataType+"' ORDER BY date";
			break;
		case ConstantsInfo.YearDataType:
			selectSql.append(" dataType='"+ConstantsInfo.YearDataType+"' ORDER BY date");
			//selectSql="select date from "+stockTable+" where dataType='"+ConstantsInfo.YearDataType+"' ORDER BY date";
			break;
		}
		
		//stockLogger.logger.fatal("select data sql："+selectSql.toString());
		dates=getQuery(selectSql.toString(),null);
		return dates;
	}
	
	public List<String> getDates(String stockFullId,int dateType) throws SQLException
	{
		List<String> dates=new ArrayList<String>();
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		String selectSql = null;
		switch(dateType)
		{
		case ConstantsInfo.DayDataType:
			 //排好序
			selectSql="select date from "+stockTable+" where dataType='"+ConstantsInfo.DayDataType+"' ORDER BY date";
			break;
		case ConstantsInfo.WeekDataType:
			selectSql="select date from "+stockTable+" where dataType='"+ConstantsInfo.WeekDataType+"' ORDER BY date";
			break;
		case ConstantsInfo.MonthDataType:
			selectSql="select date from "+stockTable+" where dataType='"+ConstantsInfo.MonthDataType+"' ORDER BY date";
			break;
		case ConstantsInfo.SeasonDataType:
			selectSql="select date from "+stockTable+" where dataType='"+ConstantsInfo.SeasonDataType+"' ORDER BY date";
			break;
		case ConstantsInfo.YearDataType:
			selectSql="select date from "+stockTable+" where dataType='"+ConstantsInfo.YearDataType+"' ORDER BY date";
			break;
		}
		dates=getQuery(selectSql,null);
		return dates;
	}
	
	public int getDatesNums(DateStock dStock,int dateType)
	{
		int size=0;
		switch(dateType)
		{
		case ConstantsInfo.DayDataType:
			size=dStock.getDayDate().size();
			break;
		case ConstantsInfo.WeekDataType:
			size=dStock.getWeekDate().size();
			break;
		case ConstantsInfo.MonthDataType:
			size=dStock.getMonthDate().size();
			break;
		case ConstantsInfo.SeasonDataType:
			size=dStock.getSeasonDate().size();
			break;
		case ConstantsInfo.YearDataType:
			size=dStock.getYearDate().size();
			break;
		}		
		return size;
	}	
	
	
	
	//获取两个时间交易日差 正数
	public int getStockDataDateGap(String stockFullId,String dateStart,String dateEnd,int dateType) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException
	{
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		String selectSql = null;	
	
		switch(dateType)
		{
		case ConstantsInfo.DayDataType:
		default:
			selectSql="select count(id) from "+stockTable+" where date>='"+dateStart+"' and date<'"+dateEnd+"' and dataType='"+ConstantsInfo.DayDataType+"'";
			break;
		case ConstantsInfo.WeekDataType:
			selectSql="select count(id)  from "+stockTable+" where date>='"+dateStart+"' and date<'"+dateEnd+"' and dataType='"+ConstantsInfo.WeekDataType+"'";
			break;
		case ConstantsInfo.MonthDataType:
			selectSql="select count(id)  from "+stockTable+" where date>='"+dateStart+"' and date<'"+dateEnd+"' and dataType='"+ConstantsInfo.MonthDataType+"'";
			break;
		}	
		
		//System.out.println(selectSql);	
		return super.getSingleIntQuery(selectSql,null); 
	}
	
	
	public float getStockMaData(String stockFullId,String date,int maType,int dateType) throws SQLException
	{
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		String selectSql = null;
		String selectColumn;
		switch(dateType)
		{
		case ConstantsInfo.DayDataType:
			if(maType==5)
				selectColumn="ma5Price";
			else
				selectColumn="ma10Price";
			selectSql="select "+selectColumn+" from "+stockTable+" where date = '"+date+"' and dataType='"+ConstantsInfo.DayDataType+"'";
			break;
		case ConstantsInfo.WeekDataType:
			if(maType==5)
				selectColumn="ma5Price";
			else
				selectColumn="ma10Price";
			selectSql="select "+selectColumn+" from "+stockTable+" where date = '"+date+"' and dataType='"+ConstantsInfo.WeekDataType+"'";
			break;
		case ConstantsInfo.MonthDataType:
			if(maType==5)
				selectColumn="ma5Price";
			else
				selectColumn="ma10Price";
			selectSql="select "+selectColumn+" from "+stockTable+" where date = '"+date+"' and dataType='"+ConstantsInfo.MonthDataType+"'";
			break;
		case ConstantsInfo.SeasonDataType:
			if(maType==5)
				selectColumn="ma5Price";
			else
				selectColumn="ma10Price";
			selectSql="select "+selectColumn+" from "+stockTable+" where date = '"+date+"' and dataType='"+ConstantsInfo.SeasonDataType+"'";
			break;
		case ConstantsInfo.YearDataType:
			if(maType==5)
				selectColumn="ma5Price";
			else
				selectColumn="ma10Price";
			selectSql="select "+selectColumn+" from "+stockTable+" where date = '"+date+"' and dataType='"+ConstantsInfo.YearDataType+"'";
			break;
		}	

		//System.out.println(selectSql);
		return super.getSingleFloatQuery(selectSql, null);
	}
	
	public StockData getMaxStockDataPoint(String stockFullId,String dateStart,String dateEnd,int dateType) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException
	{
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		String selectSql = null;	
	
		switch(dateType)
		{
		case ConstantsInfo.DayDataType:
		default:
			selectSql="select * from "+stockTable+" where date>='"+dateStart+"' and date<='"+dateEnd+"' and dataType='"+ConstantsInfo.DayDataType+"' ORDER BY highestPrice desc limit 1 ";
			break;
			/*
		case ConstantsInfo.WeekDataType:
			selectSql="select * from "+stockTable+" where date>='"+dateStart+"' and date<='"+dateEnd+"' and dataType='"+ConstantsInfo.WeekDataType+"' ORDER BY highestPrice desc limit 1 ";
			break;
		case ConstantsInfo.MonthDataType:
			selectSql="select * from "+stockTable+" where date>='"+dateStart+"' and date<='"+dateEnd+"' and dataType='"+ConstantsInfo.MonthDataType+"' ORDER BY highestPrice desc limit 1 ";
			break;
		case ConstantsInfo.SeasonDataType:
			selectSql="select * from "+stockTable+" where date>='"+dateStart+"' and date<='"+dateEnd+"' and dataType='"+ConstantsInfo.SeasonDataType+"' ORDER BY highestPrice desc limit 1 ";
			break;
		case ConstantsInfo.YearDataType:
			selectSql="select * from "+stockTable+" where date>='"+dateStart+"' and date<='"+dateEnd+"' and dataType='"+ConstantsInfo.YearDataType+"' ORDER BY highestPrice desc limit 1 ";
			break;
			*/
		}	
		
		//System.out.println(selectSql);	
		return super.executeSingleQuery(selectSql,StockData.class); 
	}
	
	public StockData getMinStockDataPoint(String stockFullId,String dateStart,String dateEnd,int dateType) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException
	{
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		String selectSql = null;
		switch(dateType)
		{
		case ConstantsInfo.DayDataType:
		default:
			selectSql="select * from "+stockTable+" where date>='"+dateStart+"' and date<='"+dateEnd+"' and dataType='"+ConstantsInfo.DayDataType+"' ORDER BY lowestPrice limit 1 ";
			break;
			/*
		case ConstantsInfo.WeekDataType:
			selectSql="select * from "+stockTable+" where date>='"+dateStart+"' and date<='"+dateEnd+"' and dataType='"+ConstantsInfo.WeekDataType+"' ORDER BY lowestPrice limit 1 ";
			break;
		case ConstantsInfo.MonthDataType:
			selectSql="select * from "+stockTable+" where date>='"+dateStart+"' and date<='"+dateEnd+"' and dataType='"+ConstantsInfo.MonthDataType+"' ORDER BY lowestPrice limit 1 ";
			break;
		case ConstantsInfo.SeasonDataType:
			selectSql="select * from "+stockTable+" where date>='"+dateStart+"' and date<='"+dateEnd+"' and dataType='"+ConstantsInfo.SeasonDataType+"' ORDER BY lowestPrice limit 1 ";
			break;
		case ConstantsInfo.YearDataType:
			selectSql="select * from "+stockTable+" where date>='"+dateStart+"' and date<='"+dateEnd+"' and dataType='"+ConstantsInfo.YearDataType+"' ORDER BY lowestPrice limit 1 ";
			break;*/
		}	
		
		//System.out.println(selectSql);		
		return super.executeSingleQuery(selectSql,StockData.class); 
	}
	
	public String getPointStartDate(String stockFullId,String dateStart,int type) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException
	{
		//select DATE_ADD('2014-04-11',INTERVAL -WEEKDAY('2014-04-11') day);
		//select DATE_ADD('2014-04-11',interval -day('2014-04-11')+1 day);
		String selectSql = null;
		switch(type)
		{
		case ConstantsInfo.WeekDataType:
			selectSql="select DATE_ADD('"+dateStart+"', INTERVAL -WEEKDAY('"+dateStart+"') day)";
			break;
		case ConstantsInfo.MonthDataType:
			selectSql="select DATE_ADD('"+dateStart+"', INTERVAL -DAY('"+dateStart+"')+1 day)";
			break;
		}
		
		String date = getSingleQuery(selectSql,null);
		return date;
	}
	
	public String getMonthPointStartDate(String stockFullId,String dateStart) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException
	{
		
		String selectSql = null;
		selectSql="select YEAR('"+dateStart+"')";
		//System.out.println(selectSql);
		String date = getSingleQuery(selectSql,null);
		return date;
	}
	
	public String getExtremeDate(String stockFullId,String dateStart,String dateEnd,float extremePrice,int type) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException
	{
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		String selectSql = null;
		switch(type)
		{
		case 1:
			selectSql="select date from "+stockTable+" where date>='"+dateStart+"' and date<='"+dateEnd+"' and dataType='"+ConstantsInfo.DayDataType+"' ORDER BY highestPrice desc limit 1";
			break;
		case 0:
			selectSql="select date from "+stockTable+" where date>='"+dateStart+"' and date<='"+dateEnd+"' and dataType='"+ConstantsInfo.DayDataType+"' ORDER BY lowestPrice limit 1";
			break;
		}	
		
		//System.out.println(selectSql);		
		return super.getSingleDateQuery(selectSql,null).toString(); 
	}
	
	
	
	
	public DateStock getDateStock(String stockFullId) throws SQLException
	{
		
		List<String> listStockDays=new ArrayList<String>();
		List<String> listStockWeeks=new ArrayList<String>();
		List<String> listStockMonths=new ArrayList<String>();
		List<String> listStockSeasons=new ArrayList<String>();
		List<String> listStockYears=new ArrayList<String>();
	
		listStockDays=getDates(stockFullId,ConstantsInfo.DayDataType);
		listStockWeeks=getDates(stockFullId,ConstantsInfo.WeekDataType);
		listStockMonths=getDates(stockFullId,ConstantsInfo.MonthDataType);
		listStockSeasons=getDates(stockFullId,ConstantsInfo.SeasonDataType);
		listStockYears=getDates(stockFullId,ConstantsInfo.YearDataType);
	//	listStockYears=sdDao.getDates("sh000001_copy",ConstantsInfo.YearDataType);
		DateStock dStock=new DateStock(listStockDays,listStockWeeks,listStockMonths,listStockSeasons,listStockYears);
		return dStock;
		
	}
	
	public List<String> getDatesFromSH000001(String stockFullId) throws SQLException
	{
		List<String> dates=new ArrayList<String>();
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		String selectSql = null;
		
			//排好序
		selectSql="select date from "+stockTable+" where YEAR(date)>=2013 and dataType='"+ConstantsInfo.DayDataType+"' ORDER BY date";
		
		dates=getQuery(selectSql,null);
		return dates;
	}
	
	
	public List<String> getStockTradeTimesFromTo(String stockFullId,String dateStart,String dateEnd,int type) throws IOException, ClassNotFoundException, SQLException
	{
		List<String> dates=new ArrayList<String>();
		String selectSql=null;
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		switch(type)
		{
		case ConstantsInfo.DayDataType:
			selectSql="select date from "+stockTable+" where date>='"+dateStart+"' and date<'"+dateEnd+"' and dataType='"+ConstantsInfo.DayDataType+"'";
			break;
		case ConstantsInfo.WeekDataType:
			selectSql="select date from "+stockTable+" where date>='"+dateStart+"' and date<'"+dateEnd+"' and dataType='"+ConstantsInfo.WeekDataType+"'";
			break;
		case ConstantsInfo.MonthDataType:
			selectSql="select date from "+stockTable+" where date>='"+dateStart+"' and date<'"+dateEnd+"' and dataType='"+ConstantsInfo.MonthDataType+"'";
			break;
		}	
		dates=getQuery(selectSql,null);
		return dates;
	}
	
	//最近时间的所有值
	public List<String> getDatesFromSH000001RecentDate(String cdate) throws SQLException
	{
		List<String> dates=new ArrayList<String>();
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+"sh000001";
		String selectSql = null;
		
			//排好序
		selectSql="select DISTINCT(date) from "+stockTable+" where date >= '"+cdate+"' and dataType='"+ConstantsInfo.DayDataType+"' ORDER BY date desc";
		System.out.println(selectSql);
		dates=getQuery(selectSql,null);
		return dates;
	}
	
	
	
	/*获取最近交易时间*/
	public String getRecetlyDateFromSH000001(String zhiDing) throws IOException, ClassNotFoundException, SQLException
	{
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+"sh000001";
		String selectSql = null;		
		selectSql="select date from "+stockTable+" where dataType='"+ConstantsInfo.DayDataType+"' and date <= '"+zhiDing+"' ORDER BY date desc limit 1";
	
		//System.out.println(selectSql);
		//return super.executeSingleQuery(selectSql,StockData.class); 	
		return super.getSingleDateQuery(selectSql,null).toString(); 
	}
	
	//最近30交易日时间的所有值
	public List<String> getDatesFromSH000001For(int days, int dateType) throws SQLException
	{
		List<String> dates=new ArrayList<String>();
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+"sh000001";
		String selectSql = null;
		
			//排好序
		selectSql="select DISTINCT(date) from "+stockTable+" where dataType='"+dateType+"' ORDER BY date desc limit "+days;
		//System.out.println(selectSql);
		dates=getQuery(selectSql,null);
		return dates;
	}
	
	//最近30交易日时间的所有值
	public List<String> getDatesFromSH000001ForStartEnd(String startDate, String endDate) throws SQLException
	{
		List<String> dates=new ArrayList<String>();
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+"sh000001";
		String selectSql = null;
		
		//排好序
		selectSql="select DISTINCT(date) from "+stockTable+" where date >='"+startDate+"' and date<= '"+endDate+"' ORDER BY date";		
		dates=getQuery(selectSql,null);
		return dates;
	}
	
	
	//删除空记录
	public int deleteColumnNULL(String stockFullId) throws IOException, ClassNotFoundException, SQLException
	{
		String tableName=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		String sql=null;
		sql="delete from "+tableName +" where dataType=1 and  ma5Price is NULL ";
		return super.saveOrUpdate(sql);
	}

		
	
	//增加索引date 与dateType索引
	public int addIndex(String stockFullId) throws IOException, ClassNotFoundException, SQLException
	{
		String tableName=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		String sql=null;
		sql="alter table "+tableName +" add index date_index(`date`)";
		super.saveOrUpdate(sql);
		sql="alter table "+tableName +" add index data_index(`dataType`)";
		return super.saveOrUpdate(sql);
	}
	
	
	//增加range 和 amplitude字段
	public int addRangeAndAmplitude(String stockFullId) throws IOException, ClassNotFoundException, SQLException
	{
		String tableName=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		String sql=null;
		sql="alter table "+tableName +" add range float default 0, add amplitude float default 0";
		//System.out.println(sql);
		return super.saveOrUpdate(sql);
	}
		
	/*获取股票两时间范围内的交叉时间天，周，月差数*/
	public int getStockTimeInterval(String stockFullId,String dateStart,String dateEnd,int type) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException
	{
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		String selectSql = null;
		switch(type)
		{
		case ConstantsInfo.DayDataType:
			selectSql="select count(id) from "+stockTable+" where date>='"+dateStart+"' and date<='"+dateEnd+"' and dataType='"+ConstantsInfo.DayDataType+"'";
			break;
		case ConstantsInfo.WeekDataType:
			selectSql="select date from "+stockTable+" where date>='"+dateStart+"' and date<='"+dateEnd+"' and dataType='"+ConstantsInfo.WeekDataType+"'";
			break;
		}	
		
		//System.out.println(selectSql);		
		 return super.getSingleIntQuery(selectSql, null);
	}	
	
	 /*统计最近一段时间交易天数，周数*/
	public int getStockTradeTimes(String stockFullId,String dateStart,String dateEnd,int type) throws IOException, ClassNotFoundException, SQLException
	{
		String selectSql=null;
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		switch(type)
		{
		case ConstantsInfo.DayDataType:
			selectSql="select count(id) from "+stockTable+" where date>='"+dateStart+"' and date<'"+dateEnd+"' and dataType='"+ConstantsInfo.DayDataType+"'";
			break;
		case ConstantsInfo.WeekDataType:
			selectSql="select count(id) from "+stockTable+" where date>='"+dateStart+"' and date<'"+dateEnd+"' and dataType='"+ConstantsInfo.WeekDataType+"'";
			break;
		case ConstantsInfo.MonthDataType:
			selectSql="select count(id) from "+stockTable+" where date>='"+dateStart+"' and date<'"+dateEnd+"' and dataType='"+ConstantsInfo.MonthDataType+"'";
			break;
		}	
		return super.getSingleIntQuery(selectSql, null);
	}
	
	
	//根据季度算历史最小的 最低值
	public float getStockMinORMaxPriceData(String stockFullId,int priceType,int type) throws SQLException
	{
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		String selectSql = null;
		if (priceType == 0)  {//min
			switch(type)
			{
			case ConstantsInfo.SeasonDataType:
				selectSql="select MIN(lowestPrice) from "+stockTable+" where dataType='"+ConstantsInfo.SeasonDataType+"'";
				break;
			case ConstantsInfo.DayDataType:
				selectSql="select MIN(lowestPrice) from "+stockTable+" where dataType='"+ConstantsInfo.DayDataType+"'";
				break;
			}
		} else { //max
			switch(type)
			{
			case ConstantsInfo.SeasonDataType:
				selectSql="select MAX(highestPrice) from "+stockTable+" where dataType='"+ConstantsInfo.SeasonDataType+"'";
				break;
			case ConstantsInfo.DayDataType:
				selectSql="select  MAX(highestPrice) from "+stockTable+" where dataType='"+ConstantsInfo.DayDataType+"'";
				break;
			}
		}
		//System.out.println(selectSql);
		return super.getSingleFloatQuery(selectSql, null);
	}
	
	
	//根据日期查询当天最低价
	public float getStockLowestPrice(String stockFullId,String date,int type) throws SQLException
	{
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		String selectSql = null;
		
		switch(type)
		{
		case ConstantsInfo.DayDataType:
			selectSql="select lowestPrice from "+stockTable+" where date='"+date+"' and dataType='"+ConstantsInfo.DayDataType+"'";
			break;
		case ConstantsInfo.WeekDataType:
			selectSql="select lowestPrice from "+stockTable+" where date='"+date+"' and dataType='"+ConstantsInfo.WeekDataType+"'";
			break;
		case ConstantsInfo.MonthDataType:
			selectSql="select lowestPrice from "+stockTable+" where date='"+date+"' and dataType='"+ConstantsInfo.MonthDataType+"'";
			break;
		}
		//System.out.println(selectSql);
		return super.getSingleFloatQuery(selectSql, null);
	}
	
	//根据日期查询当天最高价
	public float getStockHighestPrice(String stockFullId,String date,int type) throws SQLException
	{
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		String selectSql = null;
		
		switch(type)
		{
		case ConstantsInfo.DayDataType:
			selectSql="select highestPrice from "+stockTable+" where date='"+date+"' and dataType='"+ConstantsInfo.DayDataType+"'";
			break;
		case ConstantsInfo.WeekDataType:
			selectSql="select highestPrice from "+stockTable+" where date='"+date+"' and dataType='"+ConstantsInfo.WeekDataType+"'";
			break;
		case ConstantsInfo.MonthDataType:
			selectSql="select highestPrice from "+stockTable+" where date='"+date+"' and dataType='"+ConstantsInfo.MonthDataType+"'";
			break;
		}
		//System.out.println(selectSql);
		return super.getSingleFloatQuery(selectSql, null);
	}
	
	 /*离最近高点或低点大跌或大涨的最高幅度*/
	public float getStockCurHigestRange(String stockFullId,String dateStart,int type,int flag) throws IOException, ClassNotFoundException, SQLException
	{
		String selectSql=null;
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		switch(type)
		{
		case ConstantsInfo.DayDataType:
			if (flag == 1)//上涨中找下跌
				selectSql="select  range as stockRange from "+stockTable+" where date>='"+dateStart+"' and dataType='"+ConstantsInfo.DayDataType+"' and range <=0 order by stockRange limit 1";
			else //下跌中找上涨
				selectSql="select  range as stockRange from "+stockTable+" where date>='"+dateStart+"' and dataType='"+ConstantsInfo.DayDataType+"' and range >=0 order by stockRange desc limit 1";
			break;
		case ConstantsInfo.WeekDataType:
			if (flag == 1)//上涨中找下跌
				selectSql="select  range as stockRange from "+stockTable+" where date>='"+dateStart+"' and dataType='"+ConstantsInfo.WeekDataType+"' and range <=0 order by stockRange limit 1";
			else //下跌中找上涨
				selectSql="select range as stockRange from "+stockTable+" where date>='"+dateStart+"' and dataType='"+ConstantsInfo.WeekDataType+"' and range >=0 order by stockRange desc limit 1";
			break;
		case ConstantsInfo.MonthDataType:
			if (flag == 1)//上涨中找下跌
				selectSql="select  range as stockRange from "+stockTable+" where date>='"+dateStart+"' and dataType='"+ConstantsInfo.MonthDataType+"' and range <=0 order by stockRange limit 1";
			else //下跌中找上涨
				selectSql="select  range as stockRange from "+stockTable+" where date>='"+dateStart+"' and dataType='"+ConstantsInfo.MonthDataType+"' and range >=0 order by stockRange desc limit 1";
			break;
		}	
		//System.out.println(selectSql);
		return super.getSingleFloatQuery(selectSql, null);
	}
	
	//得到每日涨跌幅比例和振幅
	public List<StockData> getStockRangeAndAmplitudeData(String stockFullId,String date,int type) throws SQLException, IOException, ClassNotFoundException
	{
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		String selectSql = null;
		List<StockData> listStockData = new ArrayList<StockData>();
		
		switch(type)
		{
		
		case ConstantsInfo.DayDataType:
			selectSql="select * from "+stockTable+" where date <= '"+date+"' and dataType='"+ConstantsInfo.DayDataType+"' ORDER BY date desc limit 2";
			break;
		case ConstantsInfo.WeekDataType:
			selectSql="select * from "+stockTable+" where date <= '"+date+"' and dataType='"+ConstantsInfo.WeekDataType+"' ORDER BY date desc limit 2";
			break;
		case ConstantsInfo.MonthDataType:
			selectSql="select * from "+stockTable+" where date <= '"+date+"' and dataType='"+ConstantsInfo.MonthDataType+"' ORDER BY date desc limit 2";
			break;
		case ConstantsInfo.SeasonDataType:
			selectSql="select * from "+stockTable+" where date <= '"+date+"' and dataType='"+ConstantsInfo.SeasonDataType+"' ORDER BY date desc limit 2";
			break;
		}
		//System.out.println(selectSql);
		//涨幅比
		listStockData=super.executeQuery(selectSql,StockData.class); 
		//System.out.println("listStockData.size="+listStockData.size());
		return listStockData;
	}
	
	//更新每日涨跌幅比例和振幅
	public void updateStockRangeAndAmplitudeData(String stockFullId,String date,int type) throws SQLException, IOException, ClassNotFoundException
	{
		float range=0;
		float rangeTmp=0;	
		float amplitude=0;
		float amplitudeTmp=0;
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		String selectSql = null;
		List<StockData> listStockData = new ArrayList<StockData>();
		
		switch(type)
		{
		
		case ConstantsInfo.DayDataType:
			selectSql="select * from "+stockTable+" where date <= '"+date+"' and dataType='"+ConstantsInfo.DayDataType+"' ORDER BY date desc limit 2";
			break;
		case ConstantsInfo.WeekDataType:
			selectSql="select * from "+stockTable+" where date <= '"+date+"' and dataType='"+ConstantsInfo.WeekDataType+"' ORDER BY date desc limit 2";
			break;
		case ConstantsInfo.MonthDataType:
			selectSql="select * from "+stockTable+" where date <= '"+date+"' and dataType='"+ConstantsInfo.MonthDataType+"' ORDER BY date desc limit 2";
			break;
		case ConstantsInfo.SeasonDataType:
			selectSql="select * from "+stockTable+" where date <= '"+date+"' and dataType='"+ConstantsInfo.SeasonDataType+"' ORDER BY date desc limit 2";
			break;
		}
		//System.out.println(selectSql);
		//涨幅比
		listStockData=super.executeQuery(selectSql,StockData.class); 
		if (listStockData.size() < 2) {
			range=0;
		} else {
			rangeTmp = (listStockData.get(0).getClosingPrice() - listStockData.get(1).getClosingPrice()) / listStockData.get(1).getClosingPrice()*100;
			range = (float)(Math.round(rangeTmp*100))/100;
		}
		
		//振幅
		if(listStockData.size() < 1) {
			amplitude=0;
		} else {
			amplitudeTmp = listStockData.get(0).getHighestPrice() - listStockData.get(0).getLowestPrice();
			amplitude = (float)(Math.round(amplitudeTmp*100))/100;
		}
		insertRangeAndAmplitudetoDayStock(stockFullId,type,range,amplitude,date);
	}
	
	
	
	//只需要计算日 振幅
	public void calAllHistoryDayStockRangeAndAmplitudeData(String stockFullId,List<TimeStock> listTimeStock) throws IOException, ClassNotFoundException, SQLException 
	{
		float ma5Price,ma10Price;
		String date;
		
		for(Iterator it = listTimeStock.iterator();it.hasNext();)
		{
			TimeStock ts=(TimeStock)it.next();
			System.out.println("----------------------------------------");	
			System.out.println("year:"+ts.getYear());			
			System.out.println("day:"+ts.getDayNum());	
			
			int year=Integer.parseInt(ts.getYear());	
			int	dayNum=ts.getDayNum();
		
			//for(int i=1;i<=20;i++)
			for(int i=1;i<=dayNum;i++)		
			{
				date=getDateFromDate(stockFullId,i,year,ConstantsInfo.DayDataType);
				if(date==null || date.equals(""))//有可能停牌
					continue;
				System.out.println("date:"+date);			
				updateStockRangeAndAmplitudeData(stockFullId,date,ConstantsInfo.DayDataType);
			}
		}
				
	}
	
	public void calAllHistoryWeekStockRangeAndAmplitudeData(String stockFullId,List<TimeStock> listTimeStock) throws IOException, ClassNotFoundException, SQLException
	{
		int weekCount;
		String dateForweek = null;
		for(Iterator it = listTimeStock.iterator();it.hasNext();)
		{
			TimeStock ts=(TimeStock)it.next();
			System.out.println("----------------------------------------");	
			System.out.println("year:"+ts.getYear());		
			System.out.println("week:"+ts.getWeekNum());
			
			int year=Integer.parseInt(ts.getYear());	
			int	weekNum=ts.getWeekNum();
			
			//最后一周不计算，留在下一年第一周
			//最后一年最后一周得计算
			if(year==2015)
				weekNum=weekNum+1;
			//for(weekCount=1;weekCount<2;weekCount++)
			for(weekCount=1;weekCount<weekNum;weekCount++)
			{
				//System.out.println("第"+weekCount+"周");							
				dateForweek=getDateFromDate(stockFullId,weekCount,year,ConstantsInfo.WeekDataType);
				if(dateForweek==null || dateForweek.equals(""))//有可能停牌
					continue;	
				System.out.println("weekdate:"+dateForweek);	
				updateStockRangeAndAmplitudeData(stockFullId,dateForweek,ConstantsInfo.WeekDataType);	
			}
			
		}
	}
	
	public void calAllHistoryMonthStockRangeAndAmplitudeData(String stockFullId,List<TimeStock> listTimeStock) throws IOException, ClassNotFoundException, SQLException
	{
		
		String date;
		
		for(Iterator it = listTimeStock.iterator();it.hasNext();)
		{
			TimeStock ts=(TimeStock)it.next();
						
			int year=Integer.parseInt(ts.getYear());
			for(int i=1;i<=ts.getMonthNum();i++)
			{
				
				date=getDateFromDate(stockFullId,i,year,ConstantsInfo.MonthDataType);
				if(date==null || date.equals(""))//有可能停牌
					continue;
			
				System.out.println("monthdate:"+date);	
				updateStockRangeAndAmplitudeData(stockFullId,date,ConstantsInfo.MonthDataType);	
		
			}
		}
	}
	
	public void calAllHistorySeasonStockRangeAndAmplitudeData(String stockFullId,List<TimeStock> listTimeStock) throws IOException, ClassNotFoundException, SQLException
	{
		float closingPrice,openingPrice,highestPrice,lowestPrice=0;
		float ma5Price,ma10Price;
		String date;
		Date dateSql = null;
		StockData sdata;
		for(Iterator it = listTimeStock.iterator();it.hasNext();)
		{
			TimeStock ts=(TimeStock)it.next();
			System.out.println("year:"+ts.getYear());
			System.out.println("season:"+ts.getSeasonNum());
				
			
			int year=Integer.parseInt(ts.getYear());
			for(int i=1;i<=ts.getSeasonNum();i++)
			{
				
				date=getDateFromDate(stockFullId,i,year,ConstantsInfo.SeasonDataType);
				if(date==null || date.equals(""))//有可能停牌
					continue;
				
				System.out.println("seasondate:"+date);	
				updateStockRangeAndAmplitudeData(stockFullId,date,ConstantsInfo.SeasonDataType);	
			}
		}		
	}
	
	/**
	 * 计算股票某天ma 涨幅 涨幅比 等附加数据
	 * @param stockFullId 
	 * @param date
	 * @param dataType
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public void calStockAppendData(String stockFullId,String date,int dataType) throws IOException, ClassNotFoundException, SQLException
	{
		float ma5PriceTmp,ma10PriceTmp,ma5Price,ma10Price;
		float range=0,rangeTmp=0,amplitudeTmp=0,amplitude=0;
		List<StockData> listStockData = new ArrayList<StockData>();
		
		ma5PriceTmp=getMaValueFromDate(stockFullId,date,dataType,5);
		ma10PriceTmp=getMaValueFromDate(stockFullId,date,dataType,10);
		
		ma5Price = (float)(Math.round(ma5PriceTmp*100))/100;
		ma10Price = (float)(Math.round(ma10PriceTmp*100))/100;
	//	insertMAtoDayStock( stockFullId,dataType,ma5Price,ma10Price,date);
	//	System.out.println("ma5Price:"+ma5Price);
	//	System.out.println("ma10Price:"+ma10Price)	
	
		
		listStockData = getStockRangeAndAmplitudeData(stockFullId,date,dataType);
		if (listStockData.size() > 1) {
			rangeTmp = (listStockData.get(0).getClosingPrice() - listStockData.get(1).getClosingPrice()) / listStockData.get(1).getClosingPrice()*100;
			range = (float)(Math.round(rangeTmp*100))/100;
		}
		
		//振幅
		if(listStockData.size() > 0) {		
			amplitudeTmp = listStockData.get(0).getHighestPrice() - listStockData.get(0).getLowestPrice();
			amplitude = (float)(Math.round(amplitudeTmp*100))/100;
		}
		
	
		//更新 ma5 ma10 涨幅
		insertMaAndRangeAndAmplitudetoDayStock(stockFullId,dataType,ma5Price,ma10Price,range,amplitude,date);
		listStockData.clear();
		listStockData = null;
		
	}
	
	//计算涨跌停个数
	public int getUpOrDownTimes(String stockFullId,String startDate,String endDate,int type) throws IOException, ClassNotFoundException, SQLException
	{
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		String selectSql = null;	
		switch(type)
		{
		case 0://计算跌停
			selectSql="select count(*) from "+stockTable+" where  date >= '"+startDate+"' and  date <= '"+endDate+"' and dataType='"+ConstantsInfo.DayDataType+"' and range<-9.9";
			break;
		case 1://计算涨停
			selectSql="select count(*) from "+stockTable+" where  date >= '"+startDate+"' and  date <= '"+endDate+"' and dataType='"+ConstantsInfo.DayDataType+"' and range>9.9";
			break;
		}	
		//System.out.println(selectSql);
		return super.getSingleIntQuery(selectSql,null); 
		
	}	
	
	//计算前一个涨停时间
	public StockData getPriUpValue(String stockFullId) throws IOException, ClassNotFoundException, SQLException
	{
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		String selectSql = null;	
	
		//计算涨停
		selectSql="select * from "+stockTable+" where  dataType='"+ConstantsInfo.DayDataType+"' and range>9.9 ORDER BY date desc limit 1";

		//System.out.println(selectSql);
		return super.executeSingleQuery(selectSql,StockData.class); 
		
	}	

	
	/*获取所在周或月交易数据*/
	public StockData getZhiDingDataStock(String stockFullId,int type,String zhiDing) throws IOException, ClassNotFoundException, SQLException
	{
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		String selectSql = null;	
		switch(type)
		{
		case ConstantsInfo.DayDataType:
			selectSql="select * from "+stockTable+" where dataType='"+ConstantsInfo.DayDataType+"' and date = '"+zhiDing+"' ORDER BY date desc limit 1";
			break;
		case ConstantsInfo.WeekDataType:
			selectSql="select * from "+stockTable+" where dataType='"+ConstantsInfo.WeekDataType+"' and week(date)= week('"+zhiDing+"') ORDER BY date desc limit 1";
			break;
		case ConstantsInfo.MonthDataType:
			selectSql="select * from "+stockTable+" where dataType='"+ConstantsInfo.MonthDataType+"' and month(date) = month('"+zhiDing+"') ORDER BY date desc limit 1";
			break;
		}	
		//System.out.println(selectSql);
		return super.executeSingleQuery(selectSql,StockData.class); 	
	}
	
	//删除某天数据
	public int delStockDataDay(String stockFullId,String mouday) throws IOException, ClassNotFoundException, SQLException
	{
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		//delete from day_stock_sz000010  where id in (select id from (select  max(id) as id,count(date) as count from day_stock_sz000010 group by date having count >1 order by count desc) as tab );
		String selectSql;
		selectSql="delete from "+stockTable+" where date='"+mouday+"' and (dataType='1' or dataType= '0')";
		System.out.println(selectSql);
		return super.saveOrUpdate(selectSql);
	}
	

	//查看上证是否存在同一时间数据
	public int getDataValueIsExist(String stockFullId,String date) throws IOException, ClassNotFoundException, SQLException
	{
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		//delete from day_stock_sz000010  where id in (select id from (select  max(id) as id,count(date) as count from day_stock_sz000010 group by date having count >1 order by count desc) as tab );
		String selectSql;
		
		 int isTableExist = isExistStockTable(stockFullId,ConstantsInfo.TABLE_DATA_STOCK);
		 if(isTableExist==0){ //不存在
			 return 0;	
		 }
		
		selectSql="select id from "+stockTable+"  where date = '"+date+"' and dataType = 1";
		System.out.println(selectSql);
		return super.getSingleIntQuery(selectSql, null);
	}
	

	
	//查询最高 最低价
	//SELECT MAX(highestPrice) as maxNUM,MIN(lowestPrice) as minNUM FROM day_stock_sh000001 where WEEK(date) = 49 and YEAR(date) = 2013;
	public float getHighestPrice(String stockFullId) throws IOException, ClassNotFoundException, SQLException
	{
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		String selectSql = "select MAX(highestPrice) from "+ stockTable+" where  dataType='"+ConstantsInfo.DayDataType+"'";
		//System.out.println(selectSql);
		return super.getSingleFloatQuery(selectSql, null);
	}
	
	//查询最高 最低价
	//SELECT MAX(highestPrice) as maxNUM,MIN(lowestPrice) as minNUM FROM day_stock_sh000001 where WEEK(date) = 49 and YEAR(date) = 2013;
	public float getLowestPrice(String stockFullId) throws IOException, ClassNotFoundException, SQLException
	{
		String stockTable=ConstantsInfo.STOCK_DATA_TABLE_NAME+stockFullId;
		String selectSql = "select MIN(lowestPrice) from "+ stockTable+" where dataType='"+ConstantsInfo.DayDataType+"'";
		return super.getSingleFloatQuery(selectSql, null);
	}
	
}
	
