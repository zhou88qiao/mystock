package excel.all_v2;

//最小最大相关信息
public class StockMinMaxValue {
	
	private String minDate; //2015年最低最高点位
	private float minPrice;
	private String maxDate;
	private float maxPrice;
	private float minMaxRatio; //最低最高点比
	

	public StockMinMaxValue(){
			
	}
	
	public StockMinMaxValue(String minDate,  float minPrice,  String maxDate, float maxPrice, float minMaxRatio){
		this.minDate = minDate;
		this.minPrice = minPrice;
		this.maxDate =maxDate;
		this.maxPrice = maxPrice;
		this.minMaxRatio = minMaxRatio;
	}
	
	public String getMinDate() {
		return minDate;
	}
	public void setMinDate(String minDate) {
		this.minDate = minDate;
	}
	public float getMinPrice() {
		return minPrice;
	}
	public void setMinPrice(float minPrice) {
		this.minPrice = minPrice;
	}
	public String getMaxDate() {
		return maxDate;
	}
	public void setMaxDate(String maxDate) {
		this.maxDate = maxDate;
	}
	public float getMaxPrice() {
		return maxPrice;
	}
	public void setMaxPrice(float maxPrice) {
		this.maxPrice = maxPrice;
	}
	public float getMinMaxRatio() {
		return minMaxRatio;
	}
	public void setMinMaxRatio(float minMaxRatio) {
		this.minMaxRatio = minMaxRatio;
	}

}
