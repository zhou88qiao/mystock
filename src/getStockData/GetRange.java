package getStockData;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.PropertyConfigurator;

import com.timer.stock.TimeStock;
import common.ConstantsInfo;
import common.stockLogger;

import dao.StockBaseDao;
import dao.StockDataDao;
import dao.StockMarket;

import dao.StockPoint;
//�����Ʊÿ���Ƿ���
public class GetRange {
	static StockBaseDao sbDao =new StockBaseDao();
	static StockDataDao sdDao =new StockDataDao();
	
	//����range��Amplitude�ֶ�
	public void addStockRangeAndAmplitude() throws IOException, ClassNotFoundException, SQLException 
	{
		Iterator it;
		int ret=0;
		List<String> listStockFullId = new ArrayList<String>();
		//����	
		
		/*
		listStockFullId = sbDao.getStockMarketFullId();
		System.out.println(listStockFullId.size());
		for(it = listStockFullId.iterator();it.hasNext();)
		{
			String fullId = (String) it.next();
		//	if(!fullId.equals("SH000001")) //SH000001
		//		continue;
			
			int isTableExist=sdDao.isExistStockTable(fullId);
			if(isTableExist==0) {//������
				stockLogger.logger.fatal("fullId not exist"+fullId);
				continue;
			}
			ret = sdDao.addRangeAndAmplitude(fullId);
			if (ret!=0)
				System.out.println("result:"+ret);
		
		}
		
		*/
		
		//����
		listStockFullId=sbDao.getAllStockFullId(ConstantsInfo.StockMarket);
		System.out.println(listStockFullId.size());
		for(it = listStockFullId.iterator();it.hasNext();)
		{
			String fullId = (String) it.next();
			
		//	if(!fullId.equals("SH600000")) //SH600000
		//		continue;
			int isTableExist=sdDao.isExistStockTable(fullId,ConstantsInfo.TABLE_DATA_STOCK);
			if(isTableExist==0){//������
				stockLogger.logger.fatal("fullId not exist"+fullId);
				continue;
			}
			ret = sdDao.addRangeAndAmplitude(fullId);	
			if (ret!=0)
				System.out.println("result:"+ret);
		}
		
	}
	
	//������ʷȫ���Ƿ���
	public void calAllHistoryRangeAndAmplitude() throws IOException, ClassNotFoundException, SQLException 
	{
		Iterator it;
		List<String> listStockFullId = new ArrayList<String>();
	
		/*
		//����		
		listStockFullId = sbDao.getStockMarketFullId();
		System.out.println(listStockFullId.size());
		for(it = listStockFullId.iterator();it.hasNext();)
		{
			String fullId = (String) it.next();
		//	if(!fullId.equals("SH000001")) //SH000001
		//		continue;
			
			List<TimeStock> listTimeStock=new ArrayList<TimeStock>();
			listTimeStock=sdDao.getStockTimeStockOfYear(fullId);	
			sdDao.calAllHistoryDayStockRangeAndAmplitudeData(fullId,listTimeStock);
			sdDao.calAllHistoryWeekStockRangeAndAmplitudeData(fullId,listTimeStock);
			sdDao.calAllHistoryMonthStockRangeAndAmplitudeData(fullId,listTimeStock);
			sdDao.calAllHistorySeasonStockRangeAndAmplitudeData(fullId,listTimeStock);
		}*/
		
		//����		
		listStockFullId=sbDao.getAllStockFullId(ConstantsInfo.StockMarket);	
	
		for(int i = 0; i < listStockFullId.size(); i++)  
        {  
			 String fullId = listStockFullId.get(i);  
			 System.out.println(fullId);
           
			stockLogger.logger.fatal("fullId:"+fullId);
		//	if(!fullId.equals("SH600000")) //SH600000
		//		continue;
			int isTableExist=sdDao.isExistStockTable(fullId,ConstantsInfo.TABLE_DATA_STOCK);
			if(isTableExist==0){//������
				stockLogger.logger.fatal("fullId not exist"+fullId);
				continue;
			}
			List<TimeStock> listTimeStock=new ArrayList<TimeStock>();
			listTimeStock=sdDao.getStockTimeStockOfYear(fullId);	
			sdDao.calAllHistoryDayStockRangeAndAmplitudeData(fullId,listTimeStock);
			sdDao.calAllHistoryWeekStockRangeAndAmplitudeData(fullId,listTimeStock);
			sdDao.calAllHistoryMonthStockRangeAndAmplitudeData(fullId,listTimeStock);
			sdDao.calAllHistorySeasonStockRangeAndAmplitudeData(fullId,listTimeStock);
			
		}
		
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
		PropertyConfigurator.configure("log4j.excelWriter.properties");
		
		stockLogger.logger.fatal("excel stock range start");	
		GetRange gr=new GetRange();
		//�����ֶ�
	//	gr.addStockRangeAndAmplitude();		
		////����ȫ���Ƿ���
		gr.calAllHistoryRangeAndAmplitude();
		System.out.println("end");
		stockLogger.logger.fatal("excel stock range start");	
	}

}
