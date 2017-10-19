package excel.simple;

import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.log4j.PropertyConfigurator;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.hssf.util.HSSFColor;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.point.stock.PointClass;
import com.timer.stock.DateStock;

import common.ConstantsInfo;
import common.stockLogger;

import stockGUI.stockDialog;
import stockGUI.stocktree.StockObjectType;
import stockGUI.stocktree.StockTreeNodeData;

import dao.DbConn;
import dao.StockBaseDao;
import dao.StockBaseFace;
import dao.StockConcept;
import dao.StockData;
import dao.StockDataDao;
import dao.StockIndustry;
import dao.StockInformation;
import dao.StockInformationDao;
import dao.StockMarket;
import dao.StockPoint;
import dao.StockPointDao;
import dao.StockSingle;
import dao.StockToIndustry;
import date.timer.stockDateTimer;
import excel.all_v2.ExcelCommon;

public class StockRecentWriter {
	
	static StockInformationDao siDao;
	private StockDataDao sdDao;
	private StockPointDao spDao;
	private StockBaseDao sbDao;
	
    private static HSSFWorkbook wb;
 
	static int stockLabelNum=4;
	static int stockRow = 1;
	static stockDateTimer dateTimer = new stockDateTimer();
	//时间与列 hash表
    
	static String prevEextremeDay="";//前一个极点列值
	
   
      public StockRecentWriter(Connection stockBaseConn,Connection stockDataConn,Connection stockPointConn)
		{
		   this.sbDao = new StockBaseDao(stockBaseConn);
		   this.sdDao =new StockDataDao(stockDataConn);
		   this.spDao =new StockPointDao(stockPointConn);
		}
      
      public StockRecentWriter(StockBaseDao sbDao,StockDataDao sdDao,StockPointDao spDao)
      {
  		this.sbDao = sbDao;
  		this.sdDao = sdDao;
  		this.spDao = spDao;
  	  }
      
   
    
    //得到最近极点值
    private StockData getCurExtremeData(String stockFullId,int dataType) throws ClassNotFoundException, IOException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException
    {
    	StockData sCurData=null;
    	//当天
 		String nowTime=dateTimer.getCurDate(); 
    	//1 最后一个极值点
 		//System.out.println(dataType);
		StockPoint sp = spDao.getLastPointStock(stockFullId,dataType, nowTime);		
		if(sp == null) {
			stockLogger.logger.fatal("stockFullId："+stockFullId+"极点表无数据");
			System.out.println(stockFullId+"极点表无数据****");
			return null;  
		}
		String crossLastDate=sp.getToDate().toString();
		
		if (sp.getWillFlag()==1) { //前一个是上升，当前为下降趋势
							
			//3 最近最低点
			sCurData=sdDao.getMinStockDataPoint(stockFullId,crossLastDate,nowTime,dataType);
			if ( sCurData == null){
				stockLogger.logger.fatal("****stockFullId："+stockFullId+"无最近低点****");
				return null; 
			}
			sCurData.setDataType(0); //借用datType字段
		} else {
			sCurData=sdDao.getMaxStockDataPoint(stockFullId,crossLastDate,nowTime,dataType);
			if ( sCurData == null){
				stockLogger.logger.fatal("****stockFullId："+stockFullId+"无最近高点****");
				stockLogger.logger.fatal("****交叉点："+crossLastDate+"当前日点："+nowTime+"两日期间无交易数据****");
				return null; 
			}
			sCurData.setDataType(1); //借用datType字段
		}
		
		return sCurData;
    }
    
    //根据行业区分
    private  void exportExcelAllStockForIndustry(String filePathName,HashMap<String, Integer> stockDateColumnmap) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException 
    {    
            FileOutputStream fileOStream=null;     
        	List<StockIndustry> listIndustry = new ArrayList<StockIndustry>();         	
        	
        	 //得到当前所有行业
        	listIndustry=sbDao.getStockIndustry();        	
        	System.out.println("行业个数："+listIndustry.size());        	
        	
        	NumberFormat ddfDay=null;
        	//比例保留两位小数点
        	ddfDay=NumberFormat.getNumberInstance() ; 
        	ddfDay.setMaximumFractionDigits(2);
            //第二行开始展示  
                 
            Iterator itIndustry,ie;
	   		for(itIndustry = listIndustry.iterator();itIndustry.hasNext();)
	   		{
	   			// 输入流   
				 FileInputStream fileIStream = new FileInputStream(filePathName);  
				 XSSFWorkbook wb = new XSSFWorkbook(fileIStream);   
				 XSSFSheet sheet = wb.getSheetAt(0);  
				// System.out.println("最后一行："+sheet.getLastRowNum());   
				 //输入流   
				 fileOStream = new FileOutputStream(filePathName);
				
				 //当前行业
				 StockIndustry indu= (StockIndustry)itIndustry.next();	
				 String induName=indu.getThirdname();
				 String induCode = indu.getThirdcode();
				 if(induName==null)
					 continue;				
				stockLogger.logger.debug("行业："+induName);
	   			   			
	   			//行业
	   			Row rowIndustry = sheet.createRow(stockRow);  
	   			ExcelCommon.create07Cell(wb, rowIndustry, 0, induName, ConstantsInfo.NoColor);  
	   			stockRow++;
	   			
	   			DecimalFormat decimalFormat=new DecimalFormat(".00");
	   			   	   			
	   			//所属行业所有股票
	   			List<StockToIndustry> listIndustryStock = new ArrayList<StockToIndustry>();   
	   			listIndustryStock=sbDao.getIndustryToStock(induCode);		
	   			stockLogger.logger.debug("行业股票数："+listIndustryStock.size());
	   			for(ie=listIndustryStock.iterator();ie.hasNext();)
	   			{
	   				//测试作用	   			
	   				//if(stockRow>20)
	   				//	break;
	   				
	   				String stockFullId = (String) ie.next();	
	   			//	if(!stockFullId.equals("SH600354"))
	   			//		continue;
	   			 				

	   				String stockName=sbDao.getStockNameFromIndustryTable(stockFullId);			
	   			
	   				System.out.println("fullId："+stockFullId+" 名字："+stockName);
	   				stockLogger.logger.debug("fullId："+stockFullId+" 名字："+stockName);
	   				//每个股票
	   				Row rowDayData = sheet.createRow(stockRow);  
	   				ExcelCommon.create07Cell(wb, rowDayData, 0,  Integer.toString(stockRow), ConstantsInfo.NoColor);  
	   				ExcelCommon.create07Cell(wb, rowDayData, 1, stockFullId, ConstantsInfo.NoColor);  
	   				ExcelCommon.create07Cell(wb, rowDayData, 2, stockName, ConstantsInfo.NoColor); 	            
		            stockRow++;  		 
		            
		            //当前极值点
		            StockData sday=getCurExtremeData(stockFullId,ConstantsInfo.DayDataType);
		            if(sday!=null)
		            	ExcelCommon.create07Cell(wb, rowDayData, 4, sday.getDate().toString(), ConstantsInfo.NoColor); 	
		            StockData sweek=getCurExtremeData(stockFullId,ConstantsInfo.WeekDataType);
		            if(sweek!=null)
		            	ExcelCommon.create07Cell(wb, rowDayData, 5, sday.getDate().toString(), ConstantsInfo.NoColor);
		            StockData smonth=getCurExtremeData(stockFullId,ConstantsInfo.MonthDataType);
		            if(smonth!=null)
		            	ExcelCommon.create07Cell(wb, rowDayData, 6, sday.getDate().toString(), ConstantsInfo.NoColor);
		                    
		            
	    			List<StockPoint> stockDayPoint=new ArrayList<StockPoint>();
	    			List<StockPoint> stockWeekPoint=new ArrayList<StockPoint>();
	    			List<StockPoint> stockMonthPoint=new ArrayList<StockPoint>();
	    			List<String> stockExtremeDate=new ArrayList<String>();
	    						
	    			//最近三个月时间
	    	        Date dt=new Date();
	    	        String  cDate  = dateTimer.getAddDate(dt,-90);
	    			//取出最近所有极点数据
	    			stockDayPoint=spDao.getRecentPointStock(stockFullId,ConstantsInfo.DayDataType,cDate);
	    			stockWeekPoint=spDao.getRecentPointStock(stockFullId, ConstantsInfo.WeekDataType,cDate);
	    			stockMonthPoint=spDao.getRecentPointStock(stockFullId,  ConstantsInfo.MonthDataType,cDate);
	    			if(stockDayPoint==null) {
	    				continue;
	    			}
	    			
	    			//取出最近所有极点时间
	    			stockExtremeDate=spDao.getRecentExtremeDate(stockFullId,cDate);
	    			
	    		  	String showtext="";
	    			int extremeCol=stockLabelNum;	
	    		
	    			//日时间与极点 hash 
	                HashMap<String, StockPoint> stockDayDateExist = new HashMap<String, StockPoint>(); 	
	    			//周时间与极点 hash 
	                HashMap<String, StockPoint> stockWeekDateExist = new HashMap<String, StockPoint>(); 
	               //月时间与极点 hash 
	                HashMap<String, StockPoint> stockMonthDateExist = new HashMap<String, StockPoint>(); 
	                for(Iterator itDay = stockDayPoint.iterator();itDay.hasNext();)
		 	    	{
		 	           StockPoint sPointDay=(StockPoint)itDay.next();	 	            
		 	           stockDayDateExist.put(sPointDay.getExtremeDate().toString(), sPointDay);
		 	    	}
	                
	    			for(Iterator itWeek = stockWeekPoint.iterator();itWeek.hasNext();)
		 	    	{
		 	           StockPoint sPointWeek=(StockPoint)itWeek.next();	 	            
		 	           stockWeekDateExist.put(sPointWeek.getExtremeDate().toString(), sPointWeek);
		 	    	}
	    		
	    			for(Iterator itMonth = stockMonthPoint.iterator();itMonth.hasNext();)
		 	    	{	
		 	            StockPoint sPointMonth=(StockPoint)itMonth.next();	 	            
		 	            stockMonthDateExist.put(sPointMonth.getExtremeDate().toString(), sPointMonth);
		 	    	}
	    			
	    			int type=0;  			
	    			////判断日，周，月极值点是否重复
	    			int day=0;
	    			int week=0;
	    			int month=0;
	    			int dayweek=0;
	    			int dayweekmonth=0;
	    			int daymonth=0;
	    			int weekmonth=0;
	    			int pointdefault=0;
	    			StockPoint sPointDay,sPointWeek,sPointMonth=null;
	    			for(Iterator itDate = stockExtremeDate.iterator();itDate.hasNext();)
	    			{
	    				int dayType=0;
	    				int weekType=0;
	    				int monthType=0;
	    				String extremeDate = (String) itDate.next();
	    				 sPointDay=stockDayDateExist.get(extremeDate);
		            	if(sPointDay != null)
		            	{
		            		dayType=1;//ConstantsInfo.ExtremeDateDay;
		            	}
	    				sPointWeek=stockWeekDateExist.get(extremeDate);
		            	if(sPointWeek!=null)
		            	{
		            		weekType=1;//ConstantsInfo.ExtremeDateWeek;
		            	}
		            	sPointMonth=stockMonthDateExist.get(extremeDate);
		            	if(sPointMonth!=null)
		            	{
		            		monthType=1;//ConstantsInfo.ExtremeDateDayWeekMonth;
		            	}
		            	
		            	if(dayType==1)//日
		            	{
		            		if(weekType==1)//周
		            		{
		            			if(monthType==1)		            			
		            				type=ConstantsInfo.ExtremeDateDayWeekMonth;	//日周月	         
		            			else
		            				type=ConstantsInfo.ExtremeDateDayWeek;//日周
		            		}
		            		else
		            		{
		            			if(monthType==1)		            			
		            				type=ConstantsInfo.ExtremeDateDayMonth;	//日月         
		            			else
		            				type=ConstantsInfo.ExtremeDateDay;//日
		            		}
		            	}
		            	else
		            	{
		            		if(weekType==1)//周
		            		{
		            			if(monthType==1)		            			
		            				type=ConstantsInfo.ExtremeDateWeekMonth;	//周月	         
		            			else
		            				type=ConstantsInfo.ExtremeDateWeek;//周
		            		}
		            		else
		            		{
		            			if(monthType==1)		            			
		            				type=ConstantsInfo.ExtremeDateMonth;//月         
		            			
		            		}
		            		
		            	}
		            	String sDayRatio,sWeekRatio,sMonthRatio=null;	
		            	
		            	/*
		            	if(sPointDay!=null)
		            		System.out.println("day:"+sPointDay.getExtremeDate());
		            	if(sPointWeek!=null)
		            		System.out.println("week:"+sPointWeek.getExtremeDate());
		            	if(sPointMonth!=null)
		            		System.out.println("month"+sPointMonth.getExtremeDate());
		            	*/
		            	switch(type)
		            	{
		            	case ConstantsInfo.ExtremeDateDay:
		            		 
			            	sDayRatio= ddfDay.format(sPointDay.getRatio());
			            	//excel显示极值，比例
			            	showtext="日："+sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%)"; 
			            	if(ConstantsInfo.DEBUG)
				        	{
				            	System.out.println("极值点时间："+sPointDay.getExtremeDate());
				            	System.out.println("极值价格："+sPointDay.getExtremePrice()); 
				            	System.out.println("比例："+sPointDay.getRatio()); 
				            	System.out.println(showtext);	
				        	}
			            	
			            	extremeCol = stockDateColumnmap.get(sPointDay.getExtremeDate().toString());
			            	//System.out.println("Day极值点时间："+extremeDate);
			            	ExcelCommon.create07Cell(wb, rowDayData, extremeCol, showtext, ConstantsInfo.REDColor); 
					       
					         day++;
		            		break;
		            	case ConstantsInfo.ExtremeDateDayWeek:
		            		 
			            	sDayRatio = ddfDay.format(sPointDay.getRatio());
			            	sWeekRatio = ddfDay.format(sPointWeek.getRatio());
			            	//System.out.println("DayWeek极值点时间："+extremeDate);
			            	//excel显示极值，比例
			            	showtext="日周："+sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%,"+sWeekRatio+"%)"; 
			            	extremeCol = stockDateColumnmap.get(sPointDay.getExtremeDate().toString());
			            	ExcelCommon.create07Cell(wb, rowDayData, extremeCol, showtext, ConstantsInfo.BLUEColor); 
			            	dayweek++;
		            		break;
		            	case ConstantsInfo.ExtremeDateDayWeekMonth:
		            		
			            	sDayRatio= ddfDay.format(sPointDay.getRatio());
			            	sWeekRatio= ddfDay.format(sPointWeek.getRatio());
			            	sMonthRatio= ddfDay.format(sPointMonth.getRatio());
			            	//System.out.println("DayWeekMonth极值点时间："+extremeDate);
			            	//excel显示极值，比例
			            	showtext="日周月："+sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%,"+sWeekRatio+"%,"+sMonthRatio+"%)"; 
			            	extremeCol = stockDateColumnmap.get(sPointDay.getExtremeDate().toString());
			            	ExcelCommon.create07Cell(wb, rowDayData, extremeCol, showtext,  ConstantsInfo.ORANGEColor); 
			            	
			            	dayweekmonth++;
			            	break;
		            	case ConstantsInfo.ExtremeDateWeek:		            		 
			            	sWeekRatio= ddfDay.format(sPointWeek.getRatio()) ;
			            	//excel显示极值，比例
			            	showtext="周："+sPointWeek.getExtremeDate()+":"+Float.toString(sPointWeek.getExtremePrice())+"("+sWeekRatio+"%)"; 
			            	//System.out.println("Week极值点时间："+extremeDate);
			            	extremeCol = stockDateColumnmap.get(sPointWeek.getExtremeDate().toString());
			            	ExcelCommon.create07Cell(wb, rowDayData, extremeCol, showtext,  ConstantsInfo.CYANColor); 
					         
					         week++;
					         break;
		            	case ConstantsInfo.ExtremeDateMonth:		            		 
		            		sMonthRatio= ddfDay.format(sPointMonth.getRatio()) ;
			            	//excel显示极值，比例
		            		//System.out.println("Month极值点时间："+extremeDate);
			            	showtext="月："+sPointMonth.getExtremeDate()+":"+Float.toString(sPointMonth.getExtremePrice())+"("+sMonthRatio+"%)"; 
			            	extremeCol = stockDateColumnmap.get(sPointMonth.getExtremeDate().toString());
			            	ExcelCommon.create07Cell(wb, rowDayData, extremeCol, showtext, ConstantsInfo.GRAYColor); 
					       	
					         month++;
					         break;
		            	case ConstantsInfo.ExtremeDateDayMonth:
		            		
		            		sDayRatio = ddfDay.format(sPointDay.getRatio());
			            	sMonthRatio = ddfDay.format(sPointMonth.getRatio());
			            
			            	//excel显示极值，比例
			            	showtext="日月："+sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%,"+sMonthRatio+"%)"; 
			            	extremeCol = stockDateColumnmap.get(sPointDay.getExtremeDate().toString());
			            	ExcelCommon.create07Cell(wb, rowDayData, extremeCol, showtext,  ConstantsInfo.GRAYColor); //cellStyleDayMonth
			          
		            		daymonth++;		 
		            		//System.out.println("DayMonth极值点时间："+extremeDate);
		            		break;
		            	case ConstantsInfo.ExtremeDateWeekMonth:
		            		sWeekRatio = ddfDay.format(sPointWeek.getRatio());
			            	sMonthRatio = ddfDay.format(sPointMonth.getRatio());
			            
			            	//excel显示极值，比例
			            	showtext="周月："+sPointWeek.getExtremeDate()+":"+Float.toString(sPointWeek.getExtremePrice())+"("+sWeekRatio+"%,"+sMonthRatio+"%)"; 
			            	extremeCol = stockDateColumnmap.get(sPointWeek.getExtremeDate().toString());
			            	ExcelCommon.create07Cell(wb, rowDayData, extremeCol, showtext,  ConstantsInfo.GRAYColor); //cellStyleWeekMonth
		            		
		            		weekmonth++;	
		            		//System.out.println("WeekMonth极值点时间："+extremeDate);
		            		break;
		            	default:
		            		pointdefault++;	
		            		//System.out.println("default极值点时间："+extremeDate);
		            		break;	            		
		            	}	
		            	
	    			}	
	    			
	    			if(ConstantsInfo.DEBUG)
		        	{
		    			System.out.println("fullId： "+stockFullId+" Day points："+day);
		    			System.out.println("fullId： "+stockFullId+" Week points："+week);
		    			System.out.println("fullId： "+stockFullId+" Month points："+month);
		    			System.out.println("fullId： "+stockFullId+" DayWeek："+dayweek);
		    			System.out.println("fullId： "+stockFullId+" DayWeekMonth："+dayweekmonth);
		    			System.out.println("fullId： "+stockFullId+" DayMonth："+daymonth);
		    			System.out.println("fullId： "+stockFullId+" WeekMonth："+weekmonth);
		    			System.out.println("fullId： "+stockFullId+" pointdefault："+pointdefault);
		    			System.out.println("extremeCol:"+extremeCol);
		        	}

	   			}

	            wb.write(fileOStream);
	            fileOStream.flush();
	            fileIStream.close();
	            fileOStream.close();   
	   		 } 
    }   
    
    
   
    
    
  //根据行业区分
    private void exportExcelAllStockForMarket(String filePath, String fileName,HashMap<String, Integer> stockDateColumnmap) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException 
    {    
         FileOutputStream fileOStream=null;

         List<String> listIndustryStock = new ArrayList<String>();        
        List<StockIndustry> listIndustry = new ArrayList<StockIndustry>(); 
        	
        	 // 输入流   
       	 FileInputStream fileIStream = new FileInputStream(filePath + fileName);  
       	 XSSFWorkbook wb = new XSSFWorkbook(fileIStream);   
       	 XSSFSheet sheet = wb.getSheetAt(0);    
       	 //输出流   
       	fileOStream = new FileOutputStream(filePath + fileName);
       	List<StockMarket> listStockMarket = new ArrayList<StockMarket>(); 
       	
       	 listStockMarket=sbDao.getStockMarket(ConstantsInfo.StockMarket);
     	NumberFormat ddfDay=null;
    	//比例保留两位小数点
    	ddfDay=NumberFormat.getNumberInstance() ; 
    	ddfDay.setMaximumFractionDigits(2);
       	//先分析大盘
         //大盘指数 一行        
       	Row rowIndustry = sheet.createRow(stockRow);  
       	stockRow++;
       	ExcelCommon.create07Cell(wb, rowIndustry, 0, "大盘指数",  ConstantsInfo.NoColor);  
       	int stockType=0;
       	System.out.println(listStockMarket.size());
         	for(Iterator itMarket = listStockMarket.iterator();itMarket.hasNext();)
         	{
         		StockMarket sMarket = (StockMarket)itMarket.next();	
         		
   				String stockFullId = (String) sMarket.getCode().toString();	
   		   			 				

   				String stockName=sbDao.getStockNameFromIndustryTable(stockFullId);			
   			
   				System.out.println("fullId："+stockFullId+" 名字："+stockName);
   				stockLogger.logger.debug("fullId："+stockFullId+" 名字："+stockName);
   				//每个股票
   				Row rowDayData = sheet.createRow(stockRow);  
   				stockRow++;
   				ExcelCommon.create07Cell(wb, rowDayData, 0,  Integer.toString(stockRow), ConstantsInfo.NoColor);  
   				ExcelCommon.create07Cell(wb, rowDayData, 1, stockFullId, ConstantsInfo.NoColor);  
   				ExcelCommon.create07Cell(wb, rowDayData, 2, stockName, ConstantsInfo.NoColor); 	            
	    
		        //当前极值点
	            StockData sday=getCurExtremeData(stockFullId,ConstantsInfo.DayDataType);
	            if(sday!=null)
	            	ExcelCommon.create07Cell(wb, rowDayData, 4, sday.getDate().toString(), ConstantsInfo.NoColor); 	
	            StockData sweek=getCurExtremeData(stockFullId,ConstantsInfo.WeekDataType);
	            if(sweek!=null)
	            	ExcelCommon.create07Cell(wb, rowDayData, 5, sday.getDate().toString(), ConstantsInfo.NoColor);
	            StockData smonth=getCurExtremeData(stockFullId,ConstantsInfo.MonthDataType);
	            if(smonth!=null)
	            	ExcelCommon.create07Cell(wb, rowDayData, 6, sday.getDate().toString(), ConstantsInfo.NoColor);
	                    
	            
    			List<StockPoint> stockDayPoint=new ArrayList<StockPoint>();
    			List<StockPoint> stockWeekPoint=new ArrayList<StockPoint>();
    			List<StockPoint> stockMonthPoint=new ArrayList<StockPoint>();
    			List<String> stockExtremeDate=new ArrayList<String>();
    						
    			//最近三个月时间
    	        Date dt=new Date();
    	        String  cDate  = dateTimer.getAddDate(dt,-90);
    			//取出最近所有极点数据
    			stockDayPoint=spDao.getRecentPointStock(stockFullId,ConstantsInfo.DayDataType,cDate);
    			stockWeekPoint=spDao.getRecentPointStock(stockFullId, ConstantsInfo.WeekDataType,cDate);
    			stockMonthPoint=spDao.getRecentPointStock(stockFullId,  ConstantsInfo.MonthDataType,cDate);
    			if(stockDayPoint==null) {
    				continue;
    			}
    			
    			//取出最近所有极点时间
    			stockExtremeDate=spDao.getRecentExtremeDate(stockFullId,cDate);
    			
	        	
    		  	String showtext="";
    			int extremeCol=stockLabelNum;	
    		
    			//日时间与极点 hash 
                HashMap<String, StockPoint> stockDayDateExist = new HashMap<String, StockPoint>(); 	
    			//周时间与极点 hash 
                HashMap<String, StockPoint> stockWeekDateExist = new HashMap<String, StockPoint>(); 
               //月时间与极点 hash 
                HashMap<String, StockPoint> stockMonthDateExist = new HashMap<String, StockPoint>(); 
                for(Iterator itDay = stockDayPoint.iterator();itDay.hasNext();)
	 	    	{
	 	           StockPoint sPointDay=(StockPoint)itDay.next();	 	            
	 	           stockDayDateExist.put(sPointDay.getExtremeDate().toString(), sPointDay);
	 	    	}
                
    			for(Iterator itWeek = stockWeekPoint.iterator();itWeek.hasNext();)
	 	    	{
	 	           StockPoint sPointWeek=(StockPoint)itWeek.next();	 	            
	 	           stockWeekDateExist.put(sPointWeek.getExtremeDate().toString(), sPointWeek);
	 	    	}
    		
    			for(Iterator itMonth = stockMonthPoint.iterator();itMonth.hasNext();)
	 	    	{	
	 	            StockPoint sPointMonth=(StockPoint)itMonth.next();	 	            
	 	            stockMonthDateExist.put(sPointMonth.getExtremeDate().toString(), sPointMonth);
	 	    	}
    			
    			int type=0;  			
    			////判断日，周，月极值点是否重复
    			int day=0;
    			int week=0;
    			int month=0;
    			int dayweek=0;
    			int dayweekmonth=0;
    			int daymonth=0;
    			int weekmonth=0;
    			int pointdefault=0;
    			StockPoint sPointDay,sPointWeek,sPointMonth=null;
    			for(Iterator itDate = stockExtremeDate.iterator();itDate.hasNext();)
    			{
    				int dayType=0;
    				int weekType=0;
    				int monthType=0;
    				String extremeDate = (String) itDate.next();
    				 sPointDay=stockDayDateExist.get(extremeDate);
	            	if(sPointDay != null)
	            	{
	            		dayType=1;//ConstantsInfo.ExtremeDateDay;
	            	}
    				sPointWeek=stockWeekDateExist.get(extremeDate);
	            	if(sPointWeek!=null)
	            	{
	            		weekType=1;//ConstantsInfo.ExtremeDateWeek;
	            	}
	            	sPointMonth=stockMonthDateExist.get(extremeDate);
	            	if(sPointMonth!=null)
	            	{
	            		monthType=1;//ConstantsInfo.ExtremeDateDayWeekMonth;
	            	}
	            	
	            	if(dayType==1)//日
	            	{
	            		if(weekType==1)//周
	            		{
	            			if(monthType==1)		            			
	            				type=ConstantsInfo.ExtremeDateDayWeekMonth;	//日周月	         
	            			else
	            				type=ConstantsInfo.ExtremeDateDayWeek;//日周
	            		}
	            		else
	            		{
	            			if(monthType==1)		            			
	            				type=ConstantsInfo.ExtremeDateDayMonth;	//日月         
	            			else
	            				type=ConstantsInfo.ExtremeDateDay;//日
	            		}
	            	}
	            	else
	            	{
	            		if(weekType==1)//周
	            		{
	            			if(monthType==1)		            			
	            				type=ConstantsInfo.ExtremeDateWeekMonth;	//周月	         
	            			else
	            				type=ConstantsInfo.ExtremeDateWeek;//周
	            		}
	            		else
	            		{
	            			if(monthType==1)		            			
	            				type=ConstantsInfo.ExtremeDateMonth;//月         
	            			
	            		}
	            		
	            	}
	            	String sDayRatio,sWeekRatio,sMonthRatio=null;	
	            	
	            	/*
	            	if(sPointDay!=null)
	            		System.out.println("day:"+sPointDay.getExtremeDate());
	            	if(sPointWeek!=null)
	            		System.out.println("week:"+sPointWeek.getExtremeDate());
	            	if(sPointMonth!=null)
	            		System.out.println("month"+sPointMonth.getExtremeDate());
	            	*/
	            	switch(type)
	            	{
	            	case ConstantsInfo.ExtremeDateDay:
	            		 
		            	sDayRatio= ddfDay.format(sPointDay.getRatio());
		            	//excel显示极值，比例
		            	showtext="日："+sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%)"; 
		            	if(ConstantsInfo.DEBUG)
			        	{
			            	System.out.println("极值点时间："+sPointDay.getExtremeDate());
			            	System.out.println("极值价格："+sPointDay.getExtremePrice()); 
			            	System.out.println("比例："+sPointDay.getRatio()); 
			            	System.out.println(showtext);	
			        	}
		            	
		            	extremeCol = stockDateColumnmap.get(sPointDay.getExtremeDate().toString());
		            	//System.out.println("Day极值点时间："+extremeDate);
		            	//红 
		            	if(sPointDay.getWillFlag() == 1) //上涨	
		            		ExcelCommon.create07Cell(wb, rowDayData, extremeCol, showtext, ConstantsInfo.REDColor); 
		            	else 
		            		ExcelCommon.create07Cell(wb, rowDayData, extremeCol, showtext,  ConstantsInfo.BLUEColor);
				       
				         day++;
	            		break;
	            	case ConstantsInfo.ExtremeDateDayWeek:
	            		 
		            	sDayRatio = ddfDay.format(sPointDay.getRatio());
		            	sWeekRatio = ddfDay.format(sPointWeek.getRatio());
		            	
		            	//excel显示极值，比例
		            	showtext="日周："+sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%,"+sWeekRatio+"%)"; 
		            	extremeCol = stockDateColumnmap.get(sPointDay.getExtremeDate().toString());
		            	
		            	if(sPointDay.getWillFlag() == 1) //上涨	
		            		ExcelCommon.create07Cell(wb, rowDayData, extremeCol, showtext, ConstantsInfo.REDColor); 
		            	else 
		            		ExcelCommon.create07Cell(wb, rowDayData, extremeCol, showtext, ConstantsInfo.BLUEColor);
				       
		            	dayweek++;
	            		break;
	            	case ConstantsInfo.ExtremeDateDayWeekMonth:
	            		
		            	sDayRatio= ddfDay.format(sPointDay.getRatio());
		            	sWeekRatio= ddfDay.format(sPointWeek.getRatio());
		            	sMonthRatio= ddfDay.format(sPointMonth.getRatio());
		            	//System.out.println("DayWeekMonth极值点时间："+extremeDate);
		            	//excel显示极值，比例
		            	showtext="日周月："+sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%,"+sWeekRatio+"%,"+sMonthRatio+"%)"; 
		            	extremeCol = stockDateColumnmap.get(sPointDay.getExtremeDate().toString());
		            
		            	if(sPointDay.getWillFlag() == 1) //上涨	
		            		ExcelCommon.create07Cell(wb, rowDayData, extremeCol, showtext, ConstantsInfo.REDColor); 
		            	else 
		            		ExcelCommon.create07Cell(wb, rowDayData, extremeCol, showtext, ConstantsInfo.BLUEColor);
				       
		            	//create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleDayWeekMonth); 
		            	
		            	dayweekmonth++;
		            	break;
	            	case ConstantsInfo.ExtremeDateWeek:		            		 
		            	sWeekRatio= ddfDay.format(sPointWeek.getRatio()) ;
		            	//excel显示极值，比例
		            	showtext="周："+sPointWeek.getExtremeDate()+":"+Float.toString(sPointWeek.getExtremePrice())+"("+sWeekRatio+"%)"; 
		            	//System.out.println("Week极值点时间："+extremeDate);
		            	extremeCol = stockDateColumnmap.get(sPointWeek.getExtremeDate().toString());
		            	ExcelCommon.create07Cell(wb, rowDayData, extremeCol, showtext, ConstantsInfo.CYANColor); 
				         
				         week++;
				         break;
	            	case ConstantsInfo.ExtremeDateMonth:		            		 
	            		sMonthRatio= ddfDay.format(sPointMonth.getRatio()) ;
		            	//excel显示极值，比例
	            		//System.out.println("Month极值点时间："+extremeDate);
		            	showtext="月："+sPointMonth.getExtremeDate()+":"+Float.toString(sPointMonth.getExtremePrice())+"("+sMonthRatio+"%)"; 
		            	extremeCol = stockDateColumnmap.get(sPointMonth.getExtremeDate().toString());
		            	ExcelCommon.create07Cell(wb, rowDayData, extremeCol, showtext, ConstantsInfo.GRAYColor); 
				       	
				         month++;
				         break;
	            	case ConstantsInfo.ExtremeDateDayMonth:
	            		
	            		sDayRatio = ddfDay.format(sPointDay.getRatio());
		            	sMonthRatio = ddfDay.format(sPointMonth.getRatio());
		            
		            	//excel显示极值，比例
		            	showtext="日月："+sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%,"+sMonthRatio+"%)"; 
		            	extremeCol = stockDateColumnmap.get(sPointDay.getExtremeDate().toString());
		            	ExcelCommon.create07Cell(wb, rowDayData, extremeCol, showtext, ConstantsInfo.GRAYColor); //cellStyleDayMonth
		          
	            		daymonth++;		 
	            		//System.out.println("DayMonth极值点时间："+extremeDate);
	            		break;
	            	case ConstantsInfo.ExtremeDateWeekMonth:
	            		sWeekRatio = ddfDay.format(sPointWeek.getRatio());
		            	sMonthRatio = ddfDay.format(sPointMonth.getRatio());
		            
		            	//excel显示极值，比例
		            	showtext="周月："+sPointWeek.getExtremeDate()+":"+Float.toString(sPointWeek.getExtremePrice())+"("+sWeekRatio+"%,"+sMonthRatio+"%)"; 
		            	extremeCol = stockDateColumnmap.get(sPointWeek.getExtremeDate().toString());
		            	ExcelCommon.create07Cell(wb, rowDayData, extremeCol, showtext, ConstantsInfo.GRAYColor); //cellStyleWeekMonth
	            		
	            		weekmonth++;	
	            		//System.out.println("WeekMonth极值点时间："+extremeDate);
	            		break;
	            	default:
	            		pointdefault++;	
	            		//System.out.println("default极值点时间："+extremeDate);
	            		break;	            		
	            	}	
	            	
    			}	
    			
    			if(ConstantsInfo.DEBUG)
	        	{
	    			System.out.println("fullId： "+stockFullId+" Day points："+day);
	    			System.out.println("fullId： "+stockFullId+" Week points："+week);
	    			System.out.println("fullId： "+stockFullId+" Month points："+month);
	    			System.out.println("fullId： "+stockFullId+" DayWeek："+dayweek);
	    			System.out.println("fullId： "+stockFullId+" DayWeekMonth："+dayweekmonth);
	    			System.out.println("fullId： "+stockFullId+" DayMonth："+daymonth);
	    			System.out.println("fullId： "+stockFullId+" WeekMonth："+weekmonth);
	    			System.out.println("fullId： "+stockFullId+" pointdefault："+pointdefault);
	    			System.out.println("extremeCol:"+extremeCol);
	        	}

   			}

            wb.write(fileOStream);
            fileOStream.flush();
            fileIStream.close();
            fileOStream.close();   
	   		 
    }   
    
    private void exportStockInfo(Workbook wb,XSSFSheet sheet,String stockFullId,HashMap<String, Integer> stockDateColumnmap) throws SQLException, ClassNotFoundException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, IOException, ParseException
    {
    	NumberFormat ddfDay=null;
    	//比例保留两位小数点
    	ddfDay=NumberFormat.getNumberInstance() ; 
    	ddfDay.setMaximumFractionDigits(2);
    	int extremeCol=stockLabelNum;	
    	String stockName =null;
    	int enableMarginTrading=0;
    	StockSingle ss= sbDao.lookUpStockSingle(stockFullId);
    	int stockType=sbDao.getMarketType(stockFullId);
    	
    		
    	if (stockType == ConstantsInfo.DPMarket) {
    		stockName=sbDao.getStockNameFromIndustryTable(stockFullId);
    		enableMarginTrading =0;
    	}
    	else {
    		if(ss == null){
        		System.out.println("error");
        		return;
        	}
    		stockName = ss.getStockName(); 
    		enableMarginTrading = ss.getEnableMarginTrading();
    	}
		
		System.out.println("fullId："+stockFullId+" 名字："+stockName);
		stockLogger.logger.fatal("fullId："+stockFullId+" 名字："+stockName);
		//每个股票
		Row rowDayData = sheet.createRow(stockRow);  
		stockRow++;
		
		ExcelCommon.create07Cell(wb, rowDayData, 0,  Integer.toString(stockRow), ConstantsInfo.NoColor);  
		ExcelCommon.create07Cell(wb, rowDayData, 1, stockFullId,  ConstantsInfo.NoColor);  
		ExcelCommon.create07Cell(wb, rowDayData, 2, stockName,  ConstantsInfo.NoColor); 	            
		ExcelCommon.create07Cell(wb, rowDayData, 3, Integer.toString(enableMarginTrading), 0); 

		List<StockPoint> stockDayPoint=new ArrayList<StockPoint>();
		List<StockPoint> stockWeekPoint=new ArrayList<StockPoint>();
		List<StockPoint> stockMonthPoint=new ArrayList<StockPoint>();
		List<String> stockExtremeDate=new ArrayList<String>();
					
		//最近三个月时间
	    Date dt=new Date();
	    String  cDate  = dateTimer.getAddDate(dt,-90);
		//取出最近所有极点数据
		stockDayPoint=spDao.getRecentPointStock(stockFullId,ConstantsInfo.DayDataType,cDate);
		if(stockDayPoint==null) {
			return;
		}
		
		//不展示周，月
		//stockWeekPoint=spDao.getRecentPointStock(stockFullId, ConstantsInfo.WeekDataType,cDate);
		//stockMonthPoint=spDao.getRecentPointStock(stockFullId,  ConstantsInfo.MonthDataType,cDate);
		
		
		//取出最近所有极点时间
		stockExtremeDate=spDao.getRecentExtremeDate(stockFullId,cDate);
		
	  	String showtext="";	
	
		//日时间与极点 hash 
	    HashMap<String, StockPoint> stockDayDateExist = new HashMap<String, StockPoint>(); 	
		//周时间与极点 hash 
	    HashMap<String, StockPoint> stockWeekDateExist = new HashMap<String, StockPoint>(); 
	   //月时间与极点 hash 
	    HashMap<String, StockPoint> stockMonthDateExist = new HashMap<String, StockPoint>(); 
	    for(Iterator itDay = stockDayPoint.iterator();itDay.hasNext();)
	 	{
	        StockPoint sPointDay=(StockPoint)itDay.next();	 	            
	        stockDayDateExist.put(sPointDay.getExtremeDate().toString(), sPointDay);
	 	}
	   
	    /*
		for(Iterator itWeek = stockWeekPoint.iterator();itWeek.hasNext();)
	 	{
	        StockPoint sPointWeek=(StockPoint)itWeek.next();	 	            
	        stockWeekDateExist.put(sPointWeek.getExtremeDate().toString(), sPointWeek);
	 	}
	
		for(Iterator itMonth = stockMonthPoint.iterator();itMonth.hasNext();)
	 	{	
	         StockPoint sPointMonth=(StockPoint)itMonth.next();	 	            
	         stockMonthDateExist.put(sPointMonth.getExtremeDate().toString(), sPointMonth);
	 	}
		*/
		int type=0;  			
		////判断日，周，月极值点是否重复
		int day=0;
		int week=0;
		int month=0;
		int dayweek=0;
		int dayweekmonth=0;
		int daymonth=0;
		int weekmonth=0;
		int pointdefault=0;
		StockPoint sPointDay,sPointWeek,sPointMonth=null;
		int betweenDay=0;
	
		//判断 日 周 月极点是否有重合的
		prevEextremeDay = "";
	    for (int i= stockExtremeDate.size()-1;i>=0;i--)	
	    {
			int dayType=0;
			int weekType=0;
			int monthType=0;
				
			String extremeDate = stockExtremeDate.get(i);
			sPointDay=stockDayDateExist.get(extremeDate);
	    	if(sPointDay != null)
	    	{
	    		dayType=1;//ConstantsInfo.ExtremeDateDay;
	    	}
			sPointWeek=stockWeekDateExist.get(extremeDate);
	    	if(sPointWeek!=null)
	    	{
	    		weekType=1;//ConstantsInfo.ExtremeDateWeek;
	    	}
	    	sPointMonth=stockMonthDateExist.get(extremeDate);
	    	if(sPointMonth!=null)
	    	{
	    		monthType=1;//ConstantsInfo.ExtremeDateDayWeekMonth;
	    	}
	    	
	    	if(dayType==1)//日
	    	{
	    		if(weekType==1)//周
	    		{
	    			if(monthType==1)		            			
	    				type=ConstantsInfo.ExtremeDateDayWeekMonth;	//日周月	         
	    			else
	    				type=ConstantsInfo.ExtremeDateDayWeek;//日周
	    		}
	    		else
	    		{
	    			if(monthType==1)		            			
	    				type=ConstantsInfo.ExtremeDateDayMonth;	//日月         
	    			else
	    				type=ConstantsInfo.ExtremeDateDay;//日
	    		}
	    	}
	    	else
	    	{
	    		if(weekType==1)//周
	    		{
	    			if(monthType==1)		            			
	    				type=ConstantsInfo.ExtremeDateWeekMonth;	//周月	         
	    			else
	    				type=ConstantsInfo.ExtremeDateWeek;//周
	    		}
	    		else
	    		{
	    			if(monthType==1)		            			
	    				type=ConstantsInfo.ExtremeDateMonth;//月         
	    			
	    		}
	    		
	    	}
	    	String sDayRatio,sWeekRatio,sMonthRatio=null;	
	    	
	    	/*
	    	if(sPointDay!=null)
	    		System.out.println("day:"+sPointDay.getExtremeDate());
	    	if(sPointWeek!=null)
	    		System.out.println("week:"+sPointWeek.getExtremeDate());
	    	if(sPointMonth!=null)
	    		System.out.println("month"+sPointMonth.getExtremeDate());
	    	*/
	    	switch(type)
	    	{
	    	case ConstantsInfo.ExtremeDateDay:
	    		    	
	        	if(ConstantsInfo.DEBUG)
	        	{
	            	System.out.println("极值点时间："+sPointDay.getExtremeDate());
	            	System.out.println("极值价格："+sPointDay.getExtremePrice()); 
	            	System.out.println("比例："+sPointDay.getRatio()); 
	            	System.out.println(showtext);	
	        	}
	        	
	        	if(sPointDay!=null && stockDateColumnmap.containsKey(sPointDay.getExtremeDate().toString())) {
		        	extremeCol = stockDateColumnmap.get(sPointDay.getExtremeDate().toString());
		        	if(prevEextremeDay.equals(""))
		        		betweenDay=0;
		        	else
		        	    betweenDay= stockDateTimer.daysBetween(prevEextremeDay,sPointDay.getExtremeDate().toString(),ConstantsInfo.DayDataType);;
		        	prevEextremeDay = sPointDay.getExtremeDate().toString();
		        	//System.out.println("Day极值点时间："+extremeDate);
		        	//红 
		        	if(sPointDay.getWillFlag() == 1) //上涨	
		        		ExcelCommon.create07Cell(wb, rowDayData, extremeCol, "高:"+betweenDay,  ConstantsInfo.REDColor); 
		        	else 
		        		ExcelCommon.create07Cell(wb, rowDayData, extremeCol, "低:"+betweenDay, ConstantsInfo.BLUEColor);
	        	}
		         day++;
	    		break;
	    	case ConstantsInfo.ExtremeDateDayWeek:
	    		if(sPointDay!=null && stockDateColumnmap.containsKey(sPointDay.getExtremeDate().toString())) {
		        	extremeCol = stockDateColumnmap.get(sPointDay.getExtremeDate().toString());
		        	
		        	if(sPointDay.getWillFlag() == 1) //上涨	
		        		ExcelCommon.create07Cell(wb, rowDayData, extremeCol, "日周低", ConstantsInfo.REDColor); 
		        	else 
		        		ExcelCommon.create07Cell(wb, rowDayData, extremeCol, "日周高", ConstantsInfo.BLUEColor);
		    	}
	        	dayweek++;
	    		break;
	    	case ConstantsInfo.ExtremeDateDayWeekMonth:
	    		if(sPointDay!=null && stockDateColumnmap.containsKey(sPointDay.getExtremeDate().toString())) {
		        	extremeCol = stockDateColumnmap.get(sPointDay.getExtremeDate().toString());
		        
		        	if(sPointDay.getWillFlag() == 1) //上涨	
		        		ExcelCommon.create07Cell(wb, rowDayData, extremeCol,"日周月低", ConstantsInfo.REDColor); 
		        	else 
		        		ExcelCommon.create07Cell(wb, rowDayData, extremeCol, "日周月高", ConstantsInfo.BLUEColor);
		    	}
	        	dayweekmonth++;
	        	break;
	    	case ConstantsInfo.ExtremeDateWeek:		            		 
	    		if(sPointWeek!=null && stockDateColumnmap.containsKey(sPointWeek.getExtremeDate().toString())) {
		        	extremeCol = stockDateColumnmap.get(sPointWeek.getExtremeDate().toString());
			        if(sPointWeek.getWillFlag() == 1) //上涨	
			        		ExcelCommon.create07Cell(wb, rowDayData, extremeCol,"周低", ConstantsInfo.REDColor); 
			        else 
			        		ExcelCommon.create07Cell(wb, rowDayData, extremeCol, "周高", ConstantsInfo.BLUEColor);
	    		}
	    			week++;
		         break;
	    	case ConstantsInfo.ExtremeDateMonth:	
	    		if(sPointMonth!=null && stockDateColumnmap.containsKey(sPointMonth.getExtremeDate().toString())) {
		    		 extremeCol = stockDateColumnmap.get(sPointMonth.getExtremeDate().toString());      
		        	 if(sPointMonth.getWillFlag() == 1) //上涨	
			        		ExcelCommon.create07Cell(wb, rowDayData, extremeCol,"月低", ConstantsInfo.REDColor); 
			        	else 
			        		ExcelCommon.create07Cell(wb, rowDayData, extremeCol, "月高", ConstantsInfo.BLUEColor);
		    	}
		         month++;
		         break;
	    	case ConstantsInfo.ExtremeDateDayMonth:  		
	    		if(sPointDay!=null && stockDateColumnmap.containsKey(sPointDay.getExtremeDate().toString())) {
	        	extremeCol = stockDateColumnmap.get(sPointDay.getExtremeDate().toString());
	        	 if(sPointDay.getWillFlag() == 1) //上涨	
		        		ExcelCommon.create07Cell(wb, rowDayData, extremeCol,"日月低", ConstantsInfo.REDColor); 
		        	else 
		        		ExcelCommon.create07Cell(wb, rowDayData, extremeCol, "日月高", ConstantsInfo.BLUEColor);
	    		} 
	    		//System.out.println("DayMonth极值点时间："+extremeDate);
	    		break;
	    	case ConstantsInfo.ExtremeDateWeekMonth:
	    		if(sPointWeek!=null && stockDateColumnmap.containsKey(sPointWeek.getExtremeDate().toString())) {
	    		extremeCol = stockDateColumnmap.get(sPointWeek.getExtremeDate().toString());
	    		if(sPointWeek.getWillFlag() == 1) //上涨	
		        		ExcelCommon.create07Cell(wb, rowDayData, extremeCol,"周月低", ConstantsInfo.REDColor); 
		        	else 
		        		ExcelCommon.create07Cell(wb, rowDayData, extremeCol, "周月高", ConstantsInfo.BLUEColor);
	    		}
	    		//System.out.println("WeekMonth极值点时间："+extremeDate);
	    		break;
	    	default:
	    		pointdefault++;	
	    		//System.out.println("default极值点时间："+extremeDate);
	    		break;	            		
	    	}	
	    	
		}	
	
		 //当前极值点
	    StockData sday=getCurExtremeData(stockFullId,ConstantsInfo.DayDataType);
	    if(sday != null && stockDateColumnmap.containsKey(sday.getDate().toString())){
	    	//System.out.println("day:"+sday.getDate().toString());	
	    	extremeCol = stockDateColumnmap.get(sday.getDate().toString());	 
	    	if(prevEextremeDay.equals(""))
        		betweenDay=0;
        	else
        	    betweenDay= stockDateTimer.daysBetween(prevEextremeDay,sday.getDate().toString(),ConstantsInfo.DayDataType);;
        	prevEextremeDay = sday.getDate().toString();
	    	if(sday.getDataType() == 0)
	    		ExcelCommon.create07Cell(wb, rowDayData, extremeCol, "(低:"+betweenDay+")", ConstantsInfo.GREENColor); 	
	    	else
	    		ExcelCommon.create07Cell(wb, rowDayData, extremeCol, "(高:"+betweenDay+")", ConstantsInfo.ORANGEColor); 	
	    }
	    
	    /*
	    StockData sweek=getCurExtremeData(stockFullId,ConstantsInfo.WeekDataType);
	    if(sweek != null && stockDateColumnmap.containsKey(sweek.getDate().toString())){
	    	//System.out.println("week:"+sweek.getDate().toString());	
	    	extremeCol = stockDateColumnmap.get(sweek.getDate().toString());
	    	Cell cell = rowDayData.getCell(extremeCol);
	    	String value="";
	    	if(cell!=null) {
		    	value = cell.getStringCellValue();   
	    	}
	    	if(sweek.getDataType() == 0)
	    		ExcelCommon.create07Cell(wb, rowDayData, extremeCol, value+"(周低极点)", ConstantsInfo.GREENColor); 	
	    	else
	    		ExcelCommon.create07Cell(wb, rowDayData, extremeCol, value+"(周高极点)", ConstantsInfo.GREENColor); 
	
	    }
	    
	    StockData smonth=getCurExtremeData(stockFullId,ConstantsInfo.MonthDataType);
	    if(smonth != null && stockDateColumnmap.containsKey(smonth.getDate().toString())){
	    	//System.out.println(smonth.getDate().toString());
	    	
    		extremeCol = stockDateColumnmap.get(smonth.getDate().toString());  
    		Cell cell = rowDayData.getCell(extremeCol);
	    	String value="";
	    	if(cell!=null) {
		    	value = cell.getStringCellValue();   
	    	}
	    	if(smonth.getDataType() == 0)
	    		ExcelCommon.create07Cell(wb, rowDayData,extremeCol, value+"(月低极点)", ConstantsInfo.GREENColor);
	    	else
	    		ExcelCommon.create07Cell(wb, rowDayData,extremeCol, value+"(月高极点)", ConstantsInfo.GREENColor);	
	    }
	    */
	
	
    }
    
  //根据行业区分
    private void exportStockRecentlyForMarket(String filePathName,HashMap<String, Integer> stockDateColumnmap) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException 
    {    
         FileOutputStream fileOStream=null;
  	
         // 输入流   
       	 FileInputStream fileIStream = new FileInputStream(filePathName);  
       	 XSSFWorkbook wb = new XSSFWorkbook(fileIStream);   
       	 XSSFSheet sheet = wb.getSheetAt(0);    
       	 //输出流   
       	fileOStream = new FileOutputStream(filePathName);
       	List<StockMarket> listStockMarket = new ArrayList<StockMarket>(); 
       
       	listStockMarket=sbDao.getStockMarket(ConstantsInfo.StockMarket);
     
       	//先分析大盘
         //大盘指数 一行       
       	stockRow++;
		StockBaseFace sbFace = new StockBaseFace(0,"sh1111","","","","","","");
		ExcelCommon.writeExcelItemTitle(wb,sheet,"大盘指数",sbFace,stockRow);
     //  	ExcelCommon.writeExcelItemTitle(wb,sheet,"大盘指数",stockRow);
       	stockRow++;  
       	System.out.println(listStockMarket.size());
     	for(Iterator itMarket = listStockMarket.iterator();itMarket.hasNext();)
     	{
     		StockMarket sMarket = (StockMarket)itMarket.next();	
     		
			String stockFullId = (String) sMarket.getCode().toString();		   			 				
			exportStockInfo(wb,sheet,stockFullId,stockDateColumnmap);			
		}

        wb.write(fileOStream);
        fileOStream.flush();
        fileIStream.close();
        fileOStream.close();   
	   		 
    }   
    
    //根据行业区分
    private void exportStockRecentlyForIndustry(String filePathName,HashMap<String, Integer> stockDateColumnmap) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException 
    {    
		List<String> listIndustryStock = new ArrayList<String>();        
		List<StockIndustry> listIndustry = new ArrayList<StockIndustry>();         	
		
		 //得到当前所有行业
		listIndustry=sbDao.getStockIndustry();        	
		System.out.println("行业个数："+listIndustry.size());        	
		 
		 //第二行开始展示              
		Iterator itIndustry,ie;
		for(itIndustry = listIndustry.iterator();itIndustry.hasNext();)
		{
			// 输入流   
			 FileInputStream fileIStream = new FileInputStream(filePathName);  
			 XSSFWorkbook wb = new XSSFWorkbook(fileIStream);   
			 XSSFSheet sheet = wb.getSheetAt(0);  
			// System.out.println("最后一行："+sheet.getLastRowNum());   
			 //输入流   
			 FileOutputStream  fileOStream = new FileOutputStream(filePathName);
			 
			 //当前行业
			 StockIndustry indu= (StockIndustry)itIndustry.next();	
			 String induName=indu.getThirdname();
			 if(induName==null)
				 continue;				
			stockLogger.logger.debug("行业："+induName);
			   			
			//行业
			Row rowIndustry = sheet.createRow(stockRow);  
			ExcelCommon.create07Cell(wb, rowIndustry, 0, induName, ConstantsInfo.NoColor);  
			stockRow++;
			   			   	   			
			//所属行业所有股票
			//listIndustryStock=sbDao.getIndustryStock(induName);	  
			listIndustryStock=sbDao.getIndustryStockFromAllInfo(indu.getThirdcode());
			stockLogger.logger.debug("行业股票数："+listIndustryStock.size());
			for(ie=listIndustryStock.iterator();ie.hasNext();)
			{
				//测试作用	   			
				//if(stockRow>20)
				//	break;
				String stockFullId = (String) ie.next();				
				exportStockInfo(wb,sheet,stockFullId,stockDateColumnmap);
			}
		
		    wb.write(fileOStream);
		    fileOStream.flush();
		    fileIStream.close();
		    fileOStream.close();   
		 } 
    }   
    
      
    //根据概念区分
    private void exportStockRecentlyForConcept(String filePathName,HashMap<String, Integer> stockDateColumnmap) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException 
    {    
		      
		List<StockConcept> listConcept = new ArrayList<StockConcept>();         		
		 //得到当前所有行业
		listConcept=sbDao.getStockConcept();        	
		System.out.println("个概念数："+listConcept.size());        	
		 
		 //第二行开始展示              
		for (int i=0;i<listConcept.size();i++)	
	   	{
			// 输入流   
			 FileInputStream fileIStream = new FileInputStream(filePathName);  
			 XSSFWorkbook wb = new XSSFWorkbook(fileIStream);   
			 XSSFSheet sheet = wb.getSheetAt(0);  
			// System.out.println("最后一行："+sheet.getLastRowNum());   
			 //输入流   
			 FileOutputStream  fileOStream = new FileOutputStream(filePathName);
			 
				//当前概念
			 StockConcept concept= listConcept.get(i);	
			 String conceptName=concept.getName();
			 if(conceptName==null)
				 continue;
			 
			stockLogger.logger.fatal("概念："+conceptName);
			   			
			//行业
			Row rowIndustry = sheet.createRow(stockRow);  
			ExcelCommon.create07Cell(wb, rowIndustry, 0, conceptName, ConstantsInfo.NoColor);  
			stockRow++;
			   			   	   			
			//所属行业所有股票
			//所属概念所有股票
   			List<String> listConceptStock = new ArrayList<String>();     	
   			listConceptStock=sbDao.getConceptStock(conceptName);
   			stockLogger.logger.debug("概念股票数："+listConceptStock.size());

			for(Iterator ie=listConceptStock.iterator();ie.hasNext();)
			{
				//测试作用	   			
				//if(stockRow>20)
				//	break;
				String stockFullId = (String) ie.next();				
				exportStockInfo(wb,sheet,stockFullId,stockDateColumnmap);
			}
		
		    wb.write(fileOStream);
		    fileOStream.flush();
		    fileIStream.close();
		    fileOStream.close();   
		    listConceptStock = null;
		 } 
		  listConcept = null;
    }   
    

      
        /** 
         * 判断是否为数字 
         *  
         * @param str 
         * @return 
         */  
        public static boolean isNumeric(String str) {  
            if (str == null || "".equals(str.trim()) || str.length() > 10)  
                return false;  
            Pattern pattern = Pattern.compile("^0|[1-9]\\d*(\\.\\d+)?$");  
            return pattern.matcher(str).matches();  
        } 
               
        
        public  void exportRecentlyStockFromConcept(String filePath, String fileTime) throws IOException, SQLException, ClassNotFoundException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
        {
        	 HashMap<String, Integer> stockDateColumnmap = new HashMap<String, Integer>(); 
        	 ExcelCommon.createRecentExcel(filePath,fileTime,sdDao,stockDateColumnmap,0);
        	 String excleFileName=filePath+fileTime+"\\Stock_Concept_Simple_"+fileTime+".xlsx";
        	 stockRow = 1;
             exportStockRecentlyForMarket(excleFileName,stockDateColumnmap);
             exportStockRecentlyForConcept(excleFileName,stockDateColumnmap);//股票极值点显示一行 读取pointStock表
        }
        
        public  void exportRecentlyStockFromIndustry(String filePath, String fileTime) throws IOException, SQLException, ClassNotFoundException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
        {
        	 HashMap<String, Integer> stockDateColumnmap = new HashMap<String, Integer>(); 
        	 ExcelCommon.createRecentExcel(filePath,fileTime,sdDao,stockDateColumnmap,1);
        	 String excleFileName=filePath+fileTime+"\\Stock_Industry_Simple_"+fileTime+".xlsx";
        	 stockRow = 1;
             exportStockRecentlyForMarket(excleFileName,stockDateColumnmap);
             exportStockRecentlyForIndustry(excleFileName,stockDateColumnmap);//股票极值点显示一行 读取pointStock表
        }
        
        public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException {
    		
        	PropertyConfigurator.configure("stockConf/log4j_excelWriter.properties");
        	
    		HSSFWorkbook wb;
    		Date startDate = new Date();
    	//	wb=ew.exportExcelForStock();
    		
    		//XSSFWorkbook xwb;
    		//xwb=ew.exportExcelForAllStock();
    		
    	
   	     Connection stockBaseConn = DbConn.getConnDB("stockConf/conn_base_db.ini"); 
   	     Connection stockDataConn = DbConn.getConnDB("stockConf/conn_data_db.ini"); 
   	     Connection stockPointConn = DbConn.getConnDB("stockConf/conn_point_db.ini");  
    		
   		StockRecentWriter ew=new StockRecentWriter(stockBaseConn,stockDataConn,stockPointConn);
    		//当前路径
    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
            String dateNowStr = sdf.format(startDate);  
          //  String excleFileName="stockIndustryPoint_R_"+dateNowStr+".xlsx";
            
          // ew.exportRecentlyStockFromIndustry("export//",dateNowStr);
            ew.exportRecentlyStockFromConcept("export//",dateNowStr);
          
    		//当前路径
    	//	ew.exportExcelAllStockForConcept("load\\","stockPointConcept.xlsx");//股票极值点显示一行 读取pointStock表
    		
    		//wb=ew.exportExcelForStock("sh60002",null,null,null);
    		
    		
    		stockBaseConn.close();
    		stockPointConn.close();
    		stockDataConn.close();
    		
    		Date endDate = new Date();
    		// 计算两个时间点相差的秒数
    		long seconds =(endDate.getTime() - startDate.getTime())/1000;
    		System.out.println("总共耗时："+seconds+"秒");
    		System.out.println("end");
    		stockLogger.logger.debug("end");
    			
    	}
}
