package com.mgg;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.mgg.ReportColumn.ReportColumnIterator;

/**
 * Sales report generator, loads and associates Legacy entities (Store,Person,etc)
 * Keeps items sorted for quick lookup by legacyId
 * @author azimuth
 *
 */
public class SalesReport implements LegacyProvider
{
	/**
	 * Compares two Legacy entities by their legacyID
	 * @author azimuth
	 *
	 */
	public static class LegacyComparator implements Comparator<Legacy>
	{
		@Override
		public int compare(Legacy a, Legacy b) {
			return a.getId().compareTo(b.getId());
		}
	}
	
	/**
	 * Compares sales based on customer lastName, then firstName
	 * @author azimuth
	 *
	 */
	public static class SaleCustomerNameComparator implements Comparator<Sale>
	{
		@Override
		public int compare(Sale o1, Sale o2)
		{
			int lastNameCmp = o1.getCustomer().getLastName().compareTo(o2.getCustomer().getLastName());
			
			if (lastNameCmp == 0)
				return o1.getCustomer().getFirstName().compareTo(o2.getCustomer().getFirstName());
			
			return lastNameCmp;
		}
	}
	
	/**
	 * Compares sales based on grand total. Sorts create a list of descending value
	 * @author azimuth
	 *
	 */
	public static class SaleValueComparator implements Comparator<Sale>
	{
		@Override
		public int compare(Sale o1, Sale o2)
		{
			return o2.getGrandTotal() - o1.getGrandTotal();
		}
	}
	
	/**
	 * Compares sales based on store name, then salesperson last name, first name
	 * @author azimuth
	 *
	 */
	public static class SaleStoreComparator implements Comparator<Sale>
	{
		@Override
		public int compare(Sale o1, Sale o2)
		{
			int storeIdCmp = o1.getStore().compareTo(o2.getStore());
			
			if (storeIdCmp == 0) {
				int lastNameCmp = o1.getSalesperson().getLastName().compareTo(o2.getSalesperson().getLastName());
				
				if (lastNameCmp == 0)
					return o1.getSalesperson().getFirstName().compareTo(o2.getSalesperson().getFirstName());
				
				return lastNameCmp;
			}
			return storeIdCmp;
		}
	}
	
	List<Legacy> saleDependencies;
	SortedLinkedList<Sale> sales;
	
	public SalesReport() {
		saleDependencies = new ArrayList<Legacy>();
		sales = new SortedLinkedList<Sale>(new SaleStoreComparator());
	}
	
	public <T extends Legacy> List<T> parseFile(CSVParser<T> parser, File in) {
		return parser.parse(in);
	}
	
	public Legacy findById(String id) {
		//TODO: binary search stopped working when SaleParser was changed??
		//int i = Collections.binarySearch(all, new Legacy(id), new LegacyComparator());
		
		for (Legacy l : saleDependencies) {
			if (l.getId().equals(id))
				return l;
		}
		
		throw new RuntimeException("Failed to lookup legacyId %s".formatted(id));
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
		for (Legacy l : saleDependencies) {
			if (l instanceof Person) {
				Person p = (Person)l;
				
				if (p.getCustomerTypeLetter().equals("E")) //TODO: no magic strings
					rows.add(new SalespersonReportRow(p));
			}
		}
		
		int totalSales = 0;
		int grandTotalCents = 0;
		
		for (Sale s : sales) {
			for (SalespersonReportRow r : rows) {
				if (r.salesperson.equals(s.getSalesperson())) {
					r.sales++;
					totalSales++;
					
					r.totalCents += s.getGrandTotal();
					grandTotalCents += s.getGrandTotal();
				}
			}
		}
		
		System.out.printf("%-24s %4s  %4s\n","Salesperson","# Sales","Grand Total");
				
		for (SalespersonReportRow r : rows) {
			System.out.printf("%-24s %-4d     $%4d.%02d\n", r.salesperson.getFullNameFormal(), r.sales, r.totalCents/100, r.totalCents%100);
		}
		
		System.out.printf("%-24s %-4d     $%4d.%02d\n\n","Total Sales: ",totalSales, grandTotalCents/100, grandTotalCents%100);
	}
	
	public void storeSummaryReport() {
		List<StoresReportRow> rows = new ArrayList<StoresReportRow>();
		
		for (Legacy l : saleDependencies) {
			if (l instanceof Store) {
				Store s = (Store)l;
				
				rows.add(new StoresReportRow(s));
			}
		}
		
		int totalSales = 0;
		int grandTotalCents = 0;

		for (Sale s : sales) {
			for (StoresReportRow r : rows) {
				if (r.store.equals(s.getStore())) {
					r.sales++;
					totalSales++;
					
					r.totalCents += s.getGrandTotal();
					grandTotalCents += s.getGrandTotal();
				}
			}
		}
		
		System.out.printf("%-24s %-24s %4s  %4s\n","Store","Manager","# Sales","Grand Total");
		
		for (StoresReportRow r : rows) {
			System.out.printf("%-24s %-24s %-4d     $%4d.%02d\n", r.store.getId(), r.store.getManager().getFullNameFormal(), r.sales, r.totalCents/100, r.totalCents%100);
		}
		
		System.out.printf("%-49s %-4d     $%4d.%02d\n\n","Total sales: ",totalSales, grandTotalCents/100, grandTotalCents%100);
	}
	
	//TODO: service cost per hour and item cost per unit output is wrong (actually total)
	public void detailSaleReport() {
		for (Sale s : sales) {
			System.out.printf("Sale:  %s\n", s.getId());
			System.out.printf("Store: %s\n", s.getStore().getId());
			System.out.printf("Customer:\n");
			System.out.printf("    %s (%s)\n", s.getCustomer().getFullNameFormal(), s.getCustomer().getEmail());
			System.out.printf("    %s\n\n", s.getCustomer().getAddress());
			
			System.out.printf("Salesperson:\n");
			System.out.printf("    %s (%s)\n", s.getSalesperson().getFullNameFormal(), s.getSalesperson().getEmail());
			System.out.printf("    %s\n\n", s.getSalesperson().getAddress());
			
			int subtotal = s.getSubtotal();
			int totalTax = s.getTax();
			
			for (Product si : s.getItems()) {
				
				System.out.printf("%-48s\n", si.getName());
				System.out.printf("%10s ", si.getId());
				
				String detail = "";
				
				if (si instanceof Item) {
					Item i = (Item)si;
					
					//TODO: move formatting to respective classes or make a builder class
					
					if (i.getProductType() == ProductType.New) {
						int price = si.getBasePrice();
						detail = "(New Item) @$%d.%02d/ea".formatted(price/100, price%100);
					} else if (i.getProductType() == ProductType.Used) {
						int price = (int)Math.round(si.getBasePrice()*0.8);
						detail = "(Used Item) @$%d.%02d/ea".formatted(price/100, price%100);
					} else if (i.getProductType() == ProductType.GiftCard) {
						detail = "(Gift Card)";
					}
					
				} else if (si instanceof Service) {
					Service sv = (Service)si;
					
					int price = si.getBasePrice();
				
					detail = "(Svc by %s %s) @$%d.%02d/hr".formatted(sv.getSalesperson().getId(), sv.getSalesperson().getFullNameFormal(), price/100, price%100);
					
				} else if (si instanceof Subscription) {
					Subscription sc = (Subscription)si;
					
					int days = sc.getDurationDays();
					
					detail = "Subscription for %d days @$%d.%02d/yr".formatted(days, sc.getBasePrice()/100, sc.getBasePrice()%100);
					
				}
				
				System.out.printf("%-64s $%4d.%02d\n",detail, si.getLineSubtotal()/100, si.getLineSubtotal()%100);
			}
			
			System.out.printf("%74s: $%4d.%02d\n", "Subtotal", subtotal/100, subtotal%100);
			System.out.printf("%74s: $%4d.%02d\n", "Tax", totalTax/100, totalTax%100);

			int discount = s.getSubtotalTax() - s.getGrandTotal();
			if (s.getCustomer().getCustomerDiscount() > 0.0)
				System.out.printf("%65s (%05.2f%%): $%4d.%02d\n", "Discount", s.getCustomer().getCustomerDiscount()*100, discount/100, discount%100);
			System.out.printf("%74s: $%4d.%02d\n\n","Grand Total", s.getGrandTotal()/100, s.getGrandTotal()%100);
		}
	}
	
	public void loadCSVs(String persons, String items, String stores, String sales) {
		saleDependencies.addAll(parseFile(new PersonParser(), new File(persons)));
		saleDependencies.addAll(parseFile(new ProductParser(), new File(items)));
		saleDependencies.addAll(parseFile(new StoreParser(this), new File(stores)));
		this.sales.addAll(parseFile(new SaleParser(this), new File(sales)));
	}
	
	/**
	 * Load CSV data to database for testing
	 */
	public void commitExampleData() {
		//TODO: Update to use ADT for dependency order sorting
		for (Legacy l : saleDependencies) {
			if (l instanceof Person) {
				Person p = (Person)l;
				StreetAddress sa = p.getAddress();
				
				SalesData.addPerson(p.getId(), p.getCustomerTypeLetter(), p.getFirstName(), p.getLastName(), 
						sa.getStreet(), sa.getCity(), sa.getState(), sa.getZip(), sa.getCountry());
				
				for (String email : p.getEmail())
					SalesData.addEmail(p.getId(), email);
			}
		}
		
		for (Legacy l : saleDependencies) {
			if (l instanceof Store) {
				Store s = (Store)l;
				StreetAddress sa = s.getAddress();
				
				SalesData.addStore(s.getId(), s.getManager().getId(), 
						sa.getStreet(), sa.getCity(), sa.getState(), sa.getZip(), sa.getCountry());
			}
		}
		
		for (Legacy l : saleDependencies) {
			if (l instanceof Product) {
				Product p = (Product)l;
				if (p.isPrototype())
					SalesData.addItem(p.getId(), p.getProductTypeString(), p.getName(), ((double)p.getBasePrice())/100);
			}
		}
		
		for (Sale s : sales) {
			SalesData.addSale(s.getId(), s.getStore().getId(), s.getCustomer().getId(), s.getSalesperson().getId());
		}
		
		for (Sale s : sales) {
			for (Product p : s.getItems()) {
				if (!p.isPrototype()) {
					if (p instanceof Item) {
						Item i = (Item)p;
						
						if (i.getProductType() == ProductType.GiftCard)
							SalesData.addGiftCardToSale(s.getId(), i.getId(), ((double)i.getBasePrice())/100.0); //function takes a dollars values basePrice is in cents
						else
							SalesData.addProductToSale(s.getId(), i.getId(), i.getQuantity());
						
					} else if (p instanceof Service) {
						Service sv = (Service)p;
						SalesData.addServiceToSale(s.getId(), sv.getId(), sv.getSalesperson().getId(), sv.getHours());
						
					} else if (p instanceof Subscription) {
						Subscription sb = (Subscription)p;
						SalesData.addSubscriptionToSale(s.getId(), sb.getId(), sb.getStartDate(), sb.getEndDate());
						
					}
				}
			}
		}
	}
	
	/**
	 * Returns if the named database table is empty or non-existent
	 * I don't remember what this is for
	 * @param tableName
	 * @return
	 */
	public boolean tableEmpty(String tableName, Connection con) {
		
		try {
			String st = "select * from " + tableName + ";";
			PreparedStatement checkTable = con.prepareStatement(st);
			checkTable.execute();
			
			ResultSet rs = checkTable.getResultSet();
			if (rs.next() == false)
				return true;
			else
				return false;
			
		} catch (SQLException e) {
			throw new RuntimeException("SQL exception in tableEmpty",e);
		}
	}
	
	public void loadAllFromDatabase() {
		//this.all.addAll(StreetAddress.loadAllFromDatabase());
		
		//Load Person, Store, Product, Sale
		this.saleDependencies.addAll(Person.loadAllFromDatabase());
		this.saleDependencies.addAll(Store.loadAllFromDatabase(this));
		this.saleDependencies.addAll(Item.loadAllFromDatabase());
		this.saleDependencies.addAll(Service.loadAllFromDatabase());
		this.saleDependencies.addAll(Subscription.loadAllFromDatabase());
		
		//Ideally saleDependencies could be discarded after this point
		this.sales.addAll(Sale.loadAllFromDatabase(this));
	}
	
	public static void main(String[] args) {
		
		SalesReport sr = new SalesReport();
		
		//Load database with data from CSVs for testing
//		sr.loadCSVs("data/Persons.csv", "data/Items.csv", "data/Stores.csv", "data/Sales.csv");
//		SalesData.clearDatabase();
//		sr.commitExampleData();
		
		sr.loadAllFromDatabase();
		
		sr.salespersonSummaryReport();
		sr.storeSummaryReport();
		sr.detailSaleReport();
		
		//These also work but DetailReport is not finished
//		ReportBuilder rb1 = ReportBuilder.salespersonSummaryReportBuilder(sr.sales, sr.saleDependencies);
//		ReportBuilder rb2 = ReportBuilder.storeSummaryReportBuilder(sr.saleDependencies,sr.sales);
		
//		System.out.print(rb1);
//		System.out.print(rb2);
	}
}

