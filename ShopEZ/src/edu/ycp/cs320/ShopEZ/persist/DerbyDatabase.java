package edu.ycp.cs320.ShopEZ.persist;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.ycp.cs320.ShopEZ.model.Account;
import edu.ycp.cs320.ShopEZ.model.Graph;
import edu.ycp.cs320.ShopEZ.model.GroceryList;
import edu.ycp.cs320.ShopEZ.model.Item;
import edu.ycp.cs320.ShopEZ.model.Location;
import edu.ycp.cs320.ShopEZ.model.Node;
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

	private void loadLocation(Location location, ResultSet resultSet, int index) throws SQLException {
		location.setX(resultSet.getInt(index++));
		location.setY(resultSet.getInt(index++));
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
		while (resultSet.next()) {
			groceryList.setAccountID(resultSet.getInt(index++));
			groceryList.getList().add(resultSet.getInt(index++));
		}
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
						for(int i=0; i < list.getList().size(); i++) {
							insertGroceryList.setInt(1, list.getAccountID());
							insertGroceryList.setInt(2, list.getList().get(i));
							insertGroceryList.addBatch();
						}
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
					return result;
				} finally {
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}

	public Item findItemByItemID(final int itemID) throws SQLException{
		return executeTransaction(new Transaction<Item>() {

			public Item execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				Item result = new Item();

				try {

					stmt = conn.prepareStatement(
							"select items.item_id, items.item_name, items.item_price, items.item_location_x, items.item_location_y " +
									"	from items " +
									"	where items.item_id =  ? "
							);
					stmt.setInt(1, itemID);

					resultSet = stmt.executeQuery();
					while(resultSet.next()) {
						result.setItemID(resultSet.getInt(1));
						result.setItemName(resultSet.getString(2));
						result.setItemPrice(resultSet.getDouble(3));
						result.setItemLocationX(resultSet.getInt(4));
						result.setItemLocationY(resultSet.getInt(5));
					}
					return result;
				} finally {
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
				}
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
							"INSERT into items(item_name, item_price, item_location_x, item_location_y) "
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

	public HashMap<Item, Location> findXYMapForItemsInList(List<Item> items) throws SQLException{
		return executeTransaction(new Transaction<HashMap<Item, Location>>() {

			public HashMap<Item, Location> execute(Connection conn) throws SQLException {

				HashMap<Item, Location> finalResult = new HashMap<Item, Location>();
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				try {
					conn.setAutoCommit(true);

					int i = 0;

					while(items.get(i) != null) {
						// a canned query to find book information (including author name) from title
						stmt = conn.prepareStatement(
								"select items.item_location_x, items.item_location_y"+
										"	from items"+
										"		where items.item_name = ?"
								);

						// execute the query
						resultSet = stmt.executeQuery();

						while (resultSet.next()) {
							Location location = new Location();
							loadLocation(location, resultSet, 1);
							finalResult.put(items.get(i), location);

							System.out.println("Loaded location in the hashmap");
						}
					}
					return finalResult;

				} finally {
					// close result set, statement, connection
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
				}
			}});
	}

	public List<Item> loadGraphNodesFromGroceryListItems(final int account_id) throws SQLException{
		Graph graphNodes = new Graph();
		List<Item> items = new ArrayList<Item>();
		try {
			items = findAllItemsForAccount(account_id);
		} catch (SQLException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}

		// Node 'A' represent the starting node; the coordinate (132, 18) on the mocked up map
		String start = "start";
		Node A = new Node();
		Node B = new Node();
		Node C = new Node();
		Node D = new Node();
		Node E = new Node();
		Node F = new Node();
		Node G = new Node();
		Node H = new Node();
		Node I = new Node();
		Node J = new Node();
		Node K = new Node();
		Node L = new Node();
		Node M = new Node();
		Node N = new Node();
		Node O = new Node();
		Node P = new Node();
		Node Q = new Node();
		Node R = new Node();
		Node S = new Node();
		Node T = new Node();
		Node U = new Node();

		A.setName(start);
		A.addDestination(B, 18);
		B.addDestination(A, 18);
		B.addDestination(C, 24);
		B.addDestination(K, 24);
		C.addDestination(D, 24);
		C.addDestination(B, 24);
		D.addDestination(E, 24);
		D.addDestination(C, 24);
		E.addDestination(F, 24);
		E.addDestination(D, 24);
		F.addDestination(G, 24);
		F.addDestination(E, 24);
		G.addDestination(P, 24);
		G.addDestination(H, 24);
		G.addDestination(F, 24);
		H.addDestination(G, 24);
		H.addDestination(I, 24);
		I.addDestination(H, 24);
		I.addDestination(J, 24);
		J.addDestination(I, 24);
		J.addDestination(K, 24);
		K.addDestination(L, 24);
		K.addDestination(J, 24);
		K.addDestination(B, 24);
		L.addDestination(U, 24);
		L.addDestination(M, 24);
		L.addDestination(K, 24);
		M.addDestination(N, 24);
		M.addDestination(L, 24);
		N.addDestination(O, 24);
		N.addDestination(M, 24);
		O.addDestination(P, 24);
		O.addDestination(N, 24);
		P.addDestination(Q, 24);
		P.addDestination(O, 24);
		P.addDestination(G, 24);
		Q.addDestination(R, 24);
		Q.addDestination(P, 24);
		R.addDestination(Q, 24);
		R.addDestination(S, 24);
		S.addDestination(R, 24);
		S.addDestination(T, 24);
		T.addDestination(S, 24);
		T.addDestination(U, 24);
		U.addDestination(T, 24);
		U.addDestination(L, 24);

		// this for each loop with go through all the items for the current user's grocery list
		for (Item item : items) {
			if ((item.getItemLocationX() == 12 && item.getItemLocationY() >= 78 && item.getItemLocationY() <= 96) || (item.getItemLocationX() >= 12 && item.getItemLocationX() <= 30 && item.getItemLocationY() == 96) || (item.getItemLocationX() >= 24 && item.getItemLocationX() <= 30 && item.getItemLocationY() == 84)) {
				item.setItemLocationX(18);
				item.setItemLocationY(90);
				Q.setName(item.getItemName());
				Q.addToItemIdsList(item.getItemID());
				graphNodes.addNode(Q);
			}
			else if((item.getItemLocationX() == 12 && item.getItemLocationY() < 78 && item.getItemLocationY() > 54) || (item.getItemLocationY() == 72 && item.getItemLocationX() >= 24 && item.getItemLocationX() < 30) || (item.getItemLocationY() == 60 && item.getItemLocationX() >= 24 && item.getItemLocationX() <= 30)) {
				item.setItemLocationX(18);
				item.setItemLocationY(66);
				P.setName(item.getItemName());
				P.addToItemIdsList(item.getItemID());
				graphNodes.addNode(P);
			}
			else if((item.getItemLocationX() == 12 && item.getItemLocationY() <= 54 && item.getItemLocationY() > 30) || (item.getItemLocationY() == 48 && item.getItemLocationX() >= 24 && item.getItemLocationX() < 30) || (item.getItemLocationY() == 36 && item.getItemLocationX() >= 24 && item.getItemLocationX() <= 30)) {
				item.setItemLocationX(18);
				item.setItemLocationY(42);
				G.setName(item.getItemName());
				G.addToItemIdsList(item.getItemID());
				graphNodes.addNode(G);
			}
			else if((item.getItemLocationX() == 12 && item.getItemLocationY() <= 30 && item.getItemLocationY() >= 12) || (item.getItemLocationY() == 12 && item.getItemLocationX() >= 12 && item.getItemLocationX() <= 30) || (item.getItemLocationY() == 24 && item.getItemLocationX() >= 24 && item.getItemLocationX() <= 30)) {
				item.setItemLocationX(18);
				item.setItemLocationY(18);
				F.setName(item.getItemName());
				F.addToItemIdsList(item.getItemID());
				graphNodes.addNode(F);
			}

			else if((item.getItemLocationY() == 96 && item.getItemLocationX() > 30 && item.getItemLocationX() <= 54) || (item.getItemLocationY() == 84 && item.getItemLocationX() > 30 && item.getItemLocationX() <= 54)) {
				item.setItemLocationX(42);
				item.setItemLocationY(90);
				R.setName(item.getItemName());
				R.addToItemIdsList(item.getItemID());
				graphNodes.addNode(R);
			}
			else if((item.getItemLocationY() == 96 && item.getItemLocationX() > 54 && item.getItemLocationX() <= 78) || (item.getItemLocationY() == 84 && item.getItemLocationX() > 54 && item.getItemLocationX() <= 78)) {
				item.setItemLocationX(66);
				item.setItemLocationY(90);
				S.setName(item.getItemName());
				S.addToItemIdsList(item.getItemID());
				graphNodes.addNode(S);
			}
			else if((item.getItemLocationY() == 96 && item.getItemLocationX() > 78 && item.getItemLocationX() <= 102) || (item.getItemLocationY() == 84 && item.getItemLocationX() > 78 && item.getItemLocationX() <= 102)) {
				item.setItemLocationX(90);
				item.setItemLocationY(90);
				T.setName(item.getItemName());
				T.addToItemIdsList(item.getItemID());
				graphNodes.addNode(T);
			}
			else if((item.getItemLocationY() == 96 && item.getItemLocationX() > 102 && item.getItemLocationX() <= 144) || (item.getItemLocationY() == 84 && item.getItemLocationX() > 102 && item.getItemLocationX() <= 108)) {
				item.setItemLocationX(114);
				item.setItemLocationY(90);
				U.setName(item.getItemName());
				U.addToItemIdsList(item.getItemID());
				graphNodes.addNode(U);
			}

			else if((item.getItemLocationY() == 72 && item.getItemLocationX() > 30 && item.getItemLocationX() <= 54) || (item.getItemLocationY() == 60 && item.getItemLocationX() > 30 && item.getItemLocationX() <= 54)) {
				item.setItemLocationX(42);
				item.setItemLocationY(66);
				O.setName(item.getItemName());
				O.addToItemIdsList(item.getItemID());
				graphNodes.addNode(O);
			}
			else if((item.getItemLocationY() == 72 && item.getItemLocationX() > 54 && item.getItemLocationX() <= 78) || (item.getItemLocationY() == 60 && item.getItemLocationX() > 54 && item.getItemLocationX() <= 78)) {
				item.setItemLocationX(66);
				item.setItemLocationY(66);
				N.setName(item.getItemName());
				N.addToItemIdsList(item.getItemID());
				graphNodes.addNode(N);
			}
			else if((item.getItemLocationY() == 72 && item.getItemLocationX() > 78 && item.getItemLocationX() <= 102) || (item.getItemLocationY() == 60 && item.getItemLocationX() > 78 && item.getItemLocationX() <= 102)) {
				item.setItemLocationX(90);
				item.setItemLocationY(66);
				M.setName(item.getItemName());
				M.addToItemIdsList(item.getItemID());
				graphNodes.addNode(M);
			}
			else if((item.getItemLocationY() == 72 && item.getItemLocationX() > 102 && item.getItemLocationX() <= 108) || (item.getItemLocationY() == 60 && item.getItemLocationX() > 102 && item.getItemLocationX() <= 108)) {
				item.setItemLocationX(114);
				item.setItemLocationY(66);
				L.setName(item.getItemName());
				L.addToItemIdsList(item.getItemID());
				graphNodes.addNode(L);
			}

			else if((item.getItemLocationY() == 48 && item.getItemLocationX() > 30 && item.getItemLocationX() <= 54) || (item.getItemLocationY() == 36 && item.getItemLocationX() > 30 && item.getItemLocationX() <= 54)) {
				item.setItemLocationX(42);
				item.setItemLocationY(42);
				H.setName(item.getItemName());
				H.addToItemIdsList(item.getItemID());
				graphNodes.addNode(H);
			}
			else if((item.getItemLocationY() == 48 && item.getItemLocationX() > 54 && item.getItemLocationX() <= 78) || (item.getItemLocationY() == 36 && item.getItemLocationX() > 54 && item.getItemLocationX() <= 78)) {
				item.setItemLocationX(66);
				item.setItemLocationY(42);
				I.setName(item.getItemName());
				I.addToItemIdsList(item.getItemID());
				graphNodes.addNode(I);
			}
			else if((item.getItemLocationY() == 48 && item.getItemLocationX() > 78 && item.getItemLocationX() <= 102) || (item.getItemLocationY() == 36 && item.getItemLocationX() > 78 && item.getItemLocationX() <= 102)) {
				item.setItemLocationX(90);
				item.setItemLocationY(42);
				J.setName(item.getItemName());
				J.addToItemIdsList(item.getItemID());
				graphNodes.addNode(J);
			}
			else if((item.getItemLocationY() == 48 && item.getItemLocationX() > 102 && item.getItemLocationX() <= 108) || (item.getItemLocationY() == 36 && item.getItemLocationX() > 102 && item.getItemLocationX() <= 108)) {
				item.setItemLocationX(114);
				item.setItemLocationY(42);
				K.setName(item.getItemName());
				K.addToItemIdsList(item.getItemID());
				graphNodes.addNode(K);
			}

			else if((item.getItemLocationY() == 24 && item.getItemLocationX() > 30 && item.getItemLocationX() <= 54) || (item.getItemLocationY() == 12 && item.getItemLocationX() > 30 && item.getItemLocationX() <= 54)) {
				item.setItemLocationX(42);
				item.setItemLocationY(18);
				E.setName(item.getItemName());
				E.addToItemIdsList(item.getItemID());
				graphNodes.addNode(E);
			}
			else if((item.getItemLocationY() == 24 && item.getItemLocationX() > 54 && item.getItemLocationX() <= 78) || (item.getItemLocationY() == 12 && item.getItemLocationX() > 54 && item.getItemLocationX() <= 78)) {
				item.setItemLocationX(66);
				item.setItemLocationY(18);
				D.setName(item.getItemName());
				D.addToItemIdsList(item.getItemID());
				graphNodes.addNode(D);
			}
			else if((item.getItemLocationY() == 24 && item.getItemLocationX() > 78 && item.getItemLocationX() <= 102) || (item.getItemLocationY() == 12 && item.getItemLocationX() > 78 && item.getItemLocationX() <= 102)) {
				item.setItemLocationX(90);
				item.setItemLocationY(18);
				C.setName(item.getItemName());
				C.addToItemIdsList(item.getItemID());
				graphNodes.addNode(C);
			}
			else if((item.getItemLocationY() == 24 && item.getItemLocationX() > 102 && item.getItemLocationX() <= 108) || (item.getItemLocationY() == 12 && item.getItemLocationX() > 102 && item.getItemLocationX() <= 108)) {
				item.setItemLocationX(114);
				item.setItemLocationY(18);
				B.setName(item.getItemName());
				B.addToItemIdsList(item.getItemID());
				graphNodes.addNode(B);
			}

		}
		List<Item> result = new ArrayList<Item>();

		graphNodes = calculateShortestPathFromSource(graphNodes, A);

		for (Node n : graphNodes.getGraphNodes()) {
			for (int i = 0; i < n.getItemIds().size(); i++) {
				result.add(findItemByItemID(n.getItemIds().get(i)));
			}
		}
		return result;
	}

	public static Graph calculateShortestPathFromSource(Graph graph, Node source) {
		source.setDistance(0);

		Set<Node> settledNodes = new HashSet<>();
		Set<Node> unsettledNodes = new HashSet<>();

		unsettledNodes.add(source);

		while (unsettledNodes.size() != 0) {
			Node currentNode = getLowestDistanceNode(unsettledNodes);
			unsettledNodes.remove(currentNode);
			for (java.util.Map.Entry<Node, Integer> adjacencyPair: currentNode.getAdjacentNodes().entrySet()) {
				Node adjacentNode = adjacencyPair.getKey();
				Integer edgeWeight = adjacencyPair.getValue();
				if (!settledNodes.contains(adjacentNode)) {
					calculateMinimumDistance(adjacentNode, edgeWeight, currentNode);
					unsettledNodes.add(adjacentNode);
				}
			}
			settledNodes.add(currentNode);
		}
		return graph;
	}

	private static void calculateMinimumDistance(Node evaluationNode, Integer edgeWeigh, Node sourceNode) {
		Integer sourceDistance = sourceNode.getDistance();
		if (sourceDistance + edgeWeigh < evaluationNode.getDistance()) {
			evaluationNode.setDistance(sourceDistance + edgeWeigh);
			LinkedList<Node> shortestPath = sourceNode.getShortestPath();
			shortestPath.add(sourceNode);
			evaluationNode.setShortestPath(shortestPath);
		}
	}

	private static Node getLowestDistanceNode(Set<Node> unsettledNodes) {
		Node lowestDistanceNode = null;
		int lowestDistance = Integer.MAX_VALUE;
		for (Node node: unsettledNodes) {
			int nodeDistance = node.getDistance();
			if (nodeDistance < lowestDistance) {
				lowestDistance = nodeDistance;
				lowestDistanceNode = node;
			}
		}
		return lowestDistanceNode;
	}

	public Boolean insertItemIntoGroceryListTable(final int id, final Item item, final int qty) throws SQLException {
		return executeTransaction(new Transaction<Boolean>() {

			public Boolean execute(Connection conn) throws SQLException {
				Boolean finalResult = false;
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				try {
					int item_id = item.getItemID();
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

					if (findItemsInGroceryListTable(id, item, qty) == true) {
						finalResult = true;
					}

					return finalResult;
				} finally {
					// close result set, statement, connection
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
				}
			}});
	}

	public Boolean findItemsInGroceryListTable(final int id, final Item name, final int qty) throws SQLException{
		return executeTransaction(new Transaction<Boolean>() {
			public Boolean execute(Connection conn) throws SQLException {
				Boolean finalResult = false;
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				try {
					System.out.print("passed11");
					int item_id = name.getItemID();
					conn.setAutoCommit(true);
					stmt = conn.prepareStatement(
							"select * from groceryLists" +
									"	where groceryLists.account_id = ?" +
									"		and groceryLists.item_id = ?"
							);
					stmt.setInt(1, id);
					stmt.setInt(2, item_id);
					System.out.print("passed12");
					System.out.print(id+" "+item_id);
					// execute the query
					resultSet = stmt.executeQuery();
					int iter = 0;
					while (resultSet.next()) {
						iter++;
					}
					if((iter/2) == qty) {
						finalResult = true;
					}

					return finalResult;
				} finally {
					// close result set, statement, connection
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
				}
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
									"	where groceryLists.account_id = ? "
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
									"	where groceryLists.account_id = ? " +
									"		and groceryLists.item_id = ? "
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

	public List<Account> findAllAccounts() throws SQLException {
		return executeTransaction(new Transaction<List<Account>>() {

			public List<Account> execute(Connection conn) throws SQLException {

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

	public List<Item> findAllItemsForAccount(final int id) throws SQLException {
		return executeTransaction(new Transaction<List<Item>>() {

			public List<Item> execute(Connection conn) throws SQLException {

				List<Item> finalResult = new ArrayList<Item>();
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				try {
					conn.setAutoCommit(true);

					// a canned query to find book information (including author name) from title
					stmt = conn.prepareStatement(
							"select items.item_id, items.item_name, items.item_price, items.item_location_x, items.item_location_y" +
									"	from groceryLists, items" +
									"	where groceryLists.item_id = items.item_id" +
									"		and groceryLists.account_id = ?)"
							);

					stmt.setInt(1, id);
					// execute the query
					resultSet = stmt.executeQuery();

					while (resultSet.next()) {
						Item item = new Item();
						loadItem(item, resultSet, 1);
						finalResult.add(item);
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


