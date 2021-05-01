package com.mgg;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Annual Subscription with an annual rate in cents per year
 * No tax applied
 */
public class Subscription extends Product
{
	private int annualFee;
	
	//Required to be non-prototype
	private LocalDate startDate;
	private LocalDate endDate;
	
	public Subscription(String legacyId, String name, int annualFee) {
		super(legacyId, name);
		
		this.annualFee = annualFee;
	}
	
	public Subscription(String legacyId, String name, int annualFee, LocalDate startDate, LocalDate endDate) {
		this(legacyId, name, annualFee);
		
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	public Subscription(Subscription prototype, LocalDate startDate, LocalDate endDate) {
		this(prototype.getId(), prototype.getName(), prototype.getAnnualFee());
		
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	public int getAnnualFee() {
		return this.annualFee;
	}

	public int getDurationDays() {
		return (int)ChronoUnit.DAYS.between(startDate, endDate) + 1;
	}
	
	@Override
	public int getLineSubtotal() {
		if (isPlaceholder())
			throw new RuntimeException("Tried to calculate line total for prototype: %s\n".formatted(getName()));
		
		return (int)Math.round(getDurationDays() / 365.0 * annualFee);
	}

	@Override
	public boolean isPlaceholder()
	{
		return (startDate == null) || (endDate == null);
	}
}
