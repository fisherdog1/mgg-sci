package com.mgg;

public class Store extends Legacy
{
	private Person manager;
	private StreetAddress address;
	
	public Store(String legacyId) {
		super(legacyId);
	}
	
	public Store(String legacyId, String managerId, StreetAddress address) {
		this(legacyId);
		this.manager = new Person(managerId);
		this.address = address;
	}
	
	public Person getManager() {
		return this.manager;
	}
	
	public void setManager(Person manager) {
		//TODO: this shouldn't be handled here?
		if (manager.isPlaceholder() == false)
			this.setPlaceholder(false);
		
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
