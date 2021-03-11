package com.mgg;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Sales report generator, loads and associates Legacy entities (Store,Person,etc)
 * Keeps items sorted for quick lookup by legacyId
 * @author azimuth
 *
 */
public class SalesReport
{
	/**
	 * Compares two Legacy entities first by their class name and then by their legacyID
	 * This ensures all entities of the same type (Store,Person,etc) are sorted next to eachother
	 * @author azimuth
	 *
	 */
	public static class LegacyComparator implements Comparator<Legacy>
	{
		@Override
		public int compare(Legacy a, Legacy b) {
			int classcmp = a.getClass().getName().compareTo(b.getClass().getName());
			
			if (classcmp == 0)
				return a.getId().compareTo(b.getId());
			else
				return classcmp;
		}
	}
	
	List<Legacy> all;
	
	public SalesReport() {
		all = new ArrayList<Legacy>();
	}
	
	public void parseFile(CSVParser<? extends Legacy> parser, File in) {
		all.addAll(parser.parse(in));
	}
	
	/**
	 * Updates references in each Legacy entity so they are not placeholders
	 */
	public void updateAssociation() {
		for (Legacy l : all) {

		}
	}
	
	public static void main(String[] args) {
		
		SalesReport sr = new SalesReport();
		
		sr.parseFile(new PersonParser(), new File("data/Persons.csv"));
		sr.parseFile(new StoreParser(), new File("data/Stores.csv"));
		sr.parseFile(new SaleItemParser(), new File("data/Items.csv"));
		sr.parseFile(new SaleParser(), new File("data/Sales.csv"));
		
		//TODO: do this automatically when new items are added (tree structure?)
		sr.all.sort(new LegacyComparator());
		
		
	}
}
