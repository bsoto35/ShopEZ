package edu.ycp.cs320.ShopEZ.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.ycp.cs320.ShopEZ.controller.InsertItemController;
import edu.ycp.cs320.ShopEZ.model.Account;
import edu.ycp.cs320.ShopEZ.model.GroceryList;
import edu.ycp.cs320.ShopEZ.model.Item;
import edu.ycp.cs320.ShopEZ.persist.DatabaseProvider;
import edu.ycp.cs320.ShopEZ.persist.IDatabase;

public class InsertItemServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private InsertItemController controller = null;	
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
		String errorMessage = null;
		boolean validLogin  = false;
		login= new Account();
		double amount=1;
		req.setAttribute("quantityA", amount); 
		if(req.getParameter("Add") !=null) {
			if(req.getParameter("Item")!= null) {				
				amount = getDoubleFromParameter(req.getParameter("quantityA"));
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
				double sum= grocerys.getTotalPrice(); 
				grocerys.setListPrice(sum);
			}	
		}
		if(req.getParameter("Remove") !=null) {
			
			removeItemFromTheList(remItem, amount);
		}

		String successMessage = null;
		String item_name      = null;
		double item_price     = 0.0;
		int item_locationX    = 0;
		int item_locationY    = 0;
		int item_quantity     = 0;
		int    published	  = 0;

		// Decode form parameters and dispatch to controller
		/*
		item_name    = req.getParameter("item_itemname");
		item_price     = req.getParameter("item_price");
		item_locationX  = req.getParameter("item_locationX");
		item_locationY  = req.getParameter("item_locationY");
		item_quantity = req.getParameter("item_quantity");

		if (item_name    == null || item_name.equals("") ||
			item_price     == null || item_price.equals("")  ||
			item_locationX  == null || item_locationX.equals("")     ||
			item_locationY == null || item_locationY.equals("")	     ||
			item_quantity == null || item_quantity.equals("")) {

			errorMessage = "Please fill in all of the required fields";
		} else {
			controller = new InsertItemController();

			// convert published to integer now that it is valid
			published = Integer.parseInt(strPublished);

			// get list of books returned from query			
			if (controller.insertItemIntoLibrary(title, isbn, published, lastName, firstName)) {
				successMessage = title;
			}
			else {
				errorMessage = "Failed to insert Book: " + title;					
			}
		}
		 */
		// Add parameters as request attributes

		// Add result objects as request attributes
		req.setAttribute("errorMessage",   errorMessage);
		req.setAttribute("successMessage", successMessage);

		// Forward to view to render the result HTML document
		req.getRequestDispatcher("/_view/insertBook.jsp").forward(req, resp);
	}	
	private Double getDoubleFromParameter(String s) {
		if (s == null || s.equals("")) {
			return null;
		} else {
			return Double.parseDouble(s);
		}
	}
}
