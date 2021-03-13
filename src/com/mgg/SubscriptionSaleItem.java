package com.mgg;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class SubscriptionSaleItem extends SaleItem<Subscription>
{
	LocalDate startDate;
	LocalDate endDate;
	
	public SubscriptionSaleItem(Subscription product, LocalDate startDate, LocalDate endDate) {
		super(product);
		this.startDate = startDate;
		this.endDate = endDate;
	}

	/**
	 * Return the fractional year duration of this subscription
	 * @return
	 */
	public double getYears() {
		return (ChronoUnit.DAYS.between(startDate, endDate) + 1) / 365.0;
	}
	
	@Override
	public int getSalePrice()
	{
		return (int)Math.round(this.getProduct().getAnnualFee() * getYears());
	}

}
