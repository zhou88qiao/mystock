package excel.all_v2;

public class StockCurValue {
	
	private String priDate; //前高时间
	private float priHighOrLowest; //前高或前低极点（倒数第二个极点）
	private float reversalRegion;//反转区间	
	private String startDate;//开始时间 极点时间（前一个极点）
	private float startValue;//开始点位	
	private String endDate; //结束时间  疑似极点
	private float endValue;//结束点位	
	private String curDate;//当前时间
	private float curValue;//开始点位	
	private float workRegion;//运行区间 6个，0 0.385 1 0.5
	
	private float bugValue;//买
	private float winValue;//赢
	private float loseVaule;//亏
	
	private int dealWarn;//交易提示 0 观望 1卖出 2观注 3买入 
	private int curState;//当前状态 -1为调整，0为下跌 1为上涨
	private int tread;//当前趋势	0跌 1涨
	
	//后半部分
	private int  pointSuspectedDateGap;//极点与疑似极点时间差
	private float pointSuspectedValueGap;//极点与疑似极点位差
	private int  pointCurDateGap;//上一个极点与当前时间差
	private float  pointCurValueGap;//上一个极点与当前点位差
	private int  suspectedCurDateGap;//疑似极点与当前时间差
	private float suspectedCurValueGap;//疑似极点与当前点位差	
	
	private int  marketPSDateGap;//与大盘极疑时间差
	private float  marketPSSpace;//空间	
	private int  marketPCDateGap;////与大盘极当时间差
	private float  marketPCSpace;//空间
	private int  marketSCDateGap;////与大盘疑当时间差
	private float  marketSCSpace;////空间		
	private int trendConsistent; //与大盘趋势是否一致 0 一致，1不一致
	
	private String minDate; //2015年最低最高点位
	private float minPrice;
	private String maxDate;
	private float maxPrice;
	private float minMaxRatio; //最低最高点比

	public StockCurValue(String priDate,float priHighOrLowest,float reversalRegion, String startDate,  float startValue,String endDate, float endValue,
			String curDate,float curValue,float workRegion,
			float bugValue,float winValue,float loseVaule,int dealWarn,int curState,int tread,
			int pointSuspectedDateGap,float pointSuspectedValueGap, int pointCurDateGap, float pointCurValueGap, 
			int suspectedCurDateGap,float suspectedCurValueGap,
			int marketPSDateGap, float marketPSSpace,int marketPCDateGap, float marketPCSpace,int marketSCDateGap, float marketSCSpace,
			int trendConsistent,String minDate,  float minPrice,  String maxDate, float maxPrice, float minMaxRatio ) {
		
		this.priDate = priDate;
		this.priHighOrLowest = priHighOrLowest;
		this.reversalRegion = reversalRegion;
		this.startDate = startDate;
		this.startValue = startValue;
		this.endDate = endDate;		
		this.endValue = endValue;		
		this.curDate = curDate;
		this.curValue = curValue;
		this.workRegion = workRegion;
		
		this.bugValue = bugValue;
		this.winValue = winValue;
		this.loseVaule = loseVaule;		
		this.dealWarn = dealWarn;
		this.curState = curState;
		this.tread = tread;
		
		this.pointSuspectedDateGap = pointSuspectedDateGap;
		this.pointSuspectedValueGap = pointSuspectedValueGap;
		this.pointCurDateGap = pointCurDateGap;
		this.pointCurValueGap = pointCurValueGap;
		this.suspectedCurDateGap = suspectedCurDateGap;
		this.suspectedCurValueGap = suspectedCurValueGap;
		
		this.marketPSDateGap = marketPSDateGap;
		this.marketPSSpace = marketPSSpace;
		this.marketPCDateGap = marketPCDateGap;
		this.marketPCSpace = marketPCSpace;
		this.marketSCDateGap = marketSCDateGap;
		this.marketSCSpace = marketSCSpace;		
		
		this.trendConsistent = trendConsistent;
		
		this.minDate = minDate;
		this.minPrice = minPrice;
		this.maxDate =maxDate;
		this.maxPrice = maxPrice;
		this.minMaxRatio = minMaxRatio;
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




	public int getPointSuspectedDateGap() {
		return pointSuspectedDateGap;
	}

	public void setPointSuspectedDateGap(int pointSuspectedDateGap) {
		this.pointSuspectedDateGap = pointSuspectedDateGap;
	}

	public float getPointSuspectedValueGap() {
		return pointSuspectedValueGap;
	}

	public void setPointSuspectedValueGap(float pointSuspectedValueGap) {
		this.pointSuspectedValueGap = pointSuspectedValueGap;
	}

	public int getPointCurDateGap() {
		return pointCurDateGap;
	}

	public void setPointCurDateGap(int pointCurDateGap) {
		this.pointCurDateGap = pointCurDateGap;
	}

	public float getPointCurValueGap() {
		return pointCurValueGap;
	}

	public void setPointCurValueGap(float pointCurValueGap) {
		this.pointCurValueGap = pointCurValueGap;
	}

	public int getSuspectedCurDateGap() {
		return suspectedCurDateGap;
	}

	public void setSuspectedCurDateGap(int suspectedCurDateGap) {
		this.suspectedCurDateGap = suspectedCurDateGap;
	}

	public float getSuspectedCurValueGap() {
		return suspectedCurValueGap;
	}

	public void setSuspectedCurValueGap(float suspectedCurValueGap) {
		this.suspectedCurValueGap = suspectedCurValueGap;
	}

	

	public int getTread() {
		return tread;
	}

	public void setTread(int tread) {
		this.tread = tread;
	}

	public int getTrendConsistent() {
		return trendConsistent;
	}

	public void setTrendConsistent(int trendConsistent) {
		this.trendConsistent = trendConsistent;
	}

	public float getBugValue() {
		return bugValue;
	}

	public void setBugValue(float bugValue) {
		this.bugValue = bugValue;
	}

	public float getWinValue() {
		return winValue;
	}

	public void setWinValue(float winValue) {
		this.winValue = winValue;
	}

	public float getLoseVaule() {
		return loseVaule;
	}

	public void setLoseVaule(float loseVaule) {
		this.loseVaule = loseVaule;
	}

	public int getDealWarn() {
		return dealWarn;
	}

	public void setDealWarn(int dealWarn) {
		this.dealWarn = dealWarn;
	}

	public int getCurState() {
		return curState;
	}

	public void setCurState(int curState) {
		this.curState = curState;
	}

	public float getWorkRegion() {
		return workRegion;
	}

	public void setWorkRegion(float workRegion) {
		this.workRegion = workRegion;
	}

	public float getPriHighOrLowest() {
		return priHighOrLowest;
	}

	public void setPriHighOrLowest(float priHighOrLowest) {
		this.priHighOrLowest = priHighOrLowest;
	}

	public float getReversalRegion() {
		return reversalRegion;
	}

	public void setReversalRegion(float reversalRegion) {
		this.reversalRegion = reversalRegion;
	}

	public String getPriDate() {
		return priDate;
	}

	public void setPriDate(String priDate) {
		this.priDate = priDate;
	}

	public int getMarketPSDateGap() {
		return marketPSDateGap;
	}

	public void setMarketPSDateGap(int marketPSDateGap) {
		this.marketPSDateGap = marketPSDateGap;
	}

	public float getMarketPSSpace() {
		return marketPSSpace;
	}

	public void setMarketPSSpace(float marketPSSpace) {
		this.marketPSSpace = marketPSSpace;
	}

	public int getMarketPCDateGap() {
		return marketPCDateGap;
	}

	public void setMarketPCDateGap(int marketPCDateGap) {
		this.marketPCDateGap = marketPCDateGap;
	}

	public float getMarketPCSpace() {
		return marketPCSpace;
	}

	public void setMarketPCSpace(float marketPCSpace) {
		this.marketPCSpace = marketPCSpace;
	}

	public int getMarketSCDateGap() {
		return marketSCDateGap;
	}

	public void setMarketSCDateGap(int marketSCDateGap) {
		this.marketSCDateGap = marketSCDateGap;
	}

	public float getMarketSCSpace() {
		return marketSCSpace;
	}

	public void setMarketSCSpace(float marketSCSpace) {
		this.marketSCSpace = marketSCSpace;
	}
	
	public String getMinDate() {
		return minDate;
	}

	public void setMinDate(String minDate) {
		this.minDate = minDate;
	}

	public float getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(float minPrice) {
		this.minPrice = minPrice;
	}

	public String getMaxDate() {
		return maxDate;
	}

	public void setMaxDate(String maxDate) {
		this.maxDate = maxDate;
	}

	public float getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(float maxPrice) {
		this.maxPrice = maxPrice;
	}

	public float getMinMaxRatio() {
		return minMaxRatio;
	}

	public void setMinMaxRatio(float minMaxRatio) {
		this.minMaxRatio = minMaxRatio;
	}

}
