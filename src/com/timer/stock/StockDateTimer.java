package com.timer.stock;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import common.ConstantsInfo;

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
	 
	 //�жϵ�ǰ�ǵڼ���
	 public int getDayofYear() {
			Calendar calendar = Calendar.getInstance();  
			int day = calendar.get(Calendar.DAY_OF_YEAR);    //��ȡ��
			return day;	       
		}
	 
	  //�жϵ�ǰ�ǵڼ���
	 public int getWeekofYear() {
			Calendar calendar = Calendar.getInstance();  
			int week = calendar.get(Calendar.WEEK_OF_YEAR);    //��ȡ��
			return week;	       
		}
	 //�жϵ�ǰ�ǵڼ���
	 public int getMonthofYear() {
			Calendar calendar = Calendar.getInstance();  
			int month = calendar.get(Calendar.MONTH);    //��ȡ��
			return month;	       
		}
	 
	 
	 //�ж� 2014-5-24�ǵڼ���
	 public static int getDayInYear(String sdate) {
		  // ��ת��Ϊʱ��
		  Date date = strToDate(sdate);
		  Calendar calendar = Calendar.getInstance();
		  calendar.setTime(date);		
		  return calendar.get(Calendar.DAY_OF_YEAR); 
	 }
	 
	 //�ж� 2014-5-24�ǵڼ���
	 public static int getWeekInYear(String sdate) {
		  // ��ת��Ϊʱ��
		  Date date = strToDate(sdate);
		  Calendar calendar = Calendar.getInstance();
		  calendar.setTime(date);		
		  return calendar.get(Calendar.WEEK_OF_YEAR); 
	 }
	 
	//�ж� 2014-5-24�ǵڼ���
	 public static int getMonthInYear(String sdate) {
		  // ��ת��Ϊʱ��
		  Date date = strToDate(sdate);
		  Calendar calendar = Calendar.getInstance();
		  calendar.setTime(date);		
		  return calendar.get(Calendar.MONTH); 
	 }
	 
	 //��ȡ��ǰ����
	 public static String getCurDate() {
		Date dt=new Date();//�������Ҫ��ʽ,��ֱ����dt,dt���ǵ�ǰϵͳʱ��
	 	String nowTime="";
	 	nowTime= format.format(dt);//��DateFormat��format()������dt�л�ȡ����yyyy/MM/dd HH:mm:ss��ʽ��ʾ
	 	return nowTime;
	 }
	 
	 
	 public static boolean isSameDate(String date1,String date2, int type)
	 {
	
		  Date d1 = null;
		  Date d2 = null;
		  try
		  {
			  d1 = format.parse(date1);
			  d2 = format.parse(date2);
		  }
		  catch(Exception e)
		  {
			  e.printStackTrace();
		  }
		  Calendar cal1 = Calendar.getInstance();
		  Calendar cal2 = Calendar.getInstance();
		  cal1.setTime(d1);
		  cal2.setTime(d2);
		  int subYear = cal1.get(Calendar.YEAR)-cal2.get(Calendar.YEAR);
		  //subYear==0,˵����ͬһ��
		  if(type == ConstantsInfo.WeekDataType){
			  if(subYear == 0)
			  {
				  if(cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
					  return true;
			  }
			  //����:cal1��"2005-1-1"��cal2��"2004-12-25"
			  //java��"2004-12-25"����ɵ�52��
			  // "2004-12-26"��������˵�1�ܣ���"2005-1-1"��ͬ��
			  //��ҿ��Բ�һ���Լ�������
			  //����ıȽϺ�
			  //˵��:java��һ����"0"��ʶ����ô12����"11"
			  else if(subYear==1 && cal2.get(Calendar.MONTH)==11)
			  {
				  if(cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
					  return true;
			  }
			  //����:cal1��"2004-12-31"��cal2��"2005-1-1"
			  else if(subYear==-1 && cal1.get(Calendar.MONTH)==11)
			  {
				  if(cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
					  return true;   
			  }
		  } else {
			  if(subYear == 0)
			  {
				  if(cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH))
					  return true;
			  }
		  }
		  return false;
	 }
	 
	 public static void main(String[] args) {
		 
		 
		 Calendar aTime=Calendar.getInstance();
			int year = aTime.get(Calendar.YEAR);//�õ���
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
		 
		boolean isa =  isSameDate("2017-11-07", "2017-11-09", ConstantsInfo.MonthDataType);
		if(isa){
			 System.out.println("11111");
		}
		 

	 }
	
	
	    
	

}
