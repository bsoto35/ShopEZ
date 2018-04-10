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
import edu.ycp.cs320.ShopEZ.model.Item;
import sqldemo.DBUtil;

public abstract class DerbyDatabase implements IDatabase {
	
	public DerbyDatabase() {
		
	}
	
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
	public double findItemPriceByItemName(final Item item) {
		return executeTransaction(new Transaction<Double>() {
			@Override
			public Double execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				ResultSet resultSet = null;

				try {
					
					stmt = conn.prepareStatement(
							"select items.item_price " +
									"  from items " +
									" where items.item_id = ? "
							);
					stmt.setInt(1, item.getItemID());

					double result = 0.0;

					resultSet = stmt.executeQuery();

					// for testing that a result was returned
					Boolean found = false;

					while (resultSet.next()) {
						found = true;

						// create new Author object
						// retrieve attributes from resultSet starting with index 1
						Account account = new Account();
						loadAccount(account, resultSet, 1);

						// create new item object
						// retrieve attributes from resultSet starting at index
						Item item = new Item();
						loadItem(item, resultSet, item.getItemID());

						result = item.getItemPrice();
					}

					// check if the title was found
					if (!found) {
						System.out.println("<" + item + "> was not found in the items table");
					}

					return result;
				} finally {
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}
	
	@SuppressWarnings("null")
	@Override
	public String insertItemIntoItemsTable(String name, double price, int x, int y, int quantity) throws SQLException {
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

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet resultSet = null;
		try {
			conn.setAutoCommit(true);

			// a canned query to find book information (including author name) from title
			stmt = conn.prepareStatement(
					"insert into items(item_name, item_price, item_locationX, item_locationY, item_quantity) "
							+ "  values (?, ?, ?, ?, ?) "
					);

			// substitute the last name and first name of the existing author entered by the user for the placeholder in the query
			stmt.setString(1, name);
			stmt.setDouble(2, price);
			stmt.setInt(3, x);
			stmt.setInt(4, y);
			stmt.setInt(5, quantity);

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
	}

	@Override
	public boolean retrieveAccountFromAccountsTable(int id) throws SQLException {
		// TODO Auto-generated method stub
		
		return false;
	}

	@Override
	public String updateItemQuantity(Item item, int quantity) throws SQLException {
		// TODO Auto-generated method stub
		
		return null;
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
	}

	
	private void loadItem(Item item, ResultSet resultSet, int index) throws SQLException {
		item.setItemID(resultSet.getInt(index++));
		item.setItemName(resultSet.getString(index++));
		item.setItemPrice(resultSet.getDouble(index++));
		item.setItemLocationX(resultSet.getInt(index++));
		item.setItemLocationY(resultSet.getInt(index++));
		item.setItemQuantity(resultSet.getInt(index++));
	}

	public void createTables() {
		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement stmt1 = null;
				PreparedStatement stmt2 = null;

				try {
					stmt1 = conn.prepareStatement(
							"create table accounts (" +
									"	account_id integer primary key " +
									"		generated always as identity (start with 1, increment by 1), " +									
									"	username varchar(40)," +
									"	password varchar(40)" +
									")"
							);	
					stmt1.executeUpdate();

					stmt2 = conn.prepareStatement(
							"create table items (" +
									"	item_id integer primary key " +
									"		generated always as identity (start with 1, increment by 1), " +
									"	item_name varchar(70), " +
									"	item_price varchar(70)," +
									"	item_locationX integer," +
									"	item_locationY integer," +
									"	item_quantity varchar(15)," +
									")"
							);
					stmt2.executeUpdate();

					return true;
				} finally {
					DBUtil.closeQuietly(stmt1);
					DBUtil.closeQuietly(stmt2);
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

				try {
					accountList = InitialData.getAccounts();
					itemList = InitialData.getItems();
				} catch (IOException e) {
					throw new SQLException("Couldn't read initial data", e);
				}

				PreparedStatement insertAccount = null;
				PreparedStatement insertItem   = null;

				try {
					// populate authors table (do authors first, since author_id is foreign key in books table)
					insertAccount = conn.prepareStatement("insert into accounts (account_username, account_password) values (?, ?)");
					for (Account account : accountList) {
						//						insertAccount.setInt(1, author.getAuthorId());	// auto-generated primary key, don't insert this
						insertAccount.setString(1, account.getUsername());
						insertAccount.setString(2, account.getPassword());
						insertAccount.addBatch();
					}
					insertAccount.executeBatch();

					// populate books table (do this after authors table,
					// since author_id must exist in authors table before inserting book)
					insertItem = conn.prepareStatement("insert into items (item_name, item_price, item_locationX, item_locationY, item_quantity) values (?, ?, ?, ?)");
					for (Item item : itemList) {
						insertItem.setString(1, item.getItemName());
						insertItem.setDouble(2, item.getItemPrice());
						insertItem.setInt(3, item.getItemLocationX());
						insertItem.setInt(4, item.getItemLocationY());
						insertItem.setInt(4, item.getItemQuantity());
						insertItem.addBatch();
					}
					insertItem.executeBatch();

					return true;
				} finally {
					DBUtil.closeQuietly(insertItem);
					DBUtil.closeQuietly(insertAccount);
				}
			}
		});
	}

	// The main method creates the database tables and loads the initial data.
	public static void main(String[] args) throws IOException {
		System.out.println("Creating tables...");
		DerbyDatabase db = new DerbyDatabase();
		db.createTables();

		System.out.println("Loading initial data...");
		db.loadInitialData();

		System.out.println("Success!");
	}

	
}
