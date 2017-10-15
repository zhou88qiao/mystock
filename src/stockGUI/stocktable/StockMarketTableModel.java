package stockGUI.stocktable;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import dao.StockMarket;
import dao.StockPoint;


public class StockMarketTableModel extends AbstractTableModel{
	
	private List<StockMarket> stockAllMarket;
	
	private static final int STOCK_MARKET_COLUMN_COUNT = 2;
	
	public StockMarketTableModel(List<StockMarket> stockAllMarket) {
		this.stockAllMarket=stockAllMarket;	
	}
	
	/*
	public boolean isCellEditable(int row,int col)
	{
		return true;
	}
	*/
	
	public void setValueAt(Object aValue,int row,int col)
	{
		
	}
	public void addRow(Object aValue){
		System.out.println("add row market");
		
	}
	public void removeRow(int row) {		
		stockAllMarket.remove(row);
		System.out.println(stockAllMarket.size());		
	}
	//重写getColumnName方法，设置列名
	public String getColumnName(int c){
		String colummName=null;
		switch(c)
		{
		case 0:
			colummName="代码";
			break;
		case 1:
		default:
			colummName="名称";
			break;
		}
		return colummName;
		
	}
	public int getColumnCount() {
		
		return STOCK_MARKET_COLUMN_COUNT;
	}

	@Override
	public int getRowCount() {
		
		return stockAllMarket.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		Object oj;
		StockMarket sm;
		sm=stockAllMarket.get(row);
		switch(col)
		{
		case 0:		
			oj=sm.getCode();
			break;	
		case 1:				
			oj=sm.getName();
			break;
		default:
			return null;
		}
		return oj;
	}

	

}
