package mybatis;

public class StockSingle {
	
	private int  id;
	private String stockFullId;
	private String stockName;
	private String thirdCode; //行业
	private String thirdName; //行业
	private String secondCode; //行业
	private String secondName; //行业
	private String firstCode; //行业
	private String firstName; //行业
	private String stockConcept;//概念
	private int enableMarginTrading;//
	

	public StockSingle()
	{
		
	}
	
	public StockSingle(int id, String stockFullId, String stockName,String thirdCode,String thirdName,String secondCode,String secondName,String firstCode,String firstName,String stockConcept,int enableMarginTrading)
	{
		super();
		this.id = id;
		this.stockFullId = stockFullId;
		this.stockName = stockName;
		this.thirdCode = thirdCode;
		this.thirdName = thirdName;
		this.secondCode = secondCode;
		this.secondName = secondName;
		this.firstCode = firstCode;
		this.firstName = firstName;
		this.stockConcept = stockConcept;
		this.enableMarginTrading = enableMarginTrading;		
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
	public String getThirdName() {
		return thirdName;
	}
	public void setThirdName(String thirdName) {
		this.thirdName = thirdName;
	}
	public String getStockConcept() {
		return stockConcept;
	}
	public void setStockConcept(String stockConcept) {
		this.stockConcept = stockConcept;
	}
	public int getEnableMarginTrading() {
		return enableMarginTrading;
	}
	public void setEnableMarginTrading(int enableMarginTrading) {
		this.enableMarginTrading = enableMarginTrading;
	}

	public String getThirdCode() {
		return thirdCode;
	}

	public void setThirdCode(String thirdCode) {
		this.thirdCode = thirdCode;
	}

	public String getSecondCode() {
		return secondCode;
	}

	public void setSecondCode(String secondCode) {
		this.secondCode = secondCode;
	}

	public String getSecondName() {
		return secondName;
	}

	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}

	public String getFirstCode() {
		return firstCode;
	}

	public void setFirstCode(String firstCode) {
		this.firstCode = firstCode;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

}
