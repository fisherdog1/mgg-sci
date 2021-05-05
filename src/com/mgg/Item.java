package com.mgg;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * New, Used, or GiftCard Product with a basePrice in cents
 * For gift cards, the base price is ignored and replaced with an amount from the individual sale information
 * @return
 */
public class Item extends Product
{
	private ProductType type;
	private int basePrice;
	
	//Required to be non-prototype
	private int quantity;

	/**
	 * Create a (prototype) Item. basePrice will be set to zero for gift cards
	 * @param legacyId
	 * @param name
	 * @param type
	 * @param basePrice
	 */
	public Item(String legacyId, String name, ProductType type, int basePrice) {
		super(legacyId, name);
		
		this.type = type;
		
		if (type != ProductType.GiftCard)
			this.basePrice = basePrice;
		else
			this.basePrice = 0;
	}
	
//	public Item(String legacyId, String name, ProductType type, int basePrice, int quantity) {
//		this(legacyId, name, type, basePrice);
//		
//		this.quantity = quantity;
//	}
	
	/**
	 * Create item from prototype. 
	 * @param prototype
	 * @param quantityBasePrice A quantity for non gift card items, an amount in cents for gift cards
	 */
	public Item(Item prototype, int quantityBasePrice) {
		this(prototype.getId(), prototype.getName(), prototype.getProductType(), prototype.getBasePrice());
		
		if (prototype.getProductType() == ProductType.GiftCard) {
			this.quantity = 1;
			this.basePrice = quantityBasePrice;
		} else
			this.quantity = quantityBasePrice;
		
		clearPrototype();
	}
	
	public ProductType getProductType() {
		return type;
	}
	
	public String getProductTypeString() {
		ProductType t = getProductType();
		String str;
		
		if (t == ProductType.New)
			str = "PN";
		else if (t == ProductType.Used)
			str = "PU";
		else
			str = "PG";
		
		return str;
	}
	
	@Override
	public double getTaxRate() {
		return 0.0725;
	}
	
	public int getBasePrice() {
		return basePrice;
	}

	@Override
	public int getLineSubtotal() {
		if (isPrototype())
			throw new RuntimeException("Tried to calculate line total for prototype: %s\n".formatted(getName()));
		
		if (getProductType() == ProductType.GiftCard)
			return (int)(getBasePrice() * quantity);
		else if (getProductType() == ProductType.Used)
			return (int)Math.round(getBasePrice() * 0.8) * quantity; //Must apply rounding this was
		else
			return getBasePrice() * quantity;
	}

	public int getQuantity() {
		return quantity;
	}
	
	public static List<Item> loadAllFromDatabase() {
		List<Item> items = new ArrayList<Item>();
		
		Connection con = SalesData.obtainConnection();
		
		try {
			//Select Item prototypes
			String st = "select i.legacyId, i.productName, i.basePrice, i.newUsed from Item i where i.saleId is null;";
			PreparedStatement ps = con.prepareStatement(st);
			ps.execute();
			
			ResultSet rs = ps.getResultSet();
			
			while (rs.next()) {
				ProductType type;
				
				if (rs.getString("newUsed").equals("PN"))
					type = ProductType.New;
				else if (rs.getString("newUsed").equals("PU"))
					type = ProductType.Used;
				else
					type = ProductType.GiftCard;
				
				Item i = new Item(rs.getString("legacyId"),rs.getString("productName"),type,rs.getInt("basePrice"));
				items.add(i);
			}
			
		} catch (SQLException e) {
			throw new RuntimeException("SQL Exception opening connection",e);
		} finally {
			SalesData.commitAndClose(con);
		}
		
		return items;
	}
}
