package excel.rw;

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
import dao.StockConcept;
import dao.StockDataDao;
import dao.StockIndustry;
import dao.StockInformation;
import dao.StockInformationDao;
import dao.StockPoint;
import dao.StockPointDao;

public class ExcelWriter {
	
	static StockInformationDao siDao;
	static StockDataDao sdDao;
	static StockPointDao spDao;
	static StockBaseDao sbDao;
	static PointClass pClass=new PointClass();
	private POIFSFileSystem fs;
    private static HSSFWorkbook wb;
    private XSSFWorkbook xb;
    private HSSFSheet sheet;
    private Sheet xsheet;
    private HSSFRow row;
    private Row xrow;
	static int stockLabelNum=5;
	
	  static XSSFColor hfNormal;
      static XSSFColor hfCday;
      static XSSFColor hfCweek;
      static XSSFColor hfCmonth;
      static XSSFColor hfCDayWeek;
      static XSSFColor hfCDayWeekMonth;
      static XSSFColor hfYuce;
      
      // 文件头字体  
      static XSSFFont font0;  
      static XSSFFont fontDay; 
      static XSSFFont fontWeek;
      static XSSFFont fontMonth;
      static XSSFFont fontDayWeek;      
      static XSSFFont fontDayWeekMonth;  
      static XSSFFont fontYuce;
      
      static CellStyle cellStyleTitle;//第一行标题
      static CellStyle cellStyleGroup;//概念或行业 分组
      static CellStyle cellStyleNormal;
      static CellStyle cellStyleDay;
      static CellStyle cellStyleDayWeek;
      static CellStyle cellStyleDayWeekMonth;
      static CellStyle cellStyleWeek;
      static CellStyle cellStyleMonth;
      static CellStyle cellStyleYuce;
	
      
      public static CellStyle create07CellStyle(Workbook wb, XSSFFont font,int color) {    
          CellStyle cellStyle = wb.createCellStyle();  
          cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);  
          cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_BOTTOM);  
          cellStyle.setFont(font);  
        //  cellStyle.setFillBackgroundColor(HSSFColor.BLUE.index);
         // IndexedColors.YELLOW.getIndex()
          //前景色
          if((short)color>0)
          {	
          	cellStyle.setFillForegroundColor((short) color);            
          	cellStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);  
          }
         return cellStyle;
      }  
    
    public static void initFont(XSSFWorkbook wb)
    {
    	
    	hfNormal=new XSSFColor(java.awt.Color.BLACK);
        hfCday=new XSSFColor(java.awt.Color.RED);
        hfCweek=new XSSFColor(java.awt.Color.CYAN);
        hfCmonth=new XSSFColor(java.awt.Color.GRAY);
        hfCDayWeek=new XSSFColor(java.awt.Color.BLUE);
        hfCDayWeekMonth=new XSSFColor(java.awt.Color.ORANGE);
        hfYuce=new XSSFColor(java.awt.Color.GREEN);
        
        
    	 // 文件头字体  
        font0 = create07Fonts(wb, XSSFFont.BOLDWEIGHT_BOLD, "宋体", false,  
                (short) 400,hfNormal);  
        fontDay = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "宋体", false,  
                (short) 300,hfCday);  
        fontDayWeek = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "宋体", false,  
                (short) 300,hfCDayWeek);  
        fontDayWeekMonth = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "宋体", false,  
                (short) 300,hfCDayWeekMonth); 
        fontWeek = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "宋体", false,  
                (short) 300,hfCweek);  
        fontMonth = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "宋体", false,  
                (short) 300,hfCmonth);  
        fontYuce = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "宋体", false,  
                (short) 300,hfYuce);  
        
        //cell格式类型
        cellStyleTitle=create07CellStyle(wb,font0,IndexedColors.YELLOW.getIndex());
        cellStyleGroup=create07CellStyle(wb,font0,IndexedColors.BLUE.getIndex());
        cellStyleNormal=create07CellStyle(wb,font0,ConstantsInfo.XSSFNORMAL);
        cellStyleDay=create07CellStyle(wb,fontDay,ConstantsInfo.XSSFNORMAL);
        cellStyleWeek=create07CellStyle(wb,fontWeek,ConstantsInfo.XSSFNORMAL);
        cellStyleMonth=create07CellStyle(wb,fontMonth,ConstantsInfo.XSSFNORMAL);
        cellStyleDayWeek=create07CellStyle(wb,fontDayWeek,ConstantsInfo.XSSFNORMAL);
        cellStyleDayWeekMonth=create07CellStyle(wb,fontDayWeekMonth,ConstantsInfo.XSSFNORMAL);
        cellStyleYuce=create07CellStyle(wb,fontYuce,ConstantsInfo.XSSFNORMAL);

    }
   
    
    //创建excel
    public static void createExcel(String filePath, String fileName) throws IOException
    {
    	  // 输出流  
		OutputStream os = null;  
		int i=0;
		File file = new File(filePath);  
		System.out.println(filePath);
		if (!file.exists())
		{   
			 file.mkdir();   
		}    
		
		 os = new FileOutputStream(filePath + fileName);    
		 // 工作区
		 XSSFWorkbook wb = new XSSFWorkbook();   
		 // 创建第一个sheet     
		 XSSFSheet sheet= wb.createSheet("allstock");
		 
		  //列宽
         for(i=0;i<stockLabelNum+20;i++)
         	sheet.setColumnWidth(i, 6000);
         
      
         // 文件头字体  
         initFont(wb); 
         
         
         //创建第一行  
         Row rowHead = sheet.createRow(0);  
         
         int col=stockLabelNum;
         //给第一行添加文本  
         create07Cell(wb, rowHead, 0, "序号", cellStyleTitle);  
         create07Cell(wb, rowHead, 1, "代号", cellStyleTitle);  
         create07Cell(wb, rowHead, 2, "名称", cellStyleTitle); 	            
         create07Cell(wb, rowHead, 3, "两融", cellStyleTitle); 
         create07Cell(wb, rowHead, 4, "幅度", cellStyleTitle); 
         
         create07Cell(wb, rowHead, col, "上", cellStyleTitle); 
         create07Cell(wb, rowHead, col++, "前交叉日期", cellStyleTitle);
         create07Cell(wb, rowHead, col++, "日最近高点日期", cellStyleTitle);
         create07Cell(wb, rowHead, col++, "高点价格", cellStyleTitle); 
         create07Cell(wb, rowHead, col++, "上", cellStyleTitle); 
         create07Cell(wb, rowHead, col++, "下", cellStyleTitle); 
         create07Cell(wb, rowHead, col++, "转", cellStyleTitle); 
         create07Cell(wb, rowHead, col++, "轮", cellStyleTitle); 
         create07Cell(wb, rowHead, col++, "前交叉日期", cellStyleTitle);
         create07Cell(wb, rowHead, col++, "周最近高点日期", cellStyleTitle);
         create07Cell(wb, rowHead, col++, "高点价格", cellStyleTitle); 
         create07Cell(wb, rowHead, col++, "上", cellStyleTitle); 
         create07Cell(wb, rowHead, col++, "下", cellStyleTitle); 
         create07Cell(wb, rowHead, col++, "转", cellStyleTitle); 
         create07Cell(wb, rowHead, col++, "轮", cellStyleTitle);      

		 wb.write(os);
		 os.close();
    }
    
    
    //根据概念区分
    public static void exportExcelAllStockForConcept(String filePath, String fileName) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException 
    {    
            FileOutputStream fileOStream=null;

            List<String> listConceptStock = new ArrayList<String>();        
        	List<StockConcept> listConcept = new ArrayList<StockConcept>(); 
   
        	
        	//创建excel
        	createExcel(filePath,fileName);
        	
        	 //得到当前所有概念
        	listConcept=sbDao.getStockConcept();
        	System.out.println("概念个数："+listConcept.size());
        	
        	//比例保留两位小数点
        	NumberFormat ddfDay=null;
        	ddfDay=NumberFormat.getNumberInstance() ; 
        	ddfDay.setMaximumFractionDigits(2);
        	
            //第二行开始展示  
            int stockRow = 1;        
            Iterator itConcept,ie;
        	int monthpointcount=0;
        	int monthpoint1count=0;
	   		for(itConcept = listConcept.iterator();itConcept.hasNext();)
	   		{
	   			// 输入流   
				 FileInputStream  fileIStream = new FileInputStream(filePath + fileName);  
				 XSSFWorkbook wb = new XSSFWorkbook(fileIStream);   
				 XSSFSheet sheet = wb.getSheetAt(0);  
				//输出流	       
				 fileOStream = new FileOutputStream(filePath + fileName);
				 
				 initFont(wb);
				 
				 //当前概念
				 StockConcept concept= (StockConcept)itConcept.next();	
				 String conceptName=concept.getName();
				 if(conceptName==null)
					 continue;
				 
				stockLogger.logger.fatal("概念："+conceptName);
				   			
	   			//概念
	   			Row rowConcept = sheet.createRow(stockRow);  
	   			create07Cell(wb, rowConcept, 0, conceptName, cellStyleGroup);  
	   			stockRow++;
	   			
	   			DecimalFormat decimalFormat=new DecimalFormat(".00");
	   			//所属概念所有股票
	   			listConceptStock=sbDao.getConceptStock(conceptName);
	   			
	   			stockLogger.logger.debug("概念股票数："+listConceptStock.size());
	   			for(ie=listConceptStock.iterator();ie.hasNext();)
	   			{
	   				
	   			//测试作用
		   		//	if(stockRow>10)
		   		//		break;
	   				
	   				String stockFullId = (String) ie.next();	
	   				int isTableExist=sdDao.isExistStockTable(stockFullId,ConstantsInfo.TABLE_DATA_STOCK);
	   				if(isTableExist==0)//不存在
	   					continue;   				

	   				String stockName=sbDao.getStockNameFromConceptTable(stockFullId);			
	   				System.out.println("fullId："+stockFullId+" 名字："+stockName);
	   				stockLogger.logger.fatal("fullId："+stockFullId+" 名字："+stockName);
	   				
	   				//每个股票
	   				Row rowDayData = sheet.createRow(stockRow);  
	    			create07Cell(wb, rowDayData, 0,  Integer.toString(stockRow), cellStyleNormal);  
		            create07Cell(wb, rowDayData, 1, stockFullId, cellStyleNormal);  
		            create07Cell(wb, rowDayData, 2, stockName, cellStyleNormal); 	            
		           // create07Cell(wb, rowDayData, 3, si.getStockRegional(), font0,ConstantsInfo.XSSFNORMAL); 
		            stockRow++;
  
		       //    if(!stockFullId.equals("sh600000"))
				//		continue;
	    			//if(!stockFullId.equals("sh600000") && !stockFullId.equals("sz002142") && !stockFullId.equals("sz000001"))
	    			//		continue;
		            
	    			List<StockPoint> stockDayPoint=new ArrayList<StockPoint>();
	    			List<StockPoint> stockWeekPoint=new ArrayList<StockPoint>();
	    			List<StockPoint> stockMonthPoint=new ArrayList<StockPoint>();
	    			
	    			//取出所有极点数据
	    			stockDayPoint=spDao.getAllPointStock(stockFullId,ConstantsInfo.DayDataType);
	    			stockWeekPoint=spDao.getAllPointStock(stockFullId, ConstantsInfo.WeekDataType);
	    			stockMonthPoint=spDao.getAllPointStock(stockFullId,  ConstantsInfo.MonthDataType);
	    			if(stockDayPoint==null)
	    				continue;
	    			
	    			List<String> stockExtremeDate=new ArrayList<String>();					
	    			//取出所有极点时间
	    			stockExtremeDate=spDao.getAllExtremeDate(stockFullId);
	    			
	    			if(ConstantsInfo.DEBUG)
		        	{
		    			System.out.println("day size:"+stockDayPoint.size());
		    			System.out.println("week size:"+stockWeekPoint.size());
		    			System.out.println("month size:"+stockMonthPoint.size());    			
		        	}
	    		
	    			if(stockMonthPoint.size()>=3)
	    				monthpointcount++;
	    			else
	    				monthpoint1count++;
	    		//	System.out.println("month size:"+stockMonthPoint.size()); 
	    			
	    			
	    			int extremeCol=stockLabelNum;	
	    			//下一个极点预测值
	    			int daySize=stockDayPoint.size();
	    			int weekSize=stockWeekPoint.size();
	    			int monthSize=stockMonthPoint.size();
	    			//计算6个预测值
	    			String[] nextPointPrice=new String[6];
	    			String showtext=null;
	    			
	    			//日预测
	    			if(daySize>=2)
	    			{
	    				String nextDate=spDao.getNextPointDate(stockDayPoint.get(daySize-1).getExtremeDate().toString(),stockDayPoint.get(daySize-2).getExtremeDate().toString());
	    				showtext=nextDate+"(";
	    				float lastPrice=stockDayPoint.get(daySize-1).getExtremePrice();
	    				float priPrice=stockDayPoint.get(daySize-2).getExtremePrice();
	    				nextPointPrice[0]=decimalFormat.format((lastPrice-0.382*(lastPrice-priPrice)));
	    				nextPointPrice[1]=decimalFormat.format((lastPrice-0.5*(lastPrice-priPrice)));
	    				nextPointPrice[2]=decimalFormat.format((lastPrice-0.618*(lastPrice-priPrice)));
	    				nextPointPrice[3]=decimalFormat.format((lastPrice-0.75*(lastPrice-priPrice)));
	    				nextPointPrice[4]=decimalFormat.format((priPrice));
	    				nextPointPrice[5]=decimalFormat.format((lastPrice-1.08*(lastPrice-priPrice)));	
	    				System.out.println("日极值预测点时间："+nextDate);	
	    				for(int i=0;i<6;i++)
	    				{
	    					if(ConstantsInfo.DEBUG)
				        	{
				            	System.out.println("日极值预测点位"+i+":"+nextPointPrice[i]);	
				        	}
	    					showtext=showtext+nextPointPrice[i]+","; 
	    				}
	    				showtext=showtext+")";
		            	
		            	create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleYuce); 
		            	extremeCol++;
	    			}
	    			
	    			//周预测
	    			if(weekSize>=2)
	    			{
	    				String nextDate=spDao.getNextPointDate(stockWeekPoint.get(weekSize-1).getExtremeDate().toString(),stockWeekPoint.get(weekSize-2).getExtremeDate().toString());
	    				showtext=nextDate+"(";
	    				float lastPrice=stockWeekPoint.get(weekSize-1).getExtremePrice();
	    				float priPrice=stockWeekPoint.get(weekSize-2).getExtremePrice();
	    				nextPointPrice[0]=decimalFormat.format((lastPrice-0.382*(lastPrice-priPrice)));
	    				nextPointPrice[1]=decimalFormat.format((lastPrice-0.5*(lastPrice-priPrice)));
	    				nextPointPrice[2]=decimalFormat.format((lastPrice-0.618*(lastPrice-priPrice)));
	    				nextPointPrice[3]=decimalFormat.format((lastPrice-0.75*(lastPrice-priPrice)));
	    				nextPointPrice[4]=decimalFormat.format((priPrice));
	    				nextPointPrice[5]=decimalFormat.format((lastPrice-1.08*(lastPrice-priPrice)));	
	    				System.out.println("周极值预测点时间："+nextDate);	
	    				for(int i=0;i<6;i++)
	    				{
	    					if(ConstantsInfo.DEBUG)
				        	{
				            	System.out.println("周极值预测点位"+i+":"+nextPointPrice[i]);	
				        	}
	    					showtext=showtext+nextPointPrice[i]+","; 
	    				}
	    				showtext=showtext+")";
		            	create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleYuce); 
		            	extremeCol++;
	    			}
	    			
	    			//月预测
	    			if(monthSize>=2)
	    			{
	    				String nextDate=spDao.getNextPointDate(stockMonthPoint.get(monthSize-1).getExtremeDate().toString(),stockMonthPoint.get(monthSize-2).getExtremeDate().toString());
	    				showtext=nextDate+"(";
	    				float lastPrice=stockDayPoint.get(monthSize-1).getExtremePrice();
	    				float priPrice=stockDayPoint.get(monthSize-2).getExtremePrice();
	    				nextPointPrice[0]=decimalFormat.format((lastPrice-0.382*(lastPrice-priPrice)));
	    				nextPointPrice[1]=decimalFormat.format((lastPrice-0.5*(lastPrice-priPrice)));
	    				nextPointPrice[2]=decimalFormat.format((lastPrice-0.618*(lastPrice-priPrice)));
	    				nextPointPrice[3]=decimalFormat.format((lastPrice-0.75*(lastPrice-priPrice)));
	    				nextPointPrice[4]=decimalFormat.format((priPrice));
	    				nextPointPrice[5]=decimalFormat.format((lastPrice-1.08*(lastPrice-priPrice)));	
	    				System.out.println("月极值预测点时间："+nextDate);	
	    				for(int i=0;i<6;i++)
	    				{
	    					if(ConstantsInfo.DEBUG)
				        	{
				            	System.out.println("月极值预测点位"+i+":"+nextPointPrice[i]);	
				        	}
	    					showtext=showtext+nextPointPrice[i]+","; 
	    				}
	    				showtext=showtext+")";
		            	create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleYuce); 
		            	extremeCol++;
	    			}
	    			
	    			extremeCol=stockLabelNum+3;
	    			
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
	    			for(Iterator itDate = stockExtremeDate.iterator();itDate.hasNext();)
	    			{
	    				int dayType=0;
	    				int weekType=0;
	    				int monthType=0;
	    				String extremeDate = (String) itDate.next();
	    				StockPoint sPointDay=stockDayDateExist.get(extremeDate);
		            	if(sPointDay != null)
		            	{
		            		dayType=1;//ConstantsInfo.ExtremeDateDay;
		            	}
	    				StockPoint sPointWeek=stockWeekDateExist.get(extremeDate);
		            	if(sPointWeek!=null)
		            	{
		            		weekType=1;//ConstantsInfo.ExtremeDateWeek;
		            	}
		            	StockPoint sPointMonth=stockMonthDateExist.get(extremeDate);
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
		            	
		            	
		            	switch(type)
		            	{
		            	case ConstantsInfo.ExtremeDateDay:
		            		 
			            	sDayRatio= ddfDay.format(sPointDay.getRatio()) ;
			            	//excel显示极值，比例
			            	showtext="日："+sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%)"; 
			            	if(ConstantsInfo.DEBUG)
				        	{
				            	System.out.println("极值点时间："+sPointDay.getExtremeDate());
				            	System.out.println("极值价格："+sPointDay.getExtremePrice()); 
				            	System.out.println("比例："+sPointDay.getRatio()); 
				            	System.out.println(showtext);	
				        	}
			            	//System.out.println("Day极值点时间："+extremeDate);
					         create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleDay); 
					         extremeCol++;
					         day++;
		            		break;
		            	case ConstantsInfo.ExtremeDateDayWeek:
		            		 
			            	sDayRatio = ddfDay.format(sPointDay.getRatio());
			            	sWeekRatio = ddfDay.format(sPointWeek.getRatio());
			            	//System.out.println("DayWeek极值点时间："+extremeDate);
			            	//excel显示极值，比例
			            	showtext="日周："+sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%,"+sWeekRatio+"%)"; 
			            	
			            	create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleDayWeek); 
			            	extremeCol++;
			            	dayweek++;
		            		break;
		            	case ConstantsInfo.ExtremeDateDayWeekMonth:
		            		
			            	sDayRatio= ddfDay.format(sPointDay.getRatio());
			            	sWeekRatio= ddfDay.format(sPointWeek.getRatio());
			            	sMonthRatio= ddfDay.format(sPointMonth.getRatio());
			            	//System.out.println("DayWeekMonth极值点时间："+extremeDate);
			            	//excel显示极值，比例
			            	showtext="日周月："+sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%,"+sWeekRatio+"%,"+sMonthRatio+"%)"; 
			            	
			            	create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleDayWeekMonth); 
			            	extremeCol++;
			            	dayweekmonth++;
			            	break;
		            	case ConstantsInfo.ExtremeDateWeek:		            		 
			            	sWeekRatio= ddfDay.format(sPointWeek.getRatio()) ;
			            	//excel显示极值，比例
			            	showtext="周："+sPointWeek.getExtremeDate()+":"+Float.toString(sPointWeek.getExtremePrice())+"("+sWeekRatio+"%)"; 
			            	//System.out.println("Week极值点时间："+extremeDate);
					         create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleWeek); 
					         extremeCol++;
					         week++;
					         break;
		            	case ConstantsInfo.ExtremeDateMonth:		            		 
		            		sMonthRatio= ddfDay.format(sPointMonth.getRatio()) ;
			            	//excel显示极值，比例
		            		//System.out.println("Month极值点时间："+extremeDate);
			            	showtext="月："+sPointMonth.getExtremeDate()+":"+Float.toString(sPointMonth.getExtremePrice())+"("+sMonthRatio+"%)"; 
			            	
					         create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleMonth); 
					         extremeCol++;	
					         month++;
					         break;
		            	case ConstantsInfo.ExtremeDateDayMonth:
		            		
		            		sDayRatio = ddfDay.format(sPointDay.getRatio());
			            	sMonthRatio = ddfDay.format(sPointMonth.getRatio());
			            
			            	//excel显示极值，比例
			            	showtext="日月："+sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%,"+sMonthRatio+"%)"; 
			         
			            	create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleMonth); //cellStyleDayMonth
			            	
		            		 extremeCol++;	
		            		daymonth++;		 
		            		//System.out.println("DayMonth极值点时间："+extremeDate);
		            		break;
		            	case ConstantsInfo.ExtremeDateWeekMonth:
		            		sWeekRatio = ddfDay.format(sPointWeek.getRatio());
			            	sMonthRatio = ddfDay.format(sPointMonth.getRatio());
			            
			            	//excel显示极值，比例
			            	showtext="周月："+sPointWeek.getExtremeDate()+":"+Float.toString(sPointWeek.getExtremePrice())+"("+sWeekRatio+"%,"+sMonthRatio+"%)"; 
			            	create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleMonth); //cellStyleWeekMonth
		            		extremeCol++;	
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
	   		
	   		System.out.println("stock >3 count:"+monthpointcount);
	   		System.out.println("stock count:"+monthpoint1count);
	   		
    }   
    
    
    //根据行业区分
    public static void exportExcelAllStockForIndustry(String filePath, String fileName) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException 
    {    
            FileOutputStream fileOStream=null;

            List<String> listIndustryStock = new ArrayList<String>();        
        	List<StockIndustry> listIndustry = new ArrayList<StockIndustry>(); 
        	
        	//创建excel
        	createExcel(filePath,fileName);
        	
        	 //得到当前所有行业
        	listIndustry=sbDao.getStockIndustry();
        	System.out.println("行业个数："+listIndustry.size());
        	
        	NumberFormat ddfDay=null;
        	//比例保留两位小数点
        	ddfDay=NumberFormat.getNumberInstance() ; 
        	ddfDay.setMaximumFractionDigits(2);
            //第二行开始展示  
            int stockRow = 1;        
            Iterator itIndustry,ie;
	   		for(itIndustry = listIndustry.iterator();itIndustry.hasNext();)
	   		{
	   			// 输入流   
				 FileInputStream fileIStream = new FileInputStream(filePath + fileName);  
				 XSSFWorkbook wb = new XSSFWorkbook(fileIStream);   
				 XSSFSheet sheet = wb.getSheetAt(0);  
				// System.out.println("最后一行："+sheet.getLastRowNum());   
				 //输入流   
				 fileOStream = new FileOutputStream(filePath + fileName);
				
				 initFont(wb);
				 
				 //当前行业
				 StockIndustry indu= (StockIndustry)itIndustry.next();	
				 String induName=indu.getThirdname();
				 if(induName==null)
					 continue;				
				stockLogger.logger.debug("行业："+induName);
	   			   			
	   			//行业
	   			Row rowIndustry = sheet.createRow(stockRow);  
	   			create07Cell(wb, rowIndustry, 0, induName, cellStyleGroup);  
	   			stockRow++;
	   			
	   			DecimalFormat decimalFormat=new DecimalFormat(".00");
	   			   	   			
	   			//所属行业所有股票
	   			listIndustryStock=sbDao.getIndustryStockFromAllInfo(induName);	   		
	   			stockLogger.logger.debug("行业股票数："+listIndustryStock.size());
	   			for(ie=listIndustryStock.iterator();ie.hasNext();)
	   			{
	   				//测试作用
	   			
	   			//	if(stockRow>10)
	   			//		break;
	   				
	   				String stockFullId = (String) ie.next();	
	   			//	if(!stockFullId.equals("SH600354"))
	   			//		continue;
	   				int isTableExist=sdDao.isExistStockTable(stockFullId,ConstantsInfo.TABLE_DATA_STOCK);
	   				if(isTableExist==0)//不存在
	   					continue;   				

	   				String stockName=sbDao.getStockNameFromIndustryTable(stockFullId);			
	   			
	   				System.out.println("fullId："+stockFullId+" 名字："+stockName);
	   				stockLogger.logger.debug("fullId："+stockFullId+" 名字："+stockName);
	   				//每个股票
	   				Row rowDayData = sheet.createRow(stockRow);  
	    			create07Cell(wb, rowDayData, 0,  Integer.toString(stockRow), cellStyleNormal);  
		            create07Cell(wb, rowDayData, 1, stockFullId, cellStyleNormal);  
		            create07Cell(wb, rowDayData, 2, stockName, cellStyleNormal); 	            
		    
		            stockRow++;
  
		       //    if(!stockFullId.equals("sh600000"))
				//		continue;
	    			//if(!stockFullId.equals("sh600000") && !stockFullId.equals("sz002142") && !stockFullId.equals("sz000001"))
	    			//		continue;
		            
	    			List<StockPoint> stockDayPoint=new ArrayList<StockPoint>();
	    			List<StockPoint> stockWeekPoint=new ArrayList<StockPoint>();
	    			List<StockPoint> stockMonthPoint=new ArrayList<StockPoint>();
	    			List<String> stockExtremeDate=new ArrayList<String>();
	    						
	    			//取出所有极点数据
	    			stockDayPoint=spDao.getAllPointStock(stockFullId,ConstantsInfo.DayDataType);
	    			stockWeekPoint=spDao.getAllPointStock(stockFullId, ConstantsInfo.WeekDataType);
	    			stockMonthPoint=spDao.getAllPointStock(stockFullId,  ConstantsInfo.MonthDataType);
	    			if(stockDayPoint==null)
	    				continue;
	    			
	    			//取出所有极点时间
	    			stockExtremeDate=spDao.getAllExtremeDate(stockFullId);
	    			
	    			if(ConstantsInfo.DEBUG)
		        	{
		    			System.out.println("day size:"+stockDayPoint.size());
		    			System.out.println("week size:"+stockWeekPoint.size());
		    			System.out.println("month size:"+stockMonthPoint.size()); 
		        	}
	    		  	String showtext="";
	    			int extremeCol=stockLabelNum;	
	    			//下一个极点预测值
	    			int daySize=stockDayPoint.size();
	    			int weekSize=stockWeekPoint.size();
	    			int monthSize=stockMonthPoint.size();
	    			//计算6个预测值
	    			String[] nextPointPrice=new String[6];
	    			
	    			//日预测
	    			if(daySize>=2)
	    			{
	    				String nextDate=spDao.getNextPointDate(stockDayPoint.get(daySize-1).getExtremeDate().toString(),stockDayPoint.get(daySize-2).getExtremeDate().toString());
	    				showtext=nextDate+"(";
	    				float lastPrice=stockDayPoint.get(daySize-1).getExtremePrice();
	    				float priPrice=stockDayPoint.get(daySize-2).getExtremePrice();
	    				nextPointPrice[0]=decimalFormat.format((lastPrice-0.382*(lastPrice-priPrice)));
	    				nextPointPrice[1]=decimalFormat.format((lastPrice-0.5*(lastPrice-priPrice)));
	    				nextPointPrice[2]=decimalFormat.format((lastPrice-0.618*(lastPrice-priPrice)));
	    				nextPointPrice[3]=decimalFormat.format((lastPrice-0.75*(lastPrice-priPrice)));
	    				nextPointPrice[4]=decimalFormat.format((priPrice));
	    				nextPointPrice[5]=decimalFormat.format((lastPrice-1.08*(lastPrice-priPrice)));	
	    				System.out.println("日极值预测点时间："+nextDate);	
	    				for(int i=0;i<6;i++)
	    				{
	    					if(ConstantsInfo.DEBUG)
				        	{
				            	System.out.println("日极值预测点位"+i+":"+nextPointPrice[i]);	
				        	}
	    					showtext=showtext+nextPointPrice[i]+","; 
	    				}
	    				showtext=showtext+")";
		            	
		            	create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleYuce); 
		            	extremeCol++;
	    			}
	    			
	    			//周预测
	    			if(weekSize>=2)
	    			{
	    				String nextDate=spDao.getNextPointDate(stockWeekPoint.get(weekSize-1).getExtremeDate().toString(),stockWeekPoint.get(weekSize-2).getExtremeDate().toString());
	    				showtext=nextDate+"(";
	    				float lastPrice=stockWeekPoint.get(weekSize-1).getExtremePrice();
	    				float priPrice=stockWeekPoint.get(weekSize-2).getExtremePrice();
	    				nextPointPrice[0]=decimalFormat.format((lastPrice-0.382*(lastPrice-priPrice)));
	    				nextPointPrice[1]=decimalFormat.format((lastPrice-0.5*(lastPrice-priPrice)));
	    				nextPointPrice[2]=decimalFormat.format((lastPrice-0.618*(lastPrice-priPrice)));
	    				nextPointPrice[3]=decimalFormat.format((lastPrice-0.75*(lastPrice-priPrice)));
	    				nextPointPrice[4]=decimalFormat.format((priPrice));
	    				nextPointPrice[5]=decimalFormat.format((lastPrice-1.08*(lastPrice-priPrice)));	
	    				System.out.println("周极值预测点时间："+nextDate);	
	    				for(int i=0;i<6;i++)
	    				{
	    					if(ConstantsInfo.DEBUG)
				        	{
				            	System.out.println("周极值预测点位"+i+":"+nextPointPrice[i]);	
				        	}
	    					showtext=showtext+nextPointPrice[i]+","; 
	    				}
	    				showtext=showtext+")";
		            	create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleYuce); 
		            	extremeCol++;
	    			}
	    			
	    			//月预测
	    			if(monthSize>=2)
	    			{
	    				String nextDate=spDao.getNextPointDate(stockMonthPoint.get(monthSize-1).getExtremeDate().toString(),stockMonthPoint.get(monthSize-2).getExtremeDate().toString());
	    				showtext=nextDate+"(";
	    				float lastPrice=stockDayPoint.get(monthSize-1).getExtremePrice();
	    				float priPrice=stockDayPoint.get(monthSize-2).getExtremePrice();
	    				nextPointPrice[0]=decimalFormat.format((lastPrice-0.382*(lastPrice-priPrice)));
	    				nextPointPrice[1]=decimalFormat.format((lastPrice-0.5*(lastPrice-priPrice)));
	    				nextPointPrice[2]=decimalFormat.format((lastPrice-0.618*(lastPrice-priPrice)));
	    				nextPointPrice[3]=decimalFormat.format((lastPrice-0.75*(lastPrice-priPrice)));
	    				nextPointPrice[4]=decimalFormat.format((priPrice));
	    				nextPointPrice[5]=decimalFormat.format((lastPrice-1.08*(lastPrice-priPrice)));	
	    				System.out.println("月极值预测点时间："+nextDate);	
	    				for(int i=0;i<6;i++)
	    				{
	    					if(ConstantsInfo.DEBUG)
				        	{
				            	System.out.println("月极值预测点位"+i+":"+nextPointPrice[i]);	
				        	}
	    					showtext=showtext+nextPointPrice[i]+","; 
	    				}
	    				showtext=showtext+")";
		            	create07Cell(wb, rowDayData, extremeCol, showtext,cellStyleYuce); 
		            	extremeCol++;
	    			}
	    			
	    			extremeCol=stockLabelNum+3;
	    			
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
	    			for(Iterator itDate = stockExtremeDate.iterator();itDate.hasNext();)
	    			{
	    				int dayType=0;
	    				int weekType=0;
	    				int monthType=0;
	    				String extremeDate = (String) itDate.next();
	    				StockPoint sPointDay=stockDayDateExist.get(extremeDate);
		            	if(sPointDay != null)
		            	{
		            		dayType=1;//ConstantsInfo.ExtremeDateDay;
		            	}
	    				StockPoint sPointWeek=stockWeekDateExist.get(extremeDate);
		            	if(sPointWeek!=null)
		            	{
		            		weekType=1;//ConstantsInfo.ExtremeDateWeek;
		            	}
		            	StockPoint sPointMonth=stockMonthDateExist.get(extremeDate);
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
		            	
		            	
		            	switch(type)
		            	{
		            	case ConstantsInfo.ExtremeDateDay:
		            		 
			            	sDayRatio= ddfDay.format(sPointDay.getRatio()) ;
			            	//excel显示极值，比例
			            	showtext="日："+sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%)"; 
			            	if(ConstantsInfo.DEBUG)
				        	{
				            	System.out.println("极值点时间："+sPointDay.getExtremeDate());
				            	System.out.println("极值价格："+sPointDay.getExtremePrice()); 
				            	System.out.println("比例："+sPointDay.getRatio()); 
				            	System.out.println(showtext);	
				        	}
			            	//System.out.println("Day极值点时间："+extremeDate);
					         create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleDay); 
					         extremeCol++;
					         day++;
		            		break;
		            	case ConstantsInfo.ExtremeDateDayWeek:
		            		 
			            	sDayRatio = ddfDay.format(sPointDay.getRatio());
			            	sWeekRatio = ddfDay.format(sPointWeek.getRatio());
			            	//System.out.println("DayWeek极值点时间："+extremeDate);
			            	//excel显示极值，比例
			            	showtext="日周："+sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%,"+sWeekRatio+"%)"; 
			            	
			            	create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleDayWeek); 
			            	extremeCol++;
			            	dayweek++;
		            		break;
		            	case ConstantsInfo.ExtremeDateDayWeekMonth:
		            		
			            	sDayRatio= ddfDay.format(sPointDay.getRatio());
			            	sWeekRatio= ddfDay.format(sPointWeek.getRatio());
			            	sMonthRatio= ddfDay.format(sPointMonth.getRatio());
			            	//System.out.println("DayWeekMonth极值点时间："+extremeDate);
			            	//excel显示极值，比例
			            	showtext="日周月："+sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%,"+sWeekRatio+"%,"+sMonthRatio+"%)"; 
			            	
			            	create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleDayWeekMonth); 
			            	extremeCol++;
			            	dayweekmonth++;
			            	break;
		            	case ConstantsInfo.ExtremeDateWeek:		            		 
			            	sWeekRatio= ddfDay.format(sPointWeek.getRatio()) ;
			            	//excel显示极值，比例
			            	showtext="周："+sPointWeek.getExtremeDate()+":"+Float.toString(sPointWeek.getExtremePrice())+"("+sWeekRatio+"%)"; 
			            	//System.out.println("Week极值点时间："+extremeDate);
					         create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleWeek); 
					         extremeCol++;
					         week++;
					         break;
		            	case ConstantsInfo.ExtremeDateMonth:		            		 
		            		sMonthRatio= ddfDay.format(sPointMonth.getRatio()) ;
			            	//excel显示极值，比例
		            		//System.out.println("Month极值点时间："+extremeDate);
			            	showtext="月："+sPointMonth.getExtremeDate()+":"+Float.toString(sPointMonth.getExtremePrice())+"("+sMonthRatio+"%)"; 
			            	
					         create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleMonth); 
					         extremeCol++;	
					         month++;
					         break;
		            	case ConstantsInfo.ExtremeDateDayMonth:
		            		
		            		sDayRatio = ddfDay.format(sPointDay.getRatio());
			            	sMonthRatio = ddfDay.format(sPointMonth.getRatio());
			            
			            	//excel显示极值，比例
			            	showtext="日月："+sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%,"+sMonthRatio+"%)"; 
			         
			            	create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleMonth); //cellStyleDayMonth
			            	
		            		 extremeCol++;	
		            		daymonth++;		 
		            		//System.out.println("DayMonth极值点时间："+extremeDate);
		            		break;
		            	case ConstantsInfo.ExtremeDateWeekMonth:
		            		sWeekRatio = ddfDay.format(sPointWeek.getRatio());
			            	sMonthRatio = ddfDay.format(sPointMonth.getRatio());
			            
			            	//excel显示极值，比例
			            	showtext="周月："+sPointWeek.getExtremeDate()+":"+Float.toString(sPointWeek.getExtremePrice())+"("+sWeekRatio+"%,"+sMonthRatio+"%)"; 
			            	create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleMonth); //cellStyleWeekMonth
		            		extremeCol++;	
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
        
        //public static void create07Cell(Workbook wb, Row row, int column, String value, XSSFFont font,short color) {  
        public static void create07Cell(Workbook wb, Row row, int column, String value, CellStyle cellStyle) {  
            Cell cell = row.createCell(column);  
            cell.setCellValue(value);  
            cell.setCellStyle(cellStyle);  
        }  
        
        /*
        //public static void create07Cell(Workbook wb, Row row, int column, String value, XSSFFont font,short color) {  
        public static void create07Cell(Workbook wb, Row row, int column, String value, XSSFFont font,int color) {  
            Cell cell = row.createCell(column);  
            cell.setCellValue(value);  
            CellStyle cellStyle = wb.createCellStyle();  
            cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);  
            cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_BOTTOM);  
            cellStyle.setFont(font);  
          //  cellStyle.setFillBackgroundColor(HSSFColor.BLUE.index);
           // IndexedColors.YELLOW.getIndex()
            if(color>0)
            {	
            	cellStyle.setFillForegroundColor((short) color);            
            	cellStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);  
            }
            cell.setCellStyle(cellStyle);  
        } 
        */
        
       
        /** 
         * 07excel 设置字体 
         */   
        public static XSSFFont create07Fonts(Workbook wb, short bold, String fontName, boolean isItalic, short hight,XSSFColor color) {  
        	XSSFFont font = (XSSFFont) wb.createFont();  
            font.setFontName(fontName);  
            font.setBoldweight(bold);  
            font.setItalic(isItalic);  
            font.setFontHeight(hight);  
            font.setColor(color);             
            return font;  
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
        
        public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException {
    		
        	PropertyConfigurator.configure("stockConf/log4j_excelWriter.properties");
        	
    		ExcelWriter ew=new ExcelWriter();
    		
    		HSSFWorkbook wb;
    		Date startDate = new Date();
    	//	wb=ew.exportExcelForStock();
    		
    		//XSSFWorkbook xwb;
    		//xwb=ew.exportExcelForAllStock();
    		
    	
   	     Connection stockBaseConn = DbConn.getConnDB("stockConf/conn_base_db.ini"); 
   	     Connection stockDataConn = DbConn.getConnDB("stockConf/conn_data_db.ini"); 
   	     Connection stockPointConn = DbConn.getConnDB("stockConf/conn_point_db.ini");  
   	     spDao =new StockPointDao(stockPointConn);
   			sdDao =new StockDataDao(stockDataConn);
   			sbDao =new StockBaseDao(stockBaseConn);   		
   		
    		//当前路径
    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
            String dateNowStr = sdf.format(startDate);  
            String excleFileName="stockIndustryPoint_"+dateNowStr+".xlsx";
    		ew.exportExcelAllStockForIndustry("export\\",excleFileName);//股票极值点显示一行 读取pointStock表
    		
    		//当前路径
    		ew.exportExcelAllStockForConcept("load\\","stockPointConcept.xlsx");//股票极值点显示一行 读取pointStock表
    		
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
