package edu.ycp.cs320.ShopEZ.persist;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.ycp.cs320.ShopEZ.model.Account;
import edu.ycp.cs320.ShopEZ.model.GroceryList;
import edu.ycp.cs320.ShopEZ.model.History;
import edu.ycp.cs320.ShopEZ.model.Item;
import edu.ycp.cs320.sqldemo.DBUtil;

public abstract class DerbyDatabase implements IDatabase {
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

	@Override
	public void dropTables() throws SQLException{
		doExecuteTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement dropAccounts = null;
				PreparedStatement dropGroceryLists = null;
				PreparedStatement dropHistory = null;
				PreparedStatement dropItems = null;
				try {
					dropAccounts = conn.prepareStatement
							("drop table accounts" );
					dropAccounts.execute();
					dropAccounts.close();
					dropGroceryLists = conn.prepareStatement
							("drop table groceryLists" );
					dropGroceryLists.execute();
					dropGroceryLists.close();
					dropHistory = conn.prepareStatement
							("drop table history" );
					dropHistory.execute();
					dropHistory.close();
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
					DBUtil.closeQuietly(dropHistory);
					DBUtil.closeQuietly(dropItems);
				}
			}
		});
	}

	@Override
	public Boolean updateItemPriceByItemNameAndPrice(final String name, final double price) throws SQLException{
		return executeTransaction(new Transaction<Boolean>() {
			@SuppressWarnings("finally")
			@Override
			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				Boolean result = false;

				try {

					stmt = conn.prepareStatement(
							"UPDATE items " +
									"set items.item_price = '?' " +
									"where items.item_name =  ? "
							);
					stmt.setDouble(1, price);
					stmt.setString(2, name);

					stmt.executeUpdate();

					result = true;
				} finally {
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
					return result;
				}
			}
		});
	}

	@Override
	public Boolean updateItemLocationByItemNameAndXYCoords(final String item, final int x, final int y) throws SQLException{
		return executeTransaction(new Transaction<Boolean>() {
			@SuppressWarnings("finally")
			@Override
			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				Boolean result = false;

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

					result = true;
				} finally {
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
					return result;
				}
			}
		});
	}

	@Override
	public double findItemPriceByItemName(final String name) {
		return executeTransaction(new Transaction<Double>() {
			@Override
			public Double execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				ResultSet resultSet = null;

				try {

					stmt = conn.prepareStatement(
							"select items.item_price " +
									"  from items " +
									" where items.item_name = ? "
							);
					stmt.setString(1, name);

					double result = 0.0;

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

					return result;
				} finally {
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}


	public String insertItemIntoItemsTable(final String name, final double price, final int x, final int y) throws SQLException {
		return executeTransaction(new Transaction<String>() {
			@Override
			public String execute(Connection conn) throws SQLException {
				// TODO Auto-generated method stub
				String finalResult = "incomplete";
				// load Derby JDBC driver
				try {
					Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
				} catch (Exception e) {
					System.err.println("Could not load Derby JDBC driver");
					System.err.println(e.getMessage());
					System.exit(1);
				}
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

					finalResult = "Complete";

				} finally {
					// close result set, statement, connection
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
					DBUtil.closeQuietly(conn);
				}
				return finalResult;
			}});
	}

	
	public int findAccountIDbyUsernameAndPassword(final String username, final String password) throws SQLException {
		return executeTransaction(new Transaction<Integer>() {
			@Override
			public Integer execute(Connection conn) throws SQLException {
				// TODO Auto-generated method stub
				int finalResult = -1;
				// load Derby JDBC driver
				try {
					Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
				} catch (Exception e) {
					System.err.println("Could not load Derby JDBC driver");
					System.err.println(e.getMessage());
					System.exit(1);
				}
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

					return finalResult;

				} finally {
					// close result set, statement, connection
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
					DBUtil.closeQuietly(conn);
				}
			}});
	}

	// ---------------------------------- Utility Functions ---------------------------------- //

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
		account.setHistoryListID(resultSet.getInt(index++));
	}


	private void loadItem(Item item, ResultSet resultSet, int index) throws SQLException {
		item.setItemID(resultSet.getInt(index++));
		item.setItemName(resultSet.getString(index++));
		item.setItemPrice(resultSet.getDouble(index++));
		item.setItemLocationX(resultSet.getInt(index++));
		item.setItemLocationY(resultSet.getInt(index++));
	}

	private void loadHistory(History history, ResultSet resultSet, int index) throws SQLException{
		history.setAccountID(resultSet.getInt(index++));
		history.setHistoryID(resultSet.getInt(index++));
		history.setGroceryListID(resultSet.getInt(index++));
	}

	private void loadGroceryList(GroceryList groceryList, ResultSet resultSet, int index) throws SQLException{
		groceryList.setGroceryListID(resultSet.getInt(index++));
		groceryList.setAccountID(resultSet.getInt(index++));
		groceryList.setHistoryID(resultSet.getInt(index++));
		groceryList.setListName(resultSet.getString(index++));
		groceryList.setListPrice(resultSet.getDouble(index++));
	}



	public void createTables() {
		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement stmt1 = null;
				PreparedStatement stmt2 = null;
				PreparedStatement stmt3 = null;
				PreparedStatement stmt4 = null;

				try {
					stmt1 = conn.prepareStatement(
							"create table accounts (" +
									"	account_id integer primary key " +
									"		generated always as identity (start with 1, increment by 1), " +									
									"	account_username varchar(40)," +
									"	account_password varchar(40)," +
									"	account_history_id integer" +
									")"
							);	
					stmt1.executeUpdate();

					stmt2 = conn.prepareStatement(
							"create table items (" +
									"	item_id integer primary key " +
									"		generated always as identity (start with 1, increment by 1), " +
									"	item_name varchar(70), " +
									"	item_price varchar(70)," +
									"	item_location_x integer," +
									"	item_location_y integer" +
									")"
							);
					stmt2.executeUpdate();

					stmt3 = conn.prepareStatement(
							"create table history (" +
									"	history_id integer primary key " +
									"		generated always as identity (start with 1, increment by 1), " +
									"	account_id integer, " +
									"	grocery_list_id integer " +
									")"
							);
					stmt3.executeUpdate();

					stmt4 = conn.prepareStatement(
							"create table groceryLists (" +
									"	groceryList_id integer primary key " +
									"		generated always as identity (start with 1, increment by 1), " +
									"	account_id integer, " +
									"	history_id integer, " +
									"	list_name varchar(70) " +
									")"
							);
					stmt4.executeUpdate();

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
			@Override
			public Boolean execute(Connection conn) throws SQLException {
				List<Account> accountList;
				List<Item> itemList;
				List<History> historyList;
				List<GroceryList> groceryList;

				try {
					accountList = InitialData.getAccounts();
					itemList = InitialData.getItems();
					historyList = InitialData.getHistory();
					groceryList = InitialData.getGroceryLists();
				} catch (IOException e) {
					throw new SQLException("Couldn't read initial data", e);
				}

				PreparedStatement insertAccount = null;
				PreparedStatement insertItem   = null;
				PreparedStatement insertHistory = null;
				PreparedStatement insertGroceryList = null;

				try {
					// populate accounts table (do accounts first, since account_id is foreign key in history table)
					insertAccount = conn.prepareStatement("insert into accounts (account_username, account_password, history_id) values (?, ?, ?)");
					for (Account account : accountList) {
						//						insertAccount.setInt(1, author.getAuthorId());	// auto-generated primary key, don't insert this
						insertAccount.setString(1, account.getUsername());
						insertAccount.setString(2, account.getPassword());
						insertAccount.setInt(3, account.getHistoryListID());
						insertAccount.addBatch();
					}
					insertAccount.executeBatch();

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

					// populate history table (do this after items table)
					insertHistory = conn.prepareStatement("insert into history (account_id, grocery_list_id, ) values (?, ?, ?)");
					for (History history : historyList) {
						insertHistory.setInt(1, history.getHistoryID());
						insertHistory.setInt(2, history.getAccountID());
						insertHistory.setInt(3, history.getAccountID());
						insertHistory.addBatch();
					}
					insertHistory.executeBatch();

					// populate groceryList table (do this after items table)
					insertGroceryList = conn.prepareStatement("insert into groceryLists (account_id, history_id, name, price) values (?, ?, ?, ?)");
					for (GroceryList list : groceryList) {
						insertGroceryList.setInt(1, list.getAccountID());
						insertGroceryList.setInt(2, list.getHistoryID());
						insertGroceryList.setString(3,  list.getListName());
						insertGroceryList.setDouble(4,  list.getTotalPrice());
						insertGroceryList.addBatch();
					}
					insertGroceryList.executeBatch();


					return true;
				} finally {
					DBUtil.closeQuietly(insertItem);
					DBUtil.closeQuietly(insertAccount);
					DBUtil.closeQuietly(insertHistory);
					DBUtil.closeQuietly(insertGroceryList);
				}
			}
		});
	}

	// The main method creates the database tables and loads the initial data.
	@SuppressWarnings("null")
	public static void main(String[] args) throws IOException, SQLException {
		DerbyDatabase db = null;
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


