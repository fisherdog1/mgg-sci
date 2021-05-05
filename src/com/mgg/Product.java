package com.mgg;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Product extends Legacy
{
	private String name;
	private boolean isPrototype; 
	
	public Product(String legacyId, String name) {
		super(legacyId);
		this.name = name;
		this.isPrototype = true;
	}
	
	public String getName() {
		return this.name;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	public abstract String getProductTypeString();
	
	public abstract int getBasePrice();
	
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
	public final boolean isPrototype() {
		return isPrototype;
	}
	
	protected final void clearPrototype() {
		isPrototype = false;
	}
	
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
