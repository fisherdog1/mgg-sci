package com.mgg;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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
		Sale s = new Sale(items[COL_LEGACY],items[COL_STORE],items[COL_CUSTOMER],items[COL_SALESPERSON]);

		int i = 4;
		
		while (i+1 < items.length) {
			//add SaleItems to sale
			//First column per item will always be a LegacyID
			SaleItem si;
			
			String id = items[i];
			
			LocalDate sdate = LocalDate.now();
			LocalDate edate = LocalDate.now();
			boolean hasdates = false;
			
			try {
				sdate = LocalDate.parse(items[i+1]);
				if (i+2 < items.length)
					edate = LocalDate.parse(items[i+2]);
				
				hasdates = true;
				
			} catch (DateTimeParseException pe) { }
			
			if (i+2 < items.length && !currencyPat.matcher(items[i+1]).matches() && currencyPat.matcher(items[i+2]).matches()) { //has an employee code and decimal hours (service)
				String employeeCode = items[i+1];
				double hours = Float.parseFloat(items[i+2]);
				si = new SaleItem(new Service(id));
				si.addParameter("EmployeeID", employeeCode);
				si.addParameter("Hours", hours);
				i+=3;
			} else if (qtyPat.matcher(items[i+1]).matches()) { //is a quantity (new or used product)
				int qty = Integer.parseInt(items[i+1]);
				si = new SaleItem(new Item(id));
				si.addParameter("Quantity", qty);
				i += 2;
			} else if (currencyPat.matcher(items[i+1]).matches()) { //is a dollar amt (gift card)
				double amt = Float.parseFloat(items[i+1]);
				si = new SaleItem(new Item(id));
				si.addParameter("CardAmount", amt);
				i += 2;
			} else if (hasdates) { //is a date (subscription)
				si = new SaleItem(new Subscription(id));
				si.addParameter("StartDate", sdate);
				si.addParameter("EndDate", edate);
				i += 3;
			} else {
				throw new RuntimeException("Invalid sale item type in input file\n");
			}
			
			s.addItem(si);
		}
		
		return s;
	}

}
