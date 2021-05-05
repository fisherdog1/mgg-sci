package com.mgg;

/** 
 * Interface for providing an already loaded Legacy from SalesReport
 * Used when loading things like Store from the database when the manager Person
 * has already been loaded. Also used by SalesParser to locate prototypes for products.
 * 
 * @author azimuth
 *
 */
public interface LegacyProvider
{
	/**
	 * Return the class name of the matched id in the products list
	 * @param id
	 * @return
	 */
	public abstract Legacy findById(String id);
}
