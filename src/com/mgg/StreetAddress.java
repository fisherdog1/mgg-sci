package com.mgg;

/**
 * Represents a street address as five potentially empty but non-null strings
 * TODO: implement comparable?
 * @author azimuth
 *
 */
public class StreetAddress
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
	
	public StreetAddress() {
		this("Placeholder","","","","");
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
}
