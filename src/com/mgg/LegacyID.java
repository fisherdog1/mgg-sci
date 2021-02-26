package com.mgg;

/**
 * Hex legacy ID. Provides slightly better control of IDs
 * Should eventually provide tools for organization and control of imported IDs
 * @author azimuth
 *
 */
public class LegacyID
{
	private String id;
	
	public LegacyID(String id) {
		this.id = id.trim().toLowerCase();
		
		//basic consistency checks
		
		if (this.id.length() != 6)
			throw new RuntimeException("Bad Legacy ID length\n");
		
		for (int i = 0; i < 6; i++) {
			if (!("0123456789abcdef".contains(this.id.subSequence(i, i+1))))
				throw new RuntimeException("Legacy ID contains invalid character '%c'\n".formatted(this.id.charAt(i)));
		}
		
		//disallow id 000000?
	}
	
	/**
	 * Return (a copy of) the legacy ID
	 * @return
	 */
	public String getId() {
		return new String(id);
	}
}
