package com.mgg;

import java.util.ArrayList;
import java.util.List;

public class Store extends Legacy
{
	private Person managerId;
	private StreetAddress address;
	
	public Store(String legacyId, String managerId, StreetAddress address) {
		super(legacyId);
		this.managerId = new Person(managerId);
		this.address = address;
	}
	
	public Legacy getManager() {
		return this.managerId;
	}
	
	public StreetAddress getAddress() {
		return this.address;
	}
	
	@Override
	public String toString() {
		return address.toString();
	}
	
	@Override
	public List<Legacy> getLegacys() {
		List<Legacy> legacys = new ArrayList<Legacy>(1);
		legacys.add(managerId);
		
		return legacys;
	}
}
