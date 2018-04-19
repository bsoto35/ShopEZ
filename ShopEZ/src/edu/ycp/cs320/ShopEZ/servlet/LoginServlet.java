package edu.ycp.cs320.ShopEZ.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import edu.ycp.cs320.ShopEZ.persist.DerbyDatabase;
import edu.ycp.cs320.ShopEZ.model.Account;

public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Account login;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		System.out.println("\nLoginServlet: doGet");


		req.getRequestDispatcher("/_view/login.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		System.out.println("\nLoginServlet: doPost");

		String errorMessage = null;
		boolean validLogin  = false;
		login= new Account();
		DerbyDatabase db = new DerbyDatabase(); 
		// Decode form parameters and dispatch to controller

		if(req.getParameter("Sign In") !=null) {
			login.setUsername(req.getParameter("inUsername"));
			login.setPassword(req.getParameter("inPassword"));
			System.out.println("   Name: <" + login.getUsername() + "> PW: <" + login.getPassword() + ">");			
			if (login.getUsername() == null || login.getPassword() == null || login.getUsername().equals("") || login.getPassword().equals("")) {
				errorMessage = "Please specify both user name and password";
			} else {
				try {
					validLogin= db.verifyAccountFromAccountsTableByUsernameAndPassword(login.getUsername(), login.getPassword());
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		if(req.getParameter("Sign Up") !=null) {
			if (login.getUsername() == null || login.getPassword() == null || login.getUsername().equals("") || login.getPassword().equals("")) {
				errorMessage = "Please specify both user name and password";
			} else{
				login.setUsername(req.getParameter("upUsername"));
				login.setPassword(req.getParameter("upPassword"));
				if(login.getPassword().equals(req.getParameter("confirm_Password"))){
					db = new DerbyDatabase();
					try {
						db.addAccountIntoAccountsTable(login.getUsername(), login.getPassword());
						validLogin=true; 
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}


		
		// Add parameters as request attributes
		req.setAttribute("Username", req.getParameter("inUsername"));

		// Add result objects as request attributes
		req.setAttribute("errorMessage", errorMessage);

		// if login is valid, start a session
		if (validLogin) {
			System.out.println("   Valid login - starting session, redirecting to /insertItem");

			// store user object in session
			req.getSession().setAttribute("user", login.getUsername());

			// redirect to /index page
			resp.sendRedirect(req.getContextPath() + "/_view/insertItem.jsp");
			// redirect to /index page

			return;
		}
		else{
			errorMessage = "Username and/or password invalid";
			req.getRequestDispatcher("/_view/login.jsp").forward(req, resp);
			System.out.println("   Invalid login - redirecting to /login");
		}




		// Forward to view to render the result HTML document
	}
}

