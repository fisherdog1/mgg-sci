package com.mgg;

public class SaleItem
{
	private LegacyID legacyId;
	private String name;
	
	public SaleItem(String legacyId, String name) {
		this.legacyId = new LegacyID(legacyId);
		this.name = name;
	}
	
	public LegacyID getLegacyId() {
		return this.legacyId;
	}
	
	public String getName() {
		return this.name;
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
