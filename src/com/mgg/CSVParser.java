package com.mgg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Class which contains methods for parsing non-standard CSVs of Person
 * @author azimuth
 *
 */
public class CSVParser
{
	/**
	 * TODO: generic parser?
	 * @param in
	 * @return
	 */
	public static List<Person> parsePersons(File in) {
		Scanner sc = null;
		
		try {
			sc = new Scanner(in);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		sc.useDelimiter("\n");
		
		int count = sc.nextInt();
		int i = 2;
		
		ArrayList<Person> out = new ArrayList<Person>(count);
		
		while (sc.hasNext()) {
			String line = sc.next();
			
			String[] items = line.split(",", -1);
			
			if (items.length < 8)
				throw new RuntimeException("Invalid number of columns in file %s, line %d\n".formatted(in,i));
			
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
				throw new RuntimeException("Invalid customer type in file, line %s\n".formatted(in, i));
			}
			
			StreetAddress addr = new StreetAddress(items[4],items[5],items[6],items[7],items[8]);
			Person p = new Person(items[0],items[3],items[2],ct,addr);
			
			for (int j = 9; j < items.length; j++)
				p.addEmail(items[j]);
			
			out.add(p);
			
			//System.out.printf("%-20s %-20s\n", items[3], items[2]);
			
			i++;
		}
		
		sc.close();
		return out;
	}
	
	public static List<Store> parseStores(File in) {
		Scanner sc = null;
		
		try {
			sc = new Scanner(in);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		sc.useDelimiter("\n");
		
		int count = sc.nextInt();
		int i = 2;
		
		ArrayList<Store> out = new ArrayList<Store>(count);
		
		while (sc.hasNext()) {
			String line = sc.next();
			
			String[] items = line.split(",", -1);
			
			if (items.length < 7)
				throw new RuntimeException("Invalid number of columns in file %s, line %d\n".formatted(in,i));
			
			StreetAddress a = new StreetAddress(items[2], items[3], items[4], items[5], items[6]);
			//TODO: this should probably look up the person by id, makes a placeholder for now
			Person m = new Person(items[1]);
			Store s = new Store(items[0], m, a);
			
			out.add(s);
			
			i++;
		}
		
		sc.close();
		return out;
	}

	public static List<SaleItem> parseItems(File in)
	{
		Scanner sc = null;
		
		try {
			sc = new Scanner(in);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		sc.useDelimiter("\n");
		
		int count = sc.nextInt();
		int i = 2;
		
		ArrayList<SaleItem> out = new ArrayList<SaleItem>(count);
		
		while (sc.hasNext()) {
			String line = sc.next();
			
			String[] items = line.split(",", -1);
			
			if (items.length < 4)
				throw new RuntimeException("Invalid number of columns in file %s, line %d\n".formatted(in,i));
			

			SaleItem item = new SaleItem(items[0], items[2]);
			
			out.add(item);
			
			i++;
		}
		
		sc.close();
		return out;
	}
}
