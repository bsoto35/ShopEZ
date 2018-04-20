package edu.ycp.cs320.ShopEZ.persist;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.ycp.cs320.ShopEZ.model.Account;
import edu.ycp.cs320.ShopEZ.model.GroceryList;
import edu.ycp.cs320.ShopEZ.model.Item;
import edu.ycp.cs320.sqldemo.DBUtil;

public class DerbyDatabase {
	static {
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
		} catch (Exception e) {
			throw new IllegalStateException("Could not load Derby driver");
		}
	}

	private interface Transaction<ResultType> {
		public ResultType execute(Connection conn) throws SQLException;
	}

	private static final int MAX_ATTEMPTS = 10;

	public<ResultType> ResultType executeTransaction(Transaction<ResultType> txn) {
		try {
			return doExecuteTransaction(txn);
		} catch (SQLException e) {
			throw new PersistenceException("Transaction failed", e);
		}
	}

	public<ResultType> ResultType doExecuteTransaction(Transaction<ResultType> txn) throws SQLException {
		Connection conn = connect();

		try {
			int numAttempts = 0;
			boolean success = false;
			ResultType result = null;

			while (!success && numAttempts < MAX_ATTEMPTS) {
				try {
					result = txn.execute(conn);
					conn.commit();
					success = true;
				} catch (SQLException e) {
					if (e.getSQLState() != null && e.getSQLState().equals("41000")) {
						// Deadlock: retry (unless max retry count has been reached)
						numAttempts++;
					} else {
						// Some other kind of SQLException
						throw e;
					}
				}
			}

			if (!success) {
				throw new SQLException("Transaction failed (too many retries)");
			}

			// Success!
			return result;
		} finally {
			DBUtil.closeQuietly(conn);
		}
	}

	private Connection connect() throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:derby:test.db;create=true");

		// Set autocommit to false to allow execution of
		// multiple queries/statements as part of the same transaction.
		conn.setAutoCommit(false);

		return conn;
	}

	private void loadAccount(Account account, ResultSet resultSet, int index) throws SQLException {
		account.setAccountID(resultSet.getInt(index++));
		account.setUsername(resultSet.getString(index++));
		account.setPassword(resultSet.getString(index++));
	}

	private void loadItem(Item item, ResultSet resultSet, int index) throws SQLException {
		item.setItemID(resultSet.getInt(index++));
		item.setItemName(resultSet.getString(index++));
		item.setItemPrice(resultSet.getDouble(index++));
		item.setItemLocationX(resultSet.getInt(index++));
		item.setItemLocationY(resultSet.getInt(index++));
	}

	@SuppressWarnings("unused")
	private void loadGroceryList(GroceryList groceryList, ResultSet resultSet, int index) throws SQLException{
		groceryList.setAccountID(resultSet.getInt(index++));
		groceryList.setItemID(resultSet.getInt(index++));
	}

	public void dropTables() throws SQLException{
		doExecuteTransaction(new Transaction<Boolean>() {

			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement dropAccounts = null;
				PreparedStatement dropGroceryLists = null;
				PreparedStatement dropItems = null;
				try {
					dropGroceryLists = conn.prepareStatement
							("drop table groceryLists" );
					dropGroceryLists.execute();
					dropGroceryLists.close();
					dropAccounts = conn.prepareStatement
							("drop table accounts" );
					dropAccounts.execute();
					dropAccounts.close();
					dropItems = conn.prepareStatement
							("drop table items" );
					dropItems.execute();
					dropItems.close();
					return true;
				}catch (Exception ex) {
					return true;
				}finally {
					DBUtil.closeQuietly(dropAccounts);
					DBUtil.closeQuietly(dropGroceryLists);
					DBUtil.closeQuietly(dropItems);
				}
			}
		});
	}

	public void createTables() {
		executeTransaction(new Transaction<Boolean>() {

			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement stmt1 = null;
				PreparedStatement stmt2 = null;
				PreparedStatement stmt3 = null;

				try {
					stmt1 = conn.prepareStatement(
							"create table accounts (" +
									"	account_id integer primary key " +
									"		generated always as identity (start with 1, increment by 1), " +									
									"	account_username varchar(40)," +
									"	account_password varchar(40)" +
									")"

							);	
					stmt1.executeUpdate();

					stmt2 = conn.prepareStatement(
							"create table items (" +
									"	item_id integer primary key " +
									"		generated always as identity (start with 1, increment by 1), " +
									"	item_name varchar(70), " +
									"	item_price double," +
									"	item_location_x integer," +
									"	item_location_y integer" +
									")"
							);
					stmt2.executeUpdate();

					stmt3 = conn.prepareStatement(
							"create table groceryLists (" +
									"	account_id   integer constraint account_id references accounts, " +
									"	item_id integer constraint item_id references items" +
									")"
							);
					stmt3.executeUpdate();


					return true;
				} finally {
					DBUtil.closeQuietly(stmt1);
					DBUtil.closeQuietly(stmt2);
					DBUtil.closeQuietly(stmt3);
				}
			}
		});
	}

	public void loadInitialData() {
		executeTransaction(new Transaction<Boolean>() {

			public Boolean execute(Connection conn) throws SQLException {
				List<Account> accountList;
				List<Item> itemList;
				List<GroceryList> groceryList;
				
				try {
					accountList = InitialData.getAccounts();
					itemList = InitialData.getItems();
					groceryList = InitialData.getGroceryLists();
				} catch (IOException e) {
					throw new SQLException("Couldn't read initial data", e);
				}

				PreparedStatement insertAccount = null;
				PreparedStatement insertItem   = null;
				PreparedStatement insertGroceryList = null;

				try {
					// populate accounts table (do accounts first, since account_id is foreign key in history table)
					insertAccount = conn.prepareStatement("insert into accounts (account_username, account_password) values (?, ?)");
					for (Account account : accountList) {
						insertAccount.setString(1, account.getUsername());
						insertAccount.setString(2, account.getPassword());
						insertAccount.addBatch();
					}
					insertAccount.executeBatch();

					System.out.println("Accounts table populated ");

					// populate items table (do this after accounts table)
					insertItem = conn.prepareStatement("insert into items (item_name, item_price, item_location_x, item_location_y) values (?, ?, ?, ?)");
					for (Item item : itemList) {
						insertItem.setString(1, item.getItemName());
						insertItem.setDouble(2, item.getItemPrice());
						insertItem.setInt(3, item.getItemLocationX());
						insertItem.setInt(4, item.getItemLocationY());
						insertItem.addBatch();
					}
					insertItem.executeBatch();

					System.out.println("Items table populated");

					// populate groceryList table (do this after items table)
					insertGroceryList = conn.prepareStatement("insert into groceryLists (account_id, item_id) values (?, ?)");
					for (GroceryList list : groceryList) {
						insertGroceryList.setInt(1, list.getAccountID());
						insertGroceryList.setInt(2, list.getItemID());
						insertGroceryList.addBatch();
					}
					insertGroceryList.executeBatch();

					System.out.println("Grocery List table populated");

					return true;
				} finally {
					DBUtil.closeQuietly(insertItem);
					DBUtil.closeQuietly(insertAccount);
					DBUtil.closeQuietly(insertGroceryList);
				}
			}
		});
	}

	public Item updateItemPriceByItemNameAndPrice(final String name, final double price) throws SQLException{
		return executeTransaction(new Transaction<Item>() {

			public Item execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				Item result = new Item();

				try {

					stmt = conn.prepareStatement(
							"UPDATE items " +
									"set items.item_price = '?' " +
									"where items.item_name =  ? "
							);
					stmt.setDouble(1, price);
					stmt.setString(2, name);

					stmt.executeUpdate();

					result = findItemByItemName(name);
				}catch (Exception ex) {
					return result;
				} finally {
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
				}
				return result;
			}
		});
	}

	public Item findItemByItemName(final String itemName) throws SQLException{
		return executeTransaction(new Transaction<Item>() {

			public Item execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				Item result = new Item();

				try {

					stmt = conn.prepareStatement(
							"Select items.item_id, items.item_name, items.item_price, items.item_location_x, items.item_location_y " +
									"from items " +
									"where items.item_name =  ? "
							);
					stmt.setString(1, itemName);

					resultSet = stmt.executeQuery();
					while(resultSet.next()) {
						result.setItemID(resultSet.getInt(1));
						result.setItemName(resultSet.getString(2));
						result.setItemPrice(resultSet.getDouble(3));
						result.setItemLocationX(resultSet.getInt(4));
						result.setItemLocationY(resultSet.getInt(5));
					}
				} finally {
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
				}
				return result;
			}
		});
	}

	public Item updateItemLocationByItemNameAndXYCoords(final String item, final int x, final int y) throws SQLException{
		return executeTransaction(new Transaction<Item>() {

			public Item execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				Item result = new Item();

				try {

					stmt = conn.prepareStatement(
							"UPDATE items " +
									" set items.item_location_x = '?', items.item_location_y = '?' " +
									" where items.item_name = ? "
							);
					stmt.setInt(1, x);
					stmt.setInt(2, y);
					stmt.setString(3, item);

					stmt.executeUpdate();

					result = findItemByItemName(item);

				} finally {
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
				}
				return result;
			}
		});
	}

	public double findItemPriceByItemName(final String name) {
		return executeTransaction(new Transaction<Double>() {

			public Double execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				double result = 0.0;
				try {

					stmt = conn.prepareStatement(
							"select items.item_price " +
									"  from items " +
									" where items.item_name = ? "
							);
					stmt.setString(1, name);

					resultSet = stmt.executeQuery();

					// for testing that a result was returned
					Boolean found = false;

					while (resultSet.next()) {
						found = true;

						// create new item object
						// retrieve attributes from resultSet starting at index
						Item item = new Item();
						loadItem(item, resultSet, item.getItemID());

						result = item.getItemPrice();
						System.out.println("Found <" + name + "> in the items table");
					}

					// check if the item was found
					if (!found) {
						System.out.println("<" + name + "> was not found in the items table");
					}

				} finally {
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
				}
				return result;
			}
		});
	}

	public Boolean verifyAccountFromAccountsTableByUsernameAndPassword(final String username, final String password) throws SQLException{
		return executeTransaction(new Transaction<Boolean>() {

			public Boolean execute(Connection conn) throws SQLException {
				Boolean found = false;
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				try {
					conn.setAutoCommit(true);

					// a canned query to find book information (including author name) from title
					stmt = conn.prepareStatement(
							"select accounts.account_id " +
									"from accounts " +
									"	   where accounts.account_username = ? " +
									"	   AND accounts.account_password = ? "	
							);

					// substitute the last name and first name of the existing author entered by the user for the placeholder in the query
					stmt.setString(1, username);
					stmt.setString(2, password);

					// execute the query
					resultSet = stmt.executeQuery();

					// for testing that a result was returned


					while (resultSet.next()) {
						found = true;

						System.out.println("Found account in the accounts table");
					}

					// check if the item was found
					if (!found) {
						System.out.println("Either <" + username + "or" + password +"> is not valid");
					}

				} finally {
					// close result set, statement, connection
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
				}
				return found;
			}});
	}

	public Boolean removeItemFromItemsTable(final Item itemName) throws SQLException {
		return executeTransaction(new Transaction<Boolean>() {
			public Boolean execute(Connection conn) throws SQLException {
				Boolean finalResult = false;
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				try {

					// a canned query to find book information (including author name) from title
					stmt = conn.prepareStatement(
							"DELETE " +
									"	from items " +
									"	where items.item_id = ? "
							);

					// substitute the last name and first name of the existing author entered by the user for the placeholder in the query
					stmt.setInt(1, itemName.getItemID());

					// execute the query
					stmt.executeQuery();

					finalResult = true;

				} finally {
					// close result set, statement, connection
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
				}
				return finalResult;
			}});
	}
	
	public Account findAccountByUsername(final String username) throws SQLException{
		return executeTransaction(new Transaction<Account>() {

			public Account execute(Connection conn) throws SQLException {
				Account finalResult = new Account();
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				try {
					conn.setAutoCommit(true);

					// a canned query to find book information (including author name) from title
					stmt = conn.prepareStatement(
							"select accounts.account_id, accounts.account_username, accounts.account_password " +
									"	from accounts " +
									"	   where accounts.account_username = ? "	
							);

					// substitute the last name and first name of the existing author entered by the user for the placeholder in the query
					stmt.setString(1, username);

					// execute the query
					resultSet = stmt.executeQuery();

					// for testing that a result was returned
					Boolean found = false;

					while (resultSet.next()) {
						found = true;

						loadAccount(finalResult, resultSet, 1);

						System.out.println("Found account in the accounts table");
					}

					// check if the item was found
					if (!found) {
						System.out.println("Either <" + username +"> is not valid");
					}

				} finally {
					// close result set, statement, connection
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
				}
				return finalResult;
			}});
	}
	
	public Boolean insertItemIntoItemsTable(final String name, final double price, final int x, final int y) throws SQLException {
		return executeTransaction(new Transaction<Boolean>() {

			public Boolean execute(Connection conn) throws SQLException {

				boolean finalResult = false;
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				try {
					conn.setAutoCommit(true);

					// a canned query to find book information (including author name) from title
					stmt = conn.prepareStatement(
							"insert into items(item_name, item_price, item_location_x, item_location_x) "
									+ "  values (?, ?, ?, ?, ?) "
							);

					// substitute the last name and first name of the existing author entered by the user for the placeholder in the query
					stmt.setString(1, name);
					stmt.setDouble(2, price);
					stmt.setInt(3, x);
					stmt.setInt(4, y);

					// execute the query
					stmt.executeUpdate();

					finalResult = true;

				} finally {
					// close result set, statement, connection
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
				}
				return finalResult;
			}});
	}

	public Boolean insertItemIntoGroceryListTable(final int id, final Item name, final int qty) throws SQLException {
		return executeTransaction(new Transaction<Boolean>() {
			public Boolean execute(Connection conn) throws SQLException {

				int item_id = name.getItemID();
				Boolean finalResult = false;
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				try {
					conn.setAutoCommit(true);
					int iter = 0;
					while (iter < qty) {
						
						stmt = conn.prepareStatement(
								"insert into groceryLists(account_id, item_id) "
										+ "  values (?, ?) "
								);
						
						stmt.setInt(1, id);
						stmt.setInt(2, item_id);

						// execute the query
						stmt.executeUpdate();
						iter++;
					}
					finalResult = true;

				} finally {
					// close result set, statement, connection
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
				}
				return finalResult;
			}});
	}

	public Boolean addAccountIntoAccountsTable(final String username, final String password) throws SQLException {
		return executeTransaction(new Transaction<Boolean>() {

			public Boolean execute(Connection conn) throws SQLException {

				Boolean finalResult = false;
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				try {
					conn.setAutoCommit(true);

					// a canned query to find book information (including author name) from title
					stmt = conn.prepareStatement(
							"insert into accounts(account_username, account_password) "
									+ "  values (?, ?) "
							);

					// substitute the last name and first name of the existing author entered by the user for the placeholder in the query
					stmt.setString(1, username);
					stmt.setString(2, password);

					// execute the query
					stmt.executeUpdate();

					if (verifyAccountFromAccountsTableByUsernameAndPassword(username, password) == true) {
						finalResult = true;
					}
				} finally {
					// close result set, statement, connection
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
				}
				return finalResult;
			}});
	}

	public int findAccountIDbyUsernameAndPassword(final String username, final String password) throws SQLException {
		return executeTransaction(new Transaction<Integer>() {

			public Integer execute(Connection conn) throws SQLException {

				int finalResult = -1;
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				try {
					conn.setAutoCommit(true);

					// a canned query to find book information (including author name) from title
					stmt = conn.prepareStatement(
							"select accounts.account_id " +
									"from accounts " +
									"	   where accounts.account_username = ? " +
									"	   AND accounts.account_password = ? "	
							);

					// substitute the last name and first name of the existing author entered by the user for the placeholder in the query
					stmt.setString(1, username);
					stmt.setString(2, password);

					// execute the query
					resultSet = stmt.executeQuery();

					// for testing that a result was returned
					Boolean found = false;

					while (resultSet.next()) {
						found = true;

						// create new item object
						// retrieve attributes from resultSet starting at index
						Account account = new Account();
						loadAccount(account, resultSet, account.getAccountID());

						finalResult = account.getAccountID();
						System.out.println("Found account in the accounts table");
					}

					// check if the item was found
					if (!found) {
						System.out.println("Either <" + username + "or" + password +"> is not valid");
					}

				} finally {
					// close result set, statement, connection
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
				}
				return finalResult;
			}});
	}

	public Boolean clearGroceryListForAccount(final int id) throws SQLException {
		return executeTransaction(new Transaction<Boolean>() {
			public Boolean execute(Connection conn) throws SQLException {
				Boolean finalResult = false;
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				try {

					// a canned query to find book information (including author name) from title
					stmt = conn.prepareStatement(
							"DELETE" +
									"	from groceryLists " +
									"	where gorceryLists.account_id = ? "
							);

					// substitute the last name and first name of the existing author entered by the user for the placeholder in the query
					stmt.setInt(1, id);

					// execute the query
					stmt.executeQuery();

					finalResult = true;

				} finally {
					// close result set, statement, connection
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
				}
				return finalResult;
			}});
	}

	public Boolean removeItemFromGroceryListTable(final int id, final Item itemName, final int qty) throws SQLException {
		return executeTransaction(new Transaction<Boolean>() {
			public Boolean execute(Connection conn) throws SQLException {
				Account account = findAccountByAccountID(id);
				Boolean finalResult = false;
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				int item_id = itemName.getItemID();
				try {

					// a canned query to find book information (including author name) from title
					stmt = conn.prepareStatement(
							"DELETE TOP(?)" +
									"	from groceryLists " +
									"	where gorceryLists.account_id = ? " +
									"		and gorceryLists.item_id = ? "
							);

					// substitute the last name and first name of the existing author entered by the user for the placeholder in the query
					stmt.setInt(1, qty);
					stmt.setInt(2, account.getAccountID());
					stmt.setInt(3, item_id);

					// execute the query
					stmt.executeQuery();

					finalResult = true;

				} finally {
					// close result set, statement, connection
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
				}
				return finalResult;
			}});
	}

	public Account findAccountByAccountID(final int id) throws SQLException {
		return executeTransaction(new Transaction<Account>() {

			public Account execute(Connection conn) throws SQLException {
				Account finalResult = new Account();
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				try {
					conn.setAutoCommit(true);

					// a canned query to find book information (including author name) from title
					stmt = conn.prepareStatement(
							"select accounts.account_id, accounts.account_username, accounts.account_password " +
									"	from accounts " +
									"	   where accounts.account_id = ? "	
							);

					// substitute the last name and first name of the existing author entered by the user for the placeholder in the query
					stmt.setInt(1, id);

					// execute the query
					resultSet = stmt.executeQuery();

					// for testing that a result was returned
					Boolean found = false;

					while (resultSet.next()) {
						found = true;

						loadAccount(finalResult, resultSet, 1);

						System.out.println("Found account in the accounts table");
					}

					// check if the item was found
					if (!found) {
						System.out.println("Either <" + id +"> is not valid");
					}

					return finalResult;

				} finally {
					// close result set, statement, connection
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
				}
			}});
	}

	public ArrayList<Account> findAllAccounts() throws SQLException {
		return executeTransaction(new Transaction<ArrayList<Account>>() {

			public ArrayList<Account> execute(Connection conn) throws SQLException {

				ArrayList<Account> finalResult = new ArrayList<Account>();
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				try {
					conn.setAutoCommit(true);

					// a canned query to find book information (including author name) from title
					stmt = conn.prepareStatement(
							"select * from accounts"
							);

					// execute the query
					resultSet = stmt.executeQuery();

					while (resultSet.next()) {
						Account account = new Account();
						loadAccount(account, resultSet, 1);
						finalResult.add(account);

						System.out.println("Found account in the accounts table");
					}

					return finalResult;

				} finally {
					// close result set, statement, connection
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
				}
			}});
	}

	public ArrayList<Item> findAllItemsForAccount(final int id) throws SQLException {
		return executeTransaction(new Transaction<ArrayList<Item>>() {

			public ArrayList<Item> execute(Connection conn) throws SQLException {

				ArrayList<Item> finalResult = new ArrayList<Item>();
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				try {
					conn.setAutoCommit(true);

					// a canned query to find book information (including author name) from title
					stmt = conn.prepareStatement(
							"select from groceryLists.account_id, groceryLists.item_id" +
									"	from groceryLists" +
									"	where groceryLists.account_id = ?"
							);

					stmt.setInt(1, id);
					// execute the query
					resultSet = stmt.executeQuery();

					while (resultSet.next()) {
						Item item = new Item();
						loadItem(item, resultSet, 1);
						finalResult.add(item);

						System.out.println("Found account in the accounts table");
					}

					return finalResult;

				} finally {
					// close result set, statement, connection
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
				}
			}});
	}

	// The main method creates the database tables and loads the initial data.
	public static void main(String[] args) throws IOException, SQLException {
		DerbyDatabase db = new DerbyDatabase();
		System.out.println("Dropping tables...");

		db.dropTables();

		System.out.println("COMPLETE");

		System.out.println("Creating tables...");

		db.createTables();

		System.out.println("COMPLETE");

		System.out.println("Loading initial data...");

		db.loadInitialData();

		System.out.println("Initial data loaded");

		System.out.println("MAIN COMPLETE");
	}
}


