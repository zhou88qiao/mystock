package dao;

import java.sql.Date;
import java.sql.Time;

public class StockInformation {
	
	private String stockId;
	private String stockFullId;
	private String stockName;
	private String stockClassification;
	private String stockIndustry;
	private String stockConcept;
	private String stockRegional;
	
	public StockInformation()
	{
		
	}
	public StockInformation(String stockId, String stockFullId,String stockName, String stockClassification,
			String stockIndustry, String stockConcept, String stockRegional)
	{
		this.stockId=stockId;
		this.stockFullId=stockFullId;
		this.stockName=stockName;
		this.stockClassification=stockClassification;
		this.stockIndustry=stockIndustry;
		this.stockConcept=stockConcept;
		this.stockRegional=stockRegional;
		
	}
	
	public String getStockId() {
		return stockId;
	}

	public void setStockId(String stockId) {
		this.stockId = stockId;
	}

	public String getStockName() {
		return stockName;
	}

	public void setStockName(String stockName) {
		this.stockName = stockName;
	}

	public String getStockClassification() {
		return stockClassification;
	}

	public void setStockClassification(String stockClassification) {
		this.stockClassification = stockClassification;
	}

	public String getStockIndustry() {
		return stockIndustry;
	}

	public void setStockIndustry(String stockIndustry) {
		this.stockIndustry = stockIndustry;
	}

	public String getStockConcept() {
		return stockConcept;
	}

	public void setStockConcept(String stockConcept) {
		this.stockConcept = stockConcept;
	}

	public String getStockRegional() {
		return stockRegional;
	}

	public void setStockRegional(String stockRegional) {
		this.stockRegional = stockRegional;
	}

	public String getStockFullId() {
		return stockFullId;
	}
	public void setStockFullId(String stockFullId) {
		this.stockFullId = stockFullId;
	}
	
}
