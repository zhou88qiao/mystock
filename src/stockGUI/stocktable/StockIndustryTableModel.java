package stockGUI.stocktable;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import dao.StockIndustry;
import dao.StockMarket;

public class StockIndustryTableModel extends AbstractTableModel{

	private List<StockIndustry> stockAllIndustry;
	
	private static final int STOCK_INDUSTRY_COLUMN_COUNT =6;
	
	
	public StockIndustryTableModel(List<StockIndustry> stockAllIndustry) {
		this.stockAllIndustry=stockAllIndustry;	
	}
	
	//重写getColumnName方法，设置列名
	public String getColumnName(int c){
		String colummName="";
		switch(c)
		{
		case 0:
			colummName="三级代码";
			break;
		case 1:		
			colummName="三级名称";
			break;
		case 2:		
			colummName="二级名称";
			break;
		case 3:		
			colummName="二级名称";
			break;
		case 4:		
			colummName="一级名称";
			break;
		case 5:		
			colummName="一级名称";
			break;
		default:
			break;
		}
		return colummName;
	}
	public int getColumnCount() {
		return STOCK_INDUSTRY_COLUMN_COUNT;
	}

	
	public int getRowCount() {
		return stockAllIndustry.size();
	}


	public Object getValueAt(int row, int col) {
		Object oj;
		StockIndustry si;
		si=stockAllIndustry.get(row);
		switch(col)
		{
		case 0:		
			oj=si.getThirdcode();
			break;	
		case 1:				
			oj=si.getThirdname();
			break;
		case 2:				
			oj=si.getSecondcode();
			break;
		case 3:				
			oj=si.getSecondname();
			break;
		case 4:				
			oj=si.getFirstcode();
			break;
		case 5:				
			oj=si.getFirstname();
			break;
		default:
			return null;
		}
		return oj;
	}

}
