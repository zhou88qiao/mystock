package stockGUI.stockbase;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import stockGUI.stocktable.StockSingleTableModel;

import common.ConstantsInfo;


import dao.StockBaseDao;
import dao.StockIndustry;
import dao.StockSingle;

public class StockSingleGui {
	static StockBaseDao sbDao=new StockBaseDao();
	String listItem[] = {"否","是"};
	JLabel jlCode=new JLabel("股票代码:");
	JLabel jlName=new JLabel("名称:");
	JLabel jlIndustry=new JLabel("行业:");
	JLabel jlConcept=new JLabel("概念:");
//	JLabel jlConceptCommit=new JLabel("多个概念请以,号隔开");
	JLabel jlMarginTrading=new JLabel("融资融券:");					
	JTextField jtfCode=new JTextField(6);
	JTextField jtfName=new JTextField(12);
//	final JTextField jtfIndustry=new JTextField(24);
//	final JTextField jtfConcept=new JTextField(24);
	JButton cancelButton=new JButton("取消");	
	JButton confirmButton=new JButton("确定");	
	JOptionPane jop;	
	
	String selConcept=null;
	String selIndustry=null;
	int selMarginTrading=0;
	
	int actionFlag=0;
	public StockSingleGui()
	{
		
	}
	
	public StockSingleGui(StockSingle ss)
	{
		String code= ss.getStockFullId().substring(2);
		jtfCode.setText(code);
		jtfName.setText(ss.getStockName());		
		
	}
	
	public void init(StockSingle ss,int flag)
	{
		actionFlag=flag;	
		
		List<String> allConceptNameList = null;
		try {
			allConceptNameList = sbDao.getAllConceptName();
		} catch (SQLException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		final String[] conceptName = (String[])allConceptNameList.toArray(new String[allConceptNameList.size()]);
		final JList jlistConcept=new JList(conceptName);
		
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
		
		
		final JList jlistMarginTrading=new JList(listItem);
		jlistMarginTrading.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		//设置默认
		jlistMarginTrading.setSelectedIndex(0);
		
		//编辑，将原来结果显示
		if(actionFlag == ConstantsInfo.EDIT){
			jtfCode.setEditable(false);
			jtfName.setEditable(false);
			if(ss==null)
				return;
			jlistIndustry.setSelectedValue(ss.getThirdName(), true);
			String[] strcon=ss.getStockConcept().split(",");
			for (int i = 0; i < strcon.length; i++)
			{       
				jlistConcept.setSelectedValue(strcon[i], true);
			}
			jlistMarginTrading.setSelectedIndex(ss.getEnableMarginTrading());
		}
		
		final JFrame jfDialog = new JFrame();  
		
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
		
				
		
		jlistMarginTrading.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent arg0) {
				// TODO Auto-generated method stub	
				selMarginTrading=jlistMarginTrading.getSelectedIndex();		
				System.out.println("index:"+selMarginTrading);
			}			
		});
		
		
		jlistIndustry.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent arg0) {
				// TODO Auto-generated method stub	
				selIndustry=jlistIndustry.getSelectedValue().toString();		    
			}
		});
		
		
		
		
		jlistConcept.addListSelectionListener(new ListSelectionListener()
		{
		
			public void valueChanged(ListSelectionEvent arg0) {
				// TODO Auto-generated method stub
				int tmp = 0;	
				String stmp = "";
				int[] index = jlistConcept.getSelectedIndices();//利用JList类所提供的getSelectedIndices()方法可得到用户所选取的所有 
		        for(int i=0; i < index.length ; i++)//index值，这些index值由一个int array返回.
		        {
		            tmp = index[i];
		            stmp = stmp+conceptName[tmp]+",";
		        }
		        //去掉最后一个,号
		        selConcept=stmp.substring(0, stmp.length()-1);
		       			       
			}

		});
		
		//取消
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
				//检查股票代码长度
				System.out.println("code text:"+jtfCode.getText());
				System.out.println("code length:"+jtfCode.getText().length());
				if (jtfCode.getText().length()!=6){
					jop=new JOptionPane();					
					jfDialog.add(jop,BorderLayout.NORTH);
					jop.showMessageDialog(jfDialog, "股票代号长度有错误,请输入正确的代码","提示", JOptionPane.INFORMATION_MESSAGE); 
					return;	
				}
				
				if (jtfCode.getText().startsWith("6")) {
					stockCode = "SH"+jtfCode.getText();
				} else {
					stockCode = "SZ"+jtfCode.getText();
				}
				
				
				//检查股票代码
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
				
				//已经存在
				if(actionFlag==ConstantsInfo.ADD && sStock != null){
					jop=new JOptionPane();					
					jfDialog.add(jop,BorderLayout.NORTH);
					jop.showMessageDialog(jfDialog, "该股票信息已经存在","提示", JOptionPane.INFORMATION_MESSAGE); 
					return;	
				}							
				
				//检查行业
				StockIndustry sindustry=null;
				try {
					sindustry= sbDao.lookUpIndustry(selIndustry);
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
					jfDialog.add(jop,BorderLayout.NORTH);
					jop.showMessageDialog(jfDialog, "行业名有错误,请输入正确的行业","提示", JOptionPane.INFORMATION_MESSAGE); 
					return;	
				}
				
				/*
				//检查概念	
				StockConcept sconcept=null;
			    String[] strarray=jtfConcept.getText().split("，");
			    for (int i = 0; i < strarray.length; i++)
			    {
			         
			         try {
						sconcept=sbDao.lookUpStockConcept(strarray[i]);
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
						String conceptCmmit=strarray[i]+"概念不存在,请输入正确的概念";
						jop.showMessageDialog(jFrame, "概念不存在,请输入正确的概念","提示", JOptionPane.INFORMATION_MESSAGE); 
						return;	
					}
			    }
			    */
				
				switch (actionFlag)
				{
				case ConstantsInfo.ADD:
					//插入新股票
					sStock=new StockSingle(1,stockCode,jtfName.getText(),sindustry.getThirdcode(),selIndustry,sindustry.getSecondcode(),sindustry.getSecondname(),sindustry.getFirstcode(),sindustry.getFirstname(),selConcept,jlistMarginTrading.getSelectedIndex());
					try {
						sbDao.insertStockSingle(sStock);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					break;
				case ConstantsInfo.EDIT:
					sStock=new StockSingle(1,stockCode,jtfName.getText(),sindustry.getThirdcode(),selIndustry,sindustry.getSecondcode(),sindustry.getSecondname(),sindustry.getFirstcode(),sindustry.getFirstname(),selConcept,jlistMarginTrading.getSelectedIndex());
					try {
						sbDao.updateStockSingle(sStock);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					break;						
					
				}
		
				jfDialog.dispose();
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
	
	
	public void queryInit(List<StockSingle> listStockSingle)
	{
		StockSingleTableModel stSingleTabMod;
		JTable stockTable;
		final JFrame jfDialog = new JFrame();  
		
		stSingleTabMod=new StockSingleTableModel(listStockSingle);
		stockTable=new JTable(stSingleTabMod);	
		
		stockTable.setBorder(BorderFactory.createEtchedBorder());  
		stockTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		stockTable.setSelectionForeground(Color.blue);
		stockTable.setShowVerticalLines(false);//  
	        // 设置是否显示单元格间的分割线
		stockTable.setShowHorizontalLines(false);
		JScrollPane dialogJP=new JScrollPane(stockTable);		
		
		//JPanel dialogJP=new JPanel();
		//dialogJP.add(stockTable);
		
		jfDialog.add(dialogJP,BorderLayout.CENTER);
		jfDialog.setSize(600,400);  
		jfDialog.setVisible(true);  
		jfDialog.setLocationRelativeTo(null);	
		
		System.out.println("operate stock info");
	}
		

}
