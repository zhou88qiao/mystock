package dao;

public class StockConceptInFirstIndustry {
	
	private int  id;
	private String firstIndustryCode;//一级
	private String firstIndustryName;
	private String conceptCode; //概念
	private String conceptName;
	private String baseExpect;//基本预期
	private String main; //主力
	private String psychology; //心理
	private String risk; //风险
	private String potential; //潜力
	private String faucet; //龙头
	
	
	
	public StockConceptInFirstIndustry()
	{
		
	}

	
	
	public String getFirstIndustryCode() {
		return firstIndustryCode;
	}
	public void setFirstIndustryCode(String firstIndustryCode) {
		this.firstIndustryCode = firstIndustryCode;
	}
	public String getFirstIndustryName() {
		return firstIndustryName;
	}
	public void setFirstIndustryName(String firstIndustryName) {
		this.firstIndustryName = firstIndustryName;
	}
	public String getConceptCode() {
		return conceptCode;
	}
	public void setConceptCode(String conceptCode) {
		this.conceptCode = conceptCode;
	}
	public String getConceptName() {
		return conceptName;
	}
	public void setConceptName(String conceptName) {
		this.conceptName = conceptName;
	}
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
}
