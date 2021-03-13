package com.mgg;

import java.util.ArrayList;
import java.util.List;

/**
 * A Sale corresponds to one transaction and contains SaleItems which define each line item
 * @author azimuth
 *
 */
public class Sale extends Legacy
{
	private Store store;
	private Person customer;
	private Person salesperson;
	private List<SaleItem<?>> items;
	
	public Sale(String legacyId, String storeId, String customerId, String salespersonId) {
		super(legacyId);
		this.store = new Store(storeId);
		this.customer = new Person(customerId);
		this.salesperson = new Person(salespersonId);
		items = new ArrayList<SaleItem<?>>();
	}
	
	private void updatePlaceholderStatus() {
		if (store.isPlaceholder() || customer.isPlaceholder() || salesperson.isPlaceholder())
			return;
		
		if (!items.isEmpty()) {
			for (SaleItem<?> si : items)
				if (si.getProduct().isPlaceholder())
					return;
		}
		
		setPlaceholder(false);
	}
	
	public Store getStore() {
		return store;
	}
	
	public void setStore(Store store) {
		this.store = store;
		updatePlaceholderStatus();
	}

	public Person getCustomer() {
		return customer;
	}
	
	public void setCustomer(Person customer) {
		this.customer = customer;
		updatePlaceholderStatus();
	}
	
	public Person getSalesperson() {
		return salesperson;
	}
	
	public void setSalesperson(Person salesperson) {
		this.salesperson = salesperson;
		updatePlaceholderStatus();
	}
	
	public void addItem(SaleItem<?> item) {
		items.add(item);
		updatePlaceholderStatus();
	}
	
	/**
	 * Return the items list
	 * @return
	 */
	public List<SaleItem<?>> getItems() {
		return items;
	}

	/**
	 * returns the sale total in cents not including tax
	 * @return
	 */
	public int getSubtotal()
	{
		int total = 0;
		for (SaleItem<?> si : items)
			total += si.getSalePrice();
		
		return total;
	}
	
	/**
	 * Return only the tax amount in cents for this sale
	 * @return
	 */
	public int getTax() {
		int total = 0;
		for (SaleItem<?> si : items)
			total += si.getTax();
		
		return total;	
	}
	
	/**
	 * Returns the sale total in cents including tax.
	 * @return
	 */
	public int getSubtotalTax() {
		int total = 0;
		for (SaleItem<?> si : items)
			total += si.getTotalPrice();
		
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
