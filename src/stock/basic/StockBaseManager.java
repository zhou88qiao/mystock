package stock.basic;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.PropertyConfigurator;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import com.eltima.components.ui.DatePicker;
import com.point.stock.PointClass;
import com.timer.stock.StockDateTimer;
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

import excel.all_v2.StockExcelOperationMain;
import excel.all_v2.StockExcelPartitionMain;
import excel.all_v2.StockExcelReadMain;
import excel.rw.ExcelReader;
import excel.simple.StockRecentWriter;
import file.FileReader;

import stockGUI.StockTimeSeriesChart;
import stockGUI.stocktable.StockConceptTableModel;
import stockGUI.stocktable.StockIndustryTableModel;
import stockGUI.stocktable.StockMarketTableModel;
import stockGUI.stocktable.StockRegionalTableModel;
import stockGUI.stocktable.StockSingleTableModel;




public class StockBaseManager {
	private final static int  addIndex=0;
	private final static int  editIndex=1;
	private final static int  delIndex=2;
	private final static int  queryIndex=3;	
	private final static int  checkIndex=4;	
	
	static StockBaseManager stockBM;
	private StockDataDao sdDao;
	private StockPointDao spDao;
	private StockBaseDao sbDao;
	private StockSummaryDao ssDao;
	//static StockInformationDao siDao =new StockInformationDao();
	static PointClass pClass=new PointClass();
	 private static TrayIcon trayIcon = null;
	 static SystemTray tray = SystemTray.getSystemTray();
	
	private static JFrame jFrame=new JFrame("��Ʊ������Ϣ");
	String[] baseItems = new String[] { "�г�", "����", "��ҵ", "����","����"};
	String[] operButtons = new String[] { "�½�", "�༭", "ɾ��","��ѯ"}; //0 ,1,2,3
	char[] baseShortcuts = { 'M','R','I','C','S'};
    char[] editShortcuts = { 'Z','X','C','V' };
    Color[] colors = { Color.RED, Color.YELLOW, Color.BLUE, Color.GREEN, Color.DARK_GRAY};
	private JMenuBar mb=new JMenuBar();
	JMenu stockBase=new JMenu("��Ʊ��Ϣ");	
	JMenu stockFunction=new JMenu("��Ʊ���Ĺ���");
	JMenu futuresFunction=new JMenu("�ڻ����Ĺ���");
	JMenu stockOperate=new JMenu("��������");	
	JMenu stockMessage=new JMenu("��Ѷ");
	
	JMenuItem GetStockData=new JMenuItem("1 ���뽻������");
	JMenuItem ExtremeAnalyze=new JMenuItem("2 �������ݷ���");
	JMenuItem LoadPointExcel=new JMenuItem("3 ������������all");
	JMenuItem LoadAnalyExcel=new JMenuItem("4 �����������");
	JMenuItem LoadPointOpExcel=new JMenuItem("5 ������������point");
	JMenuItem LoadSummaryExcel=new JMenuItem("6 ����ͳ������summary");
	JMenuItem LoadOperationAnalyse=new JMenuItem("7 ������������");
	JMenuItem LoadOperationExcel=new JMenuItem("8 ����������������operation");
	JMenuItem LoadTotalOperationExcel=new JMenuItem("9 �����ܲ�������totaloperation");
	

	JMenuItem GetFuturesData=new JMenuItem("1 �����ڻ���Ʒ����");
	JMenuItem FuturesExtremeAnalyze=new JMenuItem("2 �������ݷ���");
	JMenuItem FuturesLoadPointExcel=new JMenuItem("3 ������������");
	JMenuItem FuturesLoadAnalyExcel=new JMenuItem("4 �����������");
	JMenuItem FuturesLoadPointOpExcel=new JMenuItem("5 ������������");	
	JMenuItem FuturesLoadSummaryExcel=new JMenuItem("6 ����ͳ������");
	JMenuItem FuturesLoadOperationAnalyse=new JMenuItem("7 ������������");
	JMenuItem FuturesLoadOperationExcel=new JMenuItem("8 ����������������");
	JMenuItem FuturesLoadTotalOperationExcel=new JMenuItem("9 �����ܲ�������totaloperation");
	
	JMenuItem LoadMarket=new JMenuItem("0 �����Ʊ���ڻ���Ʒ�г�");
	JMenuItem LoadIndustry=new JMenuItem("1 ����һ��������ҵ");
	JMenuItem LoadFirstIndustryConcept=new JMenuItem("2 ����һ����ҵ��Ӧ����");
	JMenuItem LoadThirdIndustrytoStock=new JMenuItem("3 ����������ҵ��Ӧ��������");
	JMenuItem LoadConcepttoStock=new JMenuItem("4 ��������Ӧ��������");
	JMenuItem LoadStockTwoRong=new JMenuItem("5 �������������ȯ");
	JMenuItem LoadStockBaseface=new JMenuItem("6 ������ɻ�����");
	JMenuItem LoadStockToFuturesBaseface=new JMenuItem("7 �����ڻ���Ʒ��Ӧ��������");
	JMenuItem LoadStockBaseYearface=new JMenuItem("8 �����Ʊ��ݽ�����Ϣ");
	
	DatePicker datepickStart = getDatePicker();
    DatePicker datepickEnd = getDatePicker();

	JButton addButton=new JButton("�½�");	//0
	JButton editButton=new JButton("�༭");//1
	JButton deleteButton=new JButton("ɾ��");//2
	JButton queryButton=new JButton("��ѯ");//3
	JButton checkButton=new JButton("ȷ��");//4
	
	JPanel Panel=new JPanel();
	JLabel jlMarket=new JLabel("�г�");
	JLabel jlConcept=new JLabel("����");
	JLabel jlProvince=new JLabel("ʡ����");
	JLabel jlCity=new JLabel("������");
	JLabel jlFirstIndustry=new JLabel("һ����ҵ����");
	JLabel jlSecondIndustry=new JLabel("������ҵ����");
	JLabel jlThirdIndustry=new JLabel("������ҵ����");
	JLabel jlStockFullId=new JLabel("��Ʊ����");
	JLabel jlStockName=new JLabel("��Ʊ����");
	JLabel jlStockIndustry=new JLabel("������ҵ");
	JLabel jlStockConcept=new JLabel("����");
	JTextField jtfMarket=new JTextField(10);//
	JTextField jtfConcept=new JTextField(10);
	JLabel tishiLabel = new JLabel("");
	
	JTextField jtfFirstIndustry=new JTextField(10);
	JTextField jtfSecondIndustry=new JTextField(10);
	JTextField jtfThirdIndustry=new JTextField(10);
	
	JTextField jtfProvince=new JTextField(10);
	JTextField jtfCity=new JTextField(10);
	JTextField jtfArea=new JTextField(10);
	
	JTextField jtfFullId=new JTextField(10);
	JTextField jtfStockName=new JTextField(10);
	JTextField jtfStockIndustry=new JTextField(10);
	JTextField jtfStockConcept=new JTextField(10);
	
	menuActionListenter actionL=new menuActionListenter();
	buttonMouseListenter actionM=new buttonMouseListenter();
	
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
	
	Task task;
	List<StockMarket> listStockMarket = new ArrayList<StockMarket>(); 
	List<StockConcept> listStockConcept = new ArrayList<StockConcept>(); 
	List<StockIndustry> listStockIndustry = new ArrayList<StockIndustry>(); 
	List<StockRegional> listStockRegional = new ArrayList<StockRegional>(); 
	List<StockSingle> listStockSingle = new ArrayList<StockSingle>(); 
	
	FileReader fr;//��������
	
	public StockBaseManager(Connection stockBaseConn,Connection stockDataConn,Connection stockPointConn,Connection stockSummaryConn)
	{
		sbDao = new StockBaseDao(stockBaseConn);
		sdDao = new StockDataDao(stockDataConn);
		spDao = new StockPointDao(stockPointConn);
		ssDao = new StockSummaryDao(stockSummaryConn);
	}
	
	public void init()
	{
		int i=0;
		 for (i=0; i < baseItems.length; i++) {
         //    JMenuItem item = new JMenuItem(baseItems[i], baseShortcuts[i]);
			 JMenuItem item = new JMenuItem(baseItems[i]);
             item.setAccelerator(KeyStroke.getKeyStroke(baseShortcuts[i],
                    Toolkit.getDefaultToolkit( ).getMenuShortcutKeyMask( ), false));
             item.setActionCommand("" + i); //����i �¼���������ȡ
             item.addActionListener(actionL);
             stockBase.add(item);
         }
	
		stockOperate.addActionListener(actionL);
		
		addButton.addMouseListener(actionM);
		deleteButton.addMouseListener(actionM);
		editButton.addMouseListener(actionM);
		queryButton.addMouseListener(actionM);
		checkButton.addMouseListener(actionM);
		
		addButton.setActionCommand(""+addIndex);
		deleteButton.setActionCommand(""+delIndex);
		editButton.setActionCommand(""+editIndex);
		queryButton.setActionCommand("" + queryIndex);
		checkButton.setActionCommand("" + checkIndex);
	
		stockOperate.add(LoadMarket);
		stockOperate.add(LoadIndustry);
		stockOperate.add(LoadFirstIndustryConcept);
		stockOperate.add(LoadThirdIndustrytoStock);
		stockOperate.add(LoadConcepttoStock);
		stockOperate.add(LoadStockTwoRong);
		stockOperate.add(LoadStockBaseface);
		stockOperate.add(LoadStockToFuturesBaseface);
		stockOperate.add(LoadStockBaseYearface);
		
		
		GetStockData.setActionCommand("" + 6); //����i �¼���������ȡ
		ExtremeAnalyze.setActionCommand("" + 7); //����i �¼���������ȡ		
		LoadPointExcel.setActionCommand("" + 8); //����i �¼���������ȡ		
		LoadAnalyExcel.setActionCommand(""+20); //�������ݿ�
		LoadSummaryExcel.setActionCommand(""+21); //����ͳ�Ʊ�
		LoadOperationAnalyse.setActionCommand(""+22); //��������
		LoadOperationExcel.setActionCommand(""+23); //��������������
		LoadPointOpExcel.setActionCommand(""+24);//������������
		LoadTotalOperationExcel.setActionCommand(""+25); //�����ܲ���������
	
		
		GetFuturesData.setActionCommand("" + 31); //����i �¼���������ȡ
		FuturesExtremeAnalyze.setActionCommand("" + 32); //����i �¼���������ȡ		
		FuturesLoadPointExcel.setActionCommand("" + 33); //����i �¼���������ȡ		
		FuturesLoadAnalyExcel.setActionCommand(""+34); //�������ݿ�
		FuturesLoadSummaryExcel.setActionCommand(""+35); //����ͳ�Ʊ�
		FuturesLoadOperationAnalyse.setActionCommand(""+36); //��������
		FuturesLoadOperationExcel.setActionCommand(""+37); //��������������
		FuturesLoadPointOpExcel.setActionCommand(""+38);//������������
		FuturesLoadTotalOperationExcel.setActionCommand(""+39); //�����ܲ���������
			
		
		LoadIndustry.setActionCommand(""+9); //������ҵ
		LoadFirstIndustryConcept.setActionCommand(""+10); // ����һ����ҵ��Ӧ�ĸ���
		LoadThirdIndustrytoStock.setActionCommand(""+11); // ����������ҵ��Ӧ�Ĺ�Ʊ
		LoadConcepttoStock.setActionCommand(""+12); // �����Ӧ��Ʊ
		LoadStockTwoRong.setActionCommand(""+13); // ����
		LoadStockBaseface.setActionCommand(""+14); //������
		LoadMarket.setActionCommand(""+15);//���� �г�
		LoadStockToFuturesBaseface.setActionCommand(""+16);//�����ڻ���Ӧ����Ʊ
		LoadStockBaseYearface.setActionCommand(""+17);//���������Ϣ
		
		GetStockData.addActionListener(actionL);
		ExtremeAnalyze.addActionListener(actionL);
		LoadPointExcel.addActionListener(actionL);
		LoadSummaryExcel.addActionListener(actionL);
		LoadAnalyExcel.addActionListener(actionL);
		LoadPointOpExcel.addActionListener(actionL);
		LoadOperationExcel.addActionListener(actionL);
		LoadOperationAnalyse.addActionListener(actionL);
		LoadTotalOperationExcel.addActionListener(actionL);
				
		GetFuturesData.addActionListener(actionL);
		FuturesExtremeAnalyze.addActionListener(actionL);
		FuturesLoadPointExcel.addActionListener(actionL);
		FuturesLoadSummaryExcel.addActionListener(actionL);
		FuturesLoadPointOpExcel.addActionListener(actionL);
		FuturesLoadAnalyExcel.addActionListener(actionL);
		FuturesLoadOperationExcel.addActionListener(actionL);
		FuturesLoadOperationAnalyse.addActionListener(actionL);
		FuturesLoadTotalOperationExcel.addActionListener(actionL);
		
		LoadMarket.addActionListener(actionL);
		LoadIndustry.addActionListener(actionL);
		LoadFirstIndustryConcept.addActionListener(actionL);
		LoadThirdIndustrytoStock.addActionListener(actionL);
		LoadConcepttoStock.addActionListener(actionL);
		LoadStockTwoRong.addActionListener(actionL);
		LoadStockBaseface.addActionListener(actionL);
		LoadStockToFuturesBaseface.addActionListener(actionL);
		LoadStockBaseYearface.addActionListener(actionL);

		stockFunction.add(GetStockData);
		stockFunction.add(ExtremeAnalyze);		
		stockFunction.add(LoadPointExcel);
		stockFunction.add(LoadAnalyExcel);
		stockFunction.add(LoadPointOpExcel);
		stockFunction.add(LoadSummaryExcel);
		stockFunction.add(LoadOperationAnalyse);
		stockFunction.add(LoadOperationExcel);	
		stockFunction.add(LoadTotalOperationExcel);
		
		futuresFunction.add(GetFuturesData);
		futuresFunction.add(FuturesExtremeAnalyze);		
		futuresFunction.add(FuturesLoadPointExcel);
		futuresFunction.add(FuturesLoadAnalyExcel);
		futuresFunction.add(FuturesLoadPointOpExcel);
		futuresFunction.add(FuturesLoadSummaryExcel);	
		futuresFunction.add(FuturesLoadOperationAnalyse);
		futuresFunction.add(FuturesLoadOperationExcel);	
		futuresFunction.add(FuturesLoadTotalOperationExcel);	
		

		mb.add(stockBase);		
		mb.add(stockFunction);
		mb.add(futuresFunction);
		mb.add(stockOperate);		
		mb.add(stockMessage);
		
	//	Panel.add(mb);		
	//	jFrame.addMouseListener(mouseL);
		
		
		jFrame.setJMenuBar(mb);
		jFrame.pack();
		jFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        
		jFrame.setVisible(true);
		
		jFrame.addWindowListener(new WindowAdapter() { // ���ڹر��¼�
		     public void windowClosing(WindowEvent e) {
		      System.exit(0);
		     };
		     /*
		     public void windowIconified(WindowEvent e) { // ������С���¼�

		      jFrame.setVisible(false);
		      stockBM.miniTray();

		     }*/

		    });
	}
	
	class menuActionListenter implements ActionListener
	{
		int ret=0;
		ExcelReader excelReader=null;
		public void actionPerformed(ActionEvent e) {
			
			if(stockSP!=null)
			{
				jFrame.remove(stockSP);
			}	
			
			if(operationPane!=null)
			{
				jFrame.remove(operationPane);
			}
			
			if(jop!=null)
			{
				jFrame.remove(jop);
			}
			
			if(timerPane!=null){
				jFrame.remove(timerPane);
			}
			
			timerPane = new JPanel();
			timerPane.add(tishiLabel);
			timerPane.add(datepickStart);
			timerPane.add(datepickEnd);
			timerPane.add(checkButton);
		
			//���ð�ť
			operationPane=new JPanel();
			operationPane.add(addButton);
			operationPane.add(deleteButton);
			operationPane.add(editButton);
		
			int index=Integer.parseInt(e.getActionCommand());
		//	System.out.println("�˵�:"+index);
			switch(index)
			{
			case 0: //�г�
				try {
						listStockMarket=sbDao.getStockMarket(ConstantsInfo.StockMarket);
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
				
				stMarketTabMod=new StockMarketTableModel(listStockMarket);
				stockTable=new JTable(stMarketTabMod);	
				
				operationPane.setName("market");
				operationPane.add(jlMarket);	
				operationPane.add(jtfMarket);
		
				break;
			case 1: //����
				
				try {
						listStockRegional=sbDao.getStockRegional();
					} catch (IOException e3) {
						// TODO Auto-generated catch block
						e3.printStackTrace();
					} catch (ClassNotFoundException e3) {
						// TODO Auto-generated catch block
						e3.printStackTrace();
					} catch (SQLException e3) {
						// TODO Auto-generated catch block
						e3.printStackTrace();
					}
				stRegionalTabMod=new StockRegionalTableModel(listStockRegional);
				stockTable=new JTable(stRegionalTabMod);	
				
				
				operationPane.setName("regional");
				operationPane.add(jlProvince);	
				operationPane.add(jtfProvince);
				operationPane.add(jlCity);			
				operationPane.add(jtfCity);
				break;
			case 2: //��ҵ
			
				try {
						listStockIndustry=sbDao.getStockIndustry();
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					} catch (ClassNotFoundException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					} catch (SQLException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					
				stIndustyTabMod=new StockIndustryTableModel(listStockIndustry);
				stockTable=new JTable(stIndustyTabMod);	
				
				operationPane.setName("industry");
				operationPane.add(jlFirstIndustry);	
				operationPane.add(jtfFirstIndustry);
				operationPane.add(jlSecondIndustry);	
				operationPane.add(jtfSecondIndustry);
				operationPane.add(jlThirdIndustry);	
				operationPane.add(jtfThirdIndustry);
				break;
			case 3: //����
				try {
						listStockConcept=sbDao.getStockConcept();
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
			
				stConceptTabMod=new StockConceptTableModel(listStockConcept);
				stockTable=new JTable(stConceptTabMod);	
				
				operationPane.setName("concept");
				operationPane.add(jlConcept);	
				operationPane.add(jtfConcept);				
				break;
			case 4: //��Ʊ
				try {
						listStockSingle=sbDao.getStockSingle();
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
				
				stSingleTabMod=new StockSingleTableModel(listStockSingle);
				stockTable=new JTable(stSingleTabMod);	
				
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
			case 6://���뽻������	
				loadStockData(ConstantsInfo.StockMarket);				
				return;
			case 31:				
				loadStockData(ConstantsInfo.FuturesMarket);
				return;
			case 7://�������켫������
				tishiLabel.setText("��2����");
				timerPane.setName("stock_analy_point");
				jFrame.add(timerPane,BorderLayout.NORTH);
				jFrame.validate();
				//analyseStockData(ConstantsInfo.StockMarket); 
				return;
			case 32://�������켫������
				tishiLabel.setText("��2����");
				timerPane.setName("future_analy_point");
				jFrame.add(timerPane,BorderLayout.NORTH);
				jFrame.validate();
				//analyseStockData(ConstantsInfo.FuturesMarket); 
				return;
			case 8: //��������
				tishiLabel.setText("��3����");
				timerPane.setName("stock_analy_summary");
				jFrame.add(timerPane,BorderLayout.NORTH);
				jFrame.validate();
				//exportExcelFile(ConstantsInfo.StockMarket); 
				return;
			case 33: //��������
				tishiLabel.setText("��3����");
				timerPane.setName("future_analy_summary");
				jFrame.add(timerPane,BorderLayout.NORTH);
				jFrame.validate();
				//exportExcelFile(ConstantsInfo.FuturesMarket); 
				return;			
			case 20:			
				loadSummaryExcelFile(ConstantsInfo.StockMarket);
				return;
			case 34:
				loadSummaryExcelFile(ConstantsInfo.FuturesMarket);
				return;
			case 21://����ͳ������
				exportSummaryExcelFile(ConstantsInfo.StockMarket);
				return;
			case 35:
				exportSummaryExcelFile(ConstantsInfo.FuturesMarket);
				return;
				
			case 22://������������
				tishiLabel.setText("��7����");				
				timerPane.setName("stock_analy_operation");
				jFrame.add(timerPane,BorderLayout.NORTH);
				jFrame.validate();
				//analyseOperation(ConstantsInfo.StockMarket);
				return;
			case 36://������������
				tishiLabel.setText("��7����");	
				timerPane.setName("stock_analy_operation");
				jFrame.add(timerPane,BorderLayout.NORTH);
				jFrame.validate();
				//analyseOperation(ConstantsInfo.FuturesMarket);
				return;
			case 23://������������
				exportOperationExcelFile(ConstantsInfo.StockMarket);
				return;	
			case 37://������������
				exportOperationExcelFile(ConstantsInfo.FuturesMarket);
				return;	
				
			case 24://������������
				exportPointExcelFile(ConstantsInfo.StockMarket);
				return;	
			case 38://������������
				exportPointExcelFile(ConstantsInfo.FuturesMarket);
				return;	
			
			case 25://������������
				exportTotalOperationExcelFile(ConstantsInfo.StockMarket);
				return;	
			case 39://������������
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
				System.out.println(index);
				try {
						loadData(index);
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
				return;
			default:
				break;
			}
			
			//���Ӳ���pane
			operationPane.add(queryButton);			
			jFrame.add(operationPane,BorderLayout.NORTH);
			
			//���ӱ�pane			
			stockTable.addMouseListener(new JtableMouseListenter());
			
		//	stockTable.setRowSelectionAllowed(false);
		//	stockTable.setCellSelectionEnabled(false);
		//	stockTable.setColumnSelectionAllowed(false);
			stockTable.setBorder(BorderFactory.createEtchedBorder());  
			stockTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			stockTable.setSelectionForeground(Color.blue);
			stockTable.setShowVerticalLines(false);//  
		     // �����Ƿ���ʾ��Ԫ���ķָ���
			stockTable.setShowHorizontalLines(false);
			stockSP=new JScrollPane(stockTable);
			
			jFrame.add(stockSP,BorderLayout.CENTER);
			jFrame.validate();						
		}
		
	}
	
	
	//�������¼�
	class JtableMouseListenter implements MouseListener
	{

		public void mouseClicked(MouseEvent e) {				
			
		  if(e.getClickCount() == 2) //ʵ��˫�� 
          { 
			  	String paneNmae=operationPane.getName();
                int row =((JTable)e.getSource()).rowAtPoint(e.getPoint()); //�����λ�� 
               // int col=((JTable)e.getSource()).columnAtPoint(e.getPoint()); //�����λ�� 
                if(paneNmae.equals("concept"))
                {	
                	System.out.println("name:"+listStockConcept.get(row).getName());
                	
                }
            	else if(paneNmae.equals("industry"))
            	{
					System.out.println("operate industry");
            	}
				else if(paneNmae.equals("regional"))
					System.out.println("operate regional");
				else if(paneNmae.equals("market"))
				{
					System.out.println("operate market");
				}
				else if(paneNmae.equals("stock"))
				{
					System.out.println("operate stock");
					String fullId=listStockSingle.get(row).getStockFullId();		
					JFreeChart timeSeriesChart = null;
					StockTimeSeriesChart stsc=new StockTimeSeriesChart(sbDao,spDao);
					
					try {
						timeSeriesChart=stsc.createTimeSeriesChart(fullId);
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
					ChartFrame cframe = new ChartFrame("StockChart", timeSeriesChart);
					 
					cframe.pack();
			        cframe.setVisible(true);						
								
				}

           }
			
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			
		}

	
		public void mouseExited(MouseEvent arg0) {
			
	
		}


		public void mousePressed(MouseEvent arg0) {
			
		}


		public void mouseReleased(MouseEvent arg0) {
			
	
		}
	
	}
	

	//��ť�¼�
	class buttonMouseListenter implements MouseListener
	{
		public void mousePressed(MouseEvent e) {
			//System.out.println("mouse:"+e.getSource());			
			
		}
		
		public void mouseEntered(MouseEvent e) {
			
			
		}

		
		public void mouseExited(MouseEvent e) {
			
			
		}

		
		public void mouseClicked(MouseEvent e) {
		
			
			String paneNmae= operationPane.getName();
			String timeNmae= timerPane.getName();
			//System.out.println("pane name:"+operationPane.getName());
			
			if(jop!=null)
			{
				jFrame.remove(jop);
			}
			
			if(e.getSource()== addButton){
				
				
				addButton.setBackground(Color.BLUE);
				
				if(paneNmae.equals("concept"))
					System.out.println("operate concept");
            	else if(paneNmae.equals("industry"))
            	{
					System.out.println("operate industry");
            	}
				else if(paneNmae.equals("regional"))
					System.out.println("operate regional");
				else if(paneNmae.equals("market"))
				{
					JLabel jlCode=new JLabel("����");
					JLabel jlName=new JLabel("����");
					final JTextField jtfCode=new JTextField(6);
					final JTextField jtfName=new JTextField(24);
					JButton cancelButton=new JButton("ȡ��");	
					JButton confirmButton=new JButton("ȷ��");	
					
					final JFrame jfDialog = new JFrameDialog();  
					//JFrame jfDialog = new JFrame(); 
					JPanel dialogJP=new JPanel();
					
					dialogJP.add(jlCode);
					dialogJP.add(jtfCode);
					dialogJP.add(jlName);
					dialogJP.add(jtfName);
					dialogJP.add(cancelButton);	
					dialogJP.add(confirmButton);	
					//ȡ��
					cancelButton.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent event)
						{
							jfDialog.dispose();
						}
					});
					confirmButton.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent event)
						{
							 
							StockMarket sMarket=new StockMarket(1,jtfCode.getText(),jtfName.getText(),"");
							try {
								sbDao.insertStockMarket(sMarket);
							} catch (IOException e) {
								e.printStackTrace();
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							} catch (SQLException e) {
								e.printStackTrace();
							}
						//	stockBase.setSelectedIndex(stockBase.getModel().getSize() -1);
							//stockBase.					
							
							jfDialog.dispose();
						}
					});
					
					jfDialog.add(dialogJP,BorderLayout.CENTER);
					jfDialog.setVisible(true);  
					System.out.println("operate market info");
				}
				else if(paneNmae.equals("stock"))
				{
					addSingleStock();
				}
				
            }
			else if(e.getSource()==editButton){
            	editButton.setBackground(Color.green);
            	if(paneNmae.equals("concept"))
					System.out.println("operate concept");
            	else if(paneNmae.equals("industry"))
					System.out.println("operate industry");
				else if(paneNmae.equals("regional"))
					System.out.println("operate regional");
				else if(paneNmae.equals("market"))
					System.out.println("operate market");
				else if(paneNmae.equals("stock")){
					System.out.println("operate stock");
				}
					
            }
			else if(e.getSource()== deleteButton){
            	deleteButton.setBackground(Color.RED);
            	if(paneNmae.equals("concept"))
					System.out.println("operate concept");
            	else if(paneNmae.equals("industry"))
					System.out.println("operate industry");
				else if(paneNmae.equals("regional"))
					System.out.println("operate regional");
				else if(paneNmae.equals("market"))
				{
					int selectedRow = stockTable.getSelectedRow();//���ѡ���е�����
					System.out.println("del selectedRow:"+selectedRow);
	                if(selectedRow!=-1)  //����ѡ����
	                {
	                	stMarketTabMod.removeRow(selectedRow);  //ɾ����
	                }
	               
					System.out.println("del market");
				}
				else if(paneNmae.equals("stock"))
				{
					int selectedRow = stockTable.getSelectedRow();//���ѡ���е�����
					System.out.println("del selectedRow:"+selectedRow);
	                if(selectedRow!=-1)  //����ѡ����
	                {
	                	System.out.println("del 1111selectedRow:"+selectedRow);
	                	stSingleTabMod.removeRow(selectedRow);  //ɾ����
	                	
	                	StockSingle ss = listStockSingle.get(selectedRow);
	                	System.out.println("del stock:"+ss.getStockFullId()+":"+ss.getStockName());
	                	//ɾ��
	                	try {
							sbDao.deleteStockSingle(ss);
						} catch (IOException ev) {
							// TODO Auto-generated catch block
							ev.printStackTrace();
						} catch (ClassNotFoundException ev) {
							// TODO Auto-generated catch block
							ev.printStackTrace();
						} catch (SQLException ev) {
							// TODO Auto-generated catch block
							ev.printStackTrace();
						}
						listStockSingle.remove(ss);
						System.out.println(listStockSingle.size());	                	
	                }						
				}
            }			
			else if(e.getSource()==queryButton){
            	queryButton.setBackground(Color.PINK);
            	if(paneNmae.equals("concept"))
					System.out.println("operate concept");
            	else if(paneNmae.equals("industry"))
					System.out.println("operate industry");
				else if(paneNmae.equals("regional"))
					System.out.println("operate regional");
				else if(paneNmae.equals("market"))
				{
					System.out.println(jtfMarket.getText());
				}
				
            } else if(e.getSource()== checkButton){
            	  		
            	String startTime = StockDateTimer.formatDate((Date)datepickStart.getValue());
        		String endTime = StockDateTimer.formatDate((Date)datepickEnd.getValue());
        	
        		if(timeNmae.equals("stock_analy_summary"))
        			exportExcelFile(ConstantsInfo.StockMarket,startTime, endTime);
        		else if (timeNmae.equals("future_analy_summary"))
        			exportExcelFile(ConstantsInfo.FuturesMarket,startTime, endTime);
        		else if(timeNmae.equals("stock_analy_point"))
        			analyseStockData(ConstantsInfo.StockMarket,startTime, endTime);
        		else if (timeNmae.equals("future_analy_point"))
        			analyseStockData(ConstantsInfo.FuturesMarket,startTime, endTime);
        		else if(timeNmae.equals("stock_analy_operation")){
        			/*
        			progressMonitor = new ProgressMonitor(progressPane,
                            "Running a Long Task",
                            "", 0, 100);
        			 progressMonitor.setProgress(0);
        			 task = new Task();
        			 task.addPropertyChangeListener(actionP);       		
        			 task.execute();*/
        			analyseOperation(ConstantsInfo.StockMarket,startTime, endTime);
        		}
        		else if (timeNmae.equals("future_analy_operation"))
        			 analyseOperation(ConstantsInfo.FuturesMarket,startTime, endTime);
            }
			
		
		}

		
		public void mouseReleased(MouseEvent e) {
			
			
		}
	
		
		
	}
	
	class JFrameDialog extends JFrame  
	{  
	    public JFrameDialog()  
	    {  
	        Jfinit();  
	    }  
	  
	    public void Jfinit()  
	    {  
	        this.setSize(300, 500);  
	        this.setTitle("������");  
	        this.setLocationRelativeTo(null);//����
//	        this.setVisible(true);  
	    }  
	}
	

	 private static void miniTray() {  //������С��������������

		  ImageIcon trayImg = new ImageIcon("image/stock.png");//����ͼ��

		  PopupMenu pop = new PopupMenu();  //���������һ��˵�
		  MenuItem show = new MenuItem("��ԭ");
		  MenuItem exit = new MenuItem("�˳�");

		  show.addActionListener(new ActionListener() {
			  public void actionPerformed(ActionEvent e) { // ���»�ԭ��
				  tray.remove(trayIcon);
				  jFrame.setVisible(true);
				//  jFrame.setExtendedState(JFrame.NORMAL);
					jFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
				  jFrame.toFront();
		   }		
		  });
		  
		  exit.addActionListener(new ActionListener() { // �����˳���
		     public void actionPerformed(ActionEvent e) {
		      tray.remove(trayIcon);
		      System.exit(0);
		     }
		    });

			  pop.add(show);
			  pop.add(exit);

			  trayIcon = new TrayIcon(trayImg.getImage(), "��Ʊϵͳ", pop);
			  trayIcon.setImageAutoSize(true);

			  trayIcon.addMouseListener(new MouseAdapter() {
			   public void mouseClicked(MouseEvent e) { // �����˫���¼�

			    if (e.getClickCount() == 2) {

			     tray.remove(trayIcon); // ��ȥ����ͼ��
			     jFrame.setVisible(true);
			   //  jFrame.setExtendedState(JFrame.NORMAL); // ��ԭ����
				jFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
			     jFrame.toFront();
			    }

			   }

			  });

			  try {
			   tray.add(trayIcon);
			  } catch (AWTException e1) {
			   // TODO Auto-generated catch block
			   e1.printStackTrace();
			  } 
		
	 }
	 
	 
	public void loadData(int loadType) throws IOException, ClassNotFoundException, SQLException{
		int ret = 0;
		ExcelReader excelReader=null;
		excelReader = new ExcelReader(sbDao);	
		String desc=null;
	
		switch(loadType){
			case 9: //����һ����ҵ	
				ret=excelReader.readIndustry();
				desc = "���ݵ�����������ļ�1Industry";
				break;
			case 10: //����һ����ҵ��Ӧ����	
				ret=excelReader.readFirstIndustry_To_Concept();
				desc = "���ݵ�����������ļ�2FirstIndustry-to-Concept.xlsx";
				break;
			case 11: //����������ҵ��Ӧ��Ʊ	
				ret=excelReader.readThirdIndustry_to_stock();
				desc="���ݵ�����������ļ�3ThirdIndustry-to-stock.xlsx";
				break;
			case 12: //��������Ӧ��Ʊ	
				ret=excelReader.readConcept_to_stock();
				desc="���ݵ�����������ļ�4Concept-to-stock.xlsx";
				break;
			case 13: //��������	
				ret=excelReader.readTwoRong();
				desc="���ݵ�����������ļ�5TwoRong.xlsx";
				break;
			case 14: //���������
				ret=excelReader.readstock_baseExpect();
				desc="���ݵ�����������ļ�5TwoRong.xlsx";
				break;
			case 15: //���������
				ret=excelReader.readMarketInfo();
				ret=excelReader.readFuturesInfo();
				desc="���ݵ�����������ļ�0Market_BaseInfo.xlsx��7ExMarket_BaseInfo.xlsx";
				break;
			case 16: //���������
				ret=excelReader.readStockToFeatures();
				desc="���ݵ�����������ļ�8ExMarket-to-stock.xlsx";
				break;
			case 17: //�����Ϣ��
				ret=excelReader.readYearInfo();
				desc="���ݵ�����������ļ�9 10 11 12�ĸ��ļ�";
				break;
			default:
				break;
		}
		
		jop=new JOptionPane();					
		jFrame.add(jop,BorderLayout.NORTH);				
		if (ret!=0){					
			jop.showMessageDialog(jFrame, desc,"��ʾ", JOptionPane.INFORMATION_MESSAGE); 
		} else {
			jop.showMessageDialog(jFrame, "���ݵ���ɹ�","��ʾ", JOptionPane.INFORMATION_MESSAGE); 
		}
		return;
		
	}
	
	
	class Task extends SwingWorker<Void, Void> {
        @Override
        public Void doInBackground() {
            Random random = new Random();
            int progress = 0;
            setProgress(0);
            try {
                Thread.sleep(1000);
                while (progress < 100 && !isCancelled()) {
                    //Sleep for up to one second.
                    Thread.sleep(random.nextInt(1000));
                    //Make random progress.
                    progress += random.nextInt(10);
                    setProgress(Math.min(progress, 100));
                }
            } catch (InterruptedException ignore) {}
            return null;
        }
 
        @Override
        public void done() {
            Toolkit.getDefaultToolkit().beep();
           // startButton.setEnabled(true);
           // progressMonitor.setProgress(0);
        }
        
    }
	
	
	//1���뽻������
	public void loadStockData(int type){
		
		Date start = new Date();
		fr=new FileReader(sbDao,sdDao,spDao,ssDao);		
		int ret = 0;
		
		stockLogger.logger.fatal("******load stock data start*****");
		//���� ���������� ������ma5 �Ƿ�
		try {
				if(type == ConstantsInfo.StockMarket)
					ret=fr.loadAllDataInfile();
				else 
					ret=fr.loadAllFuturesDataInfile();
			//	ret=fr.deleteDataInfile(); //ɾ������
			} catch (SecurityException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
				errorDialog();
				return;
			} catch (IOException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
				errorDialog();
				return;
			} catch (ClassNotFoundException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
				errorDialog();
				return;
			} catch (SQLException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
				errorDialog();
				return;
			} catch (InstantiationException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
				errorDialog();
				return;
			} catch (IllegalAccessException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
				errorDialog();
				return;
			} catch (NoSuchFieldException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
				errorDialog();
				return;
			}
			
			 Date end = new Date();
		     long minute =(end.getTime() - start.getTime())/60000;
				
		    stockLogger.logger.fatal("load stock data consume "+ minute +" minute");
		    stockLogger.logger.fatal("******load stock data end*****");	
							
			jop=new JOptionPane();					
			jFrame.add(jop,BorderLayout.NORTH);
			if (ret == 0)
				jop.showMessageDialog(jFrame, "��1�����ݵ���ɹ�,��ʱ"+minute+"����","��ʾ", JOptionPane.INFORMATION_MESSAGE); 
			else
				jop.showMessageDialog(jFrame, "��1�������Ѿ����룬�����ظ�","��ʾ", JOptionPane.INFORMATION_MESSAGE); 
			return;
	}
	
	
	
	//2������Ʊ��������
	public void analyseStockData(int type, String startdate, String enddate){
		
		PointClass pc = new PointClass(sbDao,sdDao,spDao);	
		stockLogger.logger.fatal("******analyse point start*****");
		 Date start = new Date();
		 		 
		try {
			//���㵱������
				if(type == ConstantsInfo.StockMarket){ 
					pc.getPointToTable(ConstantsInfo.StockCalCurData,ConstantsInfo.ALLMarket,ConstantsInfo.StockMarket, startdate, enddate);
				} else 
					pc.getPointToTable(ConstantsInfo.StockCalCurData,ConstantsInfo.ALLMarket,ConstantsInfo.FuturesMarket, startdate, enddate);
			} catch (SecurityException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
				errorDialog();
				return;
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
				errorDialog();
				return;
			} catch (ClassNotFoundException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
				errorDialog();
				return;
			} catch (SQLException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
				errorDialog();
				return;
			} catch (InstantiationException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
				errorDialog();
				return;
			} catch (IllegalAccessException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
				errorDialog();
				return;
			} catch (NoSuchFieldException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
				errorDialog();
				return;
			}
			
			 Date end = new Date();
		     long minute =(end.getTime() - start.getTime())/60000;
				
		    stockLogger.logger.fatal("analyse point consume "+ minute +" minute");
		    stockLogger.logger.fatal("******analyse point end*****");	
		    
			jop=new JOptionPane();					
			jFrame.add(jop,BorderLayout.NORTH);
			jop.showMessageDialog(jFrame, "��2����������ɹ�,��ʱ"+minute+"����","��ʾ", JOptionPane.INFORMATION_MESSAGE); 
			return;
	}
	
	private static DatePicker getDatePicker() {
        DatePicker datepick;
        // ��ʽ
        String DefaultFormat = "yyyy-MM-dd hh:mm:ss";
        // ��ǰʱ��
        Date date = new Date();
        // ����
        Font font = new Font("Times New Roman", Font.BOLD, 14);

        Dimension dimension = new Dimension(180, 30);

       // int[] hilightDays = { 1, 3, 5, 7 };
      //  int[] disabledDays = { 4, 6, 5, 9 };
    //���췽������ʼʱ�䣬ʱ����ʾ��ʽ�����壬�ؼ���С��
        datepick = new DatePicker(date, DefaultFormat, font, dimension);

        datepick.setLocation(130, 90);//������ʼλ��
        /*
        //Ҳ����setBounds()ֱ�����ô�С��λ��
        datepick.setBounds(137, 83, 177, 24);
        */
        // ����һ���·�����Ҫ������ʾ������
     //   datepick.setHightlightdays(hilightDays, Color.red);
        // ����һ���·��в���Ҫ�����ӣ��ʻ�ɫ��ʾ
      //  datepick.setDisableddays(disabledDays);
        // ���ù���
        datepick.setLocale(Locale.CHINA);
        // ����ʱ�����ɼ�
        datepick.setTimePanleVisible(true);
        return datepick;
    }
	 
	 //3������������
    public void exportExcelFile(int type, String startdate, String enddate){
				
		StockExcelPartitionMain sep = new StockExcelPartitionMain(sbDao,sdDao,spDao,ssDao);	   	
		StockRecentWriter ew=new StockRecentWriter(sbDao,sdDao,spDao);
			
		List<String> listStockDate = new ArrayList<String>();	         
        try {
			listStockDate = sdDao.getDatesFromSH000001ForStartEnd(startdate,enddate);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 stockLogger.logger.fatal("******analyse summary and export all execl start*****");	
		 Date start = new Date();
		int size =  listStockDate.size();
		
        for(int i = 0; i < listStockDate.size(); i++) {        	
        	String date = listStockDate.get(i);	
        	stockLogger.logger.fatal("analyse summary time:"+date);
    		
			try {
				if(type == ConstantsInfo.StockMarket) {
					if(i == size -1){
						sep.writeExcelFormIndustryOrderBy("export\\",date, true);
					} else {
						sep.writeExcelFormIndustryOrderBy("export\\",date, false);
					}			
				} else {
					if(i == size -1){
						sep.writeExcelFormFuturesOrderBy("export\\",date, true);
					} else {
						sep.writeExcelFormFuturesOrderBy("export\\",date, false);
					}
				}
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				errorDialog();
				return;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				errorDialog();
				return;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				errorDialog();
				return;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				errorDialog();
				return;
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				errorDialog();
				return;
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				errorDialog();
				return;
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				errorDialog();
				return;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				errorDialog();
				return;
			}
				//sep.writeExcelFormConceptInFirstIndustryOrderBy("export\\",dateNowStr1);	
        }
        
        Date end = new Date();
	     long minute =(end.getTime() - start.getTime())/60000;
			
	    stockLogger.logger.fatal("analyse summary and export all execl consume "+ minute +" minute");
	    stockLogger.logger.fatal("******analyse summary and export all execl end*****");	
	    
		jop=new JOptionPane();					
		jFrame.add(jop,BorderLayout.SOUTH);
		
		jop.showMessageDialog(jFrame, "��3�������ɹ�,��ʱ"+minute+"����","��ʾ", JOptionPane.INFORMATION_MESSAGE); 
		return;
	 }
    
    //4����ͳ�Ʊ�
    public void loadSummaryExcelFile(int type) 
    {
    	StockExcelReadMain seRead = new StockExcelReadMain(sbDao,sdDao,spDao,ssDao);
		//seRead.create_summarY("123");
		try {
			
			seRead.readStockAnsyleExcelData(type);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	jop=new JOptionPane();					
		jFrame.add(jop,BorderLayout.NORTH);
		jop.showMessageDialog(jFrame, "��4������ɹ�","��ʾ", JOptionPane.INFORMATION_MESSAGE); 
		return;
    }
    
  //5������������
    public void exportPointExcelFile(int type)
    {
    	Date startDate1 = new Date();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");  
		String dateNowStr1 = sdf1.format(startDate1);    	
			
		StockExcelPartitionMain sep = new StockExcelPartitionMain(sbDao,sdDao,spDao,ssDao);	    
    	try {
    		
    		String date_rectely = sdDao.getRecetlyDateFromSH000001(dateNowStr1);
    		if(date_rectely==null || date_rectely == ""){
    			return;
    		}
    		if(type == ConstantsInfo.StockMarket) {
    			//sep.writePointExcelFormConceptInFirstIndustryOrderBy("export\\",dateNowStr1);
    			sep.writePointExcelFormIndustryOrderBy("export\\",date_rectely);
    		} else {
    			sep.writePointExcelFormFuturesOrderBy("export\\",date_rectely);
    		}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	jop=new JOptionPane();					
		jFrame.add(jop,BorderLayout.NORTH);
		jop.showMessageDialog(jFrame, "��5�������ɹ�","��ʾ", JOptionPane.INFORMATION_MESSAGE); 
		return;
    }
    
    //6����ͳ������
    public void exportSummaryExcelFile(int type)
    {
    	Date startDate1 = new Date();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");  
		String dateNowStr1 = sdf1.format(startDate1);  
			
		StockExcelPartitionMain sep = new StockExcelPartitionMain(sbDao,sdDao,spDao,ssDao);	   
    	try {
    		
    		String date_rectely = sdDao.getRecetlyDateFromSH000001(dateNowStr1);
    		if(date_rectely==null || date_rectely == ""){
    			return;
    		}
    		stockLogger.logger.fatal("export summary excel time:"+date_rectely);
    		if(type == ConstantsInfo.StockMarket) {
    			sep.writeSummaryExcelFormConceptInFirstIndustryOrderBy("export\\",date_rectely);
    		} else {
    			sep.writeSummaryExcelFormFuturesOrderBy("export\\",date_rectely);
    		}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			errorDialog();
			return;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			errorDialog();
			return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			errorDialog();
			return;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			errorDialog();
			return;
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			errorDialog();
			return;
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			errorDialog();
			return;
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			errorDialog();
			return;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			errorDialog();
			return;
		}
    	
    	jop=new JOptionPane();					
		jFrame.add(jop,BorderLayout.NORTH);
		jop.showMessageDialog(jFrame, "��6�������ɹ�","��ʾ", JOptionPane.INFORMATION_MESSAGE); 
		return;
    }
    
    
    //7������������
    public void analyseOperation(int type, String startdate, String enddate)
    {	
		StockExcelOperationMain sop = new StockExcelOperationMain(sbDao,sdDao,spDao,ssDao);	 
    	
		List<String> listStockDate = new ArrayList<String>();	         
        try {
			listStockDate = sdDao.getDatesFromSH000001ForStartEnd(startdate,enddate);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		stockLogger.logger.fatal("******analy operation start******");
		Date start = new Date();
        for(int i = 0; i < listStockDate.size(); i++) {        	
        	String date = listStockDate.get(i);	
        	stockLogger.logger.fatal("analy operation time:"+date);
			try {
				if(type == ConstantsInfo.StockMarket) {
					sop.analyseStockOperationAll(ConstantsInfo.StockMarket,date);			
				} else {
					sop.analyseStockOperationAll(ConstantsInfo.FuturesMarket,date);
				}
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				errorDialog();
				return;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				errorDialog();
				return;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				errorDialog();
				return;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				errorDialog();
				return;
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				errorDialog();
				return;
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				errorDialog();
				return;
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				errorDialog();
				return;
			}
        }
        Date end = new Date();
        long minute =(end.getTime() - start.getTime())/60000;
		
		stockLogger.logger.fatal("analy operation consume "+ minute +" minute");
        stockLogger.logger.fatal("*****analy operation end*****");

    	jop=new JOptionPane();					
		jFrame.add(jop,BorderLayout.NORTH);
		jop.showMessageDialog(jFrame, "��7�����������ɹ�,��ʱ"+minute+"����","��ʾ", JOptionPane.INFORMATION_MESSAGE); 
		return;
    }
    
    public void errorDialog()
    {
    	jop=new JOptionPane();					
		jFrame.add(jop,BorderLayout.NORTH);
		jop.showMessageDialog(jFrame, "����ʧ��,������־","��ʾ", JOptionPane.INFORMATION_MESSAGE); 
		return;
    }
	 
    
    //8����������������
    public void exportOperationExcelFile(int type)
    {
    	Date startDate1 = new Date();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");  
		String dateNowStr1 = sdf1.format(startDate1);    	
		
		StockExcelPartitionMain sep = new StockExcelPartitionMain(sbDao,sdDao,spDao,ssDao);	    
    	try {
    		String date_rectely = sdDao.getRecetlyDateFromSH000001(dateNowStr1);
    		if(date_rectely==null || date_rectely == ""){
    			return;
    		}
    		
    		stockLogger.logger.fatal("export operation excel time:"+date_rectely);
    		if(type == ConstantsInfo.StockMarket) {
    			sep.writeOperationExcelFormIndustryOrderByAllType("export\\",date_rectely, ConstantsInfo.DayDataType);
    			sep.writeOperationExcelFormIndustryOrderByAllType("export\\",date_rectely, ConstantsInfo.WeekDataType);
    			sep.writeOperationExcelFormIndustryOrderByAllType("export\\",date_rectely, ConstantsInfo.MonthDataType);
    		} else {
    		//	sep.writeOperationExcelFormFuturesOrderBy("export\\",dateNowStr1);
    			sep.writeOperationExcelFormFuturesOrderByAllType("export\\",date_rectely, ConstantsInfo.DayDataType);
    			sep.writeOperationExcelFormFuturesOrderByAllType("export\\",date_rectely, ConstantsInfo.WeekDataType);
    			sep.writeOperationExcelFormFuturesOrderByAllType("export\\",date_rectely, ConstantsInfo.MonthDataType);
    		}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	jop=new JOptionPane();					
		jFrame.add(jop,BorderLayout.NORTH);
		jop.showMessageDialog(jFrame, "��8�������ɹ�","��ʾ", JOptionPane.INFORMATION_MESSAGE); 
		return;
    }
    
    //9�����ܵĲ�������
    public void exportTotalOperationExcelFile(int type)
    {
    	Date startDate1 = new Date();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");  
		String dateNowStr1 = sdf1.format(startDate1);    	
		stockLogger.logger.fatal("******export total operation start*****");	
		StockExcelPartitionMain sep = new StockExcelPartitionMain(sbDao,sdDao,spDao,ssDao);	    
    	try {
    		String date_rectely = sdDao.getRecetlyDateFromSH000001(dateNowStr1);
    		if(date_rectely==null || date_rectely == ""){
    			return;
    		}
    		stockLogger.logger.fatal("export total operation excel time:"+date_rectely);
    		if(type == ConstantsInfo.StockMarket) {

    			//sep.writeTotalOperationExcelFormIndustryOrderBy("export\\",dateNowStr1);
    			sep.writeTotalOperationExcelFormIndustryOrderByAllType("export\\",date_rectely,ConstantsInfo.DayDataType);
    			sep.writeTotalOperationExcelFormIndustryOrderByAllType("export\\",date_rectely,ConstantsInfo.WeekDataType);
    			sep.writeTotalOperationExcelFormIndustryOrderByAllType("export\\",date_rectely,ConstantsInfo.MonthDataType);

    		} else {
    			//sep.writeTotalOperationExcelFormFuturesOrderBy("export\\",date_rectely);
    			sep.writeTotalOperationExcelFormFuturesOrderByAllType("export\\",date_rectely,ConstantsInfo.DayDataType);
    			sep.writeTotalOperationExcelFormFuturesOrderByAllType("export\\",date_rectely,ConstantsInfo.WeekDataType);
    			sep.writeTotalOperationExcelFormFuturesOrderByAllType("export\\",date_rectely,ConstantsInfo.MonthDataType);

    		}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 Date end = new Date();
	     long minute =(end.getTime() - startDate1.getTime())/60000;
			
	    stockLogger.logger.fatal("analy total operation consume "+ minute +" minute");
	    stockLogger.logger.fatal("******export total operation end*****");	
    	jop=new JOptionPane();					
		jFrame.add(jop,BorderLayout.NORTH);
		jop.showMessageDialog(jFrame, "��9���ܲ��������ɹ�,��ʱ"+minute+"����","��ʾ", JOptionPane.INFORMATION_MESSAGE); 
		return;
    }
	 
	 //���ӹ�Ʊ
	 public void addSingleStock()
	 {
		 	String listItem[] = {"��","��"};
			JLabel jlCode=new JLabel("��Ʊ����");
			JLabel jlName=new JLabel("����");
			JLabel jlIndustry=new JLabel("��ҵ");
			JLabel jlConcept=new JLabel("����");
		
			JLabel jlMarginTrading=new JLabel("������ȯ");					
			final JTextField jtfCode=new JTextField(6);
			final JTextField jtfName=new JTextField(12);
			final JTextField jtfieldIndu=new JTextField(24); //��¼ѡ�����ҵ
			final JTextField jtfieldConc=new JTextField(48); //��¼ѡ��ĸ���
		//	final JTextField jtfIndustry=new JTextField(24);
		//	final JTextField jtfConcept=new JTextField(24);
			
			//����
			List<String> allConceptNameList = null;
			try {
				allConceptNameList = sbDao.getAllConceptName();
			} catch (SQLException e3) {
				
				e3.printStackTrace();
			}
			final String[] conceptName = (String[])allConceptNameList.toArray(new String[allConceptNameList.size()]);
			final JList jlistConcept=new JList(conceptName);
			
			//��ҵ
			List<String> allThirdIndustryNameList = null;
			try {
				allThirdIndustryNameList = sbDao.getAllThirdIndustryName();
			} catch (SQLException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}	
			String[] industryName = (String[])allThirdIndustryNameList.toArray(new String[allThirdIndustryNameList.size()]);
			final JList jlistIndustry=new JList(industryName);
			jlistIndustry.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			
			//������ȯ
			final JList jlistMarginTrading=new JList(listItem);
			jlistMarginTrading.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			JButton cancelButton=new JButton("ȡ��");	
			JButton confirmButton=new JButton("ȷ��");	
			
			final JFrame jfDialog = new JFrame();  // JFrameDialog
			
			//JFrame jfDialog = new JFrame(); 
			JPanel dialogJP=new JPanel();
			JPanel dialogJPCenter=new JPanel();
			JPanel dialogJPBottom=new JPanel();
			
			dialogJP.add(jlCode);
			dialogJP.add(jtfCode);
			dialogJP.add(jlName);
			dialogJP.add(jtfName);
			dialogJPCenter.add(jlIndustry);
			dialogJPCenter.add(new JScrollPane(jlistIndustry));
		//	dialogJPCenter.add(jtfIndustry);
			dialogJPCenter.add(jlConcept);
			dialogJPCenter.add(new JScrollPane(jlistConcept));
		//	dialogJP.add(jtfConcept);
		//	dialogJP.add(jlConceptCommit);
			dialogJPCenter.add(jlMarginTrading);
			dialogJPCenter.add(jlistMarginTrading);
			dialogJPBottom.add(cancelButton);	
			dialogJPBottom.add(confirmButton);	
			
			if(jop!=null)
			{
				//jFrame.remove(jop);
				jfDialog.remove(jop);
			}
			
			//��ҵѡ���				
			jlistIndustry.addListSelectionListener(new ListSelectionListener()
			{
				public void valueChanged(ListSelectionEvent arg0) {
					// TODO Auto-generated method stub
					int tmp = 0;	
					jtfieldIndu.setText(((JList)arg0.getSource()).getSelectedValue().toString());				
			        System.out.println(jlistIndustry.getSelectedValue().toString());
				}
			});
			
			//����ѡ���
			jlistConcept.addListSelectionListener(new ListSelectionListener()
			{			
				public void valueChanged(ListSelectionEvent arg0) {
					// TODO Auto-generated method stub
					int tmp = 0;	
					jtfieldConc.setText(((JList)arg0.getSource()).getSelectedValue().toString());				       
				}
			});
			
			
			//ȡ��
			cancelButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
				{
					jfDialog.dispose();
				}
			});
			
			confirmButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
				{
					String stockCode=null;
					StockSingle sStock=null;
					//����Ʊ���볤��
					if (jtfCode.getText().length()!=6){
						jop=new JOptionPane();					
						//jFrame.add(jop,BorderLayout.NORTH);
						jfDialog.add(jop,BorderLayout.NORTH);
						
						jop.showMessageDialog(jFrame, "��Ʊ���ų����д���,������6λ��ȷ�Ĵ���","��ʾ", JOptionPane.INFORMATION_MESSAGE); 
						return;	
					}
					
					if (jtfCode.getText().startsWith("6")) {
						stockCode = "SH"+jtfCode.getText();
					} else {
						stockCode = "SZ"+jtfCode.getText();
					}
					
					//����Ʊ����
					try {
						sStock = sbDao.lookUpStockSingle(stockCode);
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					} catch (ClassNotFoundException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					} catch (SQLException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					//�Ѿ�����
					if(sStock != null){
						jop=new JOptionPane();					
						//jFrame.add(jop,BorderLayout.NORTH);
						jfDialog.add(jop,BorderLayout.NORTH);
						
						jop.showMessageDialog(jFrame, "�ù�Ʊ��Ϣ�Ѿ�����","��ʾ", JOptionPane.INFORMATION_MESSAGE); 
						return;	
					}							
					
					//�����ҵ
					StockIndustry sindustry=null;
					try {
						sindustry= sbDao.lookUpIndustry(jtfieldIndu.getText());
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
					if(sindustry==null){
						jop=new JOptionPane();					
						//jFrame.add(jop,BorderLayout.NORTH);
						jfDialog.add(jop,BorderLayout.NORTH);
						jop.showMessageDialog(jFrame, "��ҵ���д���,��������ȷ����ҵ","��ʾ", JOptionPane.INFORMATION_MESSAGE); 
						return;	
					}
							
					//������	
					StockConcept sconcept=null;		    
			    	try {
						sconcept=sbDao.lookUpStockConcept(jtfieldConc.getText());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					if(sconcept==null){
						jop=new JOptionPane();					
						jFrame.add(jop,BorderLayout.NORTH);						
						jop.showMessageDialog(jFrame, "�������,��������ȷ�ĸ���","��ʾ", JOptionPane.INFORMATION_MESSAGE); 
						return;	
					}
			    				
					//�����¹�Ʊ
					sStock=new StockSingle(1,stockCode,jtfName.getText(),sindustry.getThirdcode(),jtfieldIndu.getText(),sindustry.getSecondcode(),sindustry.getSecondname(),sindustry.getFirstcode(),sindustry.getFirstname(),jtfieldConc.getText(),jlistMarginTrading.getSelectedIndex());
					try {
						//����stock_to_industry
						sbDao.insertStockSingleToIndustry(sStock);
						//����allinfo
						sbDao.insertStockSingle(sStock);
						//����stock_to_concept
						//sbDao.insertStockToConcept(stockCode,jtfName.getText(),sconcept.getCode(),sconcept.getName());
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (SQLException e) {
						e.printStackTrace();
					}			
					
					//�Ѿ�����					
					jop=new JOptionPane();					
					//jFrame.add(jop,BorderLayout.NORTH);
					jfDialog.add(jop,BorderLayout.NORTH);
					jop.showMessageDialog(jFrame, "���ݸ��³ɹ�","��ʾ", JOptionPane.INFORMATION_MESSAGE); 
					
					jfDialog.dispose();//�ر�
				}
			});
			jfDialog.add(dialogJP,BorderLayout.NORTH);
			jfDialog.add(dialogJPCenter,BorderLayout.CENTER);
			jfDialog.add(dialogJPBottom,BorderLayout.SOUTH);
		//	jfDialog.setExtendedState(Frame.MAXIMIZED_BOTH);
			jfDialog.setSize(500,300);  
				
			jfDialog.setVisible(true);  
			jfDialog.setLocationRelativeTo(null);
			System.out.println("operate stock info");
		
	 }
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
		
		Connection stockBaseConn = DbConn.getConnDB("stockConf/conn_base_db.ini"); 
	    Connection stockDataConn = DbConn.getConnDB("stockConf/conn_data_db.ini"); 
	    Connection stockPointConn = DbConn.getConnDB("stockConf/conn_point_db.ini");  
	    Connection stockSummaryConn = DbConn.getConnDB("stockConf/conn_summary_db.ini");
	   
		stockBM=new StockBaseManager(stockBaseConn,stockDataConn,stockPointConn,stockSummaryConn);
		PropertyConfigurator.configure("StockConf/log4j_manage.properties");
		stockLogger.logger.fatal("stock manager start");
		stockBM.init();	
		
		//stockBaseConn.close();
		//stockPointConn.close();
		//stockDataConn.close();

	}

}
