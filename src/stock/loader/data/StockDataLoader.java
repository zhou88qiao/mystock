package stock.loader.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.PropertyConfigurator;
import common.ConstantsInfo;
import common.stockLogger;
import dao.DbConn;
import dao.StockBaseDao;
import dao.StockDataDao;
import dao.StockPointDao;
import dao.StockSummaryDao;
import stock.analysis.MaAnalysis;
import stock.analysis.PointAnalysis;

/**
 * @author zhouqiao
 *
 */
public class StockDataLoader {

	private StockBaseDao sbDao;
	private StockDataDao sdDao;
	private StockPointDao spDao;
	private StockSummaryDao ssDao;

	public StockDataLoader(Connection stockBaseConn, Connection stockDataConn, Connection stockPointConn,
			Connection stockSummaryConn) {
		this.sbDao = new StockBaseDao(stockBaseConn);
		this.sdDao = new StockDataDao(stockDataConn);
		this.spDao = new StockPointDao(stockPointConn);
		this.ssDao = new StockSummaryDao(stockSummaryConn);
	}

	public StockDataLoader(StockBaseDao sbDao, StockDataDao sdDao, StockPointDao spDao, StockSummaryDao ssDao) {
		this.sbDao = sbDao;
		this.sdDao = sdDao;
		this.spDao = spDao;
		this.ssDao = ssDao;
	}

	/**
	 * 以行为单位读取文件，常用于读面向行的格式化文件 步骤：1：先获得文件句柄 2：获得文件句柄当做是输入一个字节码流，需要对这个输入流进行读取
	 * 3：读取到输入流后，需要读取生成字节流 4：一行一行的输出。readline()。 备注：需要考虑的是异常情况
	 * @param filePath
	 * @param stockId
	 * @param type
	 * @return
	 */
	public static int readFileByLines(String filePath, String stockId, int type) {
		int stockDataNum = 0;
		long stockVolume = 0;
		try {
			String encoding = "GBK";
			File file = new File(filePath);
			FileWriter fw = null;
			String wfPath = null;

			if (ConstantsInfo.STOCK_HAITONG_SHANGZHENG.equals(stockId)) {
				wfPath = "StockLoadFile\\" + "SH000001" + ".sql";
			} else {
				wfPath = "StockLoadFile\\" + stockId + ".sql";
			}
			String tdx = "数据来源:通达信 ";
			fw = new FileWriter(wfPath);
			if (file.isFile() && file.exists()) { 
				// 考虑到编码格式
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;

				int line = 0;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					line++;
					// 排除第一 第二 最后一行数据
					if (line == 1 || line == 2){
						continue;
					}

					// 只保留5列 （时间 开盘价 收盘价 最高价 最低价） 时间改成年月日 跟mysql保持一致
					// 去除最后一行 长度为8
					if (lineTxt.length() > 36)
					{
						String[] sourceStrArray = lineTxt.split("\t");						
						stockVolume = Long.parseLong(sourceStrArray[5]);
						
						// 不管交易量是否为零
						if (type == ConstantsInfo.FuturesMarket) {
							stockDataNum++;
							fw.write(lineTxt);
							fw.write("\r\n");
						} else {
							// 停牌股票不能导入
							if (stockVolume > 0) {
								stockDataNum++;
								fw.write(lineTxt);
								fw.write("\r\n");
							}
						}
					}
				}
				read.close();
				fw.close();
			} else {
				System.out.println("找不到指定的文件");
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}

		return stockDataNum;

	}

	/**
	 * 读取导入文件第一行的时间
	 * @return
	 */
	public static String readFileFirstLineDate() {
		String stockTime = "";
		try {
			String encoding = "GBK";
			String wfPath = null;
			wfPath = "StockData\\" + "SH999999.txt";
			File file = new File(wfPath);

			if (file.isFile() && file.exists()) {
				// 考虑到编码格式
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				int line = 0;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					line++;
					// 排除第一 第二 最后一行数据
					if (line == 1 || line == 2){
						continue;
					}
					stockTime = lineTxt.substring(0, 10);
					System.out.println(stockTime);
					break;
				}
				read.close();
			} else {
				System.out.println("找不到指定的文件");
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}

		return stockTime;
	}

	
	/**
	 * 读取目录文件名
	 * @param path
	 * @return
	 */
	private static List<String> getListFiles(String path) {
		List<String> lstFileNames = new ArrayList<String>();
		File file = new File(path);
		int fileNums = 0;

		if (file.isDirectory()) {
			File[] tmpList = file.listFiles();
			for (int i = 0; i < tmpList.length; i++) {
				if (tmpList[i].isFile()) {
					fileNums++;
					lstFileNames.add(tmpList[i].getPath());
				}
			}
		}

		System.out.println("fileNums:" + fileNums);
		return lstFileNames;
	}



	/**
	 * 1 导入当天交易数据 计算ma 涨幅 ，如果是新增加股票，需要创建交易表，极点表，计算极点
	 * 
	 * @param type
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws SecurityException
	 */
	public int loadAllDataInfile() {

		MaAnalysis cas = new MaAnalysis(sbDao, sdDao);

		String dirPath = "StockData\\";
		List<String> lstStockFileNames = null;
		lstStockFileNames = getListFiles(dirPath);
		String stockTime = "";
		int stockDataNum = 0;
		String stockSourceData = null;
		String stockName = null;

		int isTableExist = 0;
		String loadfilePath = "";
		String curPath = System.getProperty("user.dir");
		// 转义将\ 转为/
		curPath = curPath.replaceAll("\\\\", "/"); 
		int id = 0;

		// 判断重复导入 读取SH99999.txt文件
		stockTime = readFileFirstLineDate();

		try {
			id = sdDao.getDataValueIsExist("SH000001", stockTime);
			if (id > 0) {
				return 1;
			}
			// 清空stock_load_FullId表，作为下一步分析时用
			sbDao.truncateStockLoadFullId(ConstantsInfo.StockMarket);
		} catch (Exception e) {
			stockLogger.logger.fatal(e.toString());
		}

		for (int i = 0; i < lstStockFileNames.size(); i++) {
			stockSourceData = lstStockFileNames.get(i);

			int begin = stockSourceData.indexOf('\\');
			int end = stockSourceData.indexOf('.');
			stockName = stockSourceData.substring(begin + 1, end);

			stockLogger.logger.fatal("StockFullId:" + stockName);
			 // 东兴证券999999改为000001
			if ("SH999999".equals(stockName)){
				stockName = "SH000001";
			}

			try {
				sbDao.insertStockLoadFullId(ConstantsInfo.StockMarket, stockName);
				// if(!stock_name.equals("SH601899"))//只测试SH000001
				// continue;

				isTableExist = sdDao.isExistStockTable(stockName, ConstantsInfo.TABLE_DATA_STOCK);
				if (isTableExist == 0) {
					stockLogger.logger.fatal(stockName + " not found");
					stockLogger.logger.fatal(stockName + " creat table");
					sdDao.createStockDataTable(stockName);
				}

				stockDataNum = readFileByLines(stockSourceData, stockName, ConstantsInfo.StockMarket);
				// 已经存在表，空交易数据或停牌不用导入
				if ((isTableExist > 0) && (stockDataNum <= 0)) {
					System.out.println("load stock data num:" + stockDataNum);
					stockLogger.logger.fatal("load stock data num:" + stockDataNum);
					continue;
				}

				loadfilePath = curPath + "/StockLoadFile/" + stockName + ".sql";
				sdDao.loadDatafFiletoDB(loadfilePath, stockName);

				if (isTableExist == 0) {
					stockLogger.logger.fatal("cal new stock table");
					// 计算全部ma5 涨幅
					cas.calculStockAllDataForSingleStock(stockName, ConstantsInfo.StockCalAllData);
					PointAnalysis pc = new PointAnalysis(sbDao, sdDao, spDao);
					// 计算极点
					pc.getPiontToTableForSingleStock(stockName, ConstantsInfo.StockCalAllData, null, null);
				} else {
					// 计算部分
					cas.calculStockAllDataForSingleStock(stockName, ConstantsInfo.StockCalCurData); 
				}
			} catch (Exception e) {
				stockLogger.logger.fatal(e.toString());
			}

		}

		return 0;
	}

	
	
	/**
	 * 导入商品数据
	 * @return
	 */
	public int loadAllFuturesDataInfile() {

		MaAnalysis cas = new MaAnalysis(sbDao, sdDao);

		String dirPath = "FuturesData\\";
		List<String> lstStockFileNames = null;
		lstStockFileNames = getListFiles(dirPath);
		String stockSourceData = null;
		String stockName = null;
		int isTableExist = 0;
		String loadfilePath = "";
		String curPath = System.getProperty("user.dir");
		curPath = curPath.replaceAll("\\\\", "/"); 

		// 清空stock_load_FullId表，作为下一步分析时用
		try {
			sbDao.truncateStockLoadFullId(ConstantsInfo.FuturesMarket);
		} catch (Exception e) {
			stockLogger.logger.fatal(e.toString());
		}

		for (int i = 0; i < lstStockFileNames.size(); i++) {
			stockSourceData = lstStockFileNames.get(i);

			int begin = stockSourceData.indexOf('\\');
			// 去掉前面两个字符
			begin += 2;
			int end = stockSourceData.indexOf('.');
			stockName = stockSourceData.substring(begin + 1, end);
			// 去掉空格与&
			stockName = stockName.replaceAll(" ", "");
			stockName = stockName.replaceAll("&", "");

			stockLogger.logger.fatal("StockFullId:" + stockName);
			// 东兴证券999999改为000001
			if ("SH999999".equals(stockName)){
				stockName = "SH000001";
			}
			
			try {
				sbDao.insertStockLoadFullId(ConstantsInfo.FuturesMarket, stockName);

				isTableExist = sdDao.isExistStockTable(stockName, ConstantsInfo.TABLE_DATA_STOCK);
				if (isTableExist == 0){
					stockLogger.logger.fatal(stockName + " not found");
					stockLogger.logger.fatal(stockName + " creat table");
					sdDao.createStockDataTable(stockName);
				}

				readFileByLines(stockSourceData, stockName, ConstantsInfo.FuturesMarket);
				loadfilePath = curPath + "/StockLoadFile/" + stockName + ".sql";
				int ret = sdDao.loadDatafFiletoDB(loadfilePath, stockName);
				stockLogger.logger.fatal("load data nums:" + ret);

				if (isTableExist == 0) {
					stockLogger.logger.fatal("cal new stock table");
					// 计算全部ma5 涨幅
					cas.calculStockAllDataForSingleStock(stockName, ConstantsInfo.StockCalAllData);
					// 创建极点表
					spDao.createStockPointTable(stockName);
					// 创建汇总表
					ssDao.createStockSummaryTable(stockName);

					PointAnalysis pc = new PointAnalysis(sbDao, sdDao, spDao);
					// 计算极点
					pc.getPiontToTableForSingleStock(stockName, ConstantsInfo.StockCalAllData, null, null);
				} else {
					// 计算部分
					cas.calculStockAllDataForSingleStock(stockName, ConstantsInfo.StockCalCurData); 
				}

			} catch (Exception e) {
				stockLogger.logger.fatal(e.toString());
			}
		}

		return 0;
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException, SecurityException,
			InstantiationException, IllegalAccessException, NoSuchFieldException {

		Connection stockBaseConn = DbConn.getConnDB("stockConf/conn_base_db.ini");
		Connection stockDataConn = DbConn.getConnDB("stockConf/conn_data_db.ini");
		Connection stockPointConn = DbConn.getConnDB("stockConf/conn_point_db.ini");
		Connection stockSummaryConn = DbConn.getConnDB("stockConf/conn_summary_db.ini");

		StockDataLoader fr = new StockDataLoader(stockBaseConn, stockDataConn, stockPointConn, stockSummaryConn);

		PropertyConfigurator.configure("StockConf/log4j.properties");
		java.util.Date startDate = new java.util.Date();
		System.out.println("file load start");
		stockLogger.logger.fatal("file load start");
	
		// fr.loadAllFuturesDataInfile();
		fr.loadAllDataInfile();

		java.util.Date endDate = new java.util.Date();
		// 计算两个时间点相差的秒数
		long seconds = (endDate.getTime() - startDate.getTime()) / 1000;
		System.out.println("总共耗时：" + seconds + "秒");
		System.out.println("file load end");

		stockBaseConn.close();
		stockDataConn.close();
		stockPointConn.close();
		stockSummaryConn.close();
		stockLogger.logger.fatal("file load end");
	}
}
