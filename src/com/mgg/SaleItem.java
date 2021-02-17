package com.mgg;

public class SaleItem
{
	private String legacyId;
	private String name;
	
	public SaleItem(String legacyId, String name) {
		this.legacyId = legacyId;
		this.name = name;
	}
	
	public String getLegacyId()	{
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
