package dao;

import java.sql.Date;

public class StockData {
	
	private Date date;
	private float openingPrice;
	private float closingPrice;
	private float highestPrice;
	private float lowestPrice;
	private long stockVolume;//成交量
	private double dailyTurnover;//成交额
	private float ma5Price;
	private float ma10Price;
	private int dataType;
	private float range;
	private float amplitude;	
	
	public StockData()
	{
		
	}
	public StockData(Date date, float openingPrice, float highestPrice, float lowestPrice,float closingPrice,long stockVolume,double dailyTurnover,float ma5Price,float ma10Price,int dataType,float range,float amplitude)
	{
		super();
		this.date = date;
		this.openingPrice = openingPrice;		
		this.highestPrice = highestPrice;
		this.lowestPrice = lowestPrice;
		this.closingPrice = closingPrice;
		this.stockVolume = stockVolume;
		this.dailyTurnover = dailyTurnover;
		this.ma5Price = ma5Price;
		this.ma10Price = ma10Price;
		this.dataType = dataType;
		this.range = range;
		this.amplitude = amplitude;
	}
	
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public float getOpeningPrice() {
		return openingPrice;
	}
	public void setOpeningPrice(float openingPrice) {
		this.openingPrice = openingPrice;
	}
	public float getClosingPrice() {
		return closingPrice;
	}
	public void setClosingPrice(float closingPrice) {
		this.closingPrice = closingPrice;
	}
	public float getHighestPrice() {
		return highestPrice;
	}
	public void setHighestPrice(float highestPrice) {
		this.highestPrice = highestPrice;
	}
	public float getLowestPrice() {
		return lowestPrice;
	}
	public void setLowestPrice(float lowestPrice) {
		this.lowestPrice = lowestPrice;
	}
	public long getStockVolume() {
		return stockVolume;
	}
	public void setStockVolume(long stockVolume) {
		this.stockVolume = stockVolume;
	}
	public double getDailyTurnover() {
		return dailyTurnover;
	}
	public void setDailyTurnover(double dailyTurnover) {
		this.dailyTurnover = dailyTurnover;
	}
	public float getMa5Price() {
		return ma5Price;
	}
	public void setMa5Price(float ma5Price) {
		this.ma5Price = ma5Price;
	}
	public float getMa10Price() {
		return ma10Price;
	}
	public void setMa10Price(float ma10Price) {
		this.ma10Price = ma10Price;
	}
	public int getDataType() {
		return dataType;
	}
	public void setDataType(int dataType) {
		this.dataType = dataType;
	}
	public float getRange() {
		return range;
	}
	public void setRange(float range) {
		this.range = range;
	}
	public float getAmplitude() {
		return amplitude;
	}
	public void setAmplitude(float amplitude) {
		this.amplitude = amplitude;
	}

}
