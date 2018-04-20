package edu.ycp.cs320.ShopEZ.servlet;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import edu.ycp.cs320.ShopEZ.controller.ShopezeViewController;
import edu.ycp.cs320.ShopEZ.model.Account;
import edu.ycp.cs320.ShopEZ.model.ShopezeModel;


public class ShopezeViewServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ShopezeModel model;
	private Account login;



	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		System.out.println("\nLoginServlet: doGet");


		req.getRequestDispatcher("/login.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		System.out.println("\nLoginServlet: doPost");

		String errorMessage = null;
		boolean validLogin  = false;
		login= new Account();
		// Decode form parameters and dispatch to controller
		login.setUsername(req.getParameter("inUsername"));
		login.setPassword(req.getParameter("inPassword"));

		System.out.println("   Name: <" + login.getUsername() + "> PW: <" + login.getPassword() + ">");			

		if (login.getUsername() == null || login.getPassword() == null || login.getUsername().equals("") || login.getPassword().equals("")) {
			errorMessage = "Please specify both user login.getUsername() and password";
		} else {


			if (!validLogin) {
				errorMessage = "Username and/or password invalid";
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

			return;
		}

		System.out.println("   Invalid login - returning to /Login");

		// Forward to view to render the result HTML document
		req.getRequestDispatcher("/login.jsp").forward(req, resp);
	}
}