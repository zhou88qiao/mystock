package date.timer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import common.ConstantsInfo;

public class stockDateTimer {
	
	// 用来全局控制 上一周，本周，下一周的周数变化
	 private int weeks = 0;
	 private int MaxDate;// 一月最大天数
	 private int MaxYear;// 一年最大天数 
	 
	 public static String DEFAULT_FORMAT = "yyyy-MM-dd"; 
	 static SimpleDateFormat format = new   SimpleDateFormat(DEFAULT_FORMAT); 
	   /** 
	     * 格式化日期 
	     * @param date 日期对象 
	     * @return String 日期字符串 
	     */  
	    public static String formatDate(Date date){  
	        SimpleDateFormat f = new SimpleDateFormat(DEFAULT_FORMAT);  
	        String sDate = f.format(date);  
	        return sDate;  
	    }  
	    
	    public static Date strToDate(String strDate) {
			  SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			  ParsePosition pos = new ParsePosition(0);
			  Date strtodate = formatter.parse(strDate, pos);
			  return strtodate;
		 }
	    
	    //判断当前是第几周
		public int getWeekofYear() {
			Calendar calendar = Calendar.getInstance();  
			int week = calendar.get(Calendar.WEEK_OF_MONTH);    //获取年
			return week;	       
		 }
	      
	    /** 
	     * 获取某年第一天日期 
	     * @param year 年份 
	     * @return Date 
	     */  
	    public static Date getCurrYearFirst(int year){  
	        Calendar calendar = Calendar.getInstance();  
	        calendar.clear();  
	        calendar.set(Calendar.YEAR, year);  
	        Date currYearFirst = calendar.getTime();  
	        return currYearFirst;  
	    }  
	      
	    /** 
	     * 获取某年最后一天日期 
	     * @param year 年份 
	     * @return Date 
	     */  
	    public static Date getCurrYearLast(int year){  
	        Calendar calendar = Calendar.getInstance();  
	        calendar.clear();  
	        calendar.set(Calendar.YEAR, year);  
	        calendar.roll(Calendar.DAY_OF_YEAR, -1);  
	        Date currYearLast = calendar.getTime();  
	          
	        return currYearLast;  
	    }  
	 
	 public static String getTwoDay(String sj1, String sj2) 
	 {
		  SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
		  long day = 0;
		  try {
			  Date date = myFormatter.parse(sj1);
			  Date mydate = myFormatter.parse(sj2);
			  day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
		  } catch (Exception e) {
		   return "";
		  }
		  return day + "";
	 }
	 
	 public static boolean isWorkDay()
	 {
		 Calendar cd = Calendar.getInstance();
		  // 获得今天是一周的第几天，星期日是第一天，星期二是第二天......
		 int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK)-1; // 因为按中国礼拜一作为第一天所以这里减1
		 System.out.print(dayOfWeek);
		 switch(dayOfWeek)
		 {
		 case 1:
		 case 2:
		 case 3:
		 case 4:
		 case 5:
			 System.out.print("wrokDay");
			 return true;
		 case 0:
		 case 6:
		 default:
			 System.out.print("not wrokDay");
			 return false;		 
		 }
		  
		 
	 }
	 //判断 2014-5-24是周几
	 public static String getWeek(String sdate) {
		  // 再转换为时间
		  Date date = strToDate(sdate);
		  Calendar c = Calendar.getInstance();
		  c.setTime(date);
		  // int hour=c.get(Calendar.DAY_OF_WEEK);
		  // hour中存的就是星期几了，其范围 1~7
		  // 1=星期日 7=星期六，其他类推
		  return new SimpleDateFormat("EEEE").format(c.getTime());
	 }
	 
	
	 
	 public static long getDays(String date1, String date2) {
		  if (date1 == null || date1.equals(""))
			  return 0;
		  if (date2 == null || date2.equals(""))
			  return 0;
		  // 转换为标准时间
		  SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
		  Date date = null;
		  Date mydate = null;
		  try {
			  date = myFormatter.parse(date1);
			  mydate = myFormatter.parse(date2);
		  } catch (Exception e) {
		  }
		  long day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
		  return day;
	 }
	 
	 // 计算当月最后一天,返回字符串
	 public String getDefaultDay() {
		  String str = "";
		  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		  Calendar lastDate = Calendar.getInstance();
		  lastDate.set(Calendar.DATE, 1);// 设为当前月的1号
		  lastDate.add(Calendar.MONTH, 1);// 加一个月，变为下月的1号
		  lastDate.add(Calendar.DATE, -1);// 减去一天，变为当月最后一天
		  str = sdf.format(lastDate.getTime());
		  return str;
	 }
	 // 上月第一天
	 public String getPreviousMonthFirst() {
		  String str = "";
		  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		  Calendar lastDate = Calendar.getInstance();
		  lastDate.set(Calendar.DATE, 1);// 设为当前月的1号
		  lastDate.add(Calendar.MONTH, -1);// 减一个月，变为下月的1号
		  // lastDate.add(Calendar.DATE,-1);//减去一天，变为当月最后一天
		  str = sdf.format(lastDate.getTime());
		  return str;
	 }
	 // 获取当月第一天
	 public String getFirstDayOfMonth() {
		  String str = "";
		  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		  Calendar lastDate = Calendar.getInstance();
		  lastDate.set(Calendar.DATE, 1);// 设为当前月的1号
		  str = sdf.format(lastDate.getTime());
		  return str;
	 }
	 // 获得本周星期日的日期
	 public String getCurrentWeekday() {
		  weeks = 0;
		  int mondayPlus = this.getMondayPlus();
		  GregorianCalendar currentDate = new GregorianCalendar();
		  currentDate.add(GregorianCalendar.DATE, mondayPlus + 6);
		  Date monday = currentDate.getTime();
		  DateFormat df = DateFormat.getDateInstance();
		  String preMonday = df.format(monday);
		  return preMonday;
	 }
	 // 获取当天时间
	 public String getNowTime(String dateformat) {
		  Date now = new Date();
		  SimpleDateFormat dateFormat = new SimpleDateFormat(dateformat);// 可以方便地修改日期格式
		  String curTime = dateFormat.format(now);
		  return curTime;
	 }
	 
	 // 获得当前日期与本周日相差的天数
	 private int getMondayPlus() {
		  Calendar cd = Calendar.getInstance();
		  // 获得今天是一周的第几天，星期日是第一天，星期二是第二天......
		  int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK) - 1; // 因为按中国礼拜一作为第一天所以这里减1
		  
		  System.out.println("week:"+dayOfWeek);
		  if (dayOfWeek == 1) {
			  return 0;
		  } else {
			  return 1 - dayOfWeek;
		  }
	 }
	 
	 // 获得本周一的日期
	 public String getMondayOFWeek() {
		  weeks = 0;
		  int mondayPlus = this.getMondayPlus();
		  GregorianCalendar currentDate = new GregorianCalendar();
		  currentDate.add(GregorianCalendar.DATE, mondayPlus);
		  Date monday = currentDate.getTime();
		  DateFormat df = DateFormat.getDateInstance();
		  String preMonday = df.format(monday);
		  return preMonday;
	 }
	 // 获得相应周的周六的日期
	 public String getSaturday() {
		  int mondayPlus = this.getMondayPlus();
		  GregorianCalendar currentDate = new GregorianCalendar();
		  currentDate.add(GregorianCalendar.DATE, mondayPlus + 7 * weeks + 6);
		  Date monday = currentDate.getTime();
		  DateFormat df = DateFormat.getDateInstance();
		  String preMonday = df.format(monday);
		  return preMonday;
	 }
	 // 获得上周星期日的日期
	 public String getPreviousWeekSunday() {
		  weeks = 0;
		  weeks--;
		  int mondayPlus = this.getMondayPlus();
		  GregorianCalendar currentDate = new GregorianCalendar();
		  currentDate.add(GregorianCalendar.DATE, mondayPlus + weeks);
		  Date monday = currentDate.getTime();
		  DateFormat df = DateFormat.getDateInstance();
		  String preMonday = df.format(monday);
		  return preMonday;
	 }
	 // 获得上周星期一的日期
	 public String getPreviousWeekday() {
		  weeks--;
		  int mondayPlus = this.getMondayPlus();
		  GregorianCalendar currentDate = new GregorianCalendar();
		  currentDate.add(GregorianCalendar.DATE, mondayPlus + 7 * weeks);
		  Date monday = currentDate.getTime();
		  DateFormat df = DateFormat.getDateInstance();
		  String preMonday = df.format(monday);
		  return preMonday;
	 }
	 // 获得下周星期一的日期
	 public String getNextMonday() {
		  weeks++;
		  int mondayPlus = this.getMondayPlus();
		  GregorianCalendar currentDate = new GregorianCalendar();
		  currentDate.add(GregorianCalendar.DATE, mondayPlus + 7);
		  Date monday = currentDate.getTime();
		  DateFormat df = DateFormat.getDateInstance();
		  String preMonday = df.format(monday);
		  return preMonday;
	 }
	 // 获得下周星期日的日期
	 public String getNextSunday() {
		  int mondayPlus = this.getMondayPlus();
		  GregorianCalendar currentDate = new GregorianCalendar();
		  currentDate.add(GregorianCalendar.DATE, mondayPlus + 7 + 6);
		  Date monday = currentDate.getTime();
		  DateFormat df = DateFormat.getDateInstance();
		  String preMonday = df.format(monday);
		  return preMonday;
	 }
	 private int getMonthPlus() {
		  Calendar cd = Calendar.getInstance();
		  int monthOfNumber = cd.get(Calendar.DAY_OF_MONTH);
		  cd.set(Calendar.DATE, 1);// 把日期设置为当月第一天
		  cd.roll(Calendar.DATE, -1);// 日期回滚一天，也就是最后一天
		  MaxDate = cd.get(Calendar.DATE);
		  if (monthOfNumber == 1) {
		   return -MaxDate;
		  } else {
		   return 1 - monthOfNumber;
		  }
	 }
	 // 获得上月最后一天的日期
	 public String getPreviousMonthEnd() {
		  String str = "";
		  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		  Calendar lastDate = Calendar.getInstance();
		  lastDate.add(Calendar.MONTH, -1);// 减一个月
		  lastDate.set(Calendar.DATE, 1);// 把日期设置为当月第一天
		  lastDate.roll(Calendar.DATE, -1);// 日期回滚一天，也就是本月最后一天
		  str = sdf.format(lastDate.getTime());
		  return str;
	 }
	 // 获得下个月第一天的日期
	 public String getNextMonthFirst() {
		  String str = "";
		  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		  Calendar lastDate = Calendar.getInstance();
		  lastDate.add(Calendar.MONTH, 1);// 减一个月
		  lastDate.set(Calendar.DATE, 1);// 把日期设置为当月第一天
		  str = sdf.format(lastDate.getTime());
		  return str;
	 }
	 // 获得下个月最后一天的日期
	 public String getNextMonthEnd() {
		  String str = "";
		  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		  Calendar lastDate = Calendar.getInstance();
		  lastDate.add(Calendar.MONTH, 1);// 加一个月
		  lastDate.set(Calendar.DATE, 1);// 把日期设置为当月第一天
		  lastDate.roll(Calendar.DATE, -1);// 日期回滚一天，也就是本月最后一天
		  str = sdf.format(lastDate.getTime());
		  return str;
	 }
	 // 获得明年最后一天的日期
	 public String getNextYearEnd() {
		  String str = "";
		  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		  Calendar lastDate = Calendar.getInstance();
		  lastDate.add(Calendar.YEAR, 1);// 加一个年
		  lastDate.set(Calendar.DAY_OF_YEAR, 1);
		  lastDate.roll(Calendar.DAY_OF_YEAR, -1);
		  str = sdf.format(lastDate.getTime());
		  return str;
	 }
	 // 获得明年第一天的日期
	 public String getNextYearFirst() {
		  String str = "";
		  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		  Calendar lastDate = Calendar.getInstance();
		  lastDate.add(Calendar.YEAR, 1);// 加一个年
		  lastDate.set(Calendar.DAY_OF_YEAR, 1);
		  str = sdf.format(lastDate.getTime());
		  return str;
	 }
	 // 获得本年有多少天
	 private int getMaxYear() {
		  Calendar cd = Calendar.getInstance();
		  cd.set(Calendar.DAY_OF_YEAR, 1);// 把日期设为当年第一天
		  cd.roll(Calendar.DAY_OF_YEAR, -1);// 把日期回滚一天。
		  int MaxYear = cd.get(Calendar.DAY_OF_YEAR);
		  return MaxYear;
	 }
	 private int getYearPlus() {
		  Calendar cd = Calendar.getInstance();
		  int yearOfNumber = cd.get(Calendar.DAY_OF_YEAR);// 获得当天是一年中的第几天
		  cd.set(Calendar.DAY_OF_YEAR, 1);// 把日期设为当年第一天
		  cd.roll(Calendar.DAY_OF_YEAR, -1);// 把日期回滚一天。
		  int MaxYear = cd.get(Calendar.DAY_OF_YEAR);
		  if (yearOfNumber == 1) {
		   return -MaxYear;
		  } else {
		   return 1 - yearOfNumber;
		  }
	 }
	 // 获得本年第一天的日期
	 public String getCurrentYearFirst() {
		  int yearPlus = this.getYearPlus();
		  GregorianCalendar currentDate = new GregorianCalendar();
		  currentDate.add(GregorianCalendar.DATE, yearPlus);
		  Date yearDay = currentDate.getTime();
		  DateFormat df = DateFormat.getDateInstance();
		  String preYearDay = df.format(yearDay);
		  return preYearDay;
	 }
	 // 获得本年最后一天的日期 *
	 public String getCurrentYearEnd() {
		  Date date = new Date();
		  SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");// 可以方便地修改日期格式
		  String years = dateFormat.format(date);
		  return years + "-12-31";
	 }
	 // 获得上年第一天的日期 *
	 public String getPreviousYearFirst() {
		  Date date = new Date();
		  SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");// 可以方便地修改日期格式
		  String years = dateFormat.format(date);
		  int years_value = Integer.parseInt(years);
		  years_value--;
		  return years_value + "-1-1";
	 }
	 // 获得上年最后一天的日期
	 public String getPreviousYearEnd() {
		  weeks--;
		  int yearPlus = this.getYearPlus();
		  GregorianCalendar currentDate = new GregorianCalendar();
		  currentDate.add(GregorianCalendar.DATE, yearPlus + MaxYear * weeks
		    + (MaxYear - 1));
		  Date yearDay = currentDate.getTime();
		  DateFormat df = DateFormat.getDateInstance();
		  String preYearDay = df.format(yearDay);
		  getThisSeasonTime(11);
		  return preYearDay;
	 }
	 // 获得本季度
	 public String getThisSeasonTime(int month) {
		  int array[][] = { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 }, { 10, 11, 12 } };
		  int season = 1;
		  if (month >= 1 && month <= 3) {
		   season = 1;
		  }
		  if (month >= 4 && month <= 6) {
		   season = 2;
		  }
		  if (month >= 7 && month <= 9) {
		   season = 3;
		  }
		  if (month >= 10 && month <= 12) {
		   season = 4;
		  }
		  int start_month = array[season - 1][0];
		  int end_month = array[season - 1][2];
		  Date date = new Date();
		  SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");// 可以方便地修改日期格式
		  String years = dateFormat.format(date);
		  int years_value = Integer.parseInt(years);
		  int start_days = 1;// years+"-"+String.valueOf(start_month)+"-1";//getLastDayOfMonth(years_value,start_month);
		  int end_days = getLastDayOfMonth(years_value, end_month);
		  String seasonDate = years_value + "-" + start_month + "-" + start_days
		    + ";" + years_value + "-" + end_month + "-" + end_days;
		  return seasonDate;
	 }
	 
	 //获取某年某月有多少天
	 private int getLastDayOfMonth(int year, int month) {
		  if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8
		    || month == 10 || month == 12) {
		   return 31;
		  }
		  if (month == 4 || month == 6 || month == 9 || month == 11) {
		   return 30;
		  }
		  if (month == 2) {
		   if (isLeapYear(year)) {
		    return 29;
		   } else {
		    return 28;
		   }
		  }
		  return 0;
	 }
	 
	 //判断闰年
	 public boolean isLeapYear(int year) {
	  return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
	 }
	 
	 
	 
	 public static int getMonthSpace(String date1, String date2) throws ParseException
	 {
		 int result = 0;		
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		 Calendar c1 = Calendar.getInstance();
		 Calendar c2 = Calendar.getInstance();
		
		 c1.setTime(sdf.parse(date1));
		 c2.setTime(sdf.parse(date2));
		
		 result = c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH);
		
		 return result == 0 ? 1 : Math.abs(result);

	 }
	 
	 //计算时间天数
	 public static int daysBetween(String smdate,String bdate,int type) throws ParseException{  
	        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  
	        Calendar cal = Calendar.getInstance();    
	        cal.setTime(sdf.parse(smdate));    
	        long time1 = cal.getTimeInMillis();                 
	        cal.setTime(sdf.parse(bdate));    
	        long time2 = cal.getTimeInMillis();         
	        long between_days=(time2-time1)/(1000*3600*24);  
	      //  System.out.println("between_days:"+between_days);
	      //  if(between_days<0)
	    //    	between_days = -between_days;
	       int dayNums=Integer.parseInt(String.valueOf(between_days));    
	       int nums=0;
	       if (type == ConstantsInfo.DayDataType)
	    	   nums =dayNums;
	       else if (type == ConstantsInfo.WeekDataType)
	    	   nums= dayNums/7;    
	       else if (type == ConstantsInfo.MonthDataType)
	    	   nums=  dayNums/30;
	       return nums;
	   }  

	 
	 //计算后add天时间
	 public static String getAddDate(Date date,int add) {
		
		 Calendar calendar = Calendar.getInstance();
		 calendar.setTime(date);
		 calendar.add(Calendar.DATE,add); 
		 String addDate = format.format(calendar.getTime()) ;
		 return addDate;
	 }
	 
	 //获取当前日期
	 public static String getCurDate() {
		Date dt=new Date();//如果不需要格式,可直接用dt,dt就是当前系统时间
	 	String nowTime="";
	 	nowTime= format.format(dt);//用DateFormat的format()方法在dt中获取并以yyyy/MM/dd HH:mm:ss格式显示
	 	return nowTime;
	 }
	 
	 
	 public static  int compareDate(String DATE1, String DATE2) throws ParseException {     
      
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  
 
        Date dt1 = sdf.parse(DATE1);
        Date dt2 = sdf.parse(DATE2);
        if (dt1.getTime() > dt2.getTime()) {            
            return 1;
        } else if (dt1.getTime() < dt2.getTime()) {      
            return -1;
        } else {
            return 0;
        }
       
	 }

	public static void main(String[] args) throws ParseException {
		stockDateTimer tt = new stockDateTimer();
		 int i= compareDate("1995-11-12", "1999-12-16");
	       System.out.println("i=="+i);
	       /*
	       for(int i = 1999;i < 2013;i++){  
	            System.out.println(formatDate(getCurrYearFirst(i)));  
	            System.out.println(formatDate(getCurrYearLast(i)));  
	        }  
		
		System.out.println("获取当前日期是周几:" + tt.isWorkDay());
		System.out.println("获取当前日期是周几:" + tt.getWeek("2014-4-29"));
		System.out.println("获取当天日期:" + tt.getNowTime("yyyy-MM-dd"));
		System.out.println("获取本周一日期:" + tt.getMondayOFWeek());
		System.out.println("获取本周日的日期:" + tt.getCurrentWeekday());
		System.out.println("获取上周一日期:" + tt.getPreviousWeekday());
		System.out.println("获取上周日日期:" + tt.getPreviousWeekSunday());
		System.out.println("获取下周一日期:" + tt.getNextMonday());
		System.out.println("获取下周日日期:" + tt.getNextSunday());
		System.out.println("获得相应周的周六的日期:" + tt.getNowTime("yyyy-MM-dd"));
		System.out.println("获取本月第一天日期:" + tt.getFirstDayOfMonth());
		System.out.println("获取本月最后一天日期:" + tt.getDefaultDay());
		System.out.println("获取上月第一天日期:" + tt.getPreviousMonthFirst());
		System.out.println("获取上月最后一天的日期:" + tt.getPreviousMonthEnd());
		System.out.println("获取下月第一天日期:" + tt.getNextMonthFirst());
		System.out.println("获取下月最后一天日期:" + tt.getNextMonthEnd());
		System.out.println("获取本年的第一天日期:" + tt.getCurrentYearFirst());
		System.out.println("获取本年最后一天日期:" + tt.getCurrentYearEnd());
		System.out.println("获取去年的第一天日期:" + tt.getPreviousYearFirst());
		System.out.println("获取去年的最后一天日期:" + tt.getPreviousYearEnd());
		System.out.println("获取明年第一天日期:" + tt.getNextYearFirst());
		System.out.println("获取明年最后一天日期:" + tt.getNextYearEnd());
		System.out.println("获取本季度第一天到最后一天:" + tt.getThisSeasonTime(11));
		System.out.println("获取两个日期之间间隔天数2008-12-1~2008-9.29:"
	    + stockDateTimer.getTwoDay("2008-12-1", "2008-9-29"));
		*/
	}

}
