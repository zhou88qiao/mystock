package stock.manager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.PropertyConfigurator;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import com.eltima.components.ui.DatePicker;
import common.ConstantsInfo;
import common.stockLogger;
import dao.DbConn;
import dao.StockBaseDao;
import dao.StockConcept;
import dao.StockDataDao;
import dao.StockIndustry;
import dao.StockSummaryDao;
import dao.StockMarket;
import dao.StockPointDao;
import dao.StockRegional;
import dao.StockSingle;
import stock.analysis.PointAnalysis;
import stock.analysis.OperationAnalysis;
import stock.export.StockExcelExporterMain;
import stock.loader.analyse.LoadSummaryMain;
import stock.loader.base.StockBasicLoader;
import stock.loader.data.StockDataLoader;
import stock.timer.CommonDate;
import stockGUI.StockTimeSeriesChart;
import stockGUI.stocktable.StockConceptTableModel;
import stockGUI.stocktable.StockIndustryTableModel;
import stockGUI.stocktable.StockMarketTableModel;
import stockGUI.stocktable.StockRegionalTableModel;
import stockGUI.stocktable.StockSingleTableModel;

/**
 * @author zhouqiao
 *
 */
public class StockBaseManager {
	final static int ACTION_ADD = 0;
	final static int ACTION_DEL = 1;
	final static int ACTION_EDIT = 2;
	final static int ACTION_QUERY = 3;
	final static int ACTION_CHECK = 4;	
	final int LOAD_INDUSTRY = 10;
	final int Load_First_Industry_Concept = 10;
	
	
	static StockBaseManager stockBM;
	private StockDataDao sdDao;
	private StockPointDao spDao;
	private StockBaseDao sbDao;
	private StockSummaryDao ssDao;

	private static JFrame jFrame = new JFrame("股票基本信息");
	
	private JMenuBar mb = new JMenuBar();
	JMenu stockBase = new JMenu("股票信息");
	JMenu stockFunction = new JMenu("股票核心功能");
	JMenu futuresFunction = new JMenu("期货核心功能");
	JMenu stockOperate = new JMenu("基础操作");
	JMenu stockMessage = new JMenu("资讯");

	JMenuItem loadStockData = new JMenuItem("1 导入交易数据");
	JMenuItem extremeAnalyze = new JMenuItem("2 交易数据分析");
	JMenuItem loadPointExcel = new JMenuItem("3 导出分析数据all");
	JMenuItem loadAnalyExcel = new JMenuItem("4 导入分析数据");
	JMenuItem loadPointOpExcel = new JMenuItem("5 导出极点数据point");
	JMenuItem loadSummaryExcel = new JMenuItem("6 导出统计数据summary");
	JMenuItem loadOperationAnalyse = new JMenuItem("7 买卖操作分析");
	JMenuItem loadOperationExcel = new JMenuItem("8 导出操作分析数据operation");
	JMenuItem loadTotalOperationExcel = new JMenuItem("9 导出总操作数据totaloperation");

	JMenuItem loadFuturesData = new JMenuItem("1 导入期货商品数据");
	JMenuItem futuresExtremeAnalyze = new JMenuItem("2 交易数据分析");
	JMenuItem futuresLoadPointExcel = new JMenuItem("3 导出分析数据");
	JMenuItem futuresLoadAnalyExcel = new JMenuItem("4 导入分析数据");
	JMenuItem futuresLoadPointOpExcel = new JMenuItem("5 导出极点数据");
	JMenuItem futuresLoadSummaryExcel = new JMenuItem("6 导出统计数据");
	JMenuItem futuresLoadOperationAnalyse = new JMenuItem("7 买卖操作分析");
	JMenuItem futuresLoadOperationExcel = new JMenuItem("8 导出操作分析数据");
	JMenuItem futuresLoadTotalOperationExcel = new JMenuItem("9 导出总操作数据totaloperation");

	JMenuItem loadMarket = new JMenuItem("0 导入股票或期货商品市场");
	JMenuItem loadIndustry = new JMenuItem("1 导入一二三级行业");
	JMenuItem loadFirstIndustryConcept = new JMenuItem("2 导入一级行业对应概念");
	JMenuItem loadThirdIndustrytoStock = new JMenuItem("3 导入三级行业对应个股数据");
	JMenuItem loadConcepttoStock = new JMenuItem("4 导入概念对应个股数据");
	JMenuItem loadStockTwoRong = new JMenuItem("5 导入个股融资融券");
	JMenuItem loadStockBaseface = new JMenuItem("6 导入个股基本面");
	JMenuItem loadStockToFuturesBaseface = new JMenuItem("7 导入期货商品对应个股数据");
	JMenuItem loadStockBaseYearface = new JMenuItem("8 导入股票年份交易信息");

	DatePicker datepickStart = getDatePicker();
	DatePicker datepickEnd = getDatePicker();

	JButton addButton = new JButton("新建"); 
	JButton editButton = new JButton("编辑");
	JButton deleteButton = new JButton("删除");
	JButton queryButton = new JButton("查询");
	JButton checkButton = new JButton("确定");

	//JPanel Panel = new JPanel();
	JLabel jlMarket = new JLabel("市场");
	JLabel jlConcept = new JLabel("概念");
	JLabel jlProvince = new JLabel("省名称");
	JLabel jlCity = new JLabel("市名称");
	JLabel jlFirstIndustry = new JLabel("一级行业名称");
	JLabel jlSecondIndustry = new JLabel("二级行业名称");
	JLabel jlThirdIndustry = new JLabel("三级行业名称");
	JLabel jlStockFullId = new JLabel("股票代码");
	JLabel jlStockName = new JLabel("股票名称");
	JLabel jlStockIndustry = new JLabel("三级行业");
	JLabel jlStockConcept = new JLabel("概念");
	JTextField jtfMarket = new JTextField(10);
	JTextField jtfConcept = new JTextField(10);
	JLabel tishiLabel = new JLabel("");

	JTextField jtfFirstIndustry = new JTextField(10);
	JTextField jtfSecondIndustry = new JTextField(10);
	JTextField jtfThirdIndustry = new JTextField(10);

	JTextField jtfProvince = new JTextField(10);
	JTextField jtfCity = new JTextField(10);
	JTextField jtfArea = new JTextField(10);

	JTextField jtfFullId = new JTextField(10);
	JTextField jtfStockName = new JTextField(10);
	JTextField jtfStockIndustry = new JTextField(10);
	JTextField jtfStockConcept = new JTextField(10);

	MenuActionListenter actionL = new MenuActionListenter();
	ButtonMouseListenter actionM = new ButtonMouseListenter();

	StockMarketTableModel stMarketTabMod;
	StockConceptTableModel stConceptTabMod;
	StockIndustryTableModel stIndustyTabMod;
	StockRegionalTableModel stRegionalTabMod;
	StockSingleTableModel stSingleTabMod;
	JTable stockTable;
	JScrollPane stockSP;
	JPanel operationPane;
	JPanel timerPane;
	JOptionPane jop;

	List<StockMarket> listStockMarket = new ArrayList<StockMarket>();
	List<StockConcept> listStockConcept = new ArrayList<StockConcept>();
	List<StockIndustry> listStockIndustry = new ArrayList<StockIndustry>();
	List<StockRegional> listStockRegional = new ArrayList<StockRegional>();
	List<StockSingle> listStockSingle = new ArrayList<StockSingle>();

	public StockBaseManager(Connection stockBaseConn, Connection stockDataConn, Connection stockPointConn,
			Connection stockSummaryConn) {
		sbDao = new StockBaseDao(stockBaseConn);
		sdDao = new StockDataDao(stockDataConn);
		spDao = new StockPointDao(stockPointConn);
		ssDao = new StockSummaryDao(stockSummaryConn);
	}

	public void init() {
		int i = 0;
		String[] baseItems = new String[] { "市场", "地区", "行业", "概念", "个股" };
		char[] baseShortcuts = { 'M', 'R', 'I', 'C', 'S' };
		for (i = 0; i < baseItems.length; i++) {
			// JMenuItem item = new JMenuItem(baseItems[i], baseShortcuts[i]);
			JMenuItem item = new JMenuItem(baseItems[i]);
			item.setAccelerator(KeyStroke.getKeyStroke(baseShortcuts[i],
					Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
			// 根据i 事件监听器获取
			item.setActionCommand("" + i);  
			item.addActionListener(actionL);
			stockBase.add(item);
		}

		stockOperate.addActionListener(actionL);

		addButton.addMouseListener(actionM);
		deleteButton.addMouseListener(actionM);
		editButton.addMouseListener(actionM);
		queryButton.addMouseListener(actionM);
		checkButton.addMouseListener(actionM);

		addButton.setActionCommand("" + ACTION_ADD);
		deleteButton.setActionCommand("" + ACTION_DEL);
		editButton.setActionCommand("" + ACTION_EDIT);
		queryButton.setActionCommand("" + ACTION_QUERY);
		checkButton.setActionCommand("" + ACTION_CHECK);

		stockOperate.add(loadMarket);
		stockOperate.add(loadIndustry);
		stockOperate.add(loadFirstIndustryConcept);
		stockOperate.add(loadThirdIndustrytoStock);
		stockOperate.add(loadConcepttoStock);
		stockOperate.add(loadStockTwoRong);
		stockOperate.add(loadStockBaseface);
		stockOperate.add(loadStockToFuturesBaseface);
		stockOperate.add(loadStockBaseYearface);

		loadStockData.setActionCommand("" + 6);
		extremeAnalyze.setActionCommand("" + 7); // 根据i 事件监听器获取
		loadPointExcel.setActionCommand("" + 8); // 根据i 事件监听器获取
		loadAnalyExcel.setActionCommand("" + 20); // 导入数据库
		loadSummaryExcel.setActionCommand("" + 21); // 导出统计表
		loadOperationAnalyse.setActionCommand("" + 22); // 操作分析
		loadOperationExcel.setActionCommand("" + 23); // 导出操作分析表
		loadPointOpExcel.setActionCommand("" + 24);// 导出极点数据
		loadTotalOperationExcel.setActionCommand("" + 25); // 导出总操作分析表

		loadFuturesData.setActionCommand("" + 31); // 根据i 事件监听器获取
		futuresExtremeAnalyze.setActionCommand("" + 32); // 根据i 事件监听器获取
		futuresLoadPointExcel.setActionCommand("" + 33); // 根据i 事件监听器获取
		futuresLoadAnalyExcel.setActionCommand("" + 34); // 导入数据库
		futuresLoadSummaryExcel.setActionCommand("" + 35); // 导出统计表
		futuresLoadOperationAnalyse.setActionCommand("" + 36); // 操作分析
		futuresLoadOperationExcel.setActionCommand("" + 37); // 导出操作分析表
		futuresLoadPointOpExcel.setActionCommand("" + 38);// 导出极点数据
		futuresLoadTotalOperationExcel.setActionCommand("" + 39); // 导出总操作分析表

		loadIndustry.setActionCommand("" + 9); // 导入行业
		loadFirstIndustryConcept.setActionCommand("" + 10); // 导入一级行业对应的概念
		loadThirdIndustrytoStock.setActionCommand("" + 11); // 导入三级行业对应的股票
		loadConcepttoStock.setActionCommand("" + 12); // 概念对应股票
		loadStockTwoRong.setActionCommand("" + 13); // 两融
		loadStockBaseface.setActionCommand("" + 14); // 基本面
		loadMarket.setActionCommand("" + 15);// 导入 市场
		loadStockToFuturesBaseface.setActionCommand("" + 16);// 导入期货对应个股票
		loadStockBaseYearface.setActionCommand("" + 17);// 导入年份信息
		
		

		loadStockData.addActionListener(actionL);
		extremeAnalyze.addActionListener(actionL);
		loadPointExcel.addActionListener(actionL);
		loadSummaryExcel.addActionListener(actionL);
		loadAnalyExcel.addActionListener(actionL);
		loadPointOpExcel.addActionListener(actionL);
		loadOperationExcel.addActionListener(actionL);
		loadOperationAnalyse.addActionListener(actionL);
		loadTotalOperationExcel.addActionListener(actionL);

		loadFuturesData.addActionListener(actionL);
		futuresExtremeAnalyze.addActionListener(actionL);
		futuresLoadPointExcel.addActionListener(actionL);
		futuresLoadSummaryExcel.addActionListener(actionL);
		futuresLoadPointOpExcel.addActionListener(actionL);
		futuresLoadAnalyExcel.addActionListener(actionL);
		futuresLoadOperationExcel.addActionListener(actionL);
		futuresLoadOperationAnalyse.addActionListener(actionL);
		futuresLoadTotalOperationExcel.addActionListener(actionL);

		loadMarket.addActionListener(actionL);
		loadIndustry.addActionListener(actionL);
		loadFirstIndustryConcept.addActionListener(actionL);
		loadThirdIndustrytoStock.addActionListener(actionL);
		loadConcepttoStock.addActionListener(actionL);
		loadStockTwoRong.addActionListener(actionL);
		loadStockBaseface.addActionListener(actionL);
		loadStockToFuturesBaseface.addActionListener(actionL);
		loadStockBaseYearface.addActionListener(actionL);

		stockFunction.add(loadStockData);
		stockFunction.add(extremeAnalyze);
		stockFunction.add(loadPointExcel);
		// stockFunction.add(LoadAnalyExcel);
		stockFunction.add(loadPointOpExcel);
		stockFunction.add(loadSummaryExcel);
		stockFunction.add(loadOperationAnalyse);
		stockFunction.add(loadOperationExcel);
		stockFunction.add(loadTotalOperationExcel);

		futuresFunction.add(loadFuturesData);
		futuresFunction.add(futuresExtremeAnalyze);
		futuresFunction.add(futuresLoadPointExcel);
		// futuresFunction.add(FuturesLoadAnalyExcel);
		futuresFunction.add(futuresLoadPointOpExcel);
		futuresFunction.add(futuresLoadSummaryExcel);
		futuresFunction.add(futuresLoadOperationAnalyse);
		futuresFunction.add(futuresLoadOperationExcel);
		futuresFunction.add(futuresLoadTotalOperationExcel);

		mb.add(stockBase);
		mb.add(stockFunction);
		mb.add(futuresFunction);
		mb.add(stockOperate);
		mb.add(stockMessage);
		
		jFrame.setJMenuBar(mb);	
		jFrame.pack();
		jFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setVisible(true);
		
		 // 窗口关闭事件
		jFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			};
		});
	}

	class MenuActionListenter implements ActionListener {
		int ret = 0;
		StockBasicLoader stockBasicLoader = null;
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if (stockSP != null) {
				jFrame.remove(stockSP);
			}

			if (operationPane != null) {
				jFrame.remove(operationPane);
			}

			if (jop != null) {
				jFrame.remove(jop);
			}

			if (timerPane != null) {
				jFrame.remove(timerPane);
			}

			timerPane = new JPanel();
			timerPane.add(tishiLabel);
			timerPane.add(datepickStart);
			timerPane.add(datepickEnd);
			timerPane.add(checkButton);

			// 复用按钮
			operationPane = new JPanel();
			operationPane.add(addButton);
			operationPane.add(deleteButton);
			operationPane.add(editButton);

			int index = Integer.parseInt(e.getActionCommand());
			switch (index) {
			case 0: // 市场
				try {
					listStockMarket = sbDao.getStockMarket(ConstantsInfo.StockMarket);
				} catch (Exception e1) {
					e1.printStackTrace();
				}

				stMarketTabMod = new StockMarketTableModel(listStockMarket);
				stockTable = new JTable(stMarketTabMod);

				operationPane.setName("market");
				operationPane.add(jlMarket);
				operationPane.add(jtfMarket);

				break;
			case 1: // 地区
				try {
					listStockRegional = sbDao.getStockRegional();
				} catch (Exception e1) {
					e1.printStackTrace();
				} 
				stRegionalTabMod = new StockRegionalTableModel(listStockRegional);
				stockTable = new JTable(stRegionalTabMod);

				operationPane.setName("regional");
				operationPane.add(jlProvince);
				operationPane.add(jtfProvince);
				operationPane.add(jlCity);
				operationPane.add(jtfCity);
				break;
			case 2: // 行业
				try {
					listStockIndustry = sbDao.getStockIndustry();
				} catch (Exception e1) {
					e1.printStackTrace();
				} 

				stIndustyTabMod = new StockIndustryTableModel(listStockIndustry);
				stockTable = new JTable(stIndustyTabMod);

				operationPane.setName("industry");
				operationPane.add(jlFirstIndustry);
				operationPane.add(jtfFirstIndustry);
				operationPane.add(jlSecondIndustry);
				operationPane.add(jtfSecondIndustry);
				operationPane.add(jlThirdIndustry);
				operationPane.add(jtfThirdIndustry);
				break;
			case 3: // 概念
				try {
					listStockConcept = sbDao.getStockConcept();
				} catch (Exception e1) {
					e1.printStackTrace();
				} 

				stConceptTabMod = new StockConceptTableModel(listStockConcept);
				stockTable = new JTable(stConceptTabMod);

				operationPane.setName("concept");
				operationPane.add(jlConcept);
				operationPane.add(jtfConcept);
				break;
			case 4: // 股票
				try {
					listStockSingle = sbDao.getStockSingle();
				} catch (Exception e1) {
					e1.printStackTrace();
				} 

				stSingleTabMod = new StockSingleTableModel(listStockSingle);
				stockTable = new JTable(stSingleTabMod);

				operationPane.setName("stock");
				operationPane.add(jlStockFullId);
				operationPane.add(jtfFullId);
				operationPane.add(jlStockName);
				operationPane.add(jtfStockName);
				operationPane.add(jlStockIndustry);
				operationPane.add(jtfStockIndustry);
				operationPane.add(jlStockConcept);
				operationPane.add(jtfStockConcept);
				break;
			case 6:// 导入交易数据
				loadStockData(ConstantsInfo.StockMarket);
				return;
			case 31:
				loadStockData(ConstantsInfo.FuturesMarket);
				return;
			case 7:// 分析当天极点数据
				tishiLabel.setText("第2步：");
				timerPane.setName("stock_analy_point");
				jFrame.add(timerPane, BorderLayout.NORTH);
				jFrame.validate();
				// analyseStockData(ConstantsInfo.StockMarket);
				return;
			case 32:// 分析当天极点数据
				tishiLabel.setText("第2步：");
				timerPane.setName("future_analy_point");
				jFrame.add(timerPane, BorderLayout.NORTH);
				jFrame.validate();
				// analyseStockData(ConstantsInfo.FuturesMarket);
				return;
			case 8: // 导出数据
				tishiLabel.setText("第3步：");
				timerPane.setName("stock_analy_summary");
				jFrame.add(timerPane, BorderLayout.NORTH);
				jFrame.validate();
				// exportExcelFile(ConstantsInfo.StockMarket);
				return;
			case 33: // 导出数据
				tishiLabel.setText("第3步：");
				timerPane.setName("future_analy_summary");
				jFrame.add(timerPane, BorderLayout.NORTH);
				jFrame.validate();
				// exportExcelFile(ConstantsInfo.FuturesMarket);
				return;
			case 20:
				loadSummaryExcelFile(ConstantsInfo.StockMarket);
				return;
			case 34:
				loadSummaryExcelFile(ConstantsInfo.FuturesMarket);
				return;
			case 21:// 导出统计数据
				exportSummaryExcelFile(ConstantsInfo.StockMarket);
				return;
			case 35:
				exportSummaryExcelFile(ConstantsInfo.FuturesMarket);
				return;

			case 22:// 分析操作数据
				tishiLabel.setText("第7步：");
				timerPane.setName("stock_analy_operation");
				jFrame.add(timerPane, BorderLayout.NORTH);
				jFrame.validate();
				// analyseOperation(ConstantsInfo.StockMarket);
				return;
			case 36:// 分析操作数据
				tishiLabel.setText("第7步：");
				timerPane.setName("stock_analy_operation");
				jFrame.add(timerPane, BorderLayout.NORTH);
				jFrame.validate();
				// analyseOperation(ConstantsInfo.FuturesMarket);
				return;
			case 23:// 导出操作数据
				exportOperationExcelFile(ConstantsInfo.StockMarket);
				return;
			case 37:// 导出操作数据
				exportOperationExcelFile(ConstantsInfo.FuturesMarket);
				return;
			case 24:// 导出操作数据
				exportPointExcelFile(ConstantsInfo.StockMarket);
				return;
			case 38:// 导出操作数据
				exportPointExcelFile(ConstantsInfo.FuturesMarket);
				return;
			case 25:// 导出操作数据
				exportTotalOperationExcelFile(ConstantsInfo.StockMarket);
				return;
			case 39:// 导出操作数据
				exportTotalOperationExcelFile(ConstantsInfo.FuturesMarket);
				return;

			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
			case 15:
			case 16:
			case 17:				
				try {
					loadData(index);
				} catch (Exception e1) {
					e1.printStackTrace();
				} 
				return;
			default:
				break;
			}

			// 增加操作pane
			operationPane.add(queryButton);
			jFrame.add(operationPane, BorderLayout.NORTH);
			// 增加表pane
			stockTable.addMouseListener(new JtableMouseListenter());
			stockTable.setBorder(BorderFactory.createEtchedBorder());
			stockTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			stockTable.setSelectionForeground(Color.blue);
			stockTable.setShowVerticalLines(false);//
			// 设置是否显示单元格间的分割线
			stockTable.setShowHorizontalLines(false);
			stockSP = new JScrollPane(stockTable);

			jFrame.add(stockSP, BorderLayout.CENTER);
			jFrame.validate();
		}

	}

	/**
	 * 表格鼠标事件
	 * @author zhouqiao
	 *
	 */
	class JtableMouseListenter implements MouseListener {
		
		@Override
		public void mouseClicked(MouseEvent e) {

			if (e.getClickCount() == 2) // 实现双击
			{
				String paneNmae = operationPane.getName();
				// 获得行位置
				int row = ((JTable) e.getSource()).rowAtPoint(e.getPoint()); 
				if (paneNmae.equals("concept")) {
					System.out.println("name:" + listStockConcept.get(row).getName());
				} else if (paneNmae.equals("industry")) {
					System.out.println("operate industry");
				} else if (paneNmae.equals("regional")) {
					System.out.println("operate regional");
				} else if (paneNmae.equals("market")) {
					System.out.println("operate market");
				} else if (paneNmae.equals("stock")) {
					System.out.println("operate stock");
					String fullId = listStockSingle.get(row).getStockFullId();
					JFreeChart timeSeriesChart = null;
					StockTimeSeriesChart stsc = new StockTimeSeriesChart(sbDao, spDao);

					try {
						timeSeriesChart = stsc.createTimeSeriesChart(fullId);
					} catch (Exception e1) {	
						e1.printStackTrace();
					}
					
					ChartFrame cframe = new ChartFrame("StockChart", timeSeriesChart);		
					cframe.pack();
					cframe.setVisible(true);

				}

			}

		}

		@Override
		public void mouseEntered(MouseEvent arg0) {

		}
		@Override
		public void mouseExited(MouseEvent arg0) {

		}
		@Override
		public void mousePressed(MouseEvent arg0) {

		}
		@Override
		public void mouseReleased(MouseEvent arg0) {

		}

	}

	/**
	 * @author zhouqiao
	 *
	 */
	class ButtonMouseListenter implements MouseListener {
		@Override
		public void mousePressed(MouseEvent e) {
		}
		@Override
		public void mouseEntered(MouseEvent e) {

		}
		@Override
		public void mouseExited(MouseEvent e) {

		}
		@Override
		public void mouseClicked(MouseEvent e) {

			String paneNmae = operationPane.getName();
			String timeNmae = timerPane.getName();
			// System.out.println("pane name:"+operationPane.getName());

			if (jop != null) {
				jFrame.remove(jop);
			}

			if (e.getSource() == addButton) {

				addButton.setBackground(Color.BLUE);

				if (paneNmae.equals("concept")){
					System.out.println("operate concept");
				} else if (paneNmae.equals("industry")) {
					System.out.println("operate industry");
				} else if (paneNmae.equals("regional")){
					System.out.println("operate regional");
				} else if (paneNmae.equals("market")) {
					JLabel jlCode = new JLabel("代码");
					JLabel jlName = new JLabel("名称");
					final JTextField jtfCode = new JTextField(6);
					final JTextField jtfName = new JTextField(24);
					JButton cancelButton = new JButton("取消");
					JButton confirmButton = new JButton("确定");

					final JFrame jfDialog = new JframeDialog();
					// JFrame jfDialog = new JFrame();
					JPanel dialogJP = new JPanel();

					dialogJP.add(jlCode);
					dialogJP.add(jtfCode);
					dialogJP.add(jlName);
					dialogJP.add(jtfName);
					dialogJP.add(cancelButton);
					dialogJP.add(confirmButton);
					// 取消
					cancelButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent event) {
							jfDialog.dispose();
						}
					});
					confirmButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent event) {

							StockMarket sMarket = new StockMarket(1, jtfCode.getText(), jtfName.getText(), "");
							try {
								sbDao.insertStockMarket(sMarket);
							} catch (Exception e) {
								e.printStackTrace();
							}
							jfDialog.dispose();
						}
					});

					jfDialog.add(dialogJP, BorderLayout.CENTER);
					jfDialog.setVisible(true);
					System.out.println("operate market info");
				} else if (paneNmae.equals("stock")) {
					addSingleStock();
				}

			} else if (e.getSource() == editButton) {
				editButton.setBackground(Color.green);
				if (paneNmae.equals("concept")){
					System.out.println("operate concept");
				} else if (paneNmae.equals("industry")){
					System.out.println("operate industry");
				} else if (paneNmae.equals("regional")){
					System.out.println("operate regional");
				} else if (paneNmae.equals("market")){
					System.out.println("operate market");
				} else if (paneNmae.equals("stock")) {
					System.out.println("operate stock");
				}

			} else if (e.getSource() == deleteButton) {
				deleteButton.setBackground(Color.RED);
				if (paneNmae.equals("concept")) {
					System.out.println("operate concept");
				} else if (paneNmae.equals("industry")) {
					System.out.println("operate industry");
				} else if (paneNmae.equals("regional")) {
					System.out.println("operate regional");
				} else if (paneNmae.equals("market")) {
					int selectedRow = stockTable.getSelectedRow();// 获得选中行的索引
					System.out.println("del selectedRow:" + selectedRow);
					if (selectedRow != -1) // 存在选中行
					{
						stMarketTabMod.removeRow(selectedRow); // 删除行
					}

					System.out.println("del market");
				} else if (paneNmae.equals("stock")) {
					int selectedRow = stockTable.getSelectedRow();// 获得选中行的索引
					System.out.println("del selectedRow:" + selectedRow);
					if (selectedRow != -1) // 存在选中行
					{
						System.out.println("del 1111selectedRow:" + selectedRow);
						stSingleTabMod.removeRow(selectedRow); // 删除行

						StockSingle ss = listStockSingle.get(selectedRow);
						System.out.println("del stock:" + ss.getStockFullId() + ":" + ss.getStockName());
						// 删除
						try {
							sbDao.deleteStockSingle(ss);
						} catch (Exception ev) {							
							ev.printStackTrace();
						}
						listStockSingle.remove(ss);
						System.out.println(listStockSingle.size());
					}
				}
			} else if (e.getSource() == queryButton) {
				queryButton.setBackground(Color.PINK);
				if (paneNmae.equals("concept")) {
					System.out.println("operate concept");
				} else if (paneNmae.equals("industry")) {
					System.out.println("operate industry");
				} else if (paneNmae.equals("regional")) {
					System.out.println("operate regional");
				} else if (paneNmae.equals("market")) {
					System.out.println(jtfMarket.getText());
				}

			} else if (e.getSource() == checkButton) {

				String startTime = CommonDate.formatDate((Date) datepickStart.getValue());
				String endTime = CommonDate.formatDate((Date) datepickEnd.getValue());

				if (timeNmae.equals("stock_analy_summary")){
					exportExcelFile(ConstantsInfo.StockMarket, startTime, endTime);
				} else if (timeNmae.equals("future_analy_summary")){
					exportExcelFile(ConstantsInfo.FuturesMarket, startTime, endTime);
				}else if (timeNmae.equals("stock_analy_point")){
					analyseStockData(ConstantsInfo.StockMarket, startTime, endTime);
				}else if (timeNmae.equals("future_analy_point")){
					analyseStockData(ConstantsInfo.FuturesMarket, startTime, endTime);
				}else if (timeNmae.equals("stock_analy_operation")) {					
					analyseOperation(ConstantsInfo.StockMarket, startTime, endTime);
				} else if (timeNmae.equals("future_analy_operation")){
					analyseOperation(ConstantsInfo.FuturesMarket, startTime, endTime);
				}
			}

		}
		
		@Override
		public void mouseReleased(MouseEvent e) {

		}

	}

	class JframeDialog extends JFrame {
		private static final long serialVersionUID = 1L;

		public JframeDialog() {
			Jfinit();
		}

		public void Jfinit() {
			this.setSize(300, 500);
			this.setTitle("弹出框");
			this.setLocationRelativeTo(null);
		}
	}
	
	public void loadData(int loadType) throws IOException, ClassNotFoundException, SQLException {
		int ret = 0;
		StockBasicLoader stockBasicLoader = null;
		stockBasicLoader = new StockBasicLoader(sbDao);
		String desc = null;

		switch (loadType) {
		case 9:
			 // 导入一级行业
			ret = stockBasicLoader.readIndustry();
			desc = "数据导入出错，请检查文件1Industry";
			break;
		case 10:
			// 导入一级行业对应概念
			ret = stockBasicLoader.readFirstIndustry_To_Concept();
			desc = "数据导入出错，请检查文件2FirstIndustry-to-Concept.xlsx";
			break;
		case 11:
			// 导入三级行业对应股票
			ret = stockBasicLoader.readThirdIndustry_to_stock();
			desc = "数据导入出错，请检查文件3ThirdIndustry-to-stock.xlsx";
			break;
		case 12:
			// 导入概念对应股票
			ret = stockBasicLoader.readConcept_to_stock();
			desc = "数据导入出错，请检查文件4Concept-to-stock.xlsx";
			break;
		case 13:
			// 导入两融
			ret = stockBasicLoader.readTwoRong();
			desc = "数据导入出错，请检查文件5TwoRong.xlsx";
			break;
		case 14:
			// 导入基本面
			ret = stockBasicLoader.readstock_baseExpect();
			desc = "数据导入出错，请检查文件5TwoRong.xlsx";
			break;
		case 15: 
			// 导入基本面
			ret = stockBasicLoader.readMarketInfo();
			ret = stockBasicLoader.readFuturesInfo();
			desc = "数据导入出错，请检查文件0Market_BaseInfo.xlsx或7ExMarket_BaseInfo.xlsx";
			break;
		case 16:
			// 导入基本面
			ret = stockBasicLoader.readStockToFeatures();
			desc = "数据导入出错，请检查文件8ExMarket-to-stock.xlsx";
			break;
		case 17:
			// 年份信息面
			ret = stockBasicLoader.readYearInfo();
			desc = "数据导入出错，请检查文件9 10 11 12四个文件";
			break;
		default:
			break;
		}

		if (ret != 0) {
			JOptionPane.showMessageDialog(jFrame, desc, "提示", JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(jFrame, "数据导入成功", "提示", JOptionPane.INFORMATION_MESSAGE);
		}
		return;
	}


	/**
	 * 1导入交易数据
	 * @param type
	 */
	public void loadStockData(int type) {
		Date start = new Date();
		StockDataLoader fr = new StockDataLoader(sbDao, sdDao, spDao, ssDao);
		int ret = 0;
		stockLogger.logger.fatal("******load stock data start*****");
		// 导入 部分日数据 并计算ma5 涨幅
		if (type == ConstantsInfo.StockMarket){
			ret = fr.loadAllDataInfile();
		} else {
			ret = fr.loadAllFuturesDataInfile();
		}

		Date end = new Date();
		long minute = (end.getTime() - start.getTime()) / 60000;

		stockLogger.logger.fatal("load stock data consume " + minute + " minute");
		stockLogger.logger.fatal("******load stock data end*****");
	
		if (ret == 0){
			JOptionPane.showMessageDialog(jFrame, "第1步数据导入成功,耗时" + minute + "分钟", "提示", JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(jFrame, "第1步数据已经导入，不用重复", "提示", JOptionPane.INFORMATION_MESSAGE);
		}

		return;
	}

	/**
	 * 2分析股票交易数据
	 * @param type
	 * @param startdate
	 * @param enddate
	 */
	public void analyseStockData(int type, String startdate, String enddate) {

		PointAnalysis pc = new PointAnalysis(sbDao, sdDao, spDao);
		stockLogger.logger.fatal("******analyse point start*****");
		Date start = new Date();

		// 计算当天数据
		if (type == ConstantsInfo.StockMarket) {
			pc.getPointToTable(ConstantsInfo.StockCalCurData, ConstantsInfo.ALLMarket, ConstantsInfo.StockMarket,
					startdate, enddate);
		} else {
			pc.getPointToTable(ConstantsInfo.StockCalCurData, ConstantsInfo.ALLMarket, ConstantsInfo.FuturesMarket,
					startdate, enddate);
		}
		Date end = new Date();
		long minute = (end.getTime() - start.getTime()) / 60000;

		stockLogger.logger.fatal("analyse point consume " + minute + " minute");
		stockLogger.logger.fatal("******analyse point end*****");

		JOptionPane.showMessageDialog(jFrame, "第2步极点分析成功,耗时" + minute + "分钟", "提示", JOptionPane.INFORMATION_MESSAGE);
		return;
	}

	private static DatePicker getDatePicker() {
		DatePicker datepick;
		// 格式
		String defaultFormat = "yyyy-MM-dd hh:mm:ss";
		// 当前时间
		Date date = new Date();
		// 字体
		Font font = new Font("Times New Roman", Font.BOLD, 14);
		Dimension dimension = new Dimension(180, 30);
		datepick = new DatePicker(date, defaultFormat, font, dimension);
		datepick.setLocation(130, 90);// 设置起始位置
		datepick.setLocale(Locale.CHINA);
		// 设置时钟面板可见
		datepick.setTimePanleVisible(true);
		return datepick;
	}

	/**
	 * 3导出分析数据
	 * @param type
	 * @param startdate
	 * @param enddate
	 */
	public void exportExcelFile(int type, String startdate, String enddate) {

		StockExcelExporterMain sep = new StockExcelExporterMain(sbDao, sdDao, spDao, ssDao);

		List<String> listStockDate = new ArrayList<String>();
		try {
			listStockDate = sdDao.getDatesFromSH000001ForStartEnd(startdate, enddate);
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
		stockLogger.logger.fatal("******analyse summary and export all execl start*****");
		Date start = new Date();
		int size = listStockDate.size();

		for (int i = 0; i < listStockDate.size(); i++) {
			String date = listStockDate.get(i);
			stockLogger.logger.fatal("analyse summary time:" + date);

			try {
				if (type == ConstantsInfo.StockMarket) {
					if (i == size - 1) {
						sep.writeExcelFormIndustryOrderBy("export\\", date, true);
					} else {
						sep.writeExcelFormIndustryOrderBy("export\\", date, false);
					}
				} else {
					if (i == size - 1) {
						sep.writeExcelFormFuturesOrderBy("export\\", date, true);
					} else {
						sep.writeExcelFormFuturesOrderBy("export\\", date, false);
					}
				}
			} catch (Exception e) {
				stockLogger.logger.fatal(e.toString());
			}
		}

		Date end = new Date();
		long minute = (end.getTime() - start.getTime()) / 60000;
		stockLogger.logger.fatal("analyse summary and export all execl consume " + minute + " minute");
		stockLogger.logger.fatal("******analyse summary and export all execl end*****");
		JOptionPane.showMessageDialog(jFrame, "第3步导出成功,耗时" + minute + "分钟", "提示", JOptionPane.INFORMATION_MESSAGE);
		return;
	}

	/**
	 *  4导入统计表
	 * @param type
	 */
	public void loadSummaryExcelFile(int type) {
		LoadSummaryMain seRead = new LoadSummaryMain(sbDao, sdDao, spDao, ssDao);
		try {
			seRead.readStockAnsyleExcelData(type);
		} catch (Exception e) {
			stockLogger.logger.fatal(e.toString());
		}	
		JOptionPane.showMessageDialog(jFrame, "第4步导入成功", "提示", JOptionPane.INFORMATION_MESSAGE);
		return;
	}

	/**
	 * 5导出极点数据
	 * @param type
	 */
	public void exportPointExcelFile(int type) {
		Date startDate1 = new Date();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		String dateNowStr1 = sdf1.format(startDate1);
		StockExcelExporterMain sep = new StockExcelExporterMain(sbDao, sdDao, spDao, ssDao);
		try {

			String dateRectely = sdDao.getRecetlyDateFromSH000001(dateNowStr1);
			if (dateRectely == null || dateRectely == "") {
				return;
			}
			if (type == ConstantsInfo.StockMarket) {
				// sep.writePointExcelFormConceptInFirstIndustryOrderBy("export\\",dateNowStr1);
				sep.writePointExcelFormIndustryOrderBy("export\\", dateRectely);
			} else {
				sep.writePointExcelFormFuturesOrderBy("export\\", dateRectely);
			}
		} catch (Exception e) {
			stockLogger.logger.fatal(e.toString());
		}

		JOptionPane.showMessageDialog(jFrame, "第5步导出成功", "提示", JOptionPane.INFORMATION_MESSAGE);
		return;
	}


	/**
	 * 6导出统计数据
	 * @param type
	 */
	public void exportSummaryExcelFile(int type) {
		Date startDate1 = new Date();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		String dateNowStr1 = sdf1.format(startDate1);

		StockExcelExporterMain sep = new StockExcelExporterMain(sbDao, sdDao, spDao, ssDao);
		try {
			String dateRectely = sdDao.getRecetlyDateFromSH000001(dateNowStr1);
			if (dateRectely == null || dateRectely == "") {
				return;
			}
			stockLogger.logger.fatal("export summary excel time:" + dateRectely);
			if (type == ConstantsInfo.StockMarket) {
				sep.writeSummaryExcelFormConceptInFirstIndustryOrderBy("export\\", dateRectely);
			} else {
				sep.writeSummaryExcelFormFuturesOrderBy("export\\", dateRectely);
			}
		} catch (Exception e) {
			stockLogger.logger.fatal(e.toString());
		}

		JOptionPane.showMessageDialog(jFrame, "第6步导出成功", "提示", JOptionPane.INFORMATION_MESSAGE);
		return;
	}

	/**
	 * 7买卖操作分析
	 * @param type
	 * @param startdate
	 * @param enddate
	 */
	public void analyseOperation(int type, String startdate, String enddate) {
		OperationAnalysis sop = new OperationAnalysis(sbDao, sdDao, spDao, ssDao);

		List<String> listStockDate = new ArrayList<String>();
		try {
			listStockDate = sdDao.getDatesFromSH000001ForStartEnd(startdate, enddate);
		} catch (Exception e) {
			stockLogger.logger.fatal(e.toString());
		}

		stockLogger.logger.fatal("******analy operation start******");
		Date start = new Date();
		
		for (int i = 0; i < listStockDate.size(); i++) {
			String date = listStockDate.get(i);
			stockLogger.logger.fatal("analy operation time:" + date);
			try {
				if (type == ConstantsInfo.StockMarket) {
					sop.analyseStockOperationAll(ConstantsInfo.StockMarket, date);
				} else {
					sop.analyseStockOperationAll(ConstantsInfo.FuturesMarket, date);
				}
			} catch (Exception e) {
				stockLogger.logger.fatal(e.toString());
			}
		}
		
		Date end = new Date();
		long minute = (end.getTime() - start.getTime()) / 60000;

		stockLogger.logger.fatal("analy operation consume " + minute + " minute");
		stockLogger.logger.fatal("*****analy operation end*****");

		JOptionPane.showMessageDialog(jFrame, "第7步操作分析成功,耗时" + minute + "分钟", "提示", JOptionPane.INFORMATION_MESSAGE);
		return;
	}

	public void errorDialog() {		
		JOptionPane.showMessageDialog(jFrame, "分析失败,请检查日志", "提示", JOptionPane.INFORMATION_MESSAGE);
		return;
	}

	
	/**
	 * 8导出操作分析数据
	 * @param type
	 */
	public void exportOperationExcelFile(int type) {
		Date startDate1 = new Date();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		String dateNowStr1 = sdf1.format(startDate1);

		StockExcelExporterMain sep = new StockExcelExporterMain(sbDao, sdDao, spDao, ssDao);
		try {
			String dateRectely = sdDao.getRecetlyDateFromSH000001(dateNowStr1);
			if (dateRectely == null || dateRectely == "") {
				return;
			}

			stockLogger.logger.fatal("export operation excel time:" + dateRectely);
			if (type == ConstantsInfo.StockMarket) {
				sep.writeOperationExcelFormIndustryOrderByAllType("export\\", dateRectely, ConstantsInfo.DayDataType);
				sep.writeOperationExcelFormIndustryOrderByAllType("export\\", dateRectely, ConstantsInfo.WeekDataType);
				sep.writeOperationExcelFormIndustryOrderByAllType("export\\", dateRectely,
						ConstantsInfo.MonthDataType);
			} else {
				// sep.writeOperationExcelFormFuturesOrderBy("export\\",dateNowStr1);
				sep.writeOperationExcelFormFuturesOrderByAllType("export\\", dateRectely, ConstantsInfo.DayDataType);
				sep.writeOperationExcelFormFuturesOrderByAllType("export\\", dateRectely, ConstantsInfo.WeekDataType);
				sep.writeOperationExcelFormFuturesOrderByAllType("export\\", dateRectely, ConstantsInfo.MonthDataType);
			}
		} catch (Exception e) {
			stockLogger.logger.fatal(e.toString());
		}
	
		JOptionPane.showMessageDialog(jFrame, "第8步导出成功", "提示", JOptionPane.INFORMATION_MESSAGE);
		return;
	}

	/**
	 *  9导出总的操作分析
	 * @param type
	 */
	public void exportTotalOperationExcelFile(int type) {
		Date startDate1 = new Date();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		String dateNowStr1 = sdf1.format(startDate1);
		stockLogger.logger.fatal("******export total operation start*****");
		StockExcelExporterMain sep = new StockExcelExporterMain(sbDao, sdDao, spDao, ssDao);
		try {
			String dateRectely = sdDao.getRecetlyDateFromSH000001(dateNowStr1);
			if (dateRectely == null || dateRectely == "") {
				return;
			}
			stockLogger.logger.fatal("export total operation excel time:" + dateRectely);
			if (type == ConstantsInfo.StockMarket) {				
				sep.writeTotalOperationExcelFormIndustryOrderByAllType("export\\", dateRectely,
						ConstantsInfo.DayDataType);
				sep.writeTotalOperationExcelFormIndustryOrderByAllType("export\\", dateRectely,
						ConstantsInfo.WeekDataType);
				sep.writeTotalOperationExcelFormIndustryOrderByAllType("export\\", dateRectely,
						ConstantsInfo.MonthDataType);

			} else {
				sep.writeTotalOperationExcelFormFuturesOrderByAllType("export\\", dateRectely,
						ConstantsInfo.DayDataType);
				sep.writeTotalOperationExcelFormFuturesOrderByAllType("export\\", dateRectely,
						ConstantsInfo.WeekDataType);
				sep.writeTotalOperationExcelFormFuturesOrderByAllType("export\\", dateRectely,
						ConstantsInfo.MonthDataType);

			}
		} catch (Exception e) {
			stockLogger.logger.fatal(e.toString());
		}
		Date end = new Date();
		long minute = (end.getTime() - startDate1.getTime()) / 60000;

		stockLogger.logger.fatal("analy total operation consume " + minute + " minute");
		stockLogger.logger.fatal("******export total operation end*****");
		JOptionPane.showMessageDialog(jFrame, "第9步总操作导出成功,耗时" + minute + "分钟", "提示", JOptionPane.INFORMATION_MESSAGE);
		return;
	}

	/**
	 * 增加股票
	 * 
	 */
	public void addSingleStock() {
		String listItem[] = { "否", "是" };
		JLabel jlCode = new JLabel("股票代码");
		JLabel jlName = new JLabel("名称");
		JLabel jlIndustry = new JLabel("行业");
		JLabel jlConcept = new JLabel("概念");

		JLabel jlMarginTrading = new JLabel("融资融券");
		final JTextField jtfCode = new JTextField(6);
		final JTextField jtfName = new JTextField(12);
		final JTextField jtfieldIndu = new JTextField(24); 
		final JTextField jtfieldConc = new JTextField(48); 
		// 概念
		List<String> allConceptNameList = null;
		try {
			allConceptNameList = sbDao.getAllConceptName();
		} catch (SQLException e3) {

			e3.printStackTrace();
		}
		final String[] conceptName = (String[]) allConceptNameList.toArray(new String[allConceptNameList.size()]);
		final JList jlistConcept = new JList(conceptName);

		// 行业
		List<String> allThirdIndustryNameList = null;
		try {
			allThirdIndustryNameList = sbDao.getAllThirdIndustryName();
		} catch (SQLException e3) {
			e3.printStackTrace();
		}
		String[] industryName = (String[]) allThirdIndustryNameList
				.toArray(new String[allThirdIndustryNameList.size()]);
		final JList jlistIndustry = new JList(industryName);
		jlistIndustry.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// 融资融券
		final JList jlistMarginTrading = new JList(listItem);
		jlistMarginTrading.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JButton cancelButton = new JButton("取消");
		JButton confirmButton = new JButton("确定");
		final JFrame jfDialog = new JFrame(); 
		JPanel dialogJP = new JPanel();
		JPanel dialogJPCenter = new JPanel();
		JPanel dialogJPBottom = new JPanel();
		dialogJP.add(jlCode);
		dialogJP.add(jtfCode);
		dialogJP.add(jlName);
		dialogJP.add(jtfName);
		dialogJPCenter.add(jlIndustry);
		dialogJPCenter.add(new JScrollPane(jlistIndustry));
		dialogJPCenter.add(jlConcept);
		dialogJPCenter.add(new JScrollPane(jlistConcept));
		dialogJPCenter.add(jlMarginTrading);
		dialogJPCenter.add(jlistMarginTrading);
		dialogJPBottom.add(cancelButton);
		dialogJPBottom.add(confirmButton);

		if (jop != null) {
			jfDialog.remove(jop);
		}

		// 行业选择框
		jlistIndustry.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				jtfieldIndu.setText(((JList) arg0.getSource()).getSelectedValue().toString());
				System.out.println(jlistIndustry.getSelectedValue().toString());
			}
		});

		// 概念选择框
		jlistConcept.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {				
				jtfieldConc.setText(((JList) arg0.getSource()).getSelectedValue().toString());
			}
		});

		// 取消
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				jfDialog.dispose();
			}
		});

		confirmButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				String stockCode = null;
				StockSingle sStock = null;
				// 检查股票代码长度
				if (jtfCode.getText().length() != 6) {
					jop = new JOptionPane();
					
					jfDialog.add(jop, BorderLayout.NORTH);
					JOptionPane.showMessageDialog(jFrame, "股票代号长度有错误,请输入6位正确的代码", "提示", JOptionPane.INFORMATION_MESSAGE);
					return;
				}

				if (jtfCode.getText().startsWith("6")) {
					stockCode = "SH" + jtfCode.getText();
				} else {
					stockCode = "SZ" + jtfCode.getText();
				}

				// 检查股票代码
				try {
					sStock = sbDao.lookUpStockSingle(stockCode);
				} catch (Exception e2) {
					e2.printStackTrace();
				} 
				
				// 已经存在
				if (sStock != null) {					
					JOptionPane.showMessageDialog(jFrame, "该股票信息已经存在", "提示", JOptionPane.INFORMATION_MESSAGE);
					return;
				}

				// 检查行业
				StockIndustry sindustry = null;
				try {
					sindustry = sbDao.lookUpIndustry(jtfieldIndu.getText());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if (sindustry == null) {					
					JOptionPane.showMessageDialog(jFrame, "行业名有错误,请输入正确的行业", "提示", JOptionPane.INFORMATION_MESSAGE);
					return;
				}

				// 检查概念
				StockConcept sconcept = null;
				try {
					sconcept = sbDao.lookUpStockConcept(jtfieldConc.getText());
				} catch (Exception e) {
					e.printStackTrace();
				} 

				if (sconcept == null) {				
					JOptionPane.showMessageDialog(jFrame, "概念不存在,请输入正确的概念", "提示", JOptionPane.INFORMATION_MESSAGE);
					return;
				}

				// 插入新股票
				sStock = new StockSingle(1, stockCode, jtfName.getText(), sindustry.getThirdcode(),
						jtfieldIndu.getText(), sindustry.getSecondcode(), sindustry.getSecondname(),
						sindustry.getFirstcode(), sindustry.getFirstname(), jtfieldConc.getText(),
						jlistMarginTrading.getSelectedIndex());
				try {
					// 插入stock_to_industry
					sbDao.insertStockSingleToIndustry(sStock);
					// 插入allinfo
					sbDao.insertStockSingle(sStock);
					// 插入stock_to_concept
					// sbDao.insertStockToConcept(stockCode,jtfName.getText(),sconcept.getCode(),sconcept.getName());
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}		
				
				JOptionPane.showMessageDialog(jFrame, "数据更新成功", "提示", JOptionPane.INFORMATION_MESSAGE);
				jfDialog.dispose();// 关闭
			}
		});
		jfDialog.add(dialogJP, BorderLayout.NORTH);
		jfDialog.add(dialogJPCenter, BorderLayout.CENTER);
		jfDialog.add(dialogJPBottom, BorderLayout.SOUTH);
		// jfDialog.setExtendedState(Frame.MAXIMIZED_BOTH);
		jfDialog.setSize(500, 300);

		jfDialog.setVisible(true);
		jfDialog.setLocationRelativeTo(null);
		System.out.println("operate stock info");

	}

	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {

		Connection stockBaseConn = DbConn.getConnDB("stockConf/conn_base_db.ini");
		Connection stockDataConn = DbConn.getConnDB("stockConf/conn_data_db.ini");
		Connection stockPointConn = DbConn.getConnDB("stockConf/conn_point_db.ini");
		Connection stockSummaryConn = DbConn.getConnDB("stockConf/conn_summary_db.ini");

		stockBM = new StockBaseManager(stockBaseConn, stockDataConn, stockPointConn, stockSummaryConn);
		PropertyConfigurator.configure("StockConf/log4j_manage.properties");
		stockLogger.logger.fatal("stock manager start");
		stockBM.init();

		// stockBaseConn.close();
		// stockPointConn.close();
		// stockDataConn.close();

	}

}
