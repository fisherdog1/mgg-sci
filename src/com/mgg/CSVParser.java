package com.mgg;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.zip.DataFormatException;

/**
 * Class which contains methods for parsing non-standard CSVs of Person
 * @author azimuth
 *
 */
public abstract class CSVParser<T extends Legacy>
{
	/**
	 * Parse one line of input
	 * Should be implemented by subclass depending on the object to be parsed
	 * @return
	 */
	public abstract T parseLine(String[] items) throws DataFormatException;
	
	/**
	 * @param in
	 * @return
	 */
	public List<T> parse(File in) {
		Scanner sc = null;
		
		try {
			sc = new Scanner(in);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		sc.useDelimiter("\n");
		
		int count = sc.nextInt();
		int lines = 0;
		
		ArrayList<T> out = new ArrayList<T>(count);
		
		while (sc.hasNext()) {
			String line = sc.next();
			lines++;
			
			if (line.isBlank())
				continue;
			
			String[] items = line.split(",", -1);
			//remove trailing blank columns
			
			int blanks = 0;
			for (int i = items.length-1; i >= 0; i--) {
				if (items[i].isBlank())
					blanks++;
				else
					break;
			}
		
			items = Arrays.copyOfRange(items, 0, items.length-blanks);
			
			try {
				out.add(parseLine(items));
			} catch(DataFormatException e) {
				throw new RuntimeException("Could not parse line %d: %s\nin file: %s\n".formatted(lines+1, line, in));
			}		
		}
		
		sc.close();
		
		if (count != lines) {
			throw new RuntimeException("Incorrect number of lines read, expected %d, read %d\n".formatted(count,lines));
		}
		
		return out;
	}
}
