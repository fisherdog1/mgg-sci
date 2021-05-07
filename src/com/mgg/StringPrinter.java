package com.mgg;

/**
 * Allows specifying an alternate formatting for ReportColumns
 * @author azimuth
 *
 */
public interface StringPrinter<T>
{
	public abstract String print(T item);
	
	public static StringPrinter<?> defaultPrinter() {
		return new StringPrinter() {
			@Override
			public String print(Object item)
			{
				return item.toString();
			}
		};
	}
	
	public static StringPrinter<Integer> moneyPrinter() {
		return new StringPrinter<Integer>() {
			@Override
			public String print(Integer item)
			{
				return "$% 6d.%02d".formatted(item/100, item%100);
			}
		};
	}
}
