package stockGUI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;



import common.ConstantsInfo;

import dao.DbConn;
import dao.StockBaseDao;
import dao.StockDataDao;
import dao.StockPoint;
import dao.StockPointDao;

public class StockTimeSeriesChart {
	
	private StockBaseDao sbDao;
	private StockPointDao spDao;
	public StockTimeSeriesChart()
	{
		
	}
	public StockTimeSeriesChart(Connection stockBaseConn,Connection stockDataConn,Connection stockPointConn)
	{
		sbDao = new StockBaseDao(stockBaseConn);
		spDao =new StockPointDao(stockPointConn);
	}
	
	 public StockTimeSeriesChart(StockBaseDao sbDao,StockPointDao spDao)
	{
		this.sbDao = sbDao;
		this.spDao = spDao;
		///	this.spDao = spDao;
	}	
	private XYDataset createStockDataset(String fullId) throws IOException, ClassNotFoundException, SQLException {
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        int i=0;
        StockPoint sp;
        List<StockPoint> sPointListDay = new ArrayList<StockPoint>();
        List<StockPoint> sPointListWeek = new ArrayList<StockPoint>();
        List<StockPoint> sPointListMonth = new ArrayList<StockPoint>();
        
        //时间可能会重复
        sPointListDay=spDao.getLastNumPointStock(fullId,ConstantsInfo.DayDataType,300);
        sPointListWeek=spDao.getLastNumPointStock(fullId,ConstantsInfo.WeekDataType,150);
        sPointListMonth=spDao.getLastNumPointStock(fullId,ConstantsInfo.MonthDataType,40);
        

        TimeSeries timeSeriesDayPoint = new TimeSeries("日极点", Day.class);
        
        HashMap<String,String> dataValueMap = new HashMap<String,String>();
        for(i = 0;i < sPointListDay.size(); i++)
		 {
			 
			 sp = sPointListDay.get(i);
			if(dataValueMap.containsKey(sp.getExtremeDate().toString()))
				continue;
			dataValueMap.put(sp.getExtremeDate().toString(),sp.getExtremeDate().toString());
			// System.out.println(sp.getExtremeDate());
			 timeSeriesDayPoint.add(new Day(sp.getExtremeDate()), sp.getExtremePrice());
		 }	
		 
        dataValueMap.clear();
		TimeSeries timeSeriesWeekPoint = new TimeSeries("周极点", Day.class);
        for(i = 0;i < sPointListWeek.size(); i++)
		 {
			 sp = sPointListWeek.get(i);
			 if(dataValueMap.containsKey(sp.getExtremeDate().toString()))
					continue;
			 dataValueMap.put(sp.getExtremeDate().toString(),sp.getExtremeDate().toString());
			 timeSeriesWeekPoint.add(new Day(sp.getExtremeDate()), sp.getExtremePrice());
		 }
        dataValueMap.clear();
        TimeSeries timeSeriesMonthPoint = new TimeSeries("月极点", Day.class);
        for(i = 0;i < sPointListMonth.size(); i++)
		 {
			 
			 sp = sPointListMonth.get(i);
			 if(dataValueMap.containsKey(sp.getExtremeDate().toString()))
					continue;
			 dataValueMap.put(sp.getExtremeDate().toString(),sp.getExtremeDate().toString());
			 timeSeriesMonthPoint.add(new Day(sp.getExtremeDate()), sp.getExtremePrice());
		 }	
        dataValueMap.clear();
 		
        dataset.addSeries(timeSeriesDayPoint);
        dataset.addSeries(timeSeriesWeekPoint);    
        dataset.addSeries(timeSeriesMonthPoint);
        return dataset;
    }
 
    public  JFreeChart createTimeSeriesChart(String fullId) throws IOException, ClassNotFoundException, SQLException {
    	
    	 // 增加汉字支持  
        StandardChartTheme standardChartTheme=new StandardChartTheme("CN");     //创建主题样式            
        standardChartTheme.setExtraLargeFont(new Font("隶书",Font.BOLD,20));    //设置标题字体         
        standardChartTheme.setRegularFont(new Font("SimSun",Font.PLAIN,15));    //设置图例的字体      
        standardChartTheme.setLargeFont(new Font("宋体",Font.PLAIN,15));      //设置轴向的字体     
        ChartFactory.setChartTheme(standardChartTheme);   
        //添加数据集
        XYDataset stockDataSet = createStockDataset(fullId);
        JFreeChart timeSeriesChart = ChartFactory.createTimeSeriesChart(
        		fullId+"极点走势图",     // 图表名  
                  "时间",               // 横轴标签文字  
                  "点位",               // 纵轴标签文字  
                  stockDataSet,          // 图表的数据集合  
                  true,               // 是否显示图表中每条数据序列的说明  
                  true,              // 是否显示工具提示  
                  false);             // 是否显示图表中设置的url网络连接  
               
        timeSeriesChart.setBackgroundPaint(Color.WHITE);//Color.YELLOW
        XYPlot plot = timeSeriesChart.getXYPlot();
        setXYPolt(plot);
 
        return timeSeriesChart;
        
    }
 
    public static void setXYPolt(XYPlot plot) {
    	
    	plot.setBackgroundPaint(Color.darkGray); // 设置绘图区背景色  
    	// plot.setBackgroundPaint(Color.LIGHT_GRAY); // 设置绘图区背景色  
    	plot.setRangeGridlinePaint(Color.WHITE); // 设置水平方向背景线颜色  
        plot.setRangeGridlinesVisible(true);// 设置是否显示水平方向背景线,默认值为true  
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainGridlinePaint(Color.WHITE); // 设置垂直方向背景线颜色  
        plot.setDomainGridlinesVisible(true); // 设置是否显示垂直方向背景线,默认值为false
   
        
        XYItemRenderer r = plot.getRenderer();
        if (r instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
            renderer.setBaseShapesVisible(true);
            renderer.setBaseShapesFilled(false);
        }
        
        DateAxis dateaxis = (DateAxis) plot.getDomainAxis(); 
        dateaxis.setDateFormatOverride(new SimpleDateFormat("MM-dd-yyyy")); 
          
    }
 
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
		
		Connection stockBaseConn = DbConn.getConnDB("stockConf/conn_base_db.ini");  
		Connection stockDataConn = DbConn.getConnDB("stockConf/conn_data_db.ini"); 
		Connection stockPointConn = DbConn.getConnDB("stockConf/conn_point_db.ini");  
		StockTimeSeriesChart stsc =new StockTimeSeriesChart(stockBaseConn,stockDataConn,stockPointConn);
		   
		JFreeChart timeSeriesChart=stsc.createTimeSeriesChart("SH000001");
		ChartFrame frame = new ChartFrame("StockChart", timeSeriesChart);
        frame.pack();
        frame.setVisible(true);
		
		 
		stockBaseConn.close();
		stockDataConn.close();
		stockPointConn.close();

	}

}
