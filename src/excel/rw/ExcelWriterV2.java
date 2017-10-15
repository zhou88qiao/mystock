package excel.rw;

import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
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

import dao.StockBaseDao;
import dao.StockConcept;
import dao.StockDataDao;
import dao.StockIndustry;
import dao.StockInformation;
import dao.StockInformationDao;
import dao.StockPoint;
import dao.StockPointDao;
import dao.StockToIndustry;

public class ExcelWriterV2 {
	
	static StockInformationDao siDao =new StockInformationDao();
	static StockDataDao sdDao=new StockDataDao();
	static StockPointDao spDao=new StockPointDao();
	static StockBaseDao sbDao=new StockBaseDao();
	static PointClass pClass=new PointClass();
	private POIFSFileSystem fs;
    private static HSSFWorkbook wb;
    private XSSFWorkbook xb;
    private HSSFSheet sheet;
    private Sheet xsheet;
    private HSSFRow row;
    private Row xrow;
	final static int stockLabelNum=3;
	final static int stockMaxPointNum=300;
	
	  static XSSFColor hfNormal;
      static XSSFColor hfCday;
      static XSSFColor hfCweek;
      static XSSFColor hfCmonth;
      static XSSFColor hfYuce;
      
      // 文件头字体  
      static XSSFFont font0;  
      static XSSFFont fontDay;  
      static XSSFFont fontDayWeek;  
      static XSSFFont fontDayWeekMonth;  
      static XSSFFont fontYuce;
      static CellStyle cellStyleNormal;
      static CellStyle cellStyleDay;
      static CellStyle cellStyleWeek;
      static CellStyle cellStyleWeekMonth;
      static CellStyle cellStyleYuce;
	
      
      public static CellStyle create07CellStyle(Workbook wb, XSSFFont font,int color) {    
          CellStyle cellStyle = wb.createCellStyle();  
          cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);  
          cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_BOTTOM);  
          cellStyle.setFont(font);  
        //  cellStyle.setFillBackgroundColor(HSSFColor.BLUE.index);
         // IndexedColors.YELLOW.getIndex()
          if((short)color>0)
          {	
          	cellStyle.setFillForegroundColor((short) color);            
          	cellStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);  
          }
         return cellStyle;
      }  
      
    public static HSSFWorkbook exportExcelForStock() throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException 
    {    
      
    	//创建excel文件对象  
            HSSFWorkbook wb = new HSSFWorkbook();  
            //创建一个张表  
            int lt=wb.getNumberOfSheets();
            FileOutputStream os=null;
            System.out.println(lt);
            
            List<String> listStockFullId = new ArrayList<String>();
    		listStockFullId=siDao.getAllFullId();
    		int sheetCount=0;
    		int i=0;
            
    		for(Iterator it = listStockFullId.iterator();it.hasNext();)
    		{
    			String stockFullId=(String)it.next();
    			if(!stockFullId.equals("sh000001"))
					continue;
    			//if(!stockFullId.equals("sh600000") && !stockFullId.equals("sh600015") && !stockFullId.equals("sz000001"))
    			//		continue;
    			List<StockPoint> stockDayPoint=new ArrayList<StockPoint>();
    			List<StockPoint> stockWeekPoint=new ArrayList<StockPoint>();
    			List<StockPoint> stockMonthPoint=new ArrayList<StockPoint>();
    			
    			DateStock dStock=sdDao.getDateStock(stockFullId);
    			
    			stockDayPoint=pClass.getStockDayExtremePoint(stockFullId, dStock,ConstantsInfo.StockCalAllData);
    			stockWeekPoint=pClass.getStockWeekExtremePoint(stockFullId, dStock,ConstantsInfo.StockCalAllData);
    			stockMonthPoint=pClass.getStockMonthExtremePoint(stockFullId, dStock,ConstantsInfo.StockCalAllData);
    			System.out.println("day size:"+stockDayPoint.size());
    			System.out.println("week size:"+stockWeekPoint.size());
    			System.out.println("month size:"+stockMonthPoint.size());    			
    			
    			
            	System.out.println("sheet count:"+sheetCount);
	            HSSFSheet  sheet = wb.createSheet(); 
	            wb.setSheetName(sheetCount,stockFullId);
	            
	            //列宽 0,1,2,3
	            for(i=0;i<stockLabelNum;i++)
	            	sheet.setColumnWidth(i, 6000);
	            //列宽 3,4,5
	            for(i=stockLabelNum-1;i<stockMaxPointNum;i++)
	            	sheet.setColumnWidth(i, 10000);
	       
	            sheetCount++;
	          
	            //创建第一行  
	            Row row = sheet.createRow(0);  
	            //创建第二行  
	            Row row1 = sheet.createRow(1);  
	            //创建第三行  
	            Row row2 = sheet.createRow(2);  
	        
	            
	            // 文件头字体  
	            HSSFFont font0 = create03Fonts(wb, HSSFFont.BOLDWEIGHT_BOLD, "宋体", false,  
	                    (short) 300);  
	            HSSFFont font1 = create03Fonts(wb, HSSFFont.BOLDWEIGHT_NORMAL, "宋体", false,  
	                    (short) 300);  
	            
 
	            // 合并0-1行的单元格  
	            sheet.addMergedRegion(new CellRangeAddress(0, 2, 0, 0));  
	            //设置第一列的文字  
	            create03Cell(wb, row, 0, "类型", font0);  
	            //合并第一行的2列以后到8列（不包含第二列）  
	            sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 4));  
	            //设置第二列的文字  
	            create03Cell(wb, row, 1, "技术", font0);  
	            //合并时间
	            sheet.addMergedRegion(new CellRangeAddress(1, 2, 1, 1)); 
	            //合并点位
	            sheet.addMergedRegion(new CellRangeAddress(1, 2, 2, 2));
	            //合并交叉点
	            sheet.addMergedRegion(new CellRangeAddress(1, 1, 3, 4));
	            //给第三行添加文本  
	            create03Cell(wb, row1, 1, "时间", font0);  
	            create03Cell(wb, row1, 2, "点位", font0);  
	            create03Cell(wb, row1, 3, "交叉点", font0);  
	            
	            create03Cell(wb, row2, 3, "前", font0);  
	            create03Cell(wb, row2, 4, "后", font0);  
	            //第四行表示  
	            int lhead = 3;  
	            //这里将学员的信心存入到表格中  
	         
	            //日极点
	            for(Iterator itDay = stockDayPoint.iterator();itDay.hasNext();)
	    		{
	            	Row rowData = sheet.createRow(lhead++);  
	            	StockPoint sPoint=(StockPoint)itDay.next();
	               // createCell(wb, rowData, 0, String.valueOf(i + 1), font1);
	                create03Cell(wb, rowData, 0, "日", font1); 
	                create03Cell(wb, rowData, 1, sPoint.getExtremeDate().toString(), font1);  
	                create03Cell(wb, rowData, 2,((Float)sPoint.getExtremePrice()).toString(), font1);  
	                create03Cell(wb, rowData, 3,sPoint.getFromDate().toString(), font1);  
	                create03Cell(wb, rowData, 4,sPoint.getToDate().toString(), font1);  	            	
	    		}
	            
	            //周极点
	            for(Iterator itWeek = stockWeekPoint.iterator();itWeek.hasNext();)
	    		{
	            	Row rowData = sheet.createRow(lhead++);  
	            	StockPoint sPoint=(StockPoint)itWeek.next();
	               // createCell(wb, rowData, 0, String.valueOf(i + 1), font1);
	                create03Cell(wb, rowData, 0, "周", font1); 
	                create03Cell(wb, rowData, 1, sPoint.getExtremeDate().toString(), font1);  
	                create03Cell(wb, rowData, 2,((Float)sPoint.getExtremePrice()).toString(), font1);  
	                create03Cell(wb, rowData, 3,sPoint.getFromDate().toString(), font1);  
	                create03Cell(wb, rowData, 4,sPoint.getToDate().toString(), font1);              	
	    		}
	            
	            //月极点
	            for(Iterator itMonth = stockMonthPoint.iterator();itMonth.hasNext();)
	    		{
	            	Row rowData = sheet.createRow(lhead++);  
	            	StockPoint sPoint=(StockPoint)itMonth.next();
	               // createCell(wb, rowData, 0, String.valueOf(i + 1), font1);
	                create03Cell(wb, rowData, 0, "月", font1); 
	                create03Cell(wb, rowData, 1, sPoint.getExtremeDate().toString(), font1);  
	                create03Cell(wb, rowData, 2,((Float)sPoint.getExtremePrice()).toString(), font1);  
	                create03Cell(wb, rowData, 3,sPoint.getFromDate().toString(), font1);  
	                create03Cell(wb, rowData, 4,sPoint.getToDate().toString(), font1);              	
	    		}
 
	             os = new FileOutputStream("stockExcel.xls");
	         
		         wb.write(os);
		          //  os.flush();
		         os.close();
            
            }
    
            return wb;  
        }     
    
    //2007   day week表各一行
    public static XSSFWorkbook exportExcelForAllStockSeparate() throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException 
    {    
      
    	//创建excel文件对象  
    		XSSFWorkbook wb = new XSSFWorkbook();  
            //创建一个张表  
            int lt=wb.getNumberOfSheets();
            FileOutputStream os=null;
            System.out.println(lt);
            
            List<String> listStockFullId = new ArrayList<String>();
            List<StockInformation> listStockInfo = new ArrayList<StockInformation>(); 
   
            listStockInfo=siDao.getStockDaoList();
    		listStockFullId=siDao.getAllFullId();
    		int sheetCount=0;
    		
    		List<String> listStockDate = new ArrayList<String>();
    		listStockDate=sdDao.getDatesFromSH000001("sh000001");
    		System.out.println("date size:"+listStockDate.size());
    		
        	XSSFSheet  sheet = wb.createSheet(); 
            wb.setSheetName(sheetCount,"allStock");
            
            //列宽
            for(int i=0;i<=4+listStockDate.size();i++)
            {	
            	sheet.setColumnWidth(i, 6000);
            
            }
                
            //创建第一行  
            Row rowHead = sheet.createRow(0);  
            
            XSSFColor hfNormal=new XSSFColor(java.awt.Color.BLACK);
            XSSFColor hfCday=new XSSFColor(java.awt.Color.RED);
            XSSFColor hfCweek=new XSSFColor(java.awt.Color.BLUE);
            
            /*
            // 文件头字体  
            XSSFFont font0 = create07Fonts(wb, XSSFFont.BOLDWEIGHT_BOLD, "宋体", false,  
                    (short) 400,XSSFFont.COLOR_NORMAL);  
            XSSFFont fontDay = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "宋体", false,  
                    (short) 400,XSSFFont.COLOR_RED);  
            */
            
            // 文件头字体  
            XSSFFont font0 = create07Fonts(wb, XSSFFont.BOLDWEIGHT_BOLD, "宋体", false,  
                    (short) 400,hfNormal);  
            XSSFFont fontDay = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "宋体", false,  
                    (short) 400,hfCday);  
            XSSFFont fontWeek = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "宋体", false,  
                    (short) 400,hfCweek);  
             

            //给第一行添加文本  
            create07Cell(wb, rowHead, 0, "序号", font0,IndexedColors.YELLOW.getIndex());  
            create07Cell(wb, rowHead, 1, "代号", font0,IndexedColors.YELLOW.getIndex());  
            create07Cell(wb, rowHead, 2, "名称", font0,IndexedColors.YELLOW.getIndex()); 	            
            create07Cell(wb, rowHead, 3, "地区", font0,IndexedColors.YELLOW.getIndex());  
            create07Cell(wb, rowHead, 4, "类型", font0,IndexedColors.YELLOW.getIndex());  
            int stockColumn=5;
            //列宽
            
            //时间与列 hash表
            HashMap<String, Integer> stockDateColumnmap = new HashMap<String, Integer>(); 
            for(Iterator it = listStockDate.iterator();it.hasNext();)
    		{
            	String stockDate=(String)it.next();            	
            	create07Cell(wb, rowHead, stockColumn, stockDate, font0,IndexedColors.YELLOW.getIndex()); 	
            	stockDateColumnmap.put(stockDate, stockColumn);
            	stockColumn++;
    		}
       
            //第二行表示  
            int stockRow = 1;  
            
    		for(Iterator it = listStockInfo.iterator();it.hasNext();)
    		{
    			
    		//	String stockFullId=(String)it.next();
    			StockInformation si = (StockInformation) it.next();
    			String stockFullId=si.getStockFullId();
    		//	if(!stockFullId.equals("sh000001"))
			//		continue;
    			
    			if(!stockFullId.equals("sh600000") && !stockFullId.equals("sh600015") && !stockFullId.equals("sz000001"))
    					continue;
    			
    			Row rowDayData = sheet.createRow(stockRow);  
    			
    			create07Cell(wb, rowDayData, 0,  Integer.toString(stockRow), font0,ConstantsInfo.XSSFNORMAL);  
	            create07Cell(wb, rowDayData, 1, stockFullId, font0,ConstantsInfo.XSSFNORMAL);  
	            create07Cell(wb, rowDayData, 2, si.getStockName(), font0,ConstantsInfo.XSSFNORMAL); 	            
	            create07Cell(wb, rowDayData, 3, si.getStockRegional(), font0,ConstantsInfo.XSSFNORMAL); 
	            create07Cell(wb, rowDayData, 4, "日", font0,ConstantsInfo.XSSFNORMAL);  
	            stockColumn=5;
	           // stockRow++;
    			List<StockPoint> stockDayPoint=new ArrayList<StockPoint>();
    			List<StockPoint> stockWeekPoint=new ArrayList<StockPoint>();
    			List<StockPoint> stockMonthPoint=new ArrayList<StockPoint>();
    			
    			
    			DateStock dStock=sdDao.getDateStock(stockFullId);
    			
    			stockDayPoint=pClass.getStockDayExtremePoint(stockFullId, dStock,ConstantsInfo.StockCalAllData);
    			stockWeekPoint=pClass.getStockWeekExtremePoint(stockFullId, dStock,ConstantsInfo.StockCalAllData);
    			//stockMonthPoint=pClass.getStockMonthExtremePoint(stockFullId, dStock);
    			System.out.println("day size:"+stockDayPoint.size());
    			System.out.println("week size:"+stockWeekPoint.size());
    			System.out.println("month size:"+stockMonthPoint.size());    			
    				
	            //日极点
    			System.out.println("日stockRow："+stockRow);
    			System.out.println("******day point*******");
	            for(Iterator itDay = stockDayPoint.iterator();itDay.hasNext();)
	    		{
	            	StockPoint sPoint=(StockPoint)itDay.next();
	            		
	            	//比例保留两位小数点
	            	NumberFormat ddf=NumberFormat.getNumberInstance() ; 
	            	ddf.setMaximumFractionDigits(2); 
	            	String sRatio= ddf.format(sPoint.getRatio()) ;
	            	
	            	//excel显示极值，比例
	            	String showtext=Float.toString(sPoint.getExtremePrice())+"("+sRatio+"%)";       
	            	
	            	int extremeCol = stockDateColumnmap.get(sPoint.getExtremeDate());
	            	
	            //	if(ConstantsInfo.DEBUG)
	        	//	{
		            	System.out.println("极值点时间："+sPoint.getExtremeDate());
		            	System.out.println("极值价格："+sPoint.getExtremePrice()); 
		            	System.out.println("比例："+sPoint.getRatio()); 
		            	System.out.println(showtext);
		            	System.out.println("列值："+extremeCol);  
	        	//	}
	            	
	            	create07Cell(wb, rowDayData, extremeCol, showtext, fontDay,ConstantsInfo.XSSFNORMAL);  	            	
	    		}
	            
	          
	            stockRow++;
	           
	            //周极点
	            System.out.println("周stockRow："+stockRow);
	            System.out.println("******week point*******");
	            Row rowWeekData = sheet.createRow(stockRow);  
	            create07Cell(wb, rowWeekData, 0,  Integer.toString(stockRow), font0,-1); 
	            create07Cell(wb, rowWeekData, 4, "周", font0,-1); 
	            for(Iterator itWeek = stockWeekPoint.iterator();itWeek.hasNext();)
	    		{
	            	
	            	StockPoint sPoint=(StockPoint)itWeek.next();
	                          		
	            	//比例保留两位小数点
	            	NumberFormat ddf=NumberFormat.getNumberInstance() ; 
	            	ddf.setMaximumFractionDigits(2); 
	            	String sRatio= ddf.format(sPoint.getRatio()) ;
	            	
	            	//excel显示极值，比例
	            	String showtext=Float.toString(sPoint.getExtremePrice())+"("+sRatio+"%)";       
	            	
	            	int extremeCol = stockDateColumnmap.get(sPoint.getExtremeDate());
	            //	if(ConstantsInfo.DEBUG)
	        	//	{
		            	System.out.println("极值点时间："+sPoint.getExtremeDate());
		            	System.out.println("极值价格："+sPoint.getExtremePrice()); 
		            	System.out.println("比例："+sPoint.getRatio()); 
		            	System.out.println(showtext);
		            	System.out.println("列值："+extremeCol);  
	        	//	}
		       
	            	create07Cell(wb, rowWeekData, extremeCol, showtext,fontWeek,ConstantsInfo.XSSFNORMAL);  
	    		}
	          
	            stockRow++;
	            
	            /*
	            //月极点
	            for(Iterator itMonth = stockMonthPoint.iterator();itMonth.hasNext();)
	    		{
	            	Row rowMonthData = sheet.createRow(stockRow);  
	            	StockPoint sPoint=(StockPoint)itMonth.next();
	            	//比例保留两位小数点
	            	NumberFormat ddf=NumberFormat.getNumberInstance() ; 
	            	ddf.setMaximumFractionDigits(2); 
	            	String sRatio= ddf.format(sPoint.getRatio()) ;
	            	
	            	//excel显示极值，比例
	            	String showtext=Float.toString(sPoint.getExtremePrice())+"("+sRatio+")";       
	            	System.out.println("极值点时间："+sPoint.getExtremeDate());//极值点时间：2012-05-04 不在13，14年内
	            	int extremeCol = stockDateColumnmap.get(sPoint.getExtremeDate());
	            	if(ConstantsInfo.DEBUG)
	        		{
		            	System.out.println("极值点时间："+sPoint.getExtremeDate());
		            	System.out.println("极值价格："+sPoint.getExtremePrice()); 
		            	System.out.println("比例："+sPoint.getRatio()); 
		            	System.out.println(showtext);
		            	System.out.println("列值："+extremeCol);  
	        		}
	            	
	            	create07Cell(wb, rowMonthData, extremeCol, showtext, font1);              	
	    		}
	    		*/       
	             
            
            }
    		
    		os = new FileOutputStream("stockExcel.xlsx");
	         
	         wb.write(os);
	          //  os.flush();
	         os.close();
    
            return wb;  
        }  
    
    
    
    public static XSSFWorkbook exportExcelForAllStockFromTable() throws IOException, ClassNotFoundException, SQLException
    {    
    		      
	//创建excel文件对象  
		XSSFWorkbook wb = new XSSFWorkbook();  
        //创建一个张表  
        int lt=wb.getNumberOfSheets();
        FileOutputStream os=null;
        System.out.println(lt);
        int i=0;
        
    //    List<String> listStockFullId = new ArrayList<String>();
            List<StockInformation> listStockInfo = new ArrayList<StockInformation>(); 
            List<String> listIndustry = new ArrayList<String>(); 
        	List<String> listStockIndustry = new ArrayList<String>(); 
   
            listStockInfo=siDao.getStockDaoList();
    	//	listStockFullId=siDao.getAllFullId();
		int sheetCount=0;
		
		List<String> listStockDate = new ArrayList<String>();
		listStockDate=sdDao.getDatesFromSH000001("sh000001");
		System.out.println("date size:"+listStockDate.size());
		
    	XSSFSheet  sheet = wb.createSheet(); 
        wb.setSheetName(sheetCount,"allStock");
                    
        //列宽
        for(i=0;i<stockLabelNum;i++)
        	sheet.setColumnWidth(i, 6000);
        //列宽
        for(i=stockLabelNum;i<=stockMaxPointNum;i++)
        	sheet.setColumnWidth(i, 10000);
   
            
        //创建第一行  
        Row rowHead = sheet.createRow(0);  
        
        XSSFColor hfNormal=new XSSFColor(java.awt.Color.BLACK);
        XSSFColor hfCday=new XSSFColor(java.awt.Color.RED);
        XSSFColor hfCweek=new XSSFColor(java.awt.Color.BLUE);
        XSSFColor hfCmonth=new XSSFColor(java.awt.Color.GREEN);
        
        /*
        // 文件头字体  
        XSSFFont font0 = create07Fonts(wb, XSSFFont.BOLDWEIGHT_BOLD, "宋体", false,  
                (short) 400,XSSFFont.COLOR_NORMAL);  
        XSSFFont fontDay = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "宋体", false,  
                (short) 400,XSSFFont.COLOR_RED);  
        */
        
        // 文件头字体  
        XSSFFont font0 = create07Fonts(wb, XSSFFont.BOLDWEIGHT_BOLD, "宋体", false,  
                (short) 400,hfNormal);  
        XSSFFont fontDay = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "宋体", false,  
                (short) 300,hfCday);  
        XSSFFont fontDayWeek = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "宋体", false,  
                (short) 300,hfCweek);  
        XSSFFont fontDayWeekMonth = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "宋体", false,  
                (short) 300,hfCmonth);  
         

        //给第一行添加文本  
        create07Cell(wb, rowHead, 0, "序号", font0,IndexedColors.YELLOW.getIndex());  
        create07Cell(wb, rowHead, 1, "代号", font0,IndexedColors.YELLOW.getIndex());  
        create07Cell(wb, rowHead, 2, "名称", font0,IndexedColors.YELLOW.getIndex()); 	            
        create07Cell(wb, rowHead, 3, "地区", font0,IndexedColors.YELLOW.getIndex());            
        int stockColumn=4;
        //列宽
        
        //时间与列 hash表
        for(;stockColumn<100;stockColumn++)
		{
        	String colName=""+stockColumn+"";            	
        	create07Cell(wb, rowHead, stockColumn, colName, font0,IndexedColors.YELLOW.getIndex()); 	
		}
   
        //第二行开始展示  
        int stockRow = 1;  
        
        listIndustry=siDao.getAllIndustry(listStockInfo);	
        System.out.println("板块个数："+listIndustry.size());
        Iterator itIndu,ie;
        int stockNum=0;
   		 for(itIndu = listIndustry.iterator();itIndu.hasNext();)
   		 {
   			String stockIndu = (String) itIndu.next();			
   			//System.out.println(stockIndu);
   			
   		//	if(!stockIndu.equals("银行业")&& !stockIndu.equals("医疗器械制造业"))
   		//		continue;
   			
   			//行业
   			Row rowIndustry = sheet.createRow(stockRow);  
   			create07Cell(wb, rowIndustry, 0, stockIndu, font0,IndexedColors.BLUE.getIndex());  
   			stockRow++;
   			
   			listStockIndustry=siDao.getStockIndustry(stockIndu);
   			System.out.println("该板块股票个数："+listStockIndustry.size());
   			for(ie=listStockIndustry.iterator();ie.hasNext();)
   			{
   				String stockFullId = (String) ie.next();	
   				int isTableExist=sdDao.isExistStockTable(stockFullId,ConstantsInfo.TABLE_DATA_STOCK);
   				if(isTableExist==0)//不存在
   					continue;   				

   				StockInformation si=siDao.getStockInformation(stockFullId);			
   				
    		//	String stockFullId=si.getStockFullId();
    		
			
   			//股票
    			Row rowDayData = sheet.createRow(stockRow);  
    			
    			create07Cell(wb, rowDayData, 0,  Integer.toString(stockRow), font0,ConstantsInfo.XSSFNORMAL);  
	            create07Cell(wb, rowDayData, 1, stockFullId, font0,ConstantsInfo.XSSFNORMAL);  
	            create07Cell(wb, rowDayData, 2, si.getStockName(), font0,ConstantsInfo.XSSFNORMAL); 	            
	            create07Cell(wb, rowDayData, 3, si.getStockRegional(), font0,ConstantsInfo.XSSFNORMAL); 

	            
	            //第四列开始显示极值
	            stockColumn=4;
	            
	            stockRow++;
	       
	            
	        //   if(!stockFullId.equals("sh600000"))
			//		continue;
    			
    			if(!(stockFullId.equals("sh600000") || stockFullId.equals("sh601169") || stockFullId.equals("sz000045")))
    				continue;
	            
    			List<StockPoint> stockDayPoint=new ArrayList<StockPoint>();
    			List<StockPoint> stockWeekPoint=new ArrayList<StockPoint>();
    			List<StockPoint> stockMonthPoint=new ArrayList<StockPoint>();
    			
    			
    			DateStock dStock=sdDao.getDateStock(stockFullId);
    			
    			stockDayPoint=spDao.getAllPointStock(stockFullId,ConstantsInfo.DayDataType);
    			stockWeekPoint=spDao.getAllPointStock(stockFullId, ConstantsInfo.WeekDataType);
    			stockMonthPoint=spDao.getAllPointStock(stockFullId,  ConstantsInfo.MonthDataType);
    			if(stockDayPoint==null)
    				continue;
    			System.out.println("day size:"+stockDayPoint.size());
    			System.out.println("week size:"+stockWeekPoint.size());
    			System.out.println("month size:"+stockMonthPoint.size());    			
    				
	            //日极点
    			System.out.println("日stockRow："+stockRow);
    			System.out.println("******all point*******");
    			
    			  //周时间与比例 
                HashMap<String, StockPoint> stockWeekDateExist = new HashMap<String, StockPoint>(); 
                HashMap<String, StockPoint> stockMonthDateExist = new HashMap<String, StockPoint>(); 
                
    			for(Iterator itWeek = stockWeekPoint.iterator();itWeek.hasNext();)
	 	    	{
	 	            StockPoint sPointWeek=(StockPoint)itWeek.next();	 	            
	 	           stockWeekDateExist.put(sPointWeek.getExtremeDate().toString(), sPointWeek);
	 	    	}
    			//月时间与比例 
    			for(Iterator itMonth = stockMonthPoint.iterator();itMonth.hasNext();)
	 	    	{	
	 	            StockPoint sPointMonth=(StockPoint)itMonth.next();	 	            
	 	           stockMonthDateExist.put(sPointMonth.getExtremeDate().toString(), sPointMonth);
	 	    	}
    			int type=0;
    			int extremeCol=stockLabelNum;
	            for(Iterator itDay = stockDayPoint.iterator();itDay.hasNext();)
	    		{
	            	StockPoint sPointDay=(StockPoint)itDay.next();
	            	String extremeDate=sPointDay.getExtremeDate().toString();//时间
	            	type=ConstantsInfo.ExtremeDateDay;
	            	StockPoint sPointWeek=stockWeekDateExist.get(extremeDate);
	            	if(sPointWeek!=null)
	            	{
	            		type=ConstantsInfo.ExtremeDateDayWeek;
	            	}
	            	
	            	StockPoint sPointMonth=stockMonthDateExist.get(extremeDate);
	            	if(sPointMonth!=null && type==ConstantsInfo.ExtremeDateDayWeek)
	            	{
	            		type=ConstantsInfo.ExtremeDateDayWeekMonth;
	            	}
	            	
	            	NumberFormat ddfDay=null;
	            	NumberFormat ddfWeek=null;
	            	NumberFormat ddfMonth=null;
	            	String sDayRatio,sWeekRatio,sMonthRatio=null;
	            	String showtext=null;
	            	
	            	switch(type)
	            	{
	            	case ConstantsInfo.ExtremeDateDay:
	            		//比例保留两位小数点
		            	ddfDay=NumberFormat.getNumberInstance() ; 
		            	ddfDay.setMaximumFractionDigits(2); 
		            	sDayRatio= ddfDay.format(sPointDay.getRatio()) ;
		            	//excel显示极值，比例
		            	showtext=sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%)"; 
		            	 //	if(ConstantsInfo.DEBUG)
			        	//	{
				            	System.out.println("极值点时间："+sPointDay.getExtremeDate());
				            	System.out.println("极值价格："+sPointDay.getExtremePrice()); 
				            	System.out.println("比例："+sPointDay.getRatio()); 
				            	System.out.println(showtext);
				            	
			        	//	}
				         
				       
				         create07Cell(wb, rowDayData, extremeCol, showtext, fontDay,ConstantsInfo.XSSFNORMAL); 
				         extremeCol++;
	            		break;
	            	case ConstantsInfo.ExtremeDateDayWeek:
	            		//比例保留两位小数点
	            		ddfDay=NumberFormat.getNumberInstance() ; 
		            	ddfDay.setMaximumFractionDigits(2); 
		            	sDayRatio= ddfDay.format(sPointDay.getRatio()) ;
		            	
		            	ddfWeek=NumberFormat.getNumberInstance() ; 
		            	ddfWeek.setMaximumFractionDigits(2); 
		            	sWeekRatio= ddfWeek.format(sPointWeek.getRatio()) ;
		            	
		            	//excel显示极值，比例
		            	showtext=sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%,"+sWeekRatio+"%)"; 
		            	
		            	create07Cell(wb, rowDayData, extremeCol, showtext, fontDayWeek,ConstantsInfo.XSSFNORMAL); 
		            	extremeCol++;
	            		break;
	            	case ConstantsInfo.ExtremeDateDayWeekMonth:
	            		//比例保留两位小数点
	            		ddfDay=NumberFormat.getNumberInstance() ; 
		            	ddfDay.setMaximumFractionDigits(2); 
		            	sDayRatio= ddfDay.format(sPointDay.getRatio()) ;
		            	
		            	ddfWeek=NumberFormat.getNumberInstance() ; 
		            	ddfWeek.setMaximumFractionDigits(2); 
		            	sWeekRatio= ddfWeek.format(sPointWeek.getRatio()) ;
		            	
		            	ddfMonth=NumberFormat.getNumberInstance() ; 
		            	ddfMonth.setMaximumFractionDigits(2); 
		            	sMonthRatio= ddfMonth.format(sPointMonth.getRatio()) ;
		            	
		            	//excel显示极值，比例
		            	showtext=sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%,"+sWeekRatio+"%,"+sMonthRatio+"%)"; 
		            	
		            	create07Cell(wb, rowDayData, extremeCol, showtext, fontDayWeekMonth,ConstantsInfo.XSSFNORMAL); 
		            	extremeCol++;
		            	break;
	            	default:
	            		break;	            		
	            	}	
	    		}

   			}
   		 }
    	    		
		os = new FileOutputStream("stockExcel.xlsx");
         
         wb.write(os);
          //  os.flush();
         os.close();

        return wb;  
     }     
    
    public static void initFont(XSSFWorkbook wb)
    {
    	hfNormal=new XSSFColor(java.awt.Color.BLACK);
        hfCday=new XSSFColor(java.awt.Color.RED);
        hfCweek=new XSSFColor(java.awt.Color.BLUE);
        hfCmonth=new XSSFColor(java.awt.Color.ORANGE);
        hfYuce=new XSSFColor(java.awt.Color.GREEN);
    	 // 文件头字体  
        font0 = create07Fonts(wb, XSSFFont.BOLDWEIGHT_BOLD, "宋体", false,  
                (short) 400,hfNormal);  
        fontDay = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "宋体", false,  
                (short) 300,hfCday);  
        fontDayWeek = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "宋体", false,  
                (short) 300,hfCweek);  
        fontDayWeekMonth = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "宋体", false,  
                (short) 300,hfCmonth); 
        fontYuce = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "宋体", false,  
                (short) 300,hfYuce);  
        
        //cell格式类型
        cellStyleNormal=create07CellStyle(wb,font0,ConstantsInfo.XSSFNORMAL);
        cellStyleDay=create07CellStyle(wb,fontDay,ConstantsInfo.XSSFNORMAL);
        cellStyleWeek=create07CellStyle(wb,fontDayWeek,ConstantsInfo.XSSFNORMAL);
        cellStyleWeekMonth=create07CellStyle(wb,fontDayWeekMonth,ConstantsInfo.XSSFNORMAL);
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
         for(i=0;i<stockLabelNum;i++)
         	sheet.setColumnWidth(i, 6000);
         //日 周 月预测
         for(i=stockLabelNum;i<stockLabelNum+3;i++)
         	sheet.setColumnWidth(i, 12000);
         //列宽
         for(i=stockLabelNum+3;i<=stockMaxPointNum;i++)
         	sheet.setColumnWidth(i, 10000);
      
         // 文件头字体  
         initFont(wb); 
         
         
         //创建第一行  
         Row rowHead = sheet.createRow(0);  
         
         //给第一行添加文本  
         create07Cell(wb, rowHead, 0, "序号", font0,IndexedColors.YELLOW.getIndex());  
         create07Cell(wb, rowHead, 1, "代号", font0,IndexedColors.YELLOW.getIndex());  
         create07Cell(wb, rowHead, 2, "名称", font0,IndexedColors.YELLOW.getIndex()); 	            
         create07Cell(wb, rowHead, 3, "日预测值", font0,IndexedColors.YELLOW.getIndex()); 
         create07Cell(wb, rowHead, 4, "周预测值", font0,IndexedColors.YELLOW.getIndex()); 
         create07Cell(wb, rowHead, 5, "月预测值", font0,IndexedColors.YELLOW.getIndex()); 
         int stockColumn=stockLabelNum+3;
         //列宽
         
         //时间与列 hash表
         for(;stockColumn<=stockMaxPointNum;stockColumn++)
 		{
         	String colName=""+stockColumn+"";            	
         	create07Cell(wb, rowHead, stockColumn, colName, font0,IndexedColors.YELLOW.getIndex()); 	
 		}
         
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
				 
			//	 initFont(wb);
				 
				 //当前概念
				 StockConcept concept= (StockConcept)itConcept.next();	
				 String conceptName=concept.getName();
				 if(conceptName==null)
					 continue;
				 
				stockLogger.logger.fatal("概念："+conceptName);
				   			
	   			//概念
	   			Row rowConcept = sheet.createRow(stockRow);  
	   			create07Cell(wb, rowConcept, 0, conceptName, font0,IndexedColors.BLUE.getIndex());  
	   			stockRow++;
	   			
	   			DecimalFormat decimalFormat=new DecimalFormat(".00");
	   			//所属概念所有股票
	   			listConceptStock=sbDao.getConceptStock(conceptName);
	   			
	   			stockLogger.logger.debug("概念股票数："+listConceptStock.size());
	   			for(ie=listConceptStock.iterator();ie.hasNext();)
	   			{
	   				
	   			//测试作用
		   			//if(stockRow>10)
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
	    			create07Cell(wb, rowDayData, 0,  Integer.toString(stockRow), font0,ConstantsInfo.XSSFNORMAL);  
		            create07Cell(wb, rowDayData, 1, stockFullId, font0,ConstantsInfo.XSSFNORMAL);  
		            create07Cell(wb, rowDayData, 2, stockName, font0,ConstantsInfo.XSSFNORMAL); 	            
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
		            	
		            	create07Cell(wb, rowDayData, extremeCol, showtext, fontYuce,ConstantsInfo.XSSFNORMAL); 
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
		            	create07Cell(wb, rowDayData, extremeCol, showtext, fontYuce,ConstantsInfo.XSSFNORMAL); 
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
		            	create07Cell(wb, rowDayData, extremeCol, showtext, fontYuce,ConstantsInfo.XSSFNORMAL); 
		            	extremeCol++;
	    			}
	    			
	    			extremeCol=stockLabelNum+3;
	    			
	    			//周时间与比例 
	                HashMap<String, StockPoint> stockWeekDateExist = new HashMap<String, StockPoint>(); 
	                HashMap<String, StockPoint> stockMonthDateExist = new HashMap<String, StockPoint>(); 
	                
	    			for(Iterator itWeek = stockWeekPoint.iterator();itWeek.hasNext();)
		 	    	{
		 	            StockPoint sPointWeek=(StockPoint)itWeek.next();	 	            
		 	           stockWeekDateExist.put(sPointWeek.getExtremeDate().toString(), sPointWeek);
		 	    	}
	    			//月时间与比例 
	    			for(Iterator itMonth = stockMonthPoint.iterator();itMonth.hasNext();)
		 	    	{	
		 	            StockPoint sPointMonth=(StockPoint)itMonth.next();	 	            
		 	           stockMonthDateExist.put(sPointMonth.getExtremeDate().toString(), sPointMonth);
		 	    	}
	    			int type=0;	    	
	    			////判断日，周，月极值点是否重复
		          //  for(Iterator itDay = stockDayPoint.iterator();itDay.hasNext();)
		            for(int  itDay = stockDayPoint.size()-1;itDay>=0;itDay--)
		    		{
		            	//StockPoint sPointDay=(StockPoint)itDay.next();
		            	StockPoint sPointDay=(StockPoint)stockDayPoint.get(itDay);
		            	String extremeDate=sPointDay.getExtremeDate().toString();//时间
		            	type=ConstantsInfo.ExtremeDateDay;
		            	StockPoint sPointWeek=stockWeekDateExist.get(extremeDate);
		            	if(sPointWeek!=null)
		            	{
		            		type=ConstantsInfo.ExtremeDateDayWeek;
		            	}
		            	
		            	StockPoint sPointMonth=stockMonthDateExist.get(extremeDate);
		            	if(sPointMonth!=null && type==ConstantsInfo.ExtremeDateDayWeek)
		            	{
		            		type=ConstantsInfo.ExtremeDateDayWeekMonth;
		            	}
		            	
		            	//NumberFormat ddfDay=null;
		            	//NumberFormat ddfWeek=null;
		            	//NumberFormat ddfMonth=null;
		            	String sDayRatio,sWeekRatio,sMonthRatio=null;		            	
		            	
		            	switch(type)
		            	{
		            	case ConstantsInfo.ExtremeDateDay:
		            		
			            	sDayRatio= ddfDay.format(sPointDay.getRatio()) ;
			            	//excel显示极值，比例
			            	showtext=sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%)"; 
		            	 	if(ConstantsInfo.DEBUG)
			        		{
				            	System.out.println("极值点时间："+sPointDay.getExtremeDate());
				            	System.out.println("极值价格："+sPointDay.getExtremePrice()); 
				            	System.out.println("比例："+sPointDay.getRatio()); 
				            	System.out.println(showtext);
			        		}					         
					       
					         create07Cell(wb, rowDayData, extremeCol, showtext, fontDay,ConstantsInfo.XSSFNORMAL); 
					         extremeCol++;
		            		break;
		            	case ConstantsInfo.ExtremeDateDayWeek:
		            		
			            	sDayRatio= ddfDay.format(sPointDay.getRatio()) ;
			            	sWeekRatio= ddfDay.format(sPointWeek.getRatio()) ;
			            	
			            	//excel显示极值，比例
			            	showtext=sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%,"+sWeekRatio+"%)"; 
			            	
			            	create07Cell(wb, rowDayData, extremeCol, showtext, fontDayWeek,ConstantsInfo.XSSFNORMAL); 
			            	extremeCol++;
		            		break;
		            	case ConstantsInfo.ExtremeDateDayWeekMonth:
		            	
			            	sDayRatio= ddfDay.format(sPointDay.getRatio()) ;
			            	sWeekRatio= ddfDay.format(sPointWeek.getRatio()) ;
			            	sMonthRatio= ddfDay.format(sPointMonth.getRatio()) ;
			            	
			            	//excel显示极值，比例
			            	showtext=sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%,"+sWeekRatio+"%,"+sMonthRatio+"%)"; 
			            	
			            	create07Cell(wb, rowDayData, extremeCol, showtext, fontDayWeekMonth,ConstantsInfo.XSSFNORMAL); 
			            	extremeCol++;
			            	break;
		            	default:
		            		break;	            		
		            	}	
		            
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
				
				// initFont(wb);
				 
				 //当前行业
				 StockIndustry indu= (StockIndustry)itIndustry.next();	
				 String induName=indu.getThirdname();
				 if(induName==null)
					 continue;				
				stockLogger.logger.debug("行业："+induName);
	   			   			
	   			//行业
	   			Row rowIndustry = sheet.createRow(stockRow);  
	   			create07Cell(wb, rowIndustry, 0, induName, font0,IndexedColors.BLUE.getIndex());  
	   			stockRow++;
	   			
	   			DecimalFormat decimalFormat=new DecimalFormat(".00");
	   			   	   			
	   			//所属行业所有股票
	   			List<StockToIndustry> listIndustryStock = new ArrayList<StockToIndustry>();   
	   			listIndustryStock=sbDao.getIndustryToStock(indu.getThirdcode());		   		
	   			stockLogger.logger.debug("行业股票数："+listIndustryStock.size());
	   			for(ie=listIndustryStock.iterator();ie.hasNext();)
	   			{
	   				//测试作用
	   			//	if(stockRow>5)
	   		   	//		break;
	   				StockToIndustry toInduStock = (StockToIndustry) ie.next();
	   				String stockFullId = toInduStock.getStockFullId();	
	   				System.out.println("stockFullId:"+stockFullId);
	   			
	   				int isTableExist=sdDao.isExistStockTable(stockFullId,ConstantsInfo.TABLE_DATA_STOCK);
	   				if(isTableExist==0)//不存在
	   					continue;   				

	   				String stockName=sbDao.getStockNameFromIndustryTable(stockFullId);			
	   			
	   				System.out.println("fullId："+stockFullId+" 名字："+stockName);
	   				stockLogger.logger.debug("fullId："+stockFullId+" 名字："+stockName);
	   				//每个股票
	   				Row rowDayData = sheet.createRow(stockRow);  
	    			create07Cell(wb, rowDayData, 0,  Integer.toString(stockRow), font0,ConstantsInfo.XSSFNORMAL);  
		            create07Cell(wb, rowDayData, 1, stockFullId, font0,ConstantsInfo.XSSFNORMAL);  
		            create07Cell(wb, rowDayData, 2, stockName, font0,ConstantsInfo.XSSFNORMAL); 	            
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
		            	
		            	create07Cell(wb, rowDayData, extremeCol, showtext, fontYuce,ConstantsInfo.XSSFNORMAL); 
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
		            	create07Cell(wb, rowDayData, extremeCol, showtext, fontYuce,ConstantsInfo.XSSFNORMAL); 
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
		            	create07Cell(wb, rowDayData, extremeCol, showtext, fontYuce,ConstantsInfo.XSSFNORMAL); 
		            	extremeCol++;
	    			}
	    			
	    			extremeCol=stockLabelNum+3;
	    			
	    			//周时间与极点 hash 
	                HashMap<String, StockPoint> stockWeekDateExist = new HashMap<String, StockPoint>(); 
	               //月时间与极点 hash 
	                HashMap<String, StockPoint> stockMonthDateExist = new HashMap<String, StockPoint>(); 
	                
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
		            //for(Iterator itDay = stockDayPoint.iterator();itDay.hasNext();)
		            for(int  itDay = stockDayPoint.size()-1;itDay>=0;itDay--)
		    		{
		            	StockPoint sPointDay=(StockPoint)stockDayPoint.get(itDay);
		            	String extremeDate=sPointDay.getExtremeDate().toString();//时间
		            	type=ConstantsInfo.ExtremeDateDay;
		            	StockPoint sPointWeek=stockWeekDateExist.get(extremeDate);
		            	if(sPointWeek!=null)
		            	{
		            		type=ConstantsInfo.ExtremeDateDayWeek;
		            	}
		            	
		            	StockPoint sPointMonth=stockMonthDateExist.get(extremeDate);
		            	if(sPointMonth!=null && type==ConstantsInfo.ExtremeDateDayWeek)
		            	{
		            		type=ConstantsInfo.ExtremeDateDayWeekMonth;
		            	}
		            			            	
		            	String sDayRatio,sWeekRatio,sMonthRatio=null;		          
		            	
		            	switch(type)
		            	{
		            	case ConstantsInfo.ExtremeDateDay:
		            		 
			            	sDayRatio= ddfDay.format(sPointDay.getRatio()) ;
			            	//excel显示极值，比例
			            	showtext=sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%)"; 
			            	if(ConstantsInfo.DEBUG)
				        	{
				            	System.out.println("极值点时间："+sPointDay.getExtremeDate());
				            	System.out.println("极值价格："+sPointDay.getExtremePrice()); 
				            	System.out.println("比例："+sPointDay.getRatio()); 
				            	System.out.println(showtext);	
				        	}
					         create07Cell(wb, rowDayData, extremeCol, showtext, fontDay,ConstantsInfo.XSSFNORMAL); 
					         extremeCol++;
		            		break;
		            	case ConstantsInfo.ExtremeDateDayWeek:
		            		 
			            	sDayRatio = ddfDay.format(sPointDay.getRatio());
			            	sWeekRatio = ddfDay.format(sPointWeek.getRatio());
			            	
			            	//excel显示极值，比例
			            	showtext=sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%,"+sWeekRatio+"%)"; 
			            	
			            	create07Cell(wb, rowDayData, extremeCol, showtext, fontDayWeek,ConstantsInfo.XSSFNORMAL); 
			            	extremeCol++;
		            		break;
		            	case ConstantsInfo.ExtremeDateDayWeekMonth:
		            		
			            	sDayRatio= ddfDay.format(sPointDay.getRatio());
			            	sWeekRatio= ddfDay.format(sPointWeek.getRatio());
			            	sMonthRatio= ddfDay.format(sPointMonth.getRatio());
			            	
			            	//excel显示极值，比例
			            	showtext=sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%,"+sWeekRatio+"%,"+sMonthRatio+"%)"; 
			            	
			            	create07Cell(wb, rowDayData, extremeCol, showtext, fontDayWeekMonth,ConstantsInfo.XSSFNORMAL); 
			            	extremeCol++;
			            	break;
		            	default:
		            		break;	            		
		            	}	
		            
		    		}

	   			}

	            wb.write(fileOStream);
	            fileOStream.flush();
	            fileIStream.close();
	            fileOStream.close();   
	   		 } 
    }   
    
    //2007 只显示股票当前存在的极值点
    public static XSSFWorkbook exportExcelForAllStockSimple() throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException 
    {    
      
    	//创建excel文件对象  
    		XSSFWorkbook wb = new XSSFWorkbook();  
            //创建一个张表  
            int lt=wb.getNumberOfSheets();
            FileOutputStream os=null;
            System.out.println(lt);
            int i=0;
            
        //    List<String> listStockFullId = new ArrayList<String>();
            List<StockInformation> listStockInfo = new ArrayList<StockInformation>(); 
            List<String> listIndustry = new ArrayList<String>(); 
        	List<String> listStockIndustry = new ArrayList<String>(); 
   
            listStockInfo=siDao.getStockDaoList();
    	//	listStockFullId=siDao.getAllFullId();
    		int sheetCount=0;
    		
    		List<String> listStockDate = new ArrayList<String>();
    		listStockDate=sdDao.getDatesFromSH000001("sh000001");
    		System.out.println("date size:"+listStockDate.size());
    		
        	XSSFSheet  sheet = wb.createSheet(); 
            wb.setSheetName(sheetCount,"allStock");
                        
            //列宽
            for(i=0;i<stockLabelNum;i++)
            	sheet.setColumnWidth(i, 6000);
            //列宽
            for(i=stockLabelNum+1;i<=stockMaxPointNum;i++)
            	sheet.setColumnWidth(i, 10000);
       
                
            //创建第一行  
            Row rowHead = sheet.createRow(0);  
            
            XSSFColor hfNormal=new XSSFColor(java.awt.Color.BLACK);
            XSSFColor hfCday=new XSSFColor(java.awt.Color.RED);
            XSSFColor hfCweek=new XSSFColor(java.awt.Color.BLUE);
            XSSFColor hfCmonth=new XSSFColor(java.awt.Color.GREEN);
            
            /*
            // 文件头字体  
            XSSFFont font0 = create07Fonts(wb, XSSFFont.BOLDWEIGHT_BOLD, "宋体", false,  
                    (short) 400,XSSFFont.COLOR_NORMAL);  
            XSSFFont fontDay = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "宋体", false,  
                    (short) 400,XSSFFont.COLOR_RED);  
            */
            
            // 文件头字体  
            XSSFFont font0 = create07Fonts(wb, XSSFFont.BOLDWEIGHT_BOLD, "宋体", false,  
                    (short) 400,hfNormal);  
            XSSFFont fontDay = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "宋体", false,  
                    (short) 300,hfCday);  
            XSSFFont fontDayWeek = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "宋体", false,  
                    (short) 300,hfCweek);  
            XSSFFont fontDayWeekMonth = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "宋体", false,  
                    (short) 300,hfCmonth);  
             

            //给第一行添加文本  
            create07Cell(wb, rowHead, 0, "序号", font0,IndexedColors.YELLOW.getIndex());  
            create07Cell(wb, rowHead, 1, "代号", font0,IndexedColors.YELLOW.getIndex());  
            create07Cell(wb, rowHead, 2, "名称", font0,IndexedColors.YELLOW.getIndex()); 	            
            create07Cell(wb, rowHead, 3, "地区", font0,IndexedColors.YELLOW.getIndex());            
            int stockColumn=4;
            //列宽
            
            //时间与列 hash表
            for(;stockColumn<100;stockColumn++)
    		{
            	String colName=""+stockColumn+"";            	
            	create07Cell(wb, rowHead, stockColumn, colName, font0,IndexedColors.YELLOW.getIndex()); 	
    		}
       
            //第二行开始展示  
            int stockRow = 1;  
            
            listIndustry=siDao.getAllIndustry(listStockInfo);	
            System.out.println("板块个数："+listIndustry.size());
            Iterator itIndu,ie;
            int stockNum=0;
	   		 for(itIndu = listIndustry.iterator();itIndu.hasNext();)
	   		 {
	   			String stockIndu = (String) itIndu.next();			
	   			//System.out.println(stockIndu);
	   			
	   			if(!(stockIndu.equals("银行业")|| stockIndu.equals("石油和天然气开采业")))
	   				continue;
	   			
	   			//行业
	   			Row rowIndustry = sheet.createRow(stockRow);  
	   			create07Cell(wb, rowIndustry, 0, stockIndu, font0,IndexedColors.BLUE.getIndex());  
	   			stockRow++;
	   			
	   			listStockIndustry=siDao.getStockIndustry(stockIndu);
	   			System.out.println("该板块股票个数："+listStockIndustry.size());
	   			for(ie=listStockIndustry.iterator();ie.hasNext();)
	   			{
	   				String stockFullId = (String) ie.next();	
	   				int isTableExist=sdDao.isExistStockTable(stockFullId,ConstantsInfo.TABLE_DATA_STOCK);
	   				if(isTableExist==0)//不存在
	   					continue;   				

	   				StockInformation si=siDao.getStockInformation(stockFullId);			
	   				
	    		//	String stockFullId=si.getStockFullId();

	   			//股票
	    			Row rowDayData = sheet.createRow(stockRow);  
	    			
	    			create07Cell(wb, rowDayData, 0,  Integer.toString(stockRow), font0,ConstantsInfo.XSSFNORMAL);  
		            create07Cell(wb, rowDayData, 1, stockFullId, font0,ConstantsInfo.XSSFNORMAL);  
		            create07Cell(wb, rowDayData, 2, si.getStockName(), font0,ConstantsInfo.XSSFNORMAL); 	            
		            create07Cell(wb, rowDayData, 3, si.getStockRegional(), font0,ConstantsInfo.XSSFNORMAL); 

		            stockColumn=4;
		            
		            stockRow++;
		        //   if(stockRow>100)
		   		//		break;
		            
		       //    if(!stockFullId.equals("sh600000"))
				//		continue;
	    			
	    			//if(!stockFullId.equals("sh600000") && !stockFullId.equals("sz002142") && !stockFullId.equals("sz000001"))
	    			//		continue;
	    			List<StockPoint> stockDayPoint=new ArrayList<StockPoint>();
	    			List<StockPoint> stockWeekPoint=new ArrayList<StockPoint>();
	    			List<StockPoint> stockMonthPoint=new ArrayList<StockPoint>();
	    			
	    			
	    			DateStock dStock=sdDao.getDateStock(stockFullId);
	    			
	    			stockDayPoint=pClass.getStockDayExtremePoint(stockFullId, dStock,ConstantsInfo.StockCalAllData);
	    			stockWeekPoint=pClass.getStockWeekExtremePoint(stockFullId, dStock,ConstantsInfo.StockCalAllData);
	    			stockMonthPoint=pClass.getStockMonthExtremePoint(stockFullId, dStock,ConstantsInfo.StockCalAllData);
	    			System.out.println("day size:"+stockDayPoint.size());
	    			System.out.println("week size:"+stockWeekPoint.size());
	    			System.out.println("month size:"+stockMonthPoint.size());    			
	    				
		            //日极点
	    			System.out.println("日stockRow："+stockRow);
	    			System.out.println("******all point*******");
	    			
	    			  //周时间与比例 
	                HashMap<String, StockPoint> stockWeekDateExist = new HashMap<String, StockPoint>(); 
	                HashMap<String, StockPoint> stockMonthDateExist = new HashMap<String, StockPoint>(); 
	                
	    			for(Iterator itWeek = stockWeekPoint.iterator();itWeek.hasNext();)
		 	    	{
		 	            StockPoint sPointWeek=(StockPoint)itWeek.next();	 	            
		 	           stockWeekDateExist.put(sPointWeek.getExtremeDate().toString(), sPointWeek);
		 	    	}
	    			//月时间与比例 
	    			for(Iterator itMonth = stockMonthPoint.iterator();itMonth.hasNext();)
		 	    	{	
		 	            StockPoint sPointMonth=(StockPoint)itMonth.next();	 	            
		 	           stockMonthDateExist.put(sPointMonth.getExtremeDate().toString(), sPointMonth);
		 	    	}
	    			int type=0;
	    			int extremeCol=4;
		            for(Iterator itDay = stockDayPoint.iterator();itDay.hasNext();)
		    		{
		            	StockPoint sPointDay=(StockPoint)itDay.next();
		            	String extremeDate=sPointDay.getExtremeDate().toString();//时间
		            	type=ConstantsInfo.ExtremeDateDay;
		            	StockPoint sPointWeek=stockWeekDateExist.get(extremeDate);
		            	if(sPointWeek!=null)
		            	{
		            		type=ConstantsInfo.ExtremeDateDayWeek;
		            	}
		            	
		            	StockPoint sPointMonth=stockMonthDateExist.get(extremeDate);
		            	if(sPointMonth!=null && type==ConstantsInfo.ExtremeDateDayWeek)
		            	{
		            		type=ConstantsInfo.ExtremeDateDayWeekMonth;
		            	}
		            	
		            	NumberFormat ddfDay=null;
		            	NumberFormat ddfWeek=null;
		            	NumberFormat ddfMonth=null;
		            	String sDayRatio,sWeekRatio,sMonthRatio=null;
		            	String showtext=null;
		            	
		            	switch(type)
		            	{
		            	case ConstantsInfo.ExtremeDateDay:
		            		//比例保留两位小数点
			            	ddfDay=NumberFormat.getNumberInstance() ; 
			            	ddfDay.setMaximumFractionDigits(2); 
			            	sDayRatio= ddfDay.format(sPointDay.getRatio()) ;
			            	//excel显示极值，比例
			            	showtext=sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%)"; 
			            	 //	if(ConstantsInfo.DEBUG)
				        	//	{
					            	System.out.println("极值点时间："+sPointDay.getExtremeDate());
					            	System.out.println("极值价格："+sPointDay.getExtremePrice()); 
					            	System.out.println("比例："+sPointDay.getRatio()); 
					            	System.out.println(showtext);
					            	
				        	//	}
					         
					       
					         create07Cell(wb, rowDayData, extremeCol, showtext, fontDay,ConstantsInfo.XSSFNORMAL); 
					         extremeCol++;
		            		break;
		            	case ConstantsInfo.ExtremeDateDayWeek:
		            		//比例保留两位小数点
		            		ddfDay=NumberFormat.getNumberInstance() ; 
			            	ddfDay.setMaximumFractionDigits(2); 
			            	sDayRatio= ddfDay.format(sPointDay.getRatio()) ;
			            	
			            	ddfWeek=NumberFormat.getNumberInstance() ; 
			            	ddfWeek.setMaximumFractionDigits(2); 
			            	sWeekRatio= ddfWeek.format(sPointWeek.getRatio()) ;
			            	
			            	//excel显示极值，比例
			            	showtext=sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%,"+sWeekRatio+"%)"; 
			            	
			            	create07Cell(wb, rowDayData, extremeCol, showtext, fontDayWeek,ConstantsInfo.XSSFNORMAL); 
			            	extremeCol++;
		            		break;
		            	case ConstantsInfo.ExtremeDateDayWeekMonth:
		            		//比例保留两位小数点
		            		ddfDay=NumberFormat.getNumberInstance() ; 
			            	ddfDay.setMaximumFractionDigits(2); 
			            	sDayRatio= ddfDay.format(sPointDay.getRatio()) ;
			            	
			            	ddfWeek=NumberFormat.getNumberInstance() ; 
			            	ddfWeek.setMaximumFractionDigits(2); 
			            	sWeekRatio= ddfWeek.format(sPointWeek.getRatio()) ;
			            	
			            	ddfMonth=NumberFormat.getNumberInstance() ; 
			            	ddfMonth.setMaximumFractionDigits(2); 
			            	sMonthRatio= ddfMonth.format(sPointMonth.getRatio()) ;
			            	
			            	//excel显示极值，比例
			            	showtext=sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%,"+sWeekRatio+"%,"+sMonthRatio+"%)"; 
			            	
			            	create07Cell(wb, rowDayData, extremeCol, showtext, fontDayWeekMonth,ConstantsInfo.XSSFNORMAL); 
			            	extremeCol++;
			            	break;
		            	default:
		            		break;	            		
		            	}	
		    		}

	   			}
	   		 }
    		
		os = new FileOutputStream("stockExcel.xlsx",true);
         
         wb.write(os);
          os.flush();
         os.close();

        return wb;  
    }     
      
    
    //2007   day week month一行
    public static XSSFWorkbook exportExcelForAllStock() throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException 
    {    
      
    	//创建excel文件对象  
    		XSSFWorkbook wb = new XSSFWorkbook();  
            //创建一个张表  
            int lt=wb.getNumberOfSheets();
            FileOutputStream os=null;
            System.out.println(lt);
            
        //    List<String> listStockFullId = new ArrayList<String>();
            List<StockInformation> listStockInfo = new ArrayList<StockInformation>(); 
            List<String> listIndustry = new ArrayList<String>(); 
        	List<String> listStockIndustry = new ArrayList<String>(); 
   
            listStockInfo=siDao.getStockDaoList();
    	//	listStockFullId=siDao.getAllFullId();
    		int sheetCount=0;
    		
    		List<String> listStockDate = new ArrayList<String>();
    		listStockDate=sdDao.getDatesFromSH000001("sh000001");
    		System.out.println("date size:"+listStockDate.size());
    		
        	XSSFSheet  sheet = wb.createSheet(); 
            wb.setSheetName(sheetCount,"allStock");
            
            //列宽
            for(int i=0;i<=4+listStockDate.size();i++)
            {	
            	sheet.setColumnWidth(i, 6000);
            
            }
                
            //创建第一行  
            Row rowHead = sheet.createRow(0);  
            
            XSSFColor hfNormal=new XSSFColor(java.awt.Color.BLACK);
            XSSFColor hfCday=new XSSFColor(java.awt.Color.RED);
            XSSFColor hfCweek=new XSSFColor(java.awt.Color.BLUE);
            XSSFColor hfCmonth=new XSSFColor(java.awt.Color.GREEN);
            
            /*
            // 文件头字体  
            XSSFFont font0 = create07Fonts(wb, XSSFFont.BOLDWEIGHT_BOLD, "宋体", false,  
                    (short) 400,XSSFFont.COLOR_NORMAL);  
            XSSFFont fontDay = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "宋体", false,  
                    (short) 400,XSSFFont.COLOR_RED);  
            */
            
            // 文件头字体  
            XSSFFont font0 = create07Fonts(wb, XSSFFont.BOLDWEIGHT_BOLD, "宋体", false,  
                    (short) 400,hfNormal);  
            XSSFFont fontDay = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "宋体", false,  
                    (short) 400,hfCday);  
            XSSFFont fontDayWeek = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "宋体", false,  
                    (short) 400,hfCweek);  
            XSSFFont fontDayWeekMonth = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "宋体", false,  
                    (short) 400,hfCmonth);  
             

            //给第一行添加文本  
            create07Cell(wb, rowHead, 0, "序号", font0,IndexedColors.YELLOW.getIndex());  
            create07Cell(wb, rowHead, 1, "代号", font0,IndexedColors.YELLOW.getIndex());  
            create07Cell(wb, rowHead, 2, "名称", font0,IndexedColors.YELLOW.getIndex()); 	            
            create07Cell(wb, rowHead, 3, "地区", font0,IndexedColors.YELLOW.getIndex());            
            int stockColumn=4;
            //列宽
            
            //时间与列 hash表
            HashMap<String, Integer> stockDateColumnmap = new HashMap<String, Integer>(); 
            for(Iterator it = listStockDate.iterator();it.hasNext();)
    		{
            	String stockDate=(String)it.next();            	
            	create07Cell(wb, rowHead, stockColumn, stockDate, font0,IndexedColors.YELLOW.getIndex()); 	
            	stockDateColumnmap.put(stockDate, stockColumn);
            	stockColumn++;
    		}           
           
       
            //第二行开始展示  
            int stockRow = 1;  
            
            listIndustry=siDao.getAllIndustry(listStockInfo);	
            System.out.println("板块个数："+listIndustry.size());
            Iterator itIndu,ie;
            int stockNum=0;
	   		 for(itIndu = listIndustry.iterator();itIndu.hasNext();)
	   		 {
	   			String stockIndu = (String) itIndu.next();			
	   			//System.out.println(stockIndu);   			
	   		
	   			
	   			//行业
	   			Row rowIndustry = sheet.createRow(stockRow);  
	   			create07Cell(wb, rowIndustry, 0, stockIndu, font0,IndexedColors.BLUE.getIndex());  
	   			stockRow++;
	   			
	   			listStockIndustry=siDao.getStockIndustry(stockIndu);
	   			System.out.println("该板块股票个数："+listStockIndustry.size());
	   			for(ie=listStockIndustry.iterator();ie.hasNext();)
	   			{
	   				String stockFullId = (String) ie.next();	
	   				int isTableExist=sdDao.isExistStockTable(stockFullId,ConstantsInfo.TABLE_DATA_STOCK);
	   				if(isTableExist==0)//不存在
	   					continue;   				

	   				StockInformation si=siDao.getStockInformation(stockFullId);			
	   				
	    		//	String stockFullId=si.getStockFullId();
	    		
    			
	   			//股票
	    			Row rowDayData = sheet.createRow(stockRow);  
	    			
	    			create07Cell(wb, rowDayData, 0,  Integer.toString(stockRow), font0,ConstantsInfo.XSSFNORMAL);  
		            create07Cell(wb, rowDayData, 1, stockFullId, font0,ConstantsInfo.XSSFNORMAL);  
		            create07Cell(wb, rowDayData, 2, si.getStockName(), font0,ConstantsInfo.XSSFNORMAL); 	            
		            create07Cell(wb, rowDayData, 3, si.getStockRegional(), font0,ConstantsInfo.XSSFNORMAL); 

		            stockColumn=4;
		            
		            stockRow++;
		        //   if(stockRow>100)
		   		//		break;
		            
		            //if(!stockFullId.equals("sh000001"))
					//		continue;
	    			
	    			if(!stockFullId.equals("sh600000") && !stockFullId.equals("sz002142") && !stockFullId.equals("sz000001"))
	    					continue;
	    			List<StockPoint> stockDayPoint=new ArrayList<StockPoint>();
	    			List<StockPoint> stockWeekPoint=new ArrayList<StockPoint>();
	    			List<StockPoint> stockMonthPoint=new ArrayList<StockPoint>();
	    			
	    			
	    			DateStock dStock=sdDao.getDateStock(stockFullId);
	    			
	    			stockDayPoint=pClass.getStockDayExtremePoint(stockFullId, dStock,ConstantsInfo.StockCalAllData);
	    			stockWeekPoint=pClass.getStockWeekExtremePoint(stockFullId, dStock,ConstantsInfo.StockCalAllData);
	    			stockMonthPoint=pClass.getStockMonthExtremePoint(stockFullId, dStock,ConstantsInfo.StockCalAllData);
	    			System.out.println("day size:"+stockDayPoint.size());
	    			System.out.println("week size:"+stockWeekPoint.size());
	    			System.out.println("month size:"+stockMonthPoint.size());    			
	    				
		            //日极点
	    			System.out.println("日stockRow："+stockRow);
	    			System.out.println("******all point*******");
	    			
	    			  //周时间与比例 
	                HashMap<String, StockPoint> stockWeekDateExist = new HashMap<String, StockPoint>(); 
	                HashMap<String, StockPoint> stockMonthDateExist = new HashMap<String, StockPoint>(); 
	                
	    			for(Iterator itWeek = stockWeekPoint.iterator();itWeek.hasNext();)
		 	    	{
		 	            StockPoint sPointWeek=(StockPoint)itWeek.next();	 	            
		 	           stockWeekDateExist.put(sPointWeek.getExtremeDate().toString(), sPointWeek);
		 	    	}
	    			//月时间与比例 
	    			for(Iterator itMonth = stockMonthPoint.iterator();itMonth.hasNext();)
		 	    	{	
		 	            StockPoint sPointMonth=(StockPoint)itMonth.next();	 	            
		 	           stockMonthDateExist.put(sPointMonth.getExtremeDate().toString(), sPointMonth);
		 	    	}
	    			int type=0;
		            for(Iterator itDay = stockDayPoint.iterator();itDay.hasNext();)
		    		{
		            	StockPoint sPointDay=(StockPoint)itDay.next();
		            	String extremeDate=sPointDay.getExtremeDate().toString();//时间
		            	type=ConstantsInfo.ExtremeDateDay;
		            	StockPoint sPointWeek=stockWeekDateExist.get(extremeDate);
		            	if(sPointWeek!=null)
		            	{
		            		type=ConstantsInfo.ExtremeDateDayWeek;
		            	}
		            	
		            	StockPoint sPointMonth=stockMonthDateExist.get(extremeDate);
		            	if(sPointMonth!=null && type==ConstantsInfo.ExtremeDateDayWeek)
		            	{
		            		type=ConstantsInfo.ExtremeDateDayWeekMonth;
		            	}
		            	
		            	NumberFormat ddfDay=null;
		            	NumberFormat ddfWeek=null;
		            	NumberFormat ddfMonth=null;
		            	String sDayRatio,sWeekRatio,sMonthRatio=null;
		            	String showtext=null;
		            	int extremeCol=0;
		            	switch(type)
		            	{
		            	case ConstantsInfo.ExtremeDateDay:
		            		//比例保留两位小数点
			            	ddfDay=NumberFormat.getNumberInstance() ; 
			            	ddfDay.setMaximumFractionDigits(2); 
			            	sDayRatio= ddfDay.format(sPointDay.getRatio()) ;
			            	//excel显示极值，比例
			            	showtext=Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%)"; 
			            	 //	if(ConstantsInfo.DEBUG)
				        	//	{
					            	System.out.println("极值点时间："+sPointDay.getExtremeDate());
					            	System.out.println("极值价格："+sPointDay.getExtremePrice()); 
					            	System.out.println("比例："+sPointDay.getRatio()); 
					            	System.out.println(showtext);
					            	
				        	//	}
					         extremeCol = stockDateColumnmap.get(extremeDate);
					         create07Cell(wb, rowDayData, extremeCol, showtext, fontDay,ConstantsInfo.XSSFNORMAL); 
				            	
		            		break;
		            	case ConstantsInfo.ExtremeDateDayWeek:
		            		//比例保留两位小数点
		            		ddfDay=NumberFormat.getNumberInstance() ; 
			            	ddfDay.setMaximumFractionDigits(2); 
			            	sDayRatio= ddfDay.format(sPointDay.getRatio()) ;
			            	
			            	ddfWeek=NumberFormat.getNumberInstance() ; 
			            	ddfWeek.setMaximumFractionDigits(2); 
			            	sWeekRatio= ddfWeek.format(sPointWeek.getRatio()) ;
			            	
			            	//excel显示极值，比例
			            	showtext=Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%,"+sWeekRatio+"%)"; 
			            	extremeCol = stockDateColumnmap.get(extremeDate);
			            	create07Cell(wb, rowDayData, extremeCol, showtext, fontDayWeek,ConstantsInfo.XSSFNORMAL); 
		            		
		            		break;
		            	case ConstantsInfo.ExtremeDateDayWeekMonth:
		            		//比例保留两位小数点
		            		ddfDay=NumberFormat.getNumberInstance() ; 
			            	ddfDay.setMaximumFractionDigits(2); 
			            	sDayRatio= ddfDay.format(sPointDay.getRatio()) ;
			            	
			            	ddfWeek=NumberFormat.getNumberInstance() ; 
			            	ddfWeek.setMaximumFractionDigits(2); 
			            	sWeekRatio= ddfWeek.format(sPointWeek.getRatio()) ;
			            	
			            	ddfMonth=NumberFormat.getNumberInstance() ; 
			            	ddfMonth.setMaximumFractionDigits(2); 
			            	sMonthRatio= ddfMonth.format(sPointMonth.getRatio()) ;
			            	
			            	//excel显示极值，比例
			            	showtext=Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%,"+sWeekRatio+"%,"+sMonthRatio+"%)"; 
			            	extremeCol = stockDateColumnmap.get(extremeDate);
			            	create07Cell(wb, rowDayData, extremeCol, showtext, fontDayWeekMonth,ConstantsInfo.XSSFNORMAL); 
		            		break;
		            	default:
		            		break;	            		
		            	}	
		    		}

	   			}
	   		 }
    		
    		os = new FileOutputStream("stockExcel.xlsx");
	         
	         wb.write(os);
	          //  os.flush();
	         os.close();
    
            return wb;  
        }     
      
      
    /** 
         * 03excel 创建单元格并设置样式,值 
         *  
         * @param wb 
         * @param row 
         * @param column 
         * @param 
         * @param 
         * @param value 
         */  
        public static void create03Cell(Workbook wb, Row row, int column, String value, HSSFFont font) {  
            Cell cell = row.createCell(column);  
            cell.setCellValue(value);  
            CellStyle cellStyle = wb.createCellStyle();  
            cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);  
            cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_BOTTOM);  
            cellStyle.setFont(font);  
            cell.setCellStyle(cellStyle);  
        }  
        
       
        
       
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
        
        
           
        /** 
         * 03excel 设置字体 
         *  
         * @param wb 
         * @return 
         */  
        public static HSSFFont create03Fonts(Workbook wb, short bold, String fontName,  
                boolean isItalic, short hight) {  
        	HSSFFont font = (HSSFFont) wb.createFont();  
            font.setFontName(fontName);  
            font.setBoldweight(bold);  
            font.setItalic(isItalic);  
            font.setFontHeight(hight);  
            return font;  
        }  
        /** 
         * 07excel 设置字体 
         *  
         * @param wb 
         * @return 
         */ 
        /*
        public static XSSFFont create07Fonts(Workbook wb, short bold, String fontName, boolean isItalic, short hight,short color) {  
        	XSSFFont font = (XSSFFont) wb.createFont();  
            font.setFontName(fontName);  
            font.setBoldweight(bold);  
            font.setItalic(isItalic);  
            font.setFontHeight(hight);  
            font.setColor(color);             
            return font;  
        }  */
        
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
        
        
        //2007   day week month一行
        public static XSSFWorkbook exportExcelForStockIndustry() throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException 
        {    
          
        	//创建excel文件对象  
        		XSSFWorkbook wb = new XSSFWorkbook();  
                //创建一个张表  
                int lt=wb.getNumberOfSheets();
                FileOutputStream os=null;
                System.out.println(lt);
                
            //    List<String> listStockFullId = new ArrayList<String>();
                List<StockInformation> listStockInfo = new ArrayList<StockInformation>(); 
                List<String> listIndustry = new ArrayList<String>(); 
            	List<String> listStockIndustry = new ArrayList<String>(); 
       
                listStockInfo=siDao.getStockDaoList();
        	//	listStockFullId=siDao.getAllFullId();
        		int sheetCount=0;
        		
        		List<String> listStockDate = new ArrayList<String>();
        		listStockDate=sdDao.getDatesFromSH000001("sh000001");
        		System.out.println("date size:"+listStockDate.size());
        		
            	XSSFSheet  sheet = wb.createSheet(); 
                wb.setSheetName(sheetCount,"allStock");
                
               
                    
                //创建第一行  
                Row rowHead = sheet.createRow(0);  
                
                XSSFColor hfNormal=new XSSFColor(java.awt.Color.BLACK);
                XSSFColor hfCday=new XSSFColor(java.awt.Color.RED);
                XSSFColor hfCweek=new XSSFColor(java.awt.Color.BLUE);
                XSSFColor hfCmonth=new XSSFColor(java.awt.Color.GREEN);
                
                /*
                // 文件头字体  
                XSSFFont font0 = create07Fonts(wb, XSSFFont.BOLDWEIGHT_BOLD, "宋体", false,  
                        (short) 400,XSSFFont.COLOR_NORMAL);  
                XSSFFont fontDay = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "宋体", false,  
                        (short) 400,XSSFFont.COLOR_RED);  
                */
                
                // 文件头字体  
                XSSFFont font0 = create07Fonts(wb, XSSFFont.BOLDWEIGHT_BOLD, "宋体", false,  
                        (short) 400,hfNormal);  
                XSSFFont fontDay = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "宋体", false,  
                        (short) 400,hfCday);  
                XSSFFont fontDayWeek = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "宋体", false,  
                        (short) 400,hfCweek);  
                XSSFFont fontDayWeekMonth = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "宋体", false,  
                        (short) 400,hfCmonth);  
                 

                //给第一行添加文本  
                create07Cell(wb, rowHead, 0, "序号", font0,IndexedColors.YELLOW.getIndex());  
                create07Cell(wb, rowHead, 1, "代号", font0,IndexedColors.YELLOW.getIndex());  
                create07Cell(wb, rowHead, 2, "名称", font0,IndexedColors.YELLOW.getIndex()); 	            
                create07Cell(wb, rowHead, 3, "地区", font0,IndexedColors.YELLOW.getIndex());            
                int stockColumn=4;
               
                //第二行开始展示  
                int stockRow = 1;  
                
                listIndustry=siDao.getAllIndustry(listStockInfo);	
                System.out.println("板块个数："+listIndustry.size());
                Iterator itIndu,ie;
                int stockNum=0;
    	   		 for(itIndu = listIndustry.iterator();itIndu.hasNext();)
    	   		 {
    	   			String stockIndu = (String) itIndu.next();			
    	   			//System.out.println(stockIndu);
    	   			
    	   		//	if(stockRow>100)
    	   		//		break;
    	   			
    	   			//行业
    	   			Row rowIndustry = sheet.createRow(stockRow);  
    	   			create07Cell(wb, rowIndustry, 0, stockIndu, font0,IndexedColors.BLUE.getIndex());  
    	   			stockRow++;
    	   			
    	   			listStockIndustry=siDao.getStockIndustry(stockIndu);
    	   			System.out.println("该板块股票个数："+listStockIndustry.size());
    	   			for(ie=listStockIndustry.iterator();ie.hasNext();)
    	   			{
    	   				String stockFullId = (String) ie.next();	
    	   				int isTableExist=sdDao.isExistStockTable(stockFullId,ConstantsInfo.TABLE_DATA_STOCK);
    	   				if(isTableExist==0)//不存在
    	   					continue;   				

    	   				StockInformation si=siDao.getStockInformation(stockFullId);			
    	   				
    	    		//	String stockFullId=si.getStockFullId();
    	    		
        			
    	   			//股票
    	    			Row rowDayData = sheet.createRow(stockRow);  
    	    			
    	    			create07Cell(wb, rowDayData, 0,  Integer.toString(stockRow), font0,ConstantsInfo.XSSFNORMAL);  
    		            create07Cell(wb, rowDayData, 1, stockFullId, font0,ConstantsInfo.XSSFNORMAL);  
    		            create07Cell(wb, rowDayData, 2, si.getStockName(), font0,ConstantsInfo.XSSFNORMAL); 	            
    		            create07Cell(wb, rowDayData, 3, si.getStockRegional(), font0,ConstantsInfo.XSSFNORMAL); 

    		          stockColumn=4;
    		            
    		            stockRow++;
    		      		            
    		            //if(!stockFullId.equals("sh000001"))
    					//		continue;
    	    			
    	    			if(!stockFullId.equals("sh600000") && !stockFullId.equals("sz002142") && !stockFullId.equals("sz000001"))
    	    					continue;
    	    			List<StockPoint> stockDayPoint=new ArrayList<StockPoint>();
    	    			List<StockPoint> stockWeekPoint=new ArrayList<StockPoint>();
    	    			List<StockPoint> stockMonthPoint=new ArrayList<StockPoint>();
    	    			
    	    			
    	    			DateStock dStock=sdDao.getDateStock(stockFullId);
    	    			
    	    			stockDayPoint=pClass.getStockDayExtremePoint(stockFullId, dStock,ConstantsInfo.StockCalAllData);
    	    			stockWeekPoint=pClass.getStockWeekExtremePoint(stockFullId, dStock,ConstantsInfo.StockCalAllData);
    	    			stockMonthPoint=pClass.getStockMonthExtremePoint(stockFullId, dStock,ConstantsInfo.StockCalAllData);
    	    			System.out.println("day size:"+stockDayPoint.size());
    	    			System.out.println("week size:"+stockWeekPoint.size());
    	    			System.out.println("month size:"+stockMonthPoint.size());    			
    	    				
    		            //日极点
    	    			System.out.println("日stockRow："+stockRow);
    	    			System.out.println("******all point*******");
    	    			
    	    			  //周时间与比例 
    	                HashMap<String, StockPoint> stockWeekDateExist = new HashMap<String, StockPoint>(); 
    	                HashMap<String, StockPoint> stockMonthDateExist = new HashMap<String, StockPoint>(); 
    	                
    	    			for(Iterator itWeek = stockWeekPoint.iterator();itWeek.hasNext();)
    		 	    	{
    		 	            StockPoint sPointWeek=(StockPoint)itWeek.next();	 	            
    		 	           stockWeekDateExist.put(sPointWeek.getExtremeDate().toString(), sPointWeek);
    		 	    	}
    	    			
    	   			}
    	   		 }
        		
        		os = new FileOutputStream("stockExcel.xlsx");
    	         
    	         wb.write(os);
    	          //  os.flush();
    	         os.close();
        
                return wb;  
            }     
          
        
        public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException {
    		
        	PropertyConfigurator.configure("log4j.excelWriter.properties");
        	
    		ExcelWriterV2 ew=new ExcelWriterV2();
    		
    		HSSFWorkbook wb;
    		Date startDate = new Date();
    	//	wb=ew.exportExcelForStock();
    		
    		//XSSFWorkbook xwb;
    		//xwb=ew.exportExcelForAllStock();
    		
    		//	xwb=ew.exportExcelForAllStockSimple();//股票极值点显示一行 当时计算出来
    		
    		//当前路径
    		ew.exportExcelAllStockForIndustry("load\\","stockPointIndustry.xlsx");//股票极值点显示一行 读取pointStock表

    		//当前路径
    		ew.exportExcelAllStockForConcept("load\\","stockPointConcept.xlsx");//股票极值点显示一行 读取pointStock表
    		
    		
    		Date endDate = new Date();
    		// 计算两个时间点相差的秒数
    		long seconds =(endDate.getTime() - startDate.getTime())/1000;
    		System.out.println("总共耗时："+seconds+"秒");
    		System.out.println("end");
    		stockLogger.logger.debug("end");
    			
    	}
}
