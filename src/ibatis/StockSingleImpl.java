package ibatis;

import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

import dao.StockSingle;

public class StockSingleImpl implements StockSingleDao {

	private static SqlMapClient sqlMapClient = null;
	
	// ∂¡»°≈‰÷√Œƒº˛
	static {
		try {	
			Reader reader = Resources.getResourceAsReader("ibatis.SqlMapConfig.xml");
			sqlMapClient = SqlMapClientBuilder.buildSqlMapClient(reader);
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public List<StockSingle> getAllStockSingle() {
		List<StockSingle> sSingle = null;
		try {
			sSingle = sqlMapClient.queryForList("getAll");
		} catch (SQLException e) {	
			e.printStackTrace();
		}
		return sSingle;
	}

	@Override
	public List<String> getStockSingleAllFullId() {
		List<String> fullId = null;
		try {
			fullId = sqlMapClient.queryForList("getAllFullId");
		} catch (SQLException e) {	
			e.printStackTrace();
		}
		return fullId;
	}

	

}
