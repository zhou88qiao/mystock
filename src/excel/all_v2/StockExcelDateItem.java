package excel.all_v2;

public class StockExcelDateItem {
	
	private String curDate;//当前时间
	private float curValue;//当前点位
	
	private String startDate;//开始时间
	private float startValue;//开始点位
	private int  startDateGap;//与上一个极点时间差
	private String endDate; //结束时间
	private float endValue;//结束点位
	private int  endDateGap;//与最近极点时间差
	
	public StockExcelDateItem(){
		
	}
	
	
	
	public String getCurDate() {
		return curDate;
	}
	public void setCurDate(String curDate) {
		this.curDate = curDate;
	}
	public float getCurValue() {
		return curValue;
	}
	public void setCurValue(float curValue) {
		this.curValue = curValue;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public float getStartValue() {
		return startValue;
	}
	public void setStartValue(float startValue) {
		this.startValue = startValue;
	}
	public int getStartDateGap() {
		return startDateGap;
	}
	public void setStartDateGap(int startDateGap) {
		this.startDateGap = startDateGap;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public float getEndValue() {
		return endValue;
	}
	public void setEndValue(float endValue) {
		this.endValue = endValue;
	}
	public int getEndDateGap() {
		return endDateGap;
	}
	public void setEndDateGap(int endDateGap) {
		this.endDateGap = endDateGap;
	}

}
