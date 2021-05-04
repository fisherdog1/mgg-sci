package com.mgg;

/**
 * A service with a rate in cents per hour
 * @author azimuth
 *
 */
public class Service extends Product
{
	private int hourlyRate;
	
	//Required to be non-prototype
	private double hours;
	private Person salesperson;
	
	public Service(String legacyId, String name, int hourlyRate) {
		super(legacyId, name);
		
		this.hourlyRate = hourlyRate;
		this.hours = 0.0;
	}
	
	public Service(String legacyId, String name, int hourlyRate, double hours, Person salesperson) {
		this(legacyId, name, hourlyRate);
		
		this.hours = hours;
		this.salesperson = salesperson;
	}
	
	public Service(Service prototype, double hours, Person salesperson) {
		this(prototype.getId(),prototype.getName(),prototype.getHourlyRate());
		
		this.hours = hours;
		this.salesperson = salesperson;
	}
	
	@Override
	public double getTaxRate() {
		return 0.0285;
	}
	
	public int getHourlyRate() {
		return this.hourlyRate;
	}

	public Person getSalesperson() {
		return salesperson;
	}
	
	public double getHours() {
		return hours;
	}
	
	@Override
	public boolean isPlaceholder()
	{
		return (hours == 0);
	}

	@Override
	public int getLineSubtotal() {
		if (isPlaceholder())
			throw new RuntimeException("Tried to calculate line total for prototype: %s\n".formatted(getName()));
		
		//TODO math
		return (int)Math.round(hourlyRate*hours);
	}
}
