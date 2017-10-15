package dao;

//三级行业下对应的股票
public class StockToIndustry {
	
	private int  id;
	private String stockFullId;
	private String stockName;

	private String thirdIndustryCode;
	private String thirdIndustryName;

	
	public StockToIndustry()
	{
		
	}
	
	public StockToIndustry(int id, String stockFullId,String stockName,String thirdIndustryCode,String thirdIndustryName)
	{
		super();
		this.id = id;
		this.stockFullId = stockFullId;
		this.stockName = stockName;
		this.thirdIndustryCode = thirdIndustryCode;
		this.thirdIndustryName = thirdIndustryName;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getStockFullId() {
		return stockFullId;
	}
	public void setStockFullId(String stockFullId) {
		this.stockFullId = stockFullId;
	}
	public String getStockName() {
		return stockName;
	}
	public void setStockName(String stockName) {
		this.stockName = stockName;
	}
	public String getThirdIndustryCode() {
		return thirdIndustryCode;
	}
	public void setThirdIndustryCode(String thirdIndustryCode) {
		this.thirdIndustryCode = thirdIndustryCode;
	}
	public String getThirdIndustryName() {
		return thirdIndustryName;
	}
	public void setThirdIndustryName(String thirdIndustryName) {
		this.thirdIndustryName = thirdIndustryName;
	}
	
}
