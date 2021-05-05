package com.mgg;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Annual Subscription with an annual rate in cents per year
 * No tax applied
 */
public class Subscription extends Product
{
	private int annualFee;
	
	//Required to be non-prototype
	private String startDate;
	private String endDate;
	
	public Subscription(String legacyId, String name, int annualFee) {
		super(legacyId, name);
		
		this.annualFee = annualFee;
	}
	
	public Subscription(Subscription prototype, String startDate, String endDate) {
		this(prototype.getId(), prototype.getName(), prototype.getBasePrice());
		
		this.startDate = startDate;
		this.endDate = endDate;
		
		clearPrototype();
	}
	
	public int getBasePrice() {
		return this.annualFee;
	}

	public int getDurationDays() {
		LocalDate start = java.sql.Date.valueOf(startDate).toLocalDate();
		LocalDate end = java.sql.Date.valueOf(endDate).toLocalDate();
		return (int)(ChronoUnit.DAYS.between(start, end) + 1);
	}
	
	public String getProductTypeString() {
		return "SB";
	}
	
	@Override
	public int getLineSubtotal() {
		if (isPrototype())
			throw new RuntimeException("Tried to calculate line total for prototype: %s\n".formatted(getName()));
		
		return (int)Math.round(getDurationDays() / 365.0 * annualFee);
	}

	public String getStartDate() {
		return startDate;
	}

	public String getEndDate() {
		return endDate;
	}
	
	public static List<Subscription> loadAllFromDatabase() {
		List<Subscription> subscriptions = new ArrayList<Subscription>();
		
		Connection con = SalesData.obtainConnection();
		
		try {
			//Load stores from db
			String st = "select s.legacyId, s.productName, s.baseRate from Subscription s where s.saleId is null;";
			PreparedStatement ps = con.prepareStatement(st);
			ps.execute();
			
			ResultSet rs = ps.getResultSet();

			while (rs.next()) {
				Subscription s = new Subscription(rs.getString("legacyId"),rs.getString("productName"),rs.getInt("baseRate"));
				subscriptions.add(s);
			}
			
		} catch (SQLException e) {
			throw new RuntimeException("SQL Exception opening connection",e);
		} finally {
			SalesData.commitAndClose(con);
		}
		
		return subscriptions;
	}
}
