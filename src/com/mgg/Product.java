package com.mgg;

public abstract class Product extends Legacy
{
	private String name;
	
	public Product(String legacyId, String name) {
		super(legacyId);
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	/**
	 * Can be overridden by subclasses which require a different rate
	 * @return
	 */
	public double getTaxRate() {
		return 0.0;
	}
	
	/**
	 * Must be implemented by subclasses to identify whether this Product is a prototype 
	 * (fe it does not have all the information required to be a line item on a report)
	 * @return
	 */
	public abstract boolean isPlaceholder();
	
	/**
	 * Returns the total price for this line item excluding tax
	 * @return
	 */
	public abstract int getLineSubtotal();
	
	/**
	 * Return the total with tax
	 * @return
	 */
	public int getLineTotal() {
		return getLineSubtotal() + getLineTax();
	}
	
	/**
	 * Returns the total tax for this line item
	 * @return
	 */
	public int getLineTax() {
		return (int)Math.round(getLineSubtotal() * getTaxRate());
	}
}
