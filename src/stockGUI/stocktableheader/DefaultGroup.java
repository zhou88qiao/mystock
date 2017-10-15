package stockGUI.stocktableheader;

public class DefaultGroup implements Group {
	 private int row = 0;

	    private int column = 0;

	    private int rowSpan = 1;

	    private int columnSpan = 1;

	    private Object headerValue = null;

	  
	    public int getRow() {
	        return this.row;
	    }

	 
	    public void setRow(int row) {
	        this.row = row;
	    }

	  
	    public int getColumn() {
	        return this.column;
	    }

	    public void setColumn(int column) {
	        this.column = column;
	    }

	    
	    public int getColumnSpan() {
	        return this.columnSpan;
	    }

	   
	    public void setColumnSpan(int columnSpan) {
	        this.columnSpan = columnSpan;
	    }

	   
	    public int getRowSpan() {
	        return this.rowSpan;
	    }

	    
	    public void setRowSpan(int rowSpan) {
	        this.rowSpan = rowSpan;
	    }

	    public Object getHeaderValue() {
	        return this.headerValue;
	    }

	    public void setHeaderValue(Object headerValue) {
	        this.headerValue = headerValue;
	    }
}
