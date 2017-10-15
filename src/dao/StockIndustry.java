package dao;

public class StockIndustry {
	
	private int  id;
	private String thirdcode;//三级
	private String thirdname;
	private String secondcode; //二级
	private String secondname;
	private String firstcode; //一级
	private String firstname;
	private String baseExpect;//基本预期
	private String main; //主力
	private String psychology; //心理
	private String risk; //风险
	private String potential; //潜力
	private String faucet; //龙头
	
	
	
	public String getMain() {
		return main;
	}
	public void setMain(String main) {
		this.main = main;
	}
	public String getPsychology() {
		return psychology;
	}
	public void setPsychology(String psychology) {
		this.psychology = psychology;
	}
	public String getRisk() {
		return risk;
	}
	public void setRisk(String risk) {
		this.risk = risk;
	}
	public String getPotential() {
		return potential;
	}
	public void setPotential(String potential) {
		this.potential = potential;
	}
	public String getFaucet() {
		return faucet;
	}
	public void setFaucet(String faucet) {
		this.faucet = faucet;
	}
	public String getBaseExpect() {
		return baseExpect;
	}
	public void setBaseExpect(String baseExpect) {
		this.baseExpect = baseExpect;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getThirdcode() {
		return thirdcode;
	}
	public void setThirdcode(String thirdcode) {
		this.thirdcode = thirdcode;
	}
	public String getThirdname() {
		return thirdname;
	}
	public void setThirdname(String thirdname) {
		this.thirdname = thirdname;
	}
	public String getSecondcode() {
		return secondcode;
	}
	public void setSecondcode(String secondcode) {
		this.secondcode = secondcode;
	}
	public String getSecondname() {
		return secondname;
	}
	public void setSecondname(String secondname) {
		this.secondname = secondname;
	}
	public String getFirstcode() {
		return firstcode;
	}
	public void setFirstcode(String firstcode) {
		this.firstcode = firstcode;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}	

}
