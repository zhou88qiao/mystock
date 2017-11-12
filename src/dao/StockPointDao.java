package dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import common.ConstantsInfo;
import common.stockLogger;

public class StockPointDao extends BaseDao{
	
	public StockPointDao()
	{
		
	}
	public StockPointDao(Connection conn)
	{
		super(conn);
	}
	
	public int createStockPointTable(String strFullId) throws IOException, ClassNotFoundException, SQLException
	{
		//strFullID SH60000   mysql不区分大小写，表名会成为小写
		String tableName=ConstantsInfo.STOCK_POINT_TABLE_NAME+strFullId;
		
		String dropTableSql="drop table if exists "+tableName+";";
		super.saveOrUpdate(dropTableSql);
		String createTablesql="create table " + tableName+
        "(id int auto_increment primary key," + //增加id字段
        "type SMALLINT default 1, " +
        "extremeDate date, " +
        "extremePrice float default 0, " +
        "fromDate date, " +
        "toDate date, " +
		"willFlag SMALLINT default 0, " +
		"ratio float default 0,"+
		"expectation float default 0,"+
		"index data_index(`type`)) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1";
		String sql=createTablesql;
		System.out.println(sql);
		return super.saveOrUpdate(createTablesql);
	}
	
	
	//清空极值点表
	public int truncatePointStockTable(String stockFullId) throws IOException, ClassNotFoundException, SQLException
	{
		//strFullID SH60000   mysql不区分大小写，表名会成为小写
		String tableName=null;
		String sql="";		
		tableName=ConstantsInfo.STOCK_POINT_TABLE_NAME+ stockFullId;
		sql= "truncate table "+ tableName;
		return super.saveOrUpdate(sql);
	}
	
	
	public int insertPointStockTable(StockPoint sp,String stockFullId) throws IOException, ClassNotFoundException, SQLException
	{
		String stockTable=ConstantsInfo.STOCK_POINT_TABLE_NAME+stockFullId;
	
		String insertSql = "insert into "+stockTable;
		return super.saveOrUpdate(
				 insertSql+" values(?,?,?,?,?,?,?,?,?)", 
				 0,sp.getType(),sp.getExtremeDate(),sp.getExtremePrice(),sp.getFromDate(),sp.getToDate(),sp.getWillFlag(),sp.getRatio(),sp.getExpectation());  
		
	}	
	
	/*统计极点数据次数*/
	public int getStockPointTimes(String stockFullId,int type) throws IOException, ClassNotFoundException, SQLException
	{
		String selectSql=null;
		String stockTable=ConstantsInfo.STOCK_POINT_TABLE_NAME+stockFullId;
		switch(type)
		{
		case ConstantsInfo.DayDataType:
			selectSql="select count(id) from "+stockTable+" where type='"+ConstantsInfo.DayDataType+"'";
			break;
		case ConstantsInfo.WeekDataType:
			selectSql="select count(id) from "+stockTable+" where type='"+ConstantsInfo.WeekDataType+"'";
			break;
		case ConstantsInfo.MonthDataType:
			selectSql="select count(id) from "+stockTable+" where type='"+ConstantsInfo.MonthDataType+"'";
			break;
		}	
		return super.getSingleIntQuery(selectSql, null);
	}
	
	
	/*获取最后两个极点数据*/
	public StockPoint  getLastPointStock(String stockFullId,int type, String date) throws IOException, ClassNotFoundException, SQLException
	{
		String stockTable=ConstantsInfo.STOCK_POINT_TABLE_NAME+stockFullId;
		String selectSql = null;	
		switch(type)
		{
		case ConstantsInfo.DayDataType:
			if(null == date){
				selectSql="select * from "+stockTable+" where type='"+ConstantsInfo.DayDataType+"' ORDER BY todate desc limit 1";
			} else {
				selectSql="select * from "+stockTable+" where type='"+ConstantsInfo.DayDataType+"' and toDate <= '"+date+"' ORDER BY todate desc limit 1";
			}
			break;
		case ConstantsInfo.WeekDataType:
			if(null == date){
				selectSql="select * from "+stockTable+" where type='"+ConstantsInfo.WeekDataType+"' ORDER BY todate desc limit 1";
			} else {
				selectSql="select * from "+stockTable+" where type='"+ConstantsInfo.WeekDataType+"' and toDate <= '"+date+"' ORDER BY todate desc limit 1";
			}
			
			break;
		case ConstantsInfo.MonthDataType:
			if(null == date){
				selectSql="select * from "+stockTable+" where type='"+ConstantsInfo.MonthDataType+"' ORDER BY todate desc limit 1";
			} else {
				selectSql="select * from "+stockTable+" where type='"+ConstantsInfo.MonthDataType+"' and toDate <= '"+date+"' ORDER BY todate desc limit 1";
			}
			
			break;
		}	
		System.out.println(selectSql);
		stockLogger.logger.debug(selectSql);
		return super.executeSingleQuery(selectSql,StockPoint.class); 	
	}	
	
	/*获取最后两个极点数据*/
	public List<StockPoint>  getLastTwoPointStock(String stockFullId,int type, String date) throws IOException, ClassNotFoundException, SQLException
	{
		String stockTable=ConstantsInfo.STOCK_POINT_TABLE_NAME+stockFullId;
		String selectSql = null;	
		switch(type)
		{
		case ConstantsInfo.DayDataType:
			selectSql="select * from "+stockTable+" where type='"+ConstantsInfo.DayDataType+"' and toDate <= '"+date+"' ORDER BY id desc limit 2";
			break;
		case ConstantsInfo.WeekDataType:
			selectSql="select * from "+stockTable+" where type='"+ConstantsInfo.WeekDataType+"' and toDate <= '"+date+"' ORDER BY id desc limit 2";
			break;
		case ConstantsInfo.MonthDataType:
			selectSql="select * from "+stockTable+" where type='"+ConstantsInfo.MonthDataType+"' and toDate <= '"+date+"' ORDER BY id desc limit 2";
			break;
		}	
		//stockLogger.logger.debug(selectSql);
		return super.executeQuery(selectSql,StockPoint.class); 
		
	}	
		
	public List<StockPoint> getAllPointStock(String stockFullId,int type) throws IOException, ClassNotFoundException, SQLException
	{
		String stockTable=ConstantsInfo.STOCK_POINT_TABLE_NAME+stockFullId;
		
		String selectSql = "";	
		switch(type)
		{
		case ConstantsInfo.DayDataType:
			selectSql="select * from "+stockTable+" where type='"+ConstantsInfo.DayDataType+"'";
			break;
		case ConstantsInfo.WeekDataType:
			selectSql="select * from "+stockTable+" where type='"+ConstantsInfo.WeekDataType+"'";
			break;
		case ConstantsInfo.MonthDataType:
			selectSql="select * from "+stockTable+" where type='"+ConstantsInfo.MonthDataType+"'";
			break;
		case ConstantsInfo.ALLDataType:
			selectSql="select * from "+stockTable+"";
			break;
		}	
		//stockLogger.logger.debug(selectSql);
		//System.out.println(selectSql);
		return super.executeQuery(selectSql,StockPoint.class); 
	}	
	
	
	public List<StockPoint> getLastNumPointStock(String stockFullId,int type,int num) throws IOException, ClassNotFoundException, SQLException
	{
		String stockTable=ConstantsInfo.STOCK_POINT_TABLE_NAME+stockFullId;
		
		String selectSql = "";	
		switch(type)
		{
		case ConstantsInfo.DayDataType:
			selectSql="select * from "+stockTable+" where type='"+ConstantsInfo.DayDataType+"' ORDER BY id desc limit "+num;
			break;
		case ConstantsInfo.WeekDataType:
			selectSql="select * from "+stockTable+" where type='"+ConstantsInfo.WeekDataType+"' ORDER BY id desc limit "+num;
			break;
		case ConstantsInfo.MonthDataType:
			selectSql="select * from "+stockTable+" where type='"+ConstantsInfo.MonthDataType+"' ORDER BY id desc limit "+num;
			break;
		case ConstantsInfo.ALLDataType:
			selectSql="select * from "+stockTable+"";
			break;
		}	
		//stockLogger.logger.debug(selectSql);
		System.out.println(selectSql);
		return super.executeQuery(selectSql,StockPoint.class); 
	}	
	
	//在cdate之前的所有股票极点
	public List<StockPoint> getRecentPointStock(String stockFullId,int type,String cDate) throws IOException, ClassNotFoundException, SQLException
	{
		String stockTable=ConstantsInfo.STOCK_POINT_TABLE_NAME+stockFullId;
		
		String selectSql = "";	
		switch(type)
		{
		case ConstantsInfo.DayDataType:
			selectSql="select * from "+stockTable+" where extremeDate>='"+cDate+"' and type='"+ConstantsInfo.DayDataType+"'";
			break;
		case ConstantsInfo.WeekDataType:
			selectSql="select * from "+stockTable+" where extremeDate>='"+cDate+"' and type='"+ConstantsInfo.WeekDataType+"'";
			break;
		case ConstantsInfo.MonthDataType:
			selectSql="select * from "+stockTable+" where extremeDate>='"+cDate+"' and type='"+ConstantsInfo.MonthDataType+"'";
			break;
		case ConstantsInfo.ALLDataType:
			selectSql="select * from "+stockTable+"";
			break;
		}	
		//stockLogger.logger.debug(selectSql);
		//System.out.println(selectSql);
		return super.executeQuery(selectSql,StockPoint.class); 
	}	
	
	
	//在cdate之前的所有股票极点 下跌或上涨
	public  List<Float> getUpOrDownPoint(String stockFullId,int dateType,String cDate,int tread) throws IOException, ClassNotFoundException, SQLException
	{
		String stockTable=ConstantsInfo.STOCK_POINT_TABLE_NAME+stockFullId;
		
		String selectSql = "";	
		switch(dateType)
		{
		case ConstantsInfo.DayDataType:
			selectSql="select extremePrice from "+stockTable+" where extremeDate>='"+cDate+"' and type='"+ConstantsInfo.DayDataType+"' and willFlag='"+ tread +"'";
			break;
		case ConstantsInfo.WeekDataType:
			selectSql="select extremePrice from "+stockTable+" where extremeDate>='"+cDate+"' and type='"+ConstantsInfo.WeekDataType+"' and willFlag='"+ tread +"'";
			break;
		case ConstantsInfo.MonthDataType:
			selectSql="select extremePrice from "+stockTable+" where extremeDate>='"+cDate+"' and type='"+ConstantsInfo.MonthDataType+"' and willFlag='"+ tread +"'";
			break;
		case ConstantsInfo.ALLDataType:
			selectSql="select extremePrice from "+stockTable+"";
			break;
		}	
		//stockLogger.logger.debug(selectSql);
	//	System.out.println(selectSql);
		return super.getFloatQuery(selectSql,null); 
	}	
	
	
	//在cdate之前的所有股票极点个数（高与低极点都算）
	public  int getUpOrDownPointNum(String stockFullId,int dateType,String cDate) throws IOException, ClassNotFoundException, SQLException
	{
		String stockTable=ConstantsInfo.STOCK_POINT_TABLE_NAME+stockFullId;
		
		String selectSql = "";	
		switch(dateType)
		{
		case ConstantsInfo.DayDataType:
			selectSql="select count(id) from "+stockTable+" where extremeDate>='"+cDate+"' and type='"+ConstantsInfo.DayDataType+"'";
			break;
		case ConstantsInfo.WeekDataType:
			selectSql="select count(id) from "+stockTable+" where extremeDate>='"+cDate+"' and type='"+ConstantsInfo.WeekDataType+"'";
			break;
		case ConstantsInfo.MonthDataType:
			selectSql="select count(id) from "+stockTable+" where extremeDate>='"+cDate+"' and type='"+ConstantsInfo.MonthDataType+"'";
			break;
		case ConstantsInfo.ALLDataType:
			selectSql="select count(id) from "+stockTable+"";
			break;
		}	
		//stockLogger.logger.debug(selectSql);
		//System.out.println(selectSql);
		return super.getSingleIntQuery(selectSql,null); 
	}
	
	
	//全部极点时间
	public List<String> getAllExtremeDate(String stockFullId) throws SQLException
	{
		String stockTable=ConstantsInfo.STOCK_POINT_TABLE_NAME+stockFullId;
		String selectSql = "select DISTINCT(extremeDate) from "+stockTable+" ORDER BY extremeDate desc"; //distinct去重
		//	System.out.println(selectSql);
		return super.getQuery(selectSql, null);
	}
	
	//最近极点时间
	public List<String> getRecentExtremeDate(String stockFullId,String cDate) throws SQLException
	{
		String stockTable=ConstantsInfo.STOCK_POINT_TABLE_NAME+stockFullId;
		String selectSql = "select DISTINCT(extremeDate) from "+stockTable+" where extremeDate>='"+cDate+"' ORDER BY extremeDate desc"; //distinct去重
		//	System.out.println(selectSql);
		return super.getQuery(selectSql, null);
	}
	
	public String getNextPointDate(String lastDate,String preDate) throws IOException, ClassNotFoundException, SQLException
	{
		//select DATE_ADD('2015-04-16',INTERVAL DATEDIFF('2015-04-16','2015-03-03') DAY)
		String selectSql;
		selectSql="select DATE_ADD('"+lastDate+"', INTERVAL DATEDIFF('"+lastDate+"','"+preDate+"') DAY)";
		return super.getSingleQuery(selectSql, null);
	}	
	
	
	//增加索引Type索引
	public int addIndex(String stockFullId) throws IOException, ClassNotFoundException, SQLException
	{
		String tableName=ConstantsInfo.STOCK_POINT_TABLE_NAME+stockFullId;
		String sql=null;
		sql="alter table "+tableName +" add index type_index(`type`)";
		return super.saveOrUpdate(sql);
	}
	
	
	//删除某条极点数据，误操
	public int delStockPointData(String stockFullId,int id) throws IOException, ClassNotFoundException, SQLException
	{
		String stockTable=ConstantsInfo.STOCK_POINT_TABLE_NAME+stockFullId;
		//delete from day_stock_sz000010  where id in (select id from (select  max(id) as id,count(date) as count from day_stock_sz000010 group by date having count >1 order by count desc) as tab );
		String selectSql;
		selectSql="delete from "+stockTable+" where id='"+id+"'";
		//System.out.println(selectSql);
		return super.saveOrUpdate(selectSql);
	}
	
	//删除某个极点数据，误操作
	public int delStockPointDay(String stockFullId,String mouday) throws IOException, ClassNotFoundException, SQLException
	{
		String stockTable=ConstantsInfo.STOCK_POINT_TABLE_NAME+stockFullId;
		//delete from day_stock_sz000010  where id in (select id from (select  max(id) as id,count(date) as count from day_stock_sz000010 group by date having count >1 order by count desc) as tab );
		String selectSql;
		selectSql="delete from "+stockTable+" where toDate='"+mouday+"'";
		//System.out.println(selectSql);
		return super.saveOrUpdate(selectSql);
	}

}
