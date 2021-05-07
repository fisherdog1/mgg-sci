package com.mgg;

/**
 * An interface for building ReportSection rows
 * @author azimuth
 *
 * @param <T>
 */
public interface RowConsumer<T>
{
	public abstract Object getColumn(T source, String columnName);
	
	public static RowConsumer<Object> trivial(String column) {
		return new RowConsumer<Object>() {
			@Override
			public Object getColumn(Object source, String columnName) {
				if (column.equals(columnName))
					return source;
				else
					return null;
			}
		};
	}
}
