package stock.analysis;

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


/**
 * @author zhouqiao
 *
 */
public class OperationAnalysis {
	private StockDataDao sdDao;
	private StockBaseDao sbDao;
	private StockPointDao spDao;
	private StockSummaryDao ssDao;
	
	public OperationAnalysis(Connection stockBaseConn, Connection stockDataConn, Connection stockPointConn,
			Connection stockSummaryConn) {
		this.sbDao = new StockBaseDao(stockBaseConn);
		this.sdDao = new StockDataDao(stockDataConn);
		this.spDao = new StockPointDao(stockPointConn);
		this.ssDao = new StockSummaryDao(stockSummaryConn);
	}

	public OperationAnalysis(StockBaseDao sbDao, StockDataDao sdDao, StockPointDao spDao, StockSummaryDao ssDao) {
		this.sbDao = sbDao;
		this.sdDao = sdDao;
		this.spDao = spDao;
		this.ssDao = ssDao;
	}

	/**
	 * ��Ʊ���쿪�����̼۹�ϵ ����� ����۹�ϵ
	 * @param openPrice
	 * @param closePrice
	 * @return
	 */
	public float getStockOpenCloseValueInfo(float openPrice, float closePrice) {
		return (openPrice - closePrice) / closePrice;
	}
	
	public void analyseStockOperationAll(int marketType,String anaylseDate) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException 
	{
		List<String> listStockFullId = new ArrayList<String>();
			
		if(marketType == ConstantsInfo.StockMarket ) {
			listStockFullId = sbDao.getAllStockFullId(marketType);
		} else {
			listStockFullId = sbDao.getAllFuturesFullId(marketType);
		}
		
	//	analyseSingleStockOperation("SZ000333", anaylseDate);
				
		for (int i = 0; i < listStockFullId.size(); i++) {
			String fullId = listStockFullId.get(i);
			// if(!fullId.equals("SH600091"))
			// continue;
			analyseSingleStockOperation(fullId, anaylseDate);
		}
		
	}
	
	public int analyseSingleStockOperation(String fullId, String anaylseDate) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException{
		int ret =0;
		int isTableExist = 0;
		isTableExist=sdDao.isExistStockTable(fullId,ConstantsInfo.TABLE_SUMMARY_STOCK);
		if(isTableExist==0){
			return -1;
		}
				
		isTableExist=sdDao.isExistStockTable(fullId,ConstantsInfo.TABLE_OPERATION_STOCK);
		if(isTableExist==0){
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
	
	
	public int analyseSingleStockOperationDayWeekMonth(String fullId, String anaylseDate, int dateType,
			StockSummary lastSS) throws IOException, ClassNotFoundException, SQLException, SecurityException,
			InstantiationException, IllegalAccessException, NoSuchFieldException {

		stockLogger.logger.fatal("***" + fullId + "***" + anaylseDate + "***" + dateType);
		// ���һ�콻������
		StockData lastSD;
		lastSD = sdDao.getZhiDingDataStock(fullId, dateType, anaylseDate);
		if (lastSD == null) {
			stockLogger.logger.fatal("stock data no data");
			return -1;
		}

		int flagContinue = 0;
		int trend = 0;
		// ʱ��� �ɵ�
		int dateGapPs = 0;
		// ǰ����ʱ��
		String priPointDate = "";
		// ���Ƶ�ʱ��
		String pSDate = "";
		// ��ǰʱ��
		String curDate = "";
		float endValue = 0;

		float octype = 0;
		switch (dateType) {
		case ConstantsInfo.DayDataType:
		default:
			// ��
			if (lastSS.getDayPSValueGap().contains(ConstantsInfo.STOCK_DOWN)) {
				trend = 1;
			} else {
				trend = 0;
			}

			priPointDate = lastSS.getDayStartDate();
			pSDate = lastSS.getDayEndDate();
			curDate = lastSS.getDayCurDate();
			if (lastSS.getDayEndValue() == null || lastSS.getDayEndValue().length() <= 0) {
				flagContinue = 1;
				stockLogger.logger.fatal("stock summary day data null");
			} else {
				endValue = Float.parseFloat(lastSS.getDayEndValue());
			}

			// ������1 �ɵ�ʱ���>2
			dateGapPs = sdDao.getStockDataDateGap(fullId, pSDate, curDate, dateType);
			if (dateGapPs > ConstantsInfo.PS_DATE_GAP) {
				stockLogger.logger.fatal("dataGap more than 2");
				flagContinue = 1;
			}
			// �������̼۹�ϵ
			octype = getStockOpenCloseValueInfo(lastSD.getOpeningPrice(), lastSD.getClosingPrice());

			break;
		case ConstantsInfo.WeekDataType:
			// ��
			if (lastSS.getWeekPSValueGap().contains(ConstantsInfo.STOCK_DOWN)) {
				trend = 1;
			} else {
				trend = 0;
			}

			priPointDate = lastSS.getWeekStartDate();
			pSDate = lastSS.getWeekEndDate();
			curDate = lastSS.getWeekCurDate();

			if (lastSS.getWeekEndValue() == null || lastSS.getWeekEndValue().length() <= 0) {
				flagContinue = 1;
				stockLogger.logger.fatal("stock summary week data null");
			} else {
				endValue = Float.parseFloat(lastSS.getWeekEndValue());
			}
			break;
		case ConstantsInfo.MonthDataType:
			// ��
			if (lastSS.getMonthPSValueGap().contains(ConstantsInfo.STOCK_DOWN)) {
				trend = 1;
			} else {
				trend = 0;
			}

			priPointDate = lastSS.getMonthStartDate();
			pSDate = lastSS.getMonthEndDate();
			curDate = lastSS.getMonthCurDate();
			if (lastSS.getMonthEndValue() == null || lastSS.getMonthEndValue().length() <= 0) {
				flagContinue = 1;
				stockLogger.logger.fatal("stock summary month data null");
			} else {
				endValue = Float.parseFloat(lastSS.getMonthEndValue());
			}
			break;
		}

		if (flagContinue == 1) {
			return -1;
		}

		// ���һ��������¼
		StockOperation lastOp;
		lastOp = ssDao.getLastOperation(fullId, anaylseDate, dateType);
		// �ظ�����
		if (lastOp != null && lastOp.getOpDate().equals(curDate)) {
			System.out.println("double anysle");
			return -1;
		}

		// ����������2 ������͵���ǰ�߼�ֵ��ʱ���>5
		int dataGap = sdDao.getStockDataDateGap(fullId, priPointDate, pSDate, dateType);
		StockOperation sop = null;
		StockOperation sopWeek = null;
		StockOperation sopMonth = null;

		// StockOperation curOp;
		StockOperation lastOpWeek = null;
		StockOperation lastOpMonth = null;

		// ����������3 �½����
		if (trend == 1) {
			/*
			 * �����>5 ���̼�>���̼� �������>5
			 * 
			 */
			if ((dateType == ConstantsInfo.DayDataType && dataGap >= ConstantsInfo.SP_DATE_GAP && octype <= 0)
					|| (dataGap >= 5 && (dateType == ConstantsInfo.WeekDataType || dateType == ConstantsInfo.MonthDataType))) {
				// ��һ�������˵�
				if (lastOp != null && (lastOp.getOpType() == ConstantsInfo.BUY)) {

					// ����6 ������͵�<ֹ��� ��Ҫ���� �� ͬ����������
					if (dateType == ConstantsInfo.DayDataType && lastSD.getLowestPrice() < lastOp.getStopValue()) {
						stockLogger.logger.fatal("update day buy operation, update before time:" + lastOp.getOpDate()
								+ " to cur time:" + curDate);
						sop = new StockOperation(fullId, lastOp.getAssId(), curDate, lastSD.getOpeningPrice(), endValue,
								0, 0, 0, 0, ConstantsInfo.BUY, dateType);
						ssDao.updateStockOperationTable(sop, fullId, lastOp.getId());
						lastOpWeek = ssDao.getCurOperation(fullId, lastOp.getOpDate(), ConstantsInfo.WeekDataType);
						if (lastOpWeek != null && lastOpWeek.getOpType() == ConstantsInfo.BUY) {
							stockLogger.logger.fatal("update week buy operation");
							sopWeek = new StockOperation(fullId, lastOpWeek.getAssId(), curDate,
									lastSD.getOpeningPrice(), endValue, 0, 0, 0, 0, ConstantsInfo.BUY,
									ConstantsInfo.WeekDataType);
							ssDao.updateStockOperationTable(sop, fullId, lastOpWeek.getId());
						}
						lastOpMonth = ssDao.getCurOperation(fullId, lastOp.getOpDate(), ConstantsInfo.MonthDataType);
						if (lastOpMonth != null && lastOpMonth.getOpType() == ConstantsInfo.BUY) {
							stockLogger.logger.fatal("update month buy operation");
							sopMonth = new StockOperation(fullId, lastOpMonth.getAssId(), curDate,
									lastSD.getOpeningPrice(), endValue, 0, 0, 0, 0, ConstantsInfo.BUY,
									ConstantsInfo.MonthDataType);
							ssDao.updateStockOperationTable(sop, fullId, lastOpMonth.getId());
						}
					} else {
						return -1;
					}

				} else {
					if (dateType == ConstantsInfo.DayDataType) {
						// ������͵� ���̼۴������̼�
						StockData psData = sdDao.getZhiDingDataStock(fullId, ConstantsInfo.DayDataType, pSDate);
						if (psData == null) {
							return -1;
						}
						// �������̼۹�ϵ
						float psOpencloseType = getStockOpenCloseValueInfo(psData.getOpeningPrice(),
								psData.getClosingPrice());
						int opStatus = getOpStatus(dateGapPs, psOpencloseType);
						if (opStatus > 0) {
							stockLogger.logger.fatal("insert day buy boperation");
							sop = new StockOperation(fullId, opStatus, curDate, lastSD.getOpeningPrice(), endValue, 0,
									0, 0, 0, ConstantsInfo.BUY, dateType);
						}
					} else if (dateType == ConstantsInfo.WeekDataType) {
						stockLogger.logger.fatal("insert week buy operation");
						// ��
						sop = new StockOperation(fullId, ConstantsInfo.OP_STATUS_1, curDate, lastSD.getOpeningPrice(),
								endValue, 0, 0, 0, 0, ConstantsInfo.BUY, dateType);
					} else if (dateType == ConstantsInfo.MonthDataType) {
						stockLogger.logger.fatal("insert month buy operation");
						// ��
						sop = new StockOperation(fullId, ConstantsInfo.OP_STATUS_1, curDate, lastSD.getOpeningPrice(),
								endValue, 0, 0, 0, 0, ConstantsInfo.BUY, dateType);
					}
					if (sop != null) {
						ssDao.insertStockOperationTable(sop);
					}
					return ConstantsInfo.BUY;
				}
			} else if (lastOp != null && lastSD.getLowestPrice() < lastOp.getStopValue()) {
				// �� �� �£� ��������ͬ��
				if (dateType != ConstantsInfo.DayDataType) {
					return -1;
				}

				// ֹ���
				// ��һ�������ֹ�����˵�
				if (lastOp.getOpType() != ConstantsInfo.BUY) {
					return -1;
				}

				stockLogger.logger.fatal("insert day stop operation");
				float stopRation = getStockOpenCloseValueInfo(lastOp.getStopValue(), lastOp.getBuyValue());
				sop = new StockOperation(fullId, ConstantsInfo.OP_STATUS_3, curDate, 0, lastOp.getStopValue(), 0, 0,
						stopRation, 0, ConstantsInfo.STOP, dateType);
				ssDao.insertStockOperationTable(sop);
				// ������ ��ֹ���
				lastOpWeek = ssDao.getCurOperation(fullId, lastOp.getOpDate(), ConstantsInfo.WeekDataType);
				if (lastOpWeek != null && lastOpWeek.getOpType() == ConstantsInfo.BUY) {
					stockLogger.logger.fatal("insert week stop operation");
					stopRation = getStockOpenCloseValueInfo(lastOpWeek.getStopValue(), lastOpWeek.getBuyValue());
					sopWeek = new StockOperation(fullId, ConstantsInfo.OP_STATUS_3, curDate, 0,
							lastOpWeek.getStopValue(), 0, 0, stopRation, 0, ConstantsInfo.STOP,
							ConstantsInfo.WeekDataType);
					ssDao.insertStockOperationTable(sopWeek);
				}
				lastOpMonth = ssDao.getCurOperation(fullId, lastOp.getOpDate(), ConstantsInfo.MonthDataType);
				if (lastOpMonth != null && lastOpMonth.getOpType() == ConstantsInfo.BUY) {
					stockLogger.logger.fatal("insert month stop operation");
					stopRation = getStockOpenCloseValueInfo(lastOpMonth.getStopValue(), lastOpMonth.getBuyValue());
					sopMonth = new StockOperation(fullId, ConstantsInfo.OP_STATUS_3, curDate, 0, lastOp.getStopValue(),
							0, 0, stopRation, 0, ConstantsInfo.STOP, ConstantsInfo.MonthDataType);
					ssDao.insertStockOperationTable(sopMonth);
				}
				return ConstantsInfo.STOP;
			}

		} else {
			// �� �� �£� ��������ͬ��
			if (dateType != ConstantsInfo.DayDataType) {
				return -1;
			}
			// ������
			if (lastOp != null && dataGap >= ConstantsInfo.SP_DATE_GAP && octype >= 0) {
				// ��һ�������ֹ�����˵�
				if (lastOp.getOpType() != ConstantsInfo.BUY) {
					return -1;
				}
				stockLogger.logger.fatal("insert day sale operation");
				lastOpWeek = ssDao.getCurOperation(fullId, lastOp.getOpDate(), ConstantsInfo.WeekDataType);
				lastOpMonth = ssDao.getCurOperation(fullId, lastOp.getOpDate(), ConstantsInfo.MonthDataType);

				float earnOrlose = getStockOpenCloseValueInfo(lastSD.getOpeningPrice(), lastOp.getBuyValue());

				// �� ֹ �� Ӯ ֹ ��
				if (earnOrlose > 0) {
					sop = new StockOperation(fullId, ConstantsInfo.OP_STATUS_4, curDate, 0, 0, lastSD.getOpeningPrice(),
							earnOrlose, 0, 0, ConstantsInfo.SALE, dateType);
					// ״̬ת����211 -> 444 200->400 110->410
					ssDao.insertStockOperationTable(sop);

					if (lastOpWeek != null && lastOpWeek.getOpType() == ConstantsInfo.BUY) {
						stockLogger.logger.fatal("insert week sale operation");
						earnOrlose = getStockOpenCloseValueInfo(lastSD.getOpeningPrice(), lastOpWeek.getBuyValue());
						sopWeek = new StockOperation(fullId, ConstantsInfo.OP_STATUS_4, curDate, 0, 0,
								lastSD.getOpeningPrice(), earnOrlose, 0, 0, ConstantsInfo.SALE,
								ConstantsInfo.WeekDataType);
						ssDao.insertStockOperationTable(sopWeek);
					}

					if (lastOpMonth != null && lastOpMonth.getOpType() == ConstantsInfo.BUY) {
						stockLogger.logger.fatal("insert month sale operation");
						earnOrlose = getStockOpenCloseValueInfo(lastSD.getOpeningPrice(), lastOpMonth.getBuyValue());
						sopMonth = new StockOperation(fullId, ConstantsInfo.OP_STATUS_4, curDate, 0, 0,
								lastSD.getOpeningPrice(), earnOrlose, 0, 0, ConstantsInfo.SALE,
								ConstantsInfo.MonthDataType);
						ssDao.insertStockOperationTable(sopMonth);
					}

				} else {
					sop = new StockOperation(fullId, ConstantsInfo.OP_STATUS_4, curDate, 0, 0, lastSD.getOpeningPrice(),
							0, 0, earnOrlose, ConstantsInfo.SALE, dateType);

					ssDao.insertStockOperationTable(sop);
					if (lastOpWeek != null && lastOpWeek.getOpType() == ConstantsInfo.BUY) {
						stockLogger.logger.fatal("insert week sale operation");
						sopWeek = new StockOperation(fullId, ConstantsInfo.OP_STATUS_4, curDate, 0, 0,
								lastSD.getOpeningPrice(), 0, 0, earnOrlose, ConstantsInfo.SALE,
								ConstantsInfo.WeekDataType);
						ssDao.insertStockOperationTable(sopWeek);
					}

					if (lastOpMonth != null && lastOpMonth.getOpType() == ConstantsInfo.BUY) {
						stockLogger.logger.fatal("insert month sale operation");
						sopMonth = new StockOperation(fullId, ConstantsInfo.OP_STATUS_4, curDate, 0, 0,
								lastSD.getOpeningPrice(), 0, 0, earnOrlose, ConstantsInfo.SALE,
								ConstantsInfo.MonthDataType);
						ssDao.insertStockOperationTable(sopMonth);
					}
				}

				return ConstantsInfo.SALE;
			}
		}

		return -1;
	}

	public int getOpStatus(int dateGapPs, float psOcType) {
		// ���� �����Ƶ��غ�
		if (dateGapPs == 0) {
			return ConstantsInfo.OP_STATUS_1;
		} else {
			if (psOcType > 0) {
				return ConstantsInfo.OP_STATUS_2;
			}
		}
		return 0;
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
        OperationAnalysis seOp = new OperationAnalysis(stockBaseConn,stockDataConn,stockPointConn,stockSummaryConn);
		 
        seOp.analyseSingleStockOperation("SH600091","2017-11-22");
     // seOp.analyseStockOperationAll(ConstantsInfo.StockMarket,"2017-11-07");   

        stockBaseConn.close();
	    stockDataConn.close();
	    stockPointConn.close();
	    stockSummaryConn.close();

	}

}
