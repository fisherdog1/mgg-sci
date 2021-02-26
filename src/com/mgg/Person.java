package com.mgg;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a customer in the MGG sales system
 * TODO: interfaces? employee subclass?
 * @author azimuth
 *
 */
public class Person
{
	private LegacyID legacyId;
	private String firstName;
	private String lastName;
	private CustomerType type;
	private StreetAddress address;
	private List<String> email;
	
	public Person(String legacyId, String firstName, String lastName, CustomerType type, StreetAddress address) {
		this.legacyId = new LegacyID(legacyId);
		this.firstName = firstName;
		this.lastName = lastName;
		this.type = type;
		this.address = address;
		
		this.email = new ArrayList<String>(1);
	}
	
	public Person(String legacyId) {
		this(legacyId, "Polly", "Placeholder", CustomerType.Customer, new StreetAddress());
	}
	
	public LegacyID getLegacyId() {
		return legacyId;
	}
	
	public String getFirstName()	{
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName()	{
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
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
	
	@Override
	public String toString() {
		return String.format("%s %s", firstName, lastName);
	}
}
