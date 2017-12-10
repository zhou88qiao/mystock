package stock.export;

//统计项
public class StockExcelStatItem {
	private String fullId;
	private StockMinMaxValue minMaxItem;//最小最大点位
	private StockStatValue dayStatItem; //日相关
	private StockMixStatValue dayMixStatItem; //日混合
	private StockStatValue weekStatItem; //周相关
	private StockMixStatValue weekMixStatItem; //周混合
	private StockStatValue monthStatItem; //月相关
	
	/*
	private int dayFlag;//上涨1 下跌0  编号
	private int weekFlag;//上涨1 下跌0  编号
	private int monthFlag;//上涨1 下跌0  编号
	
	private int upOrDownFlag; //当前涨停1或跌停0标记
	private int dayUpOrDownTimes;//涨跌停次数
	private int weekUpOrDownTimes;//涨跌停次数
	private int monthUpOrDownTimes;//涨跌停次数
	*/
	
	public StockExcelStatItem( StockMinMaxValue minMaxItem, StockStatValue dayStatItem,StockMixStatValue dayMixStatItem,
			StockStatValue weekStatItem,StockMixStatValue weekMixStatItem,
			StockStatValue monthStatItem)
	{
		this.minMaxItem = minMaxItem;
		this.dayStatItem = dayStatItem;
		this.dayMixStatItem = dayMixStatItem;
		this.weekStatItem = weekStatItem;		
		this.weekMixStatItem = weekMixStatItem;
		this.monthStatItem = monthStatItem;
	}
	
	/*
	public StockExcelStatItem(int dayFlag,int weekFlag,int monthFlag,int upOrDownFlag,int dayUpOrDownTimes,int weekUpOrDownTimes,int monthUpOrDownTimes)
	{
		this.dayFlag = dayFlag;
		this.weekFlag = weekFlag;
		this.monthFlag = monthFlag;		
		this.upOrDownFlag = upOrDownFlag;
		this.dayUpOrDownTimes = dayUpOrDownTimes;
		this.weekUpOrDownTimes = weekUpOrDownTimes;
		this.monthUpOrDownTimes = monthUpOrDownTimes;
	}
	public String getFullId() {
		return fullId;
	}
	public void setFullId(String fullId) {
		this.fullId = fullId;
	}
	public int getDayFlag() {
		return dayFlag;
	}
	public void setDayFlag(int dayFlag) {
		this.dayFlag = dayFlag;
	}
	public int getWeekFlag() {
		return weekFlag;
	}
	public void setWeekFlag(int weekFlag) {
		this.weekFlag = weekFlag;
	}
	public int getMonthFlag() {
		return monthFlag;
	}
	public void setMonthFlag(int monthFlag) {
		this.monthFlag = monthFlag;
	}
	public int getDayUpOrDownTimes() {
		return dayUpOrDownTimes;
	}
	public void setDayUpOrDownTimes(int dayUpOrDownTimes) {
		this.dayUpOrDownTimes = dayUpOrDownTimes;
	}
	public int getWeekUpOrDownTimes() {
		return weekUpOrDownTimes;
	}
	public void setWeekUpOrDownTimes(int weekUpOrDownTimes) {
		this.weekUpOrDownTimes = weekUpOrDownTimes;
	}
	public int getMonthUpOrDownTimes() {
		return monthUpOrDownTimes;
	}
	public void setMonthUpOrDownTimes(int monthUpOrDownTimes) {
		this.monthUpOrDownTimes = monthUpOrDownTimes;
	}
	
	public int getUpOrDownFlag() {
		return upOrDownFlag;
	}
	public void setUpOrDownFlag(int upOrDownFlag) {
		this.upOrDownFlag = upOrDownFlag;
	}
	*/
	public StockStatValue getDayStatItem() {
		return dayStatItem;
	}

	public void setDayStatItem(StockStatValue dayStatItem) {
		this.dayStatItem = dayStatItem;
	}

	public StockMixStatValue getDayMixStatItem() {
		return dayMixStatItem;
	}

	public void setDayMixStatItem(StockMixStatValue dayMixStatItem) {
		this.dayMixStatItem = dayMixStatItem;
	}

	public StockStatValue getWeekStatItem() {
		return weekStatItem;
	}

	public void setWeekStatItem(StockStatValue weekStatItem) {
		this.weekStatItem = weekStatItem;
	}

	public StockMixStatValue getWeekMixStatItem() {
		return weekMixStatItem;
	}

	public void setWeekMixStatItem(StockMixStatValue weekMixStatItem) {
		this.weekMixStatItem = weekMixStatItem;
	}

	public StockStatValue getMonthStatItem() {
		return monthStatItem;
	}

	public void setMonthStatItem(StockStatValue monthStatItem) {
		this.monthStatItem = monthStatItem;
	}

	public StockMinMaxValue getMinMaxItem() {
		return minMaxItem;
	}

	public void setMinMaxItem(StockMinMaxValue minMaxItem) {
		this.minMaxItem = minMaxItem;
	}
	

	
}
