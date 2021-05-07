package com.mgg;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Column formatting class. Used by ReportBuilder to pretty print reports. Strings are
 * added the this object and may be returned one line at a time with constant width as
 * well as other features such as word wrap
 * 
 * TODO: word wrap modes and selectable delimiter for word wrap
 * 
 * @author azimuth
 *
 */
public class ReportColumn<T> implements Iterable<String>
{
	protected ArrayList<T> fields;
	private int columnWidth;
	private int maximumWidth;
	private ColumnAlignment horizontalAlignment;
	private StringPrinter<T> printer;
	private boolean seperator; //used to create trivial columns such as '$' or a vertical separator
	
	/**
	 * Returns one line at a time with constant width equal to the columnWidth of the generating ReportColumn
	 * @author azimuth
	 *
	 */
	class ReportColumnIterator implements Iterator<String>
	{
		private ReportColumn<T> col;
		private int position;
		private int field;
		
		public ReportColumnIterator(ReportColumn<T> col) {
			this.col = col;
			this.position = 0;
			this.field = 0;
		}
		
		public boolean isRepeating() {
			return col.seperator;
		}
		
		/**
		 * Get the number of the current field. 
		 * Used for printing columns with a variable number of lines per field
		 * @return
		 */
		public int getFieldNumber() {
			return field;
		}
		
		/**
		 * Returns an empty string the same width as the column
		 * Useful for printing columns with variable lines per field
		 * @return
		 */
		public String getPadding() {
			return " ".repeat(col.columnWidth);
		}
		
		@Override
		public boolean hasNext()
		{
			return this.field < col.fields.size();
		}

		@Override
		public String next()
		{
			String originalFieldString = printer.print(col.fields.get(field));

			//Generate one line and update position by the number of actual characters
			String f = originalFieldString.substring(position);
			
			//Word wrap
			if (f.length() > col.maximumWidth) {
				//TODO split around spaces
				
				String words[] = f.split(" ");
				f = "";
				int i = 0;
					
				//Try to split to fit in column
				while (i < words.length && (f.length() + words[i].length()) <= col.maximumWidth)
					if (words[i].isEmpty()) {
						i++;
						position++; //Secret sauce
					} else
						f = f + words[i++] + " ";	
				
				int actualLength = f.length(); //Actual length, needs saved before trim operation
				f = f.trim(); //Trim trailing space
				
				//Couldn't fit a single word
				if (i == 0)
					f = originalFieldString.substring(position, position += (col.maximumWidth - 1)) + "-";
				else
					position += actualLength;
				
			} else {
				position = 0;
				field++;
			}

			String fmt;
			if (col.horizontalAlignment == ColumnAlignment.Right)
				fmt = "%"+col.columnWidth+"s";
			else
				fmt = "%-"+col.columnWidth+"s";
			
			f = fmt.formatted(f);
			
			if (col.seperator && field == col.fields.size())
				field = 0;
			
			return f;
		}
	}
	
	/**
	 * An example of using ReportColumn to make a separator
	 * @return
	 */
	public static ReportColumn<String> verticalSeparator(String separator) {
		ReportColumn<String> rc = new ReportColumn<String>();
		rc.setWidth(separator.length());
		rc.seperator = true;
		rc.addField(separator);
		return rc;
	}
	
	public ReportColumn() {
		this.fields = new ArrayList<T>();
		this.columnWidth = 1;
		this.maximumWidth = 20;
		this.horizontalAlignment = ColumnAlignment.Left;
		this.seperator = false;
		this.printer = (StringPrinter<T>)StringPrinter.defaultPrinter();
	}
	
	public void setPrinter(StringPrinter<T> printer) {
		this.printer = printer;
	}
	
	public ColumnAlignment getHorizontalAlignment() {
		return this.horizontalAlignment;
	}
	
	private void updateColumnWidth(int length) {
		if (length > columnWidth) {
			if (length <= maximumWidth)
				columnWidth = length;
			else
				columnWidth = maximumWidth;
		}
	}
	
	/**
	 * Add a field to the specified position in the column
	 * Should not be possible to do directly if the column is part of a section, the section will
	 * need to manage rearranging adjacent columns to keep data in the same rows.
	 * @param field
	 * @param position
	 */
	public int addField(T field, int position) {
		this.fields.add(position, field);
		
		int length = field.toString().length();
		this.updateColumnWidth(length);
		return position;
	}
	
	/**
	 * Add a field to the end of the column
	 * @param field
	 */
	public void addField(T field) {
		this.addField(field, fields.size());
	}
	
	public void addAll(ArrayList<T> items) {
		for (T item : items)
			this.addField(item);
	}
	
	/**
	 * Remove the field at the specified position
	 * Should not be possible to do directly if the column is part of a section, the section will
	 * need to manage rearranging adjacent columns to keep data in the same rows.
	 */
	public void removeField(int position) {
		this.fields.remove(position);
	}
	
	public int getWidth() {
		return columnWidth;
	}
	
	/**
	 * Set the column width for printing. If width is larger tahn maximumWidth, maximum width is also increased.
	 * @param width
	 */
	public void setWidth(int width) {
		if (width >= this.maximumWidth)
			this.maximumWidth = width;
		
		this.columnWidth = width;
	}
	
	/**
	 * Set the maximum width for printing. If width is smaller than the current width, it is also reduced.
	 * @param width
	 */
	public void setMaximumWidth(int width) {
		if (width <= this.maximumWidth)
			this.columnWidth = width;
		
		this.maximumWidth = width;
	}

	@Override
	public String toString() {
		String out = "";
		
		for (String s : this)
			out = out + s + "\n";
		
		return out;
	}
	
	@Override
	public ReportColumnIterator iterator()
	{
		return new ReportColumnIterator(this);
	}

	public void setAlignment(ColumnAlignment align) {
		this.horizontalAlignment = align;
	}

	public boolean isSeparator() {
		return this.seperator;
	}

	public Object getField(int rowNumber) {
		Object field = fields.get(rowNumber);
		return field;
	}
}
