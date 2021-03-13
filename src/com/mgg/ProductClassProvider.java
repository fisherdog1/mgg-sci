package com.mgg;

/** 
 * I don't know how else to fix this, there are some entries in sales.csv that can be either quantity items or gift cards
 * There is not a consistent decimal place to disambiguate them, so the SalesReport class will have to tell the parser
 * what each thing by how its listed in the already imported products. This could have side effects
 * 
 * @author azimuth
 *
 */
public interface ProductClassProvider
{
	/**
	 * Return the class name of the matched id in the products list
	 * @param id
	 * @return
	 */
	public abstract Legacy findById(String id);
}
