package test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.*;

import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.*;
import org.jfree.data.time.Month;
import org.jfree.data.time.Year;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

import common.ConstantsInfo;

import dao.DbConn;
import dao.StockDataDao;
import dao.StockPoint;
import dao.StockPointDao;

public class testJfreeChart extends ApplicationFrame  {

	static StockPointDao spDao;
	/**
	 * @param args
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 */
	
	 public testJfreeChart(final String title) throws IOException, ClassNotFoundException, SQLException  
	   {  
	        super(title);  
	   
	        final JFreeChart chart = createChart();  
	        final ChartPanel chartPanel = new ChartPanel(chart);  
	      //  chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));  
	        setContentPane(chartPanel); 
	        String curPath=System.getProperty("user.dir");
			 curPath = curPath.replaceAll("\\\\", "/"); //转义将\ 转为/
			 System.out.println(curPath);
	        saveAsFile(chart, curPath+"/StockImage/a.png"); 
	       
	    }  
	 
	 private JFreeChart createChart() throws IOException, ClassNotFoundException, SQLException  
	 {
		 
		 	       
	        List<StockPoint> sPointListDay = new ArrayList<StockPoint>();
	        List<StockPoint> sPointListWeek = new ArrayList<StockPoint>();
	        List<StockPoint> sPointListMonth = new ArrayList<StockPoint>();
	        sPointListDay=spDao.getLastNumPointStock("SH600000",ConstantsInfo.DayDataType,15);
	        sPointListWeek=spDao.getLastNumPointStock("SH600000",ConstantsInfo.WeekDataType,6);
	        sPointListMonth=spDao.getLastNumPointStock("SH600000",ConstantsInfo.MonthDataType,2);
	        // 绘图数据集  
	        DefaultCategoryDataset dataSet = new DefaultCategoryDataset();  
	       
	        
	    	Iterator it; 
	    	
			 String series = "日极点线";  // series指的就是报表里的那条数据线
			 for(it = sPointListDay.iterator();it.hasNext();)
			 {
				 StockPoint sp = (StockPoint) it.next();
				 dataSet.addValue((float) sp.getExtremePrice(),series, sp.getExtremeDate().toString());  
			 }		

			 String seriesWeek = "周极点线";  // series指的就是报表里的那条数据线
			 for(it = sPointListWeek.iterator();it.hasNext();)
			 {
				 StockPoint sp = (StockPoint) it.next();
				 dataSet.addValue((float) sp.getExtremePrice(),seriesWeek, sp.getExtremeDate().toString());  
			 }
			 
			 String seriesMonth = "月极点线";  // series指的就是报表里的那条数据线
			 for(it = sPointListMonth.iterator();it.hasNext();)
			 {
				 StockPoint sp = (StockPoint) it.next();
				 dataSet.addValue((float) sp.getExtremePrice(),seriesMonth, sp.getExtremeDate().toString());  
			 }
	       	      
	        //如果把createLineChart改为createLineChart3D就变为了3D效果的折线图       
	        JFreeChart  chart = ChartFactory.createLineChart("图表标题", "X轴标题", "Y轴标题", dataSet,  
	                PlotOrientation.VERTICAL, // 绘制方向  
	                true, // 显示图例  
	                true, // 采用标准生成器  
	                false // 是否生成超链接  
	                );  
	         
	        Font titleFont = new Font("宋体", Font.BOLD, 15);
	        Font font = new Font("宋体", Font.ITALIC, 10);
	        Color bgColor=Color.WHITE;
			chart.getTitle().setFont(titleFont); // 设置标题字体  
	        chart.getLegend().setItemFont(font);// 设置图例类别字体  
	        chart.setBackgroundPaint(bgColor);// 设置背景色   
	        //获取绘图区对象  
	        CategoryPlot plot = chart.getCategoryPlot();  
	        plot.setBackgroundPaint(Color.LIGHT_GRAY); // 设置绘图区背景色  
	        plot.setRangeGridlinePaint(Color.WHITE); // 设置水平方向背景线颜色  
	        plot.setRangeGridlinesVisible(true);// 设置是否显示水平方向背景线,默认值为true  
	        plot.setDomainGridlinePaint(Color.WHITE); // 设置垂直方向背景线颜色  
	        plot.setDomainGridlinesVisible(true); // 设置是否显示垂直方向背景线,默认值为false  
	  //      XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer)plot.getRenderer();
	  //      xylineandshaperenderer.setBaseShapesVisible(true);
	      
	         //轴对象
	        
	        CategoryAxis domainAxis = plot.getDomainAxis();     
	        domainAxis.setLabelFont(font); // 设置横轴字体  
	        domainAxis.setTickLabelFont(font);// 设置坐标轴标尺值字体  
	        
	        domainAxis.setLowerMargin(0.01);// 左边距 边框距离  
	        domainAxis.setUpperMargin(0.06);// 右边距 边框距离,防止最后边的一个数据靠近了坐标轴。  
	        domainAxis.setMaximumCategoryLabelLines(2);  
	         
	      
	        //设置Y轴
	        ValueAxis rangeAxis = plot.getRangeAxis();  
	        rangeAxis.setLabelFont(font);   
	        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());//Y轴显示整数  
	        rangeAxis.setAutoRangeMinimumSize(1);   //最小跨度  
	        rangeAxis.setUpperMargin(0.18);//上边距,防止最大的一个数据靠近了坐标轴。     
	        rangeAxis.setLowerBound(0);   //最小值显示0  
	        rangeAxis.setAutoRange(false);   //不自动分配Y轴数据  
	        rangeAxis.setTickMarkStroke(new BasicStroke(1.6f));     // 设置坐标标记大小  
	        rangeAxis.setTickMarkPaint(Color.BLACK);     // 设置坐标标记颜色  
	  
	          
	     // 获取折线对象  
	        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();  
	 	        
	        BasicStroke realLine = new BasicStroke(1.8f); // 设置实线  
	        // 设置虚线  
	        float dashes[] = { 5.0f };   
	        BasicStroke brokenLine = new BasicStroke(2.2f, // 线条粗细  
	                BasicStroke.CAP_ROUND, // 端点风格  
	                BasicStroke.JOIN_ROUND, // 折点风格  
	                8f, dashes, 0.6f);   
	       
	        for (int i = 0; i < dataSet.getRowCount(); i++) {  
	            if (i % 2 == 0)  
	                renderer.setSeriesStroke(i, realLine); // 利用实线绘制  
	            else  
	                renderer.setSeriesStroke(i, brokenLine); // 利用虚线绘制  
	        }  
	        
	      
	          
	        plot.setNoDataMessage("无对应的数据，请重新查询。");  
	        plot.setNoDataMessageFont(titleFont);//字体的大小  
	        plot.setNoDataMessagePaint(Color.RED);//字体颜色  
			return chart;
		}
	 
	 /**保存为文件*/     
	    public void saveAsFile(JFreeChart chart, String outputPath) {     
	        FileOutputStream out = null;     
	        try {     
	            File outFile = new File(outputPath);     
	            if (!outFile.getParentFile().exists()) {     
	                outFile.getParentFile().mkdirs();     
	            }     
	            out = new FileOutputStream(outputPath);     
	            // 保存为PNG     
	            ChartUtilities.writeChartAsPNG(out, chart, 800, 600);     
	            // 保存为JPEG     
	            // ChartUtilities.writeChartAsJPEG(out, chart, width, height);     
	            out.flush();     
	        } catch (FileNotFoundException e) {     
	            e.printStackTrace();     
	        } catch (IOException e) {     
	            e.printStackTrace();     
	        } finally {     
	            if (out != null) {     
	                try {     
	                    out.close();     
	                } catch (IOException e) {     
	                    // do nothing     
	                }     
	            }     
	        }     
	    }     
	      
        public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
	        
	        Connection stockDataConn = DbConn.getConnDB("stockConf/conn_point_db.ini");  
	        spDao =new StockPointDao(stockDataConn);
	        final testJfreeChart demo = new testJfreeChart("test"); 
	        
	       
	       demo.pack();  
	       RefineryUtilities.centerFrameOnScreen(demo);  
	       demo.setVisible(true); 
	       stockDataConn.close();
        }

	

}
