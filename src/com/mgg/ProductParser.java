package com.mgg;

import java.util.zip.DataFormatException;

public class ProductParser extends CSVParser<Product>
{
	private final int COL_LEGACY = 0;
	private final int COL_TYPE = 1;
	private final int COL_NAME = 2;
	private final int COL_PRICE = 3;
	
	@Override
	public Product parseLine(String[] items) throws DataFormatException {

		//TODO: different checks for different saleitem types
		//TODO: this is gross, fix later
		
		Product item;
		
		if (items[COL_TYPE].equals("PN")) {
			
			if (items.length < 3)
				throw new DataFormatException("Invalid number of columns in file\n");
			
			//TODO fix cringeworthy conversion
			item = new Item(items[COL_LEGACY],items[COL_NAME], ProductType.New,((int)(Float.parseFloat(items[COL_PRICE])*100)));
			
		} else if (items[COL_TYPE].equals("PU")) {
			
			if (items.length < 3)
				throw new DataFormatException("Invalid number of columns in file\n");
			
			item = new Item(items[COL_LEGACY],items[COL_NAME], ProductType.Used,((int)(Float.parseFloat(items[COL_PRICE])*100)));
			
		} else if (items[COL_TYPE].equals("PG")) {
			
			if (items.length < 2)
				throw new DataFormatException("Invalid number of columns in file\n");
			
			item = new Item(items[COL_LEGACY],items[COL_NAME], ProductType.GiftCard, 0);
			
		} else if (items[COL_TYPE].equals("SV")) {
			
			if (items.length < 2)
				throw new DataFormatException("Invalid number of columns in file\n");
			
			item = new Service(items[COL_LEGACY],items[COL_NAME],((int)(Float.parseFloat(items[COL_PRICE])*100)));
			
		} else if (items[COL_TYPE].equals("SB")) {
			
			if (items.length < 2)
				throw new DataFormatException("Invalid number of columns in file\n");
			
			item = new Subscription(items[COL_LEGACY],items[COL_NAME],((int)(Float.parseFloat(items[COL_PRICE])*100)));

		} else {
			throw new DataFormatException("Invalid product type\n");
		}
		
		return item;
	}
}
