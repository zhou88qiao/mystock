package test;


import java.awt.BorderLayout;
import java.awt.BorderLayout;
import java.awt.Dimension;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;


public class dialog {
	
	private JFrame jfStock=new JFrame("股票");

	JButton add,select,del,update;
    JTable table;
    
    Object body[][]=new Object[50][50];
  // String title[] = {"股票名字","今日开盘价 ","昨日收盘价","当前价格","今日最高价","今日最低价","竟买价","竞卖价","成交的股票数","成交金额(元)","买一数","买一价","买二数","买二价","买三数","买三价","买四数","买四价","买五数","买五价","卖一数","卖一价","卖二数","卖二价","卖三数","卖三价","卖四数","卖四价","卖五数","卖五价","日期","时间"} ;  
    String title[] = {"股票名字","今日开盘价 ","昨日收盘价","当前价格","日期","时间"} ;  
   
    JTabbedPane tp;
    JPanel ps;
    JMenuBar menuBar=new JMenuBar();
    JMenu priceMenu,analyseMenu,functionMenu,helpMenu;

    JScrollPane scrollPane;
 //  ResultSetTableModel model;
 //   JComboBox<String> tablename=new JComboBox<>();
	public void init() {
   
		ps=new JPanel();
        add=new JButton("添加");
        select=new JButton("显示");
        update=new JButton("更改");
        del=new JButton("删除");  
               
   
        ps.add(add);
        ps.add(select);
        ps.add(update);
        ps.add(del);
        
        DataOperationAction doAc=new DataOperationAction();
        add.addActionListener(doAc);
        select.addActionListener(doAc);
        update.addActionListener(doAc);
        del.addActionListener(doAc);
        
        priceMenu=new JMenu();
        analyseMenu=new JMenu();
        functionMenu=new JMenu();
        helpMenu=new JMenu();
        menuBar.add(priceMenu);
        menuBar.add(analyseMenu);
        menuBar.add(functionMenu);
        menuBar.add(helpMenu);
        
        table=new JTable(body,title);
        table.setPreferredScrollableViewportSize(new Dimension(500,40));
        table.setPreferredScrollableViewportSize(new Dimension(700,100));
        table.setPreferredScrollableViewportSize(new Dimension(900,300));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        tp=new JTabbedPane();
        tp.add("股票信息",new JScrollPane(table));
        
       // jfStock.add(menuBar);
        jfStock.setJMenuBar(menuBar);
      //  jfStock.add(ps);
     //   jfStock.add(tp);
       jfStock.add(ps,BorderLayout.SOUTH);
        jfStock.add(tp,BorderLayout.NORTH);
        
        
        jfStock.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jfStock.pack();
        jfStock.setVisible(true);
    }
	
	private class DataOperationAction implements ActionListener
	{
		public void actionPerformed(ActionEvent e) {
			if(e.getSource()==add)
			{
				add();
			}
			if(e.getSource()==select)
			{
				select();
			}
			if(e.getSource()==update)
			{
				update();
			}
			if(e.getSource()==del)
			{
				del();
			}
		}
	}
	  
	  public void del() {
		   
	  }
	  
	  public void update() {

	  }
	  
	  public void select() {
	  
	  }
	  public void add() {
		  
	  }
	  
	  public static void main(String[] args) {
			
			dialog dl=new dialog();
			dl.init();
			
		}
	
}
