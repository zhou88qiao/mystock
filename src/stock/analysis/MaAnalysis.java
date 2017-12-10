package stock.analysis;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.PropertyConfigurator;

import common.ConstantsInfo;
import common.stockLogger;

import dao.DbConn;
import dao.StockBaseDao;
import dao.StockDataDao;
import dao.StockInformationDao;
import stock.timer.TimeStock;

/**
 * @author zhouqiao
 *
 */

public class MaAnalysis {
	static int KUA_YEAR = 2;
	static StockInformationDao siDao =new StockInformationDao();
	private StockDataDao sdDao;
	private StockBaseDao sbDao;

	public MaAnalysis() {

	}
	
	public MaAnalysis(Connection stockBaseConn, Connection stockDataConn, Connection stockPointConn) {
		sbDao = new StockBaseDao(stockBaseConn);
		sdDao = new StockDataDao(stockDataConn);
	}
	
	public MaAnalysis(StockBaseDao sbDao, StockDataDao sdDao) {
		this.sbDao = sbDao;
		this.sdDao = sdDao;
	}	
	 
	/**
	 * ����ĳ����Ʊ
	 * @param fullId
	 * @param type
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public void calculStockAllDataForSingleStock(String fullId, int type)
			throws IOException, ClassNotFoundException, SQLException {
		// ��������
		if (type == ConstantsInfo.StockCalAllData) {
			List<TimeStock> listTimeStock = new ArrayList<TimeStock>();
			// ���յ���������±�dataType��0��Ϊ1
			sdDao.updateDayTypeForNewStock(fullId);
			listTimeStock = sdDao.getStockTimeStockOfYear(fullId);
			// ������ma5 ma10
			sdDao.calAllHistoryDayStockData(fullId, listTimeStock);
			// ����������ֵ
			sdDao.calAllHistoryWeekStockData(fullId, listTimeStock);
			// ����������ֵ
			sdDao.calAllHistoryMonthStockData(fullId, listTimeStock);
			listTimeStock.clear();
		} else {
			List<String> listDay = new ArrayList<String>();
			// �������
			List<Integer> listWeekPri = new ArrayList<Integer>();
			List<Integer> listWeekCur = new ArrayList<Integer>();
			List<Integer> listMonthPri = new ArrayList<Integer>();
			List<Integer> listMonthCur = new ArrayList<Integer>();
			List<Integer> listYear = new ArrayList<Integer>();

			// ������ ������������һ�죬��������е��������
			// listDay=sdDao.getUnCalculationDay(fullId,"2015-07-20");

			listDay = sdDao.getUnCalculationDay(fullId);
			listYear = sdDao.getUnCalculationYear(fullId);

			if (listDay.size() < 1 || listYear.size() < 1) {
				stockLogger.logger.fatal(fullId + " have no data");
				System.out.println(fullId + " have no data");
				return;
			}

			listWeekPri = sdDao.getUnCalculationWeek(fullId, listYear.get(0));
			listMonthPri = sdDao.getUnCalculationMonth(fullId, listYear.get(0));

			// ����
			if (listYear.size() == KUA_YEAR) {
				stockLogger.logger.fatal(fullId + " have compute more than 1 year");
				listWeekCur = sdDao.getUnCalculationWeek(fullId, listYear.get(1));
				listMonthCur = sdDao.getUnCalculationMonth(fullId, listYear.get(1));
			}

			// ��ʼ��������һ��
			System.out.println(listDay.get(0));
			System.out.println(listDay.get(listDay.size() - 1));

			// ������ma5 ma10 �Ƿ���
			sdDao.callDayStockDataFromDate(fullId, listDay);
			sdDao.calWeekStockDataFromDate(fullId, listWeekPri, listYear.get(0));
			sdDao.calMonthStockDataFromDate(fullId, listMonthPri, listYear.get(0));

			// ����
			if (listYear.size() == KUA_YEAR) {
				sdDao.calWeekStockDataFromDate(fullId, listWeekCur, listYear.get(1));
				sdDao.calMonthStockDataFromDate(fullId, listMonthCur, listYear.get(1));
			}
		}
	}
	 	
	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
		
		System.out.println("cal data start");
		Date startDate = new Date();
		PropertyConfigurator.configure("StockConf/log4j_append.properties");
		stockLogger.logger.fatal("calculation stock append data start");

		Connection stockBaseConn = DbConn.getConnDB("stockConf/conn_base_db.ini");
		Connection stockDataConn = DbConn.getConnDB("stockConf/conn_data_db.ini");
		Connection stockPointConn = DbConn.getConnDB("stockConf/conn_point_db.ini");
		MaAnalysis cas = new MaAnalysis(stockBaseConn, stockDataConn, stockPointConn);

		// cas.calculStockAllData(0);
		// cas.calculStockAllData(1);
		cas.calculStockAllDataForSingleStock("sh000001", ConstantsInfo.StockCalCurData);

		Date endDate = new Date();
		long seconds = (endDate.getTime() - startDate.getTime()) / 1000;
		System.out.println("�ܹ���ʱ��" + seconds + "��");
		System.out.println("cal data end");

		stockBaseConn.close();
		stockDataConn.close();
		stockPointConn.close();
		stockLogger.logger.fatal("�ܹ���ʱ��" + seconds + "��");
		stockLogger.logger.fatal("calculation stock append data end");
				
	}

}
