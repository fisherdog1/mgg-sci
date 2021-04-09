package com.mgg;

/**
 * Each subclass of SaleItem should provide a method for determining total cost and tax, which will depend on 
 * the Product type. 
 * @author azimuth
 *
 */
public abstract class SaleItem<T extends Product>
{
	private T product;
	
	public SaleItem(T product) {
		if (product == null)
			throw new RuntimeException("SaleItem cannot be created with null Product");
		
		this.product = product;
	}
	
	
	public T getProduct() {
		return this.product;
	}

	/**
	 * Required to replace placeholder product with an actual product from SalesReport
	 * TODO: placeholder functionality should eventually be reworked
	 * @param product
	 */
	public void setProduct(T product) {
		this.product = product;
	}
	
	/**
	 * Returns the sale price in cents before tax. Should be determined by the type of product.
	 * @return
	 */
	public abstract int getSalePrice();
	
	/**
	 * Return tax in cents. Should be determined by the type of product.
	 * This does not need overridden by subclasses because the type of product provides
	 * the method for the tax rate, and the subclass implements the sale price
	 * @return
	 */
	public int getTax() {
		return (int)Math.round(this.product.getTaxRate() * this.getSalePrice());
	}
	
	/**
	 * Returns price including tax, should not differ from getSalePrice() + getTax()
	 * @return
	 */
	public int getTotalPrice() {
		return (int)Math.round((1.0 + this.product.getTaxRate()) * this.getSalePrice());
	}
}
