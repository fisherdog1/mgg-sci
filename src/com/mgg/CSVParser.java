package com.mgg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Class which contains methods for parsing non-standard CSVs of Person
 * @author azimuth
 *
 */
public abstract class CSVParser<T>
{
	/**
	 * Parse one line of input
	 * Should be implemented by subclass depending on the object to be parsed
	 * @return
	 */
	public abstract T parseLine(String s);
	
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
		
		ArrayList<T> out = new ArrayList<T>(count);
		
		while (sc.hasNext()) {
			String line = sc.next();
			out.add(parseLine(line));
		}
		
		sc.close();
		return out;
	}
}
