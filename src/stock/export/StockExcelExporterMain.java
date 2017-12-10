package stock.export;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.Collator;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.PropertyConfigurator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import common.ConstantsInfo;
import common.stockLogger;

import dao.DbConn;
import dao.StockBaseDao;
import dao.StockBaseFace;
import dao.StockBaseYearInfo;
import dao.StockConcept;
import dao.StockConceptInFirstIndustry;
import dao.StockData;
import dao.StockDataDao;
import dao.StockIndustry;
import dao.StockMarket;
import dao.StockOperation;
import dao.StockPoint;
import dao.StockPointDao;
import dao.StockSingle;
import dao.StockSummary;
import dao.StockSummaryDao;
import dao.StockToConcept;
import dao.StockToFutures;
import dao.StockToIndustry;
import stock.timer.stockDater;
import stock.analysis.PointAnalysis;
import stock.timer.CommonDate;

//分片处理excel 分成多个excel

public class StockExcelExporterMain {

	private StockDataDao sdDao;
	private StockPointDao spDao;
	private StockBaseDao sbDao;
	private StockSummaryDao ssDao;

	static PointAnalysis pClass=new PointAnalysis();

	
 	//static ExcelCommon eCommon=new ExcelCommon();
 	static int stockRow = 1;
 	static int stockTotalRow = 1;
 	static int stockDesiredDate = 10;	
 	static int stockMaxRow = 10;	
 	static int sheetCount=0;
 	
 	String SHDate=null; //记录上证大盘当前时间，方便与其他股票时间作对比，是不有停牌现象
 	
 	//记录market 方便后面股票对比计算
 	StockCurValue MarketStockCurInfo[][]=  new StockCurValue[4][3];
 	//全局保存定位统计数据时
 	HashMap<String, Integer> stockDateColumnmap = new HashMap<String,Integer>();
 	//比例保留两位小数点
 	static DecimalFormat decimalFormat=new DecimalFormat(".00");
 	
 	public StockExcelExporterMain(Connection stockBaseConn,Connection stockDataConn,Connection stockPointConn,Connection stockSummaryConn)
	{
		   this.sbDao = new StockBaseDao(stockBaseConn);
		   this.sdDao =new StockDataDao(stockDataConn);
		   this.spDao =new StockPointDao(stockPointConn);
		   this.ssDao = new StockSummaryDao(stockSummaryConn);
	}
    
    public StockExcelExporterMain(StockBaseDao sbDao,StockDataDao sdDao,StockPointDao spDao,StockSummaryDao ssDao)
	{
		this.sbDao = sbDao;
		this.sdDao = sdDao;
		this.spDao = spDao;
		this.ssDao = ssDao;
	}
 	
 	//保留几位小数
 	public float getDec(float value,int type)
 	{
 		 DecimalFormat df = null;
 		if (type ==2){
 			df = new DecimalFormat("#.##");
 		} else if(type==4){
 			df = new DecimalFormat("#.####");
 		} else {
 			df = new DecimalFormat("#.###");
 		}
 		  
         float f=Float.valueOf(df.format(value));  
         return f; 
 		//float valueDec = (float)(Math.round(value*10000))/10000.0;
 		//return valueDec;
 	}
 	 	
 	//获取两值比率
 	public float getGAP(float value1,float value2)
 	{
 		if(value1>-0.000001 && value1<0.000001)
 			return 0;
 		float tmpValue = (float) (value2 - value1)/value1;
		float gap = getDec(tmpValue,4);
		return gap;
 	} 	 
 	 
 	//获取交易提示
 	public int getDealWarn(int flag,int pointSuspectedDateGAP,int pointCurDateGAP,int suspectedCurDateGap)
 	{
 		int ret=0;
 		if(flag == 1) //上涨 
 		{
 			/*
 			if(pointSuspectedDateGAP < 8)
	 			ret= ConstantsInfo.DEAL_WARN_SEE; //观望
	 		else if(suspectedCurDateGap <=2)
	 		*/
 			ret = ConstantsInfo.DEAL_WARN_SALE;//卖出
 		} else {
 			/*
 			if(pointSuspectedDateGAP < 8)
	 			ret= ConstantsInfo.DEAL_WARN_INTEREST; //关注
	 		else if(suspectedCurDateGap <=2)
	 		*/
	 		//	ret= ConstantsInfo.DEAL_WARN_BUG;//买入
 			ret= ConstantsInfo.DEAL_WARN_BUG;//买入
 		}
 		return ret;
 	}
 	
 	
 	
 	
 	public StockDesireValue getStockDesireValue(float curValue,float curStartValue,float  curEndValue) 
 	{
 		float tmpValue=0;
 		//预期
 		float desireValue1,desireValue2,desireValue3,desireValue4,desireValue5,desireValue6=0;
 		float desireValue1Gap,desireValue2Gap,desireValue3Gap,desireValue4Gap,desireValue5Gap,desireValue6Gap=0;
 		float desireRange1,desireRange2,desireRange3,desireRange4,desireRange5,desireRange6=0;
 		float desireRate1,desireRate2,desireRate3,desireRate4,desireRate5,desireRate6=0;
 		
 		StockDesireValue sdValue;//预期
 		
 		//System.out.println("rate:"+rate);			
		//0.382预期点位 onstantsInfo.STOCK_DESIRE1 
		tmpValue = (float) (curEndValue - 0.382 * (curEndValue - curStartValue));
		desireValue1= getDec(tmpValue,2);
		//预期幅度
		tmpValue = desireValue1/curEndValue -1;
		desireRange1 = getDec(tmpValue,4);		
		//比率
		tmpValue= desireRange1*100/stockDesiredDate;			
		desireRate1= getDec(tmpValue,2);	
		//预期差
		desireValue1Gap = getGAP(desireValue1,curValue);
		//0.5预期点位
		tmpValue = (float) (curEndValue - 0.5 * (curEndValue - curStartValue));
		desireValue2 =  getDec(tmpValue,2);
		//预期幅度
		tmpValue = desireValue2/curEndValue -1;
		desireRange2 =  getDec(tmpValue,4);
		//比率
		tmpValue= desireRange2*100/stockDesiredDate;			
		desireRate2= getDec(tmpValue,2);	
		//预期差
		desireValue2Gap = getGAP(desireValue2,curValue);
		
		//0.618预期点位
		tmpValue = (float) (curEndValue - 0.618 * (curEndValue - curStartValue));
		desireValue3 =  getDec(tmpValue,2);	
		//预期幅度
		tmpValue = desireValue3/curEndValue -1;
		desireRange3 = getDec(tmpValue,4);
		//比率
		tmpValue= desireRange3*100/stockDesiredDate;			
		desireRate3= getDec(tmpValue,2);	
		//预期差
		//预期差
		desireValue3Gap = getGAP(desireValue3,curValue);
		
		//0.75预期点位
		tmpValue = (float) (curEndValue - 0.75 * (curEndValue - curStartValue));
		desireValue4 =  getDec(tmpValue,2);
		//预期幅度
		tmpValue = desireValue4/curEndValue -1;
		desireRange4 = getDec(tmpValue,4);
		//比率
		tmpValue= desireRange4*100/stockDesiredDate;			
		desireRate4= getDec(tmpValue,2);
		//预期差
		desireValue4Gap = getGAP(desireValue4,curValue);
		
		//1 预期点位
		tmpValue = (float) (curEndValue - 1 * (curEndValue - curStartValue));
		desireValue5 = getDec(tmpValue,2);
		//预期幅度
		tmpValue = desireValue5/curEndValue -1;
		desireRange5 = getDec(tmpValue,4);
		//比率
		tmpValue= desireRange5*100/stockDesiredDate;			
		desireRate5= getDec(tmpValue,2);	
		//预期差
		desireValue5Gap = getGAP(desireValue5,curValue);
		
		//1.08预期点位
		tmpValue = (float) (curEndValue - 1.08 * (curEndValue - curStartValue));
		desireValue6 = getDec(tmpValue,2);
		//预期幅度
		tmpValue = desireValue6/curEndValue -1;
		desireRange6 = getDec(tmpValue,4);
		//比率
		tmpValue= desireRange6*100/stockDesiredDate;	
		desireRate6= getDec(tmpValue,2);
		desireValue6Gap = getGAP(desireValue6,curValue);
		
		sdValue=new StockDesireValue(desireValue1,desireRange1,desireRate1,desireValue1Gap,
				desireValue2,desireRange2,desireRate2,desireValue2Gap,
				desireValue3,desireRange3,desireRate3,desireValue3Gap,
				desireValue4,desireRange4,desireRate4,desireValue4Gap,
				desireValue5,desireRange5,desireRate5,desireValue5Gap,
				desireValue6,desireRange6,desireRate6,desireValue6Gap);
		
		return sdValue;//预期
 		
 	}
 	
 	public float getRegion(float curValue, StockDesireValue sdValue)
 	{
 		float ret = 0;
 		if(curValue < sdValue.getDesireValue1())
 			ret = ConstantsInfo.STOCK_DESIRE1;
 		else if(curValue < sdValue.getDesireValue2())
 			ret = ConstantsInfo.STOCK_DESIRE2;
 		else if(curValue < sdValue.getDesireValue3())
 			ret = ConstantsInfo.STOCK_DESIRE3;
 		else if(curValue < sdValue.getDesireValue4())
 			ret = ConstantsInfo.STOCK_DESIRE4;
 		else if(curValue < sdValue.getDesireValue5())
 			ret = ConstantsInfo.STOCK_DESIRE5;
 		else if(curValue < sdValue.getDesireValue6())
 			ret = ConstantsInfo.STOCK_DESIRE6;
 		else 
 			ret = ConstantsInfo.STOCK_DESIRE7;
 		
 		return ret;
 	}
 	
 	//反转区域  疑似极点 前一极点 前二极点
 	public float getReversalRegion(float suspectedValue, float lastExtrmeValue,float priHighOrLowestValue){
 		float tmpValue =0;
 		float reversalRegion = 0;
 		
 		float tmp = lastExtrmeValue -priHighOrLowestValue;
 		if(tmp>-0.000001 && tmp<0.000001)
 			return 0;
 		tmpValue = (lastExtrmeValue- suspectedValue)/tmp;
 	
 		/*
 		if(tmpValue < ConstantsInfo.STOCK_DESIRE1)
 			reversalRegion = ConstantsInfo.STOCK_DESIRE1;
 		else if(tmpValue < ConstantsInfo.STOCK_DESIRE2)
 			reversalRegion = ConstantsInfo.STOCK_DESIRE2;
 		else if(tmpValue < ConstantsInfo.STOCK_DESIRE3)
 			reversalRegion = ConstantsInfo.STOCK_DESIRE3;
 		else if(tmpValue < ConstantsInfo.STOCK_DESIRE4)
 			reversalRegion = ConstantsInfo.STOCK_DESIRE4;
 		else if(tmpValue < ConstantsInfo.STOCK_DESIRE5)
 			reversalRegion = ConstantsInfo.STOCK_DESIRE5;
 		else if(tmpValue < ConstantsInfo.STOCK_DESIRE6)
 			reversalRegion = ConstantsInfo.STOCK_DESIRE6;
 		else 
 			reversalRegion = ConstantsInfo.STOCK_DESIRE7;	
 		*/
 		reversalRegion = getDec(tmpValue,3);
 		return reversalRegion;
 	}
 	
 	
 	//计算前一涨停数据
 	public String priUpData(String stockFullId, String anaylseDate,StockData sdata) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException
 	{
 		String curDate=null;
 		int priCurDateGap=-1; 
 		String info ="-1|0";
 		//取当天日数据作为计算，不拿周，月数据
		//StockData sdata = sdDao.getZhiDingDataStock(stockFullId,ConstantsInfo.DayDataType, anaylseDate);
		//前一涨停数据
		StockData prisdata =sdDao.getPriUpValue(stockFullId);
		
		if(sdata!=null && prisdata!=null) {
			curDate= sdata.getDate().toString();
			priCurDateGap = sdDao.getStockDataDateGap(stockFullId,prisdata.getDate().toString(),curDate, ConstantsInfo.DayDataType);
			//System.out.println(curDate);
			//System.out.println(prisdata.getDate().toString());
			info = priCurDateGap+ "|"+ prisdata.getDate().toString();
		}
			
		return info;
 	}
 	
 	//获取与大盘对比两时间差
 	public int getToMarketGAP(String sDate, String mDate,int dataType) throws ParseException, SecurityException, IOException, ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException
 	{
 		int dayCom = 0;
 		int gap = 0;
		dayCom = stockDater.compareDate(sDate,mDate);
		if(dayCom>0)			
			gap = sdDao.getStockDataDateGap("SH000001",mDate,sDate,dataType);	
		else if (dayCom<0)
			gap = -sdDao.getStockDataDateGap("SH000001",sDate,mDate,dataType);
		else 
			gap=0;
		return gap;
 	}
 	
 	//计算date当天 分析趋势，上，下转等
 	public StockExcelItem getExcelItem(String stockFullId,int dataType,int stockType, String anaylseDate, StockData sdata) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
 	{
 	
 		//StockData sdata = sdDao.getZhiDingDataStock(stockFullId,ConstantsInfo.DayDataType,anaylseDate);
		if(sdata == null)
			return null;
		
		//当天涨幅
		float range = sdata.getRange();
 		
 		//最后两个极值点
 		List<StockPoint> listSP = new ArrayList<StockPoint>();
 		listSP = spDao.getLastTwoPointStock(stockFullId, dataType, anaylseDate);
 		
 		StockPoint lastSp = null,priSp=null;
 		if(listSP == null || listSP.size() == 0) {
			stockLogger.logger.fatal("stockFullId："+stockFullId+"极点表 type:"+dataType+"无数据");
			System.out.println(stockFullId+"极点表type:"+dataType+"无数据");
			return null;  
		} else if(listSP.size() ==1 ){
			lastSp  = listSP.get(0);
			priSp = null;
			System.out.println(stockFullId+"无前高低点");
		} else if(listSP.size() ==2){
			lastSp  = listSP.get(0);
			priSp = listSP.get(1);
		}
 			
 		StockExcelItem eItem=null;
 		//String curExtremeDate=null;//最近极值时间
 		String lastExtremeDate =null; //前一个极点时间
 		float lastExtrmePrice=0;
 		float tmpValue=0;
 		
 		//
 		int curTread =0;//当前趋势 0跌 1涨
 		//涨跌停数
 		int upType=0;//涨跌集类型
 		int upOrdownTimes=0;
 		int curState=0;//状态，0为调整，1为上涨，2为下跌
 		int dealWarn=0;//交易提示 0 观望 1卖出 2观注 3买入 
 		
 		//2015年 最低点时间及点位 最高点
 		String minDate = "";
		float minPrice = 0;
		String maxDate = "";
		float maxPrice = 0;
		float minMaxRatio = 0; //最低最高点比
 		
 		//前一个极点时间(开始)，疑似极点（结束）， 当前， 时间   //再前高时间
 		String curStartDate = null,curEndDate = null, curDate = null,priDate=null;
 		//前一个极点时间（开始），疑似极点（结束）， 当前 ，点位 
 		float curStartValue = 0,curEndValue = 0, curValue = 0, priHighOrLowest=0;
 		float workRegion =0, reversalRegion=0;
 		
 		String priUpDateGap =""; //前一涨停时间差
 		//极点 疑点 当前时间三者 时间差
 		int pointSuspectedDateGAP,pointCurDateGAP,suspectedCurDateGap=0;
 		//极点 疑点 当前时间三者 点位比
 		float pointSuspectedValueGap,pointCurValueGAP,suspectedCurValueGap=0;
 		
 		//买赢亏
 		float bugValue = 0,winValue = 0,loseValue=0; 		
 	
 		//与大盘对比
 		int toMarketPSDateGAP,toMarketPCDateGAP,toMarketSCDateGAP;
 		float toMaretPSSpaceGAP=0,toMaretPCSpaceGAP,toMaretSCSpaceGAP;
 		int trendConsistent=0; //趋势是否一致
 		
 		int marketIndex=0;
 		
 		StockStatValue ssValue;//统计
 		StockCurValue scValue;//当前
 		StockDesireValue sdValue;//预期
 		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  	
    	    	
 		//当前趋势
 		curTread = lastSp.getWillFlag()>0?lastSp.getWillFlag()-1:lastSp.getWillFlag()+1;
 		
		//前一个极点
		lastExtrmePrice = lastSp.getExtremePrice();
		lastExtremeDate = sdf.format(lastSp.getExtremeDate());
		String crossLastDate=lastSp.getToDate().toString();				
		
		//取当天日数据作为计算，不拿周，月数据
		//StockData sdata = sdDao.getLastDataStock(stockFullId,ConstantsInfo.DayDataType);	
		//当天
 		String nowTime = anaylseDate; //stockDateTimer.getCurDate(); 
		
		StockData sMinData=null;
		StockData sMaxData=null;
			
		if (curTread == 0) { 
			
			//3 最近最低点 疑似极点
			sMinData=sdDao.getMinStockDataPoint(stockFullId,lastExtremeDate,nowTime,ConstantsInfo.DayDataType);
			if ( sMinData == null){
				stockLogger.logger.fatal("****stockFullId："+stockFullId+"无最近低点****");
				return null; 
			}
			//float minPrice = sMinData.getLowestPrice();
			//前一极点 开始时间
			curStartDate = 	lastExtremeDate;
			//开始点位
			curStartValue = lastExtrmePrice;//sdDao.getStockLowestPrice(stockFullId,curDownStartDate,dataType);
			
			//疑似极点 结束时间
			curEndDate = sMinData.getDate().toString();	
			////curExtremeDate = curEndDate;最近极点时间				
			//结束点位
			curEndValue = sMinData.getLowestPrice();
			//当前幅
			//curDownRange = sdDao.getStockCurHigestRange(stockFullId,curDownEndDate,dataType,0);		
				
		} else {			

			//3 最近最高点 疑似极点
			sMaxData=sdDao.getMaxStockDataPoint(stockFullId,lastExtremeDate,nowTime,ConstantsInfo.DayDataType);
			if ( sMaxData == null){
				stockLogger.logger.fatal("****stockFullId："+stockFullId+"无最近高点****");
				stockLogger.logger.fatal("****交叉点："+crossLastDate+"当前日点："+nowTime+"两日期间无交易数据****");
				return null; 
			}
			//开始时间
			curStartDate = lastExtremeDate;
			//开始点位
			curStartValue = lastExtrmePrice;//sdDao.getStockHighestPrice(stockFullId,curUpStartDate,dataType);//开始点位
			//结束时间
			curEndDate = sMaxData.getDate().toString();	
			//结束点位
			curEndValue = sMaxData.getHighestPrice();	
			
			//幅
			//curUpRange = sdDao.getStockCurHigestRange(stockFullId,curUpEndDate,dataType,1);
		}
		
		//反转区域与前高低点
		if(priSp == null) {
 			priHighOrLowest = 0 ;
 			priDate = "无";
 		} else {
 			priHighOrLowest = priSp.getExtremePrice();	
 			priDate = priSp.getExtremeDate().toString();
 		}
		reversalRegion = getReversalRegion(curEndValue,curStartValue,priHighOrLowest);
		
		//当前时间
		curDate = sdata.getDate().toString();
		//当前收盘价格
		curValue = sdata.getClosingPrice();
				
		//极点与疑似 差 点位比
		pointSuspectedDateGAP = sdDao.getStockDataDateGap(stockFullId,curStartDate,curEndDate,dataType);
		pointSuspectedValueGap = getGAP(curStartValue,curEndValue);
		
		//极点与 当前差 点位比
		pointCurDateGAP = sdDao.getStockDataDateGap(stockFullId,curStartDate,curDate,dataType);
		pointCurValueGAP = getGAP(curStartValue,curValue);
		
		//疑似点与当前 时间差 点位比
		suspectedCurDateGap = sdDao.getStockDataDateGap(stockFullId,curEndDate,curDate,dataType);
		
		suspectedCurValueGap = getGAP(curEndValue,curValue);
		
		//状态
		if(curEndDate.equals(curDate)) {
			curState = curTread;
		} else {
			curState = -1; //调整
		}
				
		//交易提示
		dealWarn = getDealWarn(curTread,pointSuspectedDateGAP,pointSuspectedDateGAP,suspectedCurDateGap);
		
		//预期相关
		sdValue= getStockDesireValue(curValue,curStartValue,curEndValue);
		if (curTread == 0) { 		
			//买
			tmpValue = (float) (1 + pointSuspectedValueGap/100)* curEndValue;
			bugValue = getDec(tmpValue,2);			
			
			//赢 0.618
			tmpValue = (float) (sdValue.getDesireValue3()/bugValue-1);
			winValue = getDec(tmpValue,4);	
			
			//亏
			tmpValue = (float) (curEndValue/bugValue -1);	
			loseValue = getDec(tmpValue,4);	
		}
					
		
		//运行区域
		workRegion = getRegion(curValue,sdValue);
		
		if(dataType == ConstantsInfo.DayDataType){
   		//计算上一涨停时间差
			priUpDateGap = priUpData(stockFullId,anaylseDate, sdata);
					
			StockData sData=null;
			//2015-06-12最低点
			sData = sdDao.getMinStockDataPoint(stockFullId,"2015-06-12",nowTime,dataType);
			if ( sData != null){
				minDate = sData.getDate().toString();
				minPrice= sData.getLowestPrice();
			}
		
			sData = sdDao.getMaxStockDataPoint(stockFullId,"2015-06-04",nowTime,dataType);
			if ( sData != null){
				maxDate = sData.getDate().toString();
				maxPrice = sData.getHighestPrice(); 
			}
			
			if ( maxPrice > 0.1) {
				tmpValue = (float) (minPrice/maxPrice);	
				minMaxRatio = getDec(tmpValue,2);
			}
	
		}
   		
		//计算涨跌停数
		if(sdata.getRange()>9.9) {
			upType=1;
			upOrdownTimes=sdDao.getUpOrDownTimes(stockFullId,curStartDate,curDate,upType);
		} else if(sdata.getRange()< -9.9){
			upType=0;
			upOrdownTimes=sdDao.getUpOrDownTimes(stockFullId,curStartDate,curDate,upType);
		} else {
			upType=0;
			upOrdownTimes=0;
		}			
		
		ssValue=new StockStatValue(curTread,range,priUpDateGap,upType,upOrdownTimes,pointSuspectedDateGAP,suspectedCurDateGap,dealWarn);		
		//大盘指数  或商品
		if (stockType == ConstantsInfo.DPMarket){
			marketIndex = sbDao.getMarketNum(stockFullId);
			scValue=new StockCurValue(priDate,priHighOrLowest,reversalRegion,curStartDate,curStartValue,curEndDate,curEndValue,curDate,curValue,workRegion,
					bugValue,winValue,loseValue,dealWarn,curState,curTread,
					pointSuspectedDateGAP,pointSuspectedValueGap,pointCurDateGAP,pointCurValueGAP,suspectedCurDateGap,suspectedCurValueGap,
					0,0,0,0,0,0,0, minDate, minPrice,maxDate, maxPrice,minMaxRatio);
			
			//保留大盘信息方便后续对比
			MarketStockCurInfo[marketIndex][dataType-1] = scValue;
		} else {
			marketIndex = stockType-2;	
			//System.out.println("curStartDate:"+curStartDate);	
			
			StockCurValue scMarketValue = MarketStockCurInfo[marketIndex][dataType-1];
			
			//极点时间
			String marketPDate = scMarketValue.getStartDate();
			//疑点时间
			String marketSDate = scMarketValue.getEndDate();
			//当前时间
			String marketCDate = scMarketValue.getCurDate();
			//与大盘极疑对比	
			toMarketPSDateGAP = getToMarketGAP(curStartDate,marketPDate,dataType);
			//空间
			toMaretPSSpaceGAP = pointSuspectedValueGap - scMarketValue.getPointSuspectedValueGap();
			
			//与大盘极当对比	
			toMarketPCDateGAP = getToMarketGAP(curEndDate,marketSDate,dataType);
			//空间
			toMaretPCSpaceGAP = pointCurValueGAP - scMarketValue.getPointCurValueGap();
			
			//与大盘疑当对比	
			toMarketSCDateGAP = getToMarketGAP(curDate,marketCDate,dataType);
			//空间
			toMaretSCSpaceGAP = suspectedCurValueGap - scMarketValue.getSuspectedCurValueGap();
			
			trendConsistent =  (curTread == scMarketValue.getTread()? 0:1);
			scValue=new StockCurValue(priDate,priHighOrLowest,reversalRegion,curStartDate,curStartValue,curEndDate,curEndValue,curDate,curValue,workRegion,
					bugValue,winValue,loseValue,dealWarn,curState,curTread,
					pointSuspectedDateGAP,pointSuspectedValueGap,pointCurDateGAP,pointCurValueGAP,suspectedCurDateGap,suspectedCurValueGap,
					toMarketPSDateGAP,toMaretPSSpaceGAP,toMarketPCDateGAP,toMaretPCSpaceGAP,toMarketSCDateGAP,toMaretSCSpaceGAP,trendConsistent,
					minDate, minPrice,maxDate, maxPrice,minMaxRatio);
		}		
		
		eItem=new StockExcelItem(stockFullId,ssValue,scValue,sdValue);
	
		return eItem;
 	}
 	
 	//是否出现停牌现像
 	int getEnableTingPai(String stockFullId, String time, StockData sdata) throws IOException, ClassNotFoundException, SQLException
 	{
 		//取当天取大盘时间对比，是否有停牌
 		//StockData sdata = sdDao.getZhiDingDataStock(stockFullId,ConstantsInfo.DayDataType, time);
 		if(sdata == null){
 			stockLogger.logger.fatal("tingpai "+ time+" data null");
 			return 1;
 		}
 			
 		if(SHDate.equals(sdata.getDate().toString())){
 			return 0;
 		} else {
 			stockLogger.logger.fatal("tingpai sh000001 time:"+SHDate+" anayle time:"+sdata.getDate().toString());
 			return 1;//停牌
 		}
 	}
 	
 	
 	//分析大盘
 	public void writePointExcelFromMarket(Workbook wb,Sheet sheet,String filePath, String fileName, String filetime) throws SQLException, IOException, ClassNotFoundException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
	{
 		 List<StockMarket> listStockMarket = new ArrayList<StockMarket>(); 
		 listStockMarket=sbDao.getStockMarket(ConstantsInfo.StockMarket);
		 
		//先分析大盘
	    //大盘指数 一行 
		stockRow++;
		StockBaseFace sbFace = new StockBaseFace(0,"sh1111","","","","","","");
		ExcelCommon.writeExcelItemTitle(wb,sheet,"大盘指数",sbFace,stockRow,true);
		int stockType=0;
		//StockData sdata = sdDao.getLastDataStock("SH000001",ConstantsInfo.DayDataType);
		StockData sdata = sdDao.getZhiDingDataStock("SH000001",ConstantsInfo.DayDataType, filetime);
   		SHDate = sdata.getDate().toString();
	   	for(Iterator itMarket = listStockMarket.iterator();itMarket.hasNext();)
	   	{
	   		StockMarket sMarket = (StockMarket)itMarket.next();	
	   		stockRow++;
	   		String stockFullId = sMarket.getCode().toString();
	   		System.out.println(stockFullId);
	   		stockType=sbDao.getMarketType(stockFullId);
	 
	   		
	   		//if(!sMarket.getCode().toString().equals("sh000001"))
	   		//	continue;
	   		
	   		//基本面
			StockBaseFace baseFace = new StockBaseFace(0,stockFullId,sMarket.getBaseExpect(),sMarket.getMain(),
					sMarket.getPsychology(),sMarket.getRisk(),sMarket.getPotential(),sMarket.getFaucet());
	   		
			//其他值
	   		StockOtherInfoValue soiValue=new StockOtherInfoValue(stockFullId,sMarket.getName().toString(),0,0,baseFace,null);
	   		ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow, 0, null, true);
	   		
	   		//获取最近统计数据
		    String endDate = CommonDate.getCurDate();
	        String startDate = CommonDate.getBeforeDay(endDate, 1, 180);
	       //获取最近统计数据
		   	List<StockPoint> stockPointInfo=new ArrayList<StockPoint>();
		   	stockPointInfo = spDao.getRecentPointStock(stockFullId,ConstantsInfo.DayDataType,startDate);
		//	System.out.println(stockPointInfo.size());
		   	
		 	int extremeCol = 0;
			int isTableExist=sdDao.isExistStockTable(stockFullId,ConstantsInfo.TABLE_SUMMARY_STOCK);
	    	if(isTableExist != 0){//不存在
	    		StockSummary ss = ssDao.getZhiDingSummaryFromSummaryTable(stockFullId, filetime, ConstantsInfo.DayDataType);
			  
			  //疑极点
		   		if(ss!=null){
			    	String date = ss.getDayEndDate();
			    	
			    	if(stockDateColumnmap.containsKey(date)) {
			        	extremeCol = stockDateColumnmap.get(date);	
			        	
			        	String value="疑极:"+ss.getDayEndValue();
			        	ExcelCommon.writePointExcelItem(wb,sheet,null,value,extremeCol,stockRow,1);
			        				        	
			    	} else{
			    		System.out.println("not exist the day");
					}
			       // ExcelCommon.writePointExcelItem(wb,sheet,null, value,extremeCol,stockRow,1);
		   		} 
			}
		   		   		
			
		    for (int ij=0;ij<stockPointInfo.size();ij++)	
			{
		    	StockPoint sp = stockPointInfo.get(ij);
		    	
		    	String date = sp.getExtremeDate().toString();
		    	if(sp!=null && stockDateColumnmap.containsKey(date)) {
		        	extremeCol = stockDateColumnmap.get(date);			
		        	
		        	ExcelCommon.writePointExcelItem(wb,sheet,sp,"",extremeCol,stockRow,0);
		        				        	
		    	} else{
		    		System.out.println("not exist the day");
				}
		 	}
		    
	   	}
 		
	}
 	
 	//分析大盘
 	public void writeTotalOperationExcelFromMarket(Workbook wb,Sheet sheet,String filePath, String fileName, String filetime,int dateType) throws SQLException, IOException, ClassNotFoundException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
	{
 		 List<StockMarket> listStockMarket = new ArrayList<StockMarket>(); 
		 listStockMarket=sbDao.getStockMarket(ConstantsInfo.StockMarket);
		 
		//先分析大盘
	    //大盘指数 一行 
		stockRow++;
		StockBaseFace sbFace = new StockBaseFace(0,"sh1111","","","","","","");
		ExcelCommon.writeExcelItemTitle(wb,sheet,"大盘指数",sbFace,stockRow,true);
		int stockType=0;
		
   		StockData sdata = sdDao.getZhiDingDataStock("SH000001",ConstantsInfo.DayDataType, filetime);
   		SHDate = sdata.getDate().toString();
	   	for(Iterator itMarket = listStockMarket.iterator();itMarket.hasNext();)
	   	{
	   		StockMarket sMarket = (StockMarket)itMarket.next();	
	   		stockRow++;
	   		String stockFullId = sMarket.getCode().toString();
	   		System.out.println(stockFullId);
	   		stockType=sbDao.getMarketType(stockFullId);
	 
	   		
	   		//if(!sMarket.getCode().toString().equals("sh000001"))
	   		//	continue;
	   		
	   		//基本面
			StockBaseFace baseFace = new StockBaseFace(0,stockFullId,sMarket.getBaseExpect(),sMarket.getMain(),
					sMarket.getPsychology(),sMarket.getRisk(),sMarket.getPotential(),sMarket.getFaucet());
	   		
			//其他值
	   		StockOtherInfoValue soiValue=new StockOtherInfoValue(stockFullId,sMarket.getName().toString(),0,0,baseFace,null);
	   		ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow,0, null, true);
	   		
	   		List<StockOperation> stockOperationInfo=new ArrayList<StockOperation>();
	   		//获取最近统计数据
	   		/*
		    String endDate = StockDateTimer.getCurDate();
	        String startDate = StockDateTimer.getBeforeDay(endDate, 1, 180);          	   		
	   		stockOperationInfo = ssDao.getOperationFromOperationTable(stockFullId, -1, 120);
	   		*/
	   		int nums = ConstantsInfo.ExportNum(dateType);
	   		stockOperationInfo = ssDao.getOperationFromOperationTable(stockFullId,dateType, nums);
	 	   	
	   		int extremeCol = 0;	
		//	System.out.println(stockPointInfo.size());		
		    for (int ij=0;ij<stockOperationInfo.size();ij++)	
			{
		    	StockOperation sSop = stockOperationInfo.get(ij);
		    	if (sSop==null){
		    		continue;
		    	}
		    	
		    	boolean flag = false;
		    	
		    	//先hash查找
		    	if(stockDateColumnmap.containsKey(sSop.getOpDate())) {
		        	extremeCol = stockDateColumnmap.get(sSop.getOpDate());	
		        	flag = true;
		        	/*
		        	List<StockOperation> stockOperationInfoByDate=new ArrayList<StockOperation>();
		        	stockOperationInfoByDate = ssDao.getOperationFromOperationTableByDate(sSop.getFullId(),sSop.getOpDate());
		        	for (int datesize=0;datesize<stockOperationInfoByDate.size();datesize++)	
					{
		        		StockOperation sSopDate = stockOperationInfoByDate.get(datesize);
		        		ExcelCommon.writeTotalOperationExcelItem(wb,sheet,sSopDate,extremeCol,stockRow);
					}
		        	*/
		    	} else {
		    		if (dateType == ConstantsInfo.WeekDataType || dateType == ConstantsInfo.MonthDataType){
		    			
		    			//stockOperationInfoByDate = ssDao.getOperationFromOperationTableByDate(sSop.getFullId(),sSop.getOpDate());
		    			//再遍历
		    			for(String key: stockDateColumnmap.keySet()) {
		    				System.out.println("key:"+key+"data:"+ sSop.getOpDate());
		    				if(CommonDate.isSameDate(key, sSop.getOpDate(), dateType)){
		    					flag = true;
		    					extremeCol = stockDateColumnmap.get(key);
		    					break;
		    				}	
		    			}	
		    		} 
		    	}
		    	
		    	if(flag){
		    		//将日周月展示
		        	List<StockOperation> stockOperationInfoByDate=new ArrayList<StockOperation>();
		        	stockOperationInfoByDate = ssDao.getOperationFromOperationTableByDate(sSop.getFullId(),sSop.getOpDate());
		        	for (int datesize=0;datesize<stockOperationInfoByDate.size();datesize++){
		        		StockOperation sSopDate = stockOperationInfoByDate.get(datesize);
		        		ExcelCommon.writeTotalOperationExcelItem(wb,sheet,sSopDate,extremeCol,stockRow);
					}		
		    	}  
		    	
		 	}
		    
	   	}
 		
	}
 	
 	
 	//分析大盘
 	public void writeOperationExcelFromMarket(Workbook wb,Sheet sheet,String filePath, String fileName, String filetime, int dateType) throws SQLException, IOException, ClassNotFoundException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
	{
 		 List<StockMarket> listStockMarket = new ArrayList<StockMarket>(); 
		 listStockMarket=sbDao.getStockMarket(ConstantsInfo.StockMarket);
		 
		//先分析大盘
	    //大盘指数 一行 
		stockRow++;
		StockBaseFace sbFace = new StockBaseFace(0,"sh1111","","","","","","");
		ExcelCommon.writeExcelItemTitle(wb,sheet,"大盘指数",sbFace,stockRow,true);
		int stockType=0;
	
   		StockData sdata = sdDao.getZhiDingDataStock("SH000001",ConstantsInfo.DayDataType, filetime);
   		SHDate = sdata.getDate().toString();
	   	for(Iterator itMarket = listStockMarket.iterator();itMarket.hasNext();)
	   	{
	   		StockMarket sMarket = (StockMarket)itMarket.next();	
	   		stockRow++;
	   		String stockFullId = sMarket.getCode().toString();
	   		System.out.println(stockFullId);
	   		stockType=sbDao.getMarketType(stockFullId);
	 
	   		//if(!sMarket.getCode().toString().equals("sh000001"))
	   		//	continue;
	   		
	   		//基本面
			StockBaseFace baseFace = new StockBaseFace(0,stockFullId,sMarket.getBaseExpect(),sMarket.getMain(),
					sMarket.getPsychology(),sMarket.getRisk(),sMarket.getPotential(),sMarket.getFaucet());
	   		
			//其他值
	   		StockOtherInfoValue soiValue=new StockOtherInfoValue(stockFullId,sMarket.getName().toString(),0,0,baseFace,null);
	   		ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow,0,null, true);
	   		
	   		//获取最近统计数据
			List<StockOperation> stockOperationInfo=new ArrayList<StockOperation>();
			stockOperationInfo = ssDao.getOperationFromOperationTable(stockFullId,dateType,30);
			
			int extremeCol = 0;
			int earn=0,stop=0,loss=0;
		    for (int ij=0;ij<stockOperationInfo.size();ij++)	
			{
		    	StockOperation sSop = stockOperationInfo.get(ij);
		    	            
		    	if(sSop!=null && stockDateColumnmap.containsKey(sSop.getOpDate())) {
		        	extremeCol = stockDateColumnmap.get(sSop.getOpDate());
		        	String psState = ssDao.getpsStatusFromSummaryTable(stockFullId,sSop.getOpDate());
		        	
		        	ExcelCommon.writeOperationExcelItem(wb,sheet,sSop,psState,extremeCol ,stockRow);
		        	if(sSop.getEarnRatio()!=0)
			    		earn++;
			    	if(sSop.getStopRatio()!=0)
			    		stop++;
			    	if(sSop.getLossRatio()!=0)
			    		loss++;
		    	}
		    	
		    	
		 	}
		    
		    int totalsize = earn+stop+loss;
		    if(totalsize > 0) {
		    	ExcelCommon.writeOperationTotalExcelItem(wb,sheet,0 ,stockRow,earn,stop,loss,totalsize,0);
		    }	    
	   	}
 		
	}
 	
 	
 	//分析大盘
 	public void writeSummaryExcelFromMarket(Workbook wb,Sheet sheet,String filePath, String fileName, String filetime) throws SQLException, IOException, ClassNotFoundException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
	{
 		 List<StockMarket> listStockMarket = new ArrayList<StockMarket>(); 
		 listStockMarket=sbDao.getStockMarket(ConstantsInfo.StockMarket);
		 
		//先分析大盘
	    //大盘指数 一行 
		stockRow++;
		StockBaseFace sbFace = new StockBaseFace(0,"sh1111","","","","","","");
		ExcelCommon.writeExcelItemTitle(wb,sheet,"大盘指数",sbFace,stockRow,true);
		int stockType=0;
		StockData sdata = sdDao.getZhiDingDataStock("SH000001",ConstantsInfo.DayDataType, filetime);
   		SHDate = sdata.getDate().toString();
	   	for(Iterator itMarket = listStockMarket.iterator();itMarket.hasNext();)
	   	{
	   		StockMarket sMarket = (StockMarket)itMarket.next();	
	   		stockRow++;
	   		String stockFullId = sMarket.getCode().toString();
	   		System.out.println(stockFullId);
	   		stockType=sbDao.getMarketType(stockFullId);
	 
	   		
	   		int isTableExist=sdDao.isExistStockTable(stockFullId,ConstantsInfo.TABLE_SUMMARY_STOCK);
	    	if(isTableExist == 0){//不存在
				stockLogger.logger.fatal("****stockFullId："+stockFullId+"不存在统计表****");
				System.out.println(stockFullId+"统计表不存在****");
				continue;  
			}
	   		
	   		//if(!sMarket.getCode().toString().equals("sh000001"))
	   		//	continue;
	   		
	   		//基本面
			StockBaseFace baseFace = new StockBaseFace(0,stockFullId,sMarket.getBaseExpect(),sMarket.getMain(),
					sMarket.getPsychology(),sMarket.getRisk(),sMarket.getPotential(),sMarket.getFaucet());
			StockBaseYearInfo yearInfo = sbDao.lookUpStockBaseYearInfo(stockFullId);
			//其他值
	   		StockOtherInfoValue soiValue=new StockOtherInfoValue(stockFullId,sMarket.getName().toString(),0,0,baseFace,yearInfo);
	   		ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow,0,null, true);
	   		
	   		//获取最近统计数据
			List<StockSummary> stockSummaryInfo=new ArrayList<StockSummary>();
			stockSummaryInfo = ssDao.getSummaryFromSummaryTable(stockFullId,15);
			
			int extremeCol = 0;

		    for (int ij=0;ij<stockSummaryInfo.size();ij++)	
			{
		    	StockSummary sSum = stockSummaryInfo.get(ij);
		    	            
		    	if(sSum!=null && stockDateColumnmap.containsKey(sSum.getDayCurDate())) {
		        	extremeCol = stockDateColumnmap.get(sSum.getDayCurDate());				        	
		        	ExcelCommon.writeSummaryExcelItem(wb,sheet,sSum,extremeCol ,stockRow,1);
		    	}
		    	
		 	}
	   	}
 		
	}
 	
 	//分析大盘
 	public void writeExcelFromMarket(Workbook wb,Sheet sheet,String filePath, String fileName, String filetime, boolean flag) throws SQLException, IOException, ClassNotFoundException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
	{
		 List<StockMarket> listStockMarket = new ArrayList<StockMarket>(); 
		 listStockMarket=sbDao.getStockMarket(ConstantsInfo.StockMarket);
		 
		//先分析大盘
	    //大盘指数 一行 
		stockRow++;
		
		StockBaseFace sbFace = new StockBaseFace(0,"sh1111","","","","","","");
		ExcelCommon.writeExcelItemTitle(wb,sheet,"大盘指数",sbFace,stockRow,flag);
		
		int stockType=0;
		StockData smarket_data = sdDao.getZhiDingDataStock("SH000001",ConstantsInfo.DayDataType, filetime);
   		SHDate = smarket_data.getDate().toString();
   	
		stockLogger.logger.fatal("sh000001 time:"+SHDate);
	   	for(Iterator itMarket = listStockMarket.iterator();itMarket.hasNext();)
	   	{
	   		StockMarket sMarket = (StockMarket)itMarket.next();	
	   		stockRow++;
	   		String stockFullId = sMarket.getCode().toString();
	   		System.out.println(stockFullId);
	   		stockType=sbDao.getMarketType(stockFullId);	 
	   		
	   		//if(!sMarket.getCode().toString().equals("sh000001"))
	   		//	continue;
	   		
	   		//基本面
			StockBaseFace baseFace = new StockBaseFace(0,stockFullId,sMarket.getBaseExpect(),sMarket.getMain(),
					sMarket.getPsychology(),sMarket.getRisk(),sMarket.getPotential(),sMarket.getFaucet());
	   		
		//	setHighestLowestPrice(baseFace,stockFullId);
			
			StockBaseYearInfo yearInfo = sbDao.lookUpStockBaseYearInfo(stockFullId);			
			StockSummary ssu = new StockSummary(0,stockFullId,"","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","");
			
			//其他值
	   		StockOtherInfoValue soiValue=new StockOtherInfoValue(stockFullId,sMarket.getName().toString(),0,0,baseFace,yearInfo);		
	   		ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow,1,ssu, flag);
	   		StockData sdata = sdDao.getZhiDingDataStock(stockFullId,ConstantsInfo.DayDataType, filetime);
	   			
	   		//分析日
	   	//	stockLogger.logger.debug("*****分析日*****");
	   		StockExcelItem dayItem = getExcelItem(stockFullId,ConstantsInfo.DayDataType,stockType, filetime, sdata); 
	   		ExcelCommon.writeExcelItem(wb,sheet,dayItem, stockRow, ConstantsInfo.DayDataType,ssu, flag);
	   		if (dayItem == null) 
				continue;
			//分析周预测值
			//	stockLogger.logger.debug("*****分析周*****");
			StockExcelItem weekItem = getExcelItem(stockFullId,ConstantsInfo.WeekDataType,stockType, filetime, sdata);		
			ExcelCommon.writeExcelItem(wb,sheet,weekItem, stockRow, ConstantsInfo.WeekDataType,ssu, flag);	
			if (weekItem == null)
				continue;			
			//分析月预测值
	//		stockLogger.logger.debug("*****分析月*****");
			StockExcelItem monthItem = getExcelItem(stockFullId,ConstantsInfo.MonthDataType,stockType, filetime, sdata);		
			ExcelCommon.writeExcelItem(wb,sheet,monthItem, stockRow, ConstantsInfo.MonthDataType,ssu, flag);
			if (monthItem == null)
				continue;
			
			StockExcelStatItem statItem = getExcelStatItem(dayItem, weekItem, monthItem);		
			if(flag){
				//统计
				ExcelCommon.writeExcelStatItem(wb,sheet,statItem,stockRow,ssu,flag);	
			}
			
			int isTableExist=sdDao.isExistStockTable(ssu.getFullId(),ConstantsInfo.TABLE_SUMMARY_STOCK);
			if(isTableExist==0){//不存在
				ssDao.createStockSummaryTable(ssu.getFullId());
				ssDao.insertStockSummaryTable(ssu.getFullId(), ssu); 
			} else {
				//避免多次写入	
				StockSummary lastSS = ssDao.getZhiDingSummaryFromSummaryTable(stockFullId, filetime,ConstantsInfo.DayDataType);
				if(lastSS == null) {
					ssDao.insertStockSummaryTable(ssu.getFullId(), ssu);
				}
			}
			
	   	}

	}
 	
 	
 	//按期货生成excel 按周当前幅度排序
	public void writeExcelFormFuturesOrderBy(String filePath, String fileTime, boolean flag) throws SQLException, IOException, ClassNotFoundException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
	{
	 	List<StockMarket> listMarket = new ArrayList<StockMarket>(); 
		 
	 	//得到当前所有市场
	 	listMarket=sbDao.getStockMarket(ConstantsInfo.FuturesMarket);
    	XSSFWorkbook wb=null;
    	XSSFSheet sheet =null;
    	String excleFileName=null;
    	FileInputStream fileIStream = null;
    	sheetCount=0;
    	int titleRow =0;//记录行业或概念标题行
        //每个行业输出到excel一次
        for (int i=0;i<listMarket.size();i++)	
   		{ 	
   			if(i==0 || stockRow >= 510) {
   				if(!flag){
   					stockRow = 1;
   	        		writeExcelFromMarket(wb,sheet,filePath,excleFileName,fileTime, flag);
   	        	} else {
	   				//创建日期目录
	   				File file = new File(filePath+fileTime);  
	   				System.out.println(fileTime);
	   				if (!file.exists()){   
	   					 file.mkdir();   
	   				} 
	   				
		   			// 工作区		
		   			wb = new XSSFWorkbook(); 
		   			// 创建第一个sheet     
		   			sheet=  wb.createSheet("allstock");		
		   			sheetCount++;
		   			excleFileName="Stock_Futures_"+fileTime+"_All_"+sheetCount+".xlsx";	   		    
		   		    stockRow = 1;
		   			//创建excel
		   	 		ExcelCommon.createExcel(wb,sheet,filePath,excleFileName);
		   			writeExcelFromMarket(wb,sheet,filePath,excleFileName,fileTime, flag);
		   			FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);
		   			wb.write(fileOStream);		
		   	        fileOStream.close(); 
		   	        wb=null;
		   	        sheet=null;
   	        	}
   			}
   			
   			if(flag){
	   			excleFileName="Stock_Futures_"+fileTime+"_All_"+sheetCount+".xlsx"; 			
	   			File file = new File(filePath +fileTime+ "\\"+excleFileName);
				// 输入流   
				fileIStream = new FileInputStream(file);  			
				wb = new XSSFWorkbook(fileIStream);   
				sheet = wb.getSheetAt(0);  	
   			}
			
			StockExcelStatItem  statItem;  		
   			
			//当前市场
			StockMarket sMarket = listMarket.get(i);	
			String induCode = sMarket.getCode();
			String induName = sMarket.getName();
			if(induCode == null || induName == null)
				continue;				
			stockLogger.logger.fatal("市场："+induName);   		
			System.out.println("市场："+induName);			
			//行业标题 
			stockRow++;
			titleRow = stockRow;
			
			//基本面
			StockBaseFace baseFaceIndu = new StockBaseFace(0,induCode,sMarket.getBaseExpect(),sMarket.getMain(),
					sMarket.getPsychology(),sMarket.getRisk(),sMarket.getPotential(),sMarket.getFaucet());
			
			
			ExcelCommon.writeExcelItemTitle(wb,sheet,i+":"+induName,baseFaceIndu,stockRow, flag);
   					 	
   			//所属行业所有股票
   			List<StockToFutures> listFuturesStock = new ArrayList<StockToFutures>();   
   			listFuturesStock=sbDao.getFuturesToStock(induCode);	   	
   		
   			stockLogger.logger.debug("市场股票数："+listFuturesStock.size());
   			int stockType=0;
   			
   			//按周涨幅比排序
   			List<StockExcelTotalInfo> listStockTotalInfoOrderBy = new ArrayList<StockExcelTotalInfo>(); 
   			List<String> listName = new ArrayList<String>(); 
   			for(Iterator ie=listFuturesStock.iterator();ie.hasNext();)
   			{
   				//stockRow++;
   				StockToFutures toInduStock = (StockToFutures) ie.next();
   				String stockFullId = toInduStock.getCode();	
   				System.out.println("stockFullId:"+stockFullId);
   				
   				//商品 暂时被当成上证股票处理，与上证对比
   				//stockType=sbDao.getMarketType(stockFullId);   				
   				stockType = ConstantsInfo.SHMarket;
   					
   				int isTableExist=sdDao.isExistStockTable(stockFullId,ConstantsInfo.TABLE_POINT_STOCK);
   		    	if(isTableExist == 0){//不存在
   					stockLogger.logger.fatal("****stockFullId："+stockFullId+"不存在极值表****");
   					System.out.println(stockFullId+"极值表不存在****");
   					continue;  
   				}
   		    	
   				//if(!stockFullId.equals("SH601268"))
   				//	continue;
   		    	
   				stockLogger.logger.fatal("****stockFullId："+stockFullId+"****");
   				
   				StockExcelItem dayItem;
				StockExcelItem weekItem;
				StockExcelItem monthItem;				
				
				//是否停牌
				int enableTingPai = 0; //getEnableTingPai(stockFullId);		
				//是否两融
				int enableTwoRong = sbDao.lookUpStockTwoRong(stockFullId);		
				//基本面
				//StockBaseFace baseFace = sbDao.lookUpStockBaseFace(stockFullId);
			//	StockBaseYearInfo yearInfo = sbDao.lookUpStockBaseYearInfo(stockFullId);
				StockBaseFace baseFace = new StockBaseFace(0,stockFullId,toInduStock.getBaseExpect(),toInduStock.getMain(),
						toInduStock.getPsychology(),toInduStock.getRisk(),toInduStock.getPotential(),toInduStock.getFaucet());
				
			//	setHighestLowestPrice(baseFace,stockFullId);
				
				//其他值
		   		StockOtherInfoValue soiValue=new StockOtherInfoValue(stockFullId,toInduStock.getName(),enableTwoRong,enableTingPai,baseFace,null);
		   		StockData sdata = sdDao.getZhiDingDataStock(stockFullId,ConstantsInfo.DayDataType, fileTime);				
   				//分析日预测值
   				dayItem = getExcelItem(stockFullId,ConstantsInfo.DayDataType,stockType, fileTime, sdata);   
   				if (dayItem == null){
   					stockLogger.logger.fatal("day point is null");
   					//continue;
   				} else {
   				//	ExcelCommon.writeExcelItem(wb,sheet,dayItem, stockRow, ConstantsInfo.DayDataType);
   				}
   				//分析周预测值
   				weekItem = getExcelItem(stockFullId,ConstantsInfo.WeekDataType,stockType, fileTime, sdata);
   				if (weekItem == null){
   					stockLogger.logger.fatal("week point is null");
   					//continue; 
   				} else {
   				//	ExcelCommon.writeExcelItem(wb,sheet,weekItem, stockRow, ConstantsInfo.WeekDataType);
   				}
   				//分析月预测值
   				monthItem = getExcelItem(stockFullId,ConstantsInfo.MonthDataType,stockType, fileTime, sdata);
   				if (monthItem == null){
   					stockLogger.logger.fatal("month point is null");
   					//continue; 
   				} else {
   				//	ExcelCommon.writeExcelItem(wb,sheet,monthItem,stockRow, ConstantsInfo.MonthDataType);
   				}
   				
   				//统计
   			
   				statItem = getExcelStatItem(dayItem, weekItem, monthItem);
   				
   				StockExcelTotalInfo setInfo =new StockExcelTotalInfo(soiValue,dayItem,weekItem,monthItem,statItem);
   				//停牌 放后面
   				//停牌 放后面
   				if(enableTingPai!=0 && setInfo.getStatItem()!=null  && setInfo.getStatItem().getDayMixStatItem()!=null) {
   					setInfo.getStatItem().getDayMixStatItem().setComPSState("211211");
   				} 
   				listStockTotalInfoOrderBy.add(setInfo);
   				listName.add(toInduStock.getName());
   				
   			}
   			  
   			//记录行业内各股票提示
   			int dealWarns[] = new int[4];
   			//排序
   			//Collections.sort(listStockTotalInfoOrderBy);    
   			Collections.sort(listName,Collator.getInstance(java.util.Locale.CHINA));
   			for (int kk=0;kk<listName.size();kk++)
   			{
   			for (int j=0;j<listStockTotalInfoOrderBy.size();j++)	
   			{
   				StockExcelTotalInfo setInfo = (StockExcelTotalInfo) listStockTotalInfoOrderBy.get(j);
   				if(!listName.get(kk).equals(setInfo.getSoiValue().getName()))
   					continue;
   				
   				stockRow++;   
   				StockSummary ssu = new StockSummary(0,"","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","");
   				
   				ExcelCommon.writeExcelStockOtherInfo(wb, sheet, setInfo.getSoiValue(), stockRow,1, ssu, flag);
   				
   				//未停牌
   				if (setInfo.getSoiValue().getEnableTingPai() == 0) {
   					statItem = setInfo.getStatItem();
   					
   					//统计 并设置统计值
   					ExcelCommon.writeExcelStatItem(wb,sheet,statItem,stockRow,ssu, flag);
   							
   	   		    	//统计数据已经在概念那 插入数据
   					//ssDao.insertStockSummaryTable(setInfo.getSoiValue().getFullId(),ssu);
   				
	   				if (setInfo.getDayItem() != null) {
	   					ExcelCommon.writeExcelItem(wb,sheet,setInfo.getDayItem(), stockRow, ConstantsInfo.DayDataType,ssu, flag);
	   					//记录股票交易提示
		   				dealWarns[setInfo.getDayItem().getScValue().getDealWarn()]++;
	   				}
	   				
	   				if (setInfo.getWeekItem() != null)
	   					ExcelCommon.writeExcelItem(wb,sheet,setInfo.getWeekItem(), stockRow, ConstantsInfo.WeekDataType,ssu, flag);
	   				if (setInfo.getMonthItem() != null)
	   					ExcelCommon.writeExcelItem(wb,sheet,setInfo.getMonthItem(),stockRow, ConstantsInfo.MonthDataType,ssu, flag);
	   				
	   				int isTableExist=sdDao.isExistStockTable(ssu.getFullId(),ConstantsInfo.TABLE_SUMMARY_STOCK);
	   				if(isTableExist==0){//不存在
	   					ssDao.createStockSummaryTable(ssu.getFullId());
	   				} else {
		   				//避免多次写入	
		   				StockSummary lastSS = ssDao.getZhiDingSummaryFromSummaryTable(ssu.getFullId(), fileTime,ConstantsInfo.DayDataType);
		   				if(lastSS == null) {
		   					ssDao.insertStockSummaryTable(ssu.getFullId(), ssu); 
		   				}
	   				}
   				}
   			} 	
   			}
   			
   			if(flag){
   				int sub=5;//未知
	   			if(dealWarns != null && dealWarns.length!=0)
	   				sub = getMax(dealWarns);			
				//买卖提示
				ExcelCommon.writeExcelItemDealWall(wb,sheet,null,sub,titleRow);
				FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);
				wb.write(fileOStream);
				fileOStream.flush();
				fileIStream.close();
				fileOStream.close();  
   			}
   			listStockTotalInfoOrderBy =null;  
			listFuturesStock = null;
          //测试作用
			//if(stockRow>10)
			//	break;
   		}
 
        listMarket = null;
	}
 	
 	
 	//按行业生成excel 按周当前幅度排序
	public void writeExcelFormIndustryOrderBy(String filePath, String fileTime, boolean flag) throws SQLException, IOException, ClassNotFoundException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
	{
	 	List<StockIndustry> listIndustry = new ArrayList<StockIndustry>(); 
		 
	 	//得到当前所有行业
    	listIndustry=sbDao.getStockIndustry();
    	XSSFWorkbook wb =null;
    	XSSFSheet sheet =null;
    	String excleFileName=null;
    	FileInputStream fileIStream = null;
    	sheetCount=0;
    	int titleRow =0;//记录行业或概念标题行
        //每个行业输出到excel一次
        for (int i=0;i<listIndustry.size();i++)	
   		{
   			if(i==0 || stockRow >= 510) {
   				if(!flag){
   					stockRow = 1;
   	        		writeExcelFromMarket(wb,sheet,filePath,excleFileName,fileTime, flag);
   	        	} else {
	   				//创建日期目录
	   				File file = new File(filePath+fileTime);  
	   				if (!file.exists()){   
	   					 file.mkdir();   
	   				} 
	   				
		   			// 工作区		
		   			wb = new XSSFWorkbook(); 
		   			// 创建第一个sheet     
		   			sheet = wb.createSheet("allstock");		
		   			sheetCount++;
		   			excleFileName="Stock_Industry_"+fileTime+"_All_"+sheetCount+".xlsx";	   		    
		   		    stockRow = 1;
		   			//创建excel
		   	 		ExcelCommon.createExcel(wb,sheet,filePath,excleFileName);
		   			writeExcelFromMarket(wb,sheet,filePath,excleFileName,fileTime, flag);
		   			FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);
		   			wb.write(fileOStream);		
		   	        fileOStream.close(); 
		   	        wb=null;
		   	        sheet=null;
   	        	}
   			}
   			
   			if(flag){
	   			excleFileName="Stock_Industry_"+fileTime+"_All_"+sheetCount+".xlsx"; 			
	   			File file = new File(filePath +fileTime+ "\\"+excleFileName);
				// 输入流   
				fileIStream = new FileInputStream(file);  			
				wb = new XSSFWorkbook(fileIStream);   
				sheet = wb.getSheetAt(0);  	
   			}
			
			StockExcelStatItem  statItem;  		 			
			//当前行业
			StockIndustry indu = listIndustry.get(i);	
			String induCode = indu.getThirdcode();
			String induName = indu.getThirdname();
			if(induCode == null || induName == null)
				continue;		
			
			//if(!induCode.equals("220403"))
			//	continue;
			
			stockLogger.logger.fatal("行业："+induName);   		
			System.out.println("行业："+induName);			
			//行业标题 
			stockRow++;
			titleRow = stockRow;
					
			//基本面
			StockBaseFace baseFaceIndu = new StockBaseFace(0,induCode,indu.getBaseExpect(),indu.getMain(),
					indu.getPsychology(),indu.getRisk(),indu.getPotential(),indu.getFaucet());		
			ExcelCommon.writeExcelItemTitle(wb,sheet,i+":"+induName,baseFaceIndu,stockRow, flag);
							 	
   			//所属行业所有股票
   			List<StockToIndustry> listIndustryStock = new ArrayList<StockToIndustry>();   
   			listIndustryStock=sbDao.getIndustryToStock(induCode);	   	
   		
   			stockLogger.logger.debug("行业股票数："+listIndustryStock.size());
   			int stockType=0;
   			
   			//按周涨幅比排序
   			List<StockExcelTotalInfo> listStockTotalInfoOrderBy = new ArrayList<StockExcelTotalInfo>();
   			List<String> listName = new ArrayList<String>(); 
   			
   			//中文排序
   			for(Iterator ie=listIndustryStock.iterator();ie.hasNext();)
   			{
   				//stockRow++;
   				StockToIndustry toInduStock = (StockToIndustry) ie.next();
   				String stockFullId = toInduStock.getStockFullId();	   				
   				stockType=sbDao.getMarketType(stockFullId);
   					
   				int isTableExist=sdDao.isExistStockTable(stockFullId,ConstantsInfo.TABLE_DATA_STOCK);
   		    	if(isTableExist == 0){//不存在
   					stockLogger.logger.fatal("****stockFullId："+stockFullId+"不存在极值表****");
   					System.out.println(stockFullId+"极值表不存在****");
   					continue;  
   				}
   		    	
   				//if(!stockFullId.equals("SH600004"))
   				//	continue;
   		    	
   				stockLogger.logger.fatal("****stockFullId："+stockFullId+"****");
   				
   				StockExcelItem dayItem;
				StockExcelItem weekItem;
				StockExcelItem monthItem;	
				
				StockData sdata = sdDao.getZhiDingDataStock(stockFullId,ConstantsInfo.DayDataType, fileTime);
				//是否停牌
				int enableTingPai = getEnableTingPai(stockFullId, fileTime, sdata);					
				//是否两融
				int enableTwoRong = sbDao.lookUpStockTwoRong(stockFullId);		
				//基本面
				StockBaseFace baseFace = sbDao.lookUpStockBaseFace(stockFullId);
			//	setHighestLowestPrice(baseFace,stockFullId);
				
				StockBaseYearInfo yearInfo = sbDao.lookUpStockBaseYearInfo(stockFullId);
				//其他值
		   		StockOtherInfoValue soiValue=new StockOtherInfoValue(stockFullId,toInduStock.getStockName(),enableTwoRong,enableTingPai,baseFace,yearInfo);
				 		
   				//分析日预测值
   				dayItem = getExcelItem(stockFullId,ConstantsInfo.DayDataType,stockType, fileTime, sdata);   
   				if (dayItem == null){
   					stockLogger.logger.fatal("day point is null");
   					//continue;
   				} else {
   				//	ExcelCommon.writeExcelItem(wb,sheet,dayItem, stockRow, ConstantsInfo.DayDataType);
   				}
   				//分析周预测值
   				weekItem = getExcelItem(stockFullId,ConstantsInfo.WeekDataType,stockType, fileTime, sdata);
   				if (weekItem == null){
   					stockLogger.logger.fatal("week point is null");
   					//continue; 
   				} else {
   				//	ExcelCommon.writeExcelItem(wb,sheet,weekItem, stockRow, ConstantsInfo.WeekDataType);
   				}
   				//分析月预测值
   				monthItem = getExcelItem(stockFullId,ConstantsInfo.MonthDataType,stockType,fileTime, sdata);
   				if (monthItem == null){
   					stockLogger.logger.fatal("month point is null");
   					//continue; 
   				} else {
   				//	ExcelCommon.writeExcelItem(wb,sheet,monthItem,stockRow, ConstantsInfo.MonthDataType);
   				}
   				
   				//统计
   				statItem = getExcelStatItem(dayItem, weekItem, monthItem);
  				
   				StockExcelTotalInfo setInfo =new StockExcelTotalInfo(soiValue,dayItem,weekItem,monthItem,statItem); 				
   				//停牌 放后面
   				if(enableTingPai!=0 && setInfo.getStatItem()!=null  && setInfo.getStatItem().getDayMixStatItem()!=null) {
   					setInfo.getStatItem().getDayMixStatItem().setComPSState("211211");
   				} 
   					
   				listName.add(toInduStock.getStockName());   				
   				listStockTotalInfoOrderBy.add(setInfo);
   			}
   			  		
   			//记录行业内各股票提示
   			int dealWarns[] = new int[4];
   			// 周涨幅比排序
   			//Collections.sort(listStockTotalInfoOrderBy); 
   			//中文名字排序
   			Collections.sort(listName,Collator.getInstance(java.util.Locale.CHINA));
   			
   			for (int kk=0;kk<listName.size();kk++)
   			{
   			for (int j=0;j<listStockTotalInfoOrderBy.size();j++)	
   			{				
   				StockExcelTotalInfo setInfo = (StockExcelTotalInfo) listStockTotalInfoOrderBy.get(j);
   				if(!listName.get(kk).equals(setInfo.getSoiValue().getName()))
   					continue;
   				stockRow++;   	
   				
   				StockSummary ssu = new StockSummary(0, "","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","");
   				   				
	   			//基本信息
   				ExcelCommon.writeExcelStockOtherInfo(wb, sheet, setInfo.getSoiValue(), stockRow, 1, ssu, flag);
   				
   				//未停牌
   				if (setInfo.getSoiValue().getEnableTingPai() == 0) {
   					statItem = setInfo.getStatItem();
   					
   					//统计 并设置统计值
   					ExcelCommon.writeExcelStatItem(wb,sheet,statItem,stockRow, ssu, flag);				
	   				if (setInfo.getDayItem() != null) {	   					
	   					ExcelCommon.writeExcelItem(wb,sheet,setInfo.getDayItem(), stockRow, ConstantsInfo.DayDataType, ssu, flag);
	   					//记录股票交易提示
		   				dealWarns[setInfo.getDayItem().getScValue().getDealWarn()]++;
	   				}
	   				  				
	   				if (setInfo.getWeekItem() != null)
	   					ExcelCommon.writeExcelItem(wb,sheet,setInfo.getWeekItem(), stockRow, ConstantsInfo.WeekDataType, ssu, flag);
	   				if (setInfo.getMonthItem() != null)
	   					ExcelCommon.writeExcelItem(wb,sheet,setInfo.getMonthItem(),stockRow, ConstantsInfo.MonthDataType, ssu, flag);
	   					   				
	   				//插入summary表 //避免多次写入	
	   				int isTableExist=sdDao.isExistStockTable(ssu.getFullId(),ConstantsInfo.TABLE_SUMMARY_STOCK);
	   				if(isTableExist==0){//不存在
	   					ssDao.createStockSummaryTable(ssu.getFullId());
	   					ssDao.insertStockSummaryTable(ssu.getFullId(), ssu); 
	   				} else {
		   				StockSummary lastSS = ssDao.getZhiDingSummaryFromSummaryTable(ssu.getFullId(), fileTime,ConstantsInfo.DayDataType);
		   				if(lastSS == null) {
		   					stockLogger.logger.fatal("insert into summary table:"+ssu.getFullId());
		   					//统计数据已经在概念那 插入数据
		   					ssDao.insertStockSummaryTable(ssu.getFullId(), ssu); 
		   				}
	   				}
   				} else {
   					stockLogger.logger.fatal("fullid tingpai,no summary");
   				}
   			} 
   			}
   			
   			if(flag){
   				int sub=5;//未知
   	   			if(dealWarns != null && dealWarns.length!=0)
   	   				sub = getMax(dealWarns);
				//买卖提示
				ExcelCommon.writeExcelItemDealWall(wb,sheet,null,sub,titleRow);
				FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);
				wb.write(fileOStream);
				fileOStream.flush();
				fileIStream.close();
				fileOStream.close();   
   			}
   			
   			listStockTotalInfoOrderBy =null;  
			listIndustryStock = null;
          //测试作用
		//	if(stockRow>10)
		//		break;
   		}
 
   		listIndustry = null;
	}
		
	//按概念生成excel orderby
	public void writeExcelFormConceptOrderBy(String filePath, String fileTime, boolean flag) throws SQLException, IOException, ClassNotFoundException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
	{
		     
	 	List<StockConcept> listConcept = new ArrayList<StockConcept>(); 
    	//得到当前所有概念
    	listConcept=sbDao.getStockConcept();
    	System.out.println("概念个数："+listConcept.size());
    	XSSFWorkbook wb=null;
    	XSSFSheet sheet =null;
    	String excleFileName=null;
    	sheetCount =0;
        //每个行业输出到excel一次
        for (int i=0;i<listConcept.size();i++)	
   		{
        	
   			if(i==0 || stockRow >= 510) {
   				
   				//创建日期目录
   				File file = new File(filePath+fileTime);  
   				System.out.println(fileTime);
   				if (!file.exists())
   				{   
   					 file.mkdir();   
   				} 
   				
	   			// 工作区		
	   			wb = new XSSFWorkbook(); 
	   			// 创建第一个sheet     
	   			sheet=  wb.createSheet("allstock");		
	   			sheetCount++;
	   			excleFileName="Stock_Concept_"+fileTime+"_All_"+sheetCount+".xlsx";	   		    
	   		    stockRow = 3;
	   			//创建excel
	   	 		ExcelCommon.createExcel(wb,sheet,filePath,excleFileName);
	   			writeExcelFromMarket(wb,sheet,filePath,excleFileName,fileTime, flag);
	   			FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);;
	   			wb.write(fileOStream);		
	   	        fileOStream.close(); 
	   	        wb=null;
	   	        sheet=null;
   			}
   			
   			excleFileName="Stock_Concept_"+fileTime+"_All_"+sheetCount+".xlsx";
 			
   			File file = new File(filePath +fileTime+ "\\"+excleFileName);
			// 输入流   
			FileInputStream fileIStream = new FileInputStream(file);  
			
			 wb = new XSSFWorkbook(fileIStream);   
			 sheet = wb.getSheetAt(0);  
			// System.out.println("最后一行："+sheet.getLastRowNum());   
			
			StockExcelStatItem  statItem;   		
			
			//当前概念
			 StockConcept concept= listConcept.get(i);	
			 String conceptName=concept.getName();
			 if(conceptName==null)
				 continue;
			 
			stockLogger.logger.fatal("概念："+conceptName);
			System.out.println("概念："+conceptName);	 			
  	
			//概念标题 
			stockRow++;

			//基本面
			StockBaseFace baseFaceCon = new StockBaseFace(0,conceptName,"","","","","","");
			ExcelCommon.writeExcelItemTitle(wb,sheet,i+":"+conceptName,baseFaceCon,stockRow,flag);
			
   					 	
   			//所属概念所有股票
   			List<String> listConceptStock = new ArrayList<String>();     	
   			listConceptStock=sbDao.getConceptStock(conceptName);
   			stockLogger.logger.debug("概念股票数："+listConceptStock.size());
   			
   		//按周涨幅比排序
   			List<StockExcelTotalInfo> listStockTotalInfoOrderBy = new ArrayList<StockExcelTotalInfo>(); 
   			
   			int stockType=0;
   			for(Iterator ie=listConceptStock.iterator();ie.hasNext();)
   			{
   				//stockRow++;
   				String stockFullId = (String) ie.next();
   				System.out.println("stockFullId:"+stockFullId);
   				stockType=sbDao.getMarketType(stockFullId);
   			//	if(!stockFullId.equals("SZ300488"))
   			//		continue;
   				int isTableExist=sdDao.isExistStockTable(stockFullId,ConstantsInfo.TABLE_DATA_STOCK);
   		    	if(isTableExist == 0){//不存在
   					stockLogger.logger.fatal("****stockFullId："+stockFullId+"不存在极值表****");
   					System.out.println(stockFullId+"极值表不存在****");
   					continue;  
   				}
   				stockLogger.logger.fatal("****stockFullId："+stockFullId+"****");
   				
   				StockSingle ss= sbDao.lookUpStockSingle(stockFullId);
   				if(ss == null)
   					continue;
   				StockExcelItem dayItem;
				StockExcelItem weekItem;
				StockExcelItem monthItem;
				
		   		StockData sdata = sdDao.getZhiDingDataStock(stockFullId,ConstantsInfo.DayDataType, fileTime);
				int enableTingPai = getEnableTingPai(stockFullId, fileTime, sdata);
				
				//基本面
				StockBaseFace baseFace = sbDao.lookUpStockBaseFace(stockFullId);
				StockBaseYearInfo yearInfo = sbDao.lookUpStockBaseYearInfo(stockFullId);
				
				//其他值
		   		StockOtherInfoValue soiValue=new StockOtherInfoValue(stockFullId,ss.getStockName(),ss.getEnableMarginTrading(),enableTingPai,baseFace,yearInfo);
		   		//ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow);	

   				//分析日预测值
   				dayItem = getExcelItem(stockFullId,ConstantsInfo.DayDataType,stockType, fileTime, sdata);   
   				if (dayItem == null){
   					stockLogger.logger.fatal("day point is null");
   					//continue;
   				} else {
   					//ExcelCommon.writeExcelItem(wb,sheet,dayItem, stockRow, ConstantsInfo.DayDataType);
   				}
   				//分析周预测值
   				weekItem = getExcelItem(stockFullId,ConstantsInfo.WeekDataType,stockType,fileTime,sdata);
   				if (weekItem == null){
   					stockLogger.logger.fatal("week point is null");
   					//continue; 
   				} else {
   					//ExcelCommon.writeExcelItem(wb,sheet,weekItem, stockRow, ConstantsInfo.WeekDataType);
   				}
   				//分析月预测值
   				monthItem = getExcelItem(stockFullId,ConstantsInfo.MonthDataType,stockType,fileTime,sdata);
   				if (monthItem == null){
   					stockLogger.logger.fatal("month point is null");
   					//continue; 
   				} else {
   					//ExcelCommon.writeExcelItem(wb,sheet,monthItem,stockRow, ConstantsInfo.MonthDataType);
   				}
   				
   				//统计  
   				statItem = getExcelStatItem(dayItem, weekItem, monthItem);
   				StockExcelTotalInfo setInfo =new StockExcelTotalInfo(soiValue,dayItem,weekItem,monthItem,statItem);
   				listStockTotalInfoOrderBy.add(setInfo);
   				
   			}
   			
   			//排序
   			Collections.sort(listStockTotalInfoOrderBy); 
   		           
   			for (int j=0;j<listStockTotalInfoOrderBy.size();j++)	
   			{
   				StockExcelTotalInfo setInfo = (StockExcelTotalInfo) listStockTotalInfoOrderBy.get(j);
   				stockRow++;
   				
   				StockSummary ssu = new StockSummary(0,"","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","");
   				
   				ExcelCommon.writeExcelStockOtherInfo(wb, sheet, setInfo.getSoiValue(), stockRow,1, ssu, flag);
   				if (setInfo.getDayItem() != null) {
   					ExcelCommon.writeExcelItem(wb,sheet,setInfo.getDayItem(), stockRow, ConstantsInfo.DayDataType, ssu, flag);
   					//ExcelCommon.writeExcelStatItem(wb,sheet,setInfo.getStatItem(), stockRow);
   				}
   				if (setInfo.getWeekItem() != null)
   					ExcelCommon.writeExcelItem(wb,sheet,setInfo.getWeekItem(), stockRow, ConstantsInfo.WeekDataType, ssu, flag);
   				if (setInfo.getMonthItem() != null)
   					ExcelCommon.writeExcelItem(wb,sheet,setInfo.getMonthItem(),stockRow, ConstantsInfo.MonthDataType, ssu, flag);
   				
   				//避免多次写入	
   				StockSummary lastSS = ssDao.getZhiDingSummaryFromSummaryTable(ssu.getFullId(), fileTime,ConstantsInfo.DayDataType);
   				if(lastSS == null) {
   					ssDao.insertStockSummaryTable(ssu.getFullId(), ssu); 
   				}
   			}
   			
   			listStockTotalInfoOrderBy =null;
   			
			FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);;
			wb.write(fileOStream);
			fileOStream.flush();
			fileIStream.close();
			fileOStream.close();              
			  
			listConceptStock = null;
          //测试作用
		//	if(stockRow>30)
		//		break;
   		}
   		
        listConcept = null;
	}
	
	//数组中最大值对应下标
	public static int getMax(int[] arr)
	{
		int max = arr[0];	
		int sub =0;
		for(int x=0;x<arr.length;x++)
		{
			if(arr[x]> max) {
				max = arr[x];	
				sub = x;
			}
		}

		return sub;	
	}
	
	//分级买卖
    public static String getBuySaleGrade(StockExcelItem eDayItem,StockExcelItem eWeekItem,StockExcelItem eMonthItem,int dataType)
    {
    	String desc = "";
    	int largePSTrend=0;//
    	int smallPSTrend=0;
    	float ratio=0;
    	
    	//日分级买卖 通过日、周数据计算
    	if (dataType == ConstantsInfo.DayDataType){
    		if(eDayItem ==null || eWeekItem==null)
        		return "";
    		
    		if(eDayItem.getScValue().getTread() == 0){  
    			smallPSTrend = 0; //日极疑趋势
        	} else {
        		smallPSTrend = 1;
        	}
        	
        	if(eWeekItem.getScValue().getTread() == 0){  
        		largePSTrend = 0; //周极疑趋势
        	} else {
        		largePSTrend = 1;
        	}
        	
        	if(eWeekItem.getScValue().getPointSuspectedDateGap() == 0){
        		ratio = 2; //大于1
        	} else {
        		ratio = (float) eWeekItem.getScValue().getSuspectedCurDateGap()/eWeekItem.getScValue().getPointSuspectedDateGap();
        	}
    		
    	} else {
    		
    		if(eWeekItem ==null || eMonthItem==null)
    			return "";
    		
    		if(eWeekItem.getScValue().getTread() == 0){  
    			smallPSTrend = 0; //日极疑趋势
        	} else {
        		smallPSTrend = 1;
        	}
        	
        	if(eMonthItem.getScValue().getTread() == 0){  
        		largePSTrend = 0; //周极疑趋势
        	} else {
        		largePSTrend = 1;
        	}
        	
        	if(eMonthItem.getScValue().getPointSuspectedDateGap() == 0){
        		ratio = 2; //大于1
        	} else {
        		ratio = (float) eMonthItem.getScValue().getSuspectedCurDateGap()/eMonthItem.getScValue().getPointSuspectedDateGap();
        	}
    	}
    	

    	/*
    	System.out.println("dayCurTrend:"+eDayItem.getScValue().getTread());
    	System.out.println("dayPSTrend:"+dayPSTrend);
    	
      	System.out.println("weekCurTrend:"+eWeekItem.getScValue().getTread());
    	System.out.println("weekPSTrend:"+weekPSTrend);
    	System.out.println("weekSCDate:"+eWeekItem.getScValue().getSuspectedCurDateGap());
    	System.out.println("dayPSDate:"+eWeekItem.getScValue().getPointSuspectedDateGap());
      	System.out.println("ratioWeek:"+ratioWeek);
   	*/
   
    	
    	//周跌
    	if(largePSTrend == 0 ) {
    		//日跌
    		if(smallPSTrend ==0){
    			
    			if(ratio<=0.382)
					desc="一级买入";
				else if(ratio<=0.618)
					desc="二级买入";
				else if(ratio<=1)
					desc="三级买入";
				else
					desc="四级买入";
    			
    		}else{
    			
    			if(ratio<=0.382)
					desc="四级卖出";
				else if(ratio<=0.618)
					desc="三级卖出";
				else if(ratio<=1)
					desc="二级买入";
				else
					desc="一级卖出";
    		}
    		
    	} else {
    		//日涨
    		if(smallPSTrend == 1){
    			
    			if(ratio<=0.382)
    				desc="一级卖出";
    			else if(ratio<=0.618)
    				desc="二级卖出";
    			else if(ratio<=1)
    				desc="三级卖出";
    			else
    				desc="四级卖出";
    		} else {
    			
    			if(ratio<=0.382)
					desc="四级买入";
				else if(ratio<=0.618)
					desc="三级买入";
				else if(ratio<=1)
					desc="二级买入";
				else
					desc="一级买入";
    		}
    	}
    			
	   return desc;	
    }
    
    //当前上涨还是下跌趋势
    public int getTread(StockExcelItem item)
    {
    	if(item.getScValue().getTread() == 0){  
			return  0; //日极疑趋势 跌
    	} else {
    		return 1;
    	}
    }
    
    //极点状态组合
    public String getPriPStateValue(StockExcelItem eDayItem,StockExcelItem eWeekItem,StockExcelItem eMonthItem,int type)
    {
    	String monthPSStateDesc="月跌";//极疑点位比状态
    	String weekPSStateDesc="周跌";//极疑点位比状态
    	String dayPSStateDesc="日跌";//极疑点位比状态
    	
    	int dayPSValue=0;//极疑点位比状态
    	int weekPSValue=0;//极疑点位比状态
    	int monthPSValue=0;//极疑点位比状态
    	String comState = "";
    	if(eDayItem!=null) {
    		dayPSValue = eDayItem.getScValue().getPointSuspectedValueGap()<0?1:0;
    		if(dayPSValue == 1){
    			dayPSStateDesc = "日跌";
    		} else {
    			dayPSStateDesc = "日涨";
    		}
    	}
    	 
    	if(eWeekItem!=null){
    		weekPSValue = eWeekItem.getScValue().getPointSuspectedValueGap()<0?1:0;
    		if(weekPSValue == 1){
    			weekPSStateDesc = "周跌";
    		} else {
    			weekPSStateDesc = "周涨";
    		}
    	}
    		
    	if(eMonthItem!=null){
    		monthPSValue = eMonthItem.getScValue().getPointSuspectedValueGap()<0?1:0;
    		if(monthPSValue == 1){
    			monthPSStateDesc = "月跌";
    		} else {
    			monthPSStateDesc = "月涨";
    		}
    	}
    	
    	if (type == 1 ){
    		comState = monthPSStateDesc+":"+weekPSStateDesc+":"+dayPSStateDesc;
    	} else {
    		comState = String.valueOf(monthPSValue)+String.valueOf(weekPSValue)+String.valueOf(dayPSValue);
    	}
    	return comState;
    	
    }
    
    
    //疑似极点状态组合
    public String getPSStateValue(StockExcelItem eDayItem,StockExcelItem eWeekItem,StockExcelItem eMonthItem,int type)
    {
    	String monthPSStateDesc="月跌";//极疑点位比状态
    	String weekPSStateDesc="周跌";//极疑点位比状态
    	String dayPSStateDesc="日跌";//极疑点位比状态
    	
    	int dayPSValue=0;//极疑点位比状态
    	int weekPSValue=0;//极疑点位比状态
    	int monthPSValue=0;//极疑点位比状态
    	String comState = "";
    	if(eDayItem!=null) {
    		dayPSValue = eDayItem.getScValue().getPointSuspectedValueGap()<0?1:0;
    		if(dayPSValue == 1){
    			dayPSStateDesc = "日涨";
    		} else {
    			dayPSStateDesc = "日跌";
    		}
    	}
    	 
    	if(eWeekItem!=null){
    		weekPSValue = eWeekItem.getScValue().getPointSuspectedValueGap()<0?1:0;
    		if(weekPSValue == 1){
    			weekPSStateDesc = "周涨";
    		} else {
    			weekPSStateDesc = "周跌";
    		}
    	}
    		
    	if(eMonthItem!=null){
    		monthPSValue = eMonthItem.getScValue().getPointSuspectedValueGap()<0?1:0;
    		if(monthPSValue == 1){
    			monthPSStateDesc = "月涨";
    		} else {
    			monthPSStateDesc = "月跌";
    		}
    	}
    	
    	if (type == 1 ){
    		comState = monthPSStateDesc+":"+weekPSStateDesc+":"+dayPSStateDesc;
    	} else {
    		comState = String.valueOf(monthPSValue)+String.valueOf(weekPSValue)+String.valueOf(dayPSValue);
    	}
    	return comState;
    	
    }
    
    //极点状态组合
    public String getPriPointState(StockExcelItem eDayItem,StockExcelItem eWeekItem,StockExcelItem eMonthItem) throws IOException, ClassNotFoundException, SQLException
    {
    	int monthTrend=0;
    	int weekPointNum = 0;
    	int dayPointNum = 0;
    	String psState="";
    	float monthPSValueGap=0;//极疑点位比
    	String fullId ="";
    	
    	if(eDayItem != null){
    		fullId= eDayItem.getFullId();
    	}
    	
    	if(eMonthItem == null) {
    		monthTrend = 0; //涨
    		weekPointNum = 0;
    	} else {
    		monthPSValueGap = eMonthItem.getScValue().getPointSuspectedValueGap();
    		if(monthPSValueGap < 0)
    			monthTrend = 0;
    		else 
    			monthTrend = 1;
    		//monthTrend = getTread(eMonthItem);
    		
    		//前极点时间
	    	String sMonthDate = eMonthItem.getScValue().getStartDate();
	    	weekPointNum = spDao.getUpOrDownPointNum(fullId,ConstantsInfo.WeekDataType,sMonthDate);
    	}
    	
    	
    	if(eWeekItem == null){
    		dayPointNum = 1;
    	} else {
    		//前极点时间
    		String sWeekDate = eWeekItem.getScValue().getStartDate();
	    	dayPointNum = spDao.getUpOrDownPointNum(fullId,ConstantsInfo.DayDataType,sWeekDate);
	    	//System.out.println("sWeekDate:"+sWeekDate);
    	}
    	   	
    	//System.out.println(monthTrend+":"+weekPointNum+":"+dayPointNum);
    	psState = String.valueOf(monthTrend) +":"+ String.valueOf(weekPointNum)+":"+String.valueOf(dayPointNum);
    	
    	return psState;
    }
    
    //疑似极点状态组合
    public String getPSState(StockExcelItem eDayItem,StockExcelItem eWeekItem,StockExcelItem eMonthItem) throws IOException, ClassNotFoundException, SQLException
    {
    	int monthTrend=0;
    	int weekPointNum = 0;
    	int dayPointNum = 0;
    	String psState="";
    	float monthPSValueGap=0;//极疑点位比
    	float weekPSValueGap=0;//极疑点位比
    	int dayPSValueGap=0;//极疑点位比
    	String fullId ="";
    	
    	if(eDayItem != null){
    		fullId= eDayItem.getFullId();
    	}
    	
    	if(eMonthItem == null) {
    		monthTrend = 1; //涨
    		weekPointNum = 1;
    	} else {
    		monthPSValueGap = eMonthItem.getScValue().getPointSuspectedValueGap();
    		if(monthPSValueGap < 0)
    			monthTrend = 1;
    		else 
    			monthTrend =0;
    		//monthTrend = getTread(eMonthItem);
    		
    		//疑似点
	    	String sMonthDate = eMonthItem.getScValue().getEndDate();
	    	weekPointNum = spDao.getUpOrDownPointNum(fullId,ConstantsInfo.WeekDataType,sMonthDate);
    	}
    	
    	
    	if(eWeekItem == null){
    		dayPointNum = 1;
    	} else {
    		String sWeekDate = eWeekItem.getScValue().getEndDate();
	    	dayPointNum = spDao.getUpOrDownPointNum(fullId,ConstantsInfo.DayDataType,sWeekDate);
	    	//System.out.println("sWeekDate:"+sWeekDate);
    	}
    	   	
    	//System.out.println(monthTrend+":"+weekPointNum+":"+dayPointNum);
    	psState = String.valueOf(monthTrend) + String.valueOf(weekPointNum)+String.valueOf(dayPointNum);
    	
    	return psState;
    }
    //股票状态
    public String getBuySaleState(StockExcelItem eDayItem,StockExcelItem eWeekItem,StockExcelItem eMonthItem,int dataType) throws IOException, ClassNotFoundException, SQLException
    {
    	int largePSTrend=0;//
    	int smallPSTrend=0;
    	String desc ="";
    	String startDate =null;
    	String endDate =null;
    	String fullId =null;
    	int countI =0;
    	float spPri=0;
    	float spNext=0;
    	int flag = 0;
    	List<Float> spList = new ArrayList<Float>();
    	
    	//日  通过日、周数据计算
    	if (dataType == ConstantsInfo.DayDataType){
    		if(eDayItem ==null || eWeekItem==null)
        		return "";
    		
    		if(eDayItem.getScValue().getTread() == 0){  
    			smallPSTrend = 0; //日极疑趋势
        	} else {
        		smallPSTrend = 1;
        	}
        	
        	if(eWeekItem.getScValue().getTread() == 0){  
        		largePSTrend = 0; //周极疑趋势
        	} else {
        		largePSTrend = 1;
        	}
        	
        	//周疑极时间作为开始时间 检查日的极点信息
        	startDate = eWeekItem.getScValue().getEndDate();
        	endDate = eWeekItem.getScValue().getCurDate(); 
        	fullId = eWeekItem.getFullId();
    	
    	} else {
    		
    		if(eWeekItem ==null || eMonthItem==null)
    			return "";
    		
    		if(eWeekItem.getScValue().getTread() == 0){  
    			smallPSTrend = 0; //日极疑趋势
        	} else {
        		smallPSTrend = 1;
        	}
        	
        	if(eMonthItem.getScValue().getTread() == 0){  
        		largePSTrend = 0; //周极疑趋势
        	} else {
        		largePSTrend = 1;
        	}
        	
        	//月疑极时间作为开始时间 检查周的极点信息
        	startDate = eMonthItem.getScValue().getEndDate();
        	endDate = eMonthItem.getScValue().getCurDate(); 
        	fullId = eWeekItem.getFullId();
    	}
    	
    	///System.out.println("startDate:"+startDate);
    //	System.out.println("trend:"+largePSTrend);
    	
    	if(largePSTrend == 0) {
    		spList = spDao.getUpOrDownPoint(fullId , dataType, startDate, 1); //计算出最大值
    		//增加当前疑似点
    		if (smallPSTrend == 1)
    			spList.add(eDayItem.getScValue().getEndValue());
    	} else {
			spList = spDao.getUpOrDownPoint(fullId , dataType, startDate, 0); //计算最小值
			//增加当前疑似点
    		if (smallPSTrend == 0)
    			spList.add(eDayItem.getScValue().getEndValue());
    	}
    	
		//System.out.println("spList size:"+spList.size());
		if(spList.size()<=1){
			if(largePSTrend == 0)
				desc="连续下跌";
			else
				desc="连续上涨";
			
		} else {
			spPri = spList.get(0);
			//System.out.println("first:"+spPri);
    		for (countI=1; countI<spList.size();countI++){
    			spNext = spList.get(countI);
    			//System.out.println("next:"+spNext);
    			if(largePSTrend == 0) {//下跌
	    			if(spNext>spPri){
	    				flag = 1;
	    				break;
	    			}
    			} else {
    				if(spNext<spPri){
	    				flag = 1;
	    				break;
	    			}
    			}
    			;
    			spPri = spNext; 
    	   	}
    		
    		if(flag == 1) {
    			
    			if (dataType == ConstantsInfo.DayDataType) {
    				if(largePSTrend == 0)
	    				desc ="周底部";
	    			else 
	    				desc ="周顶部";
    			} else {
    				if(largePSTrend == 0)
	    				desc ="月底部";
	    			else 
	    				desc ="月顶部";
    			}
    			
    		} else {
    			
    			if(largePSTrend == 0)
    				desc ="底部盘整";
    			else 
    				desc ="顶部盘整";
    		}
		}
	    	
    //	System.out.println(desc);
    	return desc;
    }
	
    //计算出统计项中需要混合计算的项
	StockMixStatValue getMixStatValue(StockExcelItem eDayItem,StockExcelItem eWeekItem,StockExcelItem eMonthItem,int dataType) throws IOException, ClassNotFoundException, SQLException
	{
		StockMixStatValue sMixValue;
		
		String priComStateDesc=""; //前极点状态组合对应描述
		String priComStateValue=""; //前极点状态组合对应数字
		String priState=""; //前极点组合
	
		String combStateDesc=""; //状态组合对应描述
		String combStateValue=""; //状态组合对应数字
		String comPSState="";//状态与极点两者结合的数字，方便排序
		String pSState=""; //疑似极点组合
		
		
		String buySaleGrade=""; //分级买卖
		String buySaleState=""; //状态
		
		buySaleGrade = getBuySaleGrade(eDayItem,eWeekItem,eMonthItem,dataType);
		buySaleState = getBuySaleState(eDayItem,eWeekItem,eMonthItem,dataType);
			
		priState= getPriPointState(eDayItem,eWeekItem,eMonthItem);
		priComStateDesc = getPriPStateValue(eDayItem,eWeekItem,eMonthItem,1);	
		//priComStateValue = getPriPStateValue(eDayItem,eWeekItem,eMonthItem,0);
		
		pSState= getPSState(eDayItem,eWeekItem,eMonthItem);
		combStateDesc = getPSStateValue(eDayItem,eWeekItem,eMonthItem,1);	
		combStateValue = getPSStateValue(eDayItem,eWeekItem,eMonthItem,0);
		
		//为排序
		comPSState = combStateValue+pSState;
		
		//System.out.println("comPSState:"+comPSState);
		sMixValue = new StockMixStatValue(priComStateDesc,priState,comPSState,combStateDesc,pSState,buySaleGrade,buySaleState);

		return sMixValue;
	}
	
	
	StockExcelStatItem getExcelStatItem(StockExcelItem eDayItem,StockExcelItem eWeekItem,StockExcelItem eMonthItem)
	{
		StockExcelStatItem statItem;
		
		StockMixStatValue dayMixValue=null;
		StockMixStatValue weekMixValue=null;
		
		//先计算日
		try {
			dayMixValue = getMixStatValue(eDayItem,eWeekItem,eMonthItem,ConstantsInfo.DayDataType);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//再计算周
		try {
			weekMixValue = getMixStatValue(eDayItem,eWeekItem,eMonthItem,ConstantsInfo.WeekDataType);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StockMinMaxValue minMaxItem = null;
		if (eDayItem != null){
			minMaxItem = new StockMinMaxValue(eDayItem.getScValue().getMinDate(),eDayItem.getScValue().getMinPrice(),
					eDayItem.getScValue().getMaxDate(),eDayItem.getScValue().getMaxPrice(),eDayItem.getScValue().getMinMaxRatio());		
		}
	
		//统计
		if (eDayItem == null){
			statItem = new StockExcelStatItem(null, null,null,null,null,null);   					
		} else if (eWeekItem == null){
			statItem = new StockExcelStatItem(minMaxItem, eDayItem.getSsValue(),dayMixValue,
					null,null,null);
			
		} else if (eMonthItem == null) {
			statItem = new StockExcelStatItem(minMaxItem, eDayItem.getSsValue(),dayMixValue,
					eWeekItem.getSsValue(),weekMixValue,null);
		} else {
			statItem = new StockExcelStatItem(minMaxItem, eDayItem.getSsValue(),dayMixValue,
					eWeekItem.getSsValue(),weekMixValue,eMonthItem.getSsValue());
		}
		return statItem;
	}
	
	public void setHighestLowestPrice(StockBaseFace baseFace, String stockFullId) throws IOException, ClassNotFoundException, SQLException
	{
		if(baseFace!=null){
			//将最大值 最小值代替 潜力
			float hightestPrice  = sdDao.getHighestPrice(stockFullId);
			float lowestPrice = sdDao.getLowestPrice(stockFullId);
			float rate=hightestPrice/lowestPrice;
			String p = decimalFormat.format(rate);
			String potential = Float.toString(hightestPrice)+"/"+Float.toString(lowestPrice)+"="+p;
			baseFace.setPotential(potential);
		}
	}
	
	//按概念在一级行业下生成excel orderby
	public void writeExcelFormConceptInFirstIndustryOrderBy(String filePath, String fileTime, boolean flag) throws SQLException, IOException, ClassNotFoundException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
	{
		     
	 	List<StockConceptInFirstIndustry> listConcept = new ArrayList<StockConceptInFirstIndustry>(); 
	 	List<String> listFirstIndustry = new ArrayList<String>(); 
    	//得到当前一级行业code
	 	listFirstIndustry=sbDao.getStockFirstIndustry();    	
	 	DecimalFormat decimalFormat=new DecimalFormat(".00");
    	XSSFWorkbook wb=null;
    	XSSFSheet sheet =null;
    	String excleFileName=null;
        int flag_first=0;
        int titleRow =0;//记录行业或概念标题行
        sheetCount =0;
    	//先输出到一级行业
        for (int countI=0; countI<listFirstIndustry.size();countI++)	
   		{
        	//得到当前一级行业下概念code
        	String firstIndustryId = listFirstIndustry.get(countI);
        	
        	//一级行业名称
        	String firstIndustryName = sbDao.getStockFirstIndustryName(firstIndustryId);        	
        	System.out.println("一级行业："+firstIndustryName);        	
        	
        	//得到概念id
        	listConcept = sbDao.getStockFirstIndustryConceptCode(firstIndustryId);
        	
	    	//每个行业输出到excel一次
	        for (int i=0;i<listConcept.size();i++)	
	   		{
	        	//countI第一次	        	
	   			if(flag_first == 0 || stockRow >= 510) {
	   				
	   				flag_first =1;
	   				//创建日期目录
	   				File file = new File(filePath+fileTime);  
	   				System.out.println(fileTime);
	   				if (!file.exists())
	   				{   
	   					 file.mkdir();   
	   				} 
	   				
		   			// 工作区		
		   			wb = new XSSFWorkbook(); 
		   			// 创建第一个sheet     
		   			sheet=  wb.createSheet("allstock");		
		   			sheetCount++;
		   			excleFileName="Stock_Concept_"+fileTime+"_All_"+sheetCount+".xlsx";	   		    
		   		    stockRow = 1;
		   			//创建excel
		   	 		ExcelCommon.createExcel(wb,sheet,filePath,excleFileName);
		   			writeExcelFromMarket(wb,sheet,filePath,excleFileName,fileTime, flag);
		   			FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);;
		   			wb.write(fileOStream);		
		   	        fileOStream.close(); 
		   	        wb=null;
		   	        sheet=null;
	   			}
	   			
	   			StockExcelStatItem  statItem;   	
	   			
	   			excleFileName="Stock_Concept_"+fileTime+"_All_"+sheetCount+".xlsx";
	 			
	   			File file = new File(filePath +fileTime+ "\\"+excleFileName);
				// 输入流   
				FileInputStream fileIStream = new FileInputStream(file);  	
				wb = new XSSFWorkbook(fileIStream);   
				sheet = wb.getSheetAt(0);  
 
				//当前一级行业
				StockConceptInFirstIndustry scon= listConcept.get(i);	
				String conceptCode = scon.getConceptCode();
				String conceptName = scon.getConceptName();
				if(conceptName==null)
					continue;
				 
				System.out.println("概念："+conceptName);
				//if(!conceptName.equals("黄金珠宝"))
	   			//	continue;
				
				stockLogger.logger.fatal("概念："+conceptName);
				//概念标题 
				stockRow++;
				titleRow = stockRow;
				
				//基本面
				StockBaseFace baseFaceConcept = new StockBaseFace(0,conceptCode,scon.getBaseExpect(),scon.getMain(),
						scon.getPsychology(),scon.getRisk(),scon.getPotential(),scon.getFaucet());
				ExcelCommon.writeExcelItemTitle(wb,sheet,i+":"+firstIndustryName,baseFaceConcept,stockRow,flag);
	   					 	
	   			//所属概念所有股票
	   			List<StockToConcept> listConceptStock = new ArrayList<StockToConcept>();
	   			listConceptStock=sbDao.getStockToConcept(conceptCode);
	   			stockLogger.logger.debug("概念股票数："+listConceptStock.size());
	   			
	   			//按周涨幅比排序
	   			List<StockExcelTotalInfo> listStockTotalInfoOrderBy = new ArrayList<StockExcelTotalInfo>(); 
	   			List<String> listName = new ArrayList<String>(); 
	   			int stockType=0;
	   			for(Iterator ie=listConceptStock.iterator();ie.hasNext();)
	   			{
	   				//stockRow++;
	   				StockToConcept toConstock =(StockToConcept) ie.next();
	   				String stockFullId = toConstock.getStockFullId();
	   			
	   				stockType=sbDao.getMarketType(stockFullId);
	   				//if(!stockFullId.equals("SZ600837"))
	   				//	continue;
	   				
	   				System.out.println("stockFullId:"+stockFullId);
	   				int isTableExist=sdDao.isExistStockTable(stockFullId,ConstantsInfo.TABLE_DATA_STOCK);
	   		    	if(isTableExist == 0){//不存在
	   					stockLogger.logger.fatal("****stockFullId："+stockFullId+"不存在极值表****");
	   					System.out.println(stockFullId+"极值表不存在****");
	   					continue;  
	   				}
	   				stockLogger.logger.fatal("****stockFullId："+stockFullId+"****");
	   				
	   				StockExcelItem dayItem;
					StockExcelItem weekItem;
					StockExcelItem monthItem;
					
					StockData sdata = sdDao.getZhiDingDataStock(stockFullId,ConstantsInfo.DayDataType, fileTime);
					int enableTingPai = getEnableTingPai(stockFullId, fileTime,sdata);
	
					//基本面
					StockBaseFace baseFace = sbDao.lookUpStockBaseFace(stockFullId);
					//setHighestLowestPrice(baseFace,stockFullId);		
					StockBaseYearInfo yearInfo = sbDao.lookUpStockBaseYearInfo(stockFullId);
			
					//是否两融
					int enableTwoRong = sbDao.lookUpStockTwoRong(stockFullId);	
					//其他值
			   		StockOtherInfoValue soiValue=new StockOtherInfoValue(stockFullId,toConstock.getStockName(),enableTwoRong,enableTingPai,baseFace,yearInfo);
			   		//ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow);
			   		
	   				//分析日预测值
	   				dayItem = getExcelItem(stockFullId,ConstantsInfo.DayDataType,stockType, fileTime,sdata);   
	   				if (dayItem == null){
	   					stockLogger.logger.fatal("day point is null");
	   					//continue;
	   				} else {
	   					//ExcelCommon.writeExcelItem(wb,sheet,dayItem, stockRow, ConstantsInfo.DayDataType);
	   				}
	   				//分析周预测值
	   				weekItem = getExcelItem(stockFullId,ConstantsInfo.WeekDataType,stockType, fileTime,sdata);
	   				if (weekItem == null){
	   					stockLogger.logger.fatal("week point is null");
	   					//continue; 
	   				} else {
	   					//ExcelCommon.writeExcelItem(wb,sheet,weekItem, stockRow, ConstantsInfo.WeekDataType);
	   				}
	   				//分析月预测值
	   				monthItem = getExcelItem(stockFullId,ConstantsInfo.MonthDataType,stockType, fileTime, sdata);
	   				if (monthItem == null){
	   					stockLogger.logger.fatal("month point is null");
	   					//continue; 
	   				} else {
	   					//ExcelCommon.writeExcelItem(wb,sheet,monthItem,stockRow, ConstantsInfo.MonthDataType);
	   				}
	   				
	   				//计算出统计项
	   				statItem = getExcelStatItem(dayItem, weekItem, monthItem);
	   				
	   				StockExcelTotalInfo setInfo =new StockExcelTotalInfo(soiValue,dayItem,weekItem,monthItem,statItem);
	   				
	   				//停牌 放后面 			
	   				if(enableTingPai!=0 && setInfo.getStatItem()!=null  && setInfo.getStatItem().getDayMixStatItem()!=null) {
	   					setInfo.getStatItem().getDayMixStatItem().setComPSState("211211");
	   				} 
	   					
	   				listStockTotalInfoOrderBy.add(setInfo);
	   				listName.add(toConstock.getStockName()); 
	   			}
	   			//记录行业内各股票提示
	   			int dealWarns[] = new int[4];
	   			//排序
	   			//Collections.sort(listStockTotalInfoOrderBy); 
	   			Collections.sort(listName,Collator.getInstance(java.util.Locale.CHINA));
	   			
	   			for (int kk=0;kk<listName.size();kk++)
	   			{
	   			for (int j=0;j<listStockTotalInfoOrderBy.size();j++)	
	   			{
	   				StockExcelTotalInfo setInfo = (StockExcelTotalInfo) listStockTotalInfoOrderBy.get(j);
	   				if(!listName.get(kk).equals(setInfo.getSoiValue().getName()))
	   					continue;
	   				stockRow++;
	   				stockLogger.logger.fatal("**write**stockFullId："+setInfo.getSoiValue().getFullId()+"****");
	   				System.out.println("**write**stockFullId："+setInfo.getSoiValue().getFullId()+"****");
	   				
	   				StockSummary ssu = new StockSummary(0,"","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","");
	   				
	   				ExcelCommon.writeExcelStockOtherInfo(wb, sheet, setInfo.getSoiValue(), stockRow,1, ssu, flag);
	   				//未停牌
	   				if (setInfo.getSoiValue().getEnableTingPai() == 0) {
	   					
	   					statItem = setInfo.getStatItem();	   				
	   						
	   					//统计 并设置统计值
	   					ExcelCommon.writeExcelStatItem(wb,sheet,statItem,stockRow, ssu, flag);
	   						
		   				if (setInfo.getDayItem() != null) {
		   					ExcelCommon.writeExcelItem(wb,sheet,setInfo.getDayItem(), stockRow, ConstantsInfo.DayDataType, ssu, flag);
		   					//记录股票交易提示
			   				dealWarns[setInfo.getDayItem().getScValue().getDealWarn()]++;
		   				}
		   				if (setInfo.getWeekItem() != null)
		   					ExcelCommon.writeExcelItem(wb,sheet,setInfo.getWeekItem(), stockRow, ConstantsInfo.WeekDataType, ssu, flag);
		   				if (setInfo.getMonthItem() != null)
		   					ExcelCommon.writeExcelItem(wb,sheet,setInfo.getMonthItem(),stockRow, ConstantsInfo.MonthDataType, ssu, flag);
		   				
		   				//行业已经写入， 概念不需要再次写入
		   			///	ssDao.insertStockSummaryTable(ssu.getFullId(), ssu);
		   			}
	   			}
	   			}
	   			
	   			int sub=5;//未知
	   			if(dealWarns != null && dealWarns.length!=0)
	   				sub = getMax(dealWarns);	
	   			
	   			//行业或概念对应的买卖提示
	   			ExcelCommon.writeExcelItemDealWall(wb,sheet,conceptName,sub,titleRow);
	   			listStockTotalInfoOrderBy =null;
	   			
				FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);;
				wb.write(fileOStream);
				fileOStream.flush();
				fileIStream.close();
				fileOStream.close();              
				  
				listConceptStock = null;	          
				 //测试作用
				//if(stockRow>20)
				//	break;				
	   		
	   		}
	        
	      //测试作用
			//if(stockRow>20)
			//	break;	        
   		}
   		
        listConcept = null;
	}
	

	
	//按概念在一级行业下生成excel orderby
	public void writeSummaryExcelFormConceptInFirstIndustryOrderBy(String filePath, String fileTime) throws SQLException, IOException, ClassNotFoundException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
	{
		List<StockConceptInFirstIndustry> listConcept = new ArrayList<StockConceptInFirstIndustry>(); 
	 	List<String> listFirstIndustry = new ArrayList<String>(); 
	
    	//得到当前一级行业code
	 	listFirstIndustry=sbDao.getStockFirstIndustry();    	
    	
    	XSSFWorkbook wb=null;
    	XSSFSheet sheet =null;
    	String excleFileName=null;
        int flag_first=0;
        int titleRow =0;//记录行业或概念标题行
        sheetCount =0;
    	//先输出到一级行业
        for (int countI=0; countI<listFirstIndustry.size();countI++)	
   		{
        	//得到当前一级行业下概念code
        	String firstIndustryId = listFirstIndustry.get(countI);
        	
        	//一级行业名称
        	String firstIndustryName = sbDao.getStockFirstIndustryName(firstIndustryId);        	
        	System.out.println("一级行业："+firstIndustryName);        	
        	
        	//得到概念id
        	listConcept = sbDao.getStockFirstIndustryConceptCode(firstIndustryId);
        	
	    	//每个行业输出到excel一次
	        for (int i=0;i<listConcept.size();i++)	
	   		{
	        	//countI第一次	        	
	   			if(flag_first == 0 || stockRow >= 510) {
	   				
	   				flag_first =1;
	   				//创建日期目录
	   				File file = new File(filePath+fileTime);  
	   				System.out.println(fileTime);
	   				if (!file.exists())
	   				{   
	   					 file.mkdir();   
	   				} 
	   				
		   			// 工作区		
		   			wb = new XSSFWorkbook(); 
		   			// 创建第一个sheet     
		   			sheet=  wb.createSheet("allstock");		
		   			sheetCount++;
		   			excleFileName="Stock_Concept_"+fileTime+"_Summary_"+sheetCount+".xlsx";	   		    
		   		    stockRow = 1;
		   			//创建excel
		   	    	stockDateColumnmap.clear();
		   	 		ExcelCommon.createSummaryExcel(wb,sheet,filePath,excleFileName,sdDao,stockDateColumnmap);
		   	 		writeSummaryExcelFromMarket(wb,sheet,filePath,excleFileName,fileTime);
		   			FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);;
		   			wb.write(fileOStream);		
		   	        fileOStream.close(); 
		   	        wb=null;
		   	        sheet=null;
	   			}
	   			
	   			StockExcelStatItem  statItem;   	
	   			
	   			excleFileName="Stock_Concept_"+fileTime+"_Summary_"+sheetCount+".xlsx";
	 			
	   			File file = new File(filePath +fileTime+ "\\"+excleFileName);
				// 输入流   
				FileInputStream fileIStream = new FileInputStream(file);  	
				wb = new XSSFWorkbook(fileIStream);   
				sheet = wb.getSheetAt(0);  
 
				//当前一级行业
				StockConceptInFirstIndustry scon= listConcept.get(i);	
				 String conceptCode = scon.getConceptCode();
				 String conceptName=scon.getConceptName();
				 if(conceptName==null)
					 continue;
				 
				System.out.println("概念："+conceptName);
				//if(!conceptName.equals("黄金珠宝"))
	   			//	continue;
				
				stockLogger.logger.fatal("概念："+conceptName);			
	  	
				//概念标题 
				stockRow++;
				titleRow = stockRow;
				/*
				//基本面
				StockBaseFace baseFaceConcept = new StockBaseFace(0,conceptCode,scon.getBaseExpect(),scon.getMain(),
						scon.getPsychology(),scon.getRisk(),scon.getPotential(),scon.getFaucet());
				ExcelCommon.writeExcelItemTitle(wb,sheet,i+":"+firstIndustryName,baseFaceConcept,stockRow);
				*/
				
				ExcelCommon.writeExcelItemTitle(wb,sheet,conceptName,null,stockRow,true);
	   			//所属概念所有股票
	   			List<StockToConcept> listConceptStock = new ArrayList<StockToConcept>();
	   			listConceptStock=sbDao.getStockToConcept(conceptCode);
	   			stockLogger.logger.debug("概念股票数："+listConceptStock.size());
	   			
	   			//按组合排序
	   			List<StockSummary> listStockSummaryOrderBy = new ArrayList<StockSummary>(); 
	   			List<String> listName = new ArrayList<String>(); 	 
	   		
	   			for(Iterator ie=listConceptStock.iterator();ie.hasNext();)
	   			{
	   			//stockRow++;
	   				StockToConcept toConstock =(StockToConcept) ie.next();
	   				String stockName = toConstock.getStockName();
	   				listName.add(stockName);
	   			}
	   			//按名称排序
	   			Collections.sort(listName,Collator.getInstance(java.util.Locale.CHINA));
	   			 			
	   				
   				for (int kk=0;kk<listName.size();kk++)
   	   			{
   	   			for (int j=0;j<listConceptStock.size();j++)	
   	   			{
   	   				
   	   				StockToConcept toConstock =(StockToConcept) listConceptStock.get(j);
   	   				if(!listName.get(kk).equals(toConstock.getStockName()))
   	   					continue;
	   			
	   				String stockFullId = toConstock.getStockFullId();
	   			
	   				System.out.println("stockFullId:"+stockFullId);	   				
	   				stockLogger.logger.fatal("****stockFullId："+stockFullId+"****");
	   				
	   				int isTableExist=sdDao.isExistStockTable(stockFullId,ConstantsInfo.TABLE_SUMMARY_STOCK);
	   		    	if(isTableExist == 0){//不存在
	   					stockLogger.logger.fatal("****stockFullId："+stockFullId+"不存在统计表****");
	   					System.out.println(stockFullId+"统计表不存在****");
	   					continue;  
	   				}
	   		    	
	   		    	//ssDao.truncateSummaryStock(stockFullId);
	   		    		   		    	
					//其他值
			   		StockOtherInfoValue soiValue=new StockOtherInfoValue(stockFullId,toConstock.getStockName(),0,0,null,null);
			   		//ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow);
			   		
			   		stockRow++;
			   		ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow, 0, null, true);
	   				
			   		//获取最近统计数据
			   		List<StockSummary> stockSummaryInfo=new ArrayList<StockSummary>();
			   		
			   		stockSummaryInfo = ssDao.getSummaryFromSummaryTable(stockFullId,15);
			   		
			   		int extremeCol = 0;			  
			   		//查找对应位置并写入excel
				    for (int ij=0;ij<stockSummaryInfo.size();ij++)	
					{
				    	StockSummary sSum = stockSummaryInfo.get(ij);
				    	            
				    	if(sSum!=null && stockDateColumnmap.containsKey(sSum.getDayCurDate())) {
				        	extremeCol = stockDateColumnmap.get(sSum.getDayCurDate());				        	
				        	ExcelCommon.writeSummaryExcelItem(wb,sheet,sSum,extremeCol,stockRow,0);
				    	}
				    	
				 	}
	   					
				    //第一个就是最后一天的
				   // listStockSummaryOrderBy.add(stockSummaryInfo.get(0));
			   		//排序
			   		//Collections.sort(listStockTotalInfoOrderBy); 	   		           
	   			
	   			}
   	   			}
	   			
	   		   		           	   			
				FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);;
				wb.write(fileOStream);
				fileOStream.flush();
				fileIStream.close();
				fileOStream.close();              
				  
				listConceptStock = null;	          
				 //测试作用
			//	if(stockRow>30)
			//		break;				
	   		}
	        
	      //测试作用
			//if(stockRow>30)
			//	break;	        
   		}
   		
        listConcept = null;
	}
	
	
	//按概念在一级行业下生成excel orderby
	public void writeSummaryExcelFormFuturesOrderBy(String filePath, String fileTime) throws SQLException, IOException, ClassNotFoundException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
	{
		List<StockMarket> listMarket = new ArrayList<StockMarket>(); 
		 
	 	//得到当前所有市场
	 	listMarket=sbDao.getStockMarket(ConstantsInfo.FuturesMarket);
		
    	XSSFWorkbook wb=null;
    	XSSFSheet sheet =null;
    	String excleFileName=null;
        int flag_first=0;
        int titleRow =0;//记录行业或概念标题行
        sheetCount =0;
    	//先输出到一级行业
        for (int countI=0; countI<listMarket.size();countI++)	
   		{
        	      	
	    	//每个行业输出到excel一次	      
	        	//countI第一次	        	
	   			if(flag_first == 0 || stockRow >= 510) {
	   				
	   				flag_first =1;
	   				//创建日期目录
	   				File file = new File(filePath+fileTime);  
	   				System.out.println(fileTime);
	   				if (!file.exists())
	   				{   
	   					 file.mkdir();   
	   				} 
	   				
		   			// 工作区		
		   			wb = new XSSFWorkbook(); 
		   			// 创建第一个sheet     
		   			sheet=  wb.createSheet("allstock");		
		   			sheetCount++;
		   			excleFileName="Stock_Futures_"+fileTime+"_Summary_"+sheetCount+".xlsx";	   		    
		   		    stockRow = 1;
		   			//创建excel
		   		  stockDateColumnmap.clear();
		   	 		ExcelCommon.createSummaryExcel(wb,sheet,filePath,excleFileName,sdDao,stockDateColumnmap);
		   	 		//writeSummaryExcelFromMarket(wb,sheet,filePath,excleFileName,fileTime);
		   			FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);;
		   			wb.write(fileOStream);		
		   	        fileOStream.close(); 
		   	        wb=null;
		   	        sheet=null;
	   			}
	   			
	   			StockExcelStatItem  statItem;   	
	   			
	   			excleFileName="Stock_Futures_"+fileTime+"_Summary_"+sheetCount+".xlsx";
	 			
	   			File file = new File(filePath +fileTime+ "\\"+excleFileName);
				// 输入流   
				FileInputStream fileIStream = new FileInputStream(file);  	
				wb = new XSSFWorkbook(fileIStream);   
				sheet = wb.getSheetAt(0);  
 
				//得到当前一级行业下概念code
	        	StockMarket sMarket = listMarket.get(countI);
	        	
				 String conceptCode = sMarket.getCode();
				 String conceptName=sMarket.getName();
				 if(conceptName==null)
					 continue;
				 
				System.out.println("市场："+conceptName);
				//if(!conceptName.equals("黄金珠宝"))
	   			//	continue;
				
				stockLogger.logger.fatal("市场："+conceptName);		
				//概念标题 
				stockRow++;
				titleRow = stockRow;				
				ExcelCommon.writeExcelItemTitle(wb,sheet,titleRow+":"+conceptName,null,stockRow,true);
   				 	
	   			//当前一级行业
				List<StockToFutures> listFuturesStock = new ArrayList<StockToFutures>();   
	   			listFuturesStock=sbDao.getFuturesToStock(sMarket.getCode());
	   			stockLogger.logger.debug("概念股票数："+listFuturesStock.size());	   			
	   			
	   			List<String> listName = new ArrayList<String>(); 	 
		   		
	   			for(Iterator ie=listFuturesStock.iterator();ie.hasNext();)
	   			{
	   			//stockRow++;
	   				StockToFutures toConstock =(StockToFutures) ie.next();
	   				String stockName = toConstock.getName();
	   				listName.add(stockName);
	   			}
	   			//按名称排序
	   			Collections.sort(listName,Collator.getInstance(java.util.Locale.CHINA));
	   			 			
	   				
   				for (int kk=0;kk<listName.size();kk++)
   	   			{
   	   			for (int j=0;j<listFuturesStock.size();j++)	
   	   			{
	   			
	   				//stockRow++;
	   				StockToFutures toConstock =(StockToFutures) listFuturesStock.get(j);
	   			
   	   				if(!listName.get(kk).equals(toConstock.getName()))
   	   					continue;
	   				String stockFullId = toConstock.getCode();
	   			
	   				System.out.println("stockFullId:"+stockFullId);	   				
	   				stockLogger.logger.fatal("****stockFullId："+stockFullId+"****");
	   				
	   				int isTableExist=sdDao.isExistStockTable(stockFullId,ConstantsInfo.TABLE_SUMMARY_STOCK);
	   		    	if(isTableExist == 0){//不存在
	   					stockLogger.logger.fatal("****stockFullId："+stockFullId+"不存在统计表****");
	   					System.out.println(stockFullId+"统计表不存在****");
	   					continue;  
	   				}
	   		    	//ssDao.truncateSummaryStock(stockFullId);
	   		    		   		    	
					//其他值
			   		StockOtherInfoValue soiValue=new StockOtherInfoValue(stockFullId,toConstock.getName(),0,0,null,null);
			   		//ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow);
			   		
			   		stockRow++;
			   		ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow,0, null, true);
	   				
			   		//获取最近统计数据
			   		List<StockSummary> stockSummaryInfo=new ArrayList<StockSummary>();
			   		
			   		stockSummaryInfo = ssDao.getSummaryFromSummaryTable(stockFullId,15);
			   		
			   		int extremeCol = 0;			  
			   		//查找对应位置并写入excel
				    for (int ij=0;ij<stockSummaryInfo.size();ij++)	
					{
				    	StockSummary sSum = stockSummaryInfo.get(ij);
				    	            
				    	if(sSum!=null && stockDateColumnmap.containsKey(sSum.getDayCurDate())) {
				        	extremeCol = stockDateColumnmap.get(sSum.getDayCurDate());				        	
				        	ExcelCommon.writeSummaryExcelItem(wb,sheet,sSum,extremeCol,stockRow,1);
				    	}
				    	
				 	}
	   					
				    //第一个就是最后一天的
				   // listStockSummaryOrderBy.add(stockSummaryInfo.get(0));
			   		//排序
			   		//Collections.sort(listStockTotalInfoOrderBy); 	   		           
	   			}
   	   			}
	   			   		           	   			
				FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);;
				wb.write(fileOStream);
				fileOStream.flush();
				fileIStream.close();
				fileOStream.close();              
				  
				listFuturesStock = null;	          
				 //测试作用
			//	if(stockRow>30)
			//		break;				
	   		}
	        
	      //测试作用
			//if(stockRow>30)
			//	break;	        
   		
   		
        listMarket = null;
	}

	
	//按概念在一级行业下生成excel orderby
	public void writeOperationExcelFormFuturesOrderBy(String filePath, String fileTime) throws SQLException, IOException, ClassNotFoundException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
	{
		List<StockMarket> listMarket = new ArrayList<StockMarket>(); 
		 
	 	//得到当前所有市场
	 	listMarket=sbDao.getStockMarket(ConstantsInfo.FuturesMarket);
		
    	XSSFWorkbook wb=null;
    	XSSFSheet sheet =null;
    	String excleFileName=null;
        int flag_first=0;
        int titleRow =0;//记录行业或概念标题行
        sheetCount =0;
    	//先输出到一级行业
        for (int countI=0; countI<listMarket.size();countI++)	
   		{
        	      	
	    	//每个行业输出到excel一次	      
	        	//countI第一次	        	
	   			if(flag_first == 0 || stockRow >= 510) {
	   				
	   				flag_first =1;
	   				//创建日期目录
	   				File file = new File(filePath+fileTime);  
	   				System.out.println(fileTime);
	   				if (!file.exists())
	   				{   
	   					 file.mkdir();   
	   				} 
	   				
		   			// 工作区		
		   			wb = new XSSFWorkbook(); 
		   			// 创建第一个sheet     
		   			sheet=  wb.createSheet("allstock");		
		   			sheetCount++;
		   			excleFileName="Stock_Futures_"+fileTime+"_Operation_"+sheetCount+".xlsx";	   		    
		   		    stockRow = 1;
		   	    	stockDateColumnmap.clear();
		   			//创建excel
		   	 		ExcelCommon.createOperationExcel(wb,sheet,filePath,excleFileName,sdDao,stockDateColumnmap, ConstantsInfo.DayDataType);
		   	 		//writeSummaryExcelFromMarket(wb,sheet,filePath,excleFileName,fileTime);
		   			FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);;
		   			wb.write(fileOStream);		
		   	        fileOStream.close(); 
		   	        wb=null;
		   	        sheet=null;
	   			}
	   			
	   			StockExcelStatItem  statItem;   	
	   			
	   			excleFileName="Stock_Futures_"+fileTime+"_Operation_"+sheetCount+".xlsx";
	 			
	   			File file = new File(filePath +fileTime+ "\\"+excleFileName);
				// 输入流   
				FileInputStream fileIStream = new FileInputStream(file);  	
				wb = new XSSFWorkbook(fileIStream);   
				sheet = wb.getSheetAt(0);  
 
				//得到当前一级行业下概念code
	        	StockMarket sMarket = listMarket.get(countI);
	        	
				 String conceptCode = sMarket.getCode();
				 String conceptName=sMarket.getName();
				 if(conceptName==null)
					 continue;
				 
				System.out.println("市场："+conceptName);
				//if(!conceptName.equals("黄金珠宝"))
	   			//	continue;
				
				stockLogger.logger.fatal("市场："+conceptName);			
	  	
				//概念标题 
				stockRow++;
				titleRow = stockRow;
				
				ExcelCommon.writeExcelItemTitle(wb,sheet,titleRow+":"+conceptName,null,stockRow,true);
   				 	
	   			//当前一级行业
				List<StockToFutures> listFuturesStock = new ArrayList<StockToFutures>();   
	   			listFuturesStock=sbDao.getFuturesToStock(sMarket.getCode());
	   			stockLogger.logger.debug("概念股票数："+listFuturesStock.size());
	   			
	   			List<String> listName = new ArrayList<String>(); 	 
		   		
	   			for(Iterator ie=listFuturesStock.iterator();ie.hasNext();)
	   			{
	   			//stockRow++;
	   				StockToFutures toConstock =(StockToFutures) ie.next();
	   				String stockName = toConstock.getName();
	   				listName.add(stockName);
	   			}
	   			//按名称排序
	   			Collections.sort(listName,Collator.getInstance(java.util.Locale.CHINA));
	   			 			
	   				
   				for (int kk=0;kk<listName.size();kk++)
   	   			{
   	   			for (int j=0;j<listFuturesStock.size();j++)	
   	   			{
   	   				
   	   				StockToFutures toConstock =(StockToFutures) listFuturesStock.get(j);
   	   				if(!listName.get(kk).equals(toConstock.getName()))
   	   					continue;   			
	   			//stockRow++;
	   				String stockFullId = toConstock.getCode();
	   				
	   				//if(!stockFullId.equals("SH600895"))
	   				//	continue;
	   				System.out.println("stockFullId:"+stockFullId);	   				
	   				stockLogger.logger.fatal("****stockFullId："+stockFullId+"****");
	   				
	   				int isTableExist=sdDao.isExistStockTable(stockFullId,ConstantsInfo.TABLE_OPERATION_STOCK);
	   		    	if(isTableExist == 0){//不存在
	   					stockLogger.logger.fatal("****stockFullId："+stockFullId+"不存在统计表****");
	   					System.out.println(stockFullId+"统计表不存在****");
	   					continue;  
	   				}
	   		    	
	   		    	//ssDao.truncateSummaryStock(stockFullId);
	   		    		   		    	
					//其他值
			   		StockOtherInfoValue soiValue=new StockOtherInfoValue(stockFullId,toConstock.getName(),0,0,null,null);
			   		//ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow);
			   		
			   		stockRow++;
			   		ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow,0, null, true);
	   				
			   		//获取最近统计数据
			   		List<StockOperation> stockOperationInfo=new ArrayList<StockOperation>();
			   		
			   		stockOperationInfo = ssDao.getOperationFromOperationTable(stockFullId,ConstantsInfo.DayDataType,30);
			   
			   		int extremeCol = 0;		
			   		int earn=0,stop=0,loss=0;
			   		float totalShouyi = 0; 
			   		//查找对应位置并写入excel
				    for (int ij=0;ij<stockOperationInfo.size();ij++)	
					{
				    	StockOperation sSop = stockOperationInfo.get(ij);
				    	        
				    	if(sSop!=null && stockDateColumnmap.containsKey(sSop.getOpDate())) {
				        	extremeCol = stockDateColumnmap.get(sSop.getOpDate());	
				        	
				        	String psState = ssDao.getpsStatusFromSummaryTable(stockFullId,sSop.getOpDate());
				        	
				        	ExcelCommon.writeOperationExcelItem(wb,sheet,sSop,psState, extremeCol,stockRow);
				        	
				        	//赢
				        	if(sSop.getOpType() == ConstantsInfo.STOP ){ //止
					    		stop++;
					    		totalShouyi += sSop.getStopRatio();
				        	} else if(sSop.getOpType() == ConstantsInfo.SALE && sSop.getEarnRatio()>=0) {
					    		earn++;	
					    		totalShouyi += sSop.getEarnRatio();
				        	} else if(sSop.getOpType() == ConstantsInfo.SALE && sSop.getLossRatio()<-0.000001) { //亏
					    		loss++;
					    		totalShouyi += sSop.getLossRatio();
				        	}
					    	
				    	}    	
				 	}
				 //   System.out.println(earn+":"+stop+":"+loss);
				    int totalsize = earn+stop+loss;
				    if(totalsize > 0) {				    	
				    	ExcelCommon.writeOperationTotalExcelItem(wb,sheet,0 ,stockRow,earn,stop,loss,totalsize, totalShouyi);
				    }
	   					
				    //第一个就是最后一天的
				   // listStockSummaryOrderBy.add(stockSummaryInfo.get(0));
			   		//排序
			   		//Collections.sort(listStockTotalInfoOrderBy); 	   		           
	   			
	   			}
   	   			}
	   			       	   			
				FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);;
				wb.write(fileOStream);
				fileOStream.flush();
				fileIStream.close();
				fileOStream.close();              
				  
				listFuturesStock = null;	          
				 //测试作用
			//	if(stockRow>30)
			//		break;				
	   		}
	        
	      //测试作用
			//if(stockRow>30)
			//	break;	        
   		
   		
        listMarket = null;
	}
	
	
	//按概念在一级行业下生成excel orderby
	public void writeOperationExcelFormFuturesOrderByAllType(String filePath, String fileTime, int dateType) throws SQLException, IOException, ClassNotFoundException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
	{
		List<StockMarket> listMarket = new ArrayList<StockMarket>(); 
		 
	 	//得到当前所有市场
	 	listMarket=sbDao.getStockMarket(ConstantsInfo.FuturesMarket);
		
    	XSSFWorkbook wb=null;
    	XSSFSheet sheet =null;
    	String excleFileName=null;
        int flag_first=0;
        int titleRow =0;//记录行业或概念标题行
        sheetCount =0;
    	//先输出到一级行业
        for (int countI=0; countI<listMarket.size();countI++)	
   		{
        	      	
	    	//每个行业输出到excel一次	      
	        	//countI第一次	        	
	   			if(flag_first == 0 || stockRow >= 510) {
	   				
	   				flag_first =1;
	   				//创建日期目录
	   				File file = new File(filePath+fileTime);  
	   				System.out.println(fileTime);
	   				if (!file.exists())
	   				{   
	   					 file.mkdir();   
	   				} 
	   				
		   			// 工作区		
		   			wb = new XSSFWorkbook(); 
		   			// 创建第一个sheet     
		   			sheet=  wb.createSheet("allstock");		
		   			sheetCount++;
		   		//	excleFileName="Stock_Futures_"+fileTime+"_Operation_"+sheetCount+".xlsx";	
		   			excleFileName= getExcelFileName("Futures",dateType,fileTime,"Operation",sheetCount);
		   		    stockRow = 1;
		   	    	stockDateColumnmap.clear();
		   			//创建excel
		   	 		ExcelCommon.createOperationExcel(wb,sheet,filePath,excleFileName,sdDao,stockDateColumnmap, dateType);
		   	 		//writeSummaryExcelFromMarket(wb,sheet,filePath,excleFileName,fileTime);
		   			FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);;
		   			wb.write(fileOStream);		
		   	        fileOStream.close(); 
		   	        wb=null;
		   	        sheet=null;
	   			}
	   			
	   			StockExcelStatItem  statItem;   		
	   		//	excleFileName="Stock_Futures_"+fileTime+"_Operation_"+sheetCount+".xlsx";
	   			excleFileName= getExcelFileName("Futures",dateType,fileTime,"Operation",sheetCount);
	 			
	   			File file = new File(filePath +fileTime+ "\\"+excleFileName);
				// 输入流   
				FileInputStream fileIStream = new FileInputStream(file);  	
				wb = new XSSFWorkbook(fileIStream);   
				sheet = wb.getSheetAt(0);  
 
				//得到当前一级行业下概念code
	        	StockMarket sMarket = listMarket.get(countI);
	        	
				 String conceptCode = sMarket.getCode();
				 String conceptName=sMarket.getName();
				 if(conceptName==null)
					 continue;
				 
				System.out.println("市场："+conceptName);
				//if(!conceptName.equals("黄金珠宝"))
	   			//	continue;
				
				stockLogger.logger.fatal("市场："+conceptName);			
	  	
				//概念标题 
				stockRow++;
				titleRow = stockRow;
				
				ExcelCommon.writeExcelItemTitle(wb,sheet,titleRow+":"+conceptName,null,stockRow,true);
   				 	
	   			//当前一级行业
				List<StockToFutures> listFuturesStock = new ArrayList<StockToFutures>();   
	   			listFuturesStock=sbDao.getFuturesToStock(sMarket.getCode());
	   			stockLogger.logger.debug("概念股票数："+listFuturesStock.size());
	   			
	   			List<String> listName = new ArrayList<String>(); 	 
		   		
	   			for(Iterator ie=listFuturesStock.iterator();ie.hasNext();)
	   			{
	   			//stockRow++;
	   				StockToFutures toConstock =(StockToFutures) ie.next();
	   				String stockName = toConstock.getName();
	   				listName.add(stockName);
	   			}
	   			//按名称排序
	   			Collections.sort(listName,Collator.getInstance(java.util.Locale.CHINA));
	   			 			
	   				
   				for (int kk=0;kk<listName.size();kk++)
   	   			{
   	   			for (int j=0;j<listFuturesStock.size();j++)	
   	   			{
   	   				
   	   				StockToFutures toConstock =(StockToFutures) listFuturesStock.get(j);
   	   				if(!listName.get(kk).equals(toConstock.getName()))
   	   					continue;   			
	   			//stockRow++;
	   				String stockFullId = toConstock.getCode();
	   				
	   				//if(!stockFullId.equals("SH600895"))
	   				//	continue;
	    				
	   				stockLogger.logger.fatal("****stockFullId："+stockFullId+"****");
	   		    		   		    	
					//其他值
			   		StockOtherInfoValue soiValue=new StockOtherInfoValue(stockFullId,toConstock.getName(),0,0,null,null);
			   		//ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow);
			   		
			   		stockRow++;
			   		ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow, 0, null, true);
			   		int isTableExist=sdDao.isExistStockTable(stockFullId,ConstantsInfo.TABLE_OPERATION_STOCK);
	   		    	if(isTableExist == 0){//不存在
	   					stockLogger.logger.fatal("****stockFullId："+stockFullId+"不存在统计表****");
	   					System.out.println(stockFullId+"统计表不存在****");
	   					continue;  
	   				}
			   		//获取最近统计数据
			   		List<StockOperation> stockOperationInfo=new ArrayList<StockOperation>();
			   		
			   		stockOperationInfo = ssDao.getOperationFromOperationTable(stockFullId,dateType,30);
			   
			   		int extremeCol = 0;		
			   		int earn=0,stop=0,loss=0;
			   		float totalShouyi = 0; 
			   		//查找对应位置并写入excel
				    for (int ij=0;ij<stockOperationInfo.size();ij++)	
					{
				    	StockOperation sSop = stockOperationInfo.get(ij);
				    	        
				    	if(sSop!=null && stockDateColumnmap.containsKey(sSop.getOpDate())) {
				        	extremeCol = stockDateColumnmap.get(sSop.getOpDate());	
				        	
				        	String psState = ssDao.getpsStatusFromSummaryTable(stockFullId,sSop.getOpDate());
				        	
				        	ExcelCommon.writeOperationExcelItem(wb,sheet,sSop,psState, extremeCol,stockRow);
				        	
				        	//赢
				        	if(sSop.getOpType() == ConstantsInfo.STOP ){ //止
					    		stop++;
					    		totalShouyi += sSop.getStopRatio();
				        	} else if(sSop.getOpType() == ConstantsInfo.SALE && sSop.getEarnRatio()>=0) {
					    		earn++;	
					    		totalShouyi += sSop.getEarnRatio();
				        	} else if(sSop.getOpType() == ConstantsInfo.SALE && sSop.getLossRatio()<-0.000001) { //亏
					    		loss++;
					    		totalShouyi += sSop.getLossRatio();
				        	}
					    	
				    	}    	
				 	}
				//    System.out.println(earn+":"+stop+":"+loss);
				    int totalsize = earn+stop+loss;
				    if(totalsize > 0) {				    	
				    	ExcelCommon.writeOperationTotalExcelItem(wb,sheet,0 ,stockRow,earn,stop,loss,totalsize, totalShouyi);
				    }
	   					
				    //第一个就是最后一天的
				   // listStockSummaryOrderBy.add(stockSummaryInfo.get(0));
			   		//排序
			   		//Collections.sort(listStockTotalInfoOrderBy); 	   		           
	   			
	   			}
   	   			}
	   			       	   			
				FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);;
				wb.write(fileOStream);
				fileOStream.flush();
				fileIStream.close();
				fileOStream.close();              
				  
				listFuturesStock = null;	          
				 //测试作用
			//	if(stockRow>30)
			//		break;				
	   		}
	        
	      //测试作用
			//if(stockRow>30)
			//	break;	        
   		
   		
        listMarket = null;
	}
	
	//按概念在一级行业下生成excel orderby
	public void writeTotalOperationExcelFormFuturesOrderByAllType(String filePath, String fileTime,int dateType) throws SQLException, IOException, ClassNotFoundException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
	{
		List<StockMarket> listMarket = new ArrayList<StockMarket>(); 
		 
	 	//得到当前所有市场
	 	listMarket=sbDao.getStockMarket(ConstantsInfo.FuturesMarket);
		
    	XSSFWorkbook wb=null;
    	XSSFSheet sheet =null;
    	String excleFileName=null;
        int flag_first=0;
        int titleRow =0;//记录行业或概念标题行
        sheetCount =0;
    	//先输出到一级行业
        for (int countI=0; countI<listMarket.size();countI++)	
   		{
        	      	
	    	//每个行业输出到excel一次	      
	        	//countI第一次	        	
	   			if(flag_first == 0 || stockRow >= 510) {
	   				
	   				flag_first =1;
	   				//创建日期目录
	   				File file = new File(filePath+fileTime);  
	   				System.out.println(fileTime);
	   				if (!file.exists())
	   				{   
	   					 file.mkdir();   
	   				} 
	   				
		   			// 工作区		
		   			wb = new XSSFWorkbook(); 
		   			// 创建第一个sheet     
		   			sheet=  wb.createSheet("allstock");		
		   			sheetCount++;
		   			excleFileName= getExcelFileName("Futures",dateType,fileTime,"total_operation",sheetCount);	   		    
		   		    stockRow = 1;
		   	    	stockDateColumnmap.clear();
		   			//创建excel
		   	 		ExcelCommon.createTotalOperationExcel(wb,sheet,filePath,excleFileName,sdDao,stockDateColumnmap,dateType);
		   			FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);;
		   			wb.write(fileOStream);		
		   	        fileOStream.close(); 
		   	        wb=null;
		   	        sheet=null;
	   			}
	   			
	   			StockExcelStatItem  statItem;   		
	   			excleFileName= getExcelFileName("Futures",dateType,fileTime,"total_operation",sheetCount);	   		    	   		    
	   			File file = new File(filePath +fileTime+ "\\"+excleFileName);
				// 输入流   
				FileInputStream fileIStream = new FileInputStream(file);  	
				wb = new XSSFWorkbook(fileIStream);   
				sheet = wb.getSheetAt(0);  
 
				//得到当前一级行业下概念code
	        	StockMarket sMarket = listMarket.get(countI);
	        	
				 String conceptCode = sMarket.getCode();
				 String conceptName=sMarket.getName();
				 if(conceptName==null)
					 continue;
				 
				System.out.println("市场："+conceptName);
				//if(!conceptName.equals("黄金珠宝"))
	   			//	continue;
				
				stockLogger.logger.fatal("市场："+conceptName);			
	  	
				//概念标题 
				stockRow++;
				titleRow = stockRow;
				
				ExcelCommon.writeExcelItemTitle(wb,sheet,titleRow+":"+conceptName,null,stockRow,true);
   				 	
	   			//当前一级行业
				List<StockToFutures> listFuturesStock = new ArrayList<StockToFutures>();   
	   			listFuturesStock=sbDao.getFuturesToStock(sMarket.getCode());
	   			stockLogger.logger.debug("概念股票数："+listFuturesStock.size());
	   			
	   			List<String> listName = new ArrayList<String>(); 	 
		   		
	   			for(Iterator ie=listFuturesStock.iterator();ie.hasNext();)
	   			{
	   			//stockRow++;
	   				StockToFutures toConstock =(StockToFutures) ie.next();
	   				String stockName = toConstock.getName();
	   				listName.add(stockName);
	   			}
	   			//按名称排序
	   			Collections.sort(listName,Collator.getInstance(java.util.Locale.CHINA));
	   			 			
	   				
   				for (int kk=0;kk<listName.size();kk++)
   	   			{
   	   			for (int j=0;j<listFuturesStock.size();j++)	
   	   			{
   	   				
   	   				StockToFutures toConstock =(StockToFutures) listFuturesStock.get(j);
   	   				if(!listName.get(kk).equals(toConstock.getName()))
   	   					continue;   			
	   			//stockRow++;
	   				String stockFullId = toConstock.getCode();
	   				
	   				//if(!stockFullId.equals("SH600895"))
	   				//	continue;
	   				System.out.println("stockFullId:"+stockFullId);	   				
	   				stockLogger.logger.fatal("****stockFullId："+stockFullId+"****");	   		
	   		    		   		    	
					//其他值
			   		StockOtherInfoValue soiValue=new StockOtherInfoValue(stockFullId,toConstock.getName(),0,0,null,null);
			   		//ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow);
			   		
			   		stockRow++;
			   		ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow,0,null, true);
			   		int isTableExist=sdDao.isExistStockTable(stockFullId,ConstantsInfo.TABLE_OPERATION_STOCK);
	   		    	if(isTableExist == 0){//不存在
	   					stockLogger.logger.fatal("****stockFullId："+stockFullId+"不存在统计表****");
	   					System.out.println(stockFullId+"统计表不存在****");
	   					continue;  
	   				}
			   	//获取最近统计数据
			   		List<StockOperation> stockOperationInfo=new ArrayList<StockOperation>();			 	
			   		
			   		int nums = ConstantsInfo.ExportNum(dateType);
			   		stockOperationInfo = ssDao.getOperationFromOperationTable(stockFullId,dateType, nums);
			 	   				   
			   		int extremeCol = 0;		
			   		int earn=0,stop=0,loss=0;
			   		float totalShouyi=0;
			   		//查找对应位置并写入excel
				    for (int ij=0;ij<stockOperationInfo.size();ij++)	
					{
				    	StockOperation sSop = stockOperationInfo.get(ij);           
				    	if (sSop==null){
				    		continue;
				    	}
				    	
				    	boolean flag = false;
				    	
				    	//先hash查找
				    	if(stockDateColumnmap.containsKey(sSop.getOpDate())) {
				        	extremeCol = stockDateColumnmap.get(sSop.getOpDate());	
				        	flag = true;
				        	
				    	} else {
				    		if (dateType == ConstantsInfo.WeekDataType || dateType == ConstantsInfo.MonthDataType){
				    			//再遍历
				    			for(String key: stockDateColumnmap.keySet()) {
				    				//System.out.println("key:"+key+"data:"+ sSop.getOpDate());
				    				if(CommonDate.isSameDate(key, sSop.getOpDate(), dateType)){
				    					flag = true;
				    					extremeCol = stockDateColumnmap.get(key);
				    					break;
				    				}	
				    			}	
				    		} 
				    	}
				    	
				    	if(flag){
				    		//将日周月展示
				        	List<StockOperation> stockOperationInfoByDate=new ArrayList<StockOperation>();
				        	stockOperationInfoByDate = ssDao.getOperationFromOperationTableByDate(sSop.getFullId(),sSop.getOpDate());
				        	for (int datesize=0;datesize<stockOperationInfoByDate.size();datesize++){
				        		StockOperation sSopDate = stockOperationInfoByDate.get(datesize);
				        		ExcelCommon.writeTotalOperationExcelItem(wb,sheet,sSopDate,extremeCol,stockRow);
							}		
				    	}  	
				 	}	
	   			}
   	   			}
	   			       	   			
				FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);;
				wb.write(fileOStream);
				fileOStream.flush();
				fileIStream.close();
				fileOStream.close();              
				  
				listFuturesStock = null;	          
				 //测试作用
			//	if(stockRow>30)
			//		break;				
	   		}
	        
	      //测试作用
			//if(stockRow>30)
			//	break;	        
   		
   		
        listMarket = null;
	}
	
	
	//按概念在一级行业下生成excel orderby
	public void writePointExcelFormFuturesOrderBy(String filePath, String fileTime) throws SQLException, IOException, ClassNotFoundException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
	{
		List<StockMarket> listMarket = new ArrayList<StockMarket>(); 
		 
	 	//得到当前所有市场
	 	listMarket=sbDao.getStockMarket(ConstantsInfo.FuturesMarket);
		
    	XSSFWorkbook wb=null;
    	XSSFSheet sheet =null;
    	String excleFileName=null;
        int flag_first=0;
        int titleRow =0;//记录行业或概念标题行
        sheetCount =0;
    	//先输出到一级行业
        for (int countI=0; countI<listMarket.size();countI++)	
   		{
        	      	
	    	//每个行业输出到excel一次	      
	        	//countI第一次	        	
	   			if(flag_first == 0 || stockRow >= 510) {
	   				
	   				flag_first =1;
	   				//创建日期目录
	   				File file = new File(filePath+fileTime);  
	   				System.out.println(fileTime);
	   				if (!file.exists())
	   				{   
	   					 file.mkdir();   
	   				} 
	   				
		   			// 工作区		
		   			wb = new XSSFWorkbook(); 
		   			// 创建第一个sheet     
		   			sheet=  wb.createSheet("allstock");		
		   			sheetCount++;
		   			excleFileName="Stock_Futures_"+fileTime+"_Point_"+sheetCount+".xlsx";	   		    
		   		    stockRow = 1;
		   	    	stockDateColumnmap.clear();
		   			//创建excel
		   	 		ExcelCommon.createPointExcel(wb,sheet,filePath,excleFileName,sdDao,stockDateColumnmap);
		   	 		//writeSummaryExcelFromMarket(wb,sheet,filePath,excleFileName,fileTime);
		   			FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);;
		   			wb.write(fileOStream);		
		   	        fileOStream.close(); 
		   	        wb=null;
		   	        sheet=null;
	   			}
	   			
	   			StockExcelStatItem  statItem;   	
	   			
	   			excleFileName="Stock_Futures_"+fileTime+"_Point_"+sheetCount+".xlsx";
	 			
	   			File file = new File(filePath +fileTime+ "\\"+excleFileName);
				// 输入流   
				FileInputStream fileIStream = new FileInputStream(file);  	
				wb = new XSSFWorkbook(fileIStream);   
				sheet = wb.getSheetAt(0);  
 
				//得到当前一级行业下概念code
	        	StockMarket sMarket = listMarket.get(countI);
	        	
				 String conceptCode = sMarket.getCode();
				 String conceptName=sMarket.getName();
				 if(conceptName==null)
					 continue;
				 
				System.out.println("市场："+conceptName);
				//if(!conceptName.equals("黄金珠宝"))
	   			//	continue;
				
				stockLogger.logger.fatal("市场："+conceptName);			
	  	
				//概念标题 
				stockRow++;
				titleRow = stockRow;
				
				ExcelCommon.writeExcelItemTitle(wb,sheet,titleRow+":"+conceptName,null,stockRow,true);
   				 	
	   			//当前一级行业
				List<StockToFutures> listFuturesStock = new ArrayList<StockToFutures>();   
	   			listFuturesStock=sbDao.getFuturesToStock(sMarket.getCode());
	   			stockLogger.logger.debug("概念股票数："+listFuturesStock.size());
	   			
	   			List<String> listName = new ArrayList<String>(); 	 
		   		
	   			for(Iterator ie=listFuturesStock.iterator();ie.hasNext();)
	   			{
	   			//stockRow++;
	   				StockToFutures toConstock =(StockToFutures) ie.next();
	   				String stockName = toConstock.getName();
	   				listName.add(stockName);
	   			}
	   			//按名称排序
	   			Collections.sort(listName,Collator.getInstance(java.util.Locale.CHINA));
	   			 			
	   				
   				for (int kk=0;kk<listName.size();kk++)
   	   			{
   	   			for (int j=0;j<listFuturesStock.size();j++)	
   	   			{
   	   				
   	   				StockToFutures toConstock =(StockToFutures) listFuturesStock.get(j);
   	   				if(!listName.get(kk).equals(toConstock.getName()))
   	   					continue;   			
	   			//stockRow++;
	   				String stockFullId = toConstock.getCode();
	   				
	   				//if(!stockFullId.equals("SH600895"))
	   				//	continue;
	   				System.out.println("stockFullId:"+stockFullId);	   				
	   				stockLogger.logger.fatal("****stockFullId："+stockFullId+"****");
	   				
	   				int isTableExist=sdDao.isExistStockTable(stockFullId,ConstantsInfo.TABLE_OPERATION_STOCK);
	   		    	if(isTableExist == 0){//不存在
	   					stockLogger.logger.fatal("****stockFullId："+stockFullId+"不存在统计表****");
	   					System.out.println(stockFullId+"统计表不存在****");
	   					continue;  
	   				}
	   		    	
	   		    	//ssDao.truncateSummaryStock(stockFullId);
	   		    		   		    	
					//其他值
			   		StockOtherInfoValue soiValue=new StockOtherInfoValue(stockFullId,toConstock.getName(),0,0,null,null);
			   		//ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow);
			   		
			   		stockRow++;
			   		ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow,0,null, true);
	   				
			   		
			     	//获取最近统计数据
			   		List<StockPoint> stockPointInfo=new ArrayList<StockPoint>();
				    String endDate = CommonDate.getCurDate();
			        String startDate = CommonDate.getBeforeDay(endDate, 1, 180);
			   		stockPointInfo = spDao.getRecentPointStock(stockFullId,ConstantsInfo.DayDataType,startDate);
			   
			   		int extremeCol = 0;
					isTableExist=sdDao.isExistStockTable(stockFullId,ConstantsInfo.TABLE_SUMMARY_STOCK);
			    	if(isTableExist != 0){//存在
			    		//StockSummary ss = ssDao.getLastSummaryFromSummaryTable(stockFullId);
			    		StockSummary ss = ssDao.getZhiDingSummaryFromSummaryTable(stockFullId, fileTime, ConstantsInfo.DayDataType);
						  
					  //疑极点
				   		if(ss!=null){
				   			String date = ss.getDayEndDate();
					    	
					    	if(stockDateColumnmap.containsKey(date)) {
					        	extremeCol = stockDateColumnmap.get(date);	
					        	
					        	String value="疑极:"+ss.getDayEndValue();
					        	ExcelCommon.writePointExcelItem(wb,sheet,null,value,extremeCol,stockRow,1);
					        				        	
					    	} else{
					    		System.out.println("not exist the day");
							}
				   		} 
					}
			   		
			   		//查找对应位置并写入excel
				    for (int ij=0;ij<stockPointInfo.size();ij++)	
					{
				    	StockPoint sp = stockPointInfo.get(ij);
				    	String date = sp.getExtremeDate().toString();
				    	if(sp!=null && stockDateColumnmap.containsKey(date)) {
				        	extremeCol = stockDateColumnmap.get(date);			
				        	ExcelCommon.writePointExcelItem(wb,sheet,sp,"",extremeCol,stockRow,0);
				    	} else{
				    		System.out.println("not exist the day");
						}   	
				 	}
	   					
				    //第一个就是最后一天的
				   // listStockSummaryOrderBy.add(stockSummaryInfo.get(0));
			   		//排序
			   		//Collections.sort(listStockTotalInfoOrderBy); 	   		           
	   			
	   			}
   	   			}
	   			
	   		   		           	   			
				FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);;
				wb.write(fileOStream);
				fileOStream.flush();
				fileIStream.close();
				fileOStream.close();              
				  
				listFuturesStock = null;	          
				 //测试作用
			//	if(stockRow>30)
			//		break;				
	   		}
	        
	      //测试作用
			//if(stockRow>30)
			//	break;	        
   		
   		
        listMarket = null;
	}
	

	
	//按概念在一级行业下生成excel orderby
	public void writeOperationExcelFormConceptInFirstIndustryOrderBy(String filePath, String fileTime) throws SQLException, IOException, ClassNotFoundException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
	{
		List<StockConceptInFirstIndustry> listConcept = new ArrayList<StockConceptInFirstIndustry>(); 
	 	List<String> listFirstIndustry = new ArrayList<String>(); 
	
    	//得到当前一级行业code
	 	listFirstIndustry=sbDao.getStockFirstIndustry();    	
    	
    	XSSFWorkbook wb=null;
    	XSSFSheet sheet =null;
    	String excleFileName=null;
        int flag_first=0;
        int titleRow =0;//记录行业或概念标题行
        sheetCount =0;
    	//先输出到一级行业
        for (int countI=0; countI<listFirstIndustry.size();countI++)	
   		{
        	//得到当前一级行业下概念code
        	String firstIndustryId = listFirstIndustry.get(countI);
        	
        	//一级行业名称
        	String firstIndustryName = sbDao.getStockFirstIndustryName(firstIndustryId);        	
        	System.out.println("一级行业："+firstIndustryName);        	
        	
        	//得到概念id
        	listConcept = sbDao.getStockFirstIndustryConceptCode(firstIndustryId);
        	
	    	//每个行业输出到excel一次
	        for (int i=0;i<listConcept.size();i++)	
	   		{
	        	//countI第一次	        	
	   			if(flag_first == 0 || stockRow >= 510) {
	   				
	   				flag_first =1;
	   				//创建日期目录
	   				File file = new File(filePath+fileTime);  
	   				System.out.println(fileTime);
	   				if (!file.exists())
	   				{   
	   					 file.mkdir();   
	   				} 
	   				
		   			// 工作区		
		   			wb = new XSSFWorkbook(); 
		   			// 创建第一个sheet     
		   			sheet=  wb.createSheet("allstock");		
		   			sheetCount++;
		   			excleFileName="Stock_Concept_"+fileTime+"_Operation_"+sheetCount+".xlsx";	   		    
		   		    stockRow = 1;
		   		    stockDateColumnmap.clear();
		   			//创建excel
		   	 		ExcelCommon.createOperationExcel(wb,sheet,filePath,excleFileName,sdDao,stockDateColumnmap,ConstantsInfo.DayDataType);
		   	 		writeOperationExcelFromMarket(wb,sheet,filePath,excleFileName,fileTime,ConstantsInfo.DayDataType);
		   			FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);;
		   			wb.write(fileOStream);		
		   	        fileOStream.close(); 
		   	        wb=null;
		   	        sheet=null;
	   			}
	   		  	
	   			excleFileName="Stock_Concept_"+fileTime+"_Operation_"+sheetCount+".xlsx";
	 			
	   			File file = new File(filePath +fileTime+ "\\"+excleFileName);
				// 输入流   
				FileInputStream fileIStream = new FileInputStream(file);  	
				wb = new XSSFWorkbook(fileIStream);   
				sheet = wb.getSheetAt(0);  
 
				//当前一级行业
				StockConceptInFirstIndustry scon= listConcept.get(i);	
				 String conceptCode = scon.getConceptCode();
				 String conceptName=scon.getConceptName();
				 if(conceptName==null)
					 continue;
				 
				System.out.println("概念："+conceptName);
				//if(!conceptName.equals("黄金珠宝"))
	   			//	continue;
				
				stockLogger.logger.fatal("概念："+conceptName);			
	  	
				//概念标题 
				stockRow++;
				titleRow = stockRow;
				
				//基本面				
				ExcelCommon.writeExcelItemTitle(wb,sheet,conceptName,null,stockRow,true);
				
	   			//所属概念所有股票
	   			List<StockToConcept> listConceptStock = new ArrayList<StockToConcept>();
	   			listConceptStock=sbDao.getStockToConcept(conceptCode);
	   			stockLogger.logger.debug("概念股票数："+listConceptStock.size());
	   			
	   	
	   			List<String> listName = new ArrayList<String>(); 	 
		   		
	   			for(Iterator ie=listConceptStock.iterator();ie.hasNext();)
	   			{
	   			//stockRow++;
	   				StockToConcept toConstock =(StockToConcept) ie.next();
	   				String stockName = toConstock.getStockName();
	   				listName.add(stockName);
	   			}
	   			//按名称排序
	   			Collections.sort(listName,Collator.getInstance(java.util.Locale.CHINA));
	   			 			
	   				
   				for (int kk=0;kk<listName.size();kk++)
   	   			{
   	   			for (int j=0;j<listConceptStock.size();j++)	
   	   			{
   	   				
   	   				StockToConcept toConstock =(StockToConcept) listConceptStock.get(j);
   	   				if(!listName.get(kk).equals(toConstock.getStockName()))
   	   					continue;
	   				//stockRow++;  			
	   				String stockFullId = toConstock.getStockFullId();
	   				
	   				//if(!stockFullId.equals("SH600598"))
	   				//	continue;
	   				System.out.println("stockFullId:"+stockFullId);	   				
	   				stockLogger.logger.fatal("****stockFullId："+stockFullId+"****");
	   				
	   				int isTableExist=sdDao.isExistStockTable(stockFullId,ConstantsInfo.TABLE_OPERATION_STOCK);
	   		    	if(isTableExist == 0){//不存在
	   					stockLogger.logger.fatal("****stockFullId："+stockFullId+"不存在统计表****");
	   					System.out.println(stockFullId+"统计表不存在****");
	   					continue;  
	   				}
	   		    	
	   		    	//ssDao.truncateSummaryStock(stockFullId);
	   		    		   		    	
					//其他值
			   		StockOtherInfoValue soiValue=new StockOtherInfoValue(stockFullId,toConstock.getStockName(),0,0,null,null);
			   		//ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow);
			   		
			   		stockRow++;
			   		ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow,0,null, true);
	   				
			   		//获取最近统计数据
			   		List<StockOperation> stockOperationInfo=new ArrayList<StockOperation>();
			   		
			   		stockOperationInfo = ssDao.getOperationFromOperationTable(stockFullId,ConstantsInfo.DayDataType,30);
			   
			   		int extremeCol = 0;		
			   		int earn=0,stop=0,loss=0;
			   		float totalShouyi=0;
			   		//查找对应位置并写入excel
				    for (int ij=0;ij<stockOperationInfo.size();ij++)	
					{
				    	StockOperation sSop = stockOperationInfo.get(ij);
				    	            
				    	if(sSop!=null && stockDateColumnmap.containsKey(sSop.getOpDate())) {
				        	extremeCol = stockDateColumnmap.get(sSop.getOpDate());			
				        	
				        	//String psState =  ssDao.getpsStatusFromSummaryTable(stockFullId,sSop.getOpDate());
				        	String psState = ""; //ssDao.getpsStatusFromSummaryTable(stockFullId,sSop.getOpDate());
				        	
				        	ExcelCommon.writeOperationExcelItem(wb,sheet,sSop,psState,extremeCol,stockRow);
				        	
				        	//赢
				        	if(sSop.getOpType() == ConstantsInfo.STOP ){ //止
					    		stop++;
					    		totalShouyi += sSop.getStopRatio();
				        	} else if(sSop.getOpType() == ConstantsInfo.SALE && sSop.getEarnRatio()>=0) {
					    		earn++;	
					    		totalShouyi += sSop.getEarnRatio();
				        	} else if(sSop.getOpType() == ConstantsInfo.SALE && sSop.getLossRatio()<-0.000001) { //亏
					    		loss++;
					    		totalShouyi += sSop.getLossRatio();
				        	}
				    	}    	
				 	}
				   // System.out.println(earn+":"+stop+":"+loss);
				    int totalsize = earn+stop+loss;
				    if(totalsize > 0) {				    	
				    	ExcelCommon.writeOperationTotalExcelItem(wb,sheet,0 ,stockRow,earn,stop,loss,totalsize,totalShouyi);
				    }
	   					
				    //第一个就是最后一天的
				   // listStockSummaryOrderBy.add(stockSummaryInfo.get(0));
			   		//排序
			   		//Collections.sort(listStockTotalInfoOrderBy); 	   		           
	   			
	   			}
   	   			}
	   			
	   		   		           	   			
				FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);;
				wb.write(fileOStream);
				fileOStream.flush();
				fileIStream.close();
				fileOStream.close();              
				  
				listConceptStock = null;	          
				 //测试作用
			//	if(stockRow>30)
			//		break;				
	   		}
	        
	      //测试作用
			//if(stockRow>30)
			//	break;	        
   		}
   		
        listConcept = null;
	}
	
	//按行业下生成excel orderby
	public void writeOperationExcelFormIndustryOrderBy(String filePath, String fileTime) throws SQLException, IOException, ClassNotFoundException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
	{
		
	 	List<StockIndustry> listIndustry = new ArrayList<StockIndustry>(); 	 
	 	//得到当前所有行业
    	listIndustry=sbDao.getStockIndustry();
    	System.out.println("行业个数："+listIndustry.size());
    	
    	XSSFWorkbook wb=null;
    	XSSFSheet sheet =null;
    	String excleFileName=null;
        int flag_first=0;
        int titleRow =0;//记录行业或概念标题行
        sheetCount =0;
    	//先输出到一级行业
        for (int countI=0; countI<listIndustry.size();countI++)	
   		{
        	
	        	//countI第一次	        	
	   			if(flag_first == 0 || stockRow >= 510) {
	   				
	   				flag_first =1;
	   				//创建日期目录
	   				File file = new File(filePath+fileTime);  
	   				System.out.println(fileTime);
	   				if (!file.exists())
	   				{   
	   					 file.mkdir();   
	   				} 
	   				
		   			// 工作区		
		   			wb = new XSSFWorkbook(); 
		   			// 创建第一个sheet     
		   			sheet=  wb.createSheet("allstock");		
		   			sheetCount++;
		   			excleFileName="Stock_Industry_"+fileTime+"_Operation_"+sheetCount+".xlsx";	   		    
		   		    stockRow = 1;
		   			//创建excel
		   		   stockDateColumnmap.clear();
		   	 		ExcelCommon.createOperationExcel(wb,sheet,filePath,excleFileName,sdDao,stockDateColumnmap,ConstantsInfo.DayDataType);
		   	 		writeOperationExcelFromMarket(wb,sheet,filePath,excleFileName,fileTime,ConstantsInfo.DayDataType);
		   			FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);;
		   			wb.write(fileOStream);		
		   	        fileOStream.close(); 
		   	        wb=null;
		   	        sheet=null;
	   			}
	   		  	
	   			excleFileName="Stock_Industry_"+fileTime+"_Operation_"+sheetCount+".xlsx";
	 			
	   			File file = new File(filePath +fileTime+ "\\"+excleFileName);
				// 输入流   
				FileInputStream fileIStream = new FileInputStream(file);  	
				wb = new XSSFWorkbook(fileIStream);   
				sheet = wb.getSheetAt(0);  
 
				//当前行业
				StockIndustry indu = listIndustry.get(countI);	
				String induCode = indu.getThirdcode();
				String induName = indu.getThirdname();
				if(induCode == null || induName == null)
					continue;				
				stockLogger.logger.fatal("行业："+induName);   		
				System.out.println("行业："+induName);			
				//行业标题 
				stockRow++;
				titleRow = stockRow;		
	  			
				//基本面				
				ExcelCommon.writeExcelItemTitle(wb,sheet,induName,null,stockRow,true);
				
				//所属行业所有股票
	   			List<StockToIndustry> listIndustryStock = new ArrayList<StockToIndustry>();   
	   			listIndustryStock=sbDao.getIndustryToStock(induCode);	   	
	   			stockLogger.logger.debug("行业股票数："+listIndustryStock.size());
	   			
	   			List<String> listName = new ArrayList<String>(); 	 
		   		
	   			for(Iterator ie=listIndustryStock.iterator();ie.hasNext();)
	   			{
	   			//stockRow++;
	   				StockToIndustry toConstock =(StockToIndustry) ie.next();
	   				String stockName = toConstock.getStockName();
	   				listName.add(stockName);
	   			}
	   			//按名称排序
	   			Collections.sort(listName,Collator.getInstance(java.util.Locale.CHINA));
	   			 			
	   				
   				for (int kk=0;kk<listName.size();kk++)
   	   			{
   	   			for (int j=0;j<listIndustryStock.size();j++)	
   	   			{
   	   				
   	   				StockToIndustry toConstock =(StockToIndustry) listIndustryStock.get(j);
   	   				if(!listName.get(kk).equals(toConstock.getStockName()))
   	   					continue;
	   				//stockRow++;  			
	   				String stockFullId = toConstock.getStockFullId();
	   				
	   				//if(!stockFullId.equals("SH600598"))
	   				//	continue;
	   				System.out.println("stockFullId:"+stockFullId);	   				
	   				stockLogger.logger.fatal("****stockFullId："+stockFullId+"****");
	   				
	   				int isTableExist=sdDao.isExistStockTable(stockFullId,ConstantsInfo.TABLE_OPERATION_STOCK);
	   		    	if(isTableExist == 0){//不存在
	   					stockLogger.logger.fatal("****stockFullId："+stockFullId+"不存在统计表****");
	   					System.out.println(stockFullId+"统计表不存在****");
	   					continue;  
	   				}
	   		    	
	   		    	//ssDao.truncateSummaryStock(stockFullId);
	   		    		   		    	
					//其他值
			   		StockOtherInfoValue soiValue=new StockOtherInfoValue(stockFullId,toConstock.getStockName(),0,0,null,null);
			   		//ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow);
			   		
			   		stockRow++;
			   		ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow,0,null, true);
	   				
			   		//获取最近统计数据
			   		List<StockOperation> stockOperationInfo=new ArrayList<StockOperation>();
			   		
			   		stockOperationInfo = ssDao.getOperationFromOperationTable(stockFullId,ConstantsInfo.DayDataType,30);
			   
			   		int extremeCol = 0;		
			   		int earn=0,stop=0,loss=0;
			   		float totalShouyi=0;
			   		//查找对应位置并写入excel
				    for (int ij=0;ij<stockOperationInfo.size();ij++)	
					{
				    	StockOperation sSop = stockOperationInfo.get(ij);
				    	            
				    	if(sSop!=null && stockDateColumnmap.containsKey(sSop.getOpDate())) {
				        	extremeCol = stockDateColumnmap.get(sSop.getOpDate());			
				        	
				        	String psState = ssDao.getpsStatusFromSummaryTable(stockFullId,sSop.getOpDate());
				        	
				        	ExcelCommon.writeOperationExcelItem(wb,sheet,sSop,psState,extremeCol,stockRow);
				        	
				        	//赢
				        	if(sSop.getOpType() == ConstantsInfo.STOP ){ //止
					    		stop++;
					    		totalShouyi += sSop.getStopRatio();
				        	} else if(sSop.getOpType() == ConstantsInfo.SALE && sSop.getEarnRatio()>=0) {
					    		earn++;	
					    		totalShouyi += sSop.getEarnRatio();
				        	} else if(sSop.getOpType() == ConstantsInfo.SALE && sSop.getLossRatio()<-0.000001) { //亏
					    		loss++;
					    		totalShouyi += sSop.getLossRatio();
				        	}
				    	}    	
				 	}
				   // System.out.println(earn+":"+stop+":"+loss);
				    int totalsize = earn+stop+loss;
				    if(totalsize > 0) {				    	
				    	ExcelCommon.writeOperationTotalExcelItem(wb,sheet,0 ,stockRow,earn,stop,loss,totalsize,totalShouyi);
				    }
	   					
				    //第一个就是最后一天的
				   // listStockSummaryOrderBy.add(stockSummaryInfo.get(0));
			   		//排序
			   		//Collections.sort(listStockTotalInfoOrderBy); 	   		           
	   			
	   			}
   	   			}
	   			
	   		   		           	   			
				FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);;
				wb.write(fileOStream);
				fileOStream.flush();
				fileIStream.close();
				fileOStream.close();              
				  
				listIndustryStock = null;	          
				 //测试作用
			//	if(stockRow>30)
			//		break;				
	   		}
	}
	
	
	//按行业下生成excel orderby
	public void writePointExcelFormIndustryOrderBy(String filePath, String fileTime) throws SQLException, IOException, ClassNotFoundException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
	{
		
 	    List<StockIndustry> listIndustry = new ArrayList<StockIndustry>(); 	 
	 	//得到当前所有行业
    	listIndustry=sbDao.getStockIndustry();
    	System.out.println("行业个数："+listIndustry.size());
    	
    	XSSFWorkbook wb=null;
    	XSSFSheet sheet =null;
    	String excleFileName=null;
        int flag_first=0;
        int titleRow =0;//记录行业或概念标题行
        sheetCount =0;
    	//先输出到一级行业
        for (int countI=0; countI<listIndustry.size();countI++)	
   		{
        	    //countI第一次	        	
	   			if(flag_first == 0 || stockRow >= 510) {
	   				
	   				flag_first =1;
	   				//创建日期目录
	   				File file = new File(filePath+fileTime);  
	   				System.out.println(fileTime);
	   				if (!file.exists())
	   				{   
	   					 file.mkdir();   
	   				} 
	   				
		   			// 工作区		
		   			wb = new XSSFWorkbook(); 
		   			// 创建第一个sheet     
		   			sheet=  wb.createSheet("allstock");		
		   			sheetCount++;
		   			excleFileName="Stock_Industry_"+fileTime+"_Point_"+sheetCount+".xlsx";	   		    
		   		    stockRow = 1;
		   			//创建excel
		   		    stockDateColumnmap.clear();
		   	 		ExcelCommon.createPointExcel(wb,sheet,filePath,excleFileName,sdDao,stockDateColumnmap);
		   	    	writePointExcelFromMarket(wb,sheet,filePath,excleFileName,fileTime);
		   			FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);;
		   			wb.write(fileOStream);		
		   	        fileOStream.close(); 
		   	        wb=null;
		   	        sheet=null;
	   			}
	   		  	
	   			excleFileName="Stock_Industry_"+fileTime+"_Point_"+sheetCount+".xlsx";
	 			
	   			File file = new File(filePath +fileTime+ "\\"+excleFileName);
				// 输入流   
				FileInputStream fileIStream = new FileInputStream(file);  	
				wb = new XSSFWorkbook(fileIStream);   
				sheet = wb.getSheetAt(0);  
 
				//当前行业
				StockIndustry indu = listIndustry.get(countI);	
				String induCode = indu.getThirdcode();
				String induName = indu.getThirdname();
				if(induCode == null || induName == null)
					continue;				
				stockLogger.logger.fatal("行业："+induName);   		
				System.out.println("行业："+induName);			
				//行业标题 
				stockRow++;
				titleRow = stockRow;		
	  			
				//基本面				
				ExcelCommon.writeExcelItemTitle(wb,sheet,induName,null,stockRow,true);
				
				//所属行业所有股票
	   			List<StockToIndustry> listIndustryStock = new ArrayList<StockToIndustry>();   
	   			listIndustryStock=sbDao.getIndustryToStock(induCode);	   	
	   			stockLogger.logger.debug("行业股票数："+listIndustryStock.size());
	   			
	   			List<String> listName = new ArrayList<String>(); 	 
		   		
	   			for(Iterator ie=listIndustryStock.iterator();ie.hasNext();)
	   			{
	   			//stockRow++;
	   				StockToIndustry toConstock =(StockToIndustry) ie.next();
	   				String stockName = toConstock.getStockName();
	   				listName.add(stockName);
	   			}
	   			//按名称排序
	   			Collections.sort(listName,Collator.getInstance(java.util.Locale.CHINA));
	   			 			
	   				
   				for (int kk=0;kk<listName.size();kk++)
   	   			{
   	   			for (int j=0;j<listIndustryStock.size();j++)	
   	   			{
   	   				
   	   				StockToIndustry toConstock =(StockToIndustry) listIndustryStock.get(j);
   	   				if(!listName.get(kk).equals(toConstock.getStockName()))
   	   					continue;
	   				//stockRow++;  			
	   				String stockFullId = toConstock.getStockFullId();
	   				
	   				//if(!stockFullId.equals("SH600598"))
	   				//	continue;
	   				System.out.println("stockFullId:"+stockFullId);	   				
	   				stockLogger.logger.fatal("****stockFullId："+stockFullId+"****");
	   				
	   				int isTableExist=sdDao.isExistStockTable(stockFullId,ConstantsInfo.TABLE_POINT_STOCK);
	   		    	if(isTableExist == 0){//不存在
	   					stockLogger.logger.fatal("****stockFullId："+stockFullId+"不存在统计表****");
	   					System.out.println(stockFullId+"统计表不存在****");
	   					continue;  
	   				}
	   		    	
	   		    		   		    	
					//其他值
			   		StockOtherInfoValue soiValue=new StockOtherInfoValue(stockFullId,toConstock.getStockName(),0,0,null,null);
			   		//ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow);
			   		
			   		stockRow++;
			   		ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow,0 ,null, true);
	   				
			   		//获取最近统计数据
			   		List<StockPoint> stockPointInfo=new ArrayList<StockPoint>();
				    String endDate = CommonDate.getCurDate();
			        String startDate = CommonDate.getBeforeDay(endDate, 1, 180);
			   		stockPointInfo = spDao.getRecentPointStock(stockFullId,ConstantsInfo.DayDataType,startDate);
			   
			   		int extremeCol = 0;
					isTableExist=sdDao.isExistStockTable(stockFullId,ConstantsInfo.TABLE_SUMMARY_STOCK);
			    	if(isTableExist != 0){//存在
			    		//StockSummary ss = ssDao.getLastSummaryFromSummaryTable(stockFullId);
			    		StockSummary ss = ssDao.getZhiDingSummaryFromSummaryTable(stockFullId, fileTime, ConstantsInfo.DayDataType);
						  
					  //疑极点
				   		if(ss!=null){
				   			String date = ss.getDayEndDate();
					    	
					    	if(stockDateColumnmap.containsKey(date)) {
					        	extremeCol = stockDateColumnmap.get(date);	
					        	
					        	String value="疑极:"+ss.getDayEndValue();
					        	ExcelCommon.writePointExcelItem(wb,sheet,null,value,extremeCol,stockRow,1);
					        				        	
					    	} else{
					    		System.out.println("not exist the day");
							}
				   		} 
					}
			   		
			   		//查找对应位置并写入excel
				    for (int ij=0;ij<stockPointInfo.size();ij++)	
					{
				    	StockPoint sp = stockPointInfo.get(ij);
				    	String date = sp.getExtremeDate().toString();
				    	if(sp!=null && stockDateColumnmap.containsKey(date)) {
				        	extremeCol = stockDateColumnmap.get(date);			
				        	ExcelCommon.writePointExcelItem(wb,sheet,sp,"",extremeCol,stockRow,0);
				    	} else{
				    		System.out.println("not exist the day");
						}   	
				 	}
 					
				    //第一个就是最后一天的
				   // listStockSummaryOrderBy.add(stockSummaryInfo.get(0));
			   		//排序
			   		//Collections.sort(listStockTotalInfoOrderBy); 	   		           
	   			
	   			}
   	   			}
	   			
	   		   		           	   			
				FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);;
				wb.write(fileOStream);
				fileOStream.flush();
				fileIStream.close();
				fileOStream.close();              
				  
				listIndustryStock = null;	          
				 //测试作用
			//	if(stockRow>30)
			//		break;				
	   		}
	        
   		
	}
	
	
	public String getExcelFileName(String excelType,int dateType,String fileTime, String resultType, int count)
	{
		String excleFileName = "";
		switch(dateType)
   		{
   		case ConstantsInfo.DayDataType:
   		default:
   			excleFileName="Stock_"+excelType+"_"+fileTime+"_Day_"+resultType+"_"+sheetCount+".xlsx";
   			break;
   		case ConstantsInfo.WeekDataType:
   			excleFileName="Stock_"+excelType+"_"+fileTime+"_Week_"+resultType+"_"+sheetCount+".xlsx";
   			
   			break;
   		case ConstantsInfo.MonthDataType:
   			excleFileName="Stock_"+excelType+"_"+fileTime+"_Month_"+resultType+"_"+sheetCount+".xlsx";
   			break;
   		}
 		return excleFileName;
	}

	
	
	//按行业下生成excel orderby
	public void writeOperationExcelFormIndustryOrderByAllType(String filePath, String fileTime, int dateType) throws SQLException, IOException, ClassNotFoundException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
	{
		
	 	List<StockIndustry> listIndustry = new ArrayList<StockIndustry>(); 	 
	 	//得到当前所有行业
    	listIndustry=sbDao.getStockIndustry();
    	System.out.println("行业个数："+listIndustry.size());
    	
    	XSSFWorkbook wb=null;
    	XSSFSheet sheet =null;
    	String excleFileName=null;
        int flag_first=0;
        int titleRow =0;//记录行业或概念标题行
        sheetCount =0;
    	//先输出到一级行业
        for (int countI=0; countI<listIndustry.size();countI++)	
   		{
        	//countI第一次	        	
   			if(flag_first == 0 || stockRow >= 510) {
   				
   				flag_first =1;
   				//创建日期目录
   				File file = new File(filePath+fileTime);  
   				System.out.println(fileTime);
   				if (!file.exists())
   				{   
   					 file.mkdir();   
   				} 
   				
	   			// 工作区		
	   			wb = new XSSFWorkbook(); 
	   			// 创建第一个sheet     
	   			sheet=  wb.createSheet("allstock");		
	   			sheetCount++;
	   				
	   			excleFileName= getExcelFileName("Industry",dateType,fileTime,"Operation",sheetCount);	   		    
	   		    stockRow = 1;
	   			//创建excel
	   		    stockDateColumnmap.clear();
	   	 		ExcelCommon.createOperationExcel(wb,sheet,filePath,excleFileName,sdDao,stockDateColumnmap, dateType);
	   	 		writeOperationExcelFromMarket(wb,sheet,filePath,excleFileName,fileTime, dateType);
	   			FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);;
	   			wb.write(fileOStream);		
	   	        fileOStream.close(); 
	   	        wb=null;
	   	        sheet=null;
   			}
   			excleFileName= getExcelFileName("Industry",dateType,fileTime,"Operation",sheetCount);
   			//excleFileName="Stock_Industry_"+fileTime+"_Operation_"+sheetCount+".xlsx";
 			
   			File file = new File(filePath +fileTime+ "\\"+excleFileName);
			// 输入流   
			FileInputStream fileIStream = new FileInputStream(file);  	
			wb = new XSSFWorkbook(fileIStream);   
			sheet = wb.getSheetAt(0);  
 
				//当前行业
			StockIndustry indu = listIndustry.get(countI);	
			String induCode = indu.getThirdcode();
			String induName = indu.getThirdname();
			if(induCode == null || induName == null)
				continue;				
			
			//if(!induCode.equals("620101"))
   			//	continue;
			
			stockLogger.logger.fatal("行业："+induName);   		
			System.out.println("行业："+induName);			
			//行业标题 
			stockRow++;
			titleRow = stockRow;		
  			
			//基本面				
			ExcelCommon.writeExcelItemTitle(wb,sheet,induName,null,stockRow,true);
			
			//所属行业所有股票
   			List<StockToIndustry> listIndustryStock = new ArrayList<StockToIndustry>();   
   			listIndustryStock=sbDao.getIndustryToStock(induCode);	   	
   			stockLogger.logger.debug("行业股票数："+listIndustryStock.size());
   			
   			List<String> listName = new ArrayList<String>(); 	 
	   		
   			for(Iterator ie=listIndustryStock.iterator();ie.hasNext();)
   			{
   			//stockRow++;
   				StockToIndustry toConstock =(StockToIndustry) ie.next();
   				String stockName = toConstock.getStockName();
   				listName.add(stockName);
   			}
   			//按名称排序
   			Collections.sort(listName,Collator.getInstance(java.util.Locale.CHINA));
   			 					
			for (int kk=0;kk<listName.size();kk++)
   			{
   	   			for (int j=0;j<listIndustryStock.size();j++)	
   	   			{
   	   				StockToIndustry toConstock =(StockToIndustry) listIndustryStock.get(j);
   	   				if(!listName.get(kk).equals(toConstock.getStockName()))
   	   					continue;   							
	   				String stockFullId = toConstock.getStockFullId();
	   				
	   			//	if(!stockFullId.equals("SZ002442"))
	   			//		continue;
	   							   				
	   				stockLogger.logger.fatal("****stockFullId："+stockFullId+"****");	   		    	
	   		    	//ssDao.truncateSummaryStock(stockFullId);
	   		    		   		    	
					//其他值
			   		StockOtherInfoValue soiValue=new StockOtherInfoValue(stockFullId,toConstock.getStockName(),0,0,null,null);
			   		//ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow);
			   		
			   		stockRow++;
			   		ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow, 0, null, true);
	   				
			   		int isTableExist=sdDao.isExistStockTable(stockFullId,ConstantsInfo.TABLE_OPERATION_STOCK);
	   		    	if(isTableExist == 0){//不存在
	   					stockLogger.logger.fatal("****stockFullId："+stockFullId+"不存在统计表****");
	   					System.out.println(stockFullId+"统计表不存在****");
	   					continue;  
	   				}
			   		//获取最近统计数据
			   		List<StockOperation> stockOperationInfo=new ArrayList<StockOperation>();
			   		
			   		stockOperationInfo = ssDao.getOperationFromOperationTable(stockFullId, dateType, 30);
			   
			   		int extremeCol = 0;		
			   		int earn=0,stop=0,loss=0;
			   		float totalShouyi=0;
			   		//查找对应位置并写入excel
				    for (int ij=0;ij<stockOperationInfo.size();ij++)	
					{
				    	StockOperation sSop = stockOperationInfo.get(ij);
				    	            
				    	if(sSop!=null && stockDateColumnmap.containsKey(sSop.getOpDate())) {
				        	extremeCol = stockDateColumnmap.get(sSop.getOpDate());			
				        	
				        	String psState = ssDao.getpsStatusFromSummaryTable(stockFullId,sSop.getOpDate());			        	
				        	ExcelCommon.writeOperationExcelItem(wb,sheet,sSop,psState,extremeCol,stockRow);
				        	
				        	//赢
				        	if(sSop.getOpType() == ConstantsInfo.STOP ){ //止
					    		stop++;
					    		totalShouyi += sSop.getStopRatio();
				        	} else if(sSop.getOpType() == ConstantsInfo.SALE && sSop.getEarnRatio()>=0) {
					    		earn++;	
					    		totalShouyi += sSop.getEarnRatio();
				        	} else if(sSop.getOpType() == ConstantsInfo.SALE && sSop.getLossRatio()<-0.000001) { //亏
					    		loss++;
					    		totalShouyi += sSop.getLossRatio();
				        	}
				    	}    	
				 	}
				   // System.out.println(earn+":"+stop+":"+loss);
				    int totalsize = earn+stop+loss;
				    if(totalsize > 0) {				    	
				    	ExcelCommon.writeOperationTotalExcelItem(wb,sheet,0 ,stockRow,earn,stop,loss,totalsize,totalShouyi);
				    }
	   					
				    //第一个就是最后一天的
				   // listStockSummaryOrderBy.add(stockSummaryInfo.get(0));
			   		//排序
			   		//Collections.sort(listStockTotalInfoOrderBy); 	   		           
	   			
	   			}
   	   		}
	   		   		           	   			
			FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);;
			wb.write(fileOStream);
			fileOStream.flush();
			fileIStream.close();
			fileOStream.close();              
			  
			listIndustryStock = null;	          
			 //测试作用
		//	if(stockRow>30)
		//		break;				
   		}
	}
	
	
	//按概念在一级行业下生成excel orderby
	public void writeOperationExcelFormConceptInFirstIndustryOrderByAllType(String filePath, String fileTime, int dateType) throws SQLException, IOException, ClassNotFoundException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
	{
		List<StockConceptInFirstIndustry> listConcept = new ArrayList<StockConceptInFirstIndustry>(); 
	 	List<String> listFirstIndustry = new ArrayList<String>(); 
	
    	//得到当前一级行业code
	 	listFirstIndustry=sbDao.getStockFirstIndustry();    	
    	
    	XSSFWorkbook wb=null;
    	XSSFSheet sheet =null;
    	String excleFileName=null;
        int flag_first=0;
        int titleRow =0;//记录行业或概念标题行
        sheetCount =0;
    	//先输出到一级行业
        for (int countI=0; countI<listFirstIndustry.size();countI++)	
   		{
        	//得到当前一级行业下概念code
        	String firstIndustryId = listFirstIndustry.get(countI);
        	
        	//一级行业名称
        	String firstIndustryName = sbDao.getStockFirstIndustryName(firstIndustryId);        	
        	System.out.println("一级行业："+firstIndustryName);        	
        	
        	//得到概念id
        	listConcept = sbDao.getStockFirstIndustryConceptCode(firstIndustryId);
        	
	    	//每个行业输出到excel一次
	        for (int i=0;i<listConcept.size();i++)	
	   		{
	        	//countI第一次	        	
	   			if(flag_first == 0 || stockRow >= 510) {
	   				
	   				flag_first =1;
	   				//创建日期目录
	   				File file = new File(filePath+fileTime);  
	   				System.out.println(fileTime);
	   				if (!file.exists())
	   				{   
	   					 file.mkdir();   
	   				} 
	   				
		   			// 工作区		
		   			wb = new XSSFWorkbook(); 
		   			// 创建第一个sheet     
		   			sheet=  wb.createSheet("allstock");		
		   			sheetCount++;
		   		//	excleFileName="Stock_Concept_"+fileTime+"_Operation_"+sheetCount+".xlsx";	 
		   			excleFileName= getExcelFileName("Concept",dateType,fileTime,"Operation",sheetCount);
		   		    stockRow = 1;
		   		    stockDateColumnmap.clear();
		   			//创建excel
		   	 		ExcelCommon.createOperationExcel(wb,sheet,filePath,excleFileName,sdDao,stockDateColumnmap,dateType);
		   	 		writeOperationExcelFromMarket(wb,sheet,filePath,excleFileName,fileTime,dateType);
		   			FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);;
		   			wb.write(fileOStream);		
		   	        fileOStream.close(); 
		   	        wb=null;
		   	        sheet=null;
	   			}
	   		  	
	   			excleFileName= getExcelFileName("Concept",dateType,fileTime,"Operation",sheetCount);
	   		//	excleFileName="Stock_Concept_"+fileTime+"_Operation_"+sheetCount+".xlsx";
	 			
	   			File file = new File(filePath +fileTime+ "\\"+excleFileName);
				// 输入流   
				FileInputStream fileIStream = new FileInputStream(file);  	
				wb = new XSSFWorkbook(fileIStream);   
				sheet = wb.getSheetAt(0);  
 
				//当前一级行业
				StockConceptInFirstIndustry scon= listConcept.get(i);	
				 String conceptCode = scon.getConceptCode();
				 String conceptName=scon.getConceptName();
				 if(conceptName==null)
					 continue;
				 
				System.out.println("概念："+conceptName);
				//if(!conceptName.equals("黄金珠宝"))
	   			//	continue;
				
				stockLogger.logger.fatal("概念："+conceptName);			
	  	
				//概念标题 
				stockRow++;
				titleRow = stockRow;
				
				//基本面				
				ExcelCommon.writeExcelItemTitle(wb,sheet,conceptName,null,stockRow,true);
				
	   			//所属概念所有股票
	   			List<StockToConcept> listConceptStock = new ArrayList<StockToConcept>();
	   			listConceptStock=sbDao.getStockToConcept(conceptCode);
	   			stockLogger.logger.debug("概念股票数："+listConceptStock.size());
	   			
	   	
	   			List<String> listName = new ArrayList<String>(); 	 
		   		
	   			for(Iterator ie=listConceptStock.iterator();ie.hasNext();)
	   			{
	   			//stockRow++;
	   				StockToConcept toConstock =(StockToConcept) ie.next();
	   				String stockName = toConstock.getStockName();
	   				listName.add(stockName);
	   			}
	   			//按名称排序
	   			Collections.sort(listName,Collator.getInstance(java.util.Locale.CHINA));
	   			 			
	   				
   				for (int kk=0;kk<listName.size();kk++)
   	   			{
   	   			for (int j=0;j<listConceptStock.size();j++)	
   	   			{
   	   				
   	   				StockToConcept toConstock =(StockToConcept) listConceptStock.get(j);
   	   				if(!listName.get(kk).equals(toConstock.getStockName()))
   	   					continue;
	   				//stockRow++;  			
	   				String stockFullId = toConstock.getStockFullId();
	   				
	   				//if(!stockFullId.equals("SH600598"))
	   				//	continue;
	   				  				
	   				stockLogger.logger.fatal("****stockFullId："+stockFullId+"****");
	   				
	   				int isTableExist=sdDao.isExistStockTable(stockFullId,ConstantsInfo.TABLE_OPERATION_STOCK);
	   		    	if(isTableExist == 0){//不存在
	   					stockLogger.logger.fatal("****stockFullId："+stockFullId+"不存在统计表****");
	   					System.out.println(stockFullId+"统计表不存在****");
	   					continue;  
	   				}
	   		    	
	   		    	//ssDao.truncateSummaryStock(stockFullId);
	   		    		   		    	
					//其他值
			   		StockOtherInfoValue soiValue=new StockOtherInfoValue(stockFullId,toConstock.getStockName(),0,0,null,null);
			   		//ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow);
			   		
			   		stockRow++;
			   		ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow,0, null, true);
	   				
			   		//获取最近统计数据
			   		List<StockOperation> stockOperationInfo=new ArrayList<StockOperation>();
			   		
			   		stockOperationInfo = ssDao.getOperationFromOperationTable(stockFullId, dateType, 30);
			   
			   		int extremeCol = 0;		
			   		int earn=0,stop=0,loss=0;
			   		float totalShouyi=0;
			   		//查找对应位置并写入excel
				    for (int ij=0;ij<stockOperationInfo.size();ij++)	
					{
				    	StockOperation sSop = stockOperationInfo.get(ij);
				    	            
				    	if(sSop!=null && stockDateColumnmap.containsKey(sSop.getOpDate())) {
				        	extremeCol = stockDateColumnmap.get(sSop.getOpDate());			
				        	
				        	//String psState =  ssDao.getpsStatusFromSummaryTable(stockFullId,sSop.getOpDate());
				        	String psState = ""; //ssDao.getpsStatusFromSummaryTable(stockFullId,sSop.getOpDate());
				        	
				        	ExcelCommon.writeOperationExcelItem(wb,sheet,sSop,psState,extremeCol,stockRow);
				        	
				        	//赢
				        	if(sSop.getOpType() == ConstantsInfo.STOP ){ //止
					    		stop++;
					    		totalShouyi += sSop.getStopRatio();
				        	} else if(sSop.getOpType() == ConstantsInfo.SALE && sSop.getEarnRatio()>=0) {
					    		earn++;	
					    		totalShouyi += sSop.getEarnRatio();
				        	} else if(sSop.getOpType() == ConstantsInfo.SALE && sSop.getLossRatio()<-0.000001) { //亏
					    		loss++;
					    		totalShouyi += sSop.getLossRatio();
				        	}
				    	}    	
				 	}
				   // System.out.println(earn+":"+stop+":"+loss);
				    int totalsize = earn+stop+loss;
				    if(totalsize > 0) {				    	
				    	ExcelCommon.writeOperationTotalExcelItem(wb,sheet,0 ,stockRow,earn,stop,loss,totalsize,totalShouyi);
				    }
	   					
				    //第一个就是最后一天的
				   // listStockSummaryOrderBy.add(stockSummaryInfo.get(0));
			   		//排序
			   		//Collections.sort(listStockTotalInfoOrderBy); 	   		           
	   			
	   			}
   	   			}
	   			     	   			
				FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);;
				wb.write(fileOStream);
				fileOStream.flush();
				fileIStream.close();
				fileOStream.close();              
				  
				listConceptStock = null;	          
				 //测试作用
			//	if(stockRow>30)
			//		break;				
	   		}
	        
	      //测试作用
			//if(stockRow>30)
			//	break;	        
   		}
   		
        listConcept = null;
	}
	
	
	
	//按行业下生成excel orderby
	public void writeTotalOperationExcelFormIndustryOrderBy(String filePath, String fileTime) throws SQLException, IOException, ClassNotFoundException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
	{
		
 	    List<StockIndustry> listIndustry = new ArrayList<StockIndustry>(); 	 
	 	//得到当前所有行业
    	listIndustry=sbDao.getStockIndustry();
    	System.out.println("行业个数："+listIndustry.size());
    	
    	XSSFWorkbook wb=null;
    	XSSFSheet sheet =null;
    	String excleFileName=null;
        int flag_first=0;
        int titleRow =0;//记录行业或概念标题行
        sheetCount =0;
    	//先输出到一级行业
        for (int countI=0; countI<listIndustry.size();countI++)	
   		{
        	    //countI第一次	        	
	   			if(flag_first == 0 || stockRow >= 510) {
	   				
	   				flag_first =1;
	   				//创建日期目录
	   				File file = new File(filePath+fileTime);  
	   				System.out.println(fileTime);
	   				if (!file.exists())
	   				{   
	   					 file.mkdir();   
	   				} 
	   				
		   			// 工作区		
		   			wb = new XSSFWorkbook(); 
		   			// 创建第一个sheet     
		   			sheet=  wb.createSheet("allstock");		
		   			sheetCount++;
		   			excleFileName="Stock_Industry_"+fileTime+"_total_operation_"+sheetCount+".xlsx";	   		    
		   		    stockRow = 1;
		   			//创建excel
		   		    stockDateColumnmap.clear();
		   	 		ExcelCommon.createTotalOperationExcel(wb,sheet,filePath,excleFileName,sdDao,stockDateColumnmap,ConstantsInfo.DayDataType);
		   	 		writeTotalOperationExcelFromMarket(wb,sheet,filePath,excleFileName,fileTime,ConstantsInfo.DayDataType);
		   			FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);;
		   			wb.write(fileOStream);		
		   	        fileOStream.close(); 
		   	        wb=null;
		   	        sheet=null;
	   			}
	   		  	
	   			excleFileName="Stock_Industry_"+fileTime+"_total_operation_"+sheetCount+".xlsx";
	 			
	   			File file = new File(filePath +fileTime+ "\\"+excleFileName);
				// 输入流   
				FileInputStream fileIStream = new FileInputStream(file);  	
				wb = new XSSFWorkbook(fileIStream);   
				sheet = wb.getSheetAt(0);  
 
				//当前行业
				StockIndustry indu = listIndustry.get(countI);	
				String induCode = indu.getThirdcode();
				String induName = indu.getThirdname();
				if(induCode == null || induName == null)
					continue;				
				stockLogger.logger.fatal("行业："+induName);   		
				System.out.println("行业："+induName);			
				//行业标题 
				stockRow++;
				titleRow = stockRow;		
	  			
				//基本面				
				ExcelCommon.writeExcelItemTitle(wb,sheet,induName,null,stockRow,true);
				
				//所属行业所有股票
	   			List<StockToIndustry> listIndustryStock = new ArrayList<StockToIndustry>();   
	   			listIndustryStock=sbDao.getIndustryToStock(induCode);	   	
	   			stockLogger.logger.debug("行业股票数："+listIndustryStock.size());
	   			
	   			List<String> listName = new ArrayList<String>(); 	 
		   		
	   			for(Iterator ie=listIndustryStock.iterator();ie.hasNext();)
	   			{
	   			//stockRow++;
	   				StockToIndustry toConstock =(StockToIndustry) ie.next();
	   				String stockName = toConstock.getStockName();
	   				listName.add(stockName);
	   			}
	   			//按名称排序
	   			Collections.sort(listName,Collator.getInstance(java.util.Locale.CHINA));
	   			 			
	   				
	   			for (int kk=0;kk<listName.size();kk++)
	   			{
	   	   			for (int j=0;j<listIndustryStock.size();j++)	
	   	   			{
	   	   				
	   	   				StockToIndustry toConstock =(StockToIndustry) listIndustryStock.get(j);
	   	   				if(!listName.get(kk).equals(toConstock.getStockName()))
	   	   					continue;
		   				//stockRow++;  			
		   				String stockFullId = toConstock.getStockFullId();
		   				
		   			//	if(!stockFullId.equals("SZ002442"))
		   			//		continue;
		   				
		   				//System.out.println("stockFullId:"+stockFullId);	   				
		   				stockLogger.logger.fatal("****stockFullId："+stockFullId+"****");
		   				
		   				int isTableExist=sdDao.isExistStockTable(stockFullId,ConstantsInfo.TABLE_OPERATION_STOCK);
		   		    	if(isTableExist == 0){//不存在
		   					stockLogger.logger.fatal("****stockFullId："+stockFullId+"不存在统计表****");
		   					System.out.println(stockFullId+"统计表不存在****");
		   					continue;  
		   				}
		   		    	
		   		    	//ssDao.truncateSummaryStock(stockFullId);
		   		    		   		    	
						//其他值
				   		StockOtherInfoValue soiValue=new StockOtherInfoValue(stockFullId,toConstock.getStockName(),0,0,null,null);
				   		//ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow);
				   		
				   		stockRow++;
				   		ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow,0,null, true);
		   				
				   		//获取最近统计数据
				   		List<StockOperation> stockOperationInfo=new ArrayList<StockOperation>();
				   		
				   		stockOperationInfo = ssDao.getOperationFromOperationTable(stockFullId, -1, 120);
				   
				   		int extremeCol = 0;		
				   		int earn=0,stop=0,loss=0;
				   		float totalShouyi=0;
				   		//查找对应位置并写入excel
					    for (int ij=0;ij<stockOperationInfo.size();ij++)	
						{
					    	StockOperation sSop = stockOperationInfo.get(ij);
					    	            
					    	if(sSop!=null && stockDateColumnmap.containsKey(sSop.getOpDate())) {
					        	extremeCol = stockDateColumnmap.get(sSop.getOpDate());			
					        	
					        	///String psState = ssDao.getpsStatusFromSummaryTable(stockFullId,sSop.getOpDate());			        	
					        	ExcelCommon.writeTotalOperationExcelItem(wb,sheet,sSop,extremeCol,stockRow);
					        						     
					    	}    	
					 	}	
					
		   			}
	   	   		}
	   			
	   		   		           	   			
				FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);;
				wb.write(fileOStream);
				fileOStream.flush();
				fileIStream.close();
				fileOStream.close();              
				  
				listIndustryStock = null;	          
				 //测试作用
			//	if(stockRow>30)
			//		break;				
	   		}
	}
	
	
	
	//按行业下生成excel orderby
	public void writeTotalOperationExcelFormIndustryOrderByAllType(String filePath, String fileTime,int dateType) throws SQLException, IOException, ClassNotFoundException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
	{
		
 	    List<StockIndustry> listIndustry = new ArrayList<StockIndustry>(); 	 
	 	//得到当前所有行业
    	listIndustry=sbDao.getStockIndustry();
    	System.out.println("行业个数："+listIndustry.size());
    	
    	XSSFWorkbook wb=null;
    	XSSFSheet sheet =null;
    	String excleFileName=null;
        int flag_first=0;
        int titleRow =0;//记录行业或概念标题行
        sheetCount =0;
    	//先输出到一级行业
        for (int countI=0; countI<listIndustry.size();countI++)	
   		{
    	    //countI第一次	        	
   			if(flag_first == 0 || stockRow >= 510) {
   				
   				flag_first =1;
   				//创建日期目录
   				File file = new File(filePath+fileTime);  
   				System.out.println(fileTime);
   				if (!file.exists())
   				{   
   					 file.mkdir();   
   				} 
   				
	   			// 工作区		
	   			wb = new XSSFWorkbook(); 
	   			// 创建第一个sheet     
	   			sheet=  wb.createSheet("allstock");		
	   			sheetCount++;
	   			//excleFileName="Stock_Industry_"+fileTime+"_total_operation_"+sheetCount+".xlsx";
	   			excleFileName= getExcelFileName("Industry",dateType,fileTime,"total_operation",sheetCount);
	   		    stockRow = 1;
	   			//创建excel
	   		    stockDateColumnmap.clear();
	   	 		ExcelCommon.createTotalOperationExcel(wb,sheet,filePath,excleFileName,sdDao,stockDateColumnmap,dateType);
	   	 		writeTotalOperationExcelFromMarket(wb,sheet,filePath,excleFileName,fileTime, dateType);
	   			FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);;
	   			wb.write(fileOStream);		
	   	        fileOStream.close(); 
	   	        wb=null;
	   	        sheet=null;
   			}
	   		  	
	   			excleFileName= getExcelFileName("Industry",dateType,fileTime,"total_operation",sheetCount);
	   			//excleFileName="Stock_Industry_"+fileTime+"_total_operation_"+sheetCount+".xlsx";
	 			
	   			File file = new File(filePath +fileTime+ "\\"+excleFileName);
				// 输入流   
				FileInputStream fileIStream = new FileInputStream(file);  	
				wb = new XSSFWorkbook(fileIStream);   
				sheet = wb.getSheetAt(0);  
 
				//当前行业
				StockIndustry indu = listIndustry.get(countI);	
				String induCode = indu.getThirdcode();
				String induName = indu.getThirdname();
				if(induCode == null || induName == null)
					continue;				
				stockLogger.logger.fatal("行业："+induName);   		
				System.out.println("行业："+induName);			
				//行业标题 
				stockRow++;
				titleRow = stockRow;		
	  			
				//基本面				
				ExcelCommon.writeExcelItemTitle(wb,sheet,induName,null,stockRow,true);
				
				//所属行业所有股票
	   			List<StockToIndustry> listIndustryStock = new ArrayList<StockToIndustry>();   
	   			listIndustryStock=sbDao.getIndustryToStock(induCode);	   	
	   			stockLogger.logger.debug("行业股票数："+listIndustryStock.size());
	   			
	   			List<String> listName = new ArrayList<String>(); 	 
		   		
	   			for(Iterator ie=listIndustryStock.iterator();ie.hasNext();)
	   			{
	   			//stockRow++;
	   				StockToIndustry toConstock =(StockToIndustry) ie.next();
	   				String stockName = toConstock.getStockName();
	   				listName.add(stockName);
	   			}
	   			//按名称排序
	   			Collections.sort(listName,Collator.getInstance(java.util.Locale.CHINA));
	   			 			
	   				
	   			for (int kk=0;kk<listName.size();kk++)
	   			{
	   	   			for (int j=0;j<listIndustryStock.size();j++)	
	   	   			{
	   	   				
	   	   				StockToIndustry toConstock =(StockToIndustry) listIndustryStock.get(j);
	   	   				if(!listName.get(kk).equals(toConstock.getStockName()))
	   	   					continue;
		   				//stockRow++;  			
		   				String stockFullId = toConstock.getStockFullId();
		   				
		   			//	if(!stockFullId.equals("SZ002442"))
		   			//		continue;
		   				   				
		   				stockLogger.logger.fatal("****stockFullId："+stockFullId+"****");		   				
		   		    		   		    	
						//其他值
				   		StockOtherInfoValue soiValue=new StockOtherInfoValue(stockFullId,toConstock.getStockName(),0,0,null,null);
				   		//ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow);
				   		
				   		stockRow++;
				   		ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow, 0, null, true);
		   				
				   		int isTableExist=sdDao.isExistStockTable(stockFullId,ConstantsInfo.TABLE_OPERATION_STOCK);
		   		    	if(isTableExist == 0){//不存在
		   					stockLogger.logger.fatal("****stockFullId："+stockFullId+"不存在统计表****");
		   					System.out.println(stockFullId+"统计表不存在****");
		   					continue;  
		   				}
				   		//获取最近统计数据
				   		List<StockOperation> stockOperationInfo=new ArrayList<StockOperation>();
				   		//stockOperationInfo = ssDao.getOperationFromOperationTable(stockFullId, -1, 120);
				   		
				   		int nums = ConstantsInfo.ExportNum(dateType);
				   		stockOperationInfo = ssDao.getOperationFromOperationTable(stockFullId,dateType, nums);
				 	   			   
				   		int extremeCol = 0;		
				   		int earn=0,stop=0,loss=0;
				   		float totalShouyi=0;
				   	
				   		//查找对应位置并写入excel
					    for (int ij=0;ij<stockOperationInfo.size();ij++)	
						{
					    	StockOperation sSop = stockOperationInfo.get(ij);
					    	if (sSop==null){
					    		continue;
					    	}
					    	
					    	boolean flag = false;
					    	
					    	//先hash查找
					    	if(stockDateColumnmap.containsKey(sSop.getOpDate())) {
					        	extremeCol = stockDateColumnmap.get(sSop.getOpDate());	
					        	flag = true;
					        	
					    	} else {
					    		if (dateType == ConstantsInfo.WeekDataType || dateType == ConstantsInfo.MonthDataType){
					    			//再遍历
					    			for(String key: stockDateColumnmap.keySet()) {
					    				//System.out.println("key:"+key+"data:"+ sSop.getOpDate());
					    				if(CommonDate.isSameDate(key, sSop.getOpDate(), dateType)){
					    					flag = true;
					    					extremeCol = stockDateColumnmap.get(key);
					    					break;
					    				}	
					    			}	
					    		} 
					    	}
					    	
					    	if(flag){
					    		
					    		//将日周月展示
					        	List<StockOperation> stockOperationInfoByDate=new ArrayList<StockOperation>();
					        	stockOperationInfoByDate = ssDao.getOperationFromOperationTableByDate(sSop.getFullId(),sSop.getOpDate());
					        	for (int datesize=0;datesize<stockOperationInfoByDate.size();datesize++)	
								{
					        		StockOperation sSopDate = stockOperationInfoByDate.get(datesize);
					        		ExcelCommon.writeTotalOperationExcelItem(wb,sheet,sSopDate,extremeCol,stockRow);
								}	
					    		
					    	}
					 	}	
					
		   			}
	   	   		}
	   			
	   		   		           	   			
				FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);;
				wb.write(fileOStream);
				fileOStream.flush();
				fileIStream.close();
				fileOStream.close();              
				  
				listIndustryStock = null;	          
				 //测试作用
			//	if(stockRow>30)
			//		break;				
	   		}
	}
	
	public static void main(String[] args) throws SecurityException, SQLException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException {
		PropertyConfigurator.configure("stockConf/log4j_excelWriter.properties");

		stockLogger.logger.fatal("excel stock export start");	
		
		Date startDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
        String dateNowStr = sdf.format(startDate);   
        Connection stockBaseConn = DbConn.getConnDB("stockConf/conn_base_db.ini"); 
        Connection stockDataConn = DbConn.getConnDB("stockConf/conn_data_db.ini"); 
        Connection stockPointConn = DbConn.getConnDB("stockConf/conn_point_db.ini");
        Connection stockSummaryConn = DbConn.getConnDB("stockConf/conn_summary_db.ini");
	   
		StockExcelExporterMain se = new StockExcelExporterMain(stockBaseConn,stockDataConn,stockPointConn,stockSummaryConn);
		//排序
		

		//商品
	//se.writeExcelFormFuturesOrderBy("export\\","2017-08-03", false);
	//	se.writeSummaryExcelFormFuturesOrderBy("export\\",dateNowStr);
	//	se.writePointExcelFormFuturesOrderBy("export\\",dateNowStr);
		//操作
	//se.writeOperationExcelFormFuturesOrderBy("export\\",dateNowStr);	
	//se.writeOperationExcelFormFuturesOrderByAllType("export\\",dateNowStr, ConstantsInfo.WeekDataType);
	//se.writeTotalOperationExcelFormFuturesOrderBy("export\\",dateNowStr);
	
	//股票 分析
	//se.writeExcelFormConceptInFirstIndustryOrderBy("export\\",dateNowStr);
		
	//se.writeExcelFormIndustryOrderBy("export\\","2017-11-17", true);
	se.writeOperationExcelFormIndustryOrderByAllType("export\\","2017-11-17", ConstantsInfo.DayDataType);
	
	//se.writeOperationExcelFormConceptInFirstIndustryOrderBy("export\\",dateNowStr);	
	//se.writeOperationExcelFormConceptInFirstIndustryOrderByAllType("export\\",dateNowStr, ConstantsInfo.DayDataType);
	//se.writeOperationExcelFormIndustryOrderBy("export\\",dateNowStr);	
	//se.writeSummaryExcelFormConceptInFirstIndustryOrderBy("export\\",dateNowStr);
	
//	se.writePointExcelFormIndustryOrderBy("export\\",dateNowStr);
//	se.writePointExcelFormConceptInFirstIndustryOrderBy("export\\",dateNowStr);

	
	
	//se.writeTotalOperationExcelFormIndustryOrderBy("export\\",dateNowStr);
	//se.writeTotalOperationExcelFormIndustryOrderByAllType("export\\","2017-11-03",ConstantsInfo.WeekDataType);
	
	//StockSummary ssu = new StockSummary(0, "","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","");
	//ssu.setFullId("SH603330");
   // ssu.setName("上海天洋");
   // se.ssDao.insertStockSummaryTable(ssu.getFullId(), ssu); 
		
		stockBaseConn.close();
	    stockDataConn.close();
	    stockPointConn.close();
	    stockSummaryConn.close();
		
		Date endDate = new Date();
		//计算两个时间点相差的秒数
		long seconds =(endDate.getTime() - startDate.getTime())/1000;
		System.out.println("总共耗时："+seconds+"秒");
		System.out.println("end");
		stockLogger.logger.fatal("excel stock export end");		
	}

}
