package com.mgg;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A service with a rate in cents per hour
 * @author azimuth
 *
 */
public class Service extends Product
{
	private int hourlyRate;
	
	//Required to be non-prototype
	private double hours;
	private Person salesperson;
	
	public Service(String legacyId, String name, int hourlyRate) {
		super(legacyId, name);
		
		this.hourlyRate = hourlyRate;
		this.hours = 0.0;
	}
	
	public Service(Service prototype, double hours, Person salesperson) {
		this(prototype.getId(),prototype.getName(),prototype.getBasePrice());
		
		this.hours = hours;
		this.salesperson = salesperson;
		
		clearPrototype();
	}
	
	@Override
	public double getTaxRate() {
		return 0.0285;
	}
	
	public int getBasePrice() {
		return this.hourlyRate;
	}

	public Person getSalesperson() {
		return salesperson;
	}
	
	public double getHours() {
		return hours;
	}
	
	public String getProductTypeString() {
		return "SV";
	}

	@Override
	public int getLineSubtotal() {
		if (isPrototype())
			throw new RuntimeException("Tried to calculate line total for prototype: %s\n".formatted(getName()));
		
		//TODO math
		return (int)Math.round(hourlyRate*hours);
	}
	
	public static List<Service> loadAllFromDatabase() {
		List<Service> services = new ArrayList<Service>();
		
		Connection con = SalesData.obtainConnection();
		
		try {
			//Load stores from db
			String st = "select s.legacyId, s.productName, s.baseRate from Service s where s.saleId is null;";
			PreparedStatement ps = con.prepareStatement(st);
			ps.execute();
			
			ResultSet rs = ps.getResultSet();

			while (rs.next()) {
				Service s = new Service(rs.getString("legacyId"),rs.getString("productName"),rs.getInt("baseRate"));
				services.add(s);
			}
			
		} catch (SQLException e) {
			throw new RuntimeException("SQL Exception opening connection",e);
		} finally {
			SalesData.commitAndClose(con);
		}
		
		return services;
	}
}
