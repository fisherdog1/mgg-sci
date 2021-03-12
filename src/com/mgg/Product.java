package com.mgg;

import java.util.Map;

public abstract class Product extends Legacy
{
	private String name;
	
	public Product(String legacyId) {
		super(legacyId);
		this.setPlaceholder(true);
	}
	
	public Product(String legacyId, String name) {
		super(legacyId);
		this.name = name;
		this.setPlaceholder(false);
	}
	
	public String getName() {
		return this.name;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	public double getTaxRate() {
		return 0.0;
	}
	
	public double getCustomerDiscount(CustomerType t) {
		if (t == CustomerType.Employee)
			return 0.15;
		else if (t == CustomerType.Platinum)
			return 0.10;
		else if (t == CustomerType.Gold)
			return 0.05;
		
		return 0.00;
	}
	
	public abstract int getTotalPrice(Map<String,Object> params);
}
