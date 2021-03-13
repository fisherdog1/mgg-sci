package com.mgg;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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
	ProductClassProvider provider;
	
	public SaleParser(ProductClassProvider provider) {
		fmt = new SimpleDateFormat("yyyy-MM-dd");
		qtyPat = Pattern.compile("[0-9]+");
		currencyPat = Pattern.compile("[0-9]+\\.*[0-9]*");
		
		this.provider = provider;
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
			SaleItem<?> si;
			
			String id = items[i];
			Product pdtype = (Product)this.provider.findById(id);
			String classname = pdtype.getClass().getName();
			
			boolean giftCard = false;
			
			if (pdtype instanceof Item)
				if (((Item)pdtype).getProductType() == ProductType.GiftCard)
					giftCard = true;
			
			if (classname.isBlank())
				throw new RuntimeException("Could not determine type of sale item");
			
			if (classname.endsWith("Service")) {
				String employeeCode = items[i+1];
				double hours = Float.parseFloat(items[i+2]);
				si = new ServiceSaleItem(new Service(id), hours, employeeCode);
				i+=3;
			} else if (classname.endsWith("Item") && giftCard) { //is a dollar amt (gift card)
				double amt = Float.parseFloat(items[i+1]);
				si = new ChargedSaleItem(new Item(id), (int)Math.round(amt*100.0));
				i += 2;
			} else if (classname.endsWith("Item")) { //is a quantity (new or used product)
				int qty = Integer.parseInt(items[i+1]);
				si = new QuantitySaleItem(new Item(id), qty);
				i += 2;
			} else if (classname.endsWith("Subscription")) { //is a date (subscription)
				si = new SubscriptionSaleItem(new Subscription(id), LocalDate.parse(items[i+1]), LocalDate.parse(items[i+2]));
				i += 3;
			} else {
				throw new RuntimeException("Invalid sale item type in input file\n");
			}
			
			s.addItem(si);
		}
		
		return s;
	}

}
