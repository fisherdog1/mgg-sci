package com.mgg;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A Sale corresponds to one transaction and contains non-prototype Product s which define each line item
 * @author azimuth
 *
 */
public class Sale extends Legacy
{
	private Store store;
	private Person customer;
	private Person salesperson;
	private List<Product> items;
	
	public Sale(String legacyId, Store store, Person customer, Person salesperson) {
		super(legacyId);
		this.store = store;
		this.customer = customer;
		
		if (salesperson.getType() != CustomerType.Employee)
			throw new RuntimeException("Person %s is not an employee\n".formatted(salesperson.getFullNameFormal()));
		
		this.salesperson = salesperson;
		items = new ArrayList<Product>();
	}
	
	public Store getStore() {
		return store;
	}
	
	public void setStore(Store store) {
		this.store = store;
	}

	public Person getCustomer() {
		return customer;
	}
	
	public void setCustomer(Person customer) {
		this.customer = customer;
	}
	
	public Person getSalesperson() {
		return salesperson;
	}
	
	public void setSalesperson(Person salesperson) {
		this.salesperson = salesperson;
	}
	
	public void addItem(Product item) {
		if (item.isPrototype())
			throw new RuntimeException("Cannot add prototype item to Sale: %s\n".formatted(item.getName()));
		
		items.add(item);
	}
	
	/**
	 * Return the items list
	 * @return
	 */
	public List<Product> getItems() {
		return items;
	}

	/**
	 * returns the sale total in cents not including tax
	 * @return
	 */
	public int getSubtotal()
	{
		int total = 0;
		for (Product si : items)
			total += si.getLineSubtotal();
		
		return total;
	}
	
	/**
	 * Return only the tax amount in cents for this sale
	 * @return
	 */
	public int getTax() {
		int total = 0;
		for (Product si : items)
			total += si.getLineTax();
		
		return total;	
	}
	
	/**
	 * Returns the sale total in cents including tax.
	 * @return
	 */
	public int getSubtotalTax() {
		int total = 0;
		for (Product si : items)
			total += si.getLineTotal();
		
		return total;	
	}
	
	/**
	 * Returns the sale total in cents including tax and customer discount.
	 * @return
	 */
	public int getGrandTotal() {
		int total = this.getSubtotalTax();
		return (int)Math.round(total * (1.0 - this.getCustomer().getCustomerDiscount()));
	}
	
	public static List<Sale> loadAllFromDatabase(LegacyProvider provider) {
		List<Sale> sales = new ArrayList<Sale>();
		
		Connection con = SalesData.obtainConnection();
		
		try {
			//Load sales from db
			String st = "select s.legacyId as saleId, st.legacyId as storeId, cp.legacyId as customerId, sp.legacyId as salespersonId from \n"
					+ "	Sale s\n"
					+ " join Store st on s.storeId = st.storeId\n"
					+ " join Person cp on s.customerId = cp.personId\n"
					+ " join Person sp on s.salespersonId = sp.personId;";
			PreparedStatement ps = con.prepareStatement(st);
			ps.execute();
			ResultSet rs = ps.getResultSet();

			while (rs.next()) {
				Person customer = (Person)provider.findById(rs.getString("customerId"));
				Person salesperson = (Person)provider.findById(rs.getString("salespersonId"));
				Store store = (Store)provider.findById(rs.getString("storeId"));
				
				Sale s = new Sale(rs.getString("saleId"), store, customer, salesperson);
				
				//Add each product to sale
				
				String st2 = "select i.legacyId, i.quantity, i.basePrice from Sale sale join Item i on sale.saleId = i.saleId where sale.legacyId = ?;";
				PreparedStatement ps2 = con.prepareStatement(st2);
				ps2.setString(1, rs.getString("saleId"));
				ps2.execute();
				ResultSet rs2 = ps2.getResultSet();
				
				while (rs2.next()) {
					Item prototype = (Item)provider.findById(rs2.getString("legacyId"));
					Item i;
					
					if (prototype.getProductType() == ProductType.GiftCard)
						i = new Item(prototype, rs2.getInt("basePrice"));
					else
						i = new Item(prototype, rs2.getInt("quantity"));
					
					s.addItem(i);
				}
				
				String st3 = "select s.legacyId, p.legacyId as salespersonId, s.hours from Sale sale \n"
						+ "	join Service s on sale.saleId = s.saleId\n"
						+ " join Person p on s.salespersonId = p.personId where sale.legacyId = ?;";
				ps2 = con.prepareStatement(st3);
				ps2.setString(1, rs.getString("saleId"));
				ps2.execute();
				rs2 = ps2.getResultSet();
				
				while (rs2.next()) {
					Service sv = new Service((Service)provider.findById(rs2.getString("legacyId")),
							rs2.getFloat("hours"),(Person)provider.findById(rs2.getString("salespersonId")));
					s.addItem(sv);
				}
				
				String st4 = "select s.legacyId, s.startDate, s.endDate from Sale sale join Subscription s on sale.saleId = s.saleId where sale.legacyId = ?;";
				ps2 = con.prepareStatement(st4);
				ps2.setString(1, rs.getString("saleId"));
				ps2.execute();
				rs2 = ps2.getResultSet();
				
				while (rs2.next()) {
					Subscription sb = new Subscription((Subscription)provider.findById(rs2.getString("legacyId")),
							rs2.getString("startDate"),rs2.getString("endDate"));
					s.addItem(sb);
				}
				
				sales.add(s);
			}
			
		} catch (SQLException e) {
			throw new RuntimeException("SQL Exception opening connection",e);
		} finally {
			SalesData.commitAndClose(con);
		}
		
		return sales;
	}
}
