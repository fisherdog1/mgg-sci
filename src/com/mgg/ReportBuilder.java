package com.mgg;
import java.util.ArrayList;
import java.util.Collection;

import com.mgg.ReportSection;

/**
 * Builds and formats a report using using-provided interfaces to extract columns from report items
 * 
 * Planned features: 
 * 	Automatically make columns optimally wide
 * 	Provide convenience formatter for printing a dollar amt from integer cents
 * 	Other convenience methods?
 * 	
 * A ReportBuilder combines multiple sections
 * 	
 * @author azimuth
 *
 */
public class ReportBuilder
{
	ArrayList<ReportSection> sections;
	
	public ReportBuilder() {
		this.sections = new ArrayList<ReportSection>();
	}
	
	public static ReportBuilder detailSaleReportBuilder() {
		//TODO implement detail report 
		return null;
	}
	
	public static ReportBuilder storeSummaryReportBuilder(Collection<Legacy> dep, Iterable<Sale> sales) {
		ReportBuilder rb = new ReportBuilder();
		
		ReportSection rs1 = new ReportSection();
		rs1.setPrintHeader(true);
		
		ReportColumn<String> storeCodeCol = new ReportColumn<String>();
		storeCodeCol.setWidth(14);
		rs1.addColumn("Store Code", storeCodeCol);
		
		ReportColumn<String> managerCol = new ReportColumn<String>();
		managerCol.setWidth(24);
		rs1.addColumn("Manager", managerCol);
		
		ReportColumn<String> numSales = new ReportColumn<String>();
		numSales.setWidth(6);
		rs1.addColumn("Sales", numSales);
		
		ReportColumn<Integer> totalCol = new ReportColumn<Integer>();
		totalCol.setWidth(16);
		totalCol.setAlignment(ColumnAlignment.Right);
		totalCol.setPrinter(StringPrinter.moneyPrinter());
		rs1.addColumn("Grand Total", totalCol);
		
		rb.sections.add(rs1);
		
		RowConsumer<Legacy> consumer = new StoreSummaryRowConsumer(dep, sales, rs1);
		
		rs1.addRows(dep, consumer);
		
		return rb;
	}
	
	public static ReportBuilder salespersonSummaryReportBuilder(Iterable<Sale> sales, Collection<Legacy> dep) {
		ReportBuilder rb = new ReportBuilder();
		
		ReportSection rs1 = new ReportSection();
		rs1.setPrintHeader(true);
		
		ReportColumn<String> salespersonCol = new ReportColumn<String>();
		salespersonCol.setWidth(24);
		rs1.addColumn("Salesperson", salespersonCol);
		
		ReportColumn<Integer> salesCol =  new ReportColumn<Integer>();
		salesCol.setWidth(12);
		salesCol.setAlignment(ColumnAlignment.Right);
		rs1.addColumn("No. Sales", salesCol);
		
		ReportColumn<Integer> totalSales = new ReportColumn<Integer>();
		totalSales.setWidth(12);
		totalSales.setPrinter(StringPrinter.moneyPrinter());
		totalSales.setAlignment(ColumnAlignment.Right);
		rs1.addColumn("Total Sales", totalSales);
		
		RowConsumer<Legacy> consumer = new RowConsumer<Legacy>() {

			@Override
			public Object getColumn(Legacy source, String columnName)
			{
				if (source instanceof Person) {
					Person p = (Person)source;
					
					if (p.getType() == CustomerType.Employee) {
						if (columnName.equals("Salesperson"))
							return p.getLastName() + ", " + p.getFirstName();
						else if (columnName.equals("No. Sales")) {
							int saleCount = 0;
							
							for (Sale s : sales)
								if (s.getSalesperson().equals(p))
									saleCount++;
							
							return (Integer)saleCount;
						} else if (columnName.equals("Total Sales")) {
							int saleTotal = 0;
							
							for (Sale s : sales)
								if (s.getSalesperson().equals(p))
									saleTotal += s.getGrandTotal();
							
							return (Integer)saleTotal;
						} else
							return null;
					} else
						return null;
				} else
					return null;
			}
		};
	
		rs1.addRows(dep, consumer);
		
		ReportSection rs2 = new ReportSection();
		
		ReportColumn<String> totalLabel = new ReportColumn<String>();
		totalLabel.setWidth(12);
		totalLabel.setAlignment(ColumnAlignment.Right);
		rs2.addColumn("label", totalLabel);
		
		ReportColumn<String> grandTotalSales = new ReportColumn<String>();
		grandTotalSales.setWidth(4);
		grandTotalSales.setAlignment(ColumnAlignment.Right);
		rs2.addColumn("sales", grandTotalSales);
		
		ReportColumn<Integer> grandTotal = new ReportColumn<Integer>();
		grandTotal.setAlignment(ColumnAlignment.Right);
		grandTotal.setPrinter(StringPrinter.moneyPrinter());
		grandTotal.setWidth(12);
		rs2.addColumn("total", grandTotal);
		
		int saleCount = 0;
		int total = 0;
		for (Sale s : sales) {
			total += s.getGrandTotal();
			saleCount++;
		}
		
		rs2.addRow("Total:", RowConsumer.trivial("label"));
		rs2.addRow(total, RowConsumer.trivial("total"));
		rs2.addRow(saleCount, RowConsumer.trivial("sales"));
		
		rb.sections.add(rs1);
		rb.sections.add(rs2);
		
		return rb;
	}
	
	/**
	 * Build and return the report
	 */
	@Override
	public String toString() {
		String out = "";
		
		for (ReportSection section : sections)
			out += section;
		
		return out;
	}
}
