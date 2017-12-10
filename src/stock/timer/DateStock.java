package stock.timer;

import java.util.List;

public class DateStock {
	
	private List<String> dayDate;
	private List<String> weekDate;
	private List<String> monthDate;
	private List<String> seasonDate;
	private List<String> yearDate;
	
	public DateStock()
	{
		
	}
	
	public  DateStock(List<String> dayDate,List<String> weekDate,List<String> monthDate,List<String> seasonDate,List<String> yearDate)
	{
		this.dayDate=dayDate;
		this.weekDate=weekDate;
		this.monthDate=monthDate;
		this.seasonDate=seasonDate;
		this.yearDate=yearDate;
	}
	
	
	
	public List<String> getDayDate() {
		return dayDate;
	}

	public void setDayDate(List<String> dayDate) {
		this.dayDate = dayDate;
	}

	public List<String> getWeekDate() {
		return weekDate;
	}

	public void setWeekDate(List<String> weekDate) {
		this.weekDate = weekDate;
	}

	public List<String> getMonthDate() {
		return monthDate;
	}

	public void setMonthDate(List<String> monthDate) {
		this.monthDate = monthDate;
	}

	public List<String> getSeasonDate() {
		return seasonDate;
	}

	public void setSeasonDate(List<String> seasonDate) {
		this.seasonDate = seasonDate;
	}

	public List<String> getYearDate() {
		return yearDate;
	}

	public void setYearDate(List<String> yearDate) {
		this.yearDate = yearDate;
	}

}
