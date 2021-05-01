package com.mgg;

public class Store extends Legacy
{
	private Person manager;
	private StreetAddress address;
	
	public Store(String legacyId) {
		super(legacyId);
	}
	
	public Store(String legacyId, Person manager, StreetAddress address) {
		this(legacyId);
		this.manager = manager;
		this.address = address;
	}
	
	public Person getManager() {
		return this.manager;
	}
	
	public void setManager(Person manager) {
		this.manager = manager;
	}
	
	public StreetAddress getAddress() {
		return this.address;
	}
	
	@Override
	public String toString() {
		return address.toString();
	}
}
