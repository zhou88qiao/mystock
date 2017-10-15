package excel.all_v2;

import java.sql.Date;

public class ExcelItem {
	private String fullId;
	private float ratioHistory;//历史涨幅比
	private int upTimes; //上转次数
	private int downTimes; //下转次数
	private int willTure; //是否存在转趋势 0是无 ，1是已经出现
	private int pointTimes; //历史数据出现极点次数
	private float ratioCur;//当前涨幅比
	private float likelyPrice;//疑似极点日期所在最高或最低点位
	private String likelyDate;//疑似极点日期
	private String crossLastDate; //前一个交叉点日期
	private int type; //上升过程or下跌过程 0:下跌 1:上升

	public ExcelItem(String fullId,float ratioHistory,int downTimes,int upTimes,int willTure, int pointTimes, float ratioCur, float likelyPrice, String likelyDate,String crossLastDate,int type )
	{
		this.fullId= fullId;
		this.ratioHistory= ratioHistory;
		this.pointTimes = pointTimes;
		this.crossLastDate = crossLastDate;
		this.type = type;
		this.likelyDate = likelyDate;
		this.likelyPrice = likelyPrice;
		this.upTimes = upTimes;
		this.downTimes = downTimes;
		this.willTure = willTure;
		this.ratioCur = ratioCur;
	}

	public String getFullId() {
		return fullId;
	}

	public void setFullId(String fullId) {
		this.fullId = fullId;
	}

	public int getPointTimes() {
		return pointTimes;
	}

	public void setPointTimes(int pointTimes) {
		this.pointTimes = pointTimes;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getLikelyDate() {
		return likelyDate;
	}

	public void setLikelyDate(String likelyDate) {
		this.likelyDate = likelyDate;
	}

	public float getLikelyPrice() {
		return likelyPrice;
	}

	public void setLikelyPrice(float likelyPrice) {
		this.likelyPrice = likelyPrice;
	}

	public int getUpTimes() {
		return upTimes;
	}

	public void setUpTimes(int upTimes) {
		this.upTimes = upTimes;
	}

	public int getDownTimes() {
		return downTimes;
	}

	public void setDownTimes(int downTimes) {
		this.downTimes = downTimes;
	}

	public int getWillTure() {
		return willTure;
	}

	public void setWillTure(int willTure) {
		this.willTure = willTure;
	}

	public String getCrossLastDate() {
		return crossLastDate;
	}

	public void setCrossLastDate(String crossLastDate) {
		this.crossLastDate = crossLastDate;
	}

	public float getRatioHistory() {
		return ratioHistory;
	}

	public void setRatioHistory(float ratioHistory) {
		this.ratioHistory = ratioHistory;
	}

	public float getRatioCur() {
		return ratioCur;
	}

	public void setRatioCur(float ratioCur) {
		this.ratioCur = ratioCur;
	}
	
	
	

}
