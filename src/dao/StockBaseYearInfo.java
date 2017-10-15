package dao;

public class StockBaseYearInfo {
	private int id;
	private String stockFullId;	
	private float gongJiJin;//公积金	
	private float liRun; //利润
	private String yuGaoContent; //预告内容
	private String piLouDate; //时间
	
	
	
	public StockBaseYearInfo()
	{
		
	}
	

	public StockBaseYearInfo(int id, String stockFullId,float gongJiJin,float liRun,String yuGaoContent,
			String piLouDate) {
		this.id = id;
		this.stockFullId = stockFullId;
		this.gongJiJin= gongJiJin;		
		this.liRun = liRun;
		this.yuGaoContent =yuGaoContent;
		this.piLouDate =piLouDate;		
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
	public float getGongJiJin() {
		return gongJiJin;
	}
	public void setGongJiJin(float gongJiJin) {
		this.gongJiJin = gongJiJin;
	}
	public float getLiRun() {
		return liRun;
	}
	public void setLiRun(float liRun) {
		this.liRun = liRun;
	}
	public String getYuGaoContent() {
		return yuGaoContent;
	}
	public void setYuGaoContent(String yuGaoContent) {
		this.yuGaoContent = yuGaoContent;
	}
	public String getPiLouDate() {
		return piLouDate;
	}
	public void setPiLouDate(String piLouDate) {
		this.piLouDate = piLouDate;
	}
	
	

}
