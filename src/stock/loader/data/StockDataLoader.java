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
	 * ����Ϊ��λ��ȡ�ļ��������ڶ������еĸ�ʽ���ļ� ���裺1���Ȼ���ļ���� 2������ļ��������������һ���ֽ���������Ҫ��������������ж�ȡ
	 * 3����ȡ������������Ҫ��ȡ�����ֽ��� 4��һ��һ�е������readline()�� ��ע����Ҫ���ǵ����쳣���
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
			String tdx = "������Դ:ͨ���� ";
			fw = new FileWriter(wfPath);
			if (file.isFile() && file.exists()) { 
				// ���ǵ������ʽ
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;

				int line = 0;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					line++;
					// �ų���һ �ڶ� ���һ������
					if (line == 1 || line == 2){
						continue;
					}

					// ֻ����5�� ��ʱ�� ���̼� ���̼� ��߼� ��ͼۣ� ʱ��ĳ������� ��mysql����һ��
					// ȥ�����һ�� ����Ϊ8
					if (lineTxt.length() > 36)
					{
						String[] sourceStrArray = lineTxt.split("\t");						
						stockVolume = Long.parseLong(sourceStrArray[5]);
						
						// ���ܽ������Ƿ�Ϊ��
						if (type == ConstantsInfo.FuturesMarket) {
							stockDataNum++;
							fw.write(lineTxt);
							fw.write("\r\n");
						} else {
							// ͣ�ƹ�Ʊ���ܵ���
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
				System.out.println("�Ҳ���ָ�����ļ�");
			}
		} catch (Exception e) {
			System.out.println("��ȡ�ļ����ݳ���");
			e.printStackTrace();
		}

		return stockDataNum;

	}

	/**
	 * ��ȡ�����ļ���һ�е�ʱ��
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
				// ���ǵ������ʽ
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				int line = 0;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					line++;
					// �ų���һ �ڶ� ���һ������
					if (line == 1 || line == 2){
						continue;
					}
					stockTime = lineTxt.substring(0, 10);
					System.out.println(stockTime);
					break;
				}
				read.close();
			} else {
				System.out.println("�Ҳ���ָ�����ļ�");
			}
		} catch (Exception e) {
			System.out.println("��ȡ�ļ����ݳ���");
			e.printStackTrace();
		}

		return stockTime;
	}

	
	/**
	 * ��ȡĿ¼�ļ���
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
	 * 1 ���뵱�콻������ ����ma �Ƿ� ������������ӹ�Ʊ����Ҫ�������ױ���������㼫��
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
		// ת�彫\ תΪ/
		curPath = curPath.replaceAll("\\\\", "/"); 
		int id = 0;

		// �ж��ظ����� ��ȡSH99999.txt�ļ�
		stockTime = readFileFirstLineDate();

		try {
			id = sdDao.getDataValueIsExist("SH000001", stockTime);
			if (id > 0) {
				return 1;
			}
			// ���stock_load_FullId����Ϊ��һ������ʱ��
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
			 // ����֤ȯ999999��Ϊ000001
			if ("SH999999".equals(stockName)){
				stockName = "SH000001";
			}

			try {
				sbDao.insertStockLoadFullId(ConstantsInfo.StockMarket, stockName);
				// if(!stock_name.equals("SH601899"))//ֻ����SH000001
				// continue;

				isTableExist = sdDao.isExistStockTable(stockName, ConstantsInfo.TABLE_DATA_STOCK);
				if (isTableExist == 0) {
					stockLogger.logger.fatal(stockName + " not found");
					stockLogger.logger.fatal(stockName + " creat table");
					sdDao.createStockDataTable(stockName);
				}

				stockDataNum = readFileByLines(stockSourceData, stockName, ConstantsInfo.StockMarket);
				// �Ѿ����ڱ��ս������ݻ�ͣ�Ʋ��õ���
				if ((isTableExist > 0) && (stockDataNum <= 0)) {
					System.out.println("load stock data num:" + stockDataNum);
					stockLogger.logger.fatal("load stock data num:" + stockDataNum);
					continue;
				}

				loadfilePath = curPath + "/StockLoadFile/" + stockName + ".sql";
				sdDao.loadDatafFiletoDB(loadfilePath, stockName);

				if (isTableExist == 0) {
					stockLogger.logger.fatal("cal new stock table");
					// ����ȫ��ma5 �Ƿ�
					cas.calculStockAllDataForSingleStock(stockName, ConstantsInfo.StockCalAllData);
					PointAnalysis pc = new PointAnalysis(sbDao, sdDao, spDao);
					// ���㼫��
					pc.getPiontToTableForSingleStock(stockName, ConstantsInfo.StockCalAllData, null, null);
				} else {
					// ���㲿��
					cas.calculStockAllDataForSingleStock(stockName, ConstantsInfo.StockCalCurData); 
				}
			} catch (Exception e) {
				stockLogger.logger.fatal(e.toString());
			}

		}

		return 0;
	}

	
	
	/**
	 * ������Ʒ����
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

		// ���stock_load_FullId����Ϊ��һ������ʱ��
		try {
			sbDao.truncateStockLoadFullId(ConstantsInfo.FuturesMarket);
		} catch (Exception e) {
			stockLogger.logger.fatal(e.toString());
		}

		for (int i = 0; i < lstStockFileNames.size(); i++) {
			stockSourceData = lstStockFileNames.get(i);

			int begin = stockSourceData.indexOf('\\');
			// ȥ��ǰ�������ַ�
			begin += 2;
			int end = stockSourceData.indexOf('.');
			stockName = stockSourceData.substring(begin + 1, end);
			// ȥ���ո���&
			stockName = stockName.replaceAll(" ", "");
			stockName = stockName.replaceAll("&", "");

			stockLogger.logger.fatal("StockFullId:" + stockName);
			// ����֤ȯ999999��Ϊ000001
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
					// ����ȫ��ma5 �Ƿ�
					cas.calculStockAllDataForSingleStock(stockName, ConstantsInfo.StockCalAllData);
					// ���������
					spDao.createStockPointTable(stockName);
					// �������ܱ�
					ssDao.createStockSummaryTable(stockName);

					PointAnalysis pc = new PointAnalysis(sbDao, sdDao, spDao);
					// ���㼫��
					pc.getPiontToTableForSingleStock(stockName, ConstantsInfo.StockCalAllData, null, null);
				} else {
					// ���㲿��
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
		// ��������ʱ�����������
		long seconds = (endDate.getTime() - startDate.getTime()) / 1000;
		System.out.println("�ܹ���ʱ��" + seconds + "��");
		System.out.println("file load end");

		stockBaseConn.close();
		stockDataConn.close();
		stockPointConn.close();
		stockSummaryConn.close();
		stockLogger.logger.fatal("file load end");
	}
}
