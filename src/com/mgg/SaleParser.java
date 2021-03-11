package com.mgg;

import java.util.zip.DataFormatException;

public class SaleParser extends CSVParser<Sale>
{
	private final int COL_LEGACY = 0;
	private final int COL_STORE = 1;
	private final int COL_CUSTOMER = 2;
	private final int COL_SALESPERSON = 3;
	
	@Override
	public Sale parseLine(String[] items) throws DataFormatException
	{
		//required items
		Sale s = new Sale(items[0],items[1],items[2],items[3]);
		
		
		
		return s;
	}

}
