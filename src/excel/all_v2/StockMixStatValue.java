package excel.all_v2;


//针对日、周、月统计需要混合计算的 
public class StockMixStatValue {
	
	
	private String priComState; //前极点状态组合
	private String priState; //前极点组合
	
	
	private String comPSState;//状态与极点两者结合的数字，方便排序
	private String comState; //疑似状态组合
	private String psState; //疑似极点组合
	
	
	private String buySaleGrade; //分级买卖
	private String buySaleState; //月，周顶部，连续上涨等
	
	
	public StockMixStatValue(){
		
	}
	
	public StockMixStatValue(String priComState,String priState,String comPSState,String comState,String psState,String buySaleGrade,String buySaleState){
		this.priComState = priComState;
		this.priState = priState;
		this.comPSState = comPSState;
		this.comState = comState;
		this.psState = psState;
		this.buySaleGrade = buySaleGrade;
		this.buySaleState = buySaleState;
	}
	
	public String getBuySaleGrade() {
		return buySaleGrade;
	}

	public void setBuySaleGrade(String buySaleGrade) {
		this.buySaleGrade = buySaleGrade;
	}

	public String getBuySaleState() {
		return buySaleState;
	}

	public void setBuySaleState(String buySaleState) {
		this.buySaleState = buySaleState;
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

	public String getComPSState() {
		return comPSState;
	}

	public void setComPSState(String comPSState) {
		this.comPSState = comPSState;
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
