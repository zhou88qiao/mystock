package com.timer.stock;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class StockDateTimer {
	
	public static String DEFAULT_FORMAT = "yyyy-MM-dd"; 
	
	 static SimpleDateFormat format = new   SimpleDateFormat(DEFAULT_FORMAT); 
	
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
    
		 
	 public static String getBeforeDay(String dateStr, int type,int num) {
		GregorianCalendar gregorianCal = new GregorianCalendar();  
		Date  date=strToDate(dateStr);	         
		gregorianCal.setTime(date); 
		switch(type)
		{
		case 1:
			gregorianCal.set(Calendar.DAY_OF_YEAR, gregorianCal.get(Calendar.DAY_OF_YEAR) - num);
			break;
		case 2:
			gregorianCal.set(Calendar.WEEK_OF_YEAR, gregorianCal.get(Calendar.WEEK_OF_YEAR) - num);  
			break;	
		case 3:
			gregorianCal.set(Calendar.MONTH, gregorianCal.get(Calendar.MONTH) - num);  
			break;			
		}
		
		Date beforeDate= gregorianCal.getTime();
		String before=formatDate(beforeDate);
		return before;
	 }
	 
	 //判断当前是第几天
	 public int getDayofYear() {
			Calendar calendar = Calendar.getInstance();  
			int day = calendar.get(Calendar.DAY_OF_YEAR);    //获取年
			return day;	       
		}
	 
	  //判断当前是第几周
	 public int getWeekofYear() {
			Calendar calendar = Calendar.getInstance();  
			int week = calendar.get(Calendar.WEEK_OF_YEAR);    //获取年
			return week;	       
		}
	 //判断当前是第几月
	 public int getMonthofYear() {
			Calendar calendar = Calendar.getInstance();  
			int month = calendar.get(Calendar.MONTH);    //获取年
			return month;	       
		}
	 
	 
	 //判断 2014-5-24是第几周
	 public static int getDayInYear(String sdate) {
		  // 再转换为时间
		  Date date = strToDate(sdate);
		  Calendar calendar = Calendar.getInstance();
		  calendar.setTime(date);		
		  return calendar.get(Calendar.DAY_OF_YEAR); 
	 }
	 
	 //判断 2014-5-24是第几周
	 public static int getWeekInYear(String sdate) {
		  // 再转换为时间
		  Date date = strToDate(sdate);
		  Calendar calendar = Calendar.getInstance();
		  calendar.setTime(date);		
		  return calendar.get(Calendar.WEEK_OF_YEAR); 
	 }
	 
	//判断 2014-5-24是第几月
	 public static int getMonthInYear(String sdate) {
		  // 再转换为时间
		  Date date = strToDate(sdate);
		  Calendar calendar = Calendar.getInstance();
		  calendar.setTime(date);		
		  return calendar.get(Calendar.MONTH); 
	 }
	 
	 //获取当前日期
	 public static String getCurDate() {
		Date dt=new Date();//如果不需要格式,可直接用dt,dt就是当前系统时间
	 	String nowTime="";
	 	nowTime= format.format(dt);//用DateFormat的format()方法在dt中获取并以yyyy/MM/dd HH:mm:ss格式显示
	 	return nowTime;
	 }
	 
	 public static void main(String[] args) {
		 
		 
		 Calendar aTime=Calendar.getInstance();
			int year = aTime.get(Calendar.YEAR);//得到年
			System.out.println(year);
		 
		 StockDateTimer st=new StockDateTimer();
		 int day=st.getDayofYear();
		 int day1=st.getDayInYear("2014-6-24");
		 int week=st.getWeekInYear("2015-12-11");
		 int week1=st.getWeekInYear("2015-12-12");
		 int month=st.getMonthInYear("2014-12-24");
		 System.out.println("day:"+day+"week:"+week+"month:"+month);
		 System.out.println("week1:"+week1);
		 System.out.println("day1:"+day1);
		 

	 }
	
	
	    
	

}
