package com.mgg;

/**
 * A service with a rate in cents per hour
 * @author azimuth
 *
 */
public class Service extends SaleItem
{
	private int hourlyRate;
	
	public Service(String legacyId, String name, int hourlyRate) {
		super(legacyId, name);
		
		this.hourlyRate = hourlyRate;
	}
	
	public Service(String legacyId) {
		super(legacyId);
	}
	
	public int getHourlyRate() {
		return this.hourlyRate;
	}
}
