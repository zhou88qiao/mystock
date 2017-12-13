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

	private static JFrame jFrame = new JFrame("��Ʊ������Ϣ");
	
	private JMenuBar mb = new JMenuBar();
	JMenu stockBase = new JMenu("��Ʊ��Ϣ");
	JMenu stockFunction = new JMenu("��Ʊ���Ĺ���");
	JMenu futuresFunction = new JMenu("�ڻ����Ĺ���");
	JMenu stockOperate = new JMenu("��������");
	JMenu stockMessage = new JMenu("��Ѷ");

	JMenuItem loadStockData = new JMenuItem("1 ���뽻������");
	JMenuItem extremeAnalyze = new JMenuItem("2 �������ݷ���");
	JMenuItem loadPointExcel = new JMenuItem("3 ������������all");
	JMenuItem loadAnalyExcel = new JMenuItem("4 �����������");
	JMenuItem loadPointOpExcel = new JMenuItem("5 ������������point");
	JMenuItem loadSummaryExcel = new JMenuItem("6 ����ͳ������summary");
	JMenuItem loadOperationAnalyse = new JMenuItem("7 ������������");
	JMenuItem loadOperationExcel = new JMenuItem("8 ����������������operation");
	JMenuItem loadTotalOperationExcel = new JMenuItem("9 �����ܲ�������totaloperation");

	JMenuItem loadFuturesData = new JMenuItem("1 �����ڻ���Ʒ����");
	JMenuItem futuresExtremeAnalyze = new JMenuItem("2 �������ݷ���");
	JMenuItem futuresLoadPointExcel = new JMenuItem("3 ������������");
	JMenuItem futuresLoadAnalyExcel = new JMenuItem("4 �����������");
	JMenuItem futuresLoadPointOpExcel = new JMenuItem("5 ������������");
	JMenuItem futuresLoadSummaryExcel = new JMenuItem("6 ����ͳ������");
	JMenuItem futuresLoadOperationAnalyse = new JMenuItem("7 ������������");
	JMenuItem futuresLoadOperationExcel = new JMenuItem("8 ����������������");
	JMenuItem futuresLoadTotalOperationExcel = new JMenuItem("9 �����ܲ�������totaloperation");

	JMenuItem loadMarket = new JMenuItem("0 �����Ʊ���ڻ���Ʒ�г�");
	JMenuItem loadIndustry = new JMenuItem("1 ����һ��������ҵ");
	JMenuItem loadFirstIndustryConcept = new JMenuItem("2 ����һ����ҵ��Ӧ����");
	JMenuItem loadThirdIndustrytoStock = new JMenuItem("3 ����������ҵ��Ӧ��������");
	JMenuItem loadConcepttoStock = new JMenuItem("4 ��������Ӧ��������");
	JMenuItem loadStockTwoRong = new JMenuItem("5 �������������ȯ");
	JMenuItem loadStockBaseface = new JMenuItem("6 ������ɻ�����");
	JMenuItem loadStockToFuturesBaseface = new JMenuItem("7 �����ڻ���Ʒ��Ӧ��������");
	JMenuItem loadStockBaseYearface = new JMenuItem("8 �����Ʊ��ݽ�����Ϣ");

	DatePicker datepickStart = getDatePicker();
	DatePicker datepickEnd = getDatePicker();

	JButton addButton = new JButton("�½�"); 
	JButton editButton = new JButton("�༭");
	JButton deleteButton = new JButton("ɾ��");
	JButton queryButton = new JButton("��ѯ");
	JButton checkButton = new JButton("ȷ��");

	//JPanel Panel = new JPanel();
	JLabel jlMarket = new JLabel("�г�");
	JLabel jlConcept = new JLabel("����");
	JLabel jlProvince = new JLabel("ʡ����");
	JLabel jlCity = new JLabel("������");
	JLabel jlFirstIndustry = new JLabel("һ����ҵ����");
	JLabel jlSecondIndustry = new JLabel("������ҵ����");
	JLabel jlThirdIndustry = new JLabel("������ҵ����");
	JLabel jlStockFullId = new JLabel("��Ʊ����");
	JLabel jlStockName = new JLabel("��Ʊ����");
	JLabel jlStockIndustry = new JLabel("������ҵ");
	JLabel jlStockConcept = new JLabel("����");
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
		String[] baseItems = new String[] { "�г�", "����", "��ҵ", "����", "����" };
		char[] baseShortcuts = { 'M', 'R', 'I', 'C', 'S' };
		for (i = 0; i < baseItems.length; i++) {
			// JMenuItem item = new JMenuItem(baseItems[i], baseShortcuts[i]);
			JMenuItem item = new JMenuItem(baseItems[i]);
			item.setAccelerator(KeyStroke.getKeyStroke(baseShortcuts[i],
					Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
			// ����i �¼���������ȡ
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
		extremeAnalyze.setActionCommand("" + 7); // ����i �¼���������ȡ
		loadPointExcel.setActionCommand("" + 8); // ����i �¼���������ȡ
		loadAnalyExcel.setActionCommand("" + 20); // �������ݿ�
		loadSummaryExcel.setActionCommand("" + 21); // ����ͳ�Ʊ�
		loadOperationAnalyse.setActionCommand("" + 22); // ��������
		loadOperationExcel.setActionCommand("" + 23); // ��������������
		loadPointOpExcel.setActionCommand("" + 24);// ������������
		loadTotalOperationExcel.setActionCommand("" + 25); // �����ܲ���������

		loadFuturesData.setActionCommand("" + 31); // ����i �¼���������ȡ
		futuresExtremeAnalyze.setActionCommand("" + 32); // ����i �¼���������ȡ
		futuresLoadPointExcel.setActionCommand("" + 33); // ����i �¼���������ȡ
		futuresLoadAnalyExcel.setActionCommand("" + 34); // �������ݿ�
		futuresLoadSummaryExcel.setActionCommand("" + 35); // ����ͳ�Ʊ�
		futuresLoadOperationAnalyse.setActionCommand("" + 36); // ��������
		futuresLoadOperationExcel.setActionCommand("" + 37); // ��������������
		futuresLoadPointOpExcel.setActionCommand("" + 38);// ������������
		futuresLoadTotalOperationExcel.setActionCommand("" + 39); // �����ܲ���������

		loadIndustry.setActionCommand("" + 9); // ������ҵ
		loadFirstIndustryConcept.setActionCommand("" + 10); // ����һ����ҵ��Ӧ�ĸ���
		loadThirdIndustrytoStock.setActionCommand("" + 11); // ����������ҵ��Ӧ�Ĺ�Ʊ
		loadConcepttoStock.setActionCommand("" + 12); // �����Ӧ��Ʊ
		loadStockTwoRong.setActionCommand("" + 13); // ����
		loadStockBaseface.setActionCommand("" + 14); // ������
		loadMarket.setActionCommand("" + 15);// ���� �г�
		loadStockToFuturesBaseface.setActionCommand("" + 16);// �����ڻ���Ӧ����Ʊ
		loadStockBaseYearface.setActionCommand("" + 17);// ���������Ϣ
		
		

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
		
		 // ���ڹر��¼�
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

			// ���ð�ť
			operationPane = new JPanel();
			operationPane.add(addButton);
			operationPane.add(deleteButton);
			operationPane.add(editButton);

			int index = Integer.parseInt(e.getActionCommand());
			switch (index) {
			case 0: // �г�
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
			case 1: // ����
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
			case 2: // ��ҵ
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
			case 3: // ����
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
			case 4: // ��Ʊ
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
			case 6:// ���뽻������
				loadStockData(ConstantsInfo.StockMarket);
				return;
			case 31:
				loadStockData(ConstantsInfo.FuturesMarket);
				return;
			case 7:// �������켫������
				tishiLabel.setText("��2����");
				timerPane.setName("stock_analy_point");
				jFrame.add(timerPane, BorderLayout.NORTH);
				jFrame.validate();
				// analyseStockData(ConstantsInfo.StockMarket);
				return;
			case 32:// �������켫������
				tishiLabel.setText("��2����");
				timerPane.setName("future_analy_point");
				jFrame.add(timerPane, BorderLayout.NORTH);
				jFrame.validate();
				// analyseStockData(ConstantsInfo.FuturesMarket);
				return;
			case 8: // ��������
				tishiLabel.setText("��3����");
				timerPane.setName("stock_analy_summary");
				jFrame.add(timerPane, BorderLayout.NORTH);
				jFrame.validate();
				// exportExcelFile(ConstantsInfo.StockMarket);
				return;
			case 33: // ��������
				tishiLabel.setText("��3����");
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
			case 21:// ����ͳ������
				exportSummaryExcelFile(ConstantsInfo.StockMarket);
				return;
			case 35:
				exportSummaryExcelFile(ConstantsInfo.FuturesMarket);
				return;

			case 22:// ������������
				tishiLabel.setText("��7����");
				timerPane.setName("stock_analy_operation");
				jFrame.add(timerPane, BorderLayout.NORTH);
				jFrame.validate();
				// analyseOperation(ConstantsInfo.StockMarket);
				return;
			case 36:// ������������
				tishiLabel.setText("��7����");
				timerPane.setName("stock_analy_operation");
				jFrame.add(timerPane, BorderLayout.NORTH);
				jFrame.validate();
				// analyseOperation(ConstantsInfo.FuturesMarket);
				return;
			case 23:// ������������
				exportOperationExcelFile(ConstantsInfo.StockMarket);
				return;
			case 37:// ������������
				exportOperationExcelFile(ConstantsInfo.FuturesMarket);
				return;
			case 24:// ������������
				exportPointExcelFile(ConstantsInfo.StockMarket);
				return;
			case 38:// ������������
				exportPointExcelFile(ConstantsInfo.FuturesMarket);
				return;
			case 25:// ������������
				exportTotalOperationExcelFile(ConstantsInfo.StockMarket);
				return;
			case 39:// ������������
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

			// ���Ӳ���pane
			operationPane.add(queryButton);
			jFrame.add(operationPane, BorderLayout.NORTH);
			// ���ӱ�pane
			stockTable.addMouseListener(new JtableMouseListenter());
			stockTable.setBorder(BorderFactory.createEtchedBorder());
			stockTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			stockTable.setSelectionForeground(Color.blue);
			stockTable.setShowVerticalLines(false);//
			// �����Ƿ���ʾ��Ԫ���ķָ���
			stockTable.setShowHorizontalLines(false);
			stockSP = new JScrollPane(stockTable);

			jFrame.add(stockSP, BorderLayout.CENTER);
			jFrame.validate();
		}

	}

	/**
	 * �������¼�
	 * @author zhouqiao
	 *
	 */
	class JtableMouseListenter implements MouseListener {
		
		@Override
		public void mouseClicked(MouseEvent e) {

			if (e.getClickCount() == 2) // ʵ��˫��
			{
				String paneNmae = operationPane.getName();
				// �����λ��
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
					JLabel jlCode = new JLabel("����");
					JLabel jlName = new JLabel("����");
					final JTextField jtfCode = new JTextField(6);
					final JTextField jtfName = new JTextField(24);
					JButton cancelButton = new JButton("ȡ��");
					JButton confirmButton = new JButton("ȷ��");

					final JFrame jfDialog = new JframeDialog();
					// JFrame jfDialog = new JFrame();
					JPanel dialogJP = new JPanel();

					dialogJP.add(jlCode);
					dialogJP.add(jtfCode);
					dialogJP.add(jlName);
					dialogJP.add(jtfName);
					dialogJP.add(cancelButton);
					dialogJP.add(confirmButton);
					// ȡ��
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
					int selectedRow = stockTable.getSelectedRow();// ���ѡ���е�����
					System.out.println("del selectedRow:" + selectedRow);
					if (selectedRow != -1) // ����ѡ����
					{
						stMarketTabMod.removeRow(selectedRow); // ɾ����
					}

					System.out.println("del market");
				} else if (paneNmae.equals("stock")) {
					int selectedRow = stockTable.getSelectedRow();// ���ѡ���е�����
					System.out.println("del selectedRow:" + selectedRow);
					if (selectedRow != -1) // ����ѡ����
					{
						System.out.println("del 1111selectedRow:" + selectedRow);
						stSingleTabMod.removeRow(selectedRow); // ɾ����

						StockSingle ss = listStockSingle.get(selectedRow);
						System.out.println("del stock:" + ss.getStockFullId() + ":" + ss.getStockName());
						// ɾ��
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
			this.setTitle("������");
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
			 // ����һ����ҵ
			ret = stockBasicLoader.readIndustry();
			desc = "���ݵ�����������ļ�1Industry";
			break;
		case 10:
			// ����һ����ҵ��Ӧ����
			ret = stockBasicLoader.readFirstIndustry_To_Concept();
			desc = "���ݵ�����������ļ�2FirstIndustry-to-Concept.xlsx";
			break;
		case 11:
			// ����������ҵ��Ӧ��Ʊ
			ret = stockBasicLoader.readThirdIndustry_to_stock();
			desc = "���ݵ�����������ļ�3ThirdIndustry-to-stock.xlsx";
			break;
		case 12:
			// ��������Ӧ��Ʊ
			ret = stockBasicLoader.readConcept_to_stock();
			desc = "���ݵ�����������ļ�4Concept-to-stock.xlsx";
			break;
		case 13:
			// ��������
			ret = stockBasicLoader.readTwoRong();
			desc = "���ݵ�����������ļ�5TwoRong.xlsx";
			break;
		case 14:
			// ���������
			ret = stockBasicLoader.readstock_baseExpect();
			desc = "���ݵ�����������ļ�5TwoRong.xlsx";
			break;
		case 15: 
			// ���������
			ret = stockBasicLoader.readMarketInfo();
			ret = stockBasicLoader.readFuturesInfo();
			desc = "���ݵ�����������ļ�0Market_BaseInfo.xlsx��7ExMarket_BaseInfo.xlsx";
			break;
		case 16:
			// ���������
			ret = stockBasicLoader.readStockToFeatures();
			desc = "���ݵ�����������ļ�8ExMarket-to-stock.xlsx";
			break;
		case 17:
			// �����Ϣ��
			ret = stockBasicLoader.readYearInfo();
			desc = "���ݵ�����������ļ�9 10 11 12�ĸ��ļ�";
			break;
		default:
			break;
		}

		if (ret != 0) {
			JOptionPane.showMessageDialog(jFrame, desc, "��ʾ", JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(jFrame, "���ݵ���ɹ�", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
		}
		return;
	}


	/**
	 * 1���뽻������
	 * @param type
	 */
	public void loadStockData(int type) {
		Date start = new Date();
		StockDataLoader fr = new StockDataLoader(sbDao, sdDao, spDao, ssDao);
		int ret = 0;
		stockLogger.logger.fatal("******load stock data start*****");
		// ���� ���������� ������ma5 �Ƿ�
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
			JOptionPane.showMessageDialog(jFrame, "��1�����ݵ���ɹ�,��ʱ" + minute + "����", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(jFrame, "��1�������Ѿ����룬�����ظ�", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
		}

		return;
	}

	/**
	 * 2������Ʊ��������
	 * @param type
	 * @param startdate
	 * @param enddate
	 */
	public void analyseStockData(int type, String startdate, String enddate) {

		PointAnalysis pc = new PointAnalysis(sbDao, sdDao, spDao);
		stockLogger.logger.fatal("******analyse point start*****");
		Date start = new Date();

		// ���㵱������
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

		JOptionPane.showMessageDialog(jFrame, "��2����������ɹ�,��ʱ" + minute + "����", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
		return;
	}

	private static DatePicker getDatePicker() {
		DatePicker datepick;
		// ��ʽ
		String defaultFormat = "yyyy-MM-dd hh:mm:ss";
		// ��ǰʱ��
		Date date = new Date();
		// ����
		Font font = new Font("Times New Roman", Font.BOLD, 14);
		Dimension dimension = new Dimension(180, 30);
		datepick = new DatePicker(date, defaultFormat, font, dimension);
		datepick.setLocation(130, 90);// ������ʼλ��
		datepick.setLocale(Locale.CHINA);
		// ����ʱ�����ɼ�
		datepick.setTimePanleVisible(true);
		return datepick;
	}

	/**
	 * 3������������
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
		JOptionPane.showMessageDialog(jFrame, "��3�������ɹ�,��ʱ" + minute + "����", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
		return;
	}

	/**
	 *  4����ͳ�Ʊ�
	 * @param type
	 */
	public void loadSummaryExcelFile(int type) {
		LoadSummaryMain seRead = new LoadSummaryMain(sbDao, sdDao, spDao, ssDao);
		try {
			seRead.readStockAnsyleExcelData(type);
		} catch (Exception e) {
			stockLogger.logger.fatal(e.toString());
		}	
		JOptionPane.showMessageDialog(jFrame, "��4������ɹ�", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
		return;
	}

	/**
	 * 5������������
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

		JOptionPane.showMessageDialog(jFrame, "��5�������ɹ�", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
		return;
	}


	/**
	 * 6����ͳ������
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

		JOptionPane.showMessageDialog(jFrame, "��6�������ɹ�", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
		return;
	}

	/**
	 * 7������������
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

		JOptionPane.showMessageDialog(jFrame, "��7�����������ɹ�,��ʱ" + minute + "����", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
		return;
	}

	public void errorDialog() {		
		JOptionPane.showMessageDialog(jFrame, "����ʧ��,������־", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
		return;
	}

	
	/**
	 * 8����������������
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
	
		JOptionPane.showMessageDialog(jFrame, "��8�������ɹ�", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
		return;
	}

	/**
	 *  9�����ܵĲ�������
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
		JOptionPane.showMessageDialog(jFrame, "��9���ܲ��������ɹ�,��ʱ" + minute + "����", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
		return;
	}

	/**
	 * ���ӹ�Ʊ
	 * 
	 */
	public void addSingleStock() {
		String listItem[] = { "��", "��" };
		JLabel jlCode = new JLabel("��Ʊ����");
		JLabel jlName = new JLabel("����");
		JLabel jlIndustry = new JLabel("��ҵ");
		JLabel jlConcept = new JLabel("����");

		JLabel jlMarginTrading = new JLabel("������ȯ");
		final JTextField jtfCode = new JTextField(6);
		final JTextField jtfName = new JTextField(12);
		final JTextField jtfieldIndu = new JTextField(24); 
		final JTextField jtfieldConc = new JTextField(48); 
		// ����
		List<String> allConceptNameList = null;
		try {
			allConceptNameList = sbDao.getAllConceptName();
		} catch (SQLException e3) {

			e3.printStackTrace();
		}
		final String[] conceptName = (String[]) allConceptNameList.toArray(new String[allConceptNameList.size()]);
		final JList jlistConcept = new JList(conceptName);

		// ��ҵ
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

		// ������ȯ
		final JList jlistMarginTrading = new JList(listItem);
		jlistMarginTrading.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JButton cancelButton = new JButton("ȡ��");
		JButton confirmButton = new JButton("ȷ��");
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

		// ��ҵѡ���
		jlistIndustry.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				jtfieldIndu.setText(((JList) arg0.getSource()).getSelectedValue().toString());
				System.out.println(jlistIndustry.getSelectedValue().toString());
			}
		});

		// ����ѡ���
		jlistConcept.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {				
				jtfieldConc.setText(((JList) arg0.getSource()).getSelectedValue().toString());
			}
		});

		// ȡ��
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
				// ����Ʊ���볤��
				if (jtfCode.getText().length() != 6) {
					jop = new JOptionPane();
					
					jfDialog.add(jop, BorderLayout.NORTH);
					JOptionPane.showMessageDialog(jFrame, "��Ʊ���ų����д���,������6λ��ȷ�Ĵ���", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
					return;
				}

				if (jtfCode.getText().startsWith("6")) {
					stockCode = "SH" + jtfCode.getText();
				} else {
					stockCode = "SZ" + jtfCode.getText();
				}

				// ����Ʊ����
				try {
					sStock = sbDao.lookUpStockSingle(stockCode);
				} catch (Exception e2) {
					e2.printStackTrace();
				} 
				
				// �Ѿ�����
				if (sStock != null) {					
					JOptionPane.showMessageDialog(jFrame, "�ù�Ʊ��Ϣ�Ѿ�����", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
					return;
				}

				// �����ҵ
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
					JOptionPane.showMessageDialog(jFrame, "��ҵ���д���,��������ȷ����ҵ", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
					return;
				}

				// ������
				StockConcept sconcept = null;
				try {
					sconcept = sbDao.lookUpStockConcept(jtfieldConc.getText());
				} catch (Exception e) {
					e.printStackTrace();
				} 

				if (sconcept == null) {				
					JOptionPane.showMessageDialog(jFrame, "�������,��������ȷ�ĸ���", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
					return;
				}

				// �����¹�Ʊ
				sStock = new StockSingle(1, stockCode, jtfName.getText(), sindustry.getThirdcode(),
						jtfieldIndu.getText(), sindustry.getSecondcode(), sindustry.getSecondname(),
						sindustry.getFirstcode(), sindustry.getFirstname(), jtfieldConc.getText(),
						jlistMarginTrading.getSelectedIndex());
				try {
					// ����stock_to_industry
					sbDao.insertStockSingleToIndustry(sStock);
					// ����allinfo
					sbDao.insertStockSingle(sStock);
					// ����stock_to_concept
					// sbDao.insertStockToConcept(stockCode,jtfName.getText(),sconcept.getCode(),sconcept.getName());
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}		
				
				JOptionPane.showMessageDialog(jFrame, "���ݸ��³ɹ�", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
				jfDialog.dispose();// �ر�
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
