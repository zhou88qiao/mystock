package stock.analysis;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.PropertyConfigurator;
import common.ConstantsInfo;
import common.stockLogger;
import dao.DbConn;
import dao.StockBaseDao;
import dao.StockData;
import dao.StockDataDao;
import dao.StockInformationDao;
import dao.StockPoint;
import dao.StockPointDao;
import stock.timer.DateStock;

/**
 * @author zhouqiao
 *
 */
public class PointAnalysis {

	private StockDataDao sdDao;
	private StockBaseDao sbDao;
	private StockPointDao spDao;
	static StockInformationDao siDao = new StockInformationDao();

	public PointAnalysis() {

	}

	public PointAnalysis(Connection stockBaseConn, Connection stockDataConn, Connection stockPointConn) {
		this.sbDao = new StockBaseDao(stockBaseConn);
		this.sdDao = new StockDataDao(stockDataConn);
		this.spDao = new StockPointDao(stockPointConn);
	}

	public PointAnalysis(StockBaseDao sbDao, StockDataDao sdDao, StockPointDao spDao) {
		this.sbDao = sbDao;
		this.sdDao = sdDao;
		this.spDao = spDao;
	}

	
	/**
	 * �����㷨ȡ���㣬 ͬʱ����㿪ʼʱ��Ϊǰһ����ʱ��
	 * @param fullId
	 * @param dStock
	 * @param stockType
	 * @param calType
	 * @param analyTime
	 * @return
	 * @throws SecurityException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 */
	public int getStockExtremePointValue(String fullId, DateStock dStock, int stockType, int calType, String analyTime)
			throws SecurityException, IOException, ClassNotFoundException, SQLException, InstantiationException,
			IllegalAccessException, NoSuchFieldException {

		float md5Pri = 0, md10Pri, md5Cur, md10Cur;

		String strPriDate, curDate = null;
		String pointDate = null;
		String firstDate = null;
		String pointStartDate = null, pointEndDate = null;
		String pointTrueStartDate = null;
		String pointLastExtremeDate = null;
		int getStartflag = 0;
		StockData sData;
		float extremePrice = 0;
		String extremeDate = null;
		int flagRiseFall = 0;
		int index = 0;
		StockPoint sp;
		float extremeBeforePrice = 0;
		float extremeCurPrice = 0;
		StockPoint lastSp = null;
		float ratioTmp, ratio = 0;
		List<String> dayDate = null;
		// �����ʼλ��
		int timeStart = 1; 
		int daySize = 0;

		switch (stockType) {
		case ConstantsInfo.DayDataType:
		default:
			dayDate = dStock.getDayDate();
			break;
		case ConstantsInfo.WeekDataType:
			dayDate = dStock.getWeekDate();
			break;
		case ConstantsInfo.MonthDataType:
			dayDate = dStock.getMonthDate();
			break;
		}
		daySize = dayDate.size();
		if (daySize <= 1) {
			stockLogger.logger.fatal("day size less than 1");
			return -1;
		}

		switch (calType) {
		case ConstantsInfo.StockCalAllData:
			timeStart = 1;
			extremeCurPrice = 0;
			break;
		case ConstantsInfo.StockCalCurData:
		default:
			lastSp = spDao.getLastPointStock(fullId, stockType, analyTime);
			// ���¼��㣬�Ƿ�����µļ���
			if (lastSp == null) { 
				stockLogger.logger.fatal("no point data");
				System.out.println("no point data");
				timeStart = 1;
				extremeCurPrice = 0;
			} else {
				System.out.println("extreme end date:" + lastSp.getExtremeDate());

				if (stockType == ConstantsInfo.DayDataType) {
					// ��ֵ
					timeStart = dayDate.indexOf("" + lastSp.getToDate() + ""); 
					extremeCurPrice = lastSp.getExtremePrice();
					stockLogger.logger.fatal("last extreme id:" + lastSp.getId() + " date:" + lastSp.getExtremeDate()
							+ " start:" + lastSp.getFromDate() + " to:" + lastSp.getToDate());
				} else if (stockType == ConstantsInfo.WeekDataType) {

					String spDate = lastSp.getToDate().toString();
					StockData sdata = sdDao.getZhiDingDataStock(fullId, ConstantsInfo.WeekDataType,
							lastSp.getToDate().toString());
					String sdDate = sdata.getDate().toString();

					// ��ʱ���Ѿ�����
					if (!spDate.equals(sdDate)) {
						// ɾ�����������¼��㣬��һ�²��ǽ����
						spDao.delStockPointData(fullId, lastSp.getId());
						// ��ȡ������һ��
						lastSp = spDao.getLastPointStock(fullId, stockType, analyTime);
						// ���¼��㣬�Ƿ�����µļ���
						if (lastSp == null) { 
							timeStart = 1;
							extremeCurPrice = 0;
						} else {
							// ��ȡ�����ڶ�������ʱ���ֵ
							timeStart = dayDate.indexOf("" + lastSp.getToDate().toString() + ""); 
							extremeCurPrice = lastSp.getExtremePrice();
							stockLogger.logger
									.fatal("last extreme id:" + lastSp.getId() + " date:" + lastSp.getExtremeDate()
											+ " start:" + lastSp.getFromDate() + " to:" + lastSp.getToDate());
						}
					} else {
						timeStart = dayDate.indexOf("" + lastSp.getToDate() + ""); 
					}

				} else if (stockType == ConstantsInfo.MonthDataType) {

					String spDate = lastSp.getToDate().toString();

					StockData sdata1 = sdDao.getZhiDingDataStock(fullId, ConstantsInfo.MonthDataType,
							lastSp.getToDate().toString());
					String sdDate = sdata1.getDate().toString();

					// �����ǵ���
					if (!spDate.equals(sdDate)) {
						// ɾ�����������¼��㣬��һ�²��ǽ����
						spDao.delStockPointData(fullId, lastSp.getId());
						// ��ȡ������һ��
						lastSp = spDao.getLastPointStock(fullId, stockType, analyTime);
						// ���¼��㣬�Ƿ�����µļ���
						if (lastSp == null) { 
							timeStart = 1;
							extremeCurPrice = 0;
						} else {
							// ��ȡ�����ڶ�������ʱ���ֵ
							timeStart = dayDate.indexOf("" + lastSp.getToDate().toString() + ""); 
							extremeCurPrice = lastSp.getExtremePrice();
							stockLogger.logger
									.fatal("last extreme id:" + lastSp.getId() + " date:" + lastSp.getExtremeDate()
											+ " start:" + lastSp.getFromDate() + " to:" + lastSp.getToDate());
						}
					} else {
						// ��ֵ
						timeStart = dayDate.indexOf("" + lastSp.getToDate() + ""); 
						extremeCurPrice = lastSp.getExtremePrice();
						stockLogger.logger
								.fatal("last extreme id:" + lastSp.getId() + " date:" + lastSp.getExtremeDate()
										+ " start:" + lastSp.getFromDate() + " to:" + lastSp.getToDate());
					}
				}

			}
			break;
		}

		if (timeStart < 0) {
			stockLogger.logger.error("no found start time");
			return -1;
		}
		// ���㵱ǰ����ʱ��ʼʱ��
		firstDate = dayDate.get(timeStart);
		if (firstDate == null) {
			stockLogger.logger.error("no found start time");
			return -1;
		}

		stockLogger.logger.info("StartDate::" + firstDate);
		float pointFlag = 0;
		float priFlag = 0;
		int curFlag = 0;
		int priceFlag = 0;
		// ma5=ma10�ȱ���ʱ��
		String dateZero = "";
		// ma5=ma10�ȱ���״̬�����ǻ����µ�
		int pointZeroFlag = 0;
		float tmpValue = 0;

		// timeStart+1 //��ǰһ������ ��һ����
		for (index = timeStart + 1; index < daySize; index++) {
			strPriDate = dayDate.get(index - 1);
			curDate = dayDate.get(index);

			md5Pri = sdDao.getStockMaData(fullId, strPriDate, 5, stockType);
			md10Pri = sdDao.getStockMaData(fullId, strPriDate, 10, stockType);
			md5Cur = sdDao.getStockMaData(fullId, curDate, 5, stockType);
			md10Cur = sdDao.getStockMaData(fullId, curDate, 10, stockType);
			stockLogger.logger.debug("curDate:" + curDate);
			stockLogger.logger.debug("md5Pri:" + md5Pri + " md10Pri:" + md10Pri);
			stockLogger.logger.debug("md5Cur:" + md5Cur + " md10Cur:" + md10Cur);
			if (md5Pri == 0 || md10Pri == 0 || md5Cur == 0 || md10Cur == 0) {
				continue;
			}
			// md5Pri- md10Pri>0 ����md5Cur-md10Cur)/(md5Pri- md10Pri
			priFlag = md5Pri - md10Pri;
			// pointFlag = (md5Cur-md5Pri)/(md10Cur- md10Pri);
			if (priFlag == 0){
				pointFlag = 0;
			} else {
				pointFlag = (md5Cur - md10Cur) / (md5Pri - md10Pri);
			}
			
			if (priFlag == 0) {
				// ǰһ��ma5=ma10,����
				if (pointFlag == 0) {
					tmpValue = md5Cur - md10Cur;
					// ȷ�ϵ�ǰ״̬
					if (tmpValue > 0) {
						// ����
						curFlag = 1; 
					} else if (tmpValue < 0){
						// �µ�
						curFlag = 0; 
					} else{
						// �������
						continue; 
					}

					// ���ϴ�״̬һ��
					if (curFlag != pointZeroFlag) { 
						continue;
					} else {
						// ���ϴ�״̬��һ��
						priceFlag = curFlag;
						if (!dateZero.equals("")){
							curDate = dateZero;
						}
					}
				}
				// �µ�����
			} else if (priFlag > 0) { 
				if (pointFlag == 0) { 
					dateZero = curDate;
					pointZeroFlag = 0;
					continue;
				} else if (pointFlag < 0) {
					priceFlag = 0;
				} else{
					priceFlag = 2;
				}
			} else {
				// ��ǰma5=ma10 //��������
				if (pointFlag == 0) { 
					// ����ʱ��
					dateZero = curDate;
					// ��������
					pointZeroFlag = 1;
					continue;
				} else if (pointFlag < 0) {
					// ����
					priceFlag = 1;
				} else {
					priceFlag = 2;
				}
			}

			if (priceFlag == 0 || priceFlag == 1) {
				stockLogger.logger.debug("priceWillFall:" + priceFlag);
				// ���½����
				pointDate = curDate; 
				stockLogger.logger.fatal("***pointDate**:" + pointDate);
				switch (getStartflag) {
				case 2:
				default:
					pointStartDate = pointEndDate;
					pointEndDate = pointDate;
					break;
				/*
				 * case 1: pointEndDate=curDate;//�ڶ��� getStartflag++;
				 * //System.out.println("pointEndDate:"+curDate); break;
				 */
				case 0:
					pointStartDate = firstDate;
					pointEndDate = pointDate;
					getStartflag = getStartflag + 2;
					break;
				}

				if (getStartflag == 2) {
					// �������һ������
					// ���¼��㣬�Ƿ�����µļ���
					if (lastSp == null) { 
						pointTrueStartDate = pointStartDate;
					} else {
						pointLastExtremeDate = lastSp.getExtremeDate().toString();
						pointTrueStartDate = pointLastExtremeDate;
					}
					stockLogger.logger.debug("*pointStartDate:" + pointTrueStartDate + "pointEndDate:" + pointEndDate);
					// ��ǰ���µ�������ǰһ��������
					if (priceFlag == 0)
					{
						sData = sdDao.getMaxStockDataPoint(fullId, pointTrueStartDate, pointEndDate, stockType);
						if (sData == null){
							continue;
						}
						extremePrice = sData.getHighestPrice();
						extremeDate = sData.getDate().toString();
						flagRiseFall = 1;
					} else {
						 //�µ�
						
						sData = sdDao.getMinStockDataPoint(fullId, pointTrueStartDate, pointEndDate, stockType);
						if (sData == null){
							continue;
						}
						extremePrice = sData.getLowestPrice();
						extremeDate = sData.getDate().toString();
						flagRiseFall = 0;
					}

					// ԭ����۸�
					extremeBeforePrice = extremeCurPrice;
					// ��ǰ����۸�
					extremeCurPrice = extremePrice;
					ratioTmp = (extremeCurPrice - extremeBeforePrice) * 100 / extremeCurPrice;
					ratio = (float) (Math.round(ratioTmp * 100)) / 100;

					// ������㵱ǰ���ݣ���һ�β���Ҫ���
					if (calType == ConstantsInfo.StockCalCurData && extremeDate.equals(pointLastExtremeDate)){
						continue;
					}

					stockLogger.logger.fatal("extremeDate:" + extremeDate);
					stockLogger.logger.fatal("extremePrice:" + extremePrice);
					stockLogger.logger.fatal("pointStartDate:" + pointStartDate);
					stockLogger.logger.fatal("pointEndDate:" + pointEndDate);
					stockLogger.logger.fatal("ratio:" + ratio);

					switch (stockType) {
					case ConstantsInfo.DayDataType:
					default:
						stockLogger.logger.fatal("insert day point date:" + extremeDate);
						break;
					case ConstantsInfo.WeekDataType:
						stockLogger.logger.fatal("insert week point date:" + extremeDate);
						break;
					case ConstantsInfo.MonthDataType:
						stockLogger.logger.fatal("insert month point date:" + extremeDate);
						break;
					}

					sp = new StockPoint(0, stockType, Date.valueOf(extremeDate), extremePrice,
							Date.valueOf(pointTrueStartDate), Date.valueOf(pointEndDate), flagRiseFall, ratio, 0);
					lastSp = sp;

					spDao.insertPointStockTable(sp, fullId);
				}

			}
		}
		return 0;
	}

	/**
	 * ����ĳ����Ʊ��������
	 * @param fullId
	 * @param type
	 * @param alyseTimeStart
	 * @param alyseTimeEnd
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws SecurityException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 */
	public void getPiontToTableForSingleStock(String fullId, int type, String alyseTimeStart, String alyseTimeEnd)
			throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException,
			IllegalAccessException, NoSuchFieldException {
		int isTableExist = sdDao.isExistStockTable(fullId, ConstantsInfo.TABLE_DATA_STOCK);
		if (isTableExist == 0) {
			stockLogger.logger.fatal("stock data table no exist");
			return;
		}

		List<String> listStockDays = new ArrayList<String>();
		List<String> listStockWeeks = new ArrayList<String>();
		List<String> listStockMonths = new ArrayList<String>();
		List<String> listStockSeasons = new ArrayList<String>();
		List<String> listStockYears = new ArrayList<String>();

		DateStock dStock = null;
		switch (type) {
		case ConstantsInfo.StockCalAllData:
			listStockDays = sdDao.getDatesFromTo(fullId, ConstantsInfo.DayDataType, null, null);
			listStockWeeks = sdDao.getDatesFromTo(fullId, ConstantsInfo.WeekDataType, null, null);
			listStockMonths = sdDao.getDatesFromTo(fullId, ConstantsInfo.MonthDataType, null, null);
			dStock = new DateStock(listStockDays, listStockWeeks, listStockMonths, listStockSeasons, listStockYears);

			isTableExist = sdDao.isExistStockTable(fullId, ConstantsInfo.TABLE_POINT_STOCK);
			if (isTableExist == 0) {
				stockLogger.logger.fatal("****stockFullId��" + fullId + "�����ڼ����****");
				System.out.println(fullId + "���������****");
				spDao.createStockPointTable(fullId);
			} else {
				// ��ձ�
				spDao.truncatePointStockTable(fullId);
			}
			// �������ݴ�2010-06-04 ���ݿ�������6�·ݿ�ʼ
			getStockExtremePointValue(fullId, dStock, ConstantsInfo.DayDataType, ConstantsInfo.StockCalAllData,
					alyseTimeStart);
			getStockExtremePointValue(fullId, dStock, ConstantsInfo.WeekDataType, ConstantsInfo.StockCalAllData,
					alyseTimeStart);
			getStockExtremePointValue(fullId, dStock, ConstantsInfo.MonthDataType, ConstantsInfo.StockCalAllData,
					alyseTimeStart);

			break;
		case ConstantsInfo.StockCalCurData:
		default:
			listStockDays = sdDao.getDatesFromTo(fullId, ConstantsInfo.DayDataType, null, alyseTimeEnd);
			listStockWeeks = sdDao.getDatesFromTo(fullId, ConstantsInfo.WeekDataType, null, alyseTimeEnd);
			listStockMonths = sdDao.getDatesFromTo(fullId, ConstantsInfo.MonthDataType, null, alyseTimeEnd);
			dStock = new DateStock(listStockDays, listStockWeeks, listStockMonths, listStockSeasons, listStockYears);

			stockLogger.logger.fatal("-----day point-------");
			getStockExtremePointValue(fullId, dStock, ConstantsInfo.DayDataType, ConstantsInfo.StockCalCurData,
					alyseTimeStart);
			stockLogger.logger.fatal("-----week point------");
			getStockExtremePointValue(fullId, dStock, ConstantsInfo.WeekDataType, ConstantsInfo.StockCalCurData,
					alyseTimeStart);
			stockLogger.logger.fatal("-----month point-------");
			getStockExtremePointValue(fullId, dStock, ConstantsInfo.MonthDataType, ConstantsInfo.StockCalCurData,
					alyseTimeStart);
			break;
		}

	}

	/**
	 * ���㼫���������
	 * 
	 * @param type
	 *            ����ȫ�������ǵ�ǰ����
	 * @param listStockFullId
	 * @param stockMarket
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws SecurityException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 */

	public void getPointToTable(int type, int stockMarket, int marketType, String alyseTimeStart, String alyseTimeEnd) {
		List<String> listStockFullId = new ArrayList<String>();

		try {
			if (marketType == ConstantsInfo.StockMarket) {
				listStockFullId = sbDao.getAllStockFullId(marketType);
			} else {
				listStockFullId = sbDao.getAllFuturesFullId(marketType);
			}
		} catch (Exception e) {
			stockLogger.logger.fatal(e.toString());
		}

		for (int i = 0; i < listStockFullId.size(); i++) {
			String fullId = listStockFullId.get(i);
			stockLogger.logger.fatal("stock fullId:" + fullId);

			try {
				getPiontToTableForSingleStock(fullId, type, alyseTimeStart, alyseTimeEnd);
			} catch (Exception e) {
				stockLogger.logger.fatal(e.toString());
			}
		}
	}

	public static void main(String[] args) throws SecurityException, IOException, ClassNotFoundException, SQLException,
			InstantiationException, IllegalAccessException, NoSuchFieldException {

		PropertyConfigurator.configure("StockConf/log4j_point.properties");
		stockLogger.logger.fatal("calculation stock point data start");
		Connection stockBaseConn = DbConn.getConnDB("stockConf/conn_base_db.ini");
		Connection stockDataConn = DbConn.getConnDB("stockConf/conn_data_db.ini");
		Connection stockPointConn = DbConn.getConnDB("stockConf/conn_point_db.ini");

		PointAnalysis pc = new PointAnalysis(stockBaseConn, stockDataConn, stockPointConn);
		Date startDate = new Date(0);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		// ����ȫ����ֵ��
		// pc.getPointToTable(ConstantsInfo.StockCalAllData,ConstantsInfo.ALLMarket);
		// ���㲿�ּ�ֵ��
		pc.getPiontToTableForSingleStock("SH000001", ConstantsInfo.StockCalCurData, "2017-11-10", "2017-11-10");
		// pc.getPiontToTableForSingleStock("SH000001",ConstantsInfo.StockCalAllData,
		// null, null);

		Date endDate = new Date(0);
		long seconds = (endDate.getTime() - startDate.getTime()) / 1000;
		System.out.println("�ܹ���ʱ��" + seconds + "��");

		// pc.getPointToTable(ConstantsInfo.StockCalAllData,ConstantsInfo.ALLMarket,ConstantsInfo.StockMarket,
		// "2016-01-27", "2016-05-26");

		stockBaseConn.close();
		stockPointConn.close();
		stockDataConn.close();

		stockLogger.logger.fatal("calculation stock point data end");
		System.out.println("get point end");

	}

}
