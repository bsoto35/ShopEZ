package edu.ycp.cs320.ShopEZ.controller;

import edu.ycp.cs320.ShopEZ.model.Account;
import edu.ycp.cs320.ShopEZ.model.Library;

public class LoginController {
	private Account model = null;
	
	public LoginController(Account model) {
		this.model = model;
	}
	
	public boolean checkUserName() {
		boolean valid=false;
		if(model.getUsername() != null)
			valid=true;
		return valid;
	} 
	
	public boolean validateCredentials(String name, String pw) {
		return model.confirmAccount(name, pw);
	}
	
}
