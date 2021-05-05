package com.mgg;

import java.util.zip.DataFormatException;

public class SaleParser extends CSVParser<Sale>
{
	private final int COL_LEGACY = 0;
	private final int COL_STORE = 1;
	private final int COL_CUSTOMER = 2;
	private final int COL_SALESPERSON = 3;
	
	LegacyProvider provider;
	
	public SaleParser(LegacyProvider provider) {
		
		this.provider = provider;
	}
	
	@Override
	public Sale parseLine(String[] items) throws DataFormatException
	{
		//required items
		//TODO: catch class cast exception
		Store store = (Store)this.provider.findById(items[COL_STORE]);
		Person customer = (Person)this.provider.findById(items[COL_CUSTOMER]);
		Person salesperson = (Person)this.provider.findById(items[COL_SALESPERSON]);
		
		Sale sale = new Sale(items[COL_LEGACY],store,customer,salesperson);

		int i = 4;
		
		while (i < items.length) {
			//add SaleItems to sale
			//First column per item will always be a LegacyID
			
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
				
				//look up service prototype and salesperson
				Service prot = (Service)this.provider.findById(id);
				Person salesp = (Person)this.provider.findById(employeeCode);
				
				sale.addItem(new Service(prot, hours, salesp));
				
				i+=3;
			} else if (classname.endsWith("Item") && giftCard) { //is a dollar amt (gift card)
				int amt = (int)(100*Float.parseFloat(items[i+1]));
				
				Item prot = (Item)this.provider.findById(id);
				
				sale.addItem(new Item(prot, amt));
				
				i += 2;
			} else if (classname.endsWith("Item")) { //is a quantity (new or used product)
				int qty = Integer.parseInt(items[i+1]);
				
				Item prot = (Item)this.provider.findById(id);
				
				sale.addItem(new Item(prot, qty));
				
				i += 2;
			} else if (classname.endsWith("Subscription")) { //is a date (subscription)
				String startDate = items[i+1];
				String endDate = items[i+2];
				
				Subscription prot = (Subscription)this.provider.findById(id);
				
				sale.addItem(new Subscription(prot,startDate,endDate));
				
				i += 3;
			} else {
				throw new RuntimeException("Invalid sale item type in input file\n");
			}

		}
		
		return sale;
	}

}
