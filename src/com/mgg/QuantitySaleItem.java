package com.mgg;

/**
 * Because i didn't want to call it ItemSaleItem
 * Represents a sale item that is a new or used physical item or a gift card
 * @author azimuth
 *
 */
public class QuantitySaleItem extends SaleItem<Item>
{
	private int quantity;
	
	public QuantitySaleItem(Item product, int quantity) {
		super(product);
		this.quantity = quantity;
	}

	public int getQuantity() {
		return quantity;
	}
	
	@Override
	public int getSalePrice() {
		return (int)Math.round(this.getProduct().getBasePrice() * quantity);
	}
}
