package edu.ycp.cs320.ShopEZ.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.ycp.cs320.ShopEZ.persist.DatabaseProvider;
import edu.ycp.cs320.ShopEZ.persist.IDatabase;
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
		// Decode form parameters and dispatch to controller

		if(req.getParameter("Login") !=null) {
			login.setUsername(req.getParameter("inUsername"));
			login.setPassword(req.getParameter("inPassword"));
			System.out.println("   Name: <" + login.getUsername() + "> PW: <" + login.getPassword() + ">");			
			if (login.getUsername() == null || login.getPassword() == null || login.getUsername().equals("") || login.getPassword().equals("")) {
				errorMessage = "Please specify both user name and password";
			} else {
				IDatabase db = DatabaseProvider.getInstance();
				try {
					validLogin= db.verifyAccountFromAccountsTableByUsernameAndPassword(login.getUsername(), login.getPassword());
				} catch (SQLException e) {
					e.printStackTrace();
				}
				if (!validLogin) {
					errorMessage = "Username and/or password invalid";
				}
			}
		}
		if(req.getParameter("SignUp") !=null) {
			login.setUsername(req.getParameter("upUsername"));
			login.setPassword(req.getParameter("upPassword"));
			if(login.getPassword().equals(req.getParameter("confirmPassword"))){
				IDatabase db = DatabaseProvider.getInstance();
				try {
					db.addAccountIntoAccountsTable(login.getUsername(), login.getPassword());
				} catch (SQLException e) {
					e.printStackTrace();


					if (login.getUsername() == null || login.getPassword() == null || login.getUsername().equals("") || login.getPassword().equals("")) {
						errorMessage = "Please specify both user name and password";
					} else {

					}
				}
			}
			if (login.getUsername() == null || login.getPassword() == null || login.getUsername().equals("") || login.getPassword().equals("")) {
				errorMessage = "Please specify both user name and password";
			} else{

				if(login.getPassword().equals(req.getParameter("confirmPassword"))){
					IDatabase db = DatabaseProvider.getInstance();

					try {
						db.addAccountIntoAccountsTable(login.getUsername(), login.getPassword());
					} catch (SQLException e) {
						e.printStackTrace();
					}			
				}
			}
		}


		// Add parameters as request attributes
		req.setAttribute("Username", req.getParameter("inUsername"));
		req.setAttribute("Password", req.getParameter("inPassword"));

		// Add result objects as request attributes
		req.setAttribute("errorMessage", errorMessage);
		req.setAttribute("login",        validLogin);

		// if login is valid, start a session
		if (validLogin) {
			System.out.println("   Valid login - starting session, redirecting to /index");

			// store user object in session
			req.getSession().setAttribute("user", login.getUsername());

			// redirect to /index page
			resp.sendRedirect(req.getContextPath() + "/index");
			// redirect to /index page
			resp.sendRedirect(req.getContextPath() + "/index");
			return;
		}
		System.out.println("Invalid login - returning to /Login");

		System.out.println("   Invalid login - returning to /Login");

		// Forward to view to render the result HTML document
		req.getRequestDispatcher("/_view/login.jsp").forward(req, resp);
	}
}

