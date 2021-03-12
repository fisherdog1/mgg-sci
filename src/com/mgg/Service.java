package com.mgg;

import java.util.Map;

/**
 * A service with a rate in cents per hour
 * @author azimuth
 *
 */
public class Service extends Product
{
	private int hourlyRate;
	
	public Service(String legacyId, String name, int hourlyRate) {
		super(legacyId, name);
		
		this.hourlyRate = hourlyRate;
	}
	
	public Service(String legacyId) {
		super(legacyId);
	}
	
	@Override
	public double getTaxRate() {
		return 0.0285;
	}
	
	public int getHourlyRate() {
		return this.hourlyRate;
	}

	@Override
	public int getTotalPrice(Map<String, Object> params)
	{
		CustomerType t = (CustomerType)params.get("CustomerType");
		double hours = (double)params.get("Hours");
		
		//Round to nearest cent
		double roundedPrice = Math.round((1.0 - getCustomerDiscount(t)) * (1.0 + getTaxRate()) * (double)hourlyRate * hours);
		
		return (int)roundedPrice;
	}
	
}
