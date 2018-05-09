package edu.ycp.cs320.ShopEZ.controller;

import java.sql.SQLException;
import edu.ycp.cs320.ShopEZ.model.Account;
import edu.ycp.cs320.ShopEZ.persist.DerbyDatabase;

/**
 * Controller for the guessing game.
 */
public class LoginController {
	private DerbyDatabase db= new DerbyDatabase();
	private Account login;
	
	public LoginController() {
		
	}

	public void setAccount(Account account) {
		login=account;
	}
	
	public Account getAccount() {
		return login;
	}
	
	public Account findAccountByName(String name) throws SQLException{
		return db.findAccountByUsername(name);
	}
	
	public Account getAccountbyUser(String name) throws SQLException{
		login=db.findAccountByUsername(name);
		return login;
	}
	
	public boolean verifyAccount(String name, String password) throws SQLException{
		return db.verifyAccountFromAccountsTableByUsernameAndPassword(name, password);
	}
	
	public boolean addNewAccount(String name, String password) throws SQLException{
		return db.addAccountIntoAccountsTable(name, password);
	}
}