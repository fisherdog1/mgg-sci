package com.mgg;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

/**
 * Annual Subscription with an annual rate in cents per year
 * No tax applied
 */
public class Subscription extends Product
{
	private int annualFee;
	
	public Subscription(String legacyId, String name, int annualFee) {
		super(legacyId, name);
		
		this.annualFee = annualFee;
	}
	
	public Subscription(String legacyId) {
		super(legacyId);
	}
	
	public int getAnnualFee() {
		return this.annualFee;
	}

	@Override
	public int getTotalPrice(Map<String, Object> params)
	{
		CustomerType t = (CustomerType)params.get("CustomerType");
		LocalDate startDate = (LocalDate)params.get("StartDate");
		LocalDate endDate = (LocalDate)params.get("EndDate");
		
		
		double price = annualFee * ChronoUnit.DAYS.between(startDate, endDate) / 365.0;
		
		//Round to nearest cent
		double roundedPrice = Math.round((1.0 - getCustomerDiscount(t)) * (1.0 + getTaxRate()) * price);
		
		return (int)roundedPrice;
	}
}
