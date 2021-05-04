package com.mgg;

/**
 * Annual Subscription with an annual rate in cents per year
 * No tax applied
 */
public class Subscription extends Product
{
	private int annualFee;
	
	//Required to be non-prototype
	private String startDate;
	private String endDate;
	
	public Subscription(String legacyId, String name, int annualFee) {
		super(legacyId, name);
		
		this.annualFee = annualFee;
	}
	
	public Subscription(String legacyId, String name, int annualFee, String startDate, String endDate) {
		this(legacyId, name, annualFee);
		
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	public Subscription(Subscription prototype, String startDate, String endDate) {
		this(prototype.getId(), prototype.getName(), prototype.getBasePrice());
		
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	public int getBasePrice() {
		return this.annualFee;
	}

	public int getDurationDays() {
		return java.sql.Date.valueOf(startDate).toLocalDate().until(java.sql.Date.valueOf(endDate).toLocalDate()).getDays();
	}
	
	public String getProductTypeString() {
		return "SB";
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

	public String getStartDate() {
		return startDate;
	}

	public String getEndDate() {
		return endDate;
	}
}
