package edu.ycp.cs320.ShopEZ.controller;

import edu.ycp.cs320.ShopEZ.model.Account;
import edu.ycp.cs320.ShopEZ.model.Library;

public class LoginController {
	private Account model = null;
	
	public LoginController(Account model) {
		this.model = model;
	}
	
	public boolean checkUserName(String name) {
		return model.UserName(name);
	}
	
	public boolean validateCredentials(String name, String pw) {
		return model.confirmAccount(name, pw);
	}
}
