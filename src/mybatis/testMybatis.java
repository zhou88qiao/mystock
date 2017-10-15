package mybatis;

import java.io.IOException;
import java.util.List;

public class testMybatis {

	
	public static void main(String[] args) {
		StockSingleDao stockSingleDao = new StockSingleDao();
		try {
			stockSingleDao.setup();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("≤‚ ‘≤È—ØÀ˘”–");
		List<String> fullId = stockSingleDao.getStockSingleAllFullId();
		for (String str : fullId) {
			System.out.println(str);
		}

	}

}
