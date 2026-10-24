package com.seiryo.entity;
/**
 * 把5张报表封装到一个实体类
 * @ClassName: Statltem  
 * @Description:因为sql中查出来的是：项目名+数量/项目名+数量+金额
 * @author 李 
 * @date 2026年10月23日  
 *
 */
public class Statltem {
	//项目名称（男/岁/日期等）
	private String label;
	//数量（人数/订单数）
	private Integer value;
	//金额（没有就是0）
	private double amount;
	//占百分比
	private double percent;
	public Statltem(String label, Integer value, double amount, double percent) {
		super();
		this.label = label;
		this.value = value;
		this.amount = amount;
		this.percent = percent;
	}
	public Statltem() {
		super();
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public double getPercent() {
		return percent;
	}
	public void setPercent(double percent) {
		this.percent = percent;
	}
	@Override
	public String toString() {
		return "Statltem [label=" + label + ", value=" + value + ", amount=" + amount + ", percent=" + percent + "]";
	}

	
	
	
}
