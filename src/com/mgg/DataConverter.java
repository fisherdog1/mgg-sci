package com.mgg;

import java.io.File;
import java.util.List;

public class DataConverter
{

	public static void main(String[] args)
	{
		List<Person> personList = CSVParser.parsePersons(new File("data/Persons.csv"));
		List<Store> storeList = CSVParser.parseStores(new File("data/Stores.csv"));
		List<SaleItem> itemList = CSVParser.parseItems(new File("data/Items.csv"));
		
		for (Person p : personList)
			System.out.printf(p + "\n");
		
		
		System.out.printf("---------------------------------\n");
		
		for (Store s : storeList)
			System.out.printf(s + "\n");
		
		System.out.printf("---------------------------------\n");
		
		for (SaleItem i : itemList)
			System.out.printf(i + "\n");
	}

	
}
