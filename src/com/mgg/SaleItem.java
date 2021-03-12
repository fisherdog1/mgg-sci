package com.mgg;

public class SaleItem extends Legacy
{
	private String name;
	
	public SaleItem(String legacyId) {
		super(legacyId);
		this.setPlaceholder(true);
	}
	
	public SaleItem(String legacyId, String name) {
		super(legacyId);
		this.name = name;
		this.setPlaceholder(false);
	}
	
	public String getName() {
		return this.name;
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
