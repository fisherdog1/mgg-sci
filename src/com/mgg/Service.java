package com.mgg;

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
}
