package dao;

public class StockToConcept {
	private int  id;
	private String stockFullId;
	private String stockName;
	private String stockFirstIndustryCode; 
	private String stockConceptCode; 
	private String stockConceptName; 
	
	public StockToConcept()
	{
		
	}
	
	public StockToConcept(int id, String stockFullId, String stockName,String stockFirstIndustryCode, String stockConceptCode,String stockConceptName)
	{
		super();
		this.id = id;
		this.stockFullId = stockFullId;
		this.stockName = stockName;
		this.stockFirstIndustryCode = stockFirstIndustryCode;
		this.stockConceptCode = stockConceptCode;
		this.stockConceptName = stockConceptName;
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

	public String getStockFirstIndustryCode() {
		return stockFirstIndustryCode;
	}

	public void setStockFirstIndustryCode(String stockFirstIndustryCode) {
		this.stockFirstIndustryCode = stockFirstIndustryCode;
	}

	public String getStockConceptCode() {
		return stockConceptCode;
	}

	public void setStockConceptCode(String stockConceptCode) {
		this.stockConceptCode = stockConceptCode;
	}

	public String getStockConceptName() {
		return stockConceptName;
	}

	public void setStockConceptName(String stockConceptName) {
		this.stockConceptName = stockConceptName;
	}

	
}
