package stock.loader.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.PropertyConfigurator;

import common.ConstantsInfo;
import common.stockLogger;

import dao.BaseDao;
import dao.DayStock;
import dao.DayStockDao;
import dao.DbConn;
import dao.StockBaseDao;
import dao.StockConcept;
import dao.StockDataDao;
import dao.StockInformation;
import dao.StockInformationDao;
import dao.StockPointDao;
import dao.StockSingle;
import dao.StockSummaryDao;
import stock.analysis.CalculationStock;
import stock.analysis.PointClass;

public class FileReader {
	
	   static DayStockDao ds;
	   private StockBaseDao sbDao; 
	   private StockDataDao sdDao;
	   private StockPointDao spDao;
	   private StockSummaryDao ssDao;
	 //  static StockBaseDao sbDao =new StockBaseDao();
	   
	   
	   public FileReader(Connection stockBaseConn,Connection stockDataConn,Connection stockPointConn,Connection stockSummaryConn)
		{
		   this.sbDao = new StockBaseDao(stockBaseConn);
		   this.sdDao =new StockDataDao(stockDataConn);
		   this.spDao =new StockPointDao(stockPointConn);
		   this.ssDao = new StockSummaryDao(stockSummaryConn);
		}
	   
	   public FileReader(StockBaseDao sbDao,StockDataDao sdDao,StockPointDao spDao,StockSummaryDao ssDao)
		{
			this.sbDao = sbDao;
			this.sdDao = sdDao;
			this.spDao = spDao;
			this.ssDao = ssDao;
		}
	   
	/**
    * * 以行为单位读取文件，常用于读面向行的格式化文件
    * 步骤：1：先获得文件句柄
    * 2：获得文件句柄当做是输入一个字节码流，需要对这个输入流进行读取
    * 3：读取到输入流后，需要读取生成字节流
    * 4：一行一行的输出。readline()。
    * 备注：需要考虑的是异常情况
    */
	 public static int readFileByLines(String filePath,String stockId, int type)
	 {
		 int stockDataNum = 0;  
		 long stockVolume =0;
		 try {
               String encoding="GBK";
               File file=new File(filePath);
               FileWriter fw = null;
               String wfPath=null;
                
               if (stockId.equals("SH999999"))  				
            	   wfPath="StockLoadFile\\"+"SH000001"+".sql";
               else {
            	   wfPath="StockLoadFile\\"+stockId+".sql";
               }
             //  System.out.println("wfpath:"+wfPath);
               String  tdx= "数据来源:通达信 ";
               fw = new FileWriter(wfPath);

               if(file.isFile() && file.exists()){ //判断文件是否存在
                   InputStreamReader read = new InputStreamReader(
                   new FileInputStream(file),encoding);//考虑到编码格式
                   BufferedReader bufferedReader = new BufferedReader(read);
                   String lineTxt = null;
                         
                   int line = 0;
                   while((lineTxt = bufferedReader.readLine()) != null){                 
                       line++;
                       //排除第一 第二 最后一行数据
                       if(line==1 || line==2 )
                    	   continue;
                      // System.out.println(lineTxt);
                       
                       //只保留5列 （时间 开盘价 收盘价 最高价 最低价）
                       //时间改成年月日 跟mysql保持一致
                       if(lineTxt.length()>36)//去除最后一行 长度为8
                       { 
                    	   // stock_info=lineTxt.substring(0, 40);
                    	   //stock_info=lineTxt.substring(0, 40);    
                    	   String stock_time=lineTxt.substring(0, 10);              	
                    	//   System.out.println(stock_time);                   	 
                    	   
                    	   String[] sourceStrArray = lineTxt.split("\t");//分割出来的字符数组
                    	   /*
                           for (int i = 0; i < sourceStrArray.length; i++) {
                        	  // System.out.println(sourceStrArray[i]);
                           }
                           */
                    	  // System.out.println(sourceStrArray[0]+":"+sourceStrArray[5]);
                           stockVolume =  Long.parseLong(sourceStrArray[5]);                    	  
                          // stock_time=lineTxt.substring(0, 10);   
                           //不管交易量是否为零
                           if(type == ConstantsInfo.FuturesMarket) {
                        	   stockDataNum++;
                        	   fw.write(lineTxt);
	                    	   fw.write("\r\n"); 
                           } else {
                        	   //停牌股票不能导入
	                           if(stockVolume>0) {
	                        	   stockDataNum++;
		                    	   fw.write(lineTxt);
		                    	   fw.write("\r\n"); 
	                           }
                           }
                       }   
                    
                   }
                //   System.out.println("line:"+line);
                   read.close();
                   fw.close();             
       } else {
           System.out.println("找不到指定的文件");
       }
       } catch (Exception e) {
           System.out.println("读取文件内容出错");
           e.printStackTrace();
       }
       
       return stockDataNum;
	     
	}
	 
	 
	 //读取导入文件第一行的时间
	 public static String readFileFirstLineDate(){
		 String stock_time = "";  
		 try {
			 String encoding="GBK";
			 String wfPath=null;            
        	 wfPath="StockData\\"+"SH999999.txt";
        	 File file=new File(wfPath);

        	 if(file.isFile() && file.exists()){ //判断文件是否存在
               InputStreamReader read = new InputStreamReader(new FileInputStream(file),encoding);//考虑到编码格式
               BufferedReader bufferedReader = new BufferedReader(read);
               String lineTxt = null;
               int line = 0;
               while((lineTxt = bufferedReader.readLine()) != null){                 
                   line++;
                   //排除第一 第二 最后一行数据
                   if(line==1 || line==2 )
                	   continue;
                   stock_time=lineTxt.substring(0, 10);
                   System.out.println(stock_time);
                   break;
               }
               read.close();           
	       }else{
	           System.out.println("找不到指定的文件");
	       }
       } catch (Exception e) {
           System.out.println("读取文件内容出错");
           e.printStackTrace();
       }
	     
       return stock_time;
	}
		 
	 
	 public void readFileByLinesForStockConcept(String filePath)
	 {
	      
	 try {
           String encoding="GBK";	             
           File file=new File(filePath);
           //FileWriter fw = null;
           String concept_name;
           int txtIndex=0;
           
           txtIndex=filePath.indexOf(".txt");
	       concept_name=filePath.substring(14, txtIndex);
	       
	       String conceptCode=null;
	       conceptCode=sbDao.getConceptCodeFromFirstIndeustyToConceptTable(concept_name);			
     
	       //
	       
           if(file.isFile() && file.exists()){ //判断文件是否存在               
        	   //删除原有概念股票
        	   sbDao.deleteStockToConcept(conceptCode,concept_name);
        	   InputStreamReader read = new InputStreamReader(new FileInputStream(file),encoding);//考虑到编码格式
               BufferedReader bufferedReader = new BufferedReader(read);
               String lineTxt = null;	 
               String newlineTxt=null;
               String fullId=null;
               String Id=null;
               String zhengquan=null;
               String name=null;
               String newFullId=null;
               int line = 0;
               while((lineTxt = bufferedReader.readLine()) != null){
            //       System.out.println(lineTxt);
                   fullId=lineTxt.substring(0, 9);
                   System.out.println("id:"+fullId);
                   Id=lineTxt.substring(0, 6);
                   if(Id.charAt(0)== '6')
                	   newFullId="SH"+Id;
                   else
                	   newFullId="SZ"+Id;
                   /*
                   zhengquan=lineTxt.substring(7, 9);
                   System.out.println(Id);
                   System.out.println(zhengquan);                  
                   newFullId=zhengquan+Id;
                    */
                   System.out.println("fullId:"+newFullId);
                   name=lineTxt.substring(7,lineTxt.length());
                   System.out.println("stock_name:"+name);
                   line++;
                  // newlineTxt=lineTxt+"  "+concept_name;
                 //  System.out.println(newlineTxt);
                   stockLogger.logger.fatal("fullId："+newFullId+"-name:"+name);
                   //增加个股
                   if(conceptCode==null) {                	  
                	   sbDao.insertStockToConcept(newFullId,name," "," ",concept_name);
                   } else{      			 		
      			 		sbDao.insertStockToConcept(newFullId,name," ", conceptCode,concept_name);
      			 	}
                  
                //   fw.write(newlineTxt);
                 // fw.write("\r\n");                    	  
               }
              System.out.println("该概念有:"+line+"股票");
               read.close();
           //   fw.close();             
       }else{
           System.out.println("找不到指定的文件");
       }
       } catch (Exception e) {
           System.out.println("读取文件内容出错");
           e.printStackTrace();
       }
	     
	 }
	 
	 public void readFileByLinesForStockThirdIndustry(String filePath){
	      
		 try {
	               String encoding="GBK";	             
	               File file=new File(filePath);
	               //FileWriter fw = null;
	               String industry_name;
	               String newindustry_name;
	               int txtIndex=0;
	               String allindustry_name;
	               
	               
		          	 txtIndex=filePath.indexOf(".txt");
					 industry_name=filePath.substring(15, txtIndex);
				//	 if(industry_name.lastIndexOf(ch)=='Ⅲ')
					// System.out.println("industry name:"+industry_name);	
					 if(industry_name.contains("Ⅲ"))
					 {	
					 	newindustry_name=industry_name.substring(0, industry_name.lastIndexOf('Ⅲ'));
					 	System.out.println("new industry name:"+newindustry_name);	
					 	allindustry_name=newindustry_name;
					 }
					 else
						 allindustry_name=industry_name;			
					 
					 
		            //   String wfPath="dataLoadMysql\\"+concept_name+".sql";
	              //  fw = new FileWriter(wfPath); 
	                              
	               if(file.isFile() && file.exists()){ //判断文件是否存在
	                   InputStreamReader read = new InputStreamReader(new FileInputStream(file),encoding);//考虑到编码格式
	                   BufferedReader bufferedReader = new BufferedReader(read);
	                   String lineTxt = null;	 
	                   String newlineTxt=null;
	                   String fullId=null;
	                   String Id=null;
	                   String zhengquan=null;
	                   String name=null;
	                   String newFullId=null;
	                   int line = 0;
	                   while((lineTxt = bufferedReader.readLine()) != null){
	                       System.out.println(lineTxt);
	                       fullId=lineTxt.substring(0, 9);
	                       System.out.println(fullId);
	                       Id=lineTxt.substring(0, 6);
	                       zhengquan=lineTxt.substring(7, 9);
	                       System.out.println(Id);
	                       System.out.println(zhengquan);
	                       newFullId=zhengquan+Id;
	                       System.out.println(newFullId);
	                       name=lineTxt.substring(12,lineTxt.length());
	                       System.out.println(name);
	                       line++;
	                       newlineTxt=lineTxt+"  "+allindustry_name;
	                     //  System.out.println(newlineTxt);
	                       sbDao.addStockToIndustry(newFullId,name,allindustry_name);
	                    //   fw.write(newlineTxt);
	                     // fw.write("\r\n");                    	  
	                   }
	                  System.out.println("该概念有:"+line+"股票");
	                   read.close();
	               //   fw.close();             
	       }else{
	           System.out.println("找不到指定的文件");
	       }
	       } catch (Exception e) {
	           System.out.println("读取文件内容出错");
	           e.printStackTrace();
	       }
		     
		}
	 
	 /**
	     * 以字节为单位读取文件，常用于读二进制文件，如图片、声音、影像等文件。
	     * 
	     * @param fileName
	     *            文件的名
	     */
	    public static void readFileByBytes(String fileName) {
	        File file = new File(fileName);
	        InputStream in = null;
	        try {
	            System.out.println("以字节为单位读取文件内容，一次读一个字节：");
	            // 一次读一个字节
	            in = new FileInputStream(file);
	            int tempbyte;
	            while ((tempbyte = in.read()) != -1) {
	                System.out.write(tempbyte);
	            }
	            in.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	            return;
	        }
	        try {
	            System.out.println("以字节为单位读取文件内容，一次读多个字节：");
	            // 一次读多个字节
	            byte[] tempbytes = new byte[100];
	            int byteread = 0;
	            in = new FileInputStream(fileName);
	            FileReader.showAvailableBytes(in);
	            // 读入多个字节到字节数组中，byteread为一次读入的字节数
	            while ((byteread = in.read(tempbytes)) != -1) {
	                System.out.write(tempbytes, 0, byteread);
	            }
	        } catch (Exception e1) {
	            e1.printStackTrace();
	        } finally {
	            if (in != null) {
	                try {
	                    in.close();
	                } catch (IOException e1) {
	                }
	            }
	        }
	    }
	 
	    /**
	     * 以字符为单位读取文件，常用于读文本，数字等类型的文件
	     * 
	     * @param fileName
	     *            文件名
	     */
	    public static void readFileByChars(String fileName) {
	        File file = new File(fileName);
	        Reader reader = null;
	        try {
	            System.out.println("以字符为单位读取文件内容，一次读一个字节：");
	            // 一次读一个字符
	            reader = new InputStreamReader(new FileInputStream(file));
	            int tempchar;
	            while ((tempchar = reader.read()) != -1) {
	                // 对于windows下，\r\n这两个字符在一起时，表示一个换行。
	                // 但如果这两个字符分开显示时，会换两次行。
	                // 因此，屏蔽掉\r，或者屏蔽\n。否则，将会多出很多空行。
	                if (((char) tempchar) != '\r') {
	                    System.out.print((char) tempchar);
	                }
	            }
	            reader.close();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        try {
	            System.out.println("以字符为单位读取文件内容，一次读多个字节：");
	            // 一次读多个字符
	            char[] tempchars = new char[30];
	            int charread = 0;
	            reader = new InputStreamReader(new FileInputStream(fileName));
	            // 读入多个字符到字符数组中，charread为一次读取字符数
	            while ((charread = reader.read(tempchars)) != -1) {
	                // 同样屏蔽掉\r不显示
	                if ((charread == tempchars.length)
	                        && (tempchars[tempchars.length - 1] != '\r')) {
	                    System.out.print(tempchars);
	                } else {
	                    for (int i = 0; i < charread; i++) {
	                        if (tempchars[i] == '\r') {
	                            continue;
	                        } else {
	                            System.out.print(tempchars[i]);
	                        }
	                    }
	                }
	            }
	 
	        } catch (Exception e1) {
	            e1.printStackTrace();
	        } finally {
	            if (reader != null) {
	                try {
	                    reader.close();
	                } catch (IOException e1) {
	                }
	            }
	        }
	    }
	 
	 
	 /**
	     * 随机读取文件内容
	     * 
	     * @param fileName
	     *            文件名
	     */
	    public static void readFileByRandomAccess(String fileName) {
	        RandomAccessFile randomFile = null;
	        try {
	            System.out.println("随机读取一段文件内容：");
	            // 打开一个随机访问文件流，按只读方式
	            randomFile = new RandomAccessFile(fileName, "r");
	            // 文件长度，字节数
	            long fileLength = randomFile.length();
	            // 读文件的起始位置
	            int beginIndex = (fileLength > 4) ? 4 : 0;
	            // 将读文件的开始位置移到beginIndex位置。
	            randomFile.seek(beginIndex);
	            byte[] bytes = new byte[10];
	            int byteread = 0;
	            // 一次读10个字节，如果文件内容不足10个字节，则读剩下的字节。
	            // 将一次读取的字节数赋给byteread
	            while ((byteread = randomFile.read(bytes)) != -1) {
	                System.out.write(bytes, 0, byteread);
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            if (randomFile != null) {
	                try {
	                    randomFile.close();
	                } catch (IOException e1) {
	                }
	            }
	        }
	   }
	   
	    /**
	     * 显示输入流中还剩的字节数
	     * 
	     * @param in
	     */
	    private static void showAvailableBytes(InputStream in) {
	        try {
	            System.out.println("当前字节输入流中的字节数为:" + in.available());
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	
	   //读取目录文件名
	  private static List<String> getListFiles(String path)
	  {
		 List<String> lstFileNames=new ArrayList<String>();
		 File file=new File(path);
		 int fileNums=0;
			
		  if(file.isDirectory())
		  {
			  File []tmpList=file.listFiles();
			  for(int i=0;i<tmpList.length;i++)
			  {
				  if(tmpList[i].isFile())
				  {
					  fileNums++;
					  lstFileNames.add(tmpList[i].getPath());
				  }
			  }
		  }
		 
		System.out.println("fileNums:"+fileNums);
		  return lstFileNames;
	  }
	  
	  private static void createTable(String stock_name) throws IOException, ClassNotFoundException, SQLException
	  {
		  Date time= new java.sql.Date(new java.util.Date().getTime());
		//  BigInteger vol=new BigInteger("111111" ); 
		  DayStock stif=new DayStock(time,2.0f,2.0f,2.2f,2.3f,2,2.0f,0,0);
		  ds.createTable(stock_name,1);
	  }
	  
	  
	  private void loadDataInfileForConcept(String concept_name) throws IOException, ClassNotFoundException, SQLException
	  {
		  
		//  String stock_name=filePath.substring(12, 20);
          String wfPath="D:\\\\Javawork\\\\stock\\\\dataLoadMysql\\\\"+concept_name+".sql";
          sbDao.loadDataInfileForConcept(wfPath,concept_name);
	  }
	  
	  /**
	   *  1 导入当天交易数据   计算ma 涨幅 ，如果是新增加股票，需要创建交易表，极点表，计算极点
	 * @param type
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws NoSuchFieldException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws SecurityException 
	 */
	public int loadAllDataInfile(){
		
		CalculationStock cas =new CalculationStock(sbDao,sdDao);
		
		String dirPath = "StockData\\";
		 List<String> lstStockFileNames=null;
		 lstStockFileNames= getListFiles(dirPath);
		 String stock_time = "";
		 int stockDataNum =0;
		 String stock_source_data=null;
		 String stock_name=null;
		 
		 int isTableExist=0;
		 String loadfilePath ="";
		 String curPath=System.getProperty("user.dir");
		 curPath = curPath.replaceAll("\\\\", "/"); //转义将\ 转为/
		 int id = 0;
		
		 //判断重复导入 读取SH99999.txt文件
		 stock_time= readFileFirstLineDate();		
		 
		 try {
			id = sdDao.getDataValueIsExist("SH000001",stock_time);
			if(id > 0) {
				return 1;
			}
			 //清空stock_load_FullId表，作为下一步分析时用
			 sbDao.truncateStockLoadFullId(ConstantsInfo.StockMarket);
		 } catch (Exception e) {	
			 stockLogger.logger.fatal(e.toString());
		 }
		 

		 for (int i=0;i<lstStockFileNames.size();i++)		 
		 {
			 stock_source_data = lstStockFileNames.get(i);	
			 	 
			 int begin = stock_source_data.indexOf('\\');
			 int end = stock_source_data.indexOf('.');
			 stock_name = stock_source_data.substring(begin+1, end);//stock_source_data.substring(10, 18);
			
			 stockLogger.logger.fatal("StockFullId:"+stock_name);
			 if (stock_name.equals("SH999999")) //东兴证券999999改为000001
				 stock_name="SH000001";
			
			 try {
				 sbDao.insertStockLoadFullId(ConstantsInfo.StockMarket,stock_name);
				// if(!stock_name.equals("SH601899"))//只测试SH000001
				//	 continue;	
			
				 isTableExist=sdDao.isExistStockTable(stock_name,ConstantsInfo.TABLE_DATA_STOCK);
				 if(isTableExist==0){
					 stockLogger.logger.fatal(stock_name + " not found");
					 stockLogger.logger.fatal(stock_name + " creat table");
					 sdDao.createStockDataTable(stock_name);	
				 }
				 
				 stockDataNum = readFileByLines(stock_source_data,stock_name,ConstantsInfo.StockMarket);
				 //已经存在表，空交易数据或停牌不用导入
				 if((isTableExist>0) && (stockDataNum <= 0)) {
					 System.out.println("load stock data num:"+stockDataNum);
					 stockLogger.logger.fatal("load stock data num:"+stockDataNum);
					 continue;
				 }
				 
				 loadfilePath=curPath+"/StockLoadFile/"+stock_name+".sql";
				 sdDao.loadDatafFiletoDB(loadfilePath,stock_name);
				
				 if(isTableExist==0) {//不存在				 
					 stockLogger.logger.fatal("cal new stock table");
					//计算全部ma5 涨幅	
					 cas.calculStockAllDataForSingleStock(stock_name,ConstantsInfo.StockCalAllData); 		 		 
					 PointClass pc = new PointClass(sbDao,sdDao,spDao);
					 //计算极点
					 pc.getPiontToTableForSingleStock(stock_name,ConstantsInfo.StockCalAllData, null, null);
				 } else {				
					 cas.calculStockAllDataForSingleStock(stock_name,ConstantsInfo.StockCalCurData); //计算部分
				 }
			 } catch (Exception e) {	
				 stockLogger.logger.fatal(e.toString());
			 }
			 		
		 }

		return 0;
	  }
	
	//导入商品数据
	public int loadAllFuturesDataInfile(){
		
		CalculationStock cas =new CalculationStock(sbDao,sdDao);
		
		String dirPath = "FuturesData\\";
		 List<String> lstStockFileNames=null;
		 lstStockFileNames= getListFiles(dirPath);
		 String stock_source_data=null;
		 String stock_name=null;
		 int isTableExist=0;
		 String loadfilePath ="";
		 String curPath=System.getProperty("user.dir");
		 curPath = curPath.replaceAll("\\\\", "/"); //转义将\ 转为/
		
		 //清空stock_load_FullId表，作为下一步分析时用
		 try{
			 sbDao.truncateStockLoadFullId(ConstantsInfo.FuturesMarket);
		 } catch (Exception e) {	
			 stockLogger.logger.fatal(e.toString());
		 }

		 for (int i=0;i<lstStockFileNames.size();i++)		 
		 {
			 stock_source_data = lstStockFileNames.get(i);	
			 
			 int begin = stock_source_data.indexOf('\\');
			 begin +=2;//去掉前面两个字符
			 int end = stock_source_data.indexOf('.');
			 stock_name = stock_source_data.substring(begin+1, end);//stock_source_data.substring(10, 18);
			 
			 //去掉空格与&
			 stock_name = stock_name.replaceAll(" ", "");
			 stock_name = stock_name.replaceAll("&", "");
			 		
			 stockLogger.logger.fatal("StockFullId:"+stock_name);
			 if (stock_name.equals("SH999999")) //东兴证券999999改为000001
				 stock_name="SH000001";
			
			 try{
				 sbDao.insertStockLoadFullId(ConstantsInfo.FuturesMarket,stock_name);
				 
				 isTableExist=sdDao.isExistStockTable(stock_name,ConstantsInfo.TABLE_DATA_STOCK);
				 if(isTableExist==0)//不存在
				 {
					 stockLogger.logger.fatal(stock_name + " not found");
					 stockLogger.logger.fatal(stock_name + " creat table");
					 sdDao.createStockDataTable(stock_name);	
				 }
				 
				 readFileByLines(stock_source_data,stock_name,ConstantsInfo.FuturesMarket);				
				 loadfilePath=curPath+"/StockLoadFile/"+stock_name+".sql";
				 int ret = sdDao.loadDatafFiletoDB(loadfilePath,stock_name);
				 stockLogger.logger.fatal("load data nums:"+ret);
			
				 if(isTableExist==0) {//不存在					 
					 stockLogger.logger.fatal("cal new stock table");
					//计算全部ma5 涨幅	
					 cas.calculStockAllDataForSingleStock(stock_name,ConstantsInfo.StockCalAllData); 		 
					 //创建极点表
					 spDao.createStockPointTable(stock_name);
					 //创建汇总表
					 ssDao.createStockSummaryTable(stock_name);
					 
					 PointClass pc =new PointClass(sbDao,sdDao,spDao);
					 //计算极点
					 pc.getPiontToTableForSingleStock(stock_name,ConstantsInfo.StockCalAllData, null, null);
				 }else {				
					 cas.calculStockAllDataForSingleStock(stock_name,ConstantsInfo.StockCalCurData); //计算部分
				 }
			 
			 } catch (Exception e) {	
				 stockLogger.logger.fatal(e.toString());
			 }
		 }

		return 0;
	  }
	
	
	//某个股票导入
	public int loadSingleAllDataInfile(String loadDate) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException
	{
		
		CalculationStock cas =new CalculationStock(sbDao,sdDao);
		
		String dirPath = "StockData\\";
		 List<String> lstStockFileNames=null;
		 lstStockFileNames= getListFiles(dirPath);

		 String stock_name=null;
		 int stockFile=0;
		 int isTableExist=0;
		 String loadfilePath ="";
		 String curPath=System.getProperty("user.dir");
		 curPath = curPath.replaceAll("\\\\", "/"); //转义将\ 转为/
		

			 stock_name ="D12";
			 /*			
			 System.out.println("stock name:"+stock_name);
			 stockLogger.logger.fatal("StockFullId:"+stock_name);
			 isTableExist=sdDao.isExistStockTable(stock_name,ConstantsInfo.TABLE_DATA_STOCK);
			 if(isTableExist==0)//不存在
			 {
				 stockLogger.logger.fatal(stock_name + "not found");
				 stockLogger.logger.fatal(stock_name + "creat table");
				 sdDao.createStockDataTable(stock_name);	
			 }
	*/		
			 //删除当天的数据
			// sdDao.delStockDataDay(stock_name,loadDate);

			 //修改默认值
			 //	 sdDao.alterStockDeafaultDataType(stock_name);
			
			 loadfilePath=curPath+"/StockLoadFile/"+stock_name+".sql";
			 int ret = sdDao.loadDatafFiletoDB(loadfilePath,stock_name);
			System.out.println("ret:"+ret);
			 if(isTableExist==0) {//不存在
				 
				 stockLogger.logger.fatal("cal new stock table");
				//计算全部ma5 涨幅	
				 cas.calculStockAllDataForSingleStock(stock_name,ConstantsInfo.StockCalAllData); 		 
				 //创建极点表
				 spDao.createStockPointTable(stock_name);
				 PointClass pc =new PointClass(sbDao,sdDao,spDao);
				 //计算极点
				 pc.getPiontToTableForSingleStock(stock_name,ConstantsInfo.StockCalAllData, null, null);
			 } else {				
				 cas.calculStockAllDataForSingleStock(stock_name,ConstantsInfo.StockCalCurData); //计算部分
			 }

			stockFile++;
			
		
		 System.out.println("stockFile:"+stockFile);
		
		return 0;
	  }
	
	
	public int deleteDataInfile() throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException
	{
		
		CalculationStock cas =new CalculationStock(sbDao,sdDao);
		String dirPath = "StockData\\";
		 List<String> lstStockFileNames=null;
		 lstStockFileNames= getListFiles(dirPath);
		
		 String stock_source_data=null;
		 String stock_name=null;
		 int stockFile=0;
		 int isTableExist=0;
		 String loadfilePath ="";
		 String curPath=System.getProperty("user.dir");
		 curPath = curPath.replaceAll("\\\\", "/"); //转义将\ 转为/
	
		 for (int i=0;i<lstStockFileNames.size();i++)		 
		 {
			
			 stock_source_data = lstStockFileNames.get(i);	
		
			 stock_name=stock_source_data.substring(10, 18);
			
			if (stock_name.equals("SH999999")) //东兴证券999999改为000001
				 stock_name="SH000001";
			 
			//if(!stock_name.equals("SH000001")) //只测试SH000001
			//	 continue;
			
			 System.out.println("stock name:"+stock_name);
			
			 isTableExist=sdDao.isExistStockTable(stock_name,ConstantsInfo.TABLE_DATA_STOCK);
			 if(isTableExist==0)//不存在
			 {
				 continue;
			 }
			
			 //删除当天的数据
		//	 sdDao.delStockDataDay(stock_name,loadDate);
			 sdDao.delStockDataDay(stock_name,"2015-09-22");
			 sdDao.delStockDataDay(stock_name,"2015-09-23");
			 spDao.delStockPointDay(stock_name,"2015-09-22");
			 spDao.delStockPointDay(stock_name,"2015-09-23");
			
		 }
		 System.out.println("stockFile:"+stockFile);
		
		return 0;
	  }
	  
	  
	  //读取概念文件夹各子文件名 导入概念到概念表
	  private static void loadAllConceptInfile() throws IOException, ClassNotFoundException, SQLException
	  {
		  StockBaseDao sbDao=new StockBaseDao();		
		  
		  String dirPath = "dataConcept\\";
			 List<String> lstStockConceptNames=null;
			 lstStockConceptNames= getListFiles(dirPath);
			 ListIterator<String> lit=lstStockConceptNames.listIterator();
			 String stock_source_data=null;
			 String concept_name=null;
			 int stockConceptFile=0;
			 int txtIndex=0;
			 while(lit.hasNext())
			 {
				 stock_source_data=lit.next().toString();
				 
				// System.out.println("load file name:"+stock_source_data);
			
				 txtIndex=stock_source_data.indexOf(".txt");
				 concept_name=stock_source_data.substring(12, txtIndex);
				// if(!concept_name.equals("4G"))
				//	 continue;
				 System.out.println("concept name:"+concept_name);
				
				 StockConcept sConcept=new StockConcept(1,"",concept_name);
				 sbDao.insertStockConcept(sConcept);
				
				 stockConceptFile++;
			 }
			 System.out.println("stockConceptFile:"+stockConceptFile);
	  }
	  
	  
	//读取概念文件夹各子文件内各概念下的股票到股票概念表
	  public  int  loadAllConceptContainStockInfile() throws IOException, ClassNotFoundException, SQLException
	  {
		  	
		    String dirPath = "StockBaseData\\";//14字节
			 List<String> lstStockConceptNames=null;
			 lstStockConceptNames= getListFiles(dirPath);
			 ListIterator<String> lit=lstStockConceptNames.listIterator();
			 String stock_source_data=null;
			 String concept_name=null;
			 int stockConceptFile=0;
			 int txtIndex=0;
			 
			 while(lit.hasNext())
			 {
				 stock_source_data=lit.next().toString();		
				
				 if(!stock_source_data.contains(".txt"))
					 continue;
				 txtIndex=stock_source_data.indexOf(".txt");
				 concept_name=stock_source_data.substring(14, txtIndex);
				// if(!concept_name.equals("4G"))
				//	 continue;
				 System.out.println("concept name:"+concept_name);	
				 stockLogger.logger.fatal("概念："+concept_name);
				
				 String code=null;
				 //删除概念
				 sbDao.deleteConceptFromConceptTable(concept_name);
				 //增加概念  
				 code=sbDao.getConceptCodeFromFirstIndeustyToConceptTable(concept_name);
				 if(code==null){					 
					 stockLogger.logger.fatal("概念code：null");
					 sbDao.insertStockConceptInfo(" ",concept_name);					
				 } else {
					 stockLogger.logger.fatal("概念code："+code);
					 sbDao.insertStockConceptInfo(code,concept_name);
				 }
				 //读取导入文件内容并构造新的内容
				 readFileByLinesForStockConcept(stock_source_data);					
				 stockConceptFile++;			
			
			 }
			 System.out.println("stockConceptFile:"+stockConceptFile);
			 return 0;
	  }
	  
	 public static void loadreadIndustryInfile() throws IOException, ClassNotFoundException, SQLException
	  {
		  StockBaseDao sbDao=new StockBaseDao();	
		    String dirPath = "dataIndustry\\";
			 List<String> lstStockIndustryNames=null;
			 lstStockIndustryNames= getListFiles(dirPath);
			 ListIterator<String> lit=lstStockIndustryNames.listIterator();
			 String stock_source_data=null;
			 String industry_name=null;
			 String allindustry_name=null;
			 String newindustry_name=null;
			 int stockIndustryFile=0;
			 int txtIndex=0;
			 while(lit.hasNext())
			 {
				 stock_source_data=lit.next().toString();
				
				// System.out.println("load file name:"+stock_source_data);
				 
				 txtIndex=stock_source_data.indexOf(".txt");
				 industry_name=stock_source_data.substring(15, txtIndex);
					
				 if(industry_name.contains("Ⅲ"))
				 {	
				 	newindustry_name=industry_name.substring(0, industry_name.lastIndexOf('Ⅲ'));
				 //	System.out.println("new industry name:"+newindustry_name);	
				 	allindustry_name=newindustry_name;
				 }
				 else
					 allindustry_name=industry_name;	
				 System.out.println(allindustry_name);	
				 sbDao.insertStockIndustryFromSW(allindustry_name);
				// if(!industry_name.equals("LED"))
				//	 continue;
			 }
	  }
	  
	  //读取三级行业文件夹各子文件内各行业下的股票到股票行业表
	  private  void loadAllIndustryContainStockInfile() throws IOException, ClassNotFoundException, SQLException
	  {
		  StockBaseDao sbDao=new StockBaseDao();	
		    String dirPath = "dataIndustry\\";
			 List<String> lstStockIndustryNames=null;
			 lstStockIndustryNames= getListFiles(dirPath);
			 ListIterator<String> lit=lstStockIndustryNames.listIterator();
			 String stock_source_data=null;
			 String industry_name=null;
			 String newindustry_name=null;
			 int stockIndustryFile=0;
			 int txtIndex=0;
			 while(lit.hasNext())
			 {
				 stock_source_data=lit.next().toString();
				
				// System.out.println("load file name:"+stock_source_data);
				 
				 txtIndex=stock_source_data.indexOf(".txt");
				 industry_name=stock_source_data.substring(15, txtIndex);
				System.out.println(industry_name);		
				// if(!industry_name.equals("LED"))
				//	 continue;
				 //读取导入文件内容并构造新的内容
				 readFileByLinesForStockThirdIndustry(stock_source_data);
				 	
				 stockIndustryFile++;
				// sbDao.setCharacter("set names gb2312");
				 //导入
				// loadDataInfileForConcept(concept_name);
				 System.out.println("第stockIndustryFile:"+stockIndustryFile);
			 }
			 System.out.println("stockIndustryFile:"+stockIndustryFile);
	  }
	  
	  
	  
	public void loadDataInit() throws IOException, ClassNotFoundException, SQLException
	{
		
	}
	  
	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException {
		
		 Connection stockBaseConn = DbConn.getConnDB("stockConf/conn_base_db.ini");  
	     Connection stockDataConn = DbConn.getConnDB("stockConf/conn_data_db.ini"); 
	     Connection stockPointConn = DbConn.getConnDB("stockConf/conn_point_db.ini");  
	     Connection stockSummaryConn = DbConn.getConnDB("stockConf/conn_summary_db.ini");

		FileReader fr=new FileReader(stockBaseConn,stockDataConn,stockPointConn,stockSummaryConn);
		
		PropertyConfigurator.configure("StockConf/log4j.properties");
		java.util.Date startDate = new java.util.Date();
		System.out.println("file load start");
		stockLogger.logger.fatal("file load start");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
        String dateNowStr = sdf.format(startDate); //"2015-09-11";	
		
		
			
		
		//loadAllConceptInfile(); //导入概念名
		//fr.loadAllConceptContainStockInfile();//导入概念下对应股票
		//loadreadIndustryInfile();//读取三行行业名
		//loadAllIndustryContainStockInfile(); //导入三级行业下对应 股票
    // fr.loadAllFuturesDataInfile();
      fr.loadAllDataInfile();
 	//	fr.loadSingleAllDataInfile(dateNowStr);	
		
		//fr.deleteDataInfile();
		
		java.util.Date endDate = new java.util.Date();
		// 计算两个时间点相差的秒数
		long seconds =(endDate.getTime() - startDate.getTime())/1000;
		System.out.println("总共耗时："+seconds+"秒");
		System.out.println("file load end");		
		
		
		stockBaseConn.close();
	    stockDataConn.close();
	    stockPointConn.close();
	    stockSummaryConn.close();
		stockLogger.logger.fatal("file load end");
	}
}
