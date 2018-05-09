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
import edu.ycp.cs320.ShopEZ.model.Route;
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

	private void loadGroceryList(GroceryList groceryList, ResultSet resultSet, int index) throws SQLException{
		groceryList.setAccountID(resultSet.getInt(index++));
		groceryList.getList().add(resultSet.getInt(index++));
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
							"CREATE table accounts (" +
									"	account_id integer primary key " +
									"		generated always as identity (start with 1, increment by 1), " +									
									"	account_username varchar(40)," +
									"	account_password varchar(40)" +
									")"

							);	
					stmt1.executeUpdate();

					stmt2 = conn.prepareStatement(
							"CREATE table items (" +
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
							"CREATE table groceryLists (" +
									"	account_id integer constraint account_id references accounts, " +
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
					insertAccount = conn.prepareStatement("INSERT into accounts (account_username, account_password) values (?, ?)");
					for (Account account : accountList) {
						insertAccount.setString(1, account.getUsername());
						insertAccount.setString(2, account.getPassword());
						insertAccount.addBatch();
					}
					insertAccount.executeBatch();

					System.out.println("Accounts table populated ");

					// populate items table (do this after accounts table)
					insertItem = conn.prepareStatement("INSERT into items (item_name, item_price, item_location_x, item_location_y) values (?, ?, ?, ?)");
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
					insertGroceryList = conn.prepareStatement("INSERT into groceryLists (account_id, item_id) values (?, ?)");
					for (GroceryList list : groceryList) {
						insertGroceryList.setInt(1, list.getAccountID());
						insertGroceryList.setInt(2, list.getList().get(0));
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
									"WHERE items.item_name =  ? "
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
							"SELECT TOP 1 * FROM items" + 
									"	WHERE items.item_name='?'"
							);
					stmt.setString(1, itemName);
					resultSet = stmt.executeQuery();
					int index = 0;
					while(resultSet.next()) {
						result.setItemID(resultSet.getInt(index++));
						result.setItemName(resultSet.getString(index++));
						result.setItemPrice(resultSet.getDouble(index++));
						result.setItemLocationX(resultSet.getInt(index++));
						result.setItemLocationY(resultSet.getInt(index++));
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
							"SELECT items.item_id, items.item_name, items.item_price, items.item_location_x, items.item_location_y " +
									"	FROM items " +
									"	WHERE items.item_id =  ? "
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
									" set item_location_x = ?, item_location_y = ? " +
									" WHERE item_name = ? "
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
							"SELECT items.item_price " +
									"  FROM items " +
									" WHERE items.item_name = ? "
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
							"SELECT accounts.account_id " +
									"	FROM accounts " +
									"	   WHERE accounts.account_username = ? " +
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
									"	FROM items " +
									"	WHERE items.item_id = ? "
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
							"SELECT accounts.account_id, accounts.account_username, accounts.account_password " +
									"	FROM accounts " +
									"	   WHERE accounts.account_username = ? "	
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
									+ "  values (?, ?, ?, ?) "
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
								"SELECT items.item_location_x, items.item_location_y"+
										"	FROM items"+
										"		WHERE items.item_name = ?"
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
			System.out.println("NO ITEMS FOUND");
			e.printStackTrace();
		}

		// Node 'A' represent the starting node; the coordinate (660, 90) on the map
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

		A.setOrder(1);
		B.setOrder(2);
		A.addDestination(B, 90);
		B.addDestination(A, 90);
		B.addDestination(C, 120);
		B.addDestination(K, 120);
		C.addDestination(D, 120);
		C.addDestination(B, 120);
		D.addDestination(E, 120);
		D.addDestination(C, 120);
		E.addDestination(F, 120);
		E.addDestination(D, 120);
		F.addDestination(G, 120);
		F.addDestination(E, 120);
		G.addDestination(P, 120);
		G.addDestination(H, 120);
		G.addDestination(F, 120);
		H.addDestination(G, 120);
		H.addDestination(I, 120);
		I.addDestination(H, 120);
		I.addDestination(J, 120);
		J.addDestination(I, 120);
		J.addDestination(K, 120);
		K.addDestination(L, 120);
		K.addDestination(J, 120);
		K.addDestination(B, 120);
		L.addDestination(U, 120);
		L.addDestination(M, 120);
		L.addDestination(K, 120);
		M.addDestination(N, 120);
		M.addDestination(L, 120);
		N.addDestination(O, 120);
		N.addDestination(M, 120);
		O.addDestination(P, 120);
		O.addDestination(N, 120);
		P.addDestination(Q, 120);
		P.addDestination(O, 120);
		P.addDestination(G, 120);
		Q.addDestination(R, 120);
		Q.addDestination(P, 120);
		R.addDestination(Q, 120);
		R.addDestination(S, 120);
		S.addDestination(R, 120);
		S.addDestination(T, 120);
		T.addDestination(S, 120);
		T.addDestination(U, 120);
		U.addDestination(T, 120);
		U.addDestination(L, 120);

		A.setCoordinates(660, 90);
		B.setCoordinates(570, 90);
		C.setCoordinates(450, 90);
		D.setCoordinates(330, 90);
		E.setCoordinates(210, 90);
		F.setCoordinates(90, 90);
		G.setCoordinates(90, 210);
		H.setCoordinates(210, 210);
		I.setCoordinates(330, 210);
		J.setCoordinates(450, 210);
		K.setCoordinates(570, 210);
		L.setCoordinates(570, 330);
		M.setCoordinates(450, 330);
		N.setCoordinates(330, 330);
		O.setCoordinates(210, 330);
		P.setCoordinates(90, 330);
		Q.setCoordinates(90, 450);
		R.setCoordinates(210, 450);
		S.setCoordinates(330, 450);
		T.setCoordinates(450, 450);
		U.setCoordinates(570, 90);

		graphNodes.addNode(A);
		graphNodes.addNode(B);
		graphNodes.addNode(C);
		graphNodes.addNode(D);
		graphNodes.addNode(E);
		graphNodes.addNode(F);
		graphNodes.addNode(G);
		graphNodes.addNode(H);
		graphNodes.addNode(I);
		graphNodes.addNode(J);
		graphNodes.addNode(K);
		graphNodes.addNode(L);
		graphNodes.addNode(M);
		graphNodes.addNode(N);
		graphNodes.addNode(O);
		graphNodes.addNode(P);
		graphNodes.addNode(Q);
		graphNodes.addNode(R);
		graphNodes.addNode(S);
		graphNodes.addNode(T);
		graphNodes.addNode(U);



		LinkedList<Node> groceryListNodes = new LinkedList<Node>();

		// this for each loop with go through all the items for the current user's grocery list
		for (Item item : items) {
			if ((item.getItemLocationX() == 12 && item.getItemLocationY() >= 78 && item.getItemLocationY() <= 96) || (item.getItemLocationX() >= 12 && item.getItemLocationX() <= 30 && item.getItemLocationY() == 96) || (item.getItemLocationX() >= 24 && item.getItemLocationX() <= 30 && item.getItemLocationY() == 84)) {
				item.setItemLocationX(90);
				item.setItemLocationY(450);
				Q.addToItemIdsList(item.getItemID());
				groceryListNodes.add(Q);
			}
			else if((item.getItemLocationX() == 12 && item.getItemLocationY() < 78 && item.getItemLocationY() > 54) || (item.getItemLocationY() == 72 && item.getItemLocationX() >= 24 && item.getItemLocationX() < 30) || (item.getItemLocationY() == 60 && item.getItemLocationX() >= 24 && item.getItemLocationX() <= 30)) {
				item.setItemLocationX(90);
				item.setItemLocationY(330);
				P.addToItemIdsList(item.getItemID());
				groceryListNodes.add(P);
			}
			else if((item.getItemLocationX() == 12 && item.getItemLocationY() <= 54 && item.getItemLocationY() > 30) || (item.getItemLocationY() == 48 && item.getItemLocationX() >= 24 && item.getItemLocationX() < 30) || (item.getItemLocationY() == 36 && item.getItemLocationX() >= 24 && item.getItemLocationX() <= 30)) {
				item.setItemLocationX(90);
				item.setItemLocationY(210);
				G.addToItemIdsList(item.getItemID());
				groceryListNodes.add(G);
			}
			else if((item.getItemLocationX() == 12 && item.getItemLocationY() <= 30 && item.getItemLocationY() >= 12) || (item.getItemLocationY() == 12 && item.getItemLocationX() >= 12 && item.getItemLocationX() <= 30) || (item.getItemLocationY() == 24 && item.getItemLocationX() >= 24 && item.getItemLocationX() <= 30)) {
				item.setItemLocationX(90);
				item.setItemLocationY(90);
				F.addToItemIdsList(item.getItemID());
				groceryListNodes.add(F);
			}

			else if((item.getItemLocationY() == 96 && item.getItemLocationX() > 30 && item.getItemLocationX() <= 54) || (item.getItemLocationY() == 84 && item.getItemLocationX() > 30 && item.getItemLocationX() <= 54)) {
				item.setItemLocationX(210);
				item.setItemLocationY(450);
				R.addToItemIdsList(item.getItemID());
				groceryListNodes.add(R);
			}
			else if((item.getItemLocationY() == 96 && item.getItemLocationX() > 54 && item.getItemLocationX() <= 78) || (item.getItemLocationY() == 84 && item.getItemLocationX() > 54 && item.getItemLocationX() <= 78)) {
				item.setItemLocationX(330);
				item.setItemLocationY(450);
				S.addToItemIdsList(item.getItemID());
				groceryListNodes.add(S);
			}
			else if((item.getItemLocationY() == 96 && item.getItemLocationX() > 78 && item.getItemLocationX() <= 102) || (item.getItemLocationY() == 84 && item.getItemLocationX() > 78 && item.getItemLocationX() <= 102)) {
				item.setItemLocationX(450);
				item.setItemLocationY(450);
				T.addToItemIdsList(item.getItemID());
				groceryListNodes.add(T);
			}
			else if((item.getItemLocationY() == 96 && item.getItemLocationX() > 102 && item.getItemLocationX() <= 144) || (item.getItemLocationY() == 84 && item.getItemLocationX() > 102 && item.getItemLocationX() <= 108)) {
				item.setItemLocationX(570);
				item.setItemLocationY(450);
				U.addToItemIdsList(item.getItemID());
				groceryListNodes.add(U);
			}

			else if((item.getItemLocationY() == 72 && item.getItemLocationX() > 30 && item.getItemLocationX() <= 54) || (item.getItemLocationY() == 60 && item.getItemLocationX() > 30 && item.getItemLocationX() <= 54)) {
				item.setItemLocationX(210);
				item.setItemLocationY(330);
				O.addToItemIdsList(item.getItemID());
				groceryListNodes.add(O);
			}
			else if((item.getItemLocationY() == 72 && item.getItemLocationX() > 54 && item.getItemLocationX() <= 78) || (item.getItemLocationY() == 60 && item.getItemLocationX() > 54 && item.getItemLocationX() <= 78)) {
				item.setItemLocationX(330);
				item.setItemLocationY(330);
				N.addToItemIdsList(item.getItemID());
				groceryListNodes.add(N);
			}
			else if((item.getItemLocationY() == 72 && item.getItemLocationX() > 78 && item.getItemLocationX() <= 102) || (item.getItemLocationY() == 60 && item.getItemLocationX() > 78 && item.getItemLocationX() <= 102)) {
				item.setItemLocationX(450);
				item.setItemLocationY(330);
				M.addToItemIdsList(item.getItemID());
				groceryListNodes.add(M);
			}
			else if((item.getItemLocationY() == 72 && item.getItemLocationX() > 102 && item.getItemLocationX() <= 108) || (item.getItemLocationY() == 60 && item.getItemLocationX() > 102 && item.getItemLocationX() <= 108)) {
				item.setItemLocationX(570);
				item.setItemLocationY(330);
				L.addToItemIdsList(item.getItemID());
				groceryListNodes.add(L);
			}

			else if((item.getItemLocationY() == 48 && item.getItemLocationX() > 30 && item.getItemLocationX() <= 54) || (item.getItemLocationY() == 36 && item.getItemLocationX() > 30 && item.getItemLocationX() <= 54)) {
				item.setItemLocationX(210);
				item.setItemLocationY(210);
				H.addToItemIdsList(item.getItemID());
				groceryListNodes.add(H);
			}
			else if((item.getItemLocationY() == 48 && item.getItemLocationX() > 54 && item.getItemLocationX() <= 78) || (item.getItemLocationY() == 36 && item.getItemLocationX() > 54 && item.getItemLocationX() <= 78)) {
				item.setItemLocationX(330);
				item.setItemLocationY(210);
				I.addToItemIdsList(item.getItemID());
				groceryListNodes.add(I);
			}
			else if((item.getItemLocationY() == 48 && item.getItemLocationX() > 78 && item.getItemLocationX() <= 102) || (item.getItemLocationY() == 36 && item.getItemLocationX() > 78 && item.getItemLocationX() <= 102)) {
				item.setItemLocationX(450);
				item.setItemLocationY(210);
				J.addToItemIdsList(item.getItemID());
				groceryListNodes.add(J);
			}
			else if((item.getItemLocationY() == 48 && item.getItemLocationX() > 102 && item.getItemLocationX() <= 108) || (item.getItemLocationY() == 36 && item.getItemLocationX() > 102 && item.getItemLocationX() <= 108)) {
				item.setItemLocationX(570);
				item.setItemLocationY(210);
				K.addToItemIdsList(item.getItemID());
				groceryListNodes.add(K);
			}

			else if((item.getItemLocationY() == 24 && item.getItemLocationX() > 30 && item.getItemLocationX() <= 54) || (item.getItemLocationY() == 12 && item.getItemLocationX() > 30 && item.getItemLocationX() <= 54)) {
				item.setItemLocationX(210);
				item.setItemLocationY(90);
				E.addToItemIdsList(item.getItemID());
				groceryListNodes.add(E);
			}
			else if((item.getItemLocationY() == 24 && item.getItemLocationX() > 54 && item.getItemLocationX() <= 78) || (item.getItemLocationY() == 12 && item.getItemLocationX() > 54 && item.getItemLocationX() <= 78)) {
				item.setItemLocationX(330);
				item.setItemLocationY(90);
				D.addToItemIdsList(item.getItemID());
				groceryListNodes.add(D);
			}
			else if((item.getItemLocationY() == 24 && item.getItemLocationX() > 78 && item.getItemLocationX() <= 102) || (item.getItemLocationY() == 12 && item.getItemLocationX() > 78 && item.getItemLocationX() <= 102)) {
				item.setItemLocationX(450);
				item.setItemLocationY(90);
				C.addToItemIdsList(item.getItemID());
				groceryListNodes.add(C);
			}
			else if((item.getItemLocationY() == 24 && item.getItemLocationX() > 102 && item.getItemLocationX() <= 108) || (item.getItemLocationY() == 12 && item.getItemLocationX() > 102 && item.getItemLocationX() <= 108)) {
				item.setItemLocationX(570);
				item.setItemLocationY(90);
				B.addToItemIdsList(item.getItemID());
				groceryListNodes.add(B);
			}
		}
		List<Item> orderedItems = new ArrayList<Item>();

		A.setShortestPath(groceryListNodes);

		graphNodes = calculateShortestPathFromSource(graphNodes, A);

		int numOrder = 1;
		for (Node n : graphNodes.getGraphNodes()) {
			for (int i = 1; i < n.getItemIds().size(); i++) {
				if (n.getOrder() == numOrder) {
					Item item = findItemByItemID(n.getItemIds().get(i));
					item.setOrderNumber(numOrder++);
					orderedItems.add(item);
					numOrder++;
				}
			}
		}
		return orderedItems;
	}

	public static Graph calculateShortestPathFromSource(Graph graph, Node source) {
		source.setDistance(0);

		Set<Node> settledNodes = new HashSet<>();
		Set<Node> unsettledNodes = new HashSet<>();

		unsettledNodes.add(source);

		int order = 0;
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
			currentNode.setOrder(order++);
			graph.addNodeToRoute(currentNode);
			settledNodes.add(currentNode);
		}
		for (Node n : settledNodes) {
			graph.addNodeToRoute(n);
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

	public boolean insertItemIntoGroceryListTable(final int id, final Item item, final int qty) throws SQLException {
		return executeTransaction(new Transaction<Boolean>() {

			public Boolean execute(Connection conn) throws SQLException {
				boolean finalResult = false;
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				try {
					int item_id = item.getItemID();
					conn.setAutoCommit(true);
					int iter = 0;
					while (iter < qty) {

						stmt = conn.prepareStatement(
								"INSERT into groceryLists (account_id, item_id) "
										+ "  values(?, ?) "
								);

						stmt.setInt(1, id);
						stmt.setInt(2, item_id);

						// execute the query
						stmt.executeUpdate();
						iter++;
					}

					System.out.println("Item <" + item.getItemName() + "> has been added to the groceryLists table <" + qty + "> times.");
					finalResult = true;

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
					int item_id = name.getItemID();
					conn.setAutoCommit(true);
					stmt = conn.prepareStatement(
							"SELECT * FROM groceryLists" +
									"	WHERE groceryLists.account_id = ?" +
									"		AND groceryLists.item_id = ?"
							);
					stmt.setInt(1, id);
					stmt.setInt(2, item_id);
					System.out.print("account: "+id+", item: "+item_id);
					// execute the query
					resultSet = stmt.executeQuery();
					int count = 0;
					while (resultSet.next()) {
						count++;
					}
					if((count/2) == qty) {
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
							"INSERT into accounts(account_username, account_password) "
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
		Account account = findAccountByUsername(username);
		int finalResult = 0;

		if (account.getPassword().equals(password)){
			finalResult = account.getAccountID();
			System.out.println("Found account <" + username + "> in the accounts table");
		}else{
			System.out.println("Either <" + username + "> or <" + password +"> is not valid");
		}
		return finalResult;
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
									"	FROM groceryLists " +
									"	WHERE groceryLists.account_id = ? "
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
				Boolean finalResult = false;
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				int item_id = itemName.getItemID();
				try {
					conn.setAutoCommit(true);
					System.out.println("");
					// a canned query to find book information (including author name) from title
					stmt = conn.prepareStatement(
							"DELETE TOP(?)" +
									"	FROM groceryLists " +
									"	WHERE groceryLists.account_id = ? " +
									"		and groceryLists.item_id = ? "
							);

					// substitute the last name and first name of the existing author entered by the user for the placeholder in the query
					stmt.setInt(1, qty);
					stmt.setInt(2, id);
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
							"SELECT accounts.account_id, accounts.account_username, accounts.account_password " +
									"	FROM accounts " +
									"	   WHERE accounts.account_id = ? "	
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

				List<Account> finalResult = new ArrayList<Account>();
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				try {
					conn.setAutoCommit(true);

					// a canned query to find book information (including author name) from title
					stmt = conn.prepareStatement(
							"SELECT * FROM accounts"
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
				List<GroceryList> lists = new ArrayList<GroceryList>();
				List<Item> finalResult = new ArrayList<Item>();
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				try {
					conn.setAutoCommit(true);

					// a canned query to find book information (including author name) from title
					stmt = conn.prepareStatement(
							"SELECT * FROM groceryLists" +
									"	WHERE groceryLists.account_id = ?"
							);
					stmt.setInt(1, id);
					// execute the query
					resultSet = stmt.executeQuery();
					while (resultSet.next()) {
						GroceryList list= new GroceryList();
						loadGroceryList(list, resultSet, 1);
						if (list.getAccountID() == id){
							System.out.println("  "+list.getAccountID()+" "+ list.getItemID(0));
							lists.add(list);
						}
					}
					System.out.println("list size:"+lists.size());

					for (int i = 0; i < lists.size(); i++) {;
					System.out.println("Check Item ID: "+ lists.get(i).getItemID(i));
					Item temp=findItemByItemID(lists.get(i).getItemID(i));
					finalResult.add(temp);

					String name=finalResult.get(i).getItemName();

					System.out.println("Item Name: "+name+", iterated:"+ i+ " size: "+lists.size());
					}

					System.out.println(" 1 ");

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


