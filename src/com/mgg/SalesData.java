package com.mgg;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Database interface class
 */
public class SalesData {

	/**
	 * Open an SQL/JDBC connection using the driver manager and the static connection parameters in DatabaseInfo
	 * Caller is responsible for committing changes and closing the connection
	 * @return
	 */
	private static Connection obtainConnection() {
		Connection con;
		try {
			con = DriverManager.getConnection(DatabaseInfo.URL, DatabaseInfo.USERNAME, DatabaseInfo.PASSWORD);
			con.setAutoCommit(false);
		} catch (SQLException e) {
			throw new RuntimeException("SQL Exception opening connection",e);
		}
		
		return con;
	}
	
	private static void commitConnection(Connection con) {
		try {
			con.commit();
		} catch (SQLException e) {
			throw new RuntimeException("SQL Exception on commit",e);
		}
	}
	
	private static void commitAndClose(Connection con) {
		commitConnection(con);
		
		try {
			con.close();
		} catch (SQLException e) {
			throw new RuntimeException("SQL Exception on commit",e);
		}
	}
	
	/**
	 * Removes all sales records from the database.
	 */
	public static void removeAllSales() {
		//Delete all items (which dont have a null saleId?) from Item, Service, and Subscription
		//Then delete all items from Sale table
		
		Connection con = obtainConnection();
		
		try {
			String st = "delete from Subscription;\n"
					+ "delete from Service;\n"
					+ "delete from Item;\n"
					+ "delete from Sale;\n";
			
			String[] statements = st.split("\n");
			
			for (String s : statements) {
				PreparedStatement ps = con.prepareStatement(s);
				ps.execute();
			}
			
			commitAndClose(con);
		} catch (SQLException e) {
			throw new RuntimeException("SQL Exception",e);
		}
	}

	/**
	 * Removes the single sales record associated with the given
	 * <code>saleCode</code>
	 * 
	 * @param saleCode
	 */
	public static void removeSale(String saleCode) {
		//Delete all Item, Service, and Subscription that refer to the Sale
		//Then delete the sale
	}

	/**
	 * Clears all tables of the database of all records.
	 */
	public static void clearDatabase() {
		//Empty all tables in proper order

		Connection con = obtainConnection();
		
		try {
			String st = "delete from Subscription;\n"
					+ "delete from Service;\n"
					+ "delete from Item;\n"
					+ "delete from Sale;\n"
					+ "delete from Store;\n"
					+ "delete from PersonEmail;\n"
					+ "delete from Person;\n"
					+ "delete from Email;\n"
					+ "delete from Address;";
			
			String[] statements = st.split("\n");
			
			for (String s : statements) {
				PreparedStatement ps = con.prepareStatement(s);
				ps.execute();
			}
			
			commitAndClose(con);
		} catch (SQLException e) {
			throw new RuntimeException("SQL Exception",e);
		}
	}

	/**
	 * Method to add a person record to the database with the provided data. The
	 * <code>type</code> will be one of "E", "G", "P" or "C" depending on the type
	 * (employee or type of customer).
	 * 
	 * @param personCode
	 * @param type
	 * @param firstName
	 * @param lastName
	 * @param street
	 * @param city
	 * @param state
	 * @param zip
	 * @param country
	 */
	public static void addPerson(String personCode, String type, String firstName, String lastName, String street,
			String city, String state, String zip, String country) {
		//Check for this address in the database, if it is not present, add it
		//Check if Person is in database, if not, add Person to Person table
		Connection con = obtainConnection();
		
		int addressId = containsAddress(con, street, city, state, zip, country);
		
		if (addressId == -1) 
			addAddress(street,city,state,zip,country);
		
		//TODO this fails if address was just added as above
		addressId = containsAddress(con, street, city, state, zip, country);
		
		if (containsPerson(con, personCode) != -1)
			return; //Person already in database
		
		try {
			String st = "insert into Person (legacyId, firstName, lastName, customerType, addressId) values (\n"
					+ " ?,\n"
					+ " ?,\n"
					+ " ?,\n"
					+ " ?,\n"
					+ " ?);";
					
			PreparedStatement ps = con.prepareStatement(st);
			ps.setString(1, personCode);
			ps.setString(2, firstName);
			ps.setString(3, lastName);
			
			String ctype;
			
			if (type.equals("E"))
				ctype = "employee";
			else if (type.equals("G"))
				ctype = "gold";
			else if (type.equals("P"))
				ctype = "platinum";
			else
				ctype = "customer";
			
			ps.setString(4, ctype);
			ps.setInt(5, addressId);
			
			ps.execute();
			commitAndClose(con);
			
		} catch (SQLException e) {
			throw new RuntimeException("SQL Exception",e);
		}
	}

	/**
	 * Returns the ID of the described StreetAddress or -1 if it is not present in the database
	 * @param street
	 * @param city
	 * @param state
	 * @param zip
	 * @param country
	 * @return
	 */
	private static int containsAddress(Connection con, String street, String city, String state, String zip, String country) {
		
		int addressId = -1;
		try {
			con.commit(); //bug fix?
			
			String st = "select count(a.addressId) as count, a.addressId from Address a where\n"
					+ 	"	a.street = ? and\n"
					+ 	"    a.city = ? and\n"
					+ 	"    a.state = ? and\n"
					+ 	"    a.zip = ? and\n"
					+ 	"    a.country = ?;";
					
			PreparedStatement ps = con.prepareStatement(st);
			ps.setString(1, street);
			ps.setString(2, city);
			ps.setString(3, state);
			ps.setString(4, zip);
			ps.setString(5, country);
			
			ps.execute();
			
			ResultSet rs = ps.getResultSet();
			rs.next();
			int count = rs.getInt("count");
			if (count > 0)
				return rs.getInt("addressId"); //Address already present
			
		} catch (SQLException e) {
			throw new RuntimeException("SQL Exception",e);
		}
		
		return addressId;
	}
	
	/**
	 * Returns the ID of the Person or -1 if not in database
	 * @return
	 */
	public static int containsPerson(Connection con, String personCode) {
		int personId = -1;
		
		try {
			String st = "select count(p.personId) as count, p.personId from Person p where\n"
					+ 	"p.legacyId = ?;";
					
			PreparedStatement ps = con.prepareStatement(st);
			ps.setString(1, personCode);
			ps.execute();
			
			ResultSet rs = ps.getResultSet();
			rs.next();
			int count = rs.getInt("count");
			if (count > 0)
				return rs.getInt("personId"); //Person already present
			
		} catch (SQLException e) {
			throw new RuntimeException("SQL Exception",e);
		}
		
		return personId;
	}
	
	/**
	 * Add a street address to the Address table
	 * @param street
	 * @param city
	 * @param state
	 * @param zip
	 * @param country
	 */
	public static void addAddress(String street, String city, String state, String zip, String country) {
		//Check if address present
		Connection con = obtainConnection();
		
		try {
			if (containsAddress(con,street,city,state,zip,country) != -1)
				return;	//Return if address already in database
			
			//Add new Address
			
			String st = "insert into Address (street, city, state, zip, country) values (\n"
				+ "	?,\n"
				+ " ?,\n"
				+ " ?,\n"
				+ " ?, \n"
				+ " ?);";
			
			PreparedStatement ps = con.prepareStatement(st);
			ps.setString(1, street);
			ps.setString(2, city);
			ps.setString(3, state);
			ps.setString(4, zip);
			ps.setString(5, country);
			
			ps.execute();
			commitAndClose(con);
			
		} catch (SQLException e) {
			throw new RuntimeException("SQL Exception",e);
		}
	}
	
	/**
	 * Adds an email record corresponding person record corresponding to the
	 * provided <code>personCode</code>
	 * 
	 * @param personCode
	 * @param email
	 */
	public static void addEmail(String personCode, String email) {
		//Check if email present in Email table, add if it is not present
		Connection con = obtainConnection();
		PreparedStatement ps;
		
		try {
			int emailId;
			boolean emailAdded = false;
			
			String st1 = "select count(e.emailId) as count, e.emailId from Email e where\n"
					+ 	"e.address = ?;";
					
			ps = con.prepareStatement(st1);
			ps.setString(1, email);
			
			ps.execute();
			
			ResultSet rs = ps.getResultSet();
			rs.next();
			
			int count = rs.getInt("count");
			if (count > 0) {
				//Email already present
				emailId = rs.getInt("emailId");
			
			} else {
				//Add new Email
				
				String st2 = "insert into Email (address) values (\n"
					+"?);";
						
				ps = con.prepareStatement(st2);
				ps.setString(1, email);
				
				ps.execute();
				commitConnection(con);
				
				//Get id of newly added email
				
				ps = con.prepareStatement(st1);
				ps.setString(1, email);
				
				ps.execute();
				rs = ps.getResultSet();
				rs.next();
				emailId = rs.getInt("emailId");
				emailAdded = true;
				
			}
			
			//Add PersonEmail (It cant already exist if the email was just added)
			int personId = containsPerson(con, personCode);
			
			if (personId == -1)
				return; //Person not present
			
			if (emailAdded == false) {
				//Check if PersonEmail already exists
				String st4 = "select count(pe.personId) as count from PersonEmail pe where\n"
						+ "	pe.personId = ? and\n"
						+ " pe.emailId = ?;";
				
				ps = con.prepareStatement(st4);
				ps.setInt(1, personId);
				ps.setInt(2, emailId);
				ps.execute();
				
				rs = ps.getResultSet();
				rs.next();
				
				count = rs.getInt("count");
				if (count > 0)
					//PersonEmail already present
					return;
				
			}
				
			String st3 = "insert into PersonEmail (personId, emailId) values (\n"
					+ "	?,\n"
					+ " ?);";
			
			ps = con.prepareStatement(st3);
			ps.setInt(1, personId);
			ps.setInt(2, emailId);
			ps.execute();
			
			commitAndClose(con);
			
		} catch (SQLException e) {
			throw new RuntimeException("SQL Exception",e);
		}
		
	}

	/**
	 * Returns the storeId of the store, or -1 if it is not in the database
	 * @param con
	 * @param storeCode
	 * @return
	 */
	public static int containsStore(Connection con, String storeCode) {
		int storeId = -1;
		
		try {
			con.commit(); //bug fix?
			PreparedStatement ps;
			
			String st = "select count(s.storeId) as count, s.storeId from Store s where\n"
					+ "	s.legacyId = ?;";
			
			ps = con.prepareStatement(st);
			ps.setString(1, storeCode);
			ps.execute();
			
			ResultSet rs = ps.getResultSet();
			rs.next();
			int count = rs.getInt("count");
			if (count > 0)
				return rs.getInt("storeId"); //Store already present
			
		} catch (SQLException e) {
			throw new RuntimeException("SQL Exception",e);
		}
		
		return storeId;
	}
	
	/**
	 * Adds a store record to the database managed by the person identified by the
	 * given code.
	 * 
	 * @param storeCode
	 * @param managerCode
	 * @param street
	 * @param city
	 * @param state
	 * @param zip
	 * @param country
	 */
	public static void addStore(String storeCode, String managerCode, String street, String city, String state,
			String zip, String country) {
		
		//Check if the store is already present in Store table
		//Check if the manager Person and Address are in the table
		//Add the Address if necessary (Make a helper method for this)
		//Add the store
		
		Connection con = obtainConnection();
		try {
			int storeId = containsStore(con, storeCode);
			
			if (storeId != -1)
				return; //Store already in database
			
			int managerId = containsPerson(con, managerCode);
			
			if (managerId == -1)
				return; //Person manager not in database
			
			int addressId = containsAddress(con, street, city, state, zip, country);
			
			if (addressId == -1) 
				addAddress(street,city,state,zip,country);
				
			addressId = containsAddress(con, street, city, state, zip, country);
			
			//Add store
			
			String st = "insert into Store (legacyId, managerId, addressId) values (\n"
					+ "	?,\n"
					+ " ?,\n"
					+ " ?);";
			
			PreparedStatement ps = con.prepareStatement(st);
			ps.setString(1, storeCode);
			ps.setInt(2, managerId);
			ps.setInt(3, addressId);
			
			ps.execute();
			commitAndClose(con);
			
		} catch (SQLException e) {
			throw new RuntimeException("SQL Exception",e);
		}
	}

	/**
	 * Adds a sales item (product, service, subscription) record to the database
	 * with the given <code>name</code> and <code>basePrice</code>. The type of item
	 * is specified by the <code>type</code> which may be one of "PN", "PU", "PG",
	 * "SV", or "SB". These correspond to new products, used products, gift cards
	 * (for which <code>basePrice</code> will be <code>null</code>), services, and
	 * subscriptions.
	 * 
	 * @param itemCode
	 * @param type
	 * @param name
	 * @param basePrice
	 */
	public static void addItem(String itemCode, String type, String name, Double basePrice) {
		//Check if itemCode present in Item, Service, or Subscription
		//Add item (with null saleId) to database
		Connection con = obtainConnection();
		try {
			//TODO check if item present already
			
			PreparedStatement ps;
			
			if (type.equals("PN") || type.equals("PU") || type.equals("PG")) {
				String st = "insert into Item (productName, legacyId, newUsed, basePrice, quantity) values (\n"
						+ "    ?,\n"
						+ "    ?,\n"
						+ "    ?,\n"
						+ "    ?,\n"
						+ "    ?);";
				
				ps = con.prepareStatement(st);
				ps.setString(1, name);
				ps.setString(2, itemCode);
				
				if (type.equals("PN")) {
					ps.setString(3, "new");
					ps.setInt(4, 100*(int)(double)basePrice); //dumb
				} else if (type.equals("PU")) {
					ps.setString(3, "used");
					ps.setInt(4, 100*(int)(double)basePrice);
				} else { //PU
					ps.setString(3, "card");
					ps.setInt(4, 0);
				}
					
				ps.setInt(5, 0);
				ps.execute();
				
			} else if (type.equals("SV")) {
				String st = "insert into Service (productName, legacyId, baseRate, hours) values (\n"
						+ "	?,\n"
						+ " ?,\n"
						+ " ?,\n"
						+ " ?);";
				
				ps = con.prepareStatement(st);
				ps.setString(1, name);
				ps.setString(2, itemCode);
				ps.setInt(3, 100*(int)(double)basePrice);
				ps.setFloat(4, 0.0f);
				
				ps.execute();
			} else { //type.equals("SB")
				String st = "insert into Subscription (productName, legacyId, baseRate) values (\n"
						+ "	?,\n"
						+ " ?,\n"
						+ " ?);";
				
				ps = con.prepareStatement(st);
				ps.setString(1, name);
				ps.setString(2, itemCode);
				ps.setInt(3, 100*(int)(double)basePrice);
				
				ps.execute();
			}
			
			commitAndClose(con);
		} catch (SQLException e) {
			throw new RuntimeException("SQL Exception",e);
		}
	}

	/**
	 * Returns the saleId if it is present in the Sale table, otherwise returns -1
	 * @param con
	 * @param saleCode
	 * @return
	 */
	public static int containsSale(Connection con, String saleCode) {
		int saleId = -1;
		
		try {
			String st = "select count(s.saleId) as count, s.saleId from Sale s where\n"
					+ 	"s.legacyId = ?;";
					
			PreparedStatement ps = con.prepareStatement(st);
			ps.setString(1, saleCode);
			ps.execute();
			
			ResultSet rs = ps.getResultSet();
			rs.next();
			int count = rs.getInt("count");
			if (count > 0)
				return rs.getInt("saleId"); //Sale already present
			
		} catch (SQLException e) {
			throw new RuntimeException("SQL Exception",e);
		}
		
		return saleId;
	}
	
	/**
	 * Adds a sales record to the database with the given data.
	 * 
	 * @param saleCode
	 * @param storeCode
	 * @param customerCode
	 * @param salesPersonCode
	 */
	public static void addSale(String saleCode, String storeCode, String customerCode, String salesPersonCode) {
		//Check if saleCode present in Sale table
		//Check if storeCode present in Store table, customerCode and salesPersonCode present in Person
		//Add sale to Sale table.
		Connection con = obtainConnection();
		
		int saleId = containsSale(con, saleCode);
		if (saleId != -1)
			return; //Sale already present
		
		int storeId = containsStore(con, storeCode);
		if (storeId == -1)
			return; //Store not present
		
		int customerId = containsPerson(con, customerCode);
		if (customerId == -1)
			return; //customer Person not present
		
		int salespersonId = containsPerson(con, salesPersonCode);
		if (salespersonId == -1)
			return; //salesperson Person not present
		
		try {
			String st = "insert into Sale (legacyId, storeId, customerId, salespersonId) values (\n"
					+ "	?,\n"
					+ " ?,\n"
					+ " ?,\n"
					+ " ?);";
			
			PreparedStatement ps = con.prepareStatement(st);
			ps.setString(1, saleCode);
			ps.setInt(2, storeId);
			ps.setInt(3, customerId);
			ps.setInt(4, salespersonId);
			
			ps.execute();
			commitAndClose(con);
		} catch (SQLException e) {
			throw new RuntimeException("SQL Exception",e);
		}
	}

	/**
	 * Adds a particular product (new or used, identified by <code>itemCode</code>)
	 * to a particular sale record (identified by <code>saleCode</code>) with the
	 * specified quantity.
	 * 
	 * @param saleCode
	 * @param itemCode
	 * @param quantity
	 */
	public static void addProductToSale(String saleCode, String itemCode, int quantity) {
		//Check if sale present in Sale table and itemCode present in Item
		//Add product to sale
		Connection con = obtainConnection();
		
		int saleId = containsSale(con, saleCode);
		if (saleId == -1)
			return; //Sale not present
		
		try {
			//Look in Item table for itemCode
			String st1 = "";
			
			PreparedStatement ps = con.prepareStatement(st1);
			
			
			
			
			
			String st2 = "";
			ps = con.prepareStatement(st2);
			
		} catch (SQLException e) {
			throw new RuntimeException("SQL Exception",e);
		}
	}

	/**
	 * Adds a particular gift card (identified by <code>itemCode</code>) to a
	 * particular sale record (identified by <code>saleCode</code>) in the specified
	 * amount.
	 * 
	 * @param saleCode
	 * @param itemCode
	 * @param amount
	 */
	public static void addGiftCardToSale(String saleCode, String itemCode, double amount) {
		//Same as before
	}

	/**
	 * Adds a particular service (identified by <code>itemCode</code>) to a
	 * particular sale record (identified by <code>saleCode</code>) which
	 * will be performed by the given employee for the specified number of
	 * hours.
	 * 
	 * @param saleCode
	 * @param itemCode
	 * @param employeeCode
	 * @param billedHours
	 */
	public static void addServiceToSale(String saleCode, String itemCode, String employeeCode, double billedHours) {
		//Same as before
	}

	/**
	 * Adds a particular subscription (identified by <code>itemCode</code>) to a
	 * particular sale record (identified by <code>saleCode</code>) which
	 * is effective from the <code>startDate</code> to the <code>endDate</code>
	 * inclusive of both dates.
	 * 
	 * @param saleCode
	 * @param itemCode
	 * @param startDate
	 * @param endDate
	 */
	public static void addSubscriptionToSale(String saleCode, String itemCode, String startDate, String endDate) {
		//Same as before
	}


}
