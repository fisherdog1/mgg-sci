package com.mgg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import com.mgg.ReportColumn.ReportColumnIterator;

/**
 * A ReportSection combines multiple ReportColumn objects so they can be iterated through one line at a time
 * 
 * TODO: one field at a time iteration as well
 * 
 * @author azimuth
 *
 */
public class ReportSection implements Iterable<String>
{
	private LinkedHashMap<String, ReportColumn<?>> columns;
	private int maximumWidth;
	private boolean printHeader;
	
	class ReportSectionIterator implements Iterator<String>
	{
		private ArrayList<ReportColumnIterator> iterators;
		private int field = 0;
		
		public ReportSectionIterator(ReportSection sec) {
			this.field = 0;
			this.iterators = new ArrayList<ReportColumnIterator>();
			
			for (String key : sec.columns.keySet())
				iterators.add(sec.columns.get(key).iterator());
		}
		
		@Override
		public boolean hasNext()
		{
			for (ReportColumnIterator rci : iterators)
				if (!rci.isRepeating() && rci.hasNext())
					return true;
					
			return false;
		}

		@Override
		public String next()
		{
			String line = "";
			
			boolean nextField = true;
			
			for (ReportColumnIterator rci : iterators) {

				if (rci.isRepeating() || (rci.hasNext() && rci.getFieldNumber() == field))
					line += rci.next();
				else
					line += rci.getPadding();
				
				if (!rci.isRepeating() && rci.getFieldNumber() != field + 1)
					nextField = false;
			}
			
			if (nextField)
				field++;
			
			return line;
		}
	}
	
	public ReportSection() {
		this.columns = new LinkedHashMap<String, ReportColumn<?>>();
		this.maximumWidth = 80;
		this.printHeader = false;
	}
	
	public void setPrintHeader(boolean printHeader) {
		this.printHeader = printHeader;
	}
	
	public <T> void addRow(T source, RowConsumer<T> rc) {
		int c = 0;
		
		for (String key : columns.keySet()) {
			ReportColumn col = columns.get(key);
			Object value = rc.getColumn(source, key);
			if (value != null)
				col.addField(value);
		}
	}
	
	public <T> void addRows(Collection<T> source, RowConsumer<T> rc) {
		for (T row : source) 
			this.addRow(row, rc);
	}
	
	public Object getField(String columnName, int rowNumber) {
		return columns.get(columnName).getField(rowNumber);
	}
	
	/**
	 * Re-calculate justification based on maximumWidth
	 */
	private void adjustJustification(int max) {
		int width = this.maximumWidth;
		//Adjust justification
		String cols[] = new String[this.columns.size()];
		this.columns.keySet().toArray(cols);
		
		for (int i = 0; i < cols.length; i++) {
			ReportColumn col = this.columns.get(cols[i]);
			if (i != max)
				width -= col.getWidth();
		}
		
		this.columns.get(cols[max]).setWidth(width);
	}
	
	public void maximizeColumn(String columnName) {
		int width = this.maximumWidth;
		//Adjust justification
		String cols[] = new String[this.columns.size()];
		this.columns.keySet().toArray(cols);
		
		for (int i = 0; i < cols.length; i++) {
			ReportColumn col = this.columns.get(cols[i]);
			if (!col.equals(columnName))
				width -= col.getWidth();
		}
		
		this.columns.get(columnName).setWidth(width);
	}
	
	public void addColumn(String name, ReportColumn<?> rc) {
		this.columns.put(name, rc);
	}

	/**
	 * Returns a string which can be passed to printf to generate a header for this section
	 * @return
	 */
	public String getColumnFormatString() {
		adjustJustification(0);
		String out = "";
		
		for (String key : columns.keySet()) {
			ReportColumn rc = columns.get(key);
			
			if (rc.isSeparator()) {
				out += " ".repeat(rc.getWidth());
			} else {
				out += "%";
				
				if (rc.getHorizontalAlignment() == ColumnAlignment.Left)
					out += "-";
				
				out += rc.getWidth() + "s";
			}
		}
		
		return out;
	}
	
	@Override
	public Iterator<String> iterator()
	{
		return new ReportSectionIterator(this);
	}
	
	@Override
	public String toString() {
		adjustJustification(0);
		String out = "";
		
		Set<String> headers = this.columns.keySet();
		ArrayList<String> headersArray = new ArrayList<String>();
		
		for (String head : headers)
			if (!this.columns.get(head).isSeparator())
				headersArray.add(head);
		
		if (printHeader) {
			String fmt = this.getColumnFormatString() + "\n";
			out += fmt.formatted(headersArray.toArray());
		}
		
		for (String line : this)
			out += line + "\n";
		
		return out;
	}
}
