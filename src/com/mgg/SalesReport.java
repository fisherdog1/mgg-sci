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

/**
 * Sales report generator, loads and associates Legacy entities (Store,Person,etc)
 * Keeps items sorted for quick lookup by legacyId
 * @author azimuth
 *
 */
public class SalesReport implements ProductClassProvider
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
	
	//TODO: replace with sorted ADT
	List<Legacy> all;
	
	public SalesReport() {
		all = new ArrayList<Legacy>();
	}
	
	public void parseFile(CSVParser<? extends Legacy> parser, File in) {
		all.addAll(parser.parse(in));
	}
	
	public Legacy findById(String id) {
		//TODO: binary search stopped working when SaleParser was changed??
		//int i = Collections.binarySearch(all, new Legacy(id), new LegacyComparator());
		
		for (Legacy l : all)
			if (l.getId().equals(id))
				return l;
		
//		if (i >= 0)
//			return all.get(i);
		
		return null;
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
		
		//Sort rows by name
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
						
						r.totalCents += s.getGrandTotal();
						grandTotalCents += s.getGrandTotal();
					}
				}
			}
		}
		
		//rows.get(0).sales = 24;
		
		System.out.printf("%-24s %4s  %4s\n","Salesperson","# Sales","Grand Total");
				
		for (SalespersonReportRow r : rows) {
			System.out.printf("%-24s %-4d     $%4d.%02d\n", r.salesperson.getFullNameFormal(), r.sales, r.totalCents/100, r.totalCents%100);
		}
		
		System.out.printf("%-24s %-4d     $%4d.%02d\n\n","Total Sales: ",totalSales, grandTotalCents/100, grandTotalCents%100);
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
						
						r.totalCents += s.getGrandTotal();
						grandTotalCents += s.getGrandTotal();
					}
				}
			}
		}
		
		System.out.printf("%-24s %-24s %4s  %4s\n","Store","Manager","# Sales","Grand Total");
		
		for (StoresReportRow r : rows) {
			System.out.printf("%-24s %-24s %-4d     $%4d.%02d\n", r.store.getId(), r.store.getManager().getFullNameFormal(), r.sales, r.totalCents/100, r.totalCents%100);
		}
		
		System.out.printf("%-49s %-4d     $%4d.%02d\n\n","Total sales: ",totalSales, grandTotalCents/100, grandTotalCents%100);
	}
	
	public void detailSaleReport() {
		for (Legacy l : all) {
			if (l instanceof Sale) {
				Sale s = (Sale)l;
				
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
						
						//TODO: move this?
						//TODO: clean this for sure
						
						if (i.getProductType() == ProductType.New) {
							detail = "(New Item) @$%d.%02d/ea".formatted((int)si.getLineSubtotal()/100, (int)si.getLineSubtotal()%100);
						} else if (i.getProductType() == ProductType.Used) {
							detail = "(Used Item) @$%d.%02d/ea".formatted((int)si.getLineSubtotal()/100, (int)si.getLineSubtotal()%100);
						} else if (i.getProductType() == ProductType.GiftCard) {
							detail = "(Gift Card)";
						}
						
					} else if (si instanceof Service) {
						Service sv = (Service)si;
						
						int price = si.getLineSubtotal();
					
						detail = "(Svc by %s %s) @$%d.%02d/hr".formatted(sv.getSalesperson().getId(), sv.getSalesperson().getFullNameFormal(), price/100, price%100);
						
					} else if (si instanceof Subscription) {
						Subscription sc = (Subscription)si;
						
						int days = (int)Math.round(((double)si.getLineSubtotal() / (double)sc.getBasePrice()) * 365);
						
						detail = "Subscription for %d days @$%d.%02d/yr".formatted(days, sc.getBasePrice()/100, sc.getBasePrice()%100);
						
					}
					
					System.out.printf("%-64s $%4d.%02d\n",detail, si.getLineSubtotal()/100, si.getLineSubtotal()%100);
				}
				
				System.out.printf("%74s: $%4d.%02d\n", "Subtotal", subtotal/100, subtotal%100);
				System.out.printf("%74s: $%4d.%02d\n", "Tax", totalTax/100, totalTax%100);

				int discount = s.getSubtotalTax() - s.getGrandTotal();
				System.out.printf("%65s (%05.2f%%): $%4d.%02d\n", "Discount", s.getCustomer().getCustomerDiscout()*100, discount/100, discount%100);
				System.out.printf("%74s: $%4d.%02d\n\n","Grand Total", s.getGrandTotal()/100, s.getGrandTotal()%100);
			}
		}
	}
	
	public void loadCSVs(String persons, String items, String stores, String sales) {
		this.parseFile(new PersonParser(), new File(persons));
		this.parseFile(new ProductParser(), new File(items));
		this.parseFile(new StoreParser(this), new File(stores));
		this.parseFile(new SaleParser(this), new File(sales));
		this.all.sort(new LegacyComparator());
	}
	
	/**
	 * Load CSV data to database for testing
	 */
	public void commitExampleData() {
		//TODO: Update to use ADT for dependency order sorting
		for (Legacy l : all) {
			if (l instanceof Person) {
				Person p = (Person)l;
				StreetAddress sa = p.getAddress();
				
				SalesData.addPerson(p.getId(), p.getCustomerTypeLetter(), p.getFirstName(), p.getLastName(), 
						sa.getStreet(), sa.getCity(), sa.getState(), sa.getZip(), sa.getCountry());
				
				for (String email : p.getEmail())
					SalesData.addEmail(p.getId(), email);
			}
		}
		
		for (Legacy l : all) {
			if (l instanceof Store) {
				Store s = (Store)l;
				StreetAddress sa = s.getAddress();
				
				SalesData.addStore(s.getId(), s.getManager().getId(), 
						sa.getStreet(), sa.getCity(), sa.getState(), sa.getZip(), sa.getCountry());
			}
		}
		
		for (Legacy l : all) {
			if (l instanceof Product) {
				Product p = (Product)l;
				if (p.isPlaceholder())
					SalesData.addItem(p.getId(), p.getProductTypeString(), p.getName(), ((double)p.getBasePrice())/100);
			}
		}
		
		for (Legacy l : all) {
			if (l instanceof Sale) {
				Sale s = (Sale)l;
				SalesData.addSale(s.getId(), s.getStore().getId(), s.getCustomer().getId(), s.getSalesperson().getId());
			}
		}
		
		for (Legacy l : all) {
			if (l instanceof Sale) {
				Sale s = (Sale)l;
				
				for (Product p : s.getItems()) {
					if (!p.isPlaceholder()) {
						if (p instanceof Item) {
							Item i = (Item)p;
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
	}
	
	/**
	 * Returns if the named database table is empty or non-existent
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
	
	public static void main(String[] args) {
		
		SalesReport sr = new SalesReport();
		sr.loadCSVs("data/Persons.csv", "data/Items.csv", "data/Stores.csv", "data/Sales.csv");
		
		
		SalesData.clearDatabase();
		sr.commitExampleData();
		
//		SalesData.addPerson("00ff7f", "G", "Bobby", "Tables", "1337 Havey Avenue", "Cleveland", "OH", "44177", "US");
//		SalesData.addEmail("00ff7f", "testemail2@gmail.com");
//		SalesData.addStore("f6f6f6", "00ff7f", "162 Bobus", "Omaha", "Nebraska", "68111", "US");
//		SalesData.addItem("foof70", "PN", "iPod Nano", 100.0);
//		SalesData.addItem("foof50", "PU", "iPod Touch", 50.0);
//		SalesData.addItem("foof20", "PG", "Fortnite $20", 0.0);
//		SalesData.addItem("foof10", "SV", "Repair2", 20.0);
//		SalesData.addItem("foof00", "SB", "NintendoPower2", 120.0);
//		SalesData.addSale("ffffff", "f6f6f6", "00ff7f", "00ff7f");
//		SalesData.addServiceToSale("ffffff", "foof10", "00ff7f", 2.0);
//		SalesData.addServiceToSale("ffffff", "foof10", "00ff7f", 1.5);
//		SalesData.addSubscriptionToSale("ffffff", "foof00", "2015-01-20", "2016-01-01");
		
//		sr.salespersonSummaryReport();
//		sr.storeSummaryReport();
//		sr.detailSaleReport();
	}
}
