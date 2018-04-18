package edu.ycp.cs320.ShopEZ.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.ycp.cs320.ShopEZ.model.Account;
import edu.ycp.cs320.ShopEZ.model.GroceryList;
import edu.ycp.cs320.ShopEZ.model.Item;
import edu.ycp.cs320.ShopEZ.persist.DatabaseProvider;
import edu.ycp.cs320.ShopEZ.persist.IDatabase;

public class InsertItemServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private Account login= new Account();
	private GroceryList grocerys;
	private Item newItem; 

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		System.out.println("\nInsertItemServlet: doGet");

		String user = (String) req.getSession().getAttribute("user");
		if (user == null) {
			System.out.println("   User: <" + user + "> not logged in or session timed out");

			// user is not logged in, or the session expired
			resp.sendRedirect(req.getContextPath() + "/login");
			return;
		}

		// now we have the user's User object,
		// proceed to handle request...

		System.out.println("   User: <" + user + "> logged in");

		req.getRequestDispatcher("/_view/insertItem.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		System.out.println("\nInsertItemServlet: doPost");	
		String successMessage = "failed";
		String errorMessage = null;
		login= new Account();
		int amount=1;
		req.setAttribute("quantityA", amount); 
		if(req.getParameter("Add") !=null) {
			if(req.getParameter("Item")!= null) {				
				amount = getIntFromParameter(req.getParameter("quantity"));
				newItem.setItemName(req.getParameter("Add"));
				IDatabase db = DatabaseProvider.getInstance();
				try {
					newItem=db.findItemByItemName(newItem.getItemName());
				} catch (SQLException e) {
					e.printStackTrace();
					errorMessage="Invalid Item";
					
				}
				grocerys.addItem(newItem, amount);
				grocerys.setAccountID(login.getAccountID());
				double sum = grocerys.getupdatedPrice();
				grocerys.setListPrice(sum);
			}	
		}
		if(req.getParameter("Remove") !=null) {
			IDatabase db = DatabaseProvider.getInstance();
			try {
				successMessage = db.removeItemFromTheList(login, req.getParameter("remItem"), amount);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Add result objects as request attributes
		req.setAttribute("errorMessage",   errorMessage);
		req.setAttribute("successMessage", successMessage);

		req.getRequestDispatcher("/_view/insertItem.jsp").forward(req, resp);
	}	
	private int getIntFromParameter(String s) {
		if (s == null || s.equals("")) {
			return 0;
		} else {
			return Integer.parseInt(s);
		}
	}
}
