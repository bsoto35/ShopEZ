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
	private Item remItem;

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
		IDatabase db = DatabaseProvider.getInstance();
		String errorMessage = null;
		String successMessage = null;
		login= new Account();
		req.setAttribute("quantity", 1); 
		int amount=1;

		req.setAttribute("quantityA", amount); 
		req.setAttribute("quantityR", amount); 
		if(req.getParameter("Add") !=null) {
			if(req.getParameter("Item")!= null) {				
				amount = getIntFromParameter(req.getParameter("quantityA"));
				newItem.setItemName(req.getParameter("Add"));
				try {
					newItem=db.findItemByItemName(newItem.getItemName());
				} catch (SQLException e) {
					e.printStackTrace();
					errorMessage="Invalid Item";


				}
				grocerys.addItem(newItem, amount);//use database method
				grocerys.setAccountID(login.getAccountID());
				double sum= grocerys.getTotalPrice(); 
				grocerys.setListPrice(sum);
			}	
		}
		if(req.getParameter("Remove") !=null) {	
			amount = getIntFromParameter(req.getParameter("quantityR"));
			remItem.setItemName(req.getParameter("Remove"));

			try {
				remItem=db.findItemByItemName(remItem.getItemName());
				//successMessage=db.removeItemFromTheList(login, remItem, amount);;
			} catch (SQLException e) {
				e.printStackTrace();
				errorMessage="Invalid Item";

			}
			
			// Forward to view to render the result HTML document
		}	
		req.setAttribute("errorMessage", errorMessage);
		req.setAttribute("successMessage", successMessage);

		req.getRequestDispatcher("/_view/insertItem.jsp").forward(req, resp);
	}

	// Decode form parameters and dispatch to controller

	// Add parameters as request attributes
	private Integer getIntFromParameter(String s) {
		if (s == null || s.equals("")) {
			return null;
		} else {
			return Integer.parseInt(s);
		}
	}


}
