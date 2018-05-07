package edu.ycp.cs320.ShopEZ.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.ycp.cs320.ShopEZ.controller.InsertItemController;
import edu.ycp.cs320.ShopEZ.model.Account;
import edu.ycp.cs320.ShopEZ.model.GroceryList;
import edu.ycp.cs320.ShopEZ.model.Item;

public class RouteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Account login= new Account();
	private GroceryList grocerys;
	private Item item= new Item();
	private InsertItemController controller;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HttpSession session=req.getSession(false);
		System.out.println("Review List Servlet: doGet");
		login= (Account)session.getAttribute("user");
		String user = login.getUsername();
		if (user == null) {
			System.out.println("   User: <" + user + "> not logged in or session timed out");

			// user is not logged in, or the session expired
			resp.sendRedirect(req.getContextPath() + "/login");
			return;
		}

		// now we have the user's User object,
		// proceed to handle request...

		System.out.println("   User: <" + user + "> logged in");

		req.getRequestDispatcher("/_view/Route.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HttpSession session=req.getSession(false);
		login= (Account)session.getAttribute("user");
	}
}
