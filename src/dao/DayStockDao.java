package dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import common.ConstantsInfo;

public class DayStockDao extends BaseDao{
	
	public DayStockDao()
	{
		
	}
	public DayStockDao(Connection conn)
	{
		super(conn);
	}
	
	public int createTable(String strFullId,int type) throws IOException, ClassNotFoundException, SQLException
	{
		//strFullID SH60000   mysql不区分大小写，表名会成为小写
		String tableName=null;
		switch(type)
		{
		case 1:
			tableName="day_stock_"+ strFullId;
			break;
		case 2:
			tableName="week_stock_"+ strFullId;
			break;
		case 3:
			tableName="month_stock_"+ strFullId;
			break;
		case 4:
			tableName="season_stock_"+ strFullId;
			break;
		}
		
		String dropTableSql="drop table if exists "+tableName+";";
		super.saveOrUpdate(dropTableSql);
		String createTablesql="create table " + tableName+
     //   "(stockFullId varchar(32), " +
        "(id int auto_increment primary key," + //增加id字段
       // "(date Date, " +
        "date Date, " +
        "openingPrice float, " +
        "highestPrice float, " +
        "lowestPrice float, " +
        "closingPrice float, " +
		"stockVolume bigint, " +
		"dailyTurnover double,"+
		"ma5Price float,"+
		"ma10Price float)";
		String sql=dropTableSql+createTablesql;
		System.out.println(sql);
		return super.saveOrUpdate(createTablesql);
	}
	
	public int alterMaColumnToDayStock(String stockFullId) throws IOException, ClassNotFoundException, SQLException
	{
		String tableName="day_stock_"+stockFullId;
		String sql=null;
		sql="alter table "+tableName +" add ma5Price float";
		super.saveOrUpdate(sql);
		sql ="alter table "+tableName +" add ma10Price float";
		return super.saveOrUpdate(sql);
	}
	
	
	
	  public int del_zero_data(String stockFullId) throws IOException, ClassNotFoundException, SQLException
	  {
		 String tableName="day_stock_"+stockFullId;
			String sql=null;
			sql="delete from "+tableName +" where openingPrice=0.0";
			return super.saveOrUpdate(sql);

		 }
	
	public int insertDayStock(DayStock dayStock,String stockFullId) throws IOException, ClassNotFoundException, SQLException
	{
		String stockTable="day_stock_"+stockFullId;
		String insertSql = "insert into "+stockTable;
	
		return super.saveOrUpdate(
				 insertSql+" values(?,?,?,?,?,?,?,?,?,?,?)", 
	        		0,dayStock.getDate(),dayStock.getOpeningPrice(),dayStock.getHighestPrice(),dayStock.getLowestPrice(),dayStock.getClosingPrice(),dayStock.getStockVolume(),dayStock.getDailyTurnover(),0,0,ConstantsInfo.DayDataType);  
		
	}	
	
	public int insertMAtoDayStock(String stockFullId,float ma5Price,float md10Price,int id) throws IOException, ClassNotFoundException, SQLException
	{
		String stockTable="day_stock_"+stockFullId;
		String updateSql = "update "+stockTable+" set ma5Price='"+ma5Price+"',ma10Price='"+md10Price+"' where id="+id;
		//update day_stock_atest set ma5Price='15',ma10Price='12' where id=1332;
		System.out.println(updateSql);
		return super.saveOrUpdate(updateSql);
	}
	
	//最近一周数据
	public List<DayStock> getStockLatestWeekData(String stockFullId) throws IOException, ClassNotFoundException, SQLException
	{
		String stockTable="day_stock_"+stockFullId;
		String selectSql = "select * from "+stockTable+" where DATE_SUB(CURDATE(), INTERVAL 60 DAY) <= date(date)";
		System.out.println(selectSql);
		return super.executeQuery(selectSql,DayStock.class); 
	}
	
	//获取某股票年份
	public List<String> getStockOfYear(String stockFullId) throws IOException, ClassNotFoundException, SQLException
	{
		String stockTable="day_stock_"+stockFullId;
		String selectSql = "select distinct YEAR(date) from "+stockTable; //distinct去重
	//	System.out.println(selectSql);
		return super.getQuery(selectSql, null);
	}
	
	
	//某表最大的id
	public int getMaxId(String stockFullId) throws SQLException
	{
		String stockTable="day_stock_"+stockFullId;
		String selectSql;
		selectSql="select max(id) from "+stockTable;
		return super.getSingleIntQuery(selectSql, null);
	}

	//某天的id
	public int getId(String stockFullId,String date) throws SQLException
	{
		String stockTable="day_stock_"+stockFullId;
		String selectSql;
		selectSql="select id from "+stockTable+" where date = '"+date+"'";
		System.out.println(selectSql);
		return super.getSingleIntQuery(selectSql, null);
	}
	
	//某天的date  注意id可能不是连续的，导致date为空，注意检查返回值
	public String getDate(String stockFullId,int id) throws SQLException
	{
		String stockTable="day_stock_"+stockFullId;
		String selectSql;
		selectSql="select date from "+stockTable+" where id = '"+id+"'";
	//	System.out.println(selectSql);
		return super.getSingleQuery(selectSql, null);
	}
	
	public float getStockMaData(String stockFullId,int id,int maType) throws SQLException
	{
		String stockTable="day_stock_"+stockFullId;
		String selectSql;
		switch(maType)
		{
			case 5:
			default:
				selectSql="select ma5Price from "+stockTable+" where id = '"+id+"'";
				break;
			case 10:
				selectSql="select ma10Price from "+stockTable+" where id = '"+id+"'";
				break;
		}
		
		//System.out.println(selectSql);
		return super.getSingleFloatQuery(selectSql, null);
		
	}
	
	public float getHighestPrice(String stockFullId,String date) throws SQLException
	{
		String stockTable="day_stock_"+stockFullId;
		String selectSql;
		selectSql="select highestPrice from "+stockTable+" where date= '"+date+"'";
		System.out.println(selectSql);
		return super.getSingleFloatQuery(selectSql, null);
	}
	
	public DayStock getMaxStockPoint(String stockFullId,int idStart,int idEnd) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException
	{
		String stockTable="day_stock_"+stockFullId;
		String selectSql;
		selectSql="select * from "+stockTable+" where id >= '"+idStart+"' and id <= '"+idEnd+"' ORDER BY highestPrice desc limit 1 " ;
		System.out.println(selectSql);		
		return super.executeSingleQuery(selectSql,DayStock.class); 
	}
	
	public DayStock getMinStockPoint(String stockFullId,int idStart,int idEnd) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException
	{
		String stockTable="day_stock_"+stockFullId;
		String selectSql;
		selectSql="select * from "+stockTable+" where id >= '"+idStart+"' and id <= '"+idEnd+"' ORDER BY lowestPrice limit 1 " ;
		System.out.println(selectSql);		
		return super.executeSingleQuery(selectSql,DayStock.class); 
	}
	
	
	public float getStockMaxHighestPrice(String stockFullId,int idStart,int idEnd) throws SQLException
	{
		String stockTable="day_stock_"+stockFullId;
		String selectSql;
		selectSql="select max(highestPrice) from "+stockTable+" where id >= '"+idStart+"' and id <= '"+idEnd+"'";
		System.out.println(selectSql);
		return super.getSingleFloatQuery(selectSql, null);
	}
	
	public float getStockMinHighestPrice(String stockFullId,int idStart,int idEnd) throws SQLException
	{
		String stockTable="day_stock_"+stockFullId;
		String selectSql;
		selectSql="select min(lowestPrice) from "+stockTable+" where id >= '"+idStart+"' and id <= '"+idEnd+"'";
		System.out.println(selectSql);
		return super.getSingleFloatQuery(selectSql, null);
	}
		
	
	//最近ma5,ma10
	public float calStockMaData(String stockFullId,String date,int maType) throws IOException, ClassNotFoundException, SQLException
	{
		String stockTable="day_stock_"+stockFullId;
		//select closingPrice from day_stock_sh600000 where date<="2014-05-30" order by date desc limit 5;
		String selectSql = "select closingPrice from "+stockTable+" where date<='"+date+"' order by date desc limit "+maType;
		//String selectSql = "select closingPrice from "+stockTable+" limit "+id+","+maType;
		//System.out.println(selectSql);
		List<Float> list=new ArrayList<Float>();
		list=super.getFloatQuery(selectSql,null); 
		float sum=0;
		if(list.size()!=maType)
			System.out.println("error");
		for (int i = 0; i <list.size(); i++) {
		//	System.out.println(list.get(i));
			sum+=list.get(i);
		}
	//	System.out.println("sum:"+sum+"--"+list.size()+"AVG:"+sum/list.size());
		return sum/list.size();
	}
	
	
	public String getFirstDayStock(String stockTable,String dateStart,String dateEnd) throws SQLException
	{
		//select min(date) from day_stock_sh600000 where date >= '2009-01-01';
		String selectSql;
		selectSql="select min(date) from "+stockTable+" where date >= '"+dateStart+"' and date <= '"+dateEnd+"'";
		return super.getSingleQuery(selectSql, null);
	}
	
	public String getLastDayStock(String stockTable,String dateStart,String dateEnd) throws SQLException
	{
		//select max(date) from day_stock_sh600000 where date <= '2009-12-31';
		String selectSql;
		selectSql="select max(date) from "+stockTable+" where date >= '"+dateStart+"' and date <= '"+dateEnd+"'";
		return super.getSingleQuery(selectSql, null);
	}
	
	//删除重复数据
	public int delStockSameData(String stockFullId) throws IOException, ClassNotFoundException, SQLException
	{
		String stockTable="day_stock_"+stockFullId;
		//delete from day_stock_sz000010  where id in (select id from (select  max(id) as id,count(date) as count from day_stock_sz000010 group by date having count >1 order by count desc) as tab );
		String selectSql;
		selectSql="delete from "+stockTable+" where id in (select id from (select max(id) as id,count(date) as count from "+stockTable+" group by date having count >1 order by count desc) as tab)";
		System.out.println(selectSql);
		return super.saveOrUpdate(selectSql);
	}
	
	//删除某天数据
	public int delStockDataDay(String stockFullId,String mouday) throws IOException, ClassNotFoundException, SQLException
	{
		String stockTable="day_stock_"+stockFullId;
		//delete from day_stock_sz000010  where id in (select id from (select  max(id) as id,count(date) as count from day_stock_sz000010 group by date having count >1 order by count desc) as tab );
		String selectSql;
		selectSql="delete from "+stockTable+" where date='"+mouday+"' and dataType='1'";
		System.out.println(selectSql);
		return super.saveOrUpdate(selectSql);
	}
	
	
	
	//获取某股票某年的所有相关信息
	public String getStockOfYearInfo(String stockFullId,String year) throws IOException, ClassNotFoundException, SQLException
	{
		String stockTable="day_stock_"+stockFullId;
		String firstDayYear=year+"-1-01";
		String lastDayYear=year+"-12-31";
		String firstDayofStockYear;
		String lastDayofStockYear;
				
		firstDayofStockYear=getFirstDayStock(stockTable,firstDayYear,lastDayYear);
		if(firstDayofStockYear!=null && !firstDayofStockYear.equals(""))
			System.out.println(firstDayofStockYear);
		else 
			System.out.println("value is null");
		
		lastDayofStockYear=getLastDayStock(stockTable, firstDayYear,lastDayYear);
		
		if(lastDayofStockYear!=null && !lastDayofStockYear.equals(""))
			System.out.println(lastDayofStockYear);
		else 
			System.out.println("value is null");
		
		int i;
		String firstDayOfMonth;
		String lastDayOfMonth;
		String firstDayMonthOfYear;
		String lastDayMonthOfYear;
		for(i=1;i<=12;i++)
		{
			firstDayOfMonth=year+"-"+i+"-01";
			switch(i)
			{
			case 1:
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
			case 12:
			default:
				lastDayOfMonth=year+"-"+i+"-31";
				break;			
			case 4:
			case 6:
			case 9:
			case 11:
				lastDayOfMonth=year+"-"+i+"-30";
				break;
			case 2:
				lastDayOfMonth=year+"-"+i+"-29";
				break;					
			}
			
			firstDayMonthOfYear=getFirstDayStock(stockTable,firstDayOfMonth,lastDayOfMonth);
			if(firstDayMonthOfYear==null)
				System.out.println("第"+i+"月份第一天:value is null");
			else 
				System.out.println("第"+i+"月份第一天:"+firstDayMonthOfYear);
			
			lastDayMonthOfYear=getLastDayStock(stockTable,firstDayOfMonth,lastDayOfMonth);
			if(lastDayMonthOfYear==null)
				System.out.println("第"+i+"月份最后一天:value is null");
			else 
				System.out.println("第"+i+"月份最后一天:"+lastDayMonthOfYear);
		}
		
		/*
		firstDay1MonthOfYear=getFirstDayStock(stockTable,firstDay1Month);
		if(firstDay1MonthOfYear==null)
			System.out.println("value is null");
		else 
			System.out.println(firstDay1MonthOfYear);
		
		lastDay1MonthOfYear=getLastDayStock(stockTable,lastDay1Month);
		if(lastDay1MonthOfYear==null)
			System.out.println("value is null");
		else 
			System.out.println(lastDay1MonthOfYear);
		
		firstDay2MonthOfYear=getFirstDayStock(stockTable,firstDay2Month);
		if(firstDay2MonthOfYear==null)
			System.out.println("value is null");
		else 
			System.out.println(firstDay2MonthOfYear);
		
		lastDay2MonthOfYear=getLastDayStock(stockTable,lastDay2Month);
		if(lastDay2MonthOfYear==null)
			System.out.println("value is null");
		else 
			System.out.println(lastDay2MonthOfYear);
		*/
		return lastDayofStockYear;
	//	return super.executeQuery(selectSql,DayStock.class); 
	}
	
	  //设置ma5 ma10默认值
	  public int setStockMaPriceDefault(String fullId) throws IOException, ClassNotFoundException, SQLException { 
		  return  super.saveOrUpdate(  
	        		"alter table day_stock_sh000001 alter column ma5Price set default '0',alter column ma10Price set default '0'");
	     } 
  

}
