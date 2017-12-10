package stock.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import common.ConstantsInfo;
import dao.StockBaseFace;
import dao.StockDataDao;
import dao.StockOperation;
import dao.StockPoint;

import dao.StockSummary;
import stock.timer.stockDater;
import stock.timer.CommonDate;

public class ExcelCommon {
	
	final static int stockNameIdNum=3;
	final static int stockSummaryNum=15;
	final static int stockOperationNum=7;
	final static int stockLabelNum=10;
	
	final static int summaryColNum=240; 
	
	final static int stockStatNum=22; //ͳ��
	
	final static int FirstRowFirstColNum=52; //��һ�� �� �� �·�������
	

	final static int stockRowFirstValueNum=10;
	final static int stockRowFirstDesireNum=18;
	final static int stockRowFirstOperateNum=5;
	final static int stockRowFirstDateGapNum=6; //��ֵ
	final static int stockRowFirstValueGapNum=6; //��ֵ
	final static int stockRowFirstMarketNum=7; //���̶Ա�
	
	final static int stockGapNum=15; //
	
	final static int stockMaxPointNum=300;
	final static int largeColWidth=4500;
	final static int middleColWidth=3500;
	final static int middleColWidth2=2800;
	final static int smallColWidth=2000;
	
	public ExcelCommon()
	{
		
	}
	
	//С��תΪ�ٷֱ�
	public static  String  changePercent(float value)
	{
		  //��ȡ��ʽ������
		   NumberFormat nt = NumberFormat.getPercentInstance();
		   //���ðٷ�����ȷ��2��������λС��
		   nt.setMinimumFractionDigits(2);
	
		   return nt.format(value);
	}
	
	 //excel07 
    public static void create07Cell(Workbook wb, Row row, int column, String value, int color) {  
        
    	XSSFColor xfcolor = null;
    	Cell cell = row.createCell(column); 
        
        CellStyle cellStyle = wb.createCellStyle();  
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);  
        cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_BOTTOM);  
        
        switch(color) {
        case ConstantsInfo.REDColor:
        	xfcolor=new XSSFColor(java.awt.Color.RED);
        	break;
        case ConstantsInfo.CYANColor:
        	xfcolor=new XSSFColor(java.awt.Color.CYAN);
        	break;
        case ConstantsInfo.GRAYColor:
        	xfcolor=new XSSFColor(java.awt.Color.GRAY);
        	break;
        case ConstantsInfo.BLUEColor:
        	xfcolor=new XSSFColor(java.awt.Color.BLUE);
        	break;
        case ConstantsInfo.ORANGEColor:
        	xfcolor=new XSSFColor(java.awt.Color.ORANGE);
        	break;
        case ConstantsInfo.GREENColor:
        	xfcolor=new XSSFColor(java.awt.Color.GREEN);
        	break;
        default:
        	break;
        }
       
        if (color >0){
	       // �ļ�ͷ����       
	        XSSFFont font = (XSSFFont) wb.createFont();  
	        font.setFontName("����");  
	      //  font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);  
	      //  font.setItalic(false);  
	      //  font.setFontHeight((short) 250);  
	        font.setColor(xfcolor); 
	        cellStyle.setFont(font);  
        }
        
        
      //  cellStyle.setFillBackgroundColor(HSSFColor.BLUE.index);
       // IndexedColors.YELLOW.getIndex()
        /*
        if(color>0) {
        	cellStyle.setFillForegroundColor((short) color);            
        	cellStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);  
        }
       
    
        // ## �����ұ߱߿���ɫΪ��ɫ ##//  
        if (column == 2) {
        	cellStyle.setRightBorderColor(new HSSFColor.RED().getIndex());  
        } */
        
        cell.setCellStyle(cellStyle);  
        cell.setCellValue(value);  
    } 
    
    public static XSSFFont create07Fonts(Workbook wb, short bold, String fontName, boolean isItalic, short hight,XSSFColor color) {  
    	XSSFFont font = (XSSFFont) wb.createFont();  
        font.setFontName(fontName);  
        font.setBoldweight(bold);  
        font.setItalic(isItalic);  
        font.setFontHeight(hight);  
        font.setColor(color);             
        return font;  
    }  
    
    
  //����excel
    public static void createRecentExcel(String filePath, String fileName,StockDataDao sdDao,HashMap<String, Integer> stockDateColumnmap,int type) throws IOException, SQLException
    {
    	  // �����  
		OutputStream os = null;  
		int i=0;
		File file = new File(filePath);  
		System.out.println(filePath);
		if (!file.exists())
		{   
			 file.mkdir();   
		}    
		
		//��������Ŀ¼
		file = new File(filePath+fileName);  
		System.out.println(fileName);
		if (!file.exists())
		{   
			 file.mkdir();   
		}
		
		String excleFileName=null;
		if (type == 0)
			excleFileName="Stock_Concept_Simple_"+fileName+".xlsx";
		else
			excleFileName="Stock_Industry_Simple_"+fileName+".xlsx";
		
		 os = new FileOutputStream(filePath +fileName+ "\\"+excleFileName);  
		 // ������
		 XSSFWorkbook wb = new XSSFWorkbook();   
		 // ������һ��sheet     
		 XSSFSheet sheet= wb.createSheet("allstock");
		
    
         //������һ��  
         Row rowHead = sheet.createRow(0);  

         int col=stockLabelNum;
         //����һ������ı�                
         create07Cell(wb, rowHead, 0, "���", 0);  
         create07Cell(wb, rowHead, 1, "����", 0);  
         create07Cell(wb, rowHead, 2, "����", 0); 	            
         create07Cell(wb, rowHead, 3, "����", 0);   
         
        List<String> listStockDate = new ArrayList<String>();
       //���������ʱ��
        Date dt=new Date();
     
       String  cDate  = stockDater.getAddDate(dt,-90);
       System.out.println("������ǰ��"+cDate);		
     
		listStockDate=sdDao.getDatesFromSH000001RecentDate(cDate);
 		System.out.println("date size:"+listStockDate.size());	
 		//�п�
        for(i=0;i<stockLabelNum;i++)
        {	
        	sheet.setColumnWidth(i, 3500);         
        }
         //�п�
         for(i=stockLabelNum;i<=stockLabelNum+listStockDate.size()+1;i++)
         {	
         	sheet.setColumnWidth(i, 2000);    
         }
         
         /*
         create07Cell(wb, rowHead, 4, "��ǰ�ռ���", cellStyleTitle);
         create07Cell(wb, rowHead, 5, "��ǰ�ܼ���", cellStyleTitle);
         create07Cell(wb, rowHead, 6, "��ǰ�¼���", cellStyleTitle);
        */ 
         int  stockColumn=stockLabelNum;//Ԥ����������ǰ����
             
       
        for(Iterator it = listStockDate.iterator();it.hasNext();)
 		{
        	//2015-08-20
         	String stockDate=(String)it.next();  
         	stockDateColumnmap.put(stockDate, stockColumn);
         	stockDate=stockDate.substring(5);
         	//ȡ���£��� 
         	create07Cell(wb, rowHead, stockColumn, stockDate, 0); 
         	
         	stockColumn++;
 		}  
         

		 wb.write(os);
		 os.close();
    }
    
    
  //����excel
    public  static void createPointExcel(Workbook wb,Sheet sheet,String filePath, String fileName, StockDataDao sdDao,HashMap<String, Integer> stockDateColumnmap) throws SQLException 
    {
		int i=0;
		int colStart=0;
		int colEnd=0;
		 //�п�
         for(i=0;i<stockNameIdNum;i++)
         	sheet.setColumnWidth(i, middleColWidth);
         
         String date = CommonDate.getCurDate();
         String startDate = CommonDate.getBeforeDay(date, 1, 180);
         List<String> listStockDate = new ArrayList<String>();
         
         listStockDate = sdDao.getDatesFromSH000001ForStartEnd(startDate,date);
        
         //������һ��  
         Row rowHead = sheet.createRow(0);      
         colStart = 0;
         colEnd = colStart+stockNameIdNum -1;
         //��һ�кϲ���Ԫ��
         CellRangeAddress craBase=new CellRangeAddress(0, 0, colStart, colEnd);     
         //��sheet�����Ӻϲ���Ԫ��  
         sheet.addMergedRegion(craBase);         
         create07Cell(wb, rowHead, 0, "��Ϣ", 0);          
             
        
         
         int  stockColumn=0;//
         for(Iterator it = listStockDate.iterator();it.hasNext();)
  		{
         	//2015-08-20
          	String stockDate=(String)it.next();  
          	stockDateColumnmap.put(stockDate, stockColumn);
          	sheet.setColumnWidth(stockColumn+stockNameIdNum,middleColWidth);
	        create07Cell(wb, rowHead, stockColumn+stockNameIdNum, stockDate, 0); 
          	
          	stockColumn++;
  		} 

    }
    
    
  //����excel
    public  static void createTotalOperationExcel(Workbook wb,Sheet sheet,String filePath, String fileName, StockDataDao sdDao,HashMap<String, Integer> stockDateColumnmap, int dateType) throws SQLException 
    {
		int i=0;
		int colStart=0;
		int colEnd=0;
		 //�п�
         for(i=0;i<stockNameIdNum;i++)
         	sheet.setColumnWidth(i, middleColWidth);
         
         List<String> listStockDate = new ArrayList<String>();   
         /*
         String date = StockDateTimer.getCurDate();
         String startDate = StockDateTimer.getBeforeDay(date, 1, 180);   
         listStockDate = sdDao.getDatesFromSH000001ForStartEnd(startDate,date);
         */
         
         //  listStockDate = sdDao.getDatesFromSH000001For(60, ConstantsInfo.DayDataType);
         switch(dateType)
 		{
 		case ConstantsInfo.DayDataType:
 		default:
 			listStockDate = sdDao.getDatesFromSH000001For(ConstantsInfo.ExportDayNum, ConstantsInfo.DayDataType);
 			break;
 		case ConstantsInfo.WeekDataType:
 			 listStockDate = sdDao.getDatesFromSH000001For(ConstantsInfo.ExportWeekNum, ConstantsInfo.WeekDataType);
 			break;
 		case ConstantsInfo.MonthDataType:
 			 listStockDate = sdDao.getDatesFromSH000001For(ConstantsInfo.ExportMonthNum, ConstantsInfo.MonthDataType);
 			break;
 		}
        
         //������һ��  
         Row rowHead = sheet.createRow(0);      
         colStart = 0;
         colEnd = colStart+stockNameIdNum -1;
         //��һ�кϲ���Ԫ��
         CellRangeAddress craBase=new CellRangeAddress(0, 0, colStart, colEnd);     
         //��sheet�����Ӻϲ���Ԫ��  
         sheet.addMergedRegion(craBase);         
         create07Cell(wb, rowHead, 0, "��Ϣ", 0);          
             
         int  stockColumn=0;//
         for(Iterator it = listStockDate.iterator();it.hasNext();)
  		{
         	//2015-08-20
          	String stockDate=(String)it.next();  
          	stockDateColumnmap.put(stockDate, stockColumn);
          
	        colStart = colEnd +1;
	         colEnd = colStart + stockNameIdNum -1;
	         CellRangeAddress craStat=new CellRangeAddress(0, 0, colStart, colEnd);     
	         sheet.addMergedRegion(craStat);         
	         create07Cell(wb, rowHead, colStart, stockDate, 0); 
          	
          	stockColumn++;
  		} 
         
         //�����ڶ���  
         Row rowHead2 = sheet.createRow(1);      
                  
         create07Cell(wb, rowHead2, 0, "������Ϣ", 0);             
         int  col = stockNameIdNum; 
         for(int j =0;j <listStockDate.size();j++)
  		 {
        	 create07Cell(wb, rowHead2, col++, "��", 0); 
          	 create07Cell(wb, rowHead2, col++, "��", 0); 
             create07Cell(wb, rowHead2, col++, "��", 0);   
  		} 

    }
    
    
    
    //����excel
    public  static void createOperationExcel(Workbook wb,Sheet sheet,String filePath, String fileName, StockDataDao sdDao,HashMap<String, Integer> stockDateColumnmap, int dateType) throws SQLException 
    {
		int i=0;
		int colStart=0;
		int colEnd=0;
		 //�п�
         for(i=0;i<stockNameIdNum;i++)
         	sheet.setColumnWidth(i, middleColWidth);
         
         List<String> listStockDate = new ArrayList<String>();

         /*
         switch(dateType)
 		{
 		case ConstantsInfo.DayDataType:
 		default:
 			 listStockDate = sdDao.getDatesFromSH000001For(30, ConstantsInfo.DayDataType);
 			break;
 		case ConstantsInfo.WeekDataType:
 			 listStockDate = sdDao.getDatesFromSH000001For(30, ConstantsInfo.WeekDataType);
 			break;
 		case ConstantsInfo.MonthDataType:
 			 listStockDate = sdDao.getDatesFromSH000001For(30, ConstantsInfo.MonthDataType);
 			break;
 		}
 		*/
         listStockDate = sdDao.getDatesFromSH000001For(60, ConstantsInfo.DayDataType);
        
         /*
         //�п�
         for(i=stockNameIdNum;i<=stockNameIdNum+listStockDate.size()+1;i++)
         {	
         	sheet.setColumnWidth(i, middleColWidth);    
         }
         */
             
         //������һ��  
         Row rowHead = sheet.createRow(0);      
         colStart = 0;
         colEnd = colStart+stockNameIdNum -1;
         //��һ�кϲ���Ԫ��
         CellRangeAddress craBase=new CellRangeAddress(0, 0, colStart, colEnd);     
         //��sheet�����Ӻϲ���Ԫ��  
         sheet.addMergedRegion(craBase);         
         create07Cell(wb, rowHead, 0, "��Ϣ", 0);          
        
      
         colStart = colEnd +1;
         colEnd = colStart + stockOperationNum; //7��
         CellRangeAddress craStatT=new CellRangeAddress(0, 0, colStart, colEnd);     
         sheet.addMergedRegion(craStatT);         
         create07Cell(wb, rowHead, colStart, "ͳ��", 0); 
         
         int  stockColumn=1;//
         for(Iterator it = listStockDate.iterator();it.hasNext();)
  		{
         	//2015-08-20
          	String stockDate=(String)it.next();  
          	stockDateColumnmap.put(stockDate, stockColumn);
          	
	      	 colStart = colEnd +1;
	         colEnd = colStart + stockOperationNum -1;
	         CellRangeAddress craStat=new CellRangeAddress(0, 0, colStart, colEnd);     
	         sheet.addMergedRegion(craStat);         
	         create07Cell(wb, rowHead, colStart, stockDate, 0); 
          	
          	stockColumn++;
  		} 
         
         //�����ڶ���  
         Row rowHead2 = sheet.createRow(1);      
                  
         create07Cell(wb, rowHead2, 0, "������Ϣ", 0);          
        
         int  col = stockNameIdNum; 
      	 create07Cell(wb, rowHead2, col++, "Ӯ��", 0); 
         create07Cell(wb, rowHead2, col++, "ֹ��", 0); 
         create07Cell(wb, rowHead2, col++, "����", 0);       
         create07Cell(wb, rowHead2, col++, "����", 0);  
         create07Cell(wb, rowHead2, col++, "ӯ��", 0); 
         create07Cell(wb, rowHead2, col++, "ֹ��", 0);          
         create07Cell(wb, rowHead2, col++, "����", 0); 
         create07Cell(wb, rowHead2, col++, "������", 0); 
         
         for(int j =0;j <listStockDate.size();j++)
  		 {
        	 create07Cell(wb, rowHead2, col++, "���", 0); 
          	 create07Cell(wb, rowHead2, col++, "���", 0); 
             create07Cell(wb, rowHead2, col++, "ֹ���", 0); 
             create07Cell(wb, rowHead2, col++, "����", 0);          
             create07Cell(wb, rowHead2, col++, "ӯ��", 0); 
             create07Cell(wb, rowHead2, col++, "ֹ��", 0);          
             create07Cell(wb, rowHead2, col++, "����", 0);   
  		} 
         
    }
    
    
    
    //����excel
    public  static void createSummaryExcel(Workbook wb,Sheet sheet,String filePath, String fileName, StockDataDao sdDao,HashMap<String, Integer> stockDateColumnmap) throws SQLException 
    {
		int i=0;
		int colStart=0;
		int colEnd=0;
		 //�п�
         for(i=0;i<stockNameIdNum;i++)
         	sheet.setColumnWidth(i, middleColWidth);
         
         List<String> listStockDate = new ArrayList<String>();
         
         listStockDate = sdDao.getDatesFromSH000001For(15, ConstantsInfo.DayDataType);
      //   if300
        
         /*
         //�п�
         for(i=stockNameIdNum;i<=stockNameIdNum+listStockDate.size()+1;i++)
         {	
         	sheet.setColumnWidth(i, middleColWidth);    
         }
         */
             
         //������һ��  
         Row rowHead = sheet.createRow(0);      
         colStart = 0;
         colEnd = colStart+stockNameIdNum -1;
         //��һ�кϲ���Ԫ��
         CellRangeAddress craBase=new CellRangeAddress(0, 0, colStart, colEnd);     
         //��sheet�����Ӻϲ���Ԫ��  
         sheet.addMergedRegion(craBase);         
         create07Cell(wb, rowHead, 0, "��Ϣ", 0);          
        
         int  stockColumn=0;//
         for(Iterator it = listStockDate.iterator();it.hasNext();)
  		{
         	//2015-08-20
          	String stockDate=(String)it.next();  
          	stockDateColumnmap.put(stockDate, stockColumn);
          	
	      	 colStart = colEnd +1;
	         colEnd = colStart + stockSummaryNum -1;
	         CellRangeAddress craStat=new CellRangeAddress(0, 0, colStart, colEnd);     
	         sheet.addMergedRegion(craStat);         
	         create07Cell(wb, rowHead, colStart, stockDate, 0); 
          	
          	stockColumn++;
  		} 
         
         //�����ڶ���  
         Row rowHead2 = sheet.createRow(1);      
                  
         create07Cell(wb, rowHead2, 0, "������Ϣ", 0);          
        
         int  col = stockNameIdNum;
         for(int j =0;j <listStockDate.size();j++)
  		 {
        	 sheet.setColumnWidth(col, middleColWidth);
        	 create07Cell(wb, rowHead2, col++, "�Ƿ�", 0); 
          	 create07Cell(wb, rowHead2, col++, "״̬���", 0); 
             create07Cell(wb, rowHead2, col++, "�������", 0); 
             sheet.setColumnWidth(col, middleColWidth);
             create07Cell(wb, rowHead2, col++, "��ǰ��", 0);  
             sheet.setColumnWidth(col, middleColWidth);
             create07Cell(wb, rowHead2, col++, "���ɼ�", 0); 
             create07Cell(wb, rowHead2, col++, "�ռ���", 0);          
             create07Cell(wb, rowHead2, col++, "���ɵ�", 0); 
             sheet.setColumnWidth(col, middleColWidth);
             create07Cell(wb, rowHead2, col++, "��ǰ��", 0); 
             sheet.setColumnWidth(col, middleColWidth);
             create07Cell(wb, rowHead2, col++, "���ɼ�", 0);
             create07Cell(wb, rowHead2, col++, "�ܼ���", 0);          
             create07Cell(wb, rowHead2, col++, "���ɵ�", 0); 
             sheet.setColumnWidth(col, middleColWidth);
             create07Cell(wb, rowHead2, col++, "��ǰ��", 0);  
             sheet.setColumnWidth(col, middleColWidth);
             create07Cell(wb, rowHead2, col++, "���ɼ�", 0);
             create07Cell(wb, rowHead2, col++, "�¼���", 0);          
             create07Cell(wb, rowHead2, col++, "���ɵ�", 0);  
  		} 
         
    }
    
   
	//����excel
    public  static void createExcel(Workbook wb,Sheet sheet,String filePath, String fileName) 
    {
		int i=0;
		int colStart=0;
		int colEnd=0;
		 //�п�
         for(i=0;i<3;i++)
         	sheet.setColumnWidth(i, middleColWidth);
         
         for(i=3;i<stockLabelNum+2*FirstRowFirstColNum;i++)
          	sheet.setColumnWidth(i, middleColWidth2);
             
         
         //������һ��  
         Row rowHead = sheet.createRow(0);      
         colStart = 0;
         colEnd = colStart+stockLabelNum -1;
         //��һ�кϲ���Ԫ��
         CellRangeAddress craBase=new CellRangeAddress(0, 0, colStart, colEnd);     
         //��sheet�����Ӻϲ���Ԫ��  
         sheet.addMergedRegion(craBase);         
         create07Cell(wb, rowHead, 0, "������Ϣ", 0);          
        
         colStart = colEnd +1;
         colEnd = colStart + stockStatNum -1;
         CellRangeAddress craStat=new CellRangeAddress(0, 0, colStart, colEnd);     
         sheet.addMergedRegion(craStat);         
         create07Cell(wb, rowHead, stockLabelNum, "ͳ��", 0); 
         
         colStart = colEnd +1;
         colEnd = colStart + stockRowFirstValueNum -1;
         CellRangeAddress craDayPoint=new CellRangeAddress(0, 0, colStart, colEnd);     
         sheet.addMergedRegion(craDayPoint);         
         create07Cell(wb, rowHead, colStart, "�յ�λʱ��", 0); 
         
         colStart = colEnd +1;
         colEnd = colStart + stockRowFirstDateGapNum -1;
         CellRangeAddress craDayPointGap=new CellRangeAddress(0, 0, colStart, colEnd);     
         sheet.addMergedRegion(craDayPointGap);//��ֵ��λ
         create07Cell(wb, rowHead,colStart, "*�ռ�ֵ��ֵ*", 0); 
         
         colStart = colEnd +1;
         colEnd = colStart + stockRowFirstMarketNum -1;
         CellRangeAddress craDayMarketDesc=new CellRangeAddress(0, 0,colStart, colEnd);     
         sheet.addMergedRegion(craDayMarketDesc);
         create07Cell(wb, rowHead,colStart, "*���̶Ա�*", 0); 
         
       
         colStart = colEnd +1;
         colEnd = colStart + stockRowFirstDesireNum -1;
         CellRangeAddress craDayDesire=new CellRangeAddress(0, 0, colStart, colEnd);     
         sheet.addMergedRegion(craDayDesire);
         create07Cell(wb, rowHead, colStart, "��Ԥ����Ϣ", 0); 
         
       
         colStart = colEnd +1;
         colEnd = colStart + stockRowFirstValueGapNum -1;
         CellRangeAddress craDayValueGap=new CellRangeAddress(0, 0, colStart, colEnd);     
         sheet.addMergedRegion(craDayValueGap);
         create07Cell(wb, rowHead,colStart, "*��Ԥ�ڲ�ֵ*", 0); 
         
         colStart = colEnd +1;
         colEnd = colStart + stockRowFirstOperateNum -1;
         CellRangeAddress craDayOper=new CellRangeAddress(0, 0, colStart,colEnd);     
         sheet.addMergedRegion(craDayOper);//
         create07Cell(wb, rowHead, colStart, "�ղ���", 0); 
         
         colStart = colEnd +1;
         colEnd = colStart + stockRowFirstValueNum -1;
         CellRangeAddress craWeekPoint=new CellRangeAddress(0, 0, colStart, colEnd);     
         sheet.addMergedRegion(craWeekPoint);         
         create07Cell(wb, rowHead, colStart, "�ܵ�λʱ��", 0); 
         
         colStart = colEnd +1;
         colEnd = colStart + stockRowFirstDateGapNum -1;
         CellRangeAddress craWeekPointGap=new CellRangeAddress(0, 0, colStart, colEnd);     
         sheet.addMergedRegion(craWeekPointGap);//��ֵ��λ
         create07Cell(wb, rowHead,colStart, "*�ܼ�ֵ��ֵ*", 0); 
         
         colStart = colEnd +1;
         colEnd = colStart + stockRowFirstMarketNum -1;
         CellRangeAddress craWeekMarketDesc=new CellRangeAddress(0, 0,colStart, colEnd);     
         sheet.addMergedRegion(craWeekMarketDesc);
         create07Cell(wb, rowHead,colStart, "*���̶Ա�*", 0); 
       
         colStart = colEnd +1;
         colEnd = colStart + stockRowFirstDesireNum -1;
         CellRangeAddress craWeekDesire=new CellRangeAddress(0, 0, colStart, colEnd);     
         sheet.addMergedRegion(craWeekDesire);
         create07Cell(wb, rowHead, colStart, "��Ԥ����Ϣ", 0); 
        
         
         colStart = colEnd +1;
         colEnd = colStart + stockRowFirstValueGapNum -1;
         CellRangeAddress craWeekValueGap=new CellRangeAddress(0, 0, colStart, colEnd);     
         sheet.addMergedRegion(craWeekValueGap);
         create07Cell(wb, rowHead,colStart, "*��Ԥ�ڲ�ֵ*", 0); 
         
         colStart = colEnd +1;
         colEnd = colStart + stockRowFirstOperateNum -1;
         CellRangeAddress craWeekOper=new CellRangeAddress(0, 0, colStart,colEnd);     
         sheet.addMergedRegion(craWeekOper);//
         create07Cell(wb, rowHead, colStart, "�ܲ���", 0); 
         
         colStart = colEnd +1;
         colEnd = colStart + stockRowFirstValueNum -1;
         CellRangeAddress craMonPoint=new CellRangeAddress(0, 0, colStart, colEnd);     
         sheet.addMergedRegion(craMonPoint);         
         create07Cell(wb, rowHead, colStart, "�µ�λʱ��", 0); 
       
         
         colStart = colEnd +1;
         colEnd = colStart + stockRowFirstDateGapNum -1;
         CellRangeAddress craMonPointGap=new CellRangeAddress(0, 0, colStart, colEnd);     
         sheet.addMergedRegion(craMonPointGap);//��ֵ��λ
         create07Cell(wb, rowHead,colStart, "*�¼�ֵ��ֵ*", 0); 
         
         colStart = colEnd +1;
         colEnd = colStart + stockRowFirstMarketNum -1;
         CellRangeAddress craMonMarketDesc=new CellRangeAddress(0, 0,colStart, colEnd);     
         sheet.addMergedRegion(craMonMarketDesc);
         create07Cell(wb, rowHead,colStart, "*���̶Ա�*", 0);  
         
         colStart = colEnd +1;
         colEnd = colStart + stockRowFirstDesireNum -1;
         CellRangeAddress craMonDesire=new CellRangeAddress(0, 0, colStart, colEnd);     
         sheet.addMergedRegion(craMonDesire);
         create07Cell(wb, rowHead, colStart, "��Ԥ����Ϣ", 0); 
               
         colStart = colEnd +1;
         colEnd = colStart + stockRowFirstValueGapNum -1;
         CellRangeAddress craMonValueGap=new CellRangeAddress(0, 0, colStart, colEnd);     
         sheet.addMergedRegion(craMonValueGap);
         create07Cell(wb, rowHead,colStart, "*��Ԥ�ڲ�ֵ*", 0); 
         
        
         
         colStart = colEnd +1;
         colEnd = colStart + stockRowFirstOperateNum -1;
         CellRangeAddress craMonOper=new CellRangeAddress(0, 0, colStart,colEnd);     
         sheet.addMergedRegion(craMonOper);//
         create07Cell(wb, rowHead, colStart, "�²���", 0); 
         
         Row rowHead4 = sheet.createRow(1);           
         create07Cell(wb, rowHead4, 0, "���", 0);  
         create07Cell(wb, rowHead4, 1, "����", 0);  
         create07Cell(wb, rowHead4, 2, "����", 0); 	            
         create07Cell(wb, rowHead4, 3, "����", 0);    
         sheet.setColumnWidth(4, 2*largeColWidth);
         create07Cell(wb, rowHead4, 4, "����Ԥ��", 0); 
         create07Cell(wb, rowHead4, 5, "����", 0); 
         create07Cell(wb, rowHead4, 6, "����", 0); 
         create07Cell(wb, rowHead4, 7, "����", 0); 
         create07Cell(wb, rowHead4, 8, "Ǳ��", 0); 
         create07Cell(wb, rowHead4, 9, "��ͷ", 0); 
         
         int col=stockLabelNum;  
         //ͳ��    
         create07Cell(wb, rowHead4, col++, "�����Ƿ�", 0); 
         sheet.setColumnWidth(col, middleColWidth);
         create07Cell(wb, rowHead4, col++, "ǰ����״̬", 0); 
         create07Cell(wb, rowHead4, col++, "ǰ�������", 0); 
         create07Cell(wb, rowHead4, col++, "�ɼ�״̬", 0); 
         create07Cell(wb, rowHead4, col++, "�ɼ����", 0); 
         create07Cell(wb, rowHead4, col++, "�շ�������", 0); 
         create07Cell(wb, rowHead4, col++, "��״̬", 0);
         create07Cell(wb, rowHead4, col++, "ǰ��ͣ", 0); 
         create07Cell(wb, rowHead4, col++, "���ǵ�ͣ��", 0); 
         create07Cell(wb, rowHead4, col++, "��ʾ", 0); 
         create07Cell(wb, rowHead4, col++, "�ռ���", 0);          
         create07Cell(wb, rowHead4, col++, "���ɵ�", 0); 
         
        // sheet.setColumnWidth(col, middleColWidth);
         create07Cell(wb, rowHead4, col++, "�ܷ�������", 0); 
         create07Cell(wb, rowHead4, col++, "��״̬", 0);
         create07Cell(wb, rowHead4, col++, "���ǵ�ͣ��", 0); 
         create07Cell(wb, rowHead4, col++, "��ʾ", 0); 
         create07Cell(wb, rowHead4, col++, "�ܼ���", 0);          
         create07Cell(wb, rowHead4, col++, "���ɵ�", 0); 
        // sheet.setColumnWidth(col, middleColWidth);
         create07Cell(wb, rowHead4, col++, "���ǵ�ͣ��", 0);  
         create07Cell(wb, rowHead4, col++, "��ʾ", 0); 
         create07Cell(wb, rowHead4, col++, "�¼���", 0);          
         create07Cell(wb, rowHead4, col++, "���ɵ�", 0); 
         
         
         sheet.setColumnWidth(col, middleColWidth); 
         create07Cell(wb, rowHead4, col++, "ǰ��(��)ʱ��", 0);
         create07Cell(wb, rowHead4, col++, "ǰ��(��)��", 0);
         create07Cell(wb, rowHead4, col++, "��ת����", 0); 
         sheet.setColumnWidth(col, middleColWidth);       
         create07Cell(wb, rowHead4, col++, "ǰ��ʱ��", 0); 
         create07Cell(wb, rowHead4, col++, "ǰ����λ", 0);   
         sheet.setColumnWidth(col, middleColWidth);
         create07Cell(wb, rowHead4, col++, "�ɼ�ʱ��", 0); 
         create07Cell(wb, rowHead4, col++, "�ɼ���λ", 0); 
         sheet.setColumnWidth(col, middleColWidth);  
         create07Cell(wb, rowHead4, col++, "��ǰʱ��", 0); 
         create07Cell(wb, rowHead4, col++, "��ǰ��λ", 0);     
         create07Cell(wb, rowHead4, col++, "��������", 0);
         
       ///�ղ�ֵ          
         create07Cell(wb, rowHead4, col++, "����ʱ���", 0);          
         create07Cell(wb, rowHead4, col++, "��λ��", 0); 
         create07Cell(wb, rowHead4, col++, "����ʱ���", 0); 
         create07Cell(wb, rowHead4, col++, "��λ��", 0); 
         create07Cell(wb, rowHead4, col++, "�ɵ�ʱ���", 0); 
         create07Cell(wb, rowHead4, col++, "��λ��", 0); 
         
         create07Cell(wb, rowHead4, col++, "����ʱ���", 0); 
         create07Cell(wb, rowHead4, col++, "�ռ�", 0);  
         create07Cell(wb, rowHead4, col++, "����ʱ���", 0); 
         create07Cell(wb, rowHead4, col++, "�ռ�", 0); 
         create07Cell(wb, rowHead4, col++, "�ɵ�ʱ���", 0); 
         create07Cell(wb, rowHead4, col++, "�ռ�", 0); 
         create07Cell(wb, rowHead4, col++, "����һ��", 0); 

         //��Ԥ�� 8         
         create07Cell(wb, rowHead4, col++, "Ԥ��0.382", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
        
         create07Cell(wb, rowHead4, col++, "Ԥ��0.5", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
        
         create07Cell(wb, rowHead4, col++, "Ԥ��0.618", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
         
         create07Cell(wb, rowHead4, col++, "Ԥ��0.75", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
         
         create07Cell(wb, rowHead4, col++, "Ԥ��1", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
      
         create07Cell(wb, rowHead4, col++, "Ԥ��1.08", 0);          
         create07Cell(wb, rowHead4, col++, "����", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
        
         create07Cell(wb, rowHead4, col++, "0.382", 0); 
         create07Cell(wb, rowHead4, col++, "0.5", 0);          
         create07Cell(wb, rowHead4, col++, "0.618", 0); 
         create07Cell(wb, rowHead4, col++, "0.75", 0); 
         create07Cell(wb, rowHead4, col++, "1", 0); 
         create07Cell(wb, rowHead4, col++, "1.08", 0); 
       
         
         create07Cell(wb, rowHead4, col++, "��", 0); 
         create07Cell(wb, rowHead4, col++, "Ӯ", 0); 
         create07Cell(wb, rowHead4, col++, "��", 0); 
         sheet.setColumnWidth(col, middleColWidth);
         create07Cell(wb, rowHead4, col++, "����", 0); 
         sheet.setColumnWidth(col, middleColWidth);
         create07Cell(wb, rowHead4, col++, "��ʾ", 0); 
           
         //��
        
         sheet.setColumnWidth(col, middleColWidth); 
         create07Cell(wb, rowHead4, col++, "ǰ��(��)ʱ��", 0);
         create07Cell(wb, rowHead4, col++, "ǰ��(��)��", 0);
         create07Cell(wb, rowHead4, col++, "��ת����", 0); 
         sheet.setColumnWidth(col, middleColWidth);       
         create07Cell(wb, rowHead4, col++, "ǰ��ʱ��", 0); 
         create07Cell(wb, rowHead4, col++, "ǰ����λ", 0);   
         sheet.setColumnWidth(col, middleColWidth);
         create07Cell(wb, rowHead4, col++, "�ɼ�ʱ��", 0); 
         create07Cell(wb, rowHead4, col++, "�ɼ���λ", 0); 
         sheet.setColumnWidth(col, middleColWidth);  
         create07Cell(wb, rowHead4, col++, "��ǰʱ��", 0); 
         create07Cell(wb, rowHead4, col++, "��ǰ��λ", 0);     
         create07Cell(wb, rowHead4, col++, "��������", 0); 
         
         //��
         create07Cell(wb, rowHead4, col++, "����ʱ���", 0);          
         create07Cell(wb, rowHead4, col++, "��λ��", 0); 
         create07Cell(wb, rowHead4, col++, "����ʱ���", 0); 
         create07Cell(wb, rowHead4, col++, "��λ��", 0); 
         create07Cell(wb, rowHead4, col++, "�ɵ�ʱ���", 0); 
         create07Cell(wb, rowHead4, col++, "��λ��", 0); 
         
         create07Cell(wb, rowHead4, col++, "����ʱ���", 0); 
         create07Cell(wb, rowHead4, col++, "�ռ�", 0);  
         create07Cell(wb, rowHead4, col++, "����ʱ���", 0); 
         create07Cell(wb, rowHead4, col++, "�ռ�", 0); 
         create07Cell(wb, rowHead4, col++, "�ɵ�ʱ���", 0); 
         create07Cell(wb, rowHead4, col++, "�ռ�", 0); 
         create07Cell(wb, rowHead4, col++, "����һ��", 0); 
        
         create07Cell(wb, rowHead4, col++, "Ԥ��0.382", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
        
         create07Cell(wb, rowHead4, col++, "Ԥ��0.5", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
         
         create07Cell(wb, rowHead4, col++, "Ԥ��0.618", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
         
         create07Cell(wb, rowHead4, col++, "Ԥ��0.75", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
         
         create07Cell(wb, rowHead4, col++, "Ԥ��1", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
        
         create07Cell(wb, rowHead4, col++, "Ԥ��1.08", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0);     
         create07Cell(wb, rowHead4, col++, "����", 0); 
        
      
         create07Cell(wb, rowHead4, col++, "0.382", 0); 
         create07Cell(wb, rowHead4, col++, "0.5", 0);          
         create07Cell(wb, rowHead4, col++, "0.618", 0); 
         create07Cell(wb, rowHead4, col++, "0.75", 0); 
         create07Cell(wb, rowHead4, col++, "1", 0); 
         create07Cell(wb, rowHead4, col++, "1.08", 0); 
         
         create07Cell(wb, rowHead4, col++, "��", 0); 
         create07Cell(wb, rowHead4, col++, "Ӯ", 0); 
         create07Cell(wb, rowHead4, col++, "��", 0); 
         sheet.setColumnWidth(col, middleColWidth);
         create07Cell(wb, rowHead4, col++, "����", 0); 
         sheet.setColumnWidth(col, middleColWidth);
         create07Cell(wb, rowHead4, col++, "��ʾ", 0); 
        
         //��       
         sheet.setColumnWidth(col, middleColWidth); 
         create07Cell(wb, rowHead4, col++, "ǰ��(��)ʱ��", 0);
         create07Cell(wb, rowHead4, col++, "ǰ��(��)��", 0);
         create07Cell(wb, rowHead4, col++, "��ת����", 0); 
         sheet.setColumnWidth(col, middleColWidth);       
         create07Cell(wb, rowHead4, col++, "ǰ��ʱ��", 0); 
         create07Cell(wb, rowHead4, col++, "ǰ����λ", 0);   
         sheet.setColumnWidth(col, middleColWidth);
         create07Cell(wb, rowHead4, col++, "�ɼ�ʱ��", 0); 
         create07Cell(wb, rowHead4, col++, "�ɼ���λ", 0); 
         sheet.setColumnWidth(col, middleColWidth);  
         create07Cell(wb, rowHead4, col++, "��ǰʱ��", 0); 
         create07Cell(wb, rowHead4, col++, "��ǰ��λ", 0);     
         create07Cell(wb, rowHead4, col++, "��������", 0);
        
         create07Cell(wb, rowHead4, col++, "����ʱ���", 0);          
         create07Cell(wb, rowHead4, col++, "��λ��", 0); 
         create07Cell(wb, rowHead4, col++, "����ʱ���", 0); 
         create07Cell(wb, rowHead4, col++, "��λ��", 0); 
         create07Cell(wb, rowHead4, col++, "�ɵ�ʱ���", 0); 
         create07Cell(wb, rowHead4, col++, "��λ��", 0); 
         
         //�� ���̶Ա�
         create07Cell(wb, rowHead4, col++, "����ʱ���", 0);          
         create07Cell(wb, rowHead4, col++, "��λ��", 0); 
         create07Cell(wb, rowHead4, col++, "����ʱ���", 0); 
         create07Cell(wb, rowHead4, col++, "��λ��", 0); 
         create07Cell(wb, rowHead4, col++, "�ɵ�ʱ���", 0); 
         create07Cell(wb, rowHead4, col++, "��λ��", 0); 
         create07Cell(wb, rowHead4, col++, "����һ��", 0); 

         create07Cell(wb, rowHead4, col++, "Ԥ��0.382", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
     
         create07Cell(wb, rowHead4, col++, "Ԥ��0.5", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
         
         create07Cell(wb, rowHead4, col++, "Ԥ��0.618", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
     
         create07Cell(wb, rowHead4, col++, "Ԥ��0.75", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
  
         create07Cell(wb, rowHead4, col++, "Ԥ��1", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
       
         create07Cell(wb, rowHead4, col++, "Ԥ��1.08", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0);  
         create07Cell(wb, rowHead4, col++, "����", 0); 
         
         
         
         create07Cell(wb, rowHead4, col++, "0.382", 0); 
         create07Cell(wb, rowHead4, col++, "0.5", 0);          
         create07Cell(wb, rowHead4, col++, "0.618", 0); 
         create07Cell(wb, rowHead4, col++, "0.75", 0); 
         create07Cell(wb, rowHead4, col++, "1", 0); 
         create07Cell(wb, rowHead4, col++, "1.08", 0); 
          
         create07Cell(wb, rowHead4, col++, "��", 0); 
         create07Cell(wb, rowHead4, col++, "Ӯ", 0); 
         create07Cell(wb, rowHead4, col++, "��", 0); 
         sheet.setColumnWidth(col, middleColWidth);
         create07Cell(wb, rowHead4, col++, "����", 0); 
         sheet.setColumnWidth(col, middleColWidth);
         create07Cell(wb, rowHead4, col++, "��ʾ", 0); 
         col++;
    }
    
    //���ÿ��ҵ����̱���
    public static void writeExcelItemTitle(Workbook wb,Sheet sheet,String induName,StockBaseFace sbFace,int rowIndex, boolean flag)
    {
    	if (!flag){
    		return;
    	}
    	Row rowIndustry = sheet.createRow(rowIndex);  
    	int col=4;
    	create07Cell(wb, rowIndustry, 0, induName, ConstantsInfo.NoColor);  
    	if(sbFace!=null) {
	    	create07Cell(wb, rowIndustry, col++, sbFace.getBaseExpect(), 0); 
	    	create07Cell(wb, rowIndustry, col++, sbFace.getMain(), 0); 
	    	create07Cell(wb, rowIndustry, col++, sbFace.getPsychology(), 0); 
	    	create07Cell(wb, rowIndustry, col++, sbFace.getRisk(), 0); 
	    	create07Cell(wb, rowIndustry, col++, sbFace.getPotential(), 0); 
	    	create07Cell(wb, rowIndustry, col++, sbFace.getFaucet(), 0);
    	}
    }
    
    //���ÿ��ҵ����� ������ʾ
    public static void writeExcelItemDealWall(Workbook wb,Sheet sheet,String subTitle,int dealWarn,int rowIndex)
    {
    
    	Row rowStock =  sheet.getRow(rowIndex);
    	if(subTitle!=null)
    		create07Cell(wb, rowStock, 1, subTitle, ConstantsInfo.NoColor);  
    	
    	create07Cell(wb, rowStock, 1, subTitle, ConstantsInfo.NoColor); //һ����ҵ�µĹ�Ʊ����
    	
       	String warnDeal=null;
    	switch (dealWarn) {
    		case ConstantsInfo.DEAL_WARN_SEE: //����
    			warnDeal = "����";
    			break;
    		case ConstantsInfo.DEAL_WARN_SALE://����
    			warnDeal = "����";
    			break;
    		case ConstantsInfo.DEAL_WARN_INTEREST: //��ע
    			warnDeal = "��ע";
    			break;    		
    		case ConstantsInfo.DEAL_WARN_BUG://����
    			warnDeal = "����";
    			break;
    		default:
    			warnDeal = "δ֪";
    			break;
    	}    	
    	create07Cell(wb, rowStock, 3, warnDeal, 0); 	
  
    }
    
    
    
    //���ͳ��������
    public static void writeExcelStatItem(Workbook wb,Sheet sheet, StockExcelStatItem esItem,int stockRow, StockSummary ssu, boolean flag)
    {
		Row rowStock = null;
		
		if(flag) {
			 rowStock =  sheet.getRow(stockRow);
		}
    	int colNum = stockLabelNum;
    	String desc = "";
    	int ret=0;
    	String curTrend="";
    	String nextTrend="";
    	String warnDeal="";
		//System.out.println("UpOrDownFlag:"+esItem.getUpOrDownFlag());
		
    	StockMinMaxValue minMaxItem = esItem.getMinMaxItem();
    	StockStatValue daySValue = esItem.getDayStatItem();
    	StockMixStatValue dayMSvalue = esItem.getDayMixStatItem();
    	if(daySValue==null || dayMSvalue == null) 
    		return;
    	
    	if(daySValue.getUpOrdownFlag() ==1)
    		desc = "��ͣ:";
    	else
    		desc = "��ͣ:";    	
    	
    	//�����Ƿ�
    	String range= Float.toString(daySValue.getRange());
    	if(flag){
    		create07Cell(wb, rowStock, colNum++, range, 0);
    		//ǰ����״̬���
    		create07Cell(wb, rowStock, colNum++,dayMSvalue.getPriComState(), 0); 
    		//ǰ�������
    		create07Cell(wb, rowStock, colNum++,dayMSvalue.getPriState(), 0);    		
    		//���
        	create07Cell(wb, rowStock, colNum++,dayMSvalue.getComState(), 0); 
        	//���
        	create07Cell(wb, rowStock, colNum++,dayMSvalue.getPsState(), 0); 
        	//�շּ�����	
        	create07Cell(wb, rowStock, colNum++,dayMSvalue.getBuySaleGrade(), 0); 
        	//��״̬	
        	create07Cell(wb, rowStock, colNum++,dayMSvalue.getBuySaleState(), 0); 
    	}
    	ssu.setCurRange(range);
    	ssu.setPriComState(dayMSvalue.getPriComState());
    	ssu.setPriState(dayMSvalue.getPriState());
    	ssu.setComState(dayMSvalue.getComState());  	
    	ssu.setPsState(dayMSvalue.getPsState());	
    	ssu.setDaySaleGrade(dayMSvalue.getBuySaleGrade());
    	ssu.setDaySaleState(dayMSvalue.getBuySaleState());
    	
    	//ǰ��ͣʱ���
    	String[] priUpDateGap = daySValue.getPriUpDateGap().split("\\|");
    	int dgap = Integer.parseInt(priUpDateGap[0]);
    	if(dgap <0) {
    		if(flag){ 
    			create07Cell(wb, rowStock, colNum++,"��", 0); 
    			create07Cell(wb, rowStock, colNum++,"��", 0); 
    		}
    		ssu.setDayPriUpDateGap("��");
    		ssu.setDayUpOrdownDates("��");
    	}
    	else {
    		if(flag){
    			create07Cell(wb, rowStock, colNum++, priUpDateGap[0], 0);
    		}
    		ssu.setDayPriUpDateGap(priUpDateGap[0]);
    		if (dgap < 65) {
    			if(flag){
    				create07Cell(wb, rowStock, colNum, priUpDateGap[1], 0); 
    			}
    			ssu.setDayUpOrdownDates(priUpDateGap[1]);
    		}
    		colNum++;
    	}
  
    	//���ǵ�ͣ��  �� ǰ��ͣʱ�����
    	//ret = daySValue.getUpOrdownTimes();
    	//if(ret > 0)
    	//	create07Cell(wb, rowStock, colNum,desc+Integer.toString(ret), 0); 
    	//colNum++;

    	//�� 
		ret = daySValue.getBuySaleWarn();
    	switch (ret) {
    		case ConstantsInfo.DEAL_WARN_SEE: //����
    		case ConstantsInfo.DEAL_WARN_SALE://����
    			warnDeal = "����";
    			break;
    		case ConstantsInfo.DEAL_WARN_INTEREST: //��ע
    		case ConstantsInfo.DEAL_WARN_BUG://����
    			warnDeal = "����";
    			break;
    	}
    	
    	if(flag){
    		create07Cell(wb, rowStock, colNum++, warnDeal, 0);
    	}
    	ssu.setDayWarnDeal(warnDeal);
	    	
		if(daySValue.getTread() == 0){  
    		curTrend = "��";
    		nextTrend = "��";
    	} else {
    		curTrend = "��";
    		nextTrend = "��";
    	}
		
		//ʱ��� �ռ��ɺ����ɵ� 
		//String dayPS=curTrend+Integer.toString(daySValue.getPointSuspectedDateGap());
		//String daySC=nextTrend+Integer.toString(daySValue.getSuspectedCurDateGap());
		//create07Cell(wb, rowStock, colNum++, dayPS , 0); 	
    	//create07Cell(wb, rowStock, colNum++, daySC , 0); 
		if (minMaxItem != null){
			String minprice = Float.toString(minMaxItem.getMinPrice());
			if(flag){
		    	create07Cell(wb, rowStock, colNum++, minMaxItem.getMinDate() , 0); 	
		    	create07Cell(wb, rowStock, colNum++, minprice, 0);
			}
	    	ssu.setDayPS(minMaxItem.getMinDate());
	    	ssu.setDaySC(minprice);
		} else {
			colNum = colNum+2;
		}
	
		//��
    	StockStatValue weekSValue = esItem.getWeekStatItem();
    	StockMixStatValue weekMSvalue = esItem.getWeekMixStatItem();
    	
    	if(weekSValue==null || weekMSvalue == null) {
    		colNum = colNum+4;
    		if (minMaxItem != null){
    			String maxPrice = Float.toString(minMaxItem.getMaxPrice());
    			if(flag){
	    			create07Cell(wb, rowStock, colNum++, minMaxItem.getMaxDate(), 0); 				
	    			create07Cell(wb, rowStock, colNum++, maxPrice, 0); 
	    			colNum  = colNum +2;
	    			//create07Cell(wb, rowStock, colNum++, Float.toString(minMaxItem.getMinMaxRatio()), 0); 
    			}
    			ssu.setWeekPS(minMaxItem.getMaxDate());
    			ssu.setWeekSC(maxPrice);
    			  			
    		} 
    		return;
    	}
    	if(flag){
			//�ּܷ�����	
	    	create07Cell(wb, rowStock, colNum++,weekMSvalue.getBuySaleGrade(), 0); 
	    	//��״̬	
	    	create07Cell(wb, rowStock, colNum++,weekMSvalue.getBuySaleState(), 0); 
    	}
    	ssu.setWeekSaleGrade(weekMSvalue.getBuySaleGrade());
    	ssu.setWeekSaleState(weekMSvalue.getBuySaleState());
    	
    	//���ǵ�ͣ��
    	ret = weekSValue.getUpOrdownTimes();
    	if(ret > 0) {
    		String updowntimes = desc+Integer.toString(ret);
    		if(flag){
    			create07Cell(wb, rowStock, colNum,updowntimes, 0); 
    		}
    		ssu.setWeekUpOrdownDates(updowntimes);	
    	}
    	colNum++;
		
		ret = weekSValue.getBuySaleWarn();
    	switch (ret) {
    		case ConstantsInfo.DEAL_WARN_SEE: //����
    		case ConstantsInfo.DEAL_WARN_SALE://����
    			warnDeal = "����";
    			break;
    		case ConstantsInfo.DEAL_WARN_INTEREST: //��ע
    		case ConstantsInfo.DEAL_WARN_BUG://����
    			warnDeal = "����";
    			break;
    	}
    	if(flag){
    		create07Cell(wb, rowStock, colNum++, warnDeal, 0);
    	}
    	ssu.setWeekWarnDeal(warnDeal);
    	
		if(weekSValue.getTread() == 0){  
    		curTrend = "��";
    		nextTrend = "��";
    	} else {
    		curTrend = "��";
    		nextTrend = "��";
    	}
		
		//ʱ���
	//	String weekPS=curTrend+Integer.toString(weekSValue.getPointSuspectedDateGap());
	//	String weekSC=nextTrend+Integer.toString(weekSValue.getSuspectedCurDateGap());
    //	create07Cell(wb, rowStock, colNum++, weekPS, 0); 	
    //	create07Cell(wb, rowStock, colNum++, weekSC, 0); 
		if (minMaxItem != null){	 	
			String maxPrice = Float.toString(minMaxItem.getMaxPrice());
			if(flag){
				create07Cell(wb, rowStock, colNum++, minMaxItem.getMaxDate() , 0);
				create07Cell(wb, rowStock, colNum++, maxPrice , 0); 
			}
			ssu.setWeekPS(minMaxItem.getMaxDate());
			ssu.setWeekSC(maxPrice);
		} else {
	    	colNum=colNum+2;
		}
		
    	StockStatValue monthSValue = esItem.getMonthStatItem();
    	//���Ϻ�����С������
    	if(monthSValue == null){
    		colNum = colNum+2;
    		if (minMaxItem != null){
    			if(flag){
    				create07Cell(wb, rowStock, colNum++, Float.toString(minMaxItem.getMinMaxRatio()), 0);		
    			}
    		}
    		return;
    	}
		//�� �ǵ�ͣ��
    	ret = monthSValue.getUpOrdownTimes();
    	if(ret > 0){	
    		String updowntimes = desc+Integer.toString(ret);
    		if(flag){
    			create07Cell(wb, rowStock, colNum,updowntimes, 0); 
    		}
    		ssu.setMonthUpOrdownDates(updowntimes);	
    	}
    	colNum++;
    	
		
		ret = monthSValue.getBuySaleWarn();
    	switch (ret) {
    		case ConstantsInfo.DEAL_WARN_SEE: //����
    		case ConstantsInfo.DEAL_WARN_SALE://����
    			warnDeal = "����";
    			break;
    		case ConstantsInfo.DEAL_WARN_INTEREST: //��ע
    		case ConstantsInfo.DEAL_WARN_BUG://����
    			warnDeal = "����";
    			break;
    	}    
    	if(flag){
    		create07Cell(wb, rowStock, colNum++, warnDeal, 0);
    	}
    	ssu.setMonthWarnDeal(warnDeal);
    	
		if(monthSValue.getTread() == 0){  
    		curTrend = "��";
    		nextTrend = "��";
    	} else {
    		curTrend = "��";
    		nextTrend = "��";
    	}
		//ʱ���
		//String monthPS=curTrend+Integer.toString(monthSValue.getPointSuspectedDateGap());
	//	String monthSC=nextTrend+Integer.toString(monthSValue.getSuspectedCurDateGap());

    //	create07Cell(wb, rowStock, colNum++,monthPS, 0); 	
    //	create07Cell(wb, rowStock, colNum++, monthSC, 0); 
		if (minMaxItem != null){
			if(flag){
				create07Cell(wb, rowStock, colNum++, Float.toString(minMaxItem.getMinMaxRatio()), 0); 
				create07Cell(wb, rowStock, colNum++, "", 0);
			}
	    } else {
	    	colNum = colNum+2;
		}
    	
    }
    
    //�����Ʊ����value
    public static void writeExcelStockOtherInfo(Workbook wb,Sheet sheet, StockOtherInfoValue soiValue,int stockRow,int type, StockSummary ssu,boolean flag)
    {
    	Row rowStock = null;
    	int col =0;
    	if(flag) {
    		rowStock = sheet.createRow(stockRow);  
    		create07Cell(wb, rowStock, col++, Integer.toString(stockRow), 0);  
            create07Cell(wb, rowStock, col++, soiValue.getFullId(), 0);  
            create07Cell(wb, rowStock, col++, soiValue.getName(), 0); 
    	}
    	
    	if (ssu!= null) {
	        ssu.setFullId(soiValue.getFullId());
	        ssu.setName(soiValue.getName());
    	}
        
        
        if (type == 1) {
	        if(soiValue.getEnableTingPai() == 1){
	        	String dourong = Integer.toString(soiValue.getMarginTrading())+":ͣ��";
	        	if(flag) {
	        		create07Cell(wb, rowStock, col++, dourong, 0); 
	        	}
	        	if (ssu != null) {
	        		ssu.setDouRong(dourong);
	        	}
	        } else {
	        	String dourong = Integer.toString(soiValue.getMarginTrading());
	        	if(flag) {
	        		create07Cell(wb, rowStock, col++,dourong, 0); 
	        	}
	        	if (ssu != null) {
	        		ssu.setDouRong(dourong);
	        	}
	        }
	        
	        if (soiValue.getBaseFace()!= null) {
	        	if(flag) {
		        	create07Cell(wb, rowStock, col++, soiValue.getBaseFace().getBaseExpect(), 0); 
			        create07Cell(wb, rowStock, col++, soiValue.getBaseFace().getMain(), 0); 
			    	create07Cell(wb, rowStock, col++, soiValue.getBaseFace().getPsychology(), 0); 
			    	create07Cell(wb, rowStock, col++, soiValue.getBaseFace().getRisk(), 0); 
		    		create07Cell(wb, rowStock, col++, soiValue.getBaseFace().getPotential(), 0); 	    	
			    	create07Cell(wb, rowStock, col++, soiValue.getBaseFace().getFaucet(), 0); 
	        	}
	        	if (ssu != null) {
	        		ssu.setBaseExpect(soiValue.getBaseFace().getBaseExpect());
	        		ssu.setMain(soiValue.getBaseFace().getMain());
	        		ssu.setPsychology(soiValue.getBaseFace().getPsychology());
	        		ssu.setRisk(soiValue.getBaseFace().getRisk());
			    	ssu.setPotential(soiValue.getBaseFace().getPotential());
			    	ssu.setFaucet(soiValue.getBaseFace().getFaucet());
	        	}		    		    	
		    	/*
		    	if(soiValue.getBaseYearInfo()!= null) {
		    		String combain = soiValue.getBaseYearInfo().getGongJiJin()+":"+soiValue.getBaseYearInfo().getLiRun()+":"+
			    	soiValue.getBaseYearInfo().getPiLouDate();
			    	create07Cell(wb, rowStock, col++, combain, 0);		    	
			    	create07Cell(wb, rowStock, col++, soiValue.getBaseYearInfo().getYuGaoContent(), 0);
		    	} else {
		    	}
		    	*/
	        }       
	             
        }
    	
    }
    
    //����̶Ա���ʾ��Ϣ
    public static String getMarketCompareInfo(int gap)
    {
    	String desc = "0";
    	if(gap < 0)
    		desc="��ǰ"+(-gap);
    	else if(gap>0)
    		desc="�Ӻ�"+gap;
    	else 
    		desc="0";
    	return desc;
    }
    
    //�����Ʊ����
    public static void writeExcelItem(Workbook wb,Sheet sheet, StockExcelItem eItem,int stockRow,int dataType, StockSummary ssu, boolean flag)
    {
    	if(eItem==null)
    		return; 
    	int colNum=0;
    	//ÿ����Ʊ
    	Row rowStock = null;
    	
    	String valuePer1="";//�ٷֱ�
    	String valuePer2="";//�ٷֱ�
    	String valuePer3="";//�ٷֱ�
    	String valuePer4="";//�ٷֱ�
    	String valuePer5="";//�ٷֱ�
    	String valuePer6="";//�ٷֱ�
    	
    	String desc1="";
    	String desc2="";
    	String desc3="";
    	String desc4="";
    	
    	String dateDesc="";
    	String curTrend="";
    	String nextTrend="";
    	String warnDeal="";
    	  
    	if(flag){
    		rowStock = sheet.getRow(stockRow);
    	}
    	
    	switch (dataType)
    	{
    	case ConstantsInfo.DayDataType:  
    		colNum=stockLabelNum + stockStatNum ;
    		dateDesc="��";
	        break;
    	case ConstantsInfo.WeekDataType:
    		colNum = stockLabelNum+stockStatNum+FirstRowFirstColNum;  //��	
    		dateDesc="��";
    		break;
    	case ConstantsInfo.MonthDataType:	
    		colNum = stockLabelNum+stockStatNum+2*FirstRowFirstColNum;  //��		
    		dateDesc="��";
    		break;
    	}
    	
    	//�������ǰ���������Ƶ�Ԥ��ֵ
    	if(eItem.getScValue().getTread() == 0){  
    		curTrend = "��";
    		nextTrend = "��";
    	} else {
    		curTrend = "��";
    		nextTrend = "��";
    	}
    	/*
    	//״̬
    	int state=eItem.getScValue().getCurState();
    	if(state == -1 ){
    		create07Cell(wb, rowStock, colNum++, "����", 0);
    	} else if(state == 0) {
    		create07Cell(wb, rowStock, colNum++, "�µ�", 0);
    	} else
    		create07Cell(wb, rowStock, colNum++, "����", 0);
    	
    	//����
    	if(eItem.getSsValue().getFlag() == 1){    	
    		create07Cell(wb, rowStock, colNum++, "����", 0);
    	} else {
    		create07Cell(wb, rowStock, colNum++, "�µ�", 0);
    	}
    	*/	
    	
    	//��ǰ 
    	String highorlowest = Float.toString(eItem.getScValue().getPriHighOrLowest());
    	String reverregion =  Float.toString(eItem.getScValue().getReversalRegion());
    	String startValue = Float.toString(eItem.getScValue().getStartValue());
    	String endValue = Float.toString(eItem.getScValue().getEndValue());
    	String curValue = Float.toString(eItem.getScValue().getCurValue());
    	String workRegion = Float.toString(eItem.getScValue().getWorkRegion());
    	if(flag){
    		create07Cell(wb, rowStock, colNum++, eItem.getScValue().getPriDate(), 0);
    		create07Cell(wb, rowStock, colNum++, highorlowest, 0);
        	create07Cell(wb, rowStock, colNum++, reverregion, 0);
        	create07Cell(wb, rowStock, colNum++, eItem.getScValue().getStartDate(), 0);	
        	create07Cell(wb, rowStock, colNum++, startValue, 0);
        	create07Cell(wb, rowStock, colNum++, eItem.getScValue().getEndDate(), 0);   	
        	create07Cell(wb, rowStock, colNum++, endValue, 0);    	
        	create07Cell(wb, rowStock, colNum++, eItem.getScValue().getCurDate(), 0); 	
        	create07Cell(wb, rowStock, colNum++,curValue, 0);
        	create07Cell(wb, rowStock, colNum++, workRegion, 0);
    	}
    	
    	switch (dataType)
    	{
    	case ConstantsInfo.DayDataType:  
    		ssu.setDayPriDate(eItem.getScValue().getPriDate());
    		ssu.setDayPriHighOrLowest(highorlowest);
    		ssu.setDayReversalRegion(reverregion);
    		ssu.setDayStartDate(eItem.getScValue().getStartDate());
    		ssu.setDayStartValue(startValue);
    		ssu.setDayEndDate(eItem.getScValue().getEndDate());
    		ssu.setDayEndValue(endValue);
    		ssu.setDayCurDate(eItem.getScValue().getCurDate());
    		ssu.setDayCurValue(curValue);
    		ssu.setDayWorkRegion(workRegion);
	        break;
    	case ConstantsInfo.WeekDataType:
    		ssu.setWeekPriDate(eItem.getScValue().getPriDate());
    		ssu.setWeekPriHighOrLowest(highorlowest);
    		ssu.setWeekReversalRegion(reverregion);
    		ssu.setWeekStartDate(eItem.getScValue().getStartDate());
    		ssu.setWeekStartValue(startValue);
    		ssu.setWeekEndDate(eItem.getScValue().getEndDate());
    		ssu.setWeekEndValue(endValue);
    		ssu.setWeekCurDate(eItem.getScValue().getCurDate());
    		ssu.setWeekCurValue(curValue);
    		ssu.setWeekWorkRegion(workRegion);
    		break;
    	case ConstantsInfo.MonthDataType:	
    		ssu.setMonthPriDate(eItem.getScValue().getPriDate());
    		ssu.setMonthPriHighOrLowest(highorlowest);
    		ssu.setMonthReversalRegion(reverregion);
    		ssu.setMonthStartDate(eItem.getScValue().getStartDate());
    		ssu.setMonthStartValue(startValue);
    		ssu.setMonthEndDate(eItem.getScValue().getEndDate());
    		ssu.setMonthEndValue(endValue);
    		ssu.setMonthCurDate(eItem.getScValue().getCurDate());
    		ssu.setMonthCurValue(curValue);
    		ssu.setMonthWorkRegion(workRegion);
    		break;
    	}
    
    	//ʱ���
    	String psDateGap = curTrend+Integer.toString(eItem.getScValue().getPointSuspectedDateGap());   		
    	valuePer1=changePercent(eItem.getScValue().getPointSuspectedValueGap());    		
    	String pcDateGap = Integer.toString(eItem.getScValue().getPointCurDateGap());   		
    	valuePer2=changePercent(eItem.getScValue().getPointCurValueGap());   	
    	String scDateGap = nextTrend+Integer.toString(eItem.getScValue().getSuspectedCurDateGap());    	
    	valuePer3=changePercent(eItem.getScValue().getSuspectedCurValueGap());   	
    	
    	//���̶Ա�
    	desc1 = getMarketCompareInfo(eItem.getScValue().getMarketPSDateGap());    		    	
    	valuePer4=changePercent(eItem.getScValue().getMarketPSSpace());   	
    	desc2 = getMarketCompareInfo(eItem.getScValue().getMarketPCDateGap());   		    	
    	valuePer5=changePercent(eItem.getScValue().getMarketPCSpace());    		
    	desc3 = getMarketCompareInfo(eItem.getScValue().getMarketSCDateGap());   		    	
    	valuePer6=changePercent(eItem.getScValue().getMarketSCSpace());	 	    	
    	desc4 = eItem.getScValue().getTrendConsistent()>0?"��һ��":"һ��";
    		 	
    	if(flag){
    		create07Cell(wb, rowStock, colNum++, psDateGap, 0); 
    		create07Cell(wb, rowStock, colNum++, valuePer1, 0);
    		create07Cell(wb, rowStock, colNum++, pcDateGap, 0); 
    		create07Cell(wb, rowStock, colNum++, valuePer2, 0); 
    		create07Cell(wb, rowStock, colNum++, scDateGap, 0);
    		create07Cell(wb, rowStock, colNum++, valuePer3, 0); 
    		create07Cell(wb, rowStock, colNum++, desc1, 0); 
    		create07Cell(wb, rowStock, colNum++, valuePer4, 0);
    		create07Cell(wb, rowStock, colNum++, desc2, 0); 
    		create07Cell(wb, rowStock, colNum++, valuePer5, 0); 
    		create07Cell(wb, rowStock, colNum++, desc3, 0); 
    		create07Cell(wb, rowStock, colNum++, valuePer6, 0);
    		create07Cell(wb, rowStock, colNum++, desc4, 0); 
    	}
    	
    	switch (dataType)
    	{
    	case ConstantsInfo.DayDataType:  
    		ssu.setDayPSDateGap(psDateGap);
    		ssu.setDayPSValueGap(valuePer1);
    		ssu.setDayPCDateGap(pcDateGap);
    		ssu.setDayPCValueGap(valuePer2);
    		ssu.setDaySCDateGap(scDateGap);
    		ssu.setDaySCValueGap(valuePer3);
    		ssu.setDayMarketPSDateGap(desc1);
    		ssu.setDayMarketPSSpace(valuePer4);
    		ssu.setDayMarketPCDateGap(desc2);
    		ssu.setDayMarketPCSpace(valuePer5);
    		ssu.setDayMarketSCDateGap(desc3);
    		ssu.setDayMarketSCSpace(valuePer6);
    		ssu.setDayTrendConsistent(desc4);
	        break;
    	case ConstantsInfo.WeekDataType:
    		ssu.setWeekPSDateGap(psDateGap);
    		ssu.setWeekPSValueGap(valuePer1);
    		ssu.setWeekPCDateGap(pcDateGap);
    		ssu.setWeekPCValueGap(valuePer2);
    		ssu.setWeekSCDateGap(scDateGap);
    		ssu.setWeekSCValueGap(valuePer3);
    		ssu.setWeekMarketPSDateGap(desc1);
    		ssu.setWeekMarketPSSpace(valuePer4);
    		ssu.setWeekMarketPCDateGap(desc2);
    		ssu.setWeekMarketPCSpace(valuePer5);
    		ssu.setWeekMarketSCDateGap(desc3);
    		ssu.setWeekMarketSCSpace(valuePer6);
    		ssu.setWeekTrendConsistent(desc4);
    		break;
    	case ConstantsInfo.MonthDataType:	
    		ssu.setMonthPSDateGap(psDateGap);
    		ssu.setMonthPSValueGap(valuePer1);
    		ssu.setMonthPCDateGap(pcDateGap);
    		ssu.setMonthPCValueGap(valuePer2);
    		ssu.setMonthSCDateGap(scDateGap);
    		ssu.setMonthSCValueGap(valuePer3);
    		ssu.setMonthMarketPSDateGap(desc1);
    		ssu.setMonthMarketPSSpace(valuePer4);
    		ssu.setMonthMarketPCDateGap(desc2);
    		ssu.setMonthMarketPCSpace(valuePer5);
    		ssu.setMonthMarketSCDateGap(desc3);
    		ssu.setMonthMarketSCSpace(valuePer6);
    		ssu.setMonthTrendConsistent(desc4);
    		break;
    	}
    	
    	//Ԥ��   	
    	String desireValue1 = nextTrend+Float.toString(eItem.getSdValue().getDesireValue1());   	
    	valuePer1=changePercent(eItem.getSdValue().getDesireRange1());   	
    	String desireRate1 = Float.toString(eItem.getSdValue().getDesireRate1());   	
    	String desireValue2 = nextTrend+Float.toString(eItem.getSdValue().getDesireValue2());  	
    	valuePer2=changePercent(eItem.getSdValue().getDesireRange2());   	
    	String desireRate2 = Float.toString(eItem.getSdValue().getDesireRate2());	
    	String desireValue3 = nextTrend+Float.toString(eItem.getSdValue().getDesireValue3());   	
    	valuePer3=changePercent(eItem.getSdValue().getDesireRange3()); 	
    	String desireRate3 = Float.toString(eItem.getSdValue().getDesireRate3());  	
    	String desireValue4 = nextTrend+Float.toString(eItem.getSdValue().getDesireValue4());    	
    	valuePer4=changePercent(eItem.getSdValue().getDesireRange4());   	
    	String desireRate4 = Float.toString(eItem.getSdValue().getDesireRate4());  	
    	String desireValue5 = nextTrend+Float.toString(eItem.getSdValue().getDesireValue5());    	
    	valuePer5=changePercent(eItem.getSdValue().getDesireRange5());   	
    	String desireRate5 = Float.toString(eItem.getSdValue().getDesireRate5());   	
    	String desireValue6 = nextTrend+Float.toString(eItem.getSdValue().getDesireValue6());    	
    	valuePer6=changePercent(eItem.getSdValue().getDesireRange6());    	
    	String desireRate6 = Float.toString(eItem.getSdValue().getDesireRate6());
    
    	if(flag){
    		create07Cell(wb, rowStock, colNum++, desireValue1, 0);
    		create07Cell(wb, rowStock, colNum++, valuePer1, 0); 
    		create07Cell(wb, rowStock, colNum++, desireRate1, 0);
    		create07Cell(wb, rowStock, colNum++, desireValue2, 0);
    		create07Cell(wb, rowStock, colNum++, valuePer2, 0); 
    		create07Cell(wb, rowStock, colNum++, desireRate2, 0);
    		create07Cell(wb, rowStock, colNum++,desireValue3, 0);
    		create07Cell(wb, rowStock, colNum++, valuePer3, 0); 
    		create07Cell(wb, rowStock, colNum++, desireRate3, 0);
    		create07Cell(wb, rowStock, colNum++, desireValue4, 0);
    		create07Cell(wb, rowStock, colNum++, valuePer4, 0); 
    		create07Cell(wb, rowStock, colNum++, desireRate4, 0);
    		create07Cell(wb, rowStock, colNum++, desireValue5, 0);
    		create07Cell(wb, rowStock, colNum++, valuePer5, 0); 
    		create07Cell(wb, rowStock, colNum++, desireRate5, 0);
    		create07Cell(wb, rowStock, colNum++,desireValue6, 0);
    		create07Cell(wb, rowStock, colNum++, valuePer6, 0); 
    		create07Cell(wb, rowStock, colNum++, desireRate6, 0);
    	}
    	switch (dataType)
    	{
    	case ConstantsInfo.DayDataType:  
    		ssu.setDayDesireValue1(desireValue1);
    		ssu.setDayDesireRange1(valuePer1);
    		ssu.setDayDesireRate1(desireRate1);
    		ssu.setDayDesireValue2(desireValue2);
    		ssu.setDayDesireRange2(valuePer2);
    		ssu.setDayDesireRate2(desireRate2);
    		ssu.setDayDesireValue3(desireValue3);
    		ssu.setDayDesireRange3(valuePer3);
    		ssu.setDayDesireRate3(desireRate3);
    		ssu.setDayDesireValue4(desireValue4);
    		ssu.setDayDesireRange4(valuePer4);
    		ssu.setDayDesireRate4(desireRate4);
    		ssu.setDayDesireValue5(desireValue5);
    		ssu.setDayDesireRange5(valuePer5);
    		ssu.setDayDesireRate5(desireRate5);
    		ssu.setDayDesireValue6(desireValue6);
    		ssu.setDayDesireRange6(valuePer6);
    		ssu.setDayDesireRate6(desireRate6);
    			
	        break;
    	case ConstantsInfo.WeekDataType:
    		ssu.setWeekDesireValue1(desireValue1);
    		ssu.setWeekDesireRange1(valuePer1);
    		ssu.setWeekDesireRate1(desireRate1);
    		ssu.setWeekDesireValue2(desireValue2);
    		ssu.setWeekDesireRange2(valuePer2);
    		ssu.setWeekDesireRate2(desireRate2);
    		ssu.setWeekDesireValue3(desireValue3);
    		ssu.setWeekDesireRange3(valuePer3);
    		ssu.setWeekDesireRate3(desireRate3);
    		ssu.setWeekDesireValue4(desireValue4);
    		ssu.setWeekDesireRange4(valuePer4);
    		ssu.setWeekDesireRate4(desireRate4);
    		ssu.setWeekDesireValue5(desireValue5);
    		ssu.setWeekDesireRange5(valuePer5);
    		ssu.setWeekDesireRate5(desireRate5);
    		ssu.setWeekDesireValue6(desireValue6);
    		ssu.setWeekDesireRange6(valuePer6);
    		ssu.setWeekDesireRate6(desireRate6);
    		break;
    	case ConstantsInfo.MonthDataType:	
    		ssu.setMonthDesireValue1(desireValue1);
    		ssu.setMonthDesireRange1(valuePer1);
    		ssu.setMonthDesireRate1(desireRate1);
    		ssu.setMonthDesireValue2(desireValue2);
    		ssu.setMonthDesireRange2(valuePer2);
    		ssu.setMonthDesireRate2(desireRate2);
    		ssu.setMonthDesireValue3(desireValue3);
    		ssu.setMonthDesireRange3(valuePer3);
    		ssu.setMonthDesireRate3(desireRate3);
    		ssu.setMonthDesireValue4(desireValue4);
    		ssu.setMonthDesireRange4(valuePer4);
    		ssu.setMonthDesireRate4(desireRate4);
    		ssu.setMonthDesireValue5(desireValue5);
    		ssu.setMonthDesireRange5(valuePer5);
    		ssu.setMonthDesireRate5(desireRate5);
    		ssu.setMonthDesireValue6(desireValue6);
    		ssu.setMonthDesireRange6(valuePer6);
    		ssu.setMonthDesireRate6(desireRate6);
    		break;
    	}
    	
    	//Ԥ�ڲ��
    	valuePer1=changePercent(eItem.getSdValue().getDesireValue1Gap());    		
    	valuePer2=changePercent(eItem.getSdValue().getDesireValue2Gap());    		
    	valuePer3=changePercent(eItem.getSdValue().getDesireValue3Gap());    	 	
    	valuePer4=changePercent(eItem.getSdValue().getDesireValue4Gap());  	 	
    	valuePer5=changePercent(eItem.getSdValue().getDesireValue5Gap());   	
    	valuePer6=changePercent(eItem.getSdValue().getDesireValue6Gap()); 		
    	
    	if(flag){
    		create07Cell(wb, rowStock, colNum++, valuePer1, 0); 
    		create07Cell(wb, rowStock, colNum++, valuePer2, 0); 
    		create07Cell(wb, rowStock, colNum++, valuePer3, 0);
    		create07Cell(wb, rowStock, colNum++, valuePer4, 0);
    		create07Cell(wb, rowStock, colNum++, valuePer5, 0); 
    		create07Cell(wb, rowStock, colNum++, valuePer6, 0); 
    	}
    	
    	switch (dataType)
    	{
    	case ConstantsInfo.DayDataType:  
    		ssu.setDayDesireValue1Gap(valuePer1);
    		ssu.setDayDesireValue2Gap(valuePer2);
    		ssu.setDayDesireValue3Gap(valuePer3);
    		ssu.setDayDesireValue4Gap(valuePer4);
    		ssu.setDayDesireValue5Gap(valuePer5);
    		ssu.setDayDesireValue6Gap(valuePer6);
	        break;
    	case ConstantsInfo.WeekDataType:
    		ssu.setWeekDesireValue1Gap(valuePer1);
    		ssu.setWeekDesireValue2Gap(valuePer2);
    		ssu.setWeekDesireValue3Gap(valuePer3);
    		ssu.setWeekDesireValue4Gap(valuePer4);
    		ssu.setWeekDesireValue5Gap(valuePer5);
    		ssu.setWeekDesireValue6Gap(valuePer6);
    		break;
    	case ConstantsInfo.MonthDataType:	
    		ssu.setMonthDesireValue1Gap(valuePer1);
    		ssu.setMonthDesireValue2Gap(valuePer2);
    		ssu.setMonthDesireValue3Gap(valuePer3);
    		ssu.setMonthDesireValue4Gap(valuePer4);
    		ssu.setMonthDesireValue5Gap(valuePer5);
    		ssu.setMonthDesireValue6Gap(valuePer6);
    		break;
    	}
       	     	
    	//����
    	if (eItem.getFlag() == 0) {//�µ� 
    		valuePer1 = Float.toString(eItem.getScValue().getBugValue());  		 
    		valuePer2=changePercent(eItem.getScValue().getWinValue());	    	
	    	valuePer3=changePercent(eItem.getScValue().getLoseVaule());    	
	    	if(flag){
	    		create07Cell(wb, rowStock, colNum++, valuePer1, 0);
	    		create07Cell(wb, rowStock, colNum++, valuePer2, 0); 
	    		create07Cell(wb, rowStock, colNum++, valuePer3, 0); 
	    	}
    	} else {
    		valuePer1 = "";
    		valuePer2 = "";
    		valuePer3 = "";
    		colNum = colNum+3;
    	}
    	/*
    	int runSeeDateGap = eItem.getScValue().getPointSuspectedDateGap();
    	int seeDateGap = ConstantsInfo.DEAL_DATE - runSeeDateGap;
    	if(seeDateGap<0)
    		seeDateGap = runSeeDateGap; 
    	*/
    	int ret = eItem.getScValue().getDealWarn();
    	switch (ret) {
    		case ConstantsInfo.DEAL_WARN_SEE: //����
    			//warnDeal = "����";
    			//warnDeal = "����:"+seeDateGap+dateDesc;
    			//warnRun = "�ѽ���:"+runBugDateGap+dateDesc;
    			//break;
    		case ConstantsInfo.DEAL_WARN_SALE://����
    			warnDeal = "����";
    			//warnDeal = "����:����"+runSeeDateGap+dateDesc;
    			//warnRun = "�ѹ�:"+overDateGap+dateDesc;
    			break;
    		case ConstantsInfo.DEAL_WARN_INTEREST: //��ע
    			//warnDeal = "��ע";
    			//warnDeal = "��ע:"+seeDateGap+dateDesc;
    			//warnRun = "�ѽ���:"+runBugDateGap+dateDesc;
    			//break;    		
    		case ConstantsInfo.DEAL_WARN_BUG://����
    			warnDeal = "����";
    			//warnDeal = "����:����"+runSeeDateGap+dateDesc;
    			//warnRun = "�ѹ�:"+overDateGap+dateDesc;
    			break;
    	}    
    	if(flag){
    		create07Cell(wb, rowStock, colNum++, warnDeal, 0);
    	}
    	colNum++;
    	//create07Cell(wb, rowStock, colNum++, warnRun, 0);  	
    	switch (dataType)
    	{
    	case ConstantsInfo.DayDataType:  
    		ssu.setDayBugValue(valuePer1);
    		ssu.setDayWinValue(valuePer2);
    		ssu.setDayLoseValue(valuePer3);
    		ssu.setDayWarnDeal(warnDeal);		
	        break;
    	case ConstantsInfo.WeekDataType:
    		ssu.setWeekBugValue(valuePer1);
    		ssu.setWeekWinValue(valuePer2);
    		ssu.setWeekLoseValue(valuePer3);
    		ssu.setWeekWarnDeal(warnDeal);	
    		break;
    	case ConstantsInfo.MonthDataType:	
    		ssu.setMonthBugValue(valuePer1);
    		ssu.setMonthWinValue(valuePer2);
    		ssu.setMonthLoseValue(valuePer3);
    		ssu.setMonthWarnDeal(warnDeal);	
    		break;
    	}
    }        
   
    
    //�����Ʊ����
    public static void writeSummaryExcelItem(Workbook wb,Sheet sheet, StockSummary ssum,int col,int stockRow, int type)
    {
    	if(ssum==null)
    		return; 
    	int colNum=0;
    	//ÿ����Ʊ
    	Row rowStock;
    		
    	rowStock = sheet.getRow(stockRow);
    	
    	colNum = col*stockSummaryNum + stockNameIdNum;    	
    	
    	//��ǰ
    	//if(ssum.getCurRange())
    	float rang = Float.parseFloat(ssum.getCurRange());
    	if(type == 0 && (rang > 5 || rang < -5)) //��Ʊ
    		create07Cell(wb, rowStock, colNum++, ssum.getCurRange(), 0);
    	else if(type == 1 && (rang > 2 || rang < -2)) //����
    		create07Cell(wb, rowStock, colNum++, ssum.getCurRange(), 0);
    	else
    		create07Cell(wb, rowStock, colNum++, "", 0);
    	
    	create07Cell(wb, rowStock, colNum++, ssum.getComState(), 0);
    	create07Cell(wb, rowStock, colNum++, ssum.getPsState(), 0);
    	
    	create07Cell(wb, rowStock, colNum++, ssum.getDayStartDate(), 0);
    	create07Cell(wb, rowStock, colNum++, ssum.getDayEndDate(), 0);    	
    	create07Cell(wb, rowStock, colNum++, ssum.getDayPS(), 0);
    	create07Cell(wb, rowStock, colNum++, ssum.getDaySC(), 0);
    	
    	create07Cell(wb, rowStock, colNum++, ssum.getWeekStartDate(), 0);
    	create07Cell(wb, rowStock, colNum++, ssum.getWeekEndDate(), 0);
    	create07Cell(wb, rowStock, colNum++, ssum.getWeekPS(), 0);    	
    	create07Cell(wb, rowStock, colNum++, ssum.getWeekSC(), 0);
    	
    	
    	create07Cell(wb, rowStock, colNum++, ssum.getMonthStartDate(), 0);
    	create07Cell(wb, rowStock, colNum++, ssum.getMonthEndDate(), 0);
    	create07Cell(wb, rowStock, colNum++, ssum.getMonthPS(), 0);
    	create07Cell(wb, rowStock, colNum++, ssum.getMonthSC(), 0);
    	
    }
    
    
    //�����Ʊ����
    public static void writePointExcelItem(Workbook wb,Sheet sheet, StockPoint sp,String value, int col,int stockRow,int type)
    {
    	
    	int colNum=0;
    	//ÿ����Ʊ
    	Row rowStock;
    		
    	rowStock = sheet.getRow(stockRow);
    	if(type == 0){
    		if(sp==null)
        		return; 
	    	colNum = stockNameIdNum + col;    	    	
	    	//��ǰ
	    	create07Cell(wb, rowStock, colNum, Float.toString(sp.getExtremePrice()), 0);
    	} else {
    		colNum = stockNameIdNum + col;
    		create07Cell(wb, rowStock, colNum, value, 0);
    	}
  	  	
    }
    
    
    
    //�����Ʊ����
    public static void writeTotalOperationExcelItem(Workbook wb,Sheet sheet, StockOperation sop,int col, int stockRow)
    {
    	if(sop==null)
    		return; 
    	int colNum=0;
    	//ÿ����Ʊ
    	Row rowStock;
    		
    	rowStock = sheet.getRow(stockRow);
    	
    	colNum = stockNameIdNum + col*stockNameIdNum;	
    	String valuePer="";
    	int dateType =  sop.getDateType();
    	//���
    	switch(dateType)
		{
		case ConstantsInfo.DayDataType:
		default:
			create07Cell(wb, rowStock, colNum++, Integer.toString(sop.getAssId()), 0);
			break;
		case ConstantsInfo.WeekDataType:
			colNum = colNum+1;
			create07Cell(wb, rowStock, colNum++, Integer.toString(sop.getAssId()), 0);
			break;
		case ConstantsInfo.MonthDataType:
			colNum = colNum+2;
			create07Cell(wb, rowStock, colNum++, Integer.toString(sop.getAssId()), 0);
			break;
		}	
    	
    }
    
    //�����Ʊ����
    public static void writeOperationExcelItem(Workbook wb,Sheet sheet, StockOperation sop,String psState,int col,int stockRow)
    {
    	if(sop==null)
    		return; 
    	int colNum=0;
    	//ÿ����Ʊ
    	Row rowStock;
    		
    	rowStock = sheet.getRow(stockRow);
    	
    	colNum = stockNameIdNum + stockOperationNum +(col-1)*stockOperationNum+1;    	
    	String valuePer="";
    	//���
    	//create07Cell(wb, rowStock, colNum++, psState, 0);
    	//getAssId
    	if (sop.getAssId()!=0)
    		create07Cell(wb, rowStock, colNum++, Integer.toString(sop.getAssId()), 0);
    	else 
    		create07Cell(wb, rowStock, colNum++, "", 0);
    	
    	//��ǰ
    	create07Cell(wb, rowStock, colNum++, Float.toString(sop.getBuyValue()), 0);
    	create07Cell(wb, rowStock, colNum++, Float.toString(sop.getStopValue()), 0);
    	create07Cell(wb, rowStock, colNum++, Float.toString(sop.getSaleValue()), 0);
    	
    	if(sop.getEarnRatio()>0.000001 || sop.getEarnRatio()<-0.000001)
    		valuePer=changePercent(sop.getEarnRatio());
    	else 
    		valuePer= "0";
    	create07Cell(wb, rowStock, colNum++, valuePer, 0); 
    	
    	if(sop.getStopRatio()>0.000001 || sop.getStopRatio()<-0.000001)
    		valuePer=changePercent(sop.getStopRatio());
    	else 
    		valuePer= "0";
    	
    	create07Cell(wb, rowStock, colNum++, valuePer, 0); 
    	
     	if(sop.getLossRatio()>0.000001 || sop.getLossRatio()<-0.000001)
    		valuePer=changePercent(sop.getLossRatio());
    	else 
    		valuePer= "0";
    	create07Cell(wb, rowStock, colNum++, valuePer, 0);     	
    }
    
  //���ͳ��
    public static void writeOperationTotalExcelItem(Workbook wb,Sheet sheet,int col,int stockRow,int earn,int stop,int loss,int total, float totalShouyi)
    {
    
    	int colNum=0;
    	//ÿ����Ʊ
    	Row rowStock;
    		
    	rowStock = sheet.getRow(stockRow);
    	
    	colNum = stockNameIdNum;    	
    	String valuePer="";
    	//��ǰ
    	create07Cell(wb, rowStock, colNum++, Integer.toString(earn), 0);
    	create07Cell(wb, rowStock, colNum++, Integer.toString(stop), 0);
    	create07Cell(wb, rowStock, colNum++, Integer.toString(loss), 0);
    	create07Cell(wb, rowStock, colNum++, Integer.toString(total), 0);
    	
    	
    	if(earn>0)
    		valuePer=changePercent((float)earn/total);
    	else 
    		valuePer="0";
    	create07Cell(wb, rowStock, colNum++, valuePer, 0); 
    	if(stop>0)
    		valuePer=changePercent((float)stop/total);
    	else 
    		valuePer="0";
    	create07Cell(wb, rowStock, colNum++, valuePer, 0); 	    	
    	
    	if(loss>0)
    		valuePer=changePercent((float)loss/total);
    	else 
    		valuePer="0";
    	create07Cell(wb, rowStock, colNum++, valuePer, 0); 	
    	
    	
    	valuePer=changePercent(totalShouyi);
    	create07Cell(wb, rowStock, colNum++, valuePer, 0); 	
    	
    }
    
}
