package com.mgg;

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
	}
	
	public boolean isPlaceholder() {
		//TODO: this correct?
		return (quantity == 0 && getProductType() != ProductType.GiftCard) || getBasePrice() == 0;
	}
	
	public ProductType getProductType() {
		return type;
	}
	
	public String getProductTypeString() {
		ProductType t = getProductType();
		String str;
		
		if (t == ProductType.New)
			str = "PN";
		if (t == ProductType.Used)
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
		if (isPlaceholder())
			throw new RuntimeException("Tried to calculate line total for prototype: %s\n".formatted(getName()));
		
		if (getProductType() == ProductType.GiftCard)
			return (int)Math.round(getBasePrice() * quantity);
		else if (getProductType() == ProductType.Used)
			return (int)Math.round(getBasePrice() * 0.8 * quantity);
		else
			return getBasePrice();
	}

	public int getQuantity() {
		return quantity;
	}
}
