package com.mgg;

/**
 * A sale item which has a fixed amount loaded, such as a gift card
 * @author azimuth
 *
 */
public class ChargedSaleItem extends SaleItem<Item>
{
	private int charge;
	
	public ChargedSaleItem(Item item, int charge) {
		super(item);
		this.charge = charge;
	}
	
	@Override
	public int getSalePrice()
	{
		return charge;
	}
}
