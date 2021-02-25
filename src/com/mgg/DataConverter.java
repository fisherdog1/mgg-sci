package com.mgg;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DataConverter
{

	public static void gsonWriteList(Gson gson, File out, List<?> in) {
		FileWriter writer;
		
		try {
			writer = new FileWriter(out);

			String json = gson.toJson(in);
			writer.write(json);
			
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException("Could not open or write to output file\n");
		}
	}
	
	public static void main(String[] args)
	{
		GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting();
		
		Gson gson = builder.create();
		
		List<Person> personList = new PersonParser().parse(new File("data/Persons.csv"));
		List<Store> storeList = new StoreParser().parse(new File("data/Stores.csv"));
		List<SaleItem> itemList = new SaleItemParser().parse(new File("data/Items.csv"));
		

		gsonWriteList(gson, new File("data/Persons.json"), personList);
		gsonWriteList(gson, new File("data/Stores.json"), storeList);
		gsonWriteList(gson, new File("data/Items.json"), itemList);
	}
}
