package stockGUI.stocktable;

import java.util.List;

import javax.swing.table.AbstractTableModel;


import dao.StockRegional;

public class StockRegionalTableModel extends AbstractTableModel{

	private List<StockRegional> stockAllRegional;	
	private static final int STOCK_REGIONAL_COLUMN_COUNT =4;
	
	
	public StockRegionalTableModel(List<StockRegional> stockAllRegional) {
		this.stockAllRegional=stockAllRegional;	
	}
	
	//重写getColumnName方法，设置列名
	public String getColumnName(int c){
		String colummName="";
		switch(c)
		{
		case 0:
			colummName="市代码";
			break;
		case 1:		
			colummName="市名称";
			break;
		case 2:		
			colummName="省代码";
			break;
		case 3:		
			colummName="省名称";
			break;	
		default:
			break;
		}
		return colummName;
	}
	
	public int getColumnCount() {
		
		return STOCK_REGIONAL_COLUMN_COUNT;
	}

	@Override
	public int getRowCount() {
		return stockAllRegional.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		Object oj;
		StockRegional sr;
		sr=stockAllRegional.get(row);
		switch(col)
		{
		case 0:		
			oj=sr.getCode();
			break;	
		case 1:				
			oj=sr.getName();
			break;
		case 2:				
			oj=sr.getProvincecode();
			break;
		case 3:				
			oj=sr.getProvincename();
			break;		
		default:
			return null;
		}
		return oj;
	}

}
