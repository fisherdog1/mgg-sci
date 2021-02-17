package com.mgg;

public class Store
{
	private String legacyId;
	private Person manager;
	private StreetAddress address;
	
	public Store(String legacyId, Person manager, StreetAddress address) {
		this.legacyId = legacyId;
		this.manager = manager;
		this.address = address;
	}
	
	public String getLegacyId()	{
		return this.legacyId;
	}
	
	public Person getManager() {
		return this.manager;
	}
	
	public StreetAddress getAddress() {
		return this.address;
	}
	
	@Override
	public String toString() {
		return address.toString();
	}
}
