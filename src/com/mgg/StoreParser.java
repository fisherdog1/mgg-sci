package com.mgg;

import java.util.zip.DataFormatException;

public class StoreParser extends CSVParser<Store>
{
	@Override
	public Store parseLine(String[] items) throws DataFormatException{
		
		if (items.length != 7)
			throw new DataFormatException("Invalid number of columns in file\n");
		
		StreetAddress a = new StreetAddress(items[2], items[3], items[4], items[5], items[6]);
		Store s = new Store(items[0], items[1], a);
		
		return s;
	}
}
