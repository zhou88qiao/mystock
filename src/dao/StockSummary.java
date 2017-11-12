package dao;

import java.sql.Date;

import excel.all_v2.StockExcelTotalInfo;

public class StockSummary implements Comparable {
	private  int id;
	private String fullId;
	private String name;
	private String douRong;
	private String baseExpect;
	private String main;
	private String psychology;
	private String risk;
	private String potential;        
	private String faucet;
	private String curRange;
	
	private String priComState; //前极点状态组合
	private String priState; //前极点组合

	private String comState;
	private String psState;
	
	private String daySaleGrade;
	private String daySaleState;
	private String dayPriUpDateGap;
	private String dayUpOrdownDates;
	private String dayWarnDeal;
	private String dayPS;
	private String daySC;
	private String weekSaleGrade;
	private String weekSaleState;
	private String weekUpOrdownDates;
	private String weekWarnDeal;		
	private String weekPS;
	private String weekSC;		
	private String monthUpOrdownDates;
	private String monthWarnDeal;	
	private String monthPS;
	private String monthSC;

	private String dayPriDate;
	private String dayPriHighOrLowest;
	private String dayReversalRegion;
	private String dayStartDate;//极点
	private String dayStartValue;
	private String dayEndDate;//疑极
	private String dayEndValue;		
	private String dayCurDate;
	private String dayCurValue;
	private String dayWorkRegion;


	private String dayPSDateGap;
	private String dayPSValueGap;
	private String dayPCDateGap;
	private String dayPCValueGap;
	private String daySCDateGap;
	private String daySCValueGap;
	
	private String  dayMarketPSDateGap;//与大盘极疑时间差
	private String  dayMarketPSSpace;//空间	
	private String  dayMarketPCDateGap;////与大盘极当时间差
	private String  dayMarketPCSpace;//空间
	private String  dayMarketSCDateGap;////与大盘疑当时间差
	private String  dayMarketSCSpace;////空间	
	private String  dayTrendConsistent;

	private String dayDesireValue1;
	private String dayDesireRange1;
	private String dayDesireRate1;
	private String dayDesireValue2;
	private String dayDesireRange2;
	private String dayDesireRate2;
	private String dayDesireValue3;
	private String dayDesireRange3;
	private String dayDesireRate3;
	private String dayDesireValue4;
	private String dayDesireRange4;
	private String dayDesireRate4;
	private String dayDesireValue5;
	private String dayDesireRange5;
	private String dayDesireRate5;
	private String dayDesireValue6;
	private String dayDesireRange6;
	private String dayDesireRate6;

	private String dayDesireValue1Gap;
	private String dayDesireValue2Gap;
	private String dayDesireValue3Gap;
	private String dayDesireValue4Gap;
	private String dayDesireValue5Gap;
	private String dayDesireValue6Gap;

	private String dayBugValue;
	private String dayWinValue;
	private String dayLoseValue;
	private String dayDealWarn;
	private String dayOption;
	
	private String weekPriDate;
	private String weekPriHighOrLowest;
	private String weekReversalRegion;
	private String weekStartDate;
	private String weekStartValue;
	private String weekEndDate;
	private String weekEndValue;		
	private String weekCurDate;
	private String weekCurValue;
	private String weekWorkRegion;

	private String weekPSDateGap;
	private String weekPSValueGap;
	private String weekPCDateGap;
	private String weekPCValueGap;
	private String weekSCDateGap;
	private String weekSCValueGap;
	private String  weekMarketPSDateGap;//与大盘极疑时间差
	private String  weekMarketPSSpace;//空间	
	private String  weekMarketPCDateGap;////与大盘极当时间差
	private String  weekMarketPCSpace;//空间
	private String  weekMarketSCDateGap;////与大盘疑当时间差
	private String  weekMarketSCSpace;////空间
	private String weekTrendConsistent;
	
	private String weekDesireValue1;
	private String weekDesireRange1;
	private String weekDesireRate1;
	private String weekDesireValue2;
	private String weekDesireRange2;
	private String weekDesireRate2;
	private String weekDesireValue3;
	private String weekDesireRange3;
	private String weekDesireRate3;
	private String weekDesireValue4;
	private String weekDesireRange4;
	private String weekDesireRate4;
	private String weekDesireValue5;
	private String weekDesireRange5;
	private String weekDesireRate5;
	private String weekDesireValue6;
	private String weekDesireRange6;
	private String weekDesireRate6;

	private String weekDesireValue1Gap;
	private String weekDesireValue2Gap;
	private String weekDesireValue3Gap;
	private String weekDesireValue4Gap;
	private String weekDesireValue5Gap;
	private String weekDesireValue6Gap;

	private String weekBugValue;
	private String weekWinValue;
	private String weekLoseValue;
	private String weekDealWarn;
	private String weekOption;

	private String monthPriDate;
	private String monthPriHighOrLowest;
	private String monthReversalRegion;
	private String monthStartDate;
	private String monthStartValue;
	private String monthEndDate;
	private String monthEndValue;		
	private String monthCurDate;
	private String monthCurValue;
	private String monthWorkRegion;

	private String monthPSDateGap;
	private String monthPSValueGap;
	private String monthPCDateGap;
	private String monthPCValueGap;
	private String monthSCDateGap;
	private String monthSCValueGap;
	private String  monthMarketPSDateGap;//与大盘极疑时间差
	private String  monthMarketPSSpace;//空间	
	private String  monthMarketPCDateGap;////与大盘极当时间差
	private String  monthMarketPCSpace;//空间
	private String  monthMarketSCDateGap;////与大盘疑当时间差
	private String  monthMarketSCSpace;////空间
	private String monthTrendConsistent;

	private String monthDesireValue1;
	private String monthDesireRange1;
	private String monthDesireRate1;
	private String monthDesireValue2;
	private String monthDesireRange2;
	private String monthDesireRate2;
	private String monthDesireValue3;
	private String monthDesireRange3;
	private String monthDesireRate3;
	private String monthDesireValue4;
	private String monthDesireRange4;
	private String monthDesireRate4;
	private String monthDesireValue5;
	private String monthDesireRange5;
	private String monthDesireRate5;
	private String monthDesireValue6;
	private String monthDesireRange6;
	private String monthDesireRate6;


	private String monthDesireValue1Gap;
	private String monthDesireValue2Gap;
	private String monthDesireValue3Gap;
	private String monthDesireValue4Gap;
	private String monthDesireValue5Gap;
	private String monthDesireValue6Gap;

	private String monthBugValue;
	private String monthWinValue;
	private String monthLoseValue;
	private String monthDealWarn;
	private String monthOption;
	
	
	public StockSummary(int id, String fullId, String name, String douRong,
			String baseExpect, String main, String psychology, String risk,
			String potential, String faucet, String curRange,
			String priComState, String priState, String comState,
			String psState, String daySaleGrade, String daySaleState,
			String dayPriUpDateGap, String dayUpOrdownDates,
			String dayWarnDeal, String dayPS, String daySC,
			String weekSaleGrade, String weekSaleState,
			String weekUpOrdownDates, String weekWarnDeal, String weekPS,
			String weekSC, String monthUpOrdownDates, String monthWarnDeal,
			String monthPS, String monthSC, String dayPriDate,
			String dayPriHighOrLowest, String dayReversalRegion,
			String dayStartDate, String dayStartValue, String dayEndDate,
			String dayEndValue, String dayCurDate, String dayCurValue,
			String dayWorkRegion, String dayPSDateGap, String dayPSValueGap,
			String dayPCDateGap, String dayPCValueGap, String daySCDateGap,
			String daySCValueGap, String dayMarketPSDateGap,
			String dayMarketPSSpace, String dayMarketPCDateGap,
			String dayMarketPCSpace, String dayMarketSCDateGap,
			String dayMarketSCSpace, String dayTrendConsistent,
			String dayDesireValue1, String dayDesireRange1,
			String dayDesireRate1, String dayDesireValue2,
			String dayDesireRange2, String dayDesireRate2,
			String dayDesireValue3, String dayDesireRange3,
			String dayDesireRate3, String dayDesireValue4,
			String dayDesireRange4, String dayDesireRate4,
			String dayDesireValue5, String dayDesireRange5,
			String dayDesireRate5, String dayDesireValue6,
			String dayDesireRange6, String dayDesireRate6,
			String dayDesireValue1Gap, String dayDesireValue2Gap,
			String dayDesireValue3Gap, String dayDesireValue4Gap,
			String dayDesireValue5Gap, String dayDesireValue6Gap,
			String dayBugValue, String dayWinValue, String dayLoseValue,
			String dayDealWarn, String dayOption, String weekPriDate,
			String weekPriHighOrLowest, String weekReversalRegion,
			String weekStartDate, String weekStartValue, String weekEndDate,
			String weekEndValue, String weekCurDate, String weekCurValue,
			String weekWorkRegion, String weekPSDateGap, String weekPSValueGap,
			String weekPCDateGap, String weekPCValueGap, String weekSCDateGap,
			String weekSCValueGap, String weekMarketPSDateGap,
			String weekMarketPSSpace, String weekMarketPCDateGap,
			String weekMarketPCSpace, String weekMarketSCDateGap,
			String weekMarketSCSpace, String weekTrendConsistent,
			String weekDesireValue1, String weekDesireRange1,
			String weekDesireRate1, String weekDesireValue2,
			String weekDesireRange2, String weekDesireRate2,
			String weekDesireValue3, String weekDesireRange3,
			String weekDesireRate3, String weekDesireValue4,
			String weekDesireRange4, String weekDesireRate4,
			String weekDesireValue5, String weekDesireRange5,
			String weekDesireRate5, String weekDesireValue6,
			String weekDesireRange6, String weekDesireRate6,
			String weekDesireValue1Gap, String weekDesireValue2Gap,
			String weekDesireValue3Gap, String weekDesireValue4Gap,
			String weekDesireValue5Gap, String weekDesireValue6Gap,
			String weekBugValue, String weekWinValue, String weekLoseValue,
			String weekDealWarn, String weekOption, String monthPriDate,
			String monthPriHighOrLowest, String monthReversalRegion,
			String monthStartDate, String monthStartValue, String monthEndDate,
			String monthEndValue, String monthCurDate, String monthCurValue,
			String monthWorkRegion, String monthPSDateGap,
			String monthPSValueGap, String monthPCDateGap,
			String monthPCValueGap, String monthSCDateGap,
			String monthSCValueGap, String monthMarketPSDateGap,
			String monthMarketPSSpace, String monthMarketPCDateGap,
			String monthMarketPCSpace, String monthMarketSCDateGap,
			String monthMarketSCSpace, String monthTrendConsistent,
			String monthDesireValue1, String monthDesireRange1,
			String monthDesireRate1, String monthDesireValue2,
			String monthDesireRange2, String monthDesireRate2,
			String monthDesireValue3, String monthDesireRange3,
			String monthDesireRate3, String monthDesireValue4,
			String monthDesireRange4, String monthDesireRate4,
			String monthDesireValue5, String monthDesireRange5,
			String monthDesireRate5, String monthDesireValue6,
			String monthDesireRange6, String monthDesireRate6,
			String monthDesireValue1Gap, String monthDesireValue2Gap,
			String monthDesireValue3Gap, String monthDesireValue4Gap,
			String monthDesireValue5Gap, String monthDesireValue6Gap,
			String monthBugValue, String monthWinValue, String monthLoseValue,
			String monthDealWarn, String monthOption) {
		super();
		this.id = id;
		this.fullId = fullId;
		this.name = name;
		this.douRong = douRong;
		this.baseExpect = baseExpect;
		this.main = main;
		this.psychology = psychology;
		this.risk = risk;
		this.potential = potential;
		this.faucet = faucet;
		this.curRange = curRange;
		this.priComState = priComState;
		this.priState = priState;
		this.comState = comState;
		this.psState = psState;
		this.daySaleGrade = daySaleGrade;
		this.daySaleState = daySaleState;
		this.dayPriUpDateGap = dayPriUpDateGap;
		this.dayUpOrdownDates = dayUpOrdownDates;
		this.dayWarnDeal = dayWarnDeal;
		this.dayPS = dayPS;
		this.daySC = daySC;
		this.weekSaleGrade = weekSaleGrade;
		this.weekSaleState = weekSaleState;
		this.weekUpOrdownDates = weekUpOrdownDates;
		this.weekWarnDeal = weekWarnDeal;
		this.weekPS = weekPS;
		this.weekSC = weekSC;
		this.monthUpOrdownDates = monthUpOrdownDates;
		this.monthWarnDeal = monthWarnDeal;
		this.monthPS = monthPS;
		this.monthSC = monthSC;
		this.dayPriDate = dayPriDate;
		this.dayPriHighOrLowest = dayPriHighOrLowest;
		this.dayReversalRegion = dayReversalRegion;
		this.dayStartDate = dayStartDate;
		this.dayStartValue = dayStartValue;
		this.dayEndDate = dayEndDate;
		this.dayEndValue = dayEndValue;
		this.dayCurDate = dayCurDate;
		this.dayCurValue = dayCurValue;
		this.dayWorkRegion = dayWorkRegion;
		this.dayPSDateGap = dayPSDateGap;
		this.dayPSValueGap = dayPSValueGap;
		this.dayPCDateGap = dayPCDateGap;
		this.dayPCValueGap = dayPCValueGap;
		this.daySCDateGap = daySCDateGap;
		this.daySCValueGap = daySCValueGap;
		this.dayMarketPSDateGap = dayMarketPSDateGap;
		this.dayMarketPSSpace = dayMarketPSSpace;
		this.dayMarketPCDateGap = dayMarketPCDateGap;
		this.dayMarketPCSpace = dayMarketPCSpace;
		this.dayMarketSCDateGap = dayMarketSCDateGap;
		this.dayMarketSCSpace = dayMarketSCSpace;
		this.dayTrendConsistent = dayTrendConsistent;
		this.dayDesireValue1 = dayDesireValue1;
		this.dayDesireRange1 = dayDesireRange1;
		this.dayDesireRate1 = dayDesireRate1;
		this.dayDesireValue2 = dayDesireValue2;
		this.dayDesireRange2 = dayDesireRange2;
		this.dayDesireRate2 = dayDesireRate2;
		this.dayDesireValue3 = dayDesireValue3;
		this.dayDesireRange3 = dayDesireRange3;
		this.dayDesireRate3 = dayDesireRate3;
		this.dayDesireValue4 = dayDesireValue4;
		this.dayDesireRange4 = dayDesireRange4;
		this.dayDesireRate4 = dayDesireRate4;
		this.dayDesireValue5 = dayDesireValue5;
		this.dayDesireRange5 = dayDesireRange5;
		this.dayDesireRate5 = dayDesireRate5;
		this.dayDesireValue6 = dayDesireValue6;
		this.dayDesireRange6 = dayDesireRange6;
		this.dayDesireRate6 = dayDesireRate6;
		this.dayDesireValue1Gap = dayDesireValue1Gap;
		this.dayDesireValue2Gap = dayDesireValue2Gap;
		this.dayDesireValue3Gap = dayDesireValue3Gap;
		this.dayDesireValue4Gap = dayDesireValue4Gap;
		this.dayDesireValue5Gap = dayDesireValue5Gap;
		this.dayDesireValue6Gap = dayDesireValue6Gap;
		this.dayBugValue = dayBugValue;
		this.dayWinValue = dayWinValue;
		this.dayLoseValue = dayLoseValue;
		this.dayDealWarn = dayDealWarn;
		this.dayOption = dayOption;
		this.weekPriDate = weekPriDate;
		this.weekPriHighOrLowest = weekPriHighOrLowest;
		this.weekReversalRegion = weekReversalRegion;
		this.weekStartDate = weekStartDate;
		this.weekStartValue = weekStartValue;
		this.weekEndDate = weekEndDate;
		this.weekEndValue = weekEndValue;
		this.weekCurDate = weekCurDate;
		this.weekCurValue = weekCurValue;
		this.weekWorkRegion = weekWorkRegion;
		this.weekPSDateGap = weekPSDateGap;
		this.weekPSValueGap = weekPSValueGap;
		this.weekPCDateGap = weekPCDateGap;
		this.weekPCValueGap = weekPCValueGap;
		this.weekSCDateGap = weekSCDateGap;
		this.weekSCValueGap = weekSCValueGap;
		this.weekMarketPSDateGap = weekMarketPSDateGap;
		this.weekMarketPSSpace = weekMarketPSSpace;
		this.weekMarketPCDateGap = weekMarketPCDateGap;
		this.weekMarketPCSpace = weekMarketPCSpace;
		this.weekMarketSCDateGap = weekMarketSCDateGap;
		this.weekMarketSCSpace = weekMarketSCSpace;
		this.weekTrendConsistent = weekTrendConsistent;
		this.weekDesireValue1 = weekDesireValue1;
		this.weekDesireRange1 = weekDesireRange1;
		this.weekDesireRate1 = weekDesireRate1;
		this.weekDesireValue2 = weekDesireValue2;
		this.weekDesireRange2 = weekDesireRange2;
		this.weekDesireRate2 = weekDesireRate2;
		this.weekDesireValue3 = weekDesireValue3;
		this.weekDesireRange3 = weekDesireRange3;
		this.weekDesireRate3 = weekDesireRate3;
		this.weekDesireValue4 = weekDesireValue4;
		this.weekDesireRange4 = weekDesireRange4;
		this.weekDesireRate4 = weekDesireRate4;
		this.weekDesireValue5 = weekDesireValue5;
		this.weekDesireRange5 = weekDesireRange5;
		this.weekDesireRate5 = weekDesireRate5;
		this.weekDesireValue6 = weekDesireValue6;
		this.weekDesireRange6 = weekDesireRange6;
		this.weekDesireRate6 = weekDesireRate6;
		this.weekDesireValue1Gap = weekDesireValue1Gap;
		this.weekDesireValue2Gap = weekDesireValue2Gap;
		this.weekDesireValue3Gap = weekDesireValue3Gap;
		this.weekDesireValue4Gap = weekDesireValue4Gap;
		this.weekDesireValue5Gap = weekDesireValue5Gap;
		this.weekDesireValue6Gap = weekDesireValue6Gap;
		this.weekBugValue = weekBugValue;
		this.weekWinValue = weekWinValue;
		this.weekLoseValue = weekLoseValue;
		this.weekDealWarn = weekDealWarn;
		this.weekOption = weekOption;
		this.monthPriDate = monthPriDate;
		this.monthPriHighOrLowest = monthPriHighOrLowest;
		this.monthReversalRegion = monthReversalRegion;
		this.monthStartDate = monthStartDate;
		this.monthStartValue = monthStartValue;
		this.monthEndDate = monthEndDate;
		this.monthEndValue = monthEndValue;
		this.monthCurDate = monthCurDate;
		this.monthCurValue = monthCurValue;
		this.monthWorkRegion = monthWorkRegion;
		this.monthPSDateGap = monthPSDateGap;
		this.monthPSValueGap = monthPSValueGap;
		this.monthPCDateGap = monthPCDateGap;
		this.monthPCValueGap = monthPCValueGap;
		this.monthSCDateGap = monthSCDateGap;
		this.monthSCValueGap = monthSCValueGap;
		this.monthMarketPSDateGap = monthMarketPSDateGap;
		this.monthMarketPSSpace = monthMarketPSSpace;
		this.monthMarketPCDateGap = monthMarketPCDateGap;
		this.monthMarketPCSpace = monthMarketPCSpace;
		this.monthMarketSCDateGap = monthMarketSCDateGap;
		this.monthMarketSCSpace = monthMarketSCSpace;
		this.monthTrendConsistent = monthTrendConsistent;
		this.monthDesireValue1 = monthDesireValue1;
		this.monthDesireRange1 = monthDesireRange1;
		this.monthDesireRate1 = monthDesireRate1;
		this.monthDesireValue2 = monthDesireValue2;
		this.monthDesireRange2 = monthDesireRange2;
		this.monthDesireRate2 = monthDesireRate2;
		this.monthDesireValue3 = monthDesireValue3;
		this.monthDesireRange3 = monthDesireRange3;
		this.monthDesireRate3 = monthDesireRate3;
		this.monthDesireValue4 = monthDesireValue4;
		this.monthDesireRange4 = monthDesireRange4;
		this.monthDesireRate4 = monthDesireRate4;
		this.monthDesireValue5 = monthDesireValue5;
		this.monthDesireRange5 = monthDesireRange5;
		this.monthDesireRate5 = monthDesireRate5;
		this.monthDesireValue6 = monthDesireValue6;
		this.monthDesireRange6 = monthDesireRange6;
		this.monthDesireRate6 = monthDesireRate6;
		this.monthDesireValue1Gap = monthDesireValue1Gap;
		this.monthDesireValue2Gap = monthDesireValue2Gap;
		this.monthDesireValue3Gap = monthDesireValue3Gap;
		this.monthDesireValue4Gap = monthDesireValue4Gap;
		this.monthDesireValue5Gap = monthDesireValue5Gap;
		this.monthDesireValue6Gap = monthDesireValue6Gap;
		this.monthBugValue = monthBugValue;
		this.monthWinValue = monthWinValue;
		this.monthLoseValue = monthLoseValue;
		this.monthDealWarn = monthDealWarn;
		this.monthOption = monthOption;
	}


	public StockSummary(){  
		  
	}  

	
	/*
	 * String fullId,
 String name,
 String douRong,
 String baseExpect,
 String main,
 String psychology,
 String risk,
 String potential,        
 String faucet,
 String curRange,
 String comState,
 String psState,
 String daySaleGrade,
 String daySaleState,
 String dayPriUpDateGap,
 String dayUpOrdownDates,
 String dayWarnDeal,
 String dayPS,
 String daySC,
 String weekSaleGrade,
 String weekSaleState,
 String weekPriUpDateGap,
 String weekUpOrdownDates,
 String weekWarnDeal,		
 String weekPS,
 String weekSC,		
 String monthUpOrdownDates,
 String monthWarnDeal,	
 String monthPS,
 String monthSC,

 String dayPriDate,
 String dayPriHighOrLowest,
 String dayReversalRegion,
 String dayStartDate,
 String dayStartValue,
 String dayEndDate,
 String dayEndValue,		
 String dayCurDate,
 String dayCurValue,
 String dayWorkRegion,


 String dayDesireValue1,
 String dayDesireRange1,
 String dayDesireRate1,
 String dayDesireValue2,
 String dayDesireRange2,
 String dayDesireRate2,
 String dayDesireValue3,
 String dayDesireRange3,
 String dayDesireRate3,
 String dayDesireValue4,
 String dayDesireRange4,
 String dayDesireRate4,
 String dayDesireValue5,
 String dayDesireRange5,
 String dayDesireRate5,
 String dayDesireValue6,
 String dayDesireRange6,
 String dayDesireRate6,


 String dayBugValue,
 String dayWinValue,
 String dayLoseValue,
 String dayDealWarn,
 String dayOption,


 String dayPSDateGap,
 String dayPSValueGap,
 String dayPCDateGap,
 String dayPCValueGap,
 String daySCDateGap,
 String daySCValueGap,


 String dayDesireValue1Gap,
 String dayDesireValue2Gap,
 String dayDesireValue3Gap,
 String dayDesireValue4Gap,
 String dayDesireValue5Gap,
 String dayDesireValue6Gap,

 String dayEndMarketDateGap,
 String dayMarketSpace,
 String dayTrendConsistent,

 String weekPriDate,
 String weekPriHighOrLowest,
 String weekReversalRegion,
 String weekStartDate,
 String weekStartValue,
 String weekEndDate,
 String weekEndValue,		
 String weekCurDate,
 String weekCurValue,
 String weekWorkRegion,


 String weekDesireValue1,
 String weekDesireRange1,
 String weekDesireRate1,
 String weekDesireValue2,
 String weekDesireRange2,
 String weekDesireRate2,
 String weekDesireValue3,
 String weekDesireRange3,
 String weekDesireRate3,
 String weekDesireValue4,
 String weekDesireRange4,
 String weekDesireRate4,
 String weekDesireValue5,
 String weekDesireRange5,
 String weekDesireRate5,
 String weekDesireValue6,
 String weekDesireRange6,
 String weekDesireRate6,


 String weekBugValue,
 String weekWinValue,
 String weekLoseValue,
 String weekDealWarn,
 String weekOption,


 String weekPSDateGap,
 String weekPSValueGap,
 String weekPCDateGap,
 String weekPCValueGap,
 String weekSCDateGap,
 String weekSCValueGap,


 String weekDesireValue1Gap,
 String weekDesireValue2Gap,
 String weekDesireValue3Gap,
 String weekDesireValue4Gap,
 String weekDesireValue5Gap,
 String weekDesireValue6Gap,

 String weekEndMarketDateGap,
 String weekMarketSpace,
 String weekTrendConsistent,

 String monthPriDate,
 String monthPriHighOrLowest,
 String monthReversalRegion,
 String monthStartDate,
 String monthStartValue,
 String monthEndDate,
 String monthEndValue,		
 String monthCurDate,
 String monthCurValue,
 String monthWorkRegion,


 String monthDesireValue1,
 String monthDesireRange1,
 String monthDesireRate1,
 String monthDesireValue2,
 String monthDesireRange2,
 String monthDesireRate2,
 String monthDesireValue3,
 String monthDesireRange3,
 String monthDesireRate3,
 String monthDesireValue4,
 String monthDesireRange4,
 String monthDesireRate4,
 String monthDesireValue5,
 String monthDesireRange5,
 String monthDesireRate5,
 String monthDesireValue6,
 String monthDesireRange6,
 String monthDesireRate6,


 String monthBugValue,
 String monthWinValue,
 String monthLoseValue,
 String monthDealWarn,
 String monthOption,


 String monthPSDateGap,
 String monthPSValueGap,
 String monthPCDateGap,
 String monthPCValueGap,
 String monthSCDateGap,
 String monthSCValueGap,


 String monthDesireValue1Gap,
 String monthDesireValue2Gap,
 String monthDesireValue3Gap,
 String monthDesireValue4Gap,
 String monthDesireValue5Gap,
 String monthDesireValue6Gap,

 String monthEndMarketDateGap,
 String monthMarketSpace,
 String monthTrendConsistent,
	 * */
	


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getComState() {
		return comState;
	}

	public void setComState(String comState) {
		this.comState = comState;
	}

	public String getPsState() {
		return psState;
	}

	public void setPsState(String psState) {
		this.psState = psState;
	}

	public String getDayPS() {
		return dayPS;
	}

	public void setDayPS(String dayPS) {
		this.dayPS = dayPS;
	}

	public String getDaySC() {
		return daySC;
	}

	public void setDaySC(String daySC) {
		this.daySC = daySC;
	}

	public String getWeekPS() {
		return weekPS;
	}

	public void setWeekPS(String weekPS) {
		this.weekPS = weekPS;
	}

	public String getWeekSC() {
		return weekSC;
	}

	public void setWeekSC(String weekSC) {
		this.weekSC = weekSC;
	}

	public String getMonthPS() {
		return monthPS;
	}

	public void setMonthPS(String monthPS) {
		this.monthPS = monthPS;
	}

	public String getMonthSC() {
		return monthSC;
	}

	public void setMonthSC(String monthSC) {
		this.monthSC = monthSC;
	}


	public String getFullId() {
		return fullId;
	}


	public void setFullId(String fullId) {
		this.fullId = fullId;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getDouRong() {
		return douRong;
	}


	public void setDouRong(String douRong) {
		this.douRong = douRong;
	}


	public String getBaseExpect() {
		return baseExpect;
	}


	public void setBaseExpect(String baseExpect) {
		this.baseExpect = baseExpect;
	}


	public String getMain() {
		return main;
	}


	public void setMain(String main) {
		this.main = main;
	}


	public String getPsychology() {
		return psychology;
	}


	public void setPsychology(String psychology) {
		this.psychology = psychology;
	}


	public String getRisk() {
		return risk;
	}


	public void setRisk(String risk) {
		this.risk = risk;
	}


	public String getPotential() {
		return potential;
	}


	public void setPotential(String potential) {
		this.potential = potential;
	}


	public String getFaucet() {
		return faucet;
	}


	public void setFaucet(String faucet) {
		this.faucet = faucet;
	}


	public String getCurRange() {
		return curRange;
	}


	public void setCurRange(String curRange) {
		this.curRange = curRange;
	}


	public String getDaySaleGrade() {
		return daySaleGrade;
	}


	public void setDaySaleGrade(String daySaleGrade) {
		this.daySaleGrade = daySaleGrade;
	}


	public String getDaySaleState() {
		return daySaleState;
	}


	public void setDaySaleState(String daySaleState) {
		this.daySaleState = daySaleState;
	}


	public String getDayPriUpDateGap() {
		return dayPriUpDateGap;
	}


	public void setDayPriUpDateGap(String dayPriUpDateGap) {
		this.dayPriUpDateGap = dayPriUpDateGap;
	}


	public String getDayUpOrdownDates() {
		return dayUpOrdownDates;
	}


	public void setDayUpOrdownDates(String dayUpOrdownDates) {
		this.dayUpOrdownDates = dayUpOrdownDates;
	}


	public String getDayWarnDeal() {
		return dayWarnDeal;
	}


	public void setDayWarnDeal(String dayWarnDeal) {
		this.dayWarnDeal = dayWarnDeal;
	}


	public String getWeekSaleGrade() {
		return weekSaleGrade;
	}


	public void setWeekSaleGrade(String weekSaleGrade) {
		this.weekSaleGrade = weekSaleGrade;
	}


	public String getWeekSaleState() {
		return weekSaleState;
	}


	public void setWeekSaleState(String weekSaleState) {
		this.weekSaleState = weekSaleState;
	}



	public String getWeekUpOrdownDates() {
		return weekUpOrdownDates;
	}


	public void setWeekUpOrdownDates(String weekUpOrdownDates) {
		this.weekUpOrdownDates = weekUpOrdownDates;
	}


	public String getWeekWarnDeal() {
		return weekWarnDeal;
	}


	public void setWeekWarnDeal(String weekWarnDeal) {
		this.weekWarnDeal = weekWarnDeal;
	}


	public String getMonthUpOrdownDates() {
		return monthUpOrdownDates;
	}


	public void setMonthUpOrdownDates(String monthUpOrdownDates) {
		this.monthUpOrdownDates = monthUpOrdownDates;
	}


	public String getMonthWarnDeal() {
		return monthWarnDeal;
	}


	public void setMonthWarnDeal(String monthWarnDeal) {
		this.monthWarnDeal = monthWarnDeal;
	}


	public String getDayPriDate() {
		return dayPriDate;
	}


	public void setDayPriDate(String dayPriDate) {
		this.dayPriDate = dayPriDate;
	}


	public String getDayPriHighOrLowest() {
		return dayPriHighOrLowest;
	}


	public void setDayPriHighOrLowest(String dayPriHighOrLowest) {
		this.dayPriHighOrLowest = dayPriHighOrLowest;
	}


	public String getDayReversalRegion() {
		return dayReversalRegion;
	}


	public void setDayReversalRegion(String dayReversalRegion) {
		this.dayReversalRegion = dayReversalRegion;
	}


	public String getDayStartDate() {
		return dayStartDate;
	}


	public void setDayStartDate(String dayStartDate) {
		this.dayStartDate = dayStartDate;
	}


	public String getDayStartValue() {
		return dayStartValue;
	}


	public void setDayStartValue(String dayStartValue) {
		this.dayStartValue = dayStartValue;
	}


	public String getDayEndDate() {
		return dayEndDate;
	}


	public void setDayEndDate(String dayEndDate) {
		this.dayEndDate = dayEndDate;
	}


	public String getDayEndValue() {
		return dayEndValue;
	}


	public void setDayEndValue(String dayEndValue) {
		this.dayEndValue = dayEndValue;
	}


	public String getDayCurDate() {
		return dayCurDate;
	}


	public void setDayCurDate(String dayCurDate) {
		this.dayCurDate = dayCurDate;
	}


	public String getDayCurValue() {
		return dayCurValue;
	}


	public void setDayCurValue(String dayCurValue) {
		this.dayCurValue = dayCurValue;
	}


	public String getDayWorkRegion() {
		return dayWorkRegion;
	}


	public void setDayWorkRegion(String dayWorkRegion) {
		this.dayWorkRegion = dayWorkRegion;
	}


	public String getDayDesireValue1() {
		return dayDesireValue1;
	}


	public void setDayDesireValue1(String dayDesireValue1) {
		this.dayDesireValue1 = dayDesireValue1;
	}


	public String getDayDesireRange1() {
		return dayDesireRange1;
	}


	public void setDayDesireRange1(String dayDesireRange1) {
		this.dayDesireRange1 = dayDesireRange1;
	}


	public String getDayDesireRate1() {
		return dayDesireRate1;
	}


	public void setDayDesireRate1(String dayDesireRate1) {
		this.dayDesireRate1 = dayDesireRate1;
	}


	public String getDayDesireValue2() {
		return dayDesireValue2;
	}


	public void setDayDesireValue2(String dayDesireValue2) {
		this.dayDesireValue2 = dayDesireValue2;
	}


	public String getDayDesireRange2() {
		return dayDesireRange2;
	}


	public void setDayDesireRange2(String dayDesireRange2) {
		this.dayDesireRange2 = dayDesireRange2;
	}


	public String getDayDesireRate2() {
		return dayDesireRate2;
	}


	public void setDayDesireRate2(String dayDesireRate2) {
		this.dayDesireRate2 = dayDesireRate2;
	}


	public String getDayDesireValue3() {
		return dayDesireValue3;
	}


	public void setDayDesireValue3(String dayDesireValue3) {
		this.dayDesireValue3 = dayDesireValue3;
	}


	public String getDayDesireRange3() {
		return dayDesireRange3;
	}


	public void setDayDesireRange3(String dayDesireRange3) {
		this.dayDesireRange3 = dayDesireRange3;
	}


	public String getDayDesireRate3() {
		return dayDesireRate3;
	}


	public void setDayDesireRate3(String dayDesireRate3) {
		this.dayDesireRate3 = dayDesireRate3;
	}


	public String getDayDesireValue4() {
		return dayDesireValue4;
	}


	public void setDayDesireValue4(String dayDesireValue4) {
		this.dayDesireValue4 = dayDesireValue4;
	}


	public String getDayDesireRange4() {
		return dayDesireRange4;
	}


	public void setDayDesireRange4(String dayDesireRange4) {
		this.dayDesireRange4 = dayDesireRange4;
	}


	public String getDayDesireRate4() {
		return dayDesireRate4;
	}


	public void setDayDesireRate4(String dayDesireRate4) {
		this.dayDesireRate4 = dayDesireRate4;
	}


	public String getDayDesireValue5() {
		return dayDesireValue5;
	}


	public void setDayDesireValue5(String dayDesireValue5) {
		this.dayDesireValue5 = dayDesireValue5;
	}


	public String getDayDesireRange5() {
		return dayDesireRange5;
	}


	public void setDayDesireRange5(String dayDesireRange5) {
		this.dayDesireRange5 = dayDesireRange5;
	}


	public String getDayDesireRate5() {
		return dayDesireRate5;
	}


	public void setDayDesireRate5(String dayDesireRate5) {
		this.dayDesireRate5 = dayDesireRate5;
	}


	public String getDayDesireValue6() {
		return dayDesireValue6;
	}


	public void setDayDesireValue6(String dayDesireValue6) {
		this.dayDesireValue6 = dayDesireValue6;
	}


	public String getDayDesireRange6() {
		return dayDesireRange6;
	}


	public void setDayDesireRange6(String dayDesireRange6) {
		this.dayDesireRange6 = dayDesireRange6;
	}


	public String getDayDesireRate6() {
		return dayDesireRate6;
	}


	public void setDayDesireRate6(String dayDesireRate6) {
		this.dayDesireRate6 = dayDesireRate6;
	}


	public String getDayBugValue() {
		return dayBugValue;
	}


	public void setDayBugValue(String dayBugValue) {
		this.dayBugValue = dayBugValue;
	}


	public String getDayWinValue() {
		return dayWinValue;
	}


	public void setDayWinValue(String dayWinValue) {
		this.dayWinValue = dayWinValue;
	}


	public String getDayLoseValue() {
		return dayLoseValue;
	}


	public void setDayLoseValue(String dayLoseValue) {
		this.dayLoseValue = dayLoseValue;
	}


	public String getDayDealWarn() {
		return dayDealWarn;
	}


	public void setDayDealWarn(String dayDealWarn) {
		this.dayDealWarn = dayDealWarn;
	}


	public String getDayOption() {
		return dayOption;
	}


	public void setDayOption(String dayOption) {
		this.dayOption = dayOption;
	}


	public String getDayPSDateGap() {
		return dayPSDateGap;
	}


	public void setDayPSDateGap(String dayPSDateGap) {
		this.dayPSDateGap = dayPSDateGap;
	}


	public String getDayPSValueGap() {
		return dayPSValueGap;
	}


	public void setDayPSValueGap(String dayPSValueGap) {
		this.dayPSValueGap = dayPSValueGap;
	}


	public String getDayPCDateGap() {
		return dayPCDateGap;
	}


	public void setDayPCDateGap(String dayPCDateGap) {
		this.dayPCDateGap = dayPCDateGap;
	}


	public String getDayPCValueGap() {
		return dayPCValueGap;
	}


	public void setDayPCValueGap(String dayPCValueGap) {
		this.dayPCValueGap = dayPCValueGap;
	}


	public String getDaySCDateGap() {
		return daySCDateGap;
	}


	public void setDaySCDateGap(String daySCDateGap) {
		this.daySCDateGap = daySCDateGap;
	}


	public String getDaySCValueGap() {
		return daySCValueGap;
	}


	public void setDaySCValueGap(String daySCValueGap) {
		this.daySCValueGap = daySCValueGap;
	}


	public String getDayDesireValue1Gap() {
		return dayDesireValue1Gap;
	}


	public void setDayDesireValue1Gap(String dayDesireValue1Gap) {
		this.dayDesireValue1Gap = dayDesireValue1Gap;
	}


	public String getDayDesireValue2Gap() {
		return dayDesireValue2Gap;
	}


	public void setDayDesireValue2Gap(String dayDesireValue2Gap) {
		this.dayDesireValue2Gap = dayDesireValue2Gap;
	}


	public String getDayDesireValue3Gap() {
		return dayDesireValue3Gap;
	}


	public void setDayDesireValue3Gap(String dayDesireValue3Gap) {
		this.dayDesireValue3Gap = dayDesireValue3Gap;
	}


	public String getDayDesireValue4Gap() {
		return dayDesireValue4Gap;
	}


	public void setDayDesireValue4Gap(String dayDesireValue4Gap) {
		this.dayDesireValue4Gap = dayDesireValue4Gap;
	}


	public String getDayDesireValue5Gap() {
		return dayDesireValue5Gap;
	}


	public void setDayDesireValue5Gap(String dayDesireValue5Gap) {
		this.dayDesireValue5Gap = dayDesireValue5Gap;
	}


	public String getDayDesireValue6Gap() {
		return dayDesireValue6Gap;
	}


	public void setDayDesireValue6Gap(String dayDesireValue6Gap) {
		this.dayDesireValue6Gap = dayDesireValue6Gap;
	}



	public String getDayTrendConsistent() {
		return dayTrendConsistent;
	}


	public void setDayTrendConsistent(String dayTrendConsistent) {
		this.dayTrendConsistent = dayTrendConsistent;
	}


	public String getWeekPriDate() {
		return weekPriDate;
	}


	public void setWeekPriDate(String weekPriDate) {
		this.weekPriDate = weekPriDate;
	}


	public String getWeekPriHighOrLowest() {
		return weekPriHighOrLowest;
	}


	public void setWeekPriHighOrLowest(String weekPriHighOrLowest) {
		this.weekPriHighOrLowest = weekPriHighOrLowest;
	}


	public String getWeekReversalRegion() {
		return weekReversalRegion;
	}


	public void setWeekReversalRegion(String weekReversalRegion) {
		this.weekReversalRegion = weekReversalRegion;
	}


	public String getWeekStartDate() {
		return weekStartDate;
	}


	public void setWeekStartDate(String weekStartDate) {
		this.weekStartDate = weekStartDate;
	}


	public String getWeekStartValue() {
		return weekStartValue;
	}


	public void setWeekStartValue(String weekStartValue) {
		this.weekStartValue = weekStartValue;
	}


	public String getWeekEndDate() {
		return weekEndDate;
	}


	public void setWeekEndDate(String weekEndDate) {
		this.weekEndDate = weekEndDate;
	}


	public String getWeekEndValue() {
		return weekEndValue;
	}


	public void setWeekEndValue(String weekEndValue) {
		this.weekEndValue = weekEndValue;
	}


	public String getWeekCurDate() {
		return weekCurDate;
	}


	public void setWeekCurDate(String weekCurDate) {
		this.weekCurDate = weekCurDate;
	}


	public String getWeekCurValue() {
		return weekCurValue;
	}


	public void setWeekCurValue(String weekCurValue) {
		this.weekCurValue = weekCurValue;
	}


	public String getWeekWorkRegion() {
		return weekWorkRegion;
	}


	public void setWeekWorkRegion(String weekWorkRegion) {
		this.weekWorkRegion = weekWorkRegion;
	}


	public String getWeekDesireValue1() {
		return weekDesireValue1;
	}


	public void setWeekDesireValue1(String weekDesireValue1) {
		this.weekDesireValue1 = weekDesireValue1;
	}


	public String getWeekDesireRange1() {
		return weekDesireRange1;
	}


	public void setWeekDesireRange1(String weekDesireRange1) {
		this.weekDesireRange1 = weekDesireRange1;
	}


	public String getWeekDesireRate1() {
		return weekDesireRate1;
	}


	public void setWeekDesireRate1(String weekDesireRate1) {
		this.weekDesireRate1 = weekDesireRate1;
	}


	public String getWeekDesireValue2() {
		return weekDesireValue2;
	}


	public void setWeekDesireValue2(String weekDesireValue2) {
		this.weekDesireValue2 = weekDesireValue2;
	}


	public String getWeekDesireRange2() {
		return weekDesireRange2;
	}


	public void setWeekDesireRange2(String weekDesireRange2) {
		this.weekDesireRange2 = weekDesireRange2;
	}


	public String getWeekDesireRate2() {
		return weekDesireRate2;
	}


	public void setWeekDesireRate2(String weekDesireRate2) {
		this.weekDesireRate2 = weekDesireRate2;
	}


	public String getWeekDesireValue3() {
		return weekDesireValue3;
	}


	public void setWeekDesireValue3(String weekDesireValue3) {
		this.weekDesireValue3 = weekDesireValue3;
	}


	public String getWeekDesireRange3() {
		return weekDesireRange3;
	}


	public void setWeekDesireRange3(String weekDesireRange3) {
		this.weekDesireRange3 = weekDesireRange3;
	}


	public String getWeekDesireRate3() {
		return weekDesireRate3;
	}


	public void setWeekDesireRate3(String weekDesireRate3) {
		this.weekDesireRate3 = weekDesireRate3;
	}


	public String getWeekDesireValue4() {
		return weekDesireValue4;
	}


	public void setWeekDesireValue4(String weekDesireValue4) {
		this.weekDesireValue4 = weekDesireValue4;
	}


	public String getWeekDesireRange4() {
		return weekDesireRange4;
	}


	public void setWeekDesireRange4(String weekDesireRange4) {
		this.weekDesireRange4 = weekDesireRange4;
	}


	public String getWeekDesireRate4() {
		return weekDesireRate4;
	}


	public void setWeekDesireRate4(String weekDesireRate4) {
		this.weekDesireRate4 = weekDesireRate4;
	}


	public String getWeekDesireValue5() {
		return weekDesireValue5;
	}


	public void setWeekDesireValue5(String weekDesireValue5) {
		this.weekDesireValue5 = weekDesireValue5;
	}


	public String getWeekDesireRange5() {
		return weekDesireRange5;
	}


	public void setWeekDesireRange5(String weekDesireRange5) {
		this.weekDesireRange5 = weekDesireRange5;
	}


	public String getWeekDesireRate5() {
		return weekDesireRate5;
	}


	public void setWeekDesireRate5(String weekDesireRate5) {
		this.weekDesireRate5 = weekDesireRate5;
	}


	public String getWeekDesireValue6() {
		return weekDesireValue6;
	}


	public void setWeekDesireValue6(String weekDesireValue6) {
		this.weekDesireValue6 = weekDesireValue6;
	}


	public String getWeekDesireRange6() {
		return weekDesireRange6;
	}


	public void setWeekDesireRange6(String weekDesireRange6) {
		this.weekDesireRange6 = weekDesireRange6;
	}


	public String getWeekDesireRate6() {
		return weekDesireRate6;
	}


	public void setWeekDesireRate6(String weekDesireRate6) {
		this.weekDesireRate6 = weekDesireRate6;
	}


	public String getWeekBugValue() {
		return weekBugValue;
	}


	public void setWeekBugValue(String weekBugValue) {
		this.weekBugValue = weekBugValue;
	}


	public String getWeekWinValue() {
		return weekWinValue;
	}


	public void setWeekWinValue(String weekWinValue) {
		this.weekWinValue = weekWinValue;
	}


	public String getWeekLoseValue() {
		return weekLoseValue;
	}


	public void setWeekLoseValue(String weekLoseValue) {
		this.weekLoseValue = weekLoseValue;
	}


	public String getWeekDealWarn() {
		return weekDealWarn;
	}


	public void setWeekDealWarn(String weekDealWarn) {
		this.weekDealWarn = weekDealWarn;
	}


	public String getWeekOption() {
		return weekOption;
	}


	public void setWeekOption(String weekOption) {
		this.weekOption = weekOption;
	}


	public String getWeekPSDateGap() {
		return weekPSDateGap;
	}


	public void setWeekPSDateGap(String weekPSDateGap) {
		this.weekPSDateGap = weekPSDateGap;
	}


	public String getWeekPSValueGap() {
		return weekPSValueGap;
	}


	public void setWeekPSValueGap(String weekPSValueGap) {
		this.weekPSValueGap = weekPSValueGap;
	}


	public String getWeekPCDateGap() {
		return weekPCDateGap;
	}


	public void setWeekPCDateGap(String weekPCDateGap) {
		this.weekPCDateGap = weekPCDateGap;
	}


	public String getWeekPCValueGap() {
		return weekPCValueGap;
	}


	public void setWeekPCValueGap(String weekPCValueGap) {
		this.weekPCValueGap = weekPCValueGap;
	}


	public String getWeekSCDateGap() {
		return weekSCDateGap;
	}


	public void setWeekSCDateGap(String weekSCDateGap) {
		this.weekSCDateGap = weekSCDateGap;
	}


	public String getWeekSCValueGap() {
		return weekSCValueGap;
	}


	public void setWeekSCValueGap(String weekSCValueGap) {
		this.weekSCValueGap = weekSCValueGap;
	}


	public String getWeekDesireValue1Gap() {
		return weekDesireValue1Gap;
	}


	public void setWeekDesireValue1Gap(String weekDesireValue1Gap) {
		this.weekDesireValue1Gap = weekDesireValue1Gap;
	}


	public String getWeekDesireValue2Gap() {
		return weekDesireValue2Gap;
	}


	public void setWeekDesireValue2Gap(String weekDesireValue2Gap) {
		this.weekDesireValue2Gap = weekDesireValue2Gap;
	}


	public String getWeekDesireValue3Gap() {
		return weekDesireValue3Gap;
	}


	public void setWeekDesireValue3Gap(String weekDesireValue3Gap) {
		this.weekDesireValue3Gap = weekDesireValue3Gap;
	}


	public String getWeekDesireValue4Gap() {
		return weekDesireValue4Gap;
	}


	public void setWeekDesireValue4Gap(String weekDesireValue4Gap) {
		this.weekDesireValue4Gap = weekDesireValue4Gap;
	}


	public String getWeekDesireValue5Gap() {
		return weekDesireValue5Gap;
	}


	public void setWeekDesireValue5Gap(String weekDesireValue5Gap) {
		this.weekDesireValue5Gap = weekDesireValue5Gap;
	}


	public String getWeekDesireValue6Gap() {
		return weekDesireValue6Gap;
	}


	public void setWeekDesireValue6Gap(String weekDesireValue6Gap) {
		this.weekDesireValue6Gap = weekDesireValue6Gap;
	}



	

	public String getWeekTrendConsistent() {
		return weekTrendConsistent;
	}


	public void setWeekTrendConsistent(String weekTrendConsistent) {
		this.weekTrendConsistent = weekTrendConsistent;
	}


	public String getMonthPriDate() {
		return monthPriDate;
	}


	public void setMonthPriDate(String monthPriDate) {
		this.monthPriDate = monthPriDate;
	}


	public String getMonthPriHighOrLowest() {
		return monthPriHighOrLowest;
	}


	public void setMonthPriHighOrLowest(String monthPriHighOrLowest) {
		this.monthPriHighOrLowest = monthPriHighOrLowest;
	}


	public String getMonthReversalRegion() {
		return monthReversalRegion;
	}


	public void setMonthReversalRegion(String monthReversalRegion) {
		this.monthReversalRegion = monthReversalRegion;
	}


	public String getMonthStartDate() {
		return monthStartDate;
	}


	public void setMonthStartDate(String monthStartDate) {
		this.monthStartDate = monthStartDate;
	}


	public String getMonthStartValue() {
		return monthStartValue;
	}


	public void setMonthStartValue(String monthStartValue) {
		this.monthStartValue = monthStartValue;
	}


	public String getMonthEndDate() {
		return monthEndDate;
	}


	public void setMonthEndDate(String monthEndDate) {
		this.monthEndDate = monthEndDate;
	}


	public String getMonthEndValue() {
		return monthEndValue;
	}


	public void setMonthEndValue(String monthEndValue) {
		this.monthEndValue = monthEndValue;
	}


	public String getMonthCurDate() {
		return monthCurDate;
	}


	public void setMonthCurDate(String monthCurDate) {
		this.monthCurDate = monthCurDate;
	}


	public String getMonthCurValue() {
		return monthCurValue;
	}


	public void setMonthCurValue(String monthCurValue) {
		this.monthCurValue = monthCurValue;
	}


	public String getMonthWorkRegion() {
		return monthWorkRegion;
	}


	public void setMonthWorkRegion(String monthWorkRegion) {
		this.monthWorkRegion = monthWorkRegion;
	}


	public String getMonthDesireValue1() {
		return monthDesireValue1;
	}


	public void setMonthDesireValue1(String monthDesireValue1) {
		this.monthDesireValue1 = monthDesireValue1;
	}


	public String getMonthDesireRange1() {
		return monthDesireRange1;
	}


	public void setMonthDesireRange1(String monthDesireRange1) {
		this.monthDesireRange1 = monthDesireRange1;
	}


	public String getMonthDesireRate1() {
		return monthDesireRate1;
	}


	public void setMonthDesireRate1(String monthDesireRate1) {
		this.monthDesireRate1 = monthDesireRate1;
	}


	public String getMonthDesireValue2() {
		return monthDesireValue2;
	}


	public void setMonthDesireValue2(String monthDesireValue2) {
		this.monthDesireValue2 = monthDesireValue2;
	}


	public String getMonthDesireRange2() {
		return monthDesireRange2;
	}


	public void setMonthDesireRange2(String monthDesireRange2) {
		this.monthDesireRange2 = monthDesireRange2;
	}


	public String getMonthDesireRate2() {
		return monthDesireRate2;
	}


	public void setMonthDesireRate2(String monthDesireRate2) {
		this.monthDesireRate2 = monthDesireRate2;
	}


	public String getMonthDesireValue3() {
		return monthDesireValue3;
	}


	public void setMonthDesireValue3(String monthDesireValue3) {
		this.monthDesireValue3 = monthDesireValue3;
	}


	public String getMonthDesireRange3() {
		return monthDesireRange3;
	}


	public void setMonthDesireRange3(String monthDesireRange3) {
		this.monthDesireRange3 = monthDesireRange3;
	}


	public String getMonthDesireRate3() {
		return monthDesireRate3;
	}


	public void setMonthDesireRate3(String monthDesireRate3) {
		this.monthDesireRate3 = monthDesireRate3;
	}


	public String getMonthDesireValue4() {
		return monthDesireValue4;
	}


	public void setMonthDesireValue4(String monthDesireValue4) {
		this.monthDesireValue4 = monthDesireValue4;
	}


	public String getMonthDesireRange4() {
		return monthDesireRange4;
	}


	public void setMonthDesireRange4(String monthDesireRange4) {
		this.monthDesireRange4 = monthDesireRange4;
	}


	public String getMonthDesireRate4() {
		return monthDesireRate4;
	}


	public void setMonthDesireRate4(String monthDesireRate4) {
		this.monthDesireRate4 = monthDesireRate4;
	}


	public String getMonthDesireValue5() {
		return monthDesireValue5;
	}


	public void setMonthDesireValue5(String monthDesireValue5) {
		this.monthDesireValue5 = monthDesireValue5;
	}


	public String getMonthDesireRange5() {
		return monthDesireRange5;
	}


	public void setMonthDesireRange5(String monthDesireRange5) {
		this.monthDesireRange5 = monthDesireRange5;
	}


	public String getMonthDesireRate5() {
		return monthDesireRate5;
	}


	public void setMonthDesireRate5(String monthDesireRate5) {
		this.monthDesireRate5 = monthDesireRate5;
	}


	public String getMonthDesireValue6() {
		return monthDesireValue6;
	}


	public void setMonthDesireValue6(String monthDesireValue6) {
		this.monthDesireValue6 = monthDesireValue6;
	}


	public String getMonthDesireRange6() {
		return monthDesireRange6;
	}


	public void setMonthDesireRange6(String monthDesireRange6) {
		this.monthDesireRange6 = monthDesireRange6;
	}


	public String getMonthDesireRate6() {
		return monthDesireRate6;
	}


	public void setMonthDesireRate6(String monthDesireRate6) {
		this.monthDesireRate6 = monthDesireRate6;
	}


	public String getMonthBugValue() {
		return monthBugValue;
	}


	public void setMonthBugValue(String monthBugValue) {
		this.monthBugValue = monthBugValue;
	}


	public String getMonthWinValue() {
		return monthWinValue;
	}


	public void setMonthWinValue(String monthWinValue) {
		this.monthWinValue = monthWinValue;
	}


	public String getMonthLoseValue() {
		return monthLoseValue;
	}


	public void setMonthLoseValue(String monthLoseValue) {
		this.monthLoseValue = monthLoseValue;
	}


	public String getMonthDealWarn() {
		return monthDealWarn;
	}


	public void setMonthDealWarn(String monthDealWarn) {
		this.monthDealWarn = monthDealWarn;
	}


	public String getMonthOption() {
		return monthOption;
	}


	public void setMonthOption(String monthOption) {
		this.monthOption = monthOption;
	}


	public String getMonthPSDateGap() {
		return monthPSDateGap;
	}


	public void setMonthPSDateGap(String monthPSDateGap) {
		this.monthPSDateGap = monthPSDateGap;
	}


	public String getMonthPSValueGap() {
		return monthPSValueGap;
	}


	public void setMonthPSValueGap(String monthPSValueGap) {
		this.monthPSValueGap = monthPSValueGap;
	}


	public String getMonthPCDateGap() {
		return monthPCDateGap;
	}


	public void setMonthPCDateGap(String monthPCDateGap) {
		this.monthPCDateGap = monthPCDateGap;
	}


	public String getMonthPCValueGap() {
		return monthPCValueGap;
	}


	public void setMonthPCValueGap(String monthPCValueGap) {
		this.monthPCValueGap = monthPCValueGap;
	}


	public String getMonthSCDateGap() {
		return monthSCDateGap;
	}


	public void setMonthSCDateGap(String monthSCDateGap) {
		this.monthSCDateGap = monthSCDateGap;
	}


	public String getMonthSCValueGap() {
		return monthSCValueGap;
	}


	public void setMonthSCValueGap(String monthSCValueGap) {
		this.monthSCValueGap = monthSCValueGap;
	}


	public String getMonthDesireValue1Gap() {
		return monthDesireValue1Gap;
	}


	public void setMonthDesireValue1Gap(String monthDesireValue1Gap) {
		this.monthDesireValue1Gap = monthDesireValue1Gap;
	}


	public String getMonthDesireValue2Gap() {
		return monthDesireValue2Gap;
	}


	public void setMonthDesireValue2Gap(String monthDesireValue2Gap) {
		this.monthDesireValue2Gap = monthDesireValue2Gap;
	}


	public String getMonthDesireValue3Gap() {
		return monthDesireValue3Gap;
	}


	public void setMonthDesireValue3Gap(String monthDesireValue3Gap) {
		this.monthDesireValue3Gap = monthDesireValue3Gap;
	}


	public String getMonthDesireValue4Gap() {
		return monthDesireValue4Gap;
	}


	public void setMonthDesireValue4Gap(String monthDesireValue4Gap) {
		this.monthDesireValue4Gap = monthDesireValue4Gap;
	}


	public String getMonthDesireValue5Gap() {
		return monthDesireValue5Gap;
	}


	public void setMonthDesireValue5Gap(String monthDesireValue5Gap) {
		this.monthDesireValue5Gap = monthDesireValue5Gap;
	}


	public String getMonthDesireValue6Gap() {
		return monthDesireValue6Gap;
	}


	public void setMonthDesireValue6Gap(String monthDesireValue6Gap) {
		this.monthDesireValue6Gap = monthDesireValue6Gap;
	}


	public String getDayMarketPSDateGap() {
		return dayMarketPSDateGap;
	}


	public void setDayMarketPSDateGap(String dayMarketPSDateGap) {
		this.dayMarketPSDateGap = dayMarketPSDateGap;
	}


	public String getDayMarketPSSpace() {
		return dayMarketPSSpace;
	}


	public void setDayMarketPSSpace(String dayMarketPSSpace) {
		this.dayMarketPSSpace = dayMarketPSSpace;
	}


	public String getDayMarketPCDateGap() {
		return dayMarketPCDateGap;
	}


	public void setDayMarketPCDateGap(String dayMarketPCDateGap) {
		this.dayMarketPCDateGap = dayMarketPCDateGap;
	}


	public String getDayMarketPCSpace() {
		return dayMarketPCSpace;
	}


	public void setDayMarketPCSpace(String dayMarketPCSpace) {
		this.dayMarketPCSpace = dayMarketPCSpace;
	}


	public String getDayMarketSCDateGap() {
		return dayMarketSCDateGap;
	}


	public void setDayMarketSCDateGap(String dayMarketSCDateGap) {
		this.dayMarketSCDateGap = dayMarketSCDateGap;
	}


	public String getDayMarketSCSpace() {
		return dayMarketSCSpace;
	}


	public void setDayMarketSCSpace(String dayMarketSCSpace) {
		this.dayMarketSCSpace = dayMarketSCSpace;
	}


	public String getWeekMarketPSDateGap() {
		return weekMarketPSDateGap;
	}


	public void setWeekMarketPSDateGap(String weekMarketPSDateGap) {
		this.weekMarketPSDateGap = weekMarketPSDateGap;
	}


	public String getWeekMarketPSSpace() {
		return weekMarketPSSpace;
	}


	public void setWeekMarketPSSpace(String weekMarketPSSpace) {
		this.weekMarketPSSpace = weekMarketPSSpace;
	}


	public String getWeekMarketPCDateGap() {
		return weekMarketPCDateGap;
	}


	public void setWeekMarketPCDateGap(String weekMarketPCDateGap) {
		this.weekMarketPCDateGap = weekMarketPCDateGap;
	}


	public String getWeekMarketPCSpace() {
		return weekMarketPCSpace;
	}


	public void setWeekMarketPCSpace(String weekMarketPCSpace) {
		this.weekMarketPCSpace = weekMarketPCSpace;
	}


	public String getWeekMarketSCDateGap() {
		return weekMarketSCDateGap;
	}


	public void setWeekMarketSCDateGap(String weekMarketSCDateGap) {
		this.weekMarketSCDateGap = weekMarketSCDateGap;
	}


	public String getWeekMarketSCSpace() {
		return weekMarketSCSpace;
	}


	public void setWeekMarketSCSpace(String weekMarketSCSpace) {
		this.weekMarketSCSpace = weekMarketSCSpace;
	}


	public String getMonthMarketPSDateGap() {
		return monthMarketPSDateGap;
	}


	public void setMonthMarketPSDateGap(String monthMarketPSDateGap) {
		this.monthMarketPSDateGap = monthMarketPSDateGap;
	}


	public String getMonthMarketPSSpace() {
		return monthMarketPSSpace;
	}


	public void setMonthMarketPSSpace(String monthMarketPSSpace) {
		this.monthMarketPSSpace = monthMarketPSSpace;
	}


	public String getMonthMarketPCDateGap() {
		return monthMarketPCDateGap;
	}


	public void setMonthMarketPCDateGap(String monthMarketPCDateGap) {
		this.monthMarketPCDateGap = monthMarketPCDateGap;
	}


	public String getMonthMarketPCSpace() {
		return monthMarketPCSpace;
	}


	public void setMonthMarketPCSpace(String monthMarketPCSpace) {
		this.monthMarketPCSpace = monthMarketPCSpace;
	}


	public String getMonthMarketSCDateGap() {
		return monthMarketSCDateGap;
	}


	public void setMonthMarketSCDateGap(String monthMarketSCDateGap) {
		this.monthMarketSCDateGap = monthMarketSCDateGap;
	}


	public String getMonthMarketSCSpace() {
		return monthMarketSCSpace;
	}


	public void setMonthMarketSCSpace(String monthMarketSCSpace) {
		this.monthMarketSCSpace = monthMarketSCSpace;
	}


	public String getMonthTrendConsistent() {
		return monthTrendConsistent;
	}


	public void setMonthTrendConsistent(String monthTrendConsistent) {
		this.monthTrendConsistent = monthTrendConsistent;
	}


	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		int thisState=0;
		int otherState=0;
		StockSummary ssu = (StockSummary)o;
		//组合状态
		thisState= Integer.parseInt(this.getComState());
		otherState = Integer.parseInt(ssu.getComState());
		int retRange = thisState-otherState;
		if (retRange > 0) 
			 return 1;
		 else if(retRange < 0)
			 return -1;
		 else 
			 return 0;
		
	}


	public String getPriComState() {
		return priComState;
	}


	public void setPriComState(String priComState) {
		this.priComState = priComState;
	}


	public String getPriState() {
		return priState;
	}


	public void setPriState(String priState) {
		this.priState = priState;
	}




	

}
