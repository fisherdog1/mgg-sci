package com.mgg;

/**
 * Annual Subscription with an annual rate in cents
 */
public class Subscription extends SaleItem
{
	private int annualFee;
	
	public Subscription(String legacyId, String name, int annualFee) {
		super(legacyId, name);
		
		this.annualFee = annualFee;
	}
	
	public int getAnnualFee() {
		return this.annualFee;
	}
}
