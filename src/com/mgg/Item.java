package com.mgg;

import java.util.Map;

/**
 * New, Used, or GiftCard Product with a basePrice in cents
 * For gift cards, the base price is ignored and replaced with an amount from the individual sale information
 * @return
 */
public class Item extends Product
{
	private ProductType type;
	private int basePrice;

	public Item(String legacyId, String name, ProductType type, int basePrice) {
		super(legacyId, name);
		
		this.type = type;
		
		if (type != ProductType.GiftCard)
			this.basePrice = basePrice;
	}
	
	public Item(String legacyId) {
		super(legacyId);
	}
	
	public ProductType getProductType() {
		return type;
	}
	
	@Override
	public double getTaxRate() {
		return 0.0725;
	}
	
	/**
	 * Return base price modified by type only, applies used discount
	 * @return
	 */
	public double getBasePrice() {
		if (this.type == ProductType.Used)
			return Math.round(0.8 * (double)basePrice);
		else
			return basePrice;
	}
	
	/**
	 * Return price corrected for customerType discount and tax
	 * @param params
	 * @return
	 */
	public int getTotalPrice(Map<String,Object> params) {
		double price = 0.0;
		
		if (this.type == ProductType.GiftCard)
			price = (double)params.get("CardAmount");
		else
			price = getBasePrice();
		
		//Round to nearest cent
		double roundedPrice = Math.round((1.0 + getTaxRate()) * price);
		
		return (int)roundedPrice;
	}
}
