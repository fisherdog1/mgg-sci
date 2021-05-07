package com.mgg;

import java.util.Collection;

public class StoreSummaryRowConsumer implements RowConsumer<Legacy>
{
	private Collection<Legacy> deps;
	private ReportSection table;
	private Iterable<Sale> sales;
	
	public StoreSummaryRowConsumer(Collection<Legacy> deps, Iterable<Sale> sales, ReportSection table) {
		this.deps = deps;
		this.table = table;
		this.sales = sales;
	}

	@Override
	public Object getColumn(Legacy source, String columnName)
	{
		if (source instanceof Store) {
			Store s = (Store)source;
			
			if (columnName.equals("Store Code"))
				return s.getId();
			else if (columnName.equals("Manager"))
				return s.getManager().getFullNameFormal();
			else if (columnName.equals("Sales")) {
				int count = 0;
				
				for (Sale sale : sales) {
					if (sale.getStore().equals(s))
						count++;
				}
				
				return count;
			} else if (columnName.equals("Grand Total")) {
				int total = 0;
				
				for (Sale sale : sales) {
					if (sale.getStore().equals(s))
						total += sale.getGrandTotal();
				}
				
				return total;
			} else
				return null;
		} else
			return null;
	}
}
