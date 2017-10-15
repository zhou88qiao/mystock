package excel.all_v2;

public class StockExcelItem {
	
	//
	private String fullId;	
	private int flag;
	private StockStatValue ssValue;//单一统计项
	private StockCurValue scValue;//当前值
	private StockDesireValue sdValue;//预期值 
	
	public StockExcelItem(String fullId,StockStatValue ssValue,StockCurValue scValue,StockDesireValue sdValue)
	{
		this.fullId=fullId;
		this.ssValue=ssValue;
		this.scValue=scValue;
		this.sdValue=sdValue;		
	}
	
	public StockStatValue getSsValue() {
		return ssValue;
	}
	
	public void setSsValue(StockStatValue ssValue) {
		this.ssValue = ssValue;
	}
	public StockCurValue getScValue() {
		return scValue;
	}
	public void setScValue(StockCurValue scValue) {
		this.scValue = scValue;
	}

	public String getFullId() {
		return fullId;
	}

	public void setFullId(String fullId) {
		this.fullId = fullId;
	}

	public StockDesireValue getSdValue() {
		return sdValue;
	}

	public void setSdValue(StockDesireValue sdValue) {
		this.sdValue = sdValue;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}
	

}
