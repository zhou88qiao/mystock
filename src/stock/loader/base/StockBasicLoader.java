package stock.loader.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import common.ConstantsInfo;

import dao.DbConn;
import dao.StockBaseDao;
import dao.StockBaseFace;
import dao.StockConcept;
import dao.StockDataDao;
import dao.StockIndustry;
import dao.StockInformation;
import dao.StockInformationDao;
import dao.StockPointDao;
import dao.StockSingle;

public class StockBasicLoader {
	private POIFSFileSystem fs;
	private HSSFWorkbook wb;
	private XSSFWorkbook xb;
	private HSSFSheet sheet;
	private Sheet xsheet;
	private HSSFRow row;
	private Row xrow;

	private StockDataDao sdDao;
	private StockPointDao spDao;
	private StockBaseDao sbDao;

	public StockBasicLoader(Connection stockBaseConn) {
		this.sbDao = new StockBaseDao(stockBaseConn);

	}

	static StockInformationDao sd = new StockInformationDao();

	static int insetNum = 0;
	static int insetNoNum = 0;

	public StockBasicLoader(StockBaseDao sbDao) {
		this.sbDao = sbDao;
		// this.sdDao = sdDao;
		// this.spDao = spDao;
	}

	/**
	 * 读取Excel表格表头的内容
	 * 
	 * @param InputStream
	 * @return String 表头内容的数组
	 */
	public String[] readExcelTitle(InputStream is) {
		try {
			fs = new POIFSFileSystem(is);
			wb = new HSSFWorkbook(fs);
		} catch (IOException e) {
			e.printStackTrace();
		}
		sheet = wb.getSheetAt(0);
		row = sheet.getRow(0);
		// 标题总列数
		int colNum = row.getPhysicalNumberOfCells();
		System.out.println("colNum:" + colNum);
		String[] title = new String[colNum];
		for (int i = 0; i < colNum; i++) {
			// title[i] = getStringCellValue(row.getCell((short) i));
			title[i] = getCellFormatValue(row.getCell((short) i));
		}
		return title;
	}

	/**
	 * 读取03Excel数据内容
	 * 
	 * @param InputStream
	 * @return Map 包含单元格数据内容的Map对象
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public Map<Integer, String> readExcelContent_for_stock(InputStream is)
			throws IOException, ClassNotFoundException, SQLException {

		String[] stock = new String[5];// 获取2，3，4列
		HSSFCell cell;

		Map<Integer, String> content = new HashMap<Integer, String>();
		String str = "";
		try {
			fs = new POIFSFileSystem(is);
			wb = new HSSFWorkbook(fs);
		} catch (IOException e) {
			e.printStackTrace();
		}
		sheet = wb.getSheetAt(0);
		// 得到总行数
		int rowNum = sheet.getLastRowNum();
		row = sheet.getRow(0);
		System.out.println("行数：" + rowNum);
		int colNum = row.getPhysicalNumberOfCells();// 列数
		// 正文内容应该从第二行开始,第一行为表头的标题

		// rowNum=20;
		for (int i = 1; i <= rowNum; i++) {
			row = sheet.getRow(i);
			// int j = 0;
			// while (j < colNum) {
			int j = 1;// 获取2，3，4三列
			while (j < 4) {
				// 每个单元格的数据内容用"-"分割开，以后需要时用String类的replace()方法还原数据
				// 也可以将每个单元格的数据设置到一个javabean的属性中，此时需要新建一个javabean
				// str += getStringCellValue(row.getCell((short) j)).trim() +
				// "-";
				// cell = row.getCell((short) j);
				str += getCellFormatValue(row.getCell((short) j)).trim()
						+ "    ";

				stock[j] = getCellFormatValue(row.getCell((short) j)).trim()
						.trim();
				if (j == 2) {
					switch (stock[j].length()) {
					case 6:
					default:
						if (stock[j].charAt(0) == '6')// 沪市A股
						{
							stock[4] = "sh" + stock[j];
							stock[0] = "沪市A股";
						} else if (stock[j].charAt(0) == '9') {
							stock[4] = "sh" + stock[j];
							stock[0] = "沪市B股";
						} else if (stock[j].charAt(0) == '2') {
							stock[4] = "sz" + stock[j];
							stock[0] = "深市B股";
						} else if (stock[j].charAt(0) == '3') {
							stock[4] = "sz" + stock[j];
							stock[0] = "创业板";
						} else {
							stock[4] = "sz" + stock[j];
							stock[0] = "深市B股";
						}
						break;
					case 3:
						stock[4] = "sz000" + stock[j];
						stock[0] = "深市A股";
						break;
					case 4:
						if (stock[j].charAt(0) == '2')
							stock[0] = "中小企业板";
						else
							stock[0] = "深市A股";
						stock[4] = "sz00" + stock[j];
						break;
					case 2:
						stock[4] = "sz0000" + stock[j];
						stock[0] = "深市A股";
						break;
					case 1:
						stock[4] = "sz00000" + stock[j];
						stock[0] = "深市A股";
						break;
					case 5:
						stock[4] = "sz0" + stock[j];
						stock[0] = "深市A股";
						break;
					}

					System.out.println("length:" + stock[4].length() + "stock:"
							+ stock[4]);
					System.out.println("length0:" + stock[0].length()
							+ "stock0:" + stock[0]);
				}

				// System.out.println("stock:"+stock[j]);
				j++;
			}
			stockInsertMysql(sd, stock);
			content.put(i, str);
			// System.out.println("stock:"+str);
			str = "";
		}
		return content;
	}

	/**
	 * 读取03Excel 股票行业数据内容 //读取股票行业一级二级三级
	 */
	public int readIndustry() throws IOException, ClassNotFoundException,
			SQLException {

		String[] stock = new String[14];
		HSSFCell cell;

		String curPath = System.getProperty("user.dir");
		curPath = curPath.replaceAll("\\\\", "/"); // 转义将\ 转为/
		File file = new File(curPath + "/StockBaseData/1Industry.xls");
		if (file == null || !file.exists()) {
			System.out.println("no xlsx");
			return -1;
		}

		InputStream is = new FileInputStream("StockBaseData/1Industry.xls");
		if (is == null) {
			return -1;
		}

		// 清空一二三级行业表
		sbDao.truncateStockFirstIndustry();
		sbDao.truncateStockSecondIndustry();
		sbDao.truncateStockThirdIndustry();

		String str = "";
		try {
			fs = new POIFSFileSystem(is);
			wb = new HSSFWorkbook(fs);
		} catch (IOException e) {
			e.printStackTrace();
		}
		sheet = wb.getSheetAt(0);
		// 得到总行数
		int rowNum = sheet.getLastRowNum();
		row = sheet.getRow(0);
		System.out.println("行数：" + rowNum);
		int colNum = row.getPhysicalNumberOfCells();// 列数
		System.out.println("列数：" + colNum);

		// 正文内容应该从第二行开始,第一行为表头的标题

		String beforeFirstCode = "";
		String beforeSecondCode = "";
		String beforeFirstName = "";
		String beforeSecondName = "";
		int firstIndustryNum = 0;
		int secondIndustryNum = 0;
		int thirdIndustryNum = 0;

		// 读取一级行业
		for (int i = 2; i <= rowNum; i++) {
			row = sheet.getRow(i);

			int j = 0;// 获取0,1,2，3，4,5三列

			// 11111 读取0，1列 一级代码 一级名称
			colNum = 2;
			while (j < colNum) {
				stock[j] = getCellFormatValue(row.getCell((short) j)).trim();
				// System.out.println("stock:"+stock[j]);
				j++;
			}

			if (stock[0].equals(""))
				continue;
			firstIndustryNum++;
			System.out.println("first industry code:" + stock[0] + " name:"
					+ stock[1]);
			sbDao.insertStockFirstIndustry(stock[0], stock[1]);
		}

		// 读取二级行业
		for (int i = 2; i <= rowNum; i++) {
			row = sheet.getRow(i);

			int j = 0;// 获取0,1,2，3，4,5三列

			// 2222 读取0，,2,3列 一级代码 二级代码 二级名称
			colNum = 4;
			for (j = 0; j < colNum; j++) {
				stock[j] = getCellFormatValue(row.getCell((short) j)).trim();
				if (j == 0 && stock[0].equals("")) {
					stock[0] = beforeFirstCode;
				} else
					beforeFirstCode = stock[0];
				if (j == 1 && stock[1].equals("")) {
					stock[1] = beforeFirstName;
				} else
					beforeFirstName = stock[1];
			}

			if (stock[2].equals(""))
				continue;
			secondIndustryNum++;
			System.out.println("first industry code:" + stock[0] + "first name"
					+ stock[1] + "second code:" + stock[2] + "seconde name:"
					+ stock[3]);
			sbDao.insertStockSecondIndustry(stock[2], stock[3], stock[0],
					stock[1]);
		}

		// 读取三级行业
		for (int i = 2; i <= rowNum; i++) {
			row = sheet.getRow(i);
			int j = 0;// 获取0,1,2，3，4,5三列
			colNum = 12;
			for (j = 0; j < colNum; j++) {
				stock[j] = "";
				stock[j] = getCellFormatValue(row.getCell((short) j)).trim();
				if (j == 0 && stock[0].equals("")) // 一级
				{
					stock[0] = beforeFirstCode;
				} else
					beforeFirstCode = stock[0];

				if (j == 1 && stock[1].equals("")) {
					stock[1] = beforeFirstName;
				} else
					beforeFirstName = stock[1];
				if (j == 2 && stock[2].equals(""))// 二级
				{
					stock[2] = beforeSecondCode;
				} else
					beforeSecondCode = stock[2];
				if (j == 3 && stock[3].equals("")) {
					stock[3] = beforeSecondName;
				} else
					beforeSecondName = stock[3];
			}
			thirdIndustryNum++;
			// System.out.println(stock[5]);
			System.out.println("first industry code:" + stock[0]
					+ "first name:" + stock[1] + "second code:" + stock[2]
					+ "second name:" + stock[3] + "third code:" + stock[4]
					+ "third name:" + stock[5] + "baseExcept" + stock[6]);
			sbDao.insertStockThirdIndustry(stock[4], stock[5], stock[2],
					stock[3], stock[0], stock[1], stock[6], stock[7], stock[8],
					stock[9], stock[10], stock[11]);
			str = "";
		}
		is.close();
		System.out.println("firstIndustryNum:" + firstIndustryNum);
		System.out.println("secondIndustryNum:" + secondIndustryNum);
		System.out.println("thirdIndustryNum:" + thirdIndustryNum);
		return 0;

	}

	/**
	 * 读取03Excel 股票行业数据内容 //读取股票行业一级二级三级
	 */
	public int read9Stock_GongJiJin() throws IOException,
			ClassNotFoundException, SQLException {

		String[] stock = new String[10];
		HSSFCell cell;
		XSSFWorkbook xb = null;
		Sheet xsheet;
		String str = "";

		String curPath = System.getProperty("user.dir");
		curPath = curPath.replaceAll("\\\\", "/"); // 转义将\ 转为/
		File file = new File(curPath + "/StockBaseData/9Stock_GongJiJin.xlsx");
		if (file == null || !file.exists()) {
			System.out.println("no xlsx");
			return -1;
		}

		InputStream is = new FileInputStream(
				"StockBaseData/9Stock_GongJiJin.xlsx");
		if (is == null) {
			return -1;
		}

		try {

			xb = new XSSFWorkbook(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		xsheet = xb.getSheetAt(0);
		// 得到总行数
		int rowNum = xsheet.getLastRowNum();
		xrow = xsheet.getRow(0);
		System.out.println("行数：" + rowNum);
		int colNum = xrow.getPhysicalNumberOfCells();// 列数
		System.out.println("列数：" + colNum);
		// 正文内容应该从第二行开始,第一行为表头的标题

		// rowNum=40;

		int Num = 0;

		for (int i = 1; i <= rowNum; i++) {
			xrow = xsheet.getRow(i);

			int j = 0;// 获取0,1,2列
			String fullId = "";
			colNum = 4;
			for (j = 0; j < colNum; j++) {

				stock[j] = "";

				if (xrow.getCell(j) == null) {
					stock[j] = "";
				} else {
					xrow.getCell(j).setCellType(Cell.CELL_TYPE_STRING);
					stock[j] = xrow.getCell(j).getStringCellValue();
				}

				if (stock[0].equals(""))
					continue;

			}

			if (stock[0].startsWith("6"))
				fullId = "SH" + stock[0];
			else
				fullId = "SZ" + stock[0];

			System.out.println("stock code:" + fullId + " price:" + stock[3]);
			sbDao.updateStock_baseyearinfo_GongJiJin(fullId, Float.parseFloat(stock[3]));

			Num++;

			str = "";

		}
		is.close();
		System.out.println("Num:" + Num);
		return 0;

	}

	public int read10Stock_LiRun() throws IOException, ClassNotFoundException,
			SQLException {
		String[] stock = new String[10];
		HSSFCell cell;
		XSSFWorkbook xb = null;
		Sheet xsheet;
		String str = "";

		String curPath = System.getProperty("user.dir");
		curPath = curPath.replaceAll("\\\\", "/"); // 转义将\ 转为/
		File file = new File(curPath + "/StockBaseData/10Stock_LiRun.xlsx");
		if (file == null || !file.exists()) {
			System.out.println("no xlsx");
			return -1;
		}

		InputStream is = new FileInputStream("StockBaseData/10Stock_LiRun.xlsx");
		if (is == null) {
			return -1;
		}

		try {

			xb = new XSSFWorkbook(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		xsheet = xb.getSheetAt(0);
		// 得到总行数
		int rowNum = xsheet.getLastRowNum();
		xrow = xsheet.getRow(0);
		System.out.println("行数：" + rowNum);
		int colNum = xrow.getPhysicalNumberOfCells();// 列数
		System.out.println("列数：" + colNum);
		// 正文内容应该从第二行开始,第一行为表头的标题

		// rowNum=40;

		int Num = 0;

		for (int i = 1; i <= rowNum; i++) {
			xrow = xsheet.getRow(i);

			int j = 0;// 获取0,1,2列
			String fullId = "";
			colNum = 4;
			for (j = 0; j < colNum; j++) {

				stock[j] = "";

				if (xrow.getCell(j) == null) {
					stock[j] = "";
				} else {
					xrow.getCell(j).setCellType(Cell.CELL_TYPE_STRING);
					stock[j] = xrow.getCell(j).getStringCellValue();
				}

				if (stock[0].equals(""))
					continue;
			}

			if (stock[0].startsWith("6"))
				fullId = "SH" + stock[0];
			else
				fullId = "SZ" + stock[0];

			System.out.println("stock code:" + fullId + " price:" + stock[3]);
			sbDao.updateStock_baseyearinfo_lirun(fullId, Float
					.parseFloat(stock[3]));

			Num++;

			str = "";

		}

		is.close();
		System.out.println("Num:" + Num);
		return 0;

	}

	public int read11Stock_Yejiyugao() throws IOException,
			ClassNotFoundException, SQLException {

		String[] stock = new String[10];
		HSSFCell cell;
		XSSFWorkbook xb = null;
		Sheet xsheet;
		String str = "";

		String curPath = System.getProperty("user.dir");
		curPath = curPath.replaceAll("\\\\", "/"); // 转义将\ 转为/
		File file = new File(curPath + "/StockBaseData/11Stock_YeJiYuGao.xlsx");
		if (file == null || !file.exists()) {
			System.out.println("no xlsx");
			return -1;
		}

		InputStream is = new FileInputStream(
				"StockBaseData/11Stock_YeJiYuGao.xlsx");
		if (is == null) {
			return -1;
		}

		try {
			xb = new XSSFWorkbook(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		xsheet = xb.getSheetAt(0);
		// 得到总行数
		int rowNum = xsheet.getLastRowNum();
		xrow = xsheet.getRow(0);
		System.out.println("行数：" + rowNum);
		int colNum = xrow.getPhysicalNumberOfCells();// 列数
		System.out.println("列数：" + colNum);
		// 正文内容应该从第二行开始,第一行为表头的标题
		// rowNum=40;
		int Num = 0;

		for (int i = 1; i <= rowNum; i++) {
			xrow = xsheet.getRow(i);

			int j = 0;// 获取0,1,2列
			String fullId = "";
			colNum = 6;
			for (j = 1; j < colNum; j++) {

				stock[j] = "";

				if (xrow.getCell(j) == null) {
					stock[j] = "";
				} else {
					xrow.getCell(j).setCellType(Cell.CELL_TYPE_STRING);
					stock[j] = xrow.getCell(j).getStringCellValue();
				}

				if (stock[1].equals(""))
					continue;

			}

			if (stock[1].startsWith("6"))
				fullId = "SH" + stock[1];
			else
				fullId = "SZ" + stock[1];

			System.out.println("stock code:" + fullId + " content:" + stock[5]);
			sbDao.updateStock_baseyearinfo_yuGaoContent(fullId, stock[5]);
			Num++;
			str = "";
		}

		is.close();
		return 0;
	}

	public int read12Stock_PiLouShiJian() throws IOException,
			ClassNotFoundException, SQLException {

		String[] stock = new String[10];
		HSSFCell cell;
		XSSFWorkbook xb = null;
		Sheet xsheet;
		String str = "";

		String curPath = System.getProperty("user.dir");
		curPath = curPath.replaceAll("\\\\", "/"); // 转义将\ 转为/
		File file = new File(curPath + "/StockBaseData/12Stock_PiLouShiJian.xlsx");
		if (file == null || !file.exists()) {
			System.out.println("no xlsx");
			return -1;
		}

		InputStream is = new FileInputStream(
				"StockBaseData/12Stock_PiLouShiJian.xlsx");
		if (is == null) {
			return -1;
		}

		try {
			xb = new XSSFWorkbook(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		xsheet = xb.getSheetAt(0);
		// 得到总行数
		int rowNum = xsheet.getLastRowNum();
		xrow = xsheet.getRow(0);
		System.out.println("行数：" + rowNum);
		int colNum = xrow.getPhysicalNumberOfCells();// 列数
		System.out.println("列数：" + colNum);
		// 正文内容应该从第二行开始,第一行为表头的标题
		// rowNum=40;
		int Num = 0;

		for (int i = 1; i <= rowNum; i++) {
			xrow = xsheet.getRow(i);

			int j = 0;// 获取0,1,2列
			String fullId = "";
			colNum = 4;
			for (j = 1; j < colNum; j++) {

				stock[j] = "";

				if (xrow.getCell(j) == null) {
					stock[j] = "";
				} else {
					xrow.getCell(j).setCellType(Cell.CELL_TYPE_STRING);
					stock[j] = xrow.getCell(j).getStringCellValue();
				}

				if (stock[1].equals(""))
					continue;

			}

			if (stock[1].startsWith("6"))
				fullId = "SH" + stock[1];
			else
				fullId = "SZ" + stock[1];

			System.out.println("stock code:" + fullId + " content:" + stock[3]);
			sbDao.updateStock_baseyearinfo_piLouDate(fullId, stock[3]);
			Num++;
			str = "";
		}

		is.close();
		return 0;
	}

	// 读取股票是否两融
	public int readTwoRong() throws IOException, ClassNotFoundException,
			SQLException {

		String[] stock = new String[4];
		HSSFCell cell;
		XSSFWorkbook xb = null;
		Sheet xsheet;
		String str = "";

		String curPath = System.getProperty("user.dir");
		curPath = curPath.replaceAll("\\\\", "/"); // 转义将\ 转为/
		File file = new File(curPath + "/StockBaseData/5TwoRong.xlsx");
		if (file == null || !file.exists()) {
			System.out.println("no xlsx");
			return -1;
		}

		InputStream is = new FileInputStream("StockBaseData/5TwoRong.xlsx");
		if (is == null) {
			return -1;
		}
		// 清空
		sbDao.truncateStockTwoRong();

		try {
			// fs = new POIFSFileSystem(is);
			xb = new XSSFWorkbook(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		xsheet = xb.getSheetAt(0);
		// 得到总行数
		int rowNum = xsheet.getLastRowNum();
		xrow = xsheet.getRow(0);
		System.out.println("行数：" + rowNum);
		int colNum = xrow.getPhysicalNumberOfCells();// 列数
		System.out.println("列数：" + colNum);
		// 正文内容应该从第二行开始,第一行为表头的标题

		// rowNum=20;

		int Num = 0;
		String fullId = "";
		for (int i = 1; i < rowNum; i++) {
			xrow = xsheet.getRow(i);

			int j = 0;// 获取0,1,2三列

			for (j = 0; j < colNum; j++) {
				xrow.getCell(j).setCellType(Cell.CELL_TYPE_STRING);
				// stock[j]=getCellFormatValue(xrow.getCell((short) j))
				stock[j] = xrow.getCell(j).getStringCellValue();
			}
			// 以6开头
			if (stock[0].startsWith("6"))
				fullId = "SH" + stock[0];
			else
				fullId = "SZ" + stock[0];
			Num++;
			System.out.println("stock code:" + fullId + " name:" + stock[1]);

			sbDao.insertStockTwoRong(fullId, stock[1], 1);

			str = "";
		}
		is.close();
		System.out.println("Num:" + Num);

		return 0;

	}

	// 新增股票基本预期
	// 中间有空列的，为空的
	public int readstock_baseExpect() throws IOException,
			ClassNotFoundException, SQLException {

		String[] stock = new String[14];
		HSSFCell cell;
		XSSFWorkbook xb = null;
		Sheet xsheet;
		String str = "";

		String curPath = System.getProperty("user.dir");
		curPath = curPath.replaceAll("\\\\", "/"); // 转义将\ 转为/
		File file = new File(curPath + "/StockBaseData/6Stock_BaseInfo.xlsx");
		if (file == null || !file.exists()) {
			System.out.println("no xlsx");
			return -1;
		}

		InputStream is = new FileInputStream(
				"StockBaseData/6Stock_BaseInfo.xlsx");
		if (is == null) {
			return -1;
		}

		// 清空表
		sbDao.truncateStockBaseInfo();

		try {

			xb = new XSSFWorkbook(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		xsheet = xb.getSheetAt(0);

		// 得到总行数
		int rowNum = xsheet.getLastRowNum();
		xrow = xsheet.getRow(0);
		System.out.println("行数：" + rowNum);
		int colNum = xrow.getPhysicalNumberOfCells();// 列数
		System.out.println("列数：" + colNum);
		// 正文内容应该从第二行开始,第一行为表头的标题

		// rowNum=40;

		int Num = 0;
		String fullId = "";
		for (int i = 0; i <= rowNum; i++) {
			xrow = xsheet.getRow(i);

			int j = 0;// 获取0,1,2列

			colNum = 9;
			for (j = 0; j < colNum; j++) {
				stock[j] = "";
				if (xrow.getCell(j) == null) {
					stock[j] = "";
					// break;
				} else {
					xrow.getCell(j).setCellType(Cell.CELL_TYPE_STRING);
					// stock[j]=getCellFormatValue(xrow.getCell((short) j))
					stock[j] = xrow.getCell(j).getStringCellValue();
				}
			}

			if (stock[1] == null)
				continue;

			Num++;
			if (stock[1].equals("SZ002086")) {
				System.out.println("stock code:" + stock[1] + " stock name:"
						+ stock[2] + "baseexpect:" + stock[3]);
				System.out.println("stock pot:" + stock[7] + " stock fau:"
						+ stock[8]);
			}
			// sbDao.addStockBaseFace(stock[1],stock[3]);
			if (stock[2] != null && !stock[2].equals("")) {
				// System.out.println("baseexpect:"+stock[3]);
				sbDao.addStockBaseInfo(stock[1], stock[3], stock[4], stock[5],
						stock[6], stock[7], stock[8]);
			}

			str = "";
			stock[1] = null;

		}
		is.close();
		System.out.println("Num:" + Num);
		return 0;
	}

	// 期货商品对应股票与商品
	public int readStockToFeatures() throws IOException,
			ClassNotFoundException, SQLException {

		String[] stock = new String[14];
		HSSFCell cell;
		XSSFWorkbook xb = null;
		Sheet xsheet;
		String str = "";

		String curPath = System.getProperty("user.dir");
		curPath = curPath.replaceAll("\\\\", "/"); // 转义将\ 转为/
		File file = new File(curPath + "/StockBaseData/8ExMarket-to-stock.xlsx");
		if (file == null || !file.exists()) {
			System.out.println("no xlsx");
			return -1;
		}

		InputStream is = new FileInputStream(
				"StockBaseData/8ExMarket-to-stock.xlsx");
		if (is == null) {
			return -1;
		}

		// 清空表
		sbDao.truncateStockToFutures();

		try {

			xb = new XSSFWorkbook(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		xsheet = xb.getSheetAt(0);
		// 得到总行数
		int rowNum = xsheet.getLastRowNum();
		xrow = xsheet.getRow(0);
		System.out.println("行数：" + rowNum);
		int colNum = xrow.getPhysicalNumberOfCells();// 列数
		System.out.println("列数：" + colNum);
		// 正文内容应该从第二行开始,第一行为表头的标题

		// rowNum=40;

		int Num = 0;

		String beforeFirstCode = "";

		String beforeFirstName = "";

		for (int i = 1; i <= rowNum; i++) {
			xrow = xsheet.getRow(i);

			int j = 0;// 获取0,1,2列

			colNum = 11;
			for (j = 0; j < colNum; j++) {

				stock[j] = "";

				if (xrow.getCell(j) == null) {
					stock[j] = "";
				} else {
					xrow.getCell(j).setCellType(Cell.CELL_TYPE_STRING);
					stock[j] = xrow.getCell(j).getStringCellValue();
				}


			}

			Num++;

			// 去掉空格与&
			stock[0] = stock[0].replaceAll(" ", "");
			stock[0] = stock[0].replaceAll("&", "");

			System.out.println("code:" + stock[0]
					+ "name:" + stock[1] + " Fcode:"
					+ stock[2] + "Fname:" + stock[3] + "baseexpect:"
					+ stock[4]+"faucet:"+stock[9]);

			sbDao.addStockToFutures(stock[0], stock[1], stock[2], stock[3],stock[4], stock[5], stock[6], stock[7],stock[8], stock[9]);

			for (int count = 4; count < 10; count++)
				stock[count] = "";

			str = "";

		}
		is.close();
		System.out.println("Num:" + Num);

		return 0;
	}

	// 一级行业下对应的概念
	public int readFirstIndustry_To_Concept() throws IOException,
			ClassNotFoundException, SQLException {

		String[] stock = new String[10];
		HSSFCell cell;
		XSSFWorkbook xb = null;
		Sheet xsheet;
		String str = "";

		String curPath = System.getProperty("user.dir");
		curPath = curPath.replaceAll("\\\\", "/"); // 转义将\ 转为/
		File file = new File(curPath
				+ "/StockBaseData/2FirstIndustry-to-Concept.xlsx");
		if (file == null || !file.exists()) {
			System.out.println("no xlsx");
			return -1;
		}

		InputStream is = new FileInputStream(
				"StockBaseData/2FirstIndustry-to-Concept.xlsx");
		if (is == null) {
			return -1;
		}

		// 清空表
		sbDao.truncateStockFirstIndustryToConcept();

		try {

			xb = new XSSFWorkbook(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		xsheet = xb.getSheetAt(0);
		// 得到总行数
		int rowNum = xsheet.getLastRowNum();
		xrow = xsheet.getRow(0);
		System.out.println("行数：" + rowNum);
		int colNum = xrow.getPhysicalNumberOfCells();// 列数
		System.out.println("列数：" + colNum);
		// 正文内容应该从第二行开始,第一行为表头的标题

		// rowNum=40;

		int Num = 0;

		String beforeFirstCode = "";

		String beforeFirstName = "";

		for (int i = 2; i <= rowNum; i++) {
			xrow = xsheet.getRow(i);

			int j = 0;// 获取0,1,2列

			colNum = 10;
			for (j = 0; j < colNum; j++) {

				stock[j] = "";
				
				if (xrow.getCell(j) == null) {
					stock[j] = "";
				} else {
					xrow.getCell(j).setCellType(Cell.CELL_TYPE_STRING);
					stock[j] = xrow.getCell(j).getStringCellValue();
				}
				

				if (j == 0 && (stock[0] == null || stock[0].equals(""))) // 一级
				{
					stock[0] = beforeFirstCode;
				} else
					beforeFirstCode = stock[0];

				if (j == 1 && (stock[1] == null || stock[1].equals(""))) {
					stock[1] = beforeFirstName;
				} else
					beforeFirstName = stock[1];
				// stock[j]=getCellFormatValue(xrow.getCell((short) j))

			}

			Num++;

			System.out.println("first industry code:" + stock[0]
					+ "first industry name:" + stock[1] + " cocept code:"
					+ stock[2] + "cocept name:" + stock[3] + "baseexpect:"
					+ stock[4]);

			sbDao.addStockFirstIndustryToConcept(stock[0], stock[1], stock[2],
					stock[3], stock[4], stock[5], stock[6], stock[7], stock[8],
					stock[9]);

			for (int count = 4; count < 10; count++)
				stock[count] = "";

			str = "";

		}
		is.close();
		System.out.println("Num:" + Num);

		return 0;
	}

	// 期货商品市场
	public int readFuturesInfo() throws IOException, ClassNotFoundException,
			SQLException {

		String[] stock = new String[10];
		HSSFCell cell;
		XSSFWorkbook xb = null;
		Sheet xsheet;
		String str = "";

		String curPath = System.getProperty("user.dir");
		curPath = curPath.replaceAll("\\\\", "/"); // 转义将\ 转为/
		File file = new File(curPath + "/StockBaseData/7ExMarket_BaseInfo.xlsx");
		if (file == null || !file.exists()) {
			System.out.println("no xlsx");
			return -1;
		}

		InputStream is = new FileInputStream(
				"StockBaseData/7ExMarket_BaseInfo.xlsx");
		if (is == null) {
			return -1;
		}

		// 清空表
		sbDao.truncateMarket(ConstantsInfo.FuturesMarket);

		try {
			xb = new XSSFWorkbook(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		xsheet = xb.getSheetAt(0);
		// 得到总行数
		int rowNum = xsheet.getLastRowNum();
		xrow = xsheet.getRow(0);
		System.out.println("行数：" + rowNum);
		int colNum = xrow.getPhysicalNumberOfCells();// 列数
		System.out.println("列数：" + colNum);
		// 正文内容应该从第二行开始,第一行为表头的标题

		int Num = 0;

		for (int i = 1; i <= rowNum; i++) {
			xrow = xsheet.getRow(i);

			int j = 0;
			for (j = 0; j < colNum; j++) {
				stock[j] = "";
				if (xrow.getCell(j) == null) {
					stock[j] = "";
				} else {
					xrow.getCell(j).setCellType(Cell.CELL_TYPE_STRING);
					stock[j] = xrow.getCell(j).getStringCellValue();
				}
			}

			Num++;

			System.out.println("code:" + stock[0] + "name:" + stock[1]
					+ "baseexpect:" + stock[2] + "main:" + stock[3] + ":"
					+ stock[4]);

			sbDao.addStockMarket(ConstantsInfo.FuturesMarket, stock[0],
					stock[1], stock[2], stock[3], stock[4], stock[5], stock[6],
					stock[7]);

			// / sbDao.insertStockConceptInfo(stock[2],stock[3]);
			str = "";
			for (int count = 3; count < 10; count++)
				stock[count] = "";

		}
		is.close();
		System.out.println("Num:" + Num);

		return 0;
	}

	// 一级行业下对应的概念
	public int readMarketInfo() throws IOException, ClassNotFoundException,
			SQLException {

		String[] stock = new String[10];
		HSSFCell cell;
		XSSFWorkbook xb = null;
		Sheet xsheet;
		String str = "";

		String curPath = System.getProperty("user.dir");
		curPath = curPath.replaceAll("\\\\", "/"); // 转义将\ 转为/
		File file = new File(curPath + "/StockBaseData/0Market_BaseInfo.xlsx");
		if (file == null || !file.exists()) {
			System.out.println("no xlsx");
			return -1;
		}

		InputStream is = new FileInputStream(
				"StockBaseData/0Market_BaseInfo.xlsx");
		if (is == null) {
			return -1;
		}

		// 清空表
		sbDao.truncateMarket(ConstantsInfo.StockMarket);

		try {
			xb = new XSSFWorkbook(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		xsheet = xb.getSheetAt(0);
		// 得到总行数
		int rowNum = xsheet.getLastRowNum();
		xrow = xsheet.getRow(0);
		System.out.println("行数：" + rowNum);
		int colNum = xrow.getPhysicalNumberOfCells();// 列数
		System.out.println("列数：" + colNum);
		// 正文内容应该从第二行开始,第一行为表头的标题

		int Num = 0;

		for (int i = 1; i <= rowNum; i++) {
			xrow = xsheet.getRow(i);

			int j = 0;
			for (j = 0; j < colNum; j++) {
				stock[j] = "";
				if (xrow.getCell(j) == null) {
					stock[j] = "";
				} else {
					xrow.getCell(j).setCellType(Cell.CELL_TYPE_STRING);
					stock[j] = xrow.getCell(j).getStringCellValue();
				}
			}

			Num++;

			System.out.println("code:" + stock[0] + "name:" + stock[1]
					+ "baseexpect:" + stock[2] + "main:" + stock[3] + ":"
					+ stock[4]);

			sbDao.addStockMarket(ConstantsInfo.StockMarket, stock[0], stock[1],
					stock[2], stock[3], stock[4], stock[5], stock[6], stock[7]);

			// / sbDao.insertStockConceptInfo(stock[2],stock[3]);
			str = "";
			for (int count = 3; count < 10; count++)
				stock[count] = "";

		}
		is.close();
		System.out.println("Num:" + Num);

		return 0;
	}

	// 新增三级行业对应的股票
	public int readThirdIndustry_to_stock() throws IOException,
			ClassNotFoundException, SQLException {

		String[] stock = new String[4];
		HSSFCell cell;
		XSSFWorkbook xb = null;
		Sheet xsheet;

		String curPath = System.getProperty("user.dir");
		curPath = curPath.replaceAll("\\\\", "/"); // 转义将\ 转为/
		File file = new File(curPath
				+ "/StockBaseData/3ThirdIndustry-to-stock.xlsx");
		if (file == null || !file.exists()) {
			System.out.println("no xlsx");
			return -1;
		}

		InputStream is = new FileInputStream(
				"StockBaseData/3ThirdIndustry-to-stock.xlsx");
		if (is == null) {
			return -1;
		}

		// 清空三级行业股票对应表
		sbDao.truncateStocktoIndustry();

		String str = "";
		try {
			// fs = new POIFSFileSystem(is);
			xb = new XSSFWorkbook(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		xsheet = xb.getSheetAt(0);
		// 得到总行数
		int rowNum = xsheet.getLastRowNum();
		xrow = xsheet.getRow(0);
		System.out.println("行数：" + rowNum);
		int colNum = xrow.getPhysicalNumberOfCells();// 列数
		System.out.println("列数：" + colNum);
		// 正文内容应该从第二行开始,第一行为表头的标题

		// rowNum=20;

		int Num = 0;
		String fullId = "";
		for (int i = 0; i <= rowNum; i++) {
			xrow = xsheet.getRow(i);

			int j = 0;// 获取0,1,2列

			// colNum=3;
			for (j = 0; j < colNum; j++) {
				xrow.getCell(j).setCellType(Cell.CELL_TYPE_STRING);
				// stock[j]=getCellFormatValue(xrow.getCell((short) j))
				stock[j] = xrow.getCell(j).getStringCellValue();
			}

			Num++;
			System.out.println("stock code:" + stock[0] + " stock name:"
					+ stock[1] + " third industry name:" + stock[2]
					+ " third industry code:" + stock[3]);
			// sbDao.updateStockSingle(fullId);
			StockIndustry sindustry = sbDao
					.lookUpIndustryFromThirdCode(stock[3]);
			if (sindustry == null) {
				System.out.println("cannot not found this code");
			}

			// 插入新股票
			StockSingle sStock = new StockSingle(1, stock[0], stock[1],
					sindustry.getThirdcode(), sindustry.getThirdname(),
					sindustry.getSecondcode(), sindustry.getSecondname(),
					sindustry.getFirstcode(), sindustry.getFirstname(), " ", 0);
			try {
				sbDao.insertStockSingleToIndustry(sStock);
				// sbDao.insertStockSingle(sStock);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			str = "";
		}
		is.close();
		System.out.println("Num:" + Num);
		return 0;
	}

	// 新增三级行业对应的股票
	public int readConcept_to_stock() throws IOException,
			ClassNotFoundException, SQLException {

		String[] stock = new String[5];
		HSSFCell cell;
		XSSFWorkbook xb = null;
		Sheet xsheet;

		String curPath = System.getProperty("user.dir");
		curPath = curPath.replaceAll("\\\\", "/"); // 转义将\ 转为/
		File file = new File(curPath + "/StockBaseData/4Concept-to-stock.xlsx");
		if (file == null || !file.exists()) {
			System.out.println("no xlsx");
			return -1;
		}

		InputStream is = new FileInputStream(
				"StockBaseData/4Concept-to-stock.xlsx");
		if (is == null) {
			return -1;
		}

		// 清空三级行业股票对应表
		sbDao.truncateStockToConcept();

		String str = "";
		try {
			// fs = new POIFSFileSystem(is);
			xb = new XSSFWorkbook(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		xsheet = xb.getSheetAt(0);
		// 得到总行数
		int rowNum = xsheet.getLastRowNum();
		xrow = xsheet.getRow(0);
		System.out.println("行数：" + rowNum);
		int colNum = xrow.getPhysicalNumberOfCells();// 列数
		System.out.println("列数：" + colNum);
		// 正文内容应该从第二行开始,第一行为表头的标题

		// rowNum=20;

		int Num = 0;

		for (int i = 0; i <= rowNum; i++) {
			xrow = xsheet.getRow(i);

			int j = 0;// 获取0,1,2列

			for (j = 0; j < colNum; j++) {
				xrow.getCell(j).setCellType(Cell.CELL_TYPE_STRING);
				// stock[j]=getCellFormatValue(xrow.getCell((short) j))
				stock[j] = xrow.getCell(j).getStringCellValue();
			}

			Num++;
			System.out
					.println("stock code:" + stock[0] + " stock name:"
							+ stock[1] + " first industry code:" + stock[3]
							+ " concept code:" + stock[2] + " concept name:"
							+ stock[4]);

			sbDao.insertStockToConcept(stock[0], stock[1], stock[3], stock[2],
					stock[4]);

			str = "";
		}
		is.close();
		System.out.println("Num:" + Num);
		return 0;
	}

	public static int stockInsertMysql(StockInformationDao sd, String[] st)
			throws IOException, ClassNotFoundException, SQLException {
		/*
		 * // for(int i=1;i<st.length;i++) // { if(st[i] == null ||
		 * st[i].length() <= 0) { insetNoNum++; return -1; } }
		 */

		// StockInformation stif=new
		// StockInformation(st[2],st[4],st[3],st[0],st[1]," "," ");
		// sd.addStock2(stif);
		// stock_info st[1] id st[6] fullid st[2]名称 st[7]分类 st[3] 行业 st[5] 概念
		// st[4] 地区
		StockInformation stif = new StockInformation(st[1], st[6], st[2],
				st[7], st[3], st[5], st[4]);
		sd.addStockInfo(stif);
		insetNum++;
		return 0;
	}

	/**
	 * 获取单元格数据内容为字符串类型的数据
	 * 
	 * @param cell
	 *            Excel单元格
	 * @return String 单元格数据内容
	 */
	private String getStringCellValue(HSSFCell cell) {
		String strCell = "";
		switch (cell.getCellType()) {
		case HSSFCell.CELL_TYPE_STRING:
			strCell = cell.getStringCellValue();
			break;
		case HSSFCell.CELL_TYPE_NUMERIC:
			strCell = String.valueOf(cell.getNumericCellValue());
			break;
		case HSSFCell.CELL_TYPE_BOOLEAN:
			strCell = String.valueOf(cell.getBooleanCellValue());
			break;
		case HSSFCell.CELL_TYPE_BLANK:
			strCell = "";
			break;
		default:
			strCell = "";
			break;
		}
		if (strCell.equals("") || strCell == null) {
			return "";
		}
		if (cell == null) {
			return "";
		}
		return strCell;
	}

	/**
	 * 获取单元格数据内容为日期类型的数据
	 * 
	 * @param cell
	 *            Excel单元格
	 * @return String 单元格数据内容
	 */
	private String getDateCellValue(HSSFCell cell) {
		String result = "";
		try {
			int cellType = cell.getCellType();
			if (cellType == HSSFCell.CELL_TYPE_NUMERIC) {
				Date date = cell.getDateCellValue();
				result = (date.getYear() + 1900) + "-" + (date.getMonth() + 1)
						+ "-" + date.getDate();
			} else if (cellType == HSSFCell.CELL_TYPE_STRING) {
				String date = getStringCellValue(cell);
				result = date.replaceAll("[年月]", "-").replace("日", "").trim();
			} else if (cellType == HSSFCell.CELL_TYPE_BLANK) {
				result = "";
			}
		} catch (Exception e) {
			System.out.println("日期格式不正确!");
			e.printStackTrace();
		}
		return result;
	}

	public static String getPrettyNumber(String number) {
		return BigDecimal.valueOf(Double.parseDouble(number))
				.stripTrailingZeros().toPlainString();
	}

	/**
	 * 根据HSSFCell类型设置数据
	 * 
	 * @param cell
	 * @return
	 */
	private String getCellFormatValue(Cell cell) {
		String cellvalue = "";
		String celltmpvalue = "";
		if (cell != null) {
			// 判断当前Cell的Type
			switch (cell.getCellType()) {
			// 如果当前Cell的Type为NUMERIC
			case HSSFCell.CELL_TYPE_NUMERIC:
			case HSSFCell.CELL_TYPE_FORMULA: {
				// 判断当前的cell是否为Date
				if (HSSFDateUtil.isCellDateFormatted(cell)) {
					// 如果是Date类型则，转化为Data格式

					// 方法1：这样子的data格式是带时分秒的：2011-10-12 0:00:00
					// cellvalue = cell.getDateCellValue().toLocaleString();

					// 方法2：这样子的data格式是不带带时分秒的：2011-10-12
					Date date = cell.getDateCellValue();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					cellvalue = sdf.format(date);

				}
				// 如果是纯数字
				else {
					// 取得当前Cell的数值
					celltmpvalue = String.valueOf(cell.getNumericCellValue());// 返回符点值
					cellvalue = getPrettyNumber(celltmpvalue);
				}
				break;
			}
				// 如果当前Cell的Type为STRIN
			case HSSFCell.CELL_TYPE_STRING:
				// 取得当前的Cell字符串
				cellvalue = cell.getRichStringCellValue().getString();
				break;
			// 默认的Cell值
			default:
				cellvalue = " ";
			}
		} else {
			cellvalue = "";
		}
		return cellvalue;

	}

	public int readYearInfo() throws IOException, ClassNotFoundException,
			SQLException {

		sbDao.createStockbaseyearinfoTable();
		int ret=0;
		//sbDao.truncateStockTable("stock_baseyearinfo");
		ret = read9Stock_GongJiJin();
		if(ret<0)
			return -1;
		ret =read10Stock_LiRun();
		 if(ret<0)
				return -1;
		 ret =read11Stock_Yejiyugao();
		if(ret<0)
			return -1;
		ret = read12Stock_PiLouShiJian();
		if(ret<0)
			return -1;
		
		return 0;
	}

	public static void main(String[] args) throws IOException,
			ClassNotFoundException, SQLException {
		// StockInformationDao sd=new StockInformationDao();

		Connection stockBaseConn = DbConn
				.getConnDB("stockConf/conn_base_db.ini");

		StockBasicLoader stockBasicLoader = new StockBasicLoader(stockBaseConn);

		// 导入市场
		// excelReader.readMarketInfo();

		// 导入商品市场
		//excelReader.readFuturesInfo();
		// 一二三级行业 //03excel
		// excelReader.readIndustry();

		// 一级行业下概念
		// excelReader.readFirstIndustry_To_Concept();
		
		 stockBasicLoader.readStockToFeatures();

		// 三级行业股票
		// excelReader.readThirdIndustry_to_stock();
		// 股票基本面
		// excelReader.readstock_baseExpect();
		// 概念下对应股票
		// excelReader.readConcept_to_stock();
		// excelReader.readTwoRong();

		//excelReader.readYearInfo();

		stockBaseConn.close();

	}
}