package edu.ycp.cs320.ShopEZ.controller;

import edu.ycp.cs320.ShopEZ.model.Account;
import edu.ycp.cs320.ShopEZ.persist.DerbyDatabase;

/**
 * Controller for the guessing game.
 */
public class LoginController {
	DerbyDatabase db= new DerbyDatabase();
	private Account model;
	
	public LoginController() {
		
	}

	public void setAccount(Account account) {
		login=account;
	}
	
	public Account getAccount() {
		return login;
	}
	
	public void newList() {
		
	}
	public Account getAccountbyUser(String name) throws SQL Exception{
		
	}
}