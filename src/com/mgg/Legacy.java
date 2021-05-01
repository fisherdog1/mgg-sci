package com.mgg;

/**
 * Hex legacy ID. Provides slightly better control of IDs
 * Should eventually provide tools for organization and control of imported IDs
 * placeholder indicates that this Legacy should be replace by an existing subclass with the same ID, 
 * which may be loaded from another file
 * @author azimuth
 *
 */
public abstract class Legacy
{
	private String legacyId;
	
	public Legacy(String id) {
		this.legacyId = new String(id);
	}
	
	/**
	 * Return (a copy of) the legacy ID
	 * @return
	 */
	public String getId() {
		return new String(legacyId);
	}
}
