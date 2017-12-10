package stock.export;

//针对日、周、月每种的统计中单独包含的数据，不需要混合计算的
public class StockStatValue {
	
	private int tread;//当前 上涨1 下跌0 
	private float range;//当天涨幅
	private String priUpDateGap;//前一涨停时间差 和时间
	private int upOrdownFlag;//涨跌停标记，1涨停 0跌停
	private int upOrdownTimes;//涨跌停个数	
	
	private int  pointSuspectedDateGap;//极点与疑似极点时间差
	private int  suspectedCurDateGap;//疑似极点与当前时间差
	private int buySaleWarn; //买卖提示
	
	public StockStatValue(){
		
	}
	
	public StockStatValue(int tread,float range,String priUpDateGap, int upOrdownFlag,int upOrdownTimes,
			int pointSuspectedDateGap,int suspectedCurDateGap,int buySaleWarn){
		this.tread = tread;
		this.range = range;
		this.priUpDateGap = priUpDateGap;
		this.upOrdownFlag = upOrdownFlag;
		this.upOrdownTimes = upOrdownTimes;
		this.pointSuspectedDateGap = pointSuspectedDateGap;
		this.suspectedCurDateGap = suspectedCurDateGap;
		this.buySaleWarn = buySaleWarn;
	}
	
	

	public int getTread() {
		return tread;
	}

	public void setTread(int tread) {
		this.tread = tread;
	}

	public int getUpOrdownTimes() {
		return upOrdownTimes;
	}

	public void setUpOrdownTimes(int upOrdownTimes) {
		this.upOrdownTimes = upOrdownTimes;
	}

	public int getUpOrdownFlag() {
		return upOrdownFlag;
	}

	public void setUpOrdownFlag(int upOrdownFlag) {
		this.upOrdownFlag = upOrdownFlag;
	}

	public float getRange() {
		return range;
	}

	public void setRange(float range) {
		this.range = range;
	}

	public String getPriUpDateGap() {
		return priUpDateGap;
	}

	public void setPriUpDateGap(String priUpDateGap) {
		this.priUpDateGap = priUpDateGap;
	}

	public int getPointSuspectedDateGap() {
		return pointSuspectedDateGap;
	}

	public void setPointSuspectedDateGap(int pointSuspectedDateGap) {
		this.pointSuspectedDateGap = pointSuspectedDateGap;
	}

	public int getSuspectedCurDateGap() {
		return suspectedCurDateGap;
	}

	public void setSuspectedCurDateGap(int suspectedCurDateGap) {
		this.suspectedCurDateGap = suspectedCurDateGap;
	}

	public int getBuySaleWarn() {
		return buySaleWarn;
	}

	public void setBuySaleWarn(int buySaleWarn) {
		this.buySaleWarn = buySaleWarn;
	}
	
}
