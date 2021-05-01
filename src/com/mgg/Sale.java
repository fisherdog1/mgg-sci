package com.mgg;

import java.util.ArrayList;
import java.util.List;

/**
 * A Sale corresponds to one transaction and contains non-prototype Product s which define each line item
 * @author azimuth
 *
 */
public class Sale extends Legacy
{
	private Store store;
	private Person customer;
	private Person salesperson;
	private List<Product> items;
	
	public Sale(String legacyId, Store store, Person customer, Person salesperson) {
		super(legacyId);
		this.store = store;
		this.customer = customer;
		
		if (salesperson.getType() != CustomerType.Employee)
			throw new RuntimeException("Person %s is not an employee\n".formatted(salesperson.getFullNameFormal()));
		
		this.salesperson = salesperson;
		items = new ArrayList<Product>();
	}
	
	public Store getStore() {
		return store;
	}
	
	public void setStore(Store store) {
		this.store = store;
	}

	public Person getCustomer() {
		return customer;
	}
	
	public void setCustomer(Person customer) {
		this.customer = customer;
	}
	
	public Person getSalesperson() {
		return salesperson;
	}
	
	public void setSalesperson(Person salesperson) {
		this.salesperson = salesperson;
	}
	
	public void addItem(Product item) {
		if (item.isPlaceholder())
			throw new RuntimeException("Cannot add prototype item to Sale: %s\n".formatted(item.getName()));
		
		items.add(item);
	}
	
	/**
	 * Return the items list
	 * @return
	 */
	public List<Product> getItems() {
		return items;
	}

	/**
	 * returns the sale total in cents not including tax
	 * @return
	 */
	public int getSubtotal()
	{
		int total = 0;
		for (Product si : items)
			total += si.getLineSubtotal();
		
		return total;
	}
	
	/**
	 * Return only the tax amount in cents for this sale
	 * @return
	 */
	public int getTax() {
		int total = 0;
		for (Product si : items)
			total += si.getLineTax();
		
		return total;	
	}
	
	/**
	 * Returns the sale total in cents including tax.
	 * @return
	 */
	public int getSubtotalTax() {
		int total = 0;
		for (Product si : items)
			total += si.getLineTotal();
		
		return total;	
	}
	
	/**
	 * Returns the sale total in cents including tax and customer discount.
	 * @return
	 */
	public int getGrandTotal() {
		int total = this.getSubtotalTax();
		return (int)Math.round(total * (1.0 - this.getCustomer().getCustomerDiscout()));
	}
}
