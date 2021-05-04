package com.mgg;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a customer in the MGG sales system
 * TODO: interfaces? employee subclass?
 * @author azimuth
 *
 */
public class Person extends Legacy implements IAddress
{
	private String firstName;
	private String lastName;
	private CustomerType type;
	private StreetAddress address;
	private List<String> email;
	
	public Person(String legacyId, String firstName, String lastName, CustomerType type, StreetAddress address) {
		super(legacyId);
		this.firstName = firstName;
		this.lastName = lastName;
		this.type = type;
		this.address = address;
		
		this.email = new ArrayList<String>(1);
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getFullNameFormal() {
		return getLastName() + ", " + getFirstName();
	}
	
	public CustomerType getType() {
		return type;
	}
	
	public void setType(CustomerType type) {
		this.type = type;
	}
	
	public StreetAddress getAddress() {
		return address;
	}
	
	public void setAddress(StreetAddress address) {
		this.address = address;
	}
	
	/**
	 * Get (a copy of) the list of email addresses
	 * @return
	 */
	public List<String> getEmail() {
		//Do not give out the original, which could be modified 
		//would violate OOP principles
		return new ArrayList<String>(this.email);	
	}
	
	/**
	 * Add an email to the email list
	 * @param email
	 * @return true if successful, false if address already present
	 */
	public boolean addEmail(String email) {
		if (!this.email.contains(email)) {	//Don't add duplicate email addresses
			this.email.add(email);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Remove an email from the email list
	 * @param email
	 * @return true if successful, false if email was not found in email list
	 */
	public boolean removeEmail(String email) {
		if (this.email.contains(email)) {
			this.email.remove(email);
			return true;
		}
		
		return false;
	}
	
	public static double getCustomerDiscount(CustomerType t) {
		if (t == CustomerType.Employee)
			return 0.15;
		else if (t == CustomerType.Platinum)
			return 0.10;
		else if (t == CustomerType.Gold)
			return 0.05;
		
		return 0.00;
	}
	
	public double getCustomerDiscout() {
		return Person.getCustomerDiscount(getType());
	}
	
	@Override
	public String toString() {
		return String.format("%s %s", firstName, lastName);
	}
}
