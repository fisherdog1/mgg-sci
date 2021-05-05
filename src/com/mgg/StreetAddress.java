package com.mgg;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a street address as five potentially empty but non-null strings
 * TODO: implement comparable?
 * @author azimuth
 *
 */
public class StreetAddress implements Comparable<StreetAddress>
{
	private String street;
	private String city;
	private String state;
	private String zip;
	private String country;
	
	public StreetAddress(String street, String city, String state, String zip, String country) {
		this.street = street;
		this.city = city;
		this.state = state;
		this.zip = zip;
		this.country = country;
	}

	/**
	 * @return the street
	 */
	public String getStreet()
	{
		return street;
	}

	/**
	 * @return the city
	 */
	public String getCity()
	{
		return city;
	}

	/**
	 * @return the state
	 */
	public String getState()
	{
		return state;
	}

	/**
	 * @return the zip
	 */
	public String getZip()
	{
		return zip;
	}

	/**
	 * @return the country
	 */
	public String getCountry()
	{
		return country;
	}
	
	@Override
	public String toString() {
		return String.format("%s, %s %s, %s", street, city, state, zip);
	}
	
	@Override
	public int compareTo(StreetAddress o)
	{
		return this.toString().compareTo(o.toString());
	}
	
	public static List<StreetAddress> loadAllFromDatabase() {
		List<StreetAddress> addresses = new ArrayList<StreetAddress>();
		
		Connection con = SalesData.obtainConnection();
		
		try {
			String st = "select street, city, state, zip, country from Address;";
			PreparedStatement ps = con.prepareStatement(st);
			ps.execute();
			
			ResultSet rs = ps.getResultSet();
			
			while (rs.next()) {
				StreetAddress sa = new StreetAddress(rs.getString("street"),rs.getString("city"),
						rs.getString("state"),rs.getString("zip"),rs.getString("country"));
				
				addresses.add(sa);
			}
			
		} catch (SQLException e) {
			throw new RuntimeException("SQL Exception opening connection",e);
		}
		
		return addresses;
	}
}
