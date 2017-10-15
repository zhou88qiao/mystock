package dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import common.ConstantsInfo;
import common.stockLogger;

public class StockBaseDao extends BaseDao {

	public StockBaseDao() {

	}

	public StockBaseDao(Connection conn) {
		super(conn);
	}

	public int setCharacter(String str) throws IOException,
			ClassNotFoundException, SQLException {
		return super.saveOrUpdate(str);
	}

	public int createStockbaseyearinfoTable() throws IOException,
			ClassNotFoundException, SQLException {
		// strFullID SH60000 mysql不区分大小写，表名会成为小写
		String tableName = "stock_baseyearinfo";
		;

		String dropTableSql = "drop table if exists " + tableName + ";";
		super.saveOrUpdate(dropTableSql);
		String createTablesql = "create table "
				+ tableName
				+ "(id int auto_increment primary key,"
				+ // 增加id字段
				"stockFullId varchar(9) default ' ' NOT NULL,"
				+ "gongJiJin float default 0, " + "liRun float default 0, "
				+ "yuGaoContent varchar(512) default ' ' NOT NULL, "
				+ "piLouDate varchar(64) default ' ' NOT NULL"
				+ ") ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1";
		String sql = createTablesql;
		System.out.println(sql);
		return super.saveOrUpdate(createTablesql);
	}

	// 得到大盘code
	public List<String> getStockMarketFullId() throws IOException,
			ClassNotFoundException, SQLException {
		String selectSql = "select code from stock_market";
		return super.getQuery(selectSql, null);
	}

	public List<StockMarket> getStockMarket(int type) throws IOException,
			ClassNotFoundException, SQLException {

		if (type == ConstantsInfo.StockMarket) {
			return super.executeQuery("select * from stock_market",
					StockMarket.class);
		} else {
			return super.executeQuery("select * from stock_futures",
					StockMarket.class);
		}
	}

	public List<StockConcept> getStockConcept() throws IOException,
			ClassNotFoundException, SQLException {
		return super.executeQuery("select * from stock_concept",
				StockConcept.class);
	}

	public List<String> getStockFirstIndustry() throws IOException,
			ClassNotFoundException, SQLException {
		return super.getQuery("select code from stock_first_industry", null);
	}

	public String getStockFirstIndustryName(String code) throws IOException,
			ClassNotFoundException, SQLException {
		String selectSql = "select name from stock_first_industry where code='"
				+ code + "'";
		// System.out.println(selectSql);
		return getSingleQuery(selectSql, null);
	}

	// 一级行业下概念id
	public List<String> getStockFirstIndustryConceptCode1(String industryCode)
			throws IOException, ClassNotFoundException, SQLException {
		String selectSql = "select conceptCode from stock_firstindustry_to_concept where firstIndustryCode ='"
				+ industryCode + "'";
		// System.out.println(selectSql);
		return super.getQuery(selectSql, null);
	}

	// 一级行业下概念id
	public List<StockConceptInFirstIndustry> getStockFirstIndustryConceptCode(
			String industryCode) throws IOException, ClassNotFoundException,
			SQLException {
		String selectSql = "select * from stock_firstindustry_to_concept where firstIndustryCode ='"
				+ industryCode + "'";
		// System.out.println(selectSql);
		return super.executeQuery(selectSql, StockConceptInFirstIndustry.class);
	}

	// 查询当前code概念的所有股票
	public List<StockToConcept> getStockToConcept(String code)
			throws SQLException, IOException, ClassNotFoundException {
		List<StockToConcept> fullId = new ArrayList<StockToConcept>();
		String selectSql = null;

		selectSql = "select * from stock_to_concept where stockConceptCode='"
				+ code + "'";
		// stockLogger.logger.fatal("concept sql："+selectSql);
		return super.executeQuery(selectSql, StockToConcept.class);
	}

	// 查询当前con概念的所有股票
	public List<String> getConceptStock(String con) throws SQLException {
		List<String> fullId = new ArrayList<String>();
		String selectSql = null;

		selectSql = "select stockFullId from stock_to_concept where stockConcept='"
				+ con + "'";
		stockLogger.logger.fatal("concept sql：" + selectSql);
		fullId = getQuery(selectSql, null);
		return fullId;
	}

	// 从概念表查找fullId对应股票名
	public String getStockNameFromConceptTable(String fullId)
			throws SQLException {
		String selectSql = null;
		selectSql = "select stockName from stock_to_concept where stockFullId='"
				+ fullId + "'";
		String name = null;
		name = getSingleQuery(selectSql, null);
		return name;
	}

	// 查询当前所有概念
	public List<String> getAllConceptName() throws SQLException {
		List<String> name = new ArrayList<String>();
		String selectSql = null;

		selectSql = "select name from stock_concept";

		name = getQuery(selectSql, null);
		return name;
	}

	// 查询当前所有三级行业
	public List<String> getAllThirdIndustryName() throws SQLException {
		List<String> name = new ArrayList<String>();
		String selectSql = null;

		selectSql = "select thirdname from stock_third_industry";

		name = getQuery(selectSql, null);
		return name;
	}

	// 查找所有股票
	public List<StockSingle> getStockSingle() throws IOException,
			ClassNotFoundException, SQLException {
		return super.executeQuery("select * from stock_allinfo",
				StockSingle.class);
	}

	// 查找所有股票fullId+大盘4个
	public List<String> getAllStockFullId(int type) throws IOException,
			ClassNotFoundException, SQLException {
		String fullId;
		List<String> listFullId = new ArrayList<String>();
		List<String> listMarketFullId = new ArrayList<String>();
		listFullId = getStockSingleFullId(type);
		listMarketFullId = getStockMarketFullId();
		for (int i = 0; i < listMarketFullId.size(); i++) {
			fullId = listMarketFullId.get(i);
			listFullId.add(fullId);
		}
		return listFullId;
	}

	// 查找所有商品fullId
	public List<String> getAllFuturesFullId(int type) throws IOException,
			ClassNotFoundException, SQLException {
		String fullId;
		List<String> listFullId = new ArrayList<String>();
		// List<String> listMarketFullId = new ArrayList<String>();
		listFullId = getStockSingleFullId(type);
		/*
		 * listMarketFullId = getStockMarketFullId(); for (int i=0;i<listMarketFullId.size();i++) {
		 * fullId = listMarketFullId.get(i); listFullId.add(fullId); }
		 */
		return listFullId;
	}

	// 清空导入的fullId表
	public int truncateStockLoadFullId(int type) throws IOException,
			ClassNotFoundException, SQLException {
		if (type == ConstantsInfo.StockMarket) {
			return super.saveOrUpdate("truncate stock_load_fullId");
		} else {
			return super.saveOrUpdate("truncate stock_futures_load_fullId");
		}
	}

	// 三级行业股票对应表
	public int insertStockLoadFullId(int type, String fullId)
			throws IOException, ClassNotFoundException, SQLException {
		if (type == ConstantsInfo.StockMarket) {
			return super.saveOrUpdate(
					"insert into stock_load_fullId (`stockFullId`) values(?)",
					fullId);
		} else {
			return super
					.saveOrUpdate(
							"insert into stock_futures_load_fullId (`stockFullId`) values(?)",
							fullId);
		}

	}

	// 根据导入时生成的fullId表 查找所有股票fullId
	public List<String> getStockSingleFullId(int type) throws IOException,
			ClassNotFoundException, SQLException {
		String selectSql = "";
		if (type == ConstantsInfo.StockMarket) {
			selectSql = "select stockFullId from stock_load_fullId";
		} else {
			selectSql = "select stockFullId from stock_futures_load_fullid";
		}

		return super.getQuery(selectSql, null);
	}

	public List<StockSingle> lookUpStockSingleForALL(String fullId,
			String name, String industry, String concept) throws IOException,
			ClassNotFoundException, SQLException {
		String selectSql = "";
		String sql = "";
		if (fullId != null && !fullId.equals(""))
			selectSql = "stockFullId='" + fullId + "' and";
		if (name != null && !name.equals(""))
			selectSql = selectSql + " stockName='" + name + "' and";
		if (industry != null && !industry.equals(""))
			selectSql = selectSql + " thirdname='" + industry + "' and";
		if (concept != null && !concept.equals(""))
			selectSql = selectSql + " stockConcept='" + concept + "' and";
		// 去掉最后一个and
		sql = selectSql.substring(0, selectSql.length() - 3);
		System.out.println(sql);
		return super.executeQuery("select * from stock_allinfo where " + sql,
				StockSingle.class);
	}

	public List<StockIndustry> getStockIndustry() throws IOException,
			ClassNotFoundException, SQLException {
		return super.executeQuery("select * from stock_third_industry",
				StockIndustry.class);
	}

	// 查询当前indu行业的所有股票
	public List<StockToIndustry> getIndustryToStock(String code)
			throws SQLException, IOException, ClassNotFoundException {
		List<StockToIndustry> fullId = new ArrayList<StockToIndustry>();
		String selectSql = null;

		selectSql = "select * from stock_to_industry where thirdIndustryCode='"
				+ code + "'";

		return executeQuery(selectSql, StockToIndustry.class);
	}

	// 查询当前indu行业的所有股票
	public List<StockToFutures> getFuturesToStock(String code)
			throws SQLException, IOException, ClassNotFoundException {
		List<StockToFutures> fullId = new ArrayList<StockToFutures>();
		String selectSql = null;

		selectSql = "select * from stock_to_futures where futuresCode='" + code
				+ "'";

		return executeQuery(selectSql, StockToFutures.class);
	}

	// 查询当前indu行业的所有股票
	public List<String> getIndustryStockFromAllInfo(String thirdcode)
			throws SQLException {
		List<String> fullId = new ArrayList<String>();
		String selectSql = null;

		selectSql = "select stockFullId from stock_allinfo where thirdcode='"
				+ thirdcode + "'";

		fullId = getQuery(selectSql, null);
		return fullId;
	}

	// 从行业表查找fullId对应股票名
	public String getStockNameFromIndustryTable(String fullId)
			throws SQLException {
		String selectSql = null;
		selectSql = "select stockName from stock_to_industry where stockFullId='"
				+ fullId + "'";
		String name = null;
		name = getSingleQuery(selectSql, null);
		return name;
	}

	// 从一级行业概念表查找id对应概念名
	public String getConceptBaseFirstIndeustyToConceptTable(String conceptCode)
			throws SQLException {
		String selectSql = null;
		selectSql = "select baseExpect from stock_firstindustry_to_concept where conceptCode='"
				+ conceptCode + "'";
		String name = null;
		name = getSingleQuery(selectSql, null);
		return name;
	}

	// 从一级行业概念表查找id对应概念名
	public String getConceptNameFromFirstIndeustyToConceptTable(
			String conceptCode) throws SQLException {
		String selectSql = null;
		selectSql = "select conceptName from stock_firstindustry_to_concept where conceptCode='"
				+ conceptCode + "'";
		String name = null;
		name = getSingleQuery(selectSql, null);
		return name;
	}

	// 从一级行业概念表查找概念名对应id
	public String getConceptCodeFromFirstIndeustyToConceptTable(
			String conceptName) throws SQLException {
		String selectSql = null;
		selectSql = "select conceptCode from stock_firstindustry_to_concept where conceptName='"
				+ conceptName + "'";
		String name = null;
		name = getSingleQuery(selectSql, null);
		return name;
	}

	public List<StockRegional> getStockRegional() throws IOException,
			ClassNotFoundException, SQLException {
		return super.executeQuery("select * from stock_city",
				StockRegional.class);
	}

	// 从查找概念名对应id
	public int deleteConceptFromConceptTable(String conceptName)
			throws SQLException, IOException, ClassNotFoundException {
		String selectSql = null;
		selectSql = "delete from stock_concept where name='" + conceptName
				+ "'";
		return super.saveOrUpdate(selectSql);
	}

	// 插入股票概念对应表
	public int insertStockConcept(StockConcept sConcept) throws IOException,
			ClassNotFoundException, SQLException {
		return super.saveOrUpdate(
				"insert into stock_concept (`code`, `name`) values(?,?)",
				sConcept.getCode(), sConcept.getName());
	}

	public int insertStockConceptInfo(String code, String name)
			throws IOException, ClassNotFoundException, SQLException {
		return super.saveOrUpdate(
				"insert into stock_concept (`code`, `name`) values(?,?)", code,
				name);
	}

	public int insertStockIndustryFromSW(String industryName)
			throws IOException, ClassNotFoundException, SQLException {
		return super.saveOrUpdate(
				"insert into stock_industry_from_sw (`name`) values(?)",
				industryName);
	}

	// 新股票增加到stock_allinfo
	public int insertStockSingle(StockSingle sStock) throws IOException,
			ClassNotFoundException, SQLException {
		return super
				.saveOrUpdate(
						"insert into stock_allinfo (`stockFullId`, `stockName`,`thirdcode`, `thirdname`, `secondcode`, `secondname`, `firstcode`, `firstname`, `stockConcept`, `enableMarginTrading`) values(?,?,?,?,?,?,?,?,?,?)",
						sStock.getStockFullId(), sStock.getStockName(), sStock
								.getThirdCode(), sStock.getThirdName(), sStock
								.getSecondCode(), sStock.getSecondName(),
						sStock.getFirstCode(), sStock.getFirstName(), sStock
								.getStockConcept(), sStock
								.getEnableMarginTrading());
	}

	// 删除股票stock_allinfo
	public int deleteStockSingle(StockSingle sStock) throws IOException,
			ClassNotFoundException, SQLException {
		return super
				.saveOrUpdate("delete from stock_allinfo where stockFullId='"
						+ sStock.getStockFullId() + "'");
	}

	// 清空三级行业行业股票对应表
	public int truncateStocktoIndustry() throws IOException,
			ClassNotFoundException, SQLException {
		return super.saveOrUpdate("truncate stock_to_industry");
	}

	// 三级行业股票对应表
	public int insertStockSingleToIndustry(StockSingle sStock)
			throws IOException, ClassNotFoundException, SQLException {
		return super
				.saveOrUpdate(
						"insert into stock_to_industry (`stockFullId`, `stockName`,`thirdIndustryCode`,`thirdIndustryName`) values(?,?,?,?)",
						sStock.getStockFullId(), sStock.getStockName(), sStock
								.getThirdCode(), sStock.getThirdName());
	}

	// 更新allinfo信息
	public int updateStockSingle(StockSingle sStock) throws IOException,
			ClassNotFoundException, SQLException {

		String sql = "update stock_allinfo set thirdcode='"
				+ sStock.getThirdCode() + "', thirdname='"
				+ sStock.getThirdName() + "',secondcode='"
				+ sStock.getSecondCode() + "',secondname='"
				+ sStock.getSecondName() + "', firstcode='"
				+ sStock.getFirstCode() + "',firstname='"
				+ sStock.getFirstName() + "',stockConcept='"
				+ sStock.getStockConcept() + "',enableMarginTrading='"
				+ sStock.getEnableMarginTrading() + "' where stockFullId='"
				+ sStock.getStockFullId() + "'";
		System.out.println(sql);
		return super.saveOrUpdate(sql);
	}

	// 清空股票两融
	public int truncateStockTwoRong() throws IOException,
			ClassNotFoundException, SQLException {
		return super.saveOrUpdate("truncate stock_to_tworong");
	}

	// 是否两融表
	public int insertStockTwoRong(String fullId, String name, int enable)
			throws IOException, ClassNotFoundException, SQLException {
		return super
				.saveOrUpdate(
						"insert into stock_to_tworong (`stockFullId`, `stockName`, `enableRong`) values(?,?,?)",
						fullId, name, enable);
	}

	public int insertStockMarket(StockMarket sMarket) throws IOException,
			ClassNotFoundException, SQLException {
		return super.saveOrUpdate(
				"insert into stock_market (`code`, `name`) values(?,?)",
				sMarket.getCode(), sMarket.getName());
	}

	// 清空一级行业表
	public int truncateStockFirstIndustry() throws IOException,
			ClassNotFoundException, SQLException {
		return super.saveOrUpdate("truncate stock_first_industry");
	}

	// 清空表
	public int truncateStockTable(String tableName) throws IOException,
			ClassNotFoundException, SQLException {

		String sql = "truncate " + tableName;
		return super.saveOrUpdate(sql);
	}

	public int updateStock_baseyearinfo_lirun(String fullId, float lirun)
			throws IOException, ClassNotFoundException, SQLException {
		String sql = "select id from stock_baseyearinfo where stockFullId = '"
				+ fullId + "'";
		int id = getSingleIntQuery(sql, null);
		if (id <= 0) {
			return super
					.saveOrUpdate(
							"insert into stock_baseyearinfo (`stockFullId`, `liRun`) values(?,?)",
							fullId, lirun);
		} else {
			sql = "UPDATE stock_baseyearinfo SET liRun = '" + lirun
					+ "' WHERE stockFullId = '" + fullId + "'";
			return super.saveOrUpdate(sql);
		}
	}

	public int updateStock_baseyearinfo_GongJiJin(String fullId, float gongJiJin)
			throws IOException, ClassNotFoundException, SQLException {

		String sql = "select id from stock_baseyearinfo where stockFullId = '"
				+ fullId + "'";
		int id = getSingleIntQuery(sql, null);
		if (id <= 0) {
			return super
					.saveOrUpdate(
							"insert into stock_baseyearinfo (`stockFullId`, `gongJiJin`) values(?,?)",
							fullId, gongJiJin);
		} else {

			sql = "UPDATE stock_baseyearinfo SET gongJiJin = '" + gongJiJin
					+ "' WHERE stockFullId = '" + fullId + "'";
			return super.saveOrUpdate(sql);
		}
	}

	public int updateStock_baseyearinfo_yuGaoContent(String fullId,
			String yuGaoContent) throws IOException, ClassNotFoundException,
			SQLException {
		String sql = "select id from stock_baseyearinfo where stockFullId = '"
				+ fullId + "'";
		int id = getSingleIntQuery(sql, null);
		if (id <= 0) {
			return super
					.saveOrUpdate(
							"insert into stock_baseyearinfo (`stockFullId`, `yuGaoContent`) values(?,?)",
							fullId, yuGaoContent);
		} else {

			sql = "UPDATE stock_baseyearinfo SET yuGaoContent = '"
					+ yuGaoContent + "' WHERE stockFullId = '" + fullId + "'";
			return super.saveOrUpdate(sql);
		}
	}

	public int updateStock_baseyearinfo_piLouDate(String fullId,
			String piLouDate) throws IOException, ClassNotFoundException,
			SQLException {
		String sql = "select id from stock_baseyearinfo where stockFullId = '"
				+ fullId + "'";
		int id = getSingleIntQuery(sql, null);
		if (id <= 0) {
			return super
					.saveOrUpdate(
							"insert into stock_baseyearinfo (`stockFullId`, `piLouDate`) values(?,?)",
							fullId, piLouDate);
		} else {
			sql = "UPDATE stock_baseyearinfo SET piLouDate = '" + piLouDate
					+ "' WHERE stockFullId = '" + fullId + "'";
			return super.saveOrUpdate(sql);
		}
	}

	// 插入公积金表
	public int insertStock_baseyearinfo(String code, float gongJiJin)
			throws IOException, ClassNotFoundException, SQLException {
		return super
				.saveOrUpdate(
						"insert into stock_baseyearinfo (`stockFullId`, `gongJiJin`) values(?,?)",
						code, gongJiJin);
	}

	// 插入一级行业
	public int insertStockFirstIndustry(String code, String name)
			throws IOException, ClassNotFoundException, SQLException {
		return super
				.saveOrUpdate(
						"insert into stock_first_industry (`code`, `name`) values(?,?)",
						code, name);
	}

	// 清空二级行业表
	public int truncateStockSecondIndustry() throws IOException,
			ClassNotFoundException, SQLException {
		return super.saveOrUpdate("truncate stock_second_industry");
	}

	public int insertStockSecondIndustry(String code, String name,
			String firstcode, String firstname) throws IOException,
			ClassNotFoundException, SQLException {
		return super
				.saveOrUpdate(
						"insert into stock_second_industry (`code`, `name`,`firstcode`,`firstname`) values(?,?,?,?)",
						code, name, firstcode, firstname);
	}

	// 清空三级行业表
	public int truncateStockThirdIndustry() throws IOException,
			ClassNotFoundException, SQLException {
		return super.saveOrUpdate("truncate stock_third_industry");
	}

	public int insertStockThirdIndustry(String code, String name,
			String secondcode, String secondname, String firstcode,
			String firstname, String basexpect, String main, String psychology,
			String risk, String potential, String faucet) throws IOException,
			ClassNotFoundException, SQLException {
		return super
				.saveOrUpdate(
						"insert into stock_third_industry (`thirdcode`, `thirdname`,`secondcode`,`secondname`,`firstcode`,`firstname`,`baseExpect`,`main`,`psychology`,`risk`,`potential`,`faucet`) values(?,?,?,?,?,?,?,?,?,?,?,?)",
						code, name, secondcode, secondname, firstcode,
						firstname, basexpect, main, psychology, risk,
						potential, faucet);
	}

	public int loadDataInfileForConcept(String sqlPath, String stockName)
			throws IOException, ClassNotFoundException, SQLException {
		String tableConceptName = "stock_to_concept";
		// String sql ="load data infile +"+sqlPath +" into table "+tableName+"`
		// Lines Terminated By '+"r"n'";
		String sql = "load data infile \"" + sqlPath + "\" into table "
				+ tableConceptName
				+ " FIELDS TERMINATED BY '\\t' LINES TERMINATED BY '\\r\\n'"
				+ " (`stockFullId`,`stockName`,`stockConcept`);";
		System.out.println("loadData sql:" + sql);
		return super.saveOrUpdate(sql);
	}

	// 清空概念对应股票表
	public int truncateStockToConcept() throws IOException,
			ClassNotFoundException, SQLException {
		return super.saveOrUpdate("truncate stock_to_concept");
	}

	public int insertStockToConcept(String fullId, String name,
			String firstIndustrycode, String conceptcode, String concept)
			throws IOException, ClassNotFoundException, SQLException {
		return super
				.saveOrUpdate(
						"insert into stock_to_concept (`stockFullId`, `stockName`,`stockFirstIndustryCode`,`stockConceptCode`,`stockConceptName`) values(?,?,?,?,?)",
						fullId, name, firstIndustrycode, conceptcode, concept);
	}

	public int deleteStockToConcept(String code, String concept)
			throws IOException, ClassNotFoundException, SQLException {
		String sql = "delete  from  stock_to_concept where stockConceptCode='"
				+ code + "'";
		return super.saveOrUpdate(sql);
	}

	public int addStockToIndustry(String fullId, String name, String industry)
			throws IOException, ClassNotFoundException, SQLException {
		return super
				.saveOrUpdate(
						"insert into stock_to_industry (`stockFullId`, `stockName`,`stockIndustry`) values(?,?,?)",
						fullId, name, industry);
	}

	public int truncateStockBaseInfo() throws IOException,
			ClassNotFoundException, SQLException {
		return super.saveOrUpdate("truncate stock_basefaceinfo");
	}

	public int addStockBaseInfo(String fullId, String baseface, String main,
			String psychology, String risk, String potential, String faucet)
			throws IOException, ClassNotFoundException, SQLException {
		return super
				.saveOrUpdate(
						"insert into stock_basefaceinfo (`stockFullId`, `baseExpect`,`main`,`psychology`,`risk`,`potential`,`faucet`) values(?,?,?,?,?,?,?)",
						fullId, baseface, main, psychology, risk, potential,
						faucet);
	}

	// 一级行业对应概念表
	public int addStockFirstIndustryToConcept(String firstIndustryCode,
			String firstIndustryName, String conceptCode, String conceptName,
			String baseExpect, String main, String psychology, String risk,
			String potential, String faucet) throws IOException,
			ClassNotFoundException, SQLException {
		return super
				.saveOrUpdate(
						"insert into stock_firstindustry_to_concept (`firstIndustryCode`, `firstIndustryName`, `conceptCode`, `conceptName`,`baseExpect`,`main`,`psychology`,`risk`,`potential`,`faucet`) values(?,?,?,?,?,?,?,?,?,?)",
						firstIndustryCode, firstIndustryName, conceptCode,
						conceptName, baseExpect, main, psychology, risk,
						potential, faucet);
	}

	// 期货与商品对应 表
	public int addStockToFutures(String code, String name, String futuresName,
			String futuresCode,String baseExpect, String main, String psychology, String risk,
			String potential, String faucet) throws IOException, ClassNotFoundException,
			SQLException {
		return super
				.saveOrUpdate(
						"insert into stock_to_futures (`code`, `name`, `futuresCode`, `futuresName`,`baseExpect`,`main`,`psychology`,`risk`,`potential`,`faucet`) values(?,?,?,?,?,?,?,?,?,?)",
						code, name, futuresCode, futuresName, baseExpect, main, psychology, risk,
						potential, faucet);
	}

	/*
	 * public int addStockToFutures(String code,String name,String
	 * futuresName,String futuresCode,String baseExpect,String main,String
	 * psychology,String risk,String potential,String faucet) throws
	 * IOException, ClassNotFoundException, SQLException { return
	 * super.saveOrUpdate( "insert into stock_to_futures (`code`, `name`,
	 * `futuresCode`,
	 * `futuresName`,`baseExpect`,`main`,`psychology`,`risk`,`potential`,`faucet`)
	 * values(?,?,?,?,?,?,?,?,?,?)",
	 * code,name,futuresCode,futuresName,baseExpect,main,psychology,risk,potential,faucet); }
	 */

	public int truncateStockFirstIndustryToConcept() throws IOException,
			ClassNotFoundException, SQLException {
		return super.saveOrUpdate("truncate stock_firstindustry_to_concept");
	}

	public int truncateStockToFutures() throws IOException,
			ClassNotFoundException, SQLException {
		return super.saveOrUpdate("truncate stock_to_futures");
	}

	public int addStockMarket(int type, String code, String name,
			String baseExcept, String main, String psychology, String risk,
			String potential, String faucet) throws IOException,
			ClassNotFoundException, SQLException {

		if (type == ConstantsInfo.StockMarket) {
			return super
					.saveOrUpdate(
							"insert into stock_market (`code`, `name`, `baseExpect`,`main`,`psychology`,`risk`,`potential`,`faucet`) values(?,?,?,?,?,?,?,?)",
							code, name, baseExcept, main, psychology, risk,
							potential, faucet);
		} else {
			return super
					.saveOrUpdate(
							"insert into stock_futures (`code`, `name`, `baseExpect`,`main`,`psychology`,`risk`,`potential`,`faucet`) values(?,?,?,?,?,?,?,?)",
							code, name, baseExcept, main, psychology, risk,
							potential, faucet);
		}
	}

	public int truncateMarket(int type) throws IOException,
			ClassNotFoundException, SQLException {

		if (type == ConstantsInfo.StockMarket) {
			return super.saveOrUpdate("truncate stock_market");
		} else {
			return super.saveOrUpdate("truncate stock_futures");
		}
	}

	public StockIndustry lookUpIndustry(String thridName) throws IOException,
			ClassNotFoundException, SQLException {
		String selectSql = null;
		selectSql = "select * from stock_third_industry  where thirdname='"
				+ thridName + "'";
		System.out.println(selectSql);
		return super.executeSingleQuery(selectSql, StockIndustry.class);
	}

	public StockIndustry lookUpIndustryFromThirdCode(String thirdCode)
			throws IOException, ClassNotFoundException, SQLException {
		String selectSql = null;
		selectSql = "select * from stock_third_industry  where thirdcode='"
				+ thirdCode + "'";
		// System.out.println(selectSql);
		return super.executeSingleQuery(selectSql, StockIndustry.class);
	}

	public StockSingle lookUpStockSingle(String stockCode) throws IOException,
			ClassNotFoundException, SQLException {
		String selectSql = null;
		selectSql = "select * from stock_allinfo  where stockFullId='"
				+ stockCode + "'";
		// System.out.println(selectSql);
		return super.executeSingleQuery(selectSql, StockSingle.class);
	}

	public StockConcept lookUpStockConcept(String name) throws IOException,
			ClassNotFoundException, SQLException {
		String selectSql = null;
		selectSql = "select * from stock_concept  where name='" + name + "'";
		System.out.println(selectSql);
		return super.executeSingleQuery(selectSql, StockConcept.class);
	}

	public int lookUpStockTwoRong(String stockCode) throws IOException,
			ClassNotFoundException, SQLException {
		String selectSql = "select enableRong from stock_to_tworong where stockFullId='"
				+ stockCode + "'";
		// System.out.println(selectSql);
		return getSingleIntQuery(selectSql, null);
	}

	public StockBaseFace lookUpStockBaseFace(String stockCode)
			throws IOException, ClassNotFoundException, SQLException {
		String selectSql = null;
		selectSql = "select * from stock_basefaceinfo  where stockFullId='"
				+ stockCode + "'";
		// System.out.println(selectSql);
		return super.executeSingleQuery(selectSql, StockBaseFace.class);
	}

	public StockBaseYearInfo lookUpStockBaseYearInfo(String stockCode)
			throws IOException, ClassNotFoundException, SQLException {
		String selectSql = null;
		selectSql = "select * from stock_baseyearinfo  where stockFullId='"
				+ stockCode + "'";
		// System.out.println(selectSql);
		return super.executeSingleQuery(selectSql, StockBaseYearInfo.class);
	}

	public int getMarketType(String fullId) {

		if (fullId.equals("sh000001") || fullId.equals("sz399001")
				|| fullId.equals("sz399005") || fullId.equals("sz399006"))
			return ConstantsInfo.DPMarket;
		else if (fullId.contains("SH60"))
			return ConstantsInfo.SHMarket;
		else if (fullId.contains("SZ000"))
			return ConstantsInfo.SZMarket;
		else if (fullId.contains("SZ002"))
			return ConstantsInfo.ZXMarket;
		else if (fullId.contains("SZ300"))
			return ConstantsInfo.CYMarket;
		else
			return ConstantsInfo.SHMarket; // 商品 或其他都 当成上证股票
	}

	// 市场编号
	public int getMarketNum(String fullId) {
		int type = 0;
		if (fullId.equals("sh000001"))
			type = 0;
		else if (fullId.equals("sz399001"))
			type = 1;
		else if (fullId.equals("sz399006"))
			type = 2;
		else if (fullId.equals("sz399005"))
			type = 3;
		return type;
	}

}
