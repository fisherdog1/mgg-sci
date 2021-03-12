package com.mgg;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
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
	
	public Legacy findByPlaceholder(Legacy ph) {
		int i = Collections.binarySearch(all, ph, new LegacyComparator());
		
		if (i >= 0)
			return all.get(i);
		
		return null;
	}
	
	/**
	 * Searches the list of entities for objects that fit the passed Legacy's placeholders
	 * TODO: this uses a binary search and so only has defined behavior when the list is sorted, check before running
	 * @param leg
	 */
	public void updateAssociation(Legacy leg) {
		
		if (leg.getClass().getName().endsWith("Store"))
			System.out.printf("C: %s\n",leg.getClass().getName());
		
		//TODO: there has to be a generic way to do this
		
		if (leg instanceof Store) {
			if (((Store) leg).getManager().isPlaceholder()) {
				Person mgr = (Person)findByPlaceholder(((Store) leg).getManager());
				if (mgr != null)
					((Store) leg).setManager(mgr);
			}
				
				
		} else if (leg instanceof Sale) {
			if (((Sale) leg).getStore().isPlaceholder()) {
				Store store = (Store)findByPlaceholder(((Sale) leg).getStore());
				if (store != null)
					((Sale) leg).setStore(store);
			}
			
			if (((Sale) leg).getCustomer().isPlaceholder()) {
				Person cust = (Person)findByPlaceholder(((Sale) leg).getCustomer());
				if (cust != null)
					((Sale) leg).setCustomer(cust);
			}
			
			if (((Sale) leg).getSalesperson().isPlaceholder()) {
				Person salesp = (Person)findByPlaceholder(((Sale) leg).getSalesperson());
				if (salesp != null)
					((Sale) leg).setSalesperson(salesp);
			}
			
			//Associate SaleItems
			List<SaleItem> assocItems = new ArrayList<SaleItem>();
			
			for (SaleItem item : ((Sale) leg).getItems()) {
				assocItems.add((SaleItem)findByPlaceholder(item));
			}
			
			((Sale) leg).getItems().clear();
			
			for (SaleItem item : assocItems)
				((Sale) leg).addItem(item);
		}
	}
	
	/**
	 * Updates references in each Legacy entity so they are not placeholders
	 */
	public void updateAllAssociations() {
		for (Legacy l : all) 
			updateAssociation(l);
	}
	
	private class SalespersonReportRow {
		public Person salesperson;
		public int sales;
		public int totalCents;
		
		public SalespersonReportRow(Person salesperson) {
			this.salesperson = salesperson;
			this.sales = 0;
			this.totalCents = 0;
		}
	}
	
	private class StoresReportRow {
		public Store store;
		public int sales;
		public int totalCents;
		
		public StoresReportRow(Store store) {
			this.store = store;
			this.sales = 0;
			this.totalCents = 0;
		}
	}
	
	public void salespersonSummaryReport() {
		List<SalespersonReportRow> rows = new ArrayList<SalespersonReportRow>();
	
		//TODO: make copy list of just people, also probably sales since those should change the most often
		for (Legacy l : all) {
			if (l instanceof Person) {
				Person p = (Person)l;
				
				rows.add(new SalespersonReportRow(p));
			}
		}
		
		rows.sort(new Comparator<SalespersonReportRow>() {
			@Override
			public int compare(SalespersonReportRow a, SalespersonReportRow b) {
				return a.salesperson.getFullNameFormal().compareTo(b.salesperson.getFullNameFormal());
			}
		});
		
		int totalSales = 0;
		int grandTotalCents = 0;
		
		for (Legacy l : all) {
			if (l instanceof Sale) {
				Sale s = (Sale)l;
				
				for (SalespersonReportRow r : rows) {
					if (r.salesperson.equals(s.getSalesperson())) {
						r.sales++;
						totalSales++;
						
						//TODO: add sale total to totalCents
					}
				}
			}
		}
		
		//rows.get(0).sales = 24;
		
		System.out.printf("%-24s %4s  %4s\n","Salesperson","# Sales","Grand Total");
				
		for (SalespersonReportRow r : rows) {
			System.out.printf("%-24s %-4d     $%4d.%02d\n", r.salesperson.getFullNameFormal(), r.sales, r.totalCents/100, r.totalCents%100);
		}
		
		System.out.printf("%-24s %-4d     $%4d.%02d\n","",totalSales, grandTotalCents/100, grandTotalCents%100);
	}
	
	public void storeSummaryReport() {
		List<StoresReportRow> rows = new ArrayList<StoresReportRow>();
		
		for (Legacy l : all) {
			if (l instanceof Store) {
				Store s = (Store)l;
				
				rows.add(new StoresReportRow(s));
			}
		}
		
		int totalSales = 0;
		int grandTotalCents = 0;

		for (Legacy l : all) {
			if (l instanceof Sale) {
				Sale s = (Sale)l;
				
				for (StoresReportRow r : rows) {
					if (r.store.equals(s.getStore())) {
						r.sales++;
						totalSales++;
						
						//TODO: add sale total to totalCents
					}
				}
			}
		}
		
		System.out.printf("%-24s %-24s %4s  %4s\n","Store","Manager","# Sales","Grand Total");
		
		for (StoresReportRow r : rows) {
			System.out.printf("%-24s %-24s %-4d     $%4d.%02d\n", r.store.getId(), r.store.getManager().getFullNameFormal(), r.sales, r.totalCents/100, r.totalCents%100);
		}
		
		System.out.printf("%-49s %-4d     $%4d.%02d\n","",totalSales, grandTotalCents/100, grandTotalCents%100);
	}
	
	public static void main(String[] args) {
		
		SalesReport sr = new SalesReport();
		
		sr.parseFile(new PersonParser(), new File("data/Persons.csv"));
		sr.parseFile(new StoreParser(), new File("data/Stores.csv"));
		sr.parseFile(new SaleItemParser(), new File("data/Items.csv"));
		sr.parseFile(new SaleParser(), new File("data/Sales.csv"));
		
		//TODO: do this automatically when new items are added (tree structure?)
		sr.all.sort(new LegacyComparator());
		sr.updateAllAssociations();
		
		sr.salespersonSummaryReport();
		sr.storeSummaryReport();
	}
}
