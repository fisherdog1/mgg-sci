package com.mgg;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Store extends Legacy implements IAddress
{
	private Person manager;
	private StreetAddress address;
	
	public Store(String legacyId, Person manager, StreetAddress address) {
		super(legacyId);
		this.manager = manager;
		this.address = address;
	}
	
	public Person getManager() {
		return this.manager;
	}
	
	public void setManager(Person manager) {
		this.manager = manager;
	}
	
	public StreetAddress getAddress() {
		return this.address;
	}
	
	@Override
	public String toString() {
		return address.toString();
	}

	public static List<Store> loadAllFromDatabase(LegacyProvider provider) {
		List<Store> stores = new ArrayList<Store>();
		
		Connection con = SalesData.obtainConnection();
		
		try {
			//Load stores from db
			String st = "select s.legacyId as storeId, p.legacyId as managerId, s.addressId from Store s join Person p on s.managerId = p.personId;";
			PreparedStatement ps = con.prepareStatement(st);
			ps.execute();
			
			ResultSet rs = ps.getResultSet();
			
			//Load address from db
			
			String st2 = "select street, city, state, zip, country from Address where addressId = ?;";
			PreparedStatement ps2 = con.prepareStatement(st2);

			while (rs.next()) {
				ps2.setInt(1, rs.getInt("addressId"));
				ps2.execute();
				ResultSet rs2 = ps2.getResultSet();
				rs2.next();
				
				StreetAddress sa = new StreetAddress(rs2.getString("street"), rs2.getString("city"), 
						rs2.getString("state"), rs2.getString("zip"), rs2.getString("country"));
				
				Person manager = (Person)provider.findById(rs.getString("managerId"));
				
				Store s = new Store(rs.getString("storeId"),manager,sa);
				stores.add(s);
			}
			
		} catch (SQLException e) {
			throw new RuntimeException("SQL Exception opening connection",e);
		}
		
		return stores;
	}
}
