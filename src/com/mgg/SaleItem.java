package com.mgg;

import java.util.ArrayList;
import java.util.List;

public class SaleItem extends Legacy
{
	private String name;
	
	public SaleItem(String legacyId, String name) {
		super(legacyId);
		this.name = name;
	}
	
	
	public String getName() {
		return this.name;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	@Override
	public List<Legacy> getLegacys() {
		List<Legacy> legacys = new ArrayList<Legacy>(1);
		
		return legacys;
	}
}
