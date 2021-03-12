package com.mgg;

import java.util.ArrayList;
import java.util.List;

public class Sale extends Legacy
{
	private Store store;
	private Person customer;
	private Person salesperson;
	private List<SaleItem> items;
	
	public Sale(String legacyId, String storeId, String customerId, String salespersonId) {
		super(legacyId);
		this.store = new Store(storeId);
		this.customer = new Person(customerId);
		this.salesperson = new Person(salespersonId);
		items = new ArrayList<SaleItem>();
	}
	
	private void updatePlaceholderStatus() {
		if (store.isPlaceholder() || customer.isPlaceholder() || salesperson.isPlaceholder())
			return;
		
		if (!items.isEmpty()) {
			for (SaleItem si : items)
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
	
	public void addItem(SaleItem item) {
		items.add(item);
		updatePlaceholderStatus();
	}
	
	/**
	 * Return the items list
	 * @return
	 */
	public List<SaleItem> getItems() {
		return items;
	}

	/**
	 * returns the sale total in cents
	 * @return
	 */
	public int total()
	{
		int total = 0;
		
		for (SaleItem si : items) {
			total += si.getProduct().getTotalPrice(si.getParameters());
		}
		
		return total;
	}
}
