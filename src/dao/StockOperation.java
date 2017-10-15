package dao;

import java.sql.Date;

public class StockOperation {
	
	private int id;
	private String fullId;
	private int assId;//关联id  //用买点状态1,2代替
	private String opDate;
	private float buyValue;
	private float stopValue;
	private float saleValue;
	private float earnRatio; 
	private float stopRatio;
	private float lossRatio;
	private int opType; //操作类型 买 卖 止
	private int dateType;//操作时间类型 天周月
	
	public StockOperation(){  
		  
	}

	public StockOperation(String fullId,int assId,String opDate,float buyValue,float stopValue,float saleValue,
			float earnRatio,float stopRatio,float lossRatio,int opType, int dateType){ 
		this.fullId = fullId;
		this.assId = assId;
		this.opDate = opDate;
		this.buyValue = buyValue;
		this.stopValue = stopValue;
		this.saleValue = saleValue;
		this.earnRatio = earnRatio;
		this.stopRatio = stopRatio;
		this.lossRatio = lossRatio;
		this.opType = opType;
		this.dateType = dateType;
	 
	}
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFullId() {
		return fullId;
	}

	public void setFullId(String fullId) {
		this.fullId = fullId;
	}



	public String getOpDate() {
		return opDate;
	}

	public void setOpDate(String opDate) {
		this.opDate = opDate;
	}

	public float getBuyValue() {
		return buyValue;
	}

	public void setBuyValue(float buyValue) {
		this.buyValue = buyValue;
	}

	public float getStopValue() {
		return stopValue;
	}

	public void setStopValue(float stopValue) {
		this.stopValue = stopValue;
	}

	public float getSaleValue() {
		return saleValue;
	}

	public void setSaleValue(float saleValue) {
		this.saleValue = saleValue;
	}

	public float getEarnRatio() {
		return earnRatio;
	}

	public void setEarnRatio(float earnRatio) {
		this.earnRatio = earnRatio;
	}

	public float getStopRatio() {
		return stopRatio;
	}

	public void setStopRatio(float stopRatio) {
		this.stopRatio = stopRatio;
	}

	public float getLossRatio() {
		return lossRatio;
	}

	public void setLossRatio(float lossRatio) {
		this.lossRatio = lossRatio;
	}


	public int getOpType() {
		return opType;
	}

	public void setOpType(int opType) {
		this.opType = opType;
	}

	public int getDateType() {
		return dateType;
	}

	public void setDateType(int dateType) {
		this.dateType = dateType;
	}

	public int getAssId() {
		return assId;
	}

	public void setAssId(int assId) {
		this.assId = assId;
	} 
	

}
