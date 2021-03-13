package com.mgg;

public class ServiceSaleItem extends SaleItem<Service>
{
	private double hours;
	private String servicepersonId;
	
	public ServiceSaleItem(Service product, double hours, String servicepersonId) {
		super(product);
		this.hours = hours;
		this.servicepersonId = servicepersonId;
	}

	public double getHours() {
		return this.hours;
	}
	
	public String getServicepersonId() {
		return this.servicepersonId;
	}
	
	@Override
	public int getSalePrice() {
		return (int)Math.round(this.getProduct().getHourlyRate() * hours);
	}

}
