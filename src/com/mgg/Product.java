package com.mgg;

/**
 * New, Used, or GiftCard Product with a basePrice in cents
 * @return
 */
public class Product extends SaleItem
{
	private ProductType type;
	private int basePrice;

	public Product(String legacyId, String name, ProductType type, int basePrice) {
		super(legacyId, name);
		
		this.type = type;
		
		if (type != ProductType.GiftCard)
			this.basePrice = basePrice;
	}
	
	public ProductType getProductType() {
		return type;
	}
	
	public int getBasePrice() {
		return basePrice;
	}
}
