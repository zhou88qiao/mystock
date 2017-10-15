package com.timer.stock;

import java.util.List;

public class TimeStock {	
	
	private String year;
	private int seasonNum;//4, 1-4
	private int monthNum;//12, 1-12
	private int weekNum;//52，代表从0-52，实际有53周
	private int dayNum; //
	
	public TimeStock()
	{
		
	}
	
	public TimeStock(String year,int seasonNum,int monthNum,int weekNum,int dayNum)
	{
		this.year=year;
		this.seasonNum=seasonNum;
		this.monthNum=monthNum;
		this.weekNum=weekNum;
		this.dayNum=dayNum;		
	}
	
	
	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public int getSeasonNum() {
		return seasonNum;
	}

	public void setSeasonNum(int seasonNum) {
		this.seasonNum = seasonNum;
	}

	public int getMonthNum() {
		return monthNum;
	}

	public void setMonthNum(int monthNum) {
		this.monthNum = monthNum;
	}

	public int getWeekNum() {
		return weekNum;
	}

	public void setWeekNum(int weekNum) {
		this.weekNum = weekNum;
	}

	public int getDayNum() {
		return dayNum;
	}

	public void setDayNum(int dayNum) {
		this.dayNum = dayNum;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
