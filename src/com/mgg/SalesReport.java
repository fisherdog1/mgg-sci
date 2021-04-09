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
	
	/**
	 * Searches the list of entities for objects that fit the passed Legacy's placeholders
	 * TODO: this uses a binary search and so only has defined behavior when the list is sorted, check before running
	 * @param leg
	 */
	public void updateAssociation(Legacy leg) {
		
		//TODO: there has to be a generic way to do this
		
		if (leg instanceof Store) {
			if (((Store) leg).getManager().isPlaceholder()) {
				Person mgr = (Person)findById(((Store) leg).getManager().getId());
				if (mgr != null)
					((Store) leg).setManager(mgr);
			}
				
		} else if (leg instanceof Sale) {
			if (((Sale) leg).getStore().isPlaceholder()) {
				Store store = (Store)findById(((Sale) leg).getStore().getId());
				if (store != null)
					((Sale) leg).setStore(store);
			}
			
			if (((Sale) leg).getCustomer().isPlaceholder()) {
				Person cust = (Person)findById(((Sale) leg).getCustomer().getId());
				if (cust != null)
					((Sale) leg).setCustomer(cust);
			}
			
			if (((Sale) leg).getSalesperson().isPlaceholder()) {
				Person salesp = (Person)findById(((Sale) leg).getSalesperson().getId());
				if (salesp != null)
					((Sale) leg).setSalesperson(salesp);
			}
			
			for (SaleItem item : ((Sale) leg).getItems()) {
				item.setProduct((Product)findById(item.getProduct().getId()));
			}
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
				
				for (SaleItem<?> si : s.getItems()) {
					
					System.out.printf("%-48s\n", si.getProduct().getName());
					System.out.printf("%10s ", si.getProduct().getId());
					
					String detail = "";
					
					if (si.getProduct() instanceof Item) {
						Item i = (Item)si.getProduct();
						
						//TODO: move this?
						//TODO: clean this for sure
						
						if (i.getProductType() == ProductType.New) {
							detail = "(New Item) @$%d.%02d/ea".formatted((int)si.getSalePrice()/100, (int)si.getSalePrice()%100);
						} else if (i.getProductType() == ProductType.Used) {
							detail = "(Used Item) @$%d.%02d/ea".formatted((int)si.getSalePrice()/100, (int)si.getSalePrice()%100);
						} else if (i.getProductType() == ProductType.GiftCard) {
							detail = "(Gift Card)";
						}
						
					} else if (si.getProduct() instanceof Service) {
						Service sv = (Service)si.getProduct();
						
						int price = si.getSalePrice();
						String salespersonId = ((ServiceSaleItem)si).getServicepersonId();
						String salespersonName = ((Person)findById(salespersonId)).getFullNameFormal();
					
						detail = "(Svc by %s %s) @$%d.%02d/hr".formatted(salespersonId, salespersonName, price/100, price%100);
						
					} else if (si.getProduct() instanceof Subscription) {
						Subscription sc = (Subscription)si.getProduct();
						
						int days = (int)Math.round(((double)si.getSalePrice() / (double)sc.getAnnualFee()) * 365);
						
						detail = "Subscription for %d days @$%d.%02d/yr".formatted(days, sc.getAnnualFee()/100, sc.getAnnualFee()%100);
						
					}
					
					System.out.printf("%-64s $%4d.%02d\n",detail, si.getSalePrice()/100, si.getSalePrice()%100);
				}
				
				System.out.printf("%74s: $%4d.%02d\n", "Subtotal", subtotal/100, subtotal%100);
				System.out.printf("%74s: $%4d.%02d\n", "Tax", totalTax/100, totalTax%100);

				int discount = s.getSubtotalTax() - s.getGrandTotal();
				System.out.printf("%65s (%05.2f%%): $%4d.%02d\n", "Discount", s.getCustomer().getCustomerDiscout()*100, discount/100, discount%100);
				System.out.printf("%74s: $%4d.%02d\n\n","Grand Total", s.getGrandTotal()/100, s.getGrandTotal()%100);
			}
		}
	}
	
	public static void main(String[] args) {
		
		SalesReport sr = new SalesReport();
		
		sr.parseFile(new PersonParser(), new File("data/Persons.csv"));
		sr.parseFile(new StoreParser(), new File("data/Stores.csv"));
		sr.parseFile(new ProductParser(), new File("data/Items.csv"));
		
		//TODO: do this automatically when new items are added (tree structure?)
		sr.all.sort(new LegacyComparator());
		
		sr.parseFile(new SaleParser(sr), new File("data/Sales.csv"));
	
		sr.updateAllAssociations();
		
		sr.salespersonSummaryReport();
		sr.storeSummaryReport();
		sr.detailSaleReport();
	}
}
