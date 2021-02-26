package com.mgg;

public class Store
{
	private LegacyID legacyId;
	private LegacyID managerId;
	private StreetAddress address;
	
	public Store(String legacyId, String managerId, StreetAddress address) {
		this.legacyId = new LegacyID(legacyId);
		this.managerId = new LegacyID(managerId);
		this.address = address;
	}
	
	public LegacyID getLegacyId() {
		return this.legacyId;
	}
	
	public LegacyID getManager() {
		return this.managerId;
	}
	
	public StreetAddress getAddress() {
		return this.address;
	}
	
	@Override
	public String toString() {
		return address.toString();
	}
}
