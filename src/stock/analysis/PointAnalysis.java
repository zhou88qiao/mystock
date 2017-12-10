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
	 * 按新算法取极点， 同时交叉点开始时间为前一极点时间
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
		// 计算初始位置
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
			// 重新计算，是否存在新的极点
			if (lastSp == null) { 
				stockLogger.logger.fatal("no point data");
				System.out.println("no point data");
				timeStart = 1;
				extremeCurPrice = 0;
			} else {
				System.out.println("extreme end date:" + lastSp.getExtremeDate());

				if (stockType == ConstantsInfo.DayDataType) {
					// 初值
					timeStart = dayDate.indexOf("" + lastSp.getToDate() + ""); 
					extremeCurPrice = lastSp.getExtremePrice();
					stockLogger.logger.fatal("last extreme id:" + lastSp.getId() + " date:" + lastSp.getExtremeDate()
							+ " start:" + lastSp.getFromDate() + " to:" + lastSp.getToDate());
				} else if (stockType == ConstantsInfo.WeekDataType) {

					String spDate = lastSp.getToDate().toString();
					StockData sdata = sdDao.getZhiDingDataStock(fullId, ConstantsInfo.WeekDataType,
							lastSp.getToDate().toString());
					String sdDate = sdata.getDate().toString();

					// 周时间已经更新
					if (!spDate.equals(sdDate)) {
						// 删除此条，重新计算，万一月不是交叉点
						spDao.delStockPointData(fullId, lastSp.getId());
						// 再取倒数第一条
						lastSp = spDao.getLastPointStock(fullId, stockType, analyTime);
						// 重新计算，是否存在新的极点
						if (lastSp == null) { 
							timeStart = 1;
							extremeCurPrice = 0;
						} else {
							// 再取倒数第二条结束时间初值
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

					// 正好是当月
					if (!spDate.equals(sdDate)) {
						// 删除此条，重新计算，万一月不是交叉点
						spDao.delStockPointData(fullId, lastSp.getId());
						// 再取倒数第一条
						lastSp = spDao.getLastPointStock(fullId, stockType, analyTime);
						// 重新计算，是否存在新的极点
						if (lastSp == null) { 
							timeStart = 1;
							extremeCurPrice = 0;
						} else {
							// 再取倒数第二条结束时间初值
							timeStart = dayDate.indexOf("" + lastSp.getToDate().toString() + ""); 
							extremeCurPrice = lastSp.getExtremePrice();
							stockLogger.logger
									.fatal("last extreme id:" + lastSp.getId() + " date:" + lastSp.getExtremeDate()
											+ " start:" + lastSp.getFromDate() + " to:" + lastSp.getToDate());
						}
					} else {
						// 初值
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
		// 计算当前数据时起始时间
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
		// ma5=ma10先保留时间
		String dateZero = "";
		// ma5=ma10先保留状态是上涨还是下跌
		int pointZeroFlag = 0;
		float tmpValue = 0;

		// timeStart+1 //从前一个极点 后一天算
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
			// md5Pri- md10Pri>0 并且md5Cur-md10Cur)/(md5Pri- md10Pri
			priFlag = md5Pri - md10Pri;
			// pointFlag = (md5Cur-md5Pri)/(md10Cur- md10Pri);
			if (priFlag == 0){
				pointFlag = 0;
			} else {
				pointFlag = (md5Cur - md10Cur) / (md5Pri - md10Pri);
			}
			
			if (priFlag == 0) {
				// 前一个ma5=ma10,保留
				if (pointFlag == 0) {
					tmpValue = md5Cur - md10Cur;
					// 确认当前状态
					if (tmpValue > 0) {
						// 上涨
						curFlag = 1; 
					} else if (tmpValue < 0){
						// 下跌
						curFlag = 0; 
					} else{
						// 继续相等
						continue; 
					}

					// 与上次状态一致
					if (curFlag != pointZeroFlag) { 
						continue;
					} else {
						// 与上次状态不一致
						priceFlag = curFlag;
						if (!dateZero.equals("")){
							curDate = dateZero;
						}
					}
				}
				// 下跌趋势
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
				// 当前ma5=ma10 //上涨趋势
				if (pointFlag == 0) { 
					// 保留时间
					dateZero = curDate;
					// 保留趋势
					pointZeroFlag = 1;
					continue;
				} else if (pointFlag < 0) {
					// 上涨
					priceFlag = 1;
				} else {
					priceFlag = 2;
				}
			}

			if (priceFlag == 0 || priceFlag == 1) {
				stockLogger.logger.debug("priceWillFall:" + priceFlag);
				// 更新交叉点
				pointDate = curDate; 
				stockLogger.logger.fatal("***pointDate**:" + pointDate);
				switch (getStartflag) {
				case 2:
				default:
					pointStartDate = pointEndDate;
					pointEndDate = pointDate;
					break;
				/*
				 * case 1: pointEndDate=curDate;//第二次 getStartflag++;
				 * //System.out.println("pointEndDate:"+curDate); break;
				 */
				case 0:
					pointStartDate = firstDate;
					pointEndDate = pointDate;
					getStartflag = getStartflag + 2;
					break;
				}

				if (getStartflag == 2) {
					// 计算最后一个极点
					// 重新计算，是否存在新的极点
					if (lastSp == null) { 
						pointTrueStartDate = pointStartDate;
					} else {
						pointLastExtremeDate = lastSp.getExtremeDate().toString();
						pointTrueStartDate = pointLastExtremeDate;
					}
					stockLogger.logger.debug("*pointStartDate:" + pointTrueStartDate + "pointEndDate:" + pointEndDate);
					// 当前算下跌，计算前一个是上涨
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
						 //下跌
						
						sData = sdDao.getMinStockDataPoint(fullId, pointTrueStartDate, pointEndDate, stockType);
						if (sData == null){
							continue;
						}
						extremePrice = sData.getLowestPrice();
						extremeDate = sData.getDate().toString();
						flagRiseFall = 0;
					}

					// 原极点价格
					extremeBeforePrice = extremeCurPrice;
					// 当前极点价格
					extremeCurPrice = extremePrice;
					ratioTmp = (extremeCurPrice - extremeBeforePrice) * 100 / extremeCurPrice;
					ratio = (float) (Math.round(ratioTmp * 100)) / 100;

					// 如果计算当前数据，第一次不需要入库
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
	 * 计算某个股票极点数据
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
				stockLogger.logger.fatal("****stockFullId：" + fullId + "不存在极点表****");
				System.out.println(fullId + "极点表不存在****");
				spDao.createStockPointTable(fullId);
			} else {
				// 清空表
				spDao.truncatePointStockTable(fullId);
			}
			// 极点数据从2010-06-04 数据库最早是6月份开始
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
	 * 计算极点数据入库
	 * 
	 * @param type
	 *            计算全部，还是当前数据
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

		// 计算全部极值点
		// pc.getPointToTable(ConstantsInfo.StockCalAllData,ConstantsInfo.ALLMarket);
		// 计算部分极值点
		pc.getPiontToTableForSingleStock("SH000001", ConstantsInfo.StockCalCurData, "2017-11-10", "2017-11-10");
		// pc.getPiontToTableForSingleStock("SH000001",ConstantsInfo.StockCalAllData,
		// null, null);

		Date endDate = new Date(0);
		long seconds = (endDate.getTime() - startDate.getTime()) / 1000;
		System.out.println("总共耗时：" + seconds + "秒");

		// pc.getPointToTable(ConstantsInfo.StockCalAllData,ConstantsInfo.ALLMarket,ConstantsInfo.StockMarket,
		// "2016-01-27", "2016-05-26");

		stockBaseConn.close();
		stockPointConn.close();
		stockDataConn.close();

		stockLogger.logger.fatal("calculation stock point data end");
		System.out.println("get point end");

	}

}
