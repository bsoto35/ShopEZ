package edu.ycp.cs320.ShopEZ.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import edu.ycp.cs320.ShopEZ.persist.DerbyDatabase;
import edu.ycp.cs320.ShopEZ.controller.LoginController;
import edu.ycp.cs320.ShopEZ.model.Account;

public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Account login;
	private LoginController controller;

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

		String errorMessage = "";
		String successMessage="";
		boolean validLogin =false;
		login= new Account();
		controller=new LoginController();
		DerbyDatabase db = new DerbyDatabase(); 
		// Decode form parameters and dispatch to controller

		if(req.getParameter("SignIn") !=null) {
			System.out.print("sign in pressed");
			login.setUsername(req.getParameter("inUsername"));
			login.setPassword(req.getParameter("inPassword"));
			System.out.println("   Name: <" + login.getUsername() + "> PW: <" + login.getPassword() + ">");			
			if (login.getUsername() == null || login.getPassword() == null) {
				errorMessage = "Please specify both user name and password";
			} else {
				try {		
					validLogin=controller.verifyAccount(login.getUsername(), login.getPassword());
					login=controller.getAccountbyUser(login.getUsername());
					successMessage="Successfully logged in as "+login.getUsername();
					if(!validLogin)
						errorMessage = "Username and/or password invalid";

				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		else if(req.getParameter("forgot") !=null) {			
			try {
				login=controller.findAccountByName("guest");
			} catch (SQLException e) {
				e.printStackTrace();
			}	
			System.out.println("  Name: <" + login.getUsername() + ">  PW: <" + login.getPassword() + ">");
			successMessage="Successfully logged in as "+login.getUsername();
			req.getSession().setAttribute("user", login);
			resp.sendRedirect(req.getContextPath() + "/insertItem");
			req.getRequestDispatcher("/_view/insertItem.jsp").forward(req, resp); 
		}

		else if(req.getParameter("SignUp") !=null) {
			System.out.print("sign up pressed");
			login.setUsername(req.getParameter("upUsername"));
			login.setPassword(req.getParameter("upPassword"));
			if (login.getUsername() == null || login.getPassword() == null) {
				errorMessage = "Please specify both user name and password";
			} else{
				System.out.println("   Name: <" + login.getUsername() + "> PW: <" + login.getPassword() + ">");	
				if(login.getPassword().equals(req.getParameter("ConfirmPassword"))){
					try {
						validLogin= controller.addNewAccount(login.getUsername(), login.getPassword());

					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				errorMessage = "Passwords do not match";
			}
		}
		req.getSession().setAttribute("errorMessage", errorMessage);
		req.getSession().setAttribute("successMessage", successMessage);
		if (validLogin==true) {
			System.out.println("   Valid login - starting session, redirecting to /insertItem");
			// store user object in session
			req.getSession().setAttribute("user", login);
			resp.sendRedirect(req.getContextPath() + "/insertItem");
			req.getRequestDispatcher("/_view/insertItem.jsp").forward(req, resp);
			// redirect to /index page

			return;
		}
		else{
			System.out.println("Invalid login - redirecting to /login");
			req.getRequestDispatcher("/_view/login.jsp").forward(req, resp);
		}

		req.setAttribute("app", login);
		// Add parameters as request attributes
		req.setAttribute("Username", req.getParameter("inUsername"));
		// Add result objects as request attributes
		// if login is valid, start a session	
		System.out.println("check");
	}
}