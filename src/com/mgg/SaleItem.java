package com.mgg;

import java.util.Map;
import java.util.TreeMap;

/**
 * Combines a product with parameters from the time of sale such as quantity or gift card amount
 * @author azimuth
 *
 */
public class SaleItem
{
	private Product product;
	private Map<String,Object> params;
	
	public SaleItem(Product product) {
		this.product = product;
		params =  new TreeMap<String,Object>();
	}
	
	public void addParameter(String name, Object value) {
		params.put(name, value);
	}
	
	public Map<String,Object> getParameters() {
		return params;
	}
	
	public Product getProduct() {
		return this.product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}
}
