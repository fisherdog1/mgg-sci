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
		items = new ArrayList<SaleItem>();
		//TODO: unimplemented
	}
	
	public Store getStore() {
		return store;
	}

	public Person getCustomer() {
		return customer;
	}
	
	public Person getSalesperson() {
		return salesperson;
	}
	
	public void addItem(SaleItem item) {
		items.add(item);
	}
	
	/**
	 * Return a copy of the items list
	 * @return
	 */
	public List<SaleItem> getItems() {
		return new ArrayList<SaleItem>(items);
	}
	
	@Override
	public List<Legacy> getLegacys() {
		List<Legacy> legacys = new ArrayList<Legacy>(1);
		legacys.add(store);
		legacys.add(customer);
		legacys.add(salesperson);
		legacys.addAll(items);
		
		return legacys;
	}
}
