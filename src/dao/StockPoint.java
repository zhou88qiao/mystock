package dao;

import java.sql.Date;

public class StockPoint {
	private  int id;
	private int type;
	private Date extremeDate;
	private float extremePrice;
	private Date fromDate;
	private Date toDate;
	private int willFlag; //0 rise 1 false
	private float ratio;
	private float expectation;
	
	
	public StockPoint(){  
		  
	}  
	
	public StockPoint(int id,int type,Date extremeDate, float extremePrice,Date fromDate,Date toDate,int willFlag,float ratio,float expectation) {
		super();
		this.id=id;
		this.type=type;
		this.extremeDate = extremeDate;
		this.extremePrice = extremePrice;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.willFlag = willFlag;
		this.ratio = ratio;
		this.expectation = expectation;
	}

	public Date getExtremeDate() {
		return extremeDate;
	}

	public void setExtremeDate(Date extremeDate) {
		this.extremeDate = extremeDate;
	}

	public float getExtremePrice() {
		return extremePrice;
	}

	public void setExtremePrice(float extremePrice) {
		this.extremePrice = extremePrice;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public int getWillFlag() {
		return willFlag;
	}

	public void setWillFlag(int willFlag) {
		this.willFlag = willFlag;
	}

	public float getRatio() {
		return ratio;
	}

	public void setRatio(float ratio) {
		this.ratio = ratio;
	}

	public float getExpectation() {
		return expectation;
	}

	public void setExpectation(float expectation) {
		this.expectation = expectation;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}



	

}
