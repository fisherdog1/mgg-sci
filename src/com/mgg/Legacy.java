package com.mgg;

import java.util.ArrayList;
import java.util.List;

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
	private boolean placeholder;
	
	public Legacy(String id) {
		this.legacyId = new String(id);
		//this.id = id.trim().toLowerCase();
		
		//basic consistency checks
	
		/**
		if (this.id.length() != 6)
			throw new RuntimeException("Bad Legacy ID length\n");
		
		for (int i = 0; i < 6; i++) {
			if (!("0123456789abcdef".contains(this.id.subSequence(i, i+1))))
				throw new RuntimeException("Legacy ID contains invalid character '%c'\n".formatted(this.id.charAt(i)));
		}
		**/ 
		
		//disallow id 000000?
	}
	
	/**
	 * Return (a copy of) the legacy ID
	 * @return
	 */
	public String getId() {
		return new String(legacyId);
	}
	
	public boolean getPlaceholder() {
		return placeholder;
	}
	
	/**
	 * Return legacy entities such as Persons
	 * Should be overridden by subclass if it has at least one Legacy as a child element
	 * @return
	 */
	public List<Legacy> getLegacys() {
		List<Legacy> legacys = new ArrayList<Legacy>(0);
		
		return legacys;
	}
	
	/**
	 * Return a list of Legacy entities that have not yet been associated with loaded objects
	 * Checks the list of Legacy entities and returns those that are placeholders
	 * @return
	 */
	public List<Legacy> getPlaceholders() {
		List<Legacy> placeholders = new ArrayList<Legacy>();
		
		for (Legacy placeholder : this.getLegacys())
			if (placeholder.getPlaceholder())
				placeholders.add(placeholder);
		
		return placeholders;
	}
}
