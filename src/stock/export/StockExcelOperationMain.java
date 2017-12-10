package stock.export;

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

  //��Ʊ���쿪�����̼۹�ϵ ����� ����۹�ϵ
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
		
	//	analyseSingleStockOperation("SZ000333", anaylseDate);
				
		for (int i=0;i<listStockFullId.size();i++)	
		{
			String fullId = listStockFullId.get(i);
			
			//if(!fullId.equals("SH600091"))
   			//	continue;
			analyseSingleStockOperation(fullId, anaylseDate);
		}
		
	}
	
	public int analyseSingleStockOperation(String fullId, String anaylseDate) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException{
		int ret =0;
		int isTableExist = 0;
		isTableExist=sdDao.isExistStockTable(fullId,ConstantsInfo.TABLE_SUMMARY_STOCK);
		if(isTableExist==0){//������
			return -1;
		}
				
		isTableExist=sdDao.isExistStockTable(fullId,ConstantsInfo.TABLE_OPERATION_STOCK);
		if(isTableExist==0){//������
			ssDao.createStockOperationTable(fullId);		
		}
		
		//���һ���������
		StockSummary lastSS;
		lastSS = ssDao.getZhiDingSummaryFromSummaryTable(fullId, anaylseDate, ConstantsInfo.DayDataType);
		if(lastSS == null) {
			stockLogger.logger.fatal("summary no data");
			return -1;
		}
		
		ret = analyseSingleStockOperationDayWeekMonth(fullId,anaylseDate,ConstantsInfo.DayDataType, lastSS);
		if (ret == ConstantsInfo.BUY){
			//�� ����  �ܲ��ټ��
			ret = analyseSingleStockOperationDayWeekMonth(fullId,anaylseDate,ConstantsInfo.WeekDataType, lastSS);
			if (ret == ConstantsInfo.BUY){
				ret = analyseSingleStockOperationDayWeekMonth(fullId,anaylseDate,ConstantsInfo.MonthDataType, lastSS);
			}
		}
		
		return 0;
	}
	
	
	public int analyseSingleStockOperationDayWeekMonth(String fullId, String anaylseDate, int dateType, StockSummary lastSS) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException{
			
		stockLogger.logger.fatal("***"+fullId+"***"+ anaylseDate+ "***"+dateType);		
		//���һ�콻������
		StockData lastSD;
		lastSD = sdDao.getZhiDingDataStock(fullId,dateType,anaylseDate);
		if(lastSD == null) {
			stockLogger.logger.fatal("stock data no data");
			return -1;
		}
		
		int flagContinue = 0;
		int trend = 0;
		//ʱ��� �ɵ�
		int dateGapPs =0; 
		//ǰ����ʱ��
		String priPointDate = "";
		//���Ƶ�ʱ��
		String PSDate = "";
		//��ǰʱ��
		String curDate = "";
		float endValue = 0;

		float octype = 0;
		switch(dateType)
		{
		case ConstantsInfo.DayDataType:
		default:	
			if(lastSS.getDayPSValueGap().contains("-")) //��
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
			
			//������1 �ɵ�ʱ���>2
			dateGapPs = sdDao.getStockDataDateGap(fullId,PSDate,curDate,dateType);
			if(dateGapPs>2) {
				stockLogger.logger.fatal("dataGap more than 2");	
				flagContinue = 1;	
			}
			//�������̼۹�ϵ
			octype = getStockOpenCloseValueInfo(lastSD.getOpeningPrice(),lastSD.getClosingPrice());
		
			break;
		case ConstantsInfo.WeekDataType:
			if(lastSS.getWeekPSValueGap().contains("-")) //��
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
			if(lastSS.getMonthPSValueGap().contains("-")) //��
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
		
		//���һ��������¼
		StockOperation lastOp;	
		lastOp = ssDao.getLastOperation(fullId, anaylseDate, dateType);
		//�ظ�����
		if(lastOp!=null && lastOp.getOpDate().equals(curDate)){
			System.out.println("double anysle");
			return -1;
		}	
					
		//����������2 ������͵���ǰ�߼�ֵ��ʱ���>5
		int dataGap = sdDao.getStockDataDateGap(fullId,priPointDate,PSDate,dateType);		
		StockOperation sop = null;
		StockOperation sop_week = null;
		StockOperation sop_month = null;
				
		//StockOperation curOp;
		
		//����������3 �½����
		if(trend == 1) {			
			//�����>5  ���̼�>���̼�   
			//�������>5
			if((dateType == ConstantsInfo.DayDataType && dataGap >= 5 && octype <= 0) 
					|| (dataGap >= 5 && (dateType == ConstantsInfo.WeekDataType || dateType == ConstantsInfo.MonthDataType))) {			
				//��һ�������˵�		
				if(lastOp != null && (lastOp.getOpType() == ConstantsInfo.BUY)){
					//continue;
										
					//����6 ������͵�<ֹ��� ��Ҫ���� �� ͬ����������
					if (dateType == ConstantsInfo.DayDataType && lastSD.getLowestPrice() < lastOp.getStopValue()) {
						stockLogger.logger.fatal("update day buy operation, update before time:"+ lastOp.getOpDate() + " to cur time:" +curDate);
						sop = new StockOperation(fullId, lastOp.getAssId(), curDate, lastSD.getOpeningPrice(),endValue,0,0,0,0,ConstantsInfo.BUY,dateType);								
						ssDao.updateStockOperationTable(sop, fullId, lastOp.getId());
						StockOperation lastOp_week = ssDao.getCurOperation(fullId, lastOp.getOpDate(), ConstantsInfo.WeekDataType);		
						if(lastOp_week != null && lastOp_week.getOpType() == ConstantsInfo.BUY) {
							stockLogger.logger.fatal("update week buy operation");
							sop_week = new StockOperation(fullId, lastOp_week.getAssId(), curDate, lastSD.getOpeningPrice(),endValue,0,0,0,0,ConstantsInfo.BUY,ConstantsInfo.WeekDataType);					
							ssDao.updateStockOperationTable(sop, fullId, lastOp_week.getId());
						}
						StockOperation lastOp_month = ssDao.getCurOperation(fullId, lastOp.getOpDate(), ConstantsInfo.MonthDataType);		
						if(lastOp_month != null && lastOp_month.getOpType() == ConstantsInfo.BUY) {
							stockLogger.logger.fatal("update month buy operation");
							sop_week = new StockOperation(fullId, lastOp_month.getAssId(), curDate, lastSD.getOpeningPrice(),endValue,0,0,0,0,ConstantsInfo.BUY,ConstantsInfo.MonthDataType);					
							ssDao.updateStockOperationTable(sop, fullId, lastOp_month.getId());
						}											
					} else {
						return -1;
					}
					
				} else {
					if (dateType == ConstantsInfo.DayDataType){
						//������͵�  ���̼۴������̼�
						StockData ps_data = sdDao.getZhiDingDataStock(fullId,ConstantsInfo.DayDataType,PSDate);
						if(ps_data == null) {	
							return -1;
						}
						//�������̼۹�ϵ
						float ps_octype = getStockOpenCloseValueInfo(ps_data.getOpeningPrice(),ps_data.getClosingPrice());				
						int op_status = getOpStatus(dateGapPs, ps_octype);
						//sop = new StockOperation(fullId,0, curDate,lastSD.getOpeningPrice(),lastSD.getLowestPrice(),0,0,0,0,ConstantsInfo.BUY,1);
						if (op_status > 0) {
							stockLogger.logger.fatal("insert day buy boperation");
							sop = new StockOperation(fullId, op_status, curDate, lastSD.getOpeningPrice(),endValue,0,0,0,0,ConstantsInfo.BUY,dateType);								
						}
					} else if (dateType == ConstantsInfo.WeekDataType){
						stockLogger.logger.fatal("insert week buy operation");
						 //��
						sop = new StockOperation(fullId, ConstantsInfo.OP_STATUS_1, curDate, lastSD.getOpeningPrice(),endValue,0,0,0,0,ConstantsInfo.BUY,dateType);
					} else if (dateType == ConstantsInfo.MonthDataType){
						stockLogger.logger.fatal("insert month buy operation");
						 //��
						sop = new StockOperation(fullId, ConstantsInfo.OP_STATUS_1, curDate, lastSD.getOpeningPrice(),endValue,0,0,0,0,ConstantsInfo.BUY,dateType);
					}
					if(sop!=null)
						ssDao.insertStockOperationTable(sop);
					return ConstantsInfo.BUY;
				}			
			} else if(lastOp!=null && lastSD.getLowestPrice()<lastOp.getStopValue()){				
				// �� �� �£� ��������ͬ��
				if (dateType !=  ConstantsInfo.DayDataType){						
					return -1;
				}
			
				//ֹ���				
				//��һ�������ֹ�����˵�
				if(lastOp.getOpType() != ConstantsInfo.BUY) {					
					return -1;				
				} 					
					
				stockLogger.logger.fatal("insert day stop operation");				
				float stopRation= getStockOpenCloseValueInfo(lastOp.getStopValue(),lastOp.getBuyValue());
				sop = new StockOperation(fullId,  ConstantsInfo.OP_STATUS_3, curDate,0,lastOp.getStopValue(), 0,0,stopRation,0, ConstantsInfo.STOP,dateType);	
				ssDao.insertStockOperationTable(sop);
				//������ ��ֹ���			
				StockOperation lastOp_week = ssDao.getCurOperation(fullId,  lastOp.getOpDate(), ConstantsInfo.WeekDataType);		
				if(lastOp_week != null && lastOp_week.getOpType() == ConstantsInfo.BUY) {
					stockLogger.logger.fatal("insert week stop operation");	
					stopRation= getStockOpenCloseValueInfo(lastOp_week.getStopValue(),lastOp_week.getBuyValue());
					sop_week = new StockOperation(fullId,  ConstantsInfo.OP_STATUS_3, curDate,0,lastOp_week.getStopValue(), 0,0, stopRation,0, ConstantsInfo.STOP,ConstantsInfo.WeekDataType);					
					ssDao.insertStockOperationTable(sop_week);
				} 			
				StockOperation lastOp_month = ssDao.getCurOperation(fullId,  lastOp.getOpDate(), ConstantsInfo.MonthDataType);					
				if(lastOp_month != null && lastOp_month.getOpType() == ConstantsInfo.BUY) {
					stockLogger.logger.fatal("insert month stop operation");	
					stopRation= getStockOpenCloseValueInfo(lastOp_month.getStopValue(),lastOp_month.getBuyValue());
					sop_month = new StockOperation(fullId,  ConstantsInfo.OP_STATUS_3, curDate,0,lastOp.getStopValue(), 0,0, stopRation,0, ConstantsInfo.STOP,ConstantsInfo.MonthDataType);						
					ssDao.insertStockOperationTable(sop_month);
				}
				return ConstantsInfo.STOP;	
			} 
			
		} else {
			// �� �� �£� ��������ͬ��
			if (dateType !=  ConstantsInfo.DayDataType){						
				return -1;
			}
					
			//������ 
			if(lastOp!=null && dataGap >= 5 && octype >= 0){					
				//��һ�������ֹ�����˵�
				if(lastOp.getOpType() != ConstantsInfo.BUY) {
					return -1;
				} 
				stockLogger.logger.fatal("insert day sale operation");
				StockOperation lastOp_week = ssDao.getCurOperation(fullId,  lastOp.getOpDate(), ConstantsInfo.WeekDataType);
				StockOperation lastOp_month = ssDao.getCurOperation(fullId,  lastOp.getOpDate(), ConstantsInfo.MonthDataType);
				
				float earnOrlose = getStockOpenCloseValueInfo(lastSD.getOpeningPrice(),lastOp.getBuyValue());
				
				if(earnOrlose>0) { // // �� ֹ �� Ӯ ֹ �� 					
					sop = new StockOperation(fullId,  ConstantsInfo.OP_STATUS_4,curDate,0,0,lastSD.getOpeningPrice(),earnOrlose,0, 0, ConstantsInfo.SALE,dateType);
					//211 -> 444  200->400  110->410
					ssDao.insertStockOperationTable(sop);
					
					if(lastOp_week != null && lastOp_week.getOpType() == ConstantsInfo.BUY) {
						stockLogger.logger.fatal("insert week sale operation");
						earnOrlose = getStockOpenCloseValueInfo(lastSD.getOpeningPrice(),lastOp_week.getBuyValue());
						sop_week = new StockOperation(fullId,  ConstantsInfo.OP_STATUS_4,curDate,0,0,lastSD.getOpeningPrice(),earnOrlose,0, 0, ConstantsInfo.SALE,ConstantsInfo.WeekDataType);
						ssDao.insertStockOperationTable(sop_week);
					}
					
					if(lastOp_month != null && lastOp_month.getOpType() == ConstantsInfo.BUY) {
						stockLogger.logger.fatal("insert month sale operation");
						earnOrlose = getStockOpenCloseValueInfo(lastSD.getOpeningPrice(),lastOp_month.getBuyValue());
						sop_month = new StockOperation(fullId,  ConstantsInfo.OP_STATUS_4,curDate,0,0,lastSD.getOpeningPrice(),earnOrlose,0, 0, ConstantsInfo.SALE,ConstantsInfo.MonthDataType);
						ssDao.insertStockOperationTable(sop_month);
					}
								
				} else {
					sop = new StockOperation(fullId,  ConstantsInfo.OP_STATUS_4,curDate,0,0,lastSD.getOpeningPrice(),0,0,earnOrlose, ConstantsInfo.SALE,dateType);
					
					ssDao.insertStockOperationTable(sop);				
					if(lastOp_week != null && lastOp_week.getOpType() == ConstantsInfo.BUY) {
						stockLogger.logger.fatal("insert week sale operation");
						sop_week = new StockOperation(fullId,  ConstantsInfo.OP_STATUS_4/*lastOp.getAssId()*/,curDate,0,0,lastSD.getOpeningPrice(),0,0,earnOrlose, ConstantsInfo.SALE,ConstantsInfo.WeekDataType);
						ssDao.insertStockOperationTable(sop_week);
					}
					
					if(lastOp_month != null && lastOp_month.getOpType() == ConstantsInfo.BUY) {
						stockLogger.logger.fatal("insert month sale operation");
						sop_month = new StockOperation(fullId,  ConstantsInfo.OP_STATUS_4/*lastOp.getAssId()*/,curDate,0,0,lastSD.getOpeningPrice(),0,0,earnOrlose, ConstantsInfo.SALE,ConstantsInfo.MonthDataType);
						ssDao.insertStockOperationTable(sop_month);
					}				
				}	
				
				return ConstantsInfo.SALE;
			}
		}
				
		return -1;
	}
	
	
	
	public int analyseSingleStockOperationOrigin(String fullId, String anaylseDate, int dateType) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException{
			
		//if(!fullId.equals("SH601111"))
		//	continue;
			
		stockLogger.logger.fatal("*****"+fullId+"*****" + dateType);
		int isTableExist = 0;
		isTableExist=sdDao.isExistStockTable(fullId,ConstantsInfo.TABLE_SUMMARY_STOCK);
		if(isTableExist==0){//������
			return -1;
		}
				
		isTableExist=sdDao.isExistStockTable(fullId,ConstantsInfo.TABLE_OPERATION_STOCK);
		if(isTableExist==0){//������
			ssDao.createStockOperationTable(fullId);		
		}
		
		//���һ���������
		StockSummary lastSS;
	
		lastSS = ssDao.getZhiDingSummaryFromSummaryTable(fullId, anaylseDate, dateType);
		if(lastSS == null) {
			stockLogger.logger.fatal("summary no data");
			return -1;
		}
		
		//���һ�콻������
		StockData lastSD;
		lastSD = sdDao.getZhiDingDataStock(fullId,dateType,anaylseDate);
		if(lastSD == null) {
			stockLogger.logger.fatal("stock data no data");
			return -1;
		}
		
		int flagContinue = 0;
		int trend = 0;
		//ʱ��� �ɵ�
		int dateGapPs =0; 
		//ǰ����ʱ��
		String priPointDate = "";
		//���Ƶ�ʱ��
		String PSDate = "";
		//��ǰʱ��
		String curDate = "";
		float endValue = 0;
		
		//�������̼۹�ϵ
		float octype = getStockOpenCloseValueInfo(lastSD.getOpeningPrice(),lastSD.getClosingPrice());
		
		switch(dateType)
		{
		case ConstantsInfo.DayDataType:
		default:	
			if(lastSS.getDayPSValueGap().contains("-")) //��
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
			
			//������1 �ɵ�ʱ���>2
			dateGapPs = sdDao.getStockDataDateGap(fullId,PSDate,curDate,dateType);
			if(dateGapPs>2) {
				stockLogger.logger.fatal("dataGap more than 2");	
				flagContinue = 1;	
			}
					
			break;
		case ConstantsInfo.WeekDataType:
			if(lastSS.getWeekPSValueGap().contains("-")) //��
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
			if(lastSS.getMonthPSValueGap().contains("-")) //��
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
		
		//���һ��������¼
		StockOperation lastOp;	
		lastOp = ssDao.getLastOperation(fullId, anaylseDate, dateType);
		//�ظ�����
		if(lastOp!=null && lastOp.getOpDate().equals(curDate)){
			System.out.println("double anysle");
			return -1;
		}	
					
		//����������2 ������͵���ǰ�߼�ֵ��ʱ���>5
		int dataGap = sdDao.getStockDataDateGap(fullId,priPointDate,PSDate,dateType);		
		StockOperation sop = null;
				
		int flag_of_insert_update = 0; // 0 insert  1 update
		StockOperation curOp;
		
		//����������3 �½����
		if(trend == 1) {			
			//�����>5  ���̼�>���̼�   
			//�������>5
			if((dataGap >= 5 && (dateType == ConstantsInfo.DayDataType) && octype <= 0) 
					|| (dataGap >= 5 && ((dateType == ConstantsInfo.WeekDataType) || (dateType == ConstantsInfo.MonthDataType)))) {			
				//��һ�������˵�		
				if(lastOp != null && (lastOp.getOpType() == ConstantsInfo.BUY)){
					//continue;
										
					//stockLogger.logger.fatal("cur optype"+ConstantsInfo.BUY+"-pri optype"+lastOp.getOpType());
					//if (dateType == ConstantsInfo.DayDataType){
					//	continue;
					//}
					
					//����6 ������͵�<ֹ��� ��Ҫ����
					if (lastSD.getLowestPrice() < lastOp.getStopValue()) {
						flag_of_insert_update = 1;
						stockLogger.logger.fatal("update buy operation");
					} else {
						return -1;
					}
					
				} else {
					flag_of_insert_update = 0;
					//�� �жϵ����Ƿ�Ϊ���
					if (dateType == ConstantsInfo.WeekDataType){						
						//�����Ƿ�Ϊ���
						curOp = ssDao.getCurOperation(fullId, curDate, ConstantsInfo.DayDataType);
						if(curOp!=null && curOp.getOpType() == ConstantsInfo.BUY){
							stockLogger.logger.fatal("insert week buy operation");
						} else {						
							stockLogger.logger.fatal("week buy but not day buy");
							return -1;
						}
						
					} else if (dateType == ConstantsInfo.MonthDataType){
						//�� �жϵ����Ƿ�Ϊ���
						curOp = ssDao.getCurOperation(fullId, curDate, ConstantsInfo.WeekDataType);
						if(curOp!=null && curOp.getOpType() == ConstantsInfo.BUY){
							stockLogger.logger.fatal("insert month buy operation");
						} else {
							stockLogger.logger.fatal("month buy but not  week buy");
							return -1;
						}
					} else {
						stockLogger.logger.fatal("insert day buy operation");
					}
				}
				
				//������͵�  ���̼۴������̼�
				StockData ps_data = sdDao.getZhiDingDataStock(fullId,ConstantsInfo.DayDataType,PSDate);
				if(ps_data == null) {	
					return -1;
				}
				//�������̼۹�ϵ
				float ps_octype = getStockOpenCloseValueInfo(ps_data.getOpeningPrice(),ps_data.getClosingPrice());				
				int op_status = getOpStatus(dateGapPs, ps_octype);
				//sop = new StockOperation(fullId,0, curDate,lastSD.getOpeningPrice(),lastSD.getLowestPrice(),0,0,0,0,ConstantsInfo.BUY,1);
				if (op_status > 0) {
					sop = new StockOperation(fullId, op_status, curDate, lastSD.getOpeningPrice(),endValue,0,0,0,0,ConstantsInfo.BUY,dateType);								
				}
				
			//}	else if(lastOp!=null && lastSD.getLowestPrice()<lastOp.getStopValue() && lastSD.getClosingPrice() < lastOp.getStopValue() ){
			} else if(lastOp!=null && lastSD.getLowestPrice()<lastOp.getStopValue() ){
				//ֹ���				
				//��һ�������ֹ�����˵�
				if(lastOp.getOpType() == ConstantsInfo.SALE|| lastOp.getOpType() == ConstantsInfo.STOP ) {					
					if (dateType == ConstantsInfo.DayDataType){	
						stockLogger.logger.fatal("cur optype"+ConstantsInfo.STOP+"-pri optype"+lastOp.getOpType());					
						flag_of_insert_update = 1;
						stockLogger.logger.fatal("update stop operation");
						
						float stopRation= getStockOpenCloseValueInfo(lastOp.getStopValue(),lastOp.getBuyValue() );
						sop = new StockOperation(fullId, ConstantsInfo.OP_STATUS_3 /* lastOp.getAssId()*/, curDate,0,lastOp.getStopValue(), 0,0,stopRation,0, ConstantsInfo.STOP,dateType);	
					}
				
				} else {					
					//���С����͵㣬�жϽ���������̼۴��ڿ��̼ۣ��������µ����죬�������ֹ��
					if (octype <= 0) {
						stockLogger.logger.fatal("update BUY operation");
						flag_of_insert_update = 1;
						//�������
						sop = new StockOperation(fullId, lastOp.getAssId(), curDate, lastSD.getOpeningPrice(),endValue,0,0,0,0,ConstantsInfo.BUY,dateType);							
					} else {
						flag_of_insert_update = 0;
						stockLogger.logger.fatal("insert stop operation");
						
						float stopRation= getStockOpenCloseValueInfo(lastOp.getStopValue(),lastOp.getBuyValue() );
						sop = new StockOperation(fullId,  ConstantsInfo.OP_STATUS_3, curDate,0,lastOp.getStopValue(), 0,0,stopRation,0, ConstantsInfo.STOP,dateType);	
						//������ ��ֹ���
					}
				}			
			}
			
		} else {		
			//���� 
			if(lastOp!=null && dataGap >= 5 && octype >= 0){
				
				//��һ�������ֹ�����˵�
				if(lastOp.getOpType() == ConstantsInfo.SALE|| lastOp.getOpType() == ConstantsInfo.STOP ) {
					stockLogger.logger.fatal("cur optype"+ConstantsInfo.SALE+"-pri optype"+lastOp.getOpType());
					if (dateType == ConstantsInfo.DayDataType){
						flag_of_insert_update = 1;
						stockLogger.logger.fatal("update sale operation");
					}
					//continue;
				} else {
					flag_of_insert_update = 0;
					stockLogger.logger.fatal("insert sale operation");
				}
				
				float earnOrlose = getStockOpenCloseValueInfo(lastSD.getOpeningPrice(),lastOp.getBuyValue());
				if(earnOrlose>0) { // // �� ֹ �� Ӯ ֹ �� 
					sop = new StockOperation(fullId,  ConstantsInfo.OP_STATUS_4/*lastOp.getAssId()*/,curDate,0,0,lastSD.getOpeningPrice(),earnOrlose,0, 0, ConstantsInfo.SALE,dateType);
					//sop = new StockOperation(fullId,  ConstantsInfo.OP_STATUS_4/*lastOp.getAssId()*/,curDate,0,0,lastSD.getOpeningPrice(),earnOrlose,0, 0, ConstantsInfo.SALE,ConstantsInfo.WeekDataType);
					//sop = new StockOperation(fullId,  ConstantsInfo.OP_STATUS_4/*lastOp.getAssId()*/,curDate,0,0,lastSD.getOpeningPrice(),earnOrlose,0, 0, ConstantsInfo.SALE,ConstantsInfo.MonthDataType);
				} else {
					sop = new StockOperation(fullId,  ConstantsInfo.OP_STATUS_4/*lastOp.getAssId()*/,curDate,0,0,lastSD.getOpeningPrice(),0,0,earnOrlose, ConstantsInfo.SALE,dateType);
					//sop_week = new StockOperation(fullId,  ConstantsInfo.OP_STATUS_4/*lastOp.getAssId()*/,curDate,0,0,lastSD.getOpeningPrice(),0,0,earnOrlose, ConstantsInfo.SALE,ConstantsInfo.WeekDataType);
					//sop_month = new StockOperation(fullId,  ConstantsInfo.OP_STATUS_4/*lastOp.getAssId()*/,curDate,0,0,lastSD.getOpeningPrice(),0,0,earnOrlose, ConstantsInfo.SALE,ConstantsInfo.MonthDataType);
				}
				
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
	
	public int getOpStatus(int dateGapPs, float ps_octype){
		if (dateGapPs == 0) { //���� �����Ƶ��غ�
			return ConstantsInfo.OP_STATUS_1;
		} else { 
			if (ps_octype>0){
				return ConstantsInfo.OP_STATUS_2;
			}
		}
		return 0;
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
		 
        seOp.analyseSingleStockOperation("SH600091","2017-11-22");
 

        //����
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
