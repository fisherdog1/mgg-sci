package com.mgg;

import java.util.zip.DataFormatException;

public class PersonParser extends CSVParser<Person>
{
	@Override
	public Person parseLine(String[] items) throws DataFormatException {
		
		if (items.length < 8)
			throw new DataFormatException("Invalid number of columns in file\n");
		
		CustomerType ct = null;
		
		if (items[1].toUpperCase().equals("C")) {
			ct = CustomerType.Customer;
		} else if (items[1].toUpperCase().equals("G")) {
			ct = CustomerType.Gold;
		} else if (items[1].toUpperCase().equals("P")) {
			ct = CustomerType.Platinum;
		} else if (items[1].toUpperCase().equals("E")) {
			ct = CustomerType.Employee;
		} else {
			throw new RuntimeException("Invalid customer type in file\n");
		}
		
		StreetAddress addr = new StreetAddress(items[4],items[5],items[6],items[7],items[8]);
		Person p = new Person(items[0],items[3],items[2],ct,addr);
		
		for (int j = 9; j < items.length; j++)
			p.addEmail(items[j]);
		
		return p;
	}
}
