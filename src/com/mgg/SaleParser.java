package com.mgg;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;

public class SaleParser extends CSVParser<Sale>
{
	private final int COL_LEGACY = 0;
	private final int COL_STORE = 1;
	private final int COL_CUSTOMER = 2;
	private final int COL_SALESPERSON = 3;
	
	SimpleDateFormat fmt;
	Pattern qtyPat;
	Pattern currencyPat;
	
	public SaleParser() {
		fmt = new SimpleDateFormat("yyyy-MM-dd");
		
		qtyPat = Pattern.compile("[0-9]+");
		currencyPat = Pattern.compile("[0-9]+\\.*[0-9]*");
	}
	
	@Override
	public Sale parseLine(String[] items) throws DataFormatException
	{
		//required items
		Sale s = new Sale(items[0],items[1],items[2],items[3]);

		int i = 4;
		
		while (i+1 < items.length) {
			//add SaleItems to sale
			//First column per item will always be a LegacyID
			SaleItem si;
			
			String id = items[i];
			
			Date sdate;
			Date edate;
			boolean hasdates = false;
			
			try {
				sdate = fmt.parse(items[i+1]);
				if (i+2 < items.length)
					edate = fmt.parse(items[i+2]);
				
				hasdates = true;
				
			} catch (ParseException pe) {
				
			}
			
			if (i+2 < items.length && !currencyPat.matcher(items[i+1]).matches() && currencyPat.matcher(items[i+2]).matches()) { //has an employee code and decimal hours (service)
				String employeeCode = items[i+1];
				float hours = Float.parseFloat(items[i+2]);
				si = new Service(id);
				i+=3;
			} else if (qtyPat.matcher(items[i+1]).matches()) { //is a quantity (new or used product)
				int qty = Integer.parseInt(items[i+1]);
				si = new Product(id);
				i += 2;
			} else if (currencyPat.matcher(items[i+1]).matches()) { //is a dollar amt (gift card)
				float amt = Float.parseFloat(items[i+1]);
				si = new Product(id);
				i += 2;
			} else if (hasdates) { //is a date (subscription)
				si = new Subscription(id);
				i += 3;
			} else {
				throw new RuntimeException("Invalid sale item type in input file\n");
			}
			
			s.addItem(si);
		}
		
		return s;
	}

}
